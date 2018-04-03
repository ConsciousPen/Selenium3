package aaa.modules.regression.sales.home_ss.ho3.functional;

import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.INSURANCE_SCORE_REPORT;
import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.InsuranceScoreReportRow.CUSTOMER_AGREEMENT;
import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.InsuranceScoreReportRow.ORDER_INSURANCE_SCORE;
import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.SALES_AGENT_AGREEMENT;
import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.modules.regression.sales.home_ss.helper.HelperRevisedHomeTierPA;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;

public class TestDisableReorderReport extends HomeSSHO3BaseTest {
    private ReportsTab reportTab = new ReportsTab();
    private ApplicantTab applicant = new ApplicantTab();

    /**
     * @author Igor Garkusha
     * @name PA Revised Home Tier : Disable Reorder Report at Mid Term
     * @scenario
     * 1.  Create quote
     * 2.  check that user able to Override credit score and re-order report
     * 3.  complete policy creation
     * 4.  create new Endorsement
     * 5.  check that override credit score is disabled
     * 6.  check that user can't re-order report
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6795")
    public void pas6795_disableReorderReportEndorsement(@Optional("PA") String state) {

        new HelperRevisedHomeTierPA().verifyAlgoDate();
        mainApp().open();
        createPolicyVerifyOverrideLink();

        // Initiate Endorsement and verify Override Link
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        assertThat(reportTab.tblInsuranceScoreOverride.getRow(1).getCell(6).controls.links.getFirst().isPresent()).isFalse();

        // Navigate to Applicant tab and add another named insured
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        new ApplicantTab().fillTab(getTestSpecificTD("TestData"));

        // Navigate to Reports tab; verify 'Reorder Report' radio and 'Override Score' link
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportTab.tblInsuranceScoreReport.getRow(2).getCell(ORDER_INSURANCE_SCORE.getLabel()).controls.radioGroups.get(1).setValue("Yes");
        reportTab.getAssetList().getAsset(INSURANCE_SCORE_REPORT.getLabel(), FillableTable.class).getAsset(CUSTOMER_AGREEMENT.getLabel(), RadioGroup.class).setValue("Customer agrees");
        reportTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");
        assertThat(reportTab.tblInsuranceScoreOverride.getRow(1).getCell(6).controls.links.getFirst().isPresent()).isFalse();
        assertThat(reportTab.tblInsuranceScoreReport.getRow(2).getCell("Report").controls.links.getFirst().isPresent()).isFalse();

    }

    /**
     * @author Josh Carpenter
     * @name PA Revised Home Tier : Disable Reorder Report at Renewal
     * @scenario
     * 1.  Create PA HO3 quote
     * 2.  check that user able to Override credit score and re-order report
     * 3.  complete policy creation
     * 4.  Initiate Renewal
     * 5.  Navigate to Reports tab
     * 5.  check that override credit score is disabled
     * 6.  check that user can't re-order report
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6827")
    public void pas6827_disableReorderReportRenewal(@Optional("PA") String state) {

        new HelperRevisedHomeTierPA().verifyAlgoDate();
        mainApp().open();
        createPolicyVerifyOverrideLink();

        policy.renew().perform(new SimpleDataProvider());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        assertThat(reportTab.tblInsuranceScoreOverride.getRow(1).getCell(6).controls.links.getFirst().isPresent()).isFalse();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        applicant.getNamedInsuredAssetList().getAsset("First name", TextBox.class).setValue("Hello");
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        assertThat(reportTab.tblInsuranceScoreReport.getRow(1).getCell("Report").controls.links.getFirst().isPresent()).isFalse();

    }

    /**
     * 1. Verifies Algo Date
     * 2. Opens PAS and creates customer
     * 3. Creates PA HO3 policy
     * 4. Verifies Override Score link is enabled
     * 5. Finishes creating policy
     */
    private void createPolicyVerifyOverrideLink() {
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), ReportsTab.class, true);
        assertThat(reportTab.tblInsuranceScoreOverride.getRow(1).getCell(6).controls.links.getFirst().isEnabled()).isTrue();
        reportTab.submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), PropertyInfoTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
    }

}
