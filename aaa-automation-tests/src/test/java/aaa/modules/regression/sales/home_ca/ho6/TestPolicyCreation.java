/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ca.ho6;

import static org.assertj.core.api.Assertions.assertThat;

import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.PolicyType;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO6BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;



/**
 * @author Automation team
 * @name Test Create Home Quote
 * @scenario
 * 1. Create Customer
 * 2. Create Home HO6 policy
 * 3. Verify policy status is 'Policy Active'
 * @details
 */
public class TestPolicyCreation extends HomeCaHO6BaseTest {

	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO6) 
    public void testPolicyCreation(@Optional("CA") String state) {
        mainApp().open();

        createCustomerIndividual();
        policy.createPolicy(getPolicyTD("DataGather", "TestData"));
        SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.CUSTOMER, "700032268");

        createCustomerIndividual();
        PolicyType.HOME_CA_HO3.get().createPolicy(getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO3), "DataGather", "TestData"));
        createCustomerIndividual();
        PolicyType.HOME_CA_HO4.get().createPolicy(getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_HO4), "DataGather", "TestData"));
        createCustomerIndividual();
        PolicyType.HOME_CA_DP3.get().createPolicy(getStateTestData(testDataManager.policy.get(PolicyType.HOME_CA_DP3), "DataGather", "TestData"));

		assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);

    }
}
