package aaa.modules.regression.sales.auto_ca.select.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.ErrorTab;
import aaa.modules.policy.AutoCaSelectBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Dominykas Razgunas
 * @name TestMVRReportInvalidError
 * @scenario
 * 1. Create Customer 
 * 2. Create CA Select Auto Quote
 * @details
 */
public class TestMVRReportInvalidError extends AutoCaSelectBaseTest {

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
	public void pas29837_testMVRReportInvalidError(@Optional("CA") String state) {

		TestData tdCustomer = getCustomerIndividualTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()),"Jean")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()),"Fields");
		TestData tdPolicy = getPolicyTD()
				.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "A3222003");

		mainApp().open();
		createCustomerIndividual(tdCustomer);
		policy.initiate();
		policy.getDefaultView().fillUpTo(tdPolicy, DriverActivityReportsTab.class,true);
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		new DocumentsAndBindTab().fillTab(tdPolicy);

		new ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_CAC7150833);

		}
	}
