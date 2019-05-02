package aaa.modules.regression.service.auto_ca.select.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.helper.TestMiniServicesDriversCAHelper;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestMiniServicesDriver extends TestMiniServicesDriversCAHelper {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * @author Sabra Domeika
     * @name Add Driver Service for CA
     * @scenario
     * 1. Create a new endorsement through DXP.
     * 2. Add a new driver through DXP.
     * 3. Validate the response in DXP. Make sure values that should not be defaulted are null. Make sure the fields
     *  that should be defaulted are defaulted.
     * 4. Validate the fields in the UI and make sure they're all populated as expected.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-25057"})
    public void pas25057_AddDriverCADefaultValues() {
        pas25057_AddDriverCADefaultValuesBody();
    }

    /**
     * @author Sabra Domeika
     * @name Update Driver Service for CA
     * @scenario
     * 1. Create a new endorsement through DXP.
     * 2. Add a new driver through DXP.
     * 3. Update the driver through DXP.
     * 4. Validate the response is returned as expected.
     * 5. Navigate to the PAS UI and validate that the fields are all populated as expected.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15428"})
    public void pas15428_UpdateDriverPermitBeforeLicense() {
        pas15428_UpdateDriver_CABody();
    }

    /**
     * @author Sabra Domeika
     * @name Metadata Driver Service for CA
     * @scenario
     * 1. Create a new endorsement through DXP.
     * 2. Run the metadata service for the FNI driver on the policy.
     * 3. Validate the lookups for each field.
     * 4. Validate that the appropriate fields are shown as enabled and displayed.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = {"PAS-15408"})
    public void pas15408_ValidateMetadataService_CA(@Optional("CA") String state) {
        pas15408_ValidateDriverMetadataService_CA();
    }

    /**
     * @author Sabra Domeika
     * @name Update Driver Service for CA
     * @scenario
     * 1. Create a policy in PAS with a ton of people.
     * 2. Create an endorsement through DXP.
     * 3. Run the view driver service.
     * 4. Validate that the drivers display the correct details, including named insured information and mapping of
     * relationship to Named Insured to Child and Parent, where applicable.
     */
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
