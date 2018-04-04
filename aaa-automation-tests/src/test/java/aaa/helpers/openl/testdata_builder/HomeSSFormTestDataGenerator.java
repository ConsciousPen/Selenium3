package aaa.helpers.openl.testdata_builder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLForm;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssertions;

public class HomeSSFormTestDataGenerator {
	private static Map<String, List<String>> selectedForms = new HashMap<>();

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0420DataFunction = (openLPolicy, policyLevel) -> DataProviderFactory.dataOf(
			HomeSSMetaData.EndorsementTab.EndorsementDS0420.AMOUNT_OF_INSURANCE.getLabel(), openLPolicy.getForms().stream().filter(c -> "HS0420".equals(c.getFormCode())).findFirst().get().getCovPercentage() + "%");

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0435DataFunction = (openLPolicy, policyLevel) -> DataProviderFactory.emptyData();

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0436DataFunction = (openLPolicy, policyLevel) -> {
		// TODO clarify Coverage limit
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0436".equals(c.getFormCode())).findFirst().get();
		return DataProviderFactory.dataOf(
				HomeSSMetaData.EndorsementTab.EndorsementHS0436.COVERAGE_LIMIT.getLabel(), "$" + form.getLimit().toString().split("\\.")[0] + "0",//TODO Coverage Limit=4000 not exist
				HomeSSMetaData.EndorsementTab.EndorsementHS0436.DESCRIPTION_OF_STRUCTURE.getLabel(), "index=1",
				HomeSSMetaData.EndorsementTab.EndorsementHS0436.CONSTRUCTION_TYPE.getLabel(), form.getType()
		);
	};

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0453DataFunction = (openLPolicy, policyLevel) -> DataProviderFactory.dataOf(
			HomeSSMetaData.EndorsementTab.EndorsementHS0453.COVERAGE_LIMIT.getLabel(), openLPolicy.getForms().stream().filter(c -> "HS0453".equals(c.getFormCode())).findFirst().get().getLimit().toString().split("\\.")[0]);


	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0454DataFunction = (openLPolicy, policyLevel) -> {
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0454".equals(c.getFormCode())).findFirst().get();
		return DataProviderFactory.dataOf(
				HomeSSMetaData.EndorsementTab.EndorsementHS0454.DEDUCTIBLE.getLabel(), form.getOptionalValue().toString().split("\\.")[0] + "%"
//				HomeSSMetaData.EndorsementTab.EndorsementHS0454.INCLUDE_COVERAGE_FOR_EARTHQUAKE_LOSS_TO_EXTERIOR_MASONRY_VENEER.getLabel(), "Masonry Veneer".equals(openLPolicy.getPolicyConstructionInfo().get(0).getConstructionType())?"Yes":"No"
		);
	};

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0455DataFunction = (openLPolicy, policyLevel) -> {
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0455".equals(c.getFormCode())).findFirst().get();
		return DataProviderFactory.emptyData();
	};

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0459DataFunction = (openLPolicy, policyLevel) -> {
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0459".equals(c.getFormCode())).findFirst().get();
		return DataProviderFactory.dataOf(
				HomeSSMetaData.EndorsementTab.EndorsementHS0459.NAME_OF_RELATIVE.getLabel(), "Name of relative",
				HomeSSMetaData.EndorsementTab.EndorsementHS0459.DOES_THE_FACILITY_PROVIDE_LIVING_SERVICES_SUCH_AS_DINING_THERAPY_MEDICAL_SUPERVISION_HOUSEKEEPING_AND_SOCIAL_ACTIVITIES.getLabel(), "Yes",
				HomeSSMetaData.EndorsementTab.EndorsementHS0459.NAME_OF_FACILITY.getLabel(), "Name of facility",
				HomeSSMetaData.EndorsementTab.EndorsementHS0459.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().get(0).getZip(),
				HomeSSMetaData.EndorsementTab.EndorsementHS0459.STREET_ADDRESS_1.getLabel(), "Street Address 1",
				HomeSSMetaData.EndorsementTab.EndorsementHS0459.COVERAGE_C_LIMIT.getLabel(), form.getLimit().toString().split("\\.")[0],
				HomeSSMetaData.EndorsementTab.EndorsementHS0459.COVERAGE_E_LIMIT.getLabel(), "$" + form.getOptionalValue().toString().split("\\.")[0]
		);
	};


	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0461DataFunction = (openLPolicy, policyLevel) -> DataProviderFactory.emptyData();

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0465DataFunction = (openLPolicy, policyLevel) -> {
		Map<String, String> td = new HashMap<>();
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0465".equals(form.getFormCode())) {
				switch (form.getType()) {
					case "Money":
						td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.MONEY_AND_BANK_NOTES.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						break;
					case "Silverware":
						td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.LOSS_OF_SILVERWARE.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						break;
					//TODO add all Special Limits
					default:
						CustomAssertions.assertThat(Boolean.TRUE).as("Unknown Type of Special Limit: %s", form.getType()).isFalse();
				}
			}
		}
		return new SimpleDataProvider(td);
	};

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0490DataFunction = (openLPolicy, policyLevel) -> DataProviderFactory.emptyData();

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0492DataFunction = (openLPolicy, policyLevel) -> {
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0492".equals(c.getFormCode())).findFirst().get();
		return DataProviderFactory.dataOf(
				HomeSSMetaData.EndorsementTab.EndorsementHS0492.DESCRIPTION_OF_STRUCTURE.getLabel(), "Description of Structure",
				HomeSSMetaData.EndorsementTab.EndorsementHS0492.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().get(0).getZip(),
				HomeSSMetaData.EndorsementTab.EndorsementHS0492.STREET_ADDRESS_1.getLabel(), "Street address 1",
				HomeSSMetaData.EndorsementTab.EndorsementHS0492.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
				HomeSSMetaData.EndorsementTab.EndorsementHS0492.IS_THE_STRUCTURE_USED_AS_A_DWELLING_OR_CAPABLE_OF_BEING_USED_AS_A_DWELLING.getLabel(), "No",
				HomeSSMetaData.EndorsementTab.EndorsementHS0492.IS_THE_STRUCTURE_USED_TO_CONDUCT_ANY_BUSINESS_OR_TO_STORE_ANY_BUSINESS_PROPERTY.getLabel(), "No",
				HomeSSMetaData.EndorsementTab.EndorsementHS0492.IS_THE_STRUCTURE_RENTED_OR_HELD_FOR_RENTAL_TO_ANY_PERSON_WHO_IS_NOT_A_RESIDENT_OF_THE_HOUSEHOLD.getLabel(), "No"
		);
	};

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0495DataFunction = (openLPolicy, policyLevel) -> DataProviderFactory.dataOf(
			HomeSSMetaData.EndorsementTab.EndorsementHS0495.COVERAGE_LIMIT.getLabel(), "$" + openLPolicy.getForms().stream().filter(c -> "HS0495".equals(c.getFormCode())).findFirst().get().getLimit().toString().split("\\.")[0]);

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0934DataFunction = (openLPolicy, policyLevel) -> DataProviderFactory.emptyData();

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS0965DataFunction = (openLPolicy, policyLevel) -> DataProviderFactory.dataOf(
			HomeSSMetaData.EndorsementTab.EndorsementHS0965.COVERAGE_LIMIT.getLabel(), "$"+openLPolicy.getForms().stream().filter(c -> "HS0965".equals(c.getFormCode())).findFirst().get().getLimit().toString().split("\\.")[0]);

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS2464DataFunction = (openLPolicy, policyLevel) -> {
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS2464".equals(c.getFormCode())).findFirst().get();
		return DataProviderFactory.dataOf(
				HomeSSMetaData.EndorsementTab.EndorsementHS2464.MAKE_OR_MODEL.getLabel(), "Model",
				HomeSSMetaData.EndorsementTab.EndorsementHS2464.SERIAL_OR_MOTOR_NUMBER.getLabel(), "123456789"
		);
	};

	private static BiFunction<HomeSSOpenLPolicy, String, TestData> formHS2472DataFunction = (openLPolicy, policyLevel) -> {
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS2472".equals(c.getFormCode())).findFirst().get();
		return DataProviderFactory.dataOf(
			HomeSSMetaData.EndorsementTab.EndorsementHS2472.DESCRIPTION_OF_THE_NATURE_OF_THE_FARMING.getLabel(), "Description",
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.IS_THE_FARMING_LOCATED_AT_THE_RESIDENCE_PREMISES.getLabel(), "On the Residence Premises".equals(form.getType())? "Yes":"No",
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.IS_THE_INCOME_DERIVED_FROM_THE_FARMING_A_PRIMARY_SOURCE_OF_INCOME.getLabel(), "No",
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.IS_THE_FARMING_LOCATION_USED_FOR_RACING_PURPOSES.getLabel(), "No"
		);
	};

	//TODO: add functions for other forms...

	public static TestData getFormTestData(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return getFormEnum(formCode).getTestData(openLPolicy, formCode);
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

	public enum Forms {
		HS0420(HomeSSMetaData.EndorsementTab.HS_04_20.getLabel(), "HS0420", formHS0420DataFunction),
		HS0435(HomeSSMetaData.EndorsementTab.HS_04_35.getLabel(), "HS0435", formHS0435DataFunction),
		HS0436(HomeSSMetaData.EndorsementTab.HS_04_36.getLabel(), "HS0436", formHS0436DataFunction),
		HS0453(HomeSSMetaData.EndorsementTab.HS_04_53.getLabel(), "HS0453", formHS0453DataFunction),
		HS0454(HomeSSMetaData.EndorsementTab.HS_04_54.getLabel(), "HS0454", formHS0454DataFunction),
		HS0455(HomeSSMetaData.EndorsementTab.HS_04_55.getLabel(), "HS0455", formHS0455DataFunction),
		HS0459(HomeSSMetaData.EndorsementTab.HS_04_59.getLabel(), "HS0459", formHS0459DataFunction),
		HS0461(HomeSSMetaData.EndorsementTab.HS_04_61.getLabel(), "HS0461", formHS0461DataFunction),
		HS0465(HomeSSMetaData.EndorsementTab.HS_04_65.getLabel(), "HS0465", formHS0465DataFunction),
		HS0490(HomeSSMetaData.EndorsementTab.HS_04_90.getLabel(), "HS0490", formHS0490DataFunction),
		HS0492(HomeSSMetaData.EndorsementTab.HS_04_92.getLabel(), "HS0492", formHS0492DataFunction),
		HS0495(HomeSSMetaData.EndorsementTab.HS_04_95.getLabel(), "HS0495", formHS0495DataFunction),
		HS0934(HomeSSMetaData.EndorsementTab.HS_09_34.getLabel(), "HS0934", formHS0934DataFunction),
		HS0965(HomeSSMetaData.EndorsementTab.HS_09_65.getLabel(), "HS0965", formHS0965DataFunction),
		HS2464(HomeSSMetaData.EndorsementTab.HS_24_64.getLabel(), "HS2464", formHS2464DataFunction),
		HS2472(HomeSSMetaData.EndorsementTab.HS_24_72.getLabel(), "HS2472", formHS2472DataFunction);
		//TODO: add other forms...


		private final String metaKey;
		private final String formCode;
		private final BiFunction<HomeSSOpenLPolicy, String, TestData> testDataFunction;

		Forms(String metaKey, String formCode, BiFunction<HomeSSOpenLPolicy, String, TestData> testDataFunction) {
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

		public BiFunction<HomeSSOpenLPolicy, String, TestData> getTestDataFunction() {
			return testDataFunction;
		}

		public TestData getTestData(HomeSSOpenLPolicy openLPolicy, String formCode) {
			String policyLevel = openLPolicy.getLevel();
			TestData td = getTestDataFunction().apply(openLPolicy, policyLevel);
			td.adjust("Action", isFormAdded(getFormCode(), policyLevel) ? "Edit" : "Add");
			return DataProviderFactory.dataOf(getMetaKey(), td);
		}
	}
}
