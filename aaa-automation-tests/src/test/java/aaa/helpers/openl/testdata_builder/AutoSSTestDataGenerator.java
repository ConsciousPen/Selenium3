package aaa.helpers.openl.testdata_builder;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.apache.commons.lang3.NotImplementedException;
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

	public AutoSSTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoSSOpenLPolicy openLPolicy) {
		if (openLPolicy.getReinstatements() > 0) {
			//TODO-dchubkov: to be implemented...
			throw new NotImplementedException("Test data generation for \"reinstatements\" greater than 0 is not implemented.");
		}

		assertThat(openLPolicy.getCappingDetails()).as("Policies cappingDetails list should have only one element").hasSize(1);
		assertThat(getState()).isEqualTo(openLPolicy.getCappingDetails().get(0).getState()).as("State from TestDataGenerator differs from openl file's state");

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
		assertThat(openLPolicy.getAaaInsurancePersistency()).isEqualTo(openLPolicy.getAaaAsdInsurancePersistency())
				.as("\"aaaInsurancePersistency\" openL field should be equal to \"aaaAsdInsurancePersistency\" since both are equally calculated");

		TestData namedInsuredInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getLabel(), getGeneralTabResidence(openLPolicy.isHomeOwner()),
				AutoSSMetaData.GeneralTab.NamedInsuredInformation.BASE_DATE.getLabel(), openLPolicy.getEffectiveDate().minusYears(openLPolicy.getAaaInsurancePersistency())
						.format(DateTimeUtils.MM_DD_YYYY));

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

		currentCarrierInformationData.adjust(
				getGeneralTabAgentInceptionAndExpirationData(openLPolicy.getAutoInsurancePersistency(), openLPolicy.getAaaInsurancePersistency(), openLPolicy.getEffectiveDate()));

		TestData policyInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), getGeneralTabTerm(openLPolicy.getCappingDetails().get(0).getTerm()),
				AutoSSMetaData.GeneralTab.PolicyInformation.CHANNEL_TYPE.getLabel(), "AAA Agent" // hardcoded value
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
		boolean isEmployeeSet = false;
		boolean isAARPSet = false;
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

			if (!isFirstDriver) {
				driverData.adjust(AutoSSMetaData.DriverTab.DRIVER_SEARCH_DIALOG.getLabel(), DataProviderFactory.emptyData())
						.adjust(AutoSSMetaData.DriverTab.FIRST_NAME.getLabel(), driver.getName())
						.adjust(AutoSSMetaData.DriverTab.LAST_NAME.getLabel(), driver.getName());
			}

			if (driver.isSmartDriver()) {
				driverData.adjust(AutoSSMetaData.DriverTab.SMART_DRIVER_COURSE_COMPLETED.getLabel(), getYesOrNo(driver.isSmartDriver()));
			}
			if (driver.isDistantStudent()) {
				driverData.adjust(AutoSSMetaData.DriverTab.DISTANT_STUDENT.getLabel(), getYesOrNo(driver.isDistantStudent()));
			}
			if ("Y".equalsIgnoreCase(driver.getDefensiveDrivingCourse())) {
				driverData.adjust(AutoSSMetaData.DriverTab.DEFENSIVE_DRIVER_COURSE_COMPLETED.getLabel(), getYesOrNo(driver.getDefensiveDrivingCourse()));
			}

			if (openLPolicy.isEmployee() && !isEmployeeSet) {
				driverData.adjust(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), "Spouse")
						.adjust(AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "AAA Employee");
				isEmployeeSet = true;
			}

			if (openLPolicy.isAARP() && !isAARPSet) {
				driverData.adjust(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), "Spouse")
						.adjust(AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "AARP");
				isAARPSet = true;
				if (openLPolicy.isEmployee()) {
					assertThat(openLPolicy.getDrivers().size()).isGreaterThan(1)
							.as("Policy with openl fields \"isEmployee\" and \"isAARP\" which are both TRUE should have at least 2 drivers to fill \"%s\" UI field differently for each of them",
									AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel());
					isEmployeeSet = false; // will set "Affinity Group"="AAA Employee" to the next driver
				}
			}

			if (Constants.States.MD.equals(getState()) && driver.isCleanDriver()) {
				driverData.adjust(AutoSSMetaData.DriverTab.CLEAN_DRIVER_RENEWAL.getLabel(), getYesOrNo(driver.isCleanDriver()));
			}

			if (Constants.States.VA.equals(getState()) && driver.hasFR44()) {
				driverData.adjust(AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED.getLabel(), getYesOrNo(driver.hasFR44()))
						.adjust(AutoSSMetaData.DriverTab.FORM_TYPE.getLabel(), "FR44");
			}

			if (driver.isOutOfStateLicenseSurcharge()) {
				driverData.adjust(AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), "Licensed (US)")
						.adjust(AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=" + getState());
			}

			List<TestData> activityInformationList = new ArrayList<>();
			if (openLPolicy.getAggregateCompClaims() > 0) {
				int activityNumber = 1;
				while (activityNumber <= openLPolicy.getAggregateCompClaims()) {
					TestData activityInformationData = DataProviderFactory.dataOf(AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), "Comprehensive Claim",
							AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), "Comprehensive Claim",
							AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), openLPolicy.getEffectiveDate().plusMonths(new Random().nextInt(36))
									.format(DateTimeUtils.MM_DD_YYYY),
							AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), RandomUtils.nextInt(1001, 10000));

					activityInformationList.add(activityInformationData);
					activityNumber++;
				}

			}

			if (openLPolicy.getNafAccidents() > 0) {
				int activityNumber = 1;
				while (activityNumber <= openLPolicy.getNafAccidents()) {
					TestData activityInformationData = DataProviderFactory.dataOf(AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(),
							AdvancedComboBox.RANDOM_EXCEPT_MARK + "=At-Fault Accident|Principally At-Fault Accident",
							AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|",
							AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), openLPolicy.getEffectiveDate().plusMonths(new Random().nextInt(36))
									.format(DateTimeUtils.MM_DD_YYYY),
							AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), RandomUtils.nextInt(1001, 10000));

					activityInformationList.add(activityInformationData);
					activityNumber++;
				}

			}

			if (!activityInformationList.isEmpty()) {
				driverData.adjust(AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(), activityInformationList);
			}

			driversTestData.add(driverData);
			isFirstDriver = false;
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
		List<TestData> vehiclesTestDataList = new ArrayList<>(openLPolicy.getVehicles().size());

		assertThat(openLPolicy.getNoOfVehiclesExcludingTrailer()).isLessThanOrEqualTo(openLPolicy.getVehicles().size())
				.as("\"noOfVehiclesExcludingTrailer\" openl field value should be less or equal to total number of vehicles");

		int numberOfTrailers = 0;
		if (openLPolicy.getNoOfVehiclesExcludingTrailer() < openLPolicy.getVehicles().size()) {
			numberOfTrailers = openLPolicy.getVehicles().size() - openLPolicy.getNoOfVehiclesExcludingTrailer();
		}

		for (OpenLVehicle vehicle : openLPolicy.getVehicles()) {
			if (vehicle.isHybrid()) {
				//TODO-dchubkov: to be implemented and impossible to set via UI
				throw new NotImplementedException("Test data generation for enabled isHybrid is not implemented since there is no UI field for this attribute.");
			}

			TestData vehicleData = DataProviderFactory.emptyData();
			if (numberOfTrailers > 0 && isTrailerOrMotorHomeUsage(vehicle.getUsage())) {
				vehicleData.adjust(getVehicleTabInformationData(vehicle, true));
				numberOfTrailers--;
			} else {
				vehicleData.adjust(getVehicleTabInformationData(vehicle, false));
			}

			if (vehicle.getSafetyScore() != null) {
				assertThat(vehicle.isTelematic()).as("\"isTelematic\" should be false if \"safetyScore\" is not null").isFalse();
				vehicleData.adjust(getVehicleTabVehicleDetailsData("WMWRC33536TK73512", String.valueOf(vehicle.getSafetyScore())));
			}

			if (vehicle.isTelematic()) {
				vehicleData.adjust(getVehicleTabVehicleDetailsData("2HNYD28498H554858", "No Score"));
			}

			vehiclesTestDataList.add(vehicleData);
		}
		return vehiclesTestDataList;
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

		if (openLPolicy.isSupplementalSpousalLiability()) {
			//TODO-dchubkov: to be implemented but at the moment don't have openL files with this option enabled
			throw new NotImplementedException("Test data generation for enabled supplementalSpousalLiability is not implemented.");
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
