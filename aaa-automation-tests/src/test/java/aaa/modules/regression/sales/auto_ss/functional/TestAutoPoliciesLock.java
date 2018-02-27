package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.jetty.util.ConcurrentHashSet;
import org.testng.annotations.*;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.TestAutoPolicyLockPreConditions;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestAutoPoliciesLock extends AutoSSBaseTest implements TestAutoPolicyLockPreConditions {

    private static final LocalDateTime getDate = TimeSetterUtil.getInstance().getCurrentTime();
    private static final String currentDate = getDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
    private static final String lookUpId = "(SELECT ll.id FROM lookupList ll WHERE ll.lookupName LIKE '%AAAFactorsLockLookup')";
    private static final String toDate = "to_date('%s', 'YYYY-MM-DD')";
    private static Set<String> elementNames = new ConcurrentHashSet<>();
    private static final String tomorrowDate = getDate.plusDays(1).format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));

    /**
     * @author Lev Kazarnovskiy
     * <p>
     * PAS-2247, PAS-2248 - Lock Membership and Auto Ins Persistency, Not at Fault and Comp Claims
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
    public void pas2247_pas2248_AipAndNafLock(@Optional("CT") String state) {

        TestData testData = getAdjustedTD().adjust(getTestSpecificTD("OverrideErrors").resolveLinks());

        List<String> testElements = Arrays.asList("numberNAFAccident", "autoInsurancePersistency");
        //Add locked values to the global variable to clean them up then
        elementNames.addAll(testElements);

        //Set the lock for values DB
        setLockForTheElement(testElements, currentDate);

        createQuoteAndOpenVRD(testData);

        //Save the values of listed items to compare them with values on Renewal Later
        String previousCCValue = getComprehensiveClaimsValue();
        String previousNAFValue = getNafAccidentsValue();
        String previousAIPValue = getAaaInsurancePersistencyValue();

        //Close rating details pop-up, issue the policy, initiate renewal and verify items values in VRD
        fillAllInfoAndBind(testData);
        policy.renew().start();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
        new DriverTab().fillTab(testData);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();

        //Verify that values of NAF and AIP are locked and not changed in VRD. Verify that CC values is increased (was not locked)
        assertSoftly(softly -> {
            softly.assertThat(getNafAccidentsValue()).isEqualTo(previousNAFValue);
            softly.assertThat(getAaaInsurancePersistencyValue()).isEqualTo(previousAIPValue);
            softly.assertThat(getComprehensiveClaimsValue()).isNotEqualTo(previousCCValue);
        });

        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
    }

    /**
     * @author Chris Johns
     * <p>
     * PAS-4311, PAS-6587 - Locking Advanced Shopping Discount - Continue Lock
     * @name Locking Advanced Shopping Discount Tier
     * @scenario 1. Verify that tier lock will be applied for renewal if lock effective date = policy effective date
     * 1. Configure lock for ASD TIER. Lock effective date = policy effective date
     * 2. Initiate Auto SS quote creation
     * 3. Note the values for ASD TIER
     * 4. Initiate Renewal for policy
     * 5. Verify that ASD values are locked (does not incremented)
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6587")
    public void pas4311_pas6587_ASDLock() {
        TestData testData = getPolicyTD();

        //Add locked values to the global variable to clean them up then
        List<String> testElements = Arrays.asList("asdTierFactor");
        elementNames.addAll(testElements);
        //Set the lock for values DB
        setLockForTheElement(testElements, currentDate);

        //Initiate new policy and fill up to the View Rating Details screen of the P&C Page
        createQuoteAndOpenVRD(testData);

        //Save the ASD Tier Value to compare it with values on Renewal
        String previousASDTierValue = getAdvanceShoppingDiscountValue();

        //Issue the policy overriding all errors
        fillAllInfoAndBind(testData);

        //Initiate endorsement
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        //Override the insurance score to 850; this would cause the ASD to change; if not locked
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
        new RatingDetailReportsTab().fillTab(getTestSpecificTD("RatingDetailReportsTab_ASD"));

        //Bind the endorsement
        PremiumAndCoveragesTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();

        //Initiate Renewal Entry
        policy.renew().start();

        //Navigate to the View Rating Details screen of the P&C Page
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();

        //Verify that values of ASD tier are locked and not changed in VRD
        String renewalValue = getAdvanceShoppingDiscountValue();

        assertThat(renewalValue).isEqualTo(previousASDTierValue);
        log.info("SUCCESS: ASD Tier was locked!");

        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
    }

    /**
     * @author Chris Johns
     * <p>
     * PAS-4311, PAS-6587 - Locking Advanced Shopping Discount --Newly Locked
     * @name Locking Advanced Shopping Discount Tier
     * @scenario 2. Verify that lock will NOT be applied for renewal if lock effective date > policy effective date
     * 1. Configure lock for ASD TIER. Lock effective date > policy effective date
     * 2. Initiate Auto SS quote creation
     * 3. Note the values for ASD TIER
     * 4. Initiate Renewal for policy
     * 5. Verify that ASD values are NOT Locked
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-4311")
    public void pas4311_pas6587_ASDLock_newly_locked() {
        TestData testData = getPolicyTD();

        //Add locked values to the global variable to clean them up then
        List<String> testElements = Arrays.asList("asdTierFactor");
        elementNames.addAll(testElements);
        //Set the lock for values DB
        setLockForTheElement(testElements, tomorrowDate);

        //Initiate new policy and fill up to the View Rating Details screen of the P&C Page
        createQuoteAndOpenVRD(testData);

        //Save the ASD Tier Value to compare it with values on Renewal
        String previousASDTierValue = getAdvanceShoppingDiscountValue();

        //Issue the policy
        fillAllInfoAndBind(testData);

        //Initiate endorsement
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        //Override the insurance score to 850; this would cause the ASD to change; if not locked
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
        new RatingDetailReportsTab().fillTab(getTestSpecificTD("RatingDetailReportsTab_ASD"));

        //Bind the endorsement
        PremiumAndCoveragesTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        new DocumentsAndBindTab().submitTab();

        //Initiate Renewal Entry
        policy.renew().start();

        //Navigate to the View Rating Details screen of the P&C Page
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.calculatePremium();
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();

        //Verify that values of ASD tier are locked and not changed in VRD
        String renewalValue = getAdvanceShoppingDiscountValue();

        assertThat(renewalValue).isNotEqualTo(previousASDTierValue);
        log.info("SUCCESS: ASD Tier was NOT locked!");

        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
    }

    @AfterMethod(alwaysRun = true)
    private void cleanDB() {
        //Restore lock parameters in DB to default values
        deleteLockForTheElement();
    }

    private TestData getAdjustedTD() {
        String driverTabSimpleName = new DriverTab().getMetaKey();
        String generalTabSimpleName = new GeneralTab().getMetaKey();
        String namedInsuredInformationSection = AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel();

        //Adjust data for DriverTab.
        List<TestData> driverTabAdjustment = new ArrayList<>();
        driverTabAdjustment.add(getPolicyTD().getTestData(driverTabSimpleName)
                .adjust(getTestSpecificTD("TestData").resolveLinks()));

        //Adjust data for Base Date field on General Tab
        List<TestData> baseDateAdjustment = new ArrayList<>();
        baseDateAdjustment.add(getPolicyTD().getTestData(generalTabSimpleName).getTestDataList(namedInsuredInformationSection).get(0)
                .adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel(), getDate.minusYears(1).format(DateTimeFormatter.ofPattern("MM/dd/YYYY")))
                .adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel(), "Derek")
                .adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel(), "Martin"));

        return getPolicyTD().adjust(TestData.makeKeyPath(generalTabSimpleName, namedInsuredInformationSection), baseDateAdjustment)
                .adjust(driverTabSimpleName, driverTabAdjustment);
    }

    private void fillAllInfoAndBind(TestData testData) {
        //Close rating details pop-up
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        new PremiumAndCoveragesTab().submitTab();

        policy.getDefaultView().fillFromTo(testData, DriverActivityReportsTab.class, PurchaseTab.class, true);
        new PurchaseTab().submitTab();
    }

    private void setLockForTheElement(Iterable<String> testElements, String lockEffective) {
        testElements.forEach(e -> {
            int a = DBService.get().executeUpdate(String.format(INSERT_QUERY, lookUpId, e, String.format(toDate, lockEffective), getState()));
            //Check that query was successful
            assertThat(a).isGreaterThan(0);
        });
    }

    private void deleteLockForTheElement() {
        elementNames.forEach(e ->
                DBService.get().executeUpdate(String.format(DELETE_QUERY, lookUpId, e, String.format(toDate, currentDate), String.format(toDate, tomorrowDate), getState())));
    }

    private void createQuoteAndOpenVRD(TestData testData) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, PremiumAndCoveragesTab.class, true);
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
    }

    private String getComprehensiveClaimsValue() {
        return PremiumAndCoveragesTab.tableRatingDetailsUnderwriting
                .getRow(4, "Number of Comprehensive Claims").getCell(5).getValue();
    }

    private String getNafAccidentsValue() {
        return PremiumAndCoveragesTab.tableRatingDetailsUnderwriting
                .getRow(4, "Number of Not-At-Fault Accidents").getCell(5).getValue();
    }

    private String getAaaInsurancePersistencyValue() {
        return PremiumAndCoveragesTab.tableRatingDetailsUnderwriting
                .getRow(1, "AAA Insurance Persistency").getCell("Value").getValue();
    }

    private String getAdvanceShoppingDiscountValue() {
        return PremiumAndCoveragesTab.tableRatingDetailsUnderwriting
                .getRowContains("4", "Advance Shopping Discount").getCell(5).getValue();
    }
}

