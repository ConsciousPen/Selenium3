package aaa.helpers.rest.dtoRating;

import org.codehaus.jackson.annotate.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import aaa.helpers.rest.RestBodyRequest;

public class DiscountPercentageRuntimeContext implements RestBodyRequest {

	@JsonProperty("value that will be used in request")
	public long currentDate;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	public String requestDate;

	public String lob;

	public String usState;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	public String country;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	public String usRegion;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	public String currency;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	public String lang;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	public String region;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	public String caProvince;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	public String caRegion;



}
