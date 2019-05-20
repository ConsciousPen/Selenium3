package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.CoverageInfo;
import aaa.main.enums.CoverageLimits;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class TestMiniServicesCoveragesHelperCA extends TestMiniServicesCoveragesHelper {

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

	protected void pas15412_viewCAPolicyLevelCoveragesBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		Coverage covBIExpected = Coverage.create(CoverageInfo.BI_CA);
		Coverage covPDExpected = Coverage.create(CoverageInfo.PD_CA);
		if (TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().isAfter(LocalDate.parse("2019-06-07"))) { //Starting from 2019-06-08, PD should not have availableLimit 5000
			covPDExpected.removeAvailableLimit(CoverageLimits.COV_5000);
		}
		Coverage covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).removeAvailableLimitsAbove(CoverageLimits.COV_500500);
		Coverage covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).disableCanChange();
		Coverage covMEDPMExpected = Coverage.create(CoverageInfo.MEDPM_CA);

		Coverage covBIActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.BI_CA.getCode());
		Coverage covPDActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.PD_CA.getCode());
		Coverage covUMBIActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.UMBI_CA.getCode());
		Coverage covUIMBIActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.UIMBI_CA.getCode());
		Coverage covMEDPMActual = findCoverage(viewEndorsementCoverages.policyCoverages, CoverageInfo.MEDPM_CA.getCode());

		assertThat(covBIActual).isEqualTo(covBIExpected);
		assertThat(covPDActual).isEqualTo(covPDExpected);
		assertThat(covUMBIActual).isEqualTo(covUMBIExpected);
		assertThat(covUIMBIActual).isEqualToIgnoringGivenFields(covUIMBIExpected, "availableLimits");//don't care about available limits as UIMBI is not changable
		assertThat(covMEDPMActual).isEqualTo(covMEDPMExpected);

	}

	protected void pas28579_updatePolicyLevelCoveragesCABody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Update BI to lower limit
		CoverageLimits biNewLimit = CoverageLimits.COV_1530;
		Coverage covBIExpected = Coverage.create(CoverageInfo.BI_CA).changeLimit(biNewLimit);
		Coverage covPDExpected = Coverage.create(CoverageInfo.PD_CA);//the same as at NB
		Coverage covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit);
		Coverage covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		Coverage covMEDPMExpected = Coverage.create(CoverageInfo.MEDPM_CA);//the same as at NB
		updateCoverageAndCheck(policyNumber, covBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update BI to higher limit
		biNewLimit = CoverageLimits.COV_5001000;
		covBIExpected = Coverage.create(CoverageInfo.BI_CA).changeLimit(biNewLimit);
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit);
		covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		updateCoverageAndCheck(policyNumber, covBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update PD to higher limit
		covPDExpected = Coverage.create(CoverageInfo.PD_CA).changeLimit(CoverageLimits.COV_1000000);
		updateCoverageAndCheck(policyNumber, covPDExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update PD to lower limit
		covPDExpected = Coverage.create(CoverageInfo.PD_CA).changeLimit(CoverageLimits.COV_10000);
		updateCoverageAndCheck(policyNumber, covPDExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update MEDPM to higher limit
		covMEDPMExpected = Coverage.create(CoverageInfo.MEDPM_CA).changeLimit(CoverageLimits.COV_25000);
		updateCoverageAndCheck(policyNumber, covMEDPMExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update MEDPM to lower limit
		covMEDPMExpected = Coverage.create(CoverageInfo.MEDPM_CA).changeLimit(CoverageLimits.COV_0);
		updateCoverageAndCheck(policyNumber, covMEDPMExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update MEDPM to other limit than at NB for bind
		covMEDPMExpected = Coverage.create(CoverageInfo.MEDPM_CA).changeLimit(CoverageLimits.COV_1000);
		updateCoverageAndCheck(policyNumber, covMEDPMExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update UM to lower limit
		CoverageLimits newUMBILimit = CoverageLimits.COV_1530;
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit);
		covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		updateCoverageAndCheck(policyNumber, covUMBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update UM to higher limit
		newUMBILimit = CoverageLimits.COV_3060;
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit);
		covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		updateCoverageAndCheck(policyNumber, covUMBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update BI to lower limit and see what happens
		biNewLimit = CoverageLimits.COV_50100;
		covBIExpected = Coverage.create(CoverageInfo.BI_CA).changeLimit(biNewLimit);
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit);
		covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		updateCoverageAndCheck(policyNumber, covBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas15423_rideSharingCoverageCABody() {
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyDefaultTD();
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestDataList("VehicleTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData_VehicleOtherTypes").getTestData("AssignmentTab")).resolveLinks();
		String policyNumber = createPolicy(testData);

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		PolicyCoverageInfo policyEndorsementCoverageInfo1 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, policyEndorsementCoverageInfo1);
		List<Vehicle> vehicles1 = HelperCommon.viewEndorsementVehicles(policyNumber).vehicleList;
		verifyRideShareCoverage(policyEndorsementCoverageInfo1, vehicles1);

		helperMiniServices.addVehicleWithChecks(policyNumber, "2015-02-11", "9BWFL61J244023215", true);

		PolicyCoverageInfo policyEndorsementCoverageInfo2 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		List<Vehicle> vehicles2 = HelperCommon.viewEndorsementVehicles(policyNumber).vehicleList;
		verifyRideShareCoverage(policyEndorsementCoverageInfo2, vehicles2);

		helperMiniServices.endorsementRateAndBind(policyNumber);

	}

	protected void pas26668_viewVehicleLevelCoveragesCABody() {
		TestData td = getPolicyDefaultTD();
		TestData tdError = DataProviderFactory.dataOf(ErrorTab.KEY_ERRORS, "All");
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_VehicleLevelCoverages").getTestDataList("VehicleTab"))
				.adjust(AutoCaMetaData.ErrorTab.class.getSimpleName(), tdError)
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData_VehicleLevelCoverages").getTestData("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_VehicleLevelCoverages").getTestData("PremiumAndCoveragesTab")).resolveLinks();
		//mainApp().open();//TODO-mstrazds:
		String policyNumber = openAppAndCreatePolicy(testData);
		//SearchPage.openPolicy(policyNumber);//TODO-mstrazds:
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		//Expected coverages Vehicle 1 (the same as in PAS UI)
		Coverage covCOMPDEDExpected1 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covGLASSxpected1 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_FALSE);
		Coverage covCOLLDEDDExpected1 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covETEExpected1 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected1 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();
		Coverage covLOANExpected1 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();
		;
		Coverage covNEWCARExpected1 = Coverage.create(CoverageInfo.NEWCAR).changeLimit(CoverageLimits.COV_0);
		Coverage covRIDESHAREExpected1 = Coverage.create(CoverageInfo.RIDESHARE_CA);

		List<Coverage> expectedCoveragesVeh1 = new ArrayList<>();
		expectedCoveragesVeh1.add(covCOMPDEDExpected1);
		expectedCoveragesVeh1.add(covGLASSxpected1);
		expectedCoveragesVeh1.add(covCOLLDEDDExpected1);
		expectedCoveragesVeh1.add(covETEExpected1);
		expectedCoveragesVeh1.add(covALLRISKExpected1);
		expectedCoveragesVeh1.add(covLOANExpected1);
		expectedCoveragesVeh1.add(covNEWCARExpected1);
		expectedCoveragesVeh1.add(covRIDESHAREExpected1);

		//Expected coverages Vehicle 2 (the same as in PAS UI)
		Coverage covCOMPDEDExpected2 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covGLASSxpected2 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_TRUE);
		Coverage covCOLLDEDDExpected2 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covETEXxpected2 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected2 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();
		Coverage covLOANExpected2 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_1).disableCanChange();
		Coverage covNEWCARExpected2 = Coverage.create(CoverageInfo.NEWCAR).changeLimit(CoverageLimits.COV_0);
		Coverage covRIDESHAREExpected2 = Coverage.create(CoverageInfo.RIDESHARE_CA).changeLimit(CoverageLimits.COV_0);

		List<Coverage> expectedCoveragesVeh2 = new ArrayList<>();
		expectedCoveragesVeh2.add(covCOMPDEDExpected2);
		expectedCoveragesVeh2.add(covGLASSxpected2);
		expectedCoveragesVeh2.add(covCOLLDEDDExpected2);
		expectedCoveragesVeh2.add(covETEXxpected2);
		expectedCoveragesVeh2.add(covALLRISKExpected2);
		expectedCoveragesVeh2.add(covLOANExpected2);
		expectedCoveragesVeh2.add(covNEWCARExpected2);
		expectedCoveragesVeh2.add(covRIDESHAREExpected2);

		//Expected coverages Vehicle 3 (the same as in PAS UI)
		Coverage covCOMPDEDExpected3 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covGLASSxpected3 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_TRUE);
		Coverage covCOLLDEDDExpected3 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covETEXExpected3 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected3 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();
		;
		Coverage covLOANExpected3 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();
		;
		Coverage covNEWCARExpected3 = Coverage.create(CoverageInfo.NEWCAR).changeLimit(CoverageLimits.COV_TRUE);
		Coverage covRIDESHAREExpected3 = Coverage.create(CoverageInfo.RIDESHARE_CA).changeLimit(CoverageLimits.COV_0);

		List<Coverage> expectedCoveragesVeh3 = new ArrayList<>();
		expectedCoveragesVeh3.add(covCOMPDEDExpected3);
		expectedCoveragesVeh3.add(covGLASSxpected3);
		expectedCoveragesVeh3.add(covCOLLDEDDExpected3);
		expectedCoveragesVeh3.add(covETEXExpected3);
		expectedCoveragesVeh3.add(covALLRISKExpected3);
		expectedCoveragesVeh3.add(covLOANExpected3);
		expectedCoveragesVeh3.add(covNEWCARExpected3);
		expectedCoveragesVeh3.add(covRIDESHAREExpected3);

		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		Vehicle vehicle1 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse, testData.getTestDataList("VehicleTab").get(0).getValue("VIN"));
		Vehicle vehicle2 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse, testData.getTestDataList("VehicleTab").get(1).getValue("VIN"));
		Vehicle vehicle3 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse, testData.getTestDataList("VehicleTab").get(2).getValue("VIN"));

		VehicleCoverageInfo veh1Coverages = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages, vehicle1.oid);
		VehicleCoverageInfo veh2Coverages = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages, vehicle2.oid);
		VehicleCoverageInfo veh3Coverages = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages, vehicle3.oid);

		//Check coverages
		checkCoverages_pasXXX(expectedCoveragesVeh1, veh1Coverages);
		checkCoverages_pasXXX(expectedCoveragesVeh2, veh2Coverages);
		checkCoverages_pasXXX(expectedCoveragesVeh3, veh3Coverages);

		//Add vehicle
		String newVin = "1FMCU9GD5JUB71878";
		String newVehicleOid = helperMiniServices.addVehicleWithChecks(policyNumber, "2015-02-11", newVin, true);// 2018 Ford Escape

		//Expected coverages Newly added Vehicle
		Coverage covCOMPDEDExpected4 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covGLASSxpected4 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_TRUE);
		Coverage covCOLLDEDDExpected4 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covETEXExpected4 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected4 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();
		;
		Coverage covLOANExpected4 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();
		;
		Coverage covNEWCARExpected4 = Coverage.create(CoverageInfo.NEWCAR).changeLimit(CoverageLimits.COV_TRUE);
		Coverage covRIDESHAREExpected4 = Coverage.create(CoverageInfo.RIDESHARE_CA).changeLimit(CoverageLimits.COV_0);

		List<Coverage> expectedCoveragesVeh4 = new ArrayList<>();
		expectedCoveragesVeh4.add(covCOMPDEDExpected4);
		expectedCoveragesVeh4.add(covGLASSxpected4);
		expectedCoveragesVeh4.add(covCOLLDEDDExpected4);
		expectedCoveragesVeh4.add(covETEXExpected4);
		expectedCoveragesVeh4.add(covALLRISKExpected4);
		expectedCoveragesVeh4.add(covLOANExpected4);
		expectedCoveragesVeh4.add(covNEWCARExpected4);
		expectedCoveragesVeh4.add(covRIDESHAREExpected4);

		PolicyCoverageInfo viewEndorsementCoverages2 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo veh4Coverages = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages2, newVehicleOid);

		//Check coverages
		checkCoverages_pasXXX(expectedCoveragesVeh1, veh1Coverages);
		checkCoverages_pasXXX(expectedCoveragesVeh2, veh2Coverages);
		checkCoverages_pasXXX(expectedCoveragesVeh3, veh3Coverages);
		checkCoverages_pasXXX(expectedCoveragesVeh4, veh4Coverages);

	}

	private void checkCoverages_pasXXX(List<Coverage> expectedCoverageList, VehicleCoverageInfo actualCoverageList) {
		for (Coverage expectedCoverage : expectedCoverageList) {
			Coverage actualCoverage = findCoverage(actualCoverageList.coverages, expectedCoverage.getCoverageCd());
			assertThat(expectedCoverage).isEqualTo(actualCoverage);
		}
	}

	private void verifyRideShareCoverage(PolicyCoverageInfo policyEndorsementCoverageInfo, List<Vehicle> vehicles) {
		List<Vehicle> notRegularVehicles = vehicles.stream().filter(vehicle -> !"Regular".equals(vehicle.vehTypeCd) && !"Antique".equals(vehicle.vehTypeCd)).collect(Collectors.toList());
		List<Vehicle> regularVehicles = vehicles.stream().filter(vehicle -> "Regular".equals(vehicle.vehTypeCd) || "Antique".equals(vehicle.vehTypeCd)).collect(Collectors.toList());

		//Verify that non-Regular vehicles don't have RideSharing Coverage
		for (Vehicle vehicle : notRegularVehicles) {
			printToLog("Checking other than Regular/Antique vehicle " + vehicle.model + " " + vehicle.manufacturer + " " + vehicle.modelYear + " (" + vehicle.oid + ")");
			List<Coverage> vehicleCoverages = findVehicleCoverages(policyEndorsementCoverageInfo, vehicle.oid).coverages;
			assertSoftly(softly -> {
				softly.assertThat(findCoverage(vehicleCoverages, CoverageInfo.RIDESHARE_CA.getCode(), false))
						.as("Ride Sharing coverage is expected only for Regular and Antique vehicles").isEqualTo(null);
			});
		}

		//Verify that Regular vehicles have RideSharing Coverage
		for (Vehicle vehicle : regularVehicles) {
			printToLog("Checking Regular/Antique vehicle " + vehicle.model + " " + vehicle.manufacturer + " " + vehicle.modelYear + " (" + vehicle.oid + ")");
			List<Coverage> vehicleCoverages = findVehicleCoverages(policyEndorsementCoverageInfo, vehicle.oid).coverages;
			Coverage covRideShareActual = findCoverage(vehicleCoverages, CoverageInfo.RIDESHARE_CA.getCode());
			Coverage covRideShareExpected = Coverage.create(CoverageInfo.RIDESHARE_CA).disableCanChange();
			assertSoftly(softly -> {
				softly.assertThat(covRideShareActual).isEqualTo(covRideShareExpected);
			});
		}
	}
}
