package aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;

public class AAAMakeByYear {

	@JsonCreator(mode=JsonCreator.Mode.DELEGATING)
	public AAAMakeByYear(List<String> strings) {
		setMake(strings);
	}

	private List<String> makes;

	public List<String> getMakes() {
		return makes;
	}

	public void setMake(List<String> makes) {
		this.makes = makes;
	}

}
