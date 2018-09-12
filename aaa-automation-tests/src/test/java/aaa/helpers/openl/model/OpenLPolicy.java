package aaa.helpers.openl.model;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.google.gson.JsonElement;
import aaa.helpers.mock.MocksCollection;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
import aaa.utils.excel.bind.ReflectionHelper;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.datax.TestData;

public abstract class OpenLPolicy {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected String policyNumber;

	@ExcelTransient
	private Dollar expectedPremium;

	@ExcelTransient
	private String state;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public Dollar getExpectedPremium() {
		return expectedPremium;
	}

	public void setExpectedPremium(Dollar expectedPremium) {
		this.expectedPremium = expectedPremium;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public abstract Integer getTerm();

	public Double getPreviousPolicyPremium() {
		return null;
	}

	public abstract String getUnderwriterCode();

	public abstract LocalDate getEffectiveDate();

	public MocksCollection getRequiredMocks() {
		//override this method in child classes if this type of policy require specific mocks.
		return null;
	}

	@Override
	public String toString() {
		return "OpenLPolicy{" +
				"number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}

	public abstract TestDataGenerator<? extends OpenLPolicy> getTestDataGenerator(String state, TestData baseTestData);

	public Map<String, String> getOpenLFieldsMap() {
		Map<String, String> openLFieldsMap = new LinkedHashMap<>();
		for (Field openLField : ReflectionHelper.getAllAccessibleFieldsFromThisAndSuperClasses(getClass())) {
			//TODO-dchubkov: get parentOpenLFieldPath instead of hardcoded "policy" value
			openLFieldsMap.putAll(getOpenLFieldsMap(openLField, this, "policy"));
		}
		return openLFieldsMap;
	}

	public abstract OpenLPolicy createFrom(JsonElement jsonElement);

	private Map<String, String> getOpenLFieldsMap(Field openLField, Object classInstance, String parentOpenLFieldPath) {
		Map<String, String> openLFieldsMap = new LinkedHashMap<>();

		if (openLField.isAnnotationPresent(ExcelColumnElement.class) && openLField.getAnnotation(ExcelColumnElement.class).isPrimaryKey()) {
			return openLFieldsMap;
		}

		String fieldName = openLField.getName();
		if (openLField.isAnnotationPresent(ExcelColumnElement.class) && !openLField.getAnnotation(ExcelColumnElement.class).name().equals(ExcelColumnElement.DEFAULT_COLUMN_NAME)) {
			fieldName = openLField.getAnnotation(ExcelColumnElement.class).name();
			//TODO-dchubkov: add support for containsName() and ignoreCase() (e.g. "coverageCd"/coverageCD)
		}
		String openLFieldPath = parentOpenLFieldPath != null ? parentOpenLFieldPath + "." + fieldName : fieldName;

		if (ReflectionHelper.isTableClassField(openLField)) {
			Class<?> tableClass = ReflectionHelper.getFieldType(openLField);
			List<Field> tableColumnsFields = ReflectionHelper.getAllAccessibleFieldsFromThisAndSuperClasses(tableClass);
			if (List.class.isAssignableFrom(openLField.getType())) {
				List<?> tableRowsObjects = ReflectionHelper.getValueAsList(openLField, classInstance);
				if (tableRowsObjects == null) {
					openLFieldsMap.put(openLFieldPath, null);
				} else {
					for (int i = 0; i < tableRowsObjects.size(); i++) {
						for (Field tableColumnField : tableColumnsFields) {
							openLFieldsMap.putAll(getOpenLFieldsMap(tableColumnField, tableRowsObjects.get(i), openLFieldPath + "[" + i + "]"));
						}
					}
				}
			} else {
				for (Field tableColumnField : tableColumnsFields) {
					openLFieldsMap.putAll(getOpenLFieldsMap(tableColumnField, ReflectionHelper.getFieldValue(openLField, classInstance), openLFieldPath));
				}
			}

		} else {
			Object value = classInstance == null ? null : ReflectionHelper.getFieldValue(openLField, classInstance);
			openLFieldsMap.put(openLFieldPath, String.valueOf(value));
		}
		//TODO-dchubkov: add support for multi columns fields

		return openLFieldsMap;
	}
}
