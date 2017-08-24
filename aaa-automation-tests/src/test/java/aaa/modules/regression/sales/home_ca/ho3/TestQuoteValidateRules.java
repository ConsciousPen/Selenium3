package aaa.modules.regression.sales.home_ca.ho3;

import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.composite.assets.AssetList;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;

@Test(groups = {Groups.REGRESSION, Groups.HIGH})
public class TestQuoteValidateRules extends HomeCaHO3BaseTest {

    GeneralTab generalTab = new GeneralTab();

    /**
      * @author Jurij Kuznecov
      * @name Test CAH Quote Futuredated
      * @scenario 
      * 1. Create a customer or open existed
      * 2. Initiate new CAH quote creation
      * 3. On General tab set Effective Date as Today + 91 days and verify error message is displaying under Effective Date field
      * 4. Set Effective Date as Today + 10 days
      * 5. Fill all mandatory fields on all tabs, order reports, calculate premium 
      * 6. Bind a policy
      * 7. Verify policy status is Policy Pending  
      */

    @Test(enabled = true)
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void testQuoteFuturedated() {
        String expectedWarning = "Policy effective date cannot be more than 90 days from today's date.";

        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        generalTab.fillTab(getPolicyTD());
        generalTab.getAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.class.getSimpleName(), AssetList.class).getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE)
                .setValue(DateTimeUtils.getCurrentDateTime().plusDays(91).format(DateTimeUtils.MM_DD_YYYY));
        generalTab.getAssetList().getAsset(HomeCaMetaData.GeneralTab.POLICY_INFO).getWarning(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel()).verify.contains(expectedWarning);
        generalTab.getAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.class.getSimpleName(), AssetList.class).getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE)
                .setValue(DateTimeUtils.getCurrentDateTime().plusDays(10).format(DateTimeUtils.MM_DD_YYYY));
        generalTab.submitTab();

        policy.getDefaultView().fillFromTo(getPolicyTD(), ApplicantTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_PENDING);
    }

    /**
    * @author Jurij Kuznecov
    * @name Test CAH Quote Backdated
    * @scenario
    * 1. Create a customer or open existed
    * 2. Initiate new CAH quote creation
    * 3. On General tab set Effective Date as Today - 10 days 
    * 4. Fill all mandatory fields on all tabs, order reports, calculate premium 
    * 5. Bind a policy and verify Error tab is displaying with error message
    * 6. Navigate to General tab, change Effective Date to Today - 3 days
    * 7. Recalculate premium and bind policy
    * 8. Verify policy status is Active
    */

    @Test(enabled = true)
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void testQuoteBackdated() {

        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        generalTab.fillTab(getPolicyTD());
        generalTab.fillTab(getTestSpecificTD("TestData_Minus10Days"));
        //TODO Change if bug will be fixed
        //generalTab.getAssetList().getAsset(HomeCaMetaData.GeneralTab.POLICY_INFO).getWarning(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel()).verify.contains(expectedWarning);
        //generalTab.getAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.class.getSimpleName(), AssetList.class).getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE)
        //.setValue(DateTimeUtils.getCurrentDateTime().minusDays(3).format(DateTimeUtils.MM_DD_YYYY));
        //generalTab.submitTab();
        //policy.getDefaultView().fillFromTo(getPolicyTD(), ApplicantTab.class, PurchaseTab.class, true);
        //new PurchaseTab().submitTab();

        generalTab.submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), ApplicantTab.class, BindTab.class);
        new BindTab().btnPurchase.click();

        ErrorTab errorTab = new ErrorTab();
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_CA3230672);
        errorTab.cancel();

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.GENERAL.get());
        generalTab.getAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.class.getSimpleName(), AssetList.class).getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE)
                .setValue(DateTimeUtils.getCurrentDateTime().minusDays(3).format(DateTimeUtils.MM_DD_YYYY));
        generalTab.submitTab();

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();

        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
        policy.getDefaultView().fillFromTo(getPolicyTD(), BindTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
