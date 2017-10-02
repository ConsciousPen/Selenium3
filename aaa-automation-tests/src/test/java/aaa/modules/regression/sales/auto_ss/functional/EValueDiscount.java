/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;


import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataManager;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import org.openqa.selenium.By;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.Dollar;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.table.Table;

public class TestEvalueDiscount extends AutoSSBaseTest {

    @DataProvider(name = "Evalue parameters")
    public static Object[][] evalueParams() {
        return new Object[][]{
                {"state", "AAAProductOwned_Active", "CurrentCarrierInformation", true, true, "Pending"},
                {"state", "AAAProductOwned_Active", "CurrentCarrierInformation", false, false, ""},
                {"state", "AAAProductOwned_No", "CurrentCarrierInformation", false, false, ""},
                {"state", "AAAProductOwned_Pending", "CurrentCarrierInformation", true, true, "Pending"},
                {"state", "AAAProductOwned_Pending", "CurrentCarrierInformation", false, false, ""},
                {"state", "AAAProductOwned_Active", "CurrentCarrierInformation_DayLapsedMore4", false, false, ""},
                {"state", "AAAProductOwned_Active", "CurrentCarrierInformation_BILimitLess", false, false, ""},
        };
    }

    /**
     * @author Viktoriia Lutsenko
     * @name Test presence/status of eValue discount on P&C and consolidated pages(Membership = Active, Evalue = Yes)
     * @scenario 1. Create customer
     * 2. Create active policy with next conditions:
     * TS1: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'Yes'
     * TS2: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
     * TS3: Current AAA Member = 'No', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
     * TS4: Current AAA Member = 'Membership Pending', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'Yes'
     * TS5: Current AAA Member = 'Membership Pending', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
     * TS6: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed > 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
     * TS7: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $15,000/$30,000, Apply eValue Discount  = 'No'
     * 3. Verify that on P&C page 'eValue Discount' is present in Discounts & Surcharges table (for TS1 and TS4) and 'eValue Discount' is absent in Discounts & Surcharges table (for TS2, TS3, TS5, TS6 and TS7) .
     * 4. Bind policy.
     * 5. Verify that 'eEvalue Status' = 'Pending' (for TS1 and TS4) and 'eEvalue Status' is empty (for TS2, TS3, TS5, TS6 and TS7) on Consolidated page.
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, dataProvider = "Evalue parameters")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testEvalueDiscount(@Optional("VA") String state, String MembershipStatus, String CurrentCarrier, boolean evalueIsSelected, boolean evalueIsPresent, String evalueStatus) {
        prefillEvalueTestData(MembershipStatus, CurrentCarrier);
        fillPremiumAndCoveragesTab(evalueIsSelected);
        fillDriverActivityReportsTab();
        TestData tdPolicyCreation = fillDocumentAndBindTab(evalueIsPresent);
        new PurchaseTab().fillTab(tdPolicyCreation).submitTab();

        validateEvalueStatus(evalueStatus);
        validatePolicyStatus();
    }

    private void prefillEvalueTestData(String aaaProductOwned, String currentCarrierInformation) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();

        CustomAssert.enableSoftMode();
        String validateAddressDialogKey = TestData.makeKeyPath(new PrefillTab().getMetaKey(), AutoSSMetaData.PrefillTab.VALIDATE_ADDRESS_DIALOG.getLabel());
        String currentCarrierInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());
        String policyInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel());
        String aaaProductOwnedKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

        TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData")
                .adjust(validateAddressDialogKey, getTestSpecificTD("ValidateAddressDialog"))
                .adjust(currentCarrierInformationKey, getTestSpecificTD(currentCarrierInformation))
                .adjust(policyInformationKey, getTestSpecificTD("PolicyInformation"))
                .adjust(aaaProductOwnedKey, getTestSpecificTD(aaaProductOwned));

        policy.getDefaultView().fillUpTo(tdPolicyCreation, PremiumAndCoveragesTab.class, true);
    }

    private void fillPremiumAndCoveragesTab(boolean eValueIsPresent) {
        if (eValueIsPresent) {
            new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
            PremiumAndCoveragesTab.buttonCalculatePremium.click();
            PremiumAndCoveragesTab.discountsAndSurcharges.verify.contains("eValue Discount");
        } else {
            CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("eValue Discount"));
        }
        PremiumAndCoveragesTab.buttonContinue.click();
    }

    private void fillDriverActivityReportsTab() {
        DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
        AbstractContainer<?, ?> assetList = driverActivityReportsTab.getAssetList();
        assetList.getAsset(AutoSSMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE).setValue("Yes");
        assetList.getAsset(AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
        driverActivityReportsTab.submitTab();
    }

    private TestData fillDocumentAndBindTab(boolean evalueIsPresent) {
        DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
        String metaKey = documentsAndBindTab.getMetaKey();
        String evalueAcknowledgementKey = TestData.makeKeyPath(metaKey,
                AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(),
                AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT.getLabel());
        String proofOfPriorInsuranceKey = TestData.makeKeyPath(metaKey,
                AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(),
                AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE.getLabel());
        TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData")
                .adjust(proofOfPriorInsuranceKey, "Yes");
        if (evalueIsPresent) {
            tdPolicyCreation = tdPolicyCreation.adjust(evalueAcknowledgementKey, "Physically Signed");
        }
        documentsAndBindTab.fillTab(tdPolicyCreation).submitTab();
        return tdPolicyCreation;
    }

    private void validateEvalueStatus(String expectedEvalueStatus) {
        PolicySummaryPage.tableGeneralInformation.getRows().get(0)
                .getCell("eValue Status").verify.valueByRegex("Invalid eValue status", expectedEvalueStatus);
    }

    private void validatePolicyStatus() {
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        log.info("TEST: Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void pas2241_eValueUI(@Optional("VA") String state) {
        PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

        eValueQuoteCreationVA();

        CustomAssert.enableSoftMode();
        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present();
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled();
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("No");
        CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("eValue Discount"));
        Dollar policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
        Dollar vehicleCoveragePremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle1(1);
        Dollar totalPremiumWithoutEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount.add(vehicleCoveragePremiumWithoutEvalueDiscount);

        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
        PremiumAndCoveragesTab.calculatePremium();
        CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("eValue Discount"));
        Dollar policyLevelLiabilityCoveragesPremiumWithEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
        Dollar vehicleCoveragePremiumWithEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle1(1);
        Dollar totalPremiumWithEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithEvalueDiscount.add(vehicleCoveragePremiumWithEvalueDiscount);

        log.info("totalPremiumWithoutEvalueDiscount: " + totalPremiumWithoutEvalueDiscount);
        log.info("totalPremiumWithEvalueDiscount: " + totalPremiumWithEvalueDiscount);
        CustomAssert.assertTrue(totalPremiumWithoutEvalueDiscount.moreThan(totalPremiumWithEvalueDiscount));

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void pas2241_eValuePayperlessPreferences(@Optional("VA") String state) {
        PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

        eValueQuoteCreationVA();

        CustomAssert.enableSoftMode();
        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present();
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled();
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("No");
        CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("eValue Discount"));
        Dollar policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
        Dollar vehicleCoveragePremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle1(1);
        Dollar totalPremiumWithoutEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount.add(vehicleCoveragePremiumWithoutEvalueDiscount);

        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
        PremiumAndCoveragesTab.calculatePremium();
        CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("eValue Discount"));
        Dollar policyLevelLiabilityCoveragesPremiumWithEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
        Dollar vehicleCoveragePremiumWithEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle1(1);
        Dollar totalPremiumWithEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithEvalueDiscount.add(vehicleCoveragePremiumWithEvalueDiscount);

        log.info("totalPremiumWithoutEvalueDiscount: " + totalPremiumWithoutEvalueDiscount);
        log.info("totalPremiumWithEvalueDiscount: " + totalPremiumWithEvalueDiscount);
        CustomAssert.assertTrue(totalPremiumWithoutEvalueDiscount.moreThan(totalPremiumWithEvalueDiscount));

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

/*PAS-838
    GIVEN that Paperless Preferences have not been enabled for my state/product/effective date
    WHEN I navigate to the Bind tab
    THEN I should not see the Preferences section visible
*/

/*PAS-266
* GIVEN the agent is creating a new quote/viewing a policy in other than View Only modeWHEN I click on the Paperless Preferences buttonTHEN I see the Preferences UIAND the UI shows both billing and policy preferencesGiven the agent is updating Paperless PreferencesAND selects preferences for Billing AND PolicyWHEN I click SAVE on the API UITHEN the "Enrolled in Preferences?" field is set to PendingGiven the agent is updating Paperless PreferencesAND selects preferences for Billing OR Policy but NOT BOTHWHEN I click SAVE on the API UITHEN the "Enrolled in Preferences?" field is set to See Below Paperless Preferences - Billing Paperless Preferences - Policy Status on Enrolled in Preferences? Opt In Opt In Yes Opt In Opt Out Yes (Billing Only) Opt In Pending Pending (Policy) Opt Out Opt In Yes (Policy Only) Opt Out Opt Out No Opt Out Pending Pending (Policy Only) Pending Opt In Pending (Billing) Pending Opt Out Pending (Billiing Only) Pending Pending Pending */

/*PAS-268
GIVEN I am an agentWHEN I navigate to the General Info PageTHEN I see the Enrolled in Paperless questionAND the answer is populated based on the customers current preferences
 */

/*PAS-269
* GIVEN I am an agent and I am in a policy in other than inquiry modeWHEN I go to the General Info PageTHEN I see the preferences button displayed and enabledGIVEN I am an agent and I am in a policy in inquiry modeWHEN I go to the General Info PageTHEN I see the preferences button displayed and disabled.GIVEN I am an agent and have entered the email address on General Info PageWHEN I click on the preferences buttonTHEN I am able to set my preferencesAND change email is populated in the preferences API POP UPGIVEN I am an agent and I am in a quoteWHEN I go to the General Info PageTHEN I see the preferences button is displayed and disabled
* */

/*Question to Sabra
how do we receive data from stub? can we trick the app with stub response <> Yes for example submitting a request with expected value?*/

/*PAS-271
GIVEN I am an agent on the General Info page in other than inquiry mode or QuoteWHEN I select the paperless billing preferences buttonAND I change/add my current billing preferences enrollmentAND save my choicesTHEN the answer is updated with my new current paperless billing preferences enrollment
*/

/*
*  	PAS-282 	Move Paperless Preferences Button from General Info to Bind Info Page
*
* */


/*    PAS-285

Binding Auto NB and Paperless Preferences



GIVEN I have a quote at new business
AND my quote has been rated at least once
WHEN I navigate to the Bind page
THEN I can click on paperless preferences
AND I can select both policy and billing preferences
AND the Enrolled in Paperless is updated based on the status

GIVEN I have a quote at new business
AND eValue is opted in
AND quote is rated at least once
WHEN I do NOT opt in to paperless preferences for billing AND policy
THEN I receive an error message EV100003

GIVEN I have a new business policy
AND I have opted in to paperless preferences during the quote bind process
WHEN I review my preferences for my new policy
THEN I see the preferences as pending (or based on what my quote preferences were)


PAS-287
As an agent, I want clear help text to explain the paperless preferences options that are available to me so that I am able to clearly state the requirements to my customer for opting into the discount.

Help Text ID: HT10001
Link to spreadsheet: https://csaaig.atlassian.net/wiki/x/LJCKB

Link to UX: https://xd.adobe.com/view/824e6167-dc5b-44d6-9f4b-38b4982f124d/screen/d56dba54-db51-46bd-b72d-e5e0e4705cd1/PAS-Quote-Documents-1



https://csaaig.atlassian.net/browse/PAS-283
As an Agent, I do not want to see the document delivery section on the bind page if I am opted into Paperless Policy Preferences so that it doesn't bother me anymoreOnly SS Auto
*/

/*PAS-327
*Given that I am enrolled in eValueAND at NB + 15 I did not meet the membership or billing paperless preferences CriteriaAND AT NB+ 30 I have still not purchased Membership or I have still not signed up for Paperless Billing PreferencesWHEN the system validates NB + 30THEN the eValue Discount is removed automatically from the policy.
* */

/*PAS-329
Given that I am enrolled in eValueAND at NB + 15 I did not meet the policy paperless preferences CriteriaAND AT NB+ 30 I have still not purchased Membership or I have still not signed up for Paperless Billing and policy PreferencesWHEN the system validates NB + 30THEN the eValue status is updated to show Inactive and a user note is added to show the eValue discount is removed
* */


    private void eValueQuoteCreationVA() {
        TestData defaultTestData = getPolicyTD("DataGather", "TestData");
        TestData policyInformationSectionAdjusted = getTestSpecificTD("PolicyInformation").adjust("TollFree Number", "1");
        TestData currentCarrierSectionAdjusted = getTestSpecificTD("CurrentCarrierInformation");
        TestData generalTabAdjusted = defaultTestData.getTestData("GeneralTab")
                .adjust("PolicyInformation", policyInformationSectionAdjusted)
                .adjust("CurrentCarrierInformation", currentCarrierSectionAdjusted);

        TestData eValuePolicyData = defaultTestData
                .adjust("PrefillTab", getTestSpecificTD("PrefillTab_eValue"))
                .adjust("GeneralTab", generalTabAdjusted)
                .resolveLinks();

        mainApp().open();
        createCustomerIndividual();
        //SearchPage.search(SearchEnum.SearchFor.CUSTOMER, SearchEnum.SearchBy.CUSTOMER, "700009993");

        getPolicyType().get().createQuote(eValuePolicyData);
        //PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        String policyNum = PolicySummaryPage.getPolicyNumber();
        log.info("policyNum: " + policyNum);
    }
}
