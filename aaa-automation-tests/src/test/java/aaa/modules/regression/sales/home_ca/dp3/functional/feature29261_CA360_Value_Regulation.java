package aaa.modules.regression.sales.home_ca.dp3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.products.HomeSSConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ProductOfferingTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.QuoteSummaryPage;
import aaa.modules.policy.HomeCaDP3BaseTest;
import jdk.net.SocketFlow;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class feature29261_CA360_Value_Regulation extends HomeCaDP3BaseTest {
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL })
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-1916")

    public void generateISO360InFastLane_DP3() {

        TestData td = getPolicyTD("DataGather", "TestData_CA");
        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        policy.getDefaultView().fillUpTo(td, ReportsTab.class);
        policy.getDefaultView().getTab(ReportsTab.class)
                .tblISO360Report.getRow(1)
                    .getCell(HomeSSConstants.ISO360reportTable.STATUS)
                        .verify.value("Not started");
/*        mainApp().open();

        createCustomerIndividual();

        CustomerSummaryPage.buttonAddQuote.click();
        QuoteSummaryPage.comboBoxProduct.setValue(getPolicyType().getName());
        QuoteSummaryPage.buttonAddNewQuote.verify.enabled();
        QuoteSummaryPage.buttonAddNewQuote.click();
        policy.getDefaultView().getTab(GeneralTab.class).getPolicyInfoAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.POLICY_TYPE).setValue("HO3");
        policy.getDefaultView().getTab(GeneralTab.class).getAssetList().verify.enabled();
        policy.getDefaultView().getTab(GeneralTab.class)
              .getPolicyInfoAssetList()
                    .getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.POLICY_TYPE)
                        .setValue("DP3");GeneralTab.buttonSaveAndExit.click();

        PolicySummaryPage.labelPolicyNumber.verify.present();

        log.info("Initiated Quote #" + PolicySummaryPage.labelPolicyNumber.getValue());

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.DATA_GATHERING);

        policy.getDefaultView().getTab(ApplicantTab.class).getDwellingAddressAssetList()
                .getAsset(HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS)
                    .getAsset(HomeCaMetaData.ApplicantTab.DwellingAddress.VALIDATE_ADDRESS_BTN)
                        .click();

        policy.getDefaultView().getTab(ReportsTab.class)
                .tblISO360Report.getRow(1)
                    .getCell(HomeSSConstants.ISO360reportTable.STATUS)
                        .verify.value("Not started");
        //mainApp().close();*/
    }
}
