package ru.tn.profitcalculator.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
        log.info("saving accounts", savingAccountRepository.findAll());
        log.info("cards", cardRepository.findAll());
        log.info("deposits", depositRepository.findAll());
    }
}
