package aaa.helpers.rest.dtoDxp;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(description = "Collection of RFI documents")
public class RFIDocuments {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value = "URL to access generated RFI documents")
	public String url;

	@ApiModelProperty(value = "Collection of RFI documents unwrapped")
	public List<RFIDocument> documents;
}