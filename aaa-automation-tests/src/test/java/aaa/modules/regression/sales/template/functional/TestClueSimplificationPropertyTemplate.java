package aaa.modules.regression.sales.template.functional;


import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import java.time.LocalDateTime;
import java.util.List;
import static toolkit.verification.CustomAssertions.assertThat;

public abstract class TestClueSimplificationPropertyTemplate extends TestClaimPointsVRDPageAbstract {

    protected abstract Tab getBindTab();
    protected abstract Tab getPurchaseTab();
    protected abstract Tab getApplicantTab();
    protected abstract void navigateToBindTab();
    protected abstract void navigateToApplicantTab();
    protected abstract RadioGroup getClaimChargeableAsset();
    protected abstract TextBox getClaimNonChargeableReasonAsset();
    protected abstract void reorderClueReport();

    protected void pas6759_AbilityToRemoveManuallyEnteredClaimsNB() {

        // Initialize test data
        List<TestData> tdClaims = getClaimsTD();
        TestData td = getPolicyTD().adjust(TestData.makeKeyPath(getPropertyInfoTab().getClass().getSimpleName(), getClaimHistoryLabel()), tdClaims);

        createQuoteAndFillUpTo(td, getBindTab().getClass());
        getBindTab().saveAndExit();
        String quoteNumber = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        // PAS-6759 AC2. Ability to remove Claims for unprivileged user while NB tx is not bound
        openAppNonPrivilegedUser("A30");
        SearchPage.openQuote(quoteNumber);
        policy.dataGather().start();
        navigateToPropertyInfoTab();

        // 4 Claims were added manually
        checkTblClaimRowCount(4);
        viewEditClaim(Labels.THEFT);
        removeClaim();

        // Check that 1 Claim was removed. 3 Claims left
        checkTblClaimRowCount(3);
        getPropertyInfoTab().saveAndExit();

        // PAS-6759 AC2. Ability to remove Claims for unprivileged user while NB tx is not bound
        policy.dataGather().start();
        navigateToPropertyInfoTab();
        viewEditClaim(Labels.WATER);
        removeClaim();

        // Check that table contains 2 claims
        checkTblClaimRowCount(2);
        getPropertyInfoTab().saveAndExit();
        mainApp().close();

        // Bind Policy
        mainApp().open();
        SearchPage.openQuote(quoteNumber);
        policy.dataGather().start();
        navigateToPropertyInfoTab();
        checkRemoveButtonAvailable(true);
        calculatePremiumAndOpenVRD();
        PropertyQuoteTab.RatingDetailsView.close();
        navigateToBindTab();
        getBindTab().submitTab();
        overrideAllErrorsAndBind();
        getPurchaseTab().fillTab(getPolicyTD());
        getPurchaseTab().submitTab();
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        checkAfterTXWasBound(policyNumber);
    }

    protected void pas6759_AbilityToRemoveManuallyEnteredClaimsEndorsement(){

        // Get TestData for Adding Claims
        List<TestData> tdClaims = getClaimsTD();
        // Create Empty Testdata and Adjust it with list of claims
        TestData td = DataProviderFactory.dataOf(getPropertyInfoTab().getClass().getSimpleName(), DataProviderFactory.emptyData())
                .adjust(TestData.makeKeyPath(getPropertyInfoTab().getClass().getSimpleName(), getClaimHistoryLabel()), getClaimsTD());

        openAppAndCreatePolicy(getPolicyTD());
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        mainApp().close();
        openAppNonPrivilegedUser("A30");
        searchForPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        navigateToPropertyInfoTab();
        getPropertyInfoTab().fillTab(td);

        // 4 Claims were added manually
        checkTblClaimRowCount(4);
        viewEditClaim(Labels.THEFT);
        removeClaim();

        // Check that 1 Claim was removed. 3 Claims left
        checkTblClaimRowCount(3);
        getPropertyInfoTab().saveAndExit();

        // PAS-6759 AC2. Ability to remove Claims for unprivileged user while NB tx is not bound
        policy.endorse().start();
        navigateToPropertyInfoTab();
        viewEditClaim(Labels.WATER);
        removeClaim();

        // Check that table contains 2 claims
        checkTblClaimRowCount(2);
        getPropertyInfoTab().saveAndExit();
        mainApp().close();

        // Bind Policy
        mainApp().open();
        searchForPolicy(policyNumber);
        policy.endorse().start();
        navigateToPropertyInfoTab();
        checkRemoveButtonAvailable(true);
        calculatePremiumAndOpenVRD();
        PropertyQuoteTab.RatingDetailsView.close();
        navigateToBindTab();
        getBindTab().submitTab();
        overrideAllErrorsAndBind();

        checkAfterTXWasBound(policyNumber);
    }

    protected void pas6759_AbilityToRemoveManuallyEnteredClaimsReWrite(){
        // Get TestData for Adding Claims
        // Create Empty Testdata and Adjust it with list of claims
        TestData td = DataProviderFactory.dataOf(getPropertyInfoTab().getClass().getSimpleName(), DataProviderFactory.emptyData())
                .adjust(TestData.makeKeyPath(getPropertyInfoTab().getClass().getSimpleName(), getClaimHistoryLabel()), getClaimsTD());

        // Create Policy
        openAppAndCreatePolicy(getPolicyTD());

        // Cancel Policy and Rewrite it
        cancelAndRewritePolicy();
        getPropertyInfoTab().fillTab(td);

        // 4 Claims were added manually
        checkTblClaimRowCount(4);
        viewEditClaim(Labels.THEFT);
        removeClaim();

        // Check that 1 Claim was removed. 3 Claims left
        checkTblClaimRowCount(3);
        getPropertyInfoTab().saveAndExit();

        policy.dataGather().start();
        navigateToPropertyInfoTab();
        viewEditClaim(Labels.WATER);
        removeClaim();

        // Check that table contains 2 claims
        checkTblClaimRowCount(2);
        getPropertyInfoTab().saveAndExit();
        String quoteNumber = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        // Bind Policy
        mainApp().open();
        SearchPage.openQuote(quoteNumber);
        policy.dataGather().start();
        policy.getDefaultView().fillUpTo(getPolicyTD("Rewrite", "TestDataForBindRewrittenPolicy"), getPropertyInfoTab().getClass());
        navigateToPropertyInfoTab();
        checkRemoveButtonAvailable(true);
        calculatePremiumAndOpenVRD();
        PropertyQuoteTab.RatingDetailsView.close();
        policy.getDefaultView().fillFromTo(getPolicyTD("Rewrite", "TestDataForBindRewrittenPolicy"), getPremiumAndCoveragesQuoteTab().getClass(), getBindTab().getClass());
        getBindTab().submitTab();
        overrideAllErrorsAndBind();
        getPurchaseTab().fillTab(getPolicyTD("DataGather", "TestData"));
        getPurchaseTab().submitTab();

        // Cancel Policy, Rewrite and check button availability for bound claims and newly added ones
        cancelAndRewritePolicy();

        checkRemoveButtonAvailable(false);
        getPropertyInfoTab().fillTab(td);
        // Last added Claim can be removed
        checkRemoveButtonAvailable(true);
        removeClaim();
        viewEditClaim(Labels.LIABILITY);
        checkRemoveButtonAvailable(false);
    }
    protected void pas6759_AbilityToRemoveManuallyEnteredClaimsRenewal(){

        // Get TestData for Adding Claims
        // Create Empty Testdata and Adjust it with list of claims
        TestData td = DataProviderFactory.dataOf(getPropertyInfoTab().getClass().getSimpleName(), DataProviderFactory.emptyData())
                .adjust(TestData.makeKeyPath(getPropertyInfoTab().getClass().getSimpleName(), getClaimHistoryLabel()), getClaimsTD());

        openAppAndCreatePolicy(getPolicyTD());
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime renewEff = PolicySummaryPage.getExpirationDate();

        mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(renewEff);
        openAppNonPrivilegedUser("A30");
        searchForPolicy(policyNumber);
        policy.renew().start();
        navigateToPropertyInfoTab();
        getPropertyInfoTab().fillTab(td);

        // 4 Claims were added manually
        checkTblClaimRowCount(4);
        viewEditClaim(Labels.THEFT);
        removeClaim();

        // Check that 1 Claim was removed. 3 Claims left
        checkTblClaimRowCount(3);
        getPropertyInfoTab().saveAndExit();

        // PAS-6759 AC2. Ability to remove Claims for unprivileged user while NB tx is not bound
        policy.renew().start().submit();
        navigateToPropertyInfoTab();
        viewEditClaim(Labels.WATER);
        removeClaim();

        // Check that table contains 2 claims
        checkTblClaimRowCount(2);
        getPropertyInfoTab().saveAndExit();
        mainApp().close();

        // Bind Policy
        mainApp().open();
        searchForPolicy(policyNumber);
        policy.renew().start().submit();
        navigateToPropertyInfoTab();
        checkRemoveButtonAvailable(true);
        calculatePremiumAndOpenVRD();
        PropertyQuoteTab.RatingDetailsView.close();
        navigateToBindTab();
        getBindTab().submitTab();
        overrideAllErrorsAndBind();
        payTotalAmtDue(policyNumber);
        PolicySummaryPage.buttonRenewals.click();

        checkAfterTXWasBound(policyNumber);
    }

    protected void pas6695_testClueClaimsReconciliationNB() {
        createCustomerWithClaimHistory();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD("DataGather", "TestData"), getPropertyInfoTab().getClass(), true);
        pas6742_CheckRemovedDependencyForCATAndChargeableFields();

    }

    protected void pas6695_testClueClaimsReconciliationEndorsement() {
        openAppAndCreatePolicy();
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        addNamedInsuredWithClaims();
        pas6742_CheckRemovedDependencyForCATAndChargeableFields();

    }

    protected void pas6695_testClueClaimsReconciliationRenewal() {
        openAppAndCreatePolicy();
        policy.renew().perform();
        addNamedInsuredWithClaims();
        pas6742_CheckRemovedDependencyForCATAndChargeableFields();

    }

    protected void pas6695_testClueClaimsReconciliationRewrite() {
        openAppAndCreatePolicy();
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
        addNamedInsuredWithClaims();
        pas6742_CheckRemovedDependencyForCATAndChargeableFields();

    }

    private void pas6742_CheckRemovedDependencyForCATAndChargeableFields(){

        //Validation for PAS-6695
        checkTblClaimRowCount(9);
        selectRentalClaimForCA();

        // Select Hail Claim and set CAT = YES chargeable = NO
        viewEditClaim(Labels.HAIL);
        selectRentalClaimForCA();
        // Set CAT no first so that chargeable is enabled
        getClaimCatastropheAsset().setValue("No");
        getClaimChargeableAsset().setValue("No");
        getClaimNonChargeableReasonAsset().setValue("Something");
        getClaimCatastropheAsset().setValue("Yes");

        // Check the chargeable Value is the same
        assertThat(getClaimChargeableAsset()).hasValue("No");
        assertThat(getClaimChargeableAsset()).isEnabled();
        // Check that Non Chargeable reason is not present because CAT is YES
        assertThat(getClaimNonChargeableReasonAsset()).isPresent(false);

        // Select Wind Claim and set CAT = YES chargeable = YES
        viewEditClaim(Labels.WIND);
        selectRentalClaimForCA();
        // Set CAT no first so that chargeable is enabled
        getClaimCatastropheAsset().setValue("No");
        getClaimChargeableAsset().setValue("Yes");
        getClaimCatastropheAsset().setValue("Yes");

        // Check the chargeable Value is the same
        assertThat(getClaimChargeableAsset()).hasValue("Yes");
        assertThat(getClaimChargeableAsset()).isEnabled();
        // Check that Non Chargeable reason is not present because CAT is YES
        assertThat(getClaimNonChargeableReasonAsset()).isPresent(false);

        // Select Fire Claim and set CAT = NO chargeable = YES
        viewEditClaim(Labels.FIRE);
        selectRentalClaimForCA();
        // Set CAT no first so that chargeable is enabled
        getClaimCatastropheAsset().setValue("No");
        getClaimChargeableAsset().setValue("Yes");

        // Select Water Claim and set CAT = NO chargeable = NO
        viewEditClaim(Labels.WATER);
        selectRentalClaimForCA();
        // Set CAT no first so that chargeable is enabled
        getClaimCatastropheAsset().setValue("No");
        getClaimChargeableAsset().setValue("No");
        getClaimNonChargeableReasonAsset().setValue("Something Else");
    }

    private void addNamedInsuredWithClaims() {
        TestData tdApplicantTab;
        if (isStateCA()) {
            TestData ni = DataProviderFactory.dataOf(
                    HomeCaMetaData.ApplicantTab.NamedInsured.BTN_ADD_INSURED.getLabel(), "Click",
                    HomeCaMetaData.ApplicantTab.NamedInsured.CUSTOMER_SEARCH.getLabel(), DataProviderFactory.emptyData(),
                    HomeCaMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel(), "Virat",
                    HomeCaMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel(), "Kohli",
                    HomeCaMetaData.ApplicantTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), "Spouse",
                    HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), "12/12/1985",
                    HomeCaMetaData.ApplicantTab.NamedInsured.OCCUPATION.getLabel(), "index=1");
            tdApplicantTab = DataProviderFactory.dataOf(getApplicantTab().getClass().getSimpleName(), DataProviderFactory.dataOf(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), ni));
        } else {
            TestData ni = DataProviderFactory.dataOf(
                    HomeSSMetaData.ApplicantTab.NamedInsured.BTN_ADD_INSURED.getLabel(), "Click",
                    HomeCaMetaData.ApplicantTab.NamedInsured.CUSTOMER_SEARCH.getLabel(), DataProviderFactory.emptyData(),
                    HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel(), "Virat",
                    HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel(), "Kohli",
                    HomeSSMetaData.ApplicantTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), "Spouse",
                    HomeSSMetaData.ApplicantTab.NamedInsured.MARITAL_STATUS.getLabel(), "Married",
                    HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), "12/12/1985",
                    HomeSSMetaData.ApplicantTab.NamedInsured.OCCUPATION.getLabel(), "index=1");
            tdApplicantTab = DataProviderFactory.dataOf(getApplicantTab().getClass().getSimpleName(), DataProviderFactory.dataOf(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), ni));
        }

        navigateToApplicantTab();
        getApplicantTab().fillTab(tdApplicantTab);
        getApplicantTab().submitTab();
        reorderClueReport();

        navigateToPropertyInfoTab();

    }

    private void removeClaim(){
        // Remove Claims with appropriate buttons for CA property or SS property
        if (isStateCA()){
            new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.BTN_REMOVE).click();
              } else {
            new PropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.BTN_REMOVE).click();
        }
        Page.dialogConfirmation.buttonYes.click();
    }

    private void checkRemoveButtonAvailable(Boolean expectedValue){
        // Check Remove Claims Button is unavailable for CA property or SS property
        if (isStateCA()){
            assertThat(new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.BTN_REMOVE)).isPresent(expectedValue);
        } else {
            assertThat(new PropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.BTN_REMOVE)).isPresent(expectedValue);
        }
    }

    private void checkTblClaimRowCount(Integer rowCount){
        if (isStateCA()){
            assertThat(new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab().tblClaimsList.getRowsCount()).isEqualTo(rowCount);
        } else {
            assertThat(new PropertyInfoTab().tblClaimsList.getRowsCount()).isEqualTo(rowCount);
        }
    }

    private void overrideAllErrorsAndBind() {
        // Override errors for CA property or SS property
        if (isStateCA()){
            new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().overrideAllErrors();
            new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().override();
        } else {
            new ErrorTab().overrideAllErrors();
            new ErrorTab().override();
        }
        getBindTab().submitTab();
    }

    private void checkAfterTXWasBound(String policyNumber){
        // Privileged User Removal Button
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Day"));

        // Not working until PAS-20443. Check That super user has ability to remove claim even if tx was bound.
        navigateToPropertyInfoTab();
//        checkRemoveButtonAvailable(true);

        getPropertyInfoTab().saveAndExit();
        mainApp().close();

        // Unprivileged User Check that Claim Cannot be removed
        openAppNonPrivilegedUser("A30");
        SearchPage.openPolicy(policyNumber);
        policy.endorse().start();
        navigateToPropertyInfoTab();
        checkRemoveButtonAvailable(false);
        getPropertyInfoTab().saveAndExit();
    }

    private void cancelAndRewritePolicy(){
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
        String quoteNumber = PolicySummaryPage.getPolicyNumber();
        mainApp().close();
        openAppNonPrivilegedUser("A30");
        SearchPage.openQuote(quoteNumber);
        policy.dataGather().start();
        navigateToPropertyInfoTab();
    }

    private void selectRentalClaimForCA(){
        if(isStateCA()){
            new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.RENTAL_CLAIM).setValue("Yes");
        }
    }

    private void createCustomerWithClaimHistory() {
        TestData td = getCustomerIndividualTD("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), "Virat")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), "Kohli");

        mainApp().open();
        createCustomerIndividual(td);
    }

}