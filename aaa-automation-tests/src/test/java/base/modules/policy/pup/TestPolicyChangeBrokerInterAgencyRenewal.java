/* Copyright Â© 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.ChangeBrokerActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Andrey Shashenka
 * @name Test Change Broker Inter Agency Renewal for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (Preconfigured) Policy
 * 3. Change Broker Inter Agency for Policy
 * 4. Verify Agency name was not changed
 * 5. Manual Renew Umbrella Policy
 * 6. Verify Agency name was changed
 * @details
 */
public class TestPolicyChangeBrokerInterAgencyRenewal extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyChangeBrokerInterAgencyRenewal() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String initialBroker = PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.BROKERAGE).getValue();

        log.info("TEST: Change Broker Inter Agency for Policy #" + policyNumber);
        policy.changeBrokerRequest().perform(tdPolicy.getTestData("ChangeBroker", "TestData"));

        String newBrokerName = tdPolicy.getTestData("ChangeBroker", "TestData").getValue(
                ChangeBrokerActionTab.class.getSimpleName(),
                ChangeBrokerActionTab.NEW_BROKER.getLabel(),
                ChangeBrokerActionTab.ChangeLocationMetaData.AGENCY_NAME.getLabel());

        CustomAssert.assertFalse(String.format("Broker name was changed from %s to %s", newBrokerName, initialBroker), initialBroker.equals(newBrokerName));

        log.info("Manual Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.createRenewal(tdPolicy.getTestData("Issue", "TestData_ExistentBillingAccount"));

        PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.BROKERAGE).verify.value(newBrokerName);

        PolicySummaryPage.linkPolicy.click();
        PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.BROKERAGE).verify.value(newBrokerName);
    }
}
