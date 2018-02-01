package aaa.helpers.openl.model.auto_ca.select;

import aaa.helpers.openl.model.OpenLDriver;

public class AutoCaSelectOpenLDriver extends OpenLDriver {
	private Boolean drivesync;
	private Boolean goodDriver;
	private Boolean matureDriver;
	private Boolean newDriver;
	private String type;
	private Integer yaf;

	public Boolean getDrivesync() {
		return drivesync;
	}

	public void setDrivesync(Boolean drivesync) {
		this.drivesync = drivesync;
	}

	public Boolean getGoodDriver() {
		return goodDriver;
	}

	public void setGoodDriver(Boolean goodDriver) {
		this.goodDriver = goodDriver;
	}

	public Boolean getMatureDriver() {
		return matureDriver;
	}

	public void setMatureDriver(Boolean matureDriver) {
		this.matureDriver = matureDriver;
	}

	public Boolean getNewDriver() {
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
