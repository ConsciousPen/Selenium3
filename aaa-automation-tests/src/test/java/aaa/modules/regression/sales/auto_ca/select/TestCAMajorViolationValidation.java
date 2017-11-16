/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select;



import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.modules.policy.AutoCaSelectBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name Test Create
 * @scenario
 * 1. Create Customer with CA state
 * 2. Create Quote with CA State
 * 3. Add 2 Major Violations and one 10year major violation
 * 4. Navigate to Driver Activity Reports Tab
 * 5. Validate Error message
 * @details
 */
public class TestCAMajorViolationValidation extends AutoCaSelectBaseTest {

    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-5843")
    public void testCAMajorViolationValidation(@Optional("CA") String state) {
    	
        mainApp().open();
        createCustomerIndividual();

        TestData testData = getPolicyTD();
        testData.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName()), getTestSpecificTD("TestData").getTestData(new DriverTab().getMetaKey()));

        policy.initiate();
        policy.getDefaultView().fillUpTo(testData, aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab.class);

        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());

        new ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_CAC7150833);



    }
}
