package aaa.helpers.rest.aaaAdmin.services.aaaVehicleInfoRs.v1.vinInfo.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;

public class AAABodyStyleByYearMakeModelSeries {
	@JsonCreator(mode=JsonCreator.Mode.DELEGATING)
	public AAABodyStyleByYearMakeModelSeries(List<String> string) {
		setBodyStyle(string);
	}

	private List<String>  bodyStyle;

	public List<String> getBodyStyles() {
		return bodyStyle;
	}

	public void setBodyStyle(List<String> bodyStyle) {
		this.bodyStyle = bodyStyle;
	}
}
