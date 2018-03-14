package aaa.modules.regression.service.helper;


import aaa.modules.policy.PolicyBaseTest;

public abstract class TestRatingServicesAbstract extends PolicyBaseTest {

	protected void pas10302_discountPercentageRetrieveBody(String lob, String usState, String coverageCd, String expectedValue) {
		HelperRatingServices.executeDiscountPercentageRetrieveRequest(lob, usState, coverageCd, expectedValue);
	}
}
