/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import org.testng.Assert;
import aaa.common.Constants;
import aaa.helpers.EntitiesHolder;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.cea.CeaPolicy;
import aaa.main.modules.policy.cea.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.TestData;

public class CaliforniaEarthquakeBaseTest extends PolicyBaseTest {

	public CaliforniaEarthquakeBaseTest() {
		setState(Constants.States.CA.get());
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.CEA;
	}

	/**
	 * Should be used for PUP policy creation. If you need to create PUP
	 * product, it is suggested to login, create/open customer first, then use
	 * this method to get policy num.
	 * 
	 */
	protected synchronized String getPrimaryHO3Policy() {
		String returnValue = new String();
		PolicyType type = PolicyType.HOME_CA_HO3;

		/*
		 * String key = EntitiesHolder.makeDefaultPolicyKey(type, getState());
		 * // EntitiesHolder.addNewEntiry(key, "CAH3927277087"); if
		 * (EntitiesHolder.isEntityPresent(key)) { //
		 * SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, //
		 * EntitiesHolder.getEntity(key)); //
		 * type.get().copyPolicy(tdPolicy.getTestData("CopyFromPolicy", //
		 * "TestData")); returnValue = EntitiesHolder.getEntity(key);
		 * 
		 * } else {
		 */
		type.get().createPolicy(testDataManager.policy.get(type).getTestData("DataGather", "TestData"));
		returnValue = PolicySummaryPage.labelPolicyNumber.getValue();
		// EntitiesHolder.addNewEntiry(key, returnValue);

		// }
		return returnValue;
	}

	/**
	 * Fill tabs of associated workspace with provided data and submit policy.
	 * Method returns policy number
	 *
	 * @param td
	 *            - appropriate TestData to the policy creation
	 */
	@Override
	protected String createPolicy(TestData td) {
		Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		createCustomerIndividual();
		td = adjustHO3PrimaryPolicy(td, getPrimaryHO3Policy());
		log.info("Policy Creation Started...");
		getPolicyType().get().createPolicy(td);
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		EntitiesHolder.addNewEntity(EntitiesHolder.makePolicyKey(getPolicyType(), getState()), policyNumber);
		return policyNumber;
	}

	protected TestData adjustHO3PrimaryPolicy(TestData td, String primaryHO3Policy) {
		GeneralTab generaltab = new GeneralTab();
		return generaltab.adjustWithRealPolicy(td, primaryHO3Policy);
	}

	/**
	 * Create policy with with adjustment Adjustment_CEA from provided testdata
	 * Method returns policy number
	 * 
	 * @param td
	 *            - appropriate TestData to the policy creation
	 */
	protected String createPolicyFromCa(TestData td) {
		Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		createCustomerIndividual();
		log.info("Policy Creation Started...");
		// CA policy creation
		PolicyType type = PolicyType.HOME_CA_HO3;
		TestData tdPolicy = testDataManager.policy.get(type);
		type.get().initiate();
		type.get().getDefaultView().fill(tdPolicy.getTestData("DataGather", "TestData").adjust(tdPolicy.getTestData("DataGather", "Adjustment_CEA")));
		// CEA policy creation
		((CeaPolicy) getPolicyType().get()).createPolicyFromCa(td);
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		EntitiesHolder.addNewEntity(EntitiesHolder.makePolicyKey(getPolicyType(), getState()), policyNumber);
		return policyNumber;
	}

	/**
	 * Create policy with with adjustment Adjustment_CEA Method returns policy
	 * number
	 * 
	 * @param td
	 *            - appropriate TestData to the policy creation
	 */
	protected String createPolicyFromCa() {
		TestData tdPolicy = testDataManager.policy.get(getPolicyType());
		return createPolicyFromCa(tdPolicy.getTestData("DataGather", "TestData").adjust(tdPolicy.getTestData("DataGather", "Adjustment_From_CA")));
	}
}
