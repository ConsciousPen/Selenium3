package aaa.modules.regression.service.auto_ss;

import static aaa.helpers.docgen.impl.PasDocImpl.getDocumentRequest;
import static aaa.main.enums.DocGenEnum.Documents.AA02AZ;
import static aaa.main.enums.DocGenEnum.Documents.AA11AZ;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.xml.model.pasdoc.Discount;
import aaa.helpers.xml.model.pasdoc.DocumentGenerationRequest;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

/**
 * @author mlaptsionak
 * @name Flat Cancel - Rescission Notice Due to Non-Sufficient Funds (NSF) ("D-T-AU-SS-CT-746")
 * @scernario 1. Search for AAAUBIParticipationDiscountLookup in Admin > DB Lookups > LookupList Name.
 * By default the value was configured to 50, ensure the value is changed to 3 before starting execution.
 * 2. Login to the PAS as an Agent and initiate an Auto Signature Series Quote with 1 NI/1 D/1 V.
 * 3. Fill in mandatory details on all tabs up to the Vehicle Tab
 * 4. On the Vehicle Tab, add a 1996+ newer vehicle such that it is eligible for Usage Based Insurance Program. (e.g. VIN= WMWRC33536TK73512 )
 * 5. Select "Yes" for the "Enroll in Usage Based Insurance?" question and fill in any other detail that is required to enroll this vehicle for the UBI program. Click on 'Get Vehicle Details'
 * Vehicle Eligibility Response = 'Vehicle Eligible'
 * Device Voucher Number = 'No response'
 * Then press button 'Grant Participation Discount'
 * 6. Click calculate premium and Validate Discounts & Surcharges section.
 * 7. Navigate to Bind tab, make the downpayment and purchase the policy. Run aaaDocGenBatchJob
 * 8. Check that Declaration page AA02AZ is generated and has the details of the applicable discounts.
 * 9. Run the following jobs:
 * R-96 Renewal_Offer_Generation_Part2
 * R-63 Renewal_Offer_Generation_Part1
 * R-45 Renewal_Offer_Generation_Part2
 * R-35 Renewal_Offer_Generation_Part2
 * R-20 aaaRenewalNoticeBillAsyncJob
 * 10. Navigate to billing tab and make payment for the renewal amount due
 * 11. Run the policyStatusUpdateJob and policyLapsedRenewalProcessAsyncJob
 * 12. Repeat steps 9-11 3 times
 * 13. 5DD1-20 Initiate an endorsement from the move to drop down.
 * 14. Navigate to Vehicle tab and validate Telematics Participation Discount was removed for the vehicle enrolled in UBI at previous renewal and as a result
 * Enroll in Usage Based Insurance = Yes.
 * Safety Score = 'Unable to Score'
 * Expected results
 * 15. Calculate premium and bind the endorsement.
 * 16. Run aaaDocGenBatchJob
 * 17. Validate that declaration AA02AZ is generated: System should not display "Telematics Participation Discount" OR "Safe Driving Discount"
 * 18. 5DD1 Login to PAS as user having 'Policy Cancellation' privilege
 * 19. Cancel the policy
 * 20. 5DD3-20 Login to PAS as user E34 user having 'Policy Add From Cancel' privilege.
 * 21. Select Rewrite option from Take Action drop down.
 * 22. Enter in data gathering mode.
 * 23. Enter mandatory details and calculate premium.
 * 24. Save&Exit the quote. Go to Generate On Demand document  and select the Auto Insurance Application Form (AA11AZ).
 * 25. Validate the document content. The field Telematics Participation Discount in the Motor Vehicle Information section should not be displayed in Application for Auto Insurance document
 */

public class TestTelematicsParticipationDiscount extends AutoSSBaseTest {

	@StateList(statesExcept = Constants.States.CA)
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testTelematicsParticipationDiscount(@Optional("AZ") String state) {

		int countsOfRenewals = 3;
		String expectedTelematicsDiscountText = "Safety Score is now set to 'No Score' and telematics participation discount will be granted per auto tier, if available";

		setValueForAAAUBIParticipationDiscountLookup(countsOfRenewals);
		//1-4. Initiate Auto Signature Series and proceed to Vehicle tab
		TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", TEST_DATA_KEY)
				.adjust("VehicleTab", getTestSpecificTD("VehicleTab")).adjust("DocumentsAndBindTab", getTestSpecificTD("DocumentsAndBindTab"));
		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(tdAuto, VehicleTab.class, true);

		//5. Check telematics discount text
		assertThat(VehicleTab.telematicsDiscountText.getValue()).as("Telematics Discount Text displays on Vehicle tab").isEqualTo(expectedTelematicsDiscountText);
		new VehicleTab().submitTab();
		policy.getDefaultView().fillFromTo(tdAuto, FormsTab.class, PremiumAndCoveragesTab.class, true);

		//6. Check Discounts & Surcharges section
		assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue()).as("Discount and Surchrges Section").contains("Telematics Participation Discount");

		//7. Bind policy and run aaaDocGenBatchJob
		new PremiumAndCoveragesTab().submitTab();
		policy.getDefaultView().fillFromTo(tdAuto, DriverActivityReportsTab.class, PurchaseTab.class, true);
		policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
		String policyNum = PolicySummaryPage.getPolicyNumber();
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);

		//8. Check that Declaration page AA02AZ is generated and has the details of the applicable discounts
		checkDiscount(getDocumentRequest(policyNum, AA02AZ), true);

		//9-12. Perform Renewals
		LocalDateTime policyEffectiveDate = performRenewalProcess(policyNum, countsOfRenewals);

		//13-14. Initiate endorsement and Navigate to Vehicle tab to validate Telematics Participation Discount
		TestData tdEndorsement = getTestSpecificTD("TestDataEndorsement");
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyEffectiveDate.plusMonths(1)));
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.endorse().perform(getPolicyTD("Endorsement", TEST_DATA_KEY));
		policy.getDefaultView().fillUpTo(tdEndorsement, VehicleTab.class, true);
		assertThat(new VehicleTab().getAssetList().getAsset(AutoSSMetaData.VehicleTab.SAFETY_SCORE.getLabel()).getValue().toString()).as("sd").contains("Unable to Score");

		//15. Calculate premium and bind the endorsement.
		policy.getDefaultView().fillFromTo(tdEndorsement, VehicleTab.class, DocumentsAndBindTab.class, true);
		policy.dataGather().getView().getTab(DocumentsAndBindTab.class).submitTab();

		//16. Run aaaDocGenBatchJob
		JobUtils.executeJob(BatchJob.aaaDocGenBatchJob);

		//17. Validate that declaration AA02AZ is generated: System should not display "Telematics Participation Discount"
		checkDiscount(getDocumentRequest(policyNum, AA02AZ), false);

		//18-19. Perform cancellation steps
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusMonths(1));
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);

		//20-24 Rewrite policy and generate AA11AZ document
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyEffectiveDate.plusMonths(3)));
		mainApp().open(getLoginTD(Constants.UserGroups.E34));
		SearchPage.openPolicy(policyNum, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
		policy.dataGather().start();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestDataForBindRewrittenPolicy"), DriverActivityReportsTab.class, false);
		Tab.buttonSaveAndExit.click();
		String quoteNum = PolicySummaryPage.getPolicyNumber();
		policy.policyDocGen().start();
		GenerateOnDemandDocumentActionTab goddTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		goddTab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AA11AZ);

		//25 Validate that field Telematics Participation Discount in the Motor Vehicle Information section should not be displayed
		checkDiscount(getDocumentRequest(policyNum, AA02AZ), false);
		checkDiscount(getDocumentRequest(quoteNum, AA11AZ), false);

		setValueForAAAUBIParticipationDiscountLookup(50);
	}

	private void setValueForAAAUBIParticipationDiscountLookup(int value) {
		String sqlUpdate = String.format(
				"update lookupvalue set DISPLAYVALUE = '%s' where lookuplist_id = (select id from lookuplist where lookupname='AAAUBIParticipationDiscountLookup') and code = 'ANNUALTERM'", value);
		String sqlSelect = "select displayvalue from lookupvalue where lookuplist_id = (select id from lookuplist where lookupname='AAAUBIParticipationDiscountLookup')";

		if (Integer.parseInt(DBService.get().getValue(sqlSelect).get()) != value) {
			DBService.get().executeUpdate(sqlUpdate);
			log.info("DB update +++++ To set counts of renewals that can be performed with enable Telematics Participation Discount ++++++\n");
			//Shift date forward to apply new setting for AAAUBIParticipationDiscountLookup
			TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));
		}
	}

	private void checkDiscount(DocumentGenerationRequest document, boolean isDiscountExist) {
		List<String> discounts = new ArrayList<>();
		for (Discount discount : document.getDocumentData().getVehicles().get(0).getDiscounts()) {
			discounts.add(discount.getDescription());
		}
		assertThat(discounts.contains("Telematics Participation Discount")).as("Check Telematics Participation Discount").isEqualTo(isDiscountExist);
	}

	private LocalDateTime performRenewalProcess(String policyNum, int countsOfRenewals) {
		LocalDateTime policyExpirationDate;
		for (int i = 0; i < countsOfRenewals; i++) {
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			policyExpirationDate = PolicySummaryPage.getExpirationDate();
			//R-96
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
			JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
			JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
			//R-63
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(policyExpirationDate));
			JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
			//R-45
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
			JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			PolicySummaryPage.buttonRenewals.click();
			assertThat(PolicySummaryPage.tableRenewals.getRow(1).getCell(PolicyConstants.PolicyRenewalsTable.STATUS).getValue()).isEqualTo(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
			//R-35
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
			JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
			if (i == countsOfRenewals) {
				checkDiscount(getDocumentRequest(policyNum, AA02AZ), false);
			}
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			PolicySummaryPage.buttonRenewals.click();
			//Check status of renewal
			assertThat(PolicySummaryPage.tableRenewals.getRow(1).getCell(PolicyConstants.PolicyRenewalsTable.STATUS).getValue()).isEqualTo(ProductConstants.PolicyStatus.PROPOSED);
			Dollar renewalPremium = new Dollar(PolicySummaryPage.tableRenewals.getRow(1).getCell(PolicyConstants.PolicyRenewalsTable.PREMIUM).getValue());
			//R-20
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));
			JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
			//R
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(policyExpirationDate));
			mainApp().open();
			SearchPage.openBilling(policyNum);
			new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), renewalPremium);
			//R+1
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
			JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
			JobUtils.executeJob(BatchJob.lapsedRenewalProcessJob);
		}
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		return PolicySummaryPage.getEffectiveDate();
	}
}
