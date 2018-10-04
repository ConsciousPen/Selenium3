package aaa.helpers.rest.dtoAdmin.responses;


import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;

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
