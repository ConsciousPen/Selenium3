package aaa.utils.openl.parser;

import static aaa.utils.openl.parser.AutoSSOpenLFields.Driver;
import static aaa.utils.openl.parser.AutoSSOpenLFields.Policy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import aaa.utils.excel.ExcelParser;
import aaa.utils.excel.ExcelTable;
import aaa.utils.excel.TableRow;
import aaa.utils.openl.model.AutoSSOpenLPolicy;
import aaa.utils.openl.model.OpenLDriver;

public class AutoSSOpenLFileParser extends OpenLFileParser<AutoSSOpenLPolicy, AutoSSOpenLFields> {

	public AutoSSOpenLFileParser(String openLFilePath) {
		super(openLFilePath, new AutoSSOpenLFields());
	}

	public List<OpenLDriver> getDrivers() {
		ExcelParser ep = new ExcelParser(getOpenLFile(), DRIVER_SHEET_PATTERN);
		ExcelTable driverTable = ep.getTable(openLFields.getDriverFields());
		List<OpenLDriver> openLDrivers = new ArrayList<>(driverTable.getRowsNumber());

		for (TableRow row : driverTable) {
			OpenLDriver driver = new OpenLDriver();

			driver.setNumber(row.getIntValue(Driver.PK.get()));
			driver.setId(row.getValue(Driver.ID.get()));
			driver.setName(row.getValue(Driver.NAME.get()));
			driver.setGender(row.getValue(Driver.GENDER.get()));
			driver.setMaritalStatus(row.getValue(Driver.MARITAL_STATUS.get()));
			driver.setTyde(row.getIntValue(Driver.TYDE.get()));
			driver.setDsr(row.getIntValue(Driver.DSR.get()));
			driver.setGoodStudent(row.getBoolValue(Driver.GOOD_STUDENT.get()));
			driver.setHasSR22(row.getBoolValue(Driver.HAS_SR22.get()));
			driver.setDriverAge(row.getIntValue(Driver.DRIVER_AGE.get()));
			driver.setAgeBeforeEndorsement(row.getIntValue(Driver.AGE_BEFORE_ENDORSEMENT.get()));
			driver.setSmartDriver(row.getBoolValue(Driver.SMART_DRIVER.get()));
			driver.setDistantStudent(row.getBoolValue(Driver.DISTANT_STUDENT.get()));
			driver.setDefensiveDrivingCourse(row.getValue(Driver.DEFENSIVE_DRIVING_COURSE.get()));
			driver.setForeignLicense(row.getBoolValue(Driver.FOREIGN_LICENSE.get()));
			driver.setUnverifiableDrivingRecord(row.getBoolValue(Driver.UNVERIFIABLE_DRIVING_RECORD.get()));
			driver.setOutOfStateLicenseSurcharge(row.getBoolValue(Driver.OUT_OF_STATE_LICENSE_SURCHARGE.get()));
			driver.setExposure(row.getBoolValue(Driver.EXPOSURE.get()));
			driver.setCleanDriver(row.getBoolValue(Driver.CLEAN_DRIVER.get()));
			openLDrivers.add(driver);
		}

		return openLDrivers;
	}

	@Override
	public List<AutoSSOpenLPolicy> getPolicies() {
		ExcelParser ep = new ExcelParser(getOpenLFile(), POLICY_SHEET_PATTERN);
		ExcelTable policiesTable = ep.getTable(getOpenLFields().getPolicyFields());
		List<AutoSSOpenLPolicy> openLPolicies = new ArrayList<>(policiesTable.getRowsNumber());

		for (TableRow row : policiesTable) {
			AutoSSOpenLPolicy policy = new AutoSSOpenLPolicy();
			policy.setNumber(row.getIntValue(OpenLFields.Policy.PK.get()));
			policy.setPolicyNumber(row.getValue(OpenLFields.Policy.POLICY_NUMBER.get()));
			List<OpenLDriver> drivers = getDrivers(getNumbersArray(row.getValue(Policy.DRIVERS.get())));
			policy.setDrivers(drivers);
			//to be continued...

			openLPolicies.add(policy);
		}
		return openLPolicies;
	}

	public List<OpenLDriver> getDrivers(int... driverNumbers) {
		List<Integer> searchNumbers = Arrays.stream(driverNumbers).boxed().collect(Collectors.toList());
		return getDrivers().stream().filter(d -> searchNumbers.contains(d.getNumber())).collect(Collectors.toList());
	}

	public List<AutoSSOpenLPolicy> getPolicies(int... policyNumbers) {
		List<Integer> searchNumbers = Arrays.stream(policyNumbers).boxed().collect(Collectors.toList());
		return getPolicies().stream().filter(p -> searchNumbers.contains(p.getNumber())).collect(Collectors.toList());
	}

}
