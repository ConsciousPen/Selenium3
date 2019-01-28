package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyCreationBig;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


@StateList(states = Constants.States.KY)
public class TestReducedPremiumEndorsementKY extends AutoSSBaseTest {

    /**
     * @author Dakota Berg
     * @name Agent cannot bind endorsement due to error message: "Cannot transfer payment"
     * @scenario
     * 1. Create a KY Auto SS policy with two (2) vehicles and monthly installments
     * 2. Set the system date to the first installment due date and run the 'aaaBillingInvoiceAsyncJob'
     * 3. Accept the minimum due payment for the first installment
     * 4. Decline the payment
     * 5. Create an endorsement
     * 5.1 Remove one of the vehicles
     * 5.2 Lower the Collision Deductible on the Premium and Coverages tab
     * 5.3 Calculate the premium then bind the endorsement
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-5260")
    public void pas5260_testEndorsementRPBind(@Optional("KY") String state) {

        //Open the application and adjust the yaml file so that the payment plan is Quarterly; should have two vehicles
        mainApp().open();
        List<TestData> tdVehicles = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(VehicleTab.class.getSimpleName());
        TestData td = getPolicyTD().adjust(VehicleTab.class.getSimpleName(), tdVehicles)
                .adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Monthly");

        //Create a customer and policy
        createCustomerIndividual();
        createPolicy(td);
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        //Change the VDM date to the next bill due date and generate billing invoice
        LocalDateTime dd1 = PolicySummaryPage.getEffectiveDate().plusMonths(1);
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(dd1));
        JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);

        mainApp().open();

        //Get the policy number and go to the Billing tab
        SearchPage.openPolicy(policyNumber);
        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

        //Verify that there is 1 row generated under Billing Statements, and that it is equal to the VDM date
        assertThat(BillingSummaryPage.tableBillsStatements.getRows().size()).isEqualTo(1);
        assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell("Due Date").getValue()).isEqualTo(dd1.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

        //Pay the minimum due, accept the payment, then decline the installment
        Dollar minDue = new Dollar(BillingSummaryPage.tableBillsStatements
                .getRowContains(BillingConstants.BillingBillsAndStatmentsTable.TYPE, BillingConstants.BillsAndStatementsType.BILL)
                .getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
        new BillingAccount().declinePayment().start();

        //Initiate an endorsement for the policy
        SearchPage.openPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        //Remove a vehicle and reduce premium, calculate the premium and bind the endorsement
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
        VehicleTab.tableVehicleList.removeRow(2);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE).setValueContains("No Coverage");
        new PremiumAndCoveragesTab().calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();

        //Verifies that no error messages display
        assertThat(PolicySummaryPage.labelPolicyStatus).isPresent();

    }

}