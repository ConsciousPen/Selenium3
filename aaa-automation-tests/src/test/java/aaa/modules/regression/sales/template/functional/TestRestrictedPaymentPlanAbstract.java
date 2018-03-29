/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.common.collect.ImmutableList;
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
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.table.Table;

public class TestRestrictedPaymentPlanAbstract extends PolicyBaseTest {

    private static final int PAY_PLAN_POSITION = 0;
    private static final String MONTHLY_PAYMENT_PLAN = "Monthly";
    private static final ImmutableList<String> ALL_PAYMENT_PLANS_HO4 = ImmutableList.of(
            "Pay in Full",
            "Semi-Annual",
            "Quarterly",
            "Eleven Pay Low Down",
            "Eleven Pay Standard",
            "Monthly Low Down");
    private static final ImmutableList<String> ALL_PAYMENT_PLANS = ImmutableList.<String>builder()
            .addAll(ALL_PAYMENT_PLANS_HO4)
            .add("Mortgagee Bill")
            .build();
    private static final ImmutableList<String> RESTRICTED_PAYMENT_PLANS_HO4 = ImmutableList.of(
            "Pay in Full",
            "Monthly");
    private static final ImmutableList<String> RESTRICTED_PAYMENT_PLANS = ImmutableList.<String>builder()
            .addAll(RESTRICTED_PAYMENT_PLANS_HO4)
            .add("Mortgagee Bill")
            .build();
    private static final ImmutableList<String> UNRESTRICTED_PAYMENT_PLANS = ImmutableList.of(
            "Semi-Annual",
            "Eleven Pay Low Down",
            "Eleven Pay Standard",
            "Quarterly",
            "Monthly Low Down");
    private static final ImmutableList<String> PAYMENT_PLAN_HEADER = ImmutableList.of(
            "Plan",
            "Premium",
            "Minimum Down Payment",
            "Installment Payment (w/o fees)",
            "# of Remaining Installments");

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
        Map<String, List<String>> paymentPlansBefore = getPaymentPlans(PremiumsAndCoveragesQuoteTab.tablePaymentPlans);
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        applicantTab.fillTab(getTestSpecificTD("TestData_Pending"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
        verifyRestrictedAndUnrestrictedPaymentPlans();
        Map<String, List<String>> paymentPlansRestricted = getPaymentPlans(PremiumsAndCoveragesQuoteTab.tablePaymentPlans);
        paymentPlansRestricted.remove(MONTHLY_PAYMENT_PLAN);
        Map<String, List<String>> paymentPlansUnrestricted = getPaymentPlans(PremiumsAndCoveragesQuoteTab.tableUnrestrictedPaymentPlans);
        assertThat(paymentPlansBefore.size()).isEqualTo(paymentPlansRestricted.size() + paymentPlansUnrestricted.size());
        assertRows(paymentPlansBefore, paymentPlansRestricted);
        assertRows(paymentPlansBefore, paymentPlansUnrestricted);
    }

    /**
     * @name Test Restricted Payment Plan For Home with Membership = Yes and companion auto restriction
     * @scenario
     * 1. Initiate quote creation.
     * 2. Select Membership = 'Yes' and don't add companion auto.
     * 3. Go to P&C page and verify  that:
     * - restriction is applied
     * - table for unrestricted payment plans and help text aren't present
     * @details
     */
    public void pas10894_restrictionPaymentPlansMembershipYesAutoNo(String state) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String autoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        TestData autoTD = getTestSpecificTD("OtherActiveAAAPolicies_Home");
        TestData td = getPolicyDefaultTD().adjust(autoKey, autoTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyRestrictedPaymentPlans();
    }

    /**
     * @name Test Restricted Payment Plan For Home with Membership = Pending and companion auto restriction
     * @scenario
     * 1. Initiate quote creation.
     * 2. Select Membership = 'Pending' and don't add companion auto.
     * 3. Go to P&C page and verify that:
     * - restriction is applied
     * - table for unrestricted payment plans and help text aren't present
     * @details
     */
    public void pas10894_restrictionPaymentPlansMembershipPendingAutoNo(String state) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipPendingKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipPendingTD = getTestSpecificTD("AAAMembership_Pending");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        String autoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        TestData autoTD = getTestSpecificTD("OtherActiveAAAPolicies_Home");
        TestData td = getPolicyDefaultTD().adjust(membershipPendingKey, membershipPendingTD).adjust(reportTabKey, reportTabTD).adjust(autoKey, autoTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyRestrictedPaymentPlans();
    }

    /**
     * @name Test Restricted Payment Plan For Home with Membership = No and companion auto restriction
     * @scenario
     * 1. Initiate quote creation.
     * 2. Select Membership = 'No' and don't add companion auto.
     * 3. Go to P&C page and verify  that:
     * - restriction is applied
     * - table for unrestricted payment plans and help text aren't present
     * @details
     */
    public void pas10894_restrictionPaymentPlansMembershipNoAutoNo(String state) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipNoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipNoTD = getTestSpecificTD("AAAMembership_No");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        String autoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        TestData autoTD = getTestSpecificTD("OtherActiveAAAPolicies_Home");
        TestData td = getPolicyDefaultTD().adjust(membershipNoKey, membershipNoTD).adjust(reportTabKey, reportTabTD).adjust(autoKey, autoTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyRestrictedPaymentPlans();
    }

    /**
     * @name Test Restricted Payment Plan For Home with Membership = Membership Override and companion auto restriction
     * @scenario
     * 1. Initiate quote creation.
     * 2. Select Membership = 'Membership Override' and don't add companion auto.
     * 3. Go to P&C page and verify that:
     * - restriction is applied
     * - table for unrestricted payment plans and help text aren't present
     * @details
     */
    public void pas11367_restrictionPaymentPlansMembershipOverrideAutoNo(String state) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipOverrideKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipOverrideTD = getTestSpecificTD("AAAMembership_Override");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        String autoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        TestData autoTD = getTestSpecificTD("OtherActiveAAAPolicies_Home");
        TestData td = getPolicyDefaultTD().adjust(membershipOverrideKey, membershipOverrideTD).adjust(reportTabKey, reportTabTD).adjust(autoKey, autoTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyRestrictedPaymentPlans();
    }

    private void assertRows(Map<String, List<String>> paymentPlansBefore, Map<String, List<String>> paymentPlansAfter) {
        for (Map.Entry<String, List<String>> record: paymentPlansAfter.entrySet()){
            List<String> rowBefore = paymentPlansBefore.get(record.getKey());
            List<String> rowAfter = record.getValue();
            assertThat(rowAfter).isEqualTo(rowBefore);
        }
    }

    private Map<String, List<String>> getPaymentPlans(Table paymentPlansTable) {
        Map<String, List<String>> result = new HashMap<>();
        int rowCount = paymentPlansTable.getRowsCount();
        for (int rowNum = 1; rowNum <= rowCount; rowNum++) {
            List<String> row = paymentPlansTable.getRow(rowNum).getValue();
            result.put(row.get(PAY_PLAN_POSITION), row);
        }
        return result;
    }

    private void verifyAllPayPlansAvailable(){
        //check that Payment plan drop down has all payment plans
        ComboBox paymentPlan = new PremiumsAndCoveragesQuoteTab().getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN);
        if ("HomeSS_HO4".equals(getPolicyType().getShortName())){
            verifyPaymentPlansList(paymentPlan, ALL_PAYMENT_PLANS_HO4);
        }
        else {
            verifyPaymentPlansList(paymentPlan, ALL_PAYMENT_PLANS);
        }
        PremiumsAndCoveragesQuoteTab.linkPaymentPlan.click();
        //check that table for PaymentPlans has all payment plans
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans).isPresent();
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
        if ("HomeSS_HO4".equals(getPolicyType().getShortName())) {
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).containsExactlyInAnyOrder(ALL_PAYMENT_PLANS_HO4.toArray(new String[0]));
        }
        else{
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).containsExactlyInAnyOrder(ALL_PAYMENT_PLANS.toArray(new String[0]));
        }
        //check that installment fees message is present
        assertThat(PremiumsAndCoveragesQuoteTab.labelInstallmentFees.getValue()).isEqualTo(INSTALLMENT_FEES_MESSAGE);
        //check that unrestricted payment plans table is absent
        assertThat(PremiumsAndCoveragesQuoteTab.tableUnrestrictedPaymentPlans).isAbsent();
    }

    private void verifyRestrictedAndUnrestrictedPaymentPlans(){
        //check that Payment plan drop down has all payment plans
        ComboBox paymentPlan = new PremiumsAndCoveragesQuoteTab().getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN);
        if ("HomeSS_HO4".equals(getPolicyType().getShortName())) {
            verifyPaymentPlansList(paymentPlan, RESTRICTED_PAYMENT_PLANS_HO4);
        }
        else{
            verifyPaymentPlansList(paymentPlan, RESTRICTED_PAYMENT_PLANS);
        }
        PremiumsAndCoveragesQuoteTab.linkPaymentPlan.click();
        //check that first table for PaymentPlans has restricted payment plans
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans).isPresent();
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
        if ("HomeSS_HO4".equals(getPolicyType().getShortName())) {
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).containsExactlyInAnyOrder(RESTRICTED_PAYMENT_PLANS_HO4.toArray(new String[0]));
        }
        else{
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).containsExactlyInAnyOrder(RESTRICTED_PAYMENT_PLANS.toArray(new String[0]));
        }
        //check that restricted payment plans message is present
        assertThat(PremiumsAndCoveragesQuoteTab.labelPaymentPlanRestriction.getValue()).isEqualTo(RESTRICTED_PAY_PLANS_MESSAGE);
        //check that second table for PaymentPlans has unrestricted payment plans
        assertThat(PremiumsAndCoveragesQuoteTab.tableUnrestrictedPaymentPlans).isPresent();
        assertThat(PremiumsAndCoveragesQuoteTab.tableUnrestrictedPaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
        assertThat(PremiumsAndCoveragesQuoteTab.tableUnrestrictedPaymentPlans.getColumn(1).getValue()).containsExactlyInAnyOrder(UNRESTRICTED_PAYMENT_PLANS.toArray(new String[0]));
        //check that installment fees message is present
        assertThat(PremiumsAndCoveragesQuoteTab.labelInstallmentFees.getValue()).isEqualTo(INSTALLMENT_FEES_MESSAGE);
    }

    private void verifyRestrictedPaymentPlans(){
        //check that Payment plan drop down has all payment plans
        ComboBox paymentPlan = new PremiumsAndCoveragesQuoteTab().getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN);
        if ("HomeSS_HO4".equals(getPolicyType().getShortName())) {
            verifyPaymentPlansList(paymentPlan, RESTRICTED_PAYMENT_PLANS_HO4);
        }
        else{
            verifyPaymentPlansList(paymentPlan, RESTRICTED_PAYMENT_PLANS);
        }
        PremiumsAndCoveragesQuoteTab.linkPaymentPlan.click();
        //check that table for PaymentPlans has restricted payment plans
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans).isPresent();
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
        if ("HomeSS_HO4".equals(getPolicyType().getShortName())) {
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).containsExactlyInAnyOrder(RESTRICTED_PAYMENT_PLANS_HO4.toArray(new String[0]));
        }
        else{
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).containsExactlyInAnyOrder(RESTRICTED_PAYMENT_PLANS.toArray(new String[0]));
        }
        //check that installment fees message is present
        assertThat(PremiumsAndCoveragesQuoteTab.labelInstallmentFees.getValue()).isEqualTo(INSTALLMENT_FEES_MESSAGE);
        //check that unrestricted payment plans table is absent
        assertThat(PremiumsAndCoveragesQuoteTab.tableUnrestrictedPaymentPlans).isAbsent();
    }

    private void verifyPaymentPlansList(ComboBox paymentPlan, ImmutableList<String> expectedPaymentPlans) {
        List<String> actualPaymentPlan = paymentPlan.getAllValues();
        actualPaymentPlan.remove(0);
        assertThat(actualPaymentPlan.size()).as("Incorrect PaymentPlans amount in dropdown").isEqualTo(expectedPaymentPlans.size());
        for (String expectedPaymentPlan : expectedPaymentPlans){
            String foundPaymentPlan = checkPaymentPlan(actualPaymentPlan, expectedPaymentPlan);
            assertThat(foundPaymentPlan).as("PayPlan %s isn't found", expectedPaymentPlan).isEqualTo(expectedPaymentPlan);
        }
    }

    private String checkPaymentPlan(List<String> actualPaymentPlan, String expectedPaymentPlan) {
        for (String actualPaymentPlanValue : actualPaymentPlan) {
            if (actualPaymentPlanValue.equals(expectedPaymentPlan)) {
                return actualPaymentPlanValue;
            }
        }
        return null;
    }
}
