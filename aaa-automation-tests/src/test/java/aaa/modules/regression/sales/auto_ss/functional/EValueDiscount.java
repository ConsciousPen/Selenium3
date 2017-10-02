/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;


import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.Dollar;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;

public class EValueDiscount extends AutoSSBaseTest {

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

    /**
     * @author Oleg Stasyuk
     * @name Test eValue Discount
     * @scenario 1. Create new eValue eligible quote
     * 2. Check Default properties of eValue Discount field
     * 3. Get premium before eValue discount is applied and after discount is applied
     * 4. Compare premiums
     *
     * Intermediate checks in Rating Details, Policy Summary Page
     * @details
     */
    //epic PAS-1438 eValue - New Business
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void pas272_eValueDiscountApplied(@Optional("VA") String state) {
        PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

        eValueQuoteCreationVA();

        CustomAssert.enableSoftMode();
        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        //Check field properties and default value of eValue Discount
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present();
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled();
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("No");

        //PAS-272 start
        CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("eValue Discount"));
        //PAS-272 end

        //Get premiums before discount is applied
        Dollar policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
        Dollar vehicleCoveragePremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle1(1);
        Dollar totalPremiumWithoutEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount.add(vehicleCoveragePremiumWithoutEvalueDiscount);

        //PAS-305 start
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
        PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3,"eValue Discount").getCell(4).verify.value("None");
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        //PAS-305 end

        //PAS-2053
        premiumAndCoveragesTab.saveAndExit();
        PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, "eValue Discount").verify.present(false);
        //PAS-2053


        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

        //Set discount to Yes
        premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
        //PAS-304 start
        premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium().equals(0);
        //PAS-304 end

        PremiumAndCoveragesTab.calculatePremium();

        //PAS-272 start
        CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains("eValue Discount"));
        //PAS-272 end

        //Get premiums after discount is applied
        Dollar policyLevelLiabilityCoveragesPremiumWithEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
        Dollar vehicleCoveragePremiumWithEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle1(1);
        Dollar totalPremiumWithEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithEvalueDiscount.add(vehicleCoveragePremiumWithEvalueDiscount);

        log.info("totalPremiumWithoutEvalueDiscount: " + totalPremiumWithoutEvalueDiscount);
        log.info("totalPremiumWithEvalueDiscount: " + totalPremiumWithEvalueDiscount);

        //Compare premiums before discount and after
        CustomAssert.assertTrue(totalPremiumWithoutEvalueDiscount.moreThan(totalPremiumWithEvalueDiscount));

        //PAS-2053 eValue Status on Policy Summary Page - Don't Show it When not enabled

        //PAS-305 start
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
        PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3,"eValue Discount").getCell(4).verify.value("Yes");
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        //PAS-305 end

        //PAS-2053
        premiumAndCoveragesTab.saveAndExit();
        PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, "eValue Discount").verify.present();
        //PAS-2053

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    void eValueQuoteCreationVA() {
        //Default VA test data didn't work, so had to use multiple adjustments
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

        getPolicyType().get().createQuote(eValuePolicyData);
        String policyNum = PolicySummaryPage.getPolicyNumber();
        log.info("policyNum: " + policyNum);
    }
}
