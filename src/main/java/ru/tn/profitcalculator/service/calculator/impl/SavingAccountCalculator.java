package ru.tn.profitcalculator.service.calculator.impl;

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
import ru.tn.profitcalculator.web.model.ClientProduct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
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
import static ru.tn.profitcalculator.util.SavingAccountUtils.getRate4Month;

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
        Map<LocalDate, BigDecimal> layers = new TreeMap<>();
        BigDecimal totalSum;
        boolean offerByClientProduct = false;

        ClientProduct clientProduct = request.getClientProduct();
        if (clientProduct != null && clientProduct.getAccountBalances() != null && !clientProduct.getAccountBalances().isEmpty()) {
            List<Pair<LocalDate, BigDecimal>> accountBalances = clientProduct.getAccountBalances();
            BigDecimal max = ZERO;
            for (Pair<LocalDate, BigDecimal> accountBalance : accountBalances) {
                layers.put(accountBalance.getFirst(), accountBalance.getSecond());
                if(max.compareTo(accountBalance.getSecond()) < 0) {
                    max = accountBalance.getSecond();
                }
            }
            totalSum = max;
            offerByClientProduct = true;
        } else {
            totalSum = params.getInitSum();
            layers.put(startDate, totalSum);

            if (isGreatThenZero(params.getMonthRefillSum())) {
                long monthsCount = MONTHS.between(startDate, endDate);
                for (int i = 1; i <= monthsCount; i++) {
                    layers.put(startDate.plusMonths(i), params.getMonthRefillSum());
                }
                refillSum = params.getMonthRefillSum().multiply(valueOf(monthsCount));
            }
        }
        if (isLinkedProductCard(savingAccount, CardCategoryEnum.CREDIT)) {
            Card card = (Card) savingAccount.getLinkedProduct();
            if (isLinkedProductCard(card, CardCategoryEnum.DEBIT)) {
                if (isGreatThenZero(params.getMonthRefillSum())) { // first refill go to first month, other refill go to credit repayment
                    totalSum = totalSum.add(params.getMonthRefillSum());
                    params.setMonthRefillSum(ZERO);
                }
            }
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

            if (cardOption.getBonusOption() == BonusOptionEnum.SAVING) {
                maxRate = maxRate.add(cardOption.getRate());
            }
        }
        BigDecimal optionTotalProfit = calculateTotalOptionProfit4Period(cardOption, startDate, endDate);

        return ProductCalculateResult.builder()
                .totalSum(totalSum.add(refillSum).setScale(0, RoundingMode.HALF_UP))
                .profitSum(totalProfit.setScale(0, RoundingMode.HALF_UP))
                .optionProfitSum(optionTotalProfit)
                .maxRate(maxRate)
                .daysCount(daysCount)
                .product(request.getProduct())
                .recommendation(request.isRecommendation())
                .offerByClientProduct(offerByClientProduct)
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
            return cardOption.getCashback4Month().multiply(months).setScale(0, RoundingMode.HALF_UP);
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
}
