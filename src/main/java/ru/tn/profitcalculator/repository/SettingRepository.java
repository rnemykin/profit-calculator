package ru.tn.profitcalculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.tn.profitcalculator.model.Setting;

import java.util.Optional;

@RepositoryRestResource
public interface SettingRepository extends JpaRepository<Setting, Long>, JpaSpecificationExecutor<Setting> {

    Optional<Setting> findByKey(String key);
}
