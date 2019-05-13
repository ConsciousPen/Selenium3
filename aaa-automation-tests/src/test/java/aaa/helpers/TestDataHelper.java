package aaa.helpers;

import java.util.*;
import java.util.stream.Collectors;

import aaa.common.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.datax.TestData;
import toolkit.datax.TestDataException;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;

public class TestDataHelper {
	protected static Logger log = LoggerFactory.getLogger(TestDataHelper.class);

	/**
	 * Create copy of provided test data which has SimpleDataProvider type with all resolved links and processed String values by appropriate {@link toolkit.datax.MarkupParser}
	 * Warning. Cloned data may not be equal to original one but only due to processed String values by {@link toolkit.datax.MarkupParser}
	 */
	public static TestData clone(TestData td) {
		Map<String, Object> clonedTestData = new LinkedHashMap<>(td.getKeys().size());
		for (String key : td.getKeys()) {
			switch (getValueType(td, key)) {
				case STRING:
					clonedTestData.put(key, td.getValue(key));
					break;
				case LIST_STRING:
					clonedTestData.put(key, td.getList(key));
					break;
				case TESTDATA:
					clonedTestData.put(key, clone(td.getTestData(key)));
					break;
				case LIST_TESTDATA:
					List<TestData> clonedTestDataList = new ArrayList<>(td.getTestDataList(key).size());
					for (TestData testData : td.getTestDataList(key)) {
						clonedTestDataList.add(clone(testData));
					}
					clonedTestData.put(key, clonedTestDataList);
					break;
			}
		}

		return new SimpleDataProvider(clonedTestData);
	}

	public static TestData.Type getValueType(TestData td, String... keys) {
		for (TestData.Type valueType : TestData.Type.values()) {
			try {
				td.getValue(valueType, keys);
			} catch (TestDataException e) {
				if ("Lists of mixed types are not allowed!".equals(e.getCause().getMessage())) {
					throw new IstfException("Looks like TestData contain TestData list of mixed data providers (e.g. YAMLDataProvider and SimpleDataProvider). See related defect #EISISSUE-50740.", e);
				}
				continue;
			}
			return valueType;
		}
		throw new IstfException(String.format("Provided TestData with key(s) %s has incompatible value type.", Arrays.asList(keys)));
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

	/**
	 * Merges two test datas into one unified test data.<p>
	 * Algorithm is the same as described in {@link TestDataHelper#merge(TestData, TestData, boolean, boolean)}<p>
	 * but with <b>convertStringToList=true</b> and <b>convertTestDataToList=true</b> arguments;
	 */
	public static TestData merge(TestData leftTestData, TestData rightTestData) {
		return merge(leftTestData, rightTestData, true, true);
	}

	/**
	 * Merges two test datas into one unified test data.<p>
	 *
	 * It makes copy of <b>leftTestData</b> using {@link TestDataHelper#clone(TestData)} method and adds to it all missing key-value pairs from <b>rightTestData</b>.
	 * When both test datas have same keys but different values (aka add conflict) then:
	 * <pre>
	 * - If value type is {@link TestData.Type#STRING} then String value from <b>rightTestData<b/> will be accepted;
	 * - If value type is {@link TestData.Type#TESTDATA} then these nested test datas will be merged (by recursive invocation of same method);
	 * - If value type is {@link TestData.Type#LIST_STRING} and list of Strings from <b>rightTestData</b> has same or bigger size than list size from <b>leftTestData</b> - then
	 * 		list from <b>rightTestData</b> will be accepted. Otherwise result list will have elements from <b>rightTestData</b> plus extra elements from <b>leftTestData</b>.
	 * 		E.g.: if <b>leftTestData</b> has list [“a”, “b”, “c”, “d”] and <b>rightTestData</b> has list [“x”, “y”] then merged list will be [“x”, “y”, “c”, “d”]
	 * - If value type is {@link TestData.Type#LIST_TESTDATA} then test datas from both lists with same indexes will be merged and saved to
	 * 		result {@code List<TestData>} preserving common index. If {@code List<TestData>} from <b>rightTestData</b> has bigger size then
	 * 		all extra TestData elements will be added to the result TestData list.
	 *</pre>
	 *
	 * @param leftTestData leftTestData left test data to be merged
	 * @param rightTestData right test data to be merged (on add conflicts values from this data will be accepted)
	 * @param convertStringToList if <b>true</b> and value from one test data is {@code String} and value from other test data is {@code List<String>} value,
	 *                               then String value will be converted to the single {@code List<String>} of this one value. Then appropriate merging of lists of Strings will be performed.
	 *                               Otherwise (when {@code convertStringToList} is false) - exception will be thrown due to same keys has different value types.
	 * @param convertTestDataToList if <b>true</b> and value from one test data is {@code TestData} and value from other test data is {@code List<TestData>} value,
	 * 	                              then TestData value will be converted to the single {@code List<TestData>}. Then appropriate merging of test data lists will be performed.
	 * 	                              Otherwise (when {@code convertTestDataToList} is false) - exception will be thrown due to same keys has different value types.
	 * @return unified test data which contains unique key-value pairs from <b>leftTestData<b/> and <b>rightTestData<b/>
	 * @throws IstfException if both test datas have same key but different value types
	 */
	public static TestData merge(TestData leftTestData, TestData rightTestData, boolean convertStringToList, boolean convertTestDataToList) {
		return merge(leftTestData, rightTestData, convertStringToList, convertTestDataToList, true);
	}

	private static TestData merge(TestData leftTestData, TestData rightTestData, boolean convertStringToList, boolean convertTestDataToList, boolean isFirstCycleMerge) {
		TestData resultData = clone(leftTestData);
		TestData tdRight = clone(rightTestData);

		if (resultData.equals(tdRight)) {
			if (isFirstCycleMerge) {
				log.warn("Both provided test datas are equal, there is nothing to add");
			}
			return resultData;
		}

		for (String key : resultData.getKeys()) {
			if (!tdRight.containsKey(key)) {
				continue; //nothing to add, tdRight does not have key from tdLeft, tdLeft value is copied to result data
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
				continue; //nothing to add, tdLeft and tdRight values are equal, tdLeft value is copied to result data
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

	/**
	 * Use to simplify adjusting test data.
	 * @param td The input test data to be adjusted.
	 * @param tabElementIsOn Tab class object. Element adjusted lives on this tab.
	 * @param elementLabel getLabel() for MetaData element being manipulated via adjustment.
	 * @param value The new value he element should contain.
	 * @return Adjusted Test Data Object
	 * @author Tyrone Jemison
	 */
	public static TestData adjustTD(TestData td, Class<? extends Tab> tabElementIsOn, String elementLabel, String value){
		td.adjust(TestData.makeKeyPath(tabElementIsOn.getSimpleName(), elementLabel), value);
		return td;
	}

	/**
	 * Use to simplify adjusting test data.
	 * @param td The input test data to be adjusted.
	 * @param tabElementIsOn Tab class object. Element adjusted lives on this tab.
	 * @param subChunkLabel getLabel() for AssetList that contains sub Assets.
	 * @param elementLabel getLabel() for MetaData element being manipulated via adjustment.
	 * @param value The new value he element should contain.
	 * @return Adjusted Test Data Object
	 * @author Tyrone Jemison
	 */
	public static TestData adjustTD(TestData td, Class<? extends Tab> tabElementIsOn, String subChunkLabel, String elementLabel, String value){
		String result = TestData.makeKeyPath(tabElementIsOn.getSimpleName(), subChunkLabel, elementLabel);
		td.adjust(result ,value);
		return td;
	}

	/**
	 * Use to simplify adjusting masking/removing test data.
	 * @param td The input test data to be adjusted.
	 * @param tabElementIsOn Tab class object. Element adjusted lives on this tab.
	 * @param subChunkLabel getLabel() for AssetList that contains sub Assets.
	 * @param elementLabel getLabel() for MetaData element being removed from the test data.
	 * @return Adjusted Test Data Object
	 * @author Tyrone Jemison
	 */
	public TestData maskTD(TestData td, Class<? extends Tab> tabElementIsOn, String subChunkLabel, String elementLabel){
		td.mask(TestData.makeKeyPath(tabElementIsOn.getSimpleName(), subChunkLabel, elementLabel));
		return td;
	}

	/**
	 * Use to simplify adjusting masking/removing test data.
	 * @param td The input test data to be adjusted.
	 * @param tabElementIsOn Tab class object. Element adjusted lives on this tab.
	 * @param elementLabel getLabel() for MetaData element being removed from the test data.
	 * @return Adjusted Test Data Object
	 * @author Tyrone Jemison
	 */
	public TestData maskTD(TestData td, Class<? extends Tab> tabElementIsOn, String elementLabel){
		td.mask(TestData.makeKeyPath(tabElementIsOn.getSimpleName(), elementLabel));
		return td;
	}
}
