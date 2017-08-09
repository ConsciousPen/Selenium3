package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.composite.assets.AssetList;
import aaa.common.enums.ErrorPageEnum;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.ErrorPage;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;

public class TestQuoteValidateRules extends HomeCaHO3BaseTest {

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

    @Test
    @TestInfo(component = "Policy.HomeCA")
    public void testQuoteFuturedated() {
        GeneralTab generalTab = new GeneralTab();

        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        generalTab.fillTab(getPolicyTD());
        generalTab.getAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.class.getSimpleName(), AssetList.class).getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE)
                .setValue(DateTimeUtils.getCurrentDateTime().plusDays(91).format(DateTimeUtils.MM_DD_YYYY));
        //        generalTab.verifyFieldHasMessage(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel(), "Policy effective date cannot be more than 90 days from today's date.");
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
    @Test
    @TestInfo(component = "Policy.HomeCA")
    public void testQuoteBackdated() {
        GeneralTab generalTab = new GeneralTab();

        mainApp().open();
        createCustomerIndividual();

        policy.initiate();
        generalTab.fillTab(getPolicyTD());
        generalTab.getAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.class.getSimpleName(), AssetList.class).getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE)
                .setValue(DateTimeUtils.getCurrentDateTime().minusDays(10).format(DateTimeUtils.MM_DD_YYYY));
        //TODO Change if bug will be fixed
        //generalTab.verifyFieldHasMessage(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel(), "Policy effective date cannot be backdated more than three days from today's");
        //generalTab.getAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.class.getSimpleName(), AssetList.class).getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE)
        //.setValue(DateTimeUtils.getCurrentDateTime().minusDays(3).format(DateTimeUtils.MM_DD_YYYY));
        //generalTab.submitTab();
        //policy.getDefaultView().fillFromTo(getPolicyTD(), ApplicantTab.class, PurchaseTab.class, true);
        //new PurchaseTab().submitTab();

        generalTab.submitTab();
        policy.getDefaultView().fillFromTo(getPolicyTD(), ApplicantTab.class, BindTab.class);
        new BindTab().btnPurchase.click();

        ErrorPage.tableError.getRow(1).getCell(ErrorPageEnum.ErrorsColumn.MESSAGE.get()).verify.contains("Policy effective date cannot be backdated more than three days from today's d...");
        ErrorPage.buttonCancel.click();

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
