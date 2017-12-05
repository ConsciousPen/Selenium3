package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestVINUploadTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestVINUpload extends TestVINUploadTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	//	TODO add logic to generate file names for CA states based on product type

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-1406 - Data Refresh - PAS-533 -Quote Refresh -Add New VIN
	 *
	 * See detailed steps in template file
	 * {@link aaa.modules.regression.sales.template.functional.TestVINUploadTemplate#testVINUpload_NewVINAdded(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-533")
	public void testVINUpload_NewVINAdded(@Optional("CA") String state) {
		testVINUpload_NewVINAdded(getSpecificUploadFile(UploadFileType.ADDED_VIN.get()), "1FDEU15H7KL055795");
	}

	/**
	 * @author Lev Kazarnovskiy
	 * PAS-4253 Restrict VIN Refresh by Vehicle Type
	 * See detailed steps in template file
	 * {@link aaa.modules.regression.sales.template.functional.TestVINUploadTemplate#pas4253_restrictVehicleRefreshNB(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-4253")
	public void pas4253_restrictVehicleRefreshNB(@Optional("CA") String state) {
		pas4253_restrictVehicleRefreshNB(getSpecificUploadFile(UploadFileType.ADDED_VIN.get()), "1FDEU15H7KL055795");
	}

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
	 *
	 * See detailed steps in template file
	 * {@link aaa.modules.regression.sales.template.functional.TestVINUploadTemplate#testVINUpload_NewVINAdded_Renewal(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-527")
	public void testVINUpload_NewVINAdded_Renewal(@Optional("CA") String state) {
		testVINUpload_NewVINAdded_Renewal(getSpecificUploadFile(UploadFileType.ADDED_VIN.get()), "1FDEU15H7KL055795");
	}

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
	 *
	 * See detailed steps in template file
	 * {@link aaa.modules.regression.sales.template.functional.TestVINUploadTemplate#testVINUpload_UpdatedVIN_Renewal(String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-527")
	public void testVINUpload_UpdatedVIN_Renewal(@Optional("CA") String state) {
		testVINUpload_UpdatedVIN_Renewal(getSpecificUploadFile(UploadFileType.UPDATED_VIN.get()), "1HGEM215140028445");
	}
}
