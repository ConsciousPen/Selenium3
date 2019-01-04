package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.modules.regression.sales.auto_ca.choice.TestPolicyCreationBig;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestGenderExpansionNonConformingCAAC extends AutoCaChoiceBaseTest {
    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    private DriverTab driverTab = new DriverTab();
    private PurchaseTab purchaseTab = new PurchaseTab();
    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private ErrorTab errorTab = new ErrorTab();


    /**
     * @author Sreekanth Kopparapu
     * @name Test Gender Expansion for NonConforming value of X on Applicable Pages - Create Customer, Driver page,
     *        Quote/Policy Summary page, VRD page
     * @scenario
     * 1. Open App with Create Customer page
     * 2. Create customer with Option 'X' in Gender combo box
     * 3. Create Auto Quote, fill mandatory tabs - Prefill and General
     * 4. Navigate to Driver Tab and validate for Gender - X value is prefilled
     * 5. Fill mandatory fields and navigate to P*C
     * 6. Validate VRD Page - Gender X Is displayed
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Gender Expansion for NonConforming value of X")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-23040")
    public void pas23040_ValidateGenderExpansionNonConformingNB(@Optional("CA") String state) {

        String generalTabSimpleName = CustomerMetaData.GeneralTab.class.getSimpleName();
        TestData customerTd = getCustomerIndividualTD("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(generalTabSimpleName, CustomerMetaData.GeneralTab.GENDER.getLabel()), "X");

        mainApp().open();
        createCustomerIndividual(customerTd);

        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), DriverTab.class, true);
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.GENDER).getValue()).isEqualTo("X");
        driverTab.submitTab();

        policy.getDefaultView().fillFromTo(getPolicyTD(), MembershipTab.class, PremiumAndCoveragesTab.class, true);
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
        assertThat(PremiumAndCoveragesTab.tableRatingDetailsDrivers.getRow(1, "Gender").getCell(2).getValue()).equals("X");

        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        premiumAndCoveragesTab.submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), DriverActivityReportsTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(1).getCell("Gender").getValue()).as("Gender should be displayed - X").isEqualTo("X");

    }
    /**
     * @author Sreekanth Kopparapu
     * @name Test Gender Expansion for NonConforming value of X on Applicable Pages - Create Customer, Driver page,
     * Quote/Policy Summary page, VRD page
     * @scenario 1. Create a policy for Auto SS State and Bind the policy - one NI with gender as Male
     * 2. Initiate an endorsement and add a driver with Gender 'X' in Gender combo box
     * 3. Fill mandatory fields and navigate to P*C, calculate premium
     * 6. Validate VRD Page - Gender X Is displayed in the proposed new driver column
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Gender Expansion for NonConforming value of X")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-23040")
    public void pas23040_ValidateGenderExpansionNonConformingEndTx(@Optional("CA") String state) {

        TestData td = getPolicyTD();
        TestData addDriver = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName()).get(1)
                .mask(AutoCaMetaData.DriverTab.NAMED_INSURED.getLabel())
                .adjust(AutoCaMetaData.DriverTab.FIRST_NAME.getLabel(), "Seriously")
                .adjust(AutoCaMetaData.DriverTab.LAST_NAME.getLabel(), "Yes")
                .adjust(AutoCaMetaData.DriverTab.GENDER.getLabel(), "X")
                .adjust(AutoCaMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click");

        openAppAndCreatePolicy(td);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        driverTab.fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), addDriver));
        td.mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()))
                .mask(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(),AutoCaMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel()))
                .mask(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(),AutoCaMetaData.DocumentsAndBindTab.VEHICLE_INFORMATION.getLabel()));

        validateAndBind(td);
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Gender").getValue()).as("Gender should be displayed - X").isEqualTo("X");

    }
    /**
     * @author Sreekanth Kopparapu
     * @name Test Gender Expansion for NonConforming value of X on Applicable Pages - Create Customer, Driver page,
     * Quote/Policy Summary page, VRD page
     * @scenario 1. Create a policy for Auto SS State and Bind the policy - one NI with gender as Male
     * 2. Initiate an endorsement and change the gender 'Male' to Gender 'X' in Gender combo box
     * 3. Fill mandatory fields and navigate to P*C, calculate premium
     * 6. Validate VRD Page - Gender X Is displayed in the proposed new driver column
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Gender Expansion for NonConforming value of X")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-23040")
    public void pas23040_ValidateGenderExpansionNonConformingEnd1Tx(@Optional("CA") String state) {

        TestData td = getPolicyTD();
        openAppAndCreatePolicy(td);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.GENDER).setValue("X");

        td.mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()))
                .mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY.getLabel()))
                .mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT.getLabel()))
                .mask(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(),AutoCaMetaData.DocumentsAndBindTab.VEHICLE_INFORMATION.getLabel()));

        validateAndBind(td);
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(1).getCell("Gender").getValue()).as("Gender should be displayed - X").isEqualTo("X");

    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Gender Expansion for NonConforming value of X on Applicable Pages - Create Customer, Driver page,
     * Quote/Policy Summary page, VRD page
     * @scenario 1. Create a policy for Auto CA Choice State and Bind the policy - one NI with gender as Male
     * 2. Initiate an Renewal and change the gender 'Male' to Gender 'X' in Gender combo box
     * 3. Fill mandatory fields and navigate to P*C, calculate premium
     * 6. Validate VRD Page - Gender X Is displayed in the proposed new driver column
     * 7. Propose the policy, pay the min due
     * 8 Renew the policy and assert the updated gender on Policy summary page for the Renewed policy
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Gender Expansion for NonConforming value of X")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-23040")
    public void pas23040_ValidateGenderExpansionNonConformingRenewal(@Optional("") String state) {

        String policyNumber = openAppAndCreatePolicy();
        policy.renew().start();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.GENDER).setValue("X");
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        renewalValidations(policyNumber);
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(1).getCell("Gender").getValue()).as("Gender should be displayed - X").isEqualTo("X");

    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Gender Expansion for NonConforming value of X on Applicable Pages - Create Customer, Driver page,
     * Quote/Policy Summary page, VRD page
     * @scenario 1. Create a policy for Auto CA Choice State and Bind the policy - one NI with gender as Male
     * 2. Initiate an Renewal and add a new driver on the Driver tab with Gender 'X'
     * 3. Fill mandatory fields and navigate to P*C, calculate premium
     * 6. Validate VRD Page - Gender X Is displayed in the proposed new driver column
     * 7. Propose the policy, pay the min due
     * 8 Renew the policy and assert the newly added driver gender on Policy summary page for the Renewed policy
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Gender Expansion for NonConforming value of X")
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-23040")
    public void pas23040_ValidateGenderExpansionNonConformingRenewal1(@Optional("") String state) {

        TestData addDriver = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName()).get(1)
                .mask(AutoCaMetaData.DriverTab.NAMED_INSURED.getLabel())
                .adjust(AutoCaMetaData.DriverTab.FIRST_NAME.getLabel(), "Seriously")
                .adjust(AutoCaMetaData.DriverTab.LAST_NAME.getLabel(), "Yes")
                .adjust(AutoCaMetaData.DriverTab.GENDER.getLabel(), "X")
                .adjust(AutoCaMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click");

        String policyNumber = openAppAndCreatePolicy();
        policy.renew().start();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        driverTab.fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), addDriver));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        renewalValidations(policyNumber);
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Gender").getValue()).as("Gender should be displayed - X").isEqualTo("X");

    }

    private void  validateAndBind(TestData testData) {
        premiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
        assertThat(premiumAndCoveragesTab.getRatingDetailsDriversData().get(1).getValue("Gender")).equals("X");
        //assertThat(premiumAndCoveragesTab.getRatingDetailsDriversData().get(1).getValue("Gender")).isEqualTo("X");
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
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

        premiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
        assertThat(PremiumAndCoveragesTab.tableRatingDetailsDrivers.getRow(1, "Gender").getCell(2).getValue()).equals("X");
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        premiumAndCoveragesTab.saveAndExit();
        LocalDateTime renEffective = PolicySummaryPage.getExpirationDate();
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(renEffective));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
        payTotalAmtDue(policyNumber);
        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(renEffective);
        JobUtils.executeJob(Jobs.policyStatusUpdateJob);
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
    }


}
