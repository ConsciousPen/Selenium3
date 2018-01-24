package aaa.modules.conversion.manual;

import static aaa.common.enums.Constants.States.*;
import java.util.Arrays;
import java.util.List;
import aaa.helpers.TestDataManager;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.customer.CustomerActions;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import toolkit.datax.TestData;

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
        td.adjust("InitiateRenewalEntryActionTab|Product Name", type.getName()).adjust("InitiateRenewalEntryActionTab|Legacy policy had Multi-Policy discount","Yes");
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

//                if (!type.equals(PolicyType.HOME_SS_HO3)||!type.equals(PolicyType.HOME_SS_HO4)) {
//                    td.mask(TestData.makeKeyPath(HomeSSMetaData.ReportsTab.class.getSimpleName(), HomeSSMetaData.ReportsTab.INSURANCE_SCORE_REPORT.getLabel()));
//                }
                break;
            }
            case "Auto Signature Series":{
                //TODO Add Auto Signature Series Product masks and adjustments deltas from default policy td
                break;
            }
            case "Personal Umbrella Policy": {
                td.adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel() + "[0]",
                        PersonalUmbrellaMetaData.PrefillTab.NamedInsured.OCCUPATION.getLabel()), "index=3")
                        .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(),
                                PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel()))
                        .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(),
                                PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.LEAD_SOURCE.getLabel()))
                        .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.AAA_MEMBERSHIP.getLabel()))
                        .mask(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.DWELLING_ADDRESS.getLabel()))
                        .adjust(TestData.makeKeyPath(PremiumAndCoveragesQuoteTab.class.getSimpleName(),
                                PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), "Pay in Full (Renewal)");
                break;
            }
        }
        return td;
    }



    default String getTdName() {
        return "TestData";
    }

}
