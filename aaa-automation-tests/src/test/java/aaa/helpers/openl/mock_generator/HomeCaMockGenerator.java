package aaa.helpers.openl.mock_generator;

import java.time.LocalDate;
import aaa.helpers.mock.model.property_classification.RetrievePropertyClassificationMock;

public class HomeCaMockGenerator extends MockGenerator {

	@Override
	public RetrievePropertyClassificationMock getRetrievePropertyClassificationMock() {
		String streetAddressLine = "6586 PORCUPINE WAY";
		String state = "CA";
		String district = "Los Angeles";

		RetrievePropertyClassificationMock propertyClassificationMock = super.getRetrievePropertyClassificationMock();
		propertyClassificationMock.getFirelineRequests().forEach(r -> {
			r.setStreetAddressLine(streetAddressLine);
			r.setState(state);
		});
		propertyClassificationMock.getFirelineResponses().forEach(r -> r.setSlopeType("S3"));
		propertyClassificationMock.getPpcRequests().forEach(r -> {
			r.setStreetAddressLine(streetAddressLine);
			r.setState(state);
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
			r.setState(state);
		});

		return propertyClassificationMock;
	}
}
