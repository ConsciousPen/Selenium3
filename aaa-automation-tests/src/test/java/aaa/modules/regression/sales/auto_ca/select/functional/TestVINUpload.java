package aaa.modules.regression.sales.auto_ca.select.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.common_helpers.VinUploadCommonMethods.*;
import aaa.modules.regression.sales.template.functional.TestVINUploadTemplate;
import toolkit.utils.TestInfo;

public class TestVINUpload extends TestVINUploadTemplate {

	private String newVin = "1FDEU15H7KL055795";
	private String updatableVin = "1HGEM215140028445";

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-533 Quote Refresh -Add New VIN
	 * PAS-1406 ata Refresh
	 *
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#newVinAdded(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-533")
	public void pas533_newVinAdded(@Optional("CA") String state) {
		newVinAdded(vinMethods.getSpecificUploadFile(UploadFilesTypes.ADDED_VIN.get()), newVin);
	}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-4253 Restrict VIN Refresh by Vehicle Type
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#pas4253_restrictVehicleRefreshNB(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-4253")
	public void pas4253_restrictVehicleRefreshNB(@Optional("CA") String state) {
		pas4253_restrictVehicleRefreshNB(vinMethods.getSpecificUploadFile(UploadFilesTypes.ADDED_VIN.get()), newVin);
	}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-527 Renewal Refresh -Add New VIN & Update Existing
	 * PAS-1406 - Data Refresh
	 *
	 * See detailed steps in template file
	 * {@link TestVINUploadTemplate#newVinAddedRenewal(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-527")
	public void pas527_newVinAddedRenewal(@Optional("CA") String state) {
		newVinAddedRenewal(vinMethods.getSpecificUploadFile(UploadFilesTypes.ADDED_VIN.get()), newVin);
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
	public void pas527_updatedVinRenewal(@Optional("CA") String state) {
		updatedVinRenewal(vinMethods.getSpecificUploadFile(UploadFilesTypes.UPDATED_VIN.get()), updatableVin);
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-2714")
	public void pas2714_Endorsement(@Optional("CA") String state) {
		endorsement(vinMethods.getSpecificUploadFile(UploadFilesTypes.ADDED_VIN.get()), newVin);
	}
}
