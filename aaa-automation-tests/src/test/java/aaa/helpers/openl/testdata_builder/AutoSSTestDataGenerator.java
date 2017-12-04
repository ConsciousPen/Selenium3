package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.exigen.ipb.etcsa.utils.Dollar;
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
	private Random random = new Random();

	@Override
	public TestData getRatingData(AutoSSOpenLPolicy openLPolicy) {
		TestData td = getRatingDataPattern();
		td.adjust(DataProviderFactory.dataOf(
				new PrefillTab().getMetaKey(), getPrefillTabData(),
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				new RatingDetailReportsTab().getMetaKey(), getRatingDetailReportsTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new FormsTab().getMetaKey(), getFormsTabTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy)));
		return td;
	}

	private TestData getPrefillTabData() {
		return DataProviderFactory.dataOf(
				AutoSSMetaData.PrefillTab.VALIDATE_ADDRESS_BTN.getLabel(), "click",
				AutoSSMetaData.PrefillTab.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.emptyData(),
				AutoSSMetaData.PrefillTab.ORDER_PREFILL.getLabel(), "click");
	}

	private TestData getGeneralTabData(AutoSSOpenLPolicy openLPolicy) {
		TestData namedInsuredInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE.getLabel(), getGeneralTabResidence(openLPolicy.isHomeOwner())
				/* to be continued */);

		TestData aAAProductOwnedData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel(), getYesOrNo(openLPolicy.isHomeOwner()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.HOME.getLabel(), openLPolicy.getAaaHomePolicy(),
				AutoSSMetaData.GeneralTab.AAAProductOwned.RENTERS.getLabel(), getYesOrNo(openLPolicy.getAaaRentersPolicy()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.CONDO.getLabel(), getYesOrNo(openLPolicy.getAaaCondoPolicy()),
				AutoSSMetaData.GeneralTab.AAAProductOwned.LIFE.getLabel(), openLPolicy.isAaaLifePolicy(),
				AutoSSMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE.getLabel(), openLPolicy.isAaaMotorcyclePolicy()
		);

		TestData contactInformationData = DataProviderFactory.dataOf(/* to be done */);

		TestData currentCarrierInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER.getLabel(), "Yes",
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS.getLabel(), getGeneralTabPriorBILimit(openLPolicy.getPriorBILimit()),
				AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_INCEPTION_DATE.getLabel(), openLPolicy.getCappingDetails().get(0).getPlcyInceptionDate().format(DateTimeUtils.MM_DD_YYYY));
		/* to be continued */

		TestData policyInformationData = DataProviderFactory.dataOf(
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM.getLabel(), getGeneralTabTerm(openLPolicy.getTerm()),
				AutoSSMetaData.GeneralTab.PolicyInformation.ADVANCED_SHOPPING_DISCOUNTS.getLabel(), generalTabIsAdvanceShopping(openLPolicy.isAdvanceShopping())
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
		for (OpenLDriver driver : openLPolicy.getDrivers()) {
			TestData driverData = DataProviderFactory.dataOf(
					AutoSSMetaData.DriverTab.FIRST_NAME.getLabel(), driver.getName(),
					AutoSSMetaData.DriverTab.LAST_NAME.getLabel(), driver.getName(),
					AutoSSMetaData.DriverTab.GENDER.getLabel(), getDriverTabGender(driver.getGender()),
					AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel(), getDriverTabMartialStatus(driver.getMaritalStatus()),
					AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel(), TimeSetterUtil.getInstance().getCurrentTime().minusYears(driver.getDriverAge()).format(DateTimeUtils.MM_DD_YYYY),
					AutoSSMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel(), driver.getDriverAge() - driver.getTyde(),
					AutoSSMetaData.DriverTab.SMART_DRIVER_COURSE_COMPLETED.getLabel(), driver.isSmartDriver(),
					AutoSSMetaData.DriverTab.DISTANT_STUDENT.getLabel(), driver.isDistantStudent(),
					AutoSSMetaData.DriverTab.LICENSE_TYPE.getLabel(), getDriverGetLicenseType(driver.isForeignLicense())
					/* to be done */
			);
			if (isFirstDriver) {
				driverData.adjust(AutoSSMetaData.DriverTab.DRIVER_SEARCH_DIALOG.getLabel(), DataProviderFactory.emptyData());
				isFirstDriver = false;
			}
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
				AutoSSMetaData.PremiumAndCoveragesTab.ADDITIONAL_SAVINGS_OPTIONS.getLabel(), "Yes",
				AutoSSMetaData.PremiumAndCoveragesTab.MULTI_CAR.getLabel(), openLPolicy.isMultiCar(),
				AutoSSMetaData.PremiumAndCoveragesTab.UNVERIFIABLE_DRIVING_RECORD_SURCHARGE.getLabel(), new SimpleDataProvider(unverifiableDrivingRecordSurchargeData),
				AutoSSMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel(), detailedVehicleCoveragesList);
	}

	private String getGeneralTabResidence(boolean isHomeOwner) {
		List<String> isHomeOwnerOptions = Arrays.asList("Own Home", "Own Condo", "Own Mobile Home");
		List<String> notHomeOwnerOptions = Arrays.asList("Rents Multi-Family Dwelling", "Rents Single-Family Dwelling", "Lives with Parent", "Other");
		if (isHomeOwner) {
			return isHomeOwnerOptions.get(random.nextInt(isHomeOwnerOptions.size()));
		}
		return notHomeOwnerOptions.get(random.nextInt(notHomeOwnerOptions.size()));
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
		List<String> pbLimitFr = Arrays.asList(
				getRangedDollarValue(15_000, 30_000),
				getRangedDollarValue(20_000, 40_000),
				getRangedDollarValue(25_000, 50_000),
				getRangedDollarValue(30_000, 60_000));

		List<String> pbLimit50xx = Arrays.asList(new Dollar(50_000) + "/" + new Dollar(100_000));
		List<String> pbLimit100xx = Arrays.asList(new Dollar(100_000) + "/" + new Dollar(300_000));

		List<String> pbLimit200xx = Arrays.asList(
				new Dollar(250_000) + "/" + new Dollar(500_000),
				new Dollar(300_000) + "/" + new Dollar(500_000));

		List<String> pbLimit500xx = Arrays.asList(
				new Dollar(500_000) + "/" + new Dollar(500_000),
				new Dollar(500_000) + "/" + new Dollar(1_000_000),
				new Dollar(1_000_000) + "/" + new Dollar(1_000_000));

		switch (priorBILimit) {
			case "N":
				return "None";
			case "FR":
				return pbLimitFr.get(random.nextInt(pbLimitFr.size()));
			case "50/XX":
				return pbLimit50xx.get(random.nextInt(pbLimit50xx.size()));
			case "100/XX":
				return pbLimit100xx.get(random.nextInt(pbLimit100xx.size()));
			case "200/XX":
				return pbLimit200xx.get(random.nextInt(pbLimit200xx.size()));
			case "500/XX":
				return pbLimit500xx.get(random.nextInt(pbLimit500xx.size()));
			default:
				throw new IstfException("Unknown mapping for priorBILimit = " + priorBILimit);
		}
	}
}
