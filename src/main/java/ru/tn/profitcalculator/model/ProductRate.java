package ru.tn.profitcalculator.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import java.math.BigDecimal;

@Data
@Entity
public class ProductRate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_rate_id_seq")
    @SequenceGenerator(sequenceName = "product_rate_id_seq", allocationSize = 1, name = "product_rate_id_seq")
    private Long id;

    private Long productId;
    private BigDecimal rate;
    private Integer fromPeriod;
}
