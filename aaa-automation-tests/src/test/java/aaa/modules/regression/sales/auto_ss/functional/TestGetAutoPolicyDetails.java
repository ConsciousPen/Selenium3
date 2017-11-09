package aaa.modules.regression.sales.auto_ss.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.soap.GetAutoPolicyDetailsHelper;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.soap.autopolicy.models.wsdl.ErrorInfo;
import aaa.soap.autopolicy.models.wsdl.GetAutoPolicyDetailResponse;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import javax.xml.datatype.DatatypeConfigurationException;

public class TestGetAutoPolicyDetails extends AutoSSBaseTest {

    @Parameters({"state"})
    @Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-541")
    public void checkCompCollSymbolPresence() throws ErrorInfo, DatatypeConfigurationException {
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = createPolicy();

        GetAutoPolicyDetailResponse actualResponse = GetAutoPolicyDetailsHelper.getAutoPolicyResponse(policyNumber);

        String vehicleCollSymbolCode = actualResponse.getAutoPolicySummary().getVehicles().getVehicle().get(0).getRiskFactors().getVehicleCollSymbolCode();
        String vehicleCompSymbolCode = actualResponse.getAutoPolicySummary().getVehicles().getVehicle().get(0).getRiskFactors().getVehicleCompSymbolCode();

        CustomAssert.enableSoftMode();
        log.info("\nComp Symbol Code is: " + vehicleCompSymbolCode);
        CustomAssert.assertFalse("Comp Symbol Code is empty",vehicleCompSymbolCode.isEmpty());
        log.info("\nColl Symbol Code is: " + vehicleCollSymbolCode + "\n");
        CustomAssert.assertFalse("Coll Symbol Code is empty",vehicleCollSymbolCode.isEmpty());
        CustomAssert.disableSoftMode();

        CustomAssert.assertAll();
    }
}
