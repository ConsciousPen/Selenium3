package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.DialogsMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ca.defaulttabs.MortgageesTab;
import aaa.modules.policy.PolicyBaseTest;
import org.assertj.core.api.Assertions;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestMortgageeNameAndLoanNumberCATemplate extends PolicyBaseTest {

    MortgageesTab mortgageesTab = new MortgageesTab();
    private ErrorTab errorTab = new ErrorTab();

    private String pas24699MortgageeName = "CSAA Insurance LLC\n"+
                                           "5353 W Bell Rd\n"+
                                           "Glendale AZ 85038";

    protected void pas6214_testMortgageeLoanNumberNB() {

        createQuoteAndFillUpTo(getPolicyTD(), MortgageesTab.class, false);
        validateLoanNumberMessage();
        validateMortgageeClause();

    }
    protected void pas6214_testMortgageeLoanNumberEndTx() {

        openAppAndCreatePolicy(getPolicyTD());
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
        validateLoanNumberMessage();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        new BindTab().submitTab();
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_CA7230928);

    }
    protected void pas6214_testMortgageeLoanNumberRenewal(){

        openAppAndCreatePolicy(getPolicyTD());
        policy.renew().perform();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.MORTGAGEE_AND_ADDITIONAL_INTERESTS.get());
        validateLoanNumberMessage();

    }
    private void validateLoanNumberMessage(){

        mortgageesTab.getAssetList().getAsset(HomeCaMetaData.MortgageesTab.MORTGAGEE).setValue("Yes");
        mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.NAME).setValue("MortgageeCompany");
        mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.ZIP_CODE)
                .setValue(getCustomerIndividualTD("DataGather", "GeneralTab_" + getState()).getValue("Zip Code"));
        mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.STREET_ADDRESS_1)
                .setValue(getCustomerIndividualTD("DataGather", "GeneralTab_" + getState()).getValue("Address Line 1"));
        mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.VALIDATE_ADDRESS_BTN).click();
        mortgageesTab.getValidateAddressDialogAssetList().getAsset(DialogsMetaData.AddressValidationMetaData.BTN_OK).click();
        mortgageesTab.submitTab();
        assertThat(PropertyQuoteTab.mortgageeLoanNumberErrorMsg.getValue()).contains("'Loan number' is required");
        mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.LOAN_NUMBER).setValue("A123456");

    }
    private void validateMortgageeClause(){

        mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.USE_LEGAL_MORTGAGEE_FOR_EVIDENCE_OF_INSURANCE).setValue("Yes");
        String mortgageeHelpText = "Use the Mortgagee Clause field to override the “Lender Name” entered above. Information entered here will appear on the Evidence of Insurance and the Property Insurance Invoice. Please fill in with all required text by the lender in the format of how the clause should appear.";
        Assertions.assertThat(MortgageesTab.mortgageeClauseHelpText.getAttribute("title")).contains(mortgageeHelpText);
        mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.MORTGAGEE_CLAUSE).setValue(pas24699MortgageeName);
        String mortgageeClauseValue = mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.MORTGAGEE_CLAUSE).getValue();
        assertThat(mortgageeClauseValue).isEqualTo(pas24699MortgageeName);

    }

}
