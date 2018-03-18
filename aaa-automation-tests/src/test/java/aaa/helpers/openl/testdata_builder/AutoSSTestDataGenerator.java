package aaa.helpers.openl.testdata_builder;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.helpers.TestDataHelper;
import aaa.helpers.mock.MockDataHelper;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLCoverage;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLDriver;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLVehicle;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.AssignmentTab;
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
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoSSTestDataGenerator extends AutoTestDataGenerator<AutoSSOpenLPolicy> {
	private static final String VEHICLE_ASSIGNED_ID_TESTDATA_KEY = "vehicleAssignedId";

	public AutoSSTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoSSOpenLPolicy openLPolicy) {
		if (openLPolicy.getReinstatements() != null && openLPolicy.getReinstatements() > 0) {
			//TODO-dchubkov: to be implemented...
			throw new NotImplementedException("Test data generation for \"reinstatements\" greater than 0 is not implemented.");
		}

		assertThat(openLPolicy.getCappingDetails()).as("Policies cappingDetails list should have only one element").hasSize(1);
		assertThat(getState()).as("State from TestDataGenerator differs from openl file's state").isEqualTo(openLPolicy.getCappingDetails().get(0).getState());

		if (openLPolicy.getCappingDetails().get(0).getTermCappingFactor() != null && openLPolicy.getCappingDetails().get(0).getTermCappingFactor() != 1) {
			//TODO-dchubkov: to be implemented...
			throw new NotImplementedException("Test data generation for \"termCappingFactor\" not equal to 1 is not implemented.");
		}

		String membershipNumber = null;
		if (Boolean.TRUE.equals(openLPolicy.isAAAMember())) {
			membershipNumber = MockDataHelper.getMembershipData()
					.getMembershipNumberForAvgAnnualERSperMember(openLPolicy.getEffectiveDate(), openLPolicy.getMemberPersistency(), openLPolicy.getAvgAnnualERSperMember());
		} else {
			getRatingDataPattern().mask(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel())
					.mask(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.LAST_NAME.getLabel());
		}

		List<TestData> driversTestDataList = getDriverTabData(openLPolicy);
		TestData td = DataProviderFactory.dataOf(
				new PrefillTab().getMetaKey(), getPrefillTabData(),
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy, membershipNumber),
				new DriverTab().getMetaKey(), driversTestDataList,
				new RatingDetailReportsTab().getMetaKey(), getRatingDetailReportsTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new AssignmentTab().getMetaKey(), getAssignmentTabData(driversTestDataList),
				new FormsTab().getMetaKey(), getFormsTabTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));
		return TestDataHelper.merge(getRatingDataPattern(), td);
	}

	private TestData getPrefillTabData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getGeneralTabData(AutoSSOpenLPolicy openLPolicy, String membershipNumber) {
		assertThat(openLPolicy.getAaaInsurancePersistency()).as("\"aaaInsurancePersistency\" openL field should be equal to \"aaaAsdInsurancePersistency\" "
				+ "since both are equally calculated").isEqualTo(openLPolicy.getAaaAsdInsurancePersistency());

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

		if (membershipNumber != null) {
			aAAProductOwnedData.adjust(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel(), membershipNumber);
		}

		TestData currentCarrierInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER.getLabel(), "Yes",
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel(), getGeneralTabPriorBILimit(openLPolicy.getPriorBILimit()));

		currentCarrierInformationData.adjust(
				getGeneralTabAgentInceptionAndExpirationData(openLPolicy.getAutoInsurancePersistency(), openLPolicy.getAaaInsurancePersistency(), openLPolicy.getEffectiveDate()));

		//TODO-dchubkov: all CT and ID states tests have "CSAA Affinity Insurance Company (formerly Keystone Insurance Company)" value for "Agent Entered Current/Prior Carrier" but is's missed. To be investigated...
		if (StringUtils.isNotBlank(openLPolicy.getCappingDetails().get(0).getCarrierCode()) && !getState().equals(Constants.States.CT) && !getState().equals(Constants.States.ID)) {
			currentCarrierInformationData.adjust(
					AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER.getLabel(), openLPolicy.getCappingDetails().get(0).getCarrierCode());
		}

		TestData policyInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), getGeneralTabTerm(openLPolicy.getCappingDetails().get(0).getTerm()),
				AutoSSMetaData.GeneralTab.PolicyInformation.CHANNEL_TYPE.getLabel(), "AAA Agent" // hardcoded value
				//TODO: exclude for RO state: AutoSSMetaData.GeneralTab.PolicyInformation.ADVANCED_SHOPPING_DISCOUNTS.getLabel(), generalTabIsAdvanceShopping(openLPolicy.isAdvanceShopping())
		);

		return DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), Arrays.asList(namedInsuredInformationData),
				AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), aAAProductOwnedData,
				AutoSSMetaData.GeneralTab.CONTACT_INFORMATION.getLabel(), DataProviderFactory.emptyData(),
				AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(), currentCarrierInformationData,
				AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), policyInformationData);
	}

	private List<TestData> getDriverTabData(AutoSSOpenLPolicy openLPolicy) {
		List<TestData> driversTestData = new ArrayList<>(openLPolicy.getDrivers().size());
		boolean isFirstDriver = true;
		boolean isEmployeeSet = false;
		boolean isAARPSet = false;
		boolean isAtFaultAccidentFreeSet = false;
		boolean isAccidentFreeSet = false;
		Integer aggregateCompClaims = openLPolicy.getAggregateCompClaims();
		Integer nafAccidents = openLPolicy.getNafAccidents();

		for (AutoSSOpenLDriver driver : openLPolicy.getDrivers()) {
			if (driver.getDsr() != null && driver.getDsr() != 0) {
				//TODO-dchubkov: to be implemented but at the moment don't have openL files with this value greater than 0
				throw new NotImplementedException("Test data generation for \"dsr\" greater than 0 is not implemented.");
			}

			if (!Objects.equals(driver.getDriverAge(), driver.getAgeBeforeEndorsement())) {
				//TODO-dchubkov: to be implemented but at the moment don't have openL files with ageBeforeEndorsement different from driverAge
				throw new NotImplementedException("Test data generation for \"ageBeforeEndorsement\" is not implemented.");
			}

			if (Boolean.FALSE.equals(driver.isExposure())) {
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
					AutoSSMetaData.DriverTab.OCCUPATION.getLabel(), "regex=.*\\S.*",
					AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED.getLabel(), getYesOrNo(driver.hasSR22())
			);

			if (driver.getVehicleAssignedId() != null) {
				driverData.adjust(VEHICLE_ASSIGNED_ID_TESTDATA_KEY, driver.getVehicleAssignedId()); // for searching valid vehicle for driver assignment, should be masked in result test data
			}

			if (!isFirstDriver) {
				driverData.adjust(AutoSSMetaData.DriverTab.DRIVER_SEARCH_DIALOG.getLabel(), DataProviderFactory.emptyData())
						.adjust(AutoSSMetaData.DriverTab.FIRST_NAME.getLabel(), driver.getName())
						.adjust(AutoSSMetaData.DriverTab.LAST_NAME.getLabel(), driver.getName());
			}

			if (Boolean.TRUE.equals(driver.isSmartDriver())) {
				driverData.adjust(AutoSSMetaData.DriverTab.SMART_DRIVER_COURSE_COMPLETED.getLabel(), getYesOrNo(driver.isSmartDriver()));
			}
			if (Boolean.TRUE.equals(driver.isDistantStudent())) {
				driverData.adjust(AutoSSMetaData.DriverTab.DISTANT_STUDENT.getLabel(), getYesOrNo(driver.isDistantStudent()));
			}
			if ("Y".equalsIgnoreCase(driver.getDefensiveDrivingCourse())) {
				driverData.adjust(AutoSSMetaData.DriverTab.DEFENSIVE_DRIVER_COURSE_COMPLETED.getLabel(), getYesOrNo(driver.getDefensiveDrivingCourse()));
			}

			if (Boolean.TRUE.equals(openLPolicy.isEmployee()) && !isEmployeeSet) {
				driverData.adjust(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), "Spouse")
						.adjust(AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "AAA Employee");
				isEmployeeSet = true;
			}

			if (Boolean.TRUE.equals(openLPolicy.isAARP()) && !isAARPSet) {
				driverData.adjust(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), "Spouse")
						.adjust(AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "AARP");
				isAARPSet = true;
				if (Boolean.TRUE.equals(openLPolicy.isEmployee())) {
					assertThat(openLPolicy.getDrivers().size()).as("Policy with openl fields \"isEmployee\" and \"isAARP\" which are both TRUE should have at least 2 drivers"
							+ " to fill \"%s\" UI field differently for each of them", AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel())
							.isGreaterThan(1);
					isEmployeeSet = false; // will set "Affinity Group"="AAA Employee" to the next driver
				}
			}

			if (Constants.States.MD.equals(getState()) && Boolean.TRUE.equals(driver.isCleanDriver())) {
				driverData.adjust(AutoSSMetaData.DriverTab.CLEAN_DRIVER_RENEWAL.getLabel(), getYesOrNo(driver.isCleanDriver()));
			}

			if (Constants.States.VA.equals(getState()) && Boolean.TRUE.equals(driver.hasFR44())) {
				driverData.adjust(AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED.getLabel(), getYesOrNo(driver.hasFR44()))
						.adjust(AutoSSMetaData.DriverTab.FORM_TYPE.getLabel(), "FR44");
			}

			if (Boolean.TRUE.equals(driver.isOutOfStateLicenseSurcharge())) {
				driverData.adjust(AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), "Licensed (US)")
						.adjust(AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=" + getState());
			}

			List<TestData> activityInformationList = new ArrayList<>();
			while (aggregateCompClaims != null && aggregateCompClaims > 0) {
				TestData activityInformationData = DataProviderFactory.dataOf(AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), "Comprehensive Claim",
						AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), "Comprehensive Claim",
						AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), openLPolicy.getEffectiveDate().minusMonths(new Random().nextInt(36))
								.format(DateTimeUtils.MM_DD_YYYY),
						AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), RandomUtils.nextInt(1001, 10000));

				activityInformationList.add(activityInformationData);
				aggregateCompClaims--;
			}

			while (nafAccidents != null && nafAccidents > 0) {
				TestData activityInformationData = DataProviderFactory.dataOf(
						AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), getRandom("At-Fault Accident", "Principally At-Fault Accident"),
						AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), "regex=.*\\S.*",
						AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), openLPolicy.getEffectiveDate().minusMonths(new Random().nextInt(36))
								.format(DateTimeUtils.MM_DD_YYYY),
						AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), RandomUtils.nextInt(100, 900));

				activityInformationList.add(activityInformationData);
				nafAccidents--;
			}

			if (openLPolicy.getYearsAtFaultAccidentFree() != null && openLPolicy.getYearsAtFaultAccidentFree() > 0 && !isAtFaultAccidentFreeSet) {
				activityInformationList.add(getActivityInformationData(true, openLPolicy.getEffectiveDate(), openLPolicy.getYearsAtFaultAccidentFree()));
				isAtFaultAccidentFreeSet = true;
			}

			if (openLPolicy.getYearsIncidentFree() != null && openLPolicy.getYearsIncidentFree() > 0 && !isAccidentFreeSet) {
				activityInformationList.add(getActivityInformationData(false, openLPolicy.getEffectiveDate(), openLPolicy.getYearsAtFaultAccidentFree()));
				isAccidentFreeSet = true;
			}

			if (!activityInformationList.isEmpty()) {
				driverData.adjust(AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(), activityInformationList);
			}

			driversTestData.add(driverData);
			isFirstDriver = false;
		}
		return driversTestData;
	}

	private TestData getActivityInformationData(boolean atFaultAccident, LocalDateTime policyEffectiveDate, int yearsAccidentFree) {
		assertThat(yearsAccidentFree)
				.as("Invalid \"%s\" value in openl file, UI does not allow to set \"Occurrence Date\" more than 5 years", atFaultAccident ? "yearsAtFaultAccidentFree" : "yearsIncidentFree")
				.isLessThanOrEqualTo(5);
		LocalDateTime occurrenceDate = policyEffectiveDate.minusYears(yearsAccidentFree);

		Map<String, Object> activityInformationData = new HashMap<>();
		if (atFaultAccident) {
			activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), getRandom("At-Fault Accident", "Principally At-Fault Accident"));
			activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), RandomUtils.nextInt(1001, 10000));
		} else {
			activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(),
					getRandom("Major Violation", "Minor Violation", "Speeding Violation", "Alcohol-Related Violation"));
		}
		activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), "regex=.*\\S.*");
		activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), occurrenceDate.format(DateTimeUtils.MM_DD_YYYY));
		if (!getState().equals(Constants.States.NY)) {
			activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE.getLabel(), occurrenceDate.format(DateTimeUtils.MM_DD_YYYY));
		}
		return new SimpleDataProvider(activityInformationData);
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
		if (openLPolicy.getNoOfVehiclesExcludingTrailer() != null) {
			int trailersCount = Math.toIntExact(openLPolicy.getVehicles().stream().filter(v -> isTrailerType(v.getStatCode())).count());
			int expectedTrailersCount = openLPolicy.getVehicles().size() - openLPolicy.getNoOfVehiclesExcludingTrailer();
			assertThat(trailersCount).as("Number of vehicles recognized by their stat codes set [%s] is not equal to "
					+ "total vehicles number minus \"noOfVehiclesExcludingTrailer\" value [%s]", trailersCount, expectedTrailersCount).isEqualTo(expectedTrailersCount);
			assertThat(openLPolicy.getNoOfVehiclesExcludingTrailer()).as("\"noOfVehiclesExcludingTrailer\" openl field value should be less or equal to total number of vehicles")
					.isLessThanOrEqualTo(openLPolicy.getVehicles().size());
		}

		List<TestData> vehiclesTestDataList = new ArrayList<>(openLPolicy.getVehicles().size());
		for (AutoSSOpenLVehicle vehicle : openLPolicy.getVehicles()) {
			if (Boolean.TRUE.equals(vehicle.isHybrid())) {
				//TODO-dchubkov: to be implemented and impossible to set via UI
				throw new NotImplementedException("Test data generation for enabled isHybrid is not implemented since there is no UI field for this attribute.");
			}

			TestData vehicleData = getVehicleTabInformationData(vehicle);
			if (Boolean.TRUE.equals(vehicle.isTelematic())) {
				vehicleData.adjust(getVehicleTabVehicleDetailsData("No Score"));
			}

			if (vehicle.getSafetyScore() != null) {
				//TODO-dchubkov: for ID this is possible, to be investigated
				if (!getState().equals(Constants.States.ID)) {
					assertThat(vehicle.isTelematic()).as("\"isTelematic\" should be false if \"safetyScore\" is not null").isFalse();
				}
				vehicleData.adjust(getVehicleTabVehicleDetailsData(String.valueOf(vehicle.getSafetyScore())));
			}

			vehiclesTestDataList.add(vehicleData);
		}

		return vehiclesTestDataList;
	}

	private TestData getAssignmentTabData(List<TestData> driversTestDataList) {
		if (!getState().equals(Constants.States.NY)) {
			return DataProviderFactory.emptyData();
		}

		List<TestData> driverVehicleRelationshipTable = new ArrayList<>(driversTestDataList.size());
		for (TestData driverData : driversTestDataList) {
			String assignedDriver;
			if (driverData.getValue(AutoSSMetaData.DriverTab.FIRST_NAME.getLabel()) == null || driverData.getValue(AutoSSMetaData.DriverTab.LAST_NAME.getLabel()) == null) {
				assignedDriver = "contains=Smith";
			} else {
				assignedDriver = driverData.getValue(AutoSSMetaData.DriverTab.FIRST_NAME.getLabel()) + " " + driverData.getValue(AutoSSMetaData.DriverTab.LAST_NAME.getLabel());
			}
			String vehicleIndex = driverData.getValue(VEHICLE_ASSIGNED_ID_TESTDATA_KEY).replaceAll(".*vehicle#", "");

			TestData assignmentData = DataProviderFactory.dataOf(
					AutoSSMetaData.AssignmentTab.DriverVehicleRelationshipTableRow.DRIVER.getLabel(), assignedDriver,
					AutoSSMetaData.AssignmentTab.DriverVehicleRelationshipTableRow.SELECT_VEHICLE.getLabel(), "index=" + vehicleIndex);
			driverVehicleRelationshipTable.add(assignmentData);
			driverData.mask(VEHICLE_ASSIGNED_ID_TESTDATA_KEY);
		}
		return DataProviderFactory.dataOf(AutoSSMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP.getLabel(), driverVehicleRelationshipTable);
	}

	private TestData getFormsTabTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		return DataProviderFactory.emptyData();
	}

	private TestData getPremiumAndCoveragesTabData(AutoSSOpenLPolicy openLPolicy) {
		if (Boolean.TRUE.equals(openLPolicy.isEMember())) {
			//TODO-dchubkov: to be implemented but at the moment don't have openL files with this option enabled
			throw new NotImplementedException("Test data generation for enabled isEMember is not implemented.");
		}

		if (Boolean.TRUE.equals(openLPolicy.isSupplementalSpousalLiability())) {
			//TODO-dchubkov: to be implemented but at the moment don't have openL files with this option enabled
			throw new NotImplementedException("Test data generation for enabled supplementalSpousalLiability is not implemented.");
		}

		Map<String, Object> unverifiableDrivingRecordSurchargeData = new HashMap<>();
		//TODO-dchubkov: There are no drivers list in "Unacceptable Risk Surcharge" section for NY state, to be investigated
		if (!getState().equals(Constants.States.NY)) {
			boolean isFirstDriver = true;
			for (AutoSSOpenLDriver driver : openLPolicy.getDrivers()) {
				if (isFirstDriver) {
					unverifiableDrivingRecordSurchargeData.put(UnverifiableDrivingRecordSurcharge.DRIVER_SELECTION_BY_CONTAINS_KEY + "Smith", driver.isUnverifiableDrivingRecord());
					isFirstDriver = false;
				} else {
					unverifiableDrivingRecordSurchargeData.put(driver.getName() + " " + driver.getName(), driver.isUnverifiableDrivingRecord());
				}
			}
		}

		List<TestData> detailedVehicleCoveragesList = new ArrayList<>(openLPolicy.getVehicles().size());
		Map<String, Object> policyCoveragesData = new HashMap<>();
		Map<String, Object> detailedCoveragesData = new HashMap<>();
		for (AutoSSOpenLVehicle vehicle : openLPolicy.getVehicles()) {
			if (vehicle.getCoverages().stream().anyMatch(c -> isFirstPartyBenefitsComboCoverage(c.getCoverageCd()))) {
				policyCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.FIRST_PARTY_BENEFITS.getLabel(), "Added");
			}

			boolean isTrailerOrMotorHomeVehicle = isTrailerOrMotorHomeType(vehicle.getUsage());
			for (AutoSSOpenLCoverage coverage : vehicle.getCoverages()) {
				String coverageName = getPremiumAndCoveragesTabCoverageName(coverage.getCoverageCd());
				if (isPolicyLevelCoverage(coverage.getCoverageCd())) {
					policyCoveragesData.put(coverageName, getPremiumAndCoveragesTabLimitOrDeductible(coverage));
					if ("PIP".equals(coverage.getCoverageCd()) && getState().equals(Constants.States.OR)) {
						policyCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION_DEDUCTIBLE.getLabel(),
								"starts=" + getFormattedCoverageLimit(coverage.getDeductible(), coverage.getCoverageCd()));
					}
				} else {
					detailedCoveragesData.put(coverageName, getPremiumAndCoveragesTabLimitOrDeductible(coverage));
				}

				if (isTrailerOrMotorHomeVehicle) {
					assertThat(coverage.getGlassDeductible()).as("Invalid \"glassDeductible\" openl field value since it's not possible to fill \"Full Safety Glass\" UI field "
							+ "for \"Trailer\" or \"Motor Home\" vehicle types").isIn("N/A", "0");

					//TODO-dchubkov: tests for "Trailer" and "Motor Home" vehicle types sometimes have "SP EQUIP" coverage which is impossible to set via UI
					detailedCoveragesData.remove(AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.SPECIAL_EQUIPMENT_COVERAGE.getLabel());
				} else {
					detailedCoveragesData
							.put(AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.FULL_SAFETY_GLASS.getLabel(), getPremiumAndCoveragesFullSafetyGlass(coverage.getGlassDeductible()));
				}

				if (Boolean.TRUE.equals(vehicle.isNewCarAddedProtection())) {
					detailedCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.NEW_CAR_ADDED_PROTECTION.getLabel(), "Yes");
				}
			}
			detailedVehicleCoveragesList.add(new SimpleDataProvider(detailedCoveragesData));
			detailedCoveragesData.clear();
		}

		if (getState().equals(Constants.States.PA)) {
			policyCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.TORT_THRESHOLD.getLabel(), "starts=" + openLPolicy.getTort());
		}

		return DataProviderFactory.dataOf(
				AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel(), getPremiumAndCoveragesPaymentPlan(openLPolicy.getPaymentPlanType(), openLPolicy.getCappingDetails().get(0).getTerm()),
				AutoSSMetaData.PremiumAndCoveragesTab.UNACCEPTABLE_RISK_SURCHARGE.getLabel(), openLPolicy.isUnacceptableRisk(),
				AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS.getLabel(), "Yes", //TODO-dchubkov: enable only if need to fill expanded section
				AutoSSMetaData.PremiumAndCoveragesTab.MULTI_CAR.getLabel(), openLPolicy.isMultiCar(),
				AutoSSMetaData.PremiumAndCoveragesTab.UNVERIFIABLE_DRIVING_RECORD_SURCHARGE.getLabel(), new SimpleDataProvider(unverifiableDrivingRecordSurchargeData),
				AutoSSMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(), detailedVehicleCoveragesList)
				.adjust(new SimpleDataProvider(policyCoveragesData));
	}

	private TestData getVehicleTabInformationData(AutoSSOpenLVehicle vehicle) {
		assertThat(vehicle.getAddress()).as("Vehicle's address list should have only one address").hasSize(1);

		String vin = getVinFromDb(vehicle);
		Map<String, Object> vehicleInformation = new HashMap<>();
		vehicleInformation.put(AutoSSMetaData.VehicleTab.TYPE.getLabel(), getVehicleTabType(vehicle.getStatCode()));

		if (StringUtils.isNotBlank(vin)) {
			vehicleInformation.put(AutoSSMetaData.VehicleTab.VIN.getLabel(), covertToValidVin(vin));
		} else {
			vehicleInformation.put(AutoSSMetaData.VehicleTab.YEAR.getLabel(), vehicle.getModelYear());
			vehicleInformation.put(AutoSSMetaData.VehicleTab.STATED_AMOUNT.getLabel(), vehicle.getCollSymbol() * 1000);
			if (isTrailerOrMotorHomeType(vehicle.getUsage())) {
				vehicleInformation.put(AutoSSMetaData.VehicleTab.PRIMARY_OPERATOR.getLabel(), "regex=.*\\S.*");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel(), "some other make $<rx:\\d{3}>");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel(), "some other model $<rx:\\d{3}>");

				if (isTrailerType(vehicle.getStatCode())) {
					String trailerType = getVehicleTabTrailerType(vehicle.getStatCode());
					vehicleInformation.put(AutoSSMetaData.VehicleTab.TRAILER_TYPE.getLabel(), trailerType);
					if ("Travel Trailer".equals(trailerType)) {
						vehicleInformation.put(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel(), "contains=" + getVehicleTabStatCode(vehicle.getStatCode()));
					}
				} else {
					vehicleInformation.put(AutoSSMetaData.VehicleTab.MOTOR_HOME_TYPE.getLabel(), getVehicleTabMotorHomeType(vehicle.getStatCode()));
				}
			} else {
				vehicleInformation.put(AutoSSMetaData.VehicleTab.MAKE.getLabel(), "regex=.*\\S.*");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.MODEL.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|OTHER");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel(), "regex=.*\\S.*");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel(), "regex=.*\\S.*");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.AIR_BAGS.getLabel(), getVehicleTabAirBags(vehicle.getAirbagCode()));
				vehicleInformation.put(AutoSSMetaData.VehicleTab.ANTI_THEFT.getLabel(), getVehicleTabAntiTheft(vehicle.getAntiTheftString()));
				vehicleInformation.put(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel(), "contains=" + getVehicleTabStatCode(vehicle.getStatCode()));
			}
		}

		int streetNumber = RandomUtils.nextInt(100, 1000);
		String streetName = RandomStringUtils.randomAlphabetic(10).toUpperCase() + " St";
		vehicleInformation.put(AutoSSMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel(), "Yes");
		vehicleInformation.put(AutoSSMetaData.VehicleTab.ZIP_CODE.getLabel(), vehicle.getAddress().get(0).getZip());
		vehicleInformation.put(AutoSSMetaData.VehicleTab.ADDRESS_LINE_1.getLabel(), streetNumber + " " + streetName);
		vehicleInformation.put(AutoSSMetaData.VehicleTab.STATE.getLabel(), vehicle.getAddress().get(0).getState());
		vehicleInformation.put(AutoSSMetaData.VehicleTab.VALIDATE_ADDRESS_BTN.getLabel(), "click");
		vehicleInformation.put(AutoSSMetaData.VehicleTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.dataOf("Street number", streetNumber, "Street Name", streetName));

		switch (vehicle.getUsage()) {
			case "A":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Artisan");
				break;
			case "B":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Business");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.IS_THE_VEHICLE_USED_IN_ANY_COMMERCIAL_BUSINESS_OPERATIONS.getLabel(), "No");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.BUSINESS_USE_DESCRIPTION.getLabel(), "some description $<rx:\\d{3}>");
				break;
			case "F":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Farm");
				break;
			case "W1":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Commute");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel(), RandomUtils.nextInt(1, 15));
				break;
			case "W2":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Commute");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.MILES_ONE_WAY_TO_WORK_OR_SCHOOL.getLabel(), RandomUtils.nextInt(16, 100));
				break;
			case "P":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Pleasure"); // or Nano in case of Nano policy
				break;

			//For Tailer, Golf Cart and Motor Home
			case "P1":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Pleasure Use - Occupied Less than 30 Days a Year");
				break;
			case "P2":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Pleasure Use - Occupied 30-150 Days a Year");
				break;
			case "P3":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Pleasure Use - Occupied More than 150 Days a Year");
				break;
			case "PT":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Traveling Primary Residence");
				break;
			case "PR":
				vehicleInformation.put(AutoSSMetaData.VehicleTab.USAGE.getLabel(), "Non-Traveling Primary Residence");
				break;
			default:
				throw new IstfException("Unknown mapping for usage: " + vehicle.getUsage());
		}
		return new SimpleDataProvider(vehicleInformation);
	}

	private String getVinFromDb(AutoSSOpenLVehicle vehicle) {
		String vin = "";
		// 85 is default value for PHYSICALDAMAGECOLLISION and PHYSICALDAMAGECOMPREHENSIVE if there are no vehicles in DB with valid parameters
		// Search for trailer's VIN is useless since it cannot be used on UI to automatically fill vehicles fields
		if (vehicle.getCollSymbol() != 85 && vehicle.getCompSymbol() != 85 && !isTrailerType(vehicle.getStatCode())) {
			//TODO-dchubkov: add argument for stat code
			String getVinQuery = String.format("select VIN \n"
							+ "from VEHICLEREFDATAVIN\n"
							+ "inner join VEHICLEREFDATAMODEL\n"
							+ "on VEHICLEREFDATAVIN.VEHICLEREFDATAMODELID = VEHICLEREFDATAMODEL.ID \n"
							+ "where PHYSICALDAMAGECOLLISION %1$s AND PHYSICALDAMAGECOMPREHENSIVE %2$s AND YEAR %3$s AND (RESTRAINTSCODE %4$s) AND ANTITHEFTCODE %5$s",
					vehicle.getCollSymbol() == null ? "IS NULL" : "= " + vehicle.getCollSymbol(),
					vehicle.getCompSymbol() == null ? "IS NULL" : "= " + vehicle.getCollSymbol(),
					vehicle.getModelYear() == null ? "IS NULL" : "= " + vehicle.getModelYear(),
					vehicle.getAirbagCode() == null || "N".equals(vehicle.getAirbagCode()) ? "IS NULL" : "= " + getDbRestraintsCode(vehicle.getAirbagCode()),
					vehicle.getAntiTheftString() == null ? "IS NULL" : "= " + getDbAntitheftCode(vehicle.getAntiTheftString()));

			vin = DBService.get().getValue(getVinQuery).orElse(null);
		}
		return vin;
	}

	private String getPremiumAndCoveragesTabLimitOrDeductible(AutoSSOpenLCoverage coverage) {
		String coverageCd = coverage.getCoverageCd();
		if ("SP EQUIP".equals(coverageCd)) {
			return new Dollar(coverage.getLimit()).toString();
		}

		String limitOrDeductible;
		if ("COMP".equals(coverageCd) || "COLL".equals(coverageCd) || getState().equals(Constants.States.NY) && "PIP".equals(coverageCd)) {
			limitOrDeductible = coverage.getDeductible();
		} else {
			limitOrDeductible = coverage.getLimit();
		}

		String[] limitRange = limitOrDeductible.split("/");
		assertThat(limitRange.length).as("Unknown mapping for limit/deductible: %s", limitOrDeductible).isGreaterThanOrEqualTo(1).isLessThanOrEqualTo(2);

		if ("EMB".equals(coverageCd)) {
			return "1000000".equals(limitRange[0]) ? "starts=Yes" : "starts=No";
		}

		StringBuilder returnLimit = new StringBuilder();
		String formattedLimit = getFormattedCoverageLimit(limitRange[0], coverageCd);
		if (!formattedLimit.startsWith(AdvancedComboBox.RANDOM_MARK) && !formattedLimit.startsWith("starts=")) {
			returnLimit.append("starts=");
		}
		returnLimit.append(formattedLimit);
		if (limitRange.length == 2) {
			returnLimit.append("/");
			if ("IL".equals(coverageCd)) {
				returnLimit.append("month (").append(getFormattedCoverageLimit(limitRange[1], coverageCd)).append(" max)");
			} else {
				returnLimit.append(getFormattedCoverageLimit(limitRange[1], coverageCd));
			}
		}
		return returnLimit.toString();
	}
}
