/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.templates;

import org.apache.commons.lang.StringUtils;

import toolkit.verification.CustomAssert;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author Yonggang Sun
 * @name Template Rewrite to new number for Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Home (AAA) Policy
 * 3. Cancel Policy
 * 4. Rewrite Policy
 * 5. Verify Policy status is 'Policy Active'
 * 6. Verify Policy number different the cancelled policy number
 * @details
 */
public class PolicyRewriteToNewNumber extends PolicyBaseTest {

	private String fileName;
	private String tdName;
	
	protected void testPolicyRewriteToNewNumber() {
        mainApp().open();

        getCopiedPolicy();

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));

        log.info("Cancelled Policy #" + policyNumber);

        log.info("TEST: Rewrite Policy #" + policyNumber);
        policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataNewNumber"));
        this.calculateAndPurchaseRewritePolicy();

        String rewrittenPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();

        log.info("Rewritten Policy #" + rewrittenPolicyNumber);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        CustomAssert.assertFalse(String.format("Rewritten Policy Number #%s is the same as Initial Policy Number #%s",
                policyNumber, rewrittenPolicyNumber), policyNumber.equals(rewrittenPolicyNumber));
    }
	
	protected void calculateAndPurchaseRewritePolicy() {
		if(StringUtils.isEmpty(fileName) && StringUtils.isEmpty(tdName)){
			policy.calculatePremiumAndPurchase(getPolicyTD("DataGather", "TestData"));
			return;
		}
		policy.calculatePremiumAndPurchase(getPolicyTD(fileName, tdName));
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setTdName(String tdName) {
		this.tdName = tdName;
	}
	
	
}
