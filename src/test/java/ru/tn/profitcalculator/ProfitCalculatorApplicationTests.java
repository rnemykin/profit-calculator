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
import ru.tn.profitcalculator.repository.specification.DepositSpecification;
import ru.tn.profitcalculator.repository.specification.ProductFilter;
import ru.tn.profitcalculator.service.ProductService;
import ru.tn.profitcalculator.service.calculator.CalculateResult;
import ru.tn.profitcalculator.service.calculator.Calculator;
import ru.tn.profitcalculator.service.calculator.CalculatorFactory;
import ru.tn.profitcalculator.web.model.CalculateRequest;

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
		Calculator calculator = calculatorFactory.get(ProductTypeEnum.SAVING_ACCOUNT);
		savingAccountRepository.findAll().forEach(d -> {
			CalculateRequest calculateRequest = CalculateRequest.builder()
					.product(d)
					.daysCount(181)
//					.monthRefillSum(BigDecimal.valueOf(15000))
					.initSum(BigDecimal.valueOf(10000))
					.build();

			CalculateResult result = calculator.calculate(calculateRequest);
			System.out.println(result);
		});

	}

	@Test
	public void depositCalculate() {
        int daysCount = 1830;
        Calculator calculator = calculatorFactory.get(ProductTypeEnum.DEPOSIT);
        depositRepository.findAll(new DepositSpecification<>(
				ProductFilter.builder()
						.daysCount(daysCount)
						.build()
		)).forEach(d -> {
			CalculateRequest calculateRequest = CalculateRequest.builder()
					.product(d)
					.daysCount(daysCount)
					.initSum(BigDecimal.valueOf(200000))
					.build();

			CalculateResult result = calculator.calculate(calculateRequest);
			System.out.println(result);
		});

	}
}
