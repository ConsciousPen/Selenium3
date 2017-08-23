/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.template;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author Yonggang Sun
 * @name Test Endorsement decline by customer for AAA Policy
 * @scenario
 * 1. Create Customer
 * 2. Create (AAA) Policy
 * 3. Create endorsement
 * 4. Decline by customer
 * 5. Verify Policy status is 'Customer Declined' in endorsement
 * @details
 */
public class PolicyEndorsementDeclineByCustomer extends PolicyBaseTest {

    public void testPolicyEndorsementDeclineByCustomer() {
        mainApp().open();

        getCopiedPolicy();

        log.info("TEST: Decline By Customer Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        policy.endorse().performAndExit(getPolicyTD("Endorsement", "TestData"));
        
        PolicySummaryPage.buttonPendedEndorsement.click();

        policy.declineByCustomerQuote().perform(getPolicyTD("DeclineByCustomer", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        PolicySummaryPage.tableEndorsements.getRow(1).getCell("Status").verify.value(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);
    }
}
