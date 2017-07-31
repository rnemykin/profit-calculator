package ru.tn.profitcalculator.steps;

import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Пусть;
import cucumber.api.java.ru.То;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;
import ru.tn.profitcalculator.ProfitCalculatorApplication;
import ru.tn.profitcalculator.service.calculator.impl.SavingAccountCalculator;

import javax.transaction.Transactional;
import java.math.BigDecimal;

public class SavingAccountSteps {

    private SavingAccountCalculator savingAccountCalculator;

    private int spentMonthes;
    private BigDecimal initialSumm;

    @Пусть("^Ярик положил (\\d+) рублей на накопительный счет$")
    public void ярикПоложилДеньгиНаНакопительныйСчет(BigDecimal initialSumm) {
        this.initialSumm = initialSumm;
    }

    @Когда("^прошло (\\d+) месяцев$")
    public void прошлоМесяцев(int spentMonthes) {
        this.spentMonthes = spentMonthes;
    }

    @То("^итоговая % ставка \\(R\\) составила (.+)$")
    public void итоговаяСтавкаСоставила(BigDecimal rate) {

    }
}
