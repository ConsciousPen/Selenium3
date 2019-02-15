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
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestMortgageeNameAndLoanNumberCATemplate extends PolicyBaseTest {

    MortgageesTab mortgageesTab = new MortgageesTab();
    private ErrorTab errorTab = new ErrorTab();

    protected void pas6214_testMortgageeLoanNumberNB() {

        createQuoteAndFillUpTo(getPolicyTD(), MortgageesTab.class, false);
        validateLoanNumberMessage();
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
    }
}
