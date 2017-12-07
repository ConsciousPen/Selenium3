package aaa.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.datax.TestData;
import toolkit.datax.TestDataException;
import toolkit.exceptions.IstfException;

public class TestDataHelper {
	protected static Logger log = LoggerFactory.getLogger(TestDataHelper.class);

	public static TestData merge(TestData tdLeft, TestData tdRight) {
		if (tdLeft.equals(tdRight)) { //TODO-dchubkov: double check equals for test data
			log.warn("Both provided test datas are equal, first one is returned");
			return tdLeft;
		}

		TestData resultData = tdLeft.resolveLinks();
		for (String tdLeftKey : resultData.getKeys()) {
			if (!tdRight.containsKey(tdLeftKey)) {
				continue; //nothing to merge, tdRight does not have key from tdLeft, tdLeft value is copied to result data
			}

			TestData.Type valueTypeLeft = getValueType(resultData, tdLeftKey);
			TestData.Type valueTypeRight = getValueType(tdRight, tdLeftKey);
			if (!Objects.equals(valueTypeLeft, valueTypeRight)) {
				//TODO-dchubkov: convert tdRight TESTDATA to List<TESTDATA> if tdLeft is LIST_TESTDATA or vise versa
				throw new IstfException(String.format("Both test datas have same key \"%1$s\" but different value types. Left TestData type is \"%2$s\", Right TestData type is \"%3$s\".",
						tdLeftKey, valueTypeLeft, valueTypeRight));
			}
			if (Objects.equals(resultData.getValue(valueTypeLeft, tdLeftKey), tdRight.getValue(valueTypeLeft, tdLeftKey))) {
				continue; //nothing to merge, tdLeft and tdRight values are equal, tdLeft value is copied to result data
			}

			switch (valueTypeLeft) {
				case STRING:
					resultData.adjust(tdLeftKey, tdRight.getValue(tdLeftKey));
					break;
				case TESTDATA:
					resultData.adjust(tdLeftKey, merge(resultData.getTestData(tdLeftKey), tdRight.getTestData(tdLeftKey)));
					break;
				case LIST_STRING:
					List<String> resultList = new ArrayList<>(tdRight.getList(tdLeftKey));
					if (resultList.size() < tdLeft.getList(tdLeftKey).size()) {
						List<String> tdLeftList = tdLeft.getList(tdLeftKey);
						int index = tdLeftList.size() - (tdLeftList.size() - resultList.size());
						while (index < tdLeftList.size()) {
							resultList.add(tdLeftList.get(index));
							index++;
						}
					}
					resultData.adjust(tdLeftKey, resultList);
					break;
				case LIST_TESTDATA:
					List<TestData> resultTestDataList = new ArrayList<>();
					List<TestData> tdLeftList = new ArrayList<>(tdLeft.getTestDataList(tdLeftKey));
					List<TestData> tdRightList = new ArrayList<>(tdRight.getTestDataList(tdLeftKey));
					for (int i = 0; i < tdLeftList.size(); i++) {
						if (i < tdRightList.size()) {
							resultTestDataList.add(merge(tdLeftList.get(i), tdRightList.get(i)));
						} else {
							resultTestDataList.add(tdLeftList.get(i));
						}
					}

					if (tdRightList.size() > tdLeftList.size()) {
						int index = tdRightList.size() - (tdRightList.size() - tdLeftList.size());
						while (index < tdRightList.size()) {
							resultTestDataList.add(tdRightList.get(index));
							index++;
						}
					}
					resultData.adjust(tdLeftKey, resultTestDataList);
					break;
			}
		}

		List<String> newKeys = tdRight.getKeys().stream().filter(tdRightKey -> !resultData.getKeys().contains(tdRightKey)).collect(Collectors.toList());
		for (String key : newKeys) {
			TestData.Type valueType = getValueType(tdRight, key);
			switch (valueType) {
				case STRING:
					resultData.adjust(key, tdRight.getValue(key));
					break;
				case TESTDATA:
					resultData.adjust(key, tdRight.getTestData(key));
					break;
				case LIST_STRING:
					resultData.adjust(key, tdRight.getList(key));
					break;
				case LIST_TESTDATA:
					resultData.adjust(key, tdRight.getTestDataList(key));
					break;
			}
		}
		return resultData.resolveLinks();
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
}
