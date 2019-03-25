package aaa.helpers.rest.dtoRating;

import org.codehaus.jackson.annotate.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import aaa.helpers.rest.RestBodyRequest;

public class DiscountPercentageRuntimeContext implements RestBodyRequest {

	@JsonProperty("value that will be used in request")
	public long currentDate;

	@JsonSerialize
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public String requestDate;

	public String lob;

	public String usState;

	@JsonInclude
	public String country;

	@JsonSerialize
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public String usRegion;

	@JsonSerialize
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public String currency;

	@JsonSerialize
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public String lang;

	@JsonSerialize
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public String region;

	@JsonSerialize
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public String caProvince;

	@JsonSerialize
	@JsonInclude(value = JsonInclude.Include.NON_NULL)
	public String caRegion;



}
