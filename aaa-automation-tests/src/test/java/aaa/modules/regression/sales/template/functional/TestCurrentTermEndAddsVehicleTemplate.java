package aaa.modules.regression.sales.template.functional;

import static aaa.common.Tab.buttonCancel;
import static aaa.helpers.db.queries.MsrpQueries.INSERT_MSRPCOMPCOLLCONTROL_VERSION;
import static aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.tableRatingDetailsVehicles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.db.queries.MsrpQueries;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.actiontabs.DifferencesActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.verification.ETCSCoreSoftAssertions;



public class TestCurrentTermEndAddsVehicleTemplate extends CommonTemplateMethods {

    protected VehicleTab vehicleTab = new VehicleTab();
    protected DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
    protected AssignmentTab assignmentTab = new AssignmentTab();
    protected DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();

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
    protected static final int VEHICLEYEARMIN_CHOICE = 2011;
    protected static final int VEHICLEYEARMIN_SELECT = 1997;
    protected static final int VEHICLEYEARMAX = 9999;
    protected static final String VEHICLETYPE = "Regular";
    protected static final String MSRP_2018_SELECT = "MSRP_2018_SELECT";
    protected static final String MSRP_2018_CHOICE = "MSRP_2018_CHOICE";
    protected static final int KEY = 49;


    protected TestData testDataThreeVehicles;

    protected static final String NOT_MATCHED = "NOT_MATCHED";
    protected static final String MATCHED = "MATCHED";
    protected static final String STUB = "STUB";


    protected void pas14532_refreshForCurrentAndRenewalTerms_initiateEndorsement(String scenario) {
        UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
        String vinTableFile = "VinUploadOnCurrentTerm.xlsx";
        String controlTableFile = "controlTable_CA.xlsx";
        String vinTableFileUpdatedVersion = "VinUploadOnCurrentTermUpdatedVersion.xlsx";
        TestData testDataTwoVehicles = getTestDataWithTwoVehicles(getPolicyTD(), scenario);
        log.info("testData two vehicles is " + testDataTwoVehicles);

        //1. Create CA auto policy with two vehicles and save the expiration date
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

        //4. Initiate endorsements
        initiateEndorsement();
    }

    public void pas14532_refreshForCurrentAndRenewalTerms_bindEndorsement(String scenario) {

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
        if (scenario.equals(NOT_MATCHED)) { //scenario 1
            testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD(), NOT_MATCHED);
        } else if (scenario.equals(MATCHED)) { //scenario 2
            testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD(), MATCHED);
        } else if (scenario.equals(STUB)) { //scenario 3
            testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD(), STUB);
        }

        //6. Calculate Premium and bind the endorsement.
        if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT))
            policy.getDefaultView().fillFromTo(testDataThreeVehicles, VehicleTab.class, PremiumAndCoveragesTab.class, true); //select
        else if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE))
            policy.getDefaultView().fillFromTo(testDataThreeVehicles, VehicleTab.class, DocumentsAndBindTab.class, true); //choice

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();

        //Conflicts/differences page
        DifferencesActionTab differencesActionTab = new DifferencesActionTab();
        if (scenario.equals(NOT_MATCHED) || scenario.equals(STUB)) { //scenario 1 or scenario 3
            differencesActionTab.applyDifferences(true);
        } else if (scenario.equals(MATCHED)) { //scenario 2
            differencesActionTab.applyDifferences(false);
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
        TestData endorsementData = getPolicyTD("Endorsement", "TestData");
        policy.createEndorsement(endorsementData);
    }

    //Update first vehicle with VIN matched
    protected TestData modifyVehicleTabWithExistingVin(TestData testData) {
        testData.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), VEHICLE1_UPDATED_VIN);
        return testData;
    }

    //Update first Vehicle to VIN no match
    protected TestData modifyVehicleTabNonExistingVin(TestData testData) {
        return testData.getTestData(vehicleTab.getMetaKey()).adjust(getTestSpecificTD("VehicleTab_NonExistingVIN")).resolveLinks();
    }

    //Update Y/M/M/S/S for second Vehicle
    protected TestData modifyVehicleTabVinStub(TestData testData) {
        return testData.getTestData(vehicleTab.getMetaKey()).adjust(getTestSpecificTD("VehicleTab_updateVINStub")).resolveLinks();
    }

    protected TestData getThreeAssignmentsTestData() {
        TestData testData = getTwoAssignmentsTestData();
        List<TestData> assignmentList = testData.getTestDataList("DriverVehicleRelationshipTable");
        TestData thirdAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");
        assignmentList.add(thirdAssignment);
        return new SimpleDataProvider().adjust("DriverVehicleRelationshipTable", assignmentList);
    }

    //adds two vehicles to the test data
    public TestData getTestDataWithTwoVehicles(TestData testData, String scenario) {
        // Build Assignment Tab
        TestData testDataAssignmentTab = getTwoAssignmentsTestData();
        TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE1_VIN);
        TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE2_VIN);
        TestData secondUnmatchedVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey()).adjust(getTestSpecificTD("VehicleTab_SecondUnmatchedVIN_NewBusiness")).resolveLinks();

        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
        testDataVehicleTab.add(firstVehicle);
        if (scenario.equals(MATCHED) || scenario.equals(NOT_MATCHED)) { //scenario 1 or scenario 2
            testDataVehicleTab.add(secondVehicle);
        } else if (scenario.equals(STUB)) {
            testDataVehicleTab.add(secondUnmatchedVehicle);
        }
        TestData twoVehicleData = null;
        // add 2 vehicles
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            twoVehicleData = testData.adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks()
                    .adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks()
                    .adjust(documentsAndBindTab.getMetaKey(), getTestSpecificTD("DocumentsAndBindTab_TestDataCurrentTermEndAddsVehicle")).resolveLinks();
        } else {
            twoVehicleData = testData.adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks()
                    .adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks();
        }
        return twoVehicleData;
    }

    //adds three vehicles to the test data
    public TestData getTestDataWithThreeVehicles(TestData testData, String scenario) {
        TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE1_VIN);
        TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE2_VIN);
        TestData thirdVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE3_VIN);
        // Build Assignment Tab for endorsement
        TestData testDataAssignmentTab = getThreeAssignmentsTestData();
        TestData threeVehicleData = null;

        if (scenario.equals(NOT_MATCHED)) { //scenario 1
            firstVehicle = modifyVehicleTabNonExistingVin(getPolicyTD());
        } else if (scenario.equals(MATCHED)) { //scenario 2
            firstVehicle = modifyVehicleTabWithExistingVin(getPolicyTD()).getTestData("VehicleTab");
        } else if (scenario.equals(STUB)) { //scenario 3
            secondVehicle = modifyVehicleTabVinStub(getPolicyTD());
        }

        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
        testDataVehicleTab.add(firstVehicle);
        testDataVehicleTab.add(secondVehicle);
        testDataVehicleTab.add(thirdVehicle);

        // add 3 vehicles
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            threeVehicleData = testData
                    .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks()
                    .adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks()
                    .adjust(driverActivityReportsTab.getMetaKey(), getTestSpecificTD("DriverActivityReportsTab_TestDataCurrentTermEndAddsVehicleEndorsement")).resolveLinks()
                    .adjust(documentsAndBindTab.getMetaKey(), getTestSpecificTD("DocumentsAndBindTab_TestDataCurrentTermEndAddsVehicleEndorsement")).resolveLinks();
        } else {
            threeVehicleData = testData
                    .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks()
                    .adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks()
                    .adjust(driverActivityReportsTab.getMetaKey(), getTestSpecificTD("DriverActivityReportsTab_TestDataCurrentTermEndAddsVehicleEndorsement")).resolveLinks();
        }
        return threeVehicleData;
    }

    protected void viewRatingDetails() {
        PolicySummaryPage.buttonRenewalQuoteVersion.click();
        PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).controls.links.get(1).click(); //click to enter second renewal image
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        PremiumAndCoveragesTab.RatingDetailsView.open();
    }

    protected void closeRatingDetails() {
        PremiumAndCoveragesTab.RatingDetailsView.close();
        buttonCancel.click();
    }

    protected void doSoftAssertions(ETCSCoreSoftAssertions softly, int vehicleCellIndex, String vehicleMake, String vehicleCompSymbol, String vehicleCollSymbol) {
        softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(vehicleCellIndex).getValue()).isEqualToIgnoringCase(vehicleMake);
        softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(vehicleCellIndex).getValue()).isEqualTo(vehicleCompSymbol);
        softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(vehicleCellIndex).getValue()).isEqualTo(vehicleCollSymbol);
    }

    protected void pas16522_refreshMSRPVehicleForCurrentAndRenewalTerms_initiateEndorsement() {
        UploadToVINTableTab uploadToControlTableTab = new UploadToVINTableTab();
        String controlTableMSRPFile = "controlTable_updateMSRPVersion_CA.xlsx";
        TestData testDataTwoMSRPVehicles = getTestDataWithTwoMSRPVehicles(getPolicyTD());

        //1. Create CA auto policy with two vehicles and save the expiration date
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
        DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

        policy.getDefaultView().fillUpTo(testDataTwoMSRPVehicles, PremiumAndCoveragesTab.class, true);
        premiumAndCoveragesTab.submitTab();
        policy.getDefaultView().fillFromTo(testDataTwoMSRPVehicles, DriverActivityReportsTab.class, DocumentsAndBindTab.class, true);
        documentsAndBindTab.submitTab();
        new PurchaseTab().fillTab(testDataTwoMSRPVehicles).submitTab();
        policyNumber = PolicySummaryPage.getPolicyNumber();
        log.info("policy number: " + policyNumber);

        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

        //2. control table file upload for changing the effective date and expiration date
        adminApp().open();
        uploadToControlTableTab.uploadControlTable(controlTableMSRPFile);

        LocalDateTime expirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(360);
        LocalDateTime effectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(361);
        log.info("expirationDate:" + expirationDate);
        log.info("effectiveDate:" + effectiveDate);
        updateControlTable(expirationDate, effectiveDate);

        //3. Change system date to R-35 and renew it
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));

        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            DBService.get().executeUpdate(String.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, VEHICLEYEARMIN_CHOICE, VEHICLEYEARMAX, VEHICLETYPE, MSRP_2018_CHOICE, KEY));
        } else {
            DBService.get().executeUpdate(String.format(INSERT_MSRPCOMPCOLLCONTROL_VERSION, VEHICLEYEARMIN_SELECT, VEHICLEYEARMAX, VEHICLETYPE, MSRP_2018_SELECT, KEY));
        }

        //4. Initiate endorsement
        initiateEndorsement();
    }

    public void pas16522_refreshMSRPVehicleForCurrentAndRenewalTerms_bindEndorsement() {
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

        TestData testDataThreeMSRPVehicles = getTestDataWithThreeMSRPVehicles(getPolicyTD());

        //6. Calculate Premium and bind the endorsement.
        if (getPolicyType().equals(PolicyType.AUTO_CA_SELECT))
            policy.getDefaultView().fillFromTo(testDataThreeMSRPVehicles, VehicleTab.class, PremiumAndCoveragesTab.class, true); //select
        else if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE))
            policy.getDefaultView().fillFromTo(testDataThreeMSRPVehicles, VehicleTab.class, DocumentsAndBindTab.class, true); //choice

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();
    }

    public TestData getTestDataWithTwoMSRPVehicles(TestData testData) {
        // Build Assignment Tab
        TestData testDataAssignmentTab = getTwoAssignmentsTestData();
        TestData firstMSRPVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(getTestSpecificTD("VehicleTab_updateMSRPVehicle1")).resolveLinks();
        TestData secondMSRPVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(getTestSpecificTD("VehicleTab_updateMSRPVehicle2")).resolveLinks();

        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
        testDataVehicleTab.add(firstMSRPVehicle);
        testDataVehicleTab.add(secondMSRPVehicle);

        TestData twoVehicleData = null;
        // add 2 vehicles
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            twoVehicleData = testData.adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks()
                    .adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks()
                    .adjust(documentsAndBindTab.getMetaKey(), getTestSpecificTD("DocumentsAndBindTab_TestDataCurrentTermEndAddsVehicle")).resolveLinks();
        } else {
            twoVehicleData = testData.adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks()
                    .adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks();
        }
        return twoVehicleData;
    }

    public TestData getTestDataWithThreeMSRPVehicles(TestData testData) {
        TestData modifiedfirstMSRPVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(getTestSpecificTD("VehicleTab_updateMSRPVehicle3")).resolveLinks();
        TestData modifiedsecondMSRPVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(getTestSpecificTD("VehicleTab_updateMSRPVehicle4")).resolveLinks();
        TestData thirdMSRPVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(getTestSpecificTD("VehicleTab_updateMSRPVehicle5")).resolveLinks();
        // Build Assignment Tab for endorsement
        TestData testDataAssignmentTab = getThreeAssignmentsTestData();
        TestData threeVehicleData = null;

        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
        testDataVehicleTab.add(modifiedfirstMSRPVehicle);
        testDataVehicleTab.add(modifiedsecondMSRPVehicle);
        testDataVehicleTab.add(thirdMSRPVehicle);

        // add 3 vehicles
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            threeVehicleData = testData
                    .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks()
                    .adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks()
                    .adjust(driverActivityReportsTab.getMetaKey(), getTestSpecificTD("DriverActivityReportsTab_TestDataCurrentTermEndAddsVehicleEndorsement")).resolveLinks()
                    .adjust(documentsAndBindTab.getMetaKey(), getTestSpecificTD("DocumentsAndBindTab_TestDataCurrentTermEndAddsVehicleEndorsement")).resolveLinks();
        } else {
            threeVehicleData = testData
                    .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks()
                    .adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks()
                    .adjust(driverActivityReportsTab.getMetaKey(), getTestSpecificTD("DriverActivityReportsTab_TestDataCurrentTermEndAddsVehicleEndorsement")).resolveLinks();
        }
        return threeVehicleData;
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
        DBService.get().executeUpdate(String.format(MsrpQueries.DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY, MSRP_2018_CHOICE, KEY, VEHICLETYPE));
        DBService.get().executeUpdate(String.format(MsrpQueries.DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY, MSRP_2018_SELECT, KEY, VEHICLETYPE));
        DBService.get().executeUpdate(String.format(VehicleQueries.DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_STATECD_VERSION, "CA", SYMBOL_2018));
        DBService.get().executeUpdate(String.format(VehicleQueries.DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_STATECD_VERSION, "CA", SYMBOL_2018_CHOICE));
        DBService.get().executeUpdate(String.format(VehicleQueries.DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_STATECD_VERSION, "AZ", SYMBOL_2018));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, "99999999", "AZ", SYMBOL_2000));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, "99999999", "CA", SYMBOL_2000));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, "99999999", "CA", SYMBOL_2000_CHOICE));
    }
}