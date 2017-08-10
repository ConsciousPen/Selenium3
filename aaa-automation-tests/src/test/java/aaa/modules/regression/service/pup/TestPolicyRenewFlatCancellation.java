/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Test;

import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyBackdated;

/**
 * @author Yonggang Sun
 * @name Test renew flat cancellation for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (AAA) Policy
 * 3. Manual Renew for Policy
 * 4. Cancellation Policy Renewal
 * 5. Verify Policy status is 'Cancellation Pending'
 * @details
 */
public class TestPolicyRenewFlatCancellation extends PersonalUmbrellaBaseTest {

	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
    public void testPolicyRenewFlatCancellation() {
    	new TestPolicyBackdated().testPolicyBackdated();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Manual Renew for Policy #" + policyNumber);
        policy.renew().performAndExit(new SimpleDataProvider());

        log.info("TEST: Cancellation Policy Renewal #" + policyNumber);
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus3Days"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
        PolicySummaryPage.buttonTransactionHistory.click();
        PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).verify.value(ProductConstants.TransactionHistoryType.CANCELLATION);
        PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2).verify.value(ProductConstants.TransactionHistoryType.ISSUE);
    }
}
