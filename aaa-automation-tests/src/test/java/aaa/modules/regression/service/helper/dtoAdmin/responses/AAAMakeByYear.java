package aaa.modules.regression.service.helper.dtoAdmin.responses;


import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class AAAMakeByYear {

	@JsonCreator(mode=JsonCreator.Mode.DELEGATING)
	public AAAMakeByYear(List<String> strings) {
		setMake(strings);
	}

	private List<String> makes;

	public List<String> getListMake() {
		return makes;
	}

	public void setMake(List<String> makes) {
		this.makes = makes;
	}

}
