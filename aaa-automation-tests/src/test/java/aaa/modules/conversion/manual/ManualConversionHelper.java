package aaa.modules.conversion.manual;

import aaa.common.enums.Constants;
import aaa.helpers.TestDataManager;
import aaa.main.modules.customer.CustomerActions;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;
import toolkit.datax.TestData;

public interface ManualConversionHelper {
    TestData tdCustomerIndividual = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);

    default boolean isMaigConversion(String state) {
        switch (state) {
            case Constants.States.MD:
            case Constants.States.PA:
            case Constants.States.DE:
            case Constants.States.NJ:
            case Constants.States.VA:
                return true;
            default:
                return false;
        }
    }

    default TestData modifyTd(TestData testData, TestData tdCustomer, String state) {
        if (testData == null) {
           testData =  tdCustomer.getTestData(CustomerActions.InitiateRenewalEntry.class.getSimpleName()).getTestData(getTdName() + "_Default").
                    adjust("InitiateRenewalEntryActionTab|Risk State", state);
        }
        if (isMaigConversion(state)) {
            testData.adjust("InitiateRenewalEntryActionTab|Previous Source System", "MAIG");
        }

        return testData.resolveLinks();
    }

    default TestData modifyForHome(PolicyType type, TestData td){
        td.adjust("InitiateRenewalEntryActionTab|Product Name", type.getName());
        if(td.getTestData("InitiateRenewalEntryActionTab|Policy Type") == null){
            td.adjust("InitiateRenewalEntryActionTab|Policy Type",
                    !type.getShortName().contains("_") ? "HO3" : type.getShortName().replaceAll(".+_", ""));
        }
       return td.resolveLinks();
    }

    default String getTdName() {
        return "TestData";
    }



}
