package aaa.modules.conversion.manual;

import aaa.helpers.TestDataManager;
import aaa.main.modules.customer.CustomerActions;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;
import toolkit.datax.TestData;
import static aaa.common.enums.Constants.States.*;
import java.util.Arrays;
import java.util.List;

public interface ManualConversionHelper {
    TestData tdCustomerIndividual = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
    List<String> maigStates = Arrays.asList(MD, PA, DE, NJ, VA);

    default boolean isMaigConversion(String state) {
        return maigStates.contains(state);
    }

    default TestData modifyTd(TestData testData, TestData tdCustomer, String state) {
        if (testData == null) {
            testData = tdCustomer.getTestData(CustomerActions.InitiateRenewalEntry.class.getSimpleName()).getTestData(getTdName() + "_Default").
                    adjust("InitiateRenewalEntryActionTab|Risk State", state);
        }
        if (isMaigConversion(state)) {
            testData.adjust("InitiateRenewalEntryActionTab|Previous Source System", "MAIG");
        }

        return testData.resolveLinks();
    }

    default TestData modifyForHome(PolicyType type, TestData td) {
        td.adjust("InitiateRenewalEntryActionTab|Product Name", type.getName());
        if (td.getTestData("InitiateRenewalEntryActionTab|Policy Type") == null) {
            td.adjust("InitiateRenewalEntryActionTab|Policy Type",
                    !type.getShortName().contains("_") ? "HO3" : type.getShortName().replaceAll(".+_", ""));
        }
        return td.resolveLinks();
    }

    default String getTdName() {
        return "TestData";
    }

}
