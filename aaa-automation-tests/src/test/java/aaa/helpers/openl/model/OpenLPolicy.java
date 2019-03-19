package aaa.helpers.openl.model;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import aaa.helpers.mock.MocksCollection;
import aaa.helpers.openl.annotation.MatchingField;
import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.utils.excel.bind.ReflectionHelper;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssertions;

public abstract class OpenLPolicy {
	@ExcelTransient
	protected static Logger log = LoggerFactory.getLogger(OpenLPolicy.class);

	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	@RequiredField
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

	public abstract PolicyType getTestPolicyType();

	public abstract Integer getTerm();

	public Double getPreviousPolicyPremium() {
		return null;
	}

	public abstract String getUnderwriterCode();

	public abstract LocalDate getEffectiveDate();

	public abstract boolean isLegacyConvPolicy();

	public abstract boolean isCappedPolicy();

	public abstract boolean isNewRenPasCappedPolicy();

	/**
	 * Returns {@link MocksCollection} with required mocks to be generated and uploaded (if absent) to the application server for OpenL tests which use this OpenL policy object<p>
	 * Override this method in child classes if this type of policy require specific mocks.
	 *
	 * @return {@link MocksCollection} with required mocks to be generated and uploaded (if absent) to the application server. If <b>null</b> then mocks update will be skipped.
	 */
	public MocksCollection getRequiredMocks() {
		return null;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder(getClass().getSimpleName() + "{\n");
		Map<String, String> openLFieldsMap = getOpenLFieldsMap();
		int i = 0;
		for (Map.Entry<String, String> openLFieldEntry : openLFieldsMap.entrySet()) {
			output.append("    ").append(openLFieldEntry.getKey()).append("=\"").append(openLFieldEntry.getValue()).append("\"");
			if (i < openLFieldsMap.size() - 1) {
				output.append(",");
			}
			output.append("\n");
			i++;
		}
		output.append("}\n");
		return output.toString();
	}

	/**
	 * Returns {@link TestDataGenerator} object to be used as test data generator for this particular OpenL policy object
	 *
	 * @param baseTestData default test data which will be merged with generated test specific data (used to add common field/values to the generated test data)
	 * @return {@link TestDataGenerator} object to be used as test data generator for this particular OpenL policy object
	 */
	public abstract TestDataGenerator<? extends OpenLPolicy> getTestDataGenerator(TestData baseTestData);

	/**
	 * Builds and returns Map of OpenL fields and values of this OpenL policy object where key is path to the OpenL field and value - OpenL field value itself
	 *
	 * @return Map of OpenL fields and values of this OpenL policy object where key is path to the OpenL field and value - OpenL field value itself
	 */
	public Map<String, String> getOpenLFieldsMap() {
		Map<String, String> openLFieldsMap = new LinkedHashMap<>();
		for (Field openLField : ReflectionHelper.getAllAccessibleFieldsFromThisAndSuperClasses(getClass())) {
			openLFieldsMap.putAll(getOpenLFieldsMap(openLField, this, "policy"));
		}
		return openLFieldsMap;
	}

	/**
	 * Creates and returns {@link MapDifference} object with differences between OpenL fields/values of this OpenL policy object and field/values of {@code otherOpenLPolicy} object.<p>
	 * Before {@link MapDifference} creation, both OpenL policy objects will be sorted by {@link #sortInnerListsRecursively(OpenLPolicy)} and filtered by {@link #getFilteredOpenLFieldsMap()} methods.<p>
	 *
	 * @param otherOpenLPolicy other OpenL policy to be compared with
	 * @return differences between OpenL fields/values of this OpenL policy object and field/values of {@code otherOpenLPolicy} object
	 * @throws {@link AssertionError} if this and other OpenL policy objects are instances of different classes or other OpenL policy is null
	 */
	public MapDifference<String, String> diff(OpenLPolicy otherOpenLPolicy) {
		CustomAssertions.assertThat(otherOpenLPolicy).as("Unable to compare this OpenL policy object with null").isNotNull();
		if (otherOpenLPolicy != null) {
			CustomAssertions.assertThat(this).as("Unable to compare OpenL policy objects of different classes").hasSameClassAs(otherOpenLPolicy);
		}
		Map<String, String> thisOpenLFieldsMap = this.sortInnerListsRecursively(otherOpenLPolicy).getFilteredOpenLFieldsMap();
		Map<String, String> otherOpenLFieldsMap = otherOpenLPolicy.getFilteredOpenLFieldsMap();
		return Maps.difference(thisOpenLFieldsMap, otherOpenLFieldsMap);
	}

	/**
	 * Finds recursively all inner lists within this OpenL policy object and sort them if their generic type implement {@link Comparable} interface
	 *
	 * @return this OpenL policy object after sorting
	 */
	public OpenLPolicy sortInnerListsRecursively() {
		return sortInnerListsRecursively(null);
	}

	/**
	 * Finds recursively all inner lists within this OpenL policy and {@code otherOpenLPolicy} objects and sort them if their generic type implement {@link Comparable} interface.<p>
	 * If other OpenL policy object is not <b>null</b> then these lists will be also reordered according to fields with {@link MatchingField} annotation (if present).<p>
	 * Inner list of <b>other</b> object will be reordered if its size is greater then list size of <b>this</b> object, otherwise inner list of <b>this</b> object will be reordered.<p>
	 * Example 1:<p>
	 * If this OpenL policy object has coverages with {@link OpenLCoverage#coverageCd} field values [coveraAgeCd="CovA", coverageCd="CovB", coverageCd="CovC"]<p>
	 * and {@code otherOpenLPolicy} object has coverages with {@link OpenLCoverage#coverageCd} field values [coverageCd="CovC", coverageCd="CovA", coverageCd="CovB", coverageCd="CovD"]<p>
	 * then coverages of <b>other</b> OpenL policy will be reordered this way: [coverageCd="CovA", coverageCd="CovB", coverageCd="CovC", coverageCd="CovD"].<p>
	 * Example 2:<p>
	 * If this OpenL policy object has coverages with {@link OpenLCoverage#coverageCd} field values [coverageCd="CovA", coverageCd="CovB", coverageCd="CovC", coverageCd="CovD"]<p>
	 * and {@code otherOpenLPolicy} object has coverages with {@link OpenLCoverage#coverageCd} field values [coverageCd="CovA", coverageCd="CovD"]<p>
	 * then coverages of <b>this</b> OpenL policy will be reordered this way: [coverageCd="CovA", coverageCd="CovD", coverageCd="CovC", coverageCd="CovB"].<p>
	 *
	 * @param otherOpenLPolicy other OpenL policy to be sorted and reordered by field values annotated with {@link MatchingField}
	 *
	 * @return this OpenL policy object after sorting/reordering
	 * @throws {@link AssertionError} if this and other OpenL policy objects are instances of different classes
	 */
	public OpenLPolicy sortInnerListsRecursively(OpenLPolicy otherOpenLPolicy) {
		if (otherOpenLPolicy != null) {
			CustomAssertions.assertThat(this).as("Unable to sort inner lists of OpenL objects with different types").hasSameClassAs(otherOpenLPolicy);
		}
		for (Field openLField : ReflectionHelper.getAllAccessibleFieldsFromThisAndSuperClasses(getClass())) {
			sortByComparator(openLField, this);
			sortByComparator(openLField, otherOpenLPolicy);
			reorderByMatchingFields(openLField, this, otherOpenLPolicy);
		}
		return this;
	}

	/**
	 * Gets Map of OpenL fields and values by {@link #getOpenLFieldsMap()} method and removes from it OpenL fields which do not affect rating
	 *
	 * @return filtered Map of OpenL fields and values of this OpenL policy object where key is path to the OpenL field and value - OpenL field value itself
	 */
	public Map<String, String> getFilteredOpenLFieldsMap() {
		return removeOpenLFields(getOpenLFieldsMap(), "policy.policyNumber");
	}

	/**
	 * Verifies whether OpenL policy object is proper for testing or no by checking recursively that all class fields with {@link RequiredField} annotation have non-null values (and not empty lists for Collections).<p>
	 * This check is useful to verify whether correct openl model class was used for unmarshalling while building this object or not.<p>
	 *
	 * @return <b>true</b> if openl policy is proper for testing and <b>false</b> if not
	 */
	public boolean isProper() {
		return ReflectionHelper.getAllAccessibleFieldsFromThisAndSuperClasses(getClass()).stream().allMatch(f -> isProperField(f, this));
	}

	protected boolean isProperField(Field openLField, Object classInstance) {
		if (openLField.isAnnotationPresent(RequiredField.class)) {
			if (ReflectionHelper.isTableClassField(openLField)) {
				Class<?> fieldType = ReflectionHelper.getFieldType(openLField);
				List<?> listElements = ReflectionHelper.getValueAsList(openLField, classInstance);
				if (CollectionUtils.isEmpty(listElements)) {
					log.warn("Required openL field \"{}\" has null value or empty list", openLField);
					return false;
				}

				for (Field field : ReflectionHelper.getAllAccessibleFieldsFromThisAndSuperClasses(fieldType)) {
					for (Object instance : listElements) {
						if (!isProperField(field, instance)) {
							return false;
						}
					}
				}

			} else {
				Object value = ReflectionHelper.getFieldValue(openLField, classInstance);
				if (value == null) {
					log.warn("Required openL field \"{}\" has null value", openLField);
					return false;
				}
			}
		}

		return true;
	}

	protected Map<String, String> removeOpenLFields(Map<String, String> openLFieldsMap, String... openLFieldsRegexToRemove) {
		for (String openLFieldRegex : openLFieldsRegexToRemove) {
			Pattern openLFieldPattern = Pattern.compile(openLFieldRegex);
			openLFieldsMap.entrySet().removeIf(e -> openLFieldPattern.matcher(e.getKey()).matches());
		}
		return openLFieldsMap;
	}

	@SuppressWarnings("unchecked")
	protected void sortByComparator(Field openLField, Object classInstance) {
		if (classInstance == null) {
			return;
		}

		Class<?> fieldType = ReflectionHelper.getFieldType(openLField);
		if (ReflectionHelper.isTableClassField(openLField)) {
			List<?> valuesList = ReflectionHelper.getValueAsList(openLField, classInstance);
			if (CollectionUtils.isNotEmpty(valuesList)) {
				for (Field field : ReflectionHelper.getAllAccessibleFieldsFromThisAndSuperClasses(fieldType)) {
					for (Object value : valuesList) {
						sortByComparator(field, value);
					}
				}
			}
		}

		if (List.class.isAssignableFrom(openLField.getType()) && Comparable.class.isAssignableFrom(fieldType)) {
			List<?> valuesList = ReflectionHelper.getValueAsList(openLField, classInstance);
			if (valuesList != null && valuesList.size() > 1) {
				valuesList = new ArrayList<>(valuesList);
				Collections.sort((List<Comparable<Object>>) valuesList);
				ReflectionHelper.setFieldValue(openLField, classInstance, valuesList);
			}
		}
	}

	protected void reorderByMatchingFields(Field openLField, Object thisInstance, Object otherInstance) {
		if (thisInstance == null || otherInstance == null || !ReflectionHelper.isTableClassField(openLField)) {
			return;
		}

		List<?> thisValuesList = ReflectionHelper.getValueAsList(openLField, thisInstance);
		List<?> otherValuesList = ReflectionHelper.getValueAsList(openLField, otherInstance);

		if (CollectionUtils.isNotEmpty(thisValuesList) && CollectionUtils.isNotEmpty(otherValuesList)) {
			Class<?> fieldType = ReflectionHelper.getFieldType(openLField);
			List<Field> allFields = ReflectionHelper.getAllAccessibleFieldsFromThisAndSuperClasses(fieldType);
			List<Field> matchingFields = allFields.stream().filter(f -> f.isAnnotationPresent(MatchingField.class)).collect(Collectors.toList());
			allFields.removeAll(matchingFields);

			boolean reorderOtherList = thisValuesList.size() < otherValuesList.size();
			int maxCommonIndex = thisValuesList.size() < otherValuesList.size() ? thisValuesList.size() : otherValuesList.size();
			List<?> listToReorder = reorderOtherList ? new ArrayList<>(otherValuesList) : new ArrayList<>(thisValuesList);
			Object instanceToUpdate = reorderOtherList ? otherInstance : thisInstance;
			boolean isListReordered = false;

			for (int index = 0; index < maxCommonIndex; index++) {
				Object thisValue = thisValuesList.size() > index ? thisValuesList.get(index) : null;
				Object otherValue = otherValuesList.size() > index ? otherValuesList.get(index) : null;

				List<?> valuesListToSearch = new ArrayList<>(listToReorder);
				for (Field matchingField : matchingFields) {
					Object matchingFieldValue = reorderOtherList ? ReflectionHelper.getFieldValue(matchingField, thisValue) : ReflectionHelper.getFieldValue(matchingField, otherValue);
					valuesListToSearch.removeIf(v -> !Objects.equals(matchingFieldValue, ReflectionHelper.getFieldValue(matchingField, v)));
					if (valuesListToSearch.isEmpty()) {
						break;
					}
				}

				if (!matchingFields.isEmpty() && !valuesListToSearch.isEmpty()) {
					if (valuesListToSearch.size() > 1) {
						log.warn("There are more than one [{}] matched object were found in the list, first one will be used for reordering", valuesListToSearch.size());
					}

					Object matchedValue = valuesListToSearch.get(0);
					int swapIndex = listToReorder.indexOf(matchedValue);
					if (index != swapIndex) {
						Collections.swap(listToReorder, index, swapIndex);
						isListReordered = true;
					}

					if (reorderOtherList) {
						otherValue = matchedValue;
					} else {
						thisValue = matchedValue;
					}
				}

				for (Field field : allFields) {
					reorderByMatchingFields(field, thisValue, otherValue);
				}
			}

			if (isListReordered) {
				ReflectionHelper.setFieldValue(openLField, instanceToUpdate, listToReorder);
			}
		}
	}

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

		if (ReflectionHelper.isTableClassField(openLField) && classInstance != null) {
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
