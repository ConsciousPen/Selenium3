/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.ChangeBrokerActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.StaticElement;

/**
 * @author Andrey Shashenka
 * @name Test Change Broker Intra Agency Renewal for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Change Broker Intra Agency for Policy
 * 4. Verify Broker name was not change
 * 5. Manual Renew for Policy
 * 6. Verify Broker name was change
 * @details
 */
public class TestPolicyChangeBrokerIntraAgencyRenewal extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyChangeBrokerIntraAgencyRenewal() {
        StaticElement labelAgent = new StaticElement(By.id("policyDataGatherForm:policyProducerCd"));

        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        log.info("TEST: Change Broker Inter Agency for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.changeBrokerRequest().perform(tdPolicy.getTestData("ChangeBroker", "TestData"));

        String newBrokerName = tdPolicy.getTestData("ChangeBroker", "TestData").getValue(
                ChangeBrokerActionTab.class.getSimpleName(),
                ChangeBrokerActionTab.NEW_BROKER.getLabel(),
                ChangeBrokerActionTab.ChangeLocationMetaData.AGENCY_NAME.getLabel());

        policy.policyInquiry().start();
        labelAgent.verify.value(newBrokerName);
        Tab.buttonTopCancel.click();

        log.info("Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.createRenewal(tdPolicy.getTestData("Issue", "TestData_ExistentBillingAccount"));

        policy.policyInquiry().start();
        labelAgent.verify.value(newBrokerName);
        Tab.buttonTopCancel.click();

        PolicySummaryPage.linkPolicy.click();
        policy.policyInquiry().start();
        labelAgent.verify.value(newBrokerName);
    }
}
