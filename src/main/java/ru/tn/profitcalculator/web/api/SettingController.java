package ru.tn.profitcalculator.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tn.profitcalculator.model.Setting;
import ru.tn.profitcalculator.repository.SettingRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settings")
@Slf4j
public class SettingController {

    private final SettingRepository repository;

    @Autowired
    public SettingController(SettingRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Setting> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public void save(@RequestBody List<Setting> cards) {
        repository.save(cards);
    }
}
