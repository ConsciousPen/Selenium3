/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.Assert;

import aaa.common.Constants.States;
import aaa.helpers.EntitiesHolder;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.TestData;

public class PersonalUmbrellaBaseTest extends PolicyBaseTest {

	public PersonalUmbrellaBaseTest() {
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	/**
	 * Should be used for PUP policy creation. If you need to create PUP
	 * product, it is suggested to login, create/open customer first, then use
	 * this method to get policy num.
	 * 
	 */
	protected Map<String, String> getPrimaryPolicies() {
		// EntitiesHolder.addNewEntiry(EntitiesHolder.makeDefaultHo3PolicyKey(PersonalLinesType.HOME_SS,
		// getState()), "AZH3927277286");
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
				type.get().createPolicy(getStateTestData(testDataManager.policy.get(type), "DataGather", "TestData"));
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
			return returnValue;
		}
	}

	protected TestData adjustWithRealPolicies(TestData td, Map<String, String> policies) {
		PrefillTab prefillTab = new PrefillTab();
		return prefillTab.adjustWithRealPolicies(td, policies);
	}

	/**
	 * Fill tabs of associated workspace with provided data and submit policy.
	 * Override if action is performed in non-standard manner.
	 * 
	 * @param td
	 *            - appropriate TestData to the policy creation
	 *
	 */
	@Override
	protected String createPolicy(TestData td) {
		Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		td = adjustWithRealPolicies(td, getPrimaryPolicies());
		log.info("Policy Creation Started...");
		createCustomerIndividual();
		getPolicyType().get().createPolicy(td);
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		EntitiesHolder.addNewEntity(EntitiesHolder.makePolicyKey(getPolicyType(), getState()), policyNumber);
		return policyNumber;
	}
}
