package aaa.helpers.openl.testdata_builder;

import java.time.LocalDateTime;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.openl.model.OpenLPolicy;
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
}
