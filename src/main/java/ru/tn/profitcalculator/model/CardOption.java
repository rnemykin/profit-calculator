package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;

@Data
@Entity
public class CardOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BonusOptionEnum option;
    private String name;
    private BigDecimal rate1;
    private BigDecimal rate2;
    private BigDecimal rate3;

    @Transient
    private BigDecimal rate;
}
