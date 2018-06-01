package aaa.modules.regression.sales.home_ca.dp3;

import aaa.common.Tab;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.util.ArrayList;

/**
 * @Author - Tyrone C Jemison
 * @Description - Currently WIP (5/31/18)
 */
public class TestCAFairPlanMutualExclusions extends HomeCaDP3BaseTest {

    static TestData dp3PolicyData;
    static TestData ho3PolicyData;
    static HelperCommon myHelper;

    /**
     * @Author - Tyrone C Jemison
     * @Description - Mixing and matching mutually exclusive endorsements. Should not be on same policy together.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void TS1_AC1ToAC5_MutuallyExclusiveEndorsementCombos(@Optional("") String state) {

        // Create HO3 Base Policy
        createHO3Policy(ho3PolicyData);
        // Initiate DP3 Quote from HO3 policy. Fill up to P&C Endorsements.
        startDP3Quote(dp3PolicyData);
        // Build ArrayList of Endorsement Labels
        ArrayList<String> endorsementsToAddByLabel = convertStringsToArrayList(
                HomeCaMetaData.EndorsementTab.DP_04_18.getLabel(),
                HomeCaMetaData.EndorsementTab.DP_04_75.getLabel(),
                HomeCaMetaData.EndorsementTab.DW_09_25.getLabel()
        );
        // Add endorsements DP 04 18, DP 04 75, and  DW 09 25.

        myHelper.addEndorsements(endorsementsToAddByLabel);
        myHelper.addFAIRPlanEndorsement(getPolicyType().getShortName());
    }

    /**
     * @Author - Tyrone C Jemison
     * @Description - Adding FPCECADP during renewal will remove the auto-applied DW 04 75 endorsement.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void TS2_AC6_RenewalBatch_AddingFPCECADP(@Optional("") String state) {

    }

    /**
     * @Author - Tyrone C Jemison
     * @Description - A renewal image with the FPCECADP endorsement will not automatically add the
     *  DW 04 75 endorsement.
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void TS2_AC7_RenewalBatch_NoDW0475(@Optional("") String state) {

    }

    public void createHO3Policy(TestData inputHO3TestData) {

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        inputHO3TestData = getTestSpecificTD("HO3PolicyData");
        createPolicy(inputHO3TestData);
    }

    public void startDP3Quote(TestData defaultPolicyData) {
        defaultPolicyData = getPolicyTD();
        defaultPolicyData.adjust(ApplicantTab.class.getSimpleName(), getTestSpecificTD("ApplicantTab_DP3"));
        defaultPolicyData.adjust(ReportsTab.class.getSimpleName(), getTestSpecificTD("ReportsTab_DP3"));
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, EndorsementTab.class, false);
    }

    public ArrayList<String> convertStringsToArrayList(String... allGiven) {
        ArrayList<String> createdList = new ArrayList<String>();
        for (String val : allGiven) {
            createdList.add(val);
        }

        return createdList;
    }
}
