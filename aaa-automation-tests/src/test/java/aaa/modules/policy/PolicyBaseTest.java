/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import aaa.common.enums.Constants;
import aaa.main.metadata.policy.*;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.modules.BaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;

public abstract class PolicyBaseTest extends BaseTest {

	protected IPolicy policy;
	private TestData tdPolicy;

	public PolicyBaseTest() {
		PolicyType type = getPolicyType();
		if (type != null) {
			policy = type.get();
		}
		tdPolicy = testDataManager.policy.get(type);
	}

	protected TestData getPolicyTD() {
		return getPolicyTD("DataGather", "TestData");
	}

	protected TestData getPolicyTD(String fileName, String tdName) {
		return getStateTestData(tdPolicy, fileName, tdName);
	}

	protected TestData getBackDatedPolicyTD() {
		return getBackDatedPolicyTD(getPolicyType(), DateTimeUtils.getCurrentDateTime().minusDays(2).format(DateTimeUtils.MM_DD_YYYY));
	}


	protected TestData getBackDatedPolicyTD(String date) {
		return getBackDatedPolicyTD(getPolicyType(), date);
	}


	protected TestData getBackDatedPolicyTD(PolicyType type) {
		return getBackDatedPolicyTD(type, DateTimeUtils.getCurrentDateTime().minusDays(2).format(DateTimeUtils.MM_DD_YYYY));
	}


	protected TestData getBackDatedPolicyTD(PolicyType type, String date) {
		TestData returnValue = getPolicyTD();
		switch (type.getName()) {
			case "Personal Umbrella Policy":
				String pupKey = TestData.makeKeyPath(PersonalUmbrellaMetaData.GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(), PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel());
				TestData tdPup = returnValue.resolveLinks().adjust(pupKey, date);
				//String pupAutoKey = TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel());
				TestData tdPupAuto = DataProviderFactory.emptyData();
				if (getState().equals(Constants.States.CA)) {
					String pupHomeCaKey = TestData.makeKeyPath(HomeCaMetaData.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel());
					String pupHomeCaDateKey = TestData.makeKeyPath(HomeCaMetaData.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel());
					TestData tdPupHomeCa = getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO3), "DataGather", "TestData").resolveLinks().adjust(pupHomeCaDateKey, date).adjust(pupHomeCaKey, date);
					//tdPupAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_CA_SELECT), "DataGather", "TestData").resolveLinks().adjust(pupAutoKey, date);
					tdPup = new PrefillTab().adjustWithRealPolicies(tdPup, getPrimaryPoliciesForPup(tdPupHomeCa, tdPupAuto));
				} else {
					String pupHomeKey = TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel());
					String pupHomeDateKey = TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel());
					TestData tdPupHome = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", "TestData").resolveLinks().adjust(pupHomeDateKey, date).adjust(pupHomeKey, date);
					tdPup = new PrefillTab().adjustWithRealPolicies(tdPup, getPrimaryPoliciesForPup(tdPupHome, tdPupAuto));
				}
				return tdPup.resolveLinks().adjust(pupKey, date);

			case "Homeowners Signature Series":
				String homeKey = TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel());
				String propertyDateKey = TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel());
				return returnValue.resolveLinks().adjust(homeKey, date).adjust(propertyDateKey, date);

			case "California Homeowners":
				String homeCaDateKey = TestData.makeKeyPath(HomeCaMetaData.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel());
				String baseDateKey = TestData.makeKeyPath(HomeCaMetaData.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel());
				return returnValue.resolveLinks().adjust(homeCaDateKey, date).adjust(baseDateKey, date);

			case "California Auto":
				String autoCaKey = TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel());
				return getPolicyTD().resolveLinks().adjust(autoCaKey, date);

			case "Auto Signature Series":
				String autoKey = TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel());
				return returnValue.resolveLinks().adjust(autoKey, date);

			default:
				return returnValue;
		}
	}

	/**
	 * Sets the DONOTRENEWIND value in DB to "1" and exempt it from being renewed.
	 * Can be used when renewal jobs running in parallel are creating a renewal for a policy that should not be there for another test.
	 * NOTE:  You must use this in conjunction with policy.removeDoNotRenew() if/when you wish to create a renewal.
	 * @param policyNumber String value representing the policy number
	 */
	protected void setDoNotRenewFlag(String policyNumber) {
		setDoNotRenewFlag(policyNumber, "1");
	}

	/**
	 * See description above.
	 * @param policyNumber String value representing the policy number
	 * @param flagValue value you wish to set the DONOTRENEWIND value in DB to.  Possible values include 1, 0, null
	 *
	 */
	protected void setDoNotRenewFlag(String policyNumber, String flagValue) {
		DBService.get().executeUpdate("update POLICYSUMMARY set DONOTRENEWIND = " + flagValue + " where policyNumber = '" + policyNumber + "'");
	}
}
