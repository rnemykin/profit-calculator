package ru.tn.profitcalculator.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.Deposit;
import ru.tn.profitcalculator.model.ProductRate;
import ru.tn.profitcalculator.model.RefillOption;
import ru.tn.profitcalculator.model.Setting;
import ru.tn.profitcalculator.repository.CardOptionRepository;
import ru.tn.profitcalculator.repository.CardRepository;
import ru.tn.profitcalculator.repository.DepositRepository;
import ru.tn.profitcalculator.repository.ProductRateRepository;
import ru.tn.profitcalculator.repository.RefillOptionRepository;
import ru.tn.profitcalculator.repository.SettingRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settings")
@Slf4j
public class SettingController {

    private final SettingRepository settingRepository;
    private final CardOptionRepository cardOptionRepository;
    private final ProductRateRepository productRateRepository;
    private final CardRepository cardRepository;
    private final DepositRepository depositRepository;
    private final RefillOptionRepository refillOptionRepository;

    @Autowired
    public SettingController(SettingRepository settingRepository, CardOptionRepository cardOptionRepository, ProductRateRepository productRateRepository, CardRepository cardRepository, DepositRepository depositRepository, RefillOptionRepository refillOptionRepository) {
        this.settingRepository = settingRepository;
        this.cardOptionRepository = cardOptionRepository;
        this.productRateRepository = productRateRepository;
        this.cardRepository = cardRepository;
        this.depositRepository = depositRepository;
        this.refillOptionRepository = refillOptionRepository;
    }

    @GetMapping
    public List<Setting> getAll() {
        return settingRepository.findAll();
    }

    @PostMapping
    public void save(@RequestBody List<Setting> cards) {
        settingRepository.save(cards);
    }

    @GetMapping("card-options")
    public List<CardOption> getAllCardOptions() {
        return cardOptionRepository.findAll();
    }

    @PostMapping("card-options")
    public void saveCardOptions(@RequestBody List<CardOption> cardOptions) {
        cardOptionRepository.save(cardOptions);
    }

    @GetMapping("product-rates")
    public List<ProductRate> getAllProductRates() {
        return productRateRepository.findAll();
    }

    @PostMapping("product-rates")
    public void saveProductRates(@RequestBody List<ProductRate> productRates) {
        productRateRepository.save(productRates);
    }

    @GetMapping("cards")
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @PostMapping("cards")
    public void saveCards(@RequestBody List<Card> cards) {
        cardRepository.save(cards);
    }

    @GetMapping("deposits")
    public List<Deposit> getAllDeposits() {
        return depositRepository.findAll();
    }

    @PostMapping("deposits")
    public void saveDeposits(@RequestBody List<Deposit> deposits) {
        depositRepository.save(deposits);
    }

    @GetMapping("refill-options")
    public List<RefillOption> getAllRefillOptions() {
        return refillOptionRepository.findAll();
    }

    @PostMapping("refill-options")
    public void saveRefillOptions(@RequestBody List<RefillOption> RefillOption) {
        refillOptionRepository.save(RefillOption);
    }
}
