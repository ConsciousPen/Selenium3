/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.policy.auto_ss;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Viachaslau Markouski
 * @name Test endorsement decline by company for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto (Preconfigured) Policy
 * 3. Create endorsement
 * 4. Decline by company
 * 5. Verify Policy status is 'Company Declined' in endorsement
 * @details
 */
public class TestPolicyEndorsementDeclineByCompany extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyEndorsementDeclineByCompany() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        log.info("TEST: Decline By Company Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        policy.declineByCompanyQuote().perform(getPolicyTD("DeclineByCompany", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        PolicySummaryPage.tableEndorsements.getRow(1).getCell(3).verify.value(ProductConstants.PolicyStatus.COMPANY_DECLINED);
    }
}
