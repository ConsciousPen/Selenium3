package aaa.helpers.mock.model;

import aaa.helpers.mock.MockType;

public interface UpdatableMock {
	MockType getType();

	//TODO-dchubkov: maybe better change return type to boolean (false = nothing to merge)?
	UpdatableMock merge(UpdatableMock mock);
}
