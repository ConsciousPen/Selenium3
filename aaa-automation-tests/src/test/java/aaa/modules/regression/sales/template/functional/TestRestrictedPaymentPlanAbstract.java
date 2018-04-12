/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.table.Table;

import java.util.List;

import static toolkit.verification.CustomAssertions.assertThat;

public abstract class TestRestrictedPaymentPlanAbstract extends PolicyBaseTest {

    private static final String MONTHLY_PAYMENT_PLAN = "Monthly";

    protected abstract List<String> getExpectedAllPaymentPlans();
    protected abstract List<String> getExpectedRestrictedPaymentPlans();
    protected abstract List<String> getExpectedUnrestrictedPaymentPlans();
    protected abstract List<String> getExpectedHeader();

    private static final String RESTRICTED_PAY_PLANS_MESSAGE = "The available pay plans for this quote are restricted to those shown above. The below options can be offered if the following condition is addressed: AAA Membership must be provided.\nAfter addressing the condition, recalculate premium to refresh the available pay plans.";
    private static final String INSTALLMENT_FEES_MESSAGE = "Installment Amount does not include transaction fees. View applicable fee schedule.";

    private ReportsTab reportTab = new ReportsTab();
    private ApplicantTab applicantTab = new ApplicantTab();

    /**
     * @name Test Restricted Payment Plan For Home with Membership = Yes and no other restrictions
     * @scenario
     * 1. Initiate quote creation.
     * 2. Select Membership = 'Yes', don't add any other restrictions to payment plans.
     * 3. Go to P&C page and verify  that:
     * - restriction isn't applied
     * - table for unrestricted payment plans and help text aren't present
     * @details
     */
    public void pas10894_restrictionPaymentPlansMembershipYes(String state) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyDefaultTD(), PremiumsAndCoveragesQuoteTab.class, true);
        verifyAllPayPlansAvailable();
    }

    /**
     * @name Test Restricted Payment Plan For Home with Membership = Pending and no other restrictions
     * @scenario
     * 1. Initiate quote creation.
     * 2. Select Membership = 'Pending', don't add any other restrictions to payment plans.
     * 3. Go to P&C page and verify  that:
     * - restriction is applied
     * - table for unrestricted payment plans and help text are present
     * @details
     */
    public void pas10894_restrictionPaymentPlansMembershipPending(String state) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipPendingKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipPendingTD = getTestSpecificTD("AAAMembership_Pending");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        TestData td = getPolicyDefaultTD().adjust(membershipPendingKey, membershipPendingTD).adjust(reportTabKey, reportTabTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyRestrictedAndUnrestrictedPaymentPlans();
    }

    /**
     * @name Test Restricted Payment Plan For Home with Membership = No and no other restrictions
     * @scenario
     * 1. Initiate quote creation.
     * 2. Select Membership = 'No', don't add any other restrictions to payment plans.
     * 3. Go to P&C page and verify  that:
     * - restriction is applied
     * - table for unrestricted payment plans and help text aren't present
     * @details
     */
    public void pas10894_restrictionPaymentPlansMembershipNo(String state) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipNoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipNoTD = getTestSpecificTD("AAAMembership_No");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        TestData td = getPolicyDefaultTD().adjust(membershipNoKey, membershipNoTD).adjust(reportTabKey, reportTabTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyRestrictedPaymentPlans();
    }

    /**
     * @name Test Restricted Payment Plan For Home with Membership = Membership Override and no other restrictions
     * @scenario
     * 1. Initiate quote creation.
     * 2. Select Membership = 'Membership Override', don't add any other restrictions to payment plans.
     * 3. Go to P&C page and verify  that:
     * - restriction isn't applied
     * - table for unrestricted payment plans and help text aren't present
     * @details
     */
    public void pas11367_restrictionPaymentPlansMembershipOverride(String state) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipOverrideKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipOverrideTD = getTestSpecificTD("AAAMembership_Override");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        TestData td = getPolicyDefaultTD().adjust(membershipOverrideKey, membershipOverrideTD).adjust(reportTabKey, reportTabTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyAllPayPlansAvailable();
    }

    /**
     * @name Compare payment plans premiums for Membership = Yes and Membership = Pending in restricted/unrectricted tables
     * @scenario
     * 1. Initiate quote creation.
     * 2. Select Membership = 'Yes', don't add any other restrictions to payment plans.
     * 3. Go to P&C page and calculate premium, check premiums in Payment Plans section (all payplans are present).
     * 4. Go to General page and update Membership to 'Pending'.
     * 5. Go to P&C page and calculate premium, check premiums in Payment Plans section (restricted/unrestricted tables) .
     * 6. Check that premiums are the same for payment plans when Membership = Yes and membership = 'Pending'.
     * @details
     */
    public void pas10998_restrictionPaymentPlansMembershipPendingCalculation(String state) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyDefaultTD(), ReportsTab.class, true);
        reportTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
                .getCell(8).controls.links.get(1).click();
        reportTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getAsset(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG).getAsset(HomeSSMetaData.ReportsTab.AddMemberSinceDialog.MEMBER_SINCE).setValue("01/02/2018");
        Page.dialogConfirmation.confirm();
        reportTab.submitTab();
        policy.getDefaultView().fillFromTo(getPolicyDefaultTD(), PropertyInfoTab.class, PremiumsAndCoveragesQuoteTab.class);
        PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
        verifyAllPayPlansAvailable();
        List<TestData> paymentPlansBefore = getTablePaymentPlans().getValue();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        applicantTab.fillTab(getTestSpecificTD("TestData_Pending"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
        verifyRestrictedAndUnrestrictedPaymentPlans();
        List<TestData> paymentPlansRestricted = getTablePaymentPlans().getValue();
        paymentPlansRestricted.removeIf(td->td.getValue("Plan").equals(MONTHLY_PAYMENT_PLAN));
        List<TestData> paymentPlansUnrestricted = getTableUnrestrictedPaymentPlans().getValue();
        assertThat(paymentPlansBefore).hasSize(paymentPlansRestricted.size() + paymentPlansUnrestricted.size());
        assertThat(paymentPlansBefore.size()).isEqualTo(paymentPlansRestricted.size() + paymentPlansUnrestricted.size());
        assertThat(paymentPlansBefore).containsAll(paymentPlansRestricted);
        assertThat(paymentPlansBefore).containsAll(paymentPlansUnrestricted);
    }

    protected void verifyAllPayPlansAvailable(){
        //check that Payment plan drop down has all payment plans
        assertThat(getPaymentPlanComboBox()).hasSameElementsAs(getExpectedAllPaymentPlans());
        clickPaymentPlanLink();
        //check that table for PaymentPlans has all payment plans
        assertThat(getTablePaymentPlans()).isPresent();
        assertThat(getTablePaymentPlans().getHeader().getValue()).isEqualTo(getExpectedHeader());
        assertThat(getTablePaymentPlans().getColumn(1).getValue()).hasSameElementsAs(getExpectedAllPaymentPlans());
        //check that installment fees message is present
        assertThat(getLabelInstallmentFees()).hasValue(INSTALLMENT_FEES_MESSAGE);
        //check that unrestricted payment plans table is absent
        assertThat(getTableUnrestrictedPaymentPlans()).isAbsent();
    }

    protected void verifyRestrictedAndUnrestrictedPaymentPlans(){
        //check that Payment plan drop down has all payment plans
        assertThat(getPaymentPlanComboBox()).hasSameElementsAs(getExpectedRestrictedPaymentPlans());
        clickPaymentPlanLink();
        //check that first table for PaymentPlans has restricted payment plans
        assertThat(getTablePaymentPlans()).isPresent();
        assertThat(getTablePaymentPlans().getHeader().getValue()).isEqualTo(getExpectedHeader());
        assertThat(getTablePaymentPlans().getColumn(1).getValue()).hasSameElementsAs(getExpectedRestrictedPaymentPlans());
        //check that restricted payment plans message is present
        assertThat(getLabelPaymentPlanRestriction()).hasValue(RESTRICTED_PAY_PLANS_MESSAGE);
        //check that second table for PaymentPlans has unrestricted payment plans
        assertThat(getTableUnrestrictedPaymentPlans()).isPresent();
        assertThat(getTableUnrestrictedPaymentPlans().getHeader().getValue()).isEqualTo(getExpectedHeader());
        assertThat(getTableUnrestrictedPaymentPlans().getColumn(1).getValue()).hasSameElementsAs(getExpectedUnrestrictedPaymentPlans());
        //check that installment fees message is present
        assertThat(getLabelInstallmentFees()).hasValue(INSTALLMENT_FEES_MESSAGE);
    }

    protected void verifyRestrictedPaymentPlans(){
        //check that Payment plan drop down has all payment plans
        assertThat(getPaymentPlanComboBox()).hasSameElementsAs(getExpectedRestrictedPaymentPlans());
        clickPaymentPlanLink();
        //check that table for PaymentPlans has restricted payment plans
        assertThat(getTablePaymentPlans()).isPresent();
        assertThat(getTablePaymentPlans().getHeader().getValue()).isEqualTo(getExpectedHeader());
        assertThat(getTablePaymentPlans().getColumn(1).getValue()).hasSameElementsAs(getExpectedRestrictedPaymentPlans());
        //check that installment fees message is present
        assertThat(getLabelInstallmentFees()).hasValue(INSTALLMENT_FEES_MESSAGE);
        //check that unrestricted payment plans table is absent
        assertThat(getTableUnrestrictedPaymentPlans()).isAbsent();
    }

    protected StaticElement getLabelInstallmentFees() {
        return PremiumsAndCoveragesQuoteTab.labelInstallmentFees;
    }

    protected Table getTableUnrestrictedPaymentPlans() {
        return PremiumsAndCoveragesQuoteTab.tableUnrestrictedPaymentPlans;
    }

    protected StaticElement getLabelPaymentPlanRestriction() {
        return PremiumsAndCoveragesQuoteTab.labelPaymentPlanRestriction;
    }
    protected void clickPaymentPlanLink() {
        PremiumsAndCoveragesQuoteTab.linkPaymentPlan.click();
    }

    protected Table getTablePaymentPlans() {
        return PremiumsAndCoveragesQuoteTab.tablePaymentPlans;
    }

    protected List<String> getPaymentPlanComboBox() {
        List<String> paymentPlanList = new PremiumsAndCoveragesQuoteTab().getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).getAllValues();
        paymentPlanList.removeIf(""::equals);
        return paymentPlanList;
    }

}
