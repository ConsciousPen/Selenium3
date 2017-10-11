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
     *
     *See detailed steps in template file
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.MEDIUM })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
    public void testVINUpload_NewVINAdded(@Optional("CA") String state) {
        super.testVINUpload_NewVINAdded("VINconfig_CA_SELECT.xlsx", "VINupload_CA_SELECT.xlsx", "BBBKN3DD0E0344466");
    }

    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.MEDIUM })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
    public void testVINUpload_NewVINAdded_Renewal(@Optional("CA") String state) {
        super.testVINUpload_NewVINAdded_Renewal("VINconfig_CA_SELECT.xlsx", "VINupload_CA_SELECT.xlsx", "BBBKN3DD0E0344466");
    }

    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.MEDIUM })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
    public void testVINUpload_UpdatedVIN_Renewal(@Optional("CA") String state) {
        super.testVINUpload_UpdatedVIN_Renewal("VINconfig_CA_SELECT.xlsx", "VINupload_CA_SELECT_UPDATE.xlsx", "1HGEM215140028445");
    }
}
