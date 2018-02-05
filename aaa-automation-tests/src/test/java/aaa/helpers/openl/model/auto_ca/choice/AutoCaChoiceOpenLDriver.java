package aaa.helpers.openl.model.auto_ca.choice;

import aaa.helpers.openl.model.OpenLDriver;

public class AutoCaChoiceOpenLDriver extends OpenLDriver {
	private Boolean driverTrainingDiscount;
	private Boolean goodDriver;
	private Boolean matureDriver;
	private Boolean nonSmoker;
	private Boolean occasionalUse;

	public Boolean hasDriverTrainingDiscount() {
		return driverTrainingDiscount;
	}

	public void setDriverTrainingDiscount(Boolean driverTrainingDiscount) {
		this.driverTrainingDiscount = driverTrainingDiscount;
	}

	public Boolean isGoodDriver() {
		return goodDriver;
	}

	public void setGoodDriver(Boolean goodDriver) {
		this.goodDriver = goodDriver;
	}

	public Boolean isMatureDriver() {
		return matureDriver;
	}

	public void setMatureDriver(Boolean matureDriver) {
		this.matureDriver = matureDriver;
	}

	public Boolean isNonSmoker() {
		return nonSmoker;
	}

	public void setNonSmoker(Boolean nonSmoker) {
		this.nonSmoker = nonSmoker;
	}

	public Boolean isOccasionalUse() {
		return occasionalUse;
	}

	public void setOccasionalUse(Boolean occasionalUse) {
		this.occasionalUse = occasionalUse;
	}

	@Override
	public String toString() {
		return "AutoCaCOpenLDriver{" +
				"driverTrainingDiscount=" + driverTrainingDiscount +
				", goodDriver=" + goodDriver +
				", matureDriver=" + matureDriver +
				", nonSmoker=" + nonSmoker +
				", occasionalUse=" + occasionalUse +
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
