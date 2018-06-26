package aaa.helpers.mock.model;

import static toolkit.verification.CustomAssertions.assertThat;
import java.lang.reflect.Field;
import java.util.List;
import aaa.utils.excel.bind.BindHelper;

public abstract class AbstractMock implements UpdatableMock {
	@Override
	@SuppressWarnings("unchecked")
	public boolean add(UpdatableMock otherMock) {
		assertThat(otherMock).as("Unable to add objects of different classes").hasSameClassAs(this);

		boolean isUpdated = false;
		for (Field tableField : BindHelper.getAllAccessibleFields(otherMock.getClass(), true)) {
			List<Object> thisTableObjects = (List<Object>) BindHelper.getValueAsList(tableField, this);
			List<Object> otherTableObjects = (List<Object>) BindHelper.getValueAsList(tableField, otherMock);
			if (thisTableObjects == null && otherTableObjects != null) {
				BindHelper.setFieldValue(tableField, this, otherTableObjects);
				isUpdated = true;
			} else {
				if (thisTableObjects.addAll(otherTableObjects)) {
					isUpdated = true;
				}
			}
		}
		return isUpdated;
	}
}
