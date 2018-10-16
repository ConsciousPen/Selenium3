package aaa.helpers.rest.dtoDxp;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LookupValueDto implements Serializable {
	@JsonProperty("CODE")
	private String code;
	@JsonProperty("DISPLAYVALUE")
	private String displayValue;
	@JsonProperty("S_OBJECT")
	private String serializedObject;

	public LookupValueDto() {
	}

	public LookupValueDto(String code, String displayValue) {
		this.code = code;
		this.displayValue = displayValue;
	}

	public LookupValueDto(String code, String displayValue, String serializedObject) {
		this.code = code;
		this.displayValue = displayValue;
		this.serializedObject = serializedObject;
	}
}

