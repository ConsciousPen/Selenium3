package aaa.modules.regression.sales.home_ca;

import aaa.helpers.TestDataHelper;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import toolkit.datax.TestData;

public class HOCATestDataProvider {

	public static TestData getPas550TestData(TestData sourceTestData) {
		TestData defaultTestData = sourceTestData.resolveLinks();
		return TestDataHelper.adjustTD(defaultTestData,
				ApplicantTab.class,
				HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(),
				HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(),
				"3111111111111121").resolveLinks();
	}
}