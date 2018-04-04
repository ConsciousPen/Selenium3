package aaa.helpers.openl.model.auto_ca;

import aaa.helpers.openl.model.OpenLDriver;

public class AutoCaOpenLDriver extends OpenLDriver {
	protected Boolean goodDriver;
	protected Boolean matureDriver;

	public void setGoodDriver(Boolean goodDriver) {
		this.goodDriver = goodDriver;
	}

	public void setMatureDriver(Boolean matureDriver) {
		this.matureDriver = matureDriver;
	}

	@Override
	public String toString() {
		return "AutoCaOpenLDriver{" +
				"goodDriver=" + goodDriver +
				", matureDriver=" + matureDriver +
				", number=" + number +
				", id='" + id + '\'' +
				", gender='" + gender + '\'' +
				", maritalStatus='" + maritalStatus + '\'' +
				", tyde=" + tyde +
				", dsr=" + dsr +
				", goodStudent=" + goodStudent +
				'}';
	}

	public Boolean isGoodDriver() {
		return goodDriver;
	}

	public Boolean isMatureDriver() {
		return matureDriver;
	}

	public Boolean isOccasionalUse() {
		return null;
	}

	public Boolean isNonSmoker() {
		return null;
	}

	public Boolean hasDriverTrainingDiscount() {
		return null;
	}
}
