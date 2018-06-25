package aaa.helpers.mock;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import aaa.helpers.mock.model.UpdatableMock;

public class MocksCollection implements Iterable<UpdatableMock> {
	private Map<MockType, UpdatableMock> mocks;

	public MocksCollection() {
		mocks = new HashMap<>();
	}

	public MocksCollection(UpdatableMock mock) {
		mocks = new HashMap<>();
		mocks.put(mock.getType(), mock);
	}

	@Override
	public Iterator<UpdatableMock> iterator() {
		return new MocksIterator();
	}

	public boolean has(MockType mockType) {
		return mocks.containsKey(mockType);
	}

	public UpdatableMock add(UpdatableMock mock) {
		return add(mock.getType(), mock);
	}

	public UpdatableMock add(MockType mockType, UpdatableMock mock) {
		UpdatableMock mergedMock;
		if (!has(mockType)) {
			mergedMock = mock;
		} else {
			mergedMock = get(mockType).merge(mock);
		}

		mocks.put(mockType, mergedMock);
		return mergedMock;
	}

	public boolean addAll(MocksCollection requiredMocks) {
		boolean isUpdated = false;
		for (UpdatableMock mock : requiredMocks) {
			add(mock);
		}
		return isUpdated;
	}

	public UpdatableMock get(MockType mockType) {
		return mocks.get(mockType);
	}

	private class MocksIterator implements Iterator<UpdatableMock> {
		private Iterator<UpdatableMock> iterator = mocks.values().iterator();

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public UpdatableMock next() {
			return iterator.next();
		}
	}
}
