package ru.tn.profitcalculator.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.CardCategoryEnum;
import ru.tn.profitcalculator.model.enums.CardTypeEnum;
import ru.tn.profitcalculator.model.enums.ProductStatusEnum;
import ru.tn.profitcalculator.service.CalculatorService;
import ru.tn.profitcalculator.web.comparator.ProductResponseComparator;
import ru.tn.profitcalculator.web.model.CalculateParams;
import ru.tn.profitcalculator.web.model.ProductGroup;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.singleton;

@RestController
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private CalculatorService calculatorService;


    @PostMapping
    public Set<ProductGroup> calculateProducts(@Valid @RequestBody CalculateParams request) {
        calculatorService.calculateOffers(request);
        return makeStubResponse();
    }

    private Set<ProductGroup> makeStubResponse() {
        ProductGroup productGroup = new ProductGroup();
        productGroup.setMaxRate(BigDecimal.TEN);
        productGroup.setProfitSum(BigDecimal.valueOf(15900));
        productGroup.setResultSum(BigDecimal.valueOf(250000));

        SavingAccount savingAccProduct = new SavingAccount();
        savingAccProduct.setName("Накопительный счет");
        savingAccProduct.setStatus(ProductStatusEnum.ACTIVE);
        savingAccProduct.setWeight(2);

        Card card = new Card();
        card.setWeight(2);
        card.setName("Карточный продукт");
        card.setCardType(CardTypeEnum.MIR);
        card.setCardCategory(CardCategoryEnum.DEBIT);

        CardOption cardOption = new CardOption();
        cardOption.setOption(BonusOptionEnum.TRAVEL);
        cardOption.setRate1(BigDecimal.valueOf(2));
        cardOption.setRate2(BigDecimal.valueOf(4));
        cardOption.setRate3(BigDecimal.valueOf(10));
        cardOption.setRate(BigDecimal.valueOf(500));
        card.setCardOption(cardOption);

        productGroup.setProducts(Arrays.asList(savingAccProduct, card));
        productGroup.setNotes(singleton("Очень выгодный продукт"));

        Set<ProductGroup> result = new TreeSet<>(new ProductResponseComparator());
        result.add(productGroup);
        return result;
    }

}
