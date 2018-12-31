package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.Tab;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

import static aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION;

@StateList(statesExcept = Constants.States.CA)
public class TestMPDPhase3 extends AutoSSBaseTest {

    /**
     * @author Rob Boles
     * @name Test Prefill DOB and new DOB on general tab.
     * @scenario 1. Create Customer with Prefill data from STUB
     * 2. Initiate Auto SS quote
     * 3. Ensure DOB is populated from Prefill
     * 4. Then the DOB is populated into the DOB field for the NI on the general tab
     * 5. And the DOB field is non-editable
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10099")
    public void pas1502_ADD_MVR_Renewal_Reinstatement_Retention_AC1(@Optional("") String state) {


        TestData customerTd = (getCustomerIndividualTD("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), "Wayne")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), "Rooney")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.DATE_OF_BIRTH.getLabel()), "7/8/1976")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()), "85321")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ADDRESS_LINE_1.getLabel()), "8416 Briggs Drive"));

        mainApp().open();
        createCustomerIndividual(customerTd);
        log.info("Prefill test Started...");
        policy.initiate();
        TestData td = getPolicyDefaultTD();
         //order prefill and get response
        PrefillTab prefillTab = new PrefillTab();
        prefillTab.fillTab(td);
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Named Insured").controls.checkBoxes.getFirst()).hasValue(true);
            softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Driver").controls.checkBoxes.getFirst()).hasValue(true);
            softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("First Name")).hasValue("Wayne");
            softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Last Name")).hasValue("Rooney");
            softly.assertThat(PrefillTab.tableDrivers.getRow(1).getCell("Date of Birth")).hasValue("07/08/1976");
        });

        prefillTab.submitTab();
        CustomSoftAssertions.assertSoftly(softly -> {
            //check GeneralTab
            aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab();

            MultiAssetList namedInsuredInfo = generalTab.getNamedInsuredInfoAssetList();
            softly.assertThat(namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME)).hasValue("Wayne");
            softly.assertThat(namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME)).hasValue("Rooney");
            softly.assertThat(namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH)).hasValue("07/08/1976");

            //check Driver tab
            NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

            aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab driverTab = new aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab();
            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.NAMED_INSURED)).hasValue("Wayne Rooney");
            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.FIRST_NAME)).hasValue("Wayne");
            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LAST_NAME)).hasValue("Rooney");
            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.DATE_OF_BIRTH)).hasValue("07/08/1976");

            Tab.buttonSaveAndExit.click();
        });
    }

    /**
     * @author Rob Boles
     * @name Test Prefill DOB and new DOB on general tab.
     * @scenario 1. Create Customer with Prefill data from STUB
     * 2. Initiate Auto SS quote
     * 3. Ensure DOB is populated from Prefill
     * 4. Then the DOB is populated into the DOB field for the NI on the general tab
     * 5. And the DOB field is non-editable
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10099")
    public void pas1502_ADD_MVR_Renewal_Reinstatement_Retention_AC2(@Optional("") String state) {

        //TODO: parameterize this or make it a template to account for when we deal with CA Auto
        TestData customerTd = (getCustomerIndividualTD("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), "Jada")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), "Byers")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.DATE_OF_BIRTH.getLabel()), "5/29/1977")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ZIP_CODE.getLabel()), "85032")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.ADDRESS_LINE_1.getLabel()), "27617 N 25TH DR"));

        mainApp().open();
        createCustomerIndividual(customerTd);
        log.info("Prefill test Started...");
        policy.initiate();
        //TODO: adjusted test data here to add DOB for CAROLE NIMFINE (prefill yes DOB no)
        TestData td = getPolicyDefaultTD();
                //.adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH.getLabel(), "07/28/1984");
        //order prefill and get response
        PrefillTab prefillTab = new PrefillTab();
        prefillTab.fillTab(td);

        PrefillTab.tableDrivers.getRow(1).getCell("Named Insured").controls.checkBoxes.getFirst().setValue(false);
        PrefillTab.tableDrivers.getRow(1).getCell("Driver").controls.checkBoxes.getFirst().setValue(false);
        PrefillTab.tableDrivers.getRow(2).getCell("Named Insured").controls.checkBoxes.getFirst().setValue(true);
        PrefillTab.tableDrivers.getRow(2).getCell("Driver").controls.checkBoxes.getFirst().setValue(true);

        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Named Insured").controls.checkBoxes.getFirst()).hasValue(true);
            softly.assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Driver").controls.checkBoxes.getFirst()).hasValue(true);
            softly.assertThat(PrefillTab.tableDrivers.getRow(2).getCell("First Name")).hasValue("CAROLE");
            softly.assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Last Name")).hasValue("NIMFINE");
            softly.assertThat(PrefillTab.tableDrivers.getRow(2).getCell("Date of Birth")).hasValue("");
            //hasValue("");
        });

        prefillTab.submitTab();
        CustomSoftAssertions.assertSoftly(softly -> {
            //check GeneralTab
            aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab();

            MultiAssetList namedInsuredInfo = generalTab.getNamedInsuredInfoAssetList();
            softly.assertThat(namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME)).hasValue("CAROLE");
            softly.assertThat(namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME)).hasValue("NIMFINE");
            //GENERAL TAB DOB SHOULD BE EMPTY
            softly.assertThat(namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH)).hasValue("");
            //TODO GENERAL TAB NEEDS NEW DOB

            generalTab.fillTab(td);
            generalTab.submitTab();
            //check if rule fires by trying to move away from GENERAL TAB
            //NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
            //Check Error
            //generalTab.getNamedInsuredInfoAssetList().getWarning(AutoSSMetaData.GeneralTab.))
            softly.assertThat(generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH).getWarning().equals("'Insured Date of Birth' is required"));
            //add new DOB for NI 1
            //generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.INSURED_DATE_OF_BIRTH).setValue("");
            generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH).setValue("5/5/1985");
            //generalTab.getAssetList().getAsset(NAMED_INSURED_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH).setValue("5/5/1985");

            aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab driverTab = new aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab();
            NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.NAMED_INSURED)).hasValue("Carole Nimfine");
            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.FIRST_NAME)).hasValue("Carole");
            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LAST_NAME)).hasValue("Nimfine");
            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.DATE_OF_BIRTH)).hasValue("05/05/1985");

            Tab.buttonSaveAndExit.click();
        });
    }

}


/*        AC1
        GIVEN I'm an agent in a SS auto product and I am adding a Named Insured (NI)
        WHEN Lexis Nexis prefill returns DOB for any selected NI
        THEN the DOB is populated into the respective DOB field(s)for the NI(s)on the General Tab and should not be editable

        AC2
        GIVEN I'm an agent in a SS auto product and a NI has been added by prefill
        WHEN Lexis Nexis returns a null DOB during prefill for that NI
        THEN on the General tab the DOB field for that NI will be editable AND is mandatory and must be entered

        AC3
        GIVEN I'm an agent in a SS auto product and I am adding a NI AND Lexis Nexis has not been run for that NI (i.e. on Prefill tab)
        WHEN I'm on the General tab
        THEN the DOB field for that NI will be editable AND is mandatory and must be entered

        *transaction types include NB,endorsement,revised renewal

        check BCT volume-existing policies with NIs without a DOB
 */
