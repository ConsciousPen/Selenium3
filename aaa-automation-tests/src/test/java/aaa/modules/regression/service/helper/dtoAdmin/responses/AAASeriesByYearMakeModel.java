package aaa.modules.regression.service.helper.dtoAdmin.responses;


import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

public class AAASeriesByYearMakeModel {

	@JsonCreator(mode=JsonCreator.Mode.DELEGATING)
	public AAASeriesByYearMakeModel(List<String> strings) {
		setSeries(strings);
	}

	private List<String> series;

	public List<String> getSeries() {
		return series;
	}

	public void setSeries(List<String> series) {
		this.series = series;
	}
}
;