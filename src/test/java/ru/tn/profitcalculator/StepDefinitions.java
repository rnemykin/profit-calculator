package ru.tn.profitcalculator;

import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Пусть;
import cucumber.api.java.ru.То;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;
import ru.tn.profitcalculator.service.calculator.impl.SavingAccountCalculator;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@ContextConfiguration(classes = ProfitCalculatorApplication.class, loader = SpringBootContextLoader.class)
public class StepDefinitions {

    @Autowired
    private SavingAccountCalculator savingAccountCalculator;

    private int spentMonthes;
    private BigDecimal initialSumm;

    @Пусть("^Ярик положил (\\d+) рублей на накопительный счет$")
    public void ярославПоложилРублейНаНакопительныйСчет(BigDecimal initialSumm) {
        this.initialSumm = initialSumm;
    }

    @Когда("^прошло (\\d+) месяцев$")
    public void прошлоМясяцМесяцев(int spentMonthes) {
        this.spentMonthes = spentMonthes;
    }

    @То("^итоговая % ставка \\(R\\) составила (.+)$")
    public void итоговаяСтавкаРавнаСтавка(BigDecimal rate) {
        assertEquals(rate.doubleValue(), savingAccountCalculator.getRate4Month(spentMonthes).doubleValue(), 0);
    }
}
