package ru.tn.profitcalculator.repository.specification;

import org.springframework.data.jpa.domain.Specification;
import ru.tn.profitcalculator.model.Product;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class DepositSpecification<T extends Product> implements Specification<T> {
    private ProductFilter filter;

    public DepositSpecification(ProductFilter filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.le(root.get("minPeriod"), filter.getMonthsCount()));
        predicates.add(criteriaBuilder.ge(root.get("maxPeriod"), filter.getMonthsCount()));
        if ((filter.isRefill())) {
            predicates.add(criteriaBuilder.isTrue(root.get("refill")));
        }

        if (filter.isWithdrawal()) {
            predicates.add(criteriaBuilder.isTrue(root.get("withdrawal")));
        }

        if (predicates.isEmpty()) {
            return null;
        }

        return predicates.size() > 1
                ? criteriaBuilder.and(predicates.toArray(new Predicate[0]))
                : predicates.get(0);
    }

}
