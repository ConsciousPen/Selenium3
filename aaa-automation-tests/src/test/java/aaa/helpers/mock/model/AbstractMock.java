package aaa.helpers.mock.model;

import static toolkit.verification.CustomAssertions.assertThat;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import aaa.utils.excel.bind.BindHelper;
import toolkit.exceptions.IstfException;

public abstract class AbstractMock implements UpdatableMock {
	@Override
	@SuppressWarnings("unchecked")
	public boolean add(UpdatableMock otherMock) {
		assertThat(otherMock).as("Unable to add objects of different classes").hasSameClassAs(this);
		UpdatableMock clonedMock = otherMock.clone();

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
		AbstractMock clone = null;
		try {
			clone = (AbstractMock) super.clone();
			for (Field tableField : BindHelper.getAllAccessibleFields(this.getClass(), true)) {
				List<Object> tableObjects = new ArrayList<>(BindHelper.getValueAsList(tableField, this));
				BindHelper.setFieldValue(tableField, clone, tableObjects);
			}
		} catch (CloneNotSupportedException ignore) {
			// never should happen
		} catch (Throwable e) {
			throw new IstfException("Unable to clone object " + this.getClass().getName(), e);
		}
		return clone;
	}
}
