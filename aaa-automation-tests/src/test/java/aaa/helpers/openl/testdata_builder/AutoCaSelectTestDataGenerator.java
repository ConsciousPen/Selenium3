package aaa.helpers.openl.testdata_builder;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLPolicy;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLVehicle;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.toolkit.webdriver.customcontrols.DetailedVehicleCoveragesRepeatAssetList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoCaSelectTestDataGenerator extends AutoCaTestDataGenerator<AutoCaSelectOpenLPolicy> {

	public AutoCaSelectTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoCaSelectOpenLPolicy openLPolicy) {
		TestData ratingDataPattern = getRatingDataPattern().resolveLinks();
		if (Boolean.FALSE.equals(openLPolicy.isAaaMember())) {
			ratingDataPattern
					.mask(new GeneralTab().getMetaKey(), AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoCaMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER.getLabel())
					.mask(new GeneralTab().getMetaKey(), AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER.getLabel())
					.mask(new GeneralTab().getMetaKey(), AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), AutoCaMetaData.GeneralTab.AAAProductOwned.MEMBER_LAST_NAME.getLabel());
		}

		TestData td = DataProviderFactory.dataOf(
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new DriverTab().getMetaKey(), getDriverTabData(openLPolicy),
				new VehicleTab().getMetaKey(), getVehicleTabData(openLPolicy),
				new AssignmentTab().getMetaKey(), getAssignmentTabData(openLPolicy),
				new PremiumAndCoveragesTab().getMetaKey(), getPremiumAndCoveragesTabData(openLPolicy));
		return TestDataHelper.merge(ratingDataPattern, td);
	}

	@Override
	protected TestData getVehicleTabInformationData(OpenLVehicle vehicle) {
		TestData vehicleInformation = super.getVehicleTabInformationData(vehicle);
		String usage = getRandom("Commute (to/from work and school)", "Farm non-business(on premises)", "Business (small business non-commercial)", "Pleasure (recreational driving only)");
		vehicleInformation.adjust(AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), usage);
		if ("Business (small business non-commercial)".equals(usage)) {
			vehicleInformation
					.adjust(AutoCaMetaData.VehicleTab.IS_THE_VEHICLE_USED_IN_ANY_COMMERCIAL_BUSINESS_OPERATIONS.getLabel(), "Yes")
					.adjust(AutoCaMetaData.VehicleTab.BUSINESS_USE_DESCRIPTION.getLabel(), "some business use description $<rx:\\d{3}>");
		}
		return vehicleInformation;
	}

	@Override
	protected TestData getPremiumAndCoveragesTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		TestData td = super.getPremiumAndCoveragesTabData(openLPolicy);
		td.adjust(AutoCaMetaData.PremiumAndCoveragesTab.MULTI_CAR.getLabel(), String.valueOf(openLPolicy.isMultiCar()));

		for (int i = 0; i < openLPolicy.getVehicles().size(); i++) {
			AutoCaSelectOpenLVehicle vehicle = openLPolicy.getVehicles().get(i);

			if (Boolean.TRUE.equals(vehicle.getApplyFixedExpense())) {
				assertThat(vehicle.getCoverages().stream().anyMatch(c -> "BI".equals(c.getCoverageCd()) && StringUtils.isNotBlank(c.getLimit())))
						.as("If vehicle's openl field applyFixedExpense=TRUE then it should have at least one non-empty BI coverage").isTrue();

				//TODO-dchubkov: Vehicles coverages test data should be ordered same as appropriate vehicles. Think about how to generate test data without sort order dependency
				td.getTestDataList(AutoCaMetaData.PremiumAndCoveragesTab.DETAILED_VEHICLE_COVERAGES.getLabel()).get(i)
						.adjust(DetailedVehicleCoveragesRepeatAssetList.WAIVE_LIABILITY, "Yes");
			}
		}
		return td;
	}

	@Override
	boolean isMotorHomeType(String statCode) {
		return "M".equals(statCode);
	}

	@Override
	boolean isTrailerType(String statCode) {
		return "T".equals(statCode);
	}

	@Override
	protected String getVehicleTabType(OpenLVehicle vehicle) {
		String statCode = vehicle.getStatCode() != null ? vehicle.getStatCode() : vehicle.getBiLiabilitySymbol();
		if (isRegularType(statCode)) {
			return "Regular";
		}
		if (isMotorHomeType(statCode)) {
			return "Motor Home";
		}
		if (isAntiqueClassicType(statCode)) {
			return "Antique / Classic";
		}
		if (isTrailerType(statCode)) {
			return "Trailer";
		}
		if (isCamperType(statCode)) {
			return "Camper";
		}
		throw new IstfException("Unknown vehicle type for statCode: " + statCode);
	}

	@Override
	protected String getVehicleTabStatCode(String statCode, int modelYear) {
		Map<String, String> statCodesMap = new HashMap<>();
		statCodesMap.put("A", "Antique vehicle");
		statCodesMap.put("C", "SUV Small"); // for 1990+ model years
		statCodesMap.put("D", "High Exposure car Midsize"); // for 1990+ model years
		statCodesMap.put("E", "SUV Large"); // for 1990+ model years
		statCodesMap.put("H", "High Exposure car"); // for 1989 or prior model years
		statCodesMap.put("I", "Passenger Car Midsize"); // for 1990+ model years
		statCodesMap.put("J", "High Exposure car Large"); // for 1990+ model years
		statCodesMap.put("K", "SUV Midsize"); // for 1990+ model years
		statCodesMap.put("L", "Low Speed Vehicle");
		statCodesMap.put("M", "Motorhome");
		statCodesMap.put("N", "Passenger Car Small"); // for 1990+ model years
		statCodesMap.put("O", "Pickup/ Utility Truck Standard"); // for 1990+ model years
		statCodesMap.put("P", "Passenger car, SUV, Station wagon, Passenger Van"); // for 1989 or prior model years
		statCodesMap.put("Q", "Passenger Car Large"); // for 1990+ model years
		statCodesMap.put("R", "Pickup/ Utility Truck Small"); // for 1990+ model years
		statCodesMap.put("S", "Limited Production vehicle");
		statCodesMap.put("T", "Trailer/ Shell");
		statCodesMap.put("U", "Pickup/ Utility Truck"); // for 1989 or prior model years
		statCodesMap.put("V", "Custom Van");
		statCodesMap.put("W", "Cargo Van");
		statCodesMap.put("X", "Passenger Van"); // for 1990+ model years
		statCodesMap.put("Y", "High Exposure car Small"); // for 1990+ model years
		statCodesMap.put("Z", "Station wagon");

		assertThat(statCodesMap).as("Unknown UI \"Stat Code\" combo box value for openl statCode %s", statCode).containsKey(statCode);
		return statCodesMap.get(statCode);
	}

	private TestData getGeneralTabData(AutoCaSelectOpenLPolicy openLPolicy) {
		TestData policyInformationData = DataProviderFactory.dataOf(
				AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY));

		TestData aaaProductOwnedData = DataProviderFactory.emptyData();

		if (openLPolicy.getHome3or4() != null) {
			switch (openLPolicy.getHome3or4()) {
				case "HO-3":
					aaaProductOwnedData.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.HOME.getLabel(), "Yes");
					break;
				case "HO-4":
					aaaProductOwnedData.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.RENTERS.getLabel(), "Yes");
					break;
				case "HO-6":
					aaaProductOwnedData.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.CONDO.getLabel(), "Yes");
					break;
				case "None":
					aaaProductOwnedData
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.HOME.getLabel(), "No")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.RENTERS.getLabel(), "No")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.CONDO.getLabel(), "No");
					break;
				default:
					throw new IstfException("Unknown mapping for openl field home3or4=" + openLPolicy.getHome3or4());
			}
		}

		if (openLPolicy.getLifemoto() != null) {
			switch (openLPolicy.getHome3or4()) {
				case "B":
					aaaProductOwnedData
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE.getLabel(), "Yes")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.LIFE.getLabel(), "Yes");
					break;
				case "L":
					aaaProductOwnedData
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE.getLabel(), "No")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.LIFE.getLabel(), "Yes");
					break;
				case "M":
					aaaProductOwnedData
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE.getLabel(), "Yes")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.LIFE.getLabel(), "No");
					break;
				case "N":
					aaaProductOwnedData
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE.getLabel(), "No")
							.adjust(AutoCaMetaData.GeneralTab.AAAProductOwned.LIFE.getLabel(), "No");
					break;
				default:
					throw new IstfException("Unknown mapping for openl field lifemoto=" + openLPolicy.getLifemoto());
			}
		}

		return DataProviderFactory.dataOf(
				AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), policyInformationData,
				AutoCaMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel(), aaaProductOwnedData);
	}

	private boolean isRegularType(String statCode) {
		List<String> codes = Arrays.asList("B", "C", "D", "E", "H", "I", "J", "K", "L", "N", "O", "P", "Q", "R", "S", "U", "V", "W", "X", "Y", "Z");
		return codes.contains(statCode);
	}

	private boolean isAntiqueClassicType(String statCode) {
		return "A".equals(statCode);
	}

	private boolean isCamperType(String statCode) {
		List<String> codes = Arrays.asList("RQ", "RT", "FW", "UT", "PC", "HT", "PT");
		return codes.contains(statCode);
	}
}
