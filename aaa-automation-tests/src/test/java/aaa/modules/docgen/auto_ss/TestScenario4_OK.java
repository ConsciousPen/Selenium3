package aaa.modules.docgen.auto_ss;

import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

import java.time.LocalDateTime;

import static aaa.main.enums.DocGenEnum.Documents.*;

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
	@Parameters({ "state" })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	public void TC01_EndorsementOne(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		log.info("Policy Effective date" + policyEffectiveDate);
		log.info("Make first endorsement for Policy #" + policyNumber);

		TestData tdEndorsement = getTestSpecificTD("TestData_EndorsementOne");
		policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		String termEffDt = DocGenHelper.convertToZonedDateTime(policyEffectiveDate);

		// verify the xml file AASR22
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR22).verify.mapping(getTestSpecificTD("TestData_Verification1")
				.adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AASR22", "form", "TermEffDt", "DateTimeField"), termEffDt), policyNumber);
		// verify the xml file AA41XX and AA10OK
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AA41XX, AA10OK).verify.mapping(getTestSpecificTD("TestData_Verification2")
				.adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA41XX", "form", "TermEffDt", "DateTimeField"), termEffDt)
				.adjust(TestData.makeKeyPath("AA10OK", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA10OK", "form", "TermEffDt", "DateTimeField"), termEffDt), policyNumber);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
}
