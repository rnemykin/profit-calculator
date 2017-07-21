package ru.tn.profitcalculator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.Deposit;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.repository.CardRepository;
import ru.tn.profitcalculator.repository.DepositRepository;
import ru.tn.profitcalculator.repository.SavingAccountRepository;
import ru.tn.profitcalculator.repository.specification.DepositSpecification;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static ru.tn.profitcalculator.repository.specification.ProductFilter.depositFilter;

@Service
public class ProductService {
    private final CardRepository cardRepository;
    private final DepositRepository depositRepository;
    private final SavingAccountRepository savingAccountRepository;
    private final CardService cardService;

    @Autowired
    public ProductService(CardRepository cardRepository, DepositRepository depositRepository, SavingAccountRepository savingAccountRepository, CardService cardService) {
        this.cardRepository = cardRepository;
        this.depositRepository = depositRepository;
        this.savingAccountRepository = savingAccountRepository;
        this.cardService = cardService;
    }

    public List<Product> searchProducts(int monthsCount, BigDecimal refillSum, BigDecimal withdrawalSum) {
        List<Deposit> deposits = depositRepository.findAll(new DepositSpecification<>(
                depositFilter()
                        .daysCount(monthsCount)
                        .refill(isGreatThenZero(refillSum))
                        .withdrawal(isGreatThenZero(withdrawalSum))
                        .build())
        );

        List<SavingAccount> savingAccounts = savingAccountRepository.findAll();
        List<Card> cards = cardRepository.findAll();
        return Stream.of(deposits, savingAccounts, cards).flatMap(Collection::stream).collect(toList());
    }

    private boolean isGreatThenZero(BigDecimal source) {
        return source != null && source.compareTo(BigDecimal.ZERO) > 0;
    }

}
