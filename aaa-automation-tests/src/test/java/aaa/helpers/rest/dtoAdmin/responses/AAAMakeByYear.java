package aaa.helpers.rest.dtoAdmin.responses;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;

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
