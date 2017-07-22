package ru.tn.profitcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.tn.profitcalculator.model.DepositRate;

import java.util.List;

@RepositoryRestResource
public interface DepositRateRepository extends JpaRepository<DepositRate, Long> {
    @Query("from DepositRate dr where dr.depositId = ?1 and dr.fromDay <= ?2 order by dr.fromDay desc")
    List<DepositRate> findDepositRate(Long depositId, Integer period);

}
