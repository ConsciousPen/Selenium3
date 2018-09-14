package aaa.helpers.openl.model.auto_ca;

import java.util.List;
import aaa.common.enums.Constants;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.OpenLVehicle;

public abstract class AutoCaOpenLPolicy<D extends AutoCaOpenLDriver, V extends OpenLVehicle> extends OpenLPolicy {
	protected Boolean multiCar;
	protected String nanoPolicyType;

	public String getNanoPolicyType() {
		return nanoPolicyType;
	}

	public void setNanoPolicyType(String nanoPolicyType) {
		this.nanoPolicyType = nanoPolicyType;
	}

	public abstract List<D> getDrivers();

	public abstract List<V> getVehicles();

	public void setMultiCar(Boolean multiCar) {
		this.multiCar = multiCar;
	}

	@Override
	public String getUnderwriterCode() {
		return null;
	}

	@Override
	public String getState() {
		return Constants.States.CA;
	}

	public Boolean isMultiCar() {
		return multiCar;
	}
}
