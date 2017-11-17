package aaa.modules.regression.sales.auto_ca.select.functional;

import javax.xml.datatype.DatatypeConfigurationException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.soap.GetAutoPolicyDetailsHelper;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.soap.autopolicy.models.wsdl.ErrorInfo;
import aaa.soap.autopolicy.models.wsdl.GetAutoPolicyDetailResponse;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestGetAutoPolicyDetails extends AutoCaSelectBaseTest {
	/**
	* PAS-541
	*
	* @author Viktor Petrenko
	* @name Check comprehnsive and collision symbol presence at the response
	* @scenario 1. Create customer and auto CA selects policy with 2 Vehicles
	* 1. Create quote
	* 2. Request auto policy details
	* 3. Check symbol presence
	* @details
	*/
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void pas541_checkCompCollSymbolPresence(@Optional("") String state) throws ErrorInfo, DatatypeConfigurationException {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

		GetAutoPolicyDetailResponse actualResponse = GetAutoPolicyDetailsHelper.getAutoPolicyResponse(policyNumber);

		String vehicleCollSymbolCode = actualResponse.getAutoPolicySummary().getVehicles().getVehicle().get(0).getRiskFactors().getVehicleCollSymbolCode();
		String vehicleCompSymbolCode = actualResponse.getAutoPolicySummary().getVehicles().getVehicle().get(0).getRiskFactors().getVehicleCompSymbolCode();

		CustomAssert.enableSoftMode();
		log.info("\nComp Symbol Code is: {}", vehicleCompSymbolCode);
		CustomAssert.assertFalse("Comp Symbol Code is empty", vehicleCompSymbolCode.isEmpty());
		log.info("\nColl Symbol Code is: {}\n", vehicleCollSymbolCode);
		CustomAssert.assertFalse("Coll Symbol Code is empty", vehicleCollSymbolCode.isEmpty());
		CustomAssert.disableSoftMode();

		CustomAssert.assertAll();
	}
}
