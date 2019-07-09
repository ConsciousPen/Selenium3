package aaa.helpers.openl.model.auto_ca.choice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.helpers.openl.testdata_generator.AutoCaChoiceTestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.datax.TestData;

@ExcelTableElement(sheetName = OpenLFile.POLICY_SHEET_NAME, headerRowIndex = OpenLFile.POLICY_HEADER_ROW_NUMBER)
public class AutoCaChoiceOpenLPolicy extends AutoCaOpenLPolicy<AutoCaChoiceOpenLDriver, AutoCaChoiceOpenLVehicle> {

	@RequiredField
	private List<AutoCaChoiceOpenLDriver> drivers;

	@RequiredField
	private List<AutoCaChoiceOpenLVehicle> vehicles;

	@ExcelTransient
	private LocalDate effectiveDate;

	@RequiredField
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
	public PolicyType getTestPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
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
	public AutoCaChoiceTestDataGenerator getTestDataGenerator(TestData baseTestData) {
		return new AutoCaChoiceTestDataGenerator(this.getState(), baseTestData);
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
}
