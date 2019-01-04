package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.modules.regression.sales.auto_ca.choice.TestPolicyCreationBig;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.ComboBox;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestRelToFNICAAC extends AutoCaChoiceBaseTest {
    DriverTab driverTab = new DriverTab();
    PurchaseTab purchaseTab = new PurchaseTab();
    DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    private ErrorTab errorTab = new ErrorTab();

    /**
     * @author Sreekanth Kopparapu
     * @name Test Additional options available for 'Rel to First Named Insured' field on Driver Tab
     * @scenario 1. Create a Quote for Auto CA Choice
     * 2. Fill in mandatory fields and navigate to Driver Tab
     * 3. For Rel to First Named Insured Field Validate the newly added options in the dropdown list First Named Insured
     *     Spouse, Son, Daughter, Other, Father, Mother, Domestic Partner, Other Resident Relative, Child, Parent, Sibling
     *     Employee
     * 4. Add a new driver with parent as value for Rel to Named Insured field
     * 5. Bind the policy
     * 6. Initiate an endorsement and
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-23279")
    public void pas23279_ValidateRelToFirstNamedInsuredListNB(@Optional("CA") String state) {

        TestData testData = getPolicyTD();
        TestData addDriver = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName()).get(1)
                .mask(AutoCaMetaData.DriverTab.NAMED_INSURED.getLabel())
                .adjust(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), "Parent")
                .adjust(AutoCaMetaData.DriverTab.FIRST_NAME.getLabel(), "Seriously")
                .adjust(AutoCaMetaData.DriverTab.LAST_NAME.getLabel(), "Yes")
                .adjust(AutoCaMetaData.DriverTab.GENDER.getLabel(), "X")
                .adjust(AutoCaMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click");


        createQuoteAndFillUpTo(testData, DriverTab.class);
        assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), ComboBox.class).getAllValues()).contains("Employee");
        driverTab.fillTab(DataProviderFactory.dataOf(DriverTab.class.getSimpleName(), addDriver));
        driverTab.submitTab();
        policy.getDefaultView().fillFromTo(testData, MembershipTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Rel. to First Named Insured").getValue()).as("Value should be displayed - Parent").isEqualTo("Parent");

        //Endorse the policy and change the value for second Driver "Rel to Named Insured" to Sibling from Parent
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());
        DriverTab.tableDriverList.getRow(2).getCell(5).controls.links.getFirst().click();
        driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED).setValue("Sibling");
        driverTab.submitTab();
        testData.mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()))
                .mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY.getLabel()))
                .mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT.getLabel()))
                .mask(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(),AutoCaMetaData.DocumentsAndBindTab.VEHICLE_INFORMATION.getLabel()));
        policy.getDefaultView().fillFromTo(testData, MembershipTab.class,DocumentsAndBindTab.class, true);
        documentsAndBindTab.submitTab();
        if (errorTab.tableErrors.isPresent()) {
            errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_10006002_CA);
            errorTab.override();
            documentsAndBindTab.submitTab();
        }
        assertThat(PolicySummaryPage.tablePolicyDrivers.getRow(2).getCell("Rel. to First Named Insured").getValue()).as("Value should be displayed - Sibling").isEqualTo("Sibling");
    }

}
