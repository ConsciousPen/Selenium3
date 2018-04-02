package aaa.helpers.openl.testdata_builder;

import aaa.helpers.openl.model.home_ss.HomeSSOpenLForm;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.modules.BaseTest;
import scala.util.parsing.combinator.testing.Str;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeSSFormTestDataGenerator {

	Map<String, String> selectedForms = new HashMap<>();

	public HomeSSFormTestDataGenerator() {
		selectedForms.put("HS0420", "Heritage, Legacy, Prestige");
		selectedForms.put("HS0435", "Legacy, Prestige");
		selectedForms.put("HS0455", "Legacy, Prestige");
		selectedForms.put("HS0465", "Legacy, Prestige");
		selectedForms.put("HS0490", "Legacy, Prestige");
		selectedForms.put("HS0495", "Legacy, Prestige");
		selectedForms.put("HS0906", "Legacy, Prestige");
		selectedForms.put("HS0926", "Legacy, Prestige");
		selectedForms.put("HS0931", "Prestige");
		selectedForms.put("HS0934", "Prestige");
		selectedForms.put("HS0965", "Legacy, Prestige");
		selectedForms.put("HS0477", "Prestige");
		selectedForms.put("HS0927", "Legacy, Prestige");
		selectedForms.put("HS0929", "Prestige");

	}

	public TestData formHS0420Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		int amount = openLPolicy.getForms().stream().filter(c -> formCode.equals(c.getFormCode())).findFirst().get().getCovPercentage();
		TestData formData = DataProviderFactory.dataOf(
				"Action", selectedForms.get(formCode).contains(openLPolicy.getLevel()) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementDS0420.AMOUNT_OF_INSURANCE.getLabel(), amount + "%"
		);
		return DataProviderFactory.dataOf(HomeSSMetaData.EndorsementTab.HS_04_20.getLabel(), formData);
	}

	public TestData formHS0435Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS0436Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS0453Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS0454Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS0455Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS0459Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS0461Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS0465Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS0490Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS0492Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS0495Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		Double limit = openLPolicy.getForms().stream().filter(c -> formCode.equals(c.getFormCode())).findFirst().get().getLimit();
		TestData formData = DataProviderFactory.dataOf(
				"Action", selectedForms.get(formCode).contains(openLPolicy.getLevel()) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementDS0495.COVERAGE_LIMIT.getLabel(), "$" + limit.toString().split("\\.")[0]
		);
		return DataProviderFactory.dataOf(HomeSSMetaData.EndorsementTab.HS_04_95.getLabel(), formData);
	}

	public TestData formHS0934Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		TestData formData = DataProviderFactory.dataOf(
				"Action", selectedForms.get(formCode).contains(openLPolicy.getLevel()) ? "Edit" : "Add"
		);
		return DataProviderFactory.dataOf(HomeSSMetaData.EndorsementTab.HS_09_34.getLabel(), formData);
	}

	public TestData formHS0965Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS2464Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS2472Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}
}
