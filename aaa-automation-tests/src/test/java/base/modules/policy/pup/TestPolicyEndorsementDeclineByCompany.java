/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Polina Kaziuchyts
 * @name Test Endorsement decline by company for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (Preconfigured) Policy
 * 3. Create endorsement
 * 4. Decline by company
 * 5. Verify Policy status is 'Company Declined'
 * @details
 */
public class TestPolicyEndorsementDeclineByCompany extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyEndorsementDeclineByCompany() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        log.info("TEST: Decline By Company Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.endorse().perform(tdPolicy.getTestData("Endorsement", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        policy.declineByCompanyQuote().perform(tdPolicy.getTestData("DeclineByCompany", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        PolicySummaryPage.tableEndorsements.getRow(1).getCell(3).verify.value(ProductConstants.PolicyStatus.COMPANY_DECLINED);
    }
}
