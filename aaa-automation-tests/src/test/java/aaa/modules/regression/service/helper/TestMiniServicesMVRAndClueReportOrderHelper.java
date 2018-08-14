package aaa.modules.regression.service.helper;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.*;

public class TestMiniServicesMVRAndClueReportOrderHelper  extends PolicyBaseTest {
	private DriverTab driverTab = new DriverTab();
	private AddDriverRequest addDriverRequest = new AddDriverRequest();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();
	private TestMiniServicesGeneralHelper testMiniServicesGeneralHelper = new TestMiniServicesGeneralHelper();

	protected void pas16694_orderReports_not_Named_Insured_endorsementBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		addDriverRequest.firstName = "ClueNonChargeable";
		addDriverRequest.middleName = "Doc";
		addDriverRequest.lastName = "Activity";
		addDriverRequest.birthDate = "1999-01-31";
		addDriverRequest.suffix = "III";

		DriversDto addedDriver = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		String addedDriverOid = addedDriver.oid; //get OID for added driver

		//And update missing info for the driver
		updateDriverRequest.gender = "male";
		updateDriverRequest.licenseNumber = "995860596";
		updateDriverRequest.ageFirstLicensed = 18;
		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.relationToApplicantCd = "CH";
		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, addedDriverOid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriverResponse.driver.namedInsuredType).isEqualTo("Not a Named Insured")); //Make sure that added driver is NOT a Named Insured

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Order reports through service
		HelperCommon.orderReports(policyNumber, addedDriverOid);

		//Open Driver Activity reports tab in PAS
		PolicySummaryPage.buttonPendedEndorsement.click();
		policyType.get().dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

		//validate that CLUE report is ordered in PAS
		checkThatClueIsOrdered(2, "processing complete, with results information");

		//validate that MVR report is ordered in PAS
		checkThatMvrIsOrdered(addDriverRequest, 2, "Clear");
		DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
		driverActivityReportsTab.saveAndExit();

		helperMiniServices.endorsementRateAndBind(policyNumber);

		///////////Repeat with driver 2///////////

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		addDriverRequest.firstName = "MvrNonChargeable";
		addDriverRequest.middleName = "Doc";
		addDriverRequest.lastName = "Activity";
		addDriverRequest.birthDate = "1999-01-31";
		addDriverRequest.suffix = "III";

		addedDriver = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		addedDriverOid = addedDriver.oid; //get OID for added driver

		//And update missing info for the driver
		updateDriverRequest.gender = "male";
		updateDriverRequest.licenseNumber = "995860597";
		updateDriverRequest.ageFirstLicensed = 18;
		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.relationToApplicantCd = "CH";
		DriverWithRuleSets updateDriverResponse2 = HelperCommon.updateDriver(policyNumber, addedDriverOid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriverResponse2.driver.namedInsuredType).isEqualTo("Not a Named Insured")); //Make sure that added driver is NOT a Named Insured

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Order reports through service
		HelperCommon.orderReports(policyNumber, addedDriverOid);

		//Open Driver Activity reports tab in PAS
		PolicySummaryPage.buttonPendedEndorsement.click();
		policyType.get().dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

		//validate that CLUE report is ordered in PAS
		checkThatClueIsOrdered(3, "processing complete, results clear");

		//validate that MVR report is ordered in PAS
		checkThatMvrIsOrdered(addDriverRequest, 3, "Hit - Activity Found");
		driverActivityReportsTab.saveAndExit();

		helperMiniServices.endorsementRateAndBind(policyNumber);

	}
	private void checkThatClueIsOrdered(int tableRowIndex, String expectedClueResponse) {
		assertSoftly(softly -> {
			softly.assertThat(DriverActivityReportsTab.tableCLUEReports.getRows().size()).isEqualTo(tableRowIndex);

			softly.assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.RESIDENTIAL_ADDRESS.getLabel()).getValue()).isNotEmpty();

			softly.assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.REPORT.getLabel()).getValue()).isEqualToIgnoringCase("View CLUE");

			softly.assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.ORDER_DATE.getLabel()).getValue())
					.isEqualToIgnoringCase(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

			softly.assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.RECEIPT_DATE.getLabel()).getValue())
					.isNotBlank(); //it can be also past date if report has been ordered previously, hence checking only that it is not blank

			softly.assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.RESPONSE.getLabel()).getValue()).isEqualToIgnoringCase(expectedClueResponse); //will be handled by: PAS-17059 Not getting correct CLUE report response

			softly.assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.ADDRESS_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("Current");

			softly.assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.ORDER_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("Add Driver");
		});
	}

	private void checkThatMvrIsOrdered(AddDriverRequest addDriverRequest, int tableRowIndex, String expectedMvrResponse) {
		assertSoftly(softly -> {
			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRows().size()).isEqualTo(tableRowIndex);
			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.NAME_ON_LICENSE.getLabel()).getValue()).contains(addDriverRequest.firstName);

			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.DATE_OF_BIRTH.getLabel()).getValue()).isEqualToIgnoringCase("01/31/1999"); //the same as addDriverRequest.birthDate

			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.STATE.getLabel()).getValue()).isEqualToIgnoringCase(updateDriverRequest.stateLicensed);

			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.LICENSE_NO.getLabel()).getValue()).isEqualToIgnoringCase(updateDriverRequest.licenseNumber);

			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.LICENSE_STATUS.getLabel()).getValue()).containsIgnoringCase("VALID");

			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.REPORT.getLabel()).getValue()).isEqualToIgnoringCase("View MVR");

			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.ORDER_DATE.getLabel()).getValue())
					.isEqualToIgnoringCase(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.RECEIPT_DATE.getLabel()).getValue())
					.isNotBlank(); //it can be also past date if report has been ordered previously, hence checking only that it is not blank

			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.RESPONSE.getLabel()).getValue())
					.isEqualToIgnoringCase(expectedMvrResponse);
		});
	}


	protected void pas15077_orderReports_endorsementBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement - future dated, because otherwise Insurance Score Report must be ordered for newly added NI
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		PolicySummary response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertSoftly(softly -> softly.assertThat(response.transactionEffectiveDate).isEqualTo(endorsementDate));

		addDriverRequest.firstName = "ClueNonChargeable";
		addDriverRequest.middleName = "Doc";
		addDriverRequest.lastName = "Activity";
		addDriverRequest.birthDate = "1999-01-31";
		addDriverRequest.suffix = "III";

		DriversDto addedDriver = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		String addedDriverOid = addedDriver.oid; //get OID for added driver

		//And update missing info for the driver
		updateDriverRequest.gender = "male";
		updateDriverRequest.licenseNumber = "995860596";
		updateDriverRequest.ageFirstLicensed = 18;
		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.relationToApplicantCd = "SP";
		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, addedDriverOid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriverResponse.driver.namedInsuredType).isEqualTo("NI")); //Make sure that added driver is Named Insured

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Order reports through service
		HelperCommon.orderReports(policyNumber, addedDriverOid);

		//Open Driver Activity reports tab in PAS
		PolicySummaryPage.buttonPendedEndorsement.click();
		policyType.get().dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

		//validate that CLUE report is ordered in PAS
		checkThatClueIsOrdered(2, "processing complete, with results information");

		//validate that MVR report is ordered in PAS
		checkThatMvrIsOrdered(addDriverRequest, 2, "Clear");
		DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
		driverActivityReportsTab.saveAndExit();

		helperMiniServices.endorsementRateAndBind(policyNumber);

		///////////Repeat with driver 2///////////

		//Create pended endorsement - future dated, because otherwise Insurance Score Report must be ordered for newly added NI
		PolicySummary response2 = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertSoftly(softly -> softly.assertThat(response2.transactionEffectiveDate).isEqualTo(endorsementDate));

		addDriverRequest.firstName = "MvrNonChargeable";
		addDriverRequest.middleName = "Doc";
		addDriverRequest.lastName = "Activity";
		addDriverRequest.birthDate = "1999-01-31";
		addDriverRequest.suffix = "III";

		addedDriver = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		addedDriverOid = addedDriver.oid; //get OID for added driver

		//And update missing info for the driver
		updateDriverRequest.gender = "male";
		updateDriverRequest.licenseNumber = "995860597";
		updateDriverRequest.ageFirstLicensed = 18;
		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.relationToApplicantCd = "SP";
		DriverWithRuleSets updateDriverResponse2 = HelperCommon.updateDriver(policyNumber, addedDriverOid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriverResponse2.driver.namedInsuredType).isEqualTo("NI")); //Make sure that added driver is Named Insured

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Order reports through service
		HelperCommon.orderReports(policyNumber, addedDriverOid);

		//Open Driver Activity reports tab in PAS
		PolicySummaryPage.buttonPendedEndorsement.click();
		policyType.get().dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

		//validate that CLUE report is ordered in PAS
		checkThatClueIsOrdered(3, "processing complete, results clear");

		//validate that MVR report is ordered in PAS
		checkThatMvrIsOrdered(addDriverRequest, 3, "Hit - Activity Found");
		driverActivityReportsTab.saveAndExit();

		helperMiniServices.endorsementRateAndBind(policyNumber);

	}
}
