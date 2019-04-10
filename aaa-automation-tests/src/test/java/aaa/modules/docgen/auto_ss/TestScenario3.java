package aaa.modules.docgen.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.ETCSCoreSoftAssertions;

/**
 * @author Ryan Yu
 * @name Test the form AA41XX
 * @scenario
 * 1. Create Customer
 * 2. Create Auto policy and the policy type =NANO
 * 3. Calculate premium and bind policy
 * 4. Verify the form AA41XX was generated
 * @details
 */
public class TestScenario3 extends AutoSSBaseTest {
	private String policyNumber;

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.IN, States.OK, States.PA})
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testDocGenScenario03(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(),getPolicyType(), false);

			mainApp().open();
			createCustomerIndividual();
			policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
			assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			log.info(getState() + " Policy AutoSS is created: " + policyNumber);
			TestData tdVerification = getTestSpecificTD("TestData_Verification");
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			switch (getState()) {
				case "AZ":
					tdVerification.adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum", "TextField"), policyNumber)
							.adjust(TestData.makeKeyPath("AARFIXX", "form", "PlcyNum", "TextField"), policyNumber);
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, DocGenEnum.Documents.AA41XX, DocGenEnum.Documents.AARFIXX).verify.mapping(tdVerification, policyNumber, softly);
					break;
				case "IN":
				case "OK":
					tdVerification.adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum", "TextField"), policyNumber);
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, DocGenEnum.Documents.AA41XX).verify.mapping(tdVerification, policyNumber, softly);
					break;
				case "PA":
					tdVerification.adjust(TestData.makeKeyPath("AA41PA", "form", "PlcyNum", "TextField"), policyNumber)
							.adjust(TestData.makeKeyPath("AA52UPAB", "form", "PlcyNum", "TextField"), policyNumber)
							.adjust(TestData.makeKeyPath("AA52IPAB", "form", "PlcyNum", "TextField"), policyNumber);
					DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, DocGenEnum.Documents.AA41PA, DocGenEnum.Documents.AA52UPAB, DocGenEnum.Documents.AA52IPAB).verify
							.mapping(tdVerification, policyNumber, softly);
					checkEndorseDocGen(softly);
					break;
			}
		softly.close();
	}

	private void checkEndorseDocGen(ETCSCoreSoftAssertions softly) {
		TestData tdVerification = getTestSpecificTD("TestData_EndorseVerification");
		tdVerification.adjust(TestData.makeKeyPath("AA52UPAC", "form", "PlcyNum", "TextField"), policyNumber).adjust(TestData.makeKeyPath("AA52IPAC", "form", "PlcyNum", "TextField"), policyNumber);
		policy.endorse().performAndFill(getTestSpecificTD("Endorsement").adjust(getPolicyTD("Endorsement", "TestData")));
		DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, DocGenEnum.Documents.AA52UPAC, DocGenEnum.Documents.AA52IPAC).verify.mapping(tdVerification, policyNumber, softly);
	}
}

