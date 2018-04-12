package aaa.helpers.openl.testdata_builder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLForm;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssertions;

public class HomeSSFormTestDataGenerator {
	private static Map<String, List<String>> selectedForms = new HashMap<>();
	private static BiFunction<HomeSSOpenLForm, String, TestData> formHS0420DataFunction = (openlForm, policyLevel) -> DataProviderFactory.dataOf(
			HomeSSMetaData.EndorsementTab.EndorsementDS0420.AMOUNT_OF_INSURANCE.getLabel(), openlForm.getCovPercentage() + "%");

	private static BiFunction<HomeSSOpenLForm, String, TestData> formHS0495DataFunction = (openlForm, policyLevel) -> DataProviderFactory.dataOf(
			HomeSSMetaData.EndorsementTab.EndorsementDS0495.COVERAGE_LIMIT.getLabel(), "$" + openlForm.getLimit().toString().split("\\.")[0]);
	//TODO: add functions for other forms...

	public static TestData getFormTestData(HomeSSOpenLForm openLForm, String policyLevel) {
		return getFormEnum(openLForm.getFormCode()).getTestData(openLForm, policyLevel);
	}

	public static String getFormMetaKey(String formCode) {
		return getFormEnum(formCode).getMetaKey();
	}

	public static boolean isFormAdded(String formCode, String policyLevel) {
		if (selectedForms.isEmpty()) {
			String getFormsQuery = "select COMPONENTREFNAME from POLICYPLANCOMPONENT where DTYPE='PolicyPlanEndorsementForm' and FORM_POLICYPLAN_ID in (select ID from POLICYPLAN where name=?) and availability = 'MANDATORY'";
			for (String pLevel : Arrays.asList("Heritage", "Legacy", "Prestige")) {
				List<String> forms = DBService.get().getColumn(getFormsQuery, pLevel);
				forms = forms.stream().map(f -> f.replaceAll("EndorsementForm.*", "")).collect(Collectors.toList());
				selectedForms.put(pLevel, forms);
			}
		}
		CustomAssertions.assertThat(selectedForms).as("Unknown %s policy level", policyLevel).containsKey(policyLevel);
		return selectedForms.get(policyLevel).contains(formCode);
	}

	private static Forms getFormEnum(String formCode) {
		return Arrays.stream(Forms.values()).filter(f -> f.getFormCode().equals(formCode)).findFirst().orElseThrow(() -> new IstfException("There is no Form enum with form code: " + formCode));
	}

	/*public TestData formHS0435Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}*/

	/*public TestData formHS0420Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		int amount = openLPolicy.getForms().stream().filter(c -> formCode.equals(c.getFormCode())).findFirst().get().getCovPercentage();
		TestData formData = DataProviderFactory.dataOf(
				"Action", selectedForms.get(formCode).contains(openLPolicy.getLevel()) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementDS0420.AMOUNT_OF_INSURANCE.getLabel(), amount + "%"
		);
		return DataProviderFactory.dataOf(HomeSSMetaData.EndorsementTab.HS_04_20.getLabel(), formData);
	}*/

	/*public TestData formHS0436Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
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

	public TestData formHS0934Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		TestData formData = DataProviderFactory.dataOf(
				"Action", selectedForms.get(formCode).contains(openLPolicy.getLevel()) ? "Edit" : "Add"
		);
		return DataProviderFactory.dataOf(HomeSSMetaData.EndorsementTab.HS_09_34.getLabel(), formData);
	}*/

	/*public TestData formHS0495Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		Double limit = openLPolicy.getForms().stream().filter(c -> formCode.equals(c.getFormCode())).findFirst().get().getLimit();
		TestData formData = DataProviderFactory.dataOf(
				"Action", selectedForms.get(formCode).contains(openLPolicy.getLevel()) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementDS0495.COVERAGE_LIMIT.getLabel(), "$" + limit.toString().split("\\.")[0]
		);
		return DataProviderFactory.dataOf(HomeSSMetaData.EndorsementTab.HS_04_95.getLabel(), formData);
	}*/

	/*public TestData formHS0965Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS2464Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}

	public TestData formHS2472Data(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return DataProviderFactory.emptyData();
	}*/

	public enum Forms {
		HS0420(HomeSSMetaData.EndorsementTab.HS_04_20.getLabel(), "HS0420", formHS0420DataFunction),
		HS0495(HomeSSMetaData.EndorsementTab.HS_04_95.getLabel(), "HS0495", formHS0495DataFunction);
		//TODO: add other forms...
		/*selectedForms.put("HS0420", "Heritage, Legacy, Prestige");
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
		selectedForms.put("HS0929", "Prestige");*/

		private final String metaKey;
		private final String formCode;
		private final BiFunction<HomeSSOpenLForm, String, TestData> testDataFunction;

		Forms(String metaKey, String formCode, BiFunction<HomeSSOpenLForm, String, TestData> testDataFunction) {
			this.metaKey = metaKey;
			this.formCode = formCode;
			this.testDataFunction = testDataFunction;
		}

		public String getMetaKey() {
			return metaKey;
		}

		public String getFormCode() {
			return formCode;
		}

		public BiFunction<HomeSSOpenLForm, String, TestData> getTestDataFunction() {
			return testDataFunction;
		}

		public TestData getTestData(HomeSSOpenLForm openLForm, String policyLevel) {
			TestData td = getTestDataFunction().apply(openLForm, policyLevel);
			td.adjust("Action", isFormAdded(getFormCode(), policyLevel) ? "Edit" : "Add");
			return DataProviderFactory.dataOf(getMetaKey(), td);
		}
	}
}
