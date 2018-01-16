package aaa.helpers.openl.testdata_builder;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import aaa.common.enums.Constants;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.OpenLCoverage;
import aaa.helpers.openl.model.OpenLDriver;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import aaa.toolkit.webdriver.customcontrols.UnverifiableDrivingRecordSurcharge;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoSSTestDataGenerator extends AutoTestDataGenerator<AutoSSOpenLPolicy> {

	public AutoSSTestDataGenerator(String state) {
		super(state);
	}

	public AutoSSTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoSSOpenLPolicy openLPolicy) {
		TestData td = DataProviderFactory.dataOf(
				new PrefillTab().getMetaKey(), getPrefillTabData(),
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				new RatingDetailReportsTab().getMetaKey(), getRatingDetailReportsTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new FormsTab().getMetaKey(), getFormsTabTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));
		TestData resultData = TestDataHelper.merge(getRatingDataPattern(), td);

		if (!openLPolicy.isAAAMember()) {
			resultData.mask(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel());
			resultData.mask(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.LAST_NAME.getLabel());
		}
		return resultData;
	}

	private TestData getPrefillTabData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getGeneralTabData(AutoSSOpenLPolicy openLPolicy) {
		TestData namedInsuredInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getLabel(), getGeneralTabResidence(openLPolicy.isHomeOwner())
				/* to be continued */);
		namedInsuredInformationData.adjust(getGeneralTabBaseData(openLPolicy.getAaaInsurancePersistency(), openLPolicy.getAaaAsdInsurancePersistency(), openLPolicy.getEffectiveDate()));

		TestData aAAProductOwnedData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), getYesOrNo(openLPolicy.isAAAMember()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.HOME.getLabel(), getYesOrNo(openLPolicy.getAaaHomePolicy()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.RENTERS.getLabel(), getYesOrNo(openLPolicy.getAaaRentersPolicy()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.CONDO.getLabel(), getYesOrNo(openLPolicy.getAaaCondoPolicy()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.LIFE.getLabel(), getYesOrNo(openLPolicy.isAaaLifePolicy())
				//TODO: exclude for RO state: AutoSSMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE.getLabel(), openLPolicy.isAaaMotorcyclePolicy()
		);

		TestData contactInformationData = DataProviderFactory.emptyData();

		TestData currentCarrierInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER.getLabel(), "Yes",
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel(), getGeneralTabPriorBILimit(openLPolicy.getPriorBILimit()));

		currentCarrierInformationData
				.adjust(getGeneralTabAgentInceptionAndExpirationData(openLPolicy.getAutoInsurancePersistency(), openLPolicy.getAaaInsurancePersistency(), openLPolicy.getEffectiveDate()));

		TestData policyInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), getGeneralTabTerm(openLPolicy.getTerm())
				//TODO: exclude for RO state: AutoSSMetaData.GeneralTab.PolicyInformation.ADVANCED_SHOPPING_DISCOUNTS.getLabel(), generalTabIsAdvanceShopping(openLPolicy.isAdvanceShopping())
				/* to be continued */);

		return DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), Arrays.asList(namedInsuredInformationData),
				AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), aAAProductOwnedData,
				AutoSSMetaData.GeneralTab.CONTACT_INFORMATION.getLabel(), contactInformationData,
				AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(), currentCarrierInformationData,
				AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), policyInformationData);
				/*AutoSSMetaData.PrefillTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.emptyData(),
				AutoSSMetaData.PrefillTab.ORDER_PREFILL.getLabel(), "click");*/
	}

	private List<TestData> getDriverTabData(AutoSSOpenLPolicy openLPolicy) {
		List<TestData> driversTestData = new ArrayList<>(openLPolicy.getDrivers().size());
		boolean isFirstDriver = true;
		boolean isAffinityGroupSet = false;
		for (OpenLDriver driver : openLPolicy.getDrivers()) {
			if (driver.getDsr() != 0) {
				//TODO-dchubkov: to be implemented but at the moment don't have openL files with this value greater than 0
				throw new NotImplementedException("Test data generation for \"dsr\" greater than 0 is not implemented.");
			}

			if (!Objects.equals(driver.getDriverAge(), driver.getAgeBeforeEndorsement())) {
				//TODO-dchubkov: to be implemented but at the moment don't have openL files with ageBeforeEndorsement different from driverAge
				throw new NotImplementedException("Test data generation for \"ageBeforeEndorsement\" is not implemented.");
			}

			if (!driver.isExposure()) {
				throw new IstfException("\"exposure\" openL field value should be always TRUE");
			}

			TestData driverData = DataProviderFactory.dataOf(
					AutoSSMetaData.DriverTab.GENDER.getLabel(), getDriverTabGender(driver.getGender()),
					AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), getDriverTabMartialStatus(driver.getMaritalStatus()),
					AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel(), getDriverTabDateOfBirth(driver.getDriverAge(), openLPolicy.getEffectiveDate()),
					AutoSSMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel(), driver.getDriverAge() - driver.getTyde(),
					AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), getDriverTabLicenseType(driver.isForeignLicense()),
					AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "None",
					AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=First Named Insured|",
					AutoSSMetaData.DriverTab.OCCUPATION.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|",
					AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED.getLabel(), getYesOrNo(driver.hasSR22())
			);

			if (driver.isSmartDriver()) {
				driverData.adjust(AutoSSMetaData.DriverTab.SMART_DRIVER_COURSE_COMPLETED.getLabel(), getYesOrNo(driver.isSmartDriver()));
			}
			if (driver.isDistantStudent()) {
				driverData.adjust(AutoSSMetaData.DriverTab.DISTANT_STUDENT.getLabel(), getYesOrNo(driver.isDistantStudent()));
			}
			if ("Y".equalsIgnoreCase(driver.getDefensiveDrivingCourse())) {
				driverData.adjust(AutoSSMetaData.DriverTab.DEFENSIVE_DRIVER_COURSE_COMPLETED.getLabel(), getYesOrNo(driver.getDefensiveDrivingCourse()));
			}

			if (openLPolicy.isEmployee() && !isAffinityGroupSet) {
				driverData.adjust(AutoSSMetaData.DriverTab.NAMED_INSURED.getLabel(), "contains=" + driver.getName())
						.adjust(AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "AAA Employee");
				isAffinityGroupSet = true;
			}

			if (getState().equals(Constants.States.MD) && driver.isCleanDriver()) {
				driverData.adjust(AutoSSMetaData.DriverTab.CLEAN_DRIVER_RENEWAL.getLabel(), getYesOrNo(driver.isCleanDriver()));
			}

			if (getState().equals(Constants.States.VA) && driver.hasFR44()) {
				driverData.adjust(AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED.getLabel(), getYesOrNo(driver.hasFR44()))
						.adjust(AutoSSMetaData.DriverTab.FORM_TYPE.getLabel(), "FR44");
			}

			if (driver.isOutOfStateLicenseSurcharge()) {
				driverData.adjust(AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), "Licensed (US)")
						.adjust(AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=" + getState());
			}

			if (!isFirstDriver) {
				driverData.adjust(AutoSSMetaData.DriverTab.DRIVER_SEARCH_DIALOG.getLabel(), DataProviderFactory.emptyData())
						.adjust(AutoSSMetaData.DriverTab.FIRST_NAME.getLabel(), driver.getName())
						.adjust(AutoSSMetaData.DriverTab.LAST_NAME.getLabel(), driver.getName());
			}
			isFirstDriver = false;
			driversTestData.add(driverData);
		}
		return driversTestData;
	}

	private TestData getRatingDetailReportsTabData(AutoSSOpenLPolicy openLPolicy) {
		TestData editInsuranceScoreDialogData = DataProviderFactory.dataOf(
				AutoSSMetaData.RatingDetailReportsTab.EditInsuranceScoreDialog.NEW_SCORE.getLabel(), openLPolicy.getCreditScore(),
				AutoSSMetaData.RatingDetailReportsTab.EditInsuranceScoreDialog.REASON_FOR_OVERRIDE.getLabel(), "Fair Credit Reporting Act Dispute",
				AutoSSMetaData.RatingDetailReportsTab.EditInsuranceScoreDialog.BTN_SAVE.getLabel(), "click");

		TestData insuranceScoreOverrideData = DataProviderFactory.dataOf(
				AutoSSMetaData.RatingDetailReportsTab.InsuranceScoreOverrideRow.ACTION.getLabel(), "Override Score",
				AutoSSMetaData.RatingDetailReportsTab.InsuranceScoreOverrideRow.EDIT_INSURANCE_SCORE.getLabel(), editInsuranceScoreDialogData);

		return DataProviderFactory.dataOf(AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel(), insuranceScoreOverrideData);
	}

	private List<TestData> getVehicleTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		List<TestData> vehiclesTestData = new ArrayList<>(openLPolicy.getVehicles().size());
		for (OpenLVehicle vehicle : openLPolicy.getVehicles()) {
			if (vehicle.isHybrid()) {
				//TODO-dchubkov: to be implemented but at the moment don't have openL files with this option enabled and impossible to set via UI
				throw new NotImplementedException("Test data generation for enabled isHybrid is not implemented.");
			}
			assertThat(vehicle.getAddress()).as("Vehicle's address list should have only one address").hasSize(1);

			int streetNumber = RandomUtils.nextInt(100, 1000);
			String streetName = RandomStringUtils.randomAlphabetic(10).toUpperCase() + " St";
			TestData vehicleData = DataProviderFactory.dataOf(
					AutoSSMetaData.VehicleTab.YEAR.getLabel(), vehicle.getModelYear(),
					AutoSSMetaData.VehicleTab.USAGE.getLabel(), getVehicleTabUsage(vehicle.getUsage()),
					AutoSSMetaData.VehicleTab.ANTI_THEFT.getLabel(), getVehicleTabAntiTheft(vehicle.getAntiTheftString()),
					AutoSSMetaData.VehicleTab.AIR_BAGS.getLabel(), getVehicleTabAirBags(vehicle.getAirbagCode()),
					AutoSSMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel(), "Yes",
					AutoSSMetaData.VehicleTab.ZIP_CODE.getLabel(), vehicle.getAddress().get(0).getZip(),
					AutoSSMetaData.VehicleTab.ADDRESS_LINE_1.getLabel(), streetNumber + " " + streetName,
					AutoSSMetaData.VehicleTab.STATE.getLabel(), vehicle.getAddress().get(0).getState(),
					AutoSSMetaData.VehicleTab.VALIDATE_ADDRESS_BTN.getLabel(), "click",
					AutoSSMetaData.VehicleTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.dataOf("Street number", streetNumber, "Street Name", streetName),
					AutoSSMetaData.VehicleTab.MAKE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|",
					AutoSSMetaData.VehicleTab.MODEL.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|OTHER",
					AutoSSMetaData.VehicleTab.SERIES.getLabel(), "OTHER",
					AutoSSMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|",
					AutoSSMetaData.VehicleTab.STAT_CODE.getLabel(), "contains=" + vehicle.getStatCode(),
					AutoSSMetaData.VehicleTab.STATED_AMOUNT.getLabel(), "$<rx:\\d{3}>00");

			if (vehicle.getSafetyScore() != null) {
				assertThat(vehicle.isTelematic()).as("\"isTelematic\" should be false if \"safetyScore\" is not null").isFalse();
				vehicleData.adjust(getVehicleTabVehicleDetailsData("WMWRC33536TK73512", String.valueOf(vehicle.getSafetyScore())));
			}

			if (vehicle.isTelematic()) {
				vehicleData.adjust(getVehicleTabVehicleDetailsData("2HNYD28498H554858", "No Score"));
			}

			vehiclesTestData.add(vehicleData);
		}
		return vehiclesTestData;
	}

	private TestData getFormsTabTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		return DataProviderFactory.emptyData();
	}

	private TestData getPremiumAndCoveragesTabData(AutoSSOpenLPolicy openLPolicy) {
		Map<String, Object> unverifiableDrivingRecordSurchargeData = new HashMap<>();
		if (openLPolicy.isEMember()) {
			//TODO-dchubkov: to be implemented but at the moment don't have openL files with this option enabled
			throw new NotImplementedException("Test data generation for enabled isEMember is not implemented.");
		}

		boolean isFirstDriver = true;
		for (OpenLDriver driver : openLPolicy.getDrivers()) {
			if (isFirstDriver) {
				unverifiableDrivingRecordSurchargeData.put(UnverifiableDrivingRecordSurcharge.DRIVER_SELECTION_BY_CONTAINS_KEY + "Smith", driver.isUnverifiableDrivingRecord());
				isFirstDriver = false;
			} else {
				unverifiableDrivingRecordSurchargeData.put(driver.getName() + " " + driver.getName(), driver.isUnverifiableDrivingRecord());
			}
		}

		List<TestData> detailedVehicleCoveragesList = new ArrayList<>(openLPolicy.getVehicles().size());
		for (OpenLVehicle vehicle : openLPolicy.getVehicles()) {
			for (OpenLCoverage coverage : vehicle.getCoverages()) {
				Map<String, Object> coverageData = new HashMap<>();
				coverageData.put(getPremiumAndCoveragesTabCoverageKey(coverage.getCoverageCD()), getPremiumAndCoveragesTabLimitOrDeductible(coverage));
				coverageData.put(AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.FULL_SAFETY_GLASS.getLabel(), getPremiumAndCoveragesFullSafetyGlass(coverage.getGlassDeductible()));
				if (vehicle.isNewCarAddedProtection()) {
					coverageData.put(AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.NEW_CAR_ADDED_PROTECTION.getLabel(), "Yes");
				}
				detailedVehicleCoveragesList.add(new SimpleDataProvider(coverageData));
			}
		}

		return DataProviderFactory.dataOf(
				AutoSSMetaData.PremiumAndCoveragesTab.UNACCEPTABLE_RISK_SURCHARGE.getLabel(), openLPolicy.isUnacceptableRisk(),
				AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS.getLabel(), "Yes", //TODO-dchubkov: enable only if need to fill expanded section
				AutoSSMetaData.PremiumAndCoveragesTab.MULTI_CAR.getLabel(), openLPolicy.isMultiCar(),
				AutoSSMetaData.PremiumAndCoveragesTab.UNVERIFIABLE_DRIVING_RECORD_SURCHARGE.getLabel(), new SimpleDataProvider(unverifiableDrivingRecordSurchargeData),
				AutoSSMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(), detailedVehicleCoveragesList);
	}
}
