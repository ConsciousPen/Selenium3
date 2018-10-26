package aaa.helpers.openl.model.auto_ca.select;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.helpers.openl.testdata_generator.AutoCaSelectTestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import toolkit.datax.TestData;

@ExcelTableElement(sheetName = OpenLFile.POLICY_SHEET_NAME, headerRowIndex = OpenLFile.POLICY_HEADER_ROW_NUMBER)
public class AutoCaSelectOpenLPolicy extends AutoCaOpenLPolicy<AutoCaSelectOpenLDriver, AutoCaSelectOpenLVehicle> {

	@RequiredField
	private List<AutoCaSelectOpenLDriver> drivers;

	@RequiredField
	private List<AutoCaSelectOpenLVehicle> vehicles;

	@RequiredField
	private LocalDate effectiveDate;

	@RequiredField
	private Integer baseYear;

	@RequiredField
	private Boolean aaaMember;

	@RequiredField
	private Boolean goodDriverPolicy;

	private String home3or4;
	private Integer id;

	@RequiredField
	private String lifemoto;

	public Integer getBaseYear() {
		return baseYear;
	}

	public void setBaseYear(Integer baseYear) {
		this.baseYear = baseYear;
	}

	public Boolean getGoodDriverPolicy() {
		return goodDriverPolicy;
	}

	public void setGoodDriverPolicy(Boolean goodDriverPolicy) {
		this.goodDriverPolicy = goodDriverPolicy;
	}

	public String getHome3or4() {
		return home3or4;
	}

	public void setHome3or4(String home3or4) {
		this.home3or4 = home3or4;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLifemoto() {
		return lifemoto;
	}

	public void setLifemoto(String lifemoto) {
		this.lifemoto = lifemoto;
	}

	public void setAaaMember(Boolean aaaMember) {
		this.aaaMember = aaaMember;
	}

	@Override
	public List<AutoCaSelectOpenLDriver> getDrivers() {
		return new ArrayList<>(drivers);
	}

	public void setDrivers(List<AutoCaSelectOpenLDriver> drivers) {
		this.drivers = new ArrayList<>(drivers);
	}

	@Override
	public List<AutoCaSelectOpenLVehicle> getVehicles() {
		return new ArrayList<>(vehicles);
	}

	public void setVehicles(List<AutoCaSelectOpenLVehicle> vehicles) {
		this.vehicles = new ArrayList<>(vehicles);
	}

	@Override
	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	@Override
	public AutoCaSelectTestDataGenerator getTestDataGenerator(TestData baseTestData) {
		return new AutoCaSelectTestDataGenerator(this.getState(), baseTestData);
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public PolicyType getTestPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Override
	public Integer getTerm() {
		//TODO-dchubkov: to be verified whether 12 is OK for default term or not
		return 12;
	}

	@Override
	public Map<String, String> getFilteredOpenLFieldsMap() {
		return removeOpenLFields(super.getFilteredOpenLFieldsMap(),
				"policy.id",
				"^policy\\.vehicles\\[\\d+\\]\\.primaryDriver\\.id$",
				"^policy\\.vehicles\\[\\d+\\]\\.optionalCoverages$");
	}

	public Boolean isAaaMember() {
		return aaaMember;
	}
}
