package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestYearsClaimFreeCalculation extends HomeSSHO3BaseTest
{
    /**
     * 1. Create SS HO/DP policy. <br>
     * 2. At new business no internal claim is returned. <br>
     * 3. Move to renewal time frame. <br>
     * 4. Batch order internal claims report at renewal. <br>
     * 5. Reports return at least one chargeable Internal Claim from CAS. <br>
     * 6. Review YCF value (Years of Claim Free) in the VRD screen. <br>
     *     @author - Tyrone Jemison
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-19133 Internal claims are not being included in calculating years of claim free (YCF)")
    @TestInfo(component = ComponentConstant.Service.HOME_SS_HO3,  testCaseId = "PAS-19133")
    public void pas19133_testYearsClaimFreeCalculatedCorrectly(@Optional("") String state) {
        //Test Level Data
        TestData td = getPolicyDefaultTD();
    }
}
