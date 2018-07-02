package aaa.helpers.mock;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import aaa.helpers.mock.model.AbstractMock;
import aaa.helpers.mock.model.UpdatableMock;
import aaa.utils.excel.bind.ExcelMarshaller;

public class MocksCollection implements Iterable<UpdatableMock> {
	private Map<Class<? extends UpdatableMock>, UpdatableMock> mocks;

	public MocksCollection() {
		mocks = new HashMap<>();
	}

	public MocksCollection(UpdatableMock mock) {
		mocks = new HashMap<>();
		if (mock != null) {
			mocks.put(mock.getClass(), mock);
		}
	}

	public boolean isEmpty() {
		return mocks.isEmpty();
	}

	@Override
	public Iterator<UpdatableMock> iterator() {
		return new MocksIterator(mocks.values().iterator());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (UpdatableMock mock : this) {
			sb.append(mock).append("\n");
		}
		return "MocksCollection{" + sb + "}";
	}

	public boolean has(Class<? extends UpdatableMock> mockModelClass) {
		return mocks.containsKey(mockModelClass);
	}

	public UpdatableMock get(Class<? extends UpdatableMock> mockModelClass) {
		return mocks.get(mockModelClass);
	}

	public boolean add(UpdatableMock mock) {
		return mock != null && add(mock.getClass(), mock);
	}

	public boolean addAll(MocksCollection mocks) {
		if (mocks == null) {
			return false;
		}

		boolean isUpdated = false;
		for (UpdatableMock mock : mocks) {
			if (add(mock)) {
				isUpdated = true;
			}
		}
		return isUpdated;
	}

	public void dump(String folderPath) {
		ExcelMarshaller excelMarshaller = new ExcelMarshaller();
		for (UpdatableMock mock : this) {
			File output = new File(folderPath, mock.getFileName());
			excelMarshaller.marshal(mock, output);
		}
	}

	private boolean add(Class<? extends UpdatableMock> mockModelClass, UpdatableMock mock) {
		if (mock == null) {
			return false;
		}
		if (!has(mockModelClass)) {
			mocks.put(mockModelClass, ((AbstractMock) mock).clone());
			return true;
		}
		return get(mockModelClass).add(mock);
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
