package ru.tn.profitcalculator.web.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.repository.CardRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
@Slf4j
public class CardController {
    private final CardRepository repository;

    @Autowired
    public CardController(CardRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Card> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public void save(@RequestBody List<Card> cards) {
        repository.save(cards);
    }
}
