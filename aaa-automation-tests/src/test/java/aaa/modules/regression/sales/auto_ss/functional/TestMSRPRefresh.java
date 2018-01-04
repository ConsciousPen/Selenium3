package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.queries.LookupQueries;
import aaa.modules.regression.queries.postconditions.DatabaseCleanHelper;
import aaa.modules.regression.sales.auto_ss.functional.helpers.TestVinUploadHelper;
import aaa.modules.regression.sales.common_helpers.VinUploadCommonMethods;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMSRPRefresh extends TestVinUploadHelper implements LookupQueries {

	private VinUploadCommonMethods vinMethods = new VinUploadCommonMethods(getPolicyType());

	private static final String NEW_VIN = "1FDEU15H7KL055795";
	private static final String UPDATABLE_VIN = "1HGEM215140028445";

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-730")
	public void pas730_MSRPRefreshCompColl(@Optional("UT") String state) {
		String vinTableFile = vinMethods.getSpecificUploadFile(VinUploadCommonMethods.UploadFilesTypes.ADDED_VIN.get());

		String vehYear = "2015";
		String vehMake = "VOLKSWAGEN";
		String vehModel = "GOLF";
		String vehSeries = "GOLF";
		String vehBodyStyle = "HATCHBACK 4 DOOR";

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.VIN.getLabel()), "")
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.YEAR.getLabel()), vehYear)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MAKE.getLabel()), vehMake)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.MODEL.getLabel()), vehModel)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.SERIES.getLabel()), vehSeries)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel()), vehBodyStyle).resolveLinks();

		// Enable vin refresh
		vinMethods.enableVinRefresh(true);
		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		createAndFillUpTo(testData, PremiumAndCoveragesTab.class);

		PremiumAndCoveragesTab.calculatePremium();

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		// Values from VIN comp and coll symbol in excel sheet
		assertSoftly(softly -> {
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isNotEqualTo("44");
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isNotEqualTo("35");

		});
		String compSymbol = PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue();
		String collSymbol = PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue();
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();

		VehicleTab.buttonSaveAndExit.click();

		String quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();

		//Uploading of VinUpload info, then uploading of the updates for VIN_Control table
		// Vin control table has version which overrides VERSION_2000, it is needed and important to get symbols for next steps
		vinMethods.uploadFiles(vinTableFile);

		//Go back to MainApp, open quote, calculate premium and verify if VIN value is applied
		findAndRateQuote(testData, quoteNumber);

		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		assertSoftly(softly -> {
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isEqualTo("44");
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Comp Symbol").getCell(2).getValue()).isNotEqualTo(compSymbol);

			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isEqualTo("35");
			softly.assertThat(PremiumAndCoveragesTab.tableRatingDetailsVehicles.getRow(1, "Coll Symbol").getCell(2).getValue()).isNotEqualTo(collSymbol);

		});
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
	}



	/**
	 * Info in each xml file for this test could be used only once, so for running of tests properly DB should be cleaned after
	 * each test method. So newly added values should be deleted from Vehiclerefdatavin, Vehiclerefdatamodel and VEHICLEREFDATAVINCONTROL
	 * tables. Default values should be set for EXPIRATIONDATE field for default rows in VEHICLEREFDATAVINCONTROL table.
	 * <p>
	 * 'SYMBOL_2000_SS_TEST' are names of configurations which are used and listed in excel
	 * files for each product (choice config, select config and Signature Series config ONLY for UT state). So if they will be changed there
	 * this after method should be updated. But such updates are not supposed to be done.
	 * Please refer to the files with appropriate names in each test in /resources/uploadingfiles/vinUploadFiles.
	 */
	@AfterMethod(alwaysRun = true)
	protected void vinTablesCleaner() {
		DatabaseCleanHelper.cleanVinUploadTables("('SYMBOL_2000_SS_TEST')", getState());
		vinMethods.enableVinRefresh(false);
	}
}
