package aaa.modules.regression.service.helper;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.service.helper.dtoDxp.*;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import javax.ws.rs.core.Response;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class TestMiniServicesMVRAndClueReportOrderHelper extends PolicyBaseTest {
	private DriverTab driverTab = new DriverTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();

	protected void pas16694_orderReports_not_Named_Insured_endorsementBody(PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add driver
		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest("ClueNonChargeable", "Doc", "Activity", "1999-01-31", "III");
		DriversDto addedDriver = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);

		//Update driver
		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("male", "995860596", 18, "VA", "CH", null);
		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, addedDriver.oid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriverResponse.driver.namedInsuredType).isEqualTo("Not a Named Insured")); //Make sure that added driver is NOT a Named Insured

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Order reports through service
		HelperCommon.orderReports(policyNumber, addedDriver.oid, OrderReportsResponse.class, 200);

		//Open Driver Activity reports tab in PAS
		PolicySummaryPage.buttonPendedEndorsement.click();
		policyType.get().dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

		//validate that CLUE report is ordered in PAS
		checkThatClueIsOrdered(2, "processing complete, with results information");

		//validate that MVR report is ordered in PAS
		checkThatMvrIsOrdered(2, "Clear", addDriverRequest, updateDriverRequest);
		DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
		driverActivityReportsTab.saveAndExit();

		helperMiniServices.endorsementRateAndBind(policyNumber);

		///////////Repeat with driver 2///////////

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add driver
		addDriverRequest = DXPRequestFactory.createAddDriverRequest("MvrNonChargeable", "Doc", "Activity", "1999-01-31", "III");
		DriversDto addedDriver2 = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);

		//Update driver
		updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("male", "995860597", 18, "VA", "CH", null);
		DriverWithRuleSets updateDriverResponse2 = HelperCommon.updateDriver(policyNumber, addedDriver2.oid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriverResponse2.driver.namedInsuredType).isEqualTo("Not a Named Insured")); //Make sure that added driver is NOT a Named Insured

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Order reports through service
		HelperCommon.orderReports(policyNumber, addedDriver2.oid, OrderReportsResponse.class, 200);

		//Open Driver Activity reports tab in PAS
		PolicySummaryPage.buttonPendedEndorsement.click();
		policyType.get().dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

		//validate that CLUE report is ordered in PAS
		checkThatClueIsOrdered(3, "processing complete, results clear");

		//validate that MVR report is ordered in PAS
		checkThatMvrIsOrdered(3, "Hit - Activity Found", addDriverRequest, updateDriverRequest);
		driverActivityReportsTab.saveAndExit();

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas15077_orderReports_endorsementBody(PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement - future dated, because otherwise Insurance Score Report must be ordered for newly added NI
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		PolicySummary response = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertSoftly(softly -> softly.assertThat(response.transactionEffectiveDate).isEqualTo(endorsementDate));

		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest("ClueNonChargeable", "Doc", "Activity", "1999-01-31", "III");
		DriversDto addedDriver = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);

		//And update missing info for the driver
		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("male", "995860596", 18, "VA", "SP", null);
		DriverWithRuleSets updateDriver = HelperCommon.updateDriver(policyNumber, addedDriver.oid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriver.driver.namedInsuredType).isEqualTo("NI")); //Make sure that added driver is Named Insured

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Order reports through service
		HelperCommon.orderReports(policyNumber, addedDriver.oid, OrderReportsResponse.class, 200);

		//Open Driver Activity reports tab in PAS
		PolicySummaryPage.buttonPendedEndorsement.click();
		policyType.get().dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

		//validate that CLUE report is ordered in PAS
		checkThatClueIsOrdered(2, "processing complete, with results information");

		//validate that MVR report is ordered in PAS
		checkThatMvrIsOrdered(2, "Clear", addDriverRequest, updateDriverRequest);
		DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
		driverActivityReportsTab.saveAndExit();

		helperMiniServices.endorsementRateAndBind(policyNumber);

		///////////Repeat with driver 2///////////

		//Create pended endorsement - future dated, because otherwise Insurance Score Report must be ordered for newly added NI
		PolicySummary response2 = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertSoftly(softly -> softly.assertThat(response2.transactionEffectiveDate).isEqualTo(endorsementDate));

		addDriverRequest = DXPRequestFactory.createAddDriverRequest("MvrNonChargeable", "Doc", "Activity", "1999-01-31", "III");
		addedDriver = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);

		//And update missing info for the driver

		updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("male", "995860597", 18, "VA", "SP", null);
		DriverWithRuleSets updateDriver2 = HelperCommon.updateDriver(policyNumber, addedDriver.oid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriver2.driver.namedInsuredType).isEqualTo("NI")); //Make sure that added driver is Named Insured

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		//Order reports through service
		HelperCommon.orderReports(policyNumber, addedDriver.oid, OrderReportsResponse.class, 200);

		//Open Driver Activity reports tab in PAS
		PolicySummaryPage.buttonPendedEndorsement.click();
		policyType.get().dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

		//validate that CLUE report is ordered in PAS
		checkThatClueIsOrdered(3, "processing complete, results clear");

		//validate that MVR report is ordered in PAS
		checkThatMvrIsOrdered(3, "Hit - Activity Found", addDriverRequest, updateDriverRequest);
		driverActivityReportsTab.saveAndExit();

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas15384_moreThanTwoMinorViolationsErrorBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Check driver with more that two minor violations
		String oidDriver1 = addAndUpdateDriver(policyNumber, "Three", "Minors", "1970-01-01", "B15384001");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS_VA.getCode(), ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS_VA.getMessage(), "attributeForRules", true);

		countViolationsInPas(policyNumber, 3);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS_VA.getCode(), ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS_VA.getMessage(), "attributeForRules");

		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		//Check Driver with one outdated violation
		String oidDriver2 = addAndUpdateDriver(policyNumber, "Outdated", "Minor", "1970-01-01", "B15384003");

		HelperCommon.orderReports(policyNumber, oidDriver2, OrderReportsResponse.class, 200);
		countViolationsInPas(policyNumber, 3);
		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas15371_driversWithNarcoticsDrugOrFelonyConvictionsErrorBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Check driver with more that two minor violations
		String oidDriver1 = addAndUpdateDriver(policyNumber, "One", "Felony", "1970-01-01", "B15371001");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getMessage(), "attributeForRules", true);

		countViolationsInPas(policyNumber, 1);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getMessage(), "attributeForRules");
	}

	protected void pas15370_driverWithMoreThanTwentyPointsErrorBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Check driver with more than 20 points
		String oidDriver1 = addAndUpdateDriver(policyNumber, "Twenty", "Points", "1970-01-01", "B16848001");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS_VA.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS_VA.getMessage(), "attributeForRules", true);

		countViolationsInPas(policyNumber, 5);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS_VA.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS_VA.getMessage(), "attributeForRules");

		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		//Check Driver with one outdated violation
		String oidDriver2 = addAndUpdateDriver(policyNumber, "Outdated", "Twenty", "1970-01-01", "B16848004");

		helperMiniServices.orderReportErrors(policyNumber, oidDriver2, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS_VA.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS_VA.getMessage(), "attributeForRules", false);

		countViolationsInPas(policyNumber, 5);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		ErrorResponseDto binErrorResponseDto = HelperCommon.endorsementBindError(policyNumber, oidDriver2, 422);
		assertSoftly(softly -> {
			softly.assertThat(binErrorResponseDto.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(binErrorResponseDto.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(binErrorResponseDto.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getCode());
			softly.assertThat(binErrorResponseDto.errors.get(0).message).contains(ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getMessage());
			softly.assertThat(binErrorResponseDto.errors.get(0).field).isEqualTo("attributeForRules");
			softly.assertThat(binErrorResponseDto.errors.get(1).errorCode).isEqualTo(ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_VA.getCode());
			softly.assertThat(binErrorResponseDto.errors.get(1).message).contains(ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_VA.getMessage());
			softly.assertThat(binErrorResponseDto.errors.get(1).field).isEqualTo("attributeForRules");
		});
	}

	protected void pas15385_driverWithFourOrMoreIncidentsErrorBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Check driver with 4 incidents
		String oidDriver1 = addAndUpdateDriver(policyNumber, "Four", "Incidents", "1970-01-01", "B16848123");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_THREE_INCIDENTS.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_THREE_INCIDENTS.getMessage(), "attributeForRules", true);

		countViolationsInPas(policyNumber, 4);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_THREE_INCIDENTS.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_THREE_INCIDENTS.getMessage(), "attributeForRules");

		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		//Check Driver with one outdated incident
		String oidDriver2 = addAndUpdateDriver(policyNumber, "Outdated", "Incident", "1970-01-01", "B16848385");

		helperMiniServices.orderReportErrors(policyNumber, oidDriver2, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_THREE_INCIDENTS.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_THREE_INCIDENTS.getMessage(), "attributeForRules", false);

		countViolationsInPas(policyNumber, 4);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS_VA.getCode(), ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS_VA.getMessage(), "attributeForRules");
	}

	protected void pas15375_duiIsUnacceptableForDriverUnderTheAgeErrorBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//D1-DUI violation
		String oidDriver1 = addAndUpdateDriver(policyNumber, "One", "DUI", "2000-09-01", "B16848001");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getCode(), ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getMessage(), "attributeForRules", true);

		countViolationsInPas(policyNumber, 1);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getCode(), ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getMessage(), "attributeForRules");

		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		//D2-DUD violation
		String oidDriver2 = addAndUpdateDriver(policyNumber, "One", "DUD", "2000-09-01", "B16848002");

		helperMiniServices.orderReportErrors(policyNumber, oidDriver2, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getCode(), ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getMessage(), "attributeForRules", true);

		countViolationsInPas(policyNumber, 1);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getCode(), ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getMessage(), "attributeForRules");

		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		//D2-TLQ violation
		String oidDriver3 = addAndUpdateDriver(policyNumber, "One", "TLQ", "2000-09-01", "B16848006");

		helperMiniServices.orderReportErrors(policyNumber, oidDriver3, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getCode(), ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getMessage(), "attributeForRules", true);

		countViolationsInPas(policyNumber, 1);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getCode(), ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getMessage(), "attributeForRules");

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

	private void checkThatMvrIsOrdered(int tableRowIndex, String expectedMvrResponse, AddDriverRequest addRequest, UpdateDriverRequest updateRequest) {
		assertSoftly(softly -> {
			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRows().size()).isEqualTo(tableRowIndex);
			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.NAME_ON_LICENSE.getLabel()).getValue()).contains(addRequest.firstName);

			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.DATE_OF_BIRTH.getLabel()).getValue()).isEqualToIgnoringCase("01/31/1999"); //the same as addDriverRequest.birthDate

			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.STATE.getLabel()).getValue()).isEqualToIgnoringCase(updateRequest.stateLicensed);

			softly.assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.LICENSE_NO.getLabel()).getValue()).isEqualToIgnoringCase(updateRequest.licenseNumber);

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

	private void countViolationsInPas(String policyNumber, Integer sumOfViolations) {
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		DriverTab.tableDriverList.selectRow(2);
		assertThat(DriverTab.tableActivityInformationList.getAllRowsCount()).isEqualTo(sumOfViolations);
		driverTab.saveAndExit();
	}

	private String addAndUpdateDriver(String policyNumber, String firstName, String lastName, String birthDate, String licenceNumber) {
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest(firstName, null, lastName, birthDate, null);
		DriversDto addedDriver = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("female", licenceNumber, 16, "VA", "CH", "SSS");
		HelperCommon.updateDriver(policyNumber, addedDriver.oid, updateDriverRequest);
		return addedDriver.oid;
	}
}


