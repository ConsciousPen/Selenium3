package aaa.modules.docgen.delta.home_ss.dp3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.BatchJob;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.home_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.modules.policy.HomeSSDP3BaseTest;
import aaa.utils.StateList;
import toolkit.verification.ETCSCoreSoftAssertions;

/**
 *
 * @author Ryan Yu
 *
 */
public class TestNJDocgenScenarios extends HomeSSDP3BaseTest {
	private GenerateOnDemandDocumentActionTab documentActionTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
	private String policyNum;

	/**
	 * TC Steps:
	 * 1. Create an DP3 quote and bind it;
	 * (Special conditions for this quote:
	 * For HSTPNJ:
	 * 1.User adds named insured whose age is 62 or Above
	 * 2."Is there any Third Party Designee ? = "Yes"
	 * 3.Add the Third Party Designee mandatory details
	 * (AC2)
	 * For HSFLDNJ,
	 * HSCSND,
	 * HSHU2NJ,
	 * HSELNJ:
	 * No specific conditions, generates on bind.
	 * )
	 * 2. Verify that below forms are generated in xml batch:
	 * HSFLDNJ,
	 * HSCSND,
	 * HSHU2NJ,
	 * HSELNJ
	 * 3. Go to On-Demand Documents tab.
	 * 4. Verify that below forms are absent:
	 * HSFLDNJ,
	 * HSCSND,
	 * HSHU2NJ,
	 * HSELNJ
	 * 5. Select HSTPNJ form and press "Generate" button
	 * 6. Verify that HSTPNJ is generated in xml batch
	 * <p/>
	 * Req:
	 * 17294:US NJ GD- 12 Generate Important Flood Information for New Jersey Policyholders (HSFLDNJ 11 12)
	 * 17263:US NJ GD- 21 Generate Rental Property Insurance HSCSND 11 12 (dp3 only)
	 * 17393:US NJ Capture Third Party Designee
	 * 17403:US NJ GD- 15 Generate New Jersey Third Party Designation by Senior Citizen (HSTP__ 11 12)
	 * 17306:US NJ GD- 09 Generate Information Notice Regarding Extraordinary Life Circumstance (HSELNJ 11 12)
	 * 17404:US NJ GD- 14 Generate New Jersey Policyholder Hurricane Percentage Deductible Consumer Guide (HSHU2NJ 11 12) (HO3,DP3)
	 */
	@Parameters({"state"})
	@StateList(states = States.NJ)
	@Test(groups = {Groups.DOCGEN, Groups.CRITICAL})
	public void testDeltaPolicyDocuments(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policyNum = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_DeltaPolicyDocuments")));
		// TODO No such field "Is there any Third Party Designee ?", so cannot generate HSTPNJ
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
			DocGenHelper.verifyDocumentsGenerated(softly, policyNum, DocGenEnum.Documents.HSFLDNJ, DocGenEnum.Documents.HSCSND, DocGenEnum.Documents.HSHU2NJ, DocGenEnum.Documents.HSELNJ);

			policy.policyDocGen().start();
			documentActionTab.verify.documentsPresent(softly, false,
					DocGenEnum.Documents.HSFLDNJ,
					DocGenEnum.Documents.HSCSND,
					DocGenEnum.Documents.HSHU2NJ,
					DocGenEnum.Documents.HSELNJ
			);
		softly.close();
		documentActionTab.cancel(true);

		/**
		 * Test steps:
		 * 1. Open policy which was created in dp3DeltaPolicyDocuments test;
		 * 2. Select "Cancellation Notice" from "MoveTo"
		 * 3. Fill the cancellation notice dialogue (Cancellation reason = "'Material Misrepresentation" )
		 * 4. Run DocGen Batch Job
		 * 5. Verify that AH61XX form is generated
		 * 6. Verify that AHTPC form is generated
		 * <p/>
		 * # Req
		 * 15370: US CL GD-87 Generate Cancellation Notice Document U/W or Insured Request
		 * 15780: US PA GD-02 Generate Cancellation Notice-UW or Insured Request
		 * 17315:HO-DOC-TPD01-NJ-01 17315 - US NJ GD- 17 Generate Third Party Designee Cover Page (AHTPC__ 11 12)
		 */
		policy.cancelNotice().perform(getPolicyTD("CancelNotice", "TestData_MaterialMisrepresentation"));
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, DocGenEnum.Documents.AH61XX);
	}

}
