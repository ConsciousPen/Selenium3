package aaa.modules.regression.service.helper;

import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.MIDDLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import java.util.List;

import aaa.main.enums.ErrorDxpEnum;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableList;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.TestDataManager;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.datax.TestData;

public class TestMiniServicesDriversHelper extends PolicyBaseTest {

	private DriverTab driverTab = new DriverTab();
	private AddDriverRequest addDriverRequest = new AddDriverRequest();
	private HelperMiniServices helperMiniServices = new HelperMiniServices();
	private TestEValueDiscount testEValueDiscount = new TestEValueDiscount();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private VehicleTab vehicleTab = new VehicleTab();
	private UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();

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
		AAAEndorseResponse responseEndorsement = HelperCommon.createEndorsement(policyNumber, endorsementDate);
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
		String firstName1= firstName.substring(0, firstName.length() - 5);
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

				//Add D7
				addDriverRequest.firstName = "Jovita";
				addDriverRequest.lastName = "Smith";
				addDriverRequest.birthDate = "1990-05-08";

				ErrorResponseDto addDriver8 = HelperCommon.executeEndorsementAddDriverError(policyNumber, addDriverRequest);
				softly.assertThat(addDriver8.errorCode).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getCode());
				softly.assertThat(addDriver8.message).isEqualTo(ErrorDxpEnum.Errors.ERROR_OCCURRED_WHILE_EXECUTING_OPERATIONS.getMessage());
				softly.assertThat(addDriver8.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_VEHICLES.getCode());
				softly.assertThat(addDriver8.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.MAX_NUMBER_OF_VEHICLES.getMessage());

				helperMiniServices.endorsementRateAndBind(policyNumber);










		});
	}
}



