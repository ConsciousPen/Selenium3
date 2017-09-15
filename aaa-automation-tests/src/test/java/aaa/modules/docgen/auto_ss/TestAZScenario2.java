package aaa.modules.docgen.auto_ss;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.xs.StringList;
import org.mortbay.log.Log;
import org.openqa.selenium.By;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import static aaa.main.enums.DocGenEnum.Documents.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.table.Table;
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
import aaa.main.enums.ProductConstants;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;

public class TestAZScenario2 extends AutoSSBaseTest{

	protected String policyNumber;
	protected LocalDateTime policyExpirationDate;
	PremiumAndCoveragesTab premiumandcoveragestab=policy.getDefaultView().getTab(PremiumAndCoveragesTab.class);
    public static Table tablePolicybyVehicle1 = new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table/tbody/tr/td[1]/div/div/table[2]"));
	public static Table tableTermPremiumbyVehicle = new Table(By.xpath("//div[@id='policyDataGatherForm:componentView_AAAVehicleCoveragePremiumDetails_body']/table"));
	
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void TC01_CreatePolicy(String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
//		createCustomerIndividual();
//		TestData tdpolicy = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
//		policyNumber = createPolicy(tdpolicy);
		SearchPage.openPolicy("AZSS952122201");
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		policyNumber= PolicySummaryPage.labelPolicyNumber.getValue();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		log.info("Original Policy #" + policyNumber);
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
//		get the value for vehicle Deductible
		
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		
		List<TestData> PlcySpclEqpmtTotAmt = new ArrayList<>();
		List<TestData> VehClsnDed = new ArrayList<>();
		List<TestData> VehCompDed = new ArrayList<>();
		for(int i=2;i<=3;i++){
			TestData td_PlcySpclEqpmtTotAmt=DataProviderFactory.dataOf("TextField",PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Special Equipment Limit").getCell(i).getValue().toString());
			TestData td_VehClsnDed=DataProviderFactory.dataOf("TextField", String.format("%s.00", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Collision Deductible").getCell(i).getValue().toString().replace("$", "")));
			TestData td_VehCompDed=DataProviderFactory.dataOf("TextField",String.format("%s.00", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Comprehensive Deductible").getCell(i).getValue().toString().replace("$", "")));
			
			log.info("Special Equipment Limit"+PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Special Equipment Limit").getCell(i).getValue().replace("$", "").toString());
			log.info("Collision Deductible"+String.format("%s.00", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Collision Deductible").getCell(i).getValue().toString().replace("$", "")));
			log.info("Comprehensive Deductible"+String.format("%s.00", PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1,"Comprehensive Deductible").getCell(i).getValue().toString().replace("$", "")));
			
			PlcySpclEqpmtTotAmt.add(td_PlcySpclEqpmtTotAmt);
			VehClsnDed.add(td_VehClsnDed);
			VehCompDed.add(td_VehCompDed);
		}
		
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
//		for (TestData td : premiumandcoveragestab.getRatingDetailsVehiclesData()){
//			TestData td_PlcySpclEqpmtTotAmt=DataProviderFactory.dataOf("TextField",String.format("%.2f", td.getValue("Special Equipment Limit")));
//			TestData td_VehClsnDed=DataProviderFactory.dataOf("TextField", String.format("%s.00", td.getValue("Collision Deductible").replace("$", "")));
//			TestData td_VehCompDed=DataProviderFactory.dataOf("TextField",String.format("%s.00", td.getValue("Comprehensive Deductible").replace("$", "")));
//			
//			PlcySpclEqpmtTotAmt.add(td_PlcySpclEqpmtTotAmt);
//			VehClsnDed.add(td_VehClsnDed);
//			VehCompDed.add(td_VehCompDed);
//		}
		
//		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		
		
//		get the value for vehicle coverage premium
		List<TestData> VehBdyInjPrem = new ArrayList<>();
		List<TestData> VehPDPrem = new ArrayList<>();
		List<TestData> VehUMPrem = new ArrayList<>();
		List<TestData> VehUIMBPrem = new ArrayList<>();
		List<TestData> VehMPPrem = new ArrayList<>();
		List<TestData> VehClsnPrem = new ArrayList<>();
		List<TestData> VehCompPrem = new ArrayList<>();
		List<TestData> VehSpclEqpmtPrem = new ArrayList<>();
		List<TestData> VehTotPrem = new ArrayList<>();
		log.info("tableTermPremiumbyVehicle column #"+tableTermPremiumbyVehicle.getColumnsCount());
		
		for (int i = 2; i <= 4; i++){
			TestData td_VehBdyInjPrem=DataProviderFactory.dataOf("TextField",tableTermPremiumbyVehicle.getRow(3).getCell(i).getValue().toString().replace("$", ""));
//			TestData td_VehPDPrem=DataProviderFactory.dataOf("TextField",tableTermPremiumbyVehicle.getRow(1, "Property Damage Liability").getCell(i).getValue().toString().replace("$", ""));
//			TestData td_VehUMPrem=DataProviderFactory.dataOf("TextField",tableTermPremiumbyVehicle.getRow(1, "Uninsured Motorists Bodily Injury").getCell(i).getValue().toString().replace("$", ""));
//			TestData td_VehUIMBPrem=DataProviderFactory.dataOf("TextField",tableTermPremiumbyVehicle.getRow(1, "Underinsured Motorists Bodily Injury").getCell(i).getValue().toString().replace("$", ""));
//			TestData td_VehMPPrem=DataProviderFactory.dataOf("TextField",tableTermPremiumbyVehicle.getRow(1, "Medical Payments").getCell(i).getValue().toString().replace("$", ""));
//			TestData td_VehClsnPrem=DataProviderFactory.dataOf("TextField",tableTermPremiumbyVehicle.getRow(1, "Collision Deductible").getCell(i).getValue().toString().replace("$", ""));
//			TestData td_VehCompPrem=DataProviderFactory.dataOf("TextField",tableTermPremiumbyVehicle.getRow(1, "Comprehensive Deductible").getCell(i).getValue().toString().replace("$", ""));
//			TestData td_VehSpclEqpmtPrem=DataProviderFactory.dataOf("TextField",tableTermPremiumbyVehicle.getRow(1, "Special Equipment Coverage").getCell(i).getValue().toString().replace("$", ""));
//			TestData td_VehTotPrem=DataProviderFactory.dataOf("TextField",tableTermPremiumbyVehicle.getRow(1, "Total Vehicle Term Premium").getCell(i).getValue().toString().replace("$", ""));
//			
//			VehBdyInjPrem.add(td_VehBdyInjPrem);
//			VehPDPrem.add(td_VehPDPrem);
//			VehUMPrem.add(td_VehUMPrem);
//			VehUIMBPrem.add(td_VehUIMBPrem);
//			VehMPPrem.add(td_VehMPPrem);
//			VehCompPrem.add(td_VehCompPrem);
//			VehClsnPrem.add(td_VehClsnPrem);			
//			VehSpclEqpmtPrem.add(td_VehSpclEqpmtPrem);			
//			VehTotPrem.add(td_VehTotPrem);	
			
		}
		
		String NetWrtPrem=PremiumAndCoveragesTab.totalActualPremium.getValue().toString().replace("$", "");
//		String PlcyTotPrem=PremiumAndCoveragesTab.totalTermPremium.getValue().toString().replace("$", "");
		String PlcyAutoDeadBenPrem=PremiumAndCoveragesTab.tableFormsSummary.getRow("Forms", "ADBE").getCell(2).getValue().replace("$", "");
		log.info("vehicle premium" + PlcyAutoDeadBenPrem);
		
//		Get the value for total fee from tabel tablefeesSummary
		List<BigDecimal> feeList = new ArrayList<>();
		for(int i=2; i<PremiumAndCoveragesTab.tablefeesSummary.getRowsCount();i++){
            String td_feeList=PremiumAndCoveragesTab.tablefeesSummary.getRow(i).getCell(2).getValue().toString().replace("$", "");			
			feeList.add(new BigDecimal(td_feeList));		
		}
		
		BigDecimal plcyTotFee = BigDecimal.ZERO;
		for (BigDecimal fee : feeList) {
			plcyTotFee = plcyTotFee.add(fee);
		}
		
		String PlcyTotFee=plcyTotFee.toString();
		
		BillingSummaryPage.open();
		List<TestData> dueAmount = new ArrayList<>();
		List<TestData> installmentDueDate = new ArrayList<>();
		for (int i = 2; i <= 11; i++) {
			TestData td_dueAmount = DataProviderFactory.dataOf("TextField", BillingSummaryPage.getInstallmentAmount(i).add(2).toString().replace("$", ""));
			TestData td_installmentDueDate = DataProviderFactory.dataOf("DateTimeField", DocGenHelper.convertToZonedDateTime(BillingSummaryPage.getInstallmentDueDate(i)));
			dueAmount.add(td_dueAmount);
			installmentDueDate.add(td_installmentDueDate);
		}

//		verify the xml file 
//		AH35XX // TODO need to resolve the time zone issue
//		AA02AZ
//		AA10XX
//		AA43AZ
//		AA52AZ
//		AA59XX
//		AAGCAZ
//		AARFIXX
//		AASR22
//		AHNBXX
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AA43AZ,AH35XX,AASR22,AA59XX,AAGCAZ,AA52AZ,AARFIXX,AHNBXX,AA10XX,AA02AZ).verify.mapping(getTestSpecificTD("TestData_Verification")
				.adjust(TestData.makeKeyPath("AA43AZ", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
				.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
				.adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA59XX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AAGCAZ", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA52AZ", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AARFIXX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AHNBXX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA10XX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcySpclEqpmtTotAmt"), PlcySpclEqpmtTotAmt)
//				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehBdyInjPrem"), VehBdyInjPrem)
//				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehPDPrem"), VehPDPrem)
//				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUMPrem"), VehUMPrem)
//				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehUIMBPrem"), VehUIMBPrem)
//				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehMPPrem"), VehMPPrem)
//				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompPrem"), VehCompPrem)
//				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnPrem"), VehClsnPrem)
//				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehClsnDed"), VehClsnDed)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehCompDed"), VehCompDed)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehSpclEqpmtPrem"), VehSpclEqpmtPrem)
//				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "VehTotPrem"), VehTotPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "NetWrtPrem","TextField"), NetWrtPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "CoverageDetails", "PlcyAutoDeadBenPrem","TextField"), PlcyAutoDeadBenPrem)
				.adjust(TestData.makeKeyPath("AA02AZ", "PaymentDetails", "PlcyTotFee","TextField"), PlcyTotFee),
				policyNumber);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	 }

	//	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_EndorsePolicy(String state) {
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		TestData endorsementTd = getTestSpecificTD("TestData_Endorsement");
		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
	}
	
//	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_RenewalImageGeneration(String state) {
		LocalDateTime renewImageGenDate=getTimePoints().getRenewImageGenerationDate(policyExpirationDate);	
		Log.info("Policy Renewal Image Generation Date" + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);	
	}
	
//	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
    public void TC04_RenewaPreviewGeneration(String state) {
		
		LocalDateTime renewPreviewGenDate=getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
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
	
//	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC05_RenewaOfferGeneration(String state) {
		LocalDateTime renewOfferGenDate=getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
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
	
//	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC06_RenewaOfferBillGeneration(String state) {
		LocalDateTime renewOfferBillGenDate=getTimePoints().getBillGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Bill Generation Date" + renewOfferBillGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
//		TODO verify the xml file AHREXX, AH35XX
	}

}
