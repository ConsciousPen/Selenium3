package aaa.modules.regression.sales.auto_ca.select.functional;


import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.apache.bcel.generic.NEW;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.modules.regression.sales.template.functional.CommonTemplateMethods;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.ETCSCoreSoftAssertions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.*;

public class TestCurrentTermEndAddsVehicle extends AutoCaSelectBaseTest {

    private VehicleTab vehicleTab = new VehicleTab();
    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

    private String policyNumber;
    private LocalDateTime policyExpirationDate;
    private LocalDateTime effDateForControlTable;
    private LocalDateTime expDateForControlTable;
    private static final String VEHICLE1_VIN = "KNDJT2A2XA7038383";
    private static final String VEHICLE2_VIN = "JT2AE91A7M3425407";
    private static final String VEHICLE2_NOMATCH_VIN = "WWEKN3DD0E0344466";
    private static final String VEHICLE3_VIN = "1FTRE1421YHA89455";
    private static final String VEHICLE4_VIN = "5NPEU46C991234567";
    private static final String VEHICLE1_UPDATED_VIN = "2GTEC19V531282646";
    private static final String SYMBOL_2000 = "SYMBOL_2000";
    private static final String SYMBOL_2018 = "SYMBOL_2018";
    private static final String SYMBOL_2000_CHOICE = "SYMBOL_2000_CHOICE";
    private static final String SYMBOL_2018_CHOICE = "SYMBOL_2018_CHOICE";
    private TestData testDataThreeVehicles;
	private aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab assignmentTab = new aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab();



    /**
     ** @author Kiruthika Rajendran/Chris Johns
     *
     * @name Current Term End Adds Vehicle:
     * Make refresh correct for current and renewal terms
     *
     * @scenario 2
     * 1. Create CA SELECT Auto Quote with two vehicles: First Vehicle - VIN MATCHED, Second Vehicle - VIN MATCHED
     * 2. Make policy status - Proposed
     * 3. Initiate Endorsement
     * 4. Update VIN number for second Vehicle to VIN MATCHED
     * 5. Add third Vehicle
     * 6. Calculate Premium and bind the endorsement
     * 7. Open the last renewal inscription in 'Transaction history'
     * Expected Result:
     * The First Vehicle - NOT updated will not change/not refresh
     * The second Vehicle - updated according to 4th step VIN details
     * The third Vehicle - displayed new data according to version
     * @details
     */

    //Scenario 1 - second VIN not matched
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-14532")
    public void pas14532_refreshForCurrentAndRenewalTermsVinNotMatched(@Optional("CA") String state) {
        pas14532_refreshForCurrentAndRenewalTerms(state, "NOT_MATCHED");
    }

    //Scenario 2 - second VIN matched
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-14532")
    public void pas14532_refreshForCurrentAndRenewalTermsVinMatched(@Optional("CA") String state) {
        pas14532_refreshForCurrentAndRenewalTerms(state,"MATCHED");
    }

    //Scenario 3 - VIN stub update
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-14532")
    public void pas14532_refreshForCurrentAndRenewalTermsVinStubUpdate(@Optional("CA") String state) {
        pas14532_refreshForCurrentAndRenewalTerms(state,"STUB");
    }

    @Parameters({"state"})
    public void pas14532_refreshForCurrentAndRenewalTerms(@Optional("CA") String state, String scenario) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
        String vinTableFile = "VinUploadOnCurrentTerm.xlsx";
        String controlTableFile = "controlTable_CA.xlsx";

        //1. Create CA auto quote with two vins and save the expiration date
        mainApp().open();
        createCustomerIndividual();
        TestData testDataTwoVehicles = getTestDataWithTwoVehicles(getPolicyTD(), scenario);
        policyNumber = createPolicy(testDataTwoVehicles);
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

        //2. control table file upload for changing the effective date and expiration date
        // vin upload to update second VIN To another VIN where VIN will be matched
        adminApp().open();
        uploadToVINTableTab.uploadFiles(controlTableFile, vinTableFile);
        LocalDateTime expirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(360);
        LocalDateTime effectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(361);
        updateControlTable(state, expirationDate, effectiveDate);

        //3. Change system date to R-35 and renew it
        TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(35));
	    JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
	    JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

        //<TODO>
        //<need a new method to assert the renewal image is PROPOSED>

        //4. Initiate endorsement
        initiateEndorsement();

        //5. Update VIN number for second Vehicle and add a third Vehicle
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());

        if (scenario.equals("NOT_MATCHED")) { //scenario 1
            testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD(), "NOT_MATCHED");
        } else if (scenario.equals("MATCHED")) { //scenario 2
            testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD(), "MATCHED");
        } else if (scenario.equals("STUB")) { //scenario 3
            //TODO - to be added later, if needed
            testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD(), "STUB");
        }

        //6. Calculate Premium and bind the endorsement.
        ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
	    policy.getDefaultView().fillFromTo(testDataThreeVehicles, aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab.class, aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.class, true);

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();

        //Verify the Vehicle data on the policy summary page shows the SYMBOL_200 data - NOT refreshed data
        softly.assertThat(PolicySummaryPage.tablePolicyVehicles.getRow(3).getCell(3).getValue()).doesNotContain("MOTOR");
        softly.assertThat(PolicySummaryPage.tablePolicyVehicles.getRow(2).getCell(3).getValue()).doesNotContain("MOTOR");
        softly.assertThat(PolicySummaryPage.tablePolicyVehicles.getRow(1).getCell(3).getValue()).doesNotContain("MOTOR");

        //7. Verify Latest Renewal Version has correct vehicle details

        if (scenario.equals("NOT_MATCHED")) { //scenario 1 blocked by defect PAS-15964 (Ajax error)
            PolicySummaryPage.buttonRenewalQuoteVersion.click();
            PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).controls.links.get(1).click(); //click to enter second renewal image
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
            buttonViewRatingDetails.click();

            // The First Vehicle - Displays Updated/refreshed data according to version;
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualToIgnoringCase("HYUNDAI MOTOR");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isEqualTo("12");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isEqualTo("17");

            // The second Vehicle - NOT updated will not change/not refresh;
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(3).getValue()).isEqualToIgnoringCase("TOYOTA MOTOR");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Model").getCell(3).getValue()).isEqualToIgnoringCase("TOYT COROLLA");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(3).getValue()).isEqualTo("20");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(3).getValue()).isEqualTo("30");

            // The third Vehicle - displayed updated/refreshed data according to version;
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(4).getValue()).isEqualToIgnoringCase("FORD MOTOR");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(4).getValue()).isEqualTo("25");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(4).getValue()).isEqualTo("37");

            //Close the Renewal Image
            buttonRatingDetailsOk.click();
            buttonCancel.click();

            //Catch any assertion errors seen during test
            softly.close();
        } else if (scenario.equals("MATCHED")) { //scenario 2
            //TODO - Scenario to be added later, if needed
            PolicySummaryPage.buttonRenewalQuoteVersion.click();
            PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).controls.links.get(1).click(); //click to enter second renewal image
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
            buttonViewRatingDetails.click();

            // The First Vehicle - Displays Updated/refreshed data according to version;
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualToIgnoringCase("GMC MOTOR");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isEqualTo("50");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isEqualTo("50");

            // The second Vehicle - NOT updated will not change/not refresh;
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(3).getValue()).isEqualToIgnoringCase("TOYOTA MOTOR");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Model").getCell(3).getValue()).isEqualToIgnoringCase("TOYT COROLLA");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(3).getValue()).isEqualTo("20");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(3).getValue()).isEqualTo("30");

            // The third Vehicle - displayed updated/refreshed data according to version;
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(4).getValue()).isEqualToIgnoringCase("FORD MOTOR");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(4).getValue()).isEqualTo("25");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(4).getValue()).isEqualTo("37");

            //Close the Renewal Image
            buttonRatingDetailsOk.click();
            buttonCancel.click();

            //Catch any assertion errors seen during test
            softly.close();
        } else if (scenario.equals("STUB")) { //scenario 3
            //TODO - Scenario to be added later, if needed
            PolicySummaryPage.buttonRenewalQuoteVersion.click();
            PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).controls.links.get(1).click(); //click to enter second renewal image
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
            buttonViewRatingDetails.click();

            // The First Vehicle - Displays Updated/refreshed data according to version;
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualToIgnoringCase("KIA MOTOR");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isEqualTo("20");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isEqualTo("10");

            // The second Vehicle - NOT updated will not change/not refresh;
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(3).getValue()).isEqualToIgnoringCase("BMW MOTOR");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(3).getValue()).isEqualTo("32");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(3).getValue()).isEqualTo("60");

            // The third Vehicle - displayed updated/refreshed data according to version;
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Make").getCell(4).getValue()).isEqualToIgnoringCase("FORD MOTOR");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(4).getValue()).isEqualTo("25");
            softly.assertThat(tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(4).getValue()).isEqualTo("37");

            //Close the Renewal Image
            buttonRatingDetailsOk.click();
            buttonCancel.click();

            //Catch any assertion errors seen during test
            softly.close();
        }
    }

    //FOR FIRST POLICY - ADD TWO VECHICLES
    public TestData getTestDataWithTwoVehicles(TestData testData, String scenario) {
        /*TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE1_VIN);

		// TestData secondVehicle = null;
        TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE2_VIN);

        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
        testDataVehicleTab.add(firstVehicle);
        testDataVehicleTab.add(secondVehicle);*/

        // Build Assignment Tab
        TestData testDataAssignmentTab = getTwoAssignmentsTestData();

        TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE1_VIN);
        TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE2_VIN);
        TestData secondUnmatchedVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE2_NOMATCH_VIN)
                .adjust(AutoCaMetaData.VehicleTab.YEAR.getLabel(), "2012")
                .adjust(AutoCaMetaData.VehicleTab.MAKE.getLabel(), "ACURA")
                .adjust(AutoCaMetaData.VehicleTab.MODEL.getLabel(), "MDX")
                .adjust(AutoCaMetaData.VehicleTab.SERIES.getLabel(), "MDX ADVANCE")
                .adjust(AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel(), "WAGON 4 DOOR");

        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
        testDataVehicleTab.add(firstVehicle);
        if (scenario.equals("MATCHED") || scenario.equals("NOT_MATCHED")) { //scenario 1 or scenario 2
            testDataVehicleTab.add(secondVehicle);
        } else if (scenario.equals("STUB")) {
            testDataVehicleTab.add(secondUnmatchedVehicle);
        }
        // add 2 vehicles
        return testData
                .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks()
                .adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks();
    }

    //FOR ENDORSEMENT - UPDATE first VEHICLE AND ADD THIRD
    public TestData getTestDataWithThreeVehicles(TestData testData2, String scenario) {
        /*TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE1_VIN);

        TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE2_VIN);

        TestData thirdVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE3_VIN);

        TestData updateFirstVehicle = null;
        if (scenario.equals("NOT_MATCHED")) { //scenario 1
            updateFirstVehicle = modifyVehicleTabNonExistingVin(getPolicyTD()).getTestData("VehicleTab");
        } else if (scenario.equals("MATCHED")) { //scenario 2
            //TODO - Scenario to be added later, if needed
            updateFirstVehicle = modifyVehicleTabWithExistingVin(getPolicyTD()).getTestData("VehicleTab");
        } else if (scenario.equals("STUB")) { //scenario 3
            //TODO - Scenario to be added later, if needed
        }

	    // Build Vehicle Tab old version vin + updated vehicle
	    List<TestData> testDataVehicleTab = new ArrayList<>();
	    testDataVehicleTab.add(updateFirstVehicle);
	    testDataVehicleTab.add(secondVehicle);
	    testDataVehicleTab.add(thirdVehicle);*/

            TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                    .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE1_VIN);
            TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                    .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE2_VIN);
            TestData thirdVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                    .adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), VEHICLE3_VIN);

            if(scenario.equals("NOT_MATCHED")) { //scenario 1
                firstVehicle = modifyVehicleTabNonExistingVin(getPolicyTD()).getTestData("VehicleTab");
            } else if(scenario.equals("MATCHED")) { //scenario 2
                firstVehicle = modifyVehicleTabWithExistingVin(getPolicyTD()).getTestData("VehicleTab");
            } else if(scenario.equals("STUB")) { //scenario 3
                secondVehicle = modifyVehicleTabVinStub(getPolicyTD()).getTestData("VehicleTab");
            }
            // Build Vehicle Tab old version vin + updated vehicle
            List<TestData> testDataVehicleTab = new ArrayList<>();
            testDataVehicleTab.add(firstVehicle);
            testDataVehicleTab.add(secondVehicle);
            testDataVehicleTab.add(thirdVehicle);

	    // Build Assignment Tab for endorsement
	    TestData testDataAssignmentTab = getThreeAssignmentsTestData();

	    // add 3 vehicles
	    return testData2
			    .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks()
			    .adjust(assignmentTab.getMetaKey(), testDataAssignmentTab).resolveLinks();
    }

    //Update vehicle 1 with VIN matched
    public TestData modifyVehicleTabWithExistingVin(TestData testData) {
        testData
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), VEHICLE1_UPDATED_VIN);
        return testData;
    }

    //Update Vehicle 1 info to Vin no match
    public TestData modifyVehicleTabNonExistingVin(TestData testData2) {
        testData2
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "ZZXKN3DD2E0344466")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), "2009")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), "HYUNDAI")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MODEL.getLabel()), "SONATA")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.SERIES.getLabel()), "SONATA SE/LIMITED")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel()), "SEDAN 4 DOOR")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.ODOMETER_READING_DATE.getLabel()), "12/1/18");
//	            AssignmentTab().getMetaKey()).getTestDataList("DriverVehicleRelationshipTable").get(0).mask("Vehicle").resolveLinks();
        return testData2;
    }

        //Update Y/M/M/S/S for Vehicle 1 info to Vin partial match
        public TestData modifyVehicleTabVinStub(TestData testData3) {
            testData3
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "WWEKN3DD0E0344466")
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.YEAR.getLabel()), "2010")
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MAKE.getLabel()), "BMW")
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.MODEL.getLabel()), "X5")
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.SERIES.getLabel()), "X5 M")
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.BODY_STYLE.getLabel()), "WAGON 4 DOOR");
            return testData3;
        }

	private TestData getTwoAssignmentsTestData() {
        TestData  firstAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");
        TestData  secondAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");

        List<TestData> listDataAssignmentTab = new ArrayList<>();
        listDataAssignmentTab.add(firstAssignment);
        listDataAssignmentTab.add(secondAssignment);

        return new SimpleDataProvider().adjust("DriverVehicleRelationshipTable", listDataAssignmentTab);
    }

    private TestData getThreeAssignmentsTestData() {
        TestData  firstAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");
        TestData  secondAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");
        TestData  thirdAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");

        List<TestData> listDataAssignmentTab = new ArrayList<>();
        listDataAssignmentTab.add(firstAssignment);
        listDataAssignmentTab.add(secondAssignment);
        listDataAssignmentTab.add(thirdAssignment);

        return new SimpleDataProvider().adjust("DriverVehicleRelationshipTable", listDataAssignmentTab);
    }

	private void initiateEndorsement() {
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.createEndorsement(getPolicyTD("Endorsement", "TestData"));
    }

    private void updateControlTable(String state, LocalDateTime expirationDate, LocalDateTime effectiveDate) {
        String formattedExpirationDate = expirationDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedEffectiveDate = effectiveDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, formattedExpirationDate, state, "SYMBOL_2000"));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EFFECTIVEDATE_BY_STATECD_VERSION, formattedEffectiveDate, state, "SYMBOL_2018"));
    }

    @AfterClass(alwaysRun = true)
    protected void resetDefault() {
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE1_VIN, SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE2_VIN, SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE3_VIN, SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE4_VIN, SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable("2GTEC19V&3",SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable("2HNYD2H6%C",SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable("5YMGY0C5%A",SYMBOL_2018);
        DBService.get().executeUpdate(String.format(VehicleQueries.DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_STATECD_VERSION, "CA", SYMBOL_2018));
        DBService.get().executeUpdate(String.format(VehicleQueries.DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_STATECD_VERSION, "CA", SYMBOL_2018_CHOICE));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, "99999999", "CA", SYMBOL_2000));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, "99999999", "CA", SYMBOL_2000_CHOICE));
    }
}

