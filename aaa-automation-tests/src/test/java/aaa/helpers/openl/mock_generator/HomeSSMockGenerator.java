package aaa.helpers.openl.mock_generator;

import aaa.helpers.mock.model.property_classification.RetrievePropertyClassificationMock;

public class HomeSSMockGenerator extends MockGenerator {
	@Override
	public RetrievePropertyClassificationMock buildRetrievePropertyClassificationMock() {
		RetrievePropertyClassificationMock propertyClassificationMock = super.buildRetrievePropertyClassificationMock();
		propertyClassificationMock.getPpcResponses().forEach(r -> {
			r.setFireStationDistBand("1");
			r.setFireStationDistRange("1 MILE OR LESS");
			r.setRespFireStation("MESA FS");
		});

		return propertyClassificationMock;
	}
}
