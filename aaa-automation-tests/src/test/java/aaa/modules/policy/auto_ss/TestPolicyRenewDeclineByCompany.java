/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test renew decline by company for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create AutoSS Policy
 * 3. Renew Policy
 * 4. Decline by company
 * 5. Verify Policy status is 'Company Declined'
 * @details
 */
public class TestPolicyRenewDeclineByCompany extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.AutoSS")
    public void testPolicyRenewDeclineByCompany() {
        mainApp().open();

        createCustomerIndividual();

        createPolicy();

        log.info("TEST: Decline By Company Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        policy.renew().perform(new SimpleDataProvider());
        PolicySummaryPage.buttonRenewals.click();

        policy.declineByCompanyQuote().perform(getPolicyTD("DeclineByCompany", "TestData_Plus1Year"));
        PolicySummaryPage.buttonRenewals.click();

        PolicySummaryPage.tableRenewals.getRow(1).getCell(4).verify.value(ProductConstants.PolicyStatus.COMPANY_DECLINED);
    }
}
