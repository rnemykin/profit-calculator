package ru.tn.profitcalculator.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.tn.profitcalculator.model.Deposit;
import ru.tn.profitcalculator.repository.DepositRepository;
import ru.tn.profitcalculator.repository.ProductRateRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deposits")
@Slf4j
public class DepositController {
    private final DepositRepository depositRepository;
    private final ProductRateRepository rateRepository;

    @Autowired
    public DepositController(DepositRepository depositRepository, ProductRateRepository rateRepository) {
        this.depositRepository = depositRepository;
        this.rateRepository = rateRepository;
    }

    @GetMapping
    public List<Deposit> getAll() {
        return depositRepository.findAll();
    }

    @PostMapping
    public  void saveAll(@RequestBody List<Deposit> deposits){
        depositRepository.save(deposits);
        for (Deposit deposit : deposits) {
            rateRepository.save(deposit.getRates());
        }
    }
}
