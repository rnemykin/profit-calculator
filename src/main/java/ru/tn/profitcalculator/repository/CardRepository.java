package ru.tn.profitcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.enums.CardTypeEnum;

@RepositoryRestResource
public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {

    Card findFirstByCardTypeOrderByIdDesc(CardTypeEnum cardType);
}
