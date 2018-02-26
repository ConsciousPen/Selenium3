package aaa.modules.regression.service.helper.dtoRating;

import aaa.modules.regression.service.helper.RestBodyRequest;

public class DiscountRetrieveFullRequest implements RestBodyRequest {

	public DiscountPercentageRuntimeContext runtimeContext;

	public String discountCd;

	public String coverageCd;

	public String policyType;

}
