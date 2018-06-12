package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyCreationBig;
import aaa.modules.regression.service.helper.dtoDxp.Address;
import aaa.modules.regression.service.helper.dtoDxp.PolicySummary;
import aaa.soap.aaaCSPolicyRate.com.exigenservices.Policy;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.eclipse.jetty.client.Origin;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static aaa.main.metadata.policy.AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.FIRST_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.LAST_NAME;

@StateList(states = Constants.States.KY)
public class TestPremiumBearingEndorsement extends AutoSSBaseTest {

    private LocalDateTime dd1;

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-5260")
    public void pas5260_testEndorsementRPBind(@Optional("KY") String state) {

        mainApp().open();
        TestData td = getPolicyTD("DataGather", "TestData").adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(),
                AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Monthly");



        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(td, VehicleTab.class, true);
        new VehicleTab().getAssetList().getAsset(AutoSSMetaData.VehicleTab.ADD_VEHICLE).click();
        new VehicleTab().getAssetList().getAsset(AutoSSMetaData.VehicleTab.USAGE).setValueContains("Pleasure");
        new VehicleTab().getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue("1B3BA46K4KF440231");
        new VehicleTab().submitTab();
        policy.getDefaultView().fillFromTo(td, FormsTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();


        String policyNumber = PolicySummaryPage.getPolicyNumber();

       // dd1 = PolicySummaryPage.getEffectiveDate().plusMonths(1);
       // TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillDueDate(dd1));

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        new BillingAccount().generateFutureStatement().perform();

        dd1 = BillingSummaryPage.getInstallmentDueDate(2);

        Dollar minDue = new Dollar(BillingSummaryPage.tableBillsStatements
                .getRowContains(BillingConstants.BillingBillsAndStatmentsTable.TYPE, BillingConstants.BillsAndStatementsType.BILL)
                .getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
        new BillingAccount().declinePayment().start();

        SearchPage.openPolicy(policyNumber);

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
        VehicleTab.tableVehicleList.removeRow(2);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE).setValueContains("No Coverage");
        new PremiumAndCoveragesTab().calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();

    }


    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-5260")
    public void pas5260_testNonPremiumBearingEndorsementBind(@Optional("KY") String state) {

       //new TestPolicyCreationBig().testPolicyCreationBig(state);

       mainApp().open();
       TestData td = getPolicyTD("DataGather", "TestData").adjust(TestData.makeKeyPath(PremiumAndCoveragesTab.class.getSimpleName(),
               AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), "Monthly");


        createCustomerIndividual();

        //add a second vehicle
        policy.initiate();
        policy.getDefaultView().fillUpTo(td, VehicleTab.class, true);
        new VehicleTab().getAssetList().getAsset(AutoSSMetaData.VehicleTab.ADD_VEHICLE).click();
        new VehicleTab().getAssetList().getAsset(AutoSSMetaData.VehicleTab.USAGE).setValueContains("Pleasure");
        new VehicleTab().getAssetList().getAsset(AutoSSMetaData.VehicleTab.VIN).setValue("1B3BA46K4KF440231");
        new VehicleTab().submitTab();
        policy.getDefaultView().fillFromTo(td, FormsTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        String policyNumber = PolicySummaryPage.getPolicyNumber();

     //   createPolicy(td);

        NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
        new BillingAccount().generateFutureStatement().perform();

        Dollar minDue = new Dollar(BillingSummaryPage.tableBillsStatements
                .getRowContains(BillingConstants.BillingBillsAndStatmentsTable.TYPE, BillingConstants.BillsAndStatementsType.BILL)
                .getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), minDue);
        new BillingAccount().declinePayment().start();


        SearchPage.openPolicy(policyNumber);

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
      //  new GeneralTab().getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION).setValue("1250 W Grove Pkwy");

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
       // new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE).setValueContains("No Coverage");
        //new PremiumAndCoveragesTab().calculatePremium();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();


    }


    }