package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import com.google.common.collect.ComparisonChain;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public class TestMiniServicesMVRAndClueReportOrderHelper extends PolicyBaseTest {
	private DriverTab driverTab = new DriverTab();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();

	private static final String CLUE_RECORD_TYPE = "CLUE";
	private static final String MVR_RECORD_TYPE = "MVR";
	private static final Comparator<DrivingRecord> DRIVING_RECORDS_COMPARATOR = (record1, record2) -> ComparisonChain.start()
			.compareTrueFirst(MVR_RECORD_TYPE.equals(record1.activitySource), MVR_RECORD_TYPE.equals(record2.activitySource))
			.compareTrueFirst(CLUE_RECORD_TYPE.equals(record1.activitySource), CLUE_RECORD_TYPE.equals(record2.activitySource))
			.compare(record2.accidentDate, record1.accidentDate) // newest first
			.result();

	protected void pas16694_orderReports_not_Named_Insured_endorsementBody(PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add driver
		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest("ClueNonChargeable", "Doc", "Activity", "1999-01-31", "III");
		DriversDto addedDriver = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		//Update driver
		String maritalStatusMarried;
		if (getState().equals(Constants.States.CA)) {
			maritalStatusMarried = "M";
		} else {
			maritalStatusMarried = "MSS";
		}

		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("male", "995860596", 18, "VA", "CH", maritalStatusMarried);
		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, addedDriver.oid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriverResponse.driver.namedInsuredType).isEqualTo("Not a Named Insured")); //Make sure that added driver is NOT a Named Insured

		TestMiniServicesAssignmentsCAHelper.makeAssignmentsForCA(policyNumber);
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

		TestMiniServicesAssignmentsCAHelper.makeAssignmentsForCA(policyNumber);
		helperMiniServices.endorsementRateAndBind(policyNumber);

		//Repeat with driver 2

		//Create pended endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		//Add driver
		addDriverRequest = DXPRequestFactory.createAddDriverRequest("MvrNonChargeable", "Doc", "Activity", "1999-01-31", "III");
		DriversDto addedDriver2 = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);

		//Update driver
		updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("male", "995860597", 18, "VA", "CH", maritalStatusMarried);
		DriverWithRuleSets updateDriverResponse2 = HelperCommon.updateDriver(policyNumber, addedDriver2.oid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriverResponse2.driver.namedInsuredType).isEqualTo("Not a Named Insured")); //Make sure that added driver is NOT a Named Insured

		TestMiniServicesAssignmentsCAHelper.makeAssignmentsForCA(policyNumber);
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
		helperMiniServices.createEndorsementWithCheck(policyNumber, endorsementDate);

		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest("ClueNonChargeable", "Doc", "Activity", "1999-01-31", "III");
		DriversDto addedDriver = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);

		//And update missing info for the driver
		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("male", "995860596", 18, "VA", "SP", null);
		DriverWithRuleSets updateDriver = HelperCommon.updateDriver(policyNumber, addedDriver.oid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriver.driver.namedInsuredType).isEqualTo("NI")); //Make sure that added driver is Named Insured

		TestMiniServicesAssignmentsCAHelper.makeAssignmentsForCA(policyNumber);
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
		driverActivityReportsTab.saveAndExit();

		helperMiniServices.endorsementRateAndBind(policyNumber);

		///////////Repeat with driver 2///////////

		//Create pended endorsement - future dated, because otherwise Insurance Score Report must be ordered for newly added NI
		PolicySummary response2 = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertSoftly(softly -> softly.assertThat(response2.transactionEffectiveDate).isEqualTo(HelperMiniServices.convertDateToAZDate(endorsementDate)));

		addDriverRequest = DXPRequestFactory.createAddDriverRequest("MvrNonChargeable", "Doc", "Activity", "1999-01-31", "III");
		addedDriver = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);

		//And update missing info for the driver

		updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("male", "995860597", 18, "VA", "SP", null);
		DriverWithRuleSets updateDriver2 = HelperCommon.updateDriver(policyNumber, addedDriver.oid, updateDriverRequest);
		assertSoftly(softly -> softly.assertThat(updateDriver2.driver.namedInsuredType).isEqualTo("NI")); //Make sure that added driver is Named Insured

		TestMiniServicesAssignmentsCAHelper.makeAssignmentsForCA(policyNumber);
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

	protected void pas15384_moreThanTwoMinorViolationsErrorBody(String state) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Check driver with more than two minor violations
		String oidDriver1 = addAndUpdateDriver(policyNumber, "Two", "Minors", "1970-01-01", "B15384001", "CH", "VA", "male");

		//Order reports through service
		if ("MD".equals(state)) {
			helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS_C_MD);
		} else {
			helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS_C);
		}
		countViolationsInPas(policyNumber, 3);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		if ("MD".equals(state)) {
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS_MD.getCode(), ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS_MD.getMessage());

		} else {
			helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS.getCode(), ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS.getMessage());
		}
		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		//Check Driver with one outdated violation
		String oidDriver2 = addAndUpdateDriver(policyNumber, "Outdated", "Minor", "1970-01-01", "B15384003", "CH", "VA", "female");

		HelperCommon.orderReports(policyNumber, oidDriver2, OrderReportsResponse.class, 200);
		countViolationsInPas(policyNumber, 2);
		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas15371_driversWithNarcoticsDrugOrFelonyConvictionsErrorBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Check driver with more that two minor violations
		String oidDriver1 = addAndUpdateDriver(policyNumber, "One", "Felony", "1970-01-01", "B15371001", "CH", "VA", "male");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS_C);

		countViolationsInPas(policyNumber, 1);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getMessage());

		SearchPage.openPolicy(policyNumber);
		TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
		testEValueDiscount.simplifiedPendedEndorsementIssue();

		//START PAS-17648
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest(null, null, null, "VA", null, "MSS");
		HelperCommon.updateDriver(policyNumber, oidDriver1, updateDriverRequest);

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas15370_driverWithMoreThanTwentyPointsErrorBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Check driver with more than 20 points
		String oidDriver1 = addAndUpdateDriver(policyNumber, "Twenty", "Points", "1970-01-01", "B16848001", "CH", "VA", "male");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS_C);

		countViolationsInPas(policyNumber, 6);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS.getMessage());

		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		//Check Driver with one outdated violation
		String oidDriver2 = addAndUpdateDriver(policyNumber, "Outdated", "Twenty", "1970-01-01", "B16848004", "CH", "VA", "male");

		helperMiniServices.orderReportErrors(policyNumber, oidDriver2, false, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS_C);

		countViolationsInPas(policyNumber, 5);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		ErrorResponseDto binErrorResponseDto = HelperCommon.endorsementBindError(policyNumber, oidDriver2, 422);
		assertSoftly(softly -> {
			softly.assertThat(binErrorResponseDto.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(binErrorResponseDto.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(binErrorResponseDto.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getCode());
			softly.assertThat(binErrorResponseDto.errors.get(0).message).contains(ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getMessage());
			softly.assertThat(binErrorResponseDto.errors.get(1).errorCode).isEqualTo(ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_DUI.getCode());
			softly.assertThat(binErrorResponseDto.errors.get(1).message).contains(ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_DUI.getMessage());
		});
	}

	protected void pas15385_driverWithFourOrMoreIncidentsErrorBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Check driver with 4 incidents
		String oidDriver1 = addAndUpdateDriver(policyNumber, "Four", "Incidents", "1970-01-01", "B16848123", "CH", "VA", "male");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_THREE_INCIDENTS_C);

		countViolationsInPas(policyNumber, 4);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_THREE_INCIDENTS.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_THREE_INCIDENTS.getMessage());

		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		//Check Driver with one outdated incident
		String oidDriver2 = addAndUpdateDriver(policyNumber, "Outdated", "Incident", "1970-01-01", "B16848385", "CH", "VA", "male");

		helperMiniServices.orderReportErrors(policyNumber, oidDriver2, false, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_THREE_INCIDENTS_C);

		countViolationsInPas(policyNumber, 4);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS.getCode(), ErrorDxpEnum.Errors.MORE_THAN_TWO_MINOR_VIOLATIONS.getMessage());
	}

	protected void pas15375_duiIsUnacceptableForDriverUnderTheAgeErrorBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//D1-DUI violation
		String oidDriver1 = addAndUpdateDriver(policyNumber, "One", "DUI", "2000-09-01", "B15375001", "CH", "VA", "female");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21_C);

		countViolationsInPas(policyNumber, 1);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getCode(), ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getMessage());

		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		//D2-DUD violation
		String oidDriver2 = addAndUpdateDriver(policyNumber, "One", "DUD", "2000-09-01", "B15375002", "CH", "VA", "female");

		helperMiniServices.orderReportErrors(policyNumber, oidDriver2, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21_C);

		countViolationsInPas(policyNumber, 1);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getCode(), ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getMessage());

		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		//D2-TLQ violation
		String oidDriver3 = addAndUpdateDriver(policyNumber, "One", "TLQ", "2000-09-01", "B15375006", "CH", "VA", "female");

		helperMiniServices.orderReportErrors(policyNumber, oidDriver3, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21_C);

		countViolationsInPas(policyNumber, 1);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getCode(), ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21.getMessage());
	}

	protected void pas15374_driverWithMajorViolationsErrorBody() {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = getCopiedPolicy();

		//Drivers with one violation
		checkDriverViolationDuiError(policyNumber, "AutoTheft", "B15374001");
		checkDriverViolationDuiError(policyNumber, "VehicleManslaughter", "B15374002");
		checkDriverViolationDuiError(policyNumber, "DragRacing", "B15374003");
		checkDriverViolationDuiError(policyNumber, "FleeingArrest", "B15374005");
		checkDriverViolationDuiError(policyNumber, "HitRun", "B15374006");
		checkDriverViolationDuiError(policyNumber, "LeavingScene", "B15374007");
		checkDriverViolationDuiError(policyNumber, "NegligentDriving", "B15374008");
		checkDriverViolationDuiError(policyNumber, "RecklessDriving", "B15374009");
		checkDriverViolationDuiError(policyNumber, "DrivingSuspension", "B15374010");

		//Driver with multiple majors
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		String oidDriver = addAndUpdateDriver(policyNumber, "Multiple", "Majors", "1970-01-01", "B15374013", "CH", "VA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver, ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_DUI_C, ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS_C);
		countViolationsInPas(policyNumber, 3);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_DUI.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_DUI.getMessage());
		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

		//Outdated violation
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		String oidDriver2 = addAndUpdateDriver(policyNumber, "Outdated", "Majors", "1970-01-01", "B15374015", "CH", "VA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver2, ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS_C);
		countViolationsInPas(policyNumber, 1);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS.getMessage());
		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
	}

	protected void pas19673_theOrderOfViolationsBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		//MVR
		String oidDriver1 = addAndUpdateDriver(policyNumber, "Twenty", "Points", "1970-01-01", "B16848001", "CH", "VA", "male");

		//Order reports through service
		OrderReportsResponse response = HelperCommon.orderReports(policyNumber, oidDriver1, OrderReportsResponse.class, 200);
		List<DrivingRecord> drivingRecordList = response.drivingRecords;
		assertSoftly(softly ->
				softly.assertThat(drivingRecordList).isSortedAccordingTo(DRIVING_RECORDS_COMPARATOR)
		);
		//CLUE
		String oidDriver2 = addAndUpdateDriver(policyNumber, "Three", "Claims", "1970-01-01", "B19673001", "CH", "VA", "male");

		//Order reports through service
		OrderReportsResponse response2 = HelperCommon.orderReports(policyNumber, oidDriver2, OrderReportsResponse.class, 200);
		List<DrivingRecord> drivingRecordList2 = response2.drivingRecords;
		assertSoftly(softly ->
				softly.assertThat(drivingRecordList2).isSortedAccordingTo(DRIVING_RECORDS_COMPARATOR)
		);
		//CLUE and MVR
		String oidDriver3 = addAndUpdateDriver(policyNumber, "Claims", "AndMVR", "1970-01-01", "B19673002", "CH", "VA", "male");

		//Order reports through service
		OrderReportsResponse response3 = HelperCommon.orderReports(policyNumber, oidDriver3, OrderReportsResponse.class, 200);
		List<DrivingRecord> drivingRecordList3 = response3.drivingRecords;
		assertSoftly(softly ->
				softly.assertThat(drivingRecordList3).isSortedAccordingTo(DRIVING_RECORDS_COMPARATOR)
		);
	}

	private void checkThatClueIsOrdered(int tableRowIndex, String expectedClueResponse) {
		if (getState().equals(Constants.States.CA)) {
			assertSoftly(softly -> {
				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRows().size()).isEqualTo(tableRowIndex);

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderClueRow.RESIDENTIAL_ADDRESS.getLabel()).getValue()).isNotEmpty();

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderClueRow.REPORT.getLabel()).getValue()).isEqualToIgnoringCase("View CLUE");

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderClueRow.ORDER_DATE.getLabel()).getValue())
						.isEqualToIgnoringCase(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderClueRow.RECEIPT_DATE.getLabel()).getValue())
						.isNotBlank(); //it can be also past date if report has been ordered previously, hence checking only that it is not blank

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderClueRow.RESPONSE.getLabel()).getValue()).isEqualToIgnoringCase(expectedClueResponse); //will be handled by: PAS-17059 Not getting correct CLUE report response

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderClueRow.ADDRESS_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("Current");

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderClueRow.ORDER_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("Add Driver");
			});
		} else {
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
	}

	private void checkThatMvrIsOrdered(int tableRowIndex, String expectedMvrResponse, AddDriverRequest addRequest, UpdateDriverRequest updateRequest) {
		if (getState().equals(Constants.States.CA)) {
			assertSoftly(softly -> {
				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRows().size()).isEqualTo(tableRowIndex);
				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderMVRRow.NAME_ON_LICENSE.getLabel()).getValue()).contains(addRequest.firstName);

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderMVRRow.DATE_OF_BIRTH.getLabel()).getValue()).isEqualToIgnoringCase("01/31/1999"); //the same as addDriverRequest.birthDate

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderMVRRow.STATE.getLabel()).getValue()).isEqualToIgnoringCase(updateRequest.stateLicensed);

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderMVRRow.LICENSE_NO.getLabel()).getValue()).isEqualToIgnoringCase(updateRequest.licenseNumber);

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderMVRRow.LICENSE_STATUS.getLabel()).getValue()).containsIgnoringCase("VALID");

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderMVRRow.REPORT.getLabel()).getValue()).isEqualToIgnoringCase("View MVR");

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderMVRRow.ORDER_DATE.getLabel()).getValue())
						.isEqualToIgnoringCase(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderMVRRow.RECEIPT_DATE.getLabel()).getValue())
						.isNotBlank(); //it can be also past date if report has been ordered previously, hence checking only that it is not blank

				softly.assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
						.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderMVRRow.RESPONSE.getLabel()).getValue())
						.isEqualToIgnoringCase(expectedMvrResponse);
			});
		} else {
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
	}

	protected void pas15383_driverWithOneOrMoreFaultAccidentsErrorBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1));

		mainApp().open();
		String oidDriver1 = addAndUpdateDriver(policyNumber, "Two", "AtFault", "2000-09-01", "B15383001", "SP", "VA", "female");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DRIVER_WITH_ONE_OR_MORE_FAULT_ACCIDENTS_C);

		countViolationsInPas(policyNumber, 2);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_ONE_OR_MORE_FAULT_ACCIDENTS.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_ONE_OR_MORE_FAULT_ACCIDENTS.getMessage());

		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
	}

	protected void pas15372_driverDetailsAndMvrRulesThatProvidedBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		String oidDriver1 = addAndUpdateDriver(policyNumber, "Name", "Mismatch", "1970-01-01", "B15383001", "SP", "VA", "female");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DRIVER_NAME_MISMATCH, ErrorDxpEnum.Errors.DRIVER_GENDER_MISMATCH);

		String oidDriver2 = addAndUpdateDriver(policyNumber, "Other", "Mismatches", "1971-01-01", "B15383001", "SP", "VA", "female");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver2, ErrorDxpEnum.Errors.DRIVER_GENDER_MISMATCHES, ErrorDxpEnum.Errors.DRIVER_DOB_MISMATCH);

		String oidDriver3 = addAndUpdateDriver(policyNumber, "Other", "Mismatches", "1970-01-01", "B15383001", "SP", "VA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver3);
	}

	protected void pas15369_reportOrderAndDriverBody(ETCSCoreSoftAssertions softly) {
		mainApp().open();
		String policyNumber =getCopiedPolicy();

		String oidDriver1 = addAndUpdateDriver(policyNumber, "Karen", "Yifru", "2000-01-01", "A63341764", "CH", "VA", "female");
		//Order reports through service
		OrderReportsResponse response = HelperCommon.orderReports(policyNumber, oidDriver1, OrderReportsResponse.class, 200);
		softly.assertThat(response.mvrReports.get(0).choicePointLicenseStatus).contains("VALID");

		pasDriverActivityReport(policyNumber, "VALID", "Karen Yifru");

		String oidDriver2 = addAndUpdateDriver(policyNumber, "One", "Minor", "1970-01-01", "B15384002", "CH", "VA", "female");
		OrderReportsResponse response1 = HelperCommon.orderReports(policyNumber, oidDriver2, OrderReportsResponse.class, 200);
		String acDate = pasDriverActivityReport1(policyNumber);

		softly.assertThat(response1.drivingRecords.get(0).accidentDate).isEqualTo(HelperMiniServices.convertDateToAZDate(acDate));
		softly.assertThat(response1.drivingRecords.get(0).activitySource).isEqualTo("MVR");
		softly.assertThat(response1.mvrReports.get(0).choicePointLicenseStatus).contains("VALID");

		pasDriverActivityReport(policyNumber, "VALID", "One Minor");

		String oidDriver3 = addAndUpdateDriver(policyNumber, "Sam", "Fields", "1971-02-09", "333222001", "CH", "AZ", "female");
		OrderReportsResponse response2 = HelperCommon.orderReports(policyNumber, oidDriver3, OrderReportsResponse.class, 200);
		softly.assertThat(response2.mvrReports.get(0).choicePointLicenseStatus).isEqualTo("4  EXPIRED");

		pasDriverActivityReport(policyNumber, "EXPIRED", "Sam Fields");

		String oidDriver4 = addAndUpdateDriver(policyNumber, "OSI1000", "Test3", "1971-02-09", "A12387656", "CH", "AZ", "female");
		OrderReportsResponse response3 = HelperCommon.orderReports(policyNumber, oidDriver4, OrderReportsResponse.class, 200);

		if (getState().equals(Constants.States.CA)) {
			pasDriverActivityReport(policyNumber, "Permanently Revoked", "OSI1000 Test3");
			softly.assertThat(response3.mvrReports.get(0).choicePointLicenseStatus).isEqualTo("3  REVOKED");
		} else {
			pasDriverActivityReport(policyNumber, "2 SUSPENDED", "OSI1000 Test3");
			assertThat(response3.mvrReports.get(0).choicePointLicenseStatus).isEqualTo("2  SUSPENDED");
		}

		String oidDriver5 = addAndUpdateDriver(policyNumber, "OSI1000", "Test4", "1971-02-09", "A12387656", "CH", "AZ", "female");
		OrderReportsResponse response5 = HelperCommon.orderReports(policyNumber, oidDriver5, OrderReportsResponse.class, 200);
		String acDate1 = pasDriverActivityReport1(policyNumber);

		if (getState().equals(Constants.States.CA)) {
			softly.assertThat(response5.mvrReports.get(0).choicePointLicenseStatus).isEqualTo("2  SUSPENDED");
		} else {
			softly.assertThat(response5.drivingRecords.get(0).accidentDate).isEqualTo(HelperMiniServices.convertDateToAZDate(acDate1));
			softly.assertThat(response5.drivingRecords.get(0).activitySource).isEqualTo("MVR");
			softly.assertThat(response5.mvrReports.get(0).choicePointLicenseStatus).isEqualTo("2  SUSPENDED");
		}

		pasDriverActivityReport(policyNumber, "2 SUSPENDED", "OSI1000 Test4");

		String oidDriver6 = addAndUpdateDriver(policyNumber, "DOUGLAS", "HENRY", "1972-10-01", "A1452390", "CH", "CA", "female");
		OrderReportsResponse response6 = HelperCommon.orderReports(policyNumber, oidDriver6, OrderReportsResponse.class, 200);

		softly.assertThat(response6.mvrReports.get(0).choicePointLicenseStatus).isEqualTo("5  CANCELLED");

		pasDriverActivityReport(policyNumber, "5 CANCELLED", "DOUGLAS HENRY");

		String oidDriver7 = addAndUpdateDriver(policyNumber, "Deny", "Licesia", "1974-05-01", "A11111100", "CH", "AZ", "female");
		OrderReportsResponse response7 = HelperCommon.orderReports(policyNumber, oidDriver7, OrderReportsResponse.class, 200);

		softly.assertThat(response7.mvrReports.get(0).choicePointLicenseStatus).isEqualTo("6  DENIED");

		String oidDriver8 = addAndUpdateDriver(policyNumber, "Limite", "Licesia", "1974-05-01", "A11111101", "CH", "AZ", "female");
		OrderReportsResponse response8 = HelperCommon.orderReports(policyNumber, oidDriver8, OrderReportsResponse.class, 200);

		softly.assertThat(response8.mvrReports.get(0).choicePointLicenseStatus).isEqualTo("7  LIMITED");

		String oidDriver9 = addAndUpdateDriver(policyNumber, "Ide", "Licesia", "1974-05-01", "A11111102", "CH", "AZ", "female");
		OrderReportsResponse response9 = HelperCommon.orderReports(policyNumber, oidDriver9, OrderReportsResponse.class, 200);

		softly.assertThat(response9.mvrReports.get(0).choicePointLicenseStatus).isEqualTo("9  ID ONLY");

		String oidDriver10 = addAndUpdateDriver(policyNumber, "Diskabe", "Licesia", "1974-05-01", "A11111103", "CH", "AZ", "female");
		OrderReportsResponse response10 = HelperCommon.orderReports(policyNumber, oidDriver10, OrderReportsResponse.class, 200);

		softly.assertThat(response10.mvrReports.get(0).choicePointLicenseStatus).isEqualTo("A  DISQUALIFIED");

		String oidDriver11 = addAndUpdateDriver(policyNumber, "One", "AutoTheft", "1970-01-01", "B15374001", "CH", "VA", "female");
		OrderReportsResponse response11 = HelperCommon.orderReports(policyNumber, oidDriver11, OrderReportsResponse.class, 200);

		if (getState().equals(Constants.States.CA)) {
			softly.assertThat(response11.mvrReports.get(0).choicePointLicenseStatus).contains("VALID");
		} else {
			String acDate2 = pasDriverActivityReport1(policyNumber);
			softly.assertThat(response11.drivingRecords.get(0).accidentDate).isEqualTo(HelperMiniServices.convertDateToAZDate(acDate2));
			softly.assertThat(response11.drivingRecords.get(0).activitySource).isEqualTo("MVR");
			softly.assertThat(response11.mvrReports.get(0).choicePointLicenseStatus).contains("VALID");
		}

		pasDriverActivityReport(policyNumber, "VALID", "One AutoTheft");

		String oidDriver12 = addAndUpdateDriver(policyNumber, "Two", "AtFault", "1970-01-01", "B15383001", "CH", "VA", "female");
		OrderReportsResponse response12 = HelperCommon.orderReports(policyNumber, oidDriver12, OrderReportsResponse.class, 200);

		softly.assertThat(response12.drivingRecords.get(0).accidentDate).isNotEmpty();
		softly.assertThat(response12.drivingRecords.get(0).activitySource).isEqualTo("CLUE");
		softly.assertThat(response12.mvrReports.get(0).choicePointLicenseStatus).contains("VALID");

		softly.assertThat(response12.drivingRecords.get(1).accidentDate).isNotEmpty();
		softly.assertThat(response12.drivingRecords.get(1).activitySource).isEqualTo("CLUE");

		pasDriverActivityReport(policyNumber, "VALID", "Two AtFault");
	}
	protected void pas27222_UnderwritingRulesCASelectAAA_CSA3292736_Body(ETCSCoreSoftAssertions softly) {
		mainApp().open();
		String policyNumber =  getCopiedPolicy();

		String oidDriver1 = addAndUpdateDriver(policyNumber, "White", "Shoes", "1970-01-01", "N3734401", "CH", "CA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DRIVER_WITH_SUSPENDED);

		String oidDriver2 = addAndUpdateDriver(policyNumber, "Two", "DragRacing", "1970-01-01", "N3734402", "CH", "CA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver2, ErrorDxpEnum.Errors.MAJOR_CONVICTION);

		String oidDriver3 = addAndUpdateDriver(policyNumber, "Three", "SpeedingCA", "1970-01-01", "N3734403", "CH", "CA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver3, ErrorDxpEnum.Errors.MAJOR_VIOLATION);

		String oidDriver4 = addAndUpdateDriver(policyNumber, "DUI", "DrugsCA", "1970-01-01", "N3734406", "CH", "CA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver4, ErrorDxpEnum.Errors.DRIVER_TEN_YEAR_MAJOR_CONVICTION, ErrorDxpEnum.Errors.DRUG_FELONY);

		String oidDriver5 = addAndUpdateDriver(policyNumber, "Two", "AtFault", "1970-01-01", "B15383001", "CH", "AZ", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver5, ErrorDxpEnum.Errors.INJYRY_ACCIDENT);

		String oidDriver6 = addAndUpdateDriver(policyNumber, "Karen", "Stark", "1997-10-16", "D1278333", "CH", "CA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver6, ErrorDxpEnum.Errors.TWO_AT_FAULT);

		String oidDriver7 = addAndUpdateDriver(policyNumber, "Megha", "Stark", "1970-01-01", "N3734407", "CH", "CA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver7, ErrorDxpEnum.Errors.COMBINATION_FAULT_ACCIDENT);
	}
	protected void pas15369_reportOrderAndDriverOtherStateBody(String policyNumber) {

		String oidDriver1 = addAndUpdateDriver(policyNumber, "Karen", "Yifru", "2000-01-01", "A63341764", "CH", "VA", "female");
		//Order reports through service
		OrderReportsResponse response = HelperCommon.orderReports(policyNumber, oidDriver1, OrderReportsResponse.class, 200);
		assertSoftly(softly ->
				softly.assertThat(response.mvrReports.get(0).choicePointLicenseStatus).contains("VALID")
		);
		pasDriverActivityReport(policyNumber, "VALID", "Karen Yifru");

		String oidDriver2 = addAndUpdateDriver(policyNumber, "One", "Minor", "1970-01-01", "B15384002", "CH", "VA", "female");
		OrderReportsResponse response1 = HelperCommon.orderReports(policyNumber, oidDriver2, OrderReportsResponse.class, 200);
		String acDate = pasDriverActivityReport1(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(response1.drivingRecords.get(0).accidentDate).isEqualTo(HelperMiniServices.convertDateToAZDate(acDate));
			softly.assertThat(response1.drivingRecords.get(0).activitySource).isEqualTo("MVR");
			softly.assertThat(response1.mvrReports.get(0).choicePointLicenseStatus).contains("VALID");
		});

		pasDriverActivityReport(policyNumber, "VALID", "One Minor");

		String oidDriver12 = addAndUpdateDriver(policyNumber, "Two", "AtFault", "1970-01-01", "B15383001", "CH", "VA", "female");
		OrderReportsResponse response12 = HelperCommon.orderReports(policyNumber, oidDriver12, OrderReportsResponse.class, 200);
		assertSoftly(softly -> {
			softly.assertThat(response12.drivingRecords.get(0).accidentDate).isNotEmpty();
			softly.assertThat(response12.drivingRecords.get(0).activitySource).isEqualTo("CLUE");

			softly.assertThat(response12.drivingRecords.get(1).accidentDate).isNotEmpty();
			softly.assertThat(response12.drivingRecords.get(1).activitySource).isEqualTo("CLUE");

			softly.assertThat(response12.mvrReports.get(0).choicePointLicenseStatus).contains("VALID");
		});

		pasDriverActivityReport(policyNumber, "VALID", "Two AtFault");
	}

	protected void pas15376_3OrMoreMinorOrSpeedingViolationsBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Check driver with more that three minor violations related to speeding
		String oidDriver1 = addAndUpdateDriver(policyNumber, "Three", "Minors", "1970-01-01", "B15376001", "CH", "AZ", "male");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DRIVER_WITH_THREE_OR_MORE_SPEEDING_VIOLATION_C);

		countViolationsInPas(policyNumber, 3);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_THREE_OR_MORE_SPEEDING_VIOLATION.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_THREE_OR_MORE_SPEEDING_VIOLATION.getMessage());

		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());

	}

	protected void pas19059_3OrMoreMinorOrSpeedingViolationsBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Check driver with more that three minor violations related to speeding
		String oidDriver1 = addAndUpdateDriver(policyNumber, "Over", "Twenty", "1970-01-01", "B16848002", "CH", "VA", "male");

		//Order reports through service
		helperMiniServices.orderReportErrors(policyNumber, oidDriver1, ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_DUI_C, ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS_C, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS_C, ErrorDxpEnum.Errors.DRIVER_WITH_THREE_OR_MORE_SPEEDING_VIOLATION_C);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_DUI, ErrorDxpEnum.Errors.DRIVER_WITH_NARCOTICS_DRUGS_OR_FELONY_CONVICTIONS, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWENTY_POINTS, ErrorDxpEnum.Errors.DRIVER_WITH_THREE_OR_MORE_SPEEDING_VIOLATION);

		String oidDriver2 = addAndUpdateDriver(policyNumber, "One", "DUI", "1999-01-10", "B15375001", "CH", "VA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver2, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21_C);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21_NY.getCode(), ErrorDxpEnum.Errors.DUI_IS_UNACCEPTABLE_FOR_DRIVER_UNDER_THE_AGE_21_NY.getMessage());

		String oidDriver3 = addAndUpdateDriver(policyNumber, "Two", "AtFault", "1970-01-01", "B15383001", "CH", "AZ", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver3, ErrorDxpEnum.Errors.DRIVER_WITH_ONE_OR_MORE_FAULT_ACCIDENTS_C);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_ONE_OR_MORE_FAULT_ACCIDENTS.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_ONE_OR_MORE_FAULT_ACCIDENTS.getMessage());

		String oidDriver4 = addAndUpdateDriver(policyNumber, "Three", "NotAtFault", "1970-01-01", "B15383002", "CH", "VA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver4, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWO_AT_FAULT_VIOLATION_C);
		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWO_AT_FAULT_VIOLATION.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_MORE_THAN_TWO_AT_FAULT_VIOLATION.getMessage());
	}

	//Method for 200009 rule (one violation only)
	private void checkDriverViolationDuiError(String policyNumber, String lastName, String licenseNumber) {
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		String oidDriver5 = addAndUpdateDriver(policyNumber, "One", lastName, "1970-01-01", licenseNumber, "CH", "VA", "male");
		helperMiniServices.orderReportErrors(policyNumber, oidDriver5, ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_DUI_C);
		countViolationsInPas(policyNumber, 1);

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithErrorCheck(policyNumber, ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_DUI.getCode(), ErrorDxpEnum.Errors.DRIVER_WITH_MAJOR_VIOLATION_DUI.getMessage());
		HelperCommon.deleteEndorsement(policyNumber, Response.Status.NO_CONTENT.getStatusCode());
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

	private String addAndUpdateDriver(String policyNumber, String firstName, String lastName, String birthDate, String licenceNumber, String relationship, String stateLicensed, String gender) {
		helperMiniServices.createEndorsementWithCheck(policyNumber);
		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest(firstName, null, lastName, birthDate, null);
		DriversDto addedDriver = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest(gender, licenceNumber, 16, stateLicensed, relationship, "SSS");
		HelperCommon.updateDriver(policyNumber, addedDriver.oid, updateDriverRequest);
		return addedDriver.oid;
	}

	private void pasDriverActivityReport(String policyNumber, String status, String name) {
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();

		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
		if (getState().equals(Constants.States.CA)) {
		assertThat(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRow(2).getCell(PolicyConstants.MVRReportTable.NAME_ON_LICENSE).getValue()).isEqualTo(name);
		assertThat(status.contains(aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab.tableMVRReports.getRow(2).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()));

		} else {
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(2).getCell(PolicyConstants.MVRReportTable.NAME_ON_LICENSE).getValue()).isEqualTo(name);
			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(2).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).containsIgnoringCase(status);

		}
		driverActivityReportsTab.saveAndExit();
	}

	private String pasDriverActivityReport1(String policyNumber) {
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		DriverTab.tableDriverList.selectRow(2);
		String convicDate = getAccidentViolationDate(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE);
		driverTab.saveAndExit();
		return convicDate;
	}

	private String pasDriverActivityReportOccurrenceDate(String policyNumber) {
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		DriverTab.tableDriverList.selectRow(2);
		String convicDate = getAccidentViolationDate(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE);
		driverTab.saveAndExit();
		return convicDate;
	}

	private String getAccidentViolationDate(AssetDescriptor<TextBox> dateField) {
		String date = driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION).getAsset(dateField).getValue();
		if (StringUtils.isNotEmpty(date)) {
			LocalDateTime localDate = TimeSetterUtil.getInstance().parse(date, DateTimeUtils.MM_DD_YYYY);
			date = localDate.format(DateTimeFormatter.ofPattern("YYYY-MM-dd"));
		}
		return date;
	}
}
