package aaa.modules.docgen.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;

import java.time.LocalDateTime;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.BillingInstallmentScheduleTable;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.PolicyConstants.PolicyGeneralInformationTable;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;

/**
 * @author Lina Li
 * @name Test AutoSS Billing documents
 * @scenario
 * 1. Create Customer
 * 2. Create Auto policy
 * 3. Verify policy status is active
 * @details
 */
public class TestAZScenario1 extends AutoSSBaseTest{
	protected String policyNumber;
	protected LocalDateTime installmentDD1;
	protected String policyEffectiveDate;
	protected String policyExpirationDate;
	protected String plcyDueDt;
	protected String curRnwlAmt;
	protected String instlFee;
	protected String totNwCrgAmt;
	protected String plcyPayMinAmt;
	protected String plcyPayFullAmt;
	protected String cancEffDt;
	protected String reinstmtFee;
	protected String reCalcTotFee;
	protected String reinEffDt;
	protected String priorReinEffDt;

	
	
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void TC01_CreatePolicy(@Optional("") String state) {
		mainApp().open();

	     createCustomerIndividual();
	     TestData tdpolicy = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
	     createPolicy(tdpolicy);
        
	     PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	     policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
	     policyEffectiveDate = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance().parse(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyGeneralInformationTable.EFFECTIVE_DATE).getValue(),DateTimeUtils.MM_DD_YYYY));
	     policyExpirationDate = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance().parse(PolicySummaryPage.tableGeneralInformation.getRow(1).getCell(PolicyGeneralInformationTable.EXPIRATION_DATE).getValue(),DateTimeUtils.MM_DD_YYYY));
	    
	     log.info("Original Policy #" + policyNumber);
	     
	     BillingSummaryPage.open();
	     installmentDD1=BillingSummaryPage.getInstallmentDueDate(2);
	}
    
    /**
     * @author Lina Li
     * @name Test AutoSS Billing documents
     * @scenario
     * 1. At DD-20, run 'aaaBillingInvoiceAsyncTaskJob' batch job
     * 2. Run 'aaaDocGenAsyncBatchJob'
     * 3. Generate the form AHIBXX
     * @details
     */
	
	@Parameters({"state"})
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void TC02_GenerateBillingInvoice(@Optional("") String state){
    	CustomAssert.enableSoftMode();
		LocalDateTime billingGenerationDate = getTimePoints().getBillGenerationDate(installmentDD1);
		TimeSetterUtil.getInstance().nextPhase(billingGenerationDate);
		log.info("Installment Generatetion Date" + billingGenerationDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		
	    mainApp().open();    
	    SearchPage.openBilling(policyNumber);
	    plcyDueDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillsStatements.getRow(BillingBillsAndStatmentsTable.TYPE,"Bill").getCell(BillingBillsAndStatmentsTable.DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY));
	    curRnwlAmt = formatValue(BillingSummaryPage.getInstallmentAmount(2).toString());
	    instlFee = formatValue(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,"Non EFT Installment Fee").getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
	    Dollar _instlFee = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,"Non EFT Installment Fee").getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());     
	    totNwCrgAmt = formatValue(BillingSummaryPage.tableInstallmentSchedule.getRow(2).getCell(BillingInstallmentScheduleTable.BILLED_AMOUNT).getValue());
	    plcyPayMinAmt = formatValue(BillingSummaryPage.getMinimumDue().toString());
	    plcyPayFullAmt = formatValue(BillingSummaryPage.getTotalDue().subtract(_instlFee).toString());
	    		
	    DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, AHIBXX).verify.mapping(getTestSpecificTD("TestData_AHIBXX_Verification")
				.adjust(TestData.makeKeyPath("AHIBXX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AHIBXX", "form", "PlcyEffDt", "DateTimeField"), policyEffectiveDate)
				.adjust(TestData.makeKeyPath("AHIBXX", "form", "PlcyExprDt", "DateTimeField"), policyExpirationDate)
				.adjust(TestData.makeKeyPath("AHIBXX", "PaymentDetails", "CurRnwlAmt", "TextField"), curRnwlAmt)
				.adjust(TestData.makeKeyPath("AHIBXX", "PaymentDetails", "InstlFee", "TextField"), instlFee)
				.adjust(TestData.makeKeyPath("AHIBXX", "PaymentDetails", "TotNwCrgAmt", "TextField"), totNwCrgAmt)
				.adjust(TestData.makeKeyPath("AHIBXX", "PaymentDetails", "PlcyPayMinAmt", "TextField"), plcyPayMinAmt)
				.adjust(TestData.makeKeyPath("AHIBXX", "PaymentDetails", "PlcyPayFullAmt", "TextField"), plcyPayFullAmt)
				.adjust(TestData.makeKeyPath("AHIBXX", "PaymentDetails", "PlcyDueDt", "DateTimeField"), plcyDueDt),
				policyNumber);
	    
	    CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
    }
    
    /**
     * @author Lina Li
     * @name Test AutoSS Billing documents
     * @scenario
     * 1. At DD1+8 days, run 'aaaCancellationNoticeAsyncJob' batch job
     * 2. Run 'aaaDocGenAsyncBatchJob'
     * 3. Generate the form AH34XX
     * @details
     */
	
	@Parameters({"state"})
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void TC03_GenerateCancelNotice(@Optional("") String state) {
    	CustomAssert.enableSoftMode();
    	
    	LocalDateTime cancelNoticeDate = getTimePoints().getCancellationNoticeDate(installmentDD1);
       	log.info("Cancel Notice Generatetion Date" + cancelNoticeDate);
    	TimeSetterUtil.getInstance().nextPhase(cancelNoticeDate);
    	JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
    	JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

    	 mainApp().open();
    	 SearchPage.openPolicy(policyNumber);
    	 PolicySummaryPage.labelCancelNotice.verify.present();
    	 BillingSummaryPage.open();
    	 plcyPayMinAmt = formatValue(BillingSummaryPage.getMinimumDue().toString());
    	 plcyPayFullAmt = formatValue(BillingSummaryPage.getTotalDue().toString().replace("$", ""));
    	 plcyDueDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillsStatements.getRow(BillingBillsAndStatmentsTable.TYPE,"Cancellation Notice").getCell(BillingBillsAndStatmentsTable.DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY));
    			 
    	 DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, AH34XX).verify.mapping(getTestSpecificTD("TestData_AH34XX_Verification")
 				.adjust(TestData.makeKeyPath("AH34XX", "form", "PlcyNum", "TextField"), policyNumber)
 				.adjust(TestData.makeKeyPath("AH34XX", "form", "PlcyEffDt", "DateTimeField"), policyEffectiveDate)
 				.adjust(TestData.makeKeyPath("AH34XX", "form", "PlcyExprDt", "DateTimeField"), policyExpirationDate)
 				.adjust(TestData.makeKeyPath("AH34XX", "PaymentDetails", "PlcyPayMinAmt", "TextField"), plcyPayMinAmt)
 				.adjust(TestData.makeKeyPath("AH34XX", "PaymentDetails", "PlcyPayFullAmt", "TextField"), plcyPayFullAmt)
 				.adjust(TestData.makeKeyPath("AH34XX", "PaymentDetails", "PlcyDueDt", "DateTimeField"), plcyDueDt),
 				policyNumber);
    	CustomAssert.disableSoftMode();
 		CustomAssert.assertAll();
    	
    }
    
    /**
     * @author Lina Li
     * @name Test AutoSS Billing documents
     * @scenario
     * 1. At Cancel Notice Date+8 days, run 'aaaCancellationNoticeAsyncJob' batch job
     * 2. Run 'aaaDocGenAsyncBatchJob'
     * 3. Generate the form AH67XX
     * @details
     */
	
	@Parameters({"state"})
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void TC04_GenerateCancellation(@Optional("") String state){
    	CustomAssert.enableSoftMode();
    	
    	LocalDateTime cancelNoticeDate = getTimePoints().getCancellationNoticeDate(installmentDD1);
    	LocalDateTime cancellationDate = getTimePoints().getCancellationNoticeDate(cancelNoticeDate);
    	log.info("Cancellation Generatetion Date" + cancellationDate);
    	TimeSetterUtil.getInstance().nextPhase(cancellationDate);   	
    	JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
    	JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

    	mainApp().open();
   	    SearchPage.openPolicy(policyNumber);
   	    PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
   	    BillingSummaryPage.open();
   	    cancEffDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillsStatements.getRow(BillingBillsAndStatmentsTable.TYPE,"Cancellation").getCell(BillingBillsAndStatmentsTable.DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY));
   	    
   	    
   	 DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, AH67XX).verify.mapping(getTestSpecificTD("TestData_AH67XX_Verification")
				.adjust(TestData.makeKeyPath("AH67XX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AH67XX", "form", "PlcyEffDt", "DateTimeField"), policyEffectiveDate)
				.adjust(TestData.makeKeyPath("AH67XX", "form", "PlcyExprDt", "DateTimeField"), policyExpirationDate)
				.adjust(TestData.makeKeyPath("AH67XX", "form", "CancEffDt", "DateTimeField"), cancEffDt),
				policyNumber);
   	 
 	 CustomAssert.disableSoftMode();
     CustomAssert.assertAll();		
    	
    }
    
    /**
     * @author Lina Li
     * @name Test AutoSS Billing documents
     * @scenario
     * 1. At Cancellation date +13 days, manually reinstatement the policy
     * 2. Run 'aaaDocGenAsyncBatchJob'
     * 3. Generate the form AH62XX
     * @details
     */
	
	@Parameters({"state"})
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void TC05_ReinstatementPolicy(@Optional("") String state){
    	CustomAssert.enableSoftMode();
    	
    	mainApp().open();
     	SearchPage.openPolicy(policyNumber);
     	policy.reinstate().perform(getTestSpecificTD("TestData_Plus13Days"));
     	PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
 
    	JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
    	
    	BillingSummaryPage.open();
 
    	plcyPayFullAmt = formatValue(BillingSummaryPage.getTotalDue().toString());
    	reinstmtFee = BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,"Reinstatement Fee").getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue().replace("$", "").replace(".00", "");
    	reinEffDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,"Reinstatement").getCell(BillingPaymentsAndOtherTransactionsTable.EFF_DATE).getValue(), DateTimeUtils.MM_DD_YYYY));
    	priorReinEffDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,"Reinstatement").getCell(BillingPaymentsAndOtherTransactionsTable.EFF_DATE).getValue(), DateTimeUtils.MM_DD_YYYY).minusDays(1));
    	
    	
    	Dollar fee=new Dollar(0);
    	for (int i=1;i<=BillingSummaryPage.tablePaymentsOtherTransactions.getRowsCount();i++){
    		if (BillingSummaryPage.tablePaymentsOtherTransactions.getRow(i).getCell(BillingPaymentsAndOtherTransactionsTable.TYPE).getValue().equals("Fee")){
    			
    			fee= fee.add(new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(i).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue()));	   			
    		}
    	}
    	
    	reCalcTotFee=fee.toString().replace("$", "");

    	 DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, AH62XX).verify.mapping(getTestSpecificTD("TestData_AH62XX_Verification")
 				.adjust(TestData.makeKeyPath("AH62XX", "form", "PlcyNum", "TextField"), policyNumber)
 				.adjust(TestData.makeKeyPath("AH62XX", "form", "PlcyEffDt", "DateTimeField"), policyEffectiveDate)
 				.adjust(TestData.makeKeyPath("AH62XX", "form", "PlcyExprDt", "DateTimeField"), policyExpirationDate)
 				.adjust(TestData.makeKeyPath("AH62XX", "form", "ReinEffDt", "DateTimeField"), reinEffDt)
 				.adjust(TestData.makeKeyPath("AH62XX", "form", "PriorReinEffDt", "DateTimeField"), priorReinEffDt)
 				.adjust(TestData.makeKeyPath("AH62XX", "form", "ReinstmtFee", "TextField"), reinstmtFee)
 				.adjust(TestData.makeKeyPath("AH62XX", "form", "ReCalcTotFee", "TextField"), reCalcTotFee)
 				.adjust(TestData.makeKeyPath("AH62XX", "form", "CancEffDt", "DateTimeField"), cancEffDt)
 				.adjust(TestData.makeKeyPath("AH62XX", "PaymentDetails", "PlcyPayFullAmt", "TextField"), plcyPayFullAmt),
 				policyNumber);
    	 
    	 CustomAssert.disableSoftMode();
         CustomAssert.assertAll();	
    }

	private String formatValue(String value) {
		return new Dollar(value.replace("\n", "")).toString().replace("$", "").replace(",", "");
	}
}


