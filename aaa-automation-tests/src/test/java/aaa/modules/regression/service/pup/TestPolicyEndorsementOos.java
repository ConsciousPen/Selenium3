/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.pup;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Yonggang Sun
 * @name Test OOS Endorsement for Umbrella Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Umbrella (AAA) Policy
 * 3. Create Midterm Endorsement
 * 4. Create OOS Endorsement
 * 5. Verify 'Pended Endorsement' button is disabled
 * 6. Verify Policy status is 'Pending out of sequence completion'
 * 7. Verify Policy Premium is changed
 * @details
 */
public class TestPolicyEndorsementOos extends PersonalUmbrellaBaseTest {
	
	@Parameters({"state"})
	//@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.PUP )
    public void testPolicyEndorsementOos(@Optional("") String state) {
        mainApp().open();
        getCopiedPolicy();
        
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();

        log.info("MidTerm Endorsement for Policy #" + policyNumber);
        TestData endorsement_td = getStateTestData(testDataManager.getDefault(TestPolicyEndorsement.class), "TestData");
        policy.createEndorsement(endorsement_td.adjust(getPolicyTD("Endorsement", "TestData_Plus1Month")));
        Dollar policyPremium2 = PolicySummaryPage.TransactionHistory.getEndingPremium();

        log.info("OOS Endorsement for Policy #" + policyNumber);
        TestData endorsement_td1 = getStateTestData(testDataManager.getDefault(TestPolicyEndorsement.class), "TestData");
        policy.createEndorsement(endorsement_td1.adjust(getPolicyTD("Endorsement", "TestData_Plus10Day")));

        assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);
        assertThat(policyPremium).isNotEqualTo(policyPremium2);
        assertThat(policyPremium).isNotEqualTo(PolicySummaryPage.TransactionHistory.getEndingPremium());
    }
}
