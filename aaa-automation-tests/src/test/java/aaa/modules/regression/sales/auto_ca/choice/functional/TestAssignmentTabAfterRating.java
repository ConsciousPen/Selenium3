package aaa.modules.regression.sales.auto_ca.choice.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.modules.regression.sales.auto_ca.choice.TestPolicyCreationBig;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.table.Table;
import static toolkit.verification.CustomAssertions.assertThat;
import java.util.List;

@StateList(states = Constants.States.CA)
public class TestAssignmentTabAfterRating extends AutoCaChoiceBaseTest {

	/**
	 * @author Josh Carpenter
	 * @name Test that assignment is 'un-designated' when a trailer/motor home/camper/antique vehicle is added to a policy during NB
	 * @scenario
	 * 1. Create Customer
	 * 2. Initiate Auto CA Choice Policy with 2 drivers and fill up to P & C tab
	 * 3. Navigate to Vehicle tab and add one of the following vehicles - trailer, motor home, camper, antique
	 * 4. Navigate to P & C tab
	 * 5. Calculate premium
	 * 6. Navigate to Assignments tab
	 * 7. Verify driver assignments are 'un-designated' for vehicle from step 3, otherwise is the correct rated driver
     * 8. Repeat steps 3-7 for each vehicle type listed in step 3
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-16909")
	public void pas16909_testDriverAssignmentNB(@Optional("CA") String state) {
		createQuoteAndFillUpTo(getTdWithTwoDrivers(), PremiumAndCoveragesTab.class);
		validateDriverAssignment();

	}

    /**
     * @author Josh Carpenter
     * @name Test that assignment is 'un-designated' when a trailer/motor home/camper/antique vehicle is added to a policy during endorsement
     * @scenario
     * 1. Create Customer
     * 2. Create Auto CA Choice Policy with 2 drivers
     * 3. Initiate Endorsement
     * 3. Navigate to Vehicle tab and add one of the following vehicles - trailer, motor home, camper, antique
     * 4. Navigate to P & C tab
     * 5. Calculate premium
     * 6. Navigate to Assignments tab
     * 7. Verify driver assignments are 'un-designated' for vehicle from step 3, otherwise is the correct rated driver
     * 8. Repeat steps 3-7 for each vehicle type listed in step 3
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-16909")
    public void pas16909_testDriverAssignmentEndorsement(@Optional("CA") String state) {
        openAppAndCreatePolicy(getTdWithTwoDrivers());
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        validateDriverAssignment();

    }

    /**
     * @author Josh Carpenter
     * @name Test that assignment is 'un-designated' when a trailer/motor home/camper/antique vehicle is added to a policy during renewal
     * @scenario
     * 1. Create Customer
     * 2. Create Auto CA Choice Policy with 2 drivers
     * 3. Create renewal image
     * 3. Navigate to Vehicle tab and add one of the following vehicles - trailer, motor home, camper, antique
     * 4. Navigate to P & C tab
     * 5. Calculate premium
     * 6. Navigate to Assignments tab
     * 7. Verify driver assignments are 'un-designated' for vehicle from step 3, otherwise is the correct rated driver
     * 8. Repeat steps 3-7 for each vehicle type listed in step 3
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-16909")
    public void pas16909_testDriverAssignmentRenewal(@Optional("CA") String state) {
        openAppAndCreatePolicy(getTdWithTwoDrivers());
        policy.renew().perform();
        validateDriverAssignment();

    }

	private TestData getTdWithTwoDrivers() {
        TestData tdBig = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData");
        return getPolicyTD()
                .adjust(DriverTab.class.getSimpleName(), tdBig.getTestDataList(DriverTab.class.getSimpleName()))
                .adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName() + "[1]", AutoCaMetaData.DriverTab.FIRST_NAME.getLabel()), "Sally")
                .adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName() + "[1]", AutoCaMetaData.DriverTab.LAST_NAME.getLabel()), "Smith")
                .mask(TestData.makeKeyPath(DriverTab.class.getSimpleName() + "[1]", AutoCaMetaData.DriverTab.NAMED_INSURED.getLabel()));
    }

    private Table getAssignmentsTable() {
	    return new AssignmentTab().getAssetList().getAsset(AutoCaMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP).getTable();
    }

    private void validateDriverAssignment() {
        List<TestData> tdVehicles = getTestSpecificTD("TestData").getTestDataList("Vehicles");
        for (int i = 0; i < tdVehicles.size(); i++) {
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
            new VehicleTab().fillTab(tdVehicles.get(i)).submitTab();
            getAssignmentsTable().getRow(i + 2).getCell(PolicyConstants.AssignmentTabTable.PRIMARY_DRIVER).controls.comboBoxes.getFirst().setValueByIndex(i % 2 + 1);
            new PremiumAndCoveragesTab().calculatePremium();
            NavigationPage.toViewTab(NavigationEnum.AutoCaTab.ASSIGNMENT.get());
            Table assignmentTable = getAssignmentsTable();

            for (int j = 1; i <= assignmentTable.getRows().size(); j++) {
                if (j == 1) {
                    assertThat(assignmentTable.getRow(j).getCell(PolicyConstants.AssignmentTabTable.PRIMARY_DRIVER).controls.comboBoxes.getFirst().getValue())
                            .isEqualTo(assignmentTable.getRow(j).getCell(PolicyConstants.AssignmentTabTable.SYSTEM_RATED_DRIVER).getValue())
                            .isEqualTo(assignmentTable.getRow(j).getCell(PolicyConstants.AssignmentTabTable.MANUALLY_RATED_DRIVER).getValue())
                            .isNotEqualTo(PolicyConstants.AssignmentTabTable.UNDESIGNATED);
                } else {
                    assertThat(assignmentTable.getRow(j).getCell(PolicyConstants.AssignmentTabTable.SYSTEM_RATED_DRIVER).getValue())
                            .isEqualTo(assignmentTable.getRow(j).getCell(PolicyConstants.AssignmentTabTable.MANUALLY_RATED_DRIVER).getValue())
                            .isEqualTo(PolicyConstants.AssignmentTabTable.UNDESIGNATED);
                }
            }
        }
    }
}
