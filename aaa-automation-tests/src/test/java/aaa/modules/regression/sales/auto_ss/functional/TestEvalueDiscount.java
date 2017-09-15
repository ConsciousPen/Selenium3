/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;


import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;

public class TestEvalueDiscount extends AutoSSBaseTest {

    /**
     * @author Viktoriia Lutsenko
     * @name Test presence/status of eValue discount on P&C and consolidated pages(Membership = Active, Evalue = Yes)
     * @scenario
     * 1. Create customer
     * 2. Create active policy with next conditions:
     * Current AAA Member = 'Yes'
     * Current Carrier = 'AAA Mid-Atlantic - 500016292'
     * Days Lapsed < 4
     * BI Limit = $300,000/$500,000
     * Apply eValue Discount  = 'Yes'
     * 3. Verify that on P&C page 'eValue Discount' is present in Discounts & Surcharges table.
     * 4. Bind policy.
     * 5. Verify that 'eEvalue Status' = 'Pending' on Consolidated page.
     * @details
     */

	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testMemberActiveEvalueYes() {
        prefillEvalueTestData("AAAProductOwned_Active", "CurrentCarrierInformation",
                "PolicyInformation");
        fillPremiumAndCoveragesTab(true);
        fillDriverActivityReportsTab();
        TestData tdPolicyCreation = fillDocumentAndBindTab(true);
        new PurchaseTab().fillTab(tdPolicyCreation).submitTab();

        validateEvalueStatus("Pending");
        validatePolicyStatus();
    }


    /**
     * @author Viktoriia Lutsenko
     * @name Test presence/status of eValue discount on P&C and consolidated pages(Membership = Active, Evalue = No)
     * @scenario
     * 1. Create customer
     * 2. Create active policy with next conditions:
     * Current AAA Member = 'Yes'
     * Current Carrier = 'AAA Mid-Atlantic - 500016292'
     * Days Lapsed < 4
     * BI Limit = $300,000/$500,000
     * Apply eValue Discount  = 'No'
     * 3. Verify that on P&C page 'eValue Discount' isn't present in Discounts & Surcharges table.
     * 4. Bind policy.
     * 5. Verify that 'eEvalue Status' is empty on Consolidated page.
     * @details
     */

    @Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testMemberActiveEvalueNo() {
        prefillEvalueTestData("AAAProductOwned_Active", "CurrentCarrierInformation",
                "PolicyInformation");
        fillPremiumAndCoveragesTab(false);
        fillDriverActivityReportsTab();
        TestData tdPolicyCreation = fillDocumentAndBindTab(false);
        new PurchaseTab().fillTab(tdPolicyCreation).submitTab();

        validateEvalueStatus("");
        validatePolicyStatus();
    }

    /**
     * @author Viktoriia Lutsenko
     * @name Test presence/status of eValue discount on P&C and consolidated pages(Membership = No, Evalue = No)
     * @scenario
     * 1. Create customer
     * 2. Create active policy with next conditions:
     * Current AAA Member = 'No'
     * Current Carrier = 'AAA Mid-Atlantic - 500016292'
     * Days Lapsed < 4
     * BI Limit = $300,000/$500,000
     * Apply eValue Discount  = 'No'
     * 3. Verify that on P&C page 'eValue Discount' isn't present in Discounts & Surcharges table.
     * 4. Bind policy.
     * 5. Verify that 'eEvalue Status' is empty on Consolidated page.
     * @details
     */

    @Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testMemberInactiveEvalueNo() {
        prefillEvalueTestData("AAAProductOwned_No", "CurrentCarrierInformation",
                "PolicyInformation");
        fillPremiumAndCoveragesTab(false);
        fillDriverActivityReportsTab();
        TestData tdPolicyCreation = fillDocumentAndBindTab(false);
        new PurchaseTab().fillTab(tdPolicyCreation).submitTab();

        validateEvalueStatus("");
        validatePolicyStatus();
    }

    /**
     * @author Viktoriia Lutsenko
     * @name Test presence/status of eValue discount on P&C and consolidated pages(Membership = Pending, Evalue = Yes)
     * @scenario
     * 1. Create customer
     * 2. Create active policy with next conditions:
     * Current AAA Member = 'Membership Pending'
     * Current Carrier = 'AAA Mid-Atlantic - 500016292'
     * Days Lapsed < 4
     * BI Limit = $300,000/$500,000
     * Apply eValue Discount  = 'Yes'
     * 3. Verify that on P&C page 'eValue Discount' is present in Discounts & Surcharges table.
     * 4. Bind policy.
     * 5. Verify that 'eEvalue Status' = 'Pending' on Consolidated page.
     * @details
     */

    @Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testMemberPendingEvalueYes() {
        prefillEvalueTestData("AAAProductOwned_Pending", "CurrentCarrierInformation",
                "PolicyInformation");
        fillPremiumAndCoveragesTab(true);
        fillDriverActivityReportsTab();
        TestData tdPolicyCreation = fillDocumentAndBindTab(true);
        new PurchaseTab().fillTab(tdPolicyCreation).submitTab();

        validateEvalueStatus("Pending");
        validatePolicyStatus();
    }

    /**
     * @author Viktoriia Lutsenko
     * @name Test presence/status of eValue discount on P&C and consolidated pages(Membership = Pending, Evalue = No)
     * @scenario
     * 1. Create customer
     * 2. Create active policy with next conditions:
     * Current AAA Member = 'Membership Pending'
     * Current Carrier = 'AAA Mid-Atlantic - 500016292'
     * Days Lapsed < 4
     * BI Limit = $300,000/$500,000
     * Apply eValue Discount  = 'No'
     * 3. Verify that on P&C page 'eValue Discount' isn't present in Discounts & Surcharges table.
     * 4. Bind policy.
     * 5. Verify that 'eEvalue Status' is empty on Consolidated page.
     * @details
     */

    @Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testMemberPendingEvalueNo() {
        prefillEvalueTestData("AAAProductOwned_Pending", "CurrentCarrierInformation",
                "PolicyInformation");
        fillPremiumAndCoveragesTab(false);
        fillDriverActivityReportsTab();
        TestData tdPolicyCreation = fillDocumentAndBindTab(false);
        new PurchaseTab().fillTab(tdPolicyCreation).submitTab();

        validateEvalueStatus("");
        validatePolicyStatus();
    }

    /**
     * @author Viktoriia Lutsenko
     * @name Test presence/status of eValue discount on P&C and consolidated pages(Membership = Active, Evalue = No, lapsed > 4)
     * @scenario
     * 1. Create customer
     * 2. Create active policy with next conditions:
     * Current AAA Member = 'Yes'
     * Current Carrier = 'AAA Mid-Atlantic - 500016292'
     * Days Lapsed > 4
     * BI Limit = $300,000/$500,000
     * Apply eValue Discount  = 'No'
     * 3. Verify that on P&C page 'eValue Discount' isn't present in Discounts & Surcharges table.
     * 4. Bind policy.
     * 5. Verify that 'eEvalue Status' is empty on Consolidated page.
     * @details
     */

    @Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testMemberActiveEvalueNoDayLapsedMore4() {
        prefillEvalueTestData("AAAProductOwned_Active", "CurrentCarrierInformation_DayLapsedMore4",
                "PolicyInformation");
        fillPremiumAndCoveragesTab(false);
        fillDriverActivityReportsTab();
        TestData tdPolicyCreation = fillDocumentAndBindTab(false);
        new PurchaseTab().fillTab(tdPolicyCreation).submitTab();

        validateEvalueStatus("");
        validatePolicyStatus();
    }

    /**
     * @author Viktoriia Lutsenko
     * @name Test presence/status of eValue discount on P&C and consolidated pages(Membership = Active, Evalue = No, BI limit =  $15,000/$30,000)
     * @scenario
     * 1. Create customer
     * 2. Create active policy with next conditions:
     * Current AAA Member = 'Yes'
     * Current Carrier = 'AAA Mid-Atlantic - 500016292'
     * Days Lapsed < 4
     * BI Limit = $15,000/$30,000
     * Apply eValue Discount  = 'No'
     * 3. Verify that on P&C page 'eValue Discount' isn't present in Discounts & Surcharges table.
     * 4. Bind policy.
     * 5. Verify that 'eEvalue Status' is empty on Consolidated page.
     * @details
     */

    @Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testMemberActiveEvalueNoBILimitLess() {
        prefillEvalueTestData("AAAProductOwned_Active", "CurrentCarrierInformation_BILimitLess",
                "PolicyInformation");
        fillPremiumAndCoveragesTab(false);
        fillDriverActivityReportsTab();
        TestData tdPolicyCreation = fillDocumentAndBindTab(false);
        new PurchaseTab().fillTab(tdPolicyCreation).submitTab();

        validateEvalueStatus("");
        validatePolicyStatus();
    }

    private void prefillEvalueTestData(String aaaProductOwned, String currentCarrierInformation, String policyInformation) {
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
                .adjust(policyInformationKey, getTestSpecificTD(policyInformation))
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
                .adjust(proofOfPriorInsuranceKey,"Yes");
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
}
