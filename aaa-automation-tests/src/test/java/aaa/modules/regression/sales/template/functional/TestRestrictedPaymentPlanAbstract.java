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

public abstract class TestRestrictedPaymentPlanAbstract extends PolicyBaseTest {

    private static final int PAY_PLAN_POSITION = 0;
    private static final String MONTHLY_PAYMENT_PLAN = "Monthly";
    private static final ImmutableList<String> ALL_PAYMENT_PLANS_DROPDOWN = ImmutableList.of(
            "Pay in Full",
            "Semi-Annual",
            "Mortgagee Bill",
            "Quarterly",
            "Eleven Pay Low Down",
            "Eleven Pay Standard",
            "Monthly Low Down");
    private static final ImmutableList<String> ALL_PAYMENT_PLANS_DROPDOWN_HO4 = ImmutableList.of(
            "Pay in Full",
            "Semi-Annual",
            "Quarterly",
            "Eleven Pay Low Down",
            "Eleven Pay Standard",
            "Monthly Low Down");
    private static final ImmutableList<String> RESTRICTED_PAYMENT_PLANS_DROPDOWN = ImmutableList.of(
            "Pay in Full",
            "Mortgagee Bill",
            "Monthly");
    private static final ImmutableList<String> RESTRICTED_PAYMENT_PLANS_DROPDOWN_HO4 = ImmutableList.of(
            "Pay in Full",
            "Monthly");
    private static final ImmutableList<String> ALL_PAYMENT_PLANS_TABLE = ImmutableList.of(
            "Semi-Annual",
            "Eleven Pay Low Down",
            "Eleven Pay Standard",
            "Mortgagee Bill",
            "Pay in Full",
            "Quarterly",
            "Monthly Low Down");
    private static final ImmutableList<String> ALL_PAYMENT_PLANS_TABLE_HO4 = ImmutableList.of(
            "Semi-Annual",
            "Eleven Pay Low Down",
            "Eleven Pay Standard",
            "Pay in Full",
            "Quarterly",
            "Monthly Low Down");
    private static final ImmutableList<String> UNRESTRICTED_PAYMENT_PLANS_TABLE = ImmutableList.of(
            "Semi-Annual",
            "Eleven Pay Low Down",
            "Eleven Pay Standard",
            "Quarterly",
            "Monthly Low Down");
    private static final ImmutableList<String> RESTRICTED_PAYMENT_PLANS_TABLE = ImmutableList.of(
            "Monthly",
            "Mortgagee Bill",
            "Pay in Full");
    private static final ImmutableList<String> RESTRICTED_PAYMENT_PLANS_TABLE_HO4 = ImmutableList.of(
            "Monthly",
            "Pay in Full");
    private static final ImmutableList<String> PAYMENT_PLAN_HEADER = ImmutableList.of(
            "Plan",
            "Premium",
            "Minimum Down Payment",
            "Installment Payment (w/o fees)",
            "# of Remaining Installments");

    private static final String restrictedPayPlansMessage = "The available pay plans for this quote are restricted to those shown above. The below options can be offered if the following condition is addressed: AAA Membership must be provided.\nAfter addressing the condition, recalculate premium to refresh the available pay plans.";
    private static final String installmentFeesMessage = "Installment Amount does not include transaction fees. View applicable fee schedule.";

    /**
     * @name Test Restricted Payment Plan For Home
     * @scenario
     * 1. Initiate quote creation.
     * 2. Select Membership = 'Pending', don't add any other restrictions to payment plans.
     * 3. Go to P&C page and verify table for unrestricted payment plans.
     * @details
     */
    public void pas10870_restrictionPaymentPlansMembershipPendingCalculation(String state) throws NoSuchFieldException {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyDefaultTD(), ReportsTab.class, true);
        new ReportsTab().getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getTable().getRow(1)
                .getCell(8).controls.links.get(1).click();
        new ReportsTab().getAssetList().getAsset(HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT).getAsset(HomeSSMetaData.ReportsTab.AaaMembershipReportRow.ADD_MEMBER_SINCE_DIALOG).getAsset(HomeSSMetaData.ReportsTab.AddMemberSinceDialog.MEMBER_SINCE).setValue("01/02/2018");
        Page.dialogConfirmation.confirm();
        new ReportsTab().submitTab();
        policy.getDefaultView().fillFromTo(getPolicyDefaultTD(), PropertyInfoTab.class, PremiumsAndCoveragesQuoteTab.class);
        PremiumsAndCoveragesQuoteTab.btnCalculatePremium.click();
        verifyAllPayPlansAvailable();
        Map<String, List<String>> paymentPlansBefore = getPaymentPlans(PremiumsAndCoveragesQuoteTab.tablePaymentPlans);
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        new ApplicantTab().fillTab(getTestSpecificTD("TestData_Pending"));
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

    public void pas10870_restrictionPaymentPlansMembershipYes(String state) throws NoSuchFieldException {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyDefaultTD(), PremiumsAndCoveragesQuoteTab.class, true);
        verifyAllPayPlansAvailable();
    }

    public void pas10870_restrictionPaymentPlansMembershipPending(String state) throws NoSuchFieldException {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipPendingKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipTD = getTestSpecificTD("AAAMembership_Pending");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        TestData td = getPolicyDefaultTD().adjust(membershipPendingKey, membershipTD).adjust(reportTabKey, reportTabTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyRestrictedAndUnrestrictedPaymentPlans();
    }

    public void pas10870_restrictionPaymentPlansMembershipNo(String state) throws NoSuchFieldException {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipNoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipTD = getTestSpecificTD("AAAMembership_No");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        TestData td = getPolicyDefaultTD().adjust(membershipNoKey, membershipTD).adjust(reportTabKey, reportTabTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyRestrictedPaymentPlans();
    }

    public void pas10870_restrictionPaymentPlansMembershipOverride(String state) throws NoSuchFieldException {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipNoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipTD = getTestSpecificTD("AAAMembership_Override");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        TestData td = getPolicyDefaultTD().adjust(membershipNoKey, membershipTD).adjust(reportTabKey, reportTabTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyAllPayPlansAvailable();
    }

    public void pas10870_restrictionPaymentPlansMembershipYesAutoNo(String state) throws NoSuchFieldException {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String autoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        TestData autoTD = getTestSpecificTD("OtherActiveAAAPolicies");
        TestData td = getPolicyDefaultTD().adjust(autoKey, autoTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyRestrictedPaymentPlans();
    }

    public void pas10870_restrictionPaymentPlansMembershipPendingAutoNo(String state) throws NoSuchFieldException {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipPendingKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipTD = getTestSpecificTD("AAAMembership_Pending");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        String autoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        TestData autoTD = getTestSpecificTD("OtherActiveAAAPolicies");
        TestData td = getPolicyDefaultTD().adjust(membershipPendingKey, membershipTD).adjust(reportTabKey, reportTabTD).adjust(autoKey, autoTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyRestrictedPaymentPlans();
    }

    public void pas10870_restrictionPaymentPlansMembershipNoAutoNo(String state) throws NoSuchFieldException {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipNoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipTD = getTestSpecificTD("AAAMembership_No");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        String autoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        TestData autoTD = getTestSpecificTD("OtherActiveAAAPolicies");
        TestData td = getPolicyDefaultTD().adjust(membershipNoKey, membershipTD).adjust(reportTabKey, reportTabTD).adjust(autoKey, autoTD);
        policy.getDefaultView().fillUpTo(td, PremiumsAndCoveragesQuoteTab.class, true);
        verifyRestrictedPaymentPlans();
    }

    public void pas10870_restrictionPaymentPlansMembershipOverrideAutoNo(String state) throws NoSuchFieldException {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        String membershipNoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
        TestData membershipTD = getTestSpecificTD("AAAMembership_Override");
        String reportTabKey = TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName());
        TestData reportTabTD = getTestSpecificTD("ReportsTab");
        String autoKey = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        TestData autoTD = getTestSpecificTD("OtherActiveAAAPolicies");
        TestData td = getPolicyDefaultTD().adjust(membershipNoKey, membershipTD).adjust(reportTabKey, reportTabTD).adjust(autoKey, autoTD);
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
            verifyPaymentPlansList(paymentPlan, ALL_PAYMENT_PLANS_DROPDOWN_HO4);
        }
        else {
            verifyPaymentPlansList(paymentPlan, ALL_PAYMENT_PLANS_DROPDOWN);
        }
        PremiumsAndCoveragesQuoteTab.linkPaymentPlan.click();
        //check that table for PaymentPlans has all payment plans
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans).isPresent();
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
        if ("HomeSS_HO4".equals(getPolicyType().getShortName())) {
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).isEqualTo(ALL_PAYMENT_PLANS_TABLE_HO4);
        }
        else{
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).isEqualTo(ALL_PAYMENT_PLANS_TABLE);
        }
        //check that installment fees message is present
        assertThat(PremiumsAndCoveragesQuoteTab.labelInstallmentFees.getValue()).isEqualTo(installmentFeesMessage);
        //check that unrestricted payment plans table is absent
        assertThat(PremiumsAndCoveragesQuoteTab.tableUnrestrictedPaymentPlans).isAbsent();
    }

    private void verifyRestrictedAndUnrestrictedPaymentPlans(){
        //check that Payment plan drop down has all payment plans
        ComboBox paymentPlan = new PremiumsAndCoveragesQuoteTab().getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN);
        if ("HomeSS_HO4".equals(getPolicyType().getShortName())) {
            verifyPaymentPlansList(paymentPlan, RESTRICTED_PAYMENT_PLANS_DROPDOWN_HO4);
        }
        else{
            verifyPaymentPlansList(paymentPlan, RESTRICTED_PAYMENT_PLANS_DROPDOWN);
        }
        PremiumsAndCoveragesQuoteTab.linkPaymentPlan.click();
        //check that first table for PaymentPlans has restricted payment plans
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans).isPresent();
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
        if ("HomeSS_HO4".equals(getPolicyType().getShortName())) {
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).isEqualTo(RESTRICTED_PAYMENT_PLANS_TABLE_HO4);
        }
        else{
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).isEqualTo(RESTRICTED_PAYMENT_PLANS_TABLE);
        }
        //check that restricted payment plans message is present
        assertThat(PremiumsAndCoveragesQuoteTab.labelPaymentPlanRestriction.getValue()).isEqualTo(restrictedPayPlansMessage);
        //check that second table for PaymentPlans has unrestricted payment plans
        assertThat(PremiumsAndCoveragesQuoteTab.tableUnrestrictedPaymentPlans).isPresent();
        assertThat(PremiumsAndCoveragesQuoteTab.tableUnrestrictedPaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
        assertThat(PremiumsAndCoveragesQuoteTab.tableUnrestrictedPaymentPlans.getColumn(1).getValue()).isEqualTo(UNRESTRICTED_PAYMENT_PLANS_TABLE);
                //check that installment fees message is present
        assertThat(PremiumsAndCoveragesQuoteTab.labelInstallmentFees.getValue()).isEqualTo(installmentFeesMessage);
    }

    private void verifyRestrictedPaymentPlans(){
        //check that Payment plan drop down has all payment plans
        ComboBox paymentPlan = new PremiumsAndCoveragesQuoteTab().getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN);
        if ("HomeSS_HO4".equals(getPolicyType().getShortName())) {
            verifyPaymentPlansList(paymentPlan, RESTRICTED_PAYMENT_PLANS_DROPDOWN_HO4);
        }
        else{
            verifyPaymentPlansList(paymentPlan, RESTRICTED_PAYMENT_PLANS_DROPDOWN);
        }
        PremiumsAndCoveragesQuoteTab.linkPaymentPlan.click();
        //check that table for PaymentPlans has restricted payment plans
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans).isPresent();
        assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getHeader().getValue()).isEqualTo(PAYMENT_PLAN_HEADER);
        if ("HomeSS_HO4".equals(getPolicyType().getShortName())) {
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).isEqualTo(RESTRICTED_PAYMENT_PLANS_TABLE_HO4);
        }
        else{
            assertThat(PremiumsAndCoveragesQuoteTab.tablePaymentPlans.getColumn(1).getValue()).isEqualTo(RESTRICTED_PAYMENT_PLANS_TABLE);
        }
        //check that installment fees message is present
        assertThat(PremiumsAndCoveragesQuoteTab.labelInstallmentFees.getValue()).isEqualTo(installmentFeesMessage);
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
