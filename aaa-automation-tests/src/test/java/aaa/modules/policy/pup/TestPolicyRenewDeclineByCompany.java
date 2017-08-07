/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.pup;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test renew decline by company for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Personal Umbrella Policy 
 * 3. Renew Policy
 * 4. Decline by company
 * 5. Verify Policy status is 'Company Declined'
 * @details
 */
public class TestPolicyRenewDeclineByCompany extends PersonalUmbrellaBaseTest {

    @Test
    @TestInfo(component = "Policy.PUP")
    public void testPolicyRenewDeclineByCompany() {
        mainApp().open();

        getCopiedPolicy();

        log.info("TEST: Decline By Company Renew for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        policy.renew().performAndExit(new SimpleDataProvider());
        
        
        PolicySummaryPage.buttonRenewals.click();

        policy.declineByCompanyQuote().perform(getPolicyTD("DeclineByCompany", "TestData_Plus1Year"));
        PolicySummaryPage.buttonRenewals.click();

        PolicySummaryPage.tableRenewals.getRow(1).getCell(4).verify.value(ProductConstants.PolicyStatus.COMPANY_DECLINED);
    }
}
