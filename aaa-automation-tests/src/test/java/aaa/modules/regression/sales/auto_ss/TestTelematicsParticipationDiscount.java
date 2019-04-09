package aaa.modules.regression.sales.auto_ss;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.docgen.DocumentWrapper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
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
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static aaa.helpers.docgen.impl.PasDocImpl.getDocumentRequest;
import static aaa.main.enums.DocGenEnum.Documents.AA02AZ;
import static aaa.main.enums.DocGenEnum.Documents.AA11AZ;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestTelematicsParticipationDiscount extends AutoSSBaseTest {

	@StateList(statesExcept = Constants.States.CA)
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testTelematicsParticipationDiscount(@Optional("AZ") String state) {
		//TODO set
		/*adminApp().open(); AAAUBIParticipationDiscountLookup
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_DB_LOOKUPS.get());*/
		//
		String expectedTelematicsDiscountText = "Safety Score is now set to 'No Score' and telematics participation discount will be granted per auto tier, if available";

		TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", TEST_DATA_KEY)
				.adjust("VehicleTab", getTestSpecificTD("VehicleTab")).adjust("DocumentsAndBindTab", getTestSpecificTD("DocumentsAndBindTab"));
		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(tdAuto, VehicleTab.class, true);
		// Check telematics discount text
		assertThat(VehicleTab.telematicsDiscountText.getValue()).as("Telematics Discount Text displays on Vehicle tab").isEqualTo(expectedTelematicsDiscountText);
		new VehicleTab().submitTab();
		policy.getDefaultView().fillFromTo(tdAuto, FormsTab.class, PremiumAndCoveragesTab.class, true);

		//Check Discounts & Surcharges section
		assertThat(PremiumAndCoveragesTab.discountsAndSurcharges.getValue()).as("Discount and Surchrges Section").contains("Telematics Participation Discount");

		new PremiumAndCoveragesTab().submitTab();
		policy.getDefaultView().fillFromTo(tdAuto, DriverActivityReportsTab.class, PurchaseTab.class, true);
		policy.getDefaultView().getTab(PurchaseTab.class).submitTab();
		String policyNum = PolicySummaryPage.getPolicyNumber();

		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, AA02AZ).verify.mapping(getTestSpecificTD("AA02AZ_Verification"), policyNum);
		//perform Renewals
		LocalDateTime policyEffectiveDate = performRenewalProcess(policyNum, 4);

		TestData tdEndorsement = getTestSpecificTD("TestDataEndorsement");
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyEffectiveDate.plusMonths(1)));
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.endorse().perform(getPolicyTD("Endorsement", TEST_DATA_KEY));
		policy.getDefaultView().fillUpTo(tdEndorsement, VehicleTab.class, true);
		assertThat(new VehicleTab().getAssetList().getAsset(AutoSSMetaData.VehicleTab.SAFETY_SCORE.getLabel()).getValue().toString()).as("sd").contains("Unable to Score");
		policy.getDefaultView().fillFromTo(tdEndorsement, VehicleTab.class, DocumentsAndBindTab.class, true);
		policy.dataGather().getView().getTab(DocumentsAndBindTab.class).submitTab();
		//step 48
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		//TODO step 50
		//DocGenHelper.verifyDocumentsGenerated(policyNum, AA02AZ);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, AA02AZ).verify.mapping(getTestSpecificTD("AA02AZ_Verification"), policyNum);
		//step 51
		TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusMonths(1));
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
		//56
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyEffectiveDate.plusMonths(3)));
		mainApp().open(getLoginTD(Constants.UserGroups.E34));
		SearchPage.openPolicy(policyNum, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
		String rewritePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("TEST: Rewriting Policy #" + rewritePolicyNumber);
		policy.dataGather().start();
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestDataForBindRewrittenPolicy"), DriverActivityReportsTab.class, false);
		Tab.buttonSaveAndExit.click();
		String quoteNum = PolicySummaryPage.getPolicyNumber();
		policy.policyDocGen().start();
		GenerateOnDemandDocumentActionTab goddTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		goddTab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, quoteNum, AA11AZ).verify.mapping(getTestSpecificTD("AA11AZ_Verification"), quoteNum);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, AA02AZ).verify.mapping(getTestSpecificTD("AA02AZ_Verification"), policyNum);
	}

	@StateList(statesExcept = Constants.States.CA)
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void test(@Optional("AZ") String state) {
		TestData tdEndorsement = getTestSpecificTD("TestDataEndorsement").adjust("DocumentsAndBindTab", getTestSpecificTD("DocumentsAndBindTab"));
		String policyNum = "AZSS953168476";
		mainApp().open(getLoginTD(Constants.UserGroups.E34));
		SearchPage.openPolicy(policyNum, ProductConstants.PolicyStatus.POLICY_CANCELLED);
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.DATA_GATHERING);
		String rewritePolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		log.info("TEST: Rewriting Policy #" + rewritePolicyNumber);
		policy.dataGather().start();
		policy.getDefaultView().fillUpTo(getPolicyTD("Rewrite", "TestDataForBindRewrittenPolicy"), DriverActivityReportsTab.class, false);
		Tab.buttonSaveAndExit.click();
		String quoteNum = PolicySummaryPage.getPolicyNumber();
		policy.quoteDocGen().start();

		policy.quoteDocGen().start();
		GenerateOnDemandDocumentActionTab goddTab = policy.quoteDocGen().getView().getTab(GenerateOnDemandDocumentActionTab.class);
		goddTab.generateDocuments();

		policy.policyDocGen().start();
		goddTab.generateDocuments(false, DocGenEnum.DeliveryMethod.LOCAL_PRINT, null, null, null, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, quoteNum, AA11AZ);
		DocGenHelper.verifyDocumentsGenerated(true, false, quoteNum, AA11AZ).verify.mapping(getTestSpecificTD("AA11AZ_Verification"), quoteNum);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, AA02AZ).verify.mapping(getTestSpecificTD("AA02AZ_Verification"), policyNum);

	}

	@StateList(statesExcept = Constants.States.CA)
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void test2(@Optional("AZ") String state) {
		String quoteNum = "QAZSS933644544";
		String policyNum = "AZSS933644543";

		//DocumentGenerationRequest document = PasDocImpl.getDocumentRequest(policyNum, AA02AZ);

		List<String> vihicles = new ArrayList<>();
		DocumentGenerationRequest document = getDocumentRequest(policyNum, AA02AZ);
		document.getDocumentData().getVehicle();

		DocumentWrapper dw = DocGenHelper.verifyDocumentsGenerated(policyNum,
				AA02AZ);

		/*for (aaa.helpers.xml.model.Document d : dw.getStandardDocumentRequest()) {
			for (DocumentDataSection dds : d.getDocumentDataSections()) {
				sectionNames.add(dds.getSectionName());
			}
		}
		CustomAssert.assertTrue(sectionNames.contains("Discounts"));*/

	}

	private LocalDateTime performRenewalProcess(String policyNum, int countsOfRenewals) {
		LocalDateTime policyExpirationDate;
		for (int i = 0; i < countsOfRenewals; i++) {
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			policyExpirationDate = PolicySummaryPage.getExpirationDate();
			//R-96
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewImageGenerationDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			//R-63
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
			//R-45
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			PolicySummaryPage.buttonRenewals.click();
			assertThat(PolicySummaryPage.tableRenewals.getRow(1).getCell(PolicyConstants.PolicyRenewalsTable.STATUS).getValue()).isEqualTo(ProductConstants.PolicyStatus.PREMIUM_CALCULATED);
			//R-35
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
			/*if (i == countsOfRenewals - 1) {
				DocGenHelper.verifyDocumentsGenerated(true, true, policyNum, AA02AZ).verify.mapping(getTestSpecificTD("AA02AZ_Verification"), policyNum);
			}*/
			mainApp().open();
			SearchPage.openPolicy(policyNum);
			PolicySummaryPage.buttonRenewals.click();
			//Check status of renewal
			assertThat(PolicySummaryPage.tableRenewals.getRow(1).getCell(PolicyConstants.PolicyRenewalsTable.STATUS).getValue()).isEqualTo(ProductConstants.PolicyStatus.PROPOSED);
			Dollar renewalPremium = new Dollar(PolicySummaryPage.tableRenewals.getRow(1).getCell(PolicyConstants.PolicyRenewalsTable.PREMIUM).getValue());
			//R-20
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
			//R
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(policyExpirationDate));
			mainApp().open();
			SearchPage.openBilling(policyNum);
			new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), renewalPremium);
			//R+1
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
			JobUtils.executeJob(Jobs.policyStatusUpdateJob);
			JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		}
		mainApp().open();
		SearchPage.openPolicy(policyNum);
		return PolicySummaryPage.getEffectiveDate();
	}
}
