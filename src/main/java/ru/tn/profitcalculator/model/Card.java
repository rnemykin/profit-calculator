package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.CardCategoryEnum;
import ru.tn.profitcalculator.model.enums.CardTypeEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

@Data
public class Card extends Product {
    public Card() {
        setType(ProductTypeEnum.CARD);
    }

    private CardTypeEnum cardType;
    private CardCategoryEnum category;
    private BonusOptionEnum bonusOption;
}
