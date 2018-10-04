package aaa.helpers.rest.dtoAdmin.responses;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonCreator;

public class AAABodyStyleByYearMakeModelSeries {
	@JsonCreator(mode=JsonCreator.Mode.DELEGATING)
	public AAABodyStyleByYearMakeModelSeries(List<String> string) {
		setBodyStyle(string);
	}

	private List<String>  bodyStyle;

	public List<String> getBodyStyle() {
		return bodyStyle;
	}

	public void setBodyStyle(List<String> bodyStyle) {
		this.bodyStyle = bodyStyle;
	}
}
