package aaa.modules.regression.service.helper.dtoAdmin.responses;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.List;

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
