package aaa.modules.regression.sales.auto_ss.functional;

import aaa.admin.modules.administration.uploadVIN.defaulttabs.UploadToVINTableTab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.ETCSCoreSoftAssertions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TestCurrentTermEndAddsVehicle extends AutoSSBaseTest {

    private VehicleTab vehicleTab = new VehicleTab();
    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

    private String policyNumber;
    private LocalDateTime policyExpirationDate;
    private LocalDateTime effDateForControlTable;
    private LocalDateTime expDateForControlTable;
    private static final String VEHICLE1_VIN = "KNDJT2A2XA7038383";
    private static final String VEHICLE2_VIN = "JT2AE91A7M3425407";
    private static final String VEHICLE3_VIN = "1FTRE1421YHA89455";
	private static final String VEHICLE4_VIN = "5NPEU46C991234567";
    private static final String VEHICLE1_UPDATED_VIN = "2GTEC19V531282646";
    private static final String SYMBOL_2000 = "SYMBOL_2000";
    private static final String SYMBOL_2018 = "SYMBOL_2018";
    private TestData testDataThreeVehicles;

    /**
     ** @author Kiruthika Rajendran
     *
     * @name Current Term End Adds Vehicle:
     * Make refresh correct for current and renewal terms
     *
     * @scenario 2
     * 1. Create Auto SS Quote with two vehicles: First Vehicle - VIN MATCHED, Second Vehicle - VIN MATCHED
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14532")
    public void pas14532_refreshForCurrentAndRenewalTermsVinNotMatched(@Optional("AZ") String state) {
        pas14532_refreshForCurrentAndRenewalTerms(state,"NOT_MATCHED");
    }

    //Scenario 2 - second VIN matched
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14532")
    public void pas14532_refreshForCurrentAndRenewalTermsVinMatched(@Optional("AZ") String state) {
        pas14532_refreshForCurrentAndRenewalTerms(state,"MATCHED");
    }

    //Scenario 3 - VIN stub update
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14532")
    public void pas14532_refreshForCurrentAndRenewalTermsVinStubUpdate(@Optional("AZ") String state) {
        pas14532_refreshForCurrentAndRenewalTerms(state,"STUB");
    }

    @Parameters({"state"})
    public void pas14532_refreshForCurrentAndRenewalTerms(@Optional("AZ") String state, String scenario) {
        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
        UploadToVINTableTab uploadToVINTableTab = new UploadToVINTableTab();
        String vinTableFile = "VinUploadOnCurrentTerm_AZ_SS.xlsx";
        String controlTableFile = "controlTable_AZ_SS.xlsx";

        //1. Create auto SS quote with two vins and save the expiration date
        mainApp().open();
        createCustomerIndividual();
        TestData testDataTwoVehicles = getTestDataWithTwoVehicles(getPolicyTD());
        policyNumber = createPolicy(testDataTwoVehicles);
        LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();

        //2. control table file upload for changing the effective date and expiration date
        // vin upload to update second VIN To another VIN where VIN will be matched
        adminApp().open();
        uploadToVINTableTab.uploadFiles(controlTableFile, vinTableFile);
        LocalDateTime expirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(360);
        LocalDateTime effectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(361);
        updateControlTable(state,expirationDate,effectiveDate);

        //3. Change system date to R-35 and renew it
        moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));

        //<TODO>
        //<need a new method to assert the renewal image is PROPOSED>

        //4. Initiate endorsement
        initiateEndorsement();

        //5. Update VIN number for second Vehicle and add a third Vehicle
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

        if(scenario.equals("NOT_MATCHED")) { //scenario 1
            testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD(), "NOT_MATCHED");
        } else if(scenario.equals("MATCHED")) { //scenario 2
             testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD(), "MATCHED");
        } else if(scenario.equals("STUB")) { //scenario 3
            //TODO - to be added later, if needed
        }

        //6. Calculate Premium and bind the endorsement.
	    ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
        policy.getDefaultView().fillFromTo(testDataThreeVehicles, VehicleTab.class, PremiumAndCoveragesTab.class,true);
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();

		//Verify the Vehicle data on the policy summary page shows the SYMBOL_200 data - NOT refreshed data
	    softly.assertThat(PolicySummaryPage.tablePolicyVehicles.getRow(3).getCell(3).getValue()).doesNotContain("MOTOR");
	    softly.assertThat(PolicySummaryPage.tablePolicyVehicles.getRow(2).getCell(3).getValue()).doesNotContain("MOTOR");
	    softly.assertThat(PolicySummaryPage.tablePolicyVehicles.getRow(1).getCell(3).getValue()).doesNotContain("MOTOR");

       //7. Verify Latest Renewal Version has correct vehicle details

        if(scenario.equals("NOT_MATCHED")) { //scenario 1 blocked by defect PAS-15964 (Ajax error)
            PolicySummaryPage.buttonRenewalQuoteVersion.click();
            PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(2).controls.links.get(1).click(); //click to enter second renewal image
	        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
	        PremiumAndCoveragesTab.buttonViewRatingDetails.click();

	        // The First Vehicle - Displays Updated/refreshed data according to version;
	        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(2).getValue()).isEqualToIgnoringCase("HYUNDAI MOTOR");
	        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isEqualTo("55");
	        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isEqualTo("15");

	        // The second Vehicle - NOT updated will not change/not refresh;
	        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(3).getValue()).isEqualToIgnoringCase("TOYOTA MOTOR");
	        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Model").getCell(3).getValue()).isEqualToIgnoringCase("TOYT COROLLA");
	        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(3).getValue()).isEqualTo("50");
	        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(3).getValue()).isEqualTo("51");

	        // The third Vehicle - displayed updated/refreshed data according to version;
	        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Make").getCell(4).getValue()).isEqualToIgnoringCase("FORD MOTOR");
	        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(4).getValue()).isEqualTo("24");
	        softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(4).getValue()).isEqualTo("14");

	        //Close the Renewal Image
	        PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	        PremiumAndCoveragesTab.buttonCancel.click();

	        //Catch any assertion errors seen during test
	        softly.close();

        } else if(scenario.equals("MATCHED")) { //scenario 2
	        //TODO - Scenario to be added later, if needed
        } else if(scenario.equals("STUB")) { //scenario 3
            //TODO - Scenario to be added later, if needed
        }
    }

    //FOR FIRST POLICY - ADD TWO VECHICLES
    public TestData getTestDataWithTwoVehicles(TestData testData) {
        TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), VEHICLE1_VIN);
       // TestData secondVehicle = null;

        TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), VEHICLE2_VIN);

        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
        testDataVehicleTab.add(firstVehicle);
        testDataVehicleTab.add(secondVehicle);

        // add 2 vehicles
        return testData
                .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();
    }

    //FOR ENDORSEMENT - UPDATE first VEHICLE AND ADD THIRD
      public TestData getTestDataWithThreeVehicles(TestData testData2, String scenario) {
        TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                    .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), VEHICLE1_VIN);
          TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                  .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), VEHICLE2_VIN);
          TestData thirdVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                  .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), VEHICLE3_VIN);

          TestData updateFirstVehicle = null;
          if(scenario.equals("NOT_MATCHED")) { //scenario 1
              updateFirstVehicle = modifyVehicleTabNonExistingVin(getPolicyTD()).getTestData("VehicleTab");
          } else if(scenario.equals("MATCHED")) { //scenario 2
	          //TODO - Scenario to be added later, if needed
          } else if(scenario.equals("STUB")) { //scenario 3
	          //TODO - Scenario to be added later, if needed
          }
        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
            testDataVehicleTab.add(updateFirstVehicle);
            testDataVehicleTab.add(secondVehicle);
            testDataVehicleTab.add(thirdVehicle);

        // add 2 vehicles
        return testData2
                .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();
    }

    //Update vehicle 2 with VIN matched
    public TestData modifyVehicleTabWithExistingVin(TestData testData){
        testData
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), VEHICLE1_UPDATED_VIN);
        return testData;
    }

    //Update Vehicle 2 info to Vin no match
    public TestData modifyVehicleTabNonExistingVin(TestData testData2) {
            testData2
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "ZZXKN3DD2E0344466")
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), "2009")
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), "HYUNDAI")
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), "SONATA")
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), "SONATA SE/LIMITED")
                    .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), "SEDAN 4 DOOR");
            return testData2;
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
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE1_VIN,SYMBOL_2018);
        DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE2_VIN,SYMBOL_2018);
	    DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE3_VIN,SYMBOL_2018);
	    DatabaseCleanHelper.cleanVehicleRefDataVinTable(VEHICLE4_VIN,SYMBOL_2018);
        DBService.get().executeUpdate(String.format(VehicleQueries.DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_STATECD_VERSION,"AZ",SYMBOL_2018));
        DBService.get().executeUpdate(String.format(VehicleQueries.UPDATE_VEHICLEREFDATAVINCONTROL_EXPIRATIONDATE_BY_STATECD_VERSION, "99999999", "AZ", SYMBOL_2000));
    }


    /*@Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14532")
    public void pas14532_MakeRefreshCorrectForRenewalTerm(@Optional("AZ") String state) {
//    VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
//    String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadFileType.VIN_UPLOAD_ON_CURRENT_TERM.get());
       // multipleVinTable = VIN_UPLOAD_ON_CURRENT_TERM
      // VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());

      // String vinTableFile = "multipleVinTable.xlsx";
 //      String controlTableFile = "controlTable_AZ_SS.xlsx";
//
//       adminApp().open();
//       vinMethods.uploadFiles(controlTableFile, vinTableFile);
//
//        effDateForControlTable = TimeSetterUtil.getInstance().getCurrentTime().plusDays(3);
//        expDateForControlTable = TimeSetterUtil.getInstance().getCurrentTime().plusDays(2);
//
//        insertDatesForControlTable(effDateForControlTable, expDateForControlTable);

        mainApp().open();
        createCustomerIndividual();

        TestData testDataTwoVehicles = getTestDataWithTwoVehicles(getPolicyTD());
        policyNumber = createPolicy(testDataTwoVehicles);

  //     adminApp().open();
 //      new UploadToVINTableTab().uploadVinTable(controlTableFile, vinTableFile);

        preconditionToDoFirstRenewal();
        initiateEndorsement();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

        TestData testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD());

        policy.getDefaultView().fillFromTo(testDataThreeVehicles, VehicleTab.class, PremiumAndCoveragesTab.class,true);

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();

        compareRenewalVersions();
    }

    */
//
//    //TODO - to be removed after refactoring / removing pas14532_MakeRefreshCorrectForRenewalTerm
//    private  void preconditionToDoFirstRenewal(){
//        policyExpirationDate = PolicySummaryPage.getExpirationDate();
//        LocalDateTime renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
//        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
//        HttpStub.executeAllBatches();
//        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
//    }
//
//    //TODO - to be removed
//    private void insertDatesForControlTable(LocalDateTime effDateForControlTable, LocalDateTime expDateForControlTable) {
//        String formattedEffDateForControlTable = effDateForControlTable.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        String formattedExpDateForControlTable = expDateForControlTable.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        DBService.get().executeUpdate(String.format(VehicleQueries.INSERT_EFF_DATE_INTO_CONTROL_TABLE, formattedEffDateForControlTable));
//        DBService.get().executeUpdate(String.format(VehicleQueries.INSERT_EXP_DATE_INTO_CONTROL_TABLE, formattedExpDateForControlTable));
//    }
//
//    //TODO - to be removed
//    private void compareRenewalVersions() {
//        PolicySummaryPage.buttonRenewalQuoteVersion.click();
//        PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).click();
//        PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).click();
//        PolicySummaryPage.buttonCompare.click();
//    }

 }
