package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.PolicyHelper;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TestCappingDetailsChecker extends AutoSSBaseTest {

    private final ErrorTab errorTab = new ErrorTab();

    /**
     * * @author Sarunas Jaraminas
     *
     *@name Test Capping Configuration for ID state
     *@scenario
     *1. Create customer.
     *2. Create Auto SS ID Quote.
     *3. Initiate Endorsement and add 3 accidents
     *4. Initiate first Renewal.
     *5. Select "Data Gathering" mode and Calculate Premium.
     *6. Check Capping details and Bind the policy.
     *7. Make the payment.
     *8. Initiate second Renewal.
     *9. Select "Data Gathering" mode and Calculate Premium.
     *10. Check Capping details
     *@details
     */

    private String policyNumber;
    private LocalDateTime policyExpirationDate;
    private LocalDateTime policyEffectiveDate;

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10809")
    public void pas10809_CwRefreshAndCappingConfiguration(@Optional("ID") String state) {

        mainApp().open();
        createCustomerIndividual();
        policyNumber = createPolicy(getPolicyTD());

        policyExpirationDate = PolicySummaryPage.getExpirationDate();
        policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

        initiateEndorsement();
        preconditionToDoFirstRenewal();
        initiateRenewal();
        //Capping functionality verification for first the renewal
        verifyCappingFunctionality();
        bindRenewalPolicy();
        billingPaymentAcception();
        preconditionToDoSecondRenewal();
        initiateRenewal();
        //Capping functionality verification for second the renewal
        verifyCappingFunctionality();

    }

    private void verifyCappingFunctionality() {

        String ceilingCap =  PolicyHelper.getCeilingByRegularPolicyNumber(policyNumber);
        String floorCap =  PolicyHelper.getFloorByRegularPolicyNumber(policyNumber);

        //View Capping Details
        PremiumAndCoveragesTab.buttonViewCappingDetails.click();
        String renewalTermPremiumOld = PremiumAndCoveragesTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.RENEWAL_TERM_PREMIUM_OLD_RATER);
        String calculatedTermPremium = PremiumAndCoveragesTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CALCULATED_TERM_PREMIUM);

        //Check Capping Details
//        assertThat(PremiumAndCoveragesTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.APPLIED_CAPPING_FACTOR)).isEqualTo(PolicyHelper.calculateCeilingOrFloorCap(renewalTermPremiumOld, calculatedTermPremium, ceilingCap));
        assertThat(PremiumAndCoveragesTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.CEILING_CAP)).isEqualTo(String.format("%s.0%%", ceilingCap));
        assertThat(PremiumAndCoveragesTab.tableCappedPolicyPremium.getValueByKey(PolicyConstants.ViewCappingDetailsTable.FLOOR_CAP)).isEqualTo(String.format("%s.0%%", floorCap));

        PremiumAndCoveragesTab.buttonReturnToPremiumAndCoverages.click();
    }

    private void initiateEndorsement() {

        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDate.plusDays(5));
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        TestData tdEndorsement = getTestSpecificTD("TestData_Endorsement");
        policy.createEndorsement(tdEndorsement);
    }

    private void initiateRenewal() {

        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.calculatePremium();
    }

    private void bindRenewalPolicy() {

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
        errorTab.overrideErrors(ErrorEnum.Errors.ERROR_200104);
        errorTab.override();
        new DocumentsAndBindTab().submitTab();
    }

    private  void preconditionToDoFirstRenewal(){

        LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        HttpStub.executeAllBatches();
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
    }

    private void  preconditionToDoSecondRenewal() {

        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
    }

    private void billingPaymentAcception() {

        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policyExpirationDate = PolicySummaryPage.getExpirationDate();

        BillingSummaryPage.open();
        Dollar totDue = new Dollar(BillingSummaryPage.tableBillingAccountPolicies
                .getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM, policyNumber)
                .getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount
                .getTestData("AcceptPayment", "TestData_Cash"), totDue);
    }
}