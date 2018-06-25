package aaa.helpers.mock.model;

import static toolkit.verification.CustomAssertions.assertThat;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import aaa.helpers.mock.MockType;
import aaa.utils.excel.bind.BindHelper;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.exceptions.IstfException;

public class AbstractMock implements UpdatableMock {
	@ExcelTransient
	private MockType type;

	@Override
	public MockType getType() {
		if (this.type == null) {
			type = Arrays.stream(MockType.values()).filter(mockType -> Objects.equals(mockType.getMockModel(), this.getClass())).findFirst()
					.orElseThrow(() -> new IstfException(String.format("Class \"%s\" is related to unknown mock type", this.getClass().getName())));
		}
		return this.type;
	}

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
