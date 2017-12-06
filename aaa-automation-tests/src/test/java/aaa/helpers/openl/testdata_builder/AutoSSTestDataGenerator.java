package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.NotImplementedException;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
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
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoSSTestDataGenerator extends AutoTestDataGenerator<AutoSSOpenLPolicy> {

	@Override
	public TestData getRatingData(AutoSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.dataOf(
				new PrefillTab().getMetaKey(), getPrefillTabData(),
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				new RatingDetailReportsTab().getMetaKey(), getRatingDetailReportsTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new FormsTab().getMetaKey(), getFormsTabTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));
	}

	private TestData getPrefillTabData() {
		return getRatingDataPattern().getTestData(new PrefillTab().getMetaKey());
	}

	private TestData getGeneralTabData(AutoSSOpenLPolicy openLPolicy) {
		String hasLivedHereForLessThanThreeYearsValue = getRatingDataPattern().getTestDataList(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()).get(0)
				.getValue(AutoSSMetaData.GeneralTab.NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS.getLabel());

		TestData namedInsuredInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getLabel(), getGeneralTabResidence(openLPolicy.isHomeOwner()),
				AutoSSMetaData.GeneralTab.NamedInsuredInformation.HAS_LIVED_LESS_THAN_3_YEARS.getLabel(), hasLivedHereForLessThanThreeYearsValue
				/* to be continued */);

		TestData aAAProductOwnedData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), getYesOrNo(openLPolicy.isAAAMember()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.HOME.getLabel(), getYesOrNo(openLPolicy.getAaaHomePolicy()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.RENTERS.getLabel(), getYesOrNo(openLPolicy.getAaaRentersPolicy()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.CONDO.getLabel(), getYesOrNo(openLPolicy.getAaaCondoPolicy()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.LIFE.getLabel(), getYesOrNo(openLPolicy.isAaaLifePolicy())
				//TODO: exclude for RO state: AutoSSMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE.getLabel(), openLPolicy.isAaaMotorcyclePolicy()
		);
		if (openLPolicy.isAAAMember()) {
			aAAProductOwnedData.adjust(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel(), getRatingDataPattern().getValue(
					new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel()));
			aAAProductOwnedData.adjust(AutoSSMetaData.GeneralTab.AAAProductOwned.LAST_NAME.getLabel(), getRatingDataPattern().getValue(
					new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoSSMetaData.GeneralTab.AAAProductOwned.LAST_NAME.getLabel()));
		}

		TestData contactInformationData = getRatingDataPattern().getTestData(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.CONTACT_INFORMATION.getLabel());

		TestData currentCarrierInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER.getLabel(), "Yes",
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel(), getGeneralTabPriorBILimit(openLPolicy.getPriorBILimit()),
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel(), openLPolicy.getCappingDetails().get(0).getPlcyInceptionDate()
						.format(DateTimeUtils.MM_DD_YYYY),
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER.getLabel(), getRatingDataPattern().getValue(new GeneralTab().getMetaKey(),
						AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_CURRENT_PRIOR_CARRIER.getLabel()),
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel(), getRatingDataPattern().getValue(new GeneralTab().getMetaKey(),
						AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel()));

		/* to be continued */

		TestData policyInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TYPE.getLabel(), getRatingDataPattern().getValue(new GeneralTab().getMetaKey(),
						AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TYPE.getLabel()),
				AutoSSMetaData.GeneralTab.PolicyInformation.CHANNEL_TYPE.getLabel(), getRatingDataPattern().getValue(new GeneralTab().getMetaKey(),
						AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.CHANNEL_TYPE.getLabel()),
				AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY.getLabel(), getRatingDataPattern().getValue(new GeneralTab().getMetaKey(),
						AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY.getLabel()),
				AutoSSMetaData.GeneralTab.PolicyInformation.SALES_CHANNEL.getLabel(), getRatingDataPattern().getValue(new GeneralTab().getMetaKey(),
						AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.SALES_CHANNEL.getLabel()),
				AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY_LOCATION.getLabel(), getRatingDataPattern().getValue(new GeneralTab().getMetaKey(),
						AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY_LOCATION.getLabel()),
				AutoSSMetaData.GeneralTab.PolicyInformation.AGENT.getLabel(), getRatingDataPattern().getValue(new GeneralTab().getMetaKey(),
						AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.AGENT.getLabel()),
				AutoSSMetaData.GeneralTab.PolicyInformation.LEAD_SOURCE.getLabel(), getRatingDataPattern().getValue(new GeneralTab().getMetaKey(),
						AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.LEAD_SOURCE.getLabel()),

				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), getGeneralTabTerm(openLPolicy.getTerm())
				//TODO: exclude for RO state: AutoSSMetaData.GeneralTab.PolicyInformation.ADVANCED_SHOPPING_DISCOUNTS.getLabel(), generalTabIsAdvanceShopping(openLPolicy.isAdvanceShopping())
				/* to be continued */);

		return DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), namedInsuredInformationData,
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
			TestData driverData = DataProviderFactory.dataOf(
					AutoSSMetaData.DriverTab.GENDER.getLabel(), getDriverTabGender(driver.getGender()),
					AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), getDriverTabMartialStatus(driver.getMaritalStatus()),
					AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel(), TimeSetterUtil.getInstance().getCurrentTime().minusYears(driver.getDriverAge()).format(DateTimeUtils.MM_DD_YYYY),
					AutoSSMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel(), driver.getDriverAge() - driver.getTyde(),
					AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), getDriverTabLicenseType(driver.isForeignLicense()),
					AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "None"
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
				driverData.adjust(AutoSSMetaData.DriverTab.NAMED_INSURED.getLabel(), "contains=" + driver.getName());
				driverData.adjust(AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel(), "AAA Employee");
				isAffinityGroupSet = true;
			}

			if (!isFirstDriver) {
				driverData.adjust(AutoSSMetaData.DriverTab.FIRST_NAME.getLabel(), driver.getName())
						.adjust(AutoSSMetaData.DriverTab.LAST_NAME.getLabel(), driver.getName());
				driverData.adjust(AutoSSMetaData.DriverTab.DRIVER_SEARCH_DIALOG.getLabel(), DataProviderFactory.emptyData());
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

		TestData ratingDetailReportsTabData = DataProviderFactory.dataOf(AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel(), insuranceScoreOverrideData);
		ratingDetailReportsTabData.adjust(getRatingDataPattern().getTestData(new RatingDetailReportsTab().getMetaKey()));
		return ratingDetailReportsTabData;
	}

	private List<TestData> getVehicleTabData(AutoSSOpenLPolicy openLPolicy) {
		//TODO-dchubkov: to be implemented
		List<TestData> vehiclesTestData = new ArrayList<>(openLPolicy.getVehicles().size());
		for (OpenLVehicle vehicle : openLPolicy.getVehicles()) {
			if (vehicle.isHybrid()) {
				//TODO-dchubkov: to be implemented but at the moment don't have openL files with this option enabled and impossible to set via UI
				throw new NotImplementedException("Test data generation for enabled isHybrid is not implemented.");
			}

			TestData vehicleData = DataProviderFactory.dataOf(
					AutoSSMetaData.VehicleTab.YEAR.getLabel(), vehicle.getModelYear(),
					AutoSSMetaData.VehicleTab.USAGE.getLabel(), getVehicleTabUsage(vehicle.getUsage()),
					AutoSSMetaData.VehicleTab.ANTI_THEFT.getLabel(), getVehicleTabAntiTheft(vehicle.getAntiTheftString()),
					AutoSSMetaData.VehicleTab.AIR_BAGS.getLabel(), getVehicleTabAirBags(vehicle.getAirbagCode()),
					AutoSSMetaData.VehicleTab.STAT_CODE.getLabel(), "contains=" + vehicle.getStatCode(),
					AutoSSMetaData.VehicleTab.IS_GARAGING_DIFFERENT_FROM_RESIDENTAL.getLabel(), true,
					AutoSSMetaData.VehicleTab.ZIP_CODE.getLabel(), vehicle.getAddress().get(0).getZip(),
					AutoSSMetaData.VehicleTab.ADDRESS_LINE_1.getLabel(), "5800 NE CENTER COMMONS WAY",
					AutoSSMetaData.VehicleTab.STATE.getLabel(), vehicle.getAddress().get(0).getState());

			/* to be done */
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

		for (OpenLDriver driver : openLPolicy.getDrivers()) {
			unverifiableDrivingRecordSurchargeData.put(driver.getName(), driver.isUnverifiableDrivingRecord());
		}

		List<TestData> detailedVehicleCoveragesList = new ArrayList<>(openLPolicy.getVehicles().size());
		for (int i = 1; i <= openLPolicy.getVehicles().size(); i++) {
			int vehicleNumber = i;
			OpenLVehicle vehicle = openLPolicy.getVehicles().stream().filter(v -> v.getNumber() == vehicleNumber).findFirst().get();
			for (OpenLCoverage coverage : vehicle.getCoverages()) {
				Map<String, Object> coverageData = new HashMap<>();
				coverageData.put(getPremiumAndCoveragesTabCoverageKey(coverage.getCoverageCD()), getPremiumAndCoveragesTabCoverageLimit(coverage.getLimit()));
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

	private String getGeneralTabResidence(boolean isHomeOwner) {
		if (isHomeOwner) {
			return getRandom("Own Home", "Own Condo", "Own Mobile Home");
		}
		return getRandom("Rents Multi-Family Dwelling", "Rents Single-Family Dwelling", "Lives with Parent", "Other");
	}

	private String getGeneralTabTerm(int term) {
		switch (term) {
			case 12:
				return "Annual";
			case 6:
				return "Semi-Annual";
			default:
				throw new IstfException("Unable to build test data. Unsupported openL policy term: " + term);
		}
	}

	private String generalTabIsAdvanceShopping(boolean isAdvanceShopping) {
		if (isAdvanceShopping) {
			throw new IstfException("Unknown mapping for isAdvanceShopping = true");
		}
		return "No Discount";
	}

	private String getGeneralTabPriorBILimit(String priorBILimit) {
		switch (priorBILimit) {
			case "N":
				return "None";
			case "FR":
				return getRandom(getRangedDollarValue(15_000, 30_000), getRangedDollarValue(20_000, 40_000), getRangedDollarValue(25_000, 50_000), getRangedDollarValue(30_000, 60_000));
			case "50/XX":
				return getRangedDollarValue(50_000, 100_000);
			case "100/XX":
				return getRangedDollarValue(100_000, 300_000);
			case "200/XX":
				return getRandom(getRangedDollarValue(250_000, 500_000), getRangedDollarValue(300_000, 500_000));
			case "500/XX":
				return getRandom(getRangedDollarValue(500_000, 500_000), getRangedDollarValue(500_000, 1_000_000), getRangedDollarValue(1_000_000, 1_000_000));
			default:
				throw new IstfException("Unknown mapping for priorBILimit = " + priorBILimit);
		}
	}
}
