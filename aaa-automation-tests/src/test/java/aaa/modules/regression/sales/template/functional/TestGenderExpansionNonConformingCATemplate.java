package aaa.modules.regression.sales.template.functional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ca.choice.TestPolicyCreationBig;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;

public class TestGenderExpansionNonConformingCATemplate extends PolicyBaseTest {

    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    private GeneralTab generalTab = new GeneralTab();
    private DriverTab driverTab = new DriverTab();
    private PurchaseTab purchaseTab = new PurchaseTab();
    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private ErrorTab errorTab = new ErrorTab();

    protected void pas23040_ValidateGenderExpansionNonConformingNB() {

        String generalTabSimpleName = CustomerMetaData.GeneralTab.class.getSimpleName();
        TestData customerTd = getCustomerIndividualTD("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(generalTabSimpleName, CustomerMetaData.GeneralTab.GENDER.getLabel()), "X")
                .adjust(TestData.makeKeyPath(generalTabSimpleName, CustomerMetaData.GeneralTab.SALUTATION.getLabel()), "Mx");

        mainApp().open();
        createCustomerIndividual(customerTd);
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(),GeneralTab.class, true);

        // Validate PAS-24421
        List<String> titleOptions = generalTab.getNamedInsuredInfoAssetList().getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.TITLE).getAllValues();
        titleOptions.remove("Unknown/No Preference");
        List<String> sortedTitleOptions = new ArrayList<>(titleOptions);
        sortedTitleOptions.sort(String.CASE_INSENSITIVE_ORDER);
        assertThat(titleOptions).isEqualTo(sortedTitleOptions);

        generalTab.submitTab();
        driverTab.fillTab(getPolicyTD());
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.GENDER).getValue()).isEqualTo("X");
        driverTab.submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), MembershipTab.class, PremiumAndCoveragesTab.class, true);
        PremiumAndCoveragesTab.RatingDetailsView.open();
        assertThat(PremiumAndCoveragesTab.tableRatingDetailsDrivers.getRow(1, "Gender").getCell(2).getValue()).isEqualTo("X");
        PremiumAndCoveragesTab.RatingDetailsView.close();
        premiumAndCoveragesTab.submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), DriverActivityReportsTab.class, PurchaseTab.class, true);
        PurchaseTab.btnApplyPayment.click();
        PurchaseTab.confirmPurchase.confirm();
        TestData tdSurvey = DataProviderFactory.dataOf(PurchaseMetaData.PurchaseTab.ComunityServiceSurveyPromt.class.getSimpleName(), DataProviderFactory.dataOf(
                PurchaseMetaData.PurchaseTab.ComunityServiceSurveyPromt.GENDER.getLabel(), "X",
                PurchaseMetaData.PurchaseTab.ComunityServiceSurveyPromt.RACE_OF_ORIGIN.getLabel(), "index=1"));
        purchaseTab.dialogComunityServiceSurveyPromt.fill(tdSurvey);
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(1).getCell("Gender").getValue()).as("Gender should be displayed - X").isEqualTo("X");

    }

    protected void pas23040_ValidateGenderExpansionNonConformingEndTx() {

        TestData td = getPolicyTD();

        openAppAndCreatePolicy(td);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        driverTab.fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), getDriverTd()));
        premiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.RatingDetailsView.open();
        assertThat(premiumAndCoveragesTab.getRatingDetailsDriversData().get(1).getValue("Gender")).isEqualTo("X");

        bindAndValidateEndorsement(td);
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Gender").getValue()).as("Gender should be displayed - X").isEqualTo("X");

    }

    protected void pas23040_ValidateGenderExpansionNonConformingEnd1Tx() {

        TestData td = getPolicyTD();
        openAppAndCreatePolicy(td);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.GENDER).setValue("X");
        premiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.RatingDetailsView.open();
        assertThat(PremiumAndCoveragesTab.tableRatingDetailsDrivers.getRow(1, "Gender").getCell(3).getValue()).isEqualTo("X");

        bindAndValidateEndorsement(td);
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(1).getCell("Gender").getValue()).as("Gender should be displayed - X").isEqualTo("X");

    }

    protected void pas23040_ValidateGenderExpansionNonConformingRenewal() {

        String policyNumber = openAppAndCreatePolicy();
        policy.renew().start();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.GENDER).setValue("X");
        premiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.RatingDetailsView.open();
        assertThat(PremiumAndCoveragesTab.tableRatingDetailsDrivers.getRow(1, "Gender").getCell(2).getValue()).isEqualTo("X");
        renewalValidations(policyNumber);
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(1).getCell("Gender").getValue()).as("Gender should be displayed - X").isEqualTo("X");
    }

    protected void pas23040_ValidateGenderExpansionNonConformingRenewal1() {

        String policyNumber = openAppAndCreatePolicy();
        policy.renew().start();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        driverTab.fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), getDriverTd()));
        premiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.RatingDetailsView.open();
        assertThat(PremiumAndCoveragesTab.tableRatingDetailsDrivers.getRow(1, "Gender").getCell(3).getValue()).isEqualTo("X");
        renewalValidations(policyNumber);
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Gender").getValue()).as("Gender should be displayed - X").isEqualTo("X");

    }

    protected void pas23279_ValidateRelToFirstNamedInsuredListNBEndTx() {

        TestData testData = getPolicyTD();
        TestData addDriverTd = getDriverTd().adjust(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), "Parent");
        createQuoteAndFillUpTo(testData, DriverTab.class);

        Assertions.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).getAllValues()).contains("Employee");
        driverTab.fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), addDriverTd));
        driverTab.submitTab();
        policy.getDefaultView().fillFromTo(testData, MembershipTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        Assertions.assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Rel. to First Named Insured").getValue())
                .as("Value should be displayed - Parent").isEqualTo("Parent");

        //Endorse the policy and change the value for second Driver "Rel to Named Insured" to Sibling from Parent
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        DriverTab.tableDriverList.getRow(2).getCell(5).controls.links.getFirst().click();
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED).setValue("Sibling");
        driverTab.submitTab();
        adjustPolicyTd(testData);

        policy.getDefaultView().fillFromTo(testData, MembershipTab.class,DocumentsAndBindTab.class, true);
        documentsAndBindTab.submitTab();
        if (errorTab.tableErrors.isPresent()) {
            errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_10006002_CA);
            errorTab.override();
            documentsAndBindTab.submitTab();
        }
        Assertions.assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Rel. to First Named Insured").getValue())
                .as("Value should be displayed - Sibling").isEqualTo("Sibling");
    }

    private void bindAndValidateEndorsement(TestData testData) {
        adjustPolicyTd(testData);
        PremiumAndCoveragesTab.RatingDetailsView.close();
        premiumAndCoveragesTab.submitTab();
        policy.getDefaultView().fillFromTo(testData, DriverActivityReportsTab.class, DocumentsAndBindTab.class,true);
        documentsAndBindTab.submitTab();
        if (errorTab.tableErrors.isPresent()) {
            errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_10006002_CA);
            errorTab.override();
            documentsAndBindTab.submitTab();
        }
    }

    private void renewalValidations(String policyNumber){

        PremiumAndCoveragesTab.RatingDetailsView.close();
        premiumAndCoveragesTab.saveAndExit();
        LocalDateTime renEffective = PolicySummaryPage.getExpirationDate();
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renEffective));
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
        payTotalAmtDue(policyNumber);
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(renEffective);
		JobUtils.executeJob(BatchJob.policyStatusUpdateJob);
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
    }

    private TestData getDriverTd() {
        return getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName()).get(1)
                .mask(AutoCaMetaData.DriverTab.NAMED_INSURED.getLabel())
                .adjust(AutoCaMetaData.DriverTab.FIRST_NAME.getLabel(), "Seriously")
                .adjust(AutoCaMetaData.DriverTab.LAST_NAME.getLabel(), "Yes")
                .adjust(AutoCaMetaData.DriverTab.DATE_OF_BIRTH.getLabel(), "12/12/1982")
                .adjust(AutoCaMetaData.DriverTab.GENDER.getLabel(), "X")
                .adjust(AutoCaMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click");
    }

    private void adjustPolicyTd(TestData td) {
        td.mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT_DMV.getLabel()))
                .mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(),
                        AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()));
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            td.mask(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoCaMetaData.DocumentsAndBindTab.VEHICLE_INFORMATION.getLabel() + "[0]",
                    AutoCaMetaData.DocumentsAndBindTab.VehicleInformation.ARE_THERE_ANY_ADDITIONAL_INTERESTS.getLabel()));
        }
    }

}
