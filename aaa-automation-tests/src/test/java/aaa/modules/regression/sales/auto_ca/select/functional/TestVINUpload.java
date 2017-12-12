package aaa.modules.regression.sales.auto_ca.select.functional;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestVINUploadTemplate;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestVINUpload extends TestVINUploadTemplate {
	private static final String PAS2716_VIN_NUMBER = "1FDEU15H7KL055795";
	private static final String PAS2716_CONTROLTABLEFILE = "controlTable_CA_SELECT.xlsx";
	private static final String PAS2716_VINTABLEFILE = "uploadAddedVIN_CA_SELECT.xlsx";
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	//	TODO add logic to generate file names for CA states based on product type

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-533 Quote Refresh -Add New VIN
	 * PAS-1406 ata Refresh
	 *
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#newVinAdded(String, String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-533")
	public void pas533_newVinAdded(@Optional("CA") String state) {
		newVinAdded(PAS2716_CONTROLTABLEFILE, PAS2716_VINTABLEFILE, "1FDEU15H7KL055795");
	}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-4253 Restrict VIN Refresh by Vehicle Type
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#pas4253_restrictVehicleRefreshNB(String, String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-4253")
	public void pas4253_restrictVehicleRefreshNB(@Optional("CA") String state) {
		pas4253_restrictVehicleRefreshNB(PAS2716_CONTROLTABLEFILE, PAS2716_VINTABLEFILE, "1FDEU15H7KL055795");
	}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-527 Renewal Refresh -Add New VIN & Update Existing
	 * PAS-1406 - Data Refresh
	 *
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#newVinAddedRenewal(String, String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-527")
	public void pas527_newVinAddedRenewal(@Optional("CA") String state) {
		newVinAddedRenewal(PAS2716_CONTROLTABLEFILE, PAS2716_VINTABLEFILE, "1HGEM215140028445");
	}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-527 -Renewal Refresh -Add New VIN & Update Existing
	 * PAS-1406 - Data Refresh
	 *
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#updatedVinRenewal(String, String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-527")
	public void pas527_updatedVinRenewal(@Optional("CA") String state) {
		updatedVinRenewal(PAS2716_CONTROLTABLEFILE, "uploadUpdatedVIN_CA_SELECT.xlsx", "1HGEM215140028445");
	}

	/**
	 *@author Viktor Petrenko
	 *
	 * PAS-2714 New Liability Symbols
	 *
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#updatedVinRenewal(String, String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-2714")
	public void pas2714_Endorsement(@Optional("CA") String state) {
		endorsement(PAS2716_CONTROLTABLEFILE, PAS2716_VINTABLEFILE, "1FDEU15H7KL055795");
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDate(@Optional("CA") String state) {
		TestData testData = getTestDataWithSinceMembership(PAS2716_VIN_NUMBER);
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		uploadFiles(PAS2716_CONTROLTABLEFILE,PAS2716_VINTABLEFILE);
		/**
		 * Automated Renewal R-Expiration Date
		 */
		pas2716_AutomatedRenewal(policyNumber,policyExpirationDate, PAS2716_VIN_NUMBER);
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDateMinus45(@Optional("CA") String state) {
		TestData testData = getTestDataWithSinceMembership(PAS2716_VIN_NUMBER);
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		uploadFiles(PAS2716_CONTROLTABLEFILE,PAS2716_VINTABLEFILE);
		/**
		 * Automated Renewal R-45 Expiration Date
		 */
		pas2716_AutomatedRenewal(policyNumber,policyExpirationDate.minusDays(45), PAS2716_VIN_NUMBER);
	}

	/**
	 * @author Viktor Petrenko
	 * <p>
	 * PAS-2716 Update VIN Refresh
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2716")
	public void pas2716_AutomatedRenewal_ExpirationDateMinus35(@Optional("CA") String state) {
		TestData testData = getTestDataWithSinceMembership(PAS2716_VIN_NUMBER);
		String policyNumber = createPreconds(testData);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		uploadFiles(PAS2716_CONTROLTABLEFILE, PAS2716_VINTABLEFILE);
		/**
		 * Automated Renewal R-35 Expiration Date
		 */
		pas2716_AutomatedRenewal(policyNumber,policyExpirationDate.minusDays(35), PAS2716_VIN_NUMBER);
	}

}
