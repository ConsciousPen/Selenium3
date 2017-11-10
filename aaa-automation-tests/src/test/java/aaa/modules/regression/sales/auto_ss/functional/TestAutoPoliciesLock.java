package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TestAutoPoliciesLock extends AutoSSBaseTest {

    String currentDate= TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ISO_DATE);

    /**
     * @author Lev Kazarnovskiy
     *
     * PAS-2247, PAS-2248 - Lock Membership and Auto Ins Persistency, Not at Fault and Comp Claims
     *
     * @name Test VINupload 'Update VIN' scenario.
     * @scenario 0. Create customer
     * 1. Configure lock for AIP and NAF  in DB
     * 2. Initiate Auto SS quote creation
     * 3. Note the values for CC, NAF and AIP in VRD on Premium&Coverages screen
     * 4. Initiate Renewal for quote
     * 5. Verify that NAF and AIP values are locked (does not incremented) and CC value is increased
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2247")
    public void pas2247_pas2248_AipAndNafLock(@Optional("CT") String state){

        String driverTabSimpleName = new DriverTab().getMetaKey();
        String generalTabSimpleName = new GeneralTab().getMetaKey();
        String namedInsuredInformationSection = AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel();

        //Adjust data for DriverTab.
        List<TestData> driverTabAdjustment = new ArrayList<>();
        driverTabAdjustment.add(getPolicyTD().getTestData(driverTabSimpleName)
                .adjust(getTestSpecificTD("TestData").resolveLinks()));

        //Adjust data for Base Date field on General Tab
        List<TestData> baseDateAdjustment = new ArrayList<>();
        baseDateAdjustment.add(getPolicyTD().getTestData(generalTabSimpleName).getTestDataList(namedInsuredInformationSection).
                get(0).adjust("Base Date", "/today-1y").adjust("First Name", "Derek").adjust("Last Name", "Martin"));

        TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(generalTabSimpleName, namedInsuredInformationSection), baseDateAdjustment)
                .adjust(driverTabSimpleName, driverTabAdjustment);

        setLockForTheElement("numberNAFAccident");
        setLockForTheElement("autoInsurancePersistency");

        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, true);
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();

        //Save the values of listed items to compare them with values on Renewal Later
        String previousCCValue = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting
                .getRow(4, "Number of Comprehensive Claims").getCell(5).getValue();

        String previousNAFValue = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting
                .getRow(4, "Number of Not-At-Fault Accidents").getCell(5).getValue();

        String previousAIPValue = PremiumAndCoveragesTab.tableRatingDetailsUnderwriting
                .getRow(1, "AAA Insurance Persistency").getCell("Value").getValue();

        //Close rating details pop-up, issue the policy, initiate renewal and verify items values in VRD
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        new PremiumAndCoveragesTab().submitTab();

        overrideErrorsAndBind(testData);
        policy.renew().start();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        new DriverTab().fillTab(testData);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();

        //Verify that values of NAF and AIP are locked and not changed in VRD. Verify that CC values is increased
        CustomAssert.enableSoftMode();
        CustomAssert.assertFalse("Number of Comprehensive Claims is locked on Renewal, but shouldn't",
                previousCCValue.equals(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Number of Comprehensive Claims").getCell(5).getValue()));
        CustomAssert.assertTrue("Number of Not-At-Fault Accidents is not locked on Renewal",
                previousNAFValue.equals(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(4, "Number of Not-At-Fault Accidents").getCell(5).getValue()));
        CustomAssert.assertTrue("Value for AAA Insurance Persistency is not locked on Renewal",
                previousAIPValue.equals(PremiumAndCoveragesTab.tableRatingDetailsUnderwriting.getRow(1, "AAA Insurance Persistency").getCell("Value").getValue()));
        CustomAssert.disableSoftMode();

        CustomAssert.assertAll();

        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
    }

    @AfterMethod(alwaysRun = true)
    private void cleanDB(){
        //Restore lock parameters in DB to default values
        deleteLockForTheElement("numberNAFAccident");
        deleteLockForTheElement("autoInsurancePersistency");
    }

    private void overrideErrorsAndBind (TestData testData) {
        DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
        PurchaseTab purchaseTab = new PurchaseTab();

        policy.getDefaultView().fillFromTo(testData, DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
        documentsAndBindTab.submitTab();
        new ErrorTab().overrideAllErrors();
        documentsAndBindTab.submitTab();
        purchaseTab.fillTab(testData);
        purchaseTab.submitTab();
    }

    private void setLockForTheElement (String elementName){
        DBService.get().executeUpdate("INSERT INTO lookupValue " +
                "(lookUpList_id,dType,code,displayValue,effective,expiration,productCD,riskStateCD) "+
                "VALUES ((SELECT ll.id FROM lookupList ll WHERE ll.lookupName LIKE '%AAAFactorsLockLookup'),"+
                "'BaseProductLookupValue','" + elementName + "','TRUE',to_date('" + currentDate + "', 'YYYY-MM-DD'),null,'AAA_SS','"+ getState() +"')");
    }

    private void deleteLockForTheElement (String elementName) {
        DBService.get().executeUpdate("DELETE FROM lookupValue lv " +
                "WHERE lv.lookupList_id IN (SELECT ll.id FROM lookupList ll WHERE ll.lookupName LIKE '%AAAFactorsLockLookup') " +
                "AND CODE = '" + elementName + "' AND DISPLAYVALUE='TRUE' AND EFFECTIVE=to_date('" + currentDate + "', 'YYYY-MM-DD') " +
                "AND RISKSTATECD='"+ getState() +"'");
    }
}