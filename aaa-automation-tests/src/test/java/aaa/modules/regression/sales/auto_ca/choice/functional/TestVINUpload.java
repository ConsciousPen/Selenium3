package aaa.modules.regression.sales.auto_ca.choice.functional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadFileType;
import aaa.helpers.product.VinUploadHelper;
import aaa.main.enums.DefaultVinVersions;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.helper.VinUploadCleanUpMethods;
import aaa.modules.regression.sales.template.functional.TestVINUploadTemplate;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestVINUpload extends TestVINUploadTemplate {
	private static final String NEW_VIN = "AAAVB3CC0W0455583";
	private static final String NEW_VIN2 = "BBBVB3CCXW0455583";
	private static final String NEW_VIN3 = "CCCVB3CC4W9455583";
	private static final String NEW_VIN4 = "DDDVB3CC3W9455583";
	private static final String NEW_VIN5 = "EEEVB2CCXW9455583";
	private static final String NEW_VIN6 = "FFFVB2CC9W9455583";
	private static final String REFRESHABLE_VIN = "4T1BE30K46U656311";
	private static final String GGGVB2CC8W9455583 = "GGGVB2CC8W9455583";

	private VehicleTab vehicleTab = new VehicleTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-533 Quote Refresh -Add New VIN
	 * PAS-1406 Data Refresh
	 * PAS-2714 New Liability Symbols
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#newVinAdded(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-533,PAS-1406")
	public void pas533_newVinAdded(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		newVinAdded(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN.get()), NEW_VIN);
	}

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-1406 Data Refresh
	 * PAS-527 Renewal Refresh - Add New VIN & Update Existing
	 * PAS-938 Throw Rerate Error if User Skips P&C Page after a quote is a day old
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#newVinAddedRenewal(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-527")
	public void pas527_NewVinAddedRenewal(@Optional("CA") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		newVinAddedRenewal(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN2.get()), NEW_VIN2);
	}

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-1406 Data Refresh
	 * PAS-527 Renewal Refresh - Add New VIN & Update Existing
	 *
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#updatedVinRenewal(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-527,PAS-1406")
	public void pas527_updatedVinRenewal(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		updatedVinRenewal(vinMethods.getSpecificUploadFile(VinUploadFileType.REFRESHABLE_VIN.get()), REFRESHABLE_VIN);
	}

	/**
	 *@author Viktor Petrenko
	 *
	 * PAS-2714 New Liability Symbols
	 *
	 * See detailed steps in template file
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-2714")
	public void pas2714_Endorsement(@Optional("CA") String state) {
		TestData testData = getNonExistingVehicleTestData(getPolicyTD(), GGGVB2CC8W9455583);

		endorsement(testData,GGGVB2CC8W9455583);
	}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-4253 Restrict VIN Refresh by Vehicle Type
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#pas4253_restrictVehicleRefreshNB#pas4253_restrictVehicleRefreshNB(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-4253")
	public void pas4253_restrictVehicleRefreshNB(@Optional("CA") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		pas4253_restrictVehicleRefreshNB(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN3.get()), NEW_VIN3);
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh R
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario
	 * 0. Retrieve active policy with (VIN matched)
	 * 1. Generate automated renewal image (in data gather status) according to renewal timeline
	 * 2. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
	 * 3. System rates renewal image according to renewal timeline
	 * 4. Validate vehicle information in VRD
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDate(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), NEW_VIN4)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Value($)"), "40000");

		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		adminApp().open();
		vinMethods.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN4.get()));
		/*
		 * Automated Renewal R-Expiration Date
		 */
		pas2716_AutomatedRenewal(policyNumber, policyExpirationDate, NEW_VIN4);
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh R-45
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario
	 * 0. Retrieve active policy with (VIN matched)
	 * 1. Generate automated renewal image (in data gather status) R-45
	 * 2. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
	 * 3. System rates renewal image according to renewal timeline
	 * 4. Validate vehicle information in VRD
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDateMinus45(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), NEW_VIN5)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Value($)"), "40000");

		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		adminApp().open();
		vinMethods.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN5.get()));
		/*
		 * Automated Renewal R-45 Expiration Date
		 */
		pas2716_AutomatedRenewal(policyNumber, policyExpirationDate.minusDays(45), NEW_VIN5);
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh R-35
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario
	 * 0. Retrieve active policy with (VIN matched)
	 * 1. Generate automated renewal image (in data gather status) R-35
	 * 2. Add new VIN versions/VIN data for vehicle VINs used above(4 new liability symbols prefilled in db)
	 * 3. System rates renewal image according to renewal timeline
	 * 4. Validate vehicle information in VRD
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDateMinus35(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		TestData testData = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), NEW_VIN6)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Value($)"), "40000");

		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		adminApp().open();
		vinMethods.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN6.get()));
		/*
		 * Automated Renewal R-35 Expiration Date
		 */
		pas2716_AutomatedRenewal(policyNumber, policyExpirationDate.minusDays(35), NEW_VIN6);
	}

	@AfterClass(alwaysRun = true)
	protected void vinTablesCleaner() {
		List<String> listOfVinIds = Arrays.asList(NEW_VIN, NEW_VIN2, NEW_VIN3, NEW_VIN4, NEW_VIN5, NEW_VIN6, GGGVB2CC8W9455583);
		VinUploadCleanUpMethods.deleteVinsById(listOfVinIds);

		DatabaseCleanHelper.cleanVehicleRefDataVinTable(GGGVB2CC8W9455583,DefaultVinVersions.DefaultVersions.CaliforniaChoice.get());
		DBService.get().executeUpdate(VehicleQueries.REFRESHABLE_VIN_CLEANER_CAC);
	}
}
