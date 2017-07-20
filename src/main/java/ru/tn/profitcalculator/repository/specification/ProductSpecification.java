package ru.tn.profitcalculator.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.tn.profitcalculator.model.Product;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static ru.tn.profitcalculator.model.enums.ProductTypeEnum.CARD;
import static ru.tn.profitcalculator.model.enums.ProductTypeEnum.DEPOSIT;

public class ProductSpecification<T extends Product> implements Specification<T> {
    private ProductFilter filter;

    public ProductSpecification(ProductFilter filter) {
        this.filter = filter;
    }


    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if(CARD == filter.getType()) {

        } else {
            if (DEPOSIT == filter.getType()) {
                predicates.add(criteriaBuilder.le(root.get("minPeriod"), filter.getMonthsCount()));
                predicates.add(criteriaBuilder.ge(root.get("maxPeriod"), filter.getMonthsCount()));
                if(isGreatThenZero(filter.getMonthRefillSum())) {
                    predicates.add(criteriaBuilder.isTrue(root.get("refill")));
                }

                if(isGreatThenZero(filter.getMonthWithdrawalSum())) {
                    predicates.add(criteriaBuilder.isTrue(root.get("withdrawal")));
                }
            } else {

            }
        }
        return predicates.size() > 1
                ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                : predicates.get(0);
    }

    private boolean isGreatThenZero(BigDecimal source) {
        return source != null && source.compareTo(BigDecimal.ZERO) > 0;
    }
}
