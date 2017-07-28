package ru.tn.profitcalculator.steps;

import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Пусть;
import cucumber.api.java.ru.Тогда;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;
import ru.tn.profitcalculator.ProfitCalculatorApplication;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.repository.CardOptionRepository;
import ru.tn.profitcalculator.service.calculator.option.IOptionProfitCalculator;
import ru.tn.profitcalculator.service.calculator.OptionProfitCalculatorFactory;
import ru.tn.profitcalculator.service.calculator.option.impl.SavingOptionProfitCalculator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = ProfitCalculatorApplication.class, loader = SpringBootContextLoader.class)
public class OptionProfitCalculatorSteps {

    @Autowired
    private OptionProfitCalculatorFactory optionProfitCalculatorFactory;

    @Autowired
    private CardOptionRepository cardOptionRepository;

    private IOptionProfitCalculator optionProfitCalculator;
    private Map<PosCategoryEnum, BigDecimal> categories2Costs = new HashMap<>();
    private CardOption cardOption;

    @Пусть("^Ярик подключил для мультикарты опцию (\\w+)$")
    public void ярикПодключилДляМультикартыОпцию(BonusOptionEnum option) {
        optionProfitCalculator = optionProfitCalculatorFactory.get(option);
        cardOption = cardOptionRepository.findFirstByBonusOptionOrderByIdDesc(option);
        assertNotNull(cardOption);
    }

    @И("^за месяц его POS-оборот по карте составил (.+) рублей$")
    public void заМесяцЕгоPOSОборотПоКартеСоставилРублей(BigDecimal sum) {
        categories2Costs.clear();
        categories2Costs.put(PosCategoryEnum.FUN, sum);
    }

    @Тогда("^надбавка к процентной ставке по накопительному счету равна (.+)%$")
    public void надбавкаКПроцентнойСтавкеПоНакопительномуСчетуРавна(BigDecimal rate) {
        cardOption = optionProfitCalculator.calculate(cardOption, categories2Costs);
        assertEquals(rate.doubleValue(), cardOption.getRate().doubleValue() * 100, 0);
    }

    @И("^за месяц совершил покупки в категории Авто на сумму (.+) рублей, а также по другим категориям на сумму (.+) рублей$")
    public void заМесяцСовершилПокупкиВКатегорииАвтоАТакжеПоДругимКатегориям(BigDecimal autoSum, BigDecimal otherSum) {
        categories2Costs.clear();
        categories2Costs.put(PosCategoryEnum.AUTO, autoSum);
        categories2Costs.put(PosCategoryEnum.OTHER, otherSum);
    }

    @И("^за месяц совершил покупки в категории Развлечения на сумму (.+) рублей, а также по другим категориям на сумму (.+) рублей$")
    public void заМесяцСовершилПокупкиВКатегорииРазвлеченияАТакжеПоДругимКатегориям(BigDecimal autoSum, BigDecimal otherSum) {
        categories2Costs.clear();
        categories2Costs.put(PosCategoryEnum.FUN, autoSum);
        categories2Costs.put(PosCategoryEnum.OTHER, otherSum);
    }

    @Тогда("^максимальная ставка по кешбеку составит (.+)$")
    public void максимальнаяСтавкаПоКешбекуСоставитСтавка(BigDecimal maxRate) {
        cardOption = optionProfitCalculator.calculate(cardOption, categories2Costs);
        assertEquals(maxRate.doubleValue(), cardOption.getRate().doubleValue() * 100, 0);
    }

    @И("^общая сумма кешбека составит (.+) рублей$")
    public void общаяСуммаКешбекаСоставитCashbackРублей(BigDecimal cashback) {
        assertEquals(cashback.doubleValue(), cardOption.getCashback4Month().doubleValue(), 0);
    }

    @И("^сумма надбавки к накопительному счету на сумму (.+) рублей за месяц составит (.+) рублей$")
    public void суммаНадбавкиКНакопительномуСчетуНаСуммуРублейСоставитПрофитРублей(BigDecimal accountSum, BigDecimal optionProfitSum) {
        SavingOptionProfitCalculator calculator = (SavingOptionProfitCalculator) this.optionProfitCalculator;
        BigDecimal days = BigDecimal.valueOf(30);
        assertEquals(optionProfitSum.doubleValue(), calculator.calculateProfitSum(accountSum, cardOption.getRate(), days).doubleValue(), 0);
    }

    @Тогда("^лучший коэффициент по кешбеку составит (.+) рублей$")
    public void лучшийКоэффициентПоКешбекуСоставитСтавкаРублей(BigDecimal rate) {
        cardOption = optionProfitCalculator.calculate(cardOption, categories2Costs);
        assertEquals(rate.longValue(), cardOption.getRate().longValue());
    }
}
