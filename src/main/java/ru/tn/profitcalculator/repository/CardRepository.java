package ru.tn.profitcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;

import java.util.List;

@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    @Query("from Card c join fetch c.cardOptions opt where opt.option in (:options)")
    List<Card> findAllByCategories(@Param("options") List<BonusOptionEnum> options);
}
