/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.admin.general.numberrange;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.Test;

import aaa.admin.metadata.general.GeneralMetaData;
import aaa.admin.modules.general.numberrange.INumberRange;
import aaa.admin.modules.general.numberrange.NumberRangeType;
import aaa.admin.pages.general.NumberRangePage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Andrey Shashenka
 * @name Test Basic operations execution for # Ranges section
 * @scenario
 * 1. Open Admin - General - # Ranges
 * 2. Add new range with correct data
 * 3. Search created range
 * 4. Verify that created range appears in search results
 * 5. Delete range
 * 6. Search once again
 * 7. Verify that range is absent in search results
 * @details https://jira.exigeninsurance.com/browse/EISDEV-141077
 */

public class TestNumberRangeBasicOperations extends BaseTest {

    private NumberRangeType numberRangeType = NumberRangeType.NUMBER_RANGE;
    private INumberRange numberRange = numberRangeType.get();

    private TestData tdNumberRange = testDataManager.numberRange.get(numberRangeType);

    Map<String, String> mapSearch = new HashMap<>();

    @Test
    @TestInfo(component = "Platform.Admin")
    public void testNumberRangeBasicOperations() {

        storeSearchData();

        adminApp().open();

        log.info("TEST: create '# Range' entity");
        numberRange.create(tdNumberRange.getTestData("DataGather", "TestData"));

        log.info("TEST: search and verify created '# Range' entity");
        numberRange.search(tdNumberRange.getTestData("SearchData", "TestData"));

        NumberRangePage.tableSearchResults.getRow(mapSearch).verify.present("Created '# Range' was not founded");

        log.info("TEST: eliminate '# Range' entity");
        numberRange.eliminate().perform(NumberRangePage.tableSearchResults.getRowsCount());
        numberRange.search(tdNumberRange.getTestData("SearchData", "TestData"));

        log.info("TEST: search and verify eliminated '# Range' entity");
        NumberRangePage.tableSearchResults.getRow(mapSearch).verify.present("Created '# Range' was founded", false);
    }

    private void storeSearchData() {
        mapSearch.put(GeneralMetaData.AddNumberRange.START_NUMBER.getLabel(), tdNumberRange.getValue("DataGather",
                "TestData", GeneralMetaData.AddNumberRange.class.getSimpleName(),
                GeneralMetaData.AddNumberRange.START_NUMBER.getLabel()));
        mapSearch.put(GeneralMetaData.AddNumberRange.END_NUMBER.getLabel(), tdNumberRange.getValue("DataGather",
                "TestData", GeneralMetaData.AddNumberRange.class.getSimpleName(),
                GeneralMetaData.AddNumberRange.END_NUMBER.getLabel()));
    }
}
