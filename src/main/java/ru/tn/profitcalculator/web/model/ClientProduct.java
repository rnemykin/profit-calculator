package ru.tn.profitcalculator.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientProduct {

    Long idProduct;

    /**
     * Минимальные остатки на счете на первые числа месяцев за последний год
     */
    Map<LocalDate, BigDecimal> accountBalances;
}
