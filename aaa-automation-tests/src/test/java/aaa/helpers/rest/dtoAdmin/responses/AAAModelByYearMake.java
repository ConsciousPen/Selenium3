package aaa.helpers.rest.dtoAdmin.responses;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;

public class AAAModelByYearMake {
	@JsonCreator(mode=JsonCreator.Mode.DELEGATING)
	public AAAModelByYearMake(List<String> strings) {
		setModels(strings);
	}

	private List<String> models;

	public List<String> getModels () {
		return models;
	}

	public void setModels(List<String> models) {
		this.models = models;
	}

}
