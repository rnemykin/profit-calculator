package ru.tn.profitcalculator.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientProduct {

    Long idProduct;

    /**
     * Минимальные остатки на счете на первые числа месяцев за последний год
     */
    List<Pair<LocalDate, BigDecimal>> accountBalances;
}
