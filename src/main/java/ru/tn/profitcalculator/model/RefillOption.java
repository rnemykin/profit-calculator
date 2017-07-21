package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.RefillOptionEventTypeEnum;
import ru.tn.profitcalculator.model.enums.RefillOptionSumTypeEnum;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class RefillOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Enumerated(EnumType.STRING)
    private RefillOptionEventTypeEnum eventType;

    @Enumerated(EnumType.STRING)
    private RefillOptionSumTypeEnum refillSumType;
}
