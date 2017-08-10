/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.template;


import toolkit.datax.TestData;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;

/**
 * @author Yonggang Sun
 * @name Test Endorsement decline by company for home Policy
 * @scenario
 * 1. Create Customer
 * 2. Create (AAA) Policy
 * 3. Create endorsement
 * 4. Decline by company
 * 5. Verify Policy status is 'Company Declined'
 * @details
 */
public class PolicyEndorsementDeclineByCompany extends PolicyBaseTest {
	
	public void testPolicyEndorsementDeclineByCompany() {
		mainApp().open();

        getCopiedPolicy();

        log.info("TEST: Decline By Company Endorsement for Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        if(PolicyType.HOME_SS_HO3.equals(getPolicyType())){
        	TestData endorsement_td = getPolicyTD("TestPolicyEndorsementAdd", "TestData");
        	policy.endorse().performAndExit(endorsement_td.adjust(getPolicyTD("Endorsement", "TestData")));
        }else{
        	policy.endorse().performAndExit(getPolicyTD("Endorsement", "TestData"));
        }
        
//        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        policy.declineByCompanyQuote().perform(getPolicyTD("DeclineByCompany", "TestData"));
        PolicySummaryPage.buttonPendedEndorsement.click();

        PolicySummaryPage.tableEndorsements.getRow(1).getCell(3).verify.value(ProductConstants.PolicyStatus.COMPANY_DECLINED);
	}
}
