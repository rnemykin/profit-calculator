package ru.tn.profitcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.tn.profitcalculator.model.RefillOption;
import ru.tn.profitcalculator.model.enums.RefillOptionEventTypeEnum;
import ru.tn.profitcalculator.model.enums.RefillOptionSumTypeEnum;

@RepositoryRestResource
public interface RefillOptionRepository extends JpaRepository<RefillOption, Long> {
    RefillOption findByEventTypeAndRefillSumType(RefillOptionEventTypeEnum eventType, RefillOptionSumTypeEnum sumType);
}
