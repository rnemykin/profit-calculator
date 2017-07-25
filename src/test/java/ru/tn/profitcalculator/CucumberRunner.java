package ru.tn.profitcalculator;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/java/features/",
        glue = {"classpath:ru/tn/profitcalculator/steps"},
        tags = {"~@Ignore"})
public class CucumberRunner {
}