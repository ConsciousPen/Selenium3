package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableList;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;

import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.service.helper.dtoDxp.*;
import toolkit.datax.TestData;

public class TestMiniServicesDriversAbstract extends PolicyBaseTest {

	private DriverTab driverTab = new DriverTab();

	private AddDriverRequest addDriverRequest = new AddDriverRequest();

	private HelperMiniServices helperMiniServices;

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

		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

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
			//softly.assertThat(addDriverRequestService.birthDate).isEqualTo(addDriverRequest.birthDate);
		});

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());

		assertThat(DriverTab.tableDriverList.getRow(2).getCell(2).getValue()).isEqualTo("Justin");

		DriverTab.tableDriverList.selectRow(2);
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.DEFENSIVE_DRIVER_COURSE_COMPLETED).getValue().equals("No")).isTrue();
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.OCCUPATION).getValue().equals("Employed")).isTrue();

		driverTab.saveAndExit();

		ViewDriversResponse responseViewDriverEndorsement = HelperCommon.viewEndorsementDrivers(policyNumber);
		String driverOid = responseViewDriverEndorsement.driverList.get(1).oid;
		assertSoftly(softly -> {
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).firstName).startsWith("Fernando");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).lastName).isEqualTo("Smith");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).driverType).isEqualTo("afr");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).namedInsuredType).isEqualTo("FNI");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).relationToApplicantCd).isEqualTo("IN");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).maritalStatusCd).isEqualTo("MSS");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).driverStatus).isEqualTo("active");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(0).birthDate).isEqualTo("1962-12-05");

			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).oid).isNotNull();
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).firstName).isEqualTo("Justin");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).lastName).isEqualTo("Jill");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).middleName).isEqualTo("Doc");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).driverType).isEqualTo("afr");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).namedInsuredType).isEqualTo("Not a Named Insured");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).relationToApplicantCd).isEqualTo("CH");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).maritalStatusCd).isEqualTo("SSS");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).driverStatus).isEqualTo("pendingAdd");
			softly.assertThat(responseViewDriverEndorsement.driverList.get(1).birthDate).isEqualTo("1960-02-08");

		});

		UpdateDriverRequest updateDriverRequest = new UpdateDriverRequest();
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
			softly.assertThat(updateDriverResponse.driver.driverLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse.driver.driverLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
			softly.assertThat(updateDriverResponse.driver.maritalStatusCd).isEqualTo(updateDriverRequest.maritalStatusCd);
			softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(updateDriverRequest.ageFirstLicensed);
		});

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());
		DriverTab.tableDriverList.selectRow(2);
		assertThat(DriverTab.tableDriverList.getRow(2).getCell(2).getValue()).isEqualTo("Young");
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.DEFENSIVE_DRIVER_COURSE_COMPLETED).getValue().equals("No")).isTrue();
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.AFFINITY_GROUP).getValue().equals("None")).isTrue();
		driverTab.saveAndExit();

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithCheck(policyNumber);

	}

	protected void pas477_UpdateDriversBody(PolicyType policyType) {
		mainApp().open();
		createCustomerIndividual();

		policyType.get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		String policyNumber = PolicySummaryPage.getPolicyNumber();

		//Create pended endorsement
		AAAEndorseResponse response = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(response.policyNumber).isEqualTo(policyNumber);

		SearchPage.openPolicy(policyNumber);

		addDriverRequest.firstName = "Young";
		addDriverRequest.middleName = "Driver";
		addDriverRequest.lastName = "Jill";
		addDriverRequest.birthDate = "1999-02-13";
		addDriverRequest.suffix = "III";

		DriversDto addDriverRequestService = HelperCommon.executeEndorsementAddDriver(policyNumber, addDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(addDriverRequestService.firstName).isEqualTo(addDriverRequest.firstName);
		});

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());

		assertThat(DriverTab.tableDriverList.getRow(2).getCell(2).getValue()).isEqualTo("Young");

		DriverTab.tableDriverList.selectRow(2);
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.OCCUPATION).getValue().equals("Employed")).isTrue();

		driverTab.saveAndExit();

		ViewDriversResponse responseViewDriverEndorsement = HelperCommon.viewEndorsementDrivers(policyNumber);

		String driverOid = responseViewDriverEndorsement.driverList.get(1).oid;

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
			softly.assertThat(updateDriverResponse.driver.driverLicense.licenseNumber).isEqualTo(updateDriverRequest.licenseNumber);
			softly.assertThat(updateDriverResponse.driver.driverLicense.stateLicensed).isEqualTo(updateDriverRequest.stateLicensed);
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
				.DriverTab.SMART_DRIVER_COURSE_COMPLETED).getValue().equals("No")).isTrue();
		assertThat(driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.AFFINITY_GROUP).getValue().equals("None")).isTrue().isTrue();

		driverTab.getAssetList().getAsset(AutoSSMetaData
				.DriverTab.MOST_RECENT_GPA).setValue("None");

		driverTab.saveAndExit();

		helperMiniServices.rateEndorsementWithCheck(policyNumber);
		helperMiniServices.bindEndorsementWithCheck(policyNumber);
	}
}



