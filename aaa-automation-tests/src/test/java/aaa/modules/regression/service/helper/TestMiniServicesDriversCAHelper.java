package aaa.modules.regression.service.helper;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.rest.dtoDxp.*;
import aaa.main.enums.ErrorDxpEnum;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.pages.summary.PolicySummaryPage;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

import java.time.format.DateTimeFormatter;

import static aaa.modules.regression.service.auto_ss.functional.TestMiniServicesPremiumBearing.miniServicesEndorsementDeleteDelayConfigCheck;
import static aaa.modules.regression.service.auto_ss.functional.TestMiniServicesPremiumBearing.myPolicyUserAddedConfigCheck;
import static org.assertj.core.api.Assertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;

public class TestMiniServicesDriversCAHelper extends TestMiniServicesDriversHelper {
	private DriverTab driverTab = new DriverTab();

	protected void pas25057_AddDriverCADefaultValuesBody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		HelperCommon.createEndorsement(policyNumber, endorsementDate);
		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest("Jarred", "", "Benjami", "1960-02-08", "I");
		DriversDto addDriverResponse = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		assertSoftly(softly -> {
			softly.assertThat(addDriverResponse.aaaMaritalStatusCd).isNull();
			softly.assertThat(addDriverResponse.ageFirstLicensed).isNull();
			softly.assertThat(addDriverResponse.birthDate).isEqualTo("1960-02-08");
			softly.assertThat(addDriverResponse.driverStatus).isEqualTo("pendingAdd");
			softly.assertThat(addDriverResponse.birthDate).isEqualTo("1960-02-08");
			softly.assertThat(addDriverResponse.gender).isNull();
			softly.assertThat(addDriverResponse.namedInsuredType).isEqualTo("Not a Named Insured");
			softly.assertThat(addDriverResponse.driverType).isEqualTo("afr");
			softly.assertThat(addDriverResponse.firstName).isEqualTo("Jarred");
			softly.assertThat(addDriverResponse.lastName).isEqualTo("Benjami");
			softly.assertThat(addDriverResponse.suffix).isEqualTo("I");
		});

		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());
		assertThat(DriverTab.tableDriverList.getRow(2).getCell(2).getValue()).isEqualTo("Jarred");

		DriverTab.tableDriverList.selectRow(2);
		assertSoftly(softly -> {
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.OCCUPATION).getValue()).isEqualTo("Other");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.OCCUPATION_OTHER).getValue()).isEqualTo("Employed");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSED_IN_US_CANADA_FOR_18_OR_MORE_MONTHS).getValue()).isEqualTo("Yes");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSE_TYPE).getValue()).isEqualTo("Valid");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.SMOKER_CIGARETTES_OR_PIPES).getValue()).isEqualTo("No");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.DRIVER_RIDESHARING_COVERAGE).getValue()).isEqualTo("No");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.MATURE_DRIVER_COURSE_COMPLETED_WITHIN_36_MONTHS).getValue()).isEqualTo("No");
		});
		driverTab.saveAndExit();

		AddDriverRequest addDriverRequest2 = DXPRequestFactory.createAddDriverRequest("Connemara", "", "Morgan", "2000-02-08", null);
		HelperCommon.addDriver(policyNumber, addDriverRequest2, DriversDto.class);

		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());
		assertThat(DriverTab.tableDriverList.getRow(3).getCell(2).getValue()).isEqualTo("Connemara");
		DriverTab.tableDriverList.selectRow(3);
		assertSoftly(softly -> {
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.OCCUPATION).getValue()).isEqualTo("Other");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.OCCUPATION_OTHER).getValue()).isEqualTo("Employed");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSED_IN_US_CANADA_FOR_18_OR_MORE_MONTHS).getValue()).isEqualTo("Yes");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSE_TYPE).getValue()).isEqualTo("Valid");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.SMOKER_CIGARETTES_OR_PIPES).getValue()).isEqualTo("No");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.DRIVER_RIDESHARING_COVERAGE).getValue()).isEqualTo("No");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.NEW_DRIVER_COURSE_COMPLETED).getValue()).isEqualTo("No");
		});
		driverTab.saveAndExit();
	}

	protected void pas15428_UpdateDriver_CABody() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();
		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		HelperCommon.createEndorsement(policyNumber, endorsementDate);
		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest("Connemara", "", "Morgan", "2000-02-08", null);
		DriversDto addDriverResponse = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("female", "B1234567",
				16, "CA", "CH", "S", true, false);
		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, addDriverResponse.oid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse.driver.maritalStatusCd).isEqualTo("S");
			softly.assertThat(updateDriverResponse.driver.gender).isEqualTo("female");
			softly.assertThat(updateDriverResponse.driver.ageFirstLicensed).isEqualTo(16);
			softly.assertThat(updateDriverResponse.driver.relationToApplicantCd).isEqualTo("CH");
			softly.assertThat(updateDriverResponse.driver.drivingLicense.licenseNumber).isEqualTo("B1234567");
			softly.assertThat(updateDriverResponse.driver.drivingLicense.stateLicensed).isEqualTo("CA");
			softly.assertThat(updateDriverResponse.driver.permitBeforeLicense).isTrue();
		});

		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		PolicySummaryPage.buttonPendedEndorsement.click();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DRIVER.get());
		assertThat(DriverTab.tableDriverList.getRow(2).getCell(2).getValue()).isEqualTo("Connemara");
		DriverTab.tableDriverList.selectRow(2);
		assertSoftly(softly -> {
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.MARITAL_STATUS).getValue()).isEqualTo("Single");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.GENDER).getValue()).isEqualTo("Female");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.AGE_FIRST_LICENSED).getValue()).isEqualTo("16");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED).getValue()).isEqualTo("Child");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSE_STATE).getValue()).isEqualTo("CA");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.LICENSE_NUMBER).getValue()).isEqualTo("B1234567");
			softly.assertThat(driverTab.getAssetList().getAsset(AutoCaMetaData.DriverTab.PERMIT_BEFORE_LICENSE).getValue()).isEqualTo("Yes");
		});
		driverTab.saveAndExit();
	}

	protected void pas25053_ViewDriverServiceCANameInsureIndicator_body(PolicyType policyType) {
		assertSoftly(softly -> {
			TestData td = getTestSpecificTD("TestData_FilteredRelationshipDrivers_CA");

			mainApp().open();
			createCustomerIndividual();
			policyType.get().createPolicy(td);
			String policyNumber = PolicySummaryPage.getPolicyNumber();

			String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			HelperCommon.createEndorsement(policyNumber, endorsementDate);

			ViewDriversResponse viewDriversResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
			DriversDto driverFNI = viewDriversResponse.driverList.get(0);
			verifyNiDriver(softly, driverFNI);

			DriversDto driver1 = viewDriversResponse.driverList.get(1);
			softly.assertThat(driver1.namedInsuredType).isEqualTo("NI");
			softly.assertThat(driver1.driverType).isEqualTo("afr");

			DriversDto driver2 = viewDriversResponse.driverList.get(2);
			softly.assertThat(driver2.namedInsuredType).isEqualTo("NI");
			softly.assertThat(driver2.driverType).isEqualTo("nafr");

			DriversDto driver3 = viewDriversResponse.driverList.get(3);
			softly.assertThat(driver3.namedInsuredType).isEqualTo("NI");
			softly.assertThat(driver3.driverType).isEqualTo("excl");

			DriversDto driver4 = viewDriversResponse.driverList.get(4);
			softly.assertThat(driver4.namedInsuredType).isEqualTo("Not a Named Insured");
			softly.assertThat(driver4.driverType).isEqualTo("afr");

			DriversDto driver5 = viewDriversResponse.driverList.get(5);
			softly.assertThat(driver5.namedInsuredType).isEqualTo("Not a Named Insured");
			softly.assertThat(driver5.driverType).isEqualTo("nafr");

			DriversDto driver6 = viewDriversResponse.driverList.get(6);
			softly.assertThat(driver6.namedInsuredType).isEqualTo("Not a Named Insured");
			softly.assertThat(driver6.driverType).isEqualTo("nafr");
		});

	}

	protected void pas28687_AddRideshareDriverBody(PolicyType policyType) {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		HelperCommon.createEndorsement(policyNumber, endorsementDate);

		AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest("Connemara", "", "Morgan", "2000-02-08", null);
		DriversDto addDriverResponse = HelperCommon.addDriver(policyNumber, addDriverRequest, DriversDto.class);
		UpdateDriverRequest updateDriverRequest = DXPRequestFactory.createUpdateDriverRequest("female", "B1234567",
				16, "CA", "CH", "S", true, true);

		DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, addDriverResponse.oid, updateDriverRequest);
		assertSoftly(softly -> {
			softly.assertThat(updateDriverResponse.validations.get(0).errorCode).isEqualTo("AAA_CSA190426-yCW5j");
			softly.assertThat(updateDriverResponse.validations.get(0).message).contains("Rideshare Driver (AAA_CSA190426-yCW5j)");
			softly.assertThat(updateDriverResponse.validations.get(0).field).isEqualTo("ridesharingCoverage");
		});
	}

	protected void pas25055_ViewDriverServiceMetadataServiceRideshareQuestionBody(PolicyType policyType) {
		mainApp().open();
		myPolicyUserAddedConfigCheck();
		miniServicesEndorsementDeleteDelayConfigCheck();
		mainApp().open();
		createCustomerIndividual();
		TestData td = getPolicyTD("DataGather", "TestData");
		TestData testData = td.adjust(new DriverTab().getMetaKey(), getTestSpecificTD("TestData_Rideshare").getTestDataList("DriverTab")).resolveLinks();
		policyType.get().createPolicy(testData);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		HelperCommon.createEndorsement(policyNumber, endorsementDate);
		ViewDriversResponse viewDriversResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
		String driverOid = viewDriversResponse.driverList.get(0).oid;
		String driverOid1 = viewDriversResponse.driverList.get(1).oid;
		AttributeMetadata[] metaDataResponse = HelperCommon.viewEndorsementDriversMetaData(policyNumber, driverOid);
		assertSoftly(softly -> {
			getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "ridesharingCoverage", true, true, true, null, "Boolean");

		});

		AttributeMetadata[] metaDataResponse1 = HelperCommon.viewEndorsementDriversMetaData(policyNumber, driverOid1);
		assertSoftly(softly -> {
			getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse1, "ridesharingCoverage", true, false, true, null, "Boolean");

		});

	}

	protected void pas15408_ViewDriverServiceCA_Body(PolicyType policyType) {
		TestData td = getTestSpecificTD("TestData_FilteredRelationshipDrivers_CA");

		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(td);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		HelperCommon.createEndorsement(policyNumber, endorsementDate);
		ViewDriversResponse viewDriversResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
		viewDriversResponse.driverList.stream().filter(driver -> "IN".equals(driver.driverRelToApplicantCd)).findFirst().ifPresent(driver ->
				validateExistingDriver(null, "Smith", "FNI", "afr", "IN",
						"M", "male", "1976-07-08", 18, "CA", "E1234567", driver));
		viewDriversResponse.driverList.stream().filter(driver -> "Christopher".equals(driver.firstName)).findFirst().ifPresent(driver ->
				validateExistingDriver("Christopher", "Branwen", "NI", "afr", "PA",
						"S", "male", "1956-05-01", 16, "CA", "C1234567", driver));
		viewDriversResponse.driverList.stream().filter(driver -> "Orlaith".equals(driver.firstName)).findFirst().ifPresent(driver ->
				validateExistingDriver("Orlaith", "Branwen", "NI", "nafr", "PA",
						"S", "female", "1958-05-01", 16, "CA", "D1234567", driver));
		viewDriversResponse.driverList.stream().filter(driver -> "Vincent".equals(driver.firstName)).findFirst().ifPresent(driver ->
				validateExistingDriver("Vincent", "Branwen", "NI", "excl", "CH",
						"S", "male", "2000-01-01", 16, "CA", "B1234567", driver));
		viewDriversResponse.driverList.stream().filter(driver -> "Iseult".equals(driver.firstName)).findFirst().ifPresent(driver ->
				validateExistingDriver("Iseult", "Branwen", "Not a Named Insured", "afr", "CH",
						"S", "female", "2000-05-01", 16, "CA", "A1234567", driver));
		viewDriversResponse.driverList.stream().filter(driver -> "Iona".equals(driver.firstName)).findFirst().ifPresent(driver ->
				validateExistingDriver("Iona", "Branwen", "Not a Named Insured", "nafr", "CH",
						"S", "female", "2000-05-01", 16, "CA", "G1234567", driver));
		viewDriversResponse.driverList.stream().filter(driver -> "Moira".equals(driver.firstName)).findFirst().ifPresent(driver ->
				validateExistingDriver("Moira", "Branwen", "Not a Named Insured", "nafr", "CH",
						"S", "female", "2000-05-01", 16, "CA", "H1234567", driver));
	}

	protected void pas15408_ValidateDriverMetadataService_CA() {
		mainApp().open();
		String policyNumber = getCopiedPolicy();

		String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		HelperCommon.createEndorsement(policyNumber, endorsementDate);
		ViewDriversResponse viewDriversResponse = HelperCommon.viewEndorsementDrivers(policyNumber);
		String driverOid = viewDriversResponse.driverList.get(0).oid;

		AttributeMetadata[] metaDataResponse = HelperCommon.viewEndorsementDriversMetaData(policyNumber, driverOid);
		assertSoftly(softly -> {
			AttributeMetadata metaDataFieldResponseDriverType = getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "driverType", true, true, true, null, "String");
			softly.assertThat(metaDataFieldResponseDriverType.valueRange.get(DRIVER_TYPE_NOT_AVAILABLE_FOR_RATING)).isEqualTo("Not Available for Rating");
			softly.assertThat(metaDataFieldResponseDriverType.valueRange.get(DRIVER_TYPE_AVAILABLE_FOR_RATING)).isEqualTo("Available for Rating");
			softly.assertThat(metaDataFieldResponseDriverType.valueRange.get(DRIVER_TYPE_EXCLUDED)).isEqualTo("Excluded");

			AttributeMetadata metaDataFieldResponseDriverRelation = getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "relationToApplicantCd", true, true, true, null, "String");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("IN")).isEqualTo("First Named Insured");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("SP")).isEqualTo("Spouse");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("CH")).isEqualTo("Child");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("PA")).isEqualTo("Parent");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("SI")).isEqualTo("Sibling");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("ORR")).isEqualTo("Other Resident Relative");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("EMP")).isEqualTo("Employee");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("OT")).isEqualTo("Other");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("DP")).isEqualTo("Domestic Partner");
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("S")).isNull();
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("D")).isNull();
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("FR")).isNull();
			softly.assertThat(metaDataFieldResponseDriverRelation.valueRange.get("MR")).isNull();

			getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "firstName", false, true, true, null, "String");
			getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "middleName", false, true, false, null, "String");
			getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "lastName", false, true, true, null, "String");
			getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "birthDate", true, true, true, null, "Date");

			AttributeMetadata metaDataFieldResponseGender = getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "gender", true, true, true, null, "String");
			softly.assertThat(metaDataFieldResponseGender.valueRange.get("male")).isEqualTo("Male");
			softly.assertThat(metaDataFieldResponseGender.valueRange.get("female")).isEqualTo("Female");
			softly.assertThat(metaDataFieldResponseGender.valueRange.get("nonConforming")).isEqualTo("X");

			AttributeMetadata metaDataFieldResponseMarital = getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "maritalStatusCd", true, true, true, null, "String");
			softly.assertThat(metaDataFieldResponseMarital.valueRange.get("M")).isEqualTo("Married");
			softly.assertThat(metaDataFieldResponseMarital.valueRange.get("S")).isEqualTo("Single");
			softly.assertThat(metaDataFieldResponseMarital.valueRange.get("D")).isEqualTo("Divorced");
			softly.assertThat(metaDataFieldResponseMarital.valueRange.get("W")).isEqualTo("Widowed");
			softly.assertThat(metaDataFieldResponseMarital.valueRange.get("P")).isEqualTo("Separated");

            getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "drivingLicense.stateLicensed", true, true, true, null, "String");
            getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "drivingLicense.licenseNumber", true, true, false, "255", "String");
            getTestMiniServicesGeneralHelper().getAttributeMetadata(metaDataResponse, "ageFirstLicensed", true, true, true, "3", "Integer");
        });
    }

    protected void pas28684_AddUpdateDriverValidationsAndDefaultsBody(PolicyType policyType) {
        assertSoftly(softly -> {

            mainApp().open();
            createCustomerIndividual();
            String policyNumber = getCopiedPolicy();

            String endorsementDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            HelperCommon.createEndorsement(policyNumber, endorsementDate);

            // addDriver via dxp - with age less than 16 years
            String driverBday = TimeSetterUtil.getInstance().getCurrentTime().minusYears(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            AddDriverRequest addDriverRequest = DXPRequestFactory.createAddDriverRequest("John", "Driver", "Jones", driverBday, null);
            ErrorResponseDto addDriverResponse = HelperCommon.addDriver(policyNumber, addDriverRequest, ErrorResponseDto.class, 422);

            softly.assertThat(addDriverResponse.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_CA.getCode());
            softly.assertThat(addDriverResponse.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.DRIVER_UNDER_AGE_CA.getMessage());

            // addDriver via dxp - with birth year prior to 1900
            AddDriverRequest addDriverRequest2 = DXPRequestFactory.createAddDriverRequest("John", "Driver", "Jones", "1899-12-20", null);
            ErrorResponseDto addDriverResponse2 = HelperCommon.addDriver(policyNumber, addDriverRequest2, ErrorResponseDto.class, 422);

            softly.assertThat(addDriverResponse2.errors.get(0).errorCode).isEqualTo(ErrorDxpEnum.Errors.TOO_OLD_DRIVER_ERROR_CA.getCode());
            softly.assertThat(addDriverResponse2.errors.get(0).message).isEqualTo(ErrorDxpEnum.Errors.TOO_OLD_DRIVER_ERROR_CA.getMessage());

            // addDriver via dxp - with birth year prior to 1900
            AddDriverRequest addDriverRequest3 = DXPRequestFactory
                    .createAddDriverRequest("Jane", "Driver", "Smith", "1970-12-20", null);
            DriversDto addDriverResponse3 = HelperCommon.addDriver(policyNumber, addDriverRequest3, DriversDto.class);
            String newDriverOid = addDriverResponse3.oid;

			// updateDriver via dxp
            UpdateDriverRequest updateDriverRequest = DXPRequestFactory
                    .createUpdateDriverRequest("female", "C32329585", 16, "CA", "OT", null);
            DriverWithRuleSets updateDriverResponse = HelperCommon.updateDriver(policyNumber, newDriverOid, updateDriverRequest);

            softly.assertThat(updateDriverResponse.validations.stream()
                    .anyMatch(error -> error.message.equals(ErrorDxpEnum.Errors.RELATIONSHIP_TO_FNI_ERROR_CA.getMessage())
                            && (ErrorDxpEnum.Errors.RELATIONSHIP_TO_FNI_ERROR_CA.getCode()).equals(error.errorCode))).isTrue();

			UpdateDriverRequest updateDriverRequest2 = DXPRequestFactory
                    .createUpdateDriverRequest("female", "C32329585", 16, "CA", "SP", null);
			DriverWithRuleSets updateDriverResponse2 = HelperCommon.updateDriver(policyNumber, newDriverOid, updateDriverRequest2);
            softly.assertThat(updateDriverResponse2.driver.namedInsuredType).isEqualTo("NI");
			softly.assertThat(updateDriverResponse2.driver.maritalStatusCd).isEqualTo("M");

            SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
            PolicySummaryPage.buttonPendedEndorsement.click();
            policy.dataGather().start();
            NavigationPage.toViewSubTab(NavigationEnum.AutoCaTab.GENERAL.get());

            aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab generalTab = new aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab();

            MultiAssetList namedInsuredInfo = generalTab.getNamedInsuredInfoAssetList();
            String address1 = namedInsuredInfo.getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.ADDRESS_LINE_1).getValue();

            generalTab.viewInsured(2);
            namedInsuredInfo = generalTab.getNamedInsuredInfoAssetList();
            softly.assertThat(namedInsuredInfo.getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.ADDRESS_LINE_1).getValue()).isEqualTo(address1);
            softly.assertThat(namedInsuredInfo.getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS).getValue()).isEqualTo("No");
            softly.assertThat(namedInsuredInfo.getAsset(AutoCaMetaData.GeneralTab.NamedInsuredInformation.IS_RESIDENTAL_DIFFERENF_FROM_MAILING).getValue()).isEqualTo("No");

        });
    }
}
