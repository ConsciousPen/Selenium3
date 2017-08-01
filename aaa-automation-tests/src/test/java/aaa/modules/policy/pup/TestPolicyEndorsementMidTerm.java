/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.pup;

import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.common.Constants.States;
import aaa.helpers.EntitiesHolder;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Lina Li
 * @name Test Midterm Endorsement for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create UPersonal Umbrella Policy 
 * 3. Create endorsement
 * 4. Verify 'Pended Endorsement' button is disabled
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Ending Premium is changed
 * @details
 */
public class TestPolicyEndorsementMidTerm extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PUP.EndorsementMidTerm")
    public void testPolicyEndorsementMidTerm() {
        mainApp().open();
        
        createPolicy(getStateTestData(tdPolicy, "DataGather", "TestData")
        		.adjust("GeneralTab|PolicyInfo|Effective date", "/today-2d:MM/dd/yyyy"));

        
        Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

        log.info("TEST: MidTerm Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        
        TestData endorsement_td = getStateTestData(tdPolicy, this.getClass().getSimpleName(), "TestData");
	    policy.createEndorsement(endorsement_td.adjust(tdPolicy.getTestData("Endorsement", "TestData")));
	     

        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        CustomAssert.assertFalse(policyPremium.equals(PolicySummaryPage.TransactionHistory.getEndingPremium()));
    }
    
	/**
	 * Should be used for PUP policy creation. If you need to create PUP
	 * product, it is suggested to login, create/open customer first, then use
	 * this method to get policy num.
	 * 
	 */
    @Override
	protected Map<String, String> getPrimaryPolicies() {
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
		td = td.adjust("GeneralTab|Effective date", "/today-2d:MM/dd/yyyy")
				.adjust("GeneralTab|Property insurance base date with CSAA IG", "/today-2d:MM/dd/yyyy");
		return td;
	}
}
