package aaa.modules.regression.sales.template.functional;

import aaa.common.Tab;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab;
import aaa.modules.policy.PolicyBaseTest;
import org.assertj.core.api.Assertions;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public abstract class TestMortgageeNameAndLoanNumberAbstract extends PolicyBaseTest {

    protected abstract Tab getMortgageesTab();
    protected abstract RadioGroup getUseLegalMortgageeRadioButton();
    protected abstract TextBox getMortgageeLoanNumberTextBox();
    protected abstract TextBox getMortgageeClauseTextBox();
    protected abstract void navigateToMortgageesTab();
    protected abstract void navigateToUnderwritingTab();
    protected abstract Tab getUnderwritingTab();
    protected abstract Tab getBindTab();
    ErrorTab errorTab = new ErrorTab();


    protected void pas24669_testLegalMortgageeNameNB(){

        createQuoteAndFillUpTo(getPolicyTD(), getMortgageesTab().getClass(), false);
        validateMortgageeClause();
        policy.getDefaultView().fillFromTo(getPolicyTD(), getUnderwritingTab().getClass(), getBindTab().getClass());
        getBindTab().submitTab();
        if (isStateCA()) {
            errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_AAA_HO_CA723000018, ErrorEnum.Errors.ERROR_AAA_HO_CA7230928);
        }
        else {
            errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS12141500);
        }
    }

    protected void pas24699_testMortgageeClauseEndTx() {

        openAppAndCreatePolicy(getPolicyTD());
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        navigateToMortgageesTab();
        validateMortgageeClause();

    }
    protected void pas24699_testMortgageeClauseRenewal(){

        openAppAndCreatePolicy(getPolicyTD());
        policy.renew().perform();
        navigateToMortgageesTab();
        validateMortgageeClause();

    }

    private void validateMortgageeClause(){

        String pas24699MortgageeName =  "CSAA Insurance LLC\n"+
                                        "5353 W Bell Rd\n"+
                                        "Glendale AZ 85038";

        TestData tdMortgageeTab = testDataManager.getDefault(TestMortgageeNameAndLoanNumberAbstract.class).getTestData("TestData");
        getMortgageesTab().fillTab(tdMortgageeTab).submitTab();
        assertThat(PropertyQuoteTab.mortgageeLoanNumberErrorMsg.getValue()).contains("'Loan number' is required");
        getMortgageeLoanNumberTextBox().setValue("A123456");
        getUseLegalMortgageeRadioButton().setValue("Yes");
        String mortgageeHelpText = "Use the Mortgagee Clause field to override the “Lender Name” entered above. Information entered here will appear on the Evidence of Insurance and the Property Insurance Invoice. Please fill in with all required text by the lender in the format of how the clause should appear.";
        Assertions.assertThat(PropertyQuoteTab.mortgageeClauseHelpText.getAttribute("title")).contains(mortgageeHelpText);
        getMortgageeClauseTextBox().setValue(pas24699MortgageeName);
        String mortgageeClauseValue = getMortgageeClauseTextBox().getValue();
        assertThat(mortgageeClauseValue).isEqualTo(pas24699MortgageeName);
        getMortgageeClauseTextBox().setValue("");
        Assertions.assertThat(PropertyQuoteTab.mortgageeClauseErrorMsg.getValue()).contains("'Mortgagee Clause' is required");
        getMortgageeLoanNumberTextBox().setValue("");
        navigateToUnderwritingTab();
    }

}
