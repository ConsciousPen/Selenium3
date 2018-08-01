package aaa.helpers.openl.mock_generator;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import aaa.common.enums.Constants;
import aaa.helpers.mock.model.property_classification.FirelineRequest;
import aaa.helpers.mock.model.property_classification.FirelineResponse;
import aaa.helpers.mock.model.property_classification.RetrievePropertyClassificationMock;

public class HomeCaMockGenerator extends MockGenerator {
	private static final String STATE = Constants.States.CA;

	@Override
	public boolean isPropertyClassificationMockPresent() {
		List<String> validFirelineRequestIDs = getMock(RetrievePropertyClassificationMock.class).getFirelineRequests().stream()
				.filter(r -> StringUtils.isBlank(r.getCityName())
						&& StringUtils.isBlank(r.getZipCode())
						&& STREET_ADDRESS_LINE.equals(r.getStreetAddressLine())
						&& STATE.equals(r.getState()))
				.map(FirelineRequest::getId).collect(Collectors.toList());

		List<String> validFirelineResponseIDs = getMock(RetrievePropertyClassificationMock.class).getFirelineResponses().stream()
				.filter(r -> validFirelineRequestIDs.contains(r.getId())
						&& ADJ_FUEL_RATING.equals(r.getAdjFuelRating()))
				.map(FirelineResponse::getId).collect(Collectors.toList());

		return getMock(RetrievePropertyClassificationMock.class).getPpcResponses().stream()
				.anyMatch(r -> validFirelineResponseIDs.contains(r.getId())
						&& NumberUtils.isCreatable(r.getPpcValue())
						&& StringUtils.isNotBlank(r.getFireDistrict())
						&& "T".equals(r.getFireSubscriberDistrCode()));
	}

	@Override
	public RetrievePropertyClassificationMock buildRetrievePropertyClassificationMock() {
		String district = "Los Angeles";

		RetrievePropertyClassificationMock propertyClassificationMock = super.buildRetrievePropertyClassificationMock();
		propertyClassificationMock.getFirelineRequests().forEach(r -> {
			r.setStreetAddressLine(STREET_ADDRESS_LINE);
			r.setState(STATE);
		});
		propertyClassificationMock.getFirelineResponses().forEach(r -> r.setSlopeType("S3"));
		propertyClassificationMock.getPpcRequests().forEach(r -> {
			r.setStreetAddressLine(STREET_ADDRESS_LINE);
			r.setState(STATE);
		});
		propertyClassificationMock.getPpcResponses().forEach(r -> {
			r.setFePpcValue("5");
			r.setFeEffectiveDate(LocalDate.of(2013, 9, 6));
			r.setPpcValue("5");
			r.setPpcCountyName(district);
			r.setPpcCountyName1(district);
			r.setPpcCountyName2(district);
			r.setFireDistrict(district);
			r.setFireDistrict1(district);
			r.setFireDistrict2(district);
			r.setState(STATE);
		});

		return propertyClassificationMock;
	}
}
