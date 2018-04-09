package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.Month;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.GenerateOnDemandDocumentActionTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.endorsements.AutoSSForms;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.CheckBox;

public class TestEUIMForms extends AutoSSBaseTest {

    private GenerateOnDemandDocumentActionTab generateOnDemandDocumentActionTab = new GenerateOnDemandDocumentActionTab();
    private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    private FormsTab formsTab = new FormsTab();
    private CheckBox enhancedUIM = new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.ENHANCED_UIM);
    private final String formId = DocGenEnum.Documents.AAEUIMMD.getId().substring(0, DocGenEnum.Documents.AAEUIMMD.getId().indexOf(" "));
    private final String formDesc = DocGenEnum.Documents.AAEUIMMD.getName();

    /**
     *@author Josh Carpenter
     *@name Test that form AAEUIMMD is displayed on the P&C and Forms tabs with MD EUIM coverage for NB
     *@scenario
     * 1. Create Customer
     * 2. Initiate Auto SS MD Quote after 07/01/2018
     * 3. Select EUIM coverage in the 'Policy Level Liability Coverages' section of the P&C page
     * 3. Calculate premium
     * 4. Verify form AAEUIMMD is displayed in the 'Forms' section of the P&C page and associated Term Premium is 0.00
     * 5. Navigate to the Forms tab
     * 6. Verify form AAEUIMMD 07 18 is displayed and the description is 'Maryland Enhanced Uninsured Motorist Coverage'
     * 7. Save and Exit
     * 8. Access 'Generate On Demand Document' page
     * 9. Verify form AAEUIMMD is available for generation
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-11302, PAS-11509")
    public void pas11302_testEUIMMDFormNB(@Optional("MD") String state) {

        TestData tdEUIM = getPolicyTD().adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_NB"));

        verifyAlgoDate();

        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        verifyForms(tdEUIM);
        verifyGoddPage();
    }

    /**
     *@author Josh Carpenter
     *@name Test that form AAEUIMMD is displayed on the P&C and Forms tabs with MD EUIM coverage for Conversion
     *@scenario
     * 1. Create Customer
     * 2. Initiate Auto SS MD Conversion Quote after 07/01/2018
     * 3. Select EUIM coverage in the 'Policy Level Liability Coverages' section of the P&C page
     * 3. Calculate premium
     * 4. Verify form AAEUIMMD is displayed in the 'Forms' section of the P&C page and associated Term Premium is 0.00
     * 5. Navigate to the Forms tab
     * 6. Verify form AAEUIMMD 07 18 is displayed and the description is 'Maryland Enhanced Uninsured Motorist Coverage'
     * 7. Save and Exit
     * 8. Access 'Generate On Demand Document' page
     * 9. Verify form AAEUIMMD is available for generation
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Conversions.AUTO_SS, testCaseId = "PAS-11302, PAS-11509")
    public void pas11302_testEUIMMDFormConversion(@Optional("MD") String state) {

        TestData tdEUIM = getConversionPolicyDefaultTD().adjust(PremiumAndCoveragesTab.class.getSimpleName(), getTestSpecificTD("PremiumAndCoveragesTab_Conv"));

        verifyAlgoDate();

        mainApp().open();
        createCustomerIndividual();

        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
        verifyForms(tdEUIM);
        verifyGoddPage();

    }

    /**
     *@author Josh Carpenter
     *@name Test that form AAEUIMMD is available for generation with MD EUIM coverage for Endorsements
     *@scenario
     * 1. Create Customer
     * 2. Create Auto SS MD Policy after 07/01/2018 with Standard UIM coverage
     * 3. Do a Midterm endorsement and switch UIM to EUIM coverage
     * 3. Re-calculate premium and bind endorsement
     * 4. Access 'Generate On Demand Document' page
     * 5. Verify form AAEUIMMD is available for generation
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-11509")
    public void pas11509_testEUIMMDFormEndorsement(@Optional("MD") String state) {

        verifyAlgoDate();

        mainApp().open();
        createCustomerIndividual();

        // Create policy with Standard UIM coverage
        createPolicy();

        //Perform mid-term endorsement and switch to EUIM coverage
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        switchToEUIMAndBind();

        verifyGoddPage();

    }

    /**
     *@author Josh Carpenter
     *@name Test that form AAEUIMMD is available for generation with MD EUIM coverage during Renewal
     *@scenario
     * 1. Create Customer
     * 2. Create Auto SS MD Policy after 07/01/2018 with Standard UIM coverage
     * 3. Initiate Renewal process
     * 4. Change UIM to EUIM coverage for renewal
     * 5. Re-calculate premium and propose
     * 4. Access 'Generate On Demand Document' page
     * 5. Verify form AAEUIMMD is available for generation
     *@details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-11509")
    public void pas11509_testEUIMMDFormRenewal(@Optional("MD") String state) {

        verifyAlgoDate();

        // Create policy with Standard UIM coverage
        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        // Create renewal and switch to EUIM coverage
        policy.renew().perform();
        switchToEUIMAndBind();

        verifyGoddPage();

    }

    //TODO remove verify algo date after 2018-07-01
    private void verifyAlgoDate() {
        LocalDateTime algoEffectiveDate = LocalDateTime.of(2018, Month.JULY, 1, 0, 0);
        if (TimeSetterUtil.getInstance().getCurrentTime().isBefore(algoEffectiveDate)) {
            TimeSetterUtil.getInstance().nextPhase(algoEffectiveDate);
        }
    }

    private void verifyForms(TestData td) {

        //PAS-11302 AC1
        policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
        TestData formsData = premiumAndCoveragesTab.getFormsData();
        assertThat(formsData.getKeys()).contains(formId);
        assertThat(formsData.getValue(formId)).isEqualTo("$0.00");

        //PAS-11302 AC2
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.FORMS.get());
        AutoSSForms.AutoSSPolicyFormsController policyForms = formsTab.getAssetList().getAsset(AutoSSMetaData.FormsTab.POLICY_FORMS);
        assertThat(policyForms.tableSelectedForms.getRowContains("Name", formId).getCell(2).getValue()).isEqualTo(formDesc);
        assertThat(policyForms.getRemoveLink(formId)).isPresent(false);
        formsTab.saveAndExit();

    }

    private void verifyGoddPage() {
        policy.policyDocGen().start();
        generateOnDemandDocumentActionTab.verify.documentsEnabled(DocGenEnum.Documents.AAEUIMMD);
    }

    private void switchToEUIMAndBind() {
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        enhancedUIM.setValue(true);
        premiumAndCoveragesTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();
    }
}
