package aaa.modules.regression.service.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import org.openqa.selenium.By;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Lina Li
 * @name Test Mask Driver License Number for Auto Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto policy
 * 3. Login with E34 role make endorsement other than driver check mask DLN PASBB-951
 * 4. Login with L41 role make endorsement to change driver license check mask DLN- PASBB-950
 * 5. Login with G36 role make out of sequence endorsement to check mask DLN PASBB-954
 * 6. Login with Other role to make endorsement to check mask DLN PASBB-950, PASBB-935
 * 7. Login with Other role to make out of sequence endorsement to check mask DLN PASBB-954, PASBB-935
 * @details
 */
public class TestMaskDlnEndorsement extends AutoSSBaseTest{
	private String policyNum;
	private DriverTab driverTab = new DriverTab();
	private DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private RatingDetailReportsTab ratingDetailReportsTab = new RatingDetailReportsTab();
	private Button compare = new Button(By.id("historyForm:compareVersions_footer"));
	private Table tableCompare = new Table(By.xpath("//div[@id='comparisonTreeForm:comparisonTree']/table"));


	@Parameters({"state"})
	@StateList(statesExcept = { Constants.States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS,testCaseId = "PASBB-951")

    // Login with E34 user, make endorsement other than driver (PASBB-951)
	public void testMaskDlnEndorsementOne (@Optional("") String state){
		mainApp().open(getLoginTD(Constants.UserGroups.E34));
		createCustomerIndividual();
		policyNum = createPolicy();
		TestData endorsementTD = getTestSpecificTD("TestData_EndorsementOne");
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		policy.dataGather().getView().fillUpTo(endorsementTD, driverTab.getClass(), true);
		assertThat(new DriverTab().getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()).getValue().toString()).doesNotContain("*");
		policy.dataGather().getView().fillFromTo(endorsementTD,driverTab.getClass(),
				driverActivityReportsTab.getClass(),true);
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("License #").getValue()).doesNotContain("*");
		assertThat(DriverActivityReportsTab.tableInternalClaim.getRow(1).getCell("License #").getValue()).doesNotContain("*");
		policy.dataGather().getView().fillFromTo(endorsementTD, driverActivityReportsTab.getClass(), documentsAndBindTab.getClass());
		documentsAndBindTab.submitTab();
		mainApp().close();
	}


    // Login with G36 user, make endorsement to change driver license number (PASBB-950)
	@Parameters({"state"})
	@StateList(statesExcept = { Constants.States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH},  dependsOnMethods = "testMaskDlnEndorsementOne")
	//@Test(groups = { Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS,testCaseId = "PASBB-950"+ "PASBB-943")

	public void testMaskDlnEndorsementTwo (@Optional("") String state){
		mainApp().open(getLoginTD(Constants.UserGroups.G36));
		SearchPage.openPolicy(policyNum);
		TestData endorsementTD = getTestSpecificTD("TestData_EndorsementTwo");
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus10Day"));
		policy.dataGather().getView().fillUpTo(endorsementTD, driverTab.getClass(), true);
		assertThat(new DriverTab().getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()).getValue().toString()).doesNotContain("*");
		driverTab.submitTab();
		policy.dataGather().getView().fillFromTo(endorsementTD,ratingDetailReportsTab.getClass(),driverActivityReportsTab.getClass(),true);
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("License #").getValue()).doesNotContain("*");
		policy.dataGather().getView().fillFromTo(endorsementTD, driverActivityReportsTab.getClass(), documentsAndBindTab.getClass());
		documentsAndBindTab.submitTab();

		// PASBB-943 verify compare page, DLN didn't mask
		PolicySummaryPage.TransactionHistory.open();
		PolicySummaryPage.tableTransactionHistory.getRow(1).getCell(1).click();
		PolicySummaryPage.tableTransactionHistory.getRow(3).getCell(1).click();
		compare.click();
		assertThat(tableCompare.getRow(3).getCell(2).getValue()).doesNotContain("*");
		assertThat(tableCompare.getRow(3).getCell(3).getValue()).doesNotContain("*");
		assertThat(tableCompare.getRow(7).getCell(2).getValue()).doesNotContain("*");
		assertThat(tableCompare.getRow(7).getCell(3).getValue()).doesNotContain("*");
		mainApp().close();
	}


	// PASBB-954 verify out of sequence compare page, DLN didn't mask
	@Parameters({"state"})
	@StateList(statesExcept = { Constants.States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH}, dependsOnMethods = "testMaskDlnEndorsementTwo")
	//@Test(groups = { Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS,testCaseId = "PASBB-954")

	public void testMaskDlnEndorsementThree (@Optional("") String state) {
		mainApp().open(getLoginTD(Constants.UserGroups.L41));
		SearchPage.openPolicy(policyNum);
		TestData endorsementTD = getTestSpecificTD("TestData_EndorsementThree");
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus5Day"));
		policy.dataGather().getView().fillUpTo(endorsementTD, driverTab.getClass(), true);
		assertThat(new DriverTab().getAssetList().getAsset(AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()).getValue().toString()).doesNotContain("*");
		policy.dataGather().getView().fillFromTo(endorsementTD,driverTab.getClass(), driverActivityReportsTab.getClass(),true);
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell("License #").getValue()).doesNotContain("*");
		assertThat(DriverActivityReportsTab.tableInternalClaim.getRow(1).getCell("License #").getValue()).doesNotContain("*");
		policy.dataGather().getView().fillFromTo(endorsementTD, driverActivityReportsTab.getClass(), documentsAndBindTab.getClass());
		documentsAndBindTab.submitTab();
		CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.PENDING_OUT_OF_SEQUENCE_COMPLETION);

		policy.rollOn();
		policy.rollOn().openConflictPage(false);
		assertThat(tableCompare.getRow(7).getCell(2).getValue()).doesNotContain("*");
		assertThat(tableCompare.getRow(7).getCell(3).getValue()).doesNotContain("*");
		assertThat(tableCompare.getRow(11).getCell(2).getValue()).doesNotContain("*");
		assertThat(tableCompare.getRow(11).getCell(3).getValue()).doesNotContain("*");

	}
}
