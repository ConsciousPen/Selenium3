package aaa.helpers.openl.testdata_generator;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLForm;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;

public class HomeSSHO3FormTestDataGenerator {
	private static Map<String, List<String>> selectedForms = new HashMap<>();

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0412DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0412".equals(c.getFormCode())).findFirst().get();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0412", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0412.COVERAGE_LIMIT.getLabel(), "$" + form.getLimit().toString().split("\\.")[0],
				HomeSSMetaData.EndorsementTab.EndorsementHS0412.IS_THE_BUSINESS_CONDUCTED_ON_THE_RESIDENCE_PREMISES.getLabel(), "No",
				HomeSSMetaData.EndorsementTab.EndorsementHS0412.IS_THE_BUSINESS_PROPERTY_FOR_SAMPLE_SALE_OR_DELIVERY_AFTER_SALE.getLabel(), "No"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0420DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		if (Constants.States.WV.equals(openLPolicy.getPolicyAddress().getState()) && "Heritage".equals(policyLevel)) {
			tdList.add(DataProviderFactory.dataOf(
					"Action", "Add",
					HomeSSMetaData.EndorsementTab.EndorsementHS0420.AMOUNT_OF_INSURANCE.getLabel(), openLPolicy.getForms().stream().filter(c -> "HS0420".equals(c.getFormCode())).findFirst().get().getCovPercentage() + "%"));

		} else {
			tdList.add(DataProviderFactory.dataOf(
					"Action", isFormAdded("HS0420", policyLevel) ? "Edit" : "Add",
					HomeSSMetaData.EndorsementTab.EndorsementHS0420.AMOUNT_OF_INSURANCE.getLabel(), openLPolicy.getForms().stream().filter(c -> "HS0420".equals(c.getFormCode())).findFirst().get().getCovPercentage() + "%"));
		}
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
							HomeSSMetaData.EndorsementTab.EndorsementHS0435.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
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
		// validate limit
		List<Double> validLimits = new ArrayList<>(Arrays.asList(1000.0, 5000.0, 10000.0, 15000.0, 20000.0, 25000.0, 30000.0, 35000.0, 40000.0, 45000.0, 50000.0));
		if (!validLimits.contains(form.getLimit())) {
			throw new IstfException(String.format("Form HS0436 - invalid limit %s", form.getLimit().toString()));
		}
		//
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0436", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0436.COVERAGE_LIMIT.getLabel(), "$" + form.getLimit().toString().split("\\.")[0],
				HomeSSMetaData.EndorsementTab.EndorsementHS0436.DESCRIPTION_OF_STRUCTURE.getLabel(), "index=1",
				HomeSSMetaData.EndorsementTab.EndorsementHS0436.CONSTRUCTION_TYPE.getLabel(), form.getType()));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0442DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0442".equals(c.getFormCode())).findFirst().get();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0442", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0442.DESCRIPTION_OF_BUSINESS.getLabel(), "Description",
				HomeSSMetaData.EndorsementTab.EndorsementHS0442.LOCATION_OF_BUSINESS.getLabel(), form.getType(),
				HomeSSMetaData.EndorsementTab.EndorsementHS0442.LIMIT_OF_LIABILITY.getLabel(), form.getLimit(),
				HomeSSMetaData.EndorsementTab.EndorsementHS0442.DESCRIPTION_OF_OTHER_STRUCTURE.getLabel(), "Description",
				HomeSSMetaData.EndorsementTab.EndorsementHS0442.SECTION_I_COVERAGE.getLabel(), "No"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0443DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0443", policyLevel) ? "Edit" : "Add"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0450DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0450".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", instanceNum == 1 ? isFormAdded("HS0450", policyLevel) ? "Edit" : "Add" : "Add",
						HomeSSMetaData.EndorsementTab.EndorsementHS0450.COVERAGE_LIMIT.getLabel(), form.getLimit(),
						HomeSSMetaData.EndorsementTab.EndorsementHS0450.DESCRIPTION_OF_RESIDENCE.getLabel(), "Residence" + String.format("%d", instanceNum),
						HomeSSMetaData.EndorsementTab.EndorsementHS0450.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
						HomeSSMetaData.EndorsementTab.EndorsementHS0450.STREET_ADDRESS_1.getLabel(), String.format("%d", instanceNum) + " Street Address"));
				instanceNum++;
			}
		}
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0452DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0452", policyLevel) ? "Edit" : "Add"));
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
				HomeSSMetaData.EndorsementTab.EndorsementHS0454.DEDUCTIBLE.getLabel(), form.getOptionalValue().toString().split("\\.")[0] + "%",
				//				HomeSSMetaData.EndorsementTab.EndorsementHS0454.INCLUDE_COVERAGE_FOR_EARTHQUAKE_LOSS_TO_EXTERIOR_MASONRY_VENEER.getLabel(), form.getMasonaryOrFarmPremisesInd() ? "Yes" : "No"));
				HomeSSMetaData.EndorsementTab.EndorsementHS0454.INCLUDE_COVERAGE_FOR_EARTHQUAKE_LOSS_TO_EXTERIOR_MASONRY_VENEER.getLabel(), "Masonry Veneer".equals(openLPolicy.getPolicyConstructionInfo().getConstructionType()) ? form.getMasonaryOrFarmPremisesInd() ? "Yes" : "No" : "No"));

		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0455DataFunction = (openLPolicy, policyLevel) -> {
		if (isFormAdded("HS0455", policyLevel)) {
			return null;
		}
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", "Add"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0459DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0459".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", instanceNum == 1 ? isFormAdded("HS0459", policyLevel) ? "Edit" : "Add" : "Add",
						HomeSSMetaData.EndorsementTab.EndorsementHS0459.NAME_OF_RELATIVE.getLabel(), "Name of relative",
						HomeSSMetaData.EndorsementTab.EndorsementHS0459.DOES_THE_FACILITY_PROVIDE_LIVING_SERVICES_SUCH_AS_DINING_THERAPY_MEDICAL_SUPERVISION_HOUSEKEEPING_AND_SOCIAL_ACTIVITIES.getLabel(), "Yes",
						HomeSSMetaData.EndorsementTab.EndorsementHS0459.NAME_OF_FACILITY.getLabel(), "Name of facility",
						HomeSSMetaData.EndorsementTab.EndorsementHS0459.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
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
		LinkedHashMap<String, String> td = new LinkedHashMap<>();
		td.put("Action", isFormAdded("HS0465", policyLevel) ? "Edit" : "Add");
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0465".equals(form.getFormCode())) {
				switch (form.getType()) {
					case "Firearms":
						td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.LOSS_OF_FIREARMS.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						break;
					case "Money":
						if ("Heritage".equals(policyLevel)) {
							td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.MONEY_AND_BANK_NOTES.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						}
						break;
					case "Securities":
						td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.SECURITIES.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						break;
					case "Silverware":
						td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.LOSS_OF_SILVERWARE.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						break;
					case "Trailers":
						if ("Heritage".equals(policyLevel)) {
							td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.TRAILERS.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						}
						break;
					case "Watercraft":
						td.put(HomeSSMetaData.EndorsementTab.EndorsementHS0465.WATERCRAFT.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]);
						break;
					//TODO add all Special Limits
					default:
						CustomAssertions.fail("Unknown Type of Special Limit: %s", form.getType());
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
		//		List<TestData> tdList = new ArrayList();
		//		tdList.add(DataProviderFactory.dataOf(
		//				"Action", isFormAdded("HS0490", policyLevel) ? "Edit" : "Add"));
		//		return tdList;
		return null;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0492DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0492".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", instanceNum == 1 ? isFormAdded("HS0492", policyLevel) ? "Edit" : "Add" : "Add",
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.DESCRIPTION_OF_STRUCTURE.getLabel(), "Description of Structure",
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.STREET_ADDRESS_1.getLabel(), "000 Street Address",
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.IS_THE_STRUCTURE_USED_AS_A_DWELLING_OR_CAPABLE_OF_BEING_USED_AS_A_DWELLING.getLabel(), "No",
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.IS_THE_STRUCTURE_USED_TO_CONDUCT_ANY_BUSINESS_OR_TO_STORE_ANY_BUSINESS_PROPERTY.getLabel(), "No",
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.IS_THE_STRUCTURE_RENTED_OR_HELD_FOR_RENTAL_TO_ANY_PERSON_WHO_IS_NOT_A_RESIDENT_OF_THE_HOUSEHOLD.getLabel(), "No"));
				instanceNum++;
			}
		}
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0493DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0493", policyLevel) ? "Edit" : "Add"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0495DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0495".equals(c.getFormCode())).findFirst().get();
		String limit = form.getLimit() == 5000.0 ? "$5000" : new Dollar(form.getLimit()).toString().split("\\.")[0];
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0495", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0495.COVERAGE_LIMIT.getLabel(), limit));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0499DataFunction = (openLPolicy, policyLevel) -> {
		//		List<TestData> tdList = new ArrayList();
		//		tdList.add(DataProviderFactory.dataOf(
		//				"Action", isFormAdded("HS0490", policyLevel) ? "Edit" : "Add"));
		//		return tdList;
		return null;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0546DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0546".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", instanceNum == 1 ? isFormAdded("HS0546", policyLevel) ? "Edit" : "Add" : "Add",
						HomeSSMetaData.EndorsementTab.EndorsementHS0546.DESCRIPTION_OF_RENTED_UNIT.getLabel(), "Unit" + String.format("%d", instanceNum),
						HomeSSMetaData.EndorsementTab.EndorsementHS0546.COVERAGE_AMOUNT.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]));
				instanceNum++;
			}
		}
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0578DataFunction = (openLPolicy, policyLevel) -> {
		return null;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0614DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0614", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0614.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
				HomeSSMetaData.EndorsementTab.EndorsementHS0614.STREET_ADDRESS_1.getLabel(), "Street Address 1",
				HomeSSMetaData.EndorsementTab.EndorsementHS0614.COVERAGE_LIMIT.getLabel(), openLPolicy.getForms().stream().filter(c -> "HS0614".equals(c.getFormCode())).findFirst().get().getLimit().toString().split("\\.")[0]));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0904DataFunction = (openLPolicy, policyLevel) -> {
		if (openLPolicy.isLegacyConvPolicy()) {
			List tdList = new ArrayList();
			tdList.add(DataProviderFactory.dataOf(
					"Action", isFormAdded("HS0904", policyLevel) ? "Edit" : "Add",
					HomeSSMetaData.EndorsementTab.EndorsementHS0904.IS_THIS_AN_EXTENSION_OF_A_PRIOR_STRUCTURAL_ALTERATION_COVERAGE_ENDORSEMENT.getLabel(), "Yes",
					HomeSSMetaData.EndorsementTab.EndorsementHS0904.REASON_FOR_EXTENSION.getLabel(), "Reason"
			));
			return tdList;

		} else {
			return null;
		}
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0906DataFunction = (openLPolicy, policyLevel) -> {
		if (isFormAdded("HS0906", policyLevel)) {
			return null;
		}
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

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0929DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0929".equals(c.getFormCode())).findFirst().get();
		//
		List<Double> validLimits = new ArrayList<>(Arrays.asList(15000.0, 25000.0, 50000.0));
		if (!validLimits.contains(form.getLimit())) {
			throw new IstfException(String.format("Form HS0929 - invalid limit %s", form.getLimit().toString()));
		}
		//
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0929", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0929.PROPERTY_COVERAGE_LIMIT.getLabel(), new Dollar(form.getLimit()).toString().split("\\.")[0],
				HomeSSMetaData.EndorsementTab.EndorsementHS0929.LIABILITY_COVERAGE_LIMIT.getLabel(), new Dollar(form.getOptionalValue()).toString().split("\\.")[0]));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0931DataFunction = (openLPolicy, policyLevel) -> {
		List tdList = new ArrayList();
		if (!isFormAdded("HS0931", policyLevel)) {
			tdList.add(DataProviderFactory.dataOf("Action", "Add"));
		}
		//		tdList.add(DataProviderFactory.dataOf("Action", isFormAdded("HS0931", policyLevel) ? "Edit" : "Add"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0934DataFunction = (openLPolicy, policyLevel) -> {
		List tdList = new ArrayList();
		if (!isFormAdded("HS0934", policyLevel)) {
			tdList.add(DataProviderFactory.dataOf("Action", "Add"));
		}
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0965DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS0965".equals(c.getFormCode())).findFirst().get();
		// validate limit
		List<Double> validLimits = new ArrayList<>(Arrays.asList(2500.0, 5000.0));
		if (!validLimits.contains(form.getLimit())) {
			throw new IstfException(String.format("Form HS0965 - invalid limit %s", form.getLimit().toString()));
		}
		//
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS0965", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS0965.COVERAGE_LIMIT.getLabel(), "$" + form.getLimit().toString().split("\\.")[0]));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS0988DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0988".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", instanceNum == 1 ? isFormAdded("HS0988", policyLevel) ? "Edit" : "Add" : "Add",
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.NAME_OF_PERSON_OR_ORGANIZATION.getLabel(), "Name" + String.format("%d", instanceNum),
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.STREET_ADDRESS_1.getLabel(), "Address" + String.format("%d", instanceNum),
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.DESCRIPTION_OF_INSURABLE_INTEREST.getLabel(), "Interest" + String.format("%d", instanceNum),
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.DESCRIPTION_OF_EVENT.getLabel(), "Event" + String.format("%d", instanceNum),
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.EVENT_ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.EVENT_STREET_ADDRESS_1.getLabel(), "Address" + String.format("%d", instanceNum),
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.EFFECTIVE_DATE.getLabel(), TimeSetterUtil.getInstance().getCurrentTime().plusDays(3).format(DateTimeUtils.MM_DD_YYYY),
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.EXPIRATION_DATE.getLabel(), TimeSetterUtil.getInstance().getCurrentTime().plusDays(4).format(DateTimeUtils.MM_DD_YYYY),
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.COVERAGE_E_LIMIT.getLabel(), form.getLimit().toString().split("\\.")[0],
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.ADDITIONAL_COMMENTS_ABOUT_THE_EVENT.getLabel(), "Comment" + String.format("%d", instanceNum),
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.WILL_THERE_BE_ALCOHOLIC_BEVERAGES_AT_THE_EVENT.getLabel(), "No",
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.WILL_THERE_BE_A_SWIMMING_POOL_BOUNCE_HOUSE_OR_TRAMPOLINE_AT_THE_EVENT_LOCATION.getLabel(), "No",
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.WILL_THERE_BE_HIRED_OR_VOLUNTEER_STAFF_AT_THE_EVENT.getLabel(), "No",
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.EVENT_LOCATION.getLabel(), "index=1",
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.IS_A_PERMIT_OR_LICENSE_REQUIRED_FOR_THE_EVENT.getLabel(), "No",
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.IS_THE_EVENT_BEING_HELD_FOR_BUSINESS_PURPOSES.getLabel(), "No",
						HomeSSMetaData.EndorsementTab.EndorsementHS0988.IS_THE_EVENT_BEING_HELD_FOR_COMPENSATION_OR_TO_RAISE_DONATIONS_OR_MONEY_CHARITY_EVENTS_POLITICAL_FUNDRAISERS_ETC.getLabel(), "No"));
				instanceNum++;
			}
		}
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS2338DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS2338".equals(c.getFormCode())).findFirst().get();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS2338", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.SECTION_I_LIMIT_OF_LIABILITY.getLabel(), "$" + form.getLimit().toString().split("\\.")[0],
				HomeSSMetaData.EndorsementTab.EndorsementHS2338.NUMBER_OF_PERSONS_RECEIVING_DAY_CARE_SERVICES.getLabel(), String.format("%d", form.getNoOfPersons())));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS2443DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS2443", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS2443.DESCRIPTION_OF_BUSINESS.getLabel(), "Description of business",
				HomeSSMetaData.EndorsementTab.EndorsementHS2443.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
				HomeSSMetaData.EndorsementTab.EndorsementHS2443.STREET_ADDRESS_1.getLabel(), "Street address 1"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS2452DataFunction = (openLPolicy, policyLevel) -> {
		//		List tdList = new ArrayList();
		//		tdList.add(DataProviderFactory.dataOf("Action", isFormAdded("HS2452", policyLevel) ? "Edit" : "Add"));
		//		return tdList;
		return null;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS2464DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS2464".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", instanceNum == 1 ? isFormAdded("HS2464", policyLevel) ? "Edit" : "Add" : "Add",
						HomeSSMetaData.EndorsementTab.EndorsementHS2464.MAKE_OR_MODEL.getLabel(), "Model" + String.format("%d", instanceNum),
						HomeSSMetaData.EndorsementTab.EndorsementHS2464.SERIAL_OR_MOTOR_NUMBER.getLabel(), "1234567" + String.format("%d", instanceNum)));
				instanceNum++;
			}
		}
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS2471DataFunction = (openLPolicy, policyLevel) -> {
		String classification = "";
		switch (openLPolicy.getForms().stream().filter(c -> "HS2471".equals(c.getFormCode())).findFirst().get().getType()) {
			case "CL":
				classification = "Office clerical";
				break;
			case "SI":
				classification = "Sales / Collectors / Messengers with service";
				break;
			case "SE":
				classification = "Sales / Collectors / Messengers w/out service";
				break;
			case "T1":
				classification = "Teacher - athletic/physical training, laboratory/manual training";
				break;
			case "T2":
				classification = "Teacher - Other";
				break;
			default:
				throw new IstfException("Unknown mapping for type = " + openLPolicy.getForms().stream().filter(c -> "HS2471".equals(c.getFormCode())).findFirst().get().getType());
		}
		List<TestData> tdList = new ArrayList<>();
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS2471", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS2471.NAME_OF_BUSINESS.getLabel(), "Name",
				HomeSSMetaData.EndorsementTab.EndorsementHS2471.DESCRIPTION_OF_BUSINESS.getLabel(), "Description",
				HomeSSMetaData.EndorsementTab.EndorsementHS2471.CLASSIFICATION_OCCUPATION.getLabel(), classification,
				HomeSSMetaData.EndorsementTab.EndorsementHS2471.IS_THE_INSURED_SELF_EMPLOYED_A_PARTNER_IN_THE_BUSINESS_OR_MAINTAIN_ANY_FINANCIAL_CONTROL_IN_THIS_BUSINESS.getLabel(), "No"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS2472DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		HomeSSOpenLForm form = openLPolicy.getForms().stream().filter(c -> "HS2472".equals(c.getFormCode())).findFirst().get();
		boolean isOnTheResidencePremises = "On the Residence Premises".equals(form.getType());
		tdList.add(DataProviderFactory.dataOf(
				"Action", isFormAdded("HS2472", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.DESCRIPTION_OF_THE_NATURE_OF_THE_FARMING.getLabel(), "Description",
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.IS_THE_FARMING_LOCATED_AT_THE_RESIDENCE_PREMISES.getLabel(), isOnTheResidencePremises ? "Yes" : "No",
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.ZIP_CODE.getLabel(), !isOnTheResidencePremises ? openLPolicy.getPolicyAddress().getZip() : null,
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.STREET_ADDRESS_1.getLabel(), !isOnTheResidencePremises ? "Street Address 1" : null,
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.IS_THE_INCOME_DERIVED_FROM_THE_FARMING_A_PRIMARY_SOURCE_OF_INCOME.getLabel(), "No",
				HomeSSMetaData.EndorsementTab.EndorsementHS2472.IS_THE_FARMING_LOCATION_USED_FOR_RACING_PURPOSES.getLabel(), "No"));
		return tdList;
	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS2473DataFunction = (openLPolicy, policyLevel) -> {
		List<TestData> tdList = new ArrayList<>();
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS2473".equals(form.getFormCode())) {
				tdList.add(DataProviderFactory.dataOf(
						"Action", instanceNum == 1 ? isFormAdded("HS2473", policyLevel) ? "Edit" : "Add" : "Add",
						HomeSSMetaData.EndorsementTab.EndorsementHS2473.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
						HomeSSMetaData.EndorsementTab.EndorsementHS2473.STREET_ADDRESS_1.getLabel(), String.format("%d", instanceNum) + " Street Address",
						HomeSSMetaData.EndorsementTab.EndorsementHS2473.TOTAL_ACREAGE.getLabel(), "INSOPR".equals(form.getType()) ? form.getLimit().toString().split("\\.")[0] : form.getOptionalValue1().toString().split("\\.")[0],
						HomeSSMetaData.EndorsementTab.EndorsementHS2473.IS_THE_BUILDING_PRESENT_ON_THE_FARM_PREMISES.getLabel(), form.getCovPercentage() > 0 ? "Yes" : "No",
						HomeSSMetaData.EndorsementTab.EndorsementHS2473.TOTAL_NUMBER_OF_PERSON_DAYS_WORKED_FOR_ALL_PART_TIME_EMPLOYEES_WHO_WORK_40_DAYS_OR_LESS_PER_YEAR.getLabel(), "0",
						HomeSSMetaData.EndorsementTab.EndorsementHS2473.NUMBER_OF_PART_TIME_EMPLOYEES_41_179_DAYS_PER_YEAR.getLabel(), form.getOptionalValue4().toString().split("\\.")[0],
						HomeSSMetaData.EndorsementTab.EndorsementHS2473.NUMBER_OF_FULL_TIME_EMPLOYEES_180_OR_MORE_DAYS_PER_YEAR.getLabel(), form.getOptionalValue5().toString().split("\\.")[0],
						HomeSSMetaData.EndorsementTab.EndorsementHS2473.TOTAL_ANNUAL_PAYROLL.getLabel(), "100000",
						HomeSSMetaData.EndorsementTab.EndorsementHS2473.OWNERSHIP_AND_OPERATION_OF_FARM.getLabel(), "INSOPR".equals(form.getType()) ? "Owned and operated by the Insured" : "Rented by the Insured and operated by the Insured",
						HomeSSMetaData.EndorsementTab.EndorsementHS2473.IS_ANY_OTHER_BUSINESS_ACTIVITY_CONDUCTED_AT_THE_FARM_LOCATION.getLabel(), "No"
				));
				instanceNum++;
			}
		}
		return tdList;

	};

	private static BiFunction<HomeSSOpenLPolicy, String, List<TestData>> formHS2494DataFunction = (openLPolicy, policyLevel) -> {
		List tdList = new ArrayList();
		tdList.add(DataProviderFactory.dataOf("Action", isFormAdded("HS2494", policyLevel) ? "Edit" : "Add",
				HomeSSMetaData.EndorsementTab.EndorsementHS2494.IS_THE_EMPLOYEE_A_PRIVATE_RESIDENCE_OR_ESTATE_FULL_TIME_INSERVANT.getLabel(), "Yes",
				HomeSSMetaData.EndorsementTab.EndorsementHS2494.IS_THE_EMPLOYEE_A_PRIVATE_RESIDENCE_FULL_TIME_OUTSERVANT_INCLUDING_DRIVERS.getLabel(), "No",
				HomeSSMetaData.EndorsementTab.EndorsementHS2494.IS_THE_EMPLOYEE_A_PRIVATE_ESTATE_FULL_TIME_OUTSERVANT_INCLUDING_DRIVERS.getLabel(), "No",
				HomeSSMetaData.EndorsementTab.EndorsementHS2494.NUMBER_OF_EMPLOYEES_VALUE1.getLabel(), String.format("%d", openLPolicy.getForms().stream().filter(c -> "HS2494".equals(c.getFormCode())).findFirst().get().getOptionalValue().intValue())
		));
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
		DS0499(HomeSSMetaData.EndorsementTab.HS_04_99.getLabel(), "DS0499", formHS0499DataFunction),    //temporary
		HS0412(HomeSSMetaData.EndorsementTab.HS_04_12.getLabel(), "HS0412", formHS0412DataFunction),
		HS0420(HomeSSMetaData.EndorsementTab.HS_04_20.getLabel(), "HS0420", formHS0420DataFunction),
		HS0435(HomeSSMetaData.EndorsementTab.HS_04_35.getLabel(), "HS0435", formHS0435DataFunction),
		HS0436(HomeSSMetaData.EndorsementTab.HS_04_36.getLabel(), "HS0436", formHS0436DataFunction),
		HS0442(HomeSSMetaData.EndorsementTab.HS_04_42.getLabel(), "HS0442", formHS0442DataFunction),
		HS0443(HomeSSMetaData.EndorsementTab.HS_04_43.getLabel(), "HS0443", formHS0443DataFunction),
		HS0450(HomeSSMetaData.EndorsementTab.HS_04_50.getLabel(), "HS0450", formHS0450DataFunction),
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
		HS0493(HomeSSMetaData.EndorsementTab.HS_04_93.getLabel(), "HS0493", formHS0493DataFunction),
		HS0495(HomeSSMetaData.EndorsementTab.HS_04_95.getLabel(), "HS0495", formHS0495DataFunction),
		HS0499(HomeSSMetaData.EndorsementTab.HS_04_99.getLabel(), "HS0499", formHS0499DataFunction),
		HS0546(HomeSSMetaData.EndorsementTab.HS_05_46.getLabel(), "HS0546", formHS0546DataFunction),
		HS0578(HomeSSMetaData.EndorsementTab.HS_05_78.getLabel(), "HS0578", formHS0578DataFunction),
		HS0614(HomeSSMetaData.EndorsementTab.HS_06_14.getLabel(), "HS0614", formHS0614DataFunction),
		HS0904(HomeSSMetaData.EndorsementTab.HS_09_04.getLabel(), "HS0904", formHS0904DataFunction),
		HS0906(HomeSSMetaData.EndorsementTab.HS_09_06.getLabel(), "HS0906", formHS0906DataFunction),
		HS0926(HomeSSMetaData.EndorsementTab.HS_09_26.getLabel(), "HS0926", formHS0926DataFunction),
		HS0929(HomeSSMetaData.EndorsementTab.HS_09_29.getLabel(), "HS0929", formHS0929DataFunction),
		HS0931(HomeSSMetaData.EndorsementTab.HS_09_31.getLabel(), "HS0931", formHS0931DataFunction),
		HS0934(HomeSSMetaData.EndorsementTab.HS_09_34.getLabel(), "HS0934", formHS0934DataFunction),
		HS0965(HomeSSMetaData.EndorsementTab.HS_09_65.getLabel(), "HS0965", formHS0965DataFunction),
		HS0988(HomeSSMetaData.EndorsementTab.HS_09_88.getLabel(), "HS0988", formHS0988DataFunction),
		HS2338(HomeSSMetaData.EndorsementTab.HS_23_38.getLabel(), "HS2338", formHS2338DataFunction),
		HS2443(HomeSSMetaData.EndorsementTab.HS_24_43.getLabel(), "HS2443", formHS2443DataFunction),
		HS2452(HomeSSMetaData.EndorsementTab.HS_24_52.getLabel(), "HS2452", formHS2452DataFunction),
		HS2464(HomeSSMetaData.EndorsementTab.HS_24_64.getLabel(), "HS2464", formHS2464DataFunction),
		HS2471(HomeSSMetaData.EndorsementTab.HS_24_71.getLabel(), "HS2471", formHS2471DataFunction),
		HS2472(HomeSSMetaData.EndorsementTab.HS_24_72.getLabel(), "HS2472", formHS2472DataFunction),
		HS2473(HomeSSMetaData.EndorsementTab.HS_24_73.getLabel(), "HS2473", formHS2473DataFunction),
		HS2494(HomeSSMetaData.EndorsementTab.HS_24_94.getLabel(), "HS2494", formHS2494DataFunction);
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
