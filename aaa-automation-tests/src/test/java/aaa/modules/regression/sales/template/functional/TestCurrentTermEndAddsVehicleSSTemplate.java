package aaa.modules.regression.sales.template.functional;

import static aaa.main.pages.summary.PolicySummaryPage.TransactionHistory.provideLinkExpandComparisonTree;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

public class TestCurrentTermEndAddsVehicleSSTemplate extends CommonTemplateMethods {

    protected VehicleTab vehicleTab = new VehicleTab();
    protected DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

    protected String policyNumber;
    protected static final String VEHICLE1_VIN = "KNDJT2A2XA7038383";
    protected static final String VEHICLE2_VIN = "JT2AE91A7M3425407";
    protected static final String VEHICLE3_VIN = "1FTRE1421YHA89455";
    protected static final String VEHICLE4_VIN = "5NPEU46C991234567";
    protected static final String VEHICLE1_UPDATED_VIN = "2GTEC19V531282646";
    protected static final String SYMBOL_2000 = "SYMBOL_2000";
    protected static final String SYMBOL_2018 = "SYMBOL_2018";
    protected static final String SYMBOL_2000_CHOICE = "SYMBOL_2000_CHOICE";
    protected static final String SYMBOL_2018_CHOICE = "SYMBOL_2018_CHOICE";
    protected TestData testDataThreeVehicles;

    protected static final String NOT_MATCHED = "NOT_MATCHED";
    protected static final String MATCHED = "MATCHED";
    protected static final String STUB = "STUB";

    protected void pas14532_refreshForCurrentAndRenewalTerms_initiateEndorsement(String scenario) {
        UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
        String vinTableFile = "VinUploadOnCurrentTerm.xlsx";
        String controlTableFile = "controlTable_AZ_SS.xlsx";
        String vinTableFileUpdatedVersion = "VinUploadOnCurrentTermUpdatedVersion.xlsx";
        TestData testDataTwoVehicles = getTestDataWithTwoVehicles(getPolicyTD(), scenario);
        //1. Create auto SS policy with two vehicles and save the expiration date
        mainApp().open();
        createCustomerIndividual();
        policyNumber = createPolicy(testDataTwoVehicles);
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

        //2. control table file upload for changing the effective date and expiration date
        // vin upload to update second VIN To another VIN where VIN will be matched
        adminApp().open();
        uploadToVINTableTab.uploadFiles(controlTableFile, vinTableFile);
        LocalDateTime expirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(360);
        LocalDateTime effectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(361);
        updateControlTable(expirationDate, effectiveDate);

        //3. Change system date to R-35 and renew it
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));

        //Upload the Vin table file by changing valid flag for the same version for Vehicle 2
        if (scenario.equals(MATCHED)) { //scenario 2
            adminApp().open();
            uploadToVINTableTab.uploadVinTable(vinTableFileUpdatedVersion);
        }

        //4. Initiate endorsement
        initiateEndorsement();
    }

    public void pas14532_refreshForCurrentAndRenewalTerms_bindEndorsement(String scenario) {
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
        if (scenario.equals(NOT_MATCHED)) { //scenario 1
            testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD(), NOT_MATCHED);
        } else if (scenario.equals(MATCHED)) { //scenario 2
            testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD(), MATCHED);
        } else if (scenario.equals(STUB)) { //scenario 3
            testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD(), STUB);
        }

        //6. Calculate Premium and bind the endorsement
        policy.getDefaultView().fillFromTo(testDataThreeVehicles, VehicleTab.class, PremiumAndCoveragesTab.class, true);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();

        //Conflicts page
        Table tableDifferences = PolicySummaryPage.tableDifferences;
        int columnsCount = tableDifferences.getColumnsCount();

        Link linkTriangle = provideLinkExpandComparisonTree(Collections.singletonList(0));
        if (linkTriangle.isPresent() && linkTriangle.isVisible()) {
            linkTriangle.click();

            Link linkSetCurrent = tableDifferences.getRow(2).getCell(columnsCount).controls.links.get("Current");
            Link linkSetAvailable = tableDifferences.getRow(2).getCell(columnsCount).controls.links.get("Available");

            if (scenario.equals(NOT_MATCHED) || scenario.equals(STUB)) { //scenario 1 or scenario 3
                linkSetCurrent.click();
                policy.rollOn().submit();
            } else if (scenario.equals(MATCHED)) { //scenario 2
                linkSetAvailable.click();
                policy.rollOn().submit();
            }
        } else {
            log.info("Conflict page not found. Please enable renewal merge");
        }
    }

    private void updateControlTable(LocalDateTime expirationDate, LocalDateTime effectiveDate) {
        String formattedExpirationDate = expirationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedEffectiveDate = effectiveDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, formattedExpirationDate, getState(), "SYMBOL_2000"));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EFFECTIVEDATE_BY_STATECD_VERSION, formattedEffectiveDate, getState(), "SYMBOL_2018"));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, formattedExpirationDate, getState(), "SYMBOL_2000_CHOICE"));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EFFECTIVEDATE_BY_STATECD_VERSION, formattedEffectiveDate, getState(), "SYMBOL_2018_CHOICE"));
    }

    private void initiateEndorsement() {
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.createEndorsement(getPolicyTD("Endorsement", "TestData"));
    }

    //Update first vehicle with VIN matched
    protected TestData modifyVehicleTabWithExistingVin(TestData testData) {
        testData
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), VEHICLE1_UPDATED_VIN);
        return testData;
    }

    //Update first Vehicle to Vin no match
    protected TestData modifyVehicleTabNonExistingVin(TestData testData) {
        return testData.getTestData(vehicleTab.getMetaKey()).adjust(getTestSpecificTD("VehicleTab_NonExistingVIN")).resolveLinks();
    }

    //Update Y/M/M/S/S for second Vehicle
    protected TestData modifyVehicleTabVinStub(TestData testData) {
        return testData.getTestData(vehicleTab.getMetaKey()).adjust(getTestSpecificTD("VehicleTab_updateVINStub")).resolveLinks();
    }

    //adds two vehicles to the test data
    public TestData getTestDataWithTwoVehicles(TestData testData, String scenario) {
        // Add first vehicle (Vin matched) on new business policy for Scenario1, Scenario2 and Scenario3
        TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), VEHICLE1_VIN);
        // Add second vehicle (Vin matched) on new business policy for Scenario 1 and Scenario2
        TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), VEHICLE2_VIN);
        //Add second vehicle (Vin not matched) on new business policy for Scenario3
        TestData secondUnmatchedVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(getTestSpecificTD("VehicleTab_SecondUnmatchedVIN_NewBusiness")).resolveLinks();

        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
        testDataVehicleTab.add(firstVehicle);
        if (scenario.equals(MATCHED) || scenario.equals(NOT_MATCHED)) { //scenario 1 or scenario 2
            testDataVehicleTab.add(secondVehicle);
        } else if (scenario.equals(STUB)) { //scenario 3
            testDataVehicleTab.add(secondUnmatchedVehicle);
        }
        // add two vehicles
        return testData
                .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();
    }

    //adds three vehicles to the test data
    public TestData getTestDataWithThreeVehicles(TestData testData, String scenario) {
        TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), VEHICLE1_VIN);
        TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), VEHICLE2_VIN);
        TestData thirdVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), VEHICLE3_VIN);

        if (scenario.equals(NOT_MATCHED)) { //Scenario 1 - Update first Vehicle from Vin full match to Vin no match
            firstVehicle = modifyVehicleTabNonExistingVin(getPolicyTD());
        } else if (scenario.equals(MATCHED)) { //scenario 2 - Update first Vehicle from Vin full match to Vin full match
            firstVehicle = modifyVehicleTabWithExistingVin(getPolicyTD()).getTestData("VehicleTab");
        } else if (scenario.equals(STUB)) { //scenario 3 - Update y/m/m/s/s for the second vehicle
            secondVehicle = modifyVehicleTabVinStub(getPolicyTD());
        }
        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
        testDataVehicleTab.add(firstVehicle);
        testDataVehicleTab.add(secondVehicle);
        testDataVehicleTab.add(thirdVehicle);

        // add three vehicles
        return testData
                .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();
    }

    protected void viewRatingDetails() {
        PolicySummaryPage.buttonRenewalQuoteVersion.click();
        PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).controls.links.get(1).click(); //click to enter second renewal image
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.buttonViewRatingDetails.click();
    }

    protected void closeRatingDetails() {
        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
        PremiumAndCoveragesTab.buttonCancel.click();
    }

    protected void doSoftAssertions(ETCSCoreSoftAssertions softly, int vehicleCellIndex, String vehicleMake, String vehicleCompSymbol, String vehicleCollSymbol) {
        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(vehicleCellIndex).getValue()).isEqualToIgnoringCase(vehicleMake);
        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(vehicleCellIndex).getValue()).isEqualToIgnoringCase(vehicleCompSymbol);
        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(vehicleCellIndex).getValue()).isEqualToIgnoringCase(vehicleCollSymbol);
    }

    protected void cleanup() {
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE1_VIN, SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE2_VIN, SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE3_VIN, SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE4_VIN, SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable("2GTEC19V%3", SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable("2HNYD2H6%C", SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable("WBSAK031%M", SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE1_VIN, SYMBOL_2018_CHOICE);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE2_VIN, SYMBOL_2018_CHOICE);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE3_VIN, SYMBOL_2018_CHOICE);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE4_VIN, SYMBOL_2018_CHOICE);
        DatabaseCleanHelper.deleteVehicleRefDataVinTableByVinAndMaketext("JT2AE91A%M", "TOYOTA MOTR");
        DatabaseCleanHelper.cleanVehicleRefDataVinTable("2GTEC19V%3", SYMBOL_2018_CHOICE);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable("2HNYD2H6%C", SYMBOL_2018_CHOICE);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable("WBSAK031%M", SYMBOL_2018_CHOICE);
        DBService.get().executeUpdate(String.format(VehicleQueries.DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_STATECD_VERSION, "CA", SYMBOL_2018));
        DBService.get().executeUpdate(String.format(VehicleQueries.DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_STATECD_VERSION, "CA", SYMBOL_2018_CHOICE));
        DBService.get().executeUpdate(String.format(VehicleQueries.DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_STATECD_VERSION, "AZ", SYMBOL_2018));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, "99999999", "AZ", SYMBOL_2000));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, "99999999", "CA", SYMBOL_2000));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, "99999999", "CA", SYMBOL_2000_CHOICE));
    }
}