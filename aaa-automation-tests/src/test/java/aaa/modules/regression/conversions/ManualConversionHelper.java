package aaa.modules.regression.conversions;

import aaa.helpers.TestDataManager;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.customer.CustomerActions;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import toolkit.datax.TestData;

import java.util.Arrays;
import java.util.List;

import static aaa.common.enums.Constants.States.*;

@Deprecated
//ToDo: Get rid of this interface as soon as all dependencies on it are eliminated.
//Note: Please, use PolicyBaseTest#initiateManualConversionForTest and PolicyBaseTest#getConversionPolicyTD instead
public interface ManualConversionHelper {
    TestData tdCustomerIndividual = new TestDataManager().customer.get(CustomerType.INDIVIDUAL);
    List<String> maigStates = Arrays.asList(MD, PA, DE, NJ, VA);

    default boolean isMaigConversion(String state) {
        return maigStates.contains(state);
    }

    default TestData modifyInitiationTd(TestData testData, TestData tdCustomer, String state) {
        if (testData == null) {
            testData = tdCustomer.getTestData(CustomerActions.InitiateRenewalEntry.class.getSimpleName()).getTestData(getTdName() + "_Default").
                    adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.RISK_STATE.getLabel()), state);
        }
        if (isMaigConversion(state)) {
            testData.adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.PREVIOUS_SOURCE_SYSTEM.getLabel()), "MAIG");
        }

        return testData.resolveLinks();
    }

    default TestData modifyInitiationTdForHome(PolicyType type, TestData td) {
        td.adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.PRODUCT_NAME.getLabel()), type.getName());
        if (td.getTestData(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.POLICY_TYPE.getLabel())) == null) {
            td.adjust(TestData.makeKeyPath(CustomerMetaData.InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.POLICY_TYPE.getLabel()),
                    !type.getShortName().contains("_") ? "HO3" : type.getShortName().replaceAll(".+_", ""))
                    .adjust(TestData.makeKeyPath(InitiateRenewalEntryActionTab.class.getSimpleName(), CustomerMetaData.InitiateRenewalEntryActionTab.LEGACY_POLICY_HAD_MPD_DISCOUNT.getLabel()), "No");
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
            case "Auto Signature Series": {
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
