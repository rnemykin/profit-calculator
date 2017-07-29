package ru.tn.profitcalculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.repository.SettingRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;

@Service
@Transactional
public class SettingsService {

    @Autowired
    private SettingRepository settingRepository;

    public BigDecimal getBigDecimal(String key) {
        return valueOf(settingRepository.findByKey(key).orElseThrow(EntityNotFoundException::new).getIntValue());
    }

    public Integer getInt(String key) {
        return settingRepository.findByKey(key).orElseThrow(EntityNotFoundException::new).getIntValue();
    }
}
