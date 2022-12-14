package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableList;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.AvailableCoverageLimits;
import aaa.main.enums.CoverageInfo;
import aaa.main.enums.CoverageLimits;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestMiniServicesCoveragesHelperCA extends TestMiniServicesCoveragesHelper {

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();

	protected void pas15412_viewCAPolicyLevelCoveragesBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		Coverage covBIExpected = Coverage.create(CoverageInfo.BI_CA);
		Coverage covPDExpected = Coverage.create(CoverageInfo.PD_CA);//Note: Starting from 2019-06-08, PD should not have availableLimit 5000
		Coverage covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).removeAvailableLimitsAbove(CoverageLimits.COV_500500).addAvailableLimits(CoverageLimits.COV_00);//adding No Cov at the end of the list as currently expected
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
		Coverage covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit).addAvailableLimits(CoverageLimits.COV_00);//adding No Cov at the end of the list as currently expected
		Coverage covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		Coverage covMEDPMExpected = Coverage.create(CoverageInfo.MEDPM_CA);//the same as at NB
		updateCoverageAndCheck(policyNumber, covBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update BI to higher limit
		biNewLimit = CoverageLimits.COV_5001000;
		covBIExpected = Coverage.create(CoverageInfo.BI_CA).changeLimit(biNewLimit);
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit).addAvailableLimits(CoverageLimits.COV_00);//adding No Cov at the end of the list as currently expected
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
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit).addAvailableLimits(CoverageLimits.COV_00);//adding No Cov at the end of the list as currently expected
		covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		updateCoverageAndCheck(policyNumber, covUMBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update UM to No Coverage
		newUMBILimit = CoverageLimits.COV_00;
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit).addAvailableLimits(CoverageLimits.COV_00);//adding No Cov at the end of the list as currently expected
		covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		updateCoverageAndCheck(policyNumber, covUMBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update UM to higher limit
		newUMBILimit = CoverageLimits.COV_3060;
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit).addAvailableLimits(CoverageLimits.COV_00);//adding No Cov at the end of the list as currently expected
		covUIMBIExpected = Coverage.create(CoverageInfo.UIMBI_CA).changeLimit(newUMBILimit).removeAvailableLimitsAbove(biNewLimit).disableCanChange();
		updateCoverageAndCheck(policyNumber, covUMBIExpected, covBIExpected, covPDExpected, covUMBIExpected, covUIMBIExpected, covMEDPMExpected);

		//Update BI to lower limit and see what happens
		biNewLimit = CoverageLimits.COV_50100;
		covBIExpected = Coverage.create(CoverageInfo.BI_CA).changeLimit(biNewLimit);
		covUMBIExpected = Coverage.create(CoverageInfo.UMBI_CA).changeLimit(biNewLimit).removeAvailableLimitsAbove(biNewLimit).addAvailableLimits(CoverageLimits.COV_00);//adding No Cov at the end of the list as currently expected
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
				.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_2Drivers").getTestDataList("DriverTab"))
				.adjust(AutoCaMetaData.ErrorTab.class.getSimpleName(), tdError)
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData_VehicleLevelCoverages").getTestData("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_VehicleLevelCoverages").getTestData("PremiumAndCoveragesTab")).resolveLinks();
		String policyNumber = openAppAndCreatePolicy(testData);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		//Expected coverages Vehicle 1 (the same as in PAS UI)
		Coverage covCOMPDEDExpected1 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covGLASSxpected1 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_0);
		Coverage covCOLLDEDDExpected1 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covETEExpected1 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected1 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_NO_COV).disableCanChange();
		Coverage covLOANExpected1 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_0).disableCanChange().disableCustomerDisplay();
		Coverage covNEWCARExpected1 = Coverage.create(CoverageInfo.NEWCAR_CA).changeLimit(CoverageLimits.COV_0).disableCustomerDisplay().disableCanChange();
		Coverage covRIDESHAREExpected1 = Coverage.create(CoverageInfo.RIDESHARE_CA).disableCanChange();
		Coverage covOEMExpected1 = Coverage.create(CoverageInfo.OEM_CA).changeLimit(CoverageLimits.COV_0);

		List<Coverage> expectedCoveragesVeh1 = new ArrayList<>();
		expectedCoveragesVeh1.add(covCOMPDEDExpected1);
		expectedCoveragesVeh1.add(covGLASSxpected1);
		expectedCoveragesVeh1.add(covCOLLDEDDExpected1);
		expectedCoveragesVeh1.add(covETEExpected1);
		expectedCoveragesVeh1.add(covALLRISKExpected1);
		expectedCoveragesVeh1.add(covLOANExpected1);
		expectedCoveragesVeh1.add(covNEWCARExpected1);
		expectedCoveragesVeh1.add(covRIDESHAREExpected1);
		expectedCoveragesVeh1.add(covOEMExpected1);

		//Expected coverages Vehicle 2 (the same as in PAS UI)
		Coverage covCOMPDEDExpected2 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250).removeAvailableLimit(CoverageLimits.COV_NO_COV);// Only Owned Vehicles have No Cov limit available
		Coverage covGLASSxpected2 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_1);
		Coverage covCOLLDEDDExpected2 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500).removeAvailableLimit(CoverageLimits.COV_NO_COV);// Only Owned Vehicles have No Cov limit available
		Coverage covETEXxpected2 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected2 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_NO_COV).disableCanChange();
		Coverage covLOANExpected2 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_1);
		Coverage covNEWCARExpected2 = Coverage.create(CoverageInfo.NEWCAR_CA).changeLimit(CoverageLimits.COV_0).disableCanChange().disableCustomerDisplay();
		Coverage covRIDESHAREExpected2 = Coverage.create(CoverageInfo.RIDESHARE_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();
		Coverage covOEMExpected2 = Coverage.create(CoverageInfo.OEM_CA).changeLimit(CoverageLimits.COV_1);

		List<Coverage> expectedCoveragesVeh2 = new ArrayList<>();
		expectedCoveragesVeh2.add(covCOMPDEDExpected2);
		expectedCoveragesVeh2.add(covGLASSxpected2);
		expectedCoveragesVeh2.add(covCOLLDEDDExpected2);
		expectedCoveragesVeh2.add(covETEXxpected2);
		expectedCoveragesVeh2.add(covALLRISKExpected2);
		expectedCoveragesVeh2.add(covLOANExpected2);
		expectedCoveragesVeh2.add(covNEWCARExpected2);
		expectedCoveragesVeh2.add(covRIDESHAREExpected2);
		expectedCoveragesVeh2.add(covOEMExpected2);

		//Expected coverages Vehicle 3 (the same as in PAS UI)
		Coverage covCOMPDEDExpected3 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covGLASSxpected3 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_1);
		Coverage covCOLLDEDDExpected3 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covETEXExpected3 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected3 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_NO_COV).disableCanChange();
		Coverage covLOANExpected3 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_0).disableCanChange().disableCustomerDisplay();
		Coverage covNEWCARExpected3 = Coverage.create(CoverageInfo.NEWCAR_CA).changeLimit(CoverageLimits.COV_1).disableCanChange();
		Coverage covRIDESHAREExpected3 = Coverage.create(CoverageInfo.RIDESHARE_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();
		Coverage covOEMExpected3 = Coverage.create(CoverageInfo.OEM_CA).changeLimit(CoverageLimits.COV_1);

		List<Coverage> expectedCoveragesVeh3 = new ArrayList<>();
		expectedCoveragesVeh3.add(covCOMPDEDExpected3);
		expectedCoveragesVeh3.add(covGLASSxpected3);
		expectedCoveragesVeh3.add(covCOLLDEDDExpected3);
		expectedCoveragesVeh3.add(covETEXExpected3);
		expectedCoveragesVeh3.add(covALLRISKExpected3);
		expectedCoveragesVeh3.add(covLOANExpected3);
		expectedCoveragesVeh3.add(covNEWCARExpected3);
		expectedCoveragesVeh3.add(covRIDESHAREExpected3);
		expectedCoveragesVeh3.add(covOEMExpected3);

		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		Vehicle vehicle1 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse, testData.getTestDataList("VehicleTab").get(0).getValue("VIN"));
		Vehicle vehicle2 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse, testData.getTestDataList("VehicleTab").get(1).getValue("VIN"));
		Vehicle vehicle3 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse, testData.getTestDataList("VehicleTab").get(2).getValue("VIN"));

		VehicleCoverageInfo veh1Coverages = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages, vehicle1.oid);
		VehicleCoverageInfo veh2Coverages = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages, vehicle2.oid);
		VehicleCoverageInfo veh3Coverages = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages, vehicle3.oid);

		//Check coverages
		checkCoverages_pas26668(expectedCoveragesVeh1, veh1Coverages);//etec
		checkCoverages_pas26668(expectedCoveragesVeh2, veh2Coverages);//glass, loan, etec, oem
		checkCoverages_pas26668(expectedCoveragesVeh3, veh3Coverages);//newcar, glass, oem, etec

		//Add vehicle
		String newVin = "1FMCU9GD5JUB71878";
		String newVehicleOid = helperMiniServices.addVehicleWithChecks(policyNumber, "2015-02-11", newVin, true);// 2018 Ford Escape

		//Expected coverages Newly added Vehicle
		Coverage covCOMPDEDExpected4 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covGLASSxpected4 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_0);
		Coverage covCOLLDEDExpected4 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covETEXExpected4 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected4 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_NO_COV).disableCanChange();
		Coverage covLOANExpected4 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_0).disableCanChange().disableCustomerDisplay();
		Coverage covNEWCARExpected4 = Coverage.create(CoverageInfo.NEWCAR_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();
		Coverage covRIDESHAREExpected4 = Coverage.create(CoverageInfo.RIDESHARE_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();

		List<Coverage> expectedCoveragesVeh4 = new ArrayList<>();
		expectedCoveragesVeh4.add(covCOMPDEDExpected4);
		expectedCoveragesVeh4.add(covGLASSxpected4);
		expectedCoveragesVeh4.add(covCOLLDEDExpected4);
		expectedCoveragesVeh4.add(covETEXExpected4);
		expectedCoveragesVeh4.add(covALLRISKExpected4);
		expectedCoveragesVeh4.add(covLOANExpected4);
		expectedCoveragesVeh4.add(covNEWCARExpected4);
		expectedCoveragesVeh4.add(covRIDESHAREExpected4);

		PolicyCoverageInfo viewEndorsementCoverages2 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo veh4Coverages = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages2, newVehicleOid);

		//Check coverages
		checkCoverages_pas26668(expectedCoveragesVeh1, veh1Coverages);
		checkCoverages_pas26668(expectedCoveragesVeh2, veh2Coverages);
		checkCoverages_pas26668(expectedCoveragesVeh3, veh3Coverages);
		checkCoverages_pas26668(expectedCoveragesVeh4, veh4Coverages);

		//Check in PAS UI that Newly added vehicle has all expected coverages
		expectedCoveragesVeh4.remove(covLOANExpected4);//not in UI for this newly added vehicle
		SearchPage.openPolicy(policyNumber);
		verifyCoveragesPASUI_pas26668(expectedCoveragesVeh4);

		//Update to Leased and Check Coverages
		//Update vehicle Leased Financed Info
		String financedOrLeased = getRandomFinancedOrLeased();
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = "Line1";
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = "Line 2";
		updateVehicleLeasedFinanced.vehicleOwnership.city = "LA";
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = "CA";
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = "90201";
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = financedOrLeased;
		updateVehicleLeasedFinanced.vehicleOwnership.name = "John";
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = "Benny";
		VehicleUpdateResponseDto ownershipUpdateResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);
		assertThat(ownershipUpdateResponse.vehicleOwnership.ownership).isEqualTo(financedOrLeased);
		VehicleUpdateDto updateVehicleUsageRequest = new VehicleUpdateDto();
		updateVehicleUsageRequest.distanceOneWayToWork = "15";
		updateVehicleUsageRequest.odometerReading = "32000";
		HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleUsageRequest);

		covCOMPDEDExpected4.removeAvailableLimit(CoverageLimits.COV_NO_COV);
		covCOLLDEDExpected4.removeAvailableLimit(CoverageLimits.COV_NO_COV);
		covLOANExpected4.enableCustomerDisplay().enableCanChange();
		covNEWCARExpected4.disableCustomerDisplay();
		expectedCoveragesVeh4.add(covLOANExpected4);
		expectedCoveragesVeh4.remove(covNEWCARExpected4);//not in UI for Leased vehicle
		PolicyCoverageInfo viewEndorsementCoverages3 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo veh4CoveragesLeased = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages3, newVehicleOid);
		checkCoverages_pas26668(expectedCoveragesVeh4, veh4CoveragesLeased);
		verifyCoveragesPASUI_pas26668(expectedCoveragesVeh4);

		//replace vehicle and check coverages
		replaceVehicleAndCheck_pas29261(policyNumber, "1FADP3F27JL304472", covCOMPDEDExpected2, covGLASSxpected2, covCOLLDEDDExpected2, covLOANExpected2, covNEWCARExpected2, covOEMExpected2, expectedCoveragesVeh2, vehicle2, true);
		Vehicle vehicleNotKeptAssignments = replaceVehicleAndCheck_pas29261(policyNumber, "1C4RJEAG4JC253223", covCOMPDEDExpected3, covGLASSxpected3, covCOLLDEDDExpected3, covLOANExpected3, covNEWCARExpected3, covOEMExpected3, expectedCoveragesVeh3, vehicle3, false);

		//check assignments (PAS-15405)
		ViewDriverAssignmentResponse viewDriverAssignmentResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
		assertThat(viewDriverAssignmentResponse.unassignedVehicles).containsExactlyInAnyOrder(vehicleNotKeptAssignments.oid, newVehicleOid);
		assertThat(viewDriverAssignmentResponse.driverVehicleAssignments.size()).isEqualTo(2);//Totally 4 Vehicles, Replaced with not kept assignments amd Newly added don't have assignments
		assertThat(viewDriverAssignmentResponse.assignableDrivers.size()).isEqualTo(2);
		assertThat(viewDriverAssignmentResponse.assignableVehicles.size()).isEqualTo(4);

		//Assign drivers and Calculate premium - just in case
		for (String unassignedVehicle : viewDriverAssignmentResponse.unassignedVehicles) {
			HelperCommon.updateDriverAssignment(policyNumber, unassignedVehicle, ImmutableList.of(viewDriverAssignmentResponse.assignableDrivers.stream().findFirst().orElse(null)));
		}

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
	}

	private Vehicle replaceVehicleAndCheck_pas29261(String policyNumber, String vin, Coverage covCOMPDEDExpected, Coverage covGLASSExpected, Coverage covCOLLDEDDExpected, Coverage covLOANExpected, Coverage covNEWCARExpected, Coverage covOEMExpected, List<Coverage> expectedCoveragesVeh, Vehicle vehicle, boolean keepAssignments) {
		ReplaceVehicleRequest replaceVehicleRequest = DXPRequestFactory.createReplaceVehicleRequest(vin, "2013-03-31", keepAssignments, true);//Ford Focus
		Vehicle replaceVehicleResponse = HelperCommon.replaceVehicle(policyNumber, vehicle.oid, replaceVehicleRequest, Vehicle.class, 200);
		helperMiniServices.updateVehicleUsageRegisteredOwner(policyNumber, replaceVehicleResponse.oid);
		PolicyCoverageInfo vehReplacedCoverages = HelperCommon.viewEndorsementCoveragesByVehicle(policyNumber, replaceVehicleResponse.oid, PolicyCoverageInfo.class);
		expectedCoveragesVeh.remove(covLOANExpected);
		covCOMPDEDExpected.changeAvailableLimits(AvailableCoverageLimits.COMPDED_CA);
		covCOLLDEDDExpected.changeAvailableLimits(AvailableCoverageLimits.COLLDED_CA);//expecting all available limits including No Coverage as vehicle is now Owned
		covOEMExpected.changeLimit(CoverageLimits.COV_0);
		covNEWCARExpected.enableCustomerDisplay().changeLimit(CoverageLimits.COV_0);
		checkCoverages_pas26668(expectedCoveragesVeh, vehReplacedCoverages.vehicleLevelCoverages.get(0)); //BUG: PAS-30473 GLASS coverage is not retained when replacing vehicle
		return replaceVehicleResponse;
	}

	protected void pas15424_viewUpdateOEMCoverageCATC01Body() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		Coverage covCOMPDEDExpected = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covCOLLDEDExpected = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covOEMExpected = Coverage.create(CoverageInfo.OEM_CA);
		Coverage covETECExpected = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Vehicle vehicle = HelperCommon.viewEndorsementVehicles(policyNumber).vehicleList.get(0);
		VehicleCoverageInfo veh1Coverages = findVehicleCoverages(viewEndorsementCoverages, vehicle.oid);
		Coverage covOEMActual = findCoverage(veh1Coverages.coverages, CoverageInfo.OEM_CA.getCode());
		Coverage covETECActual = findCoverage(veh1Coverages.coverages, CoverageInfo.ETEC_CA.getCode());
		assertThat(covOEMActual).isEqualTo(covOEMExpected);
		assertThat(covETECActual).isEqualTo(covETECExpected);

		//Add vehicle
		String newVin = "1FMCU9GD5JUB71878"; // 2018 Ford Escape (less than 10y old)
		String newVehicleOid = helperMiniServices.addVehicleWithChecks(policyNumber, "2015-02-11", newVin, true);
		viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo veh2Coverages = findVehicleCoverages(viewEndorsementCoverages, newVehicleOid);
		Coverage covCOMPDEDActualVeh2 = findCoverage(veh2Coverages.coverages, CoverageInfo.COMPDED_CA.getCode());
		Coverage covCOLLDEDActualVeh2 = findCoverage(veh2Coverages.coverages, CoverageInfo.COLLDED_CA.getCode());
		Coverage covOEMActualVeh2 = findCoverage(veh2Coverages.coverages, CoverageInfo.OEM_CA.getCode());
		Coverage covETECActualVeh2 = findCoverage(veh2Coverages.coverages, CoverageInfo.ETEC_CA.getCode());
		assertThat(covCOMPDEDActualVeh2).isEqualTo(covCOMPDEDExpected);
		assertThat(covCOLLDEDActualVeh2).isEqualTo(covCOLLDEDExpected);
		Coverage covOEMExpectedVeh2 = Coverage.create(CoverageInfo.OEM_CA).changeLimit(CoverageLimits.COV_0);
		Coverage covETECExpectedVeh2 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);//Defaulted to $25/$750 and all available limits are included as all active vehicles has ETEC $25/$750
		assertThat(covOEMActualVeh2).isEqualTo(covOEMExpectedVeh2);
		assertThat(covETECActualVeh2).isEqualTo(covETECExpectedVeh2);

		//Apply OEM
		covOEMExpected = Coverage.create(CoverageInfo.OEM_CA).changeLimit(CoverageLimits.COV_1);
		updateVehLevelCoverageAndCheckResponses(policyNumber, newVehicleOid, covOEMExpected, covOEMExpected, covCOMPDEDExpected, covCOLLDEDExpected);

		//Update COLLDED to 0
		covOEMExpected.disableCustomerDisplay().disableCanChange().changeLimit(CoverageLimits.COV_0);
		covCOLLDEDExpected.changeLimit(CoverageLimits.COV_NO_COV);
		covOEMExpectedVeh2.disableCanChange().disableCustomerDisplay();
		updateVehLevelCoverageAndCheckResponses(policyNumber, newVehicleOid, covCOLLDEDExpected, covCOLLDEDExpected, covCOMPDEDExpected, covOEMExpected);

		//Update COLLDED back to other than 0
		covOEMExpected.enableCustomerDisplay().enableCanChange();
		covCOLLDEDExpected.changeLimit(CoverageLimits.COV_250);
		covOEMExpectedVeh2.enableCanChange().enableCustomerDisplay();
		updateVehLevelCoverageAndCheckResponses(policyNumber, newVehicleOid, covCOLLDEDExpected, covCOLLDEDExpected, covCOMPDEDExpected, covOEMExpected);

		//Update COMPDED 0
		covOEMExpected.disableCustomerDisplay().disableCanChange();
		covCOMPDEDExpected.changeLimit(CoverageLimits.COV_NO_COV);
		covOEMExpectedVeh2.disableCustomerDisplay().disableCanChange();
		updateVehLevelCoverageAndCheckResponses(policyNumber, newVehicleOid, covCOMPDEDExpected, covCOMPDEDExpected, covOEMExpected);

		//Update COLLDED also to 0 (Tests also PAS-30268 COLLDED should have canChange=true also when COMPDED is No Coverage)
		covCOLLDEDExpected.changeLimit(CoverageLimits.COV_NO_COV);
		covOEMExpectedVeh2.disableCanChange().disableCustomerDisplay();
		updateVehLevelCoverageAndCheckResponses(policyNumber, newVehicleOid, covCOLLDEDExpected, covCOLLDEDExpected, covCOMPDEDExpected, covOEMExpected);

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas15424_viewUpdateOEMCoverageVehOlderThan10yCATC02Body() {
		TestData td = getPolicyDefaultTD();
		TestData testData = td.adjust(TestData.makeKeyPath(VehicleTab.class.getSimpleName(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "5GAER23728J239640")//2008 Buick Enclave (more than 10y old)
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssingmentTab_1Veh"))
				.resolveLinks();

		String policyNumber = openAppAndCreatePolicy(testData);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);
		String vinExistingVehicle = HelperCommon.viewEndorsementVehicles(policyNumber).vehicleList.get(0).oid;
		verifyOEMTC02_pas15424(policyNumber, vinExistingVehicle);

		//Add vehicle more than 10y old
		String newVehicleOid = helperMiniServices.addVehicleWithChecks(policyNumber, "2015-02-11", "1FAHP24W98G151839", true);//2008 Ford Taurus (more than 10y old)
		verifyOEMTC02_pas15424(policyNumber, newVehicleOid);
		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	private void verifyOEMTC02_pas15424(String policyNumber, String vehOid) {
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo vehicleCoverages = findVehicleCoverages(viewEndorsementCoverages, vehOid);
		Coverage covCOMPDED = findCoverage(vehicleCoverages.coverages, CoverageInfo.COMPDED_CA.getCode());
		Coverage covCOLLDED = findCoverage(vehicleCoverages.coverages, CoverageInfo.COLLDED_CA.getCode());
		Coverage covETEC = findCoverage(vehicleCoverages.coverages, CoverageInfo.ETEC_CA.getCode());
		Coverage covOEMActual = findCoverage(vehicleCoverages.coverages, CoverageInfo.OEM_CA.getCode(), false);

		assertThat(covCOMPDED.getCoverageLimit()).as("Precondition: should have COMPDED applied").isNotEqualTo(CoverageLimits.COV_NO_COV.getLimit());
		assertThat(covCOLLDED.getCoverageLimit()).as("Precondition: should have COLLDED applied").isNotEqualTo(CoverageLimits.COV_NO_COV.getLimit());
		assertThat(covOEMActual).isNull();
		assertThat(covOEMActual).isNull();
		Coverage covETECExpected = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		assertThat(covETEC).isEqualTo(covETECExpected);
	}

	protected void pas15424_viewUpdateOEMCoverageNewVehNoCompCollCABody(boolean removeCOMPDED, boolean removeCOLLDED) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		//Add vehicle less than 10y old
		String newVehOid = helperMiniServices.addVehicleWithChecks(policyNumber, "2015-02-11", "1FMCU9GD5JUB71878", true);// 2018 Ford Escape (less than 10y old)
		verifyOEMVehNoCompColl(removeCOMPDED, removeCOLLDED, policyNumber, newVehOid);
		helperMiniServices.endorsementRateAndBind(policyNumber);

	}

		protected void pas15424_viewUpdateOEMCoverageExistingVehicleNoCompCollTC06Body(boolean removeCOMPDED, boolean removeCOLLDED) {
		TestData td = getPolicyDefaultTD();
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_OEMYes").getTestDataList("VehicleTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData_OEMYes").getTestData("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_OEMYes").getTestData("PremiumAndCoveragesTab"))
				.resolveLinks();

		String policyNumber = openAppAndCreatePolicy(testData);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		String vehOid = HelperCommon.viewEndorsementVehicles(policyNumber).vehicleList.get(0).oid;
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		VehicleCoverageInfo vehicleCoverages = findVehicleCoverages(viewEndorsementCoverages, vehOid);
		Coverage covCOMPDED = findCoverage(vehicleCoverages.coverages, CoverageInfo.COMPDED_CA.getCode());
		Coverage covCOLLDED = findCoverage(vehicleCoverages.coverages, CoverageInfo.COLLDED_CA.getCode());
		Coverage covOEMActual = findCoverage(vehicleCoverages.coverages, CoverageInfo.OEM_CA.getCode());
		Coverage covOEMExpected = Coverage.create(CoverageInfo.OEM_CA).changeLimit(CoverageLimits.COV_1);
		Coverage covETECActual = findCoverage(vehicleCoverages.coverages, CoverageInfo.ETEC_CA.getCode());
		Coverage covETECExpected = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);

		assertThat(covCOMPDED.getCoverageLimit()).as("Precondition: should have COMPDED applied").isNotEqualTo(CoverageLimits.COV_NO_COV.getLimit());
		assertThat(covCOLLDED.getCoverageLimit()).as("Precondition: should have COLLDED applied").isNotEqualTo(CoverageLimits.COV_NO_COV.getLimit());
		assertThat(covOEMActual).isEqualTo(covOEMExpected);
		assertThat(covETECActual).isEqualTo(covETECExpected);

		verifyOEMVehNoCompColl(removeCOMPDED, removeCOLLDED, policyNumber, vehOid);
		helperMiniServices.endorsementRateAndBind(policyNumber);

	}

	protected void pas15424_viewUpdateOEMCoverageCATC09Body() {
		TestData td = getPolicyDefaultTD();
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_LessThan10yOldNoCompColl").getTestDataList("VehicleTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData_LessThan10yOldNoCompColl").getTestData("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_LessThan10yOldNoCompColl").getTestData("PremiumAndCoveragesTab"))
				.resolveLinks();

		String policyNumber = openAppAndCreatePolicy(testData);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		String oidVeh1 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse.vehicleList, testData.getTestDataList("VehicleTab").get(0).getValue("VIN")).oid;//without COMPDED
		String oidVeh2 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse.vehicleList, testData.getTestDataList("VehicleTab").get(1).getValue("VIN")).oid;//without COLLDED
		String oidVeh3 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse.vehicleList, testData.getTestDataList("VehicleTab").get(2).getValue("VIN")).oid;//without COMP and COLL
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		verifyOEMForVehicleTC09_pas15424(oidVeh1, viewEndorsementCoverages);
		verifyOEMForVehicleTC09_pas15424(oidVeh2, viewEndorsementCoverages);
		verifyOEMForVehicleTC09_pas15424(oidVeh3, viewEndorsementCoverages);

		//Apply COMPDED and/or OLLDED and check
		applyCompColl(true, false, policyNumber, oidVeh1);//apply COMPDED
		applyCompColl(false, true, policyNumber, oidVeh2);//apply COLLDED
		applyCompColl(true, true, policyNumber, oidVeh3);//apply COMPDED and COLLDED
		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	private void verifyOEMForVehicleTC09_pas15424(String oidVeh, PolicyCoverageInfo viewEndorsementCoverages) {
		VehicleCoverageInfo vehicleCoverageInfo = findVehicleCoverages(viewEndorsementCoverages, oidVeh);
		Coverage covOEMActualVeh = findCoverage(vehicleCoverageInfo.coverages, CoverageInfo.OEM_CA.getCode());
		Coverage covETECActualVeh = findCoverage(vehicleCoverageInfo.coverages, CoverageInfo.ETEC_CA.getCode());
		Coverage covETECExpected = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_00).disableCanChange();
		assertThat(covOEMActualVeh.getCustomerDisplayed()).as("OEM should not be customerDisplayed if COMPDED and/or COLLDED is not applied").isFalse();
		assertThat(covOEMActualVeh.getCanChangeCoverage()).as("OEM should not be changable if COMPDED and/or COLLDED is not applied").isFalse();
		assertThat(covETECActualVeh).isEqualToIgnoringGivenFields(covETECExpected, "availableLimits");
	}

	private void verifyOEMVehNoCompColl(boolean removeCOMPDED, boolean removeCOLLDED, String policyNumber, String vehOid) {
		PolicyCoverageInfo viewEndorsementCoverages;
		if (removeCOMPDED) {
			//Updated COMPDED to No Coverage
			updateVehicleCoverage(policyNumber, vehOid, CoverageInfo.COMPDED_CA.getCode(), CoverageLimits.COV_NO_COV.getLimit());
		}
		if (removeCOLLDED) {
			//Updated COLLDED to No Coverage (Tests also PAS-30268 COLLDED should have canChange=true also when COMPDED is No Coverage)
			updateVehicleCoverage(policyNumber, vehOid, CoverageInfo.COLLDED_CA.getCode(), CoverageLimits.COV_NO_COV.getLimit());
		}

		viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo newVehicleCoverages = findVehicleCoverages(viewEndorsementCoverages, vehOid);
		Coverage covCOMPDED = findCoverage(newVehicleCoverages.coverages, CoverageInfo.COMPDED_CA.getCode());
		Coverage covCOLLDED = findCoverage(newVehicleCoverages.coverages, CoverageInfo.COLLDED_CA.getCode());
		Coverage covOEMActual = findCoverage(newVehicleCoverages.coverages, CoverageInfo.OEM_CA.getCode());
		Coverage covETECActual = findCoverage(newVehicleCoverages.coverages, CoverageInfo.ETEC_CA.getCode());
		Coverage covETECExpected = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_00).disableCanChange();

		assertThat(covCOMPDED.getCoverageLimit().equals(CoverageLimits.COV_NO_COV.getLimit())).isEqualTo(removeCOMPDED);
		assertThat(covCOLLDED.getCoverageLimit().equals(CoverageLimits.COV_NO_COV.getLimit())).isEqualTo(removeCOLLDED);
		assertThat(covOEMActual.getCustomerDisplayed()).as("OEM should not be customerDisplayed if COMPDED and/or COLLDED is not applied").isFalse();
		assertThat(covOEMActual.getCanChangeCoverage()).as("OEM should not be changable if COMPDED and/or COLLDED is not applied").isFalse();
		assertThat(covETECActual).isEqualToIgnoringGivenFields(covETECExpected, "availableLimits");//don't care about availableLimits as canChange = false
		helperMiniServices.rateEndorsementWithCheck(policyNumber);//just in case

		//Add COMPDED and/or COLLDED back to the vehicle (AC#6). All active vehicles on the policy have Enhanced Transportation Expense = $25/$750
		if (removeCOMPDED) {
			//Updated COMPDED back to other than No Coverage
			updateVehicleCoverage(policyNumber, vehOid, CoverageInfo.COMPDED_CA.getCode(), CoverageLimits.COV_750.getLimit());
		}
		if (removeCOLLDED) {
			//Updated COLLDED back to other than No Coverage
			updateVehicleCoverage(policyNumber, vehOid, CoverageInfo.COLLDED_CA.getCode(), CoverageLimits.COV_1000.getLimit());
		}
		PolicyCoverageInfo viewEndorsementCoverages2 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo newVehicleCoverages2 = findVehicleCoverages(viewEndorsementCoverages2, vehOid);
		Coverage covETECActual2 = findCoverage(newVehicleCoverages2.coverages, CoverageInfo.ETEC_CA.getCode());
		Coverage covETECExpected2 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);//defaulted to 25/750
		assertThat(covETECActual2).isEqualTo(covETECExpected2);
	}

	private void applyCompColl(boolean applyCOMPDED, boolean applyCOLLDED, String policyNumber, String vehOid) {
		PolicyCoverageInfo viewEndorsementCoverages;
		if (applyCOMPDED) {
			//Updated COMPDED to other than No Coverage
			updateVehicleCoverage(policyNumber, vehOid, CoverageInfo.COMPDED_CA.getCode(), CoverageLimits.COV_150.getLimit());
		}
		if (applyCOLLDED) {
			//Updated COLLDED other than No Coverage
			updateVehicleCoverage(policyNumber, vehOid, CoverageInfo.COLLDED_CA.getCode(), CoverageLimits.COV_150.getLimit());
		}
		viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo newVehicleCoverages = findVehicleCoverages(viewEndorsementCoverages, vehOid);
		Coverage covCOMPDED = findCoverage(newVehicleCoverages.coverages, CoverageInfo.COMPDED_CA.getCode());
		Coverage covCOLLDED = findCoverage(newVehicleCoverages.coverages, CoverageInfo.COLLDED_CA.getCode());
		Coverage covOEMActual = findCoverage(newVehicleCoverages.coverages, CoverageInfo.OEM_CA.getCode());
		Coverage covOEMExpected = Coverage.create(CoverageInfo.OEM_CA).changeLimit(CoverageLimits.COV_0);
		Coverage covETECActual = findCoverage(newVehicleCoverages.coverages, CoverageInfo.ETEC_CA.getCode());
		Coverage covETECExpected = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);

		assertThat(covCOMPDED.getCoverageLimit().equals(CoverageLimits.COV_150.getLimit())).isEqualTo(applyCOMPDED);
		assertThat(covCOLLDED.getCoverageLimit().equals(CoverageLimits.COV_150.getLimit())).isEqualTo(applyCOLLDED);
		assertThat(covOEMActual).as("Should have OEM available if COMPDED and COLLDED is applied").isEqualTo(covOEMExpected);
		assertThat(covETECActual).isEqualTo(covETECExpected);

		//Apply OEM
		covOEMExpected.changeLimit(CoverageLimits.COV_1);
		updateVehLevelCoverageAndCheckResponses(policyNumber, vehOid, covOEMExpected, covOEMExpected);
	}

	protected void pas15424_viewUpdateOEMCoverageLessThan10yNoOEMCATC010Body() {
		TestData td = getPolicyDefaultTD();
		TestData testData = td.adjust(TestData.makeKeyPath(VehicleTab.class.getSimpleName(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "1FMCU9GD5JUB71878")// 2018 Ford Escape (less than 10y old)
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssingmentTab_1Veh"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("PremiumAndCoveragesTab_OEMNo"))// no OEM Coverage
				.resolveLinks();

		String policyNumber = openAppAndCreatePolicy(testData);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		VehicleCoverageInfo vehicleCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class).vehicleLevelCoverages.get(0);
		Coverage covCOMPDED = findCoverage(vehicleCoverages.coverages, CoverageInfo.COMPDED_CA.getCode());
		Coverage covCOLLDED = findCoverage(vehicleCoverages.coverages, CoverageInfo.COLLDED_CA.getCode());
		Coverage covOEMActual = findCoverage(vehicleCoverages.coverages, CoverageInfo.OEM_CA.getCode());
		Coverage covOEMExpected = Coverage.create(CoverageInfo.OEM_CA).changeLimit(CoverageLimits.COV_0);

		assertThat(covCOMPDED.getCoverageLimit()).as("Precondition: should have COMPDED applied at NB").isNotEqualTo(CoverageLimits.COV_NO_COV.getLimit());
		assertThat(covCOLLDED.getCoverageLimit()).as("Precondition: should have COLLDED applied at NB").isNotEqualTo(CoverageLimits.COV_NO_COV.getLimit());
		assertThat(covOEMActual).isEqualTo(covOEMExpected);

		//Apply OEM
		covOEMExpected.changeLimit(CoverageLimits.COV_1);
		updateVehLevelCoverageAndCheckResponses(policyNumber, vehicleCoverages.oid, covOEMExpected, covOEMExpected, covCOMPDED, covCOLLDED);
	}

	protected void pas15420_ETECWhenNotAllVehiclesHasDefaultLimit24750Body() {
		TestData td = getPolicyDefaultTD();
		TestData testData = td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("TestData_AllVehiclesETEIsOtherThan25750").getTestDataList("VehicleTab"))
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("TestData_AllVehiclesETEIsOtherThan25750").getTestData("AssignmentTab"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("TestData_AllVehiclesETEIsOtherThan25750").getTestData("PremiumAndCoveragesTab"))
				.resolveLinks();

		String policyNumber = openAppAndCreatePolicy(testData);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		//AC#4
		//Add vehicle
		String newVin = "1FMCU9GD5JUB71878"; // 2018 Ford Escape (less than 10y old)
		String newVehicleOid = helperMiniServices.addVehicleWithChecks(policyNumber, "2015-02-11", newVin, true);
		viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo veh2Coverages = findVehicleCoverages(viewEndorsementCoverages, newVehicleOid);
		Coverage covETECActualVeh2 = findCoverage(veh2Coverages.coverages, CoverageInfo.ETEC_CA.getCode());
		Coverage covETECDefaultExpected = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750).removeAvailableLimitsAll().changeAvailableLimits(CoverageLimits.COV_25750, CoverageLimits.COV_501500);//Defaulted to $25/$750 AND only the limit applied on the active vehicles OR the limit selected on the other vehicle is available
		assertThat(covETECActualVeh2).isEqualTo(covETECDefaultExpected);

		//AC#7
		//Remove COLLDED and put it back to see that it is correctly defaulted to 27/750
		removeAndPutBackCOMPCOLLAndCheckETC_pas15420(policyNumber, newVehicleOid, CoverageInfo.COLLDED_CA.getCode());

		//Remove COMPDED and put it back to see that it is correctly defaulted to 27/750
		removeAndPutBackCOMPCOLLAndCheckETC_pas15420(policyNumber, newVehicleOid, CoverageInfo.COMPDED_CA.getCode());

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	private void removeAndPutBackCOMPCOLLAndCheckETC_pas15420(String policyNumber, String vehicleOid, String compdedColldedCd) {
		//Remove COMP/COLL and check
		PolicyCoverageInfo updateCoverageResponse = updateVehicleCoverage(policyNumber, vehicleOid, compdedColldedCd, CoverageLimits.COV_NO_COV.getLimit());
		Coverage covETECActual = findCoverage(findVehicleCoverages(updateCoverageResponse, vehicleOid).coverages, CoverageInfo.ETEC_CA.getCode());
		Coverage covETECNoCovExpected = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_00).disableCanChange();
		assertThat(covETECActual).isEqualToIgnoringGivenFields(covETECNoCovExpected, "availableLimits");// don't care about availableLimits as canChange = false

		//Add COMP/COLL back and check
		updateCoverageResponse = updateVehicleCoverage(policyNumber, vehicleOid, compdedColldedCd, CoverageLimits.COV_150.getLimit());
		covETECActual = findCoverage(findVehicleCoverages(updateCoverageResponse, vehicleOid).coverages, CoverageInfo.ETEC_CA.getCode());
		Coverage covETECDefaultExpected = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750).removeAvailableLimitsAll().changeAvailableLimits(CoverageLimits.COV_25750, CoverageLimits.COV_501500);//Defaulted to $25/$750 AND only the limit applied on the active vehicles OR the limit selected on the other vehicle is available
		assertThat(covETECActual).isEqualTo(covETECDefaultExpected);

		validateThatUpdateVehicleCoverageIsTheSameAsViewVehicleCoverages(policyNumber, updateCoverageResponse);
	}

	private void verifyCoveragesPASUI_pas26668(List<Coverage> expectedCoveragesVeh4) {
		openPendedEndorsementInquiryAndNavigateToPC();
		for (Coverage expectedCoverage : expectedCoveragesVeh4) {
			String actualCovValueInPASUI = premiumAndCoveragesTab.getVehicleCoverageDetailsValueByVehicle(4, expectedCoverage.getCoverageDescription());
			if (expectedCoverage.getCoverageCd().equals(CoverageInfo.COMPDED_CA.getCode()) || expectedCoverage.getCoverageCd().equals(CoverageInfo.COLLDED_CA.getCode())) { //COMPDED and COLLDED in UI don't have $ sign
				assertThat(actualCovValueInPASUI).isEqualTo(expectedCoverage.getCoverageLimit());
			} else {
				assertThat(actualCovValueInPASUI).isEqualTo(expectedCoverage.getCoverageLimitDisplay());
			}
		}
		premiumAndCoveragesTab.cancel();
	}

	private void checkCoverages_pas26668(List<Coverage> expectedCoverageList, VehicleCoverageInfo actualCoverageList) {
		printToLog("Checking coverages for vehicle with Oid " + actualCoverageList.oid);
		for (Coverage expectedCoverage : expectedCoverageList) {
			printToLog("Checking " + expectedCoverage.getCoverageCd());
			Coverage actualCoverage = findCoverage(actualCoverageList.coverages, expectedCoverage.getCoverageCd(), false);
			if (!expectedCoverage.getCustomerDisplayed()) {//this is for LOAN and NEWCAR_CA, because it is not displayed always
				assertThat(actualCoverage == null || !actualCoverage.getCustomerDisplayed()).isTrue();
				if (actualCoverage != null) {
					assertThat(actualCoverage.getCanChangeCoverage()).isFalse();
				}
			} else {
				assertThat(actualCoverage).isEqualTo(expectedCoverage);
			}

			//Verify that RREIM, TOWINGLABOR and SPECEQUIP is not displayed
			Coverage covRREIM = findCoverage(actualCoverageList.coverages, "RREIM", false);
			Coverage covTOWINGLABOR = findCoverage(actualCoverageList.coverages, "TOWINGLABOR", false);
			Coverage covSPECEQUIP = findCoverage(actualCoverageList.coverages, "SPECEQUIP", false);

			assertThat(covRREIM.getCustomerDisplayed()).isFalse();
			assertThat(covTOWINGLABOR.getCustomerDisplayed()).isFalse();
			assertThat(covSPECEQUIP.getCustomerDisplayed()).isFalse();

			assertThat(covRREIM.getCanChangeCoverage()).isFalse();
			assertThat(covTOWINGLABOR.getCanChangeCoverage()).isFalse();
			assertThat(covSPECEQUIP.getCanChangeCoverage()).isFalse();

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

	private String getRandomFinancedOrLeased() {
		List<String> list = new ArrayList<>();
		list.add("LSD");//Single
		list.add("FNC");//Divorced

		Random rand = new Random();
		return list.get(rand.nextInt(list.size()));
	}

	protected void pas19057_OrderOfCoverageBodyCA(ETCSCoreSoftAssertions softly, boolean isOwnedVehicle) {
		TestData td = getPolicyDefaultTD();
		List<String> orderOfVehicleLevelCoveragesExpected;
		if (isOwnedVehicle) {
			td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("VehicleTab_NewVehicle2"));// Owned vehicle to have NEWCAR coverage
			orderOfVehicleLevelCoveragesExpected = getTestSpecificTD("TestData_OrderOfCoverages_NEWCAR").getList("VehicleLevelCoverages");
		} else {
			td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("VehicleTab_NewVehicle1"));// Leased vehicle to have LOAN coverage
			orderOfVehicleLevelCoveragesExpected = getTestSpecificTD("TestData_OrderOfCoverages_LOAN").getList("VehicleLevelCoverages");
		}
		td.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssingmentTab_1Veh"))
				.resolveLinks();

		List<String> orderOfPolicyLevelCoveragesExpected = getTestSpecificTD("TestData_OrderOfCoverages").getList("PolicyLevelCoverages");
		List<String> orderOfDriverLevelCoveragesExpected = getTestSpecificTD("TestData_OrderOfCoverages").getList("DriverLevelCoverages");

		mainApp().open();
		String policyNumber;
		createCustomerIndividual();
		policyNumber = createPolicy(td);

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		verifyViewUpdateCoverageOrder(softly, orderOfPolicyLevelCoveragesExpected, orderOfVehicleLevelCoveragesExpected, orderOfDriverLevelCoveragesExpected, policyNumber);

		//NOTE: Validation of Change History is too complicated for automation - have to update every coverage. Should be tested manually if needed.
	}
}
