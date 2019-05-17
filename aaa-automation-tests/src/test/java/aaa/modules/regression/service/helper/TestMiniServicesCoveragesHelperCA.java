package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.CoverageInfo;
import aaa.main.enums.CoverageLimits;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import toolkit.datax.TestData;

public class TestMiniServicesCoveragesHelperCA extends TestMiniServicesCoveragesHelper {

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
		TestData td = getPolicyTD("DataGather", "TestData");
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
