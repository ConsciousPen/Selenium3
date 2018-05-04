package aaa.helpers;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.datax.TestData;
import toolkit.datax.TestDataException;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;

public class TestDataHelper {
	protected static Logger log = LoggerFactory.getLogger(TestDataHelper.class);

	public static TestData merge(TestData leftTestData, TestData rightTestData) {
		return merge(leftTestData, rightTestData, true, true);
	}

	public static TestData merge(TestData leftTestData, TestData rightTestData, boolean convertStringToList, boolean convertTestDataToList) {
		return merge(leftTestData, rightTestData, convertStringToList, convertTestDataToList, true);
	}

	public static TestData adjustWithNewValues(TestData baseTestData, TestData testData) {
		List<String> newKeys = testData.getKeys().stream().filter(td -> !baseTestData.getKeys().contains(td)).collect(Collectors.toList());
		for (String key : newKeys) {
			TestData.Type valueType = getValueType(testData, key);
			switch (valueType) {
				case STRING:
					baseTestData.adjust(key, testData.getValue(key));
					break;
				case TESTDATA:
					baseTestData.adjust(key, testData.getTestData(key));
					break;
				case LIST_STRING:
					baseTestData.adjust(key, testData.getList(key));
					break;
				case LIST_TESTDATA:
					baseTestData.adjust(key, testData.getTestDataList(key));
					break;
			}
		}
		return baseTestData;
	}

	public static TestData.Type getValueType(TestData td, String... keys) {
		for (TestData.Type valueType : TestData.Type.values()) {
			try {
				td.getValue(valueType, keys);
			} catch (TestDataException e) {
				continue;
			}
			return valueType;
		}
		throw new IstfException(String.format("Provided TestData with key(s) %s has incompatible value type.", Arrays.asList(keys)));
	}

	public static List<TestData> convertAllToSimpleDataProviderTypeAndResolveLinks(List<TestData> tdList) {
		return tdList.stream().map(TestDataHelper::convertToSimpleDataProviderTypeAndResolveLinks).collect(Collectors.toCollection(() -> new ArrayList<>(tdList.size())));
	}

	/**
	 * Useful method if you need to prepare common TestData list of elements taken from different sources (e.g. from Yaml file and created by DataProviderFactory)
	 *
	 * With different testdata element types you can't use TestData adjust(String keyPath, List<?> list) due to "Lists of mixed types are not allowed!" (probably ISTF defect)
	 */
	public static TestData convertToSimpleDataProviderTypeAndResolveLinks(TestData td) {
		//to prevent TestDataException: Lists of mixed types are not allowed!
		//TODO-dchubkov: find the way how to check whether test data has mixed types or not instead of exception catching
		try {
			td = td.resolveLinks();
		} catch (TestDataException ignore) {
		}

		if (!td.getClass().isAssignableFrom(SimpleDataProvider.class)) {
			SimpleDataProvider convertedTd = new SimpleDataProvider();
			return convertedTd.adjust(td);
		}
		return td;
	}

	private static TestData merge(TestData leftTestData, TestData rightTestData, boolean convertStringToList, boolean convertTestDataToList, boolean isFirstCycleMerge) {
		TestData resultData;
		TestData tdLeft = convertToSimpleDataProviderTypeAndResolveLinks(leftTestData);
		TestData tdRight = convertToSimpleDataProviderTypeAndResolveLinks(rightTestData);

		if (tdLeft.equals(tdRight)) { //TODO-dchubkov: double check equals for test data
			if (isFirstCycleMerge) {
				log.warn("Both provided test datas are equal, there is nothing to merge");
			}
			return tdLeft;
		}

		resultData = tdLeft;
		for (String key : resultData.getKeys()) {
			if (!tdRight.containsKey(key)) {
				continue; //nothing to merge, tdRight does not have key from tdLeft, tdLeft value is copied to result data
			}

			TestData.Type valueType = getValueType(resultData, key);
			TestData.Type valueTypeRight = getValueType(tdRight, key);

			if (convertStringToList && isConvertibleToStringList(valueType, valueTypeRight)) {
				valueType = valueTypeRight = TestData.Type.LIST_STRING;
			}

			if (convertTestDataToList && isConvertibleToTestDataList(valueType, valueTypeRight)) {
				valueType = valueTypeRight = TestData.Type.LIST_TESTDATA;
			}

			if (!Objects.equals(valueType, valueTypeRight)) {
				throw new IstfException(String.format("Both test datas have same key \"%1$s\" but different value types. Left TestData type is \"%2$s\", Right TestData type is \"%3$s\".",
						key, valueType, valueTypeRight));
			}
			if (Objects.equals(resultData.getValue(valueType, key), tdRight.getValue(valueType, key))) {
				continue; //nothing to merge, tdLeft and tdRight values are equal, tdLeft value is copied to result data
			}

			switch (valueType) {
				case STRING:
					resultData.adjust(key, tdRight.getValue(key));
					break;
				case TESTDATA:
					resultData.adjust(key, merge(resultData.getTestData(key), tdRight.getTestData(key), convertStringToList, convertTestDataToList, false));
					break;
				case LIST_STRING:
					List<String> resultList = new ArrayList<>(getStringList(tdRight, key, convertStringToList));
					List<String> leftList = getStringList(resultData, key, convertStringToList);
					if (resultList.size() < leftList.size()) {
						int index = leftList.size() - (leftList.size() - resultList.size());
						Iterator<String> leftListIterator = leftList.listIterator(index);
						while (leftListIterator.hasNext()) {
							resultList.add(leftListIterator.next());
						}
					}
					resultData.adjust(key, resultList);
					break;
				case LIST_TESTDATA:
					List<TestData> resultTestDataList = new ArrayList<>();
					List<TestData> tdLeftList = getTestDataList(resultData, key, convertTestDataToList);
					List<TestData> tdRightList = getTestDataList(tdRight, key, convertTestDataToList);
					tdLeftList = convertAllToSimpleDataProviderTypeAndResolveLinks(tdLeftList);
					tdRightList = convertAllToSimpleDataProviderTypeAndResolveLinks(tdRightList);
					for (int i = 0; i < tdLeftList.size(); i++) {
						if (i < tdRightList.size()) {
							resultTestDataList.add(merge(tdLeftList.get(i), tdRightList.get(i), convertStringToList, convertTestDataToList, false));
						} else {
							resultTestDataList.add(tdLeftList.get(i));
						}
					}

					if (tdRightList.size() > tdLeftList.size()) {
						int index = tdRightList.size() - (tdRightList.size() - tdLeftList.size());
						Iterator<TestData> tdRightListIterator = tdRightList.listIterator(index);
						while (tdRightListIterator.hasNext()) {
							resultTestDataList.add(tdRightListIterator.next());
						}
					}
					resultData.adjust(key, resultTestDataList);
					break;
			}
		}

		return adjustWithNewValues(resultData, tdRight).resolveLinks();
	}

	private static boolean isConvertibleToStringList(TestData.Type valueTypeLeft, TestData.Type valueTypeRight) {
		return valueTypeLeft.equals(TestData.Type.STRING) && valueTypeRight.equals(TestData.Type.LIST_STRING) ||
				valueTypeRight.equals(TestData.Type.LIST_STRING) && valueTypeLeft.equals(TestData.Type.STRING);
	}

	private static boolean isConvertibleToTestDataList(TestData.Type valueTypeLeft, TestData.Type valueTypeRight) {
		return valueTypeLeft.equals(TestData.Type.TESTDATA) && valueTypeRight.equals(TestData.Type.LIST_TESTDATA) ||
				valueTypeLeft.equals(TestData.Type.LIST_TESTDATA) && valueTypeRight.equals(TestData.Type.TESTDATA);
	}

	private static List<String> getStringList(TestData td, String key, boolean convertStringToList) {
		if (convertStringToList && getValueType(td, key).equals(TestData.Type.STRING)) {
			return Arrays.asList(td.getValue(key));
		}
		return td.getList(key);
	}

	private static List<TestData> getTestDataList(TestData td, String key, boolean convertTestDataToList) {
		if (convertTestDataToList && getValueType(td, key).equals(TestData.Type.TESTDATA)) {
			return Arrays.asList(td.getTestData(key));
		}
		return td.getTestDataList(key);
	}
}
