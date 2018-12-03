package aaa.helpers.rest.dtoDxp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * API DTO to display common policy summary info.
 */
@ApiModel(description = "Policy Information")
public class PolicySummary {

	@ApiModelProperty(value = "Policy number", example = "VASS926232065", required = true)
	public String policyNumber;

	@ApiModelProperty(value = "Timed policy status. For all available statuses please refer to documentation in Confluence", example = "issued", required = true)
	public String policyStatus;

	@ApiModelProperty(value = "Timed policy status. For all available statuses please refer to documentation in Confluence", example = "inForce", required = true)
	public String timedPolicyStatus;

	@ApiModelProperty(value = "Policy effective date in IS8601 format (yyyy-MM-dd).", example = "2018-01-30", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public String effectiveDate;

	@ApiModelProperty(value = "Policy expiration date in IS8601 format (yyyy-MM-dd).", example = "2018-01-30", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public String expirationDate;

	@ApiModelProperty(value = "Endorsement transaction effective date in IS8601 format (yyyy-MM-dd).", example = "2018-01-30", required = true)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	public String transactionEffectiveDate;

	@ApiModelProperty(value = "Source policy number. Indicates original policy number from which this policy was created, empty if new business.", example = "CONV123456")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String sourcePolicyNumber;

	@ApiModelProperty(value = "Source of business. NEW for new business, CONV for converted policies, REW for rewritten policies", example = "NEW", required = true)
	public String sourceOfBusiness;

	@ApiModelProperty(value = "Renewal cycle. 0 if new business or conversion stub image, >= 1 if first or subsequent renewal ", example = "0", required = true)
	public int renewalCycle;

	@ApiModelProperty(value = "eValue status. For all available statuses please refer to documentation in Confluence", example = "ACTIVE", required = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public String eValueStatus;

	@ApiModelProperty(value = "Error code", example = "300")
	public String errorCode;

	@ApiModelProperty(value = "Message ", example = "Renewal quote version or issued pending renewal not found for policy number AZSS952918544.")
	public String message;

	@ApiModelProperty(value = "Actual amount", example = "4126")
	public String actualAmt;

	@ApiModelProperty(value = "Term premium", example = "4126")
	public String termPremium;

	@ApiModelProperty(value = "The Residential Address. Address from the first of the First Named Insureds.", example = "1200")
	public Address residentialAddress;

	@ApiModelProperty(value = "Policy term", example = "12")
	public String policyTerm;

	@ApiModelProperty(value = "Policy endorsement ID", example = "ivB8QApZ3n9oaVHM3CFIpQ")
	public String id;

	@ApiModelProperty(value = "Policy bind Time and Date", example = "2018-09-05T20:48:10Z")
	public String bindDate;

	@ApiModelProperty(value = "Renewal offered indicator", example = "true")
	public Boolean isRenewalOffered;

}
