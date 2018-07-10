package aaa.helpers.mock.model;

public interface UpdatableMock extends Cloneable {
	String getFileName();

	boolean add(UpdatableMock mock);

	UpdatableMock clone();
}
