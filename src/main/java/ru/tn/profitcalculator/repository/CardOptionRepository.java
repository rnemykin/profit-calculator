package ru.tn.profitcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;

@RepositoryRestResource
public interface CardOptionRepository extends JpaRepository<CardOption, Long>{

    CardOption findFirstByOptionOrderByIdDesc(BonusOptionEnum option);
}
