package ru.tn.profitcalculator.web.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.CardCategoryEnum;
import ru.tn.profitcalculator.model.enums.CardTypeEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.model.enums.ProductStatusEnum;
import ru.tn.profitcalculator.web.comparator.ProductResponseComparator;
import ru.tn.profitcalculator.web.model.ProductDto;
import ru.tn.profitcalculator.web.model.ProductResponse;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.singleton;

@RestController
@RequestMapping("/products")
public class ProductController {

    @GetMapping
    public Set<ProductResponse> findProducts(
            @RequestParam BigDecimal totalSum,
            @RequestParam Integer monthsCount,
            @RequestParam(required = false) BigDecimal monthRefillSum,
            @RequestParam(required = false) BigDecimal monthWithdrawalSum,
            @RequestParam(required = false) List<PosCategoryEnum> costCategories ) {

        ProductResponse productResponse = new ProductResponse();
        productResponse.setMaxRate(BigDecimal.TEN);
        productResponse.setProfitSum(BigDecimal.valueOf(15900));

        SavingAccount savingAccProduct = new SavingAccount();
        savingAccProduct.setName("Накопительный счет");
        savingAccProduct.setStatus(ProductStatusEnum.ACTIVE);
        savingAccProduct.setWeight(2);

        Card card = new Card();
        card.setWeight(2);
        card.setName("Карточный продукт");
        card.setCardType(CardTypeEnum.MIR);
        card.setCardCategory(CardCategoryEnum.DEBIT);
        card.setBonusOption(BonusOptionEnum.TRAVEL);

        productResponse.setProducts(Arrays.asList(
                new ProductDto(savingAccProduct, singleton("Очень выгодный продукт")),
                new ProductDto(card, singleton("Очень выгодная карта"))
        ));

        Set<ProductResponse> result = new TreeSet<>(new ProductResponseComparator());
        result.add(productResponse);
        return result;
    }

}
