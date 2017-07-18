package ru.tn.profitcalculator.model;

import lombok.Data;
import ru.tn.profitcalculator.model.enums.ProductStatusEnum;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String name;
    private ProductStatusEnum status;
    private LocalDate createDate;
    private LocalDateTime updateDate;
    private LocalDate archiveDate;
    private ProductTypeEnum type;
    private Integer weight;
}
