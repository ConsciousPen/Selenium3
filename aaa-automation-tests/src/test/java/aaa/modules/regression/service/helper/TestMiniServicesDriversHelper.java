package aaa.modules.regression.service.helper;

import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.MIDDLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableList;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataManager;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class TestMiniServicesDriversHelper extends PolicyBaseTest {

	private DriverTab driverTab = new DriverTab();
	private AddDriverRequest addDriverRequest = new AddDriverRequest();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();
	private TestMiniServicesGeneralHelper testMiniServicesGeneralHelper = new TestMiniServicesGeneralHelper();

	protected void pas11932_viewDriversInfo(PolicyType policyType) {
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			String notAvailableForRating = "nafr";
			String availableForRating = "afr";
			TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
			String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
			TestData td = getPolicyTD("DataGather", "TestData");
			TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_ThreeDrivers").getTestDataList("DriverTab")).resolveLinks();

			policyType.get().createPolicy(testData);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			//Drivers info from testData
			String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);
			String lastName1 = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("Last Name");

			String firstName2 = td.getTestDataList("DriverTab").get(1).getValue("First Name");
			String middleName2 = td.getTestDataList("DriverTab").get(1).getValue("Middle Name");
			String lastName2 = td.getTestDataList("DriverTab").get(1).getValue("Last Name");
			String suffix2 = td.getTestDataList("DriverTab").get(1).getValue("Suffix");

			String firstName3 = td.getTestDataList("DriverTab").get(2).getValue("First Name");
			String middleName3 = td.getTestDataList("DriverTab").get(2).getValue("Middle Name");
			String lastName3 = td.getTestDataList("DriverTab").get(2).getValue("Last Name");
			String suffix3 = td.getTestDataList("DriverTab").get(2).getValue("Suffix");

			//Hit service for the first time
			ViewDriversResponse response = HelperCommon.viewPolicyDrivers(policyNumber);
			DriversDto driverSt = response.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd = response.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driverRd = response.driverList.stream().filter(driver -> firstName3.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driverSt).isNotNull();
			softly.assertThat(driverSt.lastName).isEqualTo(lastName1);
			softly.assertThat(driverSt.oid).isNotEmpty();

			softly.assertThat(driverNd).isNotNull();
			softly.assertThat(driverNd.middleName).isEqualTo(middleName2);
			softly.assertThat(driverNd.lastName).isEqualTo(lastName2);
			softly.assertThat(driverNd.suffix).isEqualTo(suffix2);
			softly.assertThat(driverNd.driverType).isEqualTo(availableForRating);
			softly.assertThat(driverNd.oid).isNotEmpty();

			softly.assertThat(driverRd).isNotNull();
			softly.assertThat(driverRd.middleName).isEqualTo(middleName3);
			softly.assertThat(driverRd.lastName).isEqualTo(lastName3);
			softly.assertThat(driverRd.suffix).isEqualTo(suffix3);
			softly.assertThat(driverRd.driverType).isEqualTo(notAvailableForRating);
			softly.assertThat(driverRd.oid).isNotEmpty();

			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());
			DriverTab.tableDriverList.removeRow(3);
			DriverTab.tableDriverList.selectRow(2);
			driverTab.getAssetList().getAsset(MIDDLE_NAME).setValue("Kevin");
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			premiumAndCoveragesTab.calculatePremium();
			premiumAndCoveragesTab.saveAndExit();

			//Check dxp service with pending endorsement
			ViewDriversResponse response2 = HelperCommon.viewPolicyDrivers(policyNumber);
			DriversDto driverSt2 = response2.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd2 = response2.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);
			DriversDto driverRd2 = response2.driverList.stream().filter(driver -> firstName3.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driverSt2).isNotNull();
			softly.assertThat(driverSt2.lastName).isEqualTo(lastName1);
			softly.assertThat(driverSt2.driverType).isEqualTo(availableForRating);
			softly.assertThat(driverSt2.oid).isNotEmpty();

			softly.assertThat(driverNd2).isNotNull();
			softly.assertThat(driverNd2.middleName).isEqualTo(middleName2);
			softly.assertThat(driverNd2.lastName).isEqualTo(lastName2);
			softly.assertThat(driverNd2.suffix).isEqualTo(suffix2);
			softly.assertThat(driverNd2.oid).isNotEmpty();

			softly.assertThat(driverRd2).isNotNull();
			softly.assertThat(driverRd2.middleName).isEqualTo(middleName3);
			softly.assertThat(driverRd2.lastName).isEqualTo(lastName3);
			softly.assertThat(driverRd2.suffix).isEqualTo(suffix3);
			softly.assertThat(driverRd2.oid).isNotEmpty();

			//Check dxp service what we have in endorsement
			ViewDriversResponse response3 = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driverSt3 = response3.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd3 = response3.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driverSt3).isNotNull();
			softly.assertThat(driverSt3.lastName).isEqualTo(lastName1);
			softly.assertThat(driverSt3.driverType).isEqualTo(availableForRating);
			softly.assertThat(driverSt3.oid).isNotEmpty();

			softly.assertThat(driverNd3).isNotNull();
			softly.assertThat(driverNd3.middleName).isEqualTo("Kevin");
			softly.assertThat(driverNd3.lastName).isEqualTo(lastName2);
			softly.assertThat(driverNd3.suffix).isEqualTo(suffix2);
			softly.assertThat(driverNd3.oid).isNotEmpty();

			//Issue pended endorsement
			testEValueDiscount.simplifiedPendedEndorsementIssue();

			//Check dxp service if endorsement changes were applied
			ViewDriversResponse response4 = HelperCommon.viewPolicyDrivers(policyNumber);
			DriversDto driverSt4 = response4.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driverNd4 = response4.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driverSt4).isNotNull();
			softly.assertThat(driverSt4.lastName).isEqualTo(lastName1);
			softly.assertThat(driverSt4.driverType).isEqualTo(availableForRating);
			softly.assertThat(driverSt4.oid).isNotEmpty();

			softly.assertThat(driverNd4).isNotNull();
			softly.assertThat(driverNd4.middleName).isEqualTo("Kevin");
			softly.assertThat(driverNd4.lastName).isEqualTo(lastName2);
			softly.assertThat(driverNd4.suffix).isEqualTo(suffix2);
			softly.assertThat(driverNd4.oid).isNotEmpty();
		});
	}

	protected void pas14463_viewDriverServiceBody(PolicyType policyType, TestData td) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(td);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		ViewDriversResponse response = HelperCommon.viewPolicyDrivers(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(response.driverList.get(0).oid).isNotNull();
			softly.assertThat(response.driverList.get(0).firstName).startsWith("Fernando");
			softly.assertThat(response.driverList.get(0).lastName).isEqualTo("Smith");
			softly.assertThat(response.driverList.get(0).driverType).isEqualTo("afr");
			softly.assertThat(response.driverList.get(0).namedInsuredType).isEqualTo("FNI");
			softly.assertThat(response.driverList.get(0).relationToApplicantCd).isEqualTo("IN");
			softly.assertThat(response.driverList.get(0).maritalStatusCd).isEqualTo("MSS");

			softly.assertThat(response.driverList.get(1).oid).isNotNull();
			softly.assertThat(response.driverList.get(1).firstName).isEqualTo("Jenny");
			softly.assertThat(response.driverList.get(1).lastName).isEqualTo("Smith");
			softly.assertThat(response.driverList.get(1).driverType).isEqualTo("afr");
			softly.assertThat(response.driverList.get(1).namedInsuredType).isEqualTo("NI");
			softly.assertThat(response.driverList.get(1).relationToApplicantCd).isEqualTo("PA");
			softly.assertThat(response.driverList.get(1).maritalStatusCd).isEqualTo("SSS");

			softly.assertThat(response.driverList.get(2).oid).isNotNull();
			softly.assertThat(response.driverList.get(2).firstName).isEqualTo("Laura");
			softly.assertThat(response.driverList.get(2).lastName).isEqualTo("Smith");
			softly.assertThat(response.driverList.get(2).middleName).isEqualTo("Sara");
			softly.assertThat(response.driverList.get(2).suffix).isEqualTo("III");
			softly.assertThat(response.driverList.get(2).driverType).isEqualTo("nafr");
			softly.assertThat(response.driverList.get(2).relationToApplicantCd).isEqualTo("CH");
			softly.assertThat(response.driverList.get(2).maritalStatusCd).isEqualTo("DSS");

		});

		//Create pended endorsement
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		PolicySummary responseEndorsement = HelperCommon.createEndorsement(policyNumber, endorsementDate);
		assertThat(responseEndorsement.policyNumber).isEqualTo(policyNumber);

		ViewDriversResponse responseViewDriverEndorsement = HelperCommon.viewEndorsementDrivers(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).firstName).startsWith("Fernando");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).lastName).isEqualTo("Smith");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).driverType).isEqualTo("afr");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).namedInsuredType).isEqualTo("FNI");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).relationToApplicantCd).isEqualTo("IN");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).maritalStatusCd).isEqualTo("MSS");

			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).firstName).isEqualTo("Jenny");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).lastName).isEqualTo("Smith");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).driverType).isEqualTo("afr");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).namedInsuredType).isEqualTo("NI");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).relationToApplicantCd).isEqualTo("PA");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).maritalStatusCd).isEqualTo("SSS");

			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).firstName).isEqualTo("Laura");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).lastName).isEqualTo("Smith");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).middleName).isEqualTo("Sara");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).suffix).isEqualTo("III");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).driverType).isEqualTo("nafr");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).relationToApplicantCd).isEqualTo("CH");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).maritalStatusCd).isEqualTo("DSS");

		});
	}

	protected void pas482_ViewDriverServiceOrderOfDriverBody(TestData td) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(td);

		ViewDriversResponse responseViewDriver = HelperCommon.viewPolicyDrivers(policyNumber);
		List<DriversDto> originalOrderingFromResponse = ImmutableList.copyOf(responseViewDriver.driverList);
		List<DriversDto> sortedDriversFromResponse = responseViewDriver.driverList;
		sortedDriversFromResponse.sort(DriversDto.DRIVERS_COMPARATOR);
		assertSoftly(softly ->
				assertThat(originalOrderingFromResponse).containsAll(sortedDriversFromResponse)
		);
	}

	protected void pas478_AddDriversBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);

		String firstName = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
		String firstName1 = firstName.substring(0, firstName.length() - 5);
		String lastName1 = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("Last Name");

		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		AddDriverRequest addDriverRequest = new AddDriverRequest();

		addDriverRequest.firstName = "Justin";
		addDriverRequest.middleName = "Doc";
		addDriverRequest.lastName = "Jill";
		addDriverRequest.birthDate = "1960-02-08";
		addDriverRequest.suffix = "III";

		DriversDto addDriverRequestService = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(addDriverRequestService.firstName).isEqualTo(addDriverRequest.firstName);
			softly.assertThat(addDriverRequestService.middleName).isEqualTo(addDriverRequest.middleName);
			softly.assertThat(addDriverRequestService.lastName).isEqualTo(addDriverRequest.lastName);
			softly.assertThat(addDriverRequestService.suffix).isEqualTo(addDriverRequest.suffix);
			softly.assertThat(addDriverRequestService.driverType).isEqualTo("afr");
			softly.assertThat(addDriverRequestService.namedInsuredType).isEqualTo("Not a Named Insured");
			softly.assertThat(addDriverRequestService.relationToApplicantCd).isEqualTo("CH");
			softly.assertThat(addDriverRequestService.maritalStatusCd).isEqualTo("SSS");
			softly.assertThat(addDriverRequestService.driverStatus).isEqualTo("pendingAdd");

		});

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());

		assertThat(DriverTab.tableDriverList.getRow(2).getCell(2).getValue()).isEqualTo("Justin");

		DriverTab.tableDriverList.selectRow(2);
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.DEFENSIVE_DRIVER_COURSE_COMPLETED).getValue()).isEqualTo("No");
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.OCCUPATION).getValue()).isEqualTo("Employed");

		driverTab.saveAndExit();

		ViewDriversResponse responseViewDriverEndorsement = HelperCommon.viewEndorsementDrivers(policyNumber);
		DriversDto driver1 = responseViewDriverEndorsement.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
		DriversDto driver2 = responseViewDriverEndorsement.driverList.stream().filter(driver -> driver.firstName.startsWith(addDriverRequest.firstName)).findFirst().orElse(null);
		String driverOid = responseViewDriverEndorsement.driverList.get(1).oid;
		assertSoftly(softly -> {
			softly.assertThat(driver1.oid).isNotNull();
			softly.assertThat(driver1.firstName).startsWith(firstName1);
			softly.assertThat(driver1.lastName).isEqualTo(lastName1);
			softly.assertThat(driver1.driverType).isEqualTo("afr");
			softly.assertThat(driver1.namedInsuredType).isEqualTo("FNI");
			softly.assertThat(driver1.relationToApplicantCd).isEqualTo("IN");
			softly.assertThat(driver1.maritalStatusCd).isEqualTo("MSS");
			softly.assertThat(driver1.driverStatus).isEqualTo("active");
			softly.assertThat(driver1.birthDate).isEqualTo("1962-12-05");

			softly.assertThat(driver2.oid).isNotNull();
			softly.assertThat(driver2.firstName).isEqualTo("Justin");
			softly.assertThat(driver2.lastName).isEqualTo("Jill");
			softly.assertThat(driver2.middleName).isEqualTo("Doc");
			softly.assertThat(driver2.driverType).isEqualTo("afr");
			softly.assertThat(driver2.namedInsuredType).isEqualTo("Not a Named Insured");
			softly.assertThat(driver2.relationToApplicantCd).isEqualTo("CH");
			softly.assertThat(driver2.maritalStatusCd).isEqualTo("SSS");
			softly.assertThat(driver2.driverStatus).isEqualTo("pendingAdd");
			softly.assertThat(driver2.birthDate).isEqualTo("1960-02-08");

		});

		updateDriverRequest.stateLicensed = "AZ";
		updateDriverRequest.licenseNumber = "D32329585";
		updateDriverRequest.gender = "female";
		updateDriverRequest.relationToApplicantCd = "CH";
		updateDriverRequest.maritalStatusCd = "MSS";
		updateDriverRequest.ageFirstLicensed = 16;

		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, driverOid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse.driver.gender).isEqualTo(updateDriverRequest.gender);
			softly.assertThat(updateDriverResponse.driver.relationToApplicantCd).isEqualTo(updateDriverRequest.relationToApplicantCd);
			softly.assertThat(updateDriverResponse.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse.driver.maritalStatusCd).isEqualTo(updateDriverRequest.maritalStatusCd);
			softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
		});

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());
		DriverTab.tableDriverList.selectRow(2);
		assertThat(DriverTab.tableDriverList.getRow(2).getCell(2).getValue()).isEqualTo("Justin");
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.DEFENSIVE_DRIVER_COURSE_COMPLETED).getValue()).isEqualTo("No");
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.AFFINITY_GROUP).getValue()).isEqualTo("None");
		driverTab.saveAndExit();

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas14591_AddDriversUnhappyAgeBody(PolicyType policyType) throws ParseException {
		DriverTab driverTab = new DriverTab();
		int minimumAge; //States minimum age for Drivers License
		String errorCode;
		String errorMessage;

		if ("KS".contains(getState())) {
			minimumAge = 15;
			errorCode = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_KS.getCode();
			errorMessage = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_KS.getMessage();

		} else if ("MT".contains(getState())) {
			minimumAge = 15;
			errorCode = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_MT.getCode();
			errorMessage = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_MT.getMessage();

		} else if ("SD".contains(getState())) {
			minimumAge = 14;
			errorCode = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_SD.getCode();
			errorMessage = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_SD.getMessage();

		} else if ("VA".contains(getState())) {
			minimumAge = 16;
			errorCode = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_VA.getCode();
			errorMessage = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_VA.getMessage();

		} else if ("NV".contains(getState())) {
			minimumAge = 16;
			errorCode = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_NV.getCode();
			errorMessage = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_NV.getMessage();

		} else {
			minimumAge = 16;
			errorCode = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_COMMON.getCode();
			errorMessage = ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_COMMON.getMessage();
		}

		String birthDateError = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().minusYears(minimumAge - 1).toString(); // date for Error response scenario
		String birthDateNoError = TimeSetterUtil.getInstance().getCurrentTime().toLocalDate().minusYears(minimumAge).toString(); // date for no Error scenario
		//format birthDateNoError date for validation in drivers tab
		String birthDateNoErrorFormatted = formatBirthDateForDriverTab(birthDateNoError);

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyDefaultTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		addDriverRequest.firstName = "Young";
		addDriverRequest.middleName = "Driver";
		addDriverRequest.lastName = "Jill";
		addDriverRequest.birthDate = birthDateError;
		addDriverRequest.suffix = "III";

		ErrorResponseDto errorResponseDto = HelperCommon.executeEndorsementAddDriverError(policyNumber, addDriverRequest);
		ViewDriversResponse responseViewDrivers1 = HelperCommon.viewEndorsementDrivers(policyNumber);

		assertSoftly(softly -> {
			//validate addDriver error response
			softly.assertThat(errorResponseDto.errors.size()).isEqualTo(1);
			softly.assertThat(errorResponseDto.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(errorResponseDto.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(errorResponseDto.errors.get(0).errorCode).isEqualTo(errorCode);
			softly.assertThat(errorResponseDto.errors.get(0).message).contains(errorMessage);
			softly.assertThat(errorResponseDto.errors.get(0).field).isEqualTo("age");

			//validate viewEndorsementDrivers response
			softly.assertThat(responseViewDrivers1.driverList.size()).isEqualTo(1); //new driver is not added

		});

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

		//validate that there is only 1 driver in PAS UI (new driver is not added)
		assertSoftly(softly -> {
			softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LIST_OF_DRIVER).getTable().
					getColumn(AutoSSMetaData.DriverTab.ListOfDriver.LAST_NAME.getLabel()).getCellsCount()).isEqualTo(1);

			softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LIST_OF_DRIVER).getTable().
					getColumn(AutoSSMetaData.DriverTab.ListOfDriver.LAST_NAME.getLabel()).getCell(1).getValue()).isNotEqualTo(addDriverRequest.lastName);
		});

		driverTab.saveAndExit();

		addDriverRequest.birthDate = birthDateNoError;//modify existing request by changing DOB
		DriversDto driverResponseDto = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		ViewDriversResponse responseViewDrivers2 = HelperCommon.viewEndorsementDrivers(policyNumber);
		//filter newly added driver
		DriversDto responseNewDriverFiltered = responseViewDrivers2.driverList.stream().filter(driver -> driver.firstName.equals(addDriverRequest.firstName) && driver.lastName.equals(addDriverRequest.lastName)).findFirst().orElse(null);

		assertSoftly(softly -> {
			//validate addDriver response
			softly.assertThat(driverResponseDto.firstName).isEqualTo(addDriverRequest.firstName);
			softly.assertThat(driverResponseDto.middleName).isEqualTo(addDriverRequest.middleName);
			softly.assertThat(driverResponseDto.lastName).isEqualTo(addDriverRequest.lastName);
			softly.assertThat(driverResponseDto.birthDate).isEqualTo(birthDateNoError).isEqualTo(addDriverRequest.birthDate);
			softly.assertThat(driverResponseDto.suffix).isEqualTo(addDriverRequest.suffix);

			//Validate view drivers response
			softly.assertThat(responseViewDrivers2.driverList.size()).isEqualTo(2);
			softly.assertThat(responseNewDriverFiltered.firstName).isEqualTo(addDriverRequest.firstName);
			softly.assertThat(responseNewDriverFiltered.middleName).isEqualTo(addDriverRequest.middleName);
			softly.assertThat(responseNewDriverFiltered.lastName).isEqualTo(addDriverRequest.lastName);
			softly.assertThat(responseNewDriverFiltered.birthDate).isEqualTo(birthDateNoError).isEqualTo(addDriverRequest.birthDate);
			softly.assertThat(responseNewDriverFiltered.suffix).isEqualTo(addDriverRequest.suffix);
		});

		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		DriverTab.viewDriver(2);

		//validate that driver is added in PAS UI
		assertSoftly(softly -> {
			softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LIST_OF_DRIVER).getTable().
					getColumn(AutoSSMetaData.DriverTab.ListOfDriver.LAST_NAME.getLabel()).getCellsCount()).isEqualTo(2);

			softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LIST_OF_DRIVER).getTable().
					getColumn(AutoSSMetaData.DriverTab.ListOfDriver.FIRST_NAME.getLabel()).getCell(2).getValue()).isEqualTo(addDriverRequest.firstName);

			softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LIST_OF_DRIVER).getTable().
					getColumn(AutoSSMetaData.DriverTab.ListOfDriver.LAST_NAME.getLabel()).getCell(2).getValue()).isEqualTo(addDriverRequest.lastName);

			softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LIST_OF_DRIVER).getTable().
					getColumn(AutoSSMetaData.DriverTab.ListOfDriver.BIRTH_DATE.getLabel()).getCell(2).getValue()).contains(birthDateNoErrorFormatted);

			softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.FIRST_NAME).getValue()).isEqualTo(addDriverRequest.firstName);
			softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.LAST_NAME).getValue()).isEqualTo(addDriverRequest.lastName);
			softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.MIDDLE_NAME).getValue()).isEqualTo(addDriverRequest.middleName);
			softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.DATE_OF_BIRTH).getValue()).isEqualTo(birthDateNoErrorFormatted);
			softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.SUFFIX).getValue()).isEqualTo(addDriverRequest.suffix);
		});
	}

	protected void pas477_UpdateDriversBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();

		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		helperMiniServices.createEndorsementWithCheck(policyNumber);
		SearchPage.openPolicy(policyNumber);

		addDriverRequest.firstName = "Young";
		addDriverRequest.middleName = "Driver";
		addDriverRequest.lastName = "Jill";
		addDriverRequest.birthDate = "1999-02-13";
		addDriverRequest.suffix = "III";

		DriversDto addDriverRequestService = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		String driverOid = addDriverRequestService.oid;

		assertThat(addDriverRequestService.firstName).isEqualTo(addDriverRequest.firstName);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());

		assertThat(DriverTab.tableDriverList.getRow(2).getCell(2).getValue()).isEqualTo("Young");

		DriverTab.tableDriverList.selectRow(2);
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.OCCUPATION).getValue()).isEqualTo("Employed");

		driverTab.saveAndExit();

		UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();
		updateDriverRequest.stateLicensed = "AZ";
		updateDriverRequest.licenseNumber = "D32329585";
		updateDriverRequest.gender = "male";
		updateDriverRequest.relationToApplicantCd = "CH";
		updateDriverRequest.maritalStatusCd = "SSS";
		updateDriverRequest.ageFirstLicensed = 16;

		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, driverOid, updateDriverRequest);
		assertSoftly(softly -> {

			softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse.driver.gender).isEqualTo(updateDriverRequest.gender);
			softly.assertThat(updateDriverResponse.driver.relationToApplicantCd).isEqualTo(updateDriverRequest.relationToApplicantCd);
			softly.assertThat(updateDriverResponse.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse.driver.maritalStatusCd).isEqualTo(updateDriverRequest.maritalStatusCd);
			softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);

		});

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());

		assertThat(DriverTab.tableDriverList.getRow(2).getCell(2).getValue()).isEqualTo("Young");
		DriverTab.tableDriverList.selectRow(2);
        assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
                .DriverTab.REL_TO_FIRST_NAMED_INSURED).getValue()).isEqualTo("Child");
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.SMART_DRIVER_COURSE_COMPLETED).getValue()).isEqualTo("No");
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.AFFINITY_GROUP).getValue()).isEqualTo("None");

		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.MOST_RECENT_GPA).getValue()).isEqualTo("None");

		driverTab.saveAndExit();

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas9662_maxDriversBody(PolicyType policyType) {
		TestData td = getPolicyTD("DataGather", "TestData");
		td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_FiveDrivers").getTestDataList("DriverTab")).resolveLinks();

		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(td);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			//Check view drivers service response
			ViewDriversResponse viewDrivers = HelperCommon.viewPolicyDrivers(policyNumber);
			assertThat(viewDrivers.canAddDriver).isEqualTo(true);

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//hit view driver endorsement service
			ViewDriversResponse responseViewDriverEndorsement = HelperCommon.viewEndorsementDrivers(policyNumber);
			assertThat(responseViewDriverEndorsement.canAddDriver).isEqualTo(true);

			//Add D6
			addDriverRequest.firstName = "Justin";
			addDriverRequest.lastName = "Jill";
			addDriverRequest.birthDate = "1960-02-08";

			DriversDto addDriver6 = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
			String driverOid6 = addDriver6.oid;

			updateDriverRequest.stateLicensed = "VA";
			updateDriverRequest.licenseNumber = "T32329585";
			updateDriverRequest.gender = "male";
			updateDriverRequest.relationToApplicantCd = "CH";
			updateDriverRequest.maritalStatusCd = "SSS";
			updateDriverRequest.ageFirstLicensed = 18;
			HelperCommon.updateDriver(policyNumber, driverOid6, updateDriverRequest);

			//hit view driver endorsement service
			ViewDriversResponse responseViewDriverEndorsement2 = HelperCommon.viewEndorsementDrivers(policyNumber);
			assertThat(responseViewDriverEndorsement2.canAddDriver).isEqualTo(true);

			//Add D7
			addDriverRequest.firstName = "Maris";
			addDriverRequest.lastName = "Smith";
			addDriverRequest.birthDate = "1990-02-08";

			DriversDto addDriver7 = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
			String driverOid7 = addDriver7.oid;

			updateDriverRequest.stateLicensed = "VA";
			updateDriverRequest.licenseNumber = "T32329222";
			updateDriverRequest.gender = "male";
			updateDriverRequest.relationToApplicantCd = "CH";
			updateDriverRequest.maritalStatusCd = "SSS";
			updateDriverRequest.ageFirstLicensed = 18;
			HelperCommon.updateDriver(policyNumber, driverOid7, updateDriverRequest);

			//hit view driver endorsement service
			ViewDriversResponse responseViewDriverEndorsement3 = HelperCommon.viewEndorsementDrivers(policyNumber);
			assertThat(responseViewDriverEndorsement3.canAddDriver).isEqualTo(false);

			//Add D8
			addDriverRequest.firstName = "Vadym";
			addDriverRequest.lastName = "Smith";
			addDriverRequest.birthDate = "1990-05-01";

			ErrorResponseDto addDriver8 = HelperCommon.executeEndorsementAddDriverError(policyNumber, addDriverRequest);
			softly.assertThat(addDriver8.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(addDriver8.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(addDriver8.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_DRIVERS.getCode());
			softly.assertThat(addDriver8.errors.get(0).message).contains(ErrorDxpEnum.Errors.MAX_NUMBER_OF_DRIVERS.getMessage());

			helperMiniServices.endorsementRateAndBind(policyNumber);

			//Check view drivers service response after first endorsement
			ViewDriversResponse viewDrivers2 = HelperCommon.viewPolicyDrivers(policyNumber);
			assertThat(viewDrivers2.canAddDriver).isEqualTo(false);

			//Create second pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//hit view driver endorsement service
			ViewDriversResponse responseViewDriverEndorsement4 = HelperCommon.viewEndorsementDrivers(policyNumber);
			assertThat(responseViewDriverEndorsement4.canAddDriver).isEqualTo(false);

			ErrorResponseDto addDriver9 = HelperCommon.executeEndorsementAddDriverError(policyNumber, addDriverRequest);
			softly.assertThat(addDriver9.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
			softly.assertThat(addDriver9.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
			softly.assertThat(addDriver9.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_DRIVERS.getCode());
			softly.assertThat(addDriver9.errors.get(0).message).contains(ErrorDxpEnum.Errors.MAX_NUMBER_OF_DRIVERS.getMessage());

			helperMiniServices.endorsementRateAndBind(policyNumber);
		});
	}

	protected void pas13301_validateDriverLicenseAndAgeFirstLicensedBody(){
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		ViewDriversResponse responseViewDriverEndorsement = HelperCommon.viewEndorsementDrivers(policyNumber);
		String firstDriverOid = (responseViewDriverEndorsement.driverList.get(0).oid);

		String LicenseNr_VA = "831278809";
		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.licenseNumber = LicenseNr_VA;
		updateDriverRequest.ageFirstLicensed = 12;
		DriverWithRuleSets updateDriverResponse2 = HelperCommon.updateDriver(policyNumber, firstDriverOid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse2.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse2.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse2.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse2.ruleSets.get(0).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.AGE_FIRST_LICENSED_ERROR.getMessage())));
		});

		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.licenseNumber = "123";
		updateDriverRequest.ageFirstLicensed = 12;
		DriverWithRuleSets updateDriverResponse3 = HelperCommon.updateDriver(policyNumber, firstDriverOid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse3.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse3.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse3.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse3.ruleSets.get(0).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.AGE_FIRST_LICENSED_ERROR.getMessage())));
			softly.assertThat(updateDriverResponse3.ruleSets.get(1).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.VALIDATE_DRIVER_LICENSE_BY_STATE.getMessage())));
		});

		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.licenseNumber = LicenseNr_VA;
		updateDriverRequest.ageFirstLicensed = 18;
		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, firstDriverOid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse.ruleSets.isEmpty());
		});

		helperMiniServices.rateEndorsementWithCheck(policyNumber);

		//add new driver
		addDriverRequest.firstName = "Maris";
		addDriverRequest.lastName = "Smith";
		addDriverRequest.birthDate = "1990-02-08";

		DriversDto addDriver = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		String driverOid = addDriver.oid;

		String LicenseNr_MD = "S123456789999";

		updateDriverRequest.gender = "female";
		updateDriverRequest.relationToApplicantCd = "CH";
		updateDriverRequest.maritalStatusCd = "MSS";
		updateDriverRequest.stateLicensed = "MD";
		updateDriverRequest.licenseNumber = LicenseNr_MD;
		updateDriverRequest.ageFirstLicensed = 12;

		DriverWithRuleSets updateDriverResponse4 = HelperCommon.updateDriver(policyNumber, driverOid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse4.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse4.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse4.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse4.ruleSets.get(0).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.AGE_FIRST_LICENSED_ERROR.getMessage())));
		});

		updateDriverRequest.stateLicensed = "MD";
		updateDriverRequest.licenseNumber = "123";
		updateDriverRequest.ageFirstLicensed = 12;
		DriverWithRuleSets updateDriverResponse5 = HelperCommon.updateDriver(policyNumber, driverOid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse5.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse5.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse5.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse5.ruleSets.get(0).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.AGE_FIRST_LICENSED_ERROR.getMessage())));
			softly.assertThat(updateDriverResponse5.ruleSets.get(1).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.VALIDATE_DRIVER_LICENSE_BY_STATE.getMessage())));
		});

		updateDriverRequest.stateLicensed = "MD";
		updateDriverRequest.licenseNumber = LicenseNr_MD;
		updateDriverRequest.ageFirstLicensed = 18;
		DriverWithRuleSets updateDriverResponse6 = HelperCommon.updateDriver(policyNumber, driverOid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse6.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse6.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse6.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse6.ruleSets.isEmpty());
		});

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas15077_orderReports_endorsementBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyDefaultTD());
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

	protected void pas16694_orderReports_not_Named_Insured_endorsementBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyDefaultTD());
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

	private void checkThatMvrIsOrdered(AddDriverRequest addDriverRequest, int tableRowIndex, String expectedMvrResponse) {
		assertSoftly(softly -> {
			assertThat(DriverActivityReportsTab.tableMVRReports.getRows().size()).isEqualTo(tableRowIndex);
			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.NAME_ON_LICENSE.getLabel()).getValue()).contains(addDriverRequest.firstName);

			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.DATE_OF_BIRTH.getLabel()).getValue()).isEqualToIgnoringCase("01/31/1999"); //the same as addDriverRequest.birthDate

			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.STATE.getLabel()).getValue()).isEqualToIgnoringCase(updateDriverRequest.stateLicensed);

			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.LICENSE_NO.getLabel()).getValue()).isEqualToIgnoringCase(updateDriverRequest.licenseNumber);

			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.LICENSE_STATUS.getLabel()).getValue()).containsIgnoringCase("VALID");

			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.REPORT.getLabel()).getValue()).isEqualToIgnoringCase("View MVR");

			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.ORDER_DATE.getLabel()).getValue())
					.isEqualToIgnoringCase(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.RECEIPT_DATE.getLabel()).getValue())
					.isNotBlank(); //it can be also past date if report has been ordered previously, hence checking only that it is not blank

			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderMVRReport.RESPONSE.getLabel()).getValue())
					.isEqualToIgnoringCase(expectedMvrResponse);
		});
	}

	private void checkThatClueIsOrdered(int tableRowIndex, String expectedClueResponse) {
		assertSoftly(softly -> {
			assertThat(DriverActivityReportsTab.tableCLUEReports.getRows().size()).isEqualTo(tableRowIndex);

			assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.RESIDENTIAL_ADDRESS.getLabel()).getValue()).isNotEmpty();

			assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.REPORT.getLabel()).getValue()).isEqualToIgnoringCase("View CLUE");

			assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.ORDER_DATE.getLabel()).getValue())
					.isEqualToIgnoringCase(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

			assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.RECEIPT_DATE.getLabel()).getValue())
					.isEqualToIgnoringCase(TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));

			assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex) //TODO-mstrazds: validating exact response wll be handled in next sprints. Update to "isEqualToIgnoringCase(expectedClueResponse)"
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.RESPONSE.getLabel()).getValue()).isNotBlank(); //TODO-mstrazds: some US/defect number would be useful

			assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.ADDRESS_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("Current");

			assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(tableRowIndex)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.ORDER_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("Add Driver");
		});
	}

	//String birthDate must be in format yyyy-MM-dd
	private String formatBirthDateForDriverTab(String birthDate) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date birthDateFormatted = formatter.parse(birthDate);
		SimpleDateFormat driverPageFormatter = new SimpleDateFormat("MM/dd/yyyy");
		return driverPageFormatter.format(birthDateFormatted);
	}

    protected void pas15076_MetadataServiceDriverBody() {
        mainApp().open();
        String policyNumber = getCopiedPolicy();

        //Initiate endorsement
        helperMiniServices.createEndorsementWithCheck(policyNumber);

        //Add a driver outside of PAS
        addDriverRequest.firstName = "Steve";
        addDriverRequest.lastName = "Smith";
        addDriverRequest.birthDate = "1953-04-26";
        DriversDto addDriverRequestService = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
        String driverOid = addDriverRequestService.oid;

        //Verify that the correct responses display
        AttributeMetadata[] metaDataResponse = HelperCommon.viewEndorsementDriversMetaData(policyNumber, driverOid);
		assertSoftly(softly -> {
        	AttributeMetadata metaDataFieldResponseDriverType = testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "driverType", true, true, true, null, "String");
			softly.assertThat(metaDataFieldResponseDriverType.valueRange.get("nafr")).isEqualTo("Not Available for Rating");
			softly.assertThat(metaDataFieldResponseDriverType.valueRange.get("afr")).isEqualTo("Available for Rating");

			AttributeMetadata metaDataFieldResponseDriverRelation = testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "relationToApplicantCd", true, true, true, null, "String");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("IN")).isEqualTo("First Named Insured");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("SP")).isEqualTo("Spouse");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("CH")).isEqualTo("Child");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("PA")).isEqualTo("Parent");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("SI")).isEqualTo("Sibling");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("ORR")).isEqualTo("Other Resident Relative");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("EMP")).isEqualTo("Employee");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("OT")).isEqualTo("Other");

			testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "firstName", true, true, true, "50", "String");		   	testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "middleName", true, true, false, "50", "String");
			testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "lastName", true, true, true, "50", "String");

			AttributeMetadata metaDataFieldResponseSuffix = testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "suffix", true, true, false, null, "String");
			softly.assertThat(metaDataFieldResponseSuffix.valueRange.get("JR")).isEqualTo("Jr");
			softly.assertThat(metaDataFieldResponseSuffix.valueRange.get("SR")).isEqualTo("Sr");
			softly.assertThat(metaDataFieldResponseSuffix.valueRange.get("II")).isEqualTo("II");
			softly.assertThat(metaDataFieldResponseSuffix.valueRange.get("III")).isEqualTo("III");
			softly.assertThat(metaDataFieldResponseSuffix.valueRange.get("IV")).isEqualTo("IV");

			testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "birthDate", true, true, true, null, "Date");

			AttributeMetadata metaDataFieldResponseGender = testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "gender", true, true, true, null, "String");
			softly.assertThat(metaDataFieldResponseGender.valueRange.get("male")).isEqualTo("Male");
			softly.assertThat(metaDataFieldResponseGender.valueRange.get("female")).isEqualTo("Female");

			AttributeMetadata metaDataFieldResponseMarital = testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "maritalStatusCd", true, true, true, null, "String");
			softly.assertThat(metaDataFieldResponseMarital.valueRange.get("MSS")).isEqualTo("Married");
			softly.assertThat(metaDataFieldResponseMarital.valueRange.get("SSS")).isEqualTo("Single");
			softly.assertThat(metaDataFieldResponseMarital.valueRange.get("DSS")).isEqualTo("Divorced");
			softly.assertThat(metaDataFieldResponseMarital.valueRange.get("WSS")).isEqualTo("Widowed");
			softly.assertThat(metaDataFieldResponseMarital.valueRange.get("PSS")).isEqualTo("Separated");

			testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "drivingLicense.stateLicensed", true, true, true, null, "String");
			testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "drivingLicense.licenseNumber", true, true, false, "255", "String");
			testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "ageFirstLicensed", true, true, true, "3", "Integer");
			testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "driverStatus", true, false, false, null, "String");
		});
    }

    protected void pas14474_UpdateSpouseDriverBody(PolicyType policyType) {

        assertSoftly(softly -> {

            mainApp().open();
            createCustomerIndividual();

            // create policy via pas
            String policyNumber = getCopiedPolicy();

            System.out.println("createEndorsement via dxp");
            helperMiniServices.createEndorsementWithCheck(policyNumber);

            // addDriver via dxp
            addDriverRequest.firstName = "Spouse";
            addDriverRequest.middleName = "Driver";
            addDriverRequest.lastName = "Smith";
            addDriverRequest.birthDate = "1979-02-13";
            DriversDto addDriverRequestService = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
            String driverOid = addDriverRequestService.oid;

            assertThat(addDriverRequestService.firstName).isEqualTo(addDriverRequest.firstName);

            // updateDriver via dxp
            UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();
            updateDriverRequest.stateLicensed = "AZ";
            updateDriverRequest.licenseNumber = "D32329585";
            updateDriverRequest.gender = "female";
            updateDriverRequest.relationToApplicantCd = "SP";
            updateDriverRequest.ageFirstLicensed = 16;

            DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, driverOid, updateDriverRequest);
            softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
            softly.assertThat(updateDriverResponse.driver.gender).isEqualTo(updateDriverRequest.gender);
            softly.assertThat(updateDriverResponse.driver.relationToApplicantCd).isEqualTo(updateDriverRequest.relationToApplicantCd);
            softly.assertThat(updateDriverResponse.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
            softly.assertThat(updateDriverResponse.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
            softly.assertThat(updateDriverResponse.driver.maritalStatusCd).isEqualTo("MSS");
            softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);

            ViewDriversResponse viewDriverResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
            softly.assertThat(viewDriverResponse.driverList.get(0).oid).isNotNull();
            softly.assertThat(viewDriverResponse.driverList.get(0).firstName).isEqualTo("Spouse");
            softly.assertThat(viewDriverResponse.driverList.get(0).lastName).isEqualTo("Smith");
            softly.assertThat(viewDriverResponse.driverList.get(0).driverType).isEqualTo("afr");
            softly.assertThat(viewDriverResponse.driverList.get(0).namedInsuredType).isEqualTo("NI");
            softly.assertThat(viewDriverResponse.driverList.get(0).relationToApplicantCd).isEqualTo("SP");
            softly.assertThat(viewDriverResponse.driverList.get(0).maritalStatusCd).isEqualTo("MSS");

            softly.assertThat(viewDriverResponse.driverList.get(1).oid).isNotNull();
            softly.assertThat(viewDriverResponse.driverList.get(1).firstName).startsWith("Fernando");
            softly.assertThat(viewDriverResponse.driverList.get(1).lastName).isEqualTo("Smith");
            softly.assertThat(viewDriverResponse.driverList.get(1).driverType).isEqualTo("afr");
            softly.assertThat(viewDriverResponse.driverList.get(1).namedInsuredType).isEqualTo("FNI");
            softly.assertThat(viewDriverResponse.driverList.get(1).relationToApplicantCd).isEqualTo("IN");
            softly.assertThat(viewDriverResponse.driverList.get(1).maritalStatusCd).isEqualTo("MSS");

            mainApp().open();
            SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

            // go to pended endorsement
            PolicySummaryPage.buttonPendedEndorsement.click();
            policy.dataGather().start();

            GeneralTab generalTab = new GeneralTab();

            MultiAssetList namedInsuredInfo = generalTab.getNamedInsuredInfoAssetList();

            // get FNI's address
            String address1 = namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ADDRESS_LINE_1).getValue();

            // verify the 2nd named insureds
            generalTab.viewInsured(2);
            namedInsuredInfo = generalTab.getNamedInsuredInfoAssetList();
            softly.assertThat(namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME).getValue()).isEqualTo("Spouse");

            // assert 'less than 3 yrs' & 'residence'
            softly.assertThat(namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ADDRESS_LINE_1).getValue()).isEqualTo(address1);
            softly.assertThat(namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS).getValue()).isEqualTo("No");
            softly.assertThat(namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.IS_RESIDENTAL_DIFFERENF_FROM_MAILING).getValue()).isEqualTo("No");
            softly.assertThat(namedInsuredInfo.getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE).getValue()).isEqualTo("Own Home");

            // nav to driver tab
            NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());

            // assert fname
            softly.assertThat(DriverTab.tableDriverList.getRow(2).getCell(2).getValue()).isEqualTo("Spouse");

            // view edit 2nd driver
            DriverTab.tableDriverList.selectRow(2);

            // assert relation to FNI
            softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED).getValue()).isEqualTo("Spouse");

            driverTab.saveAndExit();
        });

    }

}



