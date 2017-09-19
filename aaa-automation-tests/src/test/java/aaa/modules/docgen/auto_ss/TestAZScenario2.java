package aaa.modules.docgen.auto_ss;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.mortbay.log.Log;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import static aaa.main.enums.DocGenEnum.Documents.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.Dollar;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;

public class TestAZScenario2 extends AutoSSBaseTest {

//	private String policyNumber="AZSS952111077";
	private String policyNumber;
	private LocalDateTime policyExpirationDate;
	private String termEffDt;
	private String termExprDt;
	private String plcyEffDt;
	private String plcyExprDt;
	private String endrEffDt;
	private PremiumAndCoveragesTab premiumAndCoveragesTab = policy.getDefaultView().getTab(PremiumAndCoveragesTab.class);
	private List<TestData> vehClsnDed = new ArrayList<TestData>();
	private List<TestData> vehCompDed = new ArrayList<TestData>();
	private List<TestData> vehBdyInjPrem = new ArrayList<TestData>();
	private List<TestData> vehPDPrem = new ArrayList<TestData>();
	private List<TestData> vehUMPrem = new ArrayList<TestData>();
	private List<TestData> vehUIMBPrem = new ArrayList<TestData>();
	private List<TestData> vehMPPrem = new ArrayList<TestData>();
	private List<TestData> vehClsnPrem = new ArrayList<TestData>();
	private List<TestData> vehCompPrem = new ArrayList<TestData>();
//	private List<TestData> vehSpclEqpmtPrem = new ArrayList<TestData>();
	private List<TestData> vehTotPrem = new ArrayList<TestData>();
	private List<TestData> vehSftyGlsDed = new ArrayList<TestData>();
	private List<TestData> vehNwAddPrtcDed = new ArrayList<TestData>();
	private List<TestData> vehRntlReimbsDed = new ArrayList<TestData>();
	private List<TestData> vehTwgLbrDed = new ArrayList<TestData>();
	private List<TestData> vehLnPrtcDed = new ArrayList<TestData>();
	private List<TestData> plcyMpEaPers = new ArrayList<TestData>();
	private List<TestData> plcyPdEaOcc = new ArrayList<TestData>();
	private List<TestData> vehRntlReimbsPrem = new ArrayList<TestData>();
	private List<TestData> vehTwgLbrPrem = new ArrayList<TestData>();
	private String plcyTotPrem;
	private String netWrtPrem;
	private String plcyAutoDeadBenPrem;
	private String plcyTotFee;
	private List<TestData> dueAmount = new ArrayList<TestData>();
	private List<TestData> installmentDueDate = new ArrayList<TestData>();

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void TC01_CreatePolicy(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
//		SearchPage.openPolicy(policyNumber);
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		log.info("Original Policy #" + policyNumber);
		termEffDt=DocGenHelper.convertToZonedDateTime(PolicySummaryPage.getEffectiveDate());
		plcyEffDt=DocGenHelper.convertToZonedDateTime(PolicySummaryPage.getEffectiveDate());
		plcyExprDt=DocGenHelper.convertToZonedDateTime(policyExpirationDate);
		termExprDt=DocGenHelper.convertToZonedDateTime(policyExpirationDate);
		storeCoveragesData();
		storeBillingData();
		
		/* verify the xml file 
		AH35XX
		AA02AZ
		AA10XX
		AA43AZ
		AA52AZ
		AA59XX
		AAGCAZ
		AARFIXX
		AASR22
		AHNBXX*/
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AA43AZ, AH35XX, AASR22, AA59XX, AAGCAZ, AA52AZ, AARFIXX, AHNBXX, AA10XX, AA02AZ).verify.mapping(getTestSpecificTD("TestData_VerificationNB")
						.adjust(TestData.makeKeyPath("AA43AZ", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AA43AZ", "form", "TermEffDt","DateTimeField"), termEffDt)
						.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
						.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
						.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyEffDt","DateTimeField"), plcyEffDt)
						.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyExprDt","DateTimeField"), plcyExprDt)
						.adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AASR22", "form", "TermEffDt","DateTimeField"), termEffDt)
						.adjust(TestData.makeKeyPath("AA59XX", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AAGCAZ", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AA52AZ", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AA52AZ", "form", "TermEffDt","DateTimeField"), termEffDt)
						.adjust(TestData.makeKeyPath("AARFIXX", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AHNBXX", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AHNBXX", "form", "TermEffDt","DateTimeField"), termEffDt)
						.adjust(TestData.makeKeyPath("AA10XX", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AA10XX", "form", "TermEffDt","DateTimeField"), termEffDt)
						.adjust(TestData.makeKeyPath("AA10XX", "form", "TermExprDt","DateTimeField"), termExprDt)
						.adjust(TestData.makeKeyPath("AA02AZ", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehPDPrem"), vehPDPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUMPrem"), vehUMPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUIMBPrem"), vehUIMBPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehMPPrem"), vehMPPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompPrem"), vehCompPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnDed"), vehClsnDed)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompDed"), vehCompDed)
//						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehSpclEqpmtPrem"), vehSpclEqpmtPrem)
//				        .adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTotPrem"), vehTotPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehSftyGlsDed"), vehSftyGlsDed)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehNwAddPrtcDed"), vehNwAddPrtcDed)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehRntlReimbsDed"), vehRntlReimbsDed)	
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTwgLbrDed"), vehTwgLbrDed)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehLnPrtcDed"), vehLnPrtcDed)						
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyAutoDeadBenPrem", "TextField"), plcyAutoDeadBenPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "PaymentDetails", "PlcyTotFee", "TextField"), plcyTotFee)
						.adjust(TestData.makeKeyPath("AA02AZ", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
						.adjust(TestData.makeKeyPath("AA02AZ", "form", "TermEffDt","DateTimeField"), termEffDt)
						.adjust(TestData.makeKeyPath("AA02AZ", "form", "TermExprDt","DateTimeField"), termExprDt),
				policyNumber);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Parameters({"state"})
//	@Test
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL}, dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_EndorsePolicy(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		TestData endorsementTd = getTestSpecificTD("TestData_Endorsement");
		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		storeCoveragesData();
		storeBillingData();
		
		endrEffDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tablePaymentsOtherTransactions
				.getRow(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,"Endorsement - Maintain Vehicle(s)")
				.getCell(BillingPaymentsAndOtherTransactionsTable.EFF_DATE).getValue(), DateTimeUtils.MM_DD_YYYY));
		
		/* verify the xml file 
		AASR26
		AH35XX
		AA02AZ
		AA10XX
		AA43AZ
		AA52AZ
		AA59XX
		AAPDXX*/
		
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR26,AA43AZ,AH35XX,AA59XX,AA52AZ,AA10XX,AAPDXX,AA02AZ).verify.mapping(getTestSpecificTD("TestData_VerificationED")
				.adjust(TestData.makeKeyPath("AASR26", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AASR26", "form", "TermEffDt","DateTimeField"), termEffDt)
				.adjust(TestData.makeKeyPath("AASR26", "form", "TermExprDt","DateTimeField"), termExprDt)
				.adjust(TestData.makeKeyPath("AA43AZ", "form", "PlcyNum","TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA43AZ", "form", "TermEffDt","DateTimeField"), termEffDt)
				.adjust(TestData.makeKeyPath("AA43AZ", "form", "EndrEffDt","DateTimeField"), endrEffDt)
				.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
				.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
				.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyEffDt","DateTimeField"), plcyEffDt)
				.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyExprDt","DateTimeField"), plcyExprDt)
				.adjust(TestData.makeKeyPath("AA59XX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA52AZ", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA52AZ", "form", "TermEffDt","DateTimeField"), termEffDt)
				.adjust(TestData.makeKeyPath("AA52AZ", "form", "EndrEffDt","DateTimeField"), endrEffDt)
				.adjust(TestData.makeKeyPath("AA10XX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA10XX", "form", "TermEffDt","DateTimeField"), termEffDt)
				.adjust(TestData.makeKeyPath("AA10XX", "form", "TermExprDt","DateTimeField"), termExprDt)
				.adjust(TestData.makeKeyPath("AA10XX", "form", "EndrEffDt","DateTimeField"), endrEffDt)
	            .adjust(TestData.makeKeyPath("AA02AZ", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyMpEaPers"), plcyMpEaPers)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyPdEaOcc"), plcyPdEaOcc)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehBdyInjPrem"), vehBdyInjPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehPDPrem"), vehPDPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUMPrem"), vehUMPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUIMBPrem"), vehUIMBPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehMPPrem"), vehMPPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompPrem"), vehCompPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnPrem"), vehClsnPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehRntlReimbsPrem"), vehRntlReimbsPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTwgLbrPrem"), vehTwgLbrPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnDed"), vehClsnDed)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompDed"), vehCompDed)
//				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehSpclEqpmtPrem"), vehSpclEqpmtPrem)
//			    .adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTotPrem"), vehTotPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehSftyGlsDed"), vehSftyGlsDed)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehNwAddPrtcDed"), vehNwAddPrtcDed)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehRntlReimbsDed"), vehRntlReimbsDed)	
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTwgLbrDed"), vehTwgLbrDed)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehLnPrtcDed"), vehLnPrtcDed)						
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "NetWrtPrem", "TextField"), netWrtPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyAutoDeadBenPrem", "TextField"), plcyAutoDeadBenPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "PaymentDetails", "PlcyTotFee", "TextField"), plcyTotFee)
				.adjust(TestData.makeKeyPath("AA02AZ", "PaymentDetails", "PlcyTotPrem", "TextField"), plcyTotPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "form", "TermEffDt","DateTimeField"), termEffDt)
				.adjust(TestData.makeKeyPath("AA02AZ", "form", "TermExprDt","DateTimeField"), termExprDt)
				.adjust(TestData.makeKeyPath("AA02AZ", "form", "EndrEffDt","DateTimeField"), endrEffDt),
		policyNumber);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();

	}

	@Parameters({"state"})
//	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_RenewalImageGeneration(@Optional("") String state) {
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Image Generation Date" + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	@Parameters({"state"})
//	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC04_RenewaPreviewGeneration(@Optional("") String state) {

		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Preview Generation Date" + renewPreviewGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		Tab.buttonOk.click();
		// Create new renewal version
		if (Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
			Page.dialogConfirmation.confirm();
		}
		policy.getDefaultView().fill(getTestSpecificTD("TestData_AddRenewal"));
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);

	}

	@Parameters({"state"})
//	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC05_RenewaOfferGeneration(@Optional("") String state) {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Generation Date" + renewOfferGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

//		TODO verify the documents AA02,AHAUXX,AA10XX,AHPNXX
	}

	@Parameters({"state"})
//	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC06_RenewaOfferBillGeneration(@Optional("") String state) {
		LocalDateTime renewOfferBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Bill Generation Date" + renewOfferBillGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
//		TODO verify the xml file AHREXX, AH35XX
	}

	private void storeCoveragesData() {
		policy.policyInquiry().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		for (TestData td : premiumAndCoveragesTab.getRatingDetailsVehiclesData()) {
			vehClsnDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Collision Deductible"))));
			vehCompDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Comprehensive Deductible"))));
			vehSftyGlsDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Full Safety Glass"))));
			vehNwAddPrtcDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("New Car Added Protection Coverage"))));
			vehRntlReimbsDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Rental Reimbursement Limit"))));
			vehTwgLbrDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Towing and Labor Coverage"))));
			vehLnPrtcDed.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Vehicle Loan/Lease Coverage"))));
			plcyMpEaPers.add(DataProviderFactory.dataOf("TextField", td.getValue("Medical Payments").toString().replace("$", "").replace(",", "")));
			plcyPdEaOcc.add(DataProviderFactory.dataOf("TextField", td.getValue("Property Damage Liability").replace("$", "").replace(",", "")));
		}
		
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		for (TestData td : premiumAndCoveragesTab.getTermPremiumByVehicleData()) {
			vehBdyInjPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Bodily Injury Liability"))));
			vehPDPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Property Damage Liability"))));
			vehUMPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Uninsured Motorists Bodily Injury"))));
			vehUIMBPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Underinsured Motorists Bodily Injury"))));
			vehMPPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Medical Payments"))));
			vehClsnPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Collision Deductible"))));
			vehCompPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Comprehensive Deductible"))));
//			vehSpclEqpmtPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Special Equipment Coverage"))));
			vehTotPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Total Vehicle Term Premium"))));
			vehRntlReimbsPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Rental Reimbursement"))));
			vehTwgLbrPrem.add(DataProviderFactory.dataOf("TextField", formatValue(td.getValue("Towing and Labor Coverage"))));
		}
		netWrtPrem = formatValue(PremiumAndCoveragesTab.totalActualPremium.getValue());
		
		plcyAutoDeadBenPrem = formatValue(PremiumAndCoveragesTab.tableFormsSummary.getRow("Forms", "ADBE").getCell(2).getValue());

		// Store the value for total fee from table tablefeesSummary
		Dollar _plcyTotFee = new Dollar(0);
		if (PremiumAndCoveragesTab.tablefeesSummary.isPresent()){
			for (int i = 1; i <= PremiumAndCoveragesTab.tablefeesSummary.getRowsCount(); i++) {
				_plcyTotFee = _plcyTotFee.add(new Dollar(PremiumAndCoveragesTab.tablefeesSummary.getRow(i).getCell(2).getValue()));
			}	
			plcyTotFee = _plcyTotFee.toString().replace("$", "").replace(",", "");
			plcyTotPrem = new Dollar(PremiumAndCoveragesTab.totalTermPremium.getValue()).add(_plcyTotFee).toString().replace("$", "").replace(",", "");
		}
		else {
			plcyTotFee="0.00";
			plcyTotPrem = new Dollar(PremiumAndCoveragesTab.totalTermPremium.getValue()).toString().replace("$", "").replace(",", "");
		}
		
		Tab.buttonTopCancel.click();
	}

	private void storeBillingData() {
		BillingSummaryPage.open();
		for (int i = 2; i <= 11; i++) {
			dueAmount.add(DataProviderFactory.dataOf("TextField", BillingSummaryPage.getInstallmentAmount(i).add(2).toString().replace("$", "")));
			installmentDueDate.add(DataProviderFactory.dataOf("DateTimeField", DocGenHelper.convertToZonedDateTime(BillingSummaryPage.getInstallmentDueDate(i))));
		}
	}

	private String formatValue(String value) {
//		return "No Coverage".contains(value) ? "0" : new Dollar(value.replace("\n", "")).toString().replace("$", "").replace(",", "");
		return value.contains("No Coverage") ? "0.00" : new Dollar(value.replace("\n", "")).toString().replace("$", "").replace(",", "");
	}

}
