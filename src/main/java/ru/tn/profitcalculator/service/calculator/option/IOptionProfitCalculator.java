package ru.tn.profitcalculator.service.calculator.option;

import org.springframework.data.util.Pair;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import java.math.BigDecimal;
import java.util.Map;

public interface IOptionProfitCalculator {

    /**
     * Вычисление ставки и суммы профита по подключенной опции мультикарты
     *
     * @param cardOption опция карты
     * @param categories2Costs затраты по категориям трат
     * @return исходный объект опции со ставкой и суммой профита (кэшбека)
     */
    CardOption calculate(CardOption cardOption, Map<PosCategoryEnum, Pair<Boolean, BigDecimal>> categories2Costs);

    BonusOptionEnum getOption();
}
