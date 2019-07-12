package aaa.modules.regression.sales.template.functional;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import toolkit.datax.TestData;

/**
 * This template is used to test the new CA Driver Assignment without Gender
 * @author Chris Johns
 */
public class TestCADriverAssignmentWithoutGenderTemplate extends CommonTemplateMethods {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_hhmmss");
    protected TestData adjusted;
    protected LocalDateTime policyExpirationDate;
    protected LocalDateTime policyEffectiveDate;
    protected String policyNumber;

    protected static DriverTab driverTab = new DriverTab();
    protected static GeneralTab generalTab = new GeneralTab();
    protected static PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
    protected static DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    protected static AssignmentTab assignmentTab = new AssignmentTab();
    protected static PurchaseTab purchaseTab = new PurchaseTab();
    protected static ErrorTab errorTab = new ErrorTab();
    protected static DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
    protected static ActivityInformationMultiAssetList activityInformationAssetList = driverTab.getActivityInformationAssetList();

    protected void pas29418_DriverAssignmentRanking(String SCENARIO_DATA) {
        //Setup test data for 7 drivers and 7 vehicles with 11/01/2019 effective date
        TestData testDataDriverDetails = getTestSpecificTD(SCENARIO_DATA).resolveLinks();
        adjusted = getPolicyTD().adjust((testDataDriverDetails).adjust(TestData.makeKeyPath("GeneralTab", AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
                AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), "11/01/2019"));

        //Create quote with 7 drivers and navigate to P&C page to get system rated drivers
        createQuoteAndFillUpTo(adjusted, PremiumAndCoveragesTab.class);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.ASSIGNMENT.get());

        //Verify System Rated Drivers are in the correct ranking order, based on driver details other than Gender
        driverAssignmentAssertion(0, "One");
        driverAssignmentAssertion(1, "Two");
        driverAssignmentAssertion(2, "Three");
        driverAssignmentAssertion(3, "Four");
        driverAssignmentAssertion(4, "Five");
        driverAssignmentAssertion(5, "Six");
        driverAssignmentAssertion(6, "Seven");
    }

    /**
     * Method changes current date to policy expiration date and issues generated renewal image
     * @param driverNumber System Rated Driver position to check - Index starts at zero
     * @param driverName System Rated Driver Name - Verifies via 'contains'
     */
    public void driverAssignmentAssertion(int driverNumber, String driverName) {
            assertThat(assignmentTab.getAssetList().getAsset(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getValue().get(driverNumber).getValue("System Rated Driver")).contains(driverName);
    }

}

