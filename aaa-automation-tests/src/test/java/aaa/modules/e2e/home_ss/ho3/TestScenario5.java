package aaa.modules.e2e.home_ss.ho3;

import org.testng.annotations.Test;
import toolkit.datax.TestData;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario5;

public class TestScenario5 extends Scenario5 {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }

    @Test
    public void TC01_createPolicy() {
        tdPolicy = testDataManager.policy.get(getPolicyType());

        TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

        super.createTestPolicy(policyCreationTD);
    }

    @Test(dependsOnMethods = "TC01_createPolicy")
    public void TC02_Generate_First_Bill() {
        super.TC02_Generate_First_Bill();
    }

    @Test(dependsOnMethods = "TC01_createPolicy")
    public void TC03_Pay_First_Bill() {
        super.TC03_Pay_First_Bill();
    }

    @Test(dependsOnMethods = "TC01_createPolicy")
    public void TC04_Generate_Second_Bill() {
        super.TC04_Generate_Second_Bill();
    }

    @Test(dependsOnMethods = "TC01_createPolicy")
    public void TC05_Pay_Second_Bill() {
        super.TC05_Pay_Second_Bill();
    }

    @Test(dependsOnMethods = "TC01_createPolicy")
    public void TC06_Decline_Payments() {
        super.TC06_Decline_Payments();
    }

    @Test(dependsOnMethods = "TC01_createPolicy")
    public void TC07_Generate_CancellNotice() {
        super.TC07_Generate_CancellNotice();
    }

    @Test(dependsOnMethods = "TC01_createPolicy")
    public void TC08_Verify_Form_AH34XX() {
        super.TC08_Verify_Form_AH34XX();
    }
}
