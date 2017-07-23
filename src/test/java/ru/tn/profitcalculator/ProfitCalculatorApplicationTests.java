package ru.tn.profitcalculator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.tn.profitcalculator.model.Product;
import ru.tn.profitcalculator.model.enums.ProductTypeEnum;
import ru.tn.profitcalculator.repository.DepositRepository;
import ru.tn.profitcalculator.repository.SavingAccountRepository;
import ru.tn.profitcalculator.service.ProductService;
import ru.tn.profitcalculator.service.calculator.CalculateRequest;
import ru.tn.profitcalculator.service.calculator.Calculator;
import ru.tn.profitcalculator.service.calculator.CalculatorFactory;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfitCalculatorApplicationTests {

	@Autowired
	private ProductService productService;

	@Autowired
	CalculatorFactory calculatorFactory;

	@Autowired
	DepositRepository depositRepository;

	@Autowired
	SavingAccountRepository savingAccountRepository;

	@Test
	public void findProducts() {
		List<Product> products = productService.searchProducts(5, BigDecimal.ONE, BigDecimal.TEN);
		System.out.println(products);
	}

	@Test
	public void savingAccountCalculate() {
		Calculator depositCalculator = calculatorFactory.get(ProductTypeEnum.SAVING_ACCOUNT);
		savingAccountRepository.findAll().forEach(d -> System.out.println(
				depositCalculator.calculate(
						CalculateRequest.builder()
								.product(d)
								.daysCount(365)
								.monthRefillSum(BigDecimal.valueOf(20000))
								.initSum(BigDecimal.valueOf(200000))
								.build()
				)
		));

	}
}
