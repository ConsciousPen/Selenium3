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

    /**
     * * @author Lev Kazarnovskiy
     *
     * PAS-1406 - Data Refresh - PAS-533 -Quote Refresh -Add New VIN
     *
     * See detailed steps in template file
     * {@link aaa.modules.regression.sales.template.functional.TestVINUploadTemplate#testVINUpload_NewVINAdded(String, String, String)}
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.MEDIUM })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-533")
    public void testVINUpload_NewVINAdded(@Optional("CA") String state) {
        super.testVINUpload_NewVINAdded("controlTable_CA_SELECT.xlsx", "uploadAddedVIN_CA_SELECT.xlsx", "BBBKN3DD0E0344466");
    }

    /**
     * * @author Lev Kazarnovskiy
     *
     * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
     *
     * See detailed steps in template file
     * {@link aaa.modules.regression.sales.template.functional.TestVINUploadTemplate#testVINUpload_NewVINAdded_Renewal(String, String, String)}
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.MEDIUM })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-527")
    public void testVINUpload_NewVINAdded_Renewal(@Optional("CA") String state) {
        super.testVINUpload_NewVINAdded_Renewal("controlTable_CA_SELECT.xlsx", "uploadAddedVIN_CA_SELECT.xlsx", "BBBKN3DD0E0344466");
    }

    /**
     * * @author Lev Kazarnovskiy
     *
     * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
     *
     * See detailed steps in template file
     * {@link aaa.modules.regression.sales.template.functional.TestVINUploadTemplate#testVINUpload_UpdatedVIN_Renewal(String, String, String)}
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.MEDIUM })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-527")
    public void testVINUpload_UpdatedVIN_Renewal(@Optional("CA") String state) {
        super.testVINUpload_UpdatedVIN_Renewal("controlTable_CA_SELECT.xlsx", "uploadUpdatedVIN_CA_SELECT.xlsx", "1HGEM215140028445");
    }
}
