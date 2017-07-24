package ru.tn.profitcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.tn.profitcalculator.model.ProductRate;

import java.util.List;

@RepositoryRestResource
public interface ProductRateRepository extends JpaRepository<ProductRate, Long> {
    @Query("from ProductRate dr where dr.productId = ?1 and dr.fromPeriod <= ?2 order by dr.fromPeriod desc")
    List<ProductRate> findDepositRate(Long depositId, Integer period);

    List<ProductRate> findAllByProductId(Long productId);

}
