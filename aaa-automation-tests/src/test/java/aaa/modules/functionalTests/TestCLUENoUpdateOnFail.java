package aaa.modules.functionalTests;

import aaa.helpers.QAHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCLUENoUpdateOnFail extends AutoSSBaseTest
{
    @Parameters({"state"})
    @BeforeClass
    public void testSetup(@Optional("") String state)
    {

    }

    @Parameters({"state"})
    @Test(groups= {Groups.SMOKE})
    @TestInfo(component= ComponentConstant.Sales.AUTO_CA_SELECT)
    public void createPreconditions(@Optional("") String state)
    {
        // Must create a helper class object. Cannot be static as it uses non-static methods and objects.
        QAHelper _myHelper = new QAHelper();

        // Create and Define Test Data To Use in Run
        //TestData _myTD = _myHelper.getTestData_BySameClassName("TestData");
        TestData _myTD = getTestSpecificTD("TestData");

        // Launching App
        _myHelper.launchApp();

        // Create Customer with helper class obj.
        _myHelper.createCustomer(true, _myTD);

        // Create a quote.
    }

    @Parameters({"state"})
    @Test(groups= {Groups.SMOKE})
    @TestInfo(component= ComponentConstant.Sales.AUTO_CA_SELECT)
    public void moveJVMTimeForward(@Optional("MT") String state)
    {

    }

}
