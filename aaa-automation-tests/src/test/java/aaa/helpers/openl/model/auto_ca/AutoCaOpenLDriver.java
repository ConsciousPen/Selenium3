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

	public Boolean isGoodDriver() {
		return goodDriver;
	}

	public Boolean isMatureDriver() {
		return matureDriver;
	}
}
