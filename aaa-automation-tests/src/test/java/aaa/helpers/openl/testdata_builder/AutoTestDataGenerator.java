package aaa.helpers.openl.testdata_builder;

import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.toolkit.webdriver.customcontrols.AdvancedComboBox;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

abstract class AutoTestDataGenerator<P extends OpenLPolicy> extends TestDataGenerator<P> {
	String getDriverTabGender(String gender) {
		if ("F".equals(gender)) {
			return "Female";
		}
		if ("M".equals(gender)) {
			return "Male";
		}
		throw new IstfException("Unknown mapping for gender: " + gender);
	}

	String getDriverTabMartialStatus(String martialStatus) {
		switch (martialStatus) {
			case "M":
				return "Married";
			case "S":
				return "Single";
			case "D":
				return "Divorced";
			case "W":
				return "Widowed";
			case "P": //TODO-dchubkov: double check openl value
				return "Separated";
			case "R":
				return "Registered Domestic Partner";
			default:
				throw new IstfException("Unknown mapping for martialStatus: " + martialStatus);
		}
	}

	String getDriverTabDateOfBirth(int ageFirstLicensed, int totalYearsDrivingExperience) {
		LocalDateTime dob = TimeSetterUtil.getInstance().getCurrentTime().minusYears(ageFirstLicensed + totalYearsDrivingExperience);
		return dob.format(DateTimeUtils.MM_DD_YYYY);
	}

	String getDriverGetLicenseType(boolean isForeignLicense) {
		if (isForeignLicense) {
			return "Foreign";
		}
		return AdvancedComboBox.RANDOM_EXCEPT_MARK + "=Foreign";
	}

	String getVehicleTabUsage(String usage) {
		switch (usage) {
			case "A":
				return "Artisan";
			case "B":
				return "Business";
			case "F":
				return "Farm";
			case "P":
			case "P1":
			case "P2":
			case "P3":
				return "Pleasure";
			case "W1":
			case "W2":
				return "Commute";
			default:
				throw new IstfException("Unknown mapping for usage: " + usage);
		}
	}

	String getVehicleTabAntiTheft(String antiTheft) {
		if ("N".equals(antiTheft)) {
			return "None";
		}
		return "Vehicle Recovery Device";
	}

	String getVehicleTabAirBags(String airBagCode) {
		switch (airBagCode) {
			case "N":
				return "None";
			//TODO-dchubkov: add other codes
			default:
				throw new IstfException("Unknown mapping for airbagCode: " + airBagCode);
		}
	}

	String getPremiumAndCoveragesTabCoverageKey(String coverageCD) {
		switch (coverageCD) {
			case "BI":
				return AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel();
			case "PD":
				return AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY.getLabel();
			case "UMBI":
				return AutoSSMetaData.PremiumAndCoveragesTab.UNDERINSURED_MOTORISTS_BODILY_INJURY.getLabel();
			case "SP EQUIP":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.SPECIAL_EQUIPMENT_COVERAGE.getLabel();
			case "COMP":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COMPREGENSIVE_DEDUCTIBLE.getLabel();
			case "COLL":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.COLLISION_DEDUCTIBLE.getLabel();
			case "UMPD":
				return AutoSSMetaData.PremiumAndCoveragesTab.DetailedVehicleCoverages.UNINSURED_MOTORIST_PROPERTY_DAMAGE.getLabel();
			case "PIP":
				return AutoSSMetaData.PremiumAndCoveragesTab.PERSONAL_INJURY_PROTECTION.getLabel();
			default:
				throw new IstfException("Unknown mapping for coverageCD: " + coverageCD);
		}
	}

	String getPremiumAndCoveragesTabCoverageLimit(String limit) {
		String[] limitRange = limit.split("/");
		String returnLimit;
		if (limitRange.length > 2) {
			throw new IstfException("Unknown mapping for limit: " + limit);
		}
		returnLimit = "contains=" + new Dollar(limitRange[0] + "000").toString().replaceAll("\\.00", "");
		if (limitRange.length == 2) {
			returnLimit = returnLimit + "/" + new Dollar(limitRange[1] + "000").toString().replaceAll("\\.00", "");
		}
		return returnLimit;
	}
}
