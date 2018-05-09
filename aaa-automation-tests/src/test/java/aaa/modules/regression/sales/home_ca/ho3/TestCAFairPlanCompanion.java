package aaa.modules.regression.sales.home_ca.ho3;

import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * @author Tyrone C Jemison
 */
public class TestCAFairPlanCompanion extends HomeCaHO3BaseTest {
    /**
     * @scenario
     * 1. Initiate a HO3 Quote.
     * 2. Observe FPCECA is visible.
     * 3. After adding FPCECA, FPCECA removed from Optional Endorsements
     * 4. After Adding FPCECA, FPCECA visible in Documents Tab and Quote Tab.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC1AC4_Quote_VisibleFPCECA(@Optional("") String state) {
    }

    /**
     * @scenario
     * 1. Create a HO3 Quote.
     * 2. Bind Quote.
     * 3. Initiate Mid-Term Endorsement.
     * 4. Observe FPCECA is visible.
     * 5. After opting to add FPCECA, a message is displayed.
     * 6. Verify message will match mock data.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC2AC5_Endorsement_VisibleFPCECA(@Optional("") String state) {
    }

    /**
     * @scenario
     * 1. Create a HO3 Quote.
     * 2. Bind Quote.
     * 3. Advance JVM to TP1, run renewal jobs.
     * 4. Advance JVM to TP2, run renewal jobs.
     * 5. Retrieve Renewal Image
     * 6. Observe FPCECA is visible.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC3_Renewal_VisibleFPCECA(@Optional("") String state) {
    }
}
