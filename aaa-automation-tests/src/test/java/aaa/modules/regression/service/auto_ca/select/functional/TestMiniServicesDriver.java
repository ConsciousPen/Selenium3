package aaa.modules.regression.service.auto_ca.select.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesDriversHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestMiniServicesDriver extends TestMiniServicesDriversHelper {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-25057"})
    public void pas25057_AddDriverCADefaultValues() {
        pas25057_AddDriverCADefaultValuesBody(getPolicyType());
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15428"})
    public void pas15428_UpdateDriverPermitBeforeLicense() {
        pas15428_UpdateDriver_CABody(getPolicyType());
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15408"})
    public void pas15408_ValidateMetadataService_CA(@Optional("CA") String state) {
        pas15408_ValidateDriverMetadataService_CA(getPolicyType());
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15408"})
    public void pas15408_ViewDriverService_CA(@Optional("CA") String state) {
        pas15408_ViewDriverServiceCA_Body(getPolicyType());
    }


    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-25053"})
    public void pas25053_ViewDriverServiceCANameInsureIndicator(@Optional("CA") String state)
    {
        pas25053_ViewDriverServiceCANameInsureIndicator_body(getPolicyType());
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-25053"})
    public void pas22513_ViewDiscountDriver(@Optional("CA") String state)
    {
        pas22513_ViewDiscountDriverBody(getPolicyType());
    }
}
