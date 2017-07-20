package ru.tn.profitcalculator.web.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.CardOption;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.model.enums.BonusOptionEnum;
import ru.tn.profitcalculator.model.enums.CardCategoryEnum;
import ru.tn.profitcalculator.model.enums.CardTypeEnum;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.model.enums.ProductStatusEnum;
import ru.tn.profitcalculator.web.comparator.ProductResponseComparator;
import ru.tn.profitcalculator.web.model.ProductGroup;
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
    public Set<ProductGroup> findProducts(
            @RequestParam BigDecimal sum,
            @RequestParam Integer monthsCount,
            @RequestParam(required = false) BigDecimal monthRefillSum,
            @RequestParam(required = false) BigDecimal monthWithdrawalSum,
            @RequestParam(required = false) List<PosCategoryEnum> costCategories) {

        ProductGroup productGroup = new ProductGroup();
        productGroup.setMaxRate(BigDecimal.TEN);
        productGroup.setProfitSum(BigDecimal.valueOf(15900));

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

        CardOption cardOption2 = new CardOption();
        cardOption.setOption(BonusOptionEnum.CASH_BACK);
        cardOption.setRate1(BigDecimal.ONE);
        cardOption.setRate2(BigDecimal.valueOf(2));
        cardOption.setRate3(BigDecimal.valueOf(3));
        card.setCardOptions(Arrays.asList(cardOption, cardOption2));

        productGroup.setProducts(Arrays.asList(
                new ProductResponse(savingAccProduct, singleton("Очень выгодный продукт")),
                new ProductResponse(card, singleton("Очень выгодная карта"))
        ));

        Set<ProductGroup> result = new TreeSet<>(new ProductResponseComparator());
        result.add(productGroup);
        return result;
    }

}
