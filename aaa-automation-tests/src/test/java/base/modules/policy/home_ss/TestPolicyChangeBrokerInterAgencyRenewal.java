/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.home_ss;

import org.testng.annotations.Test;

import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData.ChangeBrokerActionTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Viachaslau Markouski
 * @name Test Change Broker Inter Agency Renewal for Home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Change Broker Inter Agency for Home Policy
 * 4. Verify Broker name was not changed
 * 5. Manual Renew Home Policy
 * 6. Verify Broker name was changed
 * @details
 */
public class TestPolicyChangeBrokerInterAgencyRenewal extends HomeSSHO3BaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyChangeBrokerInterAgencyRenewal() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        String initialBroker = PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.BROKERAGE).getValue();

        log.info("TEST: Change Broker Inter Agency for Policy #" + policyNumber);
        policy.changeBrokerRequest().perform(getPolicyTD("ChangeBroker", "TestDataInter_Renewal"));

        String newBrokerName = getPolicyTD("ChangeBroker", "TestDataInter_Renewal").getValue(
                ChangeBrokerActionTab.class.getSimpleName(),
                ChangeBrokerActionTab.LOCATION_NAME.getLabel(),
                ChangeBrokerActionTab.ChangeLocationMetaData.AGENCY_NAME.getLabel());

        CustomAssert.assertFalse(String.format("Broker name was changed from %s to %s", newBrokerName, initialBroker), initialBroker.equals(newBrokerName));

        log.info("Manual Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.createRenewal(getPolicyTD("Issue", "TestData_ExistentBillingAccount"));

        PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.BROKERAGE).verify.value(newBrokerName);

        PolicySummaryPage.linkPolicy.click();
        PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyConstants.PolicyGeneralInformationTable.BROKERAGE).verify.value(initialBroker);
    }
}
