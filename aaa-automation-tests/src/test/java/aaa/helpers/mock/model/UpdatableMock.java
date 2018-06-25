package aaa.helpers.mock.model;

import aaa.helpers.mock.MockType;

public interface UpdatableMock {
	MockType getType();

	boolean add(UpdatableMock mock);
}
