package aaa.helpers.openl.model.auto_ca.select;

import aaa.helpers.openl.model.auto_ca.AutoCaOpenLDriver;

public class AutoCaSelectOpenLDriver extends AutoCaOpenLDriver {
	private Boolean drivesync;
	private Boolean newDriver;
	private String type;
	private Integer yaf;

	public Boolean isDrivesync() {
		return drivesync;
	}

	public void setDrivesync(Boolean drivesync) {
		this.drivesync = drivesync;
	}

	public Boolean isNewDriver() {
		return newDriver;
	}

	public void setNewDriver(Boolean newDriver) {
		this.newDriver = newDriver;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getYaf() {
		return yaf;
	}

	public void setYaf(Integer yaf) {
		this.yaf = yaf;
	}

	@Override
	public String toString() {
		return "AutoCaSelectOpenLDriver{" +
				"drivesync=" + drivesync +
				", goodDriver=" + goodDriver +
				", matureDriver=" + matureDriver +
				", newDriver=" + newDriver +
				", type='" + type + '\'' +
				", yaf=" + yaf +
				", number=" + number +
				", id='" + id + '\'' +
				", gender='" + gender + '\'' +
				", maritalStatus='" + maritalStatus + '\'' +
				", tyde=" + tyde +
				", dsr=" + dsr +
				", goodStudent=" + goodStudent +
				'}';
	}
}
