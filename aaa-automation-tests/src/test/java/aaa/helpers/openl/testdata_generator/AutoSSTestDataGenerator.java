package aaa.helpers.openl.testdata_generator;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.helpers.TestDataHelper;
import aaa.helpers.mock.ApplicationMocksManager;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLCoverage;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLDriver;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLVehicle;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
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
		TestData ratingDataPattern = getRatingDataPattern().resolveLinks();

		if (openLPolicy.getReinstatements() != null && openLPolicy.getReinstatements() > 0) {
			//TODO-dchubkov: to be implemented...
			throw new NotImplementedException("Test data generation for \"reinstatements\" greater than 0 is not implemented.");
		}
		assertThat(getState()).as("State from TestDataGenerator differs from openl file's state").isEqualTo(openLPolicy.getCappingDetails().getState());

		/*if (openLPolicy.getCappingDetails().getPreviousCappingFactor() != null && openLPolicy.getCappingDetails().getPreviousCappingFactor() != 1) {
			//TODO-dchubkov: to be implemented...
			throw new NotImplementedException("Test data generation for \"previousCappingFactor\" not equal to 1 is not implemented.");
		}*/

		String membershipNumber = null;
		if (Boolean.TRUE.equals(openLPolicy.isAAAMember())) {
			membershipNumber = ApplicationMocksManager.getRetrieveMembershipSummaryMock()
					.getMembershipNumberForAvgAnnualERSperMember(openLPolicy.getEffectiveDate(), openLPolicy.getMemberPersistency(), openLPolicy.getAvgAnnualERSperMember());
			assertThat(membershipNumber).as("No valid membership number was found for effectiveDate=%1$s, memberPersistency=%2$s and avgAnnualERSperMember=%3$s fields",
					openLPolicy.getEffectiveDate(), openLPolicy.getMemberPersistency(), openLPolicy.getAvgAnnualERSperMember()).isNotNull();
		} else {
			ratingDataPattern
					.mask(TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel()));
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

		if (openLPolicy.isLegacyConvPolicy()) {
			ratingDataPattern
					.mask(TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()))
					.mask(TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.LEAD_SOURCE.getLabel()));
		}

		td = TestDataHelper.merge(ratingDataPattern, td);
		return td;
	}

	public TestData getCappingData(AutoSSOpenLPolicy openLPolicy) {
		long manualCappingFactor = openLPolicy.isCappedPolicy() ? Math.round(openLPolicy.getCappingDetails().getTermCappingFactor() * 100) : 100;
		return DataProviderFactory.dataOf(AutoSSMetaData.PremiumAndCoveragesTab.VIEW_CAPPING_DETAILS_DIALOG.getLabel(), DataProviderFactory.dataOf(
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.ViewCappingDetailsDialog.MANUAL_CAPPING_FACTOR.getLabel(), manualCappingFactor,
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.ViewCappingDetailsDialog.CAPPING_OVERRIDE_REASON.getLabel(), "index=1",
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.ViewCappingDetailsDialog.BUTTON_CALCULATE.getLabel(), "click"));
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

		Map<String, Object> currentAAAMembershipData = new HashMap<>();
		currentAAAMembershipData.put(AutoSSMetaData.GeneralTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), getYesOrNo(openLPolicy.isAAAMember()));

		// TODO NEED TO REFACTOR According to changes in UI for Auto SS product for MPD feature.
		// ownedHome is a temporal fix for NY Auto SS test, should be deleted after MPD Merge to master
		String ownedHome = "Y".equalsIgnoreCase(openLPolicy.getAaaHomePolicy()) ? "Yes" : "No";

		/*Map<String, Object> aAAProductOwnedData = new HashMap<>();
		if (Boolean.TRUE.equals("Y".equalsIgnoreCase(openLPolicy.getAaaHomePolicy()))) {
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME_OLD.getLabel(), "Yes");
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME_POLICY_NUM.getLabel(), RandomStringUtils.randomNumeric(6));
		} else {
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.HOME_OLD.getLabel(), "No");
		}

		if (Boolean.TRUE.equals(openLPolicy.isAaaLifePolicy())) {
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE.getLabel(), true);
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.OtherAAAProductsOwned.LIFE_POLICY_NUM.getLabel(), RandomStringUtils.randomNumeric(6));
		} else {
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.AAAMembership.LIFE.getLabel(), "No");
		}

		if (Boolean.TRUE.equals("Y".equalsIgnoreCase(openLPolicy.getAaaRentersPolicy()))) {
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.AAAMembership.RENTERS.getLabel(), "Yes");
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.AAAMembership.RENTERS_POLICY_NUM.getLabel(), RandomStringUtils.randomNumeric(6));
		} else {
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.AAAMembership.RENTERS.getLabel(), "No");
		}

		if (Boolean.TRUE.equals("Y".equalsIgnoreCase(openLPolicy.getAaaCondoPolicy()))) {
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.AAAMembership.CONDO.getLabel(), "Yes");
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.AAAMembership.CONDO_POLICY_NUM.getLabel(), RandomStringUtils.randomNumeric(6));
		} else {
			aAAProductOwnedData.put(AutoSSMetaData.GeneralTab.AAAMembership.CONDO.getLabel(), "No");
		}*/

		// TODO Refactor section  END

		//TODO: exclude for RO state: AutoSSMetaData.GeneralTab.AAAMembership.MOTORCYCLE.getLabel(), openLPolicy.isAaaMotorcyclePolicy()

		if (membershipNumber != null) {
			currentAAAMembershipData.put(AutoSSMetaData.GeneralTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), membershipNumber);
		}

		Map<String, Object> currentCarrierInformationData = new HashMap<>();
		currentCarrierInformationData.put(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER.getLabel(), "Yes");
		currentCarrierInformationData.put(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel(), getGeneralTabPriorBILimit(openLPolicy.getPriorBILimit()));

		currentCarrierInformationData.putAll(
				getGeneralTabAgentInceptionAndExpirationData(openLPolicy.getAutoInsurancePersistency(), openLPolicy.getAaaInsurancePersistency(), openLPolicy.getEffectiveDate()));

		if (!openLPolicy.isLegacyConvPolicy()) {
			currentCarrierInformationData.put(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER.getLabel(), "AAA-SoCal (ACSC)");
		} else {
			//TODO-dchubkov: all ID states tests have "CSAA Affinity Insurance Company (formerly Keystone Insurance Company)" value for "Agent Entered Current/Prior Carrier" but it's missed. To be investigated...
			if (StringUtils.isNotBlank(openLPolicy.getCappingDetails().getCarrierCode()) && !getState().equals(Constants.States.ID)) {
				//TODO-dchubkov: add common method for replacing values from excel?
				String carrierCode = openLPolicy.getCappingDetails().getCarrierCode().trim().replaceAll("\u00A0", "");
				currentCarrierInformationData.put(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER.getLabel(), carrierCode);
			} else if (openLPolicy.isCappedPolicy()) {
				String carrierCode;
				switch (getState()) {
					//TODO-dchubkov: fill carrier codes for other states, see "Capping" tab -> "Carrier Code" column in algorithm files for each state
					case Constants.States.KY:
					case Constants.States.UT:
						carrierCode = "Western United";
						break;
					case Constants.States.MD:
						carrierCode = "CSAA Affinity Insurance Company (formerly Keystone Insurance Company)";
						break;
					default:
						throw new IstfException(String.format("In order to set termCappingFactor=%1$s, appropriate carrier code should be set in General tab but it's unknown for %2$s state.",
								openLPolicy.getCappingDetails().getTermCappingFactor(), getState()));
				}
				currentCarrierInformationData.put(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER.getLabel(), carrierCode);
			}
		}

		Map<String, Object> policyInformationData = new HashMap<>();
		if (!openLPolicy.isLegacyConvPolicy()) {
			policyInformationData.put(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY));
		}
		policyInformationData.put(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), getPremiumAndCoveragesPaymentPlan(openLPolicy.getCappingDetails().getTerm()));
		policyInformationData.put(AutoSSMetaData.GeneralTab.PolicyInformation.CHANNEL_TYPE.getLabel(), "AAA Agent"); // hardcoded value
		//TODO: exclude for RO state: AutoSSMetaData.GeneralTab.PolicyInformation.ADVANCED_SHOPPING_DISCOUNTS.getLabel(), generalTabIsAdvanceShopping(openLPolicy.isAdvanceShopping())

		if (Boolean.TRUE.equals(openLPolicy.isAdvanceShopping())) {
			policyInformationData.put(AutoSSMetaData.GeneralTab.PolicyInformation.OVERRIDE_ASD_LEVEL.getLabel(), "Yes");
			policyInformationData.put(AutoSSMetaData.GeneralTab.PolicyInformation.ADVANCED_SHOPPING_DISCOUNT_OVERRIDE.getLabel(), "Level 1");
			policyInformationData.put(AutoSSMetaData.GeneralTab.PolicyInformation.ASD_OVERRIDEN_BY.getLabel(), "Tester $<rx:\\d{5}>");
		}

		return DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), Arrays.asList(namedInsuredInformationData),
				AutoSSMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel(), new SimpleDataProvider(currentAAAMembershipData),
				AutoSSMetaData.GeneralTab.CONTACT_INFORMATION.getLabel(), DataProviderFactory.emptyData(),
				AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(), new SimpleDataProvider(currentCarrierInformationData),
				AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), new SimpleDataProvider(policyInformationData),
				AutoSSMetaData.GeneralTab.HOME.getLabel(), ownedHome);
	}

	private List<TestData> getDriverTabData(AutoSSOpenLPolicy openLPolicy) {
		if (openLPolicy.getYearsAtFaultAccidentFree() != null) {
			assertThat(openLPolicy.getYearsAtFaultAccidentFree()).as("Invalid \"yearsAtFaultAccidentFree\" value in openl file, UI does not allow to set \"Occurrence Date\" more than 5 years").isLessThanOrEqualTo(5);
		}
		if (openLPolicy.getYearsIncidentFree() != null) {
			assertThat(openLPolicy.getYearsAtFaultAccidentFree()).as("Invalid \"yearsIncidentFree\" value in openl file, UI does not allow to set \"Occurrence Date\" more than 5 years").isLessThanOrEqualTo(5);
		}

		List<TestData> driversTestDataList = new ArrayList<>(openLPolicy.getDrivers().size());
		boolean isFirstDriver = true;
		boolean isEmployeeSet = false;
		boolean isAARPSet = false;
		boolean setADBCoverage = openLPolicy.getVehicles().stream().map(AutoSSOpenLVehicle::getCoverages).flatMap(List::stream).anyMatch(c -> "ADB".equals(c.getCoverageCd()));
		int aggregateCompClaims = openLPolicy.getAggregateCompClaims() != null ? openLPolicy.getAggregateCompClaims() : 0;
		int nafAccidents = openLPolicy.getNafAccidents() != null ? openLPolicy.getNafAccidents() : 0;

		if (Constants.States.VA.equals(getState())) {
			int nonTrailersAndMotorHomesVehicleNumber = Math.toIntExact(openLPolicy.getVehicles().stream().filter(v -> !isTrailerOrMotorHomeOrGolfCartType(v.getUsage())).count());
			List<String> nonTrailersAndMotorHomesVehicleIds = IntStream.range(1, nonTrailersAndMotorHomesVehicleNumber + 1).boxed().map(String::valueOf).collect(Collectors.toList());
			Iterator<String> iterator = nonTrailersAndMotorHomesVehicleIds.iterator();
			openLPolicy.getDrivers().forEach(d -> d.setVehicleAssignedId(iterator.hasNext() ? iterator.next() : nonTrailersAndMotorHomesVehicleIds.get(0)));
		}

		for (AutoSSOpenLDriver driver : openLPolicy.getDrivers()) {
			if (!Objects.equals(driver.getDriverAge(), driver.getAgeBeforeEndorsement())) {
				//TODO-dchubkov: to be implemented but at the moment don't have openL files with ageBeforeEndorsement different from driverAge
				throw new NotImplementedException("Test data generation for \"ageBeforeEndorsement\" is not implemented.");
			}

			if (Boolean.FALSE.equals(driver.isExposure())) {
				throw new IstfException("\"exposure\" openL field value should be always TRUE");
			}

			String martialStatus = getDriverTabMartialStatus(driver.getMaritalStatus());

			Map<String, Object> driverData = new HashMap<>();
			driverData.put(AutoSSMetaData.DriverTab.GENDER.getLabel(), getDriverTabGender(driver.getGender()));
			driverData.put(AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), martialStatus);
			driverData.put(AutoSSMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel(), driver.getDriverAge() - driver.getTyde());
			driverData.put(AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), getDriverTabLicenseType(driver.isForeignLicense()));
			driverData.put(AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "None");
			driverData.put(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=First Named Insured|");
			driverData.put(AutoSSMetaData.DriverTab.OCCUPATION.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_EMPTY);
			driverData.put(AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED.getLabel(), getYesOrNo(driver.hasSR22()));

			if (driver.getVehicleAssignedId() != null) {
				driverData.put(VEHICLE_ASSIGNED_ID_TESTDATA_KEY, driver.getVehicleAssignedId()); // for searching valid vehicle for driver assignment, should be masked in result test data
			}

			if (driver.getDriverAge() < 26 && ("Single".equals(martialStatus) || "Divorced".equals(martialStatus) || "Separated".equals(martialStatus))) {
				if (Boolean.TRUE.equals(driver.isGoodStudent())) {
					String mostRecentGpa = getRandom("College Graduate", "A Student", "B Student", "Pass");
					driverData.put(AutoSSMetaData.DriverTab.MOST_RECENT_GPA.getLabel(), mostRecentGpa);
					if (!"College Graduate".equals(mostRecentGpa)) {
						driverData.put(AutoSSMetaData.DriverTab.OCCUPATION.getLabel(), "Student");
					}
				} else {
					driverData.put(AutoSSMetaData.DriverTab.MOST_RECENT_GPA.getLabel(), getRandom("C or Below Student", "None", "Fail"));
				}
			}

			if (!isFirstDriver) {
				String[] firstLastName = driver.getName().split("\\s");
				String firstName = firstLastName[0];
				String lastName = firstLastName.length > 1 ? firstLastName[1] : firstName;
				driverData.put(AutoSSMetaData.DriverTab.DRIVER_SEARCH_DIALOG.getLabel(), DataProviderFactory.emptyData());
				driverData.put(AutoSSMetaData.DriverTab.FIRST_NAME.getLabel(), firstName);
				driverData.put(AutoSSMetaData.DriverTab.LAST_NAME.getLabel(), lastName);
				driverData.put(AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel(), getDriverTabDateOfBirth(driver.getDriverAge(), openLPolicy.getEffectiveDate()));
			}

			if (Boolean.TRUE.equals(driver.isSmartDriver())) {
				driverData.put(AutoSSMetaData.DriverTab.SMART_DRIVER_COURSE_COMPLETED.getLabel(), getYesOrNo(driver.isSmartDriver()));
			}
			if (Boolean.TRUE.equals(driver.isDistantStudent())) {
				driverData.put(AutoSSMetaData.DriverTab.DISTANT_STUDENT.getLabel(), getYesOrNo(driver.isDistantStudent()));
			}
			if ("Y".equalsIgnoreCase(driver.getDefensiveDrivingCourse())) {
				driverData.put(AutoSSMetaData.DriverTab.DEFENSIVE_DRIVER_COURSE_COMPLETED.getLabel(), getYesOrNo(driver.getDefensiveDrivingCourse()));
			} else if ("D".equals(driver.getDefensiveDrivingCourse()) && driver.getDriverAge() > 55) {
				driverData.put(AutoSSMetaData.DriverTab.DEFENSIVE_DRIVER_COURSE_COMPLETED.getLabel(), "Yes");
				driverData.put(AutoSSMetaData.DriverTab.DEFENSIVE_DRIVER_COURSE_COMPLETION_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY));
			}

			if (Boolean.TRUE.equals(openLPolicy.isEmployee()) && !isEmployeeSet) {
				driverData.put(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), "Spouse");
				driverData.put(AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "AAA Employee");
				isEmployeeSet = true;
			}

			if (Boolean.TRUE.equals(openLPolicy.isAARP()) && !isAARPSet) {
				driverData.put(AutoSSMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel(), "Spouse");
				driverData.put(AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "AARP");
				isAARPSet = true;
				if (Boolean.TRUE.equals(openLPolicy.isEmployee())) {
					assertThat(openLPolicy.getDrivers().size()).as("Policy with openl fields \"isEmployee\" and \"isAARP\" which are both TRUE should have at least 2 drivers"
							+ " to fill \"%s\" UI field differently for each of them", AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel())
							.isGreaterThan(1);
					isEmployeeSet = false; // will set "Affinity Group"="AAA Employee" to the next driver
				}
			}

			if (Constants.States.VA.equals(getState()) && Boolean.TRUE.equals(driver.hasFR44())) {
				driverData.put(AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED.getLabel(), "Yes");
				driverData.put(AutoSSMetaData.DriverTab.FORM_TYPE.getLabel(), "FR44");
			}

			if (Constants.States.DE.equals(getState()) && driver.hasTravelink() != null) {
				driverData.put(AutoSSMetaData.DriverTab.TRAVELINK_DISCOUNT.getLabel(), getYesOrNo(driver.hasTravelink()));
			}

			if (Constants.States.NJ.equals(getState()) && Boolean.TRUE.equals(driver.isExcludedDriver())) {
				driverData.put(AutoSSMetaData.DriverTab.DRIVER_TYPE.getLabel(), "Excluded");
			}

			if (Boolean.TRUE.equals(driver.isOutOfStateLicenseSurcharge())) {
				driverData.put(AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), "Licensed (US)");
				driverData.put(AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=" + getState());
			}

			List<TestData> activityInformationList = new ArrayList<>();
			while (aggregateCompClaims > 0) {
				TestData activityInformationData = ActivityInformation.of("Comprehensive Claim").getTestData(RandomUtils.nextInt(1001, 10000), getOccurrenceDate(openLPolicy.getEffectiveDate()));
				activityInformationList.add(activityInformationData);
				aggregateCompClaims--;
			}

			while (nafAccidents > 0) {
				TestData activityInformationData = ActivityInformation.getNotAtFaultAccident(0).getTestData(getOccurrenceDate(openLPolicy.getEffectiveDate()));
				activityInformationList.add(activityInformationData);
				nafAccidents--;
			}

			Integer dsr = driver.getDsr() != null ? driver.getDsr() : 0;

			if (openLPolicy.getYearsAtFaultAccidentFree() != null && openLPolicy.getYearsAtFaultAccidentFree() < 5) {
				ActivityInformation ai = ActivityInformation.getAtFaultAccidents().stream().min(Comparator.comparing(ActivityInformation::getPoints)).get();
				int claimPoints = ai.getPoints(openLPolicy.getYearsAtFaultAccidentFree());
				if (claimPoints <= dsr) {
					LocalDate occurrenceDate = openLPolicy.getEffectiveDate().minusYears(openLPolicy.getYearsAtFaultAccidentFree());
					TestData activityInformationData = ai.getTestData(occurrenceDate);
					if (getState().equals(Constants.States.PA)) {
						activityInformationData.adjust(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE.getLabel(), occurrenceDate.format(DateTimeUtils.MM_DD_YYYY));
					}
					activityInformationList.add(activityInformationData);
					dsr = dsr - claimPoints;
				}
			}

			if (openLPolicy.getYearsIncidentFree() != null && openLPolicy.getYearsIncidentFree() < 5) {
				ActivityInformation ai = ActivityInformation.ofMinimumPoints("Major Violation", "Minor Violation", "Speeding Violation", "Alcohol-Related Violation");
				int claimPoints = ai.getPoints(openLPolicy.getYearsIncidentFree());
				if (claimPoints <= dsr) {
					LocalDate occurrenceDate = openLPolicy.getEffectiveDate().minusYears(openLPolicy.getYearsIncidentFree());
					TestData activityInformationData = ai.getTestData(occurrenceDate);
					if (getState().equals(Constants.States.NY)) {
						activityInformationData.adjust(AutoSSMetaData.DriverTab.ActivityInformation.CONVICTION_DATE.getLabel(), occurrenceDate.format(DateTimeUtils.MM_DD_YYYY));
					}
					activityInformationList.add(activityInformationData);
					dsr = dsr - claimPoints;
				}
			}

			if (dsr > 0) {
				List<Integer> availableClaimPoints = ActivityInformation.getAvailableClaimPoints();
				if (availableClaimPoints.contains(dsr)) { // lucky guy :)
					ActivityInformation ai = ActivityInformation.ofPoints(dsr);
					Integer totalYearsAccidentsFree = ai.isAtFaultAccident() ? openLPolicy.getYearsAtFaultAccidentFree() : openLPolicy.getYearsIncidentFree();
					LocalDate latestIncidentDate = openLPolicy.getEffectiveDate();
					int maxIncidentFreeInMonth = maxIncidentFreeInMonthsToAffectRating;

					if (totalYearsAccidentsFree != null) {
						assertThat(totalYearsAccidentsFree * 12).as("yearsIncidentFree or yearsAtFaultAccidentFree argument in months should not be more than %s to affect rating", maxIncidentFreeInMonth)
								.isLessThanOrEqualTo(maxIncidentFreeInMonth);
						latestIncidentDate = latestIncidentDate.minusYears(totalYearsAccidentsFree);
						maxIncidentFreeInMonth = maxIncidentFreeInMonth - totalYearsAccidentsFree * 12;
					}
					activityInformationList.add(ai.getTestData(latestIncidentDate.minusDays(new Random().nextInt(maxIncidentFreeInMonth * 28))));
				} else {
					//TODO-dchubkov: to be implemented subset sum algorithm
					throw new NotImplementedException("Subset sum algorithm is not implemented for dsr openl field");
				}
			}

			if (openLPolicy.isLegacyConvPolicy()) {
				driverData.put(AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel(), getState());

				if (!activityInformationList.isEmpty()) {
					driverData.put(AutoSSMetaData.DriverTab.ACTIVITY_INFORMATION.getLabel(), activityInformationList);
					if (isCleanDriverRenewalActive(openLPolicy, driver.getDsr())) {
						driverData.put(AutoSSMetaData.DriverTab.CLEAN_DRIVER_RENEWAL.getLabel(), getYesOrNo(driver.isCleanDriver()));
						if (Boolean.TRUE.equals(driver.isCleanDriver())) {
							driverData.put(AutoSSMetaData.DriverTab.CLEAN_DRIVER_RENEWAL_REASON.getLabel(), "some clean driver renewal reason $<rx:\\d{3}>");
						}
					}
				}
			}

			if (setADBCoverage) {
				driverData.put(AutoSSMetaData.DriverTab.ADB_COVERAGE.getLabel(), "Yes");
				setADBCoverage = false;
			}

			driversTestDataList.add(new SimpleDataProvider(driverData));
			isFirstDriver = false;
		}

		//TODO-dchubkov: check isAtFaultAccidentFreeSet and isAccidentFreeSet is set
		return driversTestDataList;
	}

	private LocalDate getOccurrenceDate(LocalDate effectiveDate) {
		// Incident should be not older than 33 month from effective date to affect premium
		return effectiveDate.minusDays(new Random().nextInt(maxIncidentFreeInMonthsToAffectRating * 28));
	}

	private boolean isCleanDriverRenewalActive(AutoSSOpenLPolicy openLPolicy, Integer dsr) {
		int baseDateYear = openLPolicy.getEffectiveDate().minusYears(openLPolicy.getAaaInsurancePersistency()).getYear();
		int dsrPoints = dsr == null ? 0 : dsr;
		return getState().equals(Constants.States.MD) && openLPolicy.getEffectiveDate().getYear() - baseDateYear > 2 && dsrPoints < 2;
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
			int trailersCount = Math.toIntExact(openLPolicy.getVehicles().stream().filter(v -> isTrailerType(v.getBiLiabilitySymbol())).count());
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
				assertThat(vehicle.isTelematic()).as("\"isTelematic\" should be false if \"safetyScore\" is not null").isFalse();
				vehicleData.adjust(getVehicleTabVehicleDetailsData(String.valueOf(vehicle.getSafetyScore())));
			}

			vehiclesTestDataList.add(vehicleData);
		}

		return vehiclesTestDataList;
	}

	private TestData getAssignmentTabData(List<TestData> driversTestDataList) {
		if (driversTestDataList.size() == 1) {
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
					String[] firstLastName = driver.getName().split("\\s");
					String firstName = firstLastName[0];
					String lastName = firstLastName.length > 1 ? firstLastName[1] : firstName;
					unverifiableDrivingRecordSurchargeData.put(firstName + " " + lastName, driver.isUnverifiableDrivingRecord());
				}
			}
		}

		List<TestData> detailedVehicleCoveragesList = new ArrayList<>(openLPolicy.getVehicles().size());
		Map<String, Object> policyCoveragesData = new HashMap<>();
		Map<String, Object> detailedCoveragesData = new HashMap<>();
		for (AutoSSOpenLVehicle vehicle : openLPolicy.getVehicles()) {
			if (vehicle.getCoverages().stream().anyMatch(c -> isFirstPartyBenefitsComboCoverage(c.getCoverageCd()))) {
				policyCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.FIRST_PARTY_BENEFITS.getLabel(), "starts=Added");
			}

			if (vehicle.getCoverages().stream().anyMatch(c -> "EUIMBI".equals(c.getCoverageCd()) || "EUIMPD".equals(c.getCoverageCd()))) {
				policyCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.ENHANCED_UIM.getLabel(), true);
			}

			boolean isTrailerOrMotorHomeVehicle = isTrailerOrMotorHomeOrGolfCartType(vehicle.getUsage());
			for (AutoSSOpenLCoverage coverage : vehicle.getCoverages()) {
				if (getState().equals(Constants.States.NJ) && "PIP".equals(coverage.getCoverageCd())) {
					// for NJ state PIP coverage should be set by covering aaaPIPMedExpLimit field ("Medical Expense" on UI)
					continue;
				}

				if ("ADB".equals(coverage.getCoverageCd())) {
					// ADB coverage should be set in Driver tab - "ADB Coverage" radio button
					continue;
				}

				if (isTrailerOrMotorHomeVehicle && "SP EQUIP".equals(coverage.getCoverageCd())) {
					// tests for "Trailer" and "Motor Home" vehicle types sometimes have "SP EQUIP" coverage which is impossible to set via UI but it does not affect rating
					continue;
				}

				String coverageName = getPremiumAndCoveragesTabCoverageName(coverage.getCoverageCd());
				if (isPolicyLevelCoverageCd(coverage.getCoverageCd())) {
					policyCoveragesData.put(coverageName, getPremiumAndCoveragesTabLimitOrDeductible(coverage));
					if ("PIP".equals(coverage.getCoverageCd()) && (getState().equals(Constants.States.OR) || getState().equals(Constants.States.KY))) {
						policyCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION_DEDUCTIBLE.getLabel(),
								"starts=" + getFormattedCoverageLimit(coverage.getDeductible(), coverage.getCoverageCd()));
					}
				} else {
					detailedCoveragesData.put(coverageName, getPremiumAndCoveragesTabLimitOrDeductible(coverage));
				}

				if (isTrailerOrMotorHomeVehicle || getState().equals(Constants.States.KY)) {
					assertThat(coverage.getGlassDeductible()).as("Invalid \"glassDeductible\" openl field value since it's not possible to fill \"Full Safety Glass\" UI field "
							+ "for \"Trailer\" or \"Motor Home\" vehicle types or for KY state").isIn("N/A", "0");
				} else {
					if ("0".equals(vehicle.getCoverages().stream().filter(c -> "COMP".equals(c.getCoverageCd())).findFirst().get().getGlassDeductible()) ||
							"0".equals(vehicle.getCoverages().stream().filter(c -> "COLL".equals(c.getCoverageCd())).findFirst().get().getGlassDeductible())) {
						detailedCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.FULL_SAFETY_GLASS.getLabel(), "Yes");
					} else {
						detailedCoveragesData.put(AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.FULL_SAFETY_GLASS.getLabel(),
								getPremiumAndCoveragesFullSafetyGlass(coverage.getGlassDeductible()));
					}
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

		if (getState().equals(Constants.States.KY) && !policyCoveragesData.containsKey(getPremiumAndCoveragesTabCoverageName("APIP"))) {
			policyCoveragesData.put(getPremiumAndCoveragesTabCoverageName("APIP"), "starts=No Coverage");
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
				if (!isTrailerType(openLPolicy.getVehicles().get(i).getBiLiabilitySymbol())) {
					detailedVehicleCoveragesList.get(i).adjust(AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.UMPD_CDW.getLabel(), "starts=No Coverage");
				}
			}
		}

		Map<String, Object> premiumAndCoveragesTabData = new HashMap<>();
		premiumAndCoveragesTabData.put(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel(), getPremiumAndCoveragesPaymentPlan(openLPolicy.getPaymentPlanType(), openLPolicy.getCappingDetails().getTerm()));
		premiumAndCoveragesTabData.put(AutoSSMetaData.PremiumAndCoveragesTab.UNACCEPTABLE_RISK_SURCHARGE.getLabel(), openLPolicy.isUnacceptableRisk());
		premiumAndCoveragesTabData.put(AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS.getLabel(), "Yes"); //TODO-dchubkov: enable only if need to fill expanded section
		premiumAndCoveragesTabData.put(AutoSSMetaData.PremiumAndCoveragesTab.MULTI_CAR.getLabel(), openLPolicy.isMultiCar());
		premiumAndCoveragesTabData.put(AutoSSMetaData.PremiumAndCoveragesTab.UNVERIFIABLE_DRIVING_RECORD_SURCHARGE.getLabel(), new SimpleDataProvider(unverifiableDrivingRecordSurchargeData));
		premiumAndCoveragesTabData.put(AutoSSMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(), detailedVehicleCoveragesList);
		if (Boolean.TRUE.equals(openLPolicy.isEMember())) {
			premiumAndCoveragesTabData.put(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT.getLabel(), "Yes");
		}

		premiumAndCoveragesTabData.putAll(policyCoveragesData);

		if (getState().equals(Constants.States.NJ)) {
			premiumAndCoveragesTabData.put(AutoSSMetaData.PremiumAndCoveragesTab.POLICY_LEVEL_PERSONAL_INJURY_PROTECTION_COVERAGES.getLabel(),
					getPolicyPersonalInjuryProtectionCoveragesData(openLPolicy));
		}

		return new SimpleDataProvider(premiumAndCoveragesTabData);
	}

	private TestData getVehicleTabInformationData(AutoSSOpenLVehicle vehicle) {
		String vin = getVinFromDb(vehicle);
		Map<String, Object> vehicleInformation = new HashMap<>();
		String statCode = vehicle.getBiLiabilitySymbol();
		vehicleInformation.put(AutoSSMetaData.VehicleTab.TYPE.getLabel(), getVehicleTabType(statCode));

		if (StringUtils.isNotBlank(vin)) {
			//vehicleInformation.put(AutoSSMetaData.VehicleTab.VIN.getLabel(), covertToValidVin(vin));
			vehicleInformation.put(AutoSSMetaData.VehicleTab.VIN.getLabel(), covertToValidVin(vin));
		} else {
			vehicleInformation.put(AutoSSMetaData.VehicleTab.YEAR.getLabel(), vehicle.getModelYear());
			vehicleInformation.put(AutoSSMetaData.VehicleTab.STATED_AMOUNT.getLabel(), vehicle.getCollSymbol() * 1000);
			if (getState().equals(Constants.States.NY) && Boolean.TRUE.equals(vehicle.isABS())) {
				assertThat(isTrailerType(statCode)).as("isABS=TRUE is valid only for non-Trailer vehicles types ").isFalse();
				vehicleInformation.put(AutoSSMetaData.VehicleTab.DAYTIME_RUNNING_LAMPS.getLabel(), "Yes");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.ANTI_LOCK_BRAKES.getLabel(), "Yes");
			}
			if (isTrailerOrMotorHomeOrGolfCartType(vehicle.getUsage())) {
				vehicleInformation.put(AutoSSMetaData.VehicleTab.PRIMARY_OPERATOR.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_EMPTY);
				vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel(), "some other make $<rx:\\d{3}>");
				vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel(), "some other model $<rx:\\d{3}>");

				if (isTrailerType(statCode)) {
					String trailerType = getVehicleTabTrailerType(statCode);
					vehicleInformation.put(AutoSSMetaData.VehicleTab.TRAILER_TYPE.getLabel(), trailerType);
					if ("Travel Trailer".equals(trailerType)) {
						vehicleInformation.put(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel(), "contains=" + getVehicleTabStatCode(statCode));
					}
				} else if (isMotorHomeType(statCode)) {
					vehicleInformation.put(AutoSSMetaData.VehicleTab.MOTOR_HOME_TYPE.getLabel(), getVehicleTabMotorHomeType(statCode));
				}
			} else {
				if (vehicle.getModelYear() > 1980) {
					vehicleInformation.put(AutoSSMetaData.VehicleTab.MAKE.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|OTHER");
					vehicleInformation.put(AutoSSMetaData.VehicleTab.MODEL.getLabel(), AdvancedComboBox.RANDOM_EXCEPT_MARK + "=|OTHER");
					vehicleInformation.put(AutoSSMetaData.VehicleTab.BODY_STYLE.getLabel(), "regex=.*\\S.*");
				} else {
					vehicleInformation.put(AutoSSMetaData.VehicleTab.MAKE.getLabel(), "OTHER");
					vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_MAKE.getLabel(), "some other make $<rx:\\d{3}>");
					vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_MODEL.getLabel(), "some other model $<rx:\\d{3}>");
					vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel(), AdvancedComboBox.RANDOM_MARK);
				}

				vehicleInformation.put(AutoSSMetaData.VehicleTab.AIR_BAGS.getLabel(), getVehicleTabAirBags(vehicle.getAirbagCode()));
				vehicleInformation.put(AutoSSMetaData.VehicleTab.ANTI_THEFT.getLabel(), getVehicleTabAntiTheft(vehicle.getAntiTheftString()));
				vehicleInformation.put(AutoSSMetaData.VehicleTab.STAT_CODE.getLabel(), "contains=" + getVehicleTabStatCode(statCode));
				vehicleInformation.put(AutoSSMetaData.VehicleTab.OTHER_BODY_STYLE.getLabel(), getOtherBodyStyle(statCode));
			}
		}

		int streetNumber = RandomUtils.nextInt(100, 1000);
		String streetName = RandomStringUtils.randomAlphabetic(10).toUpperCase() + " St";
		vehicleInformation.put(AutoSSMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel(), "Yes");

		String zipCode = vehicle.getAddress().getZip();
		if (getState().equals(Constants.States.CT)) {
			String getZipAndCountyQuery = "select POSTALCODE, TOWNSHIP from LOOKUPVALUE where CODE = ? and LOOKUPLIST_ID in (select ID from LOOKUPLIST where LOOKUPNAME = 'AAACountyTownship') and RISKSTATECD = ?";
			Map<String, String> zipAndCounty = DBService.get().getRow(getZipAndCountyQuery, zipCode, getState());
			zipCode = zipAndCounty.get("POSTALCODE");
			vehicleInformation.put(AutoSSMetaData.VehicleTab.COUNTY_TOWNSHIP.getLabel(), "contains=" + zipAndCounty.get("TOWNSHIP"));
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

	private String getVinFromDb(AutoSSOpenLVehicle vehicle) {
		String vin = "";

		// 85 is default value for PHYSICALDAMAGECOLLISION and PHYSICALDAMAGECOMPREHENSIVE if there are no vehicles in DB with valid parameters
		if (vehicle.getCollSymbol() != 85 && vehicle.getCompSymbol() != 85) {
			String getVinQuery = String.format("select VIN from VEHICLEREFDATAVIN inner join VEHICLEREFDATAMODEL \n"
							+ "on VEHICLEREFDATAVIN.VEHICLEREFDATAMODELID = VEHICLEREFDATAMODEL.ID \n"
							+ "where PHYSICALDAMAGECOLLISION %1$s and PHYSICALDAMAGECOMPREHENSIVE %2$s and YEAR %3$s \n"
							+ "and BI_SYMBOL %4$s and PD_SYMBOL %5$s and MP_SYMBOL %6$s and UM_SYMBOL %7$s \n"
							+ "and (RESTRAINTSCODE %8$s) AND (ANTITHEFTCODE %9$s)",
					vehicle.getCollSymbol() == null ? "is null" : "= " + vehicle.getCollSymbol(),
					vehicle.getCompSymbol() == null ? "is null" : "= " + vehicle.getCompSymbol(),
					vehicle.getModelYear() == null ? "is null" : "= " + vehicle.getModelYear(),
					vehicle.getBiLiabilitySymbol() == null ? "is null" : "= '" + vehicle.getBiLiabilitySymbol() + "'",
					vehicle.getPdLiabilitySymbol() == null ? "is null" : "= '" + vehicle.getPdLiabilitySymbol() + "'",
					vehicle.getMpLiabilitySymbol() == null ? "is null" : "= '" + vehicle.getMpLiabilitySymbol() + "'",
					vehicle.getUmLiabilitySymbol() == null ? "is null" : "= '" + vehicle.getUmLiabilitySymbol() + "'",
					vehicle.getAirbagCode() == null ? "is null" : getDbRestraintsCode(vehicle.getAirbagCode()),
					vehicle.getAntiTheftString() == null ? "is null" : "= 'NONE' OR ANTITHEFTCODE = 'STD' OR ANTITHEFTCODE = 'UNK'");

			vin = DBService.get().getValue(getVinQuery).orElse(null);
		}
		return vin;
	}

	private TestData getPolicyPersonalInjuryProtectionCoveragesData(AutoSSOpenLPolicy openLPolicy) {
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
			Dollar limit = openLPolicy.getAaaPIPExtMedPayLimit() == 0 || openLPolicy.getAaaPIPExtMedPayLimit() == 1 ? new Dollar(1000) : new Dollar(10000);
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

		return new SimpleDataProvider(td);
	}

	private String getOtherBodyStyle(String statCode) {
		List<String> otherBodyStyleStates = Arrays.asList(Constants.States.CO, Constants.States.DE, Constants.States.WY, Constants.States.OR, Constants.States.IN);
		return !isConversionVanType(statCode) || otherBodyStyleStates.contains(getState()) ? AdvancedComboBox.RANDOM_MARK : null;
	}

	static class ActivityInformation {
		private String type;
		private String description;
		private boolean isAtFaultAccident;
		private int points;
		private static List<ActivityInformation> activityInformationList;

		private ActivityInformation(String type, String description, boolean isAtFaultAccident, int points) {
			this.type = type;
			this.description = description;
			this.isAtFaultAccident = isAtFaultAccident;
			this.points = points;
		}

		public static ActivityInformation of(String type) {
			return getActivityInformationList().stream().filter(a -> a.getType().equals(type)).findFirst().orElseThrow(() -> new IstfException("Unknown activity information type: " + type));
		}

		public static ActivityInformation of(String type, String description) {
			return getActivityInformationList().stream().filter(a -> a.getType().equals(type) && a.getDescription().equals(description)).findFirst()
					.orElseThrow(() -> new IstfException(String.format("Unknown activity information with type \"%1$s\" and description \"%2$s\"", type, description)));
		}

		public static ActivityInformation ofPoints(int points) {
			return getActivityInformationList().stream().filter(a -> a.getPoints() == points).findFirst()
					.orElseThrow(() -> new IstfException(String.format("There is no activity information with %s claim points", points)));
		}

		public static ActivityInformation ofMinimumPoints(String... types) {
			ActivityInformation ai = null;
			for (String type : types) {
				ActivityInformation aiCandidate = getActivityInformationList().stream().filter(a -> a.getType().equals(type)).min(Comparator.comparing(ActivityInformation::getPoints))
						.orElseThrow(() -> new IstfException("Unknown activity information type: " + type));
				if (ai == null || aiCandidate.getPoints() < ai.getPoints()) {
					ai = aiCandidate;
				}
			}
			return ai;
		}

		public static ActivityInformation getNotAtFaultAccident(int points) {
			return getNotAtFaultAccidents().stream().filter(a -> a.getPoints() == points).findFirst()
					.orElseThrow(() -> new IstfException(String.format("There is no Not At-Fault Accidents with %s claim points", points)));
		}

		public static List<ActivityInformation> getAtFaultAccidents() {
			return getActivityInformationList().stream().filter(ActivityInformation::isAtFaultAccident).collect(Collectors.toList());
		}

		public static List<ActivityInformation> getNotAtFaultAccidents() {
			return getActivityInformationList().stream().filter(a -> !a.isAtFaultAccident).collect(Collectors.toList());
		}

		public static List<Integer> getAvailableClaimPoints() {
			return getActivityInformationList().stream().map(ActivityInformation::getPoints).distinct().collect(Collectors.toList());
		}

		public String getType() {
			return type;
		}

		public String getDescription() {
			return description;
		}

		public boolean isAtFaultAccident() {
			return isAtFaultAccident;
		}

		public int getPoints() {
			return points;
		}

		public int getPoints(Integer totalYearsAccidentsFree) {
			if (totalYearsAccidentsFree != null && totalYearsAccidentsFree * 12 > maxIncidentFreeInMonthsToAffectRating) {
				return 0;
			}
			return getPoints();
		}

		public TestData getTestData(LocalDate occurrenceDate) {
			Integer lossPaymentAmount = isAtFaultAccident() ? RandomUtils.nextInt(1001, 10000) : null;
			return getTestData(lossPaymentAmount, occurrenceDate);
		}

		public TestData getTestData(Integer lossPaymentAmount, LocalDate occurrenceDate) {
			Map<String, Object> activityInformationData = new HashMap<>();
			activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.TYPE.getLabel(), getType());
			if (lossPaymentAmount != null) {
				activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.LOSS_PAYMENT_AMOUNT.getLabel(), lossPaymentAmount);
			}
			activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.DESCRIPTION.getLabel(), getDescription());
			activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.OCCURENCE_DATE.getLabel(), occurrenceDate.format(DateTimeUtils.MM_DD_YYYY));
			//activityInformationData.put(AutoSSMetaData.DriverTab.ActivityInformation.INCLUDE_IN_POINTS_AND_OR_TIER.getLabel(), "Yes");
			return new SimpleDataProvider(activityInformationData);
		}

		private static synchronized List<ActivityInformation> getActivityInformationList() {
			if (activityInformationList == null) {
				activityInformationList = new ArrayList<>();
				activityInformationList.add(new ActivityInformation("Speeding Violation", "Speeding", false, 0));
				activityInformationList.add(new ActivityInformation("Comprehensive Claim", "Comprehensive Claim", false, 0));
				activityInformationList.add(new ActivityInformation("Minor Violation", "Disregard Police", false, 0));
				activityInformationList.add(new ActivityInformation("Non-Moving Violation", "Commercial Vehicle Violations", false, 0));
				activityInformationList.add(new ActivityInformation("Major Violation", "Drag Racing or Speed Contest", false, 4));
				activityInformationList.add(new ActivityInformation("Alcohol-Related Violation", "Driving Under the Influence of Alcohol", false, 4));
				activityInformationList.add(new ActivityInformation("At-Fault Accident", "Accident (Property Damage Only)", true, 6));
				activityInformationList.add(new ActivityInformation("Principally At-Fault Accident", "Principally At-Fault Accident (Property Damage Only)", true, 6));
				activityInformationList.add(new ActivityInformation("At-Fault Accident", "Accident (Resulting in Bodily Injury)", true, 7));
				activityInformationList.add(new ActivityInformation("Principally At-Fault Accident", "Principally At-Fault Accident (Resulting in Bodily Injury)", true, 7));
			}
			return activityInformationList;
		}
	}
}
