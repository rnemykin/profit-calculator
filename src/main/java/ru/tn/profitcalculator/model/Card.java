package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.CardCategoryEnum;
import ru.tn.profitcalculator.model.enums.CardTypeEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

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

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "cardId")
    private List<CardOption> cardOptions;
}
