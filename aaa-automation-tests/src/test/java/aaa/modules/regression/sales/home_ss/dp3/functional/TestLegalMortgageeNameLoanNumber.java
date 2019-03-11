package aaa.modules.regression.sales.home_ss.dp3.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.UnderwritingAndApprovalTab;
import aaa.modules.regression.sales.template.functional.TestMortgageeNameAndLoanNumberAbstract;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;

@StateList(statesExcept = {Constants.States.CA})
public class TestLegalMortgageeNameLoanNumber extends TestMortgageeNameAndLoanNumberAbstract {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_DP3;
    }

    @Override
    protected MortgageesTab getMortgageesTab() {
        return new MortgageesTab();
    }

    @Override
    protected RadioGroup getUseLegalMortgageeRadioButton(){
        return getMortgageesTab().getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.USE_LEGAL_MORTGAGEE_FOR_EVIDENCE_OF_INSURANCE);
    }

    @Override
    protected TextBox getMortgageeLoanNumberTextBox() {
        return getMortgageesTab().getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.LOAN_NUMBER);
    }

    @Override
    protected TextBox getMortgageeClauseTextBox(){
        return getMortgageesTab().getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.MORTGAGEE_CLAUSE);
    }

    @Override
    protected void navigateToMortgageesTab(){
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
    }

    @Override
    protected void navigateToUnderwritingTab(){
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.UNDERWRITING_AND_APPROVAL.get());
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
     * @name Mortgagee & Additional Interests Tab - Validate New Mortgagee Clause filed and Text box value
     * 1. Create a Quote for HO3 and navigate till Mortgagee & Additional Interests Tab
     * 2. Select Option 'Yes' for Mortgagee and Mortgagee Information section is enabled
     * 3. Select Option 'Yes' for Use Legal Mortgagee Name and Mortgagee Clause field is enabled with the text box
     * 4. Validate the Mortgagee Clause Name with the value Passed in String
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Validate New Mortgagee Clause Text box")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-24699")
    public void pas24669_testLegalMortgageeNameNB(@Optional("") String state) {

        pas24669_testLegalMortgageeNameNB();

    }

    /**
     * @author Sreekanth Kopparapu
     * @name Mortgagee & Additional Interests Tab - Validate New Mortgagee Clause filed and Text box value
     * 1. Create a Policy for HO3 without adding any Legal Mortgagee Name - Mortgagee Clause on Mortgagee & Additional Interest tab
     * 2. Initiate an endorsement and navigate till Mortgagee & Additional Interests Tab
     * 3. Select Option 'Yes' for Mortgagee and Mortgagee Information section is enabled
     * 4. Select Option 'Yes' for Use Legal Mortgagee Name and Mortgagee Clause field is enabled with the text box
     * 5. Validate the Mortgagee Clause Name with the value Passed in String
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Validate New Mortgagee Clause Text box")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-24699")
    public void pas24699_testMortgageeClauseEndTx(@Optional("") String state) {

        pas24699_testMortgageeClauseEndTx();

    }

    /**
     * @author Sreekanth Kopparapu
     * @name Mortgagee & Additional Interests Tab - Validate New Mortgagee Clause filed and Text box value
     * 1. Create a Policy for HO3 without adding any Legal Mortgagee Name - Mortgagee Clause on Mortgagee & Additional Interest tab
     * 2. Initiate an renewal and navigate till Mortgagee & Additional Interests Tab
     * 3. Select Option 'Yes' for Mortgagee and Mortgagee Information section is enabled
     * 4. Select Option 'Yes' for Use Legal Mortgagee Name and Mortgagee Clause field is enabled with the text box
     * 5. Validate the Mortgagee Clause Name with the value Passed in String
     * 6. Navigate till Bind page and 'Save & Exit'
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Validate New Mortgagee Clause Text box")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-24699")
    public void pas24699_testMortgageeClauseSSRenewal(@Optional("") String state) {

        pas24699_testMortgageeClauseRenewal();

    }

}
