/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test Delete Pending Endorsement for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Personal Umbrella Policy
 * 3. Create endorsement
 * 4. Delete Pended Transaction for Policy
 * 5. Verify 'Pended Endorsement' button is disabled
 * @details
 */
public class TestPolicyEndorsementDeletePending extends PersonalUmbrellaBaseTest {

	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
    public void testPolicyEndorsementDeletePending() {
        mainApp().open();

        getCopiedPolicy();

        log.info("TEST: Delete Pending Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
       

        policy.endorse().performAndExit(getPolicyTD("Endorsement", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        policy.deletePendedTransaction().perform(new SimpleDataProvider());
        PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
    }
}
