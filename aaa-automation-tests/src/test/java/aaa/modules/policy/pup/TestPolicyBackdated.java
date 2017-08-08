/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.pup;

import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.annotations.Test;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.common.enums.Constants.States;
import aaa.helpers.EntitiesHolder;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.GeneralTab.PolicyInfo;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;

/**
 * @author Jelena Dembovska
 * @name Test backdated policy
 * @scenario
 * 1. Create new quote
 * 2. Set Quote effective date < current date
 * 3. Check that appropriate rule about backdated Policy is fired
 * 4. Override rule and bind policy
 * @details
 */
public class TestPolicyBackdated extends PersonalUmbrellaBaseTest {

	@Test
	@TestInfo(component = "Policy.PUP")
	public void testPolicyBackdated() {

		mainApp().open();

		createCustomerIndividual();

		log.info("Policy Creation Started...");

		//adjust default policy data with
		//1. effective date = today minus 10 days
		//2. error tab: "Requested Effective Date not Available" error should be overridden 
		String effDateKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), 
        		PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(), PolicyInfo.EFFECTIVE_DATE.getLabel());
        TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData").adjust(effDateKey, "/today-2d:MM/dd/yyyy");
        tdPolicyCreation = adjustWithRealPolicies(tdPolicyCreation, getPrimaryPoliciesForPup());
        createPolicy(tdPolicyCreation);

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
	
	/**
	 * Should be used for PUP policy creation. If you need to create PUP
	 * product, it is suggested to login, create/open customer first, then use
	 * this method to get policy num.
	 * 
	 */
    @Override
	protected Map<String, String> getPrimaryPoliciesForPup() {
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
				type.get().createPolicy(getPrimaryPolicyStateTestData(testDataManager.policy.get(type), "DataGather", "TestData"));
				EntitiesHolder.addNewEntity(key, PolicySummaryPage.labelPolicyNumber.getValue());
				returnValue.put("Primary_HO3", EntitiesHolder.getEntity(key));
			}

			if (typeAuto != null) {
				String keyAuto = EntitiesHolder.makeDefaultPolicyKey(typeAuto, state);
				if (EntitiesHolder.isEntityPresent(keyAuto))
					returnValue.put("Primary_Auto", EntitiesHolder.getEntity(keyAuto));
				else {
					createCustomerIndividual();
					typeAuto.get().createPolicy(getPrimaryPolicyStateTestData(testDataManager.policy.get(typeAuto), "DataGather", "TestData"));
					EntitiesHolder.addNewEntity(keyAuto, PolicySummaryPage.labelPolicyNumber.getValue());
					returnValue.put("Primary_Auto", EntitiesHolder.getEntity(keyAuto));
				}
			}
			return returnValue;
		}
	}
	
    /**
     * To update GeneralTab - Effective date is today-10d
     * @param td TestData
     * @param fileName String
     * @param tdName String
     * @return TestData
     */
	protected TestData getPrimaryPolicyStateTestData(TestData td, String fileName, String tdName) {
		td = getStateTestData(td,fileName,tdName);
		td = td.adjust("GeneralTab|Effective date", "/today-2d:MM/dd/yyyy")
				.adjust("GeneralTab|Property insurance base date with CSAA IG", "/today-2d:MM/dd/yyyy");
		return td;
	}

}
