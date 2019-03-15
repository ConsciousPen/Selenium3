package aaa.modules.regression.sales.home_ca.ho3.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ca.defaulttabs.UnderwritingAndApprovalTab;
import aaa.modules.regression.sales.template.functional.TestMortgageeNameAndLoanNumberAbstract;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;


public class TestLegalMortgageeNameLoanNumber extends TestMortgageeNameAndLoanNumberAbstract {


    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO3;
    }

    @Override
    protected MortgageesTab getMortgageesTab() {
        return new MortgageesTab();
    }

    @Override
    protected RadioGroup getUseLegalMortgageeRadioButton(){
        return getMortgageesTab().getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.USE_LEGAL_MORTGAGEE_FOR_EVIDENCE_OF_INSURANCE);
    }

    @Override
    protected TextBox getMortgageeLoanNumberTextBox() {
        return getMortgageesTab().getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.LOAN_NUMBER);
    }

    @Override
    protected TextBox getMortgageeClauseTextBox(){
        return getMortgageesTab().getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.MORTGAGEE_CLAUSE);
    }

    @Override
    protected void navigateToMortgageesTab(){
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
    }

    @Override
    protected void navigateToUnderwritingTab(){
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.UNDERWRITING_AND_APPROVAL.get());
    }

    @Override
    protected UnderwritingAndApprovalTab getUnderwritingTab() {
        return new UnderwritingAndApprovalTab();
    }

    @Override
    protected BindTab getBindTab() {
        return new BindTab();
    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Loan Number is mandatory on Mortgagee & Additional Interests Tab
     * 1. Create a Quote for HO3 and navigate till Mortgagee & Additional Interests Tab
     * 2. Select Option "Yes' for Mortgagee and Mortgagee Information section is enabled
     * 3. Make sure Loan Number is a mandatory field
     * 4. Validate an error is displayed when trying to navigate to next tab without providing Loan Number
     * 5. Tab out to Bind Tab and purchase the policy
     * 6. Hard stop rule error -'AAA_HO_CA7230928' is displayed
     * @details
     **/

        @StateList(states = Constants.States.CA)
        @Parameters({"state"})
        @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PAS-6214-Validate Loan Number being Mandatory")
        @TestInfo(component = ComponentConstant.Service.HOME_CA_HO3,  testCaseId = "PAS-6214, PAS-24699")
        public void pas24669_testLegalMortgageeNameNB(@Optional("CA") String state) {

            pas24669_testLegalMortgageeNameNB();

            }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Loan Number is mandatory on Mortgagee & Additional Interests @Endorsement
     * 1. Create a Policy for HO3
     * 2. Initiate an endorsement and navigate till Mortgagee & Additional Interests Tab
     * 3. Select Option "Yes' for Mortgagee and Mortgagee Information section is enabled
     * 4 .Make sure Loan Number is a mandatory field
     * 5. Validate an error is displayed when trying to navigate to next tab without providing Loan Number
     * 6. Tab out to Bind Tab and purchase the policy
     * 7. Hard stop rule error -'AAA_HO_CA7230928' is displayed
     * @details
     **/

        @Parameters({"state"})
        @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PAS-6214-Validate Loan Number being Mandatory")
        @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-6214")
        public void pas24699_testMortgageeClauseEndTx(@Optional("CA") String state) {

            pas24699_testMortgageeClauseEndTx();

        }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Loan Number is mandatory on Mortgagee & Additional Interests @Renewal
     * 1. Create a Policy for HO3
     * 2. Initiate an renewal and navigate till Mortgagee & Additional Interests Tab
     * 3. Select Option "Yes' for Mortgagee and Mortgagee Information section is enabled
     * 4 .Make sure Loan Number is a mandatory field
     * 5. Validate an error is displayed when trying to navigate to next tab without providing Loan Number
     * 6. Tab out to Bind Tab and purchase the policy
     * 7. Hard stop rule error -'AAA_HO_CA7230928' is displayed
     * @details
     **/

        @Parameters({"state"})
        @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "PAS-6214-Validate Loan Number being Mandatory")
        @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-6214")
        public void pas24699_testMortgageeClauseRenewal(@Optional("CA") String state) {

            pas24699_testMortgageeClauseRenewal();
        }

}


