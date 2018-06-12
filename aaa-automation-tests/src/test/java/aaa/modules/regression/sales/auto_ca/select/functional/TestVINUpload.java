package aaa.modules.regression.sales.auto_ca.select.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.queries.VehicleQueries;
import aaa.helpers.product.DatabaseCleanHelper;
import aaa.helpers.product.VinUploadFileType;
import aaa.helpers.product.VinUploadHelper;
import aaa.helpers.ssh.RemoteHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestVINUploadTemplate;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestVINUpload extends TestVINUploadTemplate {

	private static final String NEW_VIN = "AAANK3CC0F0455583";
	private static final String NEW_VIN2 = "BBBNK3CC0F0455583";
	private static final String NEW_VIN3 = "CCCNK3CC4F9455583";
	private static final String NEW_VIN4 = "DDDNK3CC3F9455583";
	private static final String NEW_VIN5 = "EEENK2CCXF9455583";
	private static final String NEW_VIN6 = "FFFNK2CC9F9455583";
	private static final String NEW_VIN7 = "GGGNK2CC8F9455583";
	private static final String REFRESHABLE_VIN = "1HGEM215150028445";
	private static final String HHHNK2CC7F9455583 = "HHHNK2CC7F9455583";

	private VehicleTab vehicleTab = new VehicleTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Lev Kazarnovskiy, Team Scorpions
	 * PAS-533 Quote Refresh -Add New VIN
	 * PAS-1406 ata Refresh
	 *
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#newVinAdded(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-533")
	public void pas533_newVinAdded(@Optional("") String state) {
	    VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());

		newVinAdded(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN.get()), NEW_VIN);
	}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-4253 Restrict VIN Refresh by Vehicle Type
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#pas4253_restrictVehicleRefreshNB(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-4253")
	public void pas4253_restrictVehicleRefreshNB(@Optional("CA") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());
		pas4253_restrictVehicleRefreshNB(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN2.get()), NEW_VIN2);
	}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-527 Renewal Refresh -Add New VIN & Update Existing
	 * PAS-1406 - Data Refresh
	 * PAS-938 Throw Rerate Error if User Skips P&C Page after a quote is a day old
	 *
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#newVinAddedRenewal(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-527")
	public void pas527_NewVinAddedRenewal(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());
		newVinAddedRenewal(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN3.get()), NEW_VIN3);
	}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-527 -Renewal Refresh -Add New VIN & Update Existing
	 * PAS-1406 - Data Refresh
	 *
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#updatedVinRenewal(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-527")
	public void pas527_UpdatedVinRenewal(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());
		updatedVinRenewal(vinMethods.getSpecificUploadFile(VinUploadFileType.REFRESHABLE_VIN.get()), REFRESHABLE_VIN);
	}

	/**
	 *@author Viktor Petrenko
	 *
	 * PAS-2714 New Liability Symbols
	 *
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#updatedVinRenewal(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-2714")
	public void pas2714_Endorsement(@Optional("CA") String state) {
		TestData testData = getNonExistingVehicleTestData(getPolicyTD(),HHHNK2CC7F9455583).resolveLinks();

		endorsement(testData,HHHNK2CC7F9455583);
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDate(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());
		TestData testData = getNonExistingVehicleTestData(getPolicyTD(),NEW_VIN4);

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
	 * PAS-2716 Update VIN Refresh  R-45
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDateMinus45(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());
		TestData testData = getNonExistingVehicleTestData(getPolicyTD(),NEW_VIN5);

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
	 * PAS-2716 Update VIN Refresh  R-35
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDateMinus35(@Optional("") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(),getState());
		TestData testData = getNonExistingVehicleTestData(getPolicyTD(),NEW_VIN6);

		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		adminApp().open();
		vinMethods.uploadVinTable(vinMethods.getSpecificUploadFile(VinUploadFileType.NEW_VIN6.get()));
		/*
		 * Automated Renewal R-35 Expiration Date
		 */
		pas2716_AutomatedRenewal(policyNumber, policyExpirationDate.minusDays(35), NEW_VIN6);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void test(@Optional("") String state) {
		String methodName = "pas730_VehicleTypeRegular";
		StackTraceElement result = Arrays.stream(Thread.currentThread().getStackTrace()).filter(s -> s.getClassName().startsWith("aaa.modules")).reduce((a, b) -> b).orElse(null);
		if (result != null) {
			methodName = result.getClassName() + "." + result.getMethodName() + "_" + getState();
		}
		String pathToLogs = "/AAA/tcserver/pivotal-tc-server-developer-3.0.0.RELEASE/tomcat-7.0.55.A.RELEASE/logs/aaa.log";
		String log = RemoteHelper.getFileContent(pathToLogs);

	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh R-45
	 * @name Test VINupload 'Add new VIN' scenario for Renewal.
	 * @scenario 1. Create Auto policy with 2 vehicles
	 * 2. Renewal term is inforce) R-45
	 * 3. Add new VIN versions/VIN data for vehicle3 to be added during endorsement (see notes)e
	 * 4. Initiate Prior Term (backdated) endorsement with effective date in previous term (for example R-5)
	 * 5. Add new vehicle3
	 * 6. Bind endorsement
	 * 7. Roll on changes for renewal term with changes made in OOS endorsement
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2716")
	public void pas2716_BackDatedEndorsement(@Optional("CA") String state) {
		VinUploadHelper vinMethods = new VinUploadHelper(getPolicyType(), getState());
		// todo create files for ca
		String vinTableFile = "backdatedVinTable_CA_SS.xlsx";
		String controlTableFile = "backdatedControlTable_UT_SS.xlsx";

		TestData testData = getNonExistingVehicleTestData(getPolicyTD(), NEW_VIN7).resolveLinks();

		// 1. Create Auto policy with 2 vehicles
		String policyNumber = createPreconds(testData);

		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();

		adminApp().open();
		//todo create files for ca
		//vinMethods.uploadFiles(controlTableFile, vinTableFile);

		// 2. Renewal term is inforce) R-35
		moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(35));
		// Add vehicle at renewal version
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
		// Make sure refresh occurs
		assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MAKE.getLabel()).getValue()).isEqualTo("BACKDATED_SS_MAKE");
		assertThat(vehicleTab.getAssetList().getAsset(AutoCaMetaData.VehicleTab.MODEL.getLabel()).getValue()).isEqualTo("Gt");
		// Add Vehicle to new renewal version
		TestData renewalVersionVehicle = getPolicyTD().getTestData(vehicleTab.getMetaKey())
				.adjust(AutoCaMetaData.VehicleTab.TYPE.getLabel(), "Private Passenger Auto")
				.adjust(AutoCaMetaData.VehicleTab.VIN.getLabel(), "7MSRP15H5V1011111");

		List<TestData> renewalVerrsionVehicleTab = new ArrayList<>();
		renewalVerrsionVehicleTab.add(getPolicyTD()
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), NEW_VIN7).getTestData("VehicleTab"));
		renewalVerrsionVehicleTab.add(renewalVersionVehicle);

		TestData testDataRenewalVersion = getPolicyTD().adjust(vehicleTab.getMetaKey(), renewalVerrsionVehicleTab)
				.mask(TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()));

		vehicleTab.fillTab(testDataRenewalVersion);
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		new DocumentsAndBindTab().submitTab();
	}

	@AfterSuite(alwaysRun = true)
	protected void vinTablesCleaner() {
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN2,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN3,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN4,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN5,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN6,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(NEW_VIN7,"SYMBOL_2000");
		DatabaseCleanHelper.cleanVehicleRefDataVinTable(HHHNK2CC7F9455583,"SYMBOL_2000");
		DBService.get().executeUpdate(VehicleQueries.REFRESHABLE_VIN_CLEANER_CAS);
	}
}
