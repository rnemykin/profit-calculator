package ru.tn.profitcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.tn.profitcalculator.model.Deposit;

@RepositoryRestResource
public interface DepositRepository extends JpaRepository<Deposit, Long>, JpaSpecificationExecutor<Deposit> {
}
