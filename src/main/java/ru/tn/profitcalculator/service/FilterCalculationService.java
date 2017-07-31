package ru.tn.profitcalculator.service;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.web.model.CalculateParams;
import ru.tn.profitcalculator.web.model.Transactions;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingDouble;
import static java.util.stream.Collectors.toMap;
import static ru.tn.profitcalculator.model.enums.PosCategoryEnum.AUTO;
import static ru.tn.profitcalculator.model.enums.PosCategoryEnum.FUN;
import static ru.tn.profitcalculator.model.enums.PosCategoryEnum.OTHER;
import static ru.tn.profitcalculator.model.enums.PosCategoryEnum.TRAVEL;

@Service
public class FilterCalculationService {

    private static final int MONTH_OF_YEAR = 12;
    private static final double DEFAULT = 0;
    private static boolean TRIM_PEAKS = true;

    public CalculateParams calculateFilterParameter(CalculateParams params) {
        Map<PosCategoryEnum, Double> averageMonthCost2Category;
        if (TRIM_PEAKS) {
            averageMonthCost2Category = params.getTransactions().stream()
                    .flatMap(v -> v.getCategory2Cost().entrySet().stream())
                    .collect(groupingBy(Map.Entry::getKey,
                            averagingDouble(e -> e.getValue().doubleValue())));
        } else {
            Map<PosCategoryEnum, DoubleSummaryStatistics> collect =
                    params.getTransactions().stream()
                            .flatMap(v -> v.getCategory2Cost().entrySet().stream())
                            .collect(groupingBy(Map.Entry::getKey,
                                    summarizingDouble(e -> e.getValue().doubleValue())));
            averageMonthCost2Category = collect.entrySet().stream()
                    .collect(toMap(Map.Entry::getKey,
                            s -> Math.max((s.getValue().getSum() - s.getValue().getMax() - s.getValue().getMin()) / params.getTransactions().size(), DEFAULT)
                    ));
        }
        double averagePayroll = params.getPayroll().stream()
                .mapToDouble(d -> d.getSum().doubleValue()).average().orElse(DEFAULT);

        double cost = averageMonthCost2Category.values().stream().mapToDouble(d -> d).sum();
        Double oneMonthAccumulation = averagePayroll - cost;


        return CalculateParams.builder()
                .initSum(BigDecimal.valueOf(oneMonthAccumulation * MONTH_OF_YEAR / 2))   //todo Подумать сколько в качестве первоначального взноса давать вводить
                .monthRefillSum(BigDecimal.valueOf(oneMonthAccumulation))
                .daysCount(params.getDaysCount() != null ? params.getDaysCount() : 361) //todo берем из запроса среднюю продолжительность сберегательных продуктов клиента
                .creditCard(params.getCreditCard())
                .transactions(params.getTransactions())
                .payroll(params.getPayroll())
                .payrollProject(params.getPayrollProject())
                .categories2Costs(toCategory2Cost(averageMonthCost2Category))
                .build();
    }

    private Map<PosCategoryEnum, Pair<Boolean, BigDecimal>> toCategory2Cost(Map<PosCategoryEnum, Double> averageCost2Category) {
        TreeMap<PosCategoryEnum, Double> sorted = new TreeMap<>(new ValueComparator<>(averageCost2Category));
        sorted.putAll(averageCost2Category);
        Map<PosCategoryEnum, Pair<Boolean, BigDecimal>> result = new HashMap<>();
        for (Map.Entry<PosCategoryEnum, Double> entry : sorted.entrySet()) {
            Boolean importantCategory = !(result.size() > 1);
            result.put(entry.getKey(), Pair.of(importantCategory, BigDecimal.valueOf(entry.getValue())));
        }
        return result;
    }

    class ValueComparator<K, V extends Comparable<V>> implements Comparator<K> {
        HashMap<K, V> map = new HashMap<>();

        public ValueComparator(Map<K, V> map) {
            this.map.putAll(map);
        }

        @Override
        public int compare(K s1, K s2) {
            return -map.get(s1).compareTo(map.get(s2));
        }
    }
}
