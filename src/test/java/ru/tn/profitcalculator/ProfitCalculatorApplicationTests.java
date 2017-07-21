package ru.tn.profitcalculator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.enums.PosCategoryEnum;
import ru.tn.profitcalculator.service.ProductService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfitCalculatorApplicationTests {

	@Autowired
	private ProductService productService;


	@Test
	public void findAll() {
		List<Product> products = productService.searchProducts(5, BigDecimal.ONE, BigDecimal.TEN, Collections.singletonList(PosCategoryEnum.AUTO));
		System.out.println(products);
	}
}
