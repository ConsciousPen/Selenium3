package aaa.helpers.openl.testdata_builder;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.EntitiesHolder;
import aaa.helpers.TestDataHelper;
import aaa.helpers.TestDataManager;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.PolicyType;
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
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class PUPTestDataGenerator extends TestDataGenerator<PUPOpenLPolicy> {
	public PUPTestDataGenerator(String state) {
		super(state);
	}

	public PUPTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	private final Map<String, String> getPrimaryPoliciesForPup(TestData td) {
		if (!NavigationPage.isMainTabSelected(NavigationEnum.AppMainTabs.CUSTOMER.get())) {
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
		}
		String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();
		Map<String, String> returnValue = new LinkedHashMap<>();
		String state = getState().intern();
		synchronized (state) {
			PolicyType type;
			PolicyType typeAuto = null;
			if (state.equals(Constants.States.CA)) {
				type = PolicyType.HOME_CA_HO3;
				typeAuto = PolicyType.AUTO_CA_SELECT;
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

			if (typeAuto != null) {
				String keyAuto = EntitiesHolder.makeDefaultPolicyKey(typeAuto, state);
				if (EntitiesHolder.isEntityPresent(keyAuto)) {
					returnValue.put("Primary_Auto", EntitiesHolder.getEntity(keyAuto));
				} else {
					typeAuto.get().createPolicy(td);
					EntitiesHolder.addNewEntity(keyAuto, PolicySummaryPage.labelPolicyNumber.getValue());
					returnValue.put("Primary_Auto", EntitiesHolder.getEntity(keyAuto));
				}
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
				new PrefillTab().getMetaKey(), getPrefillTabData(),
				new GeneralTab().getMetaKey(), getGeneralTabData(),
				new UnderlyingRisksPropertyTab().getMetaKey(), getUnderlyingRisksPropertyData(),
				new UnderlyingRisksAutoTab().getMetaKey(), getUnderlyingRisksAutoData(),
				new UnderlyingRisksOtherVehiclesTab().getMetaKey(), getUnderlyingRisksOtherVehiclesData(),
				new ClaimsTab().getMetaKey(), getClaimsData(),
				new EndorsementsTab().getMetaKey(), getEndorsementData(),
				new PremiumAndCoveragesQuoteTab().getMetaKey(), getPremiumAndCoveragesData()
		);
		return TestDataHelper.merge(getRatingDataPattern(), td);
	}

	private TestData getPrefillTabData() {
		TestData tdPUP = new TestDataManager().policy.get(PolicyType.PUP).getTestData("DataGather", "TestData_AZ");
		TestData tdHO3 = new TestDataManager().policy.get(PolicyType.HOME_SS_HO3).getTestData("DataGather", "TestData_AZ");
		TestData preFillTabTd = new PrefillTab().adjustWithRealPolicies(tdPUP, getPrimaryPoliciesForPup(tdHO3));
		return preFillTabTd;
	}

	private TestData getGeneralTabData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getUnderlyingRisksPropertyData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getUnderlyingRisksAutoData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getUnderlyingRisksOtherVehiclesData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getClaimsData() {
		return DataProviderFactory.emptyData();
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
