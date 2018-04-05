package aaa.helpers.openl.testdata_builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomUtils;
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
import aaa.main.modules.policy.pup.defaulttabs.ClaimsTab;
import aaa.main.modules.policy.pup.defaulttabs.EndorsementsTab;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksOtherVehiclesTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksPropertyTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.DataFormat;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;

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
				new UnderlyingRisksAutoTab().getMetaKey(), getUnderlyingRisksAutoData(),
				new UnderlyingRisksOtherVehiclesTab().getMetaKey(), getUnderlyingRisksOtherVehiclesData(),
				new ClaimsTab().getMetaKey(), getClaimsData(openLPolicy),
				new EndorsementsTab().getMetaKey(), getEndorsementData(),
				new PremiumAndCoveragesQuoteTab().getMetaKey(), getPremiumAndCoveragesData()
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
		TestData dwellingAddressData = DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getDwelling().get(0).getAddress().get(0).getZipCode()
		);
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

		if (Boolean.TRUE.equals(openLPolicy.getBusinessPursuitsInd())) {
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.ADD_BUSINESS_OR_FARMING_COVERAGES.getLabel(), "Yes");
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.ENDORSEMENT.getLabel(), "HS 24 71 - Business Pursuits");
			businessOrFarmingData.adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BusinessOrFarmingCoverage.PROPERTY_POLICY_NUMBER.getLabel(), "regex=.*\\S.*");
		}
		return DataProviderFactory.dataOf(
				PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.BUSINESS_OR_FARMING_COVERAGE.getLabel(), businessOrFarmingData
		);
	}

	private TestData getUnderlyingRisksAutoData() {
		return new DataProviderFactory().applyConfiguration(DataFormat.YAML.name()).get("modules/regression/sales/pup").getTestData("TestPolicyCreationFull", "UnderlyingRisksAutoTab");
	}

	private TestData getUnderlyingRisksOtherVehiclesData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getClaimsData(PUPOpenLPolicy openLPolicy) {
		int numOfViolations = openLPolicy.getNumOfViolations();
		int numOfAccidents = openLPolicy.getNumOfAccidents();
		List<TestData> violationsTestDataList = new ArrayList<>();
		LocalDateTime occurrenceDate = openLPolicy.getEffectiveDate().minusYears(1);
		if (numOfViolations > 0 | numOfAccidents > 0) {
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

	private TestData getPremiumAndCoveragesData() {
		return DataProviderFactory.emptyData();
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
