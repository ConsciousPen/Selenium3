package aaa.modules.regression.sales.auto_ca.choice.functional;

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
        return PolicyType.AUTO_CA_CHOICE;
    }

    /**
     * * @author Lev Kazarnovskiy
     *
     * PAS-1406 - Data Refresh - PAS-533 -Quote Refresh -Add New VIN
     *
     * See detailed steps in template file
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.MEDIUM })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
    public void testVINUpload_NewVINAdded(@Optional("CA") String state) {
        super.testVINUpload_NewVINAdded("VINconfig_CA_CHOICE.xlsx", "VINupload_CA_CHOICE.xlsx", "BBBKN3DD0E0344466");
    }

    /**
     * * @author Lev Kazarnovskiy
     *
     * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
     *
     * See detailed steps in template file
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.MEDIUM })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
    public void testVINUpload_NewVINAdded_Renewal(@Optional("CA") String state) {
        super.testVINUpload_NewVINAdded_Renewal("VINconfig_CA_CHOICE.xlsx", "VINupload_CA_CHOICE.xlsx", "BBBKN3DD0E0344466");
    }

    /**
     * * @author Lev Kazarnovskiy
     *
     * PAS-1406 - Data Refresh - PAS-527 -Renewal Refresh -Add New VIN & Update Existing
     *
     * See detailed steps in template file
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.MEDIUM })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
    public void testVINUpload_UpdatedVIN_Renewal(@Optional("CA") String state) {
        super.testVINUpload_UpdatedVIN_Renewal("VINconfig_CA_CHOICE.xlsx", "VINupload_CA_CHOICE_UPDATE.xlsx", "4T1BE30K46U656311");
    }
}
