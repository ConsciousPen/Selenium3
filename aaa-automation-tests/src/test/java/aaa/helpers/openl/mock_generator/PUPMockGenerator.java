package aaa.helpers.openl.mock_generator;

import aaa.helpers.mock.model.property_classification.RetrievePropertyClassificationMock;

public class PUPMockGenerator extends MockGenerator {
	@Override
	public RetrievePropertyClassificationMock getRetrievePropertyClassificationMock() {
		return new HomeSSMockGenerator().getRetrievePropertyClassificationMock();
	}
}
