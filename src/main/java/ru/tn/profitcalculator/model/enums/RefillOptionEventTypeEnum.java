package ru.tn.profitcalculator.model.enums;

/**
 * Тип события
 */
public enum RefillOptionEventTypeEnum {

    /**
     * просто фиксированная дата
     */
    FIXED_DATE,

    /**
     * любое поступление средств на счет (в т.ч. ЗП)
     */
    TRANSFER,

    /**
     * любая покупка по карте
     */
    CARD_PURCHASES
}
