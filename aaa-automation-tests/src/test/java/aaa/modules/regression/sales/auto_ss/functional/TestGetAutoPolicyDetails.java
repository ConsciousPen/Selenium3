package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import javax.xml.datatype.DatatypeConfigurationException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.soap.GetAutoPolicyDetailsHelper;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.soap.autopolicy.models.wsdl.ErrorInfo;
import aaa.soap.autopolicy.models.wsdl.GetAutoPolicyDetailResponse;
import toolkit.utils.TestInfo;

public class TestGetAutoPolicyDetails extends AutoSSBaseTest {
	/**
	* PAS-541
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-541")
	public void pas541_checkCompCollSymbolPresence(@Optional("") String state) throws ErrorInfo, DatatypeConfigurationException {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

		GetAutoPolicyDetailResponse actualResponse = new GetAutoPolicyDetailsHelper().getAutoPolicyResponse(policyNumber);

		String vehicleCollSymbolCode = actualResponse.getAutoPolicySummary().getVehicles().getVehicle().get(0).getRiskFactors().getVehicleCollSymbolCode();
		String vehicleCompSymbolCode = actualResponse.getAutoPolicySummary().getVehicles().getVehicle().get(0).getRiskFactors().getVehicleCompSymbolCode();

		assertSoftly(softly -> {
			log.info("\nComp Symbol Code is: {}\n", vehicleCompSymbolCode);
			softly.assertThat(vehicleCompSymbolCode).isNotEmpty();
			log.info("\nColl Symbol Code is: {}\n", vehicleCollSymbolCode);
			softly.assertThat(vehicleCollSymbolCode).isNotEmpty();
		});

	}
}
