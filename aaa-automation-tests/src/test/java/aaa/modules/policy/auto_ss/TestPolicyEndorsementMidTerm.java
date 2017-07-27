/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ss;

import org.testng.annotations.Test;

import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTime;

/**
 * @author Jelena Dembovska
 * @name Test Midterm Cancellation 
 * @scenario
 * 1. Create customer
 * 2. Create backdated policy
 * 3. Endorse policy with current date
 * @details
 */
public class TestPolicyEndorsementMidTerm extends AutoSSBaseTest {


	@Test
	@TestInfo(component = "Policy.AutoSS")
	public void testPolicyEndorsementMidTerm() {
		
		
		new TestPolicyBackdated().testPolicyBackdated();
		
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		
		//make an endorsement using test data from TestPolicyEndorsementAdd test
		//adjust reports tab - no need to check "Customer Agreement" for midterm endorsement
    	TestData endorsement_td = getStateTestData(tdPolicy, TestPolicyEndorsementAdd.class.getSimpleName(), "TestData");
        policy.createEndorsement(endorsement_td
                .mask(TestData.makeKeyPath(RatingDetailReportsTab.class.getSimpleName(), "Customer Agreement"))
        		.adjust(tdPolicy.getTestData("Endorsement", "TestData")));
		

		
        String message = String.format("Bind Endorsement effective %1$s for Policy %2$s", new DateTime(DateTime.MM_DD_YYYY), policyNumber);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, message);
        
	}
}
