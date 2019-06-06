package aaa.modules.regression.sales.template.functional;

import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.common.Tab;
import aaa.common.components.OtherAAAProductsSearchTableElement;
import aaa.common.components.OtherAAAProductsTableElement;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.AAAMultiPolicyDiscountQueries;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.SearchEnum;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;
import org.testng.annotations.Optional;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.BrowserController;
import toolkit.webdriver.controls.*;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;
import java.time.DayOfWeek;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;


public abstract class TestMultiPolicyDiscountAbstract extends PolicyBaseTest {

    public enum genericTabs {
        PrefillTab, GeneralTab, DriverTab, MembershipTab, VehicleTab, AssignmentTab, FormsTab, PremiumAndCoveragesTab, DriverActivityReportsTab, DocumentsAndBindTab
    }

    public enum mpdPolicyType{
        home, renters, condo, life, motorcycle
    }

    public static List<String> _listOfMPDTableColumnNames = Arrays.asList("Policy Number / Address", "Policy Type", "Customer Name/DOB", "Expiration Date", "Status", "MPD");
    public static List<String> _listOfMPDSearchResultsTableColumnNames = Arrays.asList("Customer Name/Address", "Date of Birth", "Policy Type", "Other AAA Products/Policy Address", "Status", "Select");
    public static final String _XPATH_TO_ALL_SEARCH_RESULT_CHECKBOXES = "*//input[contains(@id, 'customerSelected')]";

    // Add more states here if they get MC policy support.
    private ArrayList<String> motorcycleSupportedStates = new ArrayList<>(Arrays.asList(Constants.States.AZ));

    /**
     *  Creates a policy with MPD discount
     *  Runs NB +30 jobs for MPD discount validation
     *  Discount is removed due to non-active products found and removes discount
     *  Reason in transaction history set to "Discount validation failure, policy information updated."
     * @param state the test will run against.
     * @author Robert Boles - CIO
     */
    public void pas29273_updateReasonMPDRemoval_Template(@Optional("") String state) {
        TestData testData = getTdAuto();
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, getGeneralTab().getClass(),true);

        getGeneralTab_CurrentAAAMemberAsset().setValue("Yes");
        //Set the membership number to active
        this.getGeneralTab_MembershipNumberAsset().setValue("4290074030137505");
        //puts quoted products into the MPD table with REFRESH_PQ@yeah.com
        getGeneralTab_ContactInformation_EmailAsset().setValue("REFRESH_PQ@yeah.com");
        getGeneralTab_OtherAAAProductsOwned_RefreshAsset().click(Waiters.AJAX);
        getGeneralTab().submitTab();

        policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(), getPurchaseTab().getClass(), true);
        if (getErrorTab_TableErrors().isPresent()) {
            errorTabOverrideErrors(ErrorEnum.Errors.ERROR_AAA_MVR_order_validation_SS);
            errorTabOverride();
            getPurchaseTab().submitTab();
        } else {
            getPurchaseTab().submitTab();
        }

        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        log.info("Policy Number " + PolicySummaryPage.getPolicyNumber());
        LocalDateTime policyEffectiveDatePlus30 = PolicySummaryPage.getEffectiveDate();
        if(policyEffectiveDatePlus30.getDayOfWeek() == DayOfWeek.SATURDAY) {
            policyEffectiveDatePlus30 = policyEffectiveDatePlus30.plusDays(2);
        } else if (policyEffectiveDatePlus30.getDayOfWeek() == DayOfWeek.SUNDAY) {
            policyEffectiveDatePlus30 = policyEffectiveDatePlus30.plusDays(1);
        }
        TimeSetterUtil.getInstance().nextPhase(policyEffectiveDatePlus30.plusDays(30));
        log.info("Time Setter Move " + policyEffectiveDatePlus30.plusDays(30));

        jobsNBplus30runNoChecks();
        mainApp().reopen();
        SearchPage.openPolicy(policyNumber);
        transactionHistoryRecordCountCheck(policyNumber, 2, "Discount validation failure, policy information updated.", new ETCSCoreSoftAssertions());
    }

    /**
     * Make sure various combos of Unquoted Other AAA Products rate properly and are listed in the UI
     * on P&C Page as well as in View Rating Details. AC3
     * @param state the test will run against.
     * @author Brian Bond - CIO
     */
    public void pas23983_MPD_unquoted_rate_and_show_discounts_Template(@Optional("") String state) {

        // Data and tools setup
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);

        ArrayList<HashMap<mpdPolicyType, Boolean>> scenarioList = getUnquotedManualScenarios();

        // Perform the following for each scenario. Done this way to avoid recreating users every scenario.
        for (int i = 0; i < scenarioList.size(); i++ ) {

            HashMap <mpdPolicyType, Boolean> currentScenario = scenarioList.get(i);

            // Set unquoted policies //
            setUnquotedCheckboxes(currentScenario);

            // On first iteration fill in data. Else jump to PnC page
            if (i == 0) {
                // Continue to next tab then move to P&C tab //
                getGeneralTab().submitTab();

                policy.getDefaultView().fillFromTo(testData, getDriverTab().getClass(),
                        getPremiumsAndCoveragesTab().getClass(), true);
            }
            else {
                navigateToPremiumAndCoveragesTab();
            }

            assertSoftly(softly -> {

                // Check in View Rating details for Multi-Policy Discount //
                String mpdDiscountApplied =
                        getPnCTab_RatingDetailsQuoteInfoData().getValue("AAA Multi-Policy Discount");

                // Close the VRD Popup
                closePnCTab_ViewRatingDetails();

                // If any value is true then the VRD MPD Discount should be Yes. Else None.
                String mpdVRDExpectedValue = currentScenario.containsValue(true) ? "Yes" : "None";

                softly.assertThat(mpdDiscountApplied).isEqualTo(mpdVRDExpectedValue);

                // Validate Discount and Surcharges //
                String discountsAndSurcharges = getPnCTab_DiscountsAndSurcharges();

                // Check against any property string. Done this way because only one property type listed in Discounts.
                Boolean propertyValuesPresent =
                        currentScenario.get(mpdPolicyType.home) ||
                                currentScenario.get(mpdPolicyType.renters) ||
                                currentScenario.get(mpdPolicyType.condo);

                Boolean propertyValuePresentInString =
                        discountsAndSurcharges.contains("Home") ||
                                discountsAndSurcharges.contains("Condo") ||
                                discountsAndSurcharges.contains("Renters");

                softly.assertThat(propertyValuePresentInString).isEqualTo(propertyValuesPresent);

                // MC and Life always show if added.
                softly.assertThat(currentScenario.get(mpdPolicyType.motorcycle)).
                        isEqualTo(discountsAndSurcharges.contains("Motorcycle"));

                softly.assertThat(currentScenario.get(mpdPolicyType.life)).
                        isEqualTo(discountsAndSurcharges.contains("Life"));
            });

            // Return to GeneralTab tab.
            navigateToGeneralTab();
        }
    }

    /**
     * This tests that when verified policies are in the list, unquoted options are disabled, and if they are removed, the options are re-enabled.
     * @param state the test will run against.
     * @scenario
     * Prereqs: enterpriseSearchService.enterpriseCustomersSearchUri setup to return all 3 homeowner types on refresh.
     * 1. Using standard test data, create customer, start auto quote, fill up to general tab with default data.
     * 2. Add second NI that has correct response pattern to get 3 property policies back. Click the refresh button.
     * 3. Verify the unquoted options that come back from refresh are disabled. (Based on prereqs, should be all HO)
     * 4. Remove all the Disabled policies and make sure checkboxes re-enable.
     * @author Brian Bond - CIO
     */
    public void pas_21481_MPD_Unquoted_Companion_Product_AC2_AC3_Template(@Optional("") String state) {

        // Step 1
        TestData testData = getPolicyTD();

        // Create customer and move to general tab. //
        createQuoteAndFillUpTo(testData, getGeneralTab().getClass(), true);

        // Step 2

        // REFRESH_P will come back with all 3 property types
        addNamedInsured("REFRESH_P", "Doe", "02/14/1990", "No", "Own Home");


        getGeneralTab_OtherAAAProductsOwned_RefreshAsset().click(Waiters.AJAX);

        // Step 3:
        // Note: If following fails on first assert, validate hitting refresh comes back with
        // Home, Renters, and Condo policies. If not, check test pre-reqs have been met.
        assertThat(getUnquotedCheckBox(mpdPolicyType.home).isEnabled()).isFalse();
        assertThat(getUnquotedCheckBox(mpdPolicyType.renters).isEnabled()).isFalse();
        assertThat(getUnquotedCheckBox(mpdPolicyType.condo).isEnabled()).isFalse();
        assertThat(getUnquotedCheckBox(mpdPolicyType.life).isEnabled()).isTrue();

        // Only add motorcycle in supported states
        if (motorcycleSupportedStates.contains(getState())){
            assertThat(getUnquotedCheckBox(mpdPolicyType.motorcycle).isEnabled()).isTrue();
        }
        else{
            assertThat(getUnquotedCheckBox(mpdPolicyType.motorcycle).isPresent()).isFalse();
        }

        // Step 4
        removeAllOtherAAAProductsOwnedTablePolicies();

        assertThat(getUnquotedCheckBox(mpdPolicyType.home).isEnabled()).isTrue();
        assertThat(getUnquotedCheckBox(mpdPolicyType.renters).isEnabled()).isTrue();
        assertThat(getUnquotedCheckBox(mpdPolicyType.condo).isEnabled()).isTrue();
    }

    /**
     * @return Test Data for an AZ SS policy with no other active policies
     */
    protected abstract TestData getTdAuto();

    protected abstract ComboBox getGeneralTab_CurrentAAAMemberAsset();

    protected abstract TextBox getGeneralTab_MembershipNumberAsset();

    protected abstract TextBox getGeneralTab_ContactInformation_EmailAsset();

    protected abstract Button getGeneralTab_OtherAAAProductsOwned_RefreshAsset();

    protected abstract Tab getGeneralTab();

    protected abstract Tab getDriverTab();

    protected abstract Tab getPremiumsAndCoveragesTab();

    protected abstract Tab getPurchaseTab();

    protected abstract Table getErrorTab_TableErrors();

    protected abstract void errorTabOverrideErrors(ErrorEnum.Errors... errors);

    protected abstract void errorTabOverride();

    protected abstract void navigateToPremiumAndCoveragesTab();

    protected abstract void navigateToGeneralTab();

    protected abstract TestData getPnCTab_RatingDetailsQuoteInfoData();

    protected abstract void closePnCTab_ViewRatingDetails();

    protected abstract String getPnCTab_DiscountsAndSurcharges();

    protected abstract Table getGeneralTab_OtherAAAProductTable();

    protected abstract void setUnquotedCheckbox(mpdPolicyType policyType, Boolean fillInCheckbox);

    /**
     * Returns Unquoted Checkbox control based on passed in data.
     * @param policyType Which checkbox to return checkbox.
     * @return Checkbox representing requested control.
     */
    protected abstract CheckBox getUnquotedCheckBox(mpdPolicyType policyType);

    /**
     * Adds another named insured and fills out required data.
     * @param firstName is named insured's first name.
     * @param lastName is named insured's last name.
     * @param dateOfBirth is named insured's date of birth in mm/dd/yyyy format
     * @param livedHereLessThan3Years is "Yes" or "No" if named insured has lived at location for less than 3 years.
     * @param residence can be any option in the Residence drop down.
     */
    public abstract void addNamedInsured(String firstName, String lastName, String dateOfBirth, String livedHereLessThan3Years, String residence);

    /**
     * Jobset needed to process MPD discount validation at NB +30
     */
    private void jobsNBplus30runNoChecks() {
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.aaaAutomatedProcessingInitiationJob);
        JobUtils.executeJob(Jobs.automatedProcessingRatingJob);
        JobUtils.executeJob(Jobs.automatedProcessingRunReportsServicesJob);
        JobUtils.executeJob(Jobs.automatedProcessingIssuingOrProposingJob);
        JobUtils.executeJob(Jobs.automatedProcessingStrategyStatusUpdateJob);
        JobUtils.executeJob(Jobs.automatedProcessingBypassingAndErrorsReportGenerationJob);
        log.info("Current application date: " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
        TimeSetterUtil.getInstance().adjustTime();
    }

    /**
     *
     * @param policyNumber policy number in test
     * @param rowCount which row of the table (how many transactions in history)
     * @param value value to be present in Reason field
     * @param softly for assertions
     */
    private void transactionHistoryRecordCountCheck(String policyNumber, int rowCount, String value, ETCSCoreSoftAssertions softly) {
        PolicySummaryPage.buttonTransactionHistory.click();
        softly.assertThat(PolicySummaryPage.tableTransactionHistory).hasRows(rowCount);

        String valueShort = "";
        if (!StringUtils.isEmpty(value)) {
            valueShort = value.substring(0, 20);
            assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").getHintValue()).contains(value);
        }
        softly.assertThat(PolicySummaryPage.tableTransactionHistory.getRow(1).getCell("Reason").getValue()).contains(valueShort);

        String transactionHistoryQuery = "select * from(\n"
                + "select pt.TXREASONTEXT\n"
                + "from PolicyTransaction pt\n"
                + "where POLICYID in \n"
                + "        (select id from POLICYSUMMARY \n"
                + "        where POLICYNUMBER = '%s')\n"
                + "    order by pt.TXDATE desc)\n"
                + "    where rownum=1";
        softly.assertThat(DBService.get().getValue(String.format(transactionHistoryQuery, policyNumber)).orElse(StringUtils.EMPTY)).isEqualTo(value);
    }

    /**
     * @info sets the time back to system time for reduction in discount
     */
    protected void setTimeToToday() {
        log.info("Current application date: " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
        TimeSetterUtil.getInstance().adjustTime();
    }

    /**
     * Preconfigured pairwise scenarios for testing Unquoted or Manual Add scenarios. Generated using AllPairs.exe
     * @return List of scenarios to be iterated through.
     */
    private ArrayList<HashMap <mpdPolicyType, Boolean>> getUnquotedManualScenarios(){
        ArrayList<HashMap <mpdPolicyType, Boolean>> scenarioList = new ArrayList<>();
        /*
        Pair Testing Scenario
        Scenario	Home	Renters	Condo	Life	Motorcycle
        1	        Yes	    Yes 	Yes 	Yes 	Yes
        2	        Yes 	No  	No  	No  	No
        3	        No  	Yes 	No  	Yes 	No
        4	        No  	No  	Yes 	No  	Yes
        5	        ~Yes	Yes 	Yes 	No  	No
        6	        ~Yes	No  	No  	Yes 	Yes
        7	        No  	No  	No  	No  	No
         */

        String currentState = getState();

        // Scenario 1
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, true);
                put(mpdPolicyType.condo, true);
                put(mpdPolicyType.life, true);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, true);
                }
            }
        });

        // Scenario 2
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, false);
                }
            }
        });

        // Scenario 3
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, false);
                put(mpdPolicyType.renters, true);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, true);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, false);
                }
            }
        });

        // Scenario 4
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, false);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, true);
                put(mpdPolicyType.life, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, true);
                }
            }
        });

        // Scenario 5
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, true);
                put(mpdPolicyType.condo, true);
                put(mpdPolicyType.life, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, false);
                }
            }
        });

        // Scenario 6
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, true);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, true);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, true);
                }
            }
        });

        // Scenario 7
        scenarioList.add(new HashMap<mpdPolicyType, Boolean>() {
            {
                put(mpdPolicyType.home, false);
                put(mpdPolicyType.renters, false);
                put(mpdPolicyType.condo, false);
                put(mpdPolicyType.life, false);

                if (motorcycleSupportedStates.contains(currentState)) {
                    put(mpdPolicyType.motorcycle, false);
                }
            }
        });

        return scenarioList;
    }

    /**
     * Sets the unquoted policy checkboxes based of passed in checkboxMap.
     * @param checkboxMap is what to set each checkbox to. Expects all 5 product keys with bool value where true checks and false unchecks.
     */
    private void setUnquotedCheckboxes(HashMap <mpdPolicyType, Boolean> checkboxMap)throws IllegalArgumentException{

        // Only some states supports MC. If in the list, only check against mpdPolicyType length. Otherwise subtract 1 so no error thrown.
        int adjustForMotorcycle = motorcycleSupportedStates.contains(getState()) ? 0 : 1 ;

        // Check values
        if (checkboxMap.size() != mpdPolicyType.values().length - adjustForMotorcycle){
            throw new IllegalArgumentException("setUnquotedCheckboxes requires that every policy type has a boolean included. " +
                    "Make sure that all values in mpdPolicyType enum are present with associated booleans for checkboxMap");
        }

        for (mpdPolicyType fillInCheckbox : checkboxMap.keySet()) {

            setUnquotedCheckbox(fillInCheckbox, checkboxMap.get(fillInCheckbox));
        }
    }

    /**
     * Removes all policies from the Other AAA Products Owned table.
     */
    public void removeAllOtherAAAProductsOwnedTablePolicies(){
        List<Row> rows = getGeneralTab_OtherAAAProductTable().getRows();

        int zeroBasedRowIterator = rows.size() - 1;

        // Start at end of list since table gets smaller
        for (int i = zeroBasedRowIterator; i >= 0; i-- ){
            // Uses cell index due to column not labelled
            rows.get(i).getCell(7).controls.links.get("Remove").click(Waiters.AJAX);
        }
    }
}
