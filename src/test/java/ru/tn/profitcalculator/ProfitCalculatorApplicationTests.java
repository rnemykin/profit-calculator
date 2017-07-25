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
import ru.tn.profitcalculator.service.calculator.Calculator;
import ru.tn.profitcalculator.service.calculator.CalculatorFactory;
import ru.tn.profitcalculator.service.calculator.ProductCalculateRequest;
import ru.tn.profitcalculator.service.calculator.ProductCalculateResult;
import ru.tn.profitcalculator.web.model.CalculateParams;

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
			ProductCalculateRequest calculateParams = ProductCalculateRequest.builder()
					.product(d)
					.params(
							CalculateParams.builder()
									.daysCount(181)
									.initSum(BigDecimal.valueOf(10000))
									.build()
					)
					.build();

			ProductCalculateResult result = calculator.calculate(calculateParams);
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
			ProductCalculateRequest request = ProductCalculateRequest.builder()
					.product(d)
					.params(CalculateParams.builder()
									.daysCount(daysCount)
									.initSum(BigDecimal.valueOf(200000))
									.build()
					)
					.build();

			ProductCalculateResult result = calculator.calculate(request);
			System.out.println(result);
		});

	}
}
