package aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.model;


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
