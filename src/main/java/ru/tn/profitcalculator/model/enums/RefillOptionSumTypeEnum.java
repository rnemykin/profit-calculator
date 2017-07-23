package ru.tn.profitcalculator.model.enums;

/**
 * Сумма автопополнения
 */
public enum RefillOptionSumTypeEnum {

    /**
     * фиксированная сумма
     */
    FIXED_SUM,

    /**
     * ??? TODO в презе не было
     */
    FIXED_PERCENTAGE,

    /**
     * •%% от операций (поступившей/ потраченной суммы)
     */
    IN_OUT_SUM_PERCENTAGE,

    /**
     * сдача с покупки (разница между округленной до тысяч суммой покупки и фактической суммой покупки)
     */
    PAYMENT_CHANGE
}
