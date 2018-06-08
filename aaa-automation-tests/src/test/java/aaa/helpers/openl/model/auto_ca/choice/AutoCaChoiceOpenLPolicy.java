package aaa.helpers.openl.model.auto_ca.choice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.helpers.openl.testdata_builder.AutoCaChoiceTestDataGenerator;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.datax.TestData;

@ExcelTableElement(sheetName = OpenLFile.POLICY_SHEET_NAME, headerRowIndex = OpenLFile.POLICY_HEADER_ROW_NUMBER)
public class AutoCaChoiceOpenLPolicy extends AutoCaOpenLPolicy<AutoCaChoiceOpenLDriver, AutoCaChoiceOpenLVehicle> {

	private List<AutoCaChoiceOpenLDriver> drivers;
	private List<AutoCaChoiceOpenLVehicle> vehicles;

	@ExcelTransient
	private LocalDate effectiveDate;

	private Integer term;
	private Integer monsOfPriorIns; // unknown type, it's always empty in excel

	public Integer getMonsOfPriorIns() {
		return monsOfPriorIns;
	}

	public void setMonsOfPriorIns(Integer monsOfPriorIns) {
		this.monsOfPriorIns = monsOfPriorIns;
	}

	@Override
	public List<AutoCaChoiceOpenLDriver> getDrivers() {
		return new ArrayList<>(drivers);
	}

	public void setDrivers(List<AutoCaChoiceOpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	@Override
	public List<AutoCaChoiceOpenLVehicle> getVehicles() {
		return new ArrayList<>(vehicles);
	}

	public void setVehicles(List<AutoCaChoiceOpenLVehicle> vehicles) {
		this.vehicles = new ArrayList<>(vehicles);
	}

	@Override
	public Integer getTerm() {
		return term;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	@Override
	public LocalDate getEffectiveDate() {
		if (effectiveDate == null) {
			return TimeSetterUtil.getInstance().getCurrentTime().toLocalDate();
		}
		return effectiveDate;
	}
	
	@Override
	public AutoCaChoiceTestDataGenerator getTestDataGenerator(String state, TestData baseTestData) {
		return new AutoCaChoiceTestDataGenerator(state, baseTestData);
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public String toString() {
		return "AutoCaChoiceOpenLPolicy{" +
				"drivers=" + drivers +
				", vehicles=" + vehicles +
				", effectiveDate=" + effectiveDate +
				", multiCar=" + multiCar +
				", nanoPolicyType='" + nanoPolicyType + '\'' +
				", term=" + term +
				", monsOfPriorIns=" + monsOfPriorIns +
				", number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}
}
