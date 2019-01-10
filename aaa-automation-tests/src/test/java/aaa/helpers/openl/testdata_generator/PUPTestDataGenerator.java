package aaa.helpers.openl.testdata_generator;

import java.time.LocalDate;
import java.util.*;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataHelper;
import aaa.helpers.TestDataManager;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.helpers.openl.model.pup.PUPOpenLRiskItem;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.modules.policy.pup.defaulttabs.*;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.TestDataException;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;

public class PUPTestDataGenerator extends TestDataGenerator<PUPOpenLPolicy> {
	private static PUPCreditScoreGenerator creditScoreGenerator = new PUPCreditScoreGenerator();

	public PUPTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	static PolicyType getHomePolicyType(PUPOpenLPolicy openLPolicy) {
		return openLPolicy.getRentalUnitsCount() > 0 ? PolicyType.HOME_SS_HO6 : PolicyType.HOME_SS_HO3;
	}

	@Override
	public TestData getRatingData(PUPOpenLPolicy openLPolicy) {
		TestData td = DataProviderFactory.dataOf(
				new PrefillTab().getMetaKey(), getPrefillTabData(openLPolicy),
				new GeneralTab().getMetaKey(), getGeneralTabData(),
				new UnderlyingRisksPropertyTab().getMetaKey(), getUnderlyingRisksPropertyData(openLPolicy),
				new UnderlyingRisksAutoTab().getMetaKey(), getUnderlyingRisksAutoData(openLPolicy),
				new UnderlyingRisksOtherVehiclesTab().getMetaKey(), getUnderlyingRisksOtherVehiclesData(openLPolicy),
				new ClaimsTab().getMetaKey(), getClaimsData(openLPolicy),
				new EndorsementsTab().getMetaKey(), getEndorsementData(),
				new PremiumAndCoveragesQuoteTab().getMetaKey(), getPremiumAndCoveragesData(openLPolicy)
		);
		return TestDataHelper.merge(getRatingDataPattern(), td);
	}

	protected TestData getStateTestData(TestData td, String fileName, String tdName) {
		if (!td.containsKey(fileName)) {
			throw new TestDataException("Can't get test data file " + fileName);
		}
		return getStateTestData(td.getTestData(fileName), tdName);
	}

	protected TestData getStateTestData(TestData td, String tdName) {
		if (td == null) {
			throw new RuntimeException(String.format("Can't get TestData '%s', parrent TestData is null", tdName));
		}
		if (td.containsKey(getStateTestDataName(tdName))) {
			td = td.getTestData(getStateTestDataName(tdName));
			log.info(String.format("==== %s Test Data is used: %s ====", getState(), getStateTestDataName(tdName)));
		} else {
			td = td.getTestData(tdName);
			log.info(String.format("==== Default state UT Test Data is used. Requested Test Data: %s is missing ====", getStateTestDataName(tdName)));
		}
		return td;
	}

	private String getStateTestDataName(String tdName) {
		String state = getState();
		tdName = tdName + "_" + state;
		return tdName;
	}

	private Map<String, String> getPrimaryPolicyForPup(TestData td, PUPOpenLPolicy openLPolicy) {
		if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
		}
		String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();
		Map<String, String> returnValue = new LinkedHashMap<>();
		String state = getState().intern();
		synchronized (state) {
			String policyType = openLPolicy.getRentalUnitsCount() > 0 ? "HO6" : "HO3";
			getHomePolicyType(openLPolicy).get().createPolicy(td);
			returnValue.put(PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.POLICY_NUMBER.getLabel(), PolicySummaryPage.labelPolicyNumber.getValue());
			returnValue.put(PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.POLICY_TYPE.getLabel(), policyType);
			//open Customer that was created in test
			if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
				SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.CUSTOMER, customerNum);
			}
			return returnValue;
		}
	}

	private TestData getPrefillTabData(PUPOpenLPolicy openLPolicy) {
		TestData tdPUP = getStateTestData(new TestDataManager().policy.get(PolicyType.PUP), "DataGather", "TestData");
		TestData tdHO = getPrimaryPolicyData(openLPolicy, getStateTestData(new TestDataManager().policy.get(getHomePolicyType(openLPolicy)), "DataGather", "TestData"));
		TestData preFillTabTd = adjustWithRealPolicies(tdPUP, getPrimaryPolicyForPup(tdHO, openLPolicy));
		return preFillTabTd.getTestData(new PrefillTab().getMetaKey());
	}

	private TestData adjustWithRealPolicies(TestData td, Map<String, String> policies) {
		String pathToList = TestData.makeKeyPath(new PrefillTab().getMetaKey(), PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel());
		String pathToValue = PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel();
		String policyNumberKey = PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.POLICY_NUMBER.getLabel();
		String policyTypeKey = PersonalUmbrellaMetaData.PrefillTab.ActiveUnderlyingPolicies.ActiveUnderlyingPoliciesSearch.POLICY_TYPE.getLabel();
		List<TestData> td2 = td.getTestData(new PrefillTab().getMetaKey()).getTestDataList(PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel());
		for (TestData tempTD : td2) {
			if (tempTD.getTestData(pathToValue) != null) {
				tempTD.adjust(TestData.makeKeyPath(pathToValue, policyNumberKey), policies.get(policyNumberKey));
				tempTD.adjust(TestData.makeKeyPath(pathToValue, policyTypeKey), policies.get(policyTypeKey));
			}
		}
		td.adjust(pathToList, td2);
		return td;
	}

	private TestData getPrimaryPolicyData(PUPOpenLPolicy openLPolicy, TestData primaryPolicyTd) {
		TestData insuranceScoreOverrideData = DataProviderFactory.emptyData();
		if (!Constants.States.MD.equals(getState())) {
			insuranceScoreOverrideData = DataProviderFactory.dataOf(
					HomeSSMetaData.ReportsTab.InsuranceScoreOverrideRow.ACTION.getLabel(), "Override Score",
					HomeSSMetaData.ReportsTab.InsuranceScoreOverrideRow.EDIT_INSURANCE_SCORE.getLabel(), DataProviderFactory.dataOf(
							HomeSSMetaData.ReportsTab.EditInsuranceScoreDialog.SCORE_AFTER_OVERRIDE.getLabel(), creditScoreGenerator.get(openLPolicy),
							HomeSSMetaData.ReportsTab.EditInsuranceScoreDialog.REASON_FOR_OVERRIDE.getLabel(), "Fair Credit Reporting Act Dispute",
							HomeSSMetaData.ReportsTab.EditInsuranceScoreDialog.BTN_SAVE.getLabel(), "click"
					)
			);
		}

		TestData td = DataProviderFactory.dataOf(
				new ApplicantTab().getMetaKey(), getApplicantTabPrimaryPolicyData(openLPolicy),
				new PropertyInfoTab().getMetaKey(), getPropertyInfoTabPrimaryPolicyData(openLPolicy),
				new ReportsTab().getMetaKey(), DataProviderFactory.dataOf(HomeSSMetaData.ReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel(), insuranceScoreOverrideData)
		);
		return TestDataHelper.merge(primaryPolicyTd, td);
	}

	private TestData getApplicantTabPrimaryPolicyData(PUPOpenLPolicy openLPolicy) {
		Map<String, Object> dwellingAddressData = new HashMap<>();
		dwellingAddressData.put(HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getDwelling().getAddress().getZipCode());

		if ("IN".equals(getState()) || "WV".equals(getState()) || "OH".equals(getState())) {
			dwellingAddressData.put(HomeSSMetaData.ApplicantTab.DwellingAddress.COUNTY.getLabel(), "1");
		}
		if (Boolean.TRUE.equals(openLPolicy.getDwelling().getRetirementCommunityInd())) {
			dwellingAddressData.put(HomeSSMetaData.ApplicantTab.DwellingAddress.RETIREMENT_COMMUNITY.getLabel(), "MountainBrook Village");
		}
		return DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), new SimpleDataProvider(dwellingAddressData)
		);
	}

	private TestData getPropertyInfoTabPrimaryPolicyData(PUPOpenLPolicy openLPolicy) {
		TestData recreationalEquipment = new SimpleDataProvider();
		TestData dwellingAddress = new SimpleDataProvider();
		TestData interior = new SimpleDataProvider();
		TestData rentalInformation = new SimpleDataProvider();

		if (Boolean.TRUE.equals(getTrampoline(openLPolicy))) {
			recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.TRAMPOLINE.getLabel(), "Restricted access in-ground with safety net");
		}

		if (Boolean.TRUE.equals(getSpaInd(openLPolicy))) {
			recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SPA_HOT_TUB.getLabel(), "Restricted access");
		}

		if (Boolean.TRUE.equals(getPoolInd(openLPolicy))) {
			if (getSlideInd(openLPolicy) && getDivingBoardInd(openLPolicy)) {
				recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SWIMMING_POOL.getLabel(), "Restricted access with slide and diving board");
			}
			if (getSlideInd(openLPolicy) && !getDivingBoardInd(openLPolicy)) {
				recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SWIMMING_POOL.getLabel(), "Restricted access with slide only");
			}
			if (getDivingBoardInd(openLPolicy) && !getSlideInd(openLPolicy)) {
				recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SWIMMING_POOL.getLabel(), "Restricted access with diving board only");
			}
			if (!getDivingBoardInd(openLPolicy) && !getSlideInd(openLPolicy)) {
				recreationalEquipment.adjust(HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SWIMMING_POOL.getLabel(), "Restricted access with no accessories");
			}
		}

		int rentalUnitsCount = openLPolicy.getRentalUnitsCount();
		if (rentalUnitsCount > 0) {
			interior.adjust(HomeSSMetaData.PropertyInfoTab.Interior.OCCUPANCY_TYPE.getLabel(), "Tenant occupied");
			rentalInformation.adjust(HomeSSMetaData.PropertyInfoTab.RentalInformation.NUMBER_OF_CONSECUTIVE_YEARS_INSURED_HAS_OWNED_ANY_RENTAL_PROPERTIES.getLabel(), "1");
			rentalInformation.adjust(HomeSSMetaData.PropertyInfoTab.RentalInformation.PROPERTY_MANAGER.getLabel(), "regex=.*\\S.*");
			rentalInformation.adjust(HomeSSMetaData.PropertyInfoTab.RentalInformation.DOES_THE_TENANT_HAVE_AN_UNDERLYING_HO4_POLICY.getLabel(), "No");
			if (rentalUnitsCount == 1) {
				dwellingAddress.adjust(HomeSSMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "1-Single Family");
			}
			if (rentalUnitsCount == 2) {
				dwellingAddress.adjust(HomeSSMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "2-Duplex");
			}
			if (rentalUnitsCount == 3) {
				dwellingAddress.adjust(HomeSSMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "3-Triplex");
			}
		}

		return DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.RECREATIONAL_EQUIPMENT.getLabel(), recreationalEquipment,
				HomeSSMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddress,
				HomeSSMetaData.PropertyInfoTab.INTERIOR.getLabel(), interior,
				HomeSSMetaData.PropertyInfoTab.RENTAL_INFORMATION.getLabel(), rentalInformation
		);
	}

	private Boolean getPoolInd(PUPOpenLPolicy openLPolicy) {
		return openLPolicy.getDwelling().getRecEquipmentInfo().getPoolInd();
	}

	private Boolean getTrampoline(PUPOpenLPolicy openLPolicy) {
		return openLPolicy.getDwelling().getRecEquipmentInfo().getTrampolineInd();
	}

	private Boolean getSpaInd(PUPOpenLPolicy openLPolicy) {
		return openLPolicy.getDwelling().getRecEquipmentInfo().getSpaInd();
	}

	private Boolean getSlideInd(PUPOpenLPolicy openLPolicy) {
		return openLPolicy.getDwelling().getRecEquipmentInfo().getSlideInd();
	}

	private Boolean getDivingBoardInd(PUPOpenLPolicy openLPolicy) {
		return openLPolicy.getDwelling().getRecEquipmentInfo().getDivingBoardInd();
	}

	private TestData getGeneralTabData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getUnderlyingRisksPropertyData(PUPOpenLPolicy openLPolicy) {
		List<TestData> businessOrFarmingData = new ArrayList<>();
		List<TestData> additionalResidenciesData = new ArrayList<>();
		int numOfAddlResidences = openLPolicy.getNumOfAddlResidences();

		if (Boolean.TRUE.equals(openLPolicy.getBusinessPursuitsInd())) {
			businessOrFarmingData.add(new SimpleDataProvider(getBusinessOrFarmingData(businessOrFarmingData, "HS 24 71 - Business Pursuits")));
		}

		if (Boolean.TRUE.equals(openLPolicy.getIncidentalFarmingInd())) {
			businessOrFarmingData.add(new SimpleDataProvider(getBusinessOrFarmingData(businessOrFarmingData, "HS 24 72 - Incidental Farming Personal Liability Coverage")));
		}

		if (Boolean.TRUE.equals(openLPolicy.getPermittedOccupancyInd())) {
			businessOrFarmingData.add(new SimpleDataProvider(getBusinessOrFarmingData(businessOrFarmingData, "HS 04 42 - Permitted Incidental Business Occupancies - Residence Premises")));
		}

		if (numOfAddlResidences > 0) {
			if (openLPolicy.getRentalUnitsCount() > 0) {
				numOfAddlResidences = numOfAddlResidences + 1;
			}
			for (int i = 0; i < numOfAddlResidences; i++) {
				Map<String, Object> residency = new HashMap<>();
				if (additionalResidenciesData.size() < 1) {
					residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.ADD.getLabel(), "Yes");
				}
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.POLICY_TYPE.getLabel(), "HO6");
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.ZIP_CODE.getLabel(), openLPolicy.getDwelling().getAddress().getZipCode());
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.STREET_ADDRESS_1.getLabel(), RandomUtils.nextInt(1001, 9999) + " S LAST CHANCE TRL");
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.POLICY_NUMBER.getLabel(), RandomUtils.nextInt(100001, 999999));
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.EFFECTIVE_DATE.getLabel(), TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY));
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.LIMIT_OF_LIABILITY.getLabel(), "$100,000");
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.NUMBER_OF_UNITS_ACRES.getLabel(), "2");
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.OCCUPANCY_TYPE.getLabel(), "Secondary Residence");
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.CURRENT_CARRIER.getLabel(), "regex=.*\\S.*");
				residency.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.AdditionalResidencies.DEDUCTIBLE.getLabel(), "100");
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

	private Map<String, Object> getBusinessOrFarmingData(List<TestData> businessOrFarmingData, String endorsementValue) {
		Map<String, Object> map = new HashMap<>();
		map.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.ENDORSEMENT.getLabel(), endorsementValue);
		map.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.PROPERTY_POLICY_NUMBER.getLabel(), "regex=.*\\S.*");
		if (businessOrFarmingData.size() < 1) {
			map.put(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.ADD_BUSINESS_OR_FARMING_COVERAGES.getLabel(), "Yes");
		}
		return map;
	}

	private void getWatercraftData(List<TestData> tdWaterCrafts, int numOfWaterCrafts, String[] listOfValues) {
		Map<String, Object> waterCrafts = new HashMap<>();
		for (int i = 0; i < numOfWaterCrafts; i++) {
			waterCrafts.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.TYPE.getLabel(), listOfValues[0]);
			waterCrafts.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
			waterCrafts.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MAKE.getLabel(), "BMW");
			waterCrafts.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MODEL.getLabel(), "Test Model");
			waterCrafts.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.LENGTH.getLabel(), listOfValues[1]);
			waterCrafts.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.HORSEPOWER.getLabel(), listOfValues[2]);
			if (tdWaterCrafts.size() < 1) {
				waterCrafts.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.ADD_WATERCRAFT.getLabel(), "Yes");
			}
			tdWaterCrafts.add(new SimpleDataProvider(waterCrafts));
		}
	}

	private TestData getClaimsData(PUPOpenLPolicy openLPolicy) {
		int numOfViolations = openLPolicy.getNumOfViolations();
		int numOfAccidents = openLPolicy.getNumOfAccidents();
		List<TestData> violationsTestDataList = new ArrayList<>();
		LocalDate occurrenceDate = openLPolicy.getEffectiveDate().minusYears(1);
		if (numOfViolations > 0 || numOfAccidents > 0) {
			if (numOfViolations > 0) {
				for (int i = 0; i < numOfViolations; i++) {
					Map<String, Object> autoViolationsData = new HashMap<>();
					autoViolationsData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.SELECT_DRIVER.getLabel(), "regex=.*\\S.*");
					autoViolationsData.put(PersonalUmbrellaMetaData.ClaimsTab.AutoViolationsClaims.TYPE.getLabel(), "Speeding Violation");
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

	private TestData getUnderlyingRisksAutoData(PUPOpenLPolicy openLPolicy) {
		int numOfMotorhomes = openLPolicy.getRiskItems().stream().filter(c -> "Motorhome".equals(c.getRiskItemCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfMotorcycles = openLPolicy.getRiskItems().stream().filter(c -> "Motorcycle".equals(c.getRiskItemCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfAntique = openLPolicy.getRiskItems().stream().filter(c -> "Antique".equals(c.getRiskItemCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfAddlAuto = openLPolicy.getRiskItems().stream().filter(c -> "AddlAuto".equals(c.getRiskItemCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfAutoCredit = openLPolicy.getRiskItems().stream().filter(c -> "AutoCredit".equals(c.getRiskItemCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int maxAutoCount = getState().equals(Constants.States.CA) ? 1 : 2;

		int numOfSeniorDriver = openLPolicy.getNumOfSeniorOps();
		int numOfYouthDriver = openLPolicy.getNumOfYouthfulOps();
		List<TestData> tdDrivers = new ArrayList<>();
		List<TestData> tdMotorHomes = new ArrayList<>();
		List<TestData> tdMotorCycles = new ArrayList<>();
		List<TestData> tdAutomobiles = new ArrayList<>();

		//add default driver
		Map<String, Object> defaultDriver = new HashMap<>();
		defaultDriver.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.ADD_DRIVERS.getLabel(), "Yes");
		defaultDriver.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.FIRST_NAME.getLabel(), "John" + RandomUtils.nextInt(1001, 9999));
		defaultDriver.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.LAST_NAME.getLabel(), "Smith" + RandomUtils.nextInt(1001, 9999));
		defaultDriver.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Drivers.DATE_OF_BIRTH.getLabel(), DateTimeUtils.getCurrentDateTime().minusYears(36).format(DateTimeUtils.MM_DD_YYYY));
		tdDrivers.add(new SimpleDataProvider(defaultDriver));

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
				antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.CURRENT_CARRIER.getLabel(), "regex=.*\\S.*");
				if (tdAutomobiles.size() < 1) {
					antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.ADD_AUTOMOBILE.getLabel(), "Yes");
					antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.PRIMARY_AUTO_POLICY.getLabel(), "Yes");
					antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.IS_THIS_A_SIGNATURE_SERIES_AUTO_POLICY.getLabel(), "Yes");
					antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.COVERAGE_TYPE.getLabel(), "Split");
					if (Boolean.FALSE.equals(openLPolicy.getDropDownInd())) {
						antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.BI_LIMITS.getLabel(), Arrays.asList("250000", "250000"));
					} else {
						antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.BI_LIMITS.getLabel(), Arrays.asList("250000", "500000"));
					}
					antiqueCar.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.PD_LIMITS.getLabel(), "100000");
				}
				tdAutomobiles.add(new SimpleDataProvider(antiqueCar));
			}
		}

		int numOfAutoToAdd = 0;
		if (numOfAutoCredit < maxAutoCount) {
			numOfAutoToAdd = maxAutoCount - numOfAutoCredit;
		}
		if (numOfAddlAuto >= maxAutoCount) {
			numOfAutoToAdd = numOfAddlAuto + maxAutoCount;
		}
		if (numOfAddlAuto == 1) {
			numOfAutoToAdd = 1 + maxAutoCount;
		}
		for (int i = 0; i < numOfAutoToAdd; i++) {
			Map<String, Object> addlAuto = new HashMap<>();
			addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.CAR_TYPE.getLabel(), "Private Passenger Auto");
			addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
			addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.MAKE.getLabel(), getRandom("BENTLEY", "AUDI", "BMW"));
			addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.MODEL.getLabel(), "regex=.*\\S.*");
			addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.STATE.getLabel(), getState());
			addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.CURRENT_CARRIER.getLabel(), "regex=.*\\S.*");
			if (tdAutomobiles.size() < 1) {
				addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.ADD_AUTOMOBILE.getLabel(), "Yes");
				addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.PRIMARY_AUTO_POLICY.getLabel(), "Yes");
				if (!Constants.States.PA.equals(getState())) { //autoTier for PA has different values and looks like does not affect rating
					addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.AUTO_TIER.getLabel(), openLPolicy.getAutoTier());
					addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.POLICY_NUM.getLabel(), String.format("%sSS123456", getState()));
				}
				addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.COVERAGE_TYPE.getLabel(), "Split");
				if (Boolean.FALSE.equals(openLPolicy.getDropDownInd())) {
					addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.BI_LIMITS.getLabel(), Arrays.asList("250000", "250000"));
				} else {
					addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.BI_LIMITS.getLabel(), Arrays.asList("250000", "500000"));
				}
				addlAuto.put(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.PD_LIMITS.getLabel(), "100000");
			}
			tdAutomobiles.add(new SimpleDataProvider(addlAuto));
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
		int numOfWaterCraftI = openLPolicy.getRiskItems().stream().filter(c -> "I".equals(c.getRiskItemCategoryCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfWaterCraftII = openLPolicy.getRiskItems().stream().filter(c -> "II".equals(c.getRiskItemCategoryCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfWaterCraftIII = openLPolicy.getRiskItems().stream().filter(c -> "III".equals(c.getRiskItemCategoryCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfWaterCraftIV = openLPolicy.getRiskItems().stream().filter(c -> "IV".equals(c.getRiskItemCategoryCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfWaterCraftV = openLPolicy.getRiskItems().stream().filter(c -> "V".equals(c.getRiskItemCategoryCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfWaterCraftVI = openLPolicy.getRiskItems().stream().filter(c -> "VI".equals(c.getRiskItemCategoryCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfWaterCraftPERS = openLPolicy.getRiskItems().stream().filter(c -> "PERS".equals(c.getRiskItemCategoryCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfAtv = openLPolicy.getRiskItems().stream().filter(c -> "ATV".equals(c.getRiskItemCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);
		int numOfSnowmobile = openLPolicy.getRiskItems().stream().filter(c -> "Snowmobile".equals(c.getRiskItemCd())).map(PUPOpenLRiskItem::getRiskItemCount).findFirst().orElse(0);

		if (numOfWaterCraftI > 0) {
			String[] listOfWatercraftIFieldValues = {"Inboard Powerboat", "14-25 ft", "0-100 hp"};
			getWatercraftData(tdWaterCrafts, numOfWaterCraftI, listOfWatercraftIFieldValues);
		}

		if (numOfWaterCraftII > 0) {
			String[] listOfWatercraftIIFieldValues = {"In/Outboard Powerboat", "14-25 ft", "101-200 hp"};
			getWatercraftData(tdWaterCrafts, numOfWaterCraftII, listOfWatercraftIIFieldValues);
		}

		if (numOfWaterCraftIII > 0) {
			String[] listOfWatercraftIIIFieldValues = {"In/Outboard Powerboat", "26-42 ft", "0-100 hp"};
			getWatercraftData(tdWaterCrafts, numOfWaterCraftIII, listOfWatercraftIIIFieldValues);
		}

		if (numOfWaterCraftIV > 0) {
			String[] listOfWatercraftIVFieldValues = {"Inboard Powerboat", "26-42 ft", "101-200 hp"};
			getWatercraftData(tdWaterCrafts, numOfWaterCraftIV, listOfWatercraftIVFieldValues);
		}

		if (numOfWaterCraftV > 0) {
			String[] listOfWatercraftVFieldValues = {"Houseboat", "26-42 ft", "101-200 hp"};
			getWatercraftData(tdWaterCrafts, numOfWaterCraftV, listOfWatercraftVFieldValues);
		}

		if (numOfWaterCraftVI > 0) {
			for (int i = 0; i < numOfWaterCraftVI; i++) {
				Map<String, Object> waterCraftVI = new HashMap<>();
				waterCraftVI.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.TYPE.getLabel(), "Sailboat");
				waterCraftVI.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				waterCraftVI.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MAKE.getLabel(), "BMW");
				waterCraftVI.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MODEL.getLabel(), "Test Model");
				if (tdWaterCrafts.size() < 1) {
					waterCraftVI.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.ADD_WATERCRAFT.getLabel(), "Yes");
				}
				tdWaterCrafts.add(new SimpleDataProvider(waterCraftVI));
			}
		}

		if (numOfWaterCraftPERS > 0) {
			for (int i = 0; i < numOfWaterCraftPERS; i++) {
				Map<String, Object> waterCraftPERS = new HashMap<>();
				waterCraftPERS.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.TYPE.getLabel(), "Personal Watercraft");
				waterCraftPERS.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				waterCraftPERS.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MAKE.getLabel(), "BMW");
				waterCraftPERS.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.MODEL.getLabel(), "Test Model");
				if (tdWaterCrafts.size() < 1) {
					waterCraftPERS.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.Watercraft.ADD_WATERCRAFT.getLabel(), "Yes");
				}
				tdWaterCrafts.add(new SimpleDataProvider(waterCraftPERS));
			}
		}

		if (numOfAtv > 0) {
			for (int i = 0; i < numOfAtv; i++) {
				Map<String, Object> atv = new HashMap<>();
				atv.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.TYPE.getLabel(), "ATV");
				atv.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				atv.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.MAKE.getLabel(), "BMW");
				atv.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.MODEL.getLabel(), "Test Model");
				atv.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.CURRENT_CARRIER.getLabel(), "regex=.*\\S.*");
				if (tdRecreationVehicles.size() < 1) {
					atv.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.ADD_RECREATIONAL_VEHICLE.getLabel(), "Yes");
				}
				tdRecreationVehicles.add(new SimpleDataProvider(atv));
			}
		}

		if (numOfSnowmobile > 0) {
			for (int i = 0; i < numOfSnowmobile; i++) {
				Map<String, Object> snowmobile = new HashMap<>();
				snowmobile.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.TYPE.getLabel(), "Snowmobile");
				snowmobile.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.YEAR.getLabel(), RandomUtils.nextInt(1999, 2017));
				snowmobile.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.MAKE.getLabel(), "BMW");
				snowmobile.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.MODEL.getLabel(), "Test Snowmobile");
				snowmobile.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.CURRENT_CARRIER.getLabel(), "regex=.*\\S.*");
				if (tdRecreationVehicles.size() < 1) {
					snowmobile.put(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RecreationalVehicle.ADD_RECREATIONAL_VEHICLE.getLabel(), "Yes");
				}
				tdRecreationVehicles.add(new SimpleDataProvider(snowmobile));
			}
		}

		return DataProviderFactory.dataOf(
				PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.WATERCRAFT.getLabel(), tdWaterCrafts,
				PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.RECREATIONAL_VEHICLE.getLabel(), tdRecreationVehicles
		);

	}

	private TestData getPremiumAndCoveragesData(PUPOpenLPolicy openLPolicy) {
		TestData premiumAndCoverageTabData = new SimpleDataProvider();
		if (openLPolicy.getCoverages().get(0).getLimit() != null) {
			premiumAndCoverageTabData.adjust(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA.getLabel(), new Dollar(openLPolicy.getCoverages().get(0).getLimit()).toString().replaceAll("\\.00", ""));
		}
		return premiumAndCoverageTabData;
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
