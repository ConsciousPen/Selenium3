package aaa.modules.regression.sales.auto_ca.choice.functional;

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
		return PolicyType.AUTO_CA_CHOICE;
	}

//	TODO add logic to generate file names for CA states based on product type

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-1406 - Data Refresh - PAS-533 -Quote Refresh -Add New VIN
	 *
	 * See detailed steps in template file
	 * {@link aaa.modules.regression.sales.template.functional.TestVINUploadTemplate#testVINUpload_NewVINAdded(String, String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-533")
	public void testVINUpload_NewVINAdded(@Optional("CA") String state) {
		testVINUpload_NewVINAdded("controlTable_CA_Choice.xlsx", "uploadAddedVIN_CA_CHOICE_.xlsx", "1FDEU15H7KL055795");
	}

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
	 *
	 * See detailed steps in template file
	 * {@link aaa.modules.regression.sales.template.functional.TestVINUploadTemplate#testVINUpload_NewVINAdded_Renewal(String, String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-527")
	public void testVINUpload_NewVINAdded_Renewal(@Optional("CA") String state) {
		testVINUpload_NewVINAdded_Renewal("controlTable_CA_Choice.xlsx", "uploadAddedVIN_CA_CHOICE_.xlsx", "1FDEU15H7KL055795");
	}

	/**
	 * @author Lev Kazarnovskiy
	 *
	 * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
	 *
	 * See detailed steps in template file
	 * {@link aaa.modules.regression.sales.template.functional.TestVINUploadTemplate#testVINUpload_UpdatedVIN_Renewal(String, String, String)}
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-527")
	public void testVINUpload_UpdatedVIN_Renewal(@Optional("CA") String state) {
		testVINUpload_UpdatedVIN_Renewal("controlTable_CA_Choice.xlsx", "uploadUpdatedVIN_CA_CHOICE.xlsx", "4T1BE30K46U656311");
	}
}
