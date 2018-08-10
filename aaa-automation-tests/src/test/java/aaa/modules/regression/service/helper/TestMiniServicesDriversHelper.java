package aaa.modules.regression.service.helper;

import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.MIDDLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Response;
import org.assertj.core.api.SoftAssertions;
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
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class TestMiniServicesDriversHelper extends PolicyBaseTest {

	private static final String DRIVER_TYPE_AVAILABLE_FOR_RATING = "afr";
	private static final String DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING = "nafr";
	private static final String DRIVER_FIRST_NAME_INSURED = "FNI";
	private static final String DRIVER_NAME_INSURED = "NI";
	private static final String DRIVER_STATUS_ACTIVE = "active";

	private DriverTab driverTab = new DriverTab();
	private AddDriverRequest addDriverRequest = new AddDriverRequest();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();
	private TestMiniServicesGeneralHelper testMiniServicesGeneralHelper = new TestMiniServicesGeneralHelper();
	private RemoveDriverRequest removeDriverRequest = new RemoveDriverRequest();

	protected void pas11932_viewDriversInfo(PolicyType policyType) {
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();

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
			softly.assertThat(driverNd.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
			softly.assertThat(driverNd.oid).isNotEmpty();

			softly.assertThat(driverRd).isNotNull();
			softly.assertThat(driverRd.middleName).isEqualTo(middleName3);
			softly.assertThat(driverRd.lastName).isEqualTo(lastName3);
			softly.assertThat(driverRd.suffix).isEqualTo(suffix3);
			softly.assertThat(driverRd.driverType).isEqualTo(DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING);
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
			softly.assertThat(driverSt2.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
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
			softly.assertThat(driverSt3.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
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
			softly.assertThat(driverSt4.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
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
			softly.assertThat(response.driverList.get(0).driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
			softly.assertThat(response.driverList.get(0).namedInsuredType).isEqualTo("FNI");
			softly.assertThat(response.driverList.get(0).relationToApplicantCd).isEqualTo("IN");
			softly.assertThat(response.driverList.get(0).maritalStatusCd).isEqualTo("MSS");

			softly.assertThat(response.driverList.get(1).oid).isNotNull();
			softly.assertThat(response.driverList.get(1).firstName).isEqualTo("Jenny");
			softly.assertThat(response.driverList.get(1).lastName).isEqualTo("Smith");
			softly.assertThat(response.driverList.get(1).driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
			softly.assertThat(response.driverList.get(1).namedInsuredType).isEqualTo("NI");
			softly.assertThat(response.driverList.get(1).relationToApplicantCd).isEqualTo("PA");
			softly.assertThat(response.driverList.get(1).maritalStatusCd).isEqualTo("SSS");

			softly.assertThat(response.driverList.get(2).oid).isNotNull();
			softly.assertThat(response.driverList.get(2).firstName).isEqualTo("Laura");
			softly.assertThat(response.driverList.get(2).lastName).isEqualTo("Smith");
			softly.assertThat(response.driverList.get(2).middleName).isEqualTo("Sara");
			softly.assertThat(response.driverList.get(2).suffix).isEqualTo("III");
			softly.assertThat(response.driverList.get(2).driverType).isEqualTo(DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING);
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
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).namedInsuredType).isEqualTo("FNI");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).relationToApplicantCd).isEqualTo("IN");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).maritalStatusCd).isEqualTo("MSS");

			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).firstName).isEqualTo("Jenny");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).lastName).isEqualTo("Smith");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).namedInsuredType).isEqualTo("NI");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).relationToApplicantCd).isEqualTo("PA");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).maritalStatusCd).isEqualTo("SSS");

			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).firstName).isEqualTo("Laura");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).lastName).isEqualTo("Smith");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).middleName).isEqualTo("Sara");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).suffix).isEqualTo("III");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).driverType).isEqualTo(DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING);
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).relationToApplicantCd).isEqualTo("CH");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).maritalStatusCd).isEqualTo("DSS");

		});
	}

	protected void pas482_ViewDriverServiceOrderOfDriverBody(TestData td) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(td);
		ViewDriversResponse viewDriversResponse = HelperCommon.viewPolicyDrivers(policyNumber);
		validateDriverListOrdering(viewDriversResponse.driverList);
	}

	protected void pas14653_ViewDriverServiceOrderOfPendingDeleteBody(TestData td) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(td);

		// create endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		// view drivers & get one to remove: afr, active, not FNI, not NI
		ViewDriversResponse viewDriversResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
		DriversDto driverSt = viewDriversResponse.driverList.stream()
				.filter(driver -> DRIVER_TYPE_AVAILABLE_FOR_RATING.equals(driver.driverType))
				.filter(driver -> DRIVER_STATUS_ACTIVE.equals(driver.driverStatus))
				.filter(driver -> !DRIVER_FIRST_NAME_INSURED.equals(driver.namedInsuredType))
				.filter(driver -> !DRIVER_NAME_INSURED.equals(driver.namedInsuredType)).findFirst().orElse(null);
		RemoveDriverRequest removeDriverRequest = new RemoveDriverRequest();
		removeDriverRequest.removalReasonCode = "RD1001";
		HelperCommon.removeDriver(policyNumber, driverSt.oid, removeDriverRequest);

		// add driver
		AddDriverRequest addDriverRequest = new AddDriverRequest();
		addDriverRequest.firstName = "Jackie";
		addDriverRequest.middleName = "Ann";
		addDriverRequest.lastName = "Jones";
		addDriverRequest.birthDate = "1964-02-08";
		addDriverRequest.suffix = "I";
		DriversDto addedDriverResponse = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);

		// update driver
		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.licenseNumber = "D32329585";
		updateDriverRequest.gender = "female";
		updateDriverRequest.relationToApplicantCd = "CH";
		updateDriverRequest.maritalStatusCd = "MSS";
		updateDriverRequest.ageFirstLicensed = 16;
		HelperCommon.updateDriver(policyNumber, addedDriverResponse.oid, updateDriverRequest);

		// verify order: pending remove should be first, then pending add
		viewDriversResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
		validateDriverListOrdering(viewDriversResponse.driverList);

		// rate and bind
		helperMiniServices.endorsementRateAndBind(policyNumber);

		// create 2nd endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		// remove previously added driver
		RemoveDriverRequest removeDriverRequest3 = new RemoveDriverRequest();
		removeDriverRequest.removalReasonCode = "RD1001";
		HelperCommon.removeDriver(policyNumber, addedDriverResponse.oid, removeDriverRequest3);

		// verify order: pending remove should be first, then pending add
		viewDriversResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
		validateDriverListOrdering(viewDriversResponse.driverList);
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
		String addedDriverOid = addDriverRequestService.oid;
		assertSoftly(softly -> {
			softly.assertThat(addDriverRequestService.firstName).isEqualTo(addDriverRequest.firstName);
			softly.assertThat(addDriverRequestService.middleName).isEqualTo(addDriverRequest.middleName);
			softly.assertThat(addDriverRequestService.lastName).isEqualTo(addDriverRequest.lastName);
			softly.assertThat(addDriverRequestService.suffix).isEqualTo(addDriverRequest.suffix);
			softly.assertThat(addDriverRequestService.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
			softly.assertThat(addDriverRequestService.namedInsuredType).isEqualTo("Not a Named Insured");
			softly.assertThat(addDriverRequestService.relationToApplicantCd).isEqualTo(null);
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
		assertSoftly(softly -> {
			softly.assertThat(driver1.oid).isNotNull();
			softly.assertThat(driver1.firstName).startsWith(firstName1);
			softly.assertThat(driver1.lastName).isEqualTo(lastName1);
			softly.assertThat(driver1.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
			softly.assertThat(driver1.namedInsuredType).isEqualTo("FNI");
			softly.assertThat(driver1.relationToApplicantCd).isEqualTo("IN");
			softly.assertThat(driver1.maritalStatusCd).isEqualTo("MSS");
			softly.assertThat(driver1.driverStatus).isEqualTo("active");
			softly.assertThat(driver1.birthDate).isEqualTo("1962-12-05");

			softly.assertThat(driver2.oid).isNotNull();
			softly.assertThat(driver2.firstName).isEqualTo("Justin");
			softly.assertThat(driver2.lastName).isEqualTo("Jill");
			softly.assertThat(driver2.middleName).isEqualTo("Doc");
			softly.assertThat(driver2.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
			softly.assertThat(driver2.namedInsuredType).isEqualTo("Not a Named Insured");
			softly.assertThat(driver2.relationToApplicantCd).isEqualTo(null);
			softly.assertThat(driver2.maritalStatusCd).isEqualTo("SSS");
			softly.assertThat(driver2.driverStatus).isEqualTo("pendingAdd");
			softly.assertThat(driver2.birthDate).isEqualTo("1960-02-08");

		});

		updateDriverRequest.stateLicensed = "AZ";
		updateDriverRequest.licenseNumber = "D32329999";
		updateDriverRequest.gender = "male";
		updateDriverRequest.relationToApplicantCd = "CH";
		updateDriverRequest.maritalStatusCd = "MSS";
		updateDriverRequest.ageFirstLicensed = 16;

		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, addedDriverOid, updateDriverRequest);
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

	protected void pas13301_validateDriverLicenseAndAgeFirstLicensedBody() {
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
			softly.assertThat(updateDriverResponse2.ruleSets.get(0).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.AGE_FIRST_LICENSED_ERROR.getMessage()))).isTrue();
		});

		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.licenseNumber = "123";
		updateDriverRequest.ageFirstLicensed = 12;
		DriverWithRuleSets updateDriverResponse3 = HelperCommon.updateDriver(policyNumber, firstDriverOid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse3.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse3.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse3.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);

			assertThat(updateDriverResponse3.ruleSets.stream()
					.anyMatch(ruleSet -> ruleSet.errors
							.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.AGE_FIRST_LICENSED_ERROR.getMessage()))))
					.isTrue();

			assertThat(updateDriverResponse3.ruleSets.stream()
					.anyMatch(ruleSet -> ruleSet.errors
							.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.VALIDATE_DRIVER_LICENSE_BY_STATE.getMessage()))))
					.isTrue();
		});

		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.licenseNumber = LicenseNr_VA;
		updateDriverRequest.ageFirstLicensed = 18;
		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, firstDriverOid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse.ruleSets.isEmpty()).isTrue();
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
			softly.assertThat(updateDriverResponse4.ruleSets.get(0).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.AGE_FIRST_LICENSED_ERROR.getMessage()))).isTrue();
		});

		updateDriverRequest.stateLicensed = "MD";
		updateDriverRequest.licenseNumber = "123";
		updateDriverRequest.ageFirstLicensed = 12;
		DriverWithRuleSets updateDriverResponse5 = HelperCommon.updateDriver(policyNumber, driverOid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse5.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse5.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse5.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse5.ruleSets.get(0).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.AGE_FIRST_LICENSED_ERROR.getMessage()))).isTrue();
			softly.assertThat(updateDriverResponse5.ruleSets.get(1).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.VALIDATE_DRIVER_LICENSE_BY_STATE.getMessage()))).isTrue();
		});

		updateDriverRequest.stateLicensed = "MD";
		updateDriverRequest.licenseNumber = LicenseNr_MD;
		updateDriverRequest.ageFirstLicensed = 18;
		DriverWithRuleSets updateDriverResponse6 = HelperCommon.updateDriver(policyNumber, driverOid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse6.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse6.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse6.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse6.ruleSets.isEmpty()).isTrue();
		});

		helperMiniServices.endorsementRateAndBind(policyNumber);
	}

	protected void pas15373_uniqueDriverLicensesBody(PolicyType policyType) {
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();
		TestData customerData = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);

		//Drivers info from testData
		String firstNameFull = getStateTestData(customerData, "DataGather", "TestData").getTestDataList("GeneralTab").get(0).getValue("First Name");
		String firstName1 = firstNameFull.substring(0, firstNameFull.length() - 5);
		String firstName2 = td.getTestDataList("DriverTab").get(1).getValue("First Name");
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(testData);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			//Create a pended Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//get drivers oid
			ViewDriversResponse dResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driver1 = dResponse.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			DriversDto driver2 = dResponse.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

			String dlDriver1 = driver1.drivingLicense.licenseNumber.toString();
			String driverOid2 = driver2.oid;
			String dlDriver2 = driver2.drivingLicense.licenseNumber.toString();

			updateDriverRequest.stateLicensed = "VA";
			updateDriverRequest.licenseNumber = dlDriver1;
			DriverWithRuleSets updateDriverResponse1 = HelperCommon.updateDriver(policyNumber, driverOid2, updateDriverRequest);
			softly.assertThat(updateDriverResponse1.ruleSets.get(0).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.DUPLICATE_DRIVER_LICENSE_ERROR.getMessage()))).isTrue();

			updateDriverRequest.licenseNumber = dlDriver2;
			DriverWithRuleSets updateDriverResponse2 = HelperCommon.updateDriver(policyNumber, driverOid2, updateDriverRequest);
			softly.assertThat(updateDriverResponse2.ruleSets.isEmpty()).isTrue();

			//Bind and create new one
			helperMiniServices.endorsementRateAndBind(policyNumber);
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			//Check drivers DL
			ViewDriversResponse dResponse2 = HelperCommon.viewEndorsementDrivers(policyNumber);
			driver1 = dResponse2.driverList.stream().filter(driver -> driver.firstName.startsWith(firstName1)).findFirst().orElse(null);
			driver2 = dResponse2.driverList.stream().filter(driver -> firstName2.equals(driver.firstName)).findFirst().orElse(null);

			softly.assertThat(driver1.firstName).startsWith(firstName1);
			softly.assertThat(driver1.drivingLicense.licenseNumber).isEqualTo(dlDriver1);
			softly.assertThat(driver2.firstName).isEqualTo(firstName2);
			softly.assertThat(driver2.drivingLicense.licenseNumber).isEqualTo(dlDriver2);

			//Add new driver
			addDriverRequest.firstName = "Jovita";
			addDriverRequest.lastName = "Smith";
			addDriverRequest.birthDate = "1990-02-08";

			DriversDto addDriver = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
			String newDriverOid = addDriver.oid;

			updateDriverRequest.gender = "female";
			updateDriverRequest.relationToApplicantCd = "CH";
			updateDriverRequest.maritalStatusCd = "MSS";
			updateDriverRequest.stateLicensed = "VA";
			updateDriverRequest.licenseNumber = dlDriver2;
			updateDriverRequest.ageFirstLicensed = 18;

			DriverWithRuleSets updateDriverResponse4 = HelperCommon.updateDriver(policyNumber, newDriverOid, updateDriverRequest);
			softly.assertThat(updateDriverResponse4.ruleSets.get(0).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.DUPLICATE_DRIVER_LICENSE_ERROR.getMessage()))).isTrue();

			String dlDriver3 = "831218809";
			updateDriverRequest.licenseNumber = dlDriver3;
			DriverWithRuleSets updateDriverResponse5 = HelperCommon.updateDriver(policyNumber, newDriverOid, updateDriverRequest);
			softly.assertThat(updateDriverResponse5.ruleSets.isEmpty()).isTrue();

			helperMiniServices.endorsementRateAndBind(policyNumber);
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
			softly.assertThat(metaDataFieldResponseDriverType.valueRange.get(DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING)).isEqualTo("Not Available for Rating");
			softly.assertThat(metaDataFieldResponseDriverType.valueRange.get(DRIVER_TYPE_AVAILABLE_FOR_RATING)).isEqualTo("Available for Rating");

			AttributeMetadata metaDataFieldResponseDriverRelation = testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "relationToApplicantCd", true, true, true, null, "String");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("IN")).isEqualTo("First Named Insured");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("SP")).isEqualTo("Spouse");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("CH")).isEqualTo("Child");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("PA")).isEqualTo("Parent");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("SI")).isEqualTo("Sibling");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("ORR")).isEqualTo("Other Resident Relative");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("EMP")).isEqualTo("Employee");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("OT")).isEqualTo("Other");

			testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "firstName", true, true, true, "50", "String");
			testMiniServicesGeneralHelper.getAttributeMetadata(metaDataResponse, "middleName", true, true, false, "50", "String");
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
			String fniDriverName = HelperCommon.viewPolicyDrivers(policyNumber).driverList.get(0).firstName;
			String fniDriverLastName = HelperCommon.viewPolicyDrivers(policyNumber).driverList.get(0).lastName;

			helperMiniServices.createEndorsementWithCheck(policyNumber);

			// addDriver via dxp
			addDriverRequest.firstName = "Spouse";
			addDriverRequest.middleName = "Driver";
			addDriverRequest.lastName = "Smith";
			addDriverRequest.birthDate = "1979-02-13";
			DriversDto addDriverRequestService = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
			String newDriverOid = addDriverRequestService.oid;

			assertThat(addDriverRequestService.firstName).isEqualTo(addDriverRequest.firstName);

			// updateDriver via dxp
			UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();
			updateDriverRequest.stateLicensed = "AZ";
			updateDriverRequest.licenseNumber = "D32329585";
			updateDriverRequest.gender = "female";
			updateDriverRequest.relationToApplicantCd = "SP";
			updateDriverRequest.ageFirstLicensed = 16;

			DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, newDriverOid, updateDriverRequest);
			softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
			softly.assertThat(updateDriverResponse.driver.gender).isEqualTo(updateDriverRequest.gender);
			softly.assertThat(updateDriverResponse.driver.relationToApplicantCd).isEqualTo(updateDriverRequest.relationToApplicantCd);
			softly.assertThat(updateDriverResponse.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse.driver.maritalStatusCd).isEqualTo("MSS");
			softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);

			ViewDriversResponse viewDriverResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			softly.assertThat(viewDriverResponse.driverList.size()).isEqualTo(2);

			DriversDto addedSpouse = viewDriverResponse.driverList.stream().filter(driver -> driver.oid.equals(newDriverOid)).findAny().orElse(null);
			softly.assertThat(addedSpouse.oid).isNotNull();
			softly.assertThat(addedSpouse.firstName).isEqualTo("Spouse");
			softly.assertThat(addedSpouse.lastName).isEqualTo("Smith");
			softly.assertThat(addedSpouse.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
			softly.assertThat(addedSpouse.namedInsuredType).isEqualTo("NI");
			softly.assertThat(addedSpouse.relationToApplicantCd).isEqualTo("SP");
			softly.assertThat(addedSpouse.maritalStatusCd).isEqualTo("MSS");

			DriversDto fniDriver = viewDriverResponse.driverList.stream().filter(driver -> !driver.oid.equals(newDriverOid)).findAny().orElse(null);
			softly.assertThat(fniDriver.oid).isNotNull();
			softly.assertThat(fniDriver.firstName).isEqualTo(fniDriverName);
			softly.assertThat(fniDriver.lastName).isEqualTo(fniDriverLastName);
			softly.assertThat(fniDriver.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
			softly.assertThat(fniDriver.namedInsuredType).isEqualTo("FNI");
			softly.assertThat(fniDriver.relationToApplicantCd).isEqualTo("IN");
			softly.assertThat(fniDriver.maritalStatusCd).isEqualTo("MSS");

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

	protected void pas16481_TransactionInformationForEndorsementsAddDriverBody(SoftAssertions softly) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		//Create a pended Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		ComparablePolicy response1 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		softly.assertThat(response1.drivers).isEqualTo(null);

		//add Driver
		addDriverRequest.firstName = "Jovita";
		addDriverRequest.lastName = "Smith";
		addDriverRequest.birthDate = "1990-02-08";
		DriversDto addDriver = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		String oid = addDriver.oid;

		updateDriverRequest.gender = "female";
		updateDriverRequest.relationToApplicantCd = "CH";
		updateDriverRequest.maritalStatusCd = "MSS";
		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.licenseNumber = "A12345678";
		updateDriverRequest.ageFirstLicensed = 18;
		HelperCommon.updateDriver(policyNumber, oid, updateDriverRequest);

		//Check first Driver
		ComparablePolicy response2 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableDriver driver2 = response2.drivers.get(oid);
		softly.assertThat(driver2.changeType).isEqualTo("ADDED");
		softly.assertThat(driver2.data.oid).isEqualTo(oid);
		softly.assertThat(driver2.data.firstName).isEqualTo(addDriverRequest.firstName);
		softly.assertThat(driver2.data.lastName).isEqualTo(addDriverRequest.lastName);
		softly.assertThat(driver2.data.middleName).isEqualTo(null);
		softly.assertThat(driver2.data.suffix).isEqualTo(null);
		softly.assertThat(driver2.data.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
		softly.assertThat(driver2.data.namedInsuredType).isEqualTo("Not a Named Insured");
		softly.assertThat(driver2.data.relationToApplicantCd).isEqualTo(updateDriverRequest.relationToApplicantCd);
		softly.assertThat(driver2.data.maritalStatusCd).isEqualTo(updateDriverRequest.maritalStatusCd);
		softly.assertThat(driver2.data.driverStatus).isEqualTo("pendingAdd");
		softly.assertThat(driver2.data.birthDate).startsWith(addDriverRequest.birthDate);
		softly.assertThat(driver2.data.gender).isEqualTo(updateDriverRequest.gender);
		softly.assertThat(driver2.data.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
		softly.assertThat(driver2.drivingLicense.changeType).isEqualTo("ADDED");
		softly.assertThat(driver2.drivingLicense.data.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
		softly.assertThat(driver2.drivingLicense.data.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);

		helperMiniServices.endorsementRateAndBind(policyNumber);
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		ComparablePolicy response3 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		softly.assertThat(response3.drivers).isEqualTo(null);

		//add Driver 3
		addDriverRequest.firstName = "Megha";
		addDriverRequest.lastName = "Smith";
		addDriverRequest.birthDate = "1987-02-08";
		DriversDto addDriver3 = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		String oid3 = addDriver3.oid;

		updateDriverRequest.gender = "female";
		updateDriverRequest.relationToApplicantCd = "CH";
		updateDriverRequest.maritalStatusCd = "MSS";
		updateDriverRequest.stateLicensed = "VA";
		updateDriverRequest.licenseNumber = "A12347777";
		updateDriverRequest.ageFirstLicensed = 16;
		HelperCommon.updateDriver(policyNumber, oid3, updateDriverRequest);

		//Check first Driver
		ComparablePolicy response4 = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		softly.assertThat(response4.drivers.get(oid)).isEqualTo(null);
		ComparableDriver driver3 = response4.drivers.get(oid3);
		softly.assertThat(driver3.changeType).isEqualTo("ADDED");
		softly.assertThat(driver3.data.oid).isEqualTo(oid3);
		softly.assertThat(driver3.data.firstName).isEqualTo(addDriverRequest.firstName);
		softly.assertThat(driver3.data.lastName).isEqualTo(addDriverRequest.lastName);
		softly.assertThat(driver3.data.middleName).isEqualTo(null);
		softly.assertThat(driver3.data.suffix).isEqualTo(null);
		softly.assertThat(driver3.data.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
		softly.assertThat(driver3.data.namedInsuredType).isEqualTo("Not a Named Insured");
		softly.assertThat(driver3.data.relationToApplicantCd).isEqualTo(updateDriverRequest.relationToApplicantCd);
		softly.assertThat(driver3.data.maritalStatusCd).isEqualTo(updateDriverRequest.maritalStatusCd);
		softly.assertThat(driver3.data.driverStatus).isEqualTo("pendingAdd");
		softly.assertThat(driver3.data.birthDate).startsWith(addDriverRequest.birthDate);
		softly.assertThat(driver3.data.gender).isEqualTo(updateDriverRequest.gender);
		softly.assertThat(driver3.data.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
		softly.assertThat(driver3.drivingLicense.changeType).isEqualTo("ADDED");
		softly.assertThat(driver3.drivingLicense.data.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
		softly.assertThat(driver3.drivingLicense.data.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);

		helperMiniServices.endorsementRateAndBind(policyNumber);

	}

	protected void pas14475_NameInsuredMaritalStatusBody() {
		assertSoftly(softly -> {

			mainApp().open();
			createCustomerIndividual();

			// create policy via pas
			String policyNumber = getCopiedPolicy();
			// Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewDriversResponse responseViewDrivers1 = HelperCommon.viewEndorsementDrivers(policyNumber);
			String dOid = responseViewDrivers1.driverList.get(0).oid;

			//Update FNI as a single
			UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();

			//Update existing driver with SSS
			updateDriver(softly, policyNumber, dOid, updateDriverRequest, "SSS");

			// add new NI Spouse
			addDriverAndVerify(policyNumber, updateDriverRequest, softly, true);

			//helperMiniServices.endorsementRateAndBind(policyNumber);

		});
	}

	private void addDriverAndVerify(String policyNumber, UpdateDriverRequest updateDriverRequest, SoftAssertions softly, boolean flag) {
		// addDriver via dxp
		addDriverRequest.firstName = "Spouse";
		addDriverRequest.middleName = "Driver";
		addDriverRequest.lastName = "Smith";
		addDriverRequest.birthDate = "1979-02-13";
		DriversDto addDriverRequestService = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		String driverOid = addDriverRequestService.oid;
		assertThat(addDriverRequestService.firstName).isEqualTo(addDriverRequest.firstName);

		// updateDriver via dxp as sp
		updateDriverRequest.stateLicensed = "AZ";
		updateDriverRequest.licenseNumber = "D32329585";
		updateDriverRequest.gender = "female";
		updateDriverRequest.relationToApplicantCd = "SP";
		updateDriverRequest.ageFirstLicensed = 16;

		DriverWithRuleSets updateDriverResponse1 = HelperCommon.updateDriver(policyNumber, driverOid, updateDriverRequest);
		softly.assertThat(updateDriverResponse1.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
		softly.assertThat(updateDriverResponse1.driver.gender).isEqualTo(updateDriverRequest.gender);
		softly.assertThat(updateDriverResponse1.driver.relationToApplicantCd).isEqualTo(updateDriverRequest.relationToApplicantCd);
		softly.assertThat(updateDriverResponse1.driver.drivingLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
		softly.assertThat(updateDriverResponse1.driver.drivingLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
		softly.assertThat(updateDriverResponse1.driver.maritalStatusCd).isEqualTo("MSS");
		softly.assertThat(updateDriverResponse1.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
		//Bug PAS-17579
		if (flag) {
			softly.assertThat(updateDriverResponse1.ruleSets.get(0).errors.stream().anyMatch(error -> error.contains(ErrorDxpEnum.Errors.INSURANCE_SCORE_ORDER_MESSAGE.getMessage()))).isTrue();
		} else {
			softly.assertThat(updateDriverResponse1.ruleSets).isEmpty();
		}

		ViewDriversResponse responseViewDrivers2 = HelperCommon.viewEndorsementDrivers(policyNumber);
		softly.assertThat(responseViewDrivers2.driverList.get(0).maritalStatusCd).isEqualTo("MSS");
		softly.assertThat(responseViewDrivers2.driverList.get(1).maritalStatusCd).isEqualTo("MSS");

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		// go to pended endorsement
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());

		softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.MARITAL_STATUS).getValue()).isEqualTo("Married");

		softly.assertThat(DriverTab.tableDriverList.getRow(2).getCell(2).getValue()).isEqualTo("Spouse");

		softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.MARITAL_STATUS).getValue()).isEqualTo("Married");
		driverTab.saveAndExit();
	}

	private void updateDriver(SoftAssertions softly, String policyNumber, String dOid, UpdateDriverRequest updateDriverRequest, String mStatus) {
		updateDriverRequest.maritalStatusCd = mStatus;
		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, dOid, updateDriverRequest);
		softly.assertThat(updateDriverResponse.driver.maritalStatusCd).isEqualTo(updateDriverRequest.maritalStatusCd);
	}

	protected void pas14475_NameInsuredMaritalStatusFNIIsDSSBody() {
		assertSoftly(softly -> {

			mainApp().open();
			createCustomerIndividual();

			// create policy via pas
			String policyNumber = getCopiedPolicy();

			// Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewDriversResponse responseViewDrivers1 = HelperCommon.viewEndorsementDrivers(policyNumber);
			String dOid = responseViewDrivers1.driverList.get(0).oid;

			//Update FNI as a single
			UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();

			//Update existing driver with SSS
			updateDriver(softly, policyNumber, dOid, updateDriverRequest, "DSS");

			// add new NI Spouse
			addDriverAndVerify(policyNumber, updateDriverRequest, softly, true);

		});
	}

	protected void pas14475_NameInsuredMaritalStatusFNIIsWSSBody() {
		assertSoftly(softly -> {

			mainApp().open();
			createCustomerIndividual();

			// create policy via pas
			String policyNumber = getCopiedPolicy();

			// Endorsement
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			ViewDriversResponse responseViewDrivers1 = HelperCommon.viewEndorsementDrivers(policyNumber);
			String dOid = responseViewDrivers1.driverList.get(0).oid;

			//Update FNI as a single
			UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();

			//Update existing driver with SSS
			updateDriver(softly, policyNumber, dOid, updateDriverRequest, "WSS");

			// add new NI Spouse
			addDriverAndVerify(policyNumber, updateDriverRequest, softly, true);

			//	helperMiniServices.endorsementRateAndBind(policyNumber);

		});
	}

	protected void pas14475_NameInsuredMaritalStatusFNIIsPSSBody(SoftAssertions softly) {

		mainApp().open();
		createCustomerIndividual();

		// create policy via pas
		String policyNumber = getCopiedPolicy();

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		ViewDriversResponse responseViewDrivers1 = HelperCommon.viewEndorsementDrivers(policyNumber);
		String dOid = responseViewDrivers1.driverList.get(0).oid;

		//Update FNI as a single
		UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();

		//Update existing driver with SSS
		updateDriver(softly, policyNumber, dOid, updateDriverRequest, "PSS");

		// add new NI Spouse
		addDriverAndVerify(policyNumber, updateDriverRequest, softly, true);

	}

	protected void pas16696_AddANameInsuredSameDayPolicyEffectiveDateBody() {
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();

			String policyNumber = getCopiedPolicy();
			helperMiniServices.createEndorsementWithCheck(policyNumber);

			addDriverAndVerify(policyNumber, updateDriverRequest, softly, true);

		});
	}

	protected void pas16696_AddANameInsuredSameDayNotPolicyEffectiveDateBody() {
		assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();

			String policyNumber = getCopiedPolicy();

			String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

			HelperCommon.createEndorsement(policyNumber, endorsementDate);

			addDriverAndVerify(policyNumber, updateDriverRequest, softly, false);

		});
	}

	protected void pas15513_ViewDriverRemoveDriverIndicatorBody(TestData td, PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createQuote(td);

		String policyNumber = testEValueDiscount.simplifiedQuoteIssue();

		ViewDriversResponse responseViewDriver = HelperCommon.viewPolicyDrivers(policyNumber);
		assertSoftly(softly -> {
			softly.assertThat(responseViewDriver.driverList.get(0).oid).isNotNull();
			softly.assertThat(responseViewDriver.driverList.get(0).availableActions).isEmpty();

			softly.assertThat(responseViewDriver.driverList.get(1).oid).isNotNull();
			softly.assertThat(responseViewDriver.driverList.get(1).availableActions).isEmpty();

			softly.assertThat(responseViewDriver.driverList.get(2).oid).isNotNull();
			softly.assertThat(responseViewDriver.driverList.get(2).availableActions.get(0)).isEqualTo("remove");

			softly.assertThat(responseViewDriver.driverList.get(3).oid).isNotNull();
			softly.assertThat(responseViewDriver.driverList.get(3).availableActions).isEmpty();

			softly.assertThat(responseViewDriver.driverList.get(4).oid).isNotNull();
			softly.assertThat(responseViewDriver.driverList.get(4).availableActions).isEmpty();

		});

		helperMiniServices.createEndorsementWithCheck(policyNumber);

		addDriverRequest.firstName = "Spouse";
		addDriverRequest.middleName = "Driver";
		addDriverRequest.lastName = "Smith";
		addDriverRequest.birthDate = "1979-02-13";
		DriversDto addDriverRequestService = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		String driverOid = addDriverRequestService.oid;
		assertThat(addDriverRequestService.firstName).isEqualTo(addDriverRequest.firstName);

		// updateDriver via dxp as sp
		updateDriverRequest.stateLicensed = "AZ";
		updateDriverRequest.licenseNumber = "D32329585";
		updateDriverRequest.gender = "female";
		updateDriverRequest.relationToApplicantCd = "CH";
		updateDriverRequest.ageFirstLicensed = 16;

		HelperCommon.updateDriver(policyNumber, driverOid, updateDriverRequest);

		ViewDriversResponse responseViewDriverEndorsement = HelperCommon.viewEndorsementDrivers(policyNumber);
		assertSoftly(softly -> {

			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).availableActions.get(0)).isEqualTo("remove");

			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).availableActions).isEmpty();

			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(2).availableActions).isEmpty();

			softly.assertThat(responseViewDriverEndorsement.driverList.get(3).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(3).availableActions.get(0)).isEqualTo("remove");

			softly.assertThat(responseViewDriverEndorsement.driverList.get(4).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(4).availableActions).isEmpty();

			softly.assertThat(responseViewDriverEndorsement.driverList.get(5).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(5).availableActions).isEmpty();

		});
	}

	protected void pas14640_NotNamedInsuredAvailableForRatingHappyPathBody() {
		assertSoftly(softly -> {
			TestData td = getPolicyTD("DataGather", "TestData");
			TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_2_Drivers_Not_NI_AfR").getTestDataList("DriverTab")).resolveLinks();

			mainApp().open();
			createCustomerIndividual();
			String policyNumber = createPolicy(testData);
			softly.assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			helperMiniServices.createEndorsementWithCheck(policyNumber);
			ViewDriversResponse viewDriversResponse = HelperCommon.viewEndorsementDrivers(policyNumber);

			DriversDto driverFNI = viewDriversResponse.driverList.get(0);
			DriversDto driver1 = viewDriversResponse.driverList.get(1);
			DriversDto driver2 = viewDriversResponse.driverList.get(2);

			validateDriverPreconditions_pas14641_pas14640_pas14642(softly, driverFNI, driver1);
			validateDriverPreconditions_pas14641_pas14640_pas14642(softly, driverFNI, driver2);

			DriversDto driver1ExpectedAfterRemove = viewDriversResponse.driverList.get(1);
			driver1ExpectedAfterRemove.driverStatus = "pendingRemoval";
			driver1ExpectedAfterRemove.availableActions.remove("remove");

			DriversDto driver2ExpectedAfterRemove = viewDriversResponse.driverList.get(2);
			driver2ExpectedAfterRemove.driverStatus = "pendingRemoval";
			driver2ExpectedAfterRemove.availableActions.remove("remove");

			//Remove driver 1
			removeDriverRequest.removalReasonCode = "RD1001";
			DriversDto removeDriver1Response = HelperCommon.removeDriver(policyNumber, driver1.oid, removeDriverRequest);
			softly.assertThat(removeDriver1Response).isEqualToComparingFieldByFieldRecursively(driver1ExpectedAfterRemove);

			//Remove driver 2
			removeDriverRequest.removalReasonCode = "RD1002";
			DriversDto removeDriver2Response = HelperCommon.removeDriver(policyNumber, driver2.oid, removeDriverRequest);
			softly.assertThat(removeDriver2Response).isEqualToComparingFieldByFieldRecursively(driver2ExpectedAfterRemove);

			//Run viewEndorsementDrivers and validate that it still contains drivers that will be removed
			ViewDriversResponse viewDriversResponseAfterDelete = HelperCommon.viewEndorsementDrivers(policyNumber);
			softly.assertThat(viewDriversResponseAfterDelete.driverList.size()).isEqualTo(3);
			softly.assertThat(viewDriversResponseAfterDelete.driverList.get(0)).isEqualToComparingFieldByFieldRecursively(driverFNI);
			softly.assertThat(viewDriversResponseAfterDelete.driverList.get(1)).isEqualToComparingFieldByFieldRecursively(driver1ExpectedAfterRemove);
			softly.assertThat(viewDriversResponseAfterDelete.driverList.get(2)).isEqualToComparingFieldByFieldRecursively(driver2ExpectedAfterRemove);

			//Run view drivers assignment and validate that drivers that are going to be removed are not present in response
			validateDriverAssignmentAfterRemove_pas14641_pas14640_pas14642(softly, policyNumber, driver1, driver2);

			//Open the Endorsement in PAS and validate that both drivers are removed
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.policyInquiry().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());

			DriverTab.tableDriverList.verify.rowsCount(1);
			softly.assertThat(DriverTab.tableDriverList.getRow(1).getCell(2).getValue()).isEqualTo(driverFNI.firstName);
			softly.assertThat(DriverTab.tableDriverList.getRow(1).getCell(3).getValue()).isEqualTo(driverFNI.lastName);
			SearchPage.openPolicy(policyNumber);

			helperMiniServices.endorsementRateAndBind(policyNumber);

			//Run viewPolicyDrivers and validate that drivers are removed
			ViewDriversResponse viewDriversResponseAfterBind = HelperCommon.viewPolicyDrivers(policyNumber);
			softly.assertThat(viewDriversResponseAfterBind.driverList.size()).isEqualTo(1);
			softly.assertThat(viewDriversResponseAfterBind.driverList.get(0)).isEqualToComparingFieldByFieldRecursively(driverFNI);
		});

	}

	private void validateDriverAssignmentAfterRemove_pas14641_pas14640_pas14642(SoftAssertions softly, String policyNumber, DriversDto driver1, DriversDto driver2) {
		ViewDriverAssignmentResponse viewDriverAssignmentResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
		softly.assertThat(viewDriverAssignmentResponse.driverVehicleAssignments.size()).isEqualTo(1);
		softly.assertThat(viewDriverAssignmentResponse.driverVehicleAssignments.get(0).driverOid).doesNotContain(driver1.oid).doesNotContain(driver2.oid);
		softly.assertThat(viewDriverAssignmentResponse.assignableDrivers).doesNotContain(driver1.oid).doesNotContain(driver2.oid);
		softly.assertThat(viewDriverAssignmentResponse.unassignedDrivers).doesNotContain(driver1.oid).doesNotContain(driver2.oid);
	}

	protected void pas14642_NotNamedInsuredAvailableForRatingHardStopBody() {
		assertSoftly(softly -> {
			TestData td = getPolicyTD("DataGather", "TestData");
			TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_2_Drivers_Not_NI_AfR").getTestDataList("DriverTab")).resolveLinks();

			mainApp().open();
			createCustomerIndividual();
			String policyNumber = createPolicy(testData);
			softly.assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			helperMiniServices.createEndorsementWithCheck(policyNumber);
			ViewDriverAssignmentResponse viewDriverAssignmentResponseBefore = HelperCommon.viewEndorsementAssignments(policyNumber);
			ViewDriversResponse viewDriversResponse = HelperCommon.viewEndorsementDrivers(policyNumber);

			DriversDto driverFNI = viewDriversResponse.driverList.get(0);
			DriversDto driver1 = viewDriversResponse.driverList.get(1);
			DriversDto driver2 = viewDriversResponse.driverList.get(2);

			validateDriverPreconditions_pas14641_pas14640_pas14642(softly, driverFNI, driver1);
			validateDriverPreconditions_pas14641_pas14640_pas14642(softly, driverFNI, driver2);

			//Try to remove driver 1
			removeDriverRequest.removalReasonCode = "RD1005";
			ErrorResponseDto removeDriver1ResponseError = HelperCommon.removeDriverError(policyNumber, driver1.oid, removeDriverRequest);
			softly.assertThat(removeDriver1ResponseError.errorCode).isEqualTo("ERROR_SERVICE_VALIDATION");
			softly.assertThat(removeDriver1ResponseError.message).isEqualTo("Driver cannot be removed. Driver is insured elsewhere.");

			//Try to remove driver 2
			removeDriverRequest.removalReasonCode = "RD1006";
			ErrorResponseDto removeDriver2ResponseError = HelperCommon.removeDriverError(policyNumber, driver2.oid, removeDriverRequest);
			softly.assertThat(removeDriver2ResponseError.errorCode).isEqualTo("ERROR_SERVICE_VALIDATION");
			softly.assertThat(removeDriver2ResponseError.message).isEqualTo("Driver cannot be removed. Driver's license is suspended or revoked.");

			//Run viewEndorsementDrivers and validate that it still contains the drivers and details have not changed
			ViewDriversResponse viewDriversResponseAfterDelete = HelperCommon.viewEndorsementDrivers(policyNumber);
			softly.assertThat(viewDriversResponseAfterDelete.driverList.size()).isEqualTo(3);
			softly.assertThat(viewDriversResponseAfterDelete.driverList.get(0)).isEqualToComparingFieldByFieldRecursively(driverFNI);
			softly.assertThat(viewDriversResponseAfterDelete.driverList.get(1)).isEqualToComparingFieldByFieldRecursively(driver1);
			softly.assertThat(viewDriversResponseAfterDelete.driverList.get(2)).isEqualToComparingFieldByFieldRecursively(driver2);

			//Run view drivers assignment and validate that the drivers are still available for assignment and assignment has not changed
			ViewDriverAssignmentResponse viewDriverAssignmentResponse = HelperCommon.viewEndorsementAssignments(policyNumber);
			softly.assertThat(viewDriverAssignmentResponse.driverVehicleAssignments.size()).isEqualTo(3);
			softly.assertThat(viewDriverAssignmentResponse.driverVehicleAssignments.get(0)).isEqualToComparingFieldByFieldRecursively(viewDriverAssignmentResponseBefore.driverVehicleAssignments.get(0));
			softly.assertThat(viewDriverAssignmentResponse.driverVehicleAssignments.get(1)).isEqualToComparingFieldByFieldRecursively(viewDriverAssignmentResponseBefore.driverVehicleAssignments.get(1));
			softly.assertThat(viewDriverAssignmentResponse.driverVehicleAssignments.get(2)).isEqualToComparingFieldByFieldRecursively(viewDriverAssignmentResponseBefore.driverVehicleAssignments.get(2));

			softly.assertThat(viewDriverAssignmentResponse.assignableDrivers).isEqualTo(viewDriverAssignmentResponseBefore.assignableDrivers);
			softly.assertThat(viewDriverAssignmentResponse.unassignedDrivers).isEqualTo(viewDriverAssignmentResponseBefore.unassignedDrivers);

			//Open the Endorsement in PAS and validate that both drivers are NOT removed
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.policyInquiry().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());

			DriverTab.tableDriverList.verify.rowsCount(3);
			validateListOfDriverNotBlank(softly, 3);
			SearchPage.openPolicy(policyNumber);

			helperMiniServices.endorsementRateAndBind(policyNumber);

			//Run viewPolicyDrivers and validate that drivers are NOT removed
			validateViewDriverResponseAfterBind_pas14641_pas14640_pas14642(softly, policyNumber, driverFNI, driver1, driver2);
		});
	}

	protected void pas14641_NotNamedInsuredUpdateToNotAvailableForRatingBody() {
		assertSoftly(softly -> {
			TestData td = getPolicyTD("DataGather", "TestData");
			TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_2_Drivers_Not_NI_AfR").getTestDataList("DriverTab")).resolveLinks();

			mainApp().open();
			createCustomerIndividual();
			String policyNumber = createPolicy(testData);
			SearchPage.openPolicy(policyNumber);
			softly.assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			helperMiniServices.createEndorsementWithCheck(policyNumber);
			ViewDriversResponse viewDriversResponse = HelperCommon.viewEndorsementDrivers(policyNumber);

			DriversDto driverFNI = viewDriversResponse.driverList.get(0);
			DriversDto driver1 = viewDriversResponse.driverList.get(1);
			DriversDto driver2 = viewDriversResponse.driverList.get(2);

			validateDriverPreconditions_pas14641_pas14640_pas14642(softly, driverFNI, driver1);
			validateDriverPreconditions_pas14641_pas14640_pas14642(softly, driverFNI, driver2);

			DriversDto driver1ExpectedAfterRemove = viewDriversResponse.driverList.get(1);
			driver1ExpectedAfterRemove.driverType = DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING;
			driver1ExpectedAfterRemove.driverStatus = "updated";
			driver1ExpectedAfterRemove.availableActions.remove("remove");

			DriversDto driver2ExpectedAfterRemove = viewDriversResponse.driverList.get(2);
			driver2ExpectedAfterRemove.driverType = DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING;
			driver2ExpectedAfterRemove.driverStatus = "updated";
			driver2ExpectedAfterRemove.availableActions.remove("remove");

			//Remove driver 1
			removeDriverRequest.removalReasonCode = "RD1003";
			DriversDto removeDriver1Response = HelperCommon.removeDriver(policyNumber, driver1.oid, removeDriverRequest);
			softly.assertThat(removeDriver1Response).isEqualToComparingFieldByFieldRecursively(driver1ExpectedAfterRemove);

			//Remove driver 2
			removeDriverRequest.removalReasonCode = "RD1004";
			DriversDto removeDriver2Response = HelperCommon.removeDriver(policyNumber, driver2.oid, removeDriverRequest);
			softly.assertThat(removeDriver2Response).isEqualToComparingFieldByFieldRecursively(driver2ExpectedAfterRemove);

			//Run viewEndorsementDrivers and validate that it still contains both drivers and they both are updated
			validateViewEndorsementDrivers_pas14641_pas14640_pas14642(softly, policyNumber, driverFNI, driver1ExpectedAfterRemove, driver2ExpectedAfterRemove);

			//Run view drivers assignment and validate that both drivers are not present in response (because they are not available for rating)
			validateDriverAssignmentAfterRemove_pas14641_pas14640_pas14642(softly, policyNumber, driver1, driver2);

			//Open the Endorsement in PAS and validate that both drivers are updated
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());

			DriverTab.tableDriverList.verify.rowsCount(3);
			validateListOfDriverNotBlank(softly, 3);

			DriverTab.tableDriverList.selectRow(2);
			validateThatDriverIsUpdated_pas14641(softly);

			DriverTab.tableDriverList.selectRow(3);
			validateThatDriverIsUpdated_pas14641(softly);

			driverTab.saveAndExit();
			SearchPage.openPolicy(policyNumber);

			helperMiniServices.endorsementRateAndBind(policyNumber);

			//Run viewPolicyDrivers and validate that drivers are updated
			driver1ExpectedAfterRemove.driverStatus = "active";
			driver2ExpectedAfterRemove.driverStatus = "active";

			validateViewDriverResponseAfterBind_pas14641_pas14640_pas14642(softly, policyNumber, driverFNI, driver1ExpectedAfterRemove, driver2ExpectedAfterRemove);
		});
	}

	protected void pas14963_remove_driver_transaction_historyBody(PolicyType policyType) {

		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_TwoDrivers").getTestDataList("DriverTab")).resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(testData);

		//Perform Endorsement
		helperMiniServices.createEndorsementWithCheck(policyNumber);

		ViewDriversResponse viewDriver = HelperCommon.viewPolicyDrivers(policyNumber);
		String driverOid2 = viewDriver.driverList.get(1).oid;
		String stateLicensed = viewDriver.driverList.get(1).drivingLicense.stateLicensed;
		String licenseNumber = viewDriver.driverList.get(1).drivingLicense.licenseNumber;

		removeDriverRequest.removalReasonCode = "RD1001";
		DriversDto removeDriver2Response = HelperCommon.removeDriver(policyNumber, driverOid2, removeDriverRequest);

		ComparablePolicy policyResponse = HelperCommon.viewEndorsementChangeLog(policyNumber, Response.Status.OK.getStatusCode());
		ComparableDriver driver1 = policyResponse.drivers.get(driverOid2);
		assertSoftly(softly -> {
			softly.assertThat(driver1.changeType).isEqualTo("REMOVED");
			softly.assertThat(driver1.drivingLicense.changeType).isEqualTo("REMOVED");
			softly.assertThat(driver1.drivingLicense.data.stateLicensed).isEqualTo(stateLicensed);
			softly.assertThat(driver1.drivingLicense.data.licenseNumber).isEqualTo(licenseNumber);
		});
	}

	private void validateThatDriverIsUpdated_pas14641(SoftAssertions softly) {
		softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.NAMED_INSURED).getValue()).contains("not a Named Insured");
		softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.DRIVER_TYPE).getValue()).isEqualTo("Not Available for Rating");
		softly.assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData.DriverTab.REASON).getValue()).isEqualTo("Other");
	}

	private void validateViewDriverResponseAfterBind_pas14641_pas14640_pas14642(SoftAssertions softly, String policyNumber, DriversDto driverFNI, DriversDto driver1ExpectedAfterRemove, DriversDto driver2ExpectedAfterRemove) {
		ViewDriversResponse viewDriversResponseAfterBind = HelperCommon.viewPolicyDrivers(policyNumber);
		softly.assertThat(viewDriversResponseAfterBind.driverList.size()).isEqualTo(3);
		softly.assertThat(viewDriversResponseAfterBind.driverList.get(0)).isEqualToComparingFieldByFieldRecursively(driverFNI);
		softly.assertThat(viewDriversResponseAfterBind.driverList.get(1)).isEqualToComparingFieldByFieldRecursively(driver1ExpectedAfterRemove);
		softly.assertThat(viewDriversResponseAfterBind.driverList.get(2)).isEqualToComparingFieldByFieldRecursively(driver2ExpectedAfterRemove);
	}

	private void validateViewEndorsementDrivers_pas14641_pas14640_pas14642(SoftAssertions softly, String policyNumber, DriversDto driverFNI, DriversDto driver1ExpectedAfterRemove, DriversDto driver2ExpectedAfterRemove) {
		ViewDriversResponse viewDriversResponseAfterDelete = HelperCommon.viewEndorsementDrivers(policyNumber);
		softly.assertThat(viewDriversResponseAfterDelete.driverList.size()).isEqualTo(3);
		softly.assertThat(viewDriversResponseAfterDelete.driverList.get(0)).isEqualToComparingFieldByFieldRecursively(driverFNI);
		softly.assertThat(viewDriversResponseAfterDelete.driverList.get(1)).isEqualToComparingFieldByFieldRecursively(driver1ExpectedAfterRemove);
		softly.assertThat(viewDriversResponseAfterDelete.driverList.get(2)).isEqualToComparingFieldByFieldRecursively(driver2ExpectedAfterRemove);
	}

	private void validateDriverPreconditions_pas14641_pas14640_pas14642(SoftAssertions softly, DriversDto driverFNI, DriversDto driver) {
		softly.assertThat(driverFNI.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
		softly.assertThat(driverFNI.namedInsuredType).isEqualTo("FNI");

		softly.assertThat(driver.driverType).isEqualTo(DRIVER_TYPE_AVAILABLE_FOR_RATING);
		softly.assertThat(driver.namedInsuredType).isEqualTo("Not a Named Insured");
		softly.assertThat(driver.driverStatus).isEqualTo("active");
		softly.assertThat(driver.availableActions).containsSequence("remove");
	}

	private void validateListOfDriverNotBlank(SoftAssertions softly, int driverCount) {
		for (int i = 1; i <= driverCount; i++) {
			softly.assertThat(DriverTab.tableDriverList.getRow(i).getCell(2).getValue()).isNotBlank();
			softly.assertThat(DriverTab.tableDriverList.getRow(i).getCell(3).getValue()).isNotBlank();
			softly.assertThat(DriverTab.tableDriverList.getRow(i).getCell(4).getValue()).isNotBlank();
		}
	}

	private void validateDriverListOrdering(List<DriversDto> driverList) {
		List<DriversDto> originalOrderingFromResponse = ImmutableList.copyOf(driverList);
		List<DriversDto> sortedDriversFromResponse = driverList;
		sortedDriversFromResponse.sort(DriversDto.DRIVERS_COMPARATOR);
		assertSoftly(softly ->
				assertThat(originalOrderingFromResponse).containsAll(sortedDriversFromResponse)
		);
	}

}
