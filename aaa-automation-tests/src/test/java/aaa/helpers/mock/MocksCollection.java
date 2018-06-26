package aaa.helpers.mock;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import aaa.helpers.mock.model.UpdatableMock;
import aaa.utils.excel.bind.ExcelMarshaller;

public class MocksCollection implements Iterable<UpdatableMock> {
	private Map<MockType, UpdatableMock> mocks;

	public MocksCollection() {
		mocks = new HashMap<>();
	}

	public MocksCollection(UpdatableMock mock) {
		mocks = new HashMap<>();
		if (mock != null) {
			mocks.put(mock.getType(), mock);
		}
	}

	public boolean isEmpty() {
		return mocks.isEmpty();
	}

	@Override
	public Iterator<UpdatableMock> iterator() {
		return new MocksIterator(mocks.values().iterator());
	}

	public boolean has(MockType mockType) {
		return mocks.containsKey(mockType);
	}

	public UpdatableMock get(MockType mockType) {
		return mocks.get(mockType);
	}

	public boolean add(UpdatableMock mock) {
		return mock != null && add(mock.getType(), mock);
	}

	public boolean add(MockType mockType, UpdatableMock mock) {
		if (mock == null) {
			return false;
		}
		if (!has(mockType)) {
			mocks.put(mockType, mock);
			return true;
		}
		return get(mockType).add(mock);
	}

	public boolean addAll(MocksCollection requiredMocks) {
		boolean isUpdated = false;
		for (UpdatableMock mock : requiredMocks) {
			if (add(mock)) {
				isUpdated = true;
			}
		}
		return isUpdated;
	}

	public void dump(String folderPath) {
		ExcelMarshaller excelMarshaller = new ExcelMarshaller();
		for (UpdatableMock mock : this) {
			File output = new File(folderPath, mock.getType().getFileName());
			excelMarshaller.marshal(mock, output);
		}
	}

	private int size() {
		return mocks.size();
	}

	private static final class MocksIterator implements Iterator<UpdatableMock> {
		private Iterator<UpdatableMock> iterator;

		private MocksIterator(Iterator<UpdatableMock> iterator) {
			this.iterator = iterator;
		}

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
