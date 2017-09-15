package aaa.modules.docgen.auto_ss;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.BillingInstallmentScheduleTable;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.BillingConstants.BillingGeneralInformationTable;
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
	
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void TC01_CreatePolicy(){
		 mainApp().open();

	     createCustomerIndividual();
	     TestData tdpolicy= getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
	     createPolicy(tdpolicy);
        
	     PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	     policyNumber=PolicySummaryPage.labelPolicyNumber.getValue();
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
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void TC02_GenerateBillingInvoice(){
		LocalDateTime billingGenerationDate=getTimePoints().getBillGenerationDate(installmentDD1);
		TimeSetterUtil.getInstance().nextPhase(billingGenerationDate);
		log.info("Installment Generatetion Date" + billingGenerationDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		
//	    mainApp().open();
//	    SearchPage.openBilling(policyNumber);
//	    String PlcyDueDt=TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingBillsAndStatmentsTable.DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY)
//	    		.atZone(ZoneId.of(ZoneId.SHORT_IDS.get("EAT"))).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T00:00:00.000'XXX"));
//	    String InstlDueDt=BillingSummaryPage.getInstallmentDueDate(2).atZone(ZoneId.of(ZoneId.SHORT_IDS.get("EAT"))).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T00:00:00.000'XXX"));
//	    String CurRnwlAmt=BillingSummaryPage.getInstallmentAmount(2).toString().replace("$", "");
//	    String instlFee = BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue().toString().replace("$", "");
//	    String TotNwCrgAmt=BillingSummaryPage.tableInstallmentSchedule.getRow(2).getCell(BillingInstallmentScheduleTable.BILLED_AMOUNT).toString().replace("$", "");
//	    String PlcyPayMinAmt=BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingBillsAndStatmentsTable.MINIMUM_DUE).toString().replace("$", "");
//	    String PlcyPayFullAmt=BillingSummaryPage.getTotalDue().divide(5).toString().replace("$", "");
//TODO get xml file
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
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void TC03_GenerateCancelNotice(){
    	
    	LocalDateTime cancelNoticeDate=getTimePoints().getCancellationNoticeDate(installmentDD1);
       	log.info("Cancel Notice Generatetion Date" + cancelNoticeDate);
    	TimeSetterUtil.getInstance().nextPhase(cancelNoticeDate);
    	JobUtils.executeJob(Jobs.aaaCancellationNoticeAsyncJob);
    	JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

//    	 mainApp().open();
//    	 SearchPage.openPolicy(policyNumber);
//    	 PolicySummaryPage.labelCancelNotice.verify.present();
//    	 BillingSummaryPage.open();
//    	 String PlcyPayMinAmt=BillingSummaryPage.getMinimumDue().toString().replace("$", "");
//    	 String PlcyPayFullAmt=BillingSummaryPage.getTotalDue().toString().replace("$", "");
//    	 String PlcyDueDtCancelNotice=TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingBillsAndStatmentsTable.DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY)
// 	    		.atZone(ZoneId.of(ZoneId.SHORT_IDS.get("EAT"))).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T00:00:00.000'XXX"));
    	//TODO get xml file
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
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void TC04_GenerateCancellation(){
    	LocalDateTime cancelNoticeDate=getTimePoints().getCancellationNoticeDate(installmentDD1);
    	LocalDateTime cancellationDate=getTimePoints().getCancellationNoticeDate(cancelNoticeDate);
    	log.info("Cancellation Generatetion Date" + cancellationDate);
    	TimeSetterUtil.getInstance().nextPhase(cancellationDate);   	
    	JobUtils.executeJob(Jobs.aaaCancellationConfirmationAsyncJob);
    	JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

//    	mainApp().open();
//   	    SearchPage.openPolicy(policyNumber);
//   	    PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
//   	    BillingSummaryPage.open();
//   	    String CancEffDt=TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingBillsAndStatmentsTable.DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY)
// 	    		.atZone(ZoneId.of(ZoneId.SHORT_IDS.get("EAT"))).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T00:00:00.000'XXX"));
    	//TODO get xml file
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
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void TC05_ReinstatementPolicy(){
    	mainApp().open();
     	SearchPage.openPolicy(policyNumber);
     	policy.reinstate().perform(getTestSpecificTD("TestData_Plus13Days"));
     	PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
 
    	JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
    	
//    	BillingSummaryPage.open();
//    	String CancEffDt=TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingBillsAndStatmentsTable.DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY)
// 	    		.atZone(ZoneId.of(ZoneId.SHORT_IDS.get("EAT"))).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T00:00:00.000'XXX"));
//    	String PlcyPayFullAmtRen=BillingSummaryPage.getTotalDue().toString().replace("$", "");
//    	String ReinstmtFee=BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue().toString().replace("$", "");
    	//TODO get xml file
    }
}
