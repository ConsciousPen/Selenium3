package aaa.helpers.rest.dtoDxp;
import java.util.Map;
import io.swagger.annotations.ApiModelProperty;

public class AttributeMetadata {
	@ApiModelProperty(value = "Attribute name", example = "vehTypeCd", required = true)
	public String attributeName;

	@ApiModelProperty(value = "Field enabled in PAS", example = "true", required = true)
	public boolean enabled;

	@ApiModelProperty(value = "Field visible in PAS", example = "true", required = true)
	public boolean visible;

	@ApiModelProperty(value = "Field required in PAS", example = "false", required = true)
	public boolean required;

	@ApiModelProperty(value = "Max length", example = "15", required = true)
	public String maxLength;

	@ApiModelProperty(value = "Attribute Type", example = "String", required = true)
	public String attributeType;

	@ApiModelProperty(value = "Lookup value range", example = "{\"someCode\" : \"someValue\"}", required = true)
	public Map<String, String> valueRange;
}
