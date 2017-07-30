package ru.tn.profitcalculator.steps;

import cucumber.api.java.ru.А;
import cucumber.api.java.ru.Допустим;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.То;
import ru.tn.profitcalculator.comparator.ProductGroupComparator;
import ru.tn.profitcalculator.model.Card;
import ru.tn.profitcalculator.model.Deposit;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.SavingAccount;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.web.model.ProductGroup;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RangingSteps {

    private ProductGroup.ProductGroupBuilder builder = ProductGroup.builder();

    @Допустим("^есть предложение с пакетом продуктов (.+)$")
    public void естьПредложениеСОднимПродуктом(String productPackage) {
        String[] packageData = productPackage.split(",");

        List<Product> products = new ArrayList<>();
        for (String productData : packageData) {

            Product product = null;
            ProductTypeEnum productType = ProductTypeEnum.valueOf(productData.split(":")[0]);

            switch (productType) {
                case SAVING_ACCOUNT:
                    product = new SavingAccount();
                    break;
                case DEPOSIT:
                    product = new Deposit();
                    break;
                case CARD:
                    product = new Card();
            }
            product.setWeight(Integer.valueOf(productData.split(":")[1]));
            products.add(product);
        }

        builder.products(products);
    }

    @Когда("^сумма профита по продукту равна (.+)$")
    public void суммаПрофитаПоПродуктуРавнаПрофит_продукта(BigDecimal profitSum) {
        builder.profitSum(profitSum);
    }

    @И("^максимальная ставка равна (.+)$")
    public void максимальнаяСтавкаРавнаСтавка(BigDecimal maxRate) {
        builder.maxRate(maxRate);
    }

    @А("^сумма профита по опции Мультикарты равна (.+)$")
    public void суммаПрофитаПоОпцииМультикартыРавнаПрофит_опции(BigDecimal optionProfitSum) {
        builder.optionProfitSum(optionProfitSum);
    }

    @То("^значение ранга по данному предложению равно (.+)$")
    public void значениеРангаПоДанномуПредложениюРавноРанг(BigDecimal rank) {
        ProductGroup productGroup = builder.build();
        ProductGroupComparator comparator = new ProductGroupComparator();
        assertEquals(rank.doubleValue(), comparator.calculateRating(productGroup).doubleValue(), 0.01);
    }
}
