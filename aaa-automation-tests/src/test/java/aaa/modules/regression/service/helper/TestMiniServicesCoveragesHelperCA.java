package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.*;
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
		String policyNumber = openAppAndCreatePolicy(testData);
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		//Expected coverages Vehicle 1 (the same as in PAS UI)
		Coverage covCOMPDEDExpected1 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covGLASSxpected1 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_FALSE_NO_COVERAGE);
		Coverage covCOLLDEDDExpected1 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covETEExpected1 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected1 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_NO_COV).disableCanChange();
		Coverage covLOANExpected1 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_0).disableCanChange().disableCustomerDisplay();
		Coverage covNEWCARExpected1 = Coverage.create(CoverageInfo.NEWCAR_CA).changeLimit(CoverageLimits.COV_FALSE).disableCustomerDisplay().disableCanChange();
		Coverage covRIDESHAREExpected1 = Coverage.create(CoverageInfo.RIDESHARE_CA).disableCanChange();

		List<Coverage> expectedCoveragesVeh1 = new ArrayList<>();
		expectedCoveragesVeh1.add(covCOMPDEDExpected1);
		//expectedCoveragesVeh1.add(covGLASSxpected1);
		expectedCoveragesVeh1.add(covCOLLDEDDExpected1);
		expectedCoveragesVeh1.add(covETEExpected1);
		expectedCoveragesVeh1.add(covALLRISKExpected1);
		expectedCoveragesVeh1.add(covLOANExpected1);
		//expectedCoveragesVeh1.add(covNEWCARExpected1);
		expectedCoveragesVeh1.add(covRIDESHAREExpected1);

		//Expected coverages Vehicle 2 (the same as in PAS UI)
		Coverage covCOMPDEDExpected2 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250).removeAvailableLimit(CoverageLimits.COV_NO_COV);// Only Owned Vehicles have No Cov limit available
		Coverage covGLASSxpected2 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_TRUE);
		Coverage covCOLLDEDDExpected2 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500).removeAvailableLimit(CoverageLimits.COV_NO_COV);// Only Owned Vehicles have No Cov limit available
		Coverage covETEXxpected2 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected2 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_NO_COV).disableCanChange();
		Coverage covLOANExpected2 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_1);
		Coverage covNEWCARExpected2 = Coverage.create(CoverageInfo.NEWCAR_CA).changeLimit(CoverageLimits.COV_FALSE).disableCanChange().disableCustomerDisplay();
		Coverage covRIDESHAREExpected2 = Coverage.create(CoverageInfo.RIDESHARE_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();

		List<Coverage> expectedCoveragesVeh2 = new ArrayList<>();
		expectedCoveragesVeh2.add(covCOMPDEDExpected2);
		//expectedCoveragesVeh2.add(covGLASSxpected2);// TODO-mstrazds: GLASS and NEWCAR will be done in next sprints, needs to be uncommented
		expectedCoveragesVeh2.add(covCOLLDEDDExpected2);
		expectedCoveragesVeh2.add(covETEXxpected2);
		expectedCoveragesVeh2.add(covALLRISKExpected2);
		expectedCoveragesVeh2.add(covLOANExpected2);
		//expectedCoveragesVeh2.add(covNEWCARExpected2);// TODO-mstrazds: GLASS and NEWCAR will be done in next sprints, needs to be uncommented
		expectedCoveragesVeh2.add(covRIDESHAREExpected2);

		//Expected coverages Vehicle 3 (the same as in PAS UI)
		Coverage covCOMPDEDExpected3 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covGLASSxpected3 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_TRUE);
		Coverage covCOLLDEDDExpected3 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covETEXExpected3 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected3 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_NO_COV).disableCanChange();
		Coverage covLOANExpected3 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_0).disableCanChange().disableCustomerDisplay();
		Coverage covNEWCARExpected3 = Coverage.create(CoverageInfo.NEWCAR_CA).changeLimit(CoverageLimits.COV_TRUE).disableCanChange();
		Coverage covRIDESHAREExpected3 = Coverage.create(CoverageInfo.RIDESHARE_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();

		List<Coverage> expectedCoveragesVeh3 = new ArrayList<>();
		expectedCoveragesVeh3.add(covCOMPDEDExpected3);
		//expectedCoveragesVeh3.add(covGLASSxpected3);// TODO-mstrazds: GLASS and NEWCAR will be done in next sprints, needs to be uncommented
		expectedCoveragesVeh3.add(covCOLLDEDDExpected3);
		expectedCoveragesVeh3.add(covETEXExpected3);
		expectedCoveragesVeh3.add(covALLRISKExpected3);
		expectedCoveragesVeh3.add(covLOANExpected3);
		//expectedCoveragesVeh3.add(covNEWCARExpected3);// TODO-mstrazds: GLASS and NEWCAR will be done in next sprints, needs to be uncommented
		expectedCoveragesVeh3.add(covRIDESHAREExpected3);

		ViewVehicleResponse viewVehicleResponse = HelperCommon.viewEndorsementVehicles(policyNumber);
		Vehicle vehicle1 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse, testData.getTestDataList("VehicleTab").get(0).getValue("VIN"));
		Vehicle vehicle2 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse, testData.getTestDataList("VehicleTab").get(1).getValue("VIN"));
		Vehicle vehicle3 = TestMiniServicesVehiclesHelper.findVehicleByVin(viewVehicleResponse, testData.getTestDataList("VehicleTab").get(2).getValue("VIN"));

		VehicleCoverageInfo veh1Coverages = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages, vehicle1.oid);
		VehicleCoverageInfo veh2Coverages = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages, vehicle2.oid);
		VehicleCoverageInfo veh3Coverages = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages, vehicle3.oid);

		//Check coverages
		checkCoverages_pas26668(expectedCoveragesVeh1, veh1Coverages);
		checkCoverages_pas26668(expectedCoveragesVeh2, veh2Coverages);
		checkCoverages_pas26668(expectedCoveragesVeh3, veh3Coverages);

		//Add vehicle
		String newVin = "1FMCU9GD5JUB71878";
		String newVehicleOid = helperMiniServices.addVehicleWithChecks(policyNumber, "2015-02-11", newVin, true);// 2018 Ford Escape

		//Expected coverages Newly added Vehicle
		Coverage covCOMPDEDExpected4 = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covGLASSxpected4 = Coverage.create(CoverageInfo.GLASS_CA).changeLimit(CoverageLimits.COV_FALSE_NO_COVERAGE);
		Coverage covCOLLDEDExpected4 = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covETEXExpected4 = Coverage.create(CoverageInfo.ETEC_CA).changeLimit(CoverageLimits.COV_25750);
		Coverage covALLRISKExpected4 = Coverage.create(CoverageInfo.ALLRISK_CA).changeLimit(CoverageLimits.COV_NO_COV).disableCanChange();
		Coverage covLOANExpected4 = Coverage.create(CoverageInfo.LOAN_CA).changeLimit(CoverageLimits.COV_0).disableCanChange().disableCustomerDisplay();
		Coverage covNEWCARExpected4 = Coverage.create(CoverageInfo.NEWCAR_CA).changeLimit(CoverageLimits.COV_FALSE).disableCanChange();
		Coverage covRIDESHAREExpected4 = Coverage.create(CoverageInfo.RIDESHARE_CA).changeLimit(CoverageLimits.COV_0).disableCanChange();

		List<Coverage> expectedCoveragesVeh4 = new ArrayList<>();
		expectedCoveragesVeh4.add(covCOMPDEDExpected4);
		//expectedCoveragesVeh4.add(covGLASSxpected4);// TODO-mstrazds: GLASS and NEWCAR will be done in next sprints, needs to be uncommented
		expectedCoveragesVeh4.add(covCOLLDEDExpected4);
		expectedCoveragesVeh4.add(covETEXExpected4);
		expectedCoveragesVeh4.add(covALLRISKExpected4);
		expectedCoveragesVeh4.add(covLOANExpected4);
		//expectedCoveragesVeh4.add(covNEWCARExpected4);// TODO-mstrazds: GLASS and NEWCAR will be done in next sprints, needs to be uncommented
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
		VehicleUpdateDto updateVehicleLeasedFinanced = new VehicleUpdateDto();
		updateVehicleLeasedFinanced.vehicleOwnership = new VehicleOwnership();
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine1 = "Line1";
		updateVehicleLeasedFinanced.vehicleOwnership.addressLine2 = "Line 2";
		updateVehicleLeasedFinanced.vehicleOwnership.city = "LA";
		updateVehicleLeasedFinanced.vehicleOwnership.stateProvCd = "CA";
		updateVehicleLeasedFinanced.vehicleOwnership.postalCode = "90201";
		updateVehicleLeasedFinanced.vehicleOwnership.ownership = "LSD";
		updateVehicleLeasedFinanced.vehicleOwnership.name = "John";
		updateVehicleLeasedFinanced.vehicleOwnership.secondName = "Benny";
		VehicleUpdateResponseDto ownershipUpdateResponse = HelperCommon.updateVehicle(policyNumber, newVehicleOid, updateVehicleLeasedFinanced);
		assertThat(ownershipUpdateResponse.vehicleOwnership.ownership).isEqualTo("LSD");

		covCOMPDEDExpected4.removeAvailableLimit(CoverageLimits.COV_NO_COV);
		covCOLLDEDExpected4.removeAvailableLimit(CoverageLimits.COV_NO_COV);
		covLOANExpected4.enableCustomerDisplay().enableCanChange();
		expectedCoveragesVeh4.add(covLOANExpected4);
		PolicyCoverageInfo viewEndorsementCoverages3 = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo veh4CoveragesLeased = TestMiniServicesCoveragesHelper.findVehicleCoverages(viewEndorsementCoverages3, newVehicleOid);
		checkCoverages_pas26668(expectedCoveragesVeh4, veh4CoveragesLeased);
		verifyCoveragesPASUI_pas26668(expectedCoveragesVeh4);

	}

	protected void pas15424_viewUpdateOEMCoverageCATC01Body() {
		String policyNumber = openAppAndCreatePolicy();

		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		Coverage covCOMPDEDExpected = Coverage.create(CoverageInfo.COMPDED_CA).changeLimit(CoverageLimits.COV_250);
		Coverage covCOLLDEDExpected = Coverage.create(CoverageInfo.COLLDED_CA).changeLimit(CoverageLimits.COV_500);
		Coverage covOEMExpected = Coverage.create(CoverageInfo.OEM_CA);
		Vehicle vehicle = HelperCommon.viewEndorsementVehicles(policyNumber).vehicleList.get(0);
		VehicleCoverageInfo veh1Coverages = findVehicleCoverages(viewEndorsementCoverages, vehicle.oid);
		Coverage covOEMActual = findCoverage(veh1Coverages.coverages, CoverageInfo.OEM_CA.getCode());
		assertThat(covOEMActual).isEqualTo(covOEMExpected);

		//Add vehicle
		String newVin = "1FMCU9GD5JUB71878"; // 2018 Ford Escape (less than 10y old)
		String newVehicleOid = helperMiniServices.addVehicleWithChecks(policyNumber, "2015-02-11", newVin, true);
		viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo veh2Coverages = findVehicleCoverages(viewEndorsementCoverages, newVehicleOid);
		Coverage covCOMPDEDActualVeh2 = findCoverage(veh2Coverages.coverages, CoverageInfo.COMPDED_CA.getCode());
		Coverage covCOLLDEDActualVeh2 = findCoverage(veh2Coverages.coverages, CoverageInfo.COLLDED_CA.getCode());
		Coverage covOEMActualVeh2 = findCoverage(veh2Coverages.coverages, CoverageInfo.OEM_CA.getCode());
		assertThat(covCOMPDEDActualVeh2).isEqualTo(covCOMPDEDExpected);
		assertThat(covCOLLDEDActualVeh2).isEqualTo(covCOLLDEDExpected);
		Coverage covOEMExpectedVeh2 = Coverage.create(CoverageInfo.OEM_CA).changeLimit(CoverageLimits.COV_0);
		assertThat(covOEMActualVeh2).isEqualTo(covOEMExpectedVeh2);

		//Apply OEM
		Coverage covOEMYesExpected = Coverage.create(CoverageInfo.OEM_CA).changeLimit(CoverageLimits.COV_1);
		updateVehLevelCoverageAndCheckResponses(policyNumber, newVehicleOid, covOEMYesExpected, covOEMYesExpected, covCOMPDEDExpected, covCOLLDEDExpected);

		//Update COLLDED to 0
		covCOLLDEDExpected.changeLimit(CoverageLimits.COV_NO_COV);
		covOEMExpectedVeh2.disableCanChange().disableCustomerDisplay();
		updateVehLevelCoverageAndCheckResponses(policyNumber, newVehicleOid, covCOLLDEDExpected, covCOLLDEDExpected, covCOMPDEDExpected, covOEMExpected);

		//Update COLLDED back to other than 0
		covCOLLDEDExpected.changeLimit(CoverageLimits.COV_250);
		covOEMExpectedVeh2.enableCanChange().enableCustomerDisplay();
		updateVehLevelCoverageAndCheckResponses(policyNumber, newVehicleOid, covCOLLDEDExpected, covCOLLDEDExpected, covCOMPDEDExpected, covOEMExpected);

		//Update COMPDED 0
		covCOMPDEDExpected.changeLimit(CoverageLimits.COV_NO_COV);
		covOEMExpectedVeh2.disableCustomerDisplay().disableCanChange();
		updateVehLevelCoverageAndCheckResponses(policyNumber, newVehicleOid, covCOMPDEDExpected, covCOMPDEDExpected, covOEMExpected);

		//Update COLLDED also to 0
		covCOLLDEDExpected.changeLimit(CoverageLimits.COV_NO_COV);
		covOEMExpectedVeh2.disableCanChange().disableCustomerDisplay();
		updateVehLevelCoverageAndCheckResponses(policyNumber, newVehicleOid, covCOLLDEDExpected, covCOLLDEDExpected, covCOMPDEDExpected, covOEMExpected);
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
	}

	private void verifyOEMTC02_pas15424(String policyNumber, String vehOid) {
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo vehicleCoverages = findVehicleCoverages(viewEndorsementCoverages, vehOid);
		Coverage covCOMPDED = findCoverage(vehicleCoverages.coverages, CoverageInfo.COMPDED_CA.getCode());
		Coverage covCOLLDED = findCoverage(vehicleCoverages.coverages, CoverageInfo.COLLDED_CA.getCode());
		Coverage covOEMActual = findCoverage(vehicleCoverages.coverages, CoverageInfo.OEM_CA.getCode());

		assertThat(covCOMPDED.getCoverageLimit()).as("Precondition: should have COMPDED applied").isNotEqualTo(CoverageLimits.COV_NO_COV.getLimit());
		assertThat(covCOLLDED.getCoverageLimit()).as("Precondition: should have COLLDED applied").isNotEqualTo(CoverageLimits.COV_NO_COV.getLimit());
		assertThat(covOEMActual.getCustomerDisplayed()).as("OEM should not be customerDisplayed if Vehicle is older than 10 years").isFalse();
		assertThat(covOEMActual.getCanChangeCoverage()).as("OEM should not be changable if Vehicle is older than 10 years").isFalse();
	}

	protected void pas15424_viewUpdateOEMCoverageNewVehNoCompCollCABody(boolean hasCOMPDED, boolean hasCOLLDED) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		PolicyCoverageInfo viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		validateViewPolicyCoveragesIsTheSameAsViewEndorsementCoverage(policyNumber, viewEndorsementCoverages);

		//Add vehicle less than 10y old
		String newVehOid = helperMiniServices.addVehicleWithChecks(policyNumber, "2015-02-11", "1FMCU9GD5JUB71878", true);// 2018 Ford Escape (less than 10y old)

		verifyOEMVehNoCompColl(hasCOMPDED, hasCOLLDED, policyNumber, newVehOid);

	}

	protected void pas15424_viewUpdateOEMCoverageExistingVehicleNoCompCollTC06Body(boolean hasCOMPDED, boolean hasCOLLDED) {
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

		assertThat(covCOMPDED.getCoverageLimit()).as("Precondition: should have COMPDED applied").isNotEqualTo(CoverageLimits.COV_NO_COV.getLimit());
		assertThat(covCOLLDED.getCoverageLimit()).as("Precondition: should have COLLDED applied").isNotEqualTo(CoverageLimits.COV_NO_COV.getLimit());
		assertThat(covOEMActual).isEqualTo(covOEMExpected);

		verifyOEMVehNoCompColl(hasCOMPDED, hasCOLLDED, policyNumber, vehOid);

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
	}

	private void verifyOEMForVehicleTC09_pas15424(String oidVeh, PolicyCoverageInfo viewEndorsementCoverages) {
		VehicleCoverageInfo vehicleCoverageInfo = findVehicleCoverages(viewEndorsementCoverages, oidVeh);
		Coverage covOEMActualVeh1 = findCoverage(vehicleCoverageInfo.coverages, CoverageInfo.OEM_CA.getCode());
		assertThat(covOEMActualVeh1.getCustomerDisplayed()).as("OEM should not be customerDisplayed if COMPDED and/or COLLDED is not applied").isFalse();
		assertThat(covOEMActualVeh1.getCanChangeCoverage()).as("OEM should not be changable if COMPDED and/or COLLDED is not applied").isFalse();
	}

	private void verifyOEMVehNoCompColl(boolean hasCOMPDED, boolean hasCOLLDED, String policyNumber, String vehOid) {
		PolicyCoverageInfo viewEndorsementCoverages;
		if (hasCOMPDED) {
			//Updated COMPDED to No Coverage
			updateVehicleCoverage(policyNumber, vehOid, CoverageInfo.COMPDED_CA.getCode(), CoverageLimits.COV_NO_COV.getLimit());
		}

		if (hasCOLLDED) {
			//Updated COLLDED to No Coverage
			updateVehicleCoverage(policyNumber, vehOid, CoverageInfo.COLLDED_CA.getCode(), CoverageLimits.COV_NO_COV.getLimit());
		}

		viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo newVehicleCoverages = findVehicleCoverages(viewEndorsementCoverages, vehOid);
		Coverage covCOMPDED = findCoverage(newVehicleCoverages.coverages, CoverageInfo.COMPDED_CA.getCode());
		Coverage covCOLLDED = findCoverage(newVehicleCoverages.coverages, CoverageInfo.COLLDED_CA.getCode());
		Coverage covOEMActual = findCoverage(newVehicleCoverages.coverages, CoverageInfo.OEM_CA.getCode());

		assertThat(covCOMPDED.getCoverageLimit().equals(CoverageLimits.COV_NO_COV.getLimit())).isEqualTo(hasCOMPDED);
		assertThat(covCOLLDED.getCoverageLimit().equals(CoverageLimits.COV_NO_COV.getLimit())).isEqualTo(hasCOLLDED);
		assertThat(covOEMActual.getCustomerDisplayed()).as("OEM should not be customerDisplayed if COMPDED and/or COLLDED is not applied").isFalse();
		assertThat(covOEMActual.getCanChangeCoverage()).as("OEM should not be changable if COMPDED and/or COLLDED is not applied").isFalse();
	}

	private void applyCompColl(boolean applyCOMPDED, boolean applyCOLLDED, String policyNumber, String vehOid) {
		PolicyCoverageInfo viewEndorsementCoverages;
		if (applyCOMPDED) {
			//Updated COMPDED to No Coverage
			updateVehicleCoverage(policyNumber, vehOid, CoverageInfo.COMPDED_CA.getCode(), CoverageLimits.COV_150.getLimit());
		}

		if (applyCOLLDED) {
			//Updated COLLDED to No Coverage
			updateVehicleCoverage(policyNumber, vehOid, CoverageInfo.COLLDED_CA.getCode(), CoverageLimits.COV_150.getLimit());
		}

		viewEndorsementCoverages = HelperCommon.viewEndorsementCoverages(policyNumber, PolicyCoverageInfo.class);
		VehicleCoverageInfo newVehicleCoverages = findVehicleCoverages(viewEndorsementCoverages, vehOid);
		Coverage covCOMPDED = findCoverage(newVehicleCoverages.coverages, CoverageInfo.COMPDED_CA.getCode());
		Coverage covCOLLDED = findCoverage(newVehicleCoverages.coverages, CoverageInfo.COLLDED_CA.getCode());
		Coverage covOEMActual = findCoverage(newVehicleCoverages.coverages, CoverageInfo.OEM_CA.getCode());
		Coverage covOEMExpected = Coverage.create(CoverageInfo.OEM_CA).changeLimit(CoverageLimits.COV_0);

		assertThat(covCOMPDED.getCoverageLimit().equals(CoverageLimits.COV_150.getLimit())).isEqualTo(applyCOMPDED);
		assertThat(covCOLLDED.getCoverageLimit().equals(CoverageLimits.COV_150.getLimit())).isEqualTo(applyCOLLDED);
		assertThat(covOEMActual).as("Should have OEM available if COMPDED and COLLDED is applied").isEqualTo(covOEMExpected);

		//Apply OEM
		covOEMExpected.changeLimit(CoverageLimits.COV_1);
		updateVehLevelCoverageAndCheck(policyNumber, covOEMExpected, covOEMExpected);
	}

	protected void pas15424_viewUpdateOEMCoveragelessThan10yNoOEMCATC010Body() {
		TestData td = getPolicyDefaultTD();
		TestData testData = td.adjust(TestData.makeKeyPath(VehicleTab.class.getSimpleName(), AutoCaMetaData.VehicleTab.VIN.getLabel()), "1FMCU9GD5JUB71878")// 2018 Ford Escape (less than 10y old)
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssingmentTab_1Veh"))
				.adjust(new PremiumAndCoveragesTab().getMetaKey(), getTestSpecificTD("PremiumAndCoveragesTab_OEMNo"))
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
		updateVehLevelCoverageAndCheck(policyNumber, covOEMExpected, covOEMExpected, covCOMPDED, covCOLLDED);
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
		td.adjust(new VehicleTab().getMetaKey(), getTestSpecificTD("VehicleTab_NewVehicle1"))// Leased vehicle to have LOAN coverage
				.adjust(new AssignmentTab().getMetaKey(), getTestSpecificTD("AssingmentTab_1Veh"))
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
