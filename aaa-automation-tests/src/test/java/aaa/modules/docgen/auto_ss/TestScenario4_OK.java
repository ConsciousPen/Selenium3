package aaa.modules.docgen.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import static aaa.main.enums.DocGenEnum.Documents.*;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;

public class TestScenario4_OK extends AutoSSBaseTest {

	/**
	 * @author Ryan Yu
	 * @name Verify the documents generated during first endorsement
	 * @scenario 1. Create a active policy 
	 * 			 2. Make an endorsement to this policy. It should change the policy so it meets conditions for forms generation:
	AASR22 - ("Financial Responsibility Filling Needed" select Yes in Driver Tab(SR22))
	AA41XX - (Policy Type = Named Non Owner)
	 * 			 3. Calculate premium and bind the policy 
	 * 			 4. Verify the documents generate AASR22, AA41XX, AA10OK
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = States.OK)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void TC01_EndorsementOne(@Optional("") String state) {
		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			String policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
			LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
			log.info("Policy Effective date" + policyEffectiveDate);
			log.info("Make first endorsement for Policy #" + policyNumber);

			TestData tdEndorsement = getTestSpecificTD("TestData_EndorsementOne");
			policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
			assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			String termEffDt = DocGenHelper.convertToZonedDateTime(policyEffectiveDate);

			// verify the xml file AASR22
			/*
			DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, AASR22).verify.mapping(getTestSpecificTD("TestData_Verification1")
					.adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber)
					.adjust(TestData.makeKeyPath("AASR22", "form", "TermEffDt", "DateTimeField"), termEffDt), policyNumber, softly);*/
			// verify the xml file AA41XX and AA10OK
			DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, AA41XX, AA10OK).verify.mapping(getTestSpecificTD("TestData_Verification2")
					.adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum", "TextField"), policyNumber)
					.adjust(TestData.makeKeyPath("AA41XX", "form", "TermEffDt", "DateTimeField"), termEffDt)
					.adjust(TestData.makeKeyPath("AA10OK", "form", "PlcyNum", "TextField"), policyNumber)
					.adjust(TestData.makeKeyPath("AA10OK", "form", "TermEffDt", "DateTimeField"), termEffDt), policyNumber, softly);
		});
	}
}
