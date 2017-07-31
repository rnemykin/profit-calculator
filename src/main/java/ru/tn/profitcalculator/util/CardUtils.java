package ru.tn.profitcalculator.util;

import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;

import java.util.ArrayList;
import java.util.List;

public class CardUtils {

    public static List<BonusOptionEnum> getBonusOptionsByPosCategories(PosCategoryEnum category) {
        List<BonusOptionEnum> bonusOptions = new ArrayList<>();
        switch (category) {
            case AUTO:
                bonusOptions.add(BonusOptionEnum.AUTO);
                break;

            case FUN:
                bonusOptions.add(BonusOptionEnum.FUN);
                break;

            case TRAVEL:
                bonusOptions.add(BonusOptionEnum.TRAVEL);
                break;

            case OTHER:
            default:
                bonusOptions.add(BonusOptionEnum.SAVING);
                bonusOptions.add(BonusOptionEnum.CASH_BACK);
                bonusOptions.add(BonusOptionEnum.TRAVEL);
                bonusOptions.add(BonusOptionEnum.COLLECTION);
        }

        return bonusOptions;
    }

}
