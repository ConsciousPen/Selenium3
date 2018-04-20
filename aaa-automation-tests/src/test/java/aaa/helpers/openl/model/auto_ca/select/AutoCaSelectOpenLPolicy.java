package aaa.helpers.openl.model.auto_ca.select;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

public class AutoCaSelectOpenLPolicy extends AutoCaOpenLPolicy<AutoCaSelectOpenLDriver, AutoCaSelectOpenLVehicle> {
	@ExcelTableElement(sheetName = OpenLFile.DRIVER_SHEET_NAME, headerRowIndex = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
	private List<AutoCaSelectOpenLDriver> drivers;

	@ExcelTableElement(sheetName = OpenLFile.VEHICLE_SHEET_NAME, headerRowIndex = OpenLFile.VEHICLE_HEADER_ROW_NUMBER)
	private List<AutoCaSelectOpenLVehicle> vehicles;

	private LocalDateTime effectiveDate;
	private Integer baseYear;
	private Boolean aaaMember;
	private Boolean goodDriverPolicy;
	private String home3or4;
	private Integer id;
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
	public LocalDateTime getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public Integer getTerm() {
		//TODO-dchubkov: to be verified whether 12 is OK for default term or not
		return 12;
	}

	@Override
	public String toString() {
		return "AutoCaSelectOpenLPolicy{" +
				"drivers=" + drivers +
				", vehicles=" + vehicles +
				", baseYear=" + baseYear +
				", multiCar=" + multiCar +
				", nanoPolicyType='" + nanoPolicyType + '\'' +
				", aaaMember=" + aaaMember +
				", effectiveDate=" + effectiveDate +
				", goodDriverPolicy=" + goodDriverPolicy +
				", home3or4='" + home3or4 + '\'' +
				", id=" + id +
				", lifemoto='" + lifemoto + '\'' +
				", number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}

	public Boolean isAaaMember() {
		return aaaMember;
	}
}
