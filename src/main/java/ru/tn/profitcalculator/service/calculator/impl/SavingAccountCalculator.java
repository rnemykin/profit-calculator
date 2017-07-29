package ru.tn.profitcalculator.service.calculator.impl;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.CardCategoryEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.service.ObjectService;
import ru.tn.profitcalculator.service.calculator.Calculator;
import ru.tn.profitcalculator.service.calculator.OptionProfitCalculatorFactory;
import ru.tn.profitcalculator.service.calculator.ProductCalculateRequest;
import ru.tn.profitcalculator.service.calculator.ProductCalculateResult;
import ru.tn.profitcalculator.service.calculator.option.IOptionProfitCalculator;
import ru.tn.profitcalculator.service.calculator.option.impl.SavingOptionProfitCalculator;
import ru.tn.profitcalculator.web.model.CalculateParams;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static org.springframework.util.CollectionUtils.isEmpty;
import static ru.tn.profitcalculator.util.MathUtils.isGreatThenZero;

@Log4j
@Service
public class SavingAccountCalculator implements Calculator {

    private static final BigDecimal DAYS_IN_MONTH = valueOf(30);
    private static final BigDecimal DAYS_IN_YEAR = valueOf(365);
    private static final BigDecimal V_100 = valueOf(100);

    private final ObjectService objectService;
    private final OptionProfitCalculatorFactory optionProfitCalculatorFactory;

    public SavingAccountCalculator(ObjectService objectService, OptionProfitCalculatorFactory optionProfitCalculatorFactory) {
        this.objectService = objectService;
        this.optionProfitCalculatorFactory = optionProfitCalculatorFactory;
    }

    @Override
    public ProductCalculateResult calculate(ProductCalculateRequest request) {
        SavingAccount savingAccount = (SavingAccount) request.getProduct();
        Map<Integer, BigDecimal> periodRates = initSavingAccountRates(savingAccount);

        CalculateParams params = request.getParams();
        Integer daysCount = params.getDaysCount();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(daysCount);
        BigDecimal totalProfit = ZERO;
        BigDecimal refillSum = ZERO;

        BigDecimal totalSum = params.getInitSum();
        if(isLinkedProductCard(savingAccount, CardCategoryEnum.CREDIT)) {
            Card card = (Card) savingAccount.getLinkedProduct();
            if(isLinkedProductCard(card, CardCategoryEnum.DEBIT)) {
                if(isGreatThenZero(params.getMonthRefillSum())) { // first refill go to first month, other refill go to credit repayment
                    totalSum = totalSum.add(params.getMonthRefillSum());
                    params.setMonthRefillSum(ZERO);
                }
            }
        }

        Map<LocalDate, BigDecimal> layers = new TreeMap<>();
        layers.put(startDate, totalSum);

        if (isGreatThenZero(params.getMonthRefillSum())) {
            long monthsCount = MONTHS.between(startDate, endDate);
            for (int i = 1; i <= monthsCount; i++) {
                layers.put(startDate.plusMonths(i), params.getMonthRefillSum());
            }

            refillSum = params.getMonthRefillSum().multiply(valueOf(monthsCount));
        }
        List<List<BigDecimal>> accountState = new ArrayList<>();

        CardOption cardOption = getCardOption(savingAccount, params.getCategories2Costs());

        for (Map.Entry<LocalDate, BigDecimal> layer : layers.entrySet()) {
            List<BigDecimal> layerAccountState = new ArrayList<>();

            LocalDate layerStartDate = layer.getKey();
            LocalDate layerNextPeriodDate = layerStartDate;
            BigDecimal layerProfitSum = layer.getValue();

            for (int i = 1; layerNextPeriodDate.isBefore(endDate); i++) {
                layerAccountState.add(layerProfitSum);

                layerNextPeriodDate = layerNextPeriodDate.plusMonths(1);

                boolean isLastPeriod = !layerNextPeriodDate.isBefore(endDate);
                long periodDays = isLastPeriod ? DAYS.between(layerStartDate, endDate) : DAYS.between(layerStartDate, layerNextPeriodDate);
                BigDecimal rate4Month = getRate4Month(periodRates, i);
                BigDecimal monthProfit = calculatePeriodSum(layerProfitSum, rate4Month, valueOf(periodDays));

                if (cardOption != null && cardOption.getBonusOption() == BonusOptionEnum.SAVING) {
                    BigDecimal sum = layer.getValue();
                    BigDecimal rate = cardOption.getRate();
                    BigDecimal days = valueOf(periodDays);

                    SavingOptionProfitCalculator calculator = (SavingOptionProfitCalculator) optionProfitCalculatorFactory.get(BonusOptionEnum.SAVING);
                    BigDecimal optionProfitSum = calculator.calculateProfitSum(sum, rate, days);
                    monthProfit = monthProfit.add(optionProfitSum);
                }

                layerProfitSum = layerProfitSum.add(monthProfit);
                layerStartDate = layerNextPeriodDate;

                totalSum = totalSum.add(monthProfit);
                totalProfit = totalProfit.add(monthProfit);
            }

            accountState.add(layerAccountState);
        }

        normalizeAccountState(accountState);
        BigDecimal maxRate = new EffectiveRateCalculator(periodRates, accountState).calculate();

        if (cardOption != null) {
            cardOption.setRate(cardOption.getRate().multiply(V_100));

            if(cardOption.getBonusOption() == BonusOptionEnum.SAVING) {
                maxRate = maxRate.add(cardOption.getRate());
            }
        }
        BigDecimal optionTotalProfit = calculateTotalOptionProfit4Period(cardOption, startDate, endDate);

        return ProductCalculateResult.builder()
                .totalSum(totalSum.add(refillSum).setScale(0, RoundingMode.HALF_UP))
                .profitSum(totalProfit.setScale(0, RoundingMode.HALF_UP))
                .optionProfitSum(optionTotalProfit.setScale(0, RoundingMode.HALF_UP))
                .maxRate(maxRate)
                .daysCount(daysCount)
                .product(request.getProduct())
                .recommendation(request.isRecommendation())
                .build();
    }

    private boolean isLinkedProductCard(Product product, CardCategoryEnum category) {
        Product linkedProduct = product.getLinkedProduct();
        return linkedProduct instanceof Card && ((Card) linkedProduct).getCardCategory() == category;
    }

    private BigDecimal calculateTotalOptionProfit4Period(CardOption cardOption, LocalDate startDate, LocalDate endDate) {
        if (cardOption != null && cardOption.getBonusOption() != BonusOptionEnum.SAVING) {
            long days = DAYS.between(startDate, endDate);
            BigDecimal months = valueOf(days).divide(DAYS_IN_MONTH, 4, RoundingMode.HALF_UP);
            return cardOption.getCashback4Month().multiply(months);
        }
        return null;
    }

    private CardOption getCardOption(SavingAccount savingAccount, Map<PosCategoryEnum, Pair<Boolean, BigDecimal>> categories2Costs) {
        if (savingAccount.getLinkedProduct() instanceof Card && !isEmpty(categories2Costs)) {
            Card card = (Card) savingAccount.getLinkedProduct();

            if (card.getCardOption() != null) {
                CardOption cardOption = objectService.clone(card.getCardOption());
                card.setCardOption(cardOption);
                IOptionProfitCalculator optionProfitCalculator = optionProfitCalculatorFactory.get(cardOption.getBonusOption());
                return optionProfitCalculator.calculate(cardOption, categories2Costs);
            }
        }
        return null;
    }

    private Map<Integer, BigDecimal> initSavingAccountRates(SavingAccount savingAccount) {
        Map<Integer, BigDecimal> rates = new HashMap<>();
        savingAccount.getRates().forEach(r -> rates.put(r.getFromPeriod(), r.getRate()));
        return rates;
    }

    private BigDecimal getRate4Month(Map<Integer, BigDecimal> periodRates, int month) {
        return periodRates.entrySet().stream()
                .filter(e -> e.getKey().compareTo(month) <= 0)
                .max(Comparator.comparing(Map.Entry::getKey))
                .orElseThrow(() -> new RuntimeException("rate not found fot month " + month))
                .getValue();
    }

    private BigDecimal calculatePeriodSum(BigDecimal totalSum, BigDecimal rate, BigDecimal periodDays) {
        return totalSum.multiply(
                rate.multiply(periodDays.divide(DAYS_IN_YEAR, 10, RoundingMode.HALF_UP))
        ).divide(V_100, 0, RoundingMode.HALF_UP);
    }

    private void normalizeAccountState(List<List<BigDecimal>> accountState) {
        int maxSize = accountState.get(0).size();
        for (List<BigDecimal> state : accountState) {
            while (state.size() != maxSize) {
                state.add(0, BigDecimal.ZERO);
            }
        }
    }

    @Override
    public ProductTypeEnum type() {
        return ProductTypeEnum.SAVING_ACCOUNT;
    }

    @AllArgsConstructor
    private class EffectiveRateCalculator {
        private Map<Integer, BigDecimal> rates;
        private List<List<BigDecimal>> accountState;

        private BigDecimal calculate() {
            BigDecimal b12 = getMinSumForPeriod(12);
            BigDecimal b6 = getMinSumForPeriod(6);
            BigDecimal b3 = getMinSumForPeriod(3);
            BigDecimal b1 = getMinSumForPeriod(1);

            BigDecimal rate12 = getRate4Month(rates, 12);
            BigDecimal rate6 = getRate4Month(rates, 6);
            BigDecimal rate3 = getRate4Month(rates, 3);
            BigDecimal rate1 = getRate4Month(rates, 1);

            return (b12.multiply(rate12)
                    .add(b6.subtract(b12).multiply(rate6))
                    .add(b3.subtract(b6).multiply(rate3))
                    .add(b1.subtract(b3).multiply(rate1))
            ).divide(b1, 1, RoundingMode.HALF_UP);
        }

        private BigDecimal getMinSumForPeriod(int period) {
            List<BigDecimal> initLayer = accountState.get(0);
            if (initLayer.size() < period) {
                return BigDecimal.ZERO;
            }

            int size = initLayer.size();
            List<BigDecimal> monthsSum = new ArrayList<>();
            for (int i = size - period; i < size; i++) {
                BigDecimal monthSum = ZERO;
                for (List<BigDecimal> state : accountState) {
                    monthSum = monthSum.add(state.get(i));
                }
                monthsSum.add(monthSum);
            }

            return monthsSum.stream()
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
        }
    }

}
