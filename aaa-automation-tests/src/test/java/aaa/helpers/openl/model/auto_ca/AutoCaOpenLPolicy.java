package aaa.helpers.openl.model.auto_ca;

import java.util.List;
import java.util.Map;
import aaa.common.enums.Constants;
import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.model.OpenLVehicle;

public abstract class AutoCaOpenLPolicy<D extends AutoCaOpenLDriver, V extends OpenLVehicle> extends OpenLPolicy {
	protected Boolean multiCar;

	@RequiredField
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
	public boolean isLegacyConvPolicy() {
		return false;
	}

	@Override
	public boolean isCappedPolicy() {
		return false;
	}

	@Override
	public boolean isNewRenPasCappedPolicy() {
		return !isLegacyConvPolicy() && isCappedPolicy();
	}

	@Override
	public String getState() {
		return Constants.States.CA;
	}

	public Boolean isMultiCar() {
		return multiCar;
	}

	@Override
	public Map<String, String> getFilteredOpenLFieldsMap() {
		return removeOpenLFields(super.getFilteredOpenLFieldsMap(),
				"^policy\\.drivers\\[\\d+\\]\\.id$",
				"^policy\\.vehicles\\[\\d+\\]\\.id$",
				"^policy\\.vehicles\\[\\d+\\]\\.coverages\\[\\d+\\]\\.additionalLimitAmount$" // is applicable for altCoverages only, for regular coverage should be always null
		);
	}
}
