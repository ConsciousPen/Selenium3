package aaa.modules.regression.sales.pup;

import java.util.LinkedHashMap;
import java.util.Map;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import org.testng.Assert;
import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.EntitiesHolder;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum.SearchBy;
import aaa.main.enums.SearchEnum.SearchFor;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;


/**
 * @author Yonggang Sun
 * @name Test Create PUP Policy Full
 * @scenario
 * 1. Create new or open existent Customer;
 * 2. Create Umbrella Quote
 * 3. Issue policy;
 * 4. Check Policy status is Active.
 */
public class TestPolicyCreationFull extends PersonalUmbrellaBaseTest {

    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.PUP)
    public void testPolicyCreation() {

        mainApp().open();
        createCustomerIndividual();
        createPupFullPolicy();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
    
    /**
	 * Create Policy using default TestData and adjust ClaimsTab and EndorsementsTab vaules.
	 *
	 * @return policy number
	 */
	private String createPupFullPolicy() {
		Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "TestPupCreatePolicyFull", "TestData");
		td = policy.getDefaultView().getTab(PrefillTab.class).adjustWithRealPolicies(td, this.createPrimaryPoliciesForPup());
		return super.createPolicy(td);
	}
	
	/**
	 * Should be used for PUP policy creation. If you need to create PUP
	 * product, it is suggested to login, create/open customer first, then use
	 * this method to get policy num.
	 */
	private Map<String, String> createPrimaryPoliciesForPup() {
		//EntitiesHolder.addNewEntity(EntitiesHolder.makeDefaultPolicyKey(PolicyType.HOME_SS_HO3,
		//getState()), "COH3927438929");
		if (!NavigationPage.isMainTabSelected(AppMainTabs.CUSTOMER.get())) {
			NavigationPage.toMainTab(AppMainTabs.CUSTOMER.get());
		}
		//remember customer that was created in test
		String customerNum = CustomerSummaryPage.labelCustomerNumber.getValue();
		Map<String, String> returnValue = new LinkedHashMap<String, String>();
		String state = getState().intern();
		synchronized (state) {
			PolicyType type;
			PolicyType typeAuto = null;
			if (state.equals(States.CA)) {
				type = PolicyType.HOME_CA_HO3;
				typeAuto = PolicyType.AUTO_CA_SELECT;
			} else
				type = PolicyType.HOME_SS_HO3;
			String key = EntitiesHolder.makeDefaultPolicyKey(type, state);
			if (EntitiesHolder.isEntityPresent(key))
				returnValue.put("Primary_HO3", EntitiesHolder.getEntity(key));
			else {
				createCustomerIndividual();
				TestData td = getStateTestData(testDataManager.policy.get(type), "DataGather", "TestData");	
				// adjust home test data
				td = td.adjust(getStateTestData(testDataManager.policy.get(PolicyType.PUP), "TestHomeSSPolicyAdjustData", "TestData"));
				type.get().createPolicy(td);
				EntitiesHolder.addNewEntity(key, PolicySummaryPage.labelPolicyNumber.getValue());
				returnValue.put("Primary_HO3", EntitiesHolder.getEntity(key));
			}

			if (typeAuto != null) {
				String keyAuto = EntitiesHolder.makeDefaultPolicyKey(typeAuto, state);
				if (EntitiesHolder.isEntityPresent(keyAuto))
					returnValue.put("Primary_Auto", EntitiesHolder.getEntity(keyAuto));
				else {
					createCustomerIndividual();
					typeAuto.get().createPolicy(getStateTestData(testDataManager.policy.get(typeAuto), "DataGather", "TestData"));
					EntitiesHolder.addNewEntity(keyAuto, PolicySummaryPage.labelPolicyNumber.getValue());
					returnValue.put("Primary_Auto", EntitiesHolder.getEntity(keyAuto));
				}
			}
			//open Customer that was created in test
			if (!NavigationPage.isMainTabSelected(AppMainTabs.CUSTOMER.get())) {
				SearchPage.search(SearchFor.CUSTOMER, SearchBy.CUSTOMER, customerNum);
			}
			return returnValue;
		}
	}
	
}
