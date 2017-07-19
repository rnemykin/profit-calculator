package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.CardCategoryEnum;
import ru.tn.profitcalculator.model.enums.CardTypeEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Entity
public class Card extends Product {
    public Card() {
        setType(ProductTypeEnum.CARD);
    }

    @Enumerated(EnumType.STRING)
    private CardTypeEnum cardType;

    @Enumerated(EnumType.STRING)
    private CardCategoryEnum cardCategory;

    @Enumerated(EnumType.STRING)
    private BonusOptionEnum bonusOption;
}
