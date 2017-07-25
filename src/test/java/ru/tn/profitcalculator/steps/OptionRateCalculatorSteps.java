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
import ru.tn.profitcalculator.service.calculator.OptionRateCalculator;
import ru.tn.profitcalculator.service.calculator.OptionRateCalculatorFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ContextConfiguration(classes = ProfitCalculatorApplication.class, loader = SpringBootContextLoader.class)
public class OptionRateCalculatorSteps {

    @Autowired
    private OptionRateCalculatorFactory optionRateCalculatorFactory;

    @Autowired
    private CardOptionRepository cardOptionRepository;

    private OptionRateCalculator optionRateCalculator;
    private Map<PosCategoryEnum, BigDecimal> categories2Costs = new HashMap<>();
    private CardOption cardOption;

    @Пусть("^Ярик подключил для мультикарты опцию (\\w+)$")
    public void ярикПодключилДляМультикартыОпцию(BonusOptionEnum option) {
        optionRateCalculator = optionRateCalculatorFactory.get(option);
        cardOption = cardOptionRepository.findFirstByOptionOrderByIdDesc(option);
        assertNotNull(cardOption);
    }

    @И("^за месяц его POS-оборот по карте составил (.+) рублей$")
    public void заМесяцЕгоPOSОборотПоКартеСоставилРублей(BigDecimal sum) {
        categories2Costs.clear();
        categories2Costs.put(PosCategoryEnum.FUN, sum);
    }

    @Тогда("^надбавка к процентной ставке по накопительному счету равна (.+)%$")
    public void надбавкаКПроцентнойСтавкеПоНакопительномуСчетуРавна(BigDecimal rate) {
        BigDecimal calculatedRate = optionRateCalculator.calculate(cardOption, categories2Costs);
        assertEquals(rate.doubleValue(), calculatedRate.doubleValue(), 0);
    }
}
