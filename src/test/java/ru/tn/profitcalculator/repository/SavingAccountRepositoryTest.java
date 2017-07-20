package ru.tn.profitcalculator.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.tn.profitcalculator.model.Deposit;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.repository.specification.ProductFilter;
import ru.tn.profitcalculator.repository.specification.ProductSpecification;

import java.math.BigDecimal;
import java.util.List;

import static ru.tn.profitcalculator.repository.specification.ProductFilter.depositFilter;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class SavingAccountRepositoryTest {
    @Autowired
    private SavingAccountRepository savingAccountRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private DepositRepository depositRepository;


    @Test
    public void findAll() {
        ProductFilter filter = depositFilter()
                .type(ProductTypeEnum.DEPOSIT)
                .monthsCount(5)
                .monthRefillSum(BigDecimal.ONE)
                .build();
        List<Deposit> all = depositRepository.findAll(new ProductSpecification<>(filter));

        log.info("saving accounts", savingAccountRepository.findAll());
        log.info("cards", cardRepository.findAll());
        log.info("deposits", depositRepository.findAll());
    }
}
