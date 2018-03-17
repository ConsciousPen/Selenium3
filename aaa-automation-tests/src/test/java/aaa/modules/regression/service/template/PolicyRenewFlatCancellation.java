/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.template;

import static toolkit.verification.CustomAssertions.assertThat;
import toolkit.datax.impl.SimpleDataProvider;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author Xiaolan Ge
 * @name Test renew flat cancellation for Umbrella Policy
 * @scenario 1. Create Customer
 * 2. Create Policy
 * 3. Manual Renew for Policy
 * 4. Cancellation Policy Renewal
 * 5. Verify Policy status is 'Cancellation Pending'
 * @details
 */
public class PolicyRenewFlatCancellation extends PolicyBaseTest {
	public void testPolicyRenewFlatCancellation() {
		mainApp().open();

		createCustomerIndividual();
		createPolicy(getBackDatedPolicyTD());
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		log.info("Manual Renew for Policy #" + policyNumber);
		if (getPolicyType().equals(PolicyType.AUTO_SS) || getPolicyType().equals(PolicyType.AUTO_CA_SELECT) || getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
			policy.renew().perform(new SimpleDataProvider());
		} else {
			policy.renew().performAndExit(new SimpleDataProvider());
		}
		log.info("TEST: Cancellation Policy Renewal #" + policyNumber);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData_Plus3Days"));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.CANCELLATION_PENDING);
		PolicySummaryPage.buttonTransactionHistory.click();
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2)).hasValue(ProductConstants.TransactionHistoryType.CANCELLATION);
		assertThat(PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(2)).hasValue(ProductConstants.TransactionHistoryType.ISSUE);
	}
}
