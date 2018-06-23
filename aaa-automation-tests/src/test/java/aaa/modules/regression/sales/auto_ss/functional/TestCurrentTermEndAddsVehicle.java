package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

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

    /**
     * * @author Sarunas Jaraminas
     *
     * @name Current Term End Adds Vehicle:
     * Make refresh correct for current and renewal terms
     *
     * @scenario
     *1. Create customer.
     *2. Create Auto SS Quote.
     *
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14532")
    public void pas14532_MakeRefreshCorrectForRenewalTerm(@Optional("AZ") String state) {

//        VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
//
//        String vinTableFile = "multipleVinTable.xlsx";
//        String controlTableFile = "controlTable_AZ_SS.xlsx";
//
//        adminApp().open();
//        vinMethods.uploadFiles(controlTableFile, vinTableFile);
//
//        effDateForControlTable = TimeSetterUtil.getInstance().getCurrentTime().plusDays(3);
//        expDateForControlTable = TimeSetterUtil.getInstance().getCurrentTime().plusDays(2);
//
//        insertDatesForControlTable(effDateForControlTable, expDateForControlTable);

        mainApp().open();
        createCustomerIndividual();

        TestData testDataTwoVehicles = getTestDataWithTwoVehicles(getPolicyTD());
        policyNumber = createPolicy(testDataTwoVehicles);

        preconditionToDoFirstRenewal();
        initiateEndorsement();

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());

        TestData testDataThreeVehicles = getTestDataWithThreeVehicles(getPolicyTD());

        policy.getDefaultView().fillFromTo(testDataThreeVehicles, VehicleTab.class, PremiumAndCoveragesTab.class,true);

        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.submitTab();

        compareRenewalVersions();
    }

    //FOR FIRST POLICY - ADD TWO VECHICLES
    public TestData getTestDataWithTwoVehicles(TestData testData) {
        TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), "KNDJT2A2XA7038383");

        TestData secondVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), "JT2AE91A7M3425407");

        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
        testDataVehicleTab.add(firstVehicle);
        testDataVehicleTab.add(secondVehicle);

        // add 2 vehicles
        return testData
                .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();
    }

//FOR ENDORSEMENT - UPDATE SECOND VEHICLE AND ADD THIRD
        public TestData getTestDataWithThreeVehicles(TestData testData2) {
        TestData firstVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                    .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), "KNDJT2A2XA7038383");

        TestData updateSecondVehicle = modifyVehicleTabNonExistingVin(getPolicyTD()).getTestData("VehicleTab");

        TestData thirdVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
                    .adjust(AutoSSMetaData.VehicleTab.VIN.getLabel(), "1FTRE1421YHA89455");

        // Build Vehicle Tab old version vin + updated vehicle
        List<TestData> testDataVehicleTab = new ArrayList<>();
            testDataVehicleTab.add(firstVehicle);
            testDataVehicleTab.add(updateSecondVehicle);
            testDataVehicleTab.add(thirdVehicle);

        // add 2 vehicles
        return testData2
                .adjust(vehicleTab.getMetaKey(), testDataVehicleTab).resolveLinks();
    }

    public TestData modifyVehicleTabNonExistingVin(TestData testData2) {
        testData2
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "ZZXKN3DD2E0344466")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), "2009")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), "HYUNDI")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), "SONAT")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), "SONAT SE/LIMITED")
                .adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), "SEDAN 4 DOOR");
        return testData2;
    }

    private void insertDatesForControlTable(LocalDateTime effDateForControlTable, LocalDateTime expDateForControlTable) {
        String formattedEffDateForControlTable = effDateForControlTable.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedExpDateForControlTable = expDateForControlTable.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        DBService.get().executeUpdate(String.format(VehicleQueries.INSERT_EFF_DATE_INTO_CONTROL_TABLE, formattedEffDateForControlTable));
        DBService.get().executeUpdate(String.format(VehicleQueries.INSERT_EXP_DATE_INTO_CONTROL_TABLE, formattedExpDateForControlTable));
    }

    private  void preconditionToDoFirstRenewal(){
        policyExpirationDate = PolicySummaryPage.getExpirationDate();
        LocalDateTime renewImageGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
        HttpStub.executeAllBatches();
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
    }

    private void initiateEndorsement() {
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        policy.createEndorsement(getPolicyTD("Endorsement", "TestData"));
    }

    private void compareRenewalVersions() {
        PolicySummaryPage.buttonRenewalQuoteVersion.click();
        PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).click();
        PolicySummaryPage.tableTransactionHistory.getRow(2).getCell(1).click();
        PolicySummaryPage.buttonCompare.click();
    }
}