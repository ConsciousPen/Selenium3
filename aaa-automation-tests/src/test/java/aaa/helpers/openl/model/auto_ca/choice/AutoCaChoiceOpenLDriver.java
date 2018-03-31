package aaa.helpers.openl.model.auto_ca.choice;

import aaa.helpers.openl.model.auto_ca.AutoCaOpenLDriver;

public class AutoCaChoiceOpenLDriver extends AutoCaOpenLDriver {
	private Boolean driverTrainingDiscount;
	private Boolean nonSmoker;
	private Boolean occasionalUse;

	public void setDriverTrainingDiscount(Boolean driverTrainingDiscount) {
		this.driverTrainingDiscount = driverTrainingDiscount;
	}

	public void setNonSmoker(Boolean nonSmoker) {
		this.nonSmoker = nonSmoker;
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

	@Override
	public Boolean hasDriverTrainingDiscount() {
		return driverTrainingDiscount;
	}

	@Override
	public Boolean isNonSmoker() {
		return nonSmoker;
	}

	@Override
	public Boolean isOccasionalUse() {
		return occasionalUse;
	}
}
