/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.pup;

import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.common.Constants.States;
import aaa.helpers.EntitiesHolder;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.GeneralTab.PolicyInfo;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Yonggang Sun
 * @name Test renew flat cancellation for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (AAA) Policy
 * 3. Manual Renew for Policy
 * 4. Cancellation Policy Renewal
 * 5. Verify Policy status is 'Cancellation Pending'
 * @details
 */
public class TestPolicyRenewFlatCancellation extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyRenewFlatCancellation() {
        mainApp().open();
        String effDateKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), 
        		PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(), PolicyInfo.EFFECTIVE_DATE.getLabel());
        TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData").adjust(effDateKey, "/today-3d:MM/dd/yyyy");
        tdPolicyCreation = adjustWithRealPolicies(tdPolicyCreation, getPrimaryPoliciesForPup());
        createPolicy(tdPolicyCreation);

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Manual Renew for Policy #" + policyNumber);
        policy.renew().performAndExit(new SimpleDataProvider());

        log.info("TEST: Cancellation Policy Renewal #" + policyNumber);
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus3Days"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
        PolicySummaryPage.buttonTransactionHistory.click();
        PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.CANCELLATION);
        PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ISSUE);
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
			if (state.equals(States.CA.get())) {
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
     * To update GeneralTab - Effective date is today-2d
     * @param td TestData
     * @param fileName String
     * @param tdName String
     * @return TestData
     */
	protected TestData getPrimaryPolicyStateTestData(TestData td, String fileName, String tdName) {
		td = getStateTestData(td,fileName,tdName);
		td = td.adjust("GeneralTab|Effective date", "/today-3d:MM/dd/yyyy")
				.adjust("GeneralTab|Property insurance base date with CSAA IG", "/today-3d:MM/dd/yyyy");
		return td;
	}
}
