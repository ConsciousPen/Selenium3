/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ss;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import aaa.main.metadata.policy.AutoSSMetaData.ChangeBrokerActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.StaticElement;

/**
 * @author Viachaslau Markouski
 * @name Test Change Broker Intra Agency Renewal for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Change Broker Intra Agency for Home Policy
 * 4. Verify Broker name was not change
 * 5. Manual Renew for Policy
 * 6. Verify Broker name was change
 * @details
 */
public class TestPolicyChangeBrokerIntraAgencyRenewal extends HomeSSHO3BaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyChangeBrokerIntraAgencyRenewal() {
        StaticElement labelAgent = new StaticElement(By.id("policyDataGatherForm:sedit_ProducerInfo_policy_subProducerCd"));

        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        policy.policyInquiry().start();
        String initialAgent = labelAgent.getValue();
        GeneralTab.buttonTopCancel.click();

        log.info("TEST: Change Broker Inter Agency for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.changeBrokerRequest().perform(getPolicyTD("ChangeBroker", "TestDataIntra_Renewal"));

        policy.policyInquiry().start();
        labelAgent.verify.value(initialAgent);
        GeneralTab.buttonTopCancel.click();

        log.info("Manual Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.createRenewal(getPolicyTD("Issue", "TestData_ExistentBillingAccount"));

        policy.policyInquiry().start();
        labelAgent.verify.value(getPolicyTD("ChangeBroker", "TestDataIntra_Renewal").getValue(
                ChangeBrokerActionTab.class.getSimpleName(),
                ChangeBrokerActionTab.INSURANCE_AGENT.getLabel()));
        GeneralTab.buttonTopCancel.click();

        PolicySummaryPage.linkPolicy.click();
        policy.policyInquiry().start();
        labelAgent.verify.value(initialAgent);
    }
}
