package aaa.helpers.mock.model;

import static toolkit.verification.CustomAssertions.assertThat;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import aaa.utils.excel.bind.BindHelper;

public abstract class AbstractMock implements UpdatableMock, Cloneable {
	@Override
	@SuppressWarnings("unchecked")
	public boolean add(UpdatableMock otherMock) {
		assertThat(otherMock).as("Unable to add objects of different classes").hasSameClassAs(this);
		AbstractMock clonedMock = ((AbstractMock) otherMock).clone();

		boolean isUpdated = false;
		for (Field tableField : BindHelper.getAllAccessibleFields(clonedMock.getClass(), true)) {
			List<Object> thisTableObjects = (List<Object>) BindHelper.getValueAsList(tableField, this);
			List<Object> otherTableObjects = new ArrayList<>(BindHelper.getValueAsList(tableField, clonedMock));
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

	@Override
	public AbstractMock clone() {
		AbstractMock clonedMock = (AbstractMock) BindHelper.getInstance(this.getClass());
		for (Field tableField : BindHelper.getAllAccessibleFields(this.getClass(), true)) {
			List<Object> tableObjects = new ArrayList<>(BindHelper.getValueAsList(tableField, this));
			BindHelper.setFieldValue(tableField, clonedMock, tableObjects);
		}
		return clonedMock;
	}
}
