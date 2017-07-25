/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package  base.modules.policy.home_ss;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import aaa.main.metadata.policy.AutoSSMetaData.ChangeBrokerActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.StaticElement;

/**
 * @author Viachaslau Markouski
 * @name Test Change Broker Intra Agency for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Change Broker Intra Agency for Home Policy
 * 4. Verify Insurance Agent name was changed
 * @details
 */
public class TestPolicyChangeBrokerIntraAgencyOther extends HomeSSHO3BaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyChangeBrokerIntraAgencyOther() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        log.info("TEST: Change Broker Inter Agency for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.changeBrokerRequest().perform(tdPolicy.getTestData("ChangeBroker", "TestDataIntra_Other"));

        policy.policyInquiry().start();
        new StaticElement(By.id("policyDataGatherForm:sedit_ProducerInfo_policy_subProducerCd")).verify.value(
                tdPolicy.getTestData("ChangeBroker", "TestDataIntra_Other").getValue(
                        ChangeBrokerActionTab.class.getSimpleName(),
                        ChangeBrokerActionTab.INSURANCE_AGENT.getLabel()));
    }
}
