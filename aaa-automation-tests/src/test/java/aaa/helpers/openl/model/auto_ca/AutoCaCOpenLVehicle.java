package aaa.helpers.openl.model.auto_ca;

import aaa.helpers.openl.model.OpenLVehicle;

public class AutoCaCOpenLVehicle extends OpenLVehicle {
	protected Boolean antiLock;
	protected Boolean antiTheft;
	protected String vehType;
	protected String vehicleUsageCd;

	public String getVehType() {
		return vehType;
	}

	public void setVehType(String vehType) {
		this.vehType = vehType;
	}

	public String getVehicleUsageCd() {
		return vehicleUsageCd;
	}

	public void setVehicleUsageCd(String vehicleUsageCd) {
		this.vehicleUsageCd = vehicleUsageCd;
	}

	public void setAntiLock(Boolean antiLock) {
		this.antiLock = antiLock;
	}

	public void setAntiTheft(Boolean antiTheft) {
		this.antiTheft = antiTheft;
	}

	@Override
	public String toString() {
		return "AutoCaCOpenLVehicle{" +
				"antiLock=" + antiLock +
				", antiTheft=" + antiTheft +
				", vehType='" + vehType + '\'' +
				", vehicleUsageCd='" + vehicleUsageCd + '\'' +
				", number=" + number +
				", annualMileage=" + annualMileage +
				", collSymbol=" + collSymbol +
				", compSymbol=" + compSymbol +
				", id='" + id + '\'' +
				", modelYear=" + modelYear +
				", statCode='" + statCode + '\'' +
				", address=" + address +
				", coverages=" + coverages +
				'}';
	}

	public Boolean hasAntiLock() {
		return antiLock;
	}

	public Boolean hasAntiTheft() {
		return antiTheft;
	}
}
