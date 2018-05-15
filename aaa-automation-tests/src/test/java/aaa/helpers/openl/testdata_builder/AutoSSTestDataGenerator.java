package aaa.helpers.openl.testdata_builder;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
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
		return getRatingData(openLPolicy, false);
	}

	public TestData getRatingData(AutoSSOpenLPolicy openLPolicy, boolean isLegacyConvPolicy) {
		TestData ratingDataPattern = getRatingDataPattern().resolveLinks();

		if (openLPolicy.getReinstatements() != null && openLPolicy.getReinstatements() > 0) {
			//TODO-dchubkov: to be implemented...
			throw new NotImplementedException("Test data generation for \"reinstatements\" greater than 0 is not implemented.");
		}
		assertThat(getState()).as("State from TestDataGenerator differs from openl file's state").isEqualTo(openLPolicy.getCappingDetails().getState());

		if (!isLegacyConvPolicy && openLPolicy.getCappingDetails().getTermCappingFactor() != null && openLPolicy.getCappingDetails().getTermCappingFactor() != 1) {
			//TODO-dchubkov: to be implemented...
			throw new NotImplementedException("Test data generation for \"termCappingFactor\" not equal to 1 is not implemented for non-legacy policy.");
		}

		/*if (openLPolicy.getCappingDetails().getPreviousCappingFactor() != null && openLPolicy.getCappingDetails().getPreviousCappingFactor() != 1) {
			//TODO-dchubkov: to be implemented...
			throw new NotImplementedException("Test data generation for \"previousCappingFactor\" not equal to 1 is not implemented.");
		}*/

		String membershipNumber = null;
		if (Boolean.TRUE.equals(openLPolicy.isAAAMember())) {
			membershipNumber = MockDataHelper.getMembershipData()
					.getMembershipNumberForAvgAnnualERSperMember(openLPolicy.getEffectiveDate(), openLPolicy.getMemberPersistency(), openLPolicy.getAvgAnnualERSperMember());
		} else {
			ratingDataPattern
					.mask(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel())
					.mask(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.LAST_NAME.getLabel());
		}

		List<TestData> driversTestDataList = getDriverTabData(openLPolicy);
		TestData td = DataProviderFactory.dataOf(
				new PrefillTab().getMetaKey(), getPrefillTabData(),
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy, membershipNumber),
				new DriverTab().getMetaKey(), driversTestDataList,
				new RatingDetailReportsTab().getMetaKey(), getRatingDetailReportsTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new FormsTab().getMetaKey(), getFormsTabTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));

		if (getState().equals(Constants.States.NY) || getState().equals(Constants.States.VA)) {
			td.adjust(new AssignmentTab().getMetaKey(), getAssignmentTabData(driversTestDataList));
		}

		td = TestDataHelper.merge(ratingDataPattern, td);

		if (isLegacyConvPolicy) {
			TestData policyInformationTd = td.getTestData(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel())
					.mask(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.LEAD_SOURCE.getLabel());
			td.adjust(TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel()), policyInformationTd);
			td.getTestDataList(new DriverTab().getMetaKey()).forEach(driverData -> driverData.adjust(AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel(), getState()));
		}
		return td;
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

		//TODO-dchubkov: all ID states tests have "CSAA Affinity Insurance Company (formerly Keystone Insurance Company)" value for "Agent Entered Current/Prior Carrier" but is's missed. To be investigated...
		if (StringUtils.isNotBlank(openLPolicy.getCappingDetails().getCarrierCode()) && !getState().equals(Constants.States.ID)) {
			//TODO-dchubkov: add common method for replacing values from excel?
			String carrierCode = openLPolicy.getCappingDetails().getCarrierCode().trim().replaceAll("\u00A0", "");
			currentCarrierInformationData.adjust(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER.getLabel(), carrierCode);
		}

		TestData policyInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), getPremiumAndCoveragesPaymentPlan(openLPolicy.getCappingDetails().getTerm()),
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
		List<TestData> driversTestDataList = new ArrayList<>(openLPolicy.getDrivers().size());
		boolean isFirstDriver = true;
		boolean isEmployeeSet = false;
		boolean isAARPSet = false;
		boolean isAtFaultAccidentFreeSet = false;
		boolean isAccidentFreeSet = false;
		Integer aggregateCompClaims = openLPolicy.getAggregateCompClaims();
		Integer nafAccidents = openLPolicy.getNafAccidents();

		if (Constants.States.VA.equals(getState())) {
			int nonTrailersAndMotorHomesVehicleNumber = Math.toIntExact(openLPolicy.getVehicles().stream().filter(v -> !isTrailerOrMotorHomeType(v.getUsage())).count());
			List<String> nonTrailersAndMotorHomesVehicleIds = IntStream.range(1, nonTrailersAndMotorHomesVehicleNumber + 1).boxed().map(String::valueOf).collect(Collectors.toList());
			Iterator<String> iterator = nonTrailersAndMotorHomesVehicleIds.iterator();
			openLPolicy.getDrivers().forEach(d -> d.setVehicleAssignedId(iterator.hasNext() ? iterator.next() : nonTrailersAndMotorHomesVehicleIds.get(0)));
		}

		for (AutoSSOpenLDriver driver : openLPolicy.getDrivers()) {
			Integer dsr = driver.getDsr() != null ? driver.getDsr() : 0;
			if (dsr != 0) {
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

			String martialStatus = getDriverTabMartialStatus(driver.getMaritalStatus());
			TestData driverData = DataProviderFactory.dataOf(
					AutoSSMetaData.DriverTab.GENDER.getLabel(), getDriverTabGender(driver.getGender()),
					AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), martialStatus,
					AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel(), getDriverTabDateOfBirth(driver.getDriverAge(), openLPolicy.getEffectiveDate()),
					AutoSSMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel(), driver.getDriverAge() - driver.getTyde(),
					AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), getDriverTabLicenseType(driver.isForeignLicense()),
					AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "None",
					AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=First Named Insured|",
					AutoSSMetaData.DriverTab.OCCUPATION.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|",
					AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED.getLabel(), getYesOrNo(driver.hasSR22())
			);

			if (driver.getVehicleAssignedId() != null) {
				driverData.adjust(VEHICLE_ASSIGNED_ID_TESTDATA_KEY, driver.getVehicleAssignedId()); // for searching valid vehicle for driver assignment, should be masked in result test data
			}

			if (driver.getDriverAge() < 26 && ("Single".equals(martialStatus) || "Divorced".equals(martialStatus) || "Separated".equals(martialStatus))) {
				if (Boolean.TRUE.equals(driver.isGoodStudent())) {
					String mostRecentGpa = getRandom("College Graduate", "A Student", "B Student", "Pass");
					driverData.adjust(AutoSSMetaData.DriverTab.MOST_RECENT_GPA.getLabel(), mostRecentGpa);
					if (!"College Graduate".equals(mostRecentGpa)) {
						driverData.adjust(AutoSSMetaData.DriverTab.OCCUPATION.getLabel(), "Student");
					}
				} else {
					driverData.adjust(AutoSSMetaData.DriverTab.MOST_RECENT_GPA.getLabel(), getRandom("C or Below Student", "None", "Fail"));
				}
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

			if (Constants.States.VA.equals(getState()) && Boolean.TRUE.equals(driver.hasFR44())) {
				driverData.adjust(AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED.getLabel(), "Yes")
						.adjust(AutoSSMetaData.DriverTab.FORM_TYPE.getLabel(), "FR44");
			}

			if (Constants.States.DE.equals(getState()) && driver.hasTravelink() != null) {
				driverData.adjust(AutoSSMetaData.DriverTab.TRAVELINK_DISCOUNT.getLabel(), getYesOrNo(driver.hasTravelink()));
			}

			if (Constants.States.NJ.equals(getState()) && Boolean.TRUE.equals(driver.isExcludedDriver())) {
				driverData.adjust(AutoSSMetaData.DriverTab.DRIVER_TYPE.getLabel(), "Excluded");
			}

			if (Boolean.TRUE.equals(driver.isOutOfStateLicenseSurcharge())) {
				driverData.adjust(AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), "Licensed (US)")
						.adjust(AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=" + getState());
			}

			List<TestData> activityInformationList = new ArrayList<>();
			while (aggregateCompClaims != null && aggregateCompClaims > 0) {
				TestData activityInformationData = DataProviderFactory.dataOf(AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), "Comprehensive Claim",
						AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), "Comprehensive Claim",
						// Incident should be not older than 33 month from effective date to affect premium;
						AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), openLPolicy.getEffectiveDate().minusDays(new Random().nextInt(maxIncidentFreeInMonthsToAffectRating * 28))
								.format(DateTimeUtils.MM_DD_YYYY),
						AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), RandomUtils.nextInt(1001, 10000));

				activityInformationList.add(activityInformationData);
				aggregateCompClaims--;
			}

			while (nafAccidents != null && nafAccidents > 0) {
				TestData activityInformationData = DataProviderFactory.dataOf(
						AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), getRandom("At-Fault Accident", "Principally At-Fault Accident"),
						AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|",
						// Incident should be not older than 33 month from effective date to affect premium
						AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), openLPolicy.getEffectiveDate().minusDays(new Random().nextInt(maxIncidentFreeInMonthsToAffectRating * 28))
								.format(DateTimeUtils.MM_DD_YYYY),
						AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), RandomUtils.nextInt(100, 900));

				activityInformationList.add(activityInformationData);
				nafAccidents--;
			}

			if (openLPolicy.getYearsAtFaultAccidentFree() != null && openLPolicy.getYearsAtFaultAccidentFree() > 0 && !isAtFaultAccidentFreeSet && dsr > 0) {
				//TODO-dchubkov: add activity information with dsr > 0
				activityInformationList.add(getActivityInformationData(true, openLPolicy.getEffectiveDate(), openLPolicy.getYearsAtFaultAccidentFree()));
				isAtFaultAccidentFreeSet = true;
			}

			if (openLPolicy.getYearsIncidentFree() != null && openLPolicy.getYearsIncidentFree() > 0 && !isAccidentFreeSet && dsr > 0) {
				//TODO-dchubkov: add activity information with dsr > 0
				activityInformationList.add(getActivityInformationData(false, openLPolicy.getEffectiveDate(), openLPolicy.getYearsIncidentFree()));
				isAccidentFreeSet = true;
			}

			if (!activityInformationList.isEmpty()) {
				driverData.adjust(AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(), activityInformationList);
				if (isCleanDriverRenewalActive(openLPolicy, driver.getDsr())) {
					driverData.adjust(AutoSSMetaData.DriverTab.CLEAN_DRIVER_RENEWAL.getLabel(), getYesOrNo(driver.isCleanDriver()));
					if (Boolean.TRUE.equals(driver.isCleanDriver())) {
						driverData.adjust(AutoSSMetaData.DriverTab.CLEAN_DRIVER_RENEWAL_REASON.getLabel(), "some clean driver renewal reason $<rx:\\d{3}>");
					}
				}
			}

			driversTestDataList.add(driverData);
			isFirstDriver = false;
		}
		return driversTestDataList;
	}

	private boolean isCleanDriverRenewalActive(AutoSSOpenLPolicy openLPolicy, Integer dsr) {
		int baseDateYear = openLPolicy.getEffectiveDate().minusYears(openLPolicy.getAaaInsurancePersistency()).getYear();
		int dsrPoints = dsr == null ? 0 : dsr;
		return getState().equals(Constants.States.MD) && openLPolicy.getEffectiveDate().getYear() - baseDateYear > 2 && dsrPoints < 2;
	}

	private TestData getActivityInformationData(boolean atFaultAccident, LocalDate policyEffectiveDate, int yearsAccidentFree) {
		assertThat(yearsAccidentFree)
				.as("Invalid \"%s\" value in openl file, UI does not allow to set \"Occurrence Date\" more than 5 years", atFaultAccident ? "yearsAtFaultAccidentFree" : "yearsIncidentFree")
				.isLessThanOrEqualTo(5);
		LocalDate occurrenceDate = policyEffectiveDate.minusYears(yearsAccidentFree);

		Map<String, Object> activityInformationData = new HashMap<>();
		if (atFaultAccident) {
			activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), getRandom("At-Fault Accident", "Principally At-Fault Accident"));
			activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), RandomUtils.nextInt(1001, 10000));
		} else {
			activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(),
					getRandom("Major Violation", "Minor Violation", "Speeding Violation", "Alcohol-Related Violation"));
		}
		activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|");
		activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), occurrenceDate.format(DateTimeUtils.MM_DD_YYYY));
		if (!atFaultAccident && (getState().equals(Constants.States.PA) || getState().equals(Constants.States.NY))) {
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
			int trailersCount = Math.toIntExact(openLPolicy.getVehicles().stream().filter(v -> isTrailerType(getStatCode(v))).count());
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

			if (vehicle.getBiLiabilitySymbol() != null) {
				HashSet<String> liabilitySymbols = new HashSet<>();
				liabilitySymbols.add(vehicle.getBiLiabilitySymbol());
				liabilitySymbols.add(vehicle.getPdLiabilitySymbol());
				liabilitySymbols.add(vehicle.getMpLiabilitySymbol());
				liabilitySymbols.add(vehicle.getUmLiabilitySymbol());
				if (liabilitySymbols.size() > 1) {
					throw new NotImplementedException(String.format("Not all *LiabilitySymbol field values are the same: %s, test data generation for this case is not implemented", liabilitySymbols));
				}
			}

			TestData vehicleData = getVehicleTabInformationData(vehicle);
			if (Boolean.TRUE.equals(vehicle.isTelematic())) {
				vehicleData.adjust(getVehicleTabVehicleDetailsData("No Score"));
			}

			if (vehicle.getSafetyScore() != null) {
				assertThat(vehicle.isTelematic()).as("\"isTelematic\" should be false if \"safetyScore\" is not null").isFalse();
				vehicleData.adjust(getVehicleTabVehicleDetailsData(String.valueOf(vehicle.getSafetyScore())));
			}

			vehiclesTestDataList.add(vehicleData);
		}

		return vehiclesTestDataList;
	}

	private TestData getAssignmentTabData(List<TestData> driversTestDataList) {
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
		//TODO-dchubkov: There are no drivers list in "Unacceptable Risk Surcharge" section for NY and NJ states, to be investigated
		if (!getState().equals(Constants.States.NY) && !getState().equals(Constants.States.NJ)) {
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
				if (getState().equals(Constants.States.NJ) && "PIP".equals(coverage.getCoverageCd())) {
					// for NJ state PIP coverage should be set by covering aaaPIPMedExpLimit field ("Medical Expense" on UI)
					continue;
				}

				String coverageName = getPremiumAndCoveragesTabCoverageName(coverage.getCoverageCd());
				if (isPolicyLevelCoverageCd(coverage.getCoverageCd())) {
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

		if (getState().equals(Constants.States.CT)) {
			policyCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORIST_CONVERSION_COVERAGE.getLabel(), getYesOrNo(openLPolicy.getUmbiConvCode()));
		}

		if (getState().equals(Constants.States.CO) && !policyCoveragesData.containsKey(getPremiumAndCoveragesTabCoverageName("UMBI"))) {
			policyCoveragesData.put(getPremiumAndCoveragesTabCoverageName("UMBI"), "starts=No Coverage");
		}

		if (getState().equals(Constants.States.MT) && !policyCoveragesData.containsKey(getPremiumAndCoveragesTabCoverageName("UIMBI"))) {
			policyCoveragesData.put(getPremiumAndCoveragesTabCoverageName("UIMBI"), "starts=No Coverage");
		}

		if (getState().equals(Constants.States.IN)) {
			policyCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.UNINSURED_MOTORIST_PROPERTY_DAMAGE_LIMIT.getLabel(), "starts=No Coverage");
		}

		if (getState().equals(Constants.States.VA)) {
			policyCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.INCOME_LOSS.getLabel(), "starts=No Coverage");
		}

		if (getState().equals(Constants.States.SD)) {
			policyCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.MEDICAL_PAYMENTS.getLabel(), "starts=No Coverage");
		}

		if (getState().equals(Constants.States.ID)) {
			policyCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel(), "starts=No Coverage");
		}

		if (getState().equals(Constants.States.NV)) {
			for (int i = 0; i < openLPolicy.getVehicles().size(); i++) {
				if (!isTrailerType(getStatCode(openLPolicy.getVehicles().get(i)))) {
					detailedVehicleCoveragesList.get(i).adjust(AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.UNINSURED_MOTORIST_PROPERTY_DAMAGE.getLabel(), "starts=No Coverage");
				}
			}
		}

		return DataProviderFactory.dataOf(
				AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel(), getPremiumAndCoveragesPaymentPlan(openLPolicy.getPaymentPlanType(), openLPolicy.getCappingDetails().getTerm()),
				AutoSSMetaData.PremiumAndCoveragesTab.UNACCEPTABLE_RISK_SURCHARGE.getLabel(), openLPolicy.isUnacceptableRisk(),
				AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS.getLabel(), "Yes", //TODO-dchubkov: enable only if need to fill expanded section
				AutoSSMetaData.PremiumAndCoveragesTab.MULTI_CAR.getLabel(), openLPolicy.isMultiCar(),
				AutoSSMetaData.PremiumAndCoveragesTab.UNVERIFIABLE_DRIVING_RECORD_SURCHARGE.getLabel(), new SimpleDataProvider(unverifiableDrivingRecordSurchargeData),
				AutoSSMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(), detailedVehicleCoveragesList)
				.adjust(new SimpleDataProvider(policyCoveragesData))
				.adjust(getPolicyPersonalInjuryProtectionCoveragesData(openLPolicy));
	}

	private TestData getVehicleTabInformationData(AutoSSOpenLVehicle vehicle) {
		String vin = getVinFromDb(vehicle);
		Map<String, Object> vehicleInformation = new HashMap<>();
		String statCode = getStatCode(vehicle);
		vehicleInformation.put(AutoSSMetaData.VehicleTab.TYPE.getLabel(), getVehicleTabType(statCode));

		if (StringUtils.isNotBlank(vin)) {
			vehicleInformation.put(AutoSSMetaData.VehicleTab.VIN.getLabel(), covertToValidVin(vin));
		} else {
			vehicleInformation.put(AutoSSMetaData.VehicleTab.YEAR.getLabel(), vehicle.getModelYear());
			vehicleInformation.put(AutoSSMetaData.VehicleTab.STATED_AMOUNT.getLabel(), vehicle.getCollSymbol() * 1000);
			if (getState().equals(Constants.States.NY) && Boolean.TRUE.equals(vehicle.isABS())) {
				assertThat(isTrailerType(statCode)).as("isABS=TRUE is valid only for non-Trailer vehicles types ").isFalse();
				vehicleInformation.put(AutoSSMetaData.VehicleTab.DAYTIME_RUNNING_LAMPS.getLabel(), "Yes");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.ANTI_LOCK_BRAKES.getLabel(), "Yes");
			}
			if (isTrailerOrMotorHomeType(vehicle.getUsage())) {
				vehicleInformation.put(AutoSSMetaData.VehicleTab.PRIMARY_OPERATOR.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel(), "some other make $<rx:\\d{3}>");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel(), "some other model $<rx:\\d{3}>");

				if (isTrailerType(statCode)) {
					String trailerType = getVehicleTabTrailerType(statCode);
					vehicleInformation.put(AutoSSMetaData.VehicleTab.TRAILER_TYPE.getLabel(), trailerType);
					if ("Travel Trailer".equals(trailerType)) {
						vehicleInformation.put(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel(), "contains=" + getVehicleTabStatCode(statCode));
					}
				} else {
					vehicleInformation.put(AutoSSMetaData.VehicleTab.MOTOR_HOME_TYPE.getLabel(), getVehicleTabMotorHomeType(statCode));
				}
			} else {
				vehicleInformation.put(AutoSSMetaData.VehicleTab.MAKE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|OTHER");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.MODEL.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|OTHER");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel(), "regex=.*\\S.*");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel(), AdvancedComboBox.RANDOM_MARK);
				vehicleInformation.put(AutoSSMetaData.VehicleTab.AIR_BAGS.getLabel(), getVehicleTabAirBags(vehicle.getAirbagCode()));
				vehicleInformation.put(AutoSSMetaData.VehicleTab.ANTI_THEFT.getLabel(), getVehicleTabAntiTheft(vehicle.getAntiTheftString()));
				vehicleInformation.put(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel(), "contains=" + getVehicleTabStatCode(statCode));
			}
		}

		int streetNumber = RandomUtils.nextInt(100, 1000);
		String streetName = RandomStringUtils.randomAlphabetic(10).toUpperCase() + " St";
		vehicleInformation.put(AutoSSMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel(), "Yes");

		String zipCode = vehicle.getAddress().getZip();
		if (getState().equals(Constants.States.CT)) {
			zipCode = getZipCodeFromDb(zipCode);
		}
		vehicleInformation.put(AutoSSMetaData.VehicleTab.ZIP_CODE.getLabel(), zipCode);
		vehicleInformation.put(AutoSSMetaData.VehicleTab.ADDRESS_LINE_1.getLabel(), streetNumber + " " + streetName);
		vehicleInformation.put(AutoSSMetaData.VehicleTab.STATE.getLabel(), vehicle.getAddress().getState());
		vehicleInformation.put(AutoSSMetaData.VehicleTab.VALIDATE_ADDRESS_BTN.getLabel(), "click");
		vehicleInformation.put(AutoSSMetaData.VehicleTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.dataOf("Street number", streetNumber, "Street Name", streetName));

		assertThat(vehicle.getUsage()).as("Vehicles \"usage\" value should not be null or empty").isNotNull().isNotEmpty();
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

			//For Trailer, Golf Cart and Motor Home
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

		TestData td = new SimpleDataProvider(vehicleInformation);
		if (vehicle.getCoverages().stream().anyMatch(c -> "LOAN".equals(c.getCoverageCd()))) {
			TestData ownershipData = DataProviderFactory.dataOf(AutoSSMetaData.VehicleTab.Ownership.OWNERSHIP_TYPE.getLabel(), "Leased");
			td.adjust(AutoSSMetaData.VehicleTab.OWNERSHIP.getLabel(), ownershipData);
		}

		return td;
	}

	private String getZipCodeFromDb(String locationCode) {
		String getZipCodeQuery = "select POSTALCODE from LOOKUPVALUE where CODE = ? and LOOKUPLIST_ID in (select ID from LOOKUPLIST where LOOKUPNAME = 'AAACountyTownship') and RISKSTATECD = ?";
		return DBService.get().getValue(getZipCodeQuery, locationCode, getState()).get();
	}

	private String getVinFromDb(AutoSSOpenLVehicle vehicle) {
		String vin = "";

		// 85 is default value for PHYSICALDAMAGECOLLISION and PHYSICALDAMAGECOMPREHENSIVE if there are no vehicles in DB with valid parameters
		// Search for trailer's VIN is useless since it cannot be used on UI to automatically fill vehicles fields
		if (vehicle.getCollSymbol() != 85 && vehicle.getCompSymbol() != 85 && !isTrailerType(getStatCode(vehicle))) {
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

	private TestData getPolicyPersonalInjuryProtectionCoveragesData(AutoSSOpenLPolicy openLPolicy) {
		if (!getState().equals(Constants.States.NJ)) {
			return DataProviderFactory.emptyData();
		}

		Map<String, Object> td = new HashMap<>();
		if (openLPolicy.getAaaAPIPIncomeContBenLimit() != null) {
			td.put(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.WEEKLY_INCOME_CONTINUATION_BENEFITS.getLabel(), getDollarValue(openLPolicy
					.getAaaAPIPIncomeContBenLimit()));
		}
		if (StringUtils.isNotBlank(openLPolicy.getAaaAPIPLengthIncomeCont())) {
			assertThat(openLPolicy.getAaaAPIPLengthIncomeCont()).as("Unknown mapping for value \"%s\" of \"aaaAPIPLengthIncomeCont\" field", openLPolicy.getAaaAPIPLengthIncomeCont())
					.isIn("TWOYR", "UNLIMITED");
			td.put(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.LENGTH_OF_INCOME_CONTINUATION.getLabel(),
					"TWOYR".equals(openLPolicy.getAaaAPIPLengthIncomeCont()) ? "Two Years" : "Unlimited");
		}
		if (openLPolicy.getAaaPIPExtMedPayLimit() != null) {
			Dollar limit = openLPolicy.getAaaPIPExtMedPayLimit() == 1 ? new Dollar(1000) : new Dollar(10000);
			td.put(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.EXTENDED_MEDICAL_PAYMENTS.getLabel(), limit.toString().replaceAll("\\.00", ""));
		}
		if (openLPolicy.getAaaPIPMedExpDeductible() != null) {
			td.put(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.MEDICAL_EXPENSE_DEDUCTIBLE.getLabel(), getDollarValue(openLPolicy.getAaaPIPMedExpDeductible()));
		}
		if (openLPolicy.getAaaPIPMedExpLimit() != null) {
			td.put(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.MEDICAL_EXPENSE.getLabel(),
					new Dollar(openLPolicy.getAaaPIPMedExpLimit()).multiply(1000).toString().replaceAll("\\.00", ""));
		}
		if (openLPolicy.getAaaPIPNonMedExp() != null) {
			assertThat(openLPolicy.getAaaPIPNonMedExp()).as("Unknown mapping for value \"%s\" of \"aaaPIPNonMedExp\" field", openLPolicy.getAaaPIPNonMedExp()).isIn("B", "D");
			td.put(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.NON_MEDICAL_EXPENSE.getLabel(), "B".equals(openLPolicy.getAaaPIPNonMedExp()) ? "Yes" : "No");
		}
		if (openLPolicy.getAaaPIPPrimaryInsurer() != null) {
			assertThat(openLPolicy.getAaaPIPPrimaryInsurer()).as("Unknown mapping for value \"%s\" of \"aaaPIPPrimaryInsurer\" field", openLPolicy.getAaaPIPPrimaryInsurer()).isIn("P", "S");
			td.put(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.PRIMARY_INSURER.getLabel(),
					"P".equals(openLPolicy.getAaaPIPPrimaryInsurer()) ? "Auto Insurance" : "Personal Health Insurance");
		}
		if (openLPolicy.getNoOfAPIPAddlNamedRel() != null) {
			assertThat(openLPolicy.getNoOfAPIPAddlNamedRel())
					.as("Unknown mapping for value \"%s\" of \"noOfAPIPAddlNamedRel\" field. It should be >= 0 and <= 6", openLPolicy.getNoOfAPIPAddlNamedRel())
					.isGreaterThanOrEqualTo(0).isLessThanOrEqualTo(6);
			if (openLPolicy.getNoOfAPIPAddlNamedRel() == 0) {
				td.put(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.ADDITIONAL_PERSONAL_INJURY_PROTECTION_BENEFIT.getLabel(), "No");
			} else {
				List<String> relativesNamesControls = Arrays.asList(
						AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.RELATIVES_NAME1.getLabel(),
						AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.RELATIVES_NAME2.getLabel(),
						AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.RELATIVES_NAME3.getLabel(),
						AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.RELATIVES_NAME4.getLabel(),
						AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.RELATIVES_NAME5.getLabel(),
						AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.RELATIVES_NAME6.getLabel());

				td.put(AutoSSMetaData.PremiumAndCoveragesTab.PolicyLevelPersonalInjuryProtectionCoverages.COVERAGE_INCLUDES.getLabel(), "Named Insureds and Family Members");
				for (int i = 0; i < openLPolicy.getNoOfAPIPAddlNamedRel(); i++) {
					td.put(relativesNamesControls.get(i), "$<rx:relative\\d{5}>");
				}
			}
		}

		return DataProviderFactory.dataOf(AutoSSMetaData.PremiumAndCoveragesTab.POLICY_LEVEL_PERSONAL_INJURY_PROTECTION_COVERAGES.getLabel(), new SimpleDataProvider(td));
	}
}
