/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Yonggang Sun
 * @name Test Endorsement decline by customer for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto (AAA) Policy
 * 3. Create endorsement
 * 4. Decline by customer
 * 5. Verify Policy status is 'Customer Declined' in endorsement
 * @details
 */
public class TestPolicyEndorsementDeclineByCustomer extends AutoSSBaseTest {

	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testPolicyEndorsementDeclineByCustomer() {
        mainApp().open();

        getCopiedPolicy();

        log.info("TEST: Decline By Customer Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        TestData endorsement_td = getPolicyTD("TestPolicyEndorsementAdd", "TestData");
        policy.endorse().performAndExit(endorsement_td.adjust(getPolicyTD("Endorsement", "TestData")));
        PolicySummaryPage.buttonPendedEndorsement.click();

        policy.declineByCustomerQuote().perform(getPolicyTD("DeclineByCustomer", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        PolicySummaryPage.tableEndorsements.getRow(1).getCell(3).verify.value(ProductConstants.PolicyStatus.CUSTOMER_DECLINED);
    }
}
