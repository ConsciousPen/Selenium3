package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.PrivilegeEnum;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ClaimConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.toolkit.webdriver.customcontrols.MultiInstanceAfterAssetList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;

public abstract class TestClueSimplificationPropertyAbstract extends TestClaimPointsVRDPageAbstract {

    protected abstract Tab getBindTab();
    protected abstract Tab getPurchaseTab();
    protected abstract Tab getApplicantTab();
    protected abstract Tab getReportsTab();
    protected abstract void navigateToBindTab();
    protected abstract void navigateToApplicantTab();
    protected abstract RadioGroup getClaimIncludedInRatingAsset();
    protected abstract TextBox getClaimNonChargeableReasonAsset();
    protected abstract TextBox getClaimCatastropheRemarksAsset();
    protected abstract ComboBox getClaimSourceAsset();
    protected abstract String getBtnAddInsuredLabel();
    protected abstract ComboBox getClaimLossForAsset();
    protected abstract ComboBox getClaimStatusAsset();
    protected abstract void reorderClueReport();
    protected abstract String getNamedInsuredLabel();

    private String pas25173SSWarningMsg = "Underwriting approval is required for claim(s) that have been modified\n\n"+

                                            "*Claims occurring in the last 60 months are considered for tier and claims discount\n"+
                                            "*Claims occurring in the last 36 months are considered for claims points and eligibility\n"+
                                            "*Catastrophe claims are not considered for rating, but are considered for eligibility\n"+
                                            "*Paid open, subrogated, and closed claims are considered for rating and eligibility";

    private String pas25173CAWarningMsg = "Underwriting approval is required for claim(s) that have been modified\n\n"+

                                            "*Claims occurring in the last 60 months are considered for tier (DP3 policies only)\n"+
                                            "*Claims occurring in the last 36 months are considered for claims points and eligibility\n"+
                                            "*Catastrophe claims are not considered for rating, but are considered for eligibility\n"+
                                            "*Paid open, subrogated, and closed claims are considered for rating and eligibility";

    protected void pas6759_AbilityToRemoveManuallyEnteredClaimsNB() {

        // Initialize test data
        List<TestData> tdClaims = getClaimsTD();
        TestData td = getPolicyTD().adjust(TestData.makeKeyPath(getPropertyInfoTab().getClass().getSimpleName(), getClaimHistoryLabel()), tdClaims);

        createQuoteAndFillUpTo(td, getBindTab().getClass());
        getBindTab().saveAndExit();
        String quoteNumber = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        // PAS-6759 AC2. Ability to remove Claims for unprivileged user while NB tx is not bound
        openAppNonPrivilegedUser(PrivilegeEnum.Privilege.A30);
        SearchPage.openQuote(quoteNumber);
        policy.dataGather().start();
        navigateToPropertyInfoTab();

        // 4 Claims were added manually
        checkTblClaimRowCount(4);
        viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.THEFT);
        removeClaim();

        // Check that 1 Claim was removed. 3 Claims left
        checkTblClaimRowCount(3);
        getPropertyInfoTab().saveAndExit();

        // PAS-6759 AC2. Ability to remove Claims for unprivileged user while NB tx is not bound
        policy.dataGather().start();
        navigateToPropertyInfoTab();
        viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.WATER);
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
                .adjust(TestData.makeKeyPath(getPropertyInfoTab().getClass().getSimpleName(), getClaimHistoryLabel()), tdClaims);

        openAppAndCreatePolicy(getPolicyTD());
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        mainApp().close();
        openAppNonPrivilegedUser(PrivilegeEnum.Privilege.A30);
        searchForPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        navigateToPropertyInfoTab();

        // PAS-21609 Check that ClaimHistory section is visible,  PAS-20984 AC2
        checkClaimHistorySectionActive();

        getPropertyInfoTab().fillTab(td);

        // 4 Claims were added manually
        checkTblClaimRowCount(4);
        viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.THEFT);
        removeClaim();

        // Check that 1 Claim was removed. 3 Claims left
        checkTblClaimRowCount(3);
        getPropertyInfoTab().saveAndExit();

        // PAS-6759 AC2. Ability to remove Claims for unprivileged user while NB tx is not bound
        policy.endorse().start();
        navigateToPropertyInfoTab();
        viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.WATER);
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

        // PAS-21609 Check that ClaimHistory section is visible,  PAS-20984 AC2
        checkClaimHistorySectionActive();

        getPropertyInfoTab().fillTab(td);

        // 4 Claims were added manually
        checkTblClaimRowCount(4);
        viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.THEFT);
        removeClaim();

        // Check that 1 Claim was removed. 3 Claims left
        checkTblClaimRowCount(3);
        getPropertyInfoTab().saveAndExit();

        policy.dataGather().start();
        navigateToPropertyInfoTab();
        viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.WATER);
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
        viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.LIABILITY);
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

        // Open App and start renewal process with a privileged user because unprivileged user does not have a manual action for Home SS
        mainApp().open();
        searchForPolicy(policyNumber);
        policy.renew().performAndExit();
        mainApp().close();

        openAppNonPrivilegedUser(PrivilegeEnum.Privilege.A30);
        searchForPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        navigateToPropertyInfoTab();

        // PAS-21609 Check that ClaimHistory section is visible,  PAS-20984 AC2
        checkClaimHistorySectionActive();

        getPropertyInfoTab().fillTab(td);

        // 4 Claims were added manually
        checkTblClaimRowCount(4);
        viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.THEFT);
        removeClaim();

        // Check that 1 Claim was removed. 3 Claims left
        checkTblClaimRowCount(3);
        getPropertyInfoTab().saveAndExit();

        // PAS-6759 AC2. Ability to remove Claims for unprivileged user while NB tx is not bound
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        navigateToPropertyInfoTab();

        getPropertyInfoTab().fillTab(td);
        viewEditClaimByCauseOfLoss(ClaimConstants.CauseOfLoss.WATER);
        removeClaim();

        // Check that table contains 2 claims
        checkTblClaimRowCount(3);
        getPropertyInfoTab().saveAndExit();
    }

    protected void pas6695_testClueClaimsReconciliationNB() {
        TestData tdSilviaKohli;
        if (isDP3()) {
            createSpecificCustomerIndividual("ViratDP", "Kohli");
            tdSilviaKohli = getNamedInsuredTd("SilviaDP", "Kohli");
        } else {
            createSpecificCustomerIndividual("Virat", "Kohli");
            tdSilviaKohli = getNamedInsuredTd("Silvia", "Kohli");
        }
        TestData tdApplicantTab = DataProviderFactory.dataOf(getApplicantTab().getClass().getSimpleName(),
                DataProviderFactory.dataOf(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), tdSilviaKohli));

        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), getApplicantTab().getClass(), true);
        getApplicantTab().fillTab(tdApplicantTab).submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), getReportsTab().getClass(), getPropertyInfoTab().getClass(), true);
        //validation for Peril Not Covered Status
        validatePerilNotCoveredStatus();
        //Validation for PAS-6695 and PAS-6703
        validateNumberOfClaims();
        validateCatastropheAndLossForFields();

        // Validation for PAS-6742
        pas6742_pas20851_CheckRemovedDependencyForCATAndIncludedInRatingFields();

        // Validate PAS-20851 (already validated for other transactions in above method)
        tdApplicantTab = DataProviderFactory.dataOf(getApplicantTab().getClass().getSimpleName(),
                DataProviderFactory.dataOf(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), getNamedInsuredTd("James", "Wajakowski")));
        navigateToApplicantTab();
        getApplicantTab().fillTab(tdApplicantTab).submitTab();
        reorderClueReport();
        navigateToPropertyInfoTab();
        assertThat(getClaimIncludedInRatingAsset()).isEnabled();

        // Validation for PAS-22144
        openPolicyQuoteAsAgentUser();
        validateLossForFieldAsAgent();

    }

    protected void pas6695_testClueClaimsReconciliationEndorsement() {
        openAppAndCreatePolicy();
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        addNamedInsuredWithClaims();
        //validation for Peril Not Covered Status
        validatePerilNotCoveredStatus();
        // Validation for PAS-6695 and PAS-6703
        validateNumberOfClaims();
        validateCatastropheAndLossForFields();

        // Validation for PAS-6742
        pas6742_pas20851_CheckRemovedDependencyForCATAndIncludedInRatingFields();

        // Validation for PAS-22144
        openPolicyQuoteAsAgentUser();
        PolicySummaryPage.buttonPendedEndorsement.click();
        validateLossForFieldAsAgent();

    }

    protected void pas6695_testClueClaimsReconciliationRenewal() {
        openAppAndCreatePolicy();
        policy.renew().perform();
        addNamedInsuredWithClaims();
        //validation for Peril Not Covered Status
        validatePerilNotCoveredStatus();
        // Validation for PAS-6695 and PAS-6703
        validateNumberOfClaims();
        validateCatastropheAndLossForFields();

        // Validation for PAS-6742
        pas6742_pas20851_CheckRemovedDependencyForCATAndIncludedInRatingFields();

        // Validation for PAS-22144
        openPolicyQuoteAsAgentUser();
        PolicySummaryPage.buttonRenewals.click();
        validateLossForFieldAsAgent();

    }

    protected void pas6695_testClueClaimsReconciliationRewrite() {
        openAppAndCreatePolicy();
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
        policy.dataGather().start();
        addNamedInsuredWithClaims();

        //validation for Peril Not Covered Status
        validatePerilNotCoveredStatus();
        // Validation for PAS-6695 and PAS-6703
        validateNumberOfClaims();
        validateCatastropheAndLossForFields();

        // Validation for PAS-6742
        pas6742_pas20851_CheckRemovedDependencyForCATAndIncludedInRatingFields();

        // Validation for PAS-22144
        openPolicyQuoteAsAgentUser();
        validateLossForFieldAsAgent();

    }

    protected void pas6695_testClueClaimsReconciliationClaimantOnly() {
        if (isDP3()) {
            createSpecificCustomerIndividual("AgustinDP", "Miras");
        } else {
            createSpecificCustomerIndividual("Agustin", "Miras");
        }
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), getPropertyInfoTab().getClass(), true);
        checkTblClaimRowCount(0);

    }

    protected void pas6695_testClueClaimsReconciliationInsuredAndNotClaimant() {
        if (isDP3()) {
            createSpecificCustomerIndividual("MARSHADP", "LACKEYDP");
        } else {
            createSpecificCustomerIndividual("MARSHA", "LACKEY");
        }
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), getPropertyInfoTab().getClass(), true);
        assertThat(getClaimSourceAsset().getValue()).isEqualTo("CLUE");

    }

    protected void pas6703_testCatastropheIndicatorUnknownNB() {
        if (isDP3()) {
            createSpecificCustomerIndividual("SachinDP", "Kohli");
        } else {
            createSpecificCustomerIndividual("Sachin", "Kohli");
        }

        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), getPropertyInfoTab().getClass(), true);
        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            // Can't use checkTblClaimRowCount with 1 claim (no table present)
            assertThat(new PropertyInfoTab().tblClaimsList).isPresent(false);
            assertThat(getClaimSourceAsset().getValue()).isEqualTo("CLUE");
        } else {
            checkTblClaimRowCount(2);
            viewEditClaimByLossAmount("14000");
        }

        // Validates 'Applicant & Property' with catastrophe = 'Unknown'
        assertThat(getClaimLossForAsset().getValue()).isEqualTo(ClaimConstants.LossFor.APPLICANT_PROPERTY);
        assertThat(getClaimCatastropheAsset().getValue()).isEqualTo(Labels.RADIO_NO);

        if (!getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            // Validates 'Applicant' with catastrophe = 'Unknown'
            viewEditClaimByLossAmount("13000");
            assertThat(getClaimLossForAsset().getValue()).isEqualTo(ClaimConstants.LossFor.APPLICANT);
            assertThat(getClaimCatastropheAsset().getValue()).isEqualTo(Labels.RADIO_NO);
        }

    }

    protected void pas22075_testAddingNamedInsuredWithClueClaimsMidtermEndorsement() {
        TestData tdBruceKohli;

        // Create customer with CLUE claims and initiate policy
        if (isDP3()) {
            createSpecificCustomerIndividual("SilviaDP", "Kohli");
            tdBruceKohli = getNamedInsuredTd("BruceDP", "Kohli");
        } else {
            createSpecificCustomerIndividual("Silvia", "Kohli");
            tdBruceKohli = getNamedInsuredTd("Bruce", "Kohli");
        }

        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), getApplicantTab().getClass(), true);

        // Add 2 additional named insured (no claims)
        List<TestData> tdNamedInsured = new ArrayList<>();
        tdNamedInsured.add(getNamedInsuredTd("Jim", "Smith"));
        tdNamedInsured.add(getNamedInsuredTd("John", "Smith").mask(getBtnAddInsuredLabel()));
        TestData tdApplicantTab = DataProviderFactory.dataOf(getApplicantTab().getClass().getSimpleName(), DataProviderFactory.dataOf(getNamedInsuredLabel(), tdNamedInsured));
        getApplicantTab().fillTab(tdApplicantTab).submitTab();

        // Validate 2 claims on Property info tab, finish and bind policy (except SS DP3:  PAS-22188)
        getReportsTab().fillTab(getPolicyTD());
        if (!isStateCA()) {
            new ReportsTab().tblInsuranceScoreReport.getRow(2).getCell("Report").controls.links.getFirst().click();
            new ReportsTab().tblInsuranceScoreReport.getRow(3).getCell("Report").controls.links.getFirst().click();
        }

        getReportsTab().submitTab();
        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            // Can't use checkTblClaimRowCount with 1 claim (no table present)
            assertThat(new PropertyInfoTab().tblClaimsList).isPresent(false);
            assertThat(getClaimSourceAsset().getValue()).isEqualTo("CLUE");
            assertThat(getClaimLossForAsset().getValue()).isEqualTo(ClaimConstants.LossFor.APPLICANT_PROPERTY);
        } else {
            checkTblClaimRowCount(2);
        }

        selectRentalClaimForCADP3();
        policy.getDefaultView().fillFromTo(getPolicyTD(), getPropertyInfoTab().getClass(), getPurchaseTab().getClass(), true);
        getPurchaseTab().submitTab();

        // Initiate endorsement and add a named insured that returns additional Clue claims
        tdApplicantTab = DataProviderFactory.dataOf(getApplicantTab().getClass().getSimpleName(), DataProviderFactory.dataOf(getNamedInsuredLabel(), tdBruceKohli));
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus3Days"));
        navigateToApplicantTab();
        getApplicantTab().fillTab(tdApplicantTab).submitTab();
        reorderClueReport();
        navigateToPropertyInfoTab();

        // Validate 4 claims on Property info tab (except SS DP3:  PAS-22188)
        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            checkTblClaimRowCount(3);
        } else {
            checkTblClaimRowCount(4);
        }

    }

    protected void pas21557_RequireUWRuleCATIndicatorIncludeInRatingAndEligibilityFieldsAreChanged() {
        TestData tdSilviaKohli;
        if (isDP3()) {
            createSpecificCustomerIndividual("ViratDP", "Kohli");
            tdSilviaKohli = getNamedInsuredTd("SilviaDP", "Kohli");
        } else {
            createSpecificCustomerIndividual("Virat", "Kohli");
            tdSilviaKohli = getNamedInsuredTd("Silvia", "Kohli");
        }
        TestData tdApplicantTab = DataProviderFactory.dataOf(getApplicantTab().getClass().getSimpleName(),
                DataProviderFactory.dataOf(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), tdSilviaKohli));
        TestData policyTDnoPremiumAndCoverages = getPolicyTD().adjust(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), new SimpleDataProvider());

        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), getApplicantTab().getClass(), true);
        getApplicantTab().fillTab(tdApplicantTab).submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), getReportsTab().getClass(), getPropertyInfoTab().getClass(), true);

        selectRentalClaimForCADP3();
        // Validate non-dependency between CAT and Chargeable indicator radio buttons
        getClaimIncludedInRatingAsset().setValue("No");
        getClaimNonChargeableReasonAsset().setValue("Something");
        // Validate Warning message when Include in rating field changed
        validateWarningMessage();
        getClaimCatastropheAsset().setValue("No");

        // Validate Warning message when CAT field and Chargeable field changed
        validateWarningMessage();

        viewEditClaimByLossAmount("10588");
        selectRentalClaimForCADP3();
        getClaimIncludedInRatingAsset().setValue("No");
        getClaimNonChargeableReasonAsset().setValue("Something");
        // Validate Warning message when Include in rating field changed
        validateWarningMessage();

        viewEditClaimByLossAmount("11000");
        selectRentalClaimForCADP3();
        getClaimCatastropheAsset().setValue("No");
        // Validate Warning message when CAT field changed
        validateWarningMessage();

        calculatePremiumAndOpenVRD();
        PropertyQuoteTab.RatingDetailsView.close();
        policy.getDefaultView().fillFromTo(policyTDnoPremiumAndCoverages, getPremiumAndCoveragesQuoteTab().getClass(), getBindTab().getClass());
        getBindTab().submitTab();
        // Override errors for CA property or SS property
        if (isStateCA()){
            // Check UW rule is added
            new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_CA1210012);
            assertThat(new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().getErrorRowFrequencyByCode(ErrorEnum.Errors.ERROR_AAA_HO_CA1210012.getCode())).isEqualTo(3);
            new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().overrideAllErrors(ErrorEnum.Duration.TERM, ErrorEnum.ReasonForOverride.OTHER);
            new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().override();
        } else {
            // Check UW rule is added
            new ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS1210011);
            assertThat(new ErrorTab().getErrorRowFrequencyByCode(ErrorEnum.Errors.ERROR_AAA_HO_SS1210011.getCode())).isEqualTo(3);
            new ErrorTab().overrideAllErrors(ErrorEnum.Duration.TERM, ErrorEnum.ReasonForOverride.OTHER);
            new ErrorTab().override();
        }
        getBindTab().submitTab();
        getPurchaseTab().fillTab(getPolicyTD("DataGather", "TestData"));
        getPurchaseTab().submitTab();
        //Endorsement check that no UW rules are fired.
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        calculatePremiumAndOpenVRD();
        PropertyQuoteTab.RatingDetailsView.close();
        navigateToBindTab();
        getBindTab().submitTab();
        if(isStateCA()){
            new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().overrideAllErrors(ErrorEnum.Duration.TERM, ErrorEnum.ReasonForOverride.OTHER);
            new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().override();
            getBindTab().submitTab();
        }
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        LocalDateTime renewEffDate = PolicySummaryPage.getExpirationDate();
        //Change Date renew policy and no override same UW rules
        TimeSetterUtil.getInstance().nextPhase(renewEffDate);
        searchForPolicy(policyNumber);
        policy.renew().perform();
        calculatePremiumAndOpenVRD();
        PropertyQuoteTab.RatingDetailsView.close();
        navigateToBindTab();
        getBindTab().submitTab();
        // Override errors for CA property or SS property
        if (isStateCA()){
            // Check UW rule is added
            new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_CA1210012);
            assertThat(new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().getErrorRowFrequencyByCode(ErrorEnum.Errors.ERROR_AAA_HO_CA1210012.getCode())).isEqualTo(3);
            new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().overrideAllErrors(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.OTHER);
            new aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab().override();
        } else {
            // Check UW rule is added
            new ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS1210011);
            assertThat(new ErrorTab().getErrorRowFrequencyByCode(ErrorEnum.Errors.ERROR_AAA_HO_SS1210011.getCode())).isEqualTo(3);
            new ErrorTab().overrideAllErrors(ErrorEnum.Duration.LIFE, ErrorEnum.ReasonForOverride.OTHER);
            new ErrorTab().override();
        }
        getBindTab().submitTab();
        payTotalAmtDue(policyNumber);
        // Create new renewal with rules overriden for life and bind renewal with no UW rules
        PolicySummaryPage.buttonRenewals.click();
        policy.renew().perform();
        calculatePremiumAndOpenVRD();
        PropertyQuoteTab.RatingDetailsView.close();
        navigateToBindTab();
        getBindTab().submitTab();
        assertThat(PolicySummaryPage.getPolicyNumber()).contains(policyNumber);

    }

    protected void pas23639_testClueMappingForIncludedInRatingFieldNB() {
        if (isDP3()) {
            createSpecificCustomerIndividual("TestDP", "IIRE");
        } else {
            createSpecificCustomerIndividual("Test", "IIRE");
        }
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), getPropertyInfoTab().getClass(), true);
        validateClaimsIIRE();

    }

    protected void pas23639_testClueMappingForIncludedInRatingFieldEndorsement() {
        openAppAndCreatePolicy();
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        addNamedInsuredWithClaimsForIIRE();
        validateClaimsIIRE();

    }

    protected void pas23639_testClueMappingForIncludedInRatingFieldRenewal() {
        openAppAndCreatePolicy();
        policy.renew().perform();
        addNamedInsuredWithClaimsForIIRE();
        validateClaimsIIRE();
    }

    protected void pas23639_testClueMappingForIncludedInRatingFieldRewrite() {
        openAppAndCreatePolicy();
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
        policy.dataGather().start();
        addNamedInsuredWithClaimsForIIRE();
        validateClaimsIIRE();
    }

    private void validateClaimsIIRE() {
        for (int i = 1; i <= 4; i++) {
            getClaimHistoryTable().getRow(i).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.MODIFY).controls.links.getFirst().click();
            assertThat(getClaimHistoryTable().getRow(i).getCell(PolicyConstants.PropertyInfoClaimHistoryTable.INCLUDED_IN_RATING_AND_ELIGIBILITY).getValue()).isEqualTo("Yes");
            assertThat(getClaimIncludedInRatingAsset().getValue()).isEqualTo("Yes");
        }
    }

    private void pas6742_pas20851_CheckRemovedDependencyForCATAndIncludedInRatingFields(){
        // 'Chargeable' label was changed to 'Included in Rating'

        selectRentalClaimForCADP3();

        // Select Hail Claim
        viewEditClaimByLossAmount("10588");
        selectRentalClaimForCADP3();

        // Verify CAT = RADIO_NO chargeable = RADIO_YES
        assertThat(getClaimCatastropheAsset()).hasValue("No");
        assertThat(getClaimIncludedInRatingAsset()).hasValue("Yes");

        // Verify Chargeable text field and CAT code/remarks text field are both hidden
        assertThat(getClaimNonChargeableReasonAsset()).isAbsent();
        assertThat(getClaimCatastropheRemarksAsset()).isAbsent();

        // Validate non-dependency between CAT and Chargeable indicator radio buttons
        getClaimIncludedInRatingAsset().setValue("No");
        // Validate Warning message when Include in rating field changed
        validateWarningMessage();
        getClaimNonChargeableReasonAsset().setValue("Something");
        getClaimCatastropheAsset().setValue("Yes");
        // Validate Warning message when CAT field changed
        validateWarningMessage();
        getClaimCatastropheRemarksAsset().setValue("CAT");

        // Verify Chargeable text field and CAT code/remarks text field are both visible
        assertThat(getClaimNonChargeableReasonAsset()).isPresent();
        assertThat(getClaimCatastropheRemarksAsset()).isPresent();

        // Check the chargeable Value is the same
        assertThat(getClaimIncludedInRatingAsset()).hasValue("No");
        assertThat(getClaimIncludedInRatingAsset()).isEnabled();

        // Select Fire Claim
        viewEditClaimByLossAmount("999");
        selectRentalClaimForCADP3();

        // Verify CAT = RADIO_YES chargeable = RADIO_YES
        assertThat(getClaimCatastropheAsset()).hasValue("Yes");
        assertThat(getClaimIncludedInRatingAsset()).hasValue("Yes");

        // Set CAT no and verify non-dependency with Chargeable indicator
        getClaimCatastropheAsset().setValue("No");
        // Validate Warning message when CAT field changed
        validateWarningMessage();
        assertThat(getClaimCatastropheAsset()).hasValue("No");
        assertThat(getClaimIncludedInRatingAsset()).hasValue("Yes");

        // Select Water Claim
        viewEditClaimByLossAmount("42500");
        selectRentalClaimForCADP3();

        // Verify CAT = RADIO_NO chargeable = RADIO_YES
        assertThat(getClaimCatastropheAsset()).hasValue("No");
        assertThat(getClaimIncludedInRatingAsset()).hasValue("Yes");

        // Set CAT no first so that chargeable is enabled
        getClaimCatastropheAsset().setValue("Yes");
        // Validate Warning message when CAT field changed
        validateWarningMessage();
        getClaimIncludedInRatingAsset().setValue("No");
        // Validate Warning message when Include in rating field changed
        validateWarningMessage();
        getClaimNonChargeableReasonAsset().setValue("Something Else");
        getClaimCatastropheRemarksAsset().setValue("CAT");
        assertThat(getClaimCatastropheAsset()).hasValue("Yes");
        assertThat(getClaimIncludedInRatingAsset()).hasValue("No");

        // Select Wind Claim and set CAT = RADIO_YES chargeable = RADIO_YES (all except SS DP3)
        if (!getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            viewEditClaimByLossAmount("2500");
            selectRentalClaimForCADP3();

            // Set CAT no first so that chargeable is enabled
            assertThat(getClaimCatastropheAsset()).hasValue("No");
            assertThat(getClaimIncludedInRatingAsset()).hasValue("Yes");
            getClaimCatastropheAsset().setValue("Yes");
            // Validate Warning message when CAT field changed
            validateWarningMessage();
            getClaimCatastropheRemarksAsset().setValue("CAT");

            // Check the chargeable Value is the same
            assertThat(getClaimIncludedInRatingAsset()).hasValue("Yes");
            assertThat(getClaimIncludedInRatingAsset()).isEnabled();

            // Check that Non Chargeable reason is not present because CAT is RADIO_YES
            assertThat(getClaimNonChargeableReasonAsset()).isAbsent();
        }
    }

    private void addNamedInsuredWithClaims() {
        List<TestData> tdNamedInsured = new ArrayList<>();
        if (isDP3()) {
            tdNamedInsured.add(getNamedInsuredTd("ViratDP", "Kohli"));
            tdNamedInsured.add(getNamedInsuredTd("SilviaDP", "Kohli").mask(getBtnAddInsuredLabel()));
        } else {
            tdNamedInsured.add(getNamedInsuredTd("Virat", "Kohli"));
            tdNamedInsured.add(getNamedInsuredTd("Silvia", "Kohli").mask(getBtnAddInsuredLabel()));
        }

        TestData tdApplicantTab = DataProviderFactory.dataOf(getApplicantTab().getClass().getSimpleName(),
                DataProviderFactory.dataOf(getNamedInsuredLabel(), tdNamedInsured));

        navigateToApplicantTab();
        getApplicantTab().fillTab(tdApplicantTab).submitTab();
        reorderClueReport();
        navigateToPropertyInfoTab();
    }

    private void addNamedInsuredWithClaimsForIIRE() {
        List<TestData> tdNamedInsured = new ArrayList<>();
        if (isDP3()) {
            tdNamedInsured.add(getNamedInsuredTd("TestDP", "IIRE"));
        } else {
            tdNamedInsured.add(getNamedInsuredTd("Test", "IIRE"));
        }
        TestData tdApplicantTab = DataProviderFactory.dataOf(getApplicantTab().getClass().getSimpleName(), DataProviderFactory.dataOf(getNamedInsuredLabel(), tdNamedInsured));

        navigateToApplicantTab();
        getApplicantTab().fillTab(tdApplicantTab).submitTab();
        reorderClueReport();
        navigateToPropertyInfoTab();
    }

    private TestData getNamedInsuredTd(String fName, String lName) {
        if (isStateCA()) {
            return DataProviderFactory.dataOf(
                    HomeCaMetaData.ApplicantTab.NamedInsured.BTN_ADD_INSURED.getLabel(), "Click",
                    HomeCaMetaData.ApplicantTab.NamedInsured.CUSTOMER_SEARCH.getLabel(), DataProviderFactory.emptyData(),
                    HomeCaMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel(), fName,
                    HomeCaMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel(), lName,
                    HomeCaMetaData.ApplicantTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), "Parent",
                    HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), "12/12/1985",
                    HomeCaMetaData.ApplicantTab.NamedInsured.OCCUPATION.getLabel(), "Other");
        }
        return DataProviderFactory.dataOf(
                HomeSSMetaData.ApplicantTab.NamedInsured.BTN_ADD_INSURED.getLabel(), "Click",
                HomeCaMetaData.ApplicantTab.NamedInsured.CUSTOMER_SEARCH.getLabel(), DataProviderFactory.emptyData(),
                HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel(), fName,
                HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel(), lName,
                HomeSSMetaData.ApplicantTab.NamedInsured.RELATIONSHIP_TO_PRIMARY_NAMED_INSURED.getLabel(), "Parent",
                HomeSSMetaData.ApplicantTab.NamedInsured.MARITAL_STATUS.getLabel(), "Married",
                HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), "12/12/1985",
                HomeSSMetaData.ApplicantTab.NamedInsured.OCCUPATION.getLabel(), "Other");
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

    private void checkClaimHistorySectionActive(){
        if (isStateCA()){
            assertThat(new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.LABEL_CLAIM_HISTORY)).isPresent();
        } else {
            assertThat(getPropertyInfoTab().getAssetList().getAsset(HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY).getAsset(HomeSSMetaData.PropertyInfoTab.ClaimHistory.LABEL_CLAIM_HISTORY)).isPresent();
        }
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
        openAppNonPrivilegedUser(PrivilegeEnum.Privilege.A30);
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
        openAppNonPrivilegedUser(PrivilegeEnum.Privilege.A30);
        SearchPage.openQuote(quoteNumber);
        policy.dataGather().start();
        navigateToPropertyInfoTab();
    }

    private void selectRentalClaimForCADP3(){
        if(getPolicyType().equals(PolicyType.HOME_CA_DP3)){
            new aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab().getClaimHistoryAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.ClaimHistory.RENTAL_CLAIM).setValue("Yes");
        }
    }

    private void createSpecificCustomerIndividual(String fName, String lName) {
        TestData td = getCustomerIndividualTD("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), fName)
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), lName);

        mainApp().open();
        createCustomerIndividual(td);
    }

    private void validateCatastropheAndLossForFields() {

        // Validates 'Applicant & Property' with catastrophe = 'Yes'
        viewEditClaimByLossAmount("11000");
        assertThat(getClaimLossForAsset().getValue()).isEqualTo(ClaimConstants.LossFor.APPLICANT_PROPERTY);
        assertThat(getClaimCatastropheAsset().getValue()).isEqualTo(Labels.RADIO_YES);
        // Validate Loss For Field is enabled for L41 (PAS-22144)
        assertThat(getClaimLossForAsset()).isEnabled();

        // Validates 'Applicant & Property' with catastrophe = 'No'
        viewEditClaimByLossAmount("42500");
        assertThat(getClaimLossForAsset().getValue()).isEqualTo(ClaimConstants.LossFor.APPLICANT_PROPERTY);
        assertThat(getClaimCatastropheAsset().getValue()).isEqualTo(Labels.RADIO_NO);
        // Validate Loss For Field is enabled for L41 (PAS-22144)
        assertThat(getClaimLossForAsset()).isEnabled();

        if (!getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            // Validates 'Applicant' with catastrophe = 'Yes'
            viewEditClaimByLossAmount("1500");
            assertThat(getClaimLossForAsset().getValue()).isEqualTo(ClaimConstants.LossFor.APPLICANT);
            assertThat(getClaimCatastropheAsset().getValue()).isEqualTo(Labels.RADIO_YES);
            // Validate Loss For Field is enabled for L41 (PAS-22144)
            assertThat(getClaimLossForAsset()).isEnabled();

            // Validates 'Applicant' with catastrophe = 'No'
            viewEditClaimByLossAmount("2500");
            assertThat(getClaimLossForAsset().getValue()).isEqualTo(ClaimConstants.LossFor.APPLICANT);
            assertThat(getClaimCatastropheAsset().getValue()).isEqualTo(Labels.RADIO_NO);
            // Validate Loss For Field is enabled for L41 (PAS-22144)
            assertThat(getClaimLossForAsset()).isEnabled();
        }

    }

    private void openPolicyQuoteAsAgentUser() {
        String policyQuoteNum = getPropertyInfoTab().getPolicyNumber();
        getPropertyInfoTab().saveAndExit();
        mainApp().close();
        openAppNonPrivilegedUser(PrivilegeEnum.Privilege.A30);
        if (policyQuoteNum.startsWith("Q")) {
            SearchPage.openQuote(policyQuoteNum);
        } else {
            SearchPage.openPolicy(policyQuoteNum);
        }

    }

    private void validateLossForFieldAsAgent() {
        policy.dataGather().start();
        navigateToPropertyInfoTab();
        viewEditClaimByLossAmount("42500");
        assertThat(getClaimLossForAsset()).isDisabled();
        viewEditClaimByLossAmount("11000");
        assertThat(getClaimLossForAsset()).isDisabled();

        if (!getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            viewEditClaimByLossAmount("1500");
            assertThat(getClaimLossForAsset()).isDisabled();
            viewEditClaimByLossAmount("2500");
            assertThat(getClaimLossForAsset()).isDisabled();
        }

    }

    private void validateNumberOfClaims() {
        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            checkTblClaimRowCount(5);
        } else {
            checkTblClaimRowCount(9);
        }
    }

    private void validateWarningMessage(){
        //PAS-25173 [replacing PAS-21557] - a new requirement to update the warning message - disposition 'CAT' or 'IIRE' frm [Yes to No] or [No to Yes]
        if (isStateCA()){
            // Check warning message is fired for CA
            assertThat(PropertyQuoteTab.tableClaimsHistoryWarningMessages.getRow(1).getCell(1).getValue()).isEqualTo(pas25173CAWarningMsg);
        } else {
            // Check warning message is fired for SS
            assertThat(PropertyQuoteTab.tableClaimsHistoryWarningMessages.getRow(1).getCell(1).getValue()).isEqualTo(pas25173SSWarningMsg);
        }

    }
    
    private boolean isDP3() {
        return getPolicyType().equals(PolicyType.HOME_SS_DP3) || getPolicyType().equals(PolicyType.HOME_CA_DP3);
    }

    private void validatePerilNotCoveredStatus(){
        viewEditClaimByLossAmount("11000");
        assertThat(getClaimStatusAsset().getValue()).isEqualTo("Peril Not Covered");
    }

}