package aaa.modules.regression.sales.home_ca.ho3;

import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ca.defaulttabs.UnderwritingAndApprovalTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;

public class TestQuoteUnderwritingRules extends HomeCaHO3BaseTest {

    BindTab bindTab = new BindTab();

    /**
      * @author Jurij Kuznecov
      * @name Test CAH Quote Underwriting Rules
      * @scenario 
      * 1.  Create new or open existent Customer
      * 2.  Start CAH quote creation
      * 3.  Fill all mandatory fields, calculate premium. Navigate to UW&A tab
      * 4.  Fill UW tab according to "TestQuoteUnderwritingRules" "TestData_Wrong" dataset
      * 5.  Check error messages
      * 6.  Set 3 to Total number of part time and full time resident employees, fill Remarks, check WM0541 error on Bind action
      * 7.  Fill UW with correct values
      * 8.  Check Remarks fields and WM0533, WM0534, WM0535, WM0533, WM0540, WM9801 errors on Bind action
      * 9.  Fill the rest tabs and issue policy
      * 10. Check Policy status is Active
      */

    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void testQuoteUnderwritingRules(String state) {
        UnderwritingAndApprovalTab underwritingAndApprovalTab = new UnderwritingAndApprovalTab();

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), UnderwritingAndApprovalTab.class, false);

        //  4. Fill UW tab according to "TestQuoteUnderwritingRules" "TestData_Wrong" dataset
        policy.getDefaultView().fill(getTestSpecificTD("TestData_Wrong"));
        underwritingAndApprovalTab.submitTab();

        //  5.  Check error messages
        underwritingAndApprovalTab.getAssetList().getWarning(HomeCaMetaData.UnderwritingAndApprovalTab.HAVE_ANY_OF_THE_APPLICANT_S_CURRENT_PETS_INJURED_CREATURE.getLabel()).verify
                .contains("Applicants/insureds with any dogs or other animals, reptiles, or pets with any prior biting history are unacceptable.");
        underwritingAndApprovalTab.getAssetList().getWarning(HomeCaMetaData.UnderwritingAndApprovalTab.ARE_ALL_WATER_HEATERS_STRAPPED_TO_THE_WALL.getLabel()).verify
                .contains("Water heaters (except electric heaters) must be strapped to the wall or if located in the garage must be raised at least 18 inches from the floor.");
        underwritingAndApprovalTab.getAssetList().getWarning(HomeCaMetaData.UnderwritingAndApprovalTab.DO_YOU_HAVE_A_LICENSE.getLabel()).verify
                .contains("Dwellings or applicants that perform a home day care, including child day care, adult day care, or pet day care, are unacceptable unless they are licensed and insured.");
        underwritingAndApprovalTab.getAssetList().getWarning(HomeCaMetaData.UnderwritingAndApprovalTab.IS_IT_A_FOR_PROFIT_BUSINESS.getLabel()).verify
                .contains("Farming/Ranching on premises is unacceptable unless it is incidental and not for profit.");
        underwritingAndApprovalTab.getAssetList().getWarning(HomeCaMetaData.UnderwritingAndApprovalTab.INCIDENTAL_BUSINESS_OCCUPANCY.getLabel()).verify
                .contains("Policies must be endorsed with the HO 04 42 10 00 Permitted Incidental Occupancies – Residence Premises Endorsement when a permitted incidental occupancy exposure is present on the premises and is deemed eligible for coverage.");
        underwritingAndApprovalTab.getAssetList().getWarning(HomeCaMetaData.UnderwritingAndApprovalTab.OTHERS.getLabel()).verify.contains("Other business exposures on premises are unacceptable.");

        //  6.  Set 3 to Total number of part time and full time resident employees, fill Remarks, check WM0541 error on Bind action
        policy.getDefaultView().fill(getTestSpecificTD("TestData_TotalNumber3"));
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
        policy.getDefaultView().fill(getPolicyTD().ksam(DocumentsTab.class.getSimpleName()));
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_HO_CA12230792);

        //  7.  Fill UW with correct values
        policy.getDefaultView().fill(getPolicyTD().ksam(UnderwritingAndApprovalTab.class.getSimpleName()));

        //  8.  Check Remarks fields and WM0533, WM0535, WM9801 errors on Bind action
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.UNDERWRITING_AND_APPROVAL.get());
        underwritingAndApprovalTab.getAssetList().getAsset(HomeCaMetaData.UnderwritingAndApprovalTab.HAVE_ANY_APPLICANTS_HAD_A_PRIOR_INSURANCE_POLICY_CANCELLED_IN_THE_PAST_3_YEARS).setValue("Yes");
        underwritingAndApprovalTab.getAssetList().getAsset(HomeCaMetaData.UnderwritingAndApprovalTab.REMARK_PRIOR_INSURANCE).setValue("Remarks");
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_HO_CA338657_18);
        underwritingAndApprovalTab.getAssetList().getAsset(HomeCaMetaData.UnderwritingAndApprovalTab.HAVE_ANY_APPLICANTS_HAD_A_PRIOR_INSURANCE_POLICY_CANCELLED_IN_THE_PAST_3_YEARS).setValue("No");

        underwritingAndApprovalTab.getAssetList().getAsset(HomeCaMetaData.UnderwritingAndApprovalTab.HAS_THE_PROPERTY_BEEN_IN_FORECLOSURE_PROCEEDINGS_WITHIN_THE_PAST_18_MONTHS).setValue("Yes");
        underwritingAndApprovalTab.getAssetList().getAsset(HomeCaMetaData.UnderwritingAndApprovalTab.REMARK_FORECLOSURE).setValue("Remarks");
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_HO_CA338657_20);
        underwritingAndApprovalTab.getAssetList().getAsset(HomeCaMetaData.UnderwritingAndApprovalTab.HAS_THE_PROPERTY_BEEN_IN_FORECLOSURE_PROCEEDINGS_WITHIN_THE_PAST_18_MONTHS).setValue("No");

        underwritingAndApprovalTab.getAssetList().getAsset(HomeCaMetaData.UnderwritingAndApprovalTab.IS_THE_DWELLING_LOCATED_WITHIN_500_FEET_OF_BAY_OR_COASTAL_WATERS).setValue("Yes");
        goToBindAndVerifyError(ErrorEnum.Errors.ERROR_AAA_HO_CA338657_36);
        underwritingAndApprovalTab.getAssetList().getAsset(HomeCaMetaData.UnderwritingAndApprovalTab.IS_THE_DWELLING_LOCATED_WITHIN_500_FEET_OF_BAY_OR_COASTAL_WATERS).setValue("No");

        //  9.  Fill the rest tabs and issue policy
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        policy.getDefaultView().fillFromTo(getPolicyTD(), BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        // 10. Check Policy status is Active
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    private void goToBindAndVerifyError(ErrorEnum.Errors... errors) {
        ErrorTab errorTab = new ErrorTab();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        bindTab.btnPurchase.click();
        errorTab.verify.errorsPresent(errors);
        errorTab.cancel();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.UNDERWRITING_AND_APPROVAL.get());
    }
}
