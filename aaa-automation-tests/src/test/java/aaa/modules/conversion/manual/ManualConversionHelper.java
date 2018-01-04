package aaa.modules.conversion.manual;

import aaa.helpers.TestDataManager;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
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

    default TestData modifyInitiationTd(TestData testData, TestData tdCustomer, String state) {
        if (testData == null) {
            testData = tdCustomer.getTestData(CustomerActions.InitiateRenewalEntry.class.getSimpleName()).getTestData(getTdName() + "_Default").
                    adjust("InitiateRenewalEntryActionTab|Risk State", state);
        }
        if (isMaigConversion(state)) {
            testData.adjust("InitiateRenewalEntryActionTab|Previous Source System", "MAIG");
        }

        return testData.resolveLinks();
    }

    default TestData modifyInitiationTdForHome(PolicyType type, TestData td) {
        td.adjust("InitiateRenewalEntryActionTab|Product Name", type.getName());
        if (td.getTestData("InitiateRenewalEntryActionTab|Policy Type") == null) {
            td.adjust("InitiateRenewalEntryActionTab|Policy Type",
                    !type.getShortName().contains("_") ? "HO3" : type.getShortName().replaceAll(".+_", ""));
        }
        return td.resolveLinks();
    }


    default TestData modifyConversionPolicyDefaultTD(TestData td, PolicyType type) {

        switch (type.getName()) {
            case "Homeowners Signature Series": {
                td.mask(TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel()))
                        .mask(TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.LEAD_SOURCE.getLabel()))
                        .mask(TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel()))
                        .adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP
                                .getLabel(), HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel()), "Yes")
                        .adjust(TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN
                                .getLabel()), "Pay in Full (Renewal)")
                        .adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.NAMED_INSURED
                                .getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.MARITAL_STATUS.getLabel()), "Married")
                        .mask(TestData.makeKeyPath(PurchaseMetaData.PurchaseTab.class.getSimpleName()));

                if (!type.equals(PolicyType.HOME_SS_HO3)) {
                    td.mask(TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName(), HomeSSMetaData.ReportsTab.INSURANCE_SCORE_REPORT.getLabel()));
                }
                break;
            }
            case "Auto Signature Series":{
                //TODO Add Auto Signature Series Product masks and adjustments deltas from default policy td
                break;
            }
            case "Personal Umbrella Policy": {
                break;
            }
        }
        return td;
    }



    default String getTdName() {
        return "TestData";
    }

}
