package aaa.modules.regression.service.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import javax.ws.rs.core.Response;
import aaa.helpers.rest.JsonClient;
import aaa.helpers.rest.dtoRating.DiscountPercentageRuntimeContext;
import aaa.helpers.rest.dtoRating.DiscountRetrieveFullRequest;
import toolkit.db.DBService;

public class HelperRatingServices {

	private static final String RATING_URL_TEMPLATE = "select value from propertyconfigurerentity\n"
			+ "where propertyname = 'aaaCaHomeRulesClientProxyFactoryBean.address'";
	private static final String RATING_SERVICE_TYPE = "/determineDiscountPercentage";

	static void executeDiscountPercentageRetrieveRequest(String lob, String usState, String coverageCd, String expectedValue) {
		DiscountRetrieveFullRequest request = new DiscountRetrieveFullRequest();
		request.runtimeContext = new DiscountPercentageRuntimeContext();
		request.runtimeContext.currentDate = 1517382000000L;
		request.runtimeContext.lob = lob;
		request.runtimeContext.usState = usState;
		request.discountCd = "MEMDIS";
		request.coverageCd = coverageCd;
		request.policyType = lob;
		String requestUrl = DBService.get().getValue(RATING_URL_TEMPLATE).get() + RATING_SERVICE_TYPE;
		String discountPercentageValue = JsonClient.sendPostRequest(requestUrl, request, String.class, Response.Status.OK.getStatusCode());
		assertThat(discountPercentageValue).isEqualTo(expectedValue);
	}
}
