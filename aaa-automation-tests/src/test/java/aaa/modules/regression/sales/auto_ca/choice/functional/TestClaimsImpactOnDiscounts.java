package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsCATemplate;
import aaa.toolkit.webdriver.customcontrols.ActivityInformationMultiAssetList;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.remote.service.DriverCommandExecutor;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.RadioGroup;

import java.util.Map;

import static aaa.main.pages.summary.PolicySummaryPage.buttonRenewals;
import static org.assertj.core.api.Assertions.assertThat;

@StateList(states = {Constants.States.CA})
public class TestClaimsImpactOnDiscounts extends TestOfflineClaimsCATemplate {

    private static final String CLAIM_NUMBER_1 = "Claim-GDD-111";
    private static final String CLAIM_NUMBER_2 = "Claim-GDD-222";
    private static final String GDD_PU_CLAIMS_DATA_MODEL = "gdd_PUClaims_data_model.yaml";

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    /**
     * @author Mantas Garsvinskas
     * PAS-18303 - Renewal: Good Driver Discount Cannot be Influenced by Permissive Use Claims (CAS/CLUE/CCInputs)
     * PAS-23190 - Endorsement/NB/Rewrite: Good Driver Discount Cannot be Influenced by Permissive Use Claims (CAS/CLUE/CCInputs)
     * @name Test Permissive Use Claims (CC input/internal/CLUE) impact on policy Good Driver Discount (GDD)
     * @scenario Test Steps:
     * 1. Create a Quote with 1 driver:
     * 1.1: FNI: 2 CLUE Claims (Accidents); 2 CAS Claims (visible on R only); 2 CC Input Claims: Major/Minor Violation;
     * 1.2 All Claims have Points: more than 1 and are Included in rating.
     * 2. Leave one CC Input Claim as NOT Permissive Use (PU = No)
     * 3. - Verify: GDD is not available for Quote
     * 4. Change all Claims to PU Claims;
     * 5. - Verify: GDD is available for Quote with only PU Claims;
     * 6. Issue Policy
     * 7. R-63: Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 8. R-46: Run Renewal Part2 + "renewalClaimReceiveAsyncJob"
     * 9. Repeat Validations on Renewal, Endorsement, Rewritten Quote;
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = {"PAS-18303", "PAS-23190"})
    public void pas18303_goodDriverDiscountForPUClaims(@Optional("CA") @SuppressWarnings("unused") String state) {

        // Claim Dates: claimDateOfLoss/claimOpenDate/claimCloseDate all are the same
        String claims_dates = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1).minusDays(1).toLocalDate().toString();

        Map<String, String> UPDATE_CAS_RESPONSE_DATE_FIELDS =
                ImmutableMap.of(CLAIM_NUMBER_1, claims_dates, CLAIM_NUMBER_2, claims_dates);


        // Toggle ON PermissiveUse Logic
		// Set DATEOFLOSS Parameter in DB: Equal to Claim3 dateOfLoss
		DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
		DBService.get().executeUpdate(String.format(SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS, "11-NOV-18"));

        TestData testData = getTestSpecificTD("TestData_DriverTab_DiscountsGDD_CAC").resolveLinks();
        TestData td = getPolicyTD().adjust(testData);


        // --------------------------------------- Verify GDD during NB Quote Creation ---------------------------------------
        createQuoteAndFillUpTo(td, DriverActivityReportsTab.class);

        validateGDD();

        //policy.getDefaultView().fillFromTo(td, PremiumAndCoveragesTab.class, DocumentsAndBindTab.class, true);
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().fillTab(td).submitTab();
        //policy.getDefaultView().getTab(DocumentsAndBindTab.class).submitTab();


        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        mainApp().close();


        runRenewalClaimOrderJob();     // Move to R-63, run batch job part 1 and offline claims batch job
        generateClaimRequest();        // Download claim request and assert it

        // Create the claim response
        createCasClaimResponseAndUploadWithUpdatedDates(policyNumber, GDD_PU_CLAIMS_DATA_MODEL, UPDATE_CAS_RESPONSE_DATE_FIELDS);

        runRenewalClaimReceiveJob();   // Move to R-46 and run batch job part 2 and offline claims receive batch job

        // Retrieve policy
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

        // --------------------------------------- Verify GDD during Renewal Quote Creation ---------------------------------------
        buttonRenewals.click();
        policy.dataGather().start();
        validateGDD();
        premiumAndCoveragesTab.saveAndExit();

        // --------------------------------------- Verify GDD during Endorsement Quote Creation ---------------------------------------
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        validateGDD();
        premiumAndCoveragesTab.saveAndExit();


        // --------------------------------------- Verify GDD during Rewritten Quote Creation ---------------------------------------
        policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
        policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));

        policy.dataGather().start();
        validateGDD();

    }
    /*
    Method Validates P&C tab, and that Good Driver Discount is applied with Permissive Use Claims only
     */
    private void validateGDD(){
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());

        //Making sure that PU = Yes, and its included in rating.

        ActivityInformationMultiAssetList activityInformationAssetList = new DriverTab().getActivityInformationAssetList();

        for (int i = 1; i <= DriverTab.tableActivityInformationList.getAllRowsCount(); i++){
            DriverTab.tableActivityInformationList.selectRow(i);
            if (activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_YAF.getLabel(), RadioGroup.class).getValue().equals("No")) {
                activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_YAF.getLabel(), RadioGroup.class).setValue("Yes");
            } else if (activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS.getLabel(), RadioGroup.class).getValue().equals("No")) {
                activityInformationAssetList.getAsset(AutoCaMetaData.DriverTab.ActivityInformation.PERMISSIVE_USE_LOSS.getLabel(), RadioGroup.class).setValue("Yes");
            }
        }
        //TODO:gunxgar Test data: Change CLUE claims to have Occ Dates and!


        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        premiumAndCoveragesTab.calculatePremium();

        assertThat(PremiumAndCoveragesTab.tableDiscounts.getColumn(1).getCell(1).getValue()).contains("Good Driver (Matthew Fox)");
        //for each, List Items: For, check if contains


        //Negative Case: Make One Claimas non PU; validate;
        //TODO:gunxgar NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER.get());


    }
}
