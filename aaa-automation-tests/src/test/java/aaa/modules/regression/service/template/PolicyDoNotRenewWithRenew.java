/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.template;


import java.time.LocalDateTime;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.pages.SearchPage;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author Lina Li
 * @name Test Policy do not renewal with Renewal
 * @scenario
 * 1. Find customer or create new if customer does not exist;
 * 2. Create new Policy;
 * 3. Mark do not renewal flag on policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Verify 'Do Not Renew' flag is displayed in the policy overview header
 * 6. Change time to renewal image generation date, run jobs renewalOfferGenerationPart1 and renewalOfferGenerationPart2
 * 7. Verify 'Renewals' button is not displayed in the policy overview header
 * @details
 */

public abstract class PolicyDoNotRenewWithRenew extends PolicyBaseTest {
	protected String policyNumber;
	protected LocalDateTime policyExpirationDate;

	protected void TC01_CreatePolicyAddDoNotRenewTemplate() {
		mainApp().open();
		getCopiedPolicy();
		policyNumber=PolicySummaryPage.labelPolicyNumber.getValue();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		log.info("Do Not Renew for Policy #" + policyNumber);
		
		policy.doNotRenew().perform(getPolicyTD("DoNotRenew", "TestData"));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.labelDoNotRenew.verify.present();
	}
	
	protected void TC02_RenewPolicyTemplate(){
		log.info("TEST: Renew Policy #" + policyNumber);
		LocalDateTime renewDate=getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.verify.enabled(false);
	}
}
