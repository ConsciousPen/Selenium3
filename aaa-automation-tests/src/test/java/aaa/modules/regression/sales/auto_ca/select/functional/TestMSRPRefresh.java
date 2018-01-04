package aaa.modules.regression.sales.auto_ca.select.functional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.queries.LookupQueries;
import aaa.modules.regression.queries.postconditions.DatabaseCleanHelper;
import aaa.modules.regression.sales.template.functional.TestMSRPRefreshTemplate;
import toolkit.utils.TestInfo;

public class TestMSRPRefresh extends TestMSRPRefreshTemplate implements LookupQueries {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	private static final String UPDATABLE_VIN = "1HGEM215140028445";
	private static final String NEW_VIN = "1FDEU15H7KL055795";

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_MSRPRefreshCompColl(@Optional("CA") String state) {
		MSRPRefresh();
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
		DatabaseCleanHelper.cleanVinUploadTables("('SYMBOL_2000_CA_SELECT')", getState());
		vinMethods.enableVinRefresh(false);
	}
}
