package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Data
@Entity
public class CardOption {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_option_id_seq")
    @SequenceGenerator(sequenceName = "card_option_id_seq", allocationSize = 1, name = "card_option_id_seq")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BonusOptionEnum bonusOption;
    private String name;
    private BigDecimal rate1;
    private BigDecimal rate2;
    private BigDecimal rate3;

    @Transient
    private BigDecimal rate;

    @Transient
    private BigDecimal cashback;
}
