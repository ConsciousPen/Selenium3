package aaa.modules.regression.document_fulfillment.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.billing.DeclineRecurringPaymentHelper;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum.EventName;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.waiters.Waiters;

import static aaa.main.enums.BillingConstants.*;
import static aaa.main.enums.DocGenEnum.Documents.*;
import static toolkit.verification.CustomAssertions.assertThat;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class PasDoc_OnlineBatch_Billing extends AutoSSBaseTest {
	
	private static final String GET_PAYMENT_NUMBER_BY_BILLING_ACCOUNT = "select paymentNumber from(\n"
			+ "select p.* --, bt.*,  ba.*,\n"
			+ " from Payment p\n"
			+ " join BillingTransaction bt on bt.paymentNumber = p.paymentNumber\n"
			+ " join BillingAccount ba on ba.id = bt.account_id\n"
			+ " and ba.accountnumber = '%s'\n"
			+ " order by p.CREATIONDATE desc\n"
			+ ")where rownum = 1";
	
	//private IBillingAccount billingAccount = new BillingAccount();
	private TestData tdBilling = testDataManager.billingAccount;
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario22(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();	
		TestData td_activeAutoPay = getPolicyTD().adjust(getTestSpecificTD("TestData_ActiveAutoPay").resolveLinks());
		TestData td_notActiveAutoPay = getPolicyTD().adjust(getTestSpecificTD("TestData_NotActiveAutoPay").resolveLinks());
		
		//Scenario 22a: AutoPay is NOT active. 
		//Generate Billing invoice manually ('Generate Future Statement' action on Billing tab)
		String policy1_notActiveAutoPay = createPolicy(td_notActiveAutoPay); 
		log.info("PAS DOC: Scenario 22a: Policy without AutoPay created: " + policy1_notActiveAutoPay);
		BillingSummaryPage.open();
		IBillingAccount billingAccount = new BillingAccount();
		billingAccount.generateFutureStatement().perform();
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy1_notActiveAutoPay, EventName.AUTO_STATEMENT, AHIBXX);

		//Scenario 22c: AutoPay is active. 
		//Generate Billing invoice manually ('Generate Future Statement' action on Billing tab)
		String policy_activeAutoPay = createPolicy(td_activeAutoPay);
		log.info("PAS DOC: Scenario 22c: Policy with AutoPay created: " + policy_activeAutoPay);
		BillingSummaryPage.open();
		billingAccount.generateFutureStatement().perform();
		PasDocImpl.verifyDocumentsGenerated(null, false, false, policy_activeAutoPay, AHIBXX);		
		
		//Scenario 22b: AutoPay is NOT active
		//Set time to DD1-20 and run aaaBillingInvoiceAsyncTaskJob to generate bill
		String policy2_notActiveAutoPay = createPolicy(td_notActiveAutoPay); 
		log.info("PAS DOC: Scenario 22b: Policy without AutoPay created: " + policy2_notActiveAutoPay);
		BillingSummaryPage.open();
		List<LocalDateTime> installmentDueDates = BillingHelper.getInstallmentDueDates();
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policy2_notActiveAutoPay);
		new BillingBillsAndStatementsVerifier().verifyBillGenerated(installmentDueDates.get(1), billGenDate, null, BillingHelper.DZERO);		
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy2_notActiveAutoPay, EventName.AUTO_STATEMENT, AHIBXX);		
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario23(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policy_activeAutoPay = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_ActiveAutoPay").resolveLinks()));
		log.info("PAS DOC: Scenario 23: Policy with AutoPay created: " + policy_activeAutoPay);
        BillingSummaryPage.open();
        List<LocalDateTime> installmentDueDates = BillingHelper.getInstallmentDueDates();
        Dollar minDue = new Dollar(BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(1)));              
        //On Billing tab: add payment manually
        IBillingAccount billingAccount = new BillingAccount();
        billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_CC_Visa_Payment"), minDue);
        new BillingPaymentsAndTransactionsVerifier().verifyManualPaymentAccepted(DateTimeUtils.getCurrentDateTime(), minDue.negate());        
        PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_activeAutoPay, EventName.PAYMENT, AH35XX);
	}
	
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario25(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policy_activeAutoPay = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData_ActiveAutoPay").resolveLinks()));
		log.info("PAS DOC: Scenario 25: Policy with AutoPay created: " + policy_activeAutoPay);
		BillingSummaryPage.open();
		String billingAccountNumber = BillingSummaryPage.tableBillingGeneralInformation.getRow(1).getCell(BillingGeneralInformationTable.ID).getValue();
		List<LocalDateTime> installmentDueDates = BillingHelper.getInstallmentDueDates();
		
		//Set time to DD1-20 and run aaaBillingInvoiceAsyncTaskJob to generate bill
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);
		
		//Set time to DD1 and run aaaRecurringPaymentsProcessingJob to make a payment
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(billDueDate);
		JobUtils.executeJob(Jobs.aaaRecurringPaymentsProcessingJob);
		mainApp().open();
		SearchPage.openBilling(policy_activeAutoPay);
		Dollar minDue = new Dollar(BillingHelper.getBillCellValue(installmentDueDates.get(1), BillingBillsAndStatmentsTable.MINIMUM_DUE));
		new BillingPaymentsAndTransactionsVerifier().verifyAutoPaymentGenerated(DateTimeUtils.getCurrentDateTime(), minDue.negate());
		String paymentAmount = BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue();
		String paymentAmountPlain = new Dollar(paymentAmount).multiply(-1).toPlaingString();
		
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
		String paymentNumber = DBService.get().getValue(String.format(GET_PAYMENT_NUMBER_BY_BILLING_ACCOUNT, billingAccountNumber)).get();
		File declineRecurringPaymentFile = DeclineRecurringPaymentHelper.createFile(getState(), policy_activeAutoPay, paymentNumber, paymentAmountPlain);
		DeclineRecurringPaymentHelper.copyFileToServer(declineRecurringPaymentFile);
		JobUtils.executeJob(Jobs.aaaRecurringPaymentsResponseProcessAsyncJob, true);
		Waiters.SLEEP(5000).go();
		
		mainApp().open();
		SearchPage.openBilling(policy_activeAutoPay);
		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.ADJUSTMENT)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.PAYMENT_DECLINED)
				.setAmount(new Dollar(paymentAmountPlain)).setStatus(PaymentsAndOtherTransactionStatus.APPLIED).verifyPresent();
		new BillingPaymentsAndTransactionsVerifier().setType(PaymentsAndOtherTransactionType.PAYMENT)
				.setSubtypeReason(PaymentsAndOtherTransactionSubtypeReason.RECURRING_PAYMENT)
				.setAmount(new Dollar(paymentAmount)).setStatus(PaymentsAndOtherTransactionStatus.DECLINED).verifyPresent();		
		IBillingAccount billingAccount = new BillingAccount();
		billingAccount.update().start();
		assertThat(new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.ACTIVATE_AUTOPAY)).hasValue(false);
		assertThat(new UpdateBillingAccountActionTab().getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION)).hasValue("");
		UpdateBillingAccountActionTab.buttonCancel.click();
		
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_activeAutoPay, EventName.AUTO_PAY_METHOD_REMOVED, _60_5004);
	}
	
	
	@Parameters({"state"})
	@StateList(states = States.AZ)	
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario65(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();	
		TestData td_activeAutoPay = getPolicyTD().adjust(getTestSpecificTD("TestData_ActiveAutoPay").resolveLinks());
		TestData td_notActiveAutoPay = getPolicyTD().adjust(getTestSpecificTD("TestData_NotActiveAutoPayWithCC").resolveLinks());
		
		//Scenario 65a:  AutoPay is NOT active, go to Billing and activate AutoPay
		String policy_notActiveAutoPay = createPolicy(td_notActiveAutoPay);
		log.info("PAS DOC: Scenario 65a: Policy without AutoPay (with Credit Card) created: " + policy_notActiveAutoPay);
		BillingSummaryPage.open();
		IBillingAccount billingAccount = new BillingAccount();
        billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_EnableAutopay"));   
        PasDocImpl.verifyDocumentsGenerated(null, true, false, policy_notActiveAutoPay, EventName.AUTO_PAY_METNOD_CHANGED, AH35XX);
        
        //Scenario 65b: AutoPay is active (Payment Method = Visa)
        //1. add payment method e.g. Master Card and select it for AutoPay
		String policy1_activeAutoPay = createPolicy(td_activeAutoPay);
        log.info("PAS DOC: Scenario 65b: Policy 1 with AutoPay created: " + policy1_activeAutoPay);
        BillingSummaryPage.open();
        billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_AddAutopayWithMasterCard")); 
        PasDocImpl.verifyDocumentsGenerated(null, true, false, policy1_activeAutoPay, EventName.AUTO_PAY_METNOD_CHANGED, AH35XX);
        
        //2. add payment method e.g. Checking/Savings and select it for AutoPay
        String policy2_activeAutoPay = createPolicy(td_activeAutoPay);
        log.info("PAS DOC: Scenario 65b: Policy 2 with AutoPay created: " + policy2_activeAutoPay);
        BillingSummaryPage.open();
        billingAccount.update().perform(tdBilling.getTestData("Update", "TestData_AddAutopayWithEFT")); 
        PasDocImpl.verifyDocumentsGenerated(null, true, false, policy2_activeAutoPay, EventName.AUTO_PAY_METNOD_CHANGED, AH35XX);
	}

}
