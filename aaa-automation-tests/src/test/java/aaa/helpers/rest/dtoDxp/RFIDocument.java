package aaa.helpers.rest.dtoDxp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "RFI Document")
public class RFIDocument {

	@ApiModelProperty(value = "Document code of RFI document", required = true, example = "PAA")
	public String documentCode;
	@ApiModelProperty(value = "Document Name", required = true, example = "Auto Insurance Application")
	public String documentName;
	@ApiModelProperty(value = "Document Status", required = true, example = "NS")
	public String status;
	@ApiModelProperty(value = "Unique identifier for RFI document", required = true, example = "PAA_kRp7hnbhUbbqVe96LB1Lqg")
	public String documentId;
	@ApiModelProperty(value = "Parent of the document - policy, driver, vehicle", required = true, example = "policy")
	public String parent;
	@ApiModelProperty(value = "Policy, driver, vehicle OID", required = true, example = "kRp7hnbhUbbqVe96LB1Lqg")
	public String parentOid;
}