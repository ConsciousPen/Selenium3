package aaa.modules.regression.service.helper;

import static org.assertj.core.api.Assertions.assertThat;
import aaa.helpers.config.CustomTestProperties;
import aaa.modules.regression.service.helper.dtoRating.DiscountPercentageRuntimeContext;
import aaa.modules.regression.service.helper.dtoRating.DiscountRetrieveFullRequest;
import toolkit.config.PropertyProvider;

public class HelperRatingServices {

	private static final String RATING_URL_TEMPLATE = "http://"+ PropertyProvider.getProperty(CustomTestProperties.APP_HOST)+":9089/aaa-rating-engine-app/REST/ws/home-ca";
	private static final String RATING_SERVICE_TYPE = "/determineDiscountPercentage";

	static void executeDiscountPercentageRetrieveRequest(String lob, String usState, String coverageCd, String expectedValue) {
		aaa.modules.regression.service.helper.dtoRating.DiscountRetrieveFullRequest request = new DiscountRetrieveFullRequest();
		request.runtimeContext = new DiscountPercentageRuntimeContext();
		request.runtimeContext.currentDate = 1517382000000L;
		request.runtimeContext.lob = lob;
		request.runtimeContext.usState = usState;
		request.discountCd = "MEMDIS";
		request.coverageCd = coverageCd;
		request.policyType = lob;
		String requestUrl = RATING_URL_TEMPLATE + RATING_SERVICE_TYPE;
		String discountPercentageValue = HelperCommon.runJsonRequestPostDxp(requestUrl, request);
		assertThat(discountPercentageValue).isEqualTo(expectedValue);
	}
}
