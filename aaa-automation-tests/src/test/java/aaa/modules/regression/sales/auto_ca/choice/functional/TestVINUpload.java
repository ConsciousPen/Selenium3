package aaa.modules.regression.sales.auto_ca.choice.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestVINUploadTemplate;
import toolkit.utils.TestInfo;

public class TestVINUpload extends TestVINUploadTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    /**
     * * @author Lev Kazarnovskiy
     * PAS-533 Quote Refresh -Add New VIN
     * PAS-1406 Data Refresh
     *
     * See detailed steps in template file
     * {@link TestVINUploadTemplate#newVinAdded(String, String, String)}
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-533,PAS-1406")
    public void pas533_newVinAdded(@Optional("CA") String state) {
        newVinAdded("controlTable_CA_Choice.xlsx", "uploadAddedVIN_CA_CHOICE_.xlsx", "BBBKN3DD0E0344466");
    }

    /**
     * * @author Lev Kazarnovskiy
     * PAS-1406 Data Refresh
     * PAS-527 Renewal Refresh -Add New VIN & Update Existing
     * See detailed steps in template file
     * {@link TestVINUploadTemplate#newVinAddedRenewal(String, String, String)}
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-527,PAS-1406")
    public void pas527_newVinAddedRenewal(@Optional("CA") String state) {
        newVinAddedRenewal("controlTable_CA_Choice.xlsx", "uploadAddedVIN_CA_CHOICE_.xlsx", "BBBKN3DD0E0344466");
    }

    /**
     *@author Lev Kazarnovskiy
     * PAS-1406 Data Refresh
     * PAS-527 Renewal Refresh -Add New VIN & Update Existing
     * See detailed steps in template file
     * {@link TestVINUploadTemplate#updatedVinRenewal(String, String, String)}
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-527,PAS-1406")
    public void pas527_updatedVinRenewal(@Optional("CA") String state) {
        updatedVinRenewal("controlTable_CA_Choice.xlsx", "uploadUpdatedVIN_CA_CHOICE.xlsx", "4T1BE30K46U656311");
    }
}
