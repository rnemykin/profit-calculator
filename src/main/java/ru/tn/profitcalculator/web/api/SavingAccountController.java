package ru.tn.profitcalculator.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.repository.ProductRateRepository;
import ru.tn.profitcalculator.repository.SavingAccountRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/savingAccounts")
@Slf4j
public class SavingAccountController {
    private final SavingAccountRepository savingAccountRepository;
    private final ProductRateRepository productRateRepository;

    @Autowired
    public SavingAccountController(SavingAccountRepository savingAccountRepository, ProductRateRepository productRateRepository) {
        this.savingAccountRepository = savingAccountRepository;
        this.productRateRepository = productRateRepository;
    }

    @GetMapping
    public List<SavingAccount> getAll() {
        return savingAccountRepository.findAll();
    }

    @PostMapping
    public void save(@RequestBody List<SavingAccount> savingAccounts) {
        savingAccountRepository.save(savingAccounts);
        for (SavingAccount savingAccount : savingAccounts) {
            productRateRepository.save(savingAccount.getRates());
        }
    }

}
