package aaa.modules.regression.sales.auto_ss.functional;

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
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestMultiPolicyDiscountAbstract;
import aaa.utils.StateList;
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

@StateList(statesExcept = Constants.States.CA)
public class TestMultiPolicyDiscount extends TestMultiPolicyDiscountAbstract {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }


    //public enum mpdPolicyType{
    //    home, renters, condo, life, motorcycle
    //}

    public static List<String> _listOfMPDTableColumnNames = Arrays.asList("Policy Number / Address", "Policy Type", "Customer Name/DOB", "Expiration Date", "Status", "MPD");
    public static List<String> _listOfMPDSearchResultsTableColumnNames = Arrays.asList("Customer Name/Address", "Date of Birth", "Policy Type", "Other AAA Products/Policy Address", "Status", "Select");
    public static final String _XPATH_TO_ALL_SEARCH_RESULT_CHECKBOXES = "*//input[contains(@id, 'customerSelected')]";

    // Add more states here if they get MC policy support.
    private ArrayList<String> motorcycleSupportedStates = new ArrayList<>(Arrays.asList(Constants.States.AZ));

    private GeneralTab _generalTab = new GeneralTab();
    private DriverTab _driverTab = new DriverTab();
    private ErrorTab _errorTab = new ErrorTab();
    private PremiumAndCoveragesTab _pncTab = new PremiumAndCoveragesTab();
    private DocumentsAndBindTab _documentsAndBindTab = new DocumentsAndBindTab();
    private PurchaseTab _purchaseTab = new PurchaseTab();

    /**
     *  Creates a policy with MPD discount
     *  Runs NB +30 jobs for MPD discount validation
     *  Discount is removed due to non-active products found and removes discount
     *  Reason in transaction history set to "Discount validation failure, policy information updated."
     * @param state the test will run against.
     * @author Robert Boles - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Provide 'Reason' type for a MTC to show generic wording when MPD discount is added/removed/change")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-29273")
    public void pas29273_updateReasonMPDRemoval(@Optional("") String state) {
        pas29273_updateReasonMPDRemoval_Template(state);
        setTimeToToday();
    }

    /**
     * Make sure various combos of Unquoted Other AAA Products rate properly and are listed in the UI
     * on P&C Page as well as in View Rating Details. AC3
     * @param state the test will run against.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23983")
    public void pas23983_MPD_unquoted_rate_and_show_discounts(@Optional("") String state) {
        pas23983_MPD_unquoted_rate_and_show_discounts_Template(state);
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
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-21481")
    public void pas_21481_MPD_Unquoted_Companion_Product_AC2_AC3(@Optional("") String state) {
        pas_21481_MPD_Unquoted_Companion_Product_AC2_AC3_Template(state);
    }



    /**
     * This tests that when unquoted HO policies are checked, that refresh button returned policies replace them in the table and
     * the unquoted options for those become disabled.
     * @param state the test will run against.
     * @scenario
     * Prereqs: enterpriseSearchService.enterpriseCustomersSearchUri setup to return all 3 homeowner types on refresh.
     * 1. Using standard test data, create customer, start auto quote, fill up to general tab with default data.
     * 2. Check unquoted Home, Condo, and Renters.
     * 3. Add second NI that has correct response pattern to get 3 property policies back. Click the refresh button.
     * 4. Verify the the MDM returned policies replace the unquoted options. (Based on prereqs, should be all HO)
     * 5. Verify the checkboxes are disabled for Home, Renters, and Condo.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Rate SS Auto with Quoted/Unquoted Products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-21481")
    public void pas_21481_MPD_Unquoted_Companion_Product_AC5(@Optional("") String state) {

        pas_21481_MPD_Unquoted_Companion_Product_AC5_Template(state);
    }

    /**
     * This test validates that New Business scenarios that have unquoted options checked result in error at bind time.
     * @param state the test will run against.
     * @scenario PAS-18315 Test 1
     * 1. Create new customer with default test data.
     * 2. Create new quote checking the current scenario boxes that are marked yes.
     * 3. Finish running through the quote and attempt to bind.
     * 4. Verify all scenarios but #7 block binding with hard stop error message: Policy cannot be bound with an unquoted companion policy.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Prevent Unquoted Bind at NB")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-18315")
    public void pas18315_CIO_Prevent_Unquoted_Bind_NB(@Optional("") String state) {
        pas18315_CIO_Prevent_Unquoted_Bind_NB_Template(state);
    }

    /**
     * This test validates the endorsement scenario with unquoted options checked result in error at bind time.
     * @param state the test will run against.
     * @scenario PAS-18315 Test 2
     * 1. Bind policy with no MPD.
     * 2. Create an endorsement
     * 3. Check all unquoted checkboxes
     * 4. Attempt to complete the endorsement
     * 5. Verify error message stops you from completing endorsement
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Prevent Unquoted Bind during Endorsment")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-18315")
    public void pas18315_CIO_Prevent_Unquoted_Bind_Endorsment(@Optional("") String state) {
        pas18315_CIO_Prevent_Unquoted_Bind_Endorsment_Template(state);
    }

    /**
     * This test validates the Amended Renewal scenario with unquoted options checked result in error at bind time.
     * @param state the test will run against.
     * @scenario PAS-18315 Test 3
     * 1. Bind policy with no MPD.
     * 2. Create and rate renewal image. Create an endorsement on renewal image (testing UI lockout so no need to run the timechange job execution process).
     * 3. Check all unquoted checkboxes
     * 4. Attempt to complete the endorsement
     * 5. Verify error message stops you from completing endorsement
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Prevent Unquoted Bind during Amended Renewal")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-18315")
    public void pas18315_CIO_Prevent_Unquoted_Bind_Amended_Renewal(@Optional("") String state) {
        pas18315_CIO_Prevent_Unquoted_Bind_Amended_Renewal_Template(state);
    }

    /**
     * This test validates that removing named insureds without rating results in error message at bind time.
     * @param state the test will run against.
     * @scenario
     * 1. Create quote with 2 NIs
     * 2. Remove one of the NI (NO mpd data returned)
     * 3. Navigate to Doc and Bind tab and bind
     * 4. Verify a hard stop error occurs directing user to Re-Rate the policy.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Removing a NI and associated companion products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC1_1(@Optional("") String state) {
        pas_3622_CIO_Remove_NI_Companion_AC1_1_Template(state);
    }

    /**
     * This test validates that removing named insureds without rating results in error message at bind time.
     * @param state the test will run against.
     * @scenario
     * 1. Create quote with 2 NI (one of the NI is ELASTIC_QUOTED)
     * 2. Populate MPD table via refresh
     * 3. Remove ELASTIC_QUOTED
     * 4. Re trigger refresh (Table will now be empty)
     * 5. Bind
     * 6. Verify a hard stop error occurs directing user to Re-Rate the policy.
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Removing a NI and associated companion products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC1_2(@Optional("") String state) {
        pas_3622_CIO_Remove_NI_Companion_AC1_2_Template(state);
    }

    /**
     * This test validates that removing named insureds on endorsements does not refresh MPD table.
     * @param state the test will run against.
     * @scenario
     * 1. Bind a policy with 2 NI (one of the NI is REFRESH_P) with MPD table populated
     * 2. Create Endorsement
     * 3. Remove REFRESH_P
     * 4. Verify Table does not refresh when removing REFRESH_P (Data will stay Peter Parker instead of reverting to default).
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Removing a NI and associated companion products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC2_1(@Optional("") String state) {
        pas_3622_CIO_Remove_NI_Companion_AC2_1_Template(state);
    }

    /**
     * This test validates that removing named insureds on amended renewals does not refresh MPD table.
     * @param state the test will run against.
     * @scenario
     * 1. Bind a policy with 2 NI (one of the NI is REFRESH_P) with MPD table populated
     * 2. Create Renewal Image
     * 3. Remove REFRESH_P
     * 4. Verify Table does not refresh when removing REFRESH_P (Data will stay Peter Parker instead of reverting to default).
     * @author Brian Bond - CIO
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Removing a NI and associated companion products")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3622")
    public void pas_3622_CIO_Remove_NI_Companion_AC2_2(@Optional("") String state) {
        pas_3622_CIO_Remove_NI_Companion_AC2_2_Template(state);
    }

    /**
     * This test validates that removing MPD policies removes discounts during a Flat (Same day) Endorsement
     * @scenario
     * 1. Create/Bind an Auto SS policy with Home, Condo, Renters, Motorcycle(if AZ), and Life insurance added.
     * 2. Start a Flat (same as effective date) endorsement
     * 3. Navigate to the Premiums & Coverages (P&C) tab and note the premium, Discounts & Surcharges sections, and the View Rating Details (VRD).
     * 4. Navigate back to the General Tab and remove all the discounts from MPD table.
     * 5. Navigate back to the P&C tab and Calculate Premium.
     * Note: The automation actually opens up another endorsement to grab the final values rather than adding new code to get the proposed values.
     * @author Brian Bond - CIO
     * @param state to override.
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Remove MPD flat endorsement when companion product is removed")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28659")
    public void pas28659_Discount_Removed_Flat_Endorsement(@Optional("") String state) {
        run_pas28659_DiscountRemovalTest(EndorsementType.Flat);
    }

    /**
     * This test validates that removing MPD policies removes discounts during a Future Dated (more than 24 hours after effective date) Endorsement
     * @scenario
     * 1. Create/Bind an Auto SS policy with Home, Condo, Renters, Motorcycle(if AZ), and Life insurance added.
     * 2. Start a Future Dated endorsement (after effective date) endorsement.
     * 3. Navigate to the Premiums & Coverages (P&C) tab and note the premium, Discounts & Surcharges sections, and the View Rating Details (VRD).
     * 4. Navigate back to the General Tab and remove all the discounts from MPD table.
     * 5. Navigate back to the P&C tab and Calculate Premium.
     * Note: The automation actually opens up another endorsement to grab the final values rather than adding new code to get the proposed values.
     * @author Brian Bond - CIO
     * @param state to override.
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Remove MPD Future-Dated endorsement when companion product is removed")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28659")
    public void pas28659_Discount_Removed_FutureDated_Endorsement(@Optional("") String state) {
        run_pas28659_DiscountRemovalTest(EndorsementType.FutureDated);
    }

    /**
     * This test validates that removing MPD policies removes discounts during a Future Dated (more than 24 hours after effective date) Endorsement
     * @scenario
     * 1. Create/Bind an Auto SS policy with Home, Condo, Renters, Motorcycle(if AZ), and Life insurance added.
     * 2. Move the date to New Business plus 2 days (NB+2) and start an endorsement.
     * 3. Navigate to the Premiums & Coverages (P&C) tab and note the premium, Discounts & Surcharges sections, and the View Rating Details (VRD).
     * 4. Navigate back to the General Tab and remove all the discounts from MPD table.
     * 5. Navigate back to the P&C tab and Calculate Premium.
     * Note: The automation actually opens up another endorsement to grab the final values rather than adding new code to get the proposed values.
     * @author Brian Bond - CIO
     * @param state to override.
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Remove MPD Mid-Term endorsement when companion product is removed")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-28659")
    public void pas28659_Discount_Removed_MidTerm_Endorsement(@Optional("") String state) {
        run_pas28659_DiscountRemovalTest(EndorsementType.MidTerm);
    }

    /**
     * This test validates that removing named insureds without rating results in error message at bind time.
     * @param endorsementType What scenario to run.
     */
    private void run_pas28659_DiscountRemovalTest(EndorsementType endorsementType){

        TestData testData = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData")
                .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()))
                .mask(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel()));

        run_pas28659_DiscountRemovalTest_Template(testData, endorsementType);
    }

    /**
     * Will check that property, life, and motorcycle (if applicable) are present/not present depending on discountExpected.
     * @param mpdDiscounts List of discounts that were present in XML. See parseXMLDocMPDList() for more info.
     * @param discountExpected Property, Life, and Motorcycle if applicable are there if True, not if False.
     */
    private static void validateAllMPDiscounts(List<String> mpdDiscounts, Boolean discountExpected){

        // Only one of the property discounts will be listed.
        Boolean homeDiscount = mpdDiscounts.contains("Home") ||
                mpdDiscounts.contains("Condo") ||
                mpdDiscounts.contains("Renters");

        if (discountExpected) {

            assertThat(homeDiscount).isTrue();
            assertThat(mpdDiscounts.contains("Life")).isTrue();

            if (getState().equals("AZ")) {
                assertThat(mpdDiscounts.contains("Motorcycle")).isTrue();
            }

        }else{
            assertThat(homeDiscount).isFalse();
            assertThat(mpdDiscounts.contains("Life")).isFalse();

            if (getState().equals("AZ")) {
                assertThat(mpdDiscounts.contains("Motorcycle")).isFalse();
            }
        }
    }

    /**
     * Takes value from XML PlcyDiscDesc and parses it into a list with MPD policies separated individually for assertions.
     * @param plcyDiscDesc This is usually pulled from the following method getAHDRXXValueFromNodeName("PlcyDiscDesc", XML)
     * @return List of type String that contains MPD discounts as individual items.
     */
    public static List<String> parseXMLDocMPDList(String plcyDiscDesc){
        // Typical string this parses: "Loyalty Discount;Membership Discount;Multi-Policy Discount (Life, Home);Payment Plan Discount"

        String[] discounts = plcyDiscDesc.split(";");

        String mpdRaw = "";

        // Locate element with Multi-Policy Discount
        for (String discount : discounts){
            if (discount.startsWith("Multi-Policy Discount")){
                mpdRaw = discount;
                break;
            }
        }

        // Remove all but the policy types, and split on the comma.
        String[] mpdParsed = mpdRaw.replace("Multi-Policy Discount (", "").replace(")","").split(",");

        // Trim off white space on all elements in list.
        for (int i = 0; i < mpdParsed.length; i++){
            mpdParsed[i] = mpdParsed[i].trim();
        }

        return Arrays.asList(mpdParsed);
    }


    /**
     * Pulls a specific value from AHDRXX.
     * @param nodeName The name that houses the value in the XML you are looking for.
     * @param xml DB value generally retrieved through: AAAMultiPolicyDiscountQueries.getDecPage(policyNumber).orElse("Null");
     * @return The String value associated with specified nodeName.
     */
    public static String getAHDRXXValueFromNodeName(String nodeName, String xml){

        // Find element with text value of nodename, walk back to parent.
        String xpathString  = "//* [.='"+ nodeName + "'] /..";

        String result = "No results returned.";

        try {
            XPathFactory xpathfactory = XPathFactory.newInstance();
            Document xmlDoc = loadXMLFromString(xml);
            XPathExpression expr = xpathfactory.newXPath().compile(xpathString);
            NodeList nodes = (NodeList) expr.evaluate(xmlDoc, XPathConstants.NODESET);

            if (nodes.getLength() > 1 || nodes.getLength() == 0){
                CustomAssertions.fail("Found invalid results.");
            }

            // Return first child which contains the actual value we want to get.
            result = nodes.item(0).getFirstChild().getTextContent();

        }catch (XPathExpressionException E){
            CustomAssertions.fail("Couldn't evaluate Xpath.", E);
        }

        return result;
    }

    /**
     * Transforms a string into an XML Document file
     * @param xml
     * @return org.w3c.dom.Document for later parsing
     * @throws NullPointerException
     */
    public static Document loadXMLFromString(String xml) throws NullPointerException {

        Document doc;

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
            doc = builder.parse(inputStream);
        } catch (Exception E){
            throw new NullPointerException("Could not parse XML" + E);
        }
        return doc;
    }

    /**
     * This test is provides coverage for validating that the Under Writer rerate rule is thrown as an error whenever the following conditions are met: <br>
     *     1. A rated quote has an MPD element edited (policy type or policy number). <br>
     *     2. A rated quote has an MPD element added. <br>
     *     3. A rated quote has an MPD element removed.
     * @param state
     * @author Tyrone Jemison - CIO
     * @runtime 4 minutes
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Trigger Re-rate event when companion policies are edited or removed")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24021")
    public void pas24021_MPD_ValidateRerateRuleFires(@Optional("") String state) {
        pas24021_MPD_ValidateRerateRuleFiresTemplate(state);
    }



    /**
     * Validates that the MPD Companion Validation Error occurs when manually adding a 'Home/Renters/Condo' MPD policy to a quote.
     * @param state
     * @author Tyrone Jemison - CIO
     * @Runtime 2min
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas3649_MPD_ValidateEligibilityRuleFires_Home(@Optional("") String state) {
        doMPDEligibilityTest("Home");
    }

    /**
     * Validates that the MPD Companion Validation Error DOES NOT occur when manually adding a 'Life/Motorcycle' MPD policy to a quote.
     * @param state
     * @author Tyrone Jemison - CIO
     * @Runtime 2min
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas3649_MPD_ValidateEligibilityRuleFires_Life(@Optional("") String state) {
        doMPDEligibilityTest("Life");
    }

    /**
     * Validates that the MPD Companion Validation Error occurs when manually adding a 'Home/Renters/Condo' MPD policy to a MidTerm Endorsement.
     * @param state
     * @author Tyrone Jemison - CIO
     * @Runtime 3min
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas3649_MPD_ValidateEligibility_MidTerm_Renters(@Optional("") String state) {
        doMPDEligibilityTest_MidTerm(false, "Renters");
    }

    /**
     * Validates that the MPD Companion Validation Error occurs when manually adding a 'Home/Renters/Condo' MPD policy to a Renewal Image.
     * @param state
     * @author Tyrone Jemison - CIO
     * @Runtime 8min
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas3649_MPD_ValidateEligibility_Renewal_Home(@Optional("") String state) {
        doMPDEligibilityTest_Renewal("Condo");
    }



    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Need ability to prevent MTE bind with MPD when policy has quoted companion products.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23456")
    public void pas23456_MPD_Prevent_MTEBind(@Optional("") String state) {
        doMTEPreventBindTest(false, "Home");
    }

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: Need ability to prevent MTE bind with MPD when policy has quoted companion products.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-23456")
    public void pas23456_MPD_Allow_MTEBind(@Optional("") String state) {
        doMTEAllowBindTest(false, "Home");
    }

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas23456_MPD_Prevent_Renewal(@Optional("") String state) {
        doMTEPreventBindTest_Renewals("Renters", false);
    }

    /**
     * Validates that a NB policy can be bound when adding a System-Validated Home policy.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "MPD Validation Phase 3: UW Eligibility Rule on Manually Adding a Companion Policy.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-24729")
    public void pas23456_MPD_Allow_NBBindWithSystemValidatedPolicy(@Optional("") String state) {
        TestData testData = getPolicyTD();
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("ELASTIC_QUOTED");
        otherAAAProductsSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.
        policy.getDefaultView().fillFromTo(testData, GeneralTab.class, PurchaseTab.class, true);
        PurchaseTab.btnApplyPayment.click();
        Page.dialogConfirmation.buttonYes.click();
        CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus.getValue().contains("Active")).isTrue();
    }



    /**
     * This test validates that adding an unquoted HOME companion product appears on a generated AH11AZ document. <br>
     *     This test has been disabled until new DOCGEN process has been implemented.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC1(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);
        getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME).setValue(true);
        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        //TODO: Add new DocGen assertion methods here.
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Home)");
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "HOME-UNQUOTED");
    }

    /**
     * This test validates that adding an unquoted RENTERS companion product appears on a generated AH11AZ document. <br>
     *      *     This test has been disabled until new DOCGEN process has been implemented.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC1dot1(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);
        getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS).setValue(true);
        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        //TODO: Add new DocGen assertion methods here.
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Renters)");
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "RENTERS-UNQUOTED");
    }

    /**
     * This test validates that adding an unquoted CONDO companion product appears on a generated AH11AZ document. <br>
     *      *     This test has been disabled until new DOCGEN process has been implemented.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC1dot2(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);
        getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO).setValue(true);
        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        //TODO: Add new DocGen assertion methods here.
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Condo)");
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "CONDO-UNQUOTED");
    }

    /**
     * This test validates that adding multiple unquoted companion product appears on a generated AH11AZ document. <br>
     *      *     This test has been disabled until new DOCGEN process has been implemented.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC1dot3(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);
        getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME).setValue(true);
        getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO).setValue(true);
        getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS).setValue(true);
        getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE).setValue(true);
        getUnquotedCheckBox(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE).setValue(true);
        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        //TODO: Add new DocGen assertion methods here.
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Life, Motorcycle, Home)");
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "HOME-UNQUOTED");
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "LIFE-UNQUOTED");
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "MOTORCYCLE-UNQUOTED");
    }

    /**
     * This test validates that two quoted companion products of different types appear on a generated AH11AZ document. <br>
     *      *     This test has been disabled until new DOCGEN process has been implemented.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC2(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Quoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);

        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("ELASTIC_QUOTED");
        otherAAAProductsSearchTable_addSelected(0);
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("ELASTIC_QUOTED");
        otherAAAProductsSearchTable_addSelected(1);

        String policyTypeMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE.getLabel();
        String policyNumberMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_NUMBER.getLabel();

        // Find row in MPD table matching policyType, then pull the policy num / address cell out of it to assert on. Remove address with split.
        String homeCompanionPolicyNumberColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.home.toString())
                .getCell(policyNumberMetaDataLabel)
                .getValue().split("\\n")[0];

        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        //TODO: Add new DocGen assertion methods here.
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Home)");
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, homeCompanionPolicyNumberColumnValue); //"QAZH3206557376"); //Asserts that the Mockwire Home policy shows up instead of the Renters policy.
    }

    /**
     * This test validates that two agent entered companion products of same types appear on a generated AH11AZ document. <br>
     *      *     This test has been disabled until new DOCGEN process has been implemented.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC3(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);

        otherAAAProducts_SearchByPolicyNumber("Home", "NOT_FOUND");
        otherAAAProducts_ManuallyAddPolicyAfterNoResultsFound("Home", "TestHome_FirstAdded");
        otherAAAProducts_SearchByPolicyNumber("Home", "NOT_FOUND");
        otherAAAProducts_ManuallyAddPolicyAfterNoResultsFound("Home", "TestHome_SecondAdded");

        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        //TODO: Add new DocGen assertion methods here.
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Home)");
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "TestHome_FirstAdded"); //Asserts that the Mockwire Home policy shows up instead of the Renters policy.
    }

    /**
     * This test validates that ineligible companion products do not appear on a generated AH11AZ document. <br>
     *      *     This test has been disabled until new DOCGEN process has been implemented.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC4(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);

        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMER_NE");
        otherAAAProductsSearchTable_addSelected(0);
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMER_NE");
        otherAAAProductsSearchTable_addSelected(1);
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMER_NE");
        otherAAAProductsSearchTable_addSelected(2);

        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        //TODO: Add new DocGen assertion methods here.
        //String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, document.getId(), AaaDocGenEntityQueries.EventNames.ENDORSEMENT_ISSUE);
        //try {
        //    CustomAssertions.assertThat(DocGenHelper.getDocument(document, query).toString().contains("Multi-Policy Discount")).isFalse();
        //}catch(NoSuchElementException ex){
        //    CustomAssertions.assertThat(ex).hasMessage("No value present");
        //}
    }

    /**
     * This test validates that an eligible home, life and motorcycle companion products appear on a generated AH11AZ document. <br>
     *      *     This test has been disabled until new DOCGEN process has been implemented.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC5(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);

        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("ELASTIC_QUOTED");
        otherAAAProductsSearchTable_addSelected(0);
        otherAAAProducts_SearchAndManuallyAddCompanionPolicy("Motorcycle", "NOT_FOUND");
        otherAAAProducts_SearchByPolicyNumber("Life", "NOT_FOUND");
        otherAAAProducts_ManuallyAddPolicyAfterNoResultsFound( "Life", "TestLife");

        String policyTypeMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE.getLabel();
        String policyNumberMetaDataLabel = AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_NUMBER.getLabel();

        // Find row in MPD table matching policyType, then pull the policy num / address cell out of it to assert on. Remove address with split.
        String homeCompanionPolicyNumberColumnValue =_generalTab.getOtherAAAProductTable().getRowContains(
                policyTypeMetaDataLabel,mpdPolicyType.home.toString())
                .getCell(policyNumberMetaDataLabel)
                .getValue().split("\\n")[0];

        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        //TODO: Add new DocGen assertion methods here.
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Motorcycle, Life, Home)");

        // Checking Affinity Group Section for Listed Policies.
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, homeCompanionPolicyNumberColumnValue);
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "NOT_FOUND");
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "TestLife");
    }

    /**
     * This test validates that an ineligible companion product does not appear on a generated AH11AZ document, but the life policy does. <br>
     *      *     This test has been disabled until new DOCGEN process has been implemented.
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = false, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-22193,PAS-22901")
    public void pas22193_AH11AZDocGen_QAAC6(@Optional("AZ") String state){
        DocGenEnum.Documents document = DocGenEnum.Documents.AA11AZ;
        String policyNumber = "";
        AaaDocGenEntityQueries.EventNames event = AaaDocGenEntityQueries.EventNames.ADHOC_DOC_GENERATE;

        // Create AutoSS quote with Unquoted Home Companion Policy added.
        TestData td = getPolicyDefaultTD();
        createQuoteAndFillUpTo(td, GeneralTab.class, true);

        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMER_NE");
        otherAAAProductsSearchTable_addSelected(0);
        otherAAAProducts_SearchAndManuallyAddCompanionPolicy("Life", "TestLifePolicy");

        policy.getDefaultView().fillFromTo(td, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnGenerateDocuments.click();
        policyNumber = Tab.labelPolicyNumber.getValue();

        DocGenHelper.waitForDocumentsAppearanceInDB(document, policyNumber, event);
        //TODO: Add new DocGen assertion methods here.
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "Multi-Policy Discount (Life)");
        //DocGenHelper.DoesDocumentFromDBContainString(document, policyNumber, event, "TestLifePolicy");
    }

    /**
     *
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = true, groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-27241")
    public void pas27241_MPDPagination(@Optional("AZ") String state){

        int numberOfResultsRequiredForSuccessfulValidation = 6;
        // Handles getting us a policy and moves us up to our testing point, on the General Tab.
        createQuoteAndFillUpTo(getPolicyDefaultTD(), GeneralTab.class, false);

        // Test Results > 50 display error on UI.
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMERS_51");

        // Validate Error appears and count the number of results on the page.
        CustomAssertions.assertThat(_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.EXCEEDED_LIMIT_MESSAGE)).isPresent();
        CustomAssertions.assertThat(getSearchResultsCount()).isEqualTo(numberOfResultsRequiredForSuccessfulValidation);
        otherAAAProductsSearchTable_addSelected(new int[]{0, 1, 2, 3, 4, 5});

        // Test Results <= 50 DO NOT display error on UI.
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMER_GBY");

        // Validate Error does NOT appear and count the number of results on the page.
        CustomAssertions.assertThat(_generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.EXCEEDED_LIMIT_MESSAGE)).isAbsent();
        CustomAssertions.assertThat(getSearchResultsCount()).isEqualTo(numberOfResultsRequiredForSuccessfulValidation);
    }

    // CLASS METHODS

    /**
     * Returns Unquoted Checkbox control based on passed in data.
     * @param assetDescriptor AssetDescriptor for each checkbox.
     * @return Checkbox representing requested control.
     */
    public CheckBox getUnquotedCheckBox(AssetDescriptor<CheckBox> assetDescriptor){
        return _generalTab.getOtherAAAProductOwnedAssetList().getAsset(assetDescriptor);
    }

    /**
     * Removes all policies from the Other AAA Products Owned table.
     */
    public void removeAllOtherAAAProductsOwnedTablePolicies(){
        List<Row> rows = _generalTab.getOtherAAAProductTable().getRows();

        int zeroBasedRowIterator = rows.size() - 1;

        // Start at end of list since table gets smaller
        for (int i = zeroBasedRowIterator; i >= 0; i-- ){
            // Uses cell index due to column not labelled
            rows.get(i).getCell(7).controls.links.get("Remove").click(Waiters.AJAX);
        }
    }


    public void otherAAAProducts_SearchAndManuallyAddCompanionPolicy(String policyType, String policyNumber){
        otherAAAProducts_SearchByPolicyNumber(policyType, policyNumber);
        otherAAAProducts_ManuallyAddPolicyAfterNoResultsFound(policyType);
    }

    /**
     * Conducts a basic search using the input String as a policy number.
     * @param inputPolicyNumber
     */
    @Override
    protected void otherAAAProducts_SearchByPolicyNumber(String policyType, String inputPolicyNumber){
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getControlClass()).click();
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getControlClass()).setValue("Policy Number");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_TYPE.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_TYPE.getControlClass()).setValue(policyType);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getControlClass()).setValue(inputPolicyNumber);

        if (!policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel()) && !policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE.getLabel())){
            _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getControlClass()).click();
        }

    }

    /**
     * This method is used when viewing the Search Other AAA Products popup after searching via Policy Number. <br>
     * Clicks 'Add' button, unless provided instruction to change data.
     */
    @Override
    protected void otherAAAProducts_ManuallyAddPolicyAfterNoResultsFound(String policyType){
        if(policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME.getLabel()) || policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS.getLabel()) ||
                policyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO.getLabel())){

            _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_HOME_RENTERS_CONDO_BTN.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_HOME_RENTERS_CONDO_BTN.getControlClass()).click();

        }else{
            _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_MOTOR_OR_LIFE_BTN.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_MOTOR_OR_LIFE_BTN.getControlClass()).click();
        }
    }

    /**
     * This method is used when viewing the Search Other AAA Products popup after searching via Policy Number. <br>
     * Will simply click 'Add' button after changing policy data. <br>
     * @param policyType The type of policy being entered.
     * @param inputPolicyNumber The policy number to search for. This field also manipulates mockwire response results, if given a mapped string.
     */
    public void otherAAAProducts_ManuallyAddPolicyAfterNoResultsFound(String policyType, String inputPolicyNumber){
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_TYPE.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_TYPE.getControlClass()).setValue(policyType);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.POLICY_QUOTE_NUMBER.getControlClass()).setValue(inputPolicyNumber);

        otherAAAProducts_ManuallyAddPolicyAfterNoResultsFound(policyType);
    }

    /**
     * Used to search an MPD policy, via Customer Details. Applies provided string over 'First Name' <br>
     * All of the other fields are populated using 'Junk' data, allowing a tester to call the method using only the parameter that controls the wire-mock response.
     * @param searchFieldValue This variable is applied to the First Name field of the Customer Details Search and can manipulate response results. <br>
     */
    public void otherAAAProducts_SearchCustomerDetails_UsePrefilledData(String searchFieldValue){
        otherAAAProducts_SearchCustomerDetails(searchFieldValue, "JunkLastName", "01/01/1980", "JunkAddress", "JunkCity", "AZ", "JunkZip");
    }

    /**
     * Used to search an MPD policy, via Customer Details. <br>
     * @param firstName This parameter has been chosen to drive the search results/response. Edit this field with mapped MPD search string to manipulate which response comes back. <br>
     * @param lastName Customer Last Name. <br>
     * @param dateOfBirth Customer Date of Birth in 'mm/dd/yyyy' format. <br>
     * @param address Customer Street Address. <br>
     * @param city Customer City. <br>
     * @param zipCode Customer Zip Code. <br>
     */
    public void otherAAAProducts_SearchCustomerDetails(String firstName, String lastName, String dateOfBirth, String address, String city, String state, String zipCode){
        _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SEARCH_AND_ADD_MANUALLY.getControlClass()).click();
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BY.getControlClass()).setValue("Customer Details");
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ZIP_CODE.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ZIP_CODE.getControlClass()).setValue(zipCode);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.FIRST_NAME.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.FIRST_NAME.getControlClass()).setValue(firstName);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.LAST_NAME.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.LAST_NAME.getControlClass()).setValue(lastName);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.DATE_OF_BIRTH.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.DATE_OF_BIRTH.getControlClass()).setValue(dateOfBirth);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADDRESS_LINE_1.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADDRESS_LINE_1.getControlClass()).setValue(address);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.CITY.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.CITY.getControlClass()).setValue(city);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.STATE.getLabel(), (AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.STATE.getControlClass())).setValue(state);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.SEARCH_BTN.getControlClass()).click();
    }

    public Button otherAAAProducts_getRefreshButton() {
        return _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH.getLabel(), Button.class);
    }

    /**
     * Used to access the selectable checkbox directly
     * @param index
     * @return
     */
    public CheckBox otherAAAProductsSearchTable_getSelectBoxByIndex(int index){
        index = otherAAAProductsTableIndexWatchDog(index);
        return new CheckBox(By.id("autoOtherPolicySearchForm:elasticSearchResponseTable:" + String.valueOf(index) + ":customerSelected"));
    }

    /**
     * Given an index beginning from 0, this will select and add the chosen system returned policy.
     * @param index
     */
    public void otherAAAProductsSearchTable_addSelected(int index){
        new CheckBox(By.id("autoOtherPolicySearchForm:elasticSearchResponseTable:" + String.valueOf(index) + ":customerSelected")).setValue(true);
        _generalTab.getSearchOtherAAAProducts().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_SELECTED_BTN.getLabel(), AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_SELECTED_BTN.getControlClass()).click();
    }



    /**
     * Returns 'Policy Number / Address', 'Date of Birth', etc. data via the given row index.
     * @param index First row of data begins at index 1, not index 0.
     * @return
     */
    public String otherAAAProductsTable_viewData(String columnName, int index) {
        index = otherAAAProductsTableIndexWatchDog(index);
        return _generalTab.getOtherAAAProductTable().getColumn(columnName).getCell(index).getValue();
    }

    /**
     * Returns 'Policy Number / Address', 'Date of Birth', etc. data via the given row index.
     * @param index First row of data begins at index 1, not index 0.
     * @return
     */
    public String otherAAAProductsSearchTable_viewData(String columnName, int index) {
        index = otherAAAProductsTableIndexWatchDog(index);
        return _generalTab.getManualSearchResultTable().getColumn(columnName).getCell(index).getValue();
    }

    public ArrayList<String> otherAAAProductsTable_viewAllRowDataByColumn(String columnName) {
        ArrayList<String> myStringArray = new ArrayList<>();
        for(int i = 1; i <= _generalTab.getOtherAAAProductTable().getRowsCount(); i++) {
            myStringArray.add(otherAAAProductsTable_viewData(columnName, i));
        }
        return myStringArray;
    }

    /**
     *
     * @param index_RowToGet Index begins at 1.
     * @return
     */
    public ArrayList<String> otherAAAProductsTable_viewAllColumnDataByRow(int index_RowToGet) {
        index_RowToGet = otherAAAProductsTableIndexWatchDog(index_RowToGet);
        ArrayList<String> myStringArray = new ArrayList<>();

        for(String columnName: _listOfMPDTableColumnNames){
            myStringArray.add(otherAAAProductsTable_viewData(columnName, index_RowToGet));
        }
        return myStringArray;
    }

    /**
     *
     * @param index_RowToGet Index begins at 1.
     * @return
     */
    public ArrayList<String> otherAAAProductsSearchTable_viewAllColumnDataByRow(int index_RowToGet) {
        index_RowToGet = otherAAAProductsTableIndexWatchDog(index_RowToGet);
        ArrayList<String> myStringArray = new ArrayList<>();

        for(String columnName: _listOfMPDSearchResultsTableColumnNames){
            myStringArray.add(otherAAAProductsSearchTable_viewData(columnName, index_RowToGet));
        }
        return myStringArray;
    }

    /**
     * Used to create a java object to represent a chosen companion policy, via index. <br>
     * This method is used to capture an element on the OtherAAAProducts table on the General Tab.
     * @param index_RowToGet Index begins at 1.
     * @return
     */
    public OtherAAAProductsTableElement otherAAAProductsTable_getTableRowAsObject(int index_RowToGet) {
        index_RowToGet = otherAAAProductsTableIndexWatchDog(index_RowToGet);
        ArrayList<String> dataAsArray = otherAAAProductsTable_viewAllColumnDataByRow(index_RowToGet);

        OtherAAAProductsTableElement _rowAsObject = new OtherAAAProductsTableElement(
                dataAsArray.get(0), dataAsArray.get(1), dataAsArray.get(2), dataAsArray.get(3), dataAsArray.get(4), dataAsArray.get(5)
        );
        return _rowAsObject;
    }

    /**
     * Used to create a java object to represent a chosen companion policy, via index. <br>
     * This method is used to capture an element when viewing search results in the OtherAAAProducts search table.
     * @param index_RowToGet Index begins at 1.
     * @return
     */
    public OtherAAAProductsSearchTableElement otherAAAProductsSearchResultsTable_getTableRowAsObject(int index_RowToGet){
        index_RowToGet = otherAAAProductsTableIndexWatchDog(index_RowToGet);
        ArrayList<String> dataAsArray = otherAAAProductsSearchTable_viewAllColumnDataByRow(index_RowToGet);

        OtherAAAProductsSearchTableElement _rowAsObject = new OtherAAAProductsSearchTableElement(
                dataAsArray.get(0), dataAsArray.get(1), dataAsArray.get(2), dataAsArray.get(3), dataAsArray.get(4)
        );

        return _rowAsObject;
    }

    /**
     * Used to silently correct improper index input to mpd methods. Some methods that involve MPD tables do not begin with index 0. <br>
     *     This method will catch an input of 0 and silently convert it to 1, which should be the row the user intended to access.
     * @param i The input integer.
     * @return If i = 0, returns 1.
     */
    protected int otherAAAProductsTableIndexWatchDog(int i){
        if(i==0){
            i = 1;
        }
        return i;
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

            setGeneralTab_OtherAAAProductsOwned_UnquotedCheckbox(fillInCheckbox, checkboxMap.get(fillInCheckbox));
        }
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

    private void doMPDEligibilityTest(String in_policyType){
        // Using default test data.
        TestData testData = getPolicyTD();

        // Add MPD Element manually (after no results found)
        createQuoteAndFillUpTo(testData, GeneralTab.class, true);
        otherAAAProducts_SearchAndManuallyAddCompanionPolicy(in_policyType, "NOT_FOUND");

        // Continue towards purchase of quote.
        policy.getDefaultView().fillFromTo(testData, GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnPurchase.click();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        validateMPDCompanionError(in_policyType);
    }

    private void doMPDEligibilityTest_MidTerm(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        openAppCreatePolicy();

        handleEndorsementType(bFlatEndorsement);

        otherAAAProducts_SearchAndManuallyAddCompanionPolicy(in_policyType, "NOT_FOUND");
        fillFromGeneralTabToErrorMsg();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        validateMPDCompanionError(in_policyType);
    }

    private void doMPDEligibilityTest_Renewal(String in_policyType){
        // Create Policy
        createPolicyAdvanceToRenewalImage();

        // In Renewal Image, Add MPD Element and Bind
        otherAAAProducts_SearchAndManuallyAddCompanionPolicy(in_policyType, "NOT_FOUND");
        fillFromGeneralTabToErrorMsg();

        // Validate UW Rule fires and requires at least level 1 authorization to be eligible to purchase.
        validateMPDCompanionError(in_policyType);
    }

    private void doMTEPreventBindTest(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        openAppCreatePolicy();

        handleEndorsementType(bFlatEndorsement);

        // Add MPD Element via Customer Search
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("ELASTIC_QUOTED");
        otherAAAProductsSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.

        fillFromGeneralTabToErrorMsg();

        // Validate error message appears.
        validateMTEBindError();
    }

    private void doMTEAllowBindTest(Boolean bFlatEndorsement, String in_policyType){
        // Create Policy and Initiate Endorsement
        openAppCreatePolicy();

        handleEndorsementType(bFlatEndorsement);

        // Add MPD Element via Customer Search
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMER_E");
        otherAAAProductsSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("CUSTOMER_NE");
        otherAAAProductsSearchTable_addSelected(1);

        fillFromGeneralTabToErrorMsg();

        // Validate error message appears.
        validateMTEBindErrorDoesNotOccur();
    }

    private void doMTEPreventBindTest_Renewals(String in_policyType, boolean bAmendedRenew){
        // Get into Renewal Image
        createPolicyAdvanceToRenewalImage();

        if(bAmendedRenew) {
            // Provide blank data for renewal image. Complete and save it.
            fillFromGeneralTabToErrorMsg();
            // From policy summary page, begin endorsement on the renewal image.
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        }

        // Add MPD Home element.
        otherAAAProducts_SearchCustomerDetails_UsePrefilledData("ELASTIC_QUOTED");
        otherAAAProductsSearchTable_addSelected(0); // Should be adding a HOME policy here. Can only grab by index, so must match.

        // Complete Endorsement.
        fillFromGeneralTabToErrorMsg();

        // Validate error message appears.
        validateMTEBindError();
    }

    private void createPolicyAdvanceToRenewalImage(){
        String policyNumber = openAppCreatePolicy();
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime _renewalImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
        mainApp().close();

        // Advance JVM to Image Creation Date
        TimeSetterUtil.getInstance().nextPhase(_renewalImageGenDate);
        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalImageRatingAsyncTaskJob);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

        // Go to Policy and Open Renewal Image
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        Tab.buttonGo.click();
        Tab.buttonOk.click();
        Page.dialogConfirmation.buttonOk.click();
    }

    private String openAppCreatePolicy(){
        TestData td = getPolicyDefaultTD();
        mainApp().open();
        createCustomerIndividual();
        return createPolicy(td);
    }

    private void validateMPDCompanionError(String thePolicyType){
        if (!thePolicyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel()) && !thePolicyType.equalsIgnoreCase(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE.getLabel())){
            new ErrorTab().verify.errorsPresent(true, ErrorEnum.Errors.MPD_COMPANION_VALIDATION);
        }else {
            CustomAssertions.assertThat(PolicySummaryPage.labelPolicyNumber.isPresent());
        }
    }

    private void validateMTEBindError(){
        new ErrorTab().verify.errorsPresent(true, ErrorEnum.Errors.AAA_SS02012019);
    }

    private void validateMTEBindErrorDoesNotOccur(){
        try{
            new ErrorTab().verify.errorsPresent(false, ErrorEnum.Errors.AAA_SS02012019);
        }catch(IstfException ex){
            CustomAssertions.assertThat(ex.getMessage()).isEqualToIgnoringCase("Column Code was not found in the table");
        }
    }

    private void fillFromGeneralTabToErrorMsg(){
        policy.getDefaultView().fillFromTo(getPolicyTD("Endorsement", "TestData_Empty_Endorsement"), GeneralTab.class, DocumentsAndBindTab.class, true);
        _documentsAndBindTab.btnPurchase.click();
        Page.dialogConfirmation.buttonYes.click();
    }

    private void handleEndorsementType(boolean bFlatEndorsement){
        if (bFlatEndorsement){
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        }else{
            policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        }
    }

    /***
     * This method will use an open-ended xpath to capture the total number of checkboxes visible in search results. <br>
     *     This value can be used to count the total number of results returned, due to the difficulty of navigating the MPD Table DOM.
     * @return
     */
    public int getSearchResultsCount(){
        List<WebElement> arrayOfCheckboxesFound = BrowserController.get().driver().findElements(By.xpath(_XPATH_TO_ALL_SEARCH_RESULT_CHECKBOXES));
        return arrayOfCheckboxesFound.size();
    }


    /**
     * @return Test Data for an AZ SS policy with no other active policies
     */
    @Override
    protected TestData getTdAuto() {
        return getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData")
                .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel()))
                .mask(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel()));
    }

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

    @Override
    protected ComboBox getGeneralTab_CurrentAAAMemberAsset() {
        return _generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER);
    }

    @Override
    protected TextBox getGeneralTab_MembershipNumberAsset(){
        return _generalTab.getAAAMembershipAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER);
    }

    @Override
    protected TextBox getGeneralTab_ContactInformation_EmailAsset(){
        return _generalTab.getContactInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.ContactInformation.EMAIL);
    }

    @Override
    protected Button getGeneralTab_OtherAAAProductsOwned_RefreshAsset(){
        return _generalTab.getOtherAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.REFRESH);
    }

    @Override
    protected Button getGeneralTab_OtherAAAProductsOwned_ManualPolicyAddButton(){
        return _generalTab.getSearchOtherAAAProducts().getAsset(
                AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.SearchOtherAAAProducts.ADD_SELECTED_BTN);
    }

    @Override
    protected ComboBox getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_PolicyTypeEditAsset(){
        return _generalTab.getListOfProductsRowsAssetList()
                .getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE_EDIT);
    }

    @Override
    protected TextBox getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_QuotePolicyNumberEditAsset(){
        return _generalTab.getListOfProductsRowsAssetList()
                .getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.QUOTE_POLICY_NUMBER_EDIT);
    }

    @Override
    protected Button getGeneralTab_OtherAAAProductsOwned_ListOfProductsRows_SaveBtnAsset(){
        return _generalTab.getListOfProductsRowsAssetList()
                .getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.SAVE_BTN);
    }

    @Override
    protected Table getErrorTab_TableErrors(){
        return _errorTab.tableErrors;
    }

    @Override
    protected Button getErrorTab_ButtonCancel(){
        return ErrorTab.buttonCancel;
    }

    @Override
    protected void errorTabOverrideErrors(ErrorEnum.Errors... errors) {
        _errorTab.overrideErrors(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.OTHER, errors);
    }

    @Override
    protected void errorTabOverride() {
        _errorTab.override();
    }

    @Override
    protected void errorTabOverrideAllErrors() {
        _errorTab.overrideAllErrors();
    }

    @Override
    protected Tab getGeneralTab(){
        return _generalTab;
    }

    @Override
    protected Tab getDriverTab(){
        return _driverTab;
    }

    @Override
    protected Tab getPremiumsAndCoveragesTab(){
        return _pncTab;
    }

    @Override
    protected Tab getDocumentsAndBindTab() { return _documentsAndBindTab; }

    @Override
    protected Tab getPurchaseTab(){ return _purchaseTab; }

    @Override
    protected Tab getErrorTab() { return _errorTab; }

    @Override
    protected void navigateToGeneralTab(){
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.GENERAL.get());
    }

    @Override
    protected void navigateToPremiumAndCoveragesTab(){
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
    }

    @Override
    protected void navigateToDocumentsAndBindTab(){
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
    }

    @Override
    protected TestData getPnCTab_RatingDetailsQuoteInfoData(){
        return  _pncTab.getRatingDetailsQuoteInfoData();
    }

    @Override
    protected void closePnCTab_ViewRatingDetails(){
        PremiumAndCoveragesTab.RatingDetailsView.buttonRatingDetailsOk.click();
    }

    @Override
    protected String getPnCTab_DiscountsAndSurcharges(){
        return PremiumAndCoveragesTab.discountsAndSurcharges.getValue();
    }

    @Override
    protected Button getPnCTab_BtnCalculatePremium() {
        return _pncTab.btnCalculatePremium();
    }

    @Override
    protected Button getDocumentsAndBindTab_BtnPurchase(){
        return DocumentsAndBindTab.btnPurchase;
    }

    @Override
    protected Button getDocumentsAndBindTab_ConfirmPurchase_ButtonYes(){
        return DocumentsAndBindTab.confirmPurchase.buttonYes;
    }

    @Override
    protected Button getDocumentsAndBindTab_ConfirmPurchase_ButtonNo(){
        return DocumentsAndBindTab.confirmPurchase.buttonNo;
    }

    @Override
    protected Table getGeneralTab_OtherAAAProductTable(){
        return _generalTab.getOtherAAAProductTable();
    }

    @Override
    protected Dollar getPnCTab_getPolicyCoveragePremium(){
        return _pncTab.getPolicyCoveragePremium();
    }

    @Override
    protected String getGeneralTab_PolicyTypeMetaDataLabel(){
        return AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.POLICY_TYPE.getLabel();
    }

    @Override
    protected String getGeneralTab_PolicyStatusMetaDataLabel(){
        return AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.STATUS.getLabel();
    }

    @Override
    protected String getGeneralTab_CustomerNameDOBMetaDataLabel(){
        return AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.ListOfProductsRows.CUSTOMER_NAME_DOB.getLabel();
    }

    @Override
    protected Button getErrorTab_ButtonOverrideAsset(){
        return _errorTab.buttonOverride;
    }

    @Override
    protected String getErrorTab_ErrorOverride_ErrorCodeValue(){
        return getErrorTab_TableErrors().getColumn(AutoSSMetaData.ErrorTab.ErrorsOverride.CODE.getLabel()).getValue().toString();
    }

    /**
     * Adds another named insured and fills out required data.
     * @param firstName is named insured's first name.
     * @param lastName is named insured's last name.
     * @param dateOfBirth is named insured's date of birth in mm/dd/yyyy format
     * @param livedHereLessThan3Years is "Yes" or "No" if named insured has lived at location for less than 3 years.
     * @param residence can be any option in the Residence drop down.
     */
    @Override
    public void addNamedInsured(String firstName, String lastName, String dateOfBirth, String livedHereLessThan3Years, String residence){
        // Click Add Insured Button
        _generalTab.getNamedInsuredInfoAssetList()
                .getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ADD_INSURED.getLabel(),
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.ADD_INSURED.getControlClass()).click(Waiters.AJAX);

        // Click cancel on the Named Insured Popup
        _generalTab.getNamedInsuredInfoAssetList()
                .getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_SEARCH_DIALOG.getLabel(),
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_SEARCH_DIALOG.getControlClass()).cancel();

        // First Name
        _generalTab.getNamedInsuredInfoAssetList().
                getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel(),
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getControlClass()).setValue(firstName);

        // Last Name
        _generalTab.getNamedInsuredInfoAssetList().
                getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel(),
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getControlClass()).setValue(lastName);

        // Date of Birth
        _generalTab.getNamedInsuredInfoAssetList().
                getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH.getLabel(),
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.INSURED_DATE_OF_BIRTH.getControlClass()).setValue(dateOfBirth);

        // Lived here less than 3 years
        _generalTab.getNamedInsuredInfoAssetList().
                getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS.getLabel(),
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS.getControlClass()).setValue(livedHereLessThan3Years);

        // Residence
        _generalTab.getNamedInsuredInfoAssetList().
                getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getLabel(),
                        AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getControlClass()).setValue(residence);
    }

    @Override
    protected void generalTab_RemoveInsured(int index){
        _generalTab.removeInsured(index);
    }

    /**
     * Returns Unquoted Checkbox control based on passed in data.
     * @param policyType Which checkbox to return checkbox.
     * @return Checkbox representing requested control.
     */
    @Override
    protected CheckBox getUnquotedCheckBox(mpdPolicyType policyType){

        CheckBox unquotedCheckBox = null;
        switch(policyType){
            case home:
                unquotedCheckBox = _generalTab.getOtherAAAProductOwnedAssetList().
                        getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME);
                break;

            case renters:
                unquotedCheckBox = _generalTab.getOtherAAAProductOwnedAssetList().
                        getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.RENTERS);
                break;

            case condo:
                unquotedCheckBox = _generalTab.getOtherAAAProductOwnedAssetList().
                        getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.CONDO);
                break;

            case life:
                unquotedCheckBox = _generalTab.getOtherAAAProductOwnedAssetList().
                        getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE);
                break;

            case motorcycle:
                unquotedCheckBox = _generalTab.getOtherAAAProductOwnedAssetList().
                        getAsset(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.MOTORCYCLE);
                break;
        }

        if (unquotedCheckBox == null){
            CustomAssertions.fail("getUnquotedCheckBox(mpdPolicyType policyType) Unsupported policy type " +
                    policyType.toString());
        }
        return unquotedCheckBox;
    }

    /**
     * Sets an individual checkbox to whatever is passed in.
     * @param policyType is which policy type unquoted box to fill in.
     * @param fillInCheckbox true = check, false = uncheck.
     */
    @Override
    protected void setGeneralTab_OtherAAAProductsOwned_UnquotedCheckbox(mpdPolicyType policyType, Boolean fillInCheckbox){

        switch (policyType){
            case condo:
                getUnquotedCheckBox(mpdPolicyType.condo).setValue(fillInCheckbox);
                break;

            case home:
                getUnquotedCheckBox(mpdPolicyType.home).setValue(fillInCheckbox);
                break;

            case renters:
                getUnquotedCheckBox(mpdPolicyType.renters).setValue(fillInCheckbox);
                break;

            case life:
                getUnquotedCheckBox(mpdPolicyType.life).setValue(fillInCheckbox);
                break;

            case motorcycle:
                getUnquotedCheckBox(mpdPolicyType.motorcycle).setValue(fillInCheckbox);
                break;
        }
    }
}