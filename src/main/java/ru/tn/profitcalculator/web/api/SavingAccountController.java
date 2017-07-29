package ru.tn.profitcalculator.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tn.profitcalculator.model.Deposit;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.repository.SavingAccountRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/savingAccounts")
@Slf4j
public class SavingAccountController {
    private final SavingAccountRepository repository;

    @Autowired
    public SavingAccountController(SavingAccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<SavingAccount> getAll() {
        return repository.findAll();
    }

}
