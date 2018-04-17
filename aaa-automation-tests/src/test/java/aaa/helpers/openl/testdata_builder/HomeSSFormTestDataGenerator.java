package aaa.helpers.openl.testdata_builder;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLForm;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.verification.CustomAssertions;

public class HomeSSFormTestDataGenerator extends BaseTest {
	private static Map<String, List<String>> selectedForms = new HashMap<>();

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0420DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0420", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0420.AMOUNT_OF_INSURANCE.getLabel(), openLPolicy.getForms().stream().filter(c -> "HS0420".equals(c.getFormCode())).findFirst().get().getCovPercentage() + "%"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0435DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0435".equals(form.getFormCode())) {
				if (instanceNum == 1) {
					tdList.add(DataProviderFactory.dataOf(
							"Action", isFormAdded("HS0435", policyLevel) ? "Edit" : "Add",
							HomeSSMetaData.EndorsementTab.EndorsementHS0435.LOCATION_TYPE.getLabel(), "Residential".equals(form.getType()) ? "Residence Premises" : "Other Structure off Premises",
							HomeSSMetaData.EndorsementTab.EndorsementHS0435.COVERAGE_LIMIT.getLabel(), new Dollar(form.getLimit()).toString().split("\\.")[0]));
				} else {
					tdList.add(DataProviderFactory.dataOf(
							"Action", "Add",
							HomeSSMetaData.EndorsementTab.EndorsementHS0435.LOCATION_TYPE.getLabel(), "Residential".equals(form.getType()) ? "Residence Premises" : "Other Structure off Premises",
							HomeSSMetaData.EndorsementTab.EndorsementHS0435.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().get(0).getZip(),
							HomeSSMetaData.EndorsementTab.EndorsementHS0435.STREET_ADDRESS_1.getLabel(), "Street address 1",
							HomeSSMetaData.EndorsementTab.EndorsementHS0435.DESCRIPTION_OF_STRUCTURE.getLabel(), "index=2",
							HomeSSMetaData.EndorsementTab.EndorsementHS0435.COVERAGE_LIMIT.getLabel(), new Dollar(form.getLimit()).toString().split("\\.")[0]));
				}
				instanceNum++;
			}
		}
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0436DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0436".equals(c.getFormCode())).findFirst().get();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0436", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0436.COVERAGE_LIMIT.getLabel(), "$" + form.getLimit().toString().split("\\.")[0],
				HomeSSMetaData.EndorsementTab.EndorsementHS0436.DESCRIPTION_OF_STRUCTURE.getLabel(), "index=1",
				HomeSSMetaData.EndorsementTab.EndorsementHS0436.CONSTRUCTION_TYPE.getLabel(), form.getType()));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0443DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0443", policyLevel) ? "Edit" : "Add"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0452DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0452", policyLevel) ? "Edit" : "Add"));
//				HomeSSMetaData.EndorsementTab.EndorsementHS0452.COVERAGE_LIMIT.getLabel(),
//				openLPolicy.getForms().stream().filter(c -> "HS0453".equals(c.getFormCode())).findFirst().get().getLimit().toString().split("\\.")[0]));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0453DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0453", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0453.COVERAGE_LIMIT.getLabel(),
				openLPolicy.getForms().stream().filter(c -> "HS0453".equals(c.getFormCode())).findFirst().get().getLimit().toString().split("\\.")[0]));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0454DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0454".equals(c.getFormCode())).findFirst().get();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0454", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0454.DEDUCTIBLE.getLabel(), form.getOptionalValue().toString().split("\\.")[0] + "%"));
//				HomeSSMetaData.EndorsementTab.EndorsementHS0454.INCLUDE_COVERAGE_FOR_EARTHQUAKE_LOSS_TO_EXTERIOR_MASONRY_VENEER.getLabel(), "Masonry Veneer".equals(openLPolicy.getPolicyConstructionInfo().get(0).getConstructionType())?"Yes":"No"

		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0455DataFunction = (openLPolicy, policyLevel) -> {
		if (isFormAdded("HS0455", policyLevel))
			return null;
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action",  "Add"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0459DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0459".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", instanceNum == 1 ? (isFormAdded("HS0459", policyLevel) ? "Edit" : "Add") : "Add",
						HomeSSMetaData.EndorsementTab.EndorsementHS0459.NAME_OF_RELATIVE.getLabel(), "Name of relative",
						HomeSSMetaData.EndorsementTab.EndorsementHS0459.DOES_THE_FACILITY_PROVIDE_LIVING_SERVICES_SUCH_AS_DINING_THERAPY_MEDICAL_SUPERVISION_HOUSEKEEPING_AND_SOCIAL_ACTIVITIES.getLabel(), "Yes",
						HomeSSMetaData.EndorsementTab.EndorsementHS0459.NAME_OF_FACILITY.getLabel(), "Name of facility",
						HomeSSMetaData.EndorsementTab.EndorsementHS0459.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().get(0).getZip(),
						HomeSSMetaData.EndorsementTab.EndorsementHS0459.STREET_ADDRESS_1.getLabel(), "Street Address 1",
						HomeSSMetaData.EndorsementTab.EndorsementHS0459.COVERAGE_C_LIMIT.getLabel(), form.getLimit().toString().split("\\.")[0],
						HomeSSMetaData.EndorsementTab.EndorsementHS0459.COVERAGE_E_LIMIT.getLabel(), "$" + form.getOptionalValue().toString().split("\\.")[0]));
				instanceNum++;
			}
		}
		return tdList;
	};


	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0461DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0461", policyLevel) ? "Edit" : "Add"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0465DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		Map<String, String> td = new HashMap<>();
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0465".equals(form.getFormCode())) {
				switch (form.getType()) {
					case "Money":
						td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.MONEY_AND_BANK_NOTES.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						break;
					case "Securities":
						td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.SECURITIES.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						break;
					case "Silverware":
						td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.LOSS_OF_SILVERWARE.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						break;
					case "Trailers":
						td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.TRAILERS.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						break;
					//TODO add all Special Limits
					default:
						CustomAssertions.assertThat(Boolean.TRUE).as("Unknown Type of Special Limit: %s", form.getType()).isFalse();
				}

			}
		}
		tdList.add(new SimpleDataProvider(td));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0477DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0477", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0477.COVERAGE_LIMIT.getLabel(), openLPolicy.getForms().stream().filter(c -> "HS0477".equals(c.getFormCode())).findFirst().get().getCovPercentage() + "%"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0490DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0490", policyLevel) ? "Edit" : "Add"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0492DataFunction = (openLPolicy, policyLevel) -> {

		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0492".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", instanceNum == 1 ? (isFormAdded("HS0492", policyLevel) ? "Edit" : "Add") : "Add",
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.DESCRIPTION_OF_STRUCTURE.getLabel(), "Description of Structure",
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().get(0).getZip(),
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.STREET_ADDRESS_1.getLabel(), "Street address 1",
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.IS_THE_STRUCTURE_USED_AS_A_DWELLING_OR_CAPABLE_OF_BEING_USED_AS_A_DWELLING.getLabel(), "No",
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.IS_THE_STRUCTURE_USED_TO_CONDUCT_ANY_BUSINESS_OR_TO_STORE_ANY_BUSINESS_PROPERTY.getLabel(), "No",
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.IS_THE_STRUCTURE_RENTED_OR_HELD_FOR_RENTAL_TO_ANY_PERSON_WHO_IS_NOT_A_RESIDENT_OF_THE_HOUSEHOLD.getLabel(), "No"));
				instanceNum++;
			}
		}
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0495DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0495", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0495.COVERAGE_LIMIT.getLabel(), "$" + openLPolicy.getForms().stream().filter(c -> "HS0495".equals(c.getFormCode())).findFirst().get().getLimit().toString().split("\\.")[0]));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0614DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0614", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0614.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().get(0).getZip(),
				HomeSSMetaData.EndorsementTab.EndorsementHS0614.STREET_ADDRESS_1.getLabel(), "Street Address 1",
				HomeSSMetaData.EndorsementTab.EndorsementHS0614.COVERAGE_LIMIT.getLabel(), openLPolicy.getForms().stream().filter(c -> "HS0614".equals(c.getFormCode())).findFirst().get().getLimit().toString().split("\\.")[0]));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0904DataFunction = (openLPolicy, policyLevel) -> {
//		List tdList = new ArrayList();
//		tdList.add(DataProviderFactory.dataOf(
//				"Action", isFormAdded("HS0904", policyLevel) ? "Edit" : "Add"
//		));
//		return tdList;
		return null;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0906DataFunction = (openLPolicy, policyLevel) -> {
		if (isFormAdded("HS0906", policyLevel))
			return null;
		List tdList = new ArrayList();
		tdList.add(DataProviderFactory.dataOf("Action", "Add"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0926DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0926", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0926.COVERAGE_LIMIT.getLabel(), new Dollar(openLPolicy.getForms().stream().filter(c -> "HS0926".equals(c.getFormCode())).findFirst().get().getLimit()).toString().split("\\.")[0]));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0931DataFunction = (openLPolicy, policyLevel) -> {
		List tdList = new ArrayList();
		tdList.add(DataProviderFactory.dataOf("Action", isFormAdded("HS0931", policyLevel) ? "Edit" : "Add"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0934DataFunction = (openLPolicy, policyLevel) -> {
		List tdList = new ArrayList();
		tdList.add(DataProviderFactory.dataOf("Action", isFormAdded("HS0934", policyLevel) ? "Edit" : "Add"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0965DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0965", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0965.COVERAGE_LIMIT.getLabel(), "$" + openLPolicy.getForms().stream().filter(c -> "HS0965".equals(c.getFormCode())).findFirst().get().getLimit().toString().split("\\.")[0]));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS2443DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS2443", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS2443.DESCRIPTION_OF_BUSINESS.getLabel(), "Description of business",
				HomeSSMetaData.EndorsementTab.EndorsementHS2443.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().get(0).getZip(),
				HomeSSMetaData.EndorsementTab.EndorsementHS2443.STREET_ADDRESS_1.getLabel(), "Street address 1"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS2464DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS2464".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", instanceNum == 1 ? (isFormAdded("HS2464", policyLevel) ? "Edit" : "Add") : "Add",
						HomeSSMetaData.EndorsementTab.EndorsementHS2464.MAKE_OR_MODEL.getLabel(), "Model" + String.format("%d", instanceNum),
						HomeSSMetaData.EndorsementTab.EndorsementHS2464.SERIAL_OR_MOTOR_NUMBER.getLabel(), "1234567" + String.format("%d", instanceNum)));
				instanceNum++;
			}
		}
		return tdList;
	};


	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS2472DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS2472".equals(c.getFormCode())).findFirst().get();
		tdList.add(DataProviderFactory.dataOf(
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.DESCRIPTION_OF_THE_NATURE_OF_THE_FARMING.getLabel(), "Description",
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.IS_THE_FARMING_LOCATED_AT_THE_RESIDENCE_PREMISES.getLabel(), "On the Residence Premises".equals(form.getType()) ? "Yes" : "No",
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.IS_THE_INCOME_DERIVED_FROM_THE_FARMING_A_PRIMARY_SOURCE_OF_INCOME.getLabel(), "No",
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.IS_THE_FARMING_LOCATION_USED_FOR_RACING_PURPOSES.getLabel(), "No"));
		return tdList;
	};

	//TODO: add functions for other forms...

	public static List<TestData> getFormTestData(HomeSSOpenLPolicy openLPolicy, String formCode) {
		return getFormEnum(formCode).getTestData(openLPolicy);
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
		HS0443(HomeSSMetaData.EndorsementTab.HS_04_43.getLabel(), "HS0443", formHS0443DataFunction),
		HS0452(HomeSSMetaData.EndorsementTab.HS_04_52.getLabel(), "HS0452", formHS0452DataFunction),
		HS0453(HomeSSMetaData.EndorsementTab.HS_04_53.getLabel(), "HS0453", formHS0453DataFunction),
		HS0454(HomeSSMetaData.EndorsementTab.HS_04_54.getLabel(), "HS0454", formHS0454DataFunction),
		HS0455(HomeSSMetaData.EndorsementTab.HS_04_55.getLabel(), "HS0455", formHS0455DataFunction),
		HS0459(HomeSSMetaData.EndorsementTab.HS_04_59.getLabel(), "HS0459", formHS0459DataFunction),
		HS0461(HomeSSMetaData.EndorsementTab.HS_04_61.getLabel(), "HS0461", formHS0461DataFunction),
		HS0465(HomeSSMetaData.EndorsementTab.HS_04_65.getLabel(), "HS0465", formHS0465DataFunction),
		HS0477(HomeSSMetaData.EndorsementTab.HS_04_77.getLabel(), "HS0477", formHS0477DataFunction),
		HS0490(HomeSSMetaData.EndorsementTab.HS_04_90.getLabel(), "HS0490", formHS0490DataFunction),
		HS0492(HomeSSMetaData.EndorsementTab.HS_04_92.getLabel(), "HS0492", formHS0492DataFunction),
		HS0495(HomeSSMetaData.EndorsementTab.HS_04_95.getLabel(), "HS0495", formHS0495DataFunction),
		HS0614(HomeSSMetaData.EndorsementTab.HS_06_14.getLabel(), "HS0614", formHS0614DataFunction),
		HS0904(HomeSSMetaData.EndorsementTab.HS_09_04.getLabel(), "HS0904", formHS0904DataFunction),
		HS0906(HomeSSMetaData.EndorsementTab.HS_09_06.getLabel(), "HS0906", formHS0906DataFunction),
		HS0926(HomeSSMetaData.EndorsementTab.HS_09_26.getLabel(), "HS0926", formHS0926DataFunction),
		HS0931(HomeSSMetaData.EndorsementTab.HS_09_31.getLabel(), "HS0931", formHS0931DataFunction),
		HS0934(HomeSSMetaData.EndorsementTab.HS_09_34.getLabel(), "HS0934", formHS0934DataFunction),
		HS0965(HomeSSMetaData.EndorsementTab.HS_09_65.getLabel(), "HS0965", formHS0965DataFunction),
		HS2443(HomeSSMetaData.EndorsementTab.HS_24_43.getLabel(), "HS2443", formHS2443DataFunction),
		HS2464(HomeSSMetaData.EndorsementTab.HS_24_64.getLabel(), "HS2464", formHS2464DataFunction),
		HS2472(HomeSSMetaData.EndorsementTab.HS_24_72.getLabel(), "HS2472", formHS2472DataFunction);
		//TODO: add other forms...


		private final String metaKey;
		private final String formCode;
		private final BiFunction<HomeSSOpenLPolicy, String, List<TestData>> testDataFunction;

		Forms(String metaKey, String formCode, BiFunction<HomeSSOpenLPolicy, String, List<TestData>> testDataFunction) {
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

		public BiFunction<HomeSSOpenLPolicy, String, List<TestData>> getTestDataFunction() {
			return testDataFunction;
		}

		public List<TestData> getTestData(HomeSSOpenLPolicy openLPolicy) {
			String policyLevel = openLPolicy.getLevel();
			List<TestData> tdList = getTestDataFunction().apply(openLPolicy, policyLevel);
			return tdList;
		}
	}
}
