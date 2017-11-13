package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.AssetList;

public class TestMembershipValidationErrorTemplate extends AutoSSBaseTest {
    private GeneralTab generalTab = new GeneralTab();
    private ErrorTab errorTab = new ErrorTab();
    private AssetList namedInsuredInfo = generalTab.getCurrentCarrierInfoAssetList();
    private final String quoteEffectiveDate = "01/01/2018";
    private String insertLookup =
            "insert into Lookupvalue (LOOKUPLIST_ID,DTYPE,CODE,DISPLAYVALUE,EFFECTIVE,EXPIRATION,PRODUCTCD,RISKSTATECD) " +
                    "values ((select Ll.Id From Lookuplist Ll Where Ll.Lookupname like '%AAAFactorsLockLookup'),'AAAFactorsLockLookupValue','compClaims','TRUE',to_date('02-02-2016', 'MM-DD-YYYY'),null,'AAA_SS','CT')";
    private String deleteLookup = "delete from Lookupvalue Lv where Lv.Lookuplist_Id " +
            "In (Select Ll.Id From Lookuplist Ll Where Ll.Lookupname like '%AAAFactorsLockLookup') AND CODE = 'compClaims' " +
            "AND DISPLAYVALUE='TRUE' and EFFECTIVE=to_date('02-02-2016', 'MM-DD-YYYY') and RISKSTATECD='CT'";
    // %1$s
    private String selectMembershipNumber = "SELECT DISTINCT ORDERMEMBERSHIPNUMBER,MEMBERSHIPSTATUS FROM MEMBERSHIPSUMMARYENTITY WHERE (MEMBERSHIPSTATUS != 'Active' OR MEMBERSHIPSTATUS IS NULL) AND ORDERMEMBERSHIPNUMBER IS NOT NULL";

	/*@BeforeMethod(alwaysRun = true)
    protected void insertAAAMembershipConfigurationLookupValue(){
		try {
            DBService.get().executeUpdate(String.format(insertLookup, ""));
        } catch (NoSuchElementException e){
			log.error("Unable to ");
		}
	}*/

    public void verifyMembershipValidationError() {
        TestData testDataGeneralTabAAAProductsOwned = getAdjustedTestData().getTestData("GeneralTab").ksam("CurrentCarrierInformation").resolveLinks();

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillFromTo(getPolicyTD(), PrefillTab.class, PremiumAndCoveragesTab.class, true);

        CustomAssert.enableSoftMode();

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

   /* @AfterMethod(alwaysRun = true)
    protected void deleteAAAMembershipConfigurationLookupValue(){   
        try {
            DBService.get().executeUpdate(String.format(deleteLookup, ""));
        } catch (NoSuchElementException e){
            log.error("Unable to ");
        }
    }*/

    /**
     * Prepare testdata which will trigger error appearence
     * AND the policy effective date is on or after 01/01/2018
     * invalid membership number
     */
    private TestData getAdjustedTestData() {
        TestData defaultTestData = getPolicyTD();
        TestData policyInformation = defaultTestData.getTestData("GeneralTab").getTestData("PolicyInformation")
                .adjust("Effective Date", quoteEffectiveDate);
        TestData aaaProductsOwned = defaultTestData.getTestData("GeneralTab").getTestData("AAAProductOwned")
                .adjust("Membership Number", "")
                .adjust("Last name", "");
        TestData generalTabAdjusted = defaultTestData.getTestData("GeneralTab")
                .adjust("AAAProductOwned", aaaProductsOwned)
                .adjust("PolicyInformation", policyInformation);

        return defaultTestData.adjust("GeneralTab", generalTabAdjusted);
    }
}
