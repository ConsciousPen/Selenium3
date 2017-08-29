package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.soap.GetAutoPolicyDetailsHelper;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.soap.getAutoPolicyDetails.aaancnu_wsdl_getautopolicydetail_version2.ErrorInfo;
import aaa.soap.getAutoPolicyDetails.aaancnu_wsdl_getautopolicydetail_version2.GetAutoPolicyDetailResponse;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import javax.xml.datatype.DatatypeConfigurationException;

public class TestGetAutoPolicyDetails extends AutoCaSelectBaseTest {

    @Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
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
