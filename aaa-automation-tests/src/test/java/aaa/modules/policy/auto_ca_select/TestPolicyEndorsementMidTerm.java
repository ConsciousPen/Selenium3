/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy.auto_ca_select;

import org.testng.annotations.Test;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.modules.policy.auto_ca_select.TestPolicyEndorsementAdd;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;




/**
 * @author Xiaolan Ge
 * @name Test Midterm Endorsement for Auto Policy
 * @scenario
 * 1. Create customer
 * 2. Create backdated policy
 * 3. Endorse policy with current date
 * @details
 */
public class TestPolicyEndorsementMidTerm extends AutoCaSelectBaseTest {

    @Test
    @TestInfo(component = "Policy.AutoCA")
    public void testPolicyEndorsementMidTerm() {
    new TestPolicyBackdated().testPolicyBackdated();
		
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		//make an endorsement using test data from TestPolicyEndorsementAdd test
		//adjust reports tab - no need to check "Customer Agreement" for midterm endorsement
    	
        log.info("TEST: MidTerm Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        
        TestData endorsement_td = getPolicyTD(TestPolicyEndorsementAdd.class.getSimpleName(), "TestData");
        policy.createEndorsement(endorsement_td
                .mask(TestData.makeKeyPath(MembershipTab.class.getSimpleName(), "Customer Agreement"))
        		.adjust(getPolicyTD("Endorsement", "TestData")));
	     

        String message = String.format("Bind Endorsement effective %1$s for Policy %2$s", DateTimeUtils.getCurrentDateTime().format(DateTimeUtils.MM_DD_YYYY), policyNumber);
        NotesAndAlertsSummaryPage.activitiesAndUserNotes.verify.description(1, message);
    }
}
