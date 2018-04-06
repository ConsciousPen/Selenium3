package aaa.helpers.openl.testdata_builder;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.EntitiesHolder;
import aaa.helpers.TestDataHelper;
import aaa.helpers.TestDataManager;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.pup.defaulttabs.*;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomUtils;
import toolkit.datax.DataFormat;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.*;

public class PUPTestDataGenerator extends TestDataGenerator<PUPOpenLPolicy> {
	public PUPTestDataGenerator(String state) {
		super(state);
	}

	public PUPTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	private final Map<String, String> getPrimaryPolicyForPup(TestData td) {
		if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
		}
		String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();
		Map<String, String> returnValue = new LinkedHashMap<>();
		String state = getState().intern();
		synchronized (state) {
			PolicyType type;
			if (state.equals(Constants.States.CA)) {
				type = PolicyType.HOME_CA_HO3;
			} else {
				type = PolicyType.HOME_SS_HO3;
			}
			String key = EntitiesHolder.makeDefaultPolicyKey(type, state);
			if (EntitiesHolder.isEntityPresent(key)) {
				returnValue.put("Primary_HO3", EntitiesHolder.getEntity(key));
			} else {
				type.get().createPolicy(td);
				EntitiesHolder.addNewEntity(key, PolicySummaryPage.labelPolicyNumber.getValue());
				returnValue.put("Primary_HO3", EntitiesHolder.getEntity(key));
			}
			//open Customer that was created in test
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
				SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.CUSTOMER, customerNum);
			}
			return returnValue;
		}
	}

	@Override
	public TestData getRatingData(PUPOpenLPolicy openLPolicy) {
		TestData td = DataProviderFactory.dataOf(
				new PrefillTab().getMetaKey(), getPrefillTabData(openLPolicy),
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new UnderlyingRisksPropertyTab().getMetaKey(), getUnderlyingRisksPropertyData(openLPolicy),
				new UnderlyingRisksAutoTab().getMetaKey(), getUnderlyingRisksAutoData(openLPolicy),
				new UnderlyingRisksOtherVehiclesTab().getMetaKey(), getUnderlyingRisksOtherVehiclesData(openLPolicy),
				new ClaimsTab().getMetaKey(), getClaimsData(openLPolicy),
				new EndorsementsTab().getMetaKey(), getEndorsementData(),
				new PremiumAndCoveragesQuoteTab().getMetaKey(), getPremiumAndCoveragesData(openLPolicy)
		);
		return TestDataHelper.merge(getRatingDataPattern(), td);
	}

	private TestData getPrimaryPolicyData(PUPOpenLPolicy openLPolicy, TestData primaryPolicyTd) {
		TestData td = DataProviderFactory.dataOf(
				new ApplicantTab().getMetaKey(), getApplicantTabPrimaryPolicyData(openLPolicy),
				new PropertyInfoTab().getMetaKey(), getPropertyInfoTabPrimaryPolicyData(openLPolicy)
		);
		return TestDataHelper.merge(primaryPolicyTd, td);
	}

	private TestData getApplicantTabPrimaryPolicyData(PUPOpenLPolicy openLPolicy) {
		TestData dwellingAddressData = new SimpleDataProvider();
		dwellingAddressData.adjust(HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getDwelling().get(0).getAddress().get(0).getZipCode());
		if (Boolean.TRUE.equals(openLPolicy.getDwelling().get(0).getRetirementCommunityInd())) {
			dwellingAddressData.adjust(HomeSSMetaData.ApplicantTab.DwellingAddress.RETIREMENT_COMMUNITY.getLabel(), "MountainBrook Village");
		}
		return DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData
		);
	}

	private TestData getPropertyInfoTabPrimaryPolicyData(PUPOpenLPolicy openLPolicy) {
		TestData recreationalEquipment = new SimpleDataProvider();

		if (Boolean.TRUE.equals(getTrampoline(openLPolicy))) {
			recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.TRAMPOLINE.getLabel(), "Restricted access in-ground with safety net");
		}

		if (Boolean.TRUE.equals(getSpaInd(openLPolicy))) {
			recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SPA_HOT_TUB.getLabel(), "Restricted access");
		}

		if (Boolean.TRUE.equals(getPoolInd(openLPolicy))) {
			if (Boolean.TRUE.equals(getSlideInd(openLPolicy) && getDivingBoardInd(openLPolicy))) {
				recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SWIMMING_POOL.getLabel(), "Restricted access with slide and diving board");
			}
			if (Boolean.TRUE.equals(getSlideInd(openLPolicy) && Boolean.FALSE.equals(getDivingBoardInd(openLPolicy)))) {
				recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SWIMMING_POOL.getLabel(), "Restricted access with slide only");
			}
			if (Boolean.FALSE.equals(getSlideInd(openLPolicy) && Boolean.TRUE.equals(getDivingBoardInd(openLPolicy)))) {
				recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SWIMMING_POOL.getLabel(), "Restricted access with diving board only");
			} else {
				recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SWIMMING_POOL.getLabel(), "Restricted access with no accessories");
			}
			recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SPA_HOT_TUB.getLabel(), "Restricted access");
		}

		return DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.RECREATIONAL_EQUIPMENT.getLabel(), recreationalEquipment
		);
	}

	private Boolean getPoolInd(PUPOpenLPolicy openLPolicy) {
		return openLPolicy.getDwelling().get(0).getRecEquipmentInfo().get(0).getPoolInd();
	}

	private Boolean getTrampoline(PUPOpenLPolicy openLPolicy) {
		return openLPolicy.getDwelling().get(0).getRecEquipmentInfo().get(0).getTrampolineInd();
	}

	private Boolean getSpaInd(PUPOpenLPolicy openLPolicy) {
		return openLPolicy.getDwelling().get(0).getRecEquipmentInfo().get(0).getSpaInd();
	}

	private Boolean getSlideInd(PUPOpenLPolicy openLPolicy) {
		return openLPolicy.getDwelling().get(0).getRecEquipmentInfo().get(0).getSlideInd();
	}

	private Boolean getDivingBoardInd(PUPOpenLPolicy openLPolicy) {
		return openLPolicy.getDwelling().get(0).getRecEquipmentInfo().get(0).getDivingBoardInd();
	}

	private TestData getPrefillTabData(PUPOpenLPolicy openLPolicy) {
		TestData tdPUP = new TestDataManager().policy.get(PolicyType.PUP).getTestData("DataGather", "TestData_AZ");
		TestData tdHO3 = getPrimaryPolicyData(openLPolicy, new TestDataManager().policy.get(PolicyType.HOME_SS_HO3).getTestData("DataGather", "TestData_AZ"));
		TestData preFillTabTd = new PrefillTab().adjustWithRealPolicies(tdPUP, getPrimaryPolicyForPup(tdHO3));
		return preFillTabTd.getTestData(new PrefillTab().getMetaKey());
	}

	private TestData getGeneralTabData(PUPOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	private TestData getUnderlyingRisksPropertyData(PUPOpenLPolicy openLPolicy) {
		TestData businessOrFarmingData = new SimpleDataProvider();
		List<TestData> additionalResidenciesData = new ArrayList<>();
		int numOfAddlResidences = openLPolicy.getNumOfAddlResidences();

		if (Boolean.TRUE.equals(openLPolicy.getBusinessPursuitsInd())) {
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.ADD_BUSINESS_OR_FARMING_COVERAGES.getLabel(), "Yes");
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.ENDORSEMENT.getLabel(), "HS 24 71 - Business Pursuits");
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.PROPERTY_POLICY_NUMBER.getLabel(), "regex=.*\\S.*");
		}

		if (Boolean.TRUE.equals(openLPolicy.getIncidentalFarmingInd())) {
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.ADD_BUSINESS_OR_FARMING_COVERAGES.getLabel(), "Yes");
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.ENDORSEMENT.getLabel(), "HS 24 72 - Incidental Farming Personal Liability Coverage");
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.PROPERTY_POLICY_NUMBER.getLabel(), "regex=.*\\S.*");
		}

		if (Boolean.TRUE.equals(openLPolicy.getIncidentalFarmingInd())) {
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.ADD_BUSINESS_OR_FARMING_COVERAGES.getLabel(), "Yes");
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.ENDORSEMENT.getLabel(), "HS 04 42 - Permitted Incidental Business Occupancies - Residence Premises");
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.PROPERTY_POLICY_NUMBER.getLabel(), "regex=.*\\S.*");
		}

		if (numOfAddlResidences > 0) {
			for (int i = 0; i < numOfAddlResidences; i++) {
				Map<String, Object> residency = new HashMap<>();
				if (residency.size() < 1) {
					residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.ADD.getLabel(), "Yes");
				}
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.ZIP_CODE.getLabel(), openLPolicy.getDwelling().get(0).getAddress().get(0).getZipCode());
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.STREET_ADDRESS_1.getLabel(), RandomUtils.nextInt(1001, 9999) + " S LAST CHANCE TRL");
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.POLICY_NUMBER.getLabel(), RandomUtils.nextInt(100001, 999999));
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.LIMIT_OF_LIABILITY.getLabel(), "$100.000");
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.OCCUPANCY_TYPE.getLabel(), "Secondary Residence");
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.CURRENT_CARRIER.getLabel(), "regex=.*\\S.*");
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.VALIDATE_ADDRESS_BTN.getLabel(), "Yes");
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.VALIDATE_ADDRESS_DIALOG.getLabel(), new SimpleDataProvider());
				additionalResidenciesData.add(new SimpleDataProvider(residency));
			}
		}

		return DataProviderFactory.dataOf(
				PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BUSINESS_OR_FARMING_COVERAGE.getLabel(), businessOrFarmingData,
				PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.ADDITIONAL_RESIDENCIES.getLabel(), additionalResidenciesData
		);
	}

	private TestData getUnderlyingRisksAutoData(PUPOpenLPolicy openLPolicy) {
		TestData tdUnderlyingRisksAuto = new DataProviderFactory().applyConfiguration(DataFormat.YAML.name()).get("modules/regression/sales/pup").getTestData("TestPolicyCreationFull", "UnderlyingRisksAutoTab");
		TestData defaultAutomobilesTd = tdUnderlyingRisksAuto.getTestDataList("Automobiles").get(0);
		int numOfMotorhomes = openLPolicy.getRiskItems().get(0).getRiskItemCount();
		int numOfMotorcycles = openLPolicy.getRiskItems().get(1).getRiskItemCount();
		int numOfAntique = openLPolicy.getRiskItems().get(3).getRiskItemCount();

		if (Boolean.FALSE.equals(openLPolicy.getDropDownInd())) {
			defaultAutomobilesTd.adjust(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.BI_LIMITS.getLabel(), Arrays.asList("250000", "250000"));
		}
		int numOfSeniorDriver = openLPolicy.getNumOfSeniorOps();
		int numOfYouthDriver = openLPolicy.getNumOfYouthfulOps();
		List<TestData> tdDrivers = new ArrayList<>();
		List<TestData> tdMotorHomes = new ArrayList<>();
		List<TestData> tdMotorCycles = new ArrayList<>();
		List<TestData> tdAntiqueCars = new ArrayList<>();
		List<TestData> tdAutomobiles = new ArrayList<>();
		// add default car and driver to Test Data
		tdAutomobiles.add(defaultAutomobilesTd);
		tdDrivers.add(new DataProviderFactory().applyConfiguration(DataFormat.YAML.name()).get("modules/regression/sales/pup").getTestData("TestPolicyCreationFull", "UnderlyingRisksAutoTab").getTestData("Drivers"));

		if (numOfSeniorDriver + numOfYouthDriver > 0) {
			if (numOfSeniorDriver > 0) {
				for (int i = 0; i < numOfSeniorDriver; i++) {
					Map<String, Object> seniorDriver = new HashMap<>();
					seniorDriver.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.FIRST_NAME.getLabel(), "John" + RandomUtils.nextInt(1001, 9999));
					seniorDriver.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.LAST_NAME.getLabel(), "Smith" + RandomUtils.nextInt(1001, 9999));
					seniorDriver.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.DATE_OF_BIRTH.getLabel(), DateTimeUtils.getCurrentDateTime().minusYears(71).format(DateTimeUtils.MM_DD_YYYY));
					tdDrivers.add(new SimpleDataProvider(seniorDriver));
				}
			}
			if (numOfYouthDriver > 0) {
				for (int i = 0; i < numOfYouthDriver; i++) {
					Map<String, Object> youthDriver = new HashMap<>();
					youthDriver.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.FIRST_NAME.getLabel(), "John" + RandomUtils.nextInt(1001, 9999));
					youthDriver.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.LAST_NAME.getLabel(), "Smith" + RandomUtils.nextInt(1001, 9999));
					youthDriver.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.DATE_OF_BIRTH.getLabel(), DateTimeUtils.getCurrentDateTime().minusYears(19).format(DateTimeUtils.MM_DD_YYYY));
					tdDrivers.add(new SimpleDataProvider(youthDriver));
				}
			}
		}
		if (numOfMotorhomes > 0) {
			for (int i = 0; i < numOfMotorhomes; i++) {
				Map<String, Object> motorHome = new HashMap<>();
				motorHome.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MotorHomes.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				motorHome.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MotorHomes.MAKE.getLabel(), "BMW");
				motorHome.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MotorHomes.MODEL.getLabel(), "Test Model");
				if (tdMotorHomes.size() < 1) {
					motorHome.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MotorHomes.ADD_MOTORE_HOME.getLabel(), "Yes");
				}
				tdMotorHomes.add(new SimpleDataProvider(motorHome));
			}
		}

		if (numOfMotorcycles > 0) {
			for (int i = 0; i < numOfMotorcycles; i++) {
				Map<String, Object> motorCycle = new HashMap<>();
				motorCycle.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Motorcycles.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				motorCycle.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Motorcycles.MAKE.getLabel(), "BMW");
				motorCycle.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Motorcycles.MODEL.getLabel(), "Test Model");
				if (tdMotorCycles.size() < 1) {
					motorCycle.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Motorcycles.ADD_MOTORCYCLE.getLabel(), "Yes");
				}
				tdMotorCycles.add(new SimpleDataProvider(motorCycle));
			}
		}

		if (numOfAntique > 0) {
			for (int i = 0; i < numOfAntique; i++) {
				Map<String, Object> antiqueCar = new HashMap<>();
				antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.CAR_TYPE.getLabel(), "Limited Production/Antique");
				antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.MAKE.getLabel(), getRandom("BENTLEY", "AUDI", "BMW"));
				antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.MODEL.getLabel(), "regex=.*\\S.*");
				antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.STATE.getLabel(), getState());
				if (tdAntiqueCars.size() < 1) {
					antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.ADD.getLabel(), "Yes");
				}
				tdAutomobiles.add(new SimpleDataProvider(antiqueCar));
			}
		}

		return DataProviderFactory.dataOf(
				PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.DRIVERS.getLabel(), tdDrivers,
				PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel(), tdAutomobiles,
				PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MOTORCYCLES.getLabel(), tdMotorCycles,
				PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.MOTOR_HOMES.getLabel(), tdMotorHomes
		);
	}

	private TestData getUnderlyingRisksOtherVehiclesData(PUPOpenLPolicy openLPolicy) {
		List<TestData> tdWaterCrafts = new ArrayList<>();
		List<TestData> tdRecreationVehicles = new ArrayList<>();
		//int numOfWaterCraft_I = openLPolicy.getRiskItems().get(5).getRiskItemCount();
		int numOfWaterCraft_II = openLPolicy.getRiskItems().get(6).getRiskItemCount();
		int numOfWaterCraft_III = openLPolicy.getRiskItems().get(7).getRiskItemCount();
		//int numOfWaterCraft_IV = openLPolicy.getRiskItems().get(8).getRiskItemCount();
		int numOfWaterCraft_V = openLPolicy.getRiskItems().get(9).getRiskItemCount();
		int numOfWaterCraft_VI = openLPolicy.getRiskItems().get(10).getRiskItemCount();
		int numOfWaterCraft_PERS = openLPolicy.getRiskItems().get(11).getRiskItemCount();

		if (numOfWaterCraft_II > 0) {
			for (int i = 0; i < numOfWaterCraft_II; i++) {
				Map<String, Object> waterCraft_II = new HashMap<>();
				waterCraft_II.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.TYPE.getLabel(), "In/Outboard Powerboat");
				waterCraft_II.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				waterCraft_II.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MAKE.getLabel(), "BMW");
				waterCraft_II.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MODEL.getLabel(), "Test Model");
				waterCraft_II.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.LENGTH.getLabel(), "regex=.*\\S.*");
				waterCraft_II.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.HORSEPOWER.getLabel(), "regex=.*\\S.*");
				if (tdWaterCrafts.size() < 1) {
					waterCraft_II.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.ADD_WATERCRAFT.getLabel(), "Yes");
				}
				tdWaterCrafts.add(new SimpleDataProvider(waterCraft_II));
			}
		}

		if (numOfWaterCraft_III > 0) {
			for (int i = 0; i < numOfWaterCraft_III; i++) {
				Map<String, Object> waterCraft_III = new HashMap<>();
				waterCraft_III.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.TYPE.getLabel(), "Inboard Powerboat");
				waterCraft_III.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				waterCraft_III.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MAKE.getLabel(), "BMW");
				waterCraft_III.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MODEL.getLabel(), "Test Model");
				waterCraft_III.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.LENGTH.getLabel(), "regex=.*\\S.*");
				waterCraft_III.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.HORSEPOWER.getLabel(), "regex=.*\\S.*");
				if (tdWaterCrafts.size() < 1) {
					waterCraft_III.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.ADD_WATERCRAFT.getLabel(), "Yes");
				}
				tdWaterCrafts.add(new SimpleDataProvider(waterCraft_III));
			}
		}

		if (numOfWaterCraft_V > 0) {
			for (int i = 0; i < numOfWaterCraft_V; i++) {
				Map<String, Object> waterCraft_V = new HashMap<>();
				waterCraft_V.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.TYPE.getLabel(), "Outboard Powerboat");
				waterCraft_V.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				waterCraft_V.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MAKE.getLabel(), "BMW");
				waterCraft_V.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MODEL.getLabel(), "Test Model");
				waterCraft_V.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.LENGTH.getLabel(), "regex=.*\\S.*");
				waterCraft_V.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.HORSEPOWER.getLabel(), "regex=.*\\S.*");
				if (tdWaterCrafts.size() < 1) {
					waterCraft_V.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.ADD_WATERCRAFT.getLabel(), "Yes");
				}
				tdWaterCrafts.add(new SimpleDataProvider(waterCraft_V));
			}
		}

		if (numOfWaterCraft_VI > 0) {
			for (int i = 0; i < numOfWaterCraft_VI; i++) {
				Map<String, Object> waterCraft_VI = new HashMap<>();
				waterCraft_VI.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.TYPE.getLabel(), "Sailboat");
				waterCraft_VI.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				waterCraft_VI.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MAKE.getLabel(), "BMW");
				waterCraft_VI.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MODEL.getLabel(), "Test Model");
				if (tdWaterCrafts.size() < 1) {
					waterCraft_VI.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.ADD_WATERCRAFT.getLabel(), "Yes");
				}
				tdWaterCrafts.add(new SimpleDataProvider(waterCraft_VI));
			}
		}

		if (numOfWaterCraft_PERS > 0) {
			for (int i = 0; i < numOfWaterCraft_PERS; i++) {
				Map<String, Object> waterCraft_PERS = new HashMap<>();
				waterCraft_PERS.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.TYPE.getLabel(), "Personal Watercraft");
				waterCraft_PERS.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				waterCraft_PERS.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MAKE.getLabel(), "BMW");
				waterCraft_PERS.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MODEL.getLabel(), "Test Model");
				if (tdWaterCrafts.size() < 1) {
					waterCraft_PERS.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.ADD_WATERCRAFT.getLabel(), "Yes");
				}
				tdWaterCrafts.add(new SimpleDataProvider(waterCraft_PERS));
			}
		}

		return DataProviderFactory.dataOf(
				PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.WATERCRAFT.getLabel(), tdWaterCrafts,
				PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RECREATIONAL_VEHICLE.getLabel(), tdRecreationVehicles
		);
	}

	private TestData getClaimsData(PUPOpenLPolicy openLPolicy) {
		int numOfViolations = openLPolicy.getNumOfViolations();
		int numOfAccidents = openLPolicy.getNumOfAccidents();
		List<TestData> violationsTestDataList = new ArrayList<>();
		LocalDateTime occurrenceDate = openLPolicy.getEffectiveDate().minusYears(1);
		if (numOfViolations > 0 || numOfAccidents > 0) {
			if (numOfViolations > 0) {
				for (int i = 0; i < numOfViolations; i++) {
					Map<String, Object> autoViolationsData = new HashMap<>();
					autoViolationsData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.SELECT_DRIVER.getLabel(), "regex=.*\\S.*");
					autoViolationsData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.TYPE
							.getLabel(), getRandom("Major Violation", "Minor Violation", "Speeding Violation", "Alcohol-Related Violation"));
					autoViolationsData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.DESCRIPTION.getLabel(), "Other");
					autoViolationsData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.OCCURRENCE_DATE.getLabel(), occurrenceDate.format(DateTimeUtils.MM_DD_YYYY));
					if (violationsTestDataList.size() < 1) {
						autoViolationsData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.ADD_AUTO_VIOLATION_CLAIM.getLabel(), "Yes");
					}
					violationsTestDataList.add(new SimpleDataProvider(autoViolationsData));
				}
			}
			if (numOfAccidents > 0) {
				for (int i = 0; i < numOfAccidents; i++) {
					Map<String, Object> autoAccidentData = new HashMap<>();
					autoAccidentData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.SELECT_DRIVER.getLabel(), "regex=.*\\S.*");
					autoAccidentData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.TYPE.getLabel(), "At-Fault Accident");
					autoAccidentData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.DESCRIPTION.getLabel(), "Other");
					autoAccidentData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.OCCURRENCE_DATE.getLabel(), occurrenceDate.format(DateTimeUtils.MM_DD_YYYY));
					autoAccidentData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.LOSS_PAYMENT_AMOUNT.getLabel(), RandomUtils.nextInt(100, 10000));
					if (violationsTestDataList.size() < 1) {
						autoAccidentData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.ADD_AUTO_VIOLATION_CLAIM.getLabel(), "Yes");
					}
					violationsTestDataList.add(new SimpleDataProvider(autoAccidentData));
				}
			}
		}

		return DataProviderFactory.dataOf(
				PersonalUmbrellaMetaData.ClaimsTab.AUTO_VIOLATIONS_CLAIMS.getLabel(), violationsTestDataList
		);
	}

	private TestData getEndorsementData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getPremiumAndCoveragesData(PUPOpenLPolicy openLPolicy) {
		TestData premiumAndCoverageTabData = new SimpleDataProvider();
		if (openLPolicy.getCoverages().get(0).getLimitAmount() != null) {
			premiumAndCoverageTabData.adjust(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA.getLabel(), "$" + NumberFormat.getInstance().format(openLPolicy.getCoverages().get(0).getLimitAmount()));
		}
		return premiumAndCoverageTabData;
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
