package aaa.modules.regression.sales.auto_ss.functional;


import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import static org.assertj.core.api.Assertions.doesNotHave;
import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author Lina Li
 * @name Test Mask Driver License Number for Auto Policy
 * @scenario
 * 1. Login PAS with group A30 (without the privilege "View Driver License Number")
 * 1. Create Customer
 * 2. Create Auto quote
 * 3. Login with E34 role check the DLN doesn't masked
 * 4. Login with L41 role check the DLN doesn't masked
 * 5. Login with G36 role check the DLN doesn't masked
 * 6. Login with Other role check check the DLN was masked
 * 7. Issue the quote
 * 8. Login with E34 role check the DLN doesn't masked
 * 9. Login with L41 role check the DLN doesn't masked
 * 10. Login with G36 role check the DLN doesn't masked
 * 11. Login with Other role check check the DLN was masked
 * @details
 */

public class TestMaskDLN extends AutoSSBaseTest {
	private String quoteNum;

	@Parameters({"state"})
	@StateList(statesExcept = { Constants.States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS,testCaseId = ("PASBB-953"+"PASBB-928"+"PASBB-931"))


	public void testMaskDLN(@Optional("") String state){
		//PASBB-931 Create the quote when the driver witout privilege
		mainApp().open(getLoginTD(Constants.UserGroups.A30));
		createCustomerIndividual();
		quoteNum = createQuote();
		mainApp().close();

         //PASBB-953, PASBB928 Inquiry quote to check mask DLN for Driver Tab, DAR tab
		//Login with E34 role with privilege
		verifyMaskDLN(Constants.UserGroups.E34, false);

		//Login with L41 role with privilege
		verifyMaskDLN(Constants.UserGroups.L41, false);

		//Login with G36 role with privilege
		verifyMaskDLN(Constants.UserGroups.G36, false);

		//Login with A30 without privilege
		verifyMaskDLN(Constants.UserGroups.F35, false);

		//PASBB-931 Bind the Quote when the driver without privilege
		mainApp().open(getLoginTD(Constants.UserGroups.A30));
		SearchPage.openQuote(quoteNum);
		policy.purchase(getPolicyTD());
		quoteNum = PolicySummaryPage.getPolicyNumber();
		mainApp().close();

         //PASBB-953, PASBB928 Inquiry quote to check mask DLN for Driver Tab, DAR tab
		//Login with E34 role with privilege
		verifyMaskDLN(Constants.UserGroups.E34, true);

		//Login with L41 role with privilege
		verifyMaskDLN(Constants.UserGroups.L41, true);

		//Login with G36 role with privilege
		verifyMaskDLN(Constants.UserGroups.G36, true);

		//Login with B31 without privilege
		verifyMaskDLN(Constants.UserGroups.F35, true);
	}

	private void verifyMaskDLN(Constants.UserGroups userGroups, boolean isPolicy) {
		mainApp().open(getLoginTD(userGroups));
		if(isPolicy){
			SearchPage.openPolicy(quoteNum);
		}else{
			SearchPage.openQuote(quoteNum);
		}
		policy.policyInquiry().start();

		String reg = "^[*]+[A-Za-z0-9]{4}$";

		switch (userGroups) {
			case E34:
				NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
				assertThat(new DriverTab().getInquiryAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()).getValue().toString()).doesNotContain("*");
				NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
				assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("License #").getValue()).doesNotContain("*");
				assertThat(DriverActivityReportsTab.tableInternalClaim.getRow(1).getCell("License #").getValue()).doesNotContain("*");
				break;
			case L41:
				NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
				assertThat(new DriverTab().getInquiryAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()).getValue().toString()).doesNotContain("*");
				NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
				assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("License #").getValue()).doesNotContain("*");
				assertThat(DriverActivityReportsTab.tableInternalClaim.getRow(1).getCell("License #").getValue()).doesNotContain("*");
				break;
			case G36:
				NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
				assertThat(new DriverTab().getInquiryAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()).getValue().toString()).doesNotContain("*");
				NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
				assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("License #").getValue()).doesNotContain("*");
				break;
			default:
				NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
				assertThat(new DriverTab().getInquiryAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()).getValue().toString().matches(reg));
				NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
				assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("License #").getValue().matches(reg));
		}
		mainApp().close();
	 }
	}

