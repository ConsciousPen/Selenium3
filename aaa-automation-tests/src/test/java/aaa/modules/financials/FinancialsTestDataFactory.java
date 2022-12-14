package aaa.modules.financials;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import aaa.common.enums.Constants;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.policy.*;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;

public class FinancialsTestDataFactory extends PolicyBaseTest {

    protected static final List<String> ALL_POLICIES = Collections.synchronizedList(new ArrayList<>());

    private static Map<String, String> underlyingHOPolicies = Collections.synchronizedMap(new HashMap<>());
    private static Map<String, String> underlyingAutoPolicies = Collections.synchronizedMap(new HashMap<>());

    private static final String CA_SELECT = "AutoCA";
    private static final String CA_CHOICE = "AutoCAC";
    private static final String AUTO_SS = "AutoSS";
    private static final String HOME_SS_HO3 = "HomeSS_HO3";
    private static final String HOME_SS_HO4 = "HomeSS_HO4";
    private static final String HOME_SS_HO6 = "HomeSS_HO6";
    private static final String HOME_SS_DP3 = "HomeSS_DP3";
    private static final String HOME_CA_HO3 = "HomeCA_HO3";
    private static final String HOME_CA_HO4 = "HomeCA_HO4";
    private static final String HOME_CA_HO6 = "HomeCA_HO6";
    private static final String HOME_CA_DP3 = "HomeCA_DP3";
    private static final String PUP = "PUP";

    @Override
    protected TestData getPolicyTD() {
        TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData");
        if (getPolicyType().equals(PolicyType.PUP)) {
            td = new PrefillTab().adjustWithRealPolicies(td, getPupUnderlyingPolicies());
            td.adjust(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.dataOf(
                    PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA.getLabel(), "contains=$2,000,000"));
        }
        return td;
    }

    protected TestData getEndorsementTD(LocalDateTime effDate) {
        TestData td =  getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Endorsement"), "TestData");
        String type = getPolicyType().getShortName();
        String date = formatDateToString(effDate);
        switch (type) {
            case CA_SELECT:
            case CA_CHOICE:
                td.adjust(TestData.makeKeyPath(AutoCaMetaData.EndorsementActionTab.class.getSimpleName(), AutoCaMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()), date);
                break;
            case AUTO_SS:
                td.adjust(TestData.makeKeyPath(AutoSSMetaData.EndorsementActionTab.class.getSimpleName(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()), date);
                break;
            case HOME_SS_HO3:
            case HOME_SS_HO4:
            case HOME_SS_HO6:
            case HOME_SS_DP3:
                td.adjust(TestData.makeKeyPath(HomeSSMetaData.EndorsementActionTab.class.getSimpleName(), HomeSSMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()), date);
                break;
            case HOME_CA_HO3:
            case HOME_CA_HO4:
            case HOME_CA_HO6:
            case HOME_CA_DP3:
                td.adjust(TestData.makeKeyPath(HomeCaMetaData.EndorsementActionTab.class.getSimpleName(), HomeCaMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()), date);
                break;
            case PUP:
                td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.EndorsementActionTab.class.getSimpleName(), PersonalUmbrellaMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel()), date);
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    protected TestData getCancellationTD(LocalDateTime effDate) {
        TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Cancellation"), "TestData");
        String type = getPolicyType().getShortName();
        String date = formatDateToString(effDate);
        switch (type) {
            case CA_SELECT:
                td.adjust(TestData.makeKeyPath(AutoCaMetaData.CancellationActionTab.class.getSimpleName(), AutoCaMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "contains=Non-Payment");
                td.adjust(TestData.makeKeyPath(AutoCaMetaData.CancellationActionTab.class.getSimpleName(), AutoCaMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            case CA_CHOICE:
                td.adjust(TestData.makeKeyPath(AutoCaMetaData.CancellationActionTab.class.getSimpleName(), AutoCaMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "contains=Insured's Request");
                td.adjust(TestData.makeKeyPath(AutoCaMetaData.CancellationActionTab.class.getSimpleName(), AutoCaMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            case AUTO_SS:
                td.adjust(TestData.makeKeyPath(AutoSSMetaData.CancellationActionTab.class.getSimpleName(), AutoSSMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "contains=Insured's Request");
                td.adjust(TestData.makeKeyPath(AutoSSMetaData.CancellationActionTab.class.getSimpleName(), AutoSSMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            case HOME_SS_HO3:
            case HOME_SS_HO4:
            case HOME_SS_HO6:
            case HOME_SS_DP3:
                td.adjust(TestData.makeKeyPath(HomeSSMetaData.CancellationActionTab.class.getSimpleName(), HomeSSMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "contains=Insured's Request");
                td.adjust(TestData.makeKeyPath(HomeSSMetaData.CancellationActionTab.class.getSimpleName(), HomeSSMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            case HOME_CA_HO3:
            case HOME_CA_HO4:
            case HOME_CA_HO6:
            case HOME_CA_DP3:
                td.adjust(TestData.makeKeyPath(HomeCaMetaData.CancelActionTab.class.getSimpleName(), HomeCaMetaData.CancelActionTab.CANCELLATION_REASON.getLabel()), "contains=Insured's Request");
                td.adjust(TestData.makeKeyPath(HomeCaMetaData.CancelActionTab.class.getSimpleName(), HomeCaMetaData.CancelActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            case PUP:
                td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.CancellationActionTab.class.getSimpleName(),
                        PersonalUmbrellaMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "contains=Insured's Request");
                td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.CancellationActionTab.class.getSimpleName(),
                        PersonalUmbrellaMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    protected TestData getCancellationNonPaymentTd(LocalDateTime cxEffDate) {
        TestData td;
        String type = getPolicyType().getShortName();
        String date = formatDateToString(cxEffDate);
        switch (type) {
            case HOME_SS_HO3:
            case HOME_SS_HO4:
            case HOME_SS_HO6:
            case HOME_SS_DP3:
                td = getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Cancellation"), "TestData")
                        .adjust(TestData.makeKeyPath(HomeSSMetaData.CancellationActionTab.class.getSimpleName(),
                                HomeSSMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "contains=Non-Payment")
                        .adjust(TestData.makeKeyPath(HomeSSMetaData.CancellationActionTab.class.getSimpleName(),
                                HomeSSMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            case PUP:
                td = getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Cancellation"), "TestData")
                        .adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.CancellationActionTab.class.getSimpleName(),
                                PersonalUmbrellaMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "contains=Non-Payment")
                        .adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.CancellationActionTab.class.getSimpleName(),
                                PersonalUmbrellaMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    protected TestData getChangeRenewalLapseTd(LocalDateTime renewalEffDate) {
        TestData td;
        String type = getPolicyType().getShortName();
        String date = formatDateToString(renewalEffDate);
        switch (type) {
            case HOME_SS_HO3:
            case HOME_SS_HO4:
            case HOME_SS_HO6:
            case HOME_SS_DP3:
                td = DataProviderFactory.dataOf(HomeSSMetaData.ManualRenewalWithOrWithoutLapseActionTab.class.getSimpleName(), DataProviderFactory.dataOf(
                        HomeSSMetaData.ManualRenewalWithOrWithoutLapseActionTab.REVISED_RENEWAL_DATE.getLabel(), date,
                        HomeSSMetaData.ManualRenewalWithOrWithoutLapseActionTab.LAPSE_CHANGE_REASON.getLabel(), "index=1"));
                break;
            case PUP:
                td = DataProviderFactory.dataOf(PersonalUmbrellaMetaData.ManualRenewalWithOrWithoutLapseActionTab.class.getSimpleName(), DataProviderFactory.dataOf(
                        PersonalUmbrellaMetaData.ManualRenewalWithOrWithoutLapseActionTab.REVISED_RENEWAL_DATE.getLabel(), date,
                        PersonalUmbrellaMetaData.ManualRenewalWithOrWithoutLapseActionTab.LAPSE_CHANGE_REASON.getLabel(), "index=1"));
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    protected TestData getReinstatementTD() {
        TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Reinstatement"), "TestData");
        if (getPolicyType().equals(PolicyType.AUTO_CA_CHOICE)) {
            return td.adjust(TestData.makeKeyPath(AutoCaMetaData.ReinstatementActionTab.class.getSimpleName(),
                    AutoCaMetaData.ReinstatementActionTab.REINSTATE_DATE.getLabel()), "$<today:MM/dd/yyyy>");
        }
        return td;
    }

    protected TestData getRemoveReinstatementLapseTd(LocalDateTime cxDate) {
        if (isStateCA()) {
            return getPolicyTD("ReinstatementChangeLapse", "TestData").adjust(TestData.makeKeyPath(HomeCaMetaData.ChangeReinstatementLapsePeriodActionTab.class.getSimpleName(),
                    HomeCaMetaData.ChangeReinstatementLapsePeriodActionTab.REVISED_REINSTATEMENT_DATE.getLabel()), formatDateToString(cxDate));
        }
        return getPolicyTD("ReinstatementChangeLapse", "TestData").adjust(TestData.makeKeyPath(HomeSSMetaData.ChangeReinstatementLapseActionTab.class.getSimpleName(),
                HomeSSMetaData.ChangeReinstatementLapseActionTab.REVISED_REINSTATEMENT_DATE.getLabel()), formatDateToString(cxDate));
    }

    protected TestData adjustTdPolicyEffDate(TestData td, LocalDateTime date) {
        String type = getPolicyType().getShortName();
        String sDate = formatDateToString(date);
        switch (type) {
            case CA_SELECT:
            case CA_CHOICE:
                td.adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
                        AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), sDate);
                break;
            case AUTO_SS:
                td.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
                        AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), sDate);
                break;
            case HOME_SS_HO3:
            case HOME_SS_HO4:
            case HOME_SS_HO6:
            case HOME_SS_DP3:
                td.adjust(TestData.makeKeyPath(HomeSSMetaData.GeneralTab.class.getSimpleName(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel()), sDate);
                break;
            case HOME_CA_HO3:
            case HOME_CA_HO4:
            case HOME_CA_HO6:
            case HOME_CA_DP3:
                td.adjust(TestData.makeKeyPath(HomeCaMetaData.GeneralTab.class.getSimpleName(), HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(),
                        HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel()), sDate);
                break;
            case PUP:
                td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(),
                        PersonalUmbrellaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE.getLabel()), sDate);
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    protected TestData adjustTdWithEmpBenefit(TestData td) {
        String type = getPolicyType().getShortName();
        switch (type) {
            case CA_SELECT:
            case CA_CHOICE:
                td.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.EMPLOYEE_BENEFIT_TYPE.getLabel()), "Active employee");
                td.adjust(TestData.makeKeyPath(AutoCaMetaData.DriverTab.class.getSimpleName(), AutoCaMetaData.DriverTab.EMPLOYEE_ID.getLabel()), "123456789");
                break;
            case AUTO_SS:
                td.adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(),AutoSSMetaData.DriverTab.AFFINITY_GROUP.getLabel()), "AAA Employee");
                break;
            case HOME_SS_HO3:
            case HOME_SS_HO4:
            case HOME_SS_HO6:
            case HOME_SS_DP3:
                td.adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(),
                        HomeSSMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel()), "Yes");
                break;
            case HOME_CA_HO3:
            case HOME_CA_HO4:
            case HOME_CA_HO6:
            case HOME_CA_DP3:
                td.adjust(TestData.makeKeyPath(HomeCaMetaData.ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(),
                        HomeCaMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel()), "Yes");
                break;
            case PUP:
                td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel() + "[0]",
                        PersonalUmbrellaMetaData.PrefillTab.NamedInsured.AAA_EMPLOYEE.getLabel()), "Yes");
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    protected TestData adjustTdMonthlyPaymentPlan(TestData td) {
        String type = getPolicyType().getShortName();
        switch (type) {
            case CA_SELECT:
            case CA_CHOICE:
                td.adjust(TestData.makeKeyPath(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
                        AutoCaMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.STANDARD_MONTHLY);
                break;
            case AUTO_SS:
                td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
                        AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.AUTO_ELEVEN_PAY);
                break;
            case HOME_SS_HO3:
            case HOME_SS_HO4:
            case HOME_SS_HO6:
            case HOME_SS_DP3:
                td.adjust(TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(),
                        HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.ELEVEN_PAY);
                break;
            case HOME_CA_HO3:
            case HOME_CA_HO4:
            case HOME_CA_HO6:
            case HOME_CA_DP3:
                td.adjust(TestData.makeKeyPath(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(),
                        HomeCaMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.MONTHLY_STANDARD);
                break;
            case PUP:
                if (getState().equals(Constants.States.CA)) {
                    td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.class.getSimpleName(),
                            PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.MONTHLY_STANDARD);
                } else {
                    td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.class.getSimpleName(),
                            PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.ELEVEN_PAY);
                }
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    protected TestData getAddPremiumTD() {
        TestData td;
        String type = getPolicyType().getShortName();
        switch (type) {
            case CA_SELECT:
                td = getCaSelectAddPremiumTd();
                break;
            case CA_CHOICE:
                td = getCaChoiceAddPremiumTd();
                break;
            case AUTO_SS:
                td = getSSAutoAddPremiumTd();
                break;
            case HOME_SS_HO3:
            case HOME_SS_HO4:
            case HOME_SS_HO6:
            case HOME_SS_DP3:
                td = getSSHomeAddPremiumTd();
                break;
            case HOME_CA_HO3:
            case HOME_CA_HO4:
            case HOME_CA_HO6:
            case HOME_CA_DP3:
                td = getCaHomeAddPremiumTd();
                break;
            case PUP:
                td = getPupAddPremiumTd();
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    protected TestData getReducePremiumTD() {
        TestData td;
        String type = getPolicyType().getShortName();
        switch (type) {
            case CA_SELECT:
                td = getCaSelectReducePremiumTd();
                break;
            case CA_CHOICE:
                td = getCaChoiceReducePremiumTd();
                break;
            case AUTO_SS:
                td = getSSAutoReducePremiumTd();
                break;
            case HOME_SS_HO3:
            case HOME_SS_HO4:
            case HOME_SS_HO6:
            case HOME_SS_DP3:
                td = getSSHomeReducePremiumTd();
                break;
            case HOME_CA_HO3:
            case HOME_CA_HO4:
            case HOME_CA_HO6:
            case HOME_CA_DP3:
                td = getCaHomeReducePremiumTd();
                break;
            case PUP:
                td = getPupReducePremiumTd();
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    protected TestData getNPBEndorsementTD() {
        TestData td;
        String type = getPolicyType().getShortName();
        switch (type) {
            case CA_SELECT:
            case CA_CHOICE:
                td = getEmptyTestDataCaAuto().adjust(AutoCaMetaData.GeneralTab.class.getSimpleName(), DataProviderFactory.dataOf(
                        AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), DataProviderFactory.dataOf(
                                AutoCaMetaData.GeneralTab.NamedInsuredInformation.MIDDLE_NAME.getLabel(), "Test")));
                break;
            case AUTO_SS:
                td = getEmptyTestDataSSAuto().adjust(AutoSSMetaData.GeneralTab.class.getSimpleName(), DataProviderFactory.dataOf(
                        AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel(), DataProviderFactory.dataOf(
                                AutoSSMetaData.GeneralTab.NamedInsuredInformation.MIDDLE_NAME.getLabel(), "Test")));
                break;
            case HOME_SS_DP3:
            case HOME_SS_HO3:
                td = getEmptyTestDataSSHome()
                        .adjust(HomeSSMetaData.ApplicantTab.class.getSimpleName(), DataProviderFactory.dataOf(
                                HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), DataProviderFactory.dataOf(
                                        HomeSSMetaData.ApplicantTab.NamedInsured.MIDDLE_NAME.getLabel(), "Test")))
                        .adjust(TestData.makeKeyPath(HomeSSMetaData.UnderwritingAndApprovalTab.class.getSimpleName(),
                                HomeSSMetaData.UnderwritingAndApprovalTab.UNDERWRITER_SELECTED_INSPECTION_TYPE.getLabel()), "index=1");
                break;
            case HOME_SS_HO4:
            case HOME_SS_HO6:
                td = getEmptyTestDataSSHome()
                        .adjust(HomeSSMetaData.ApplicantTab.class.getSimpleName(), DataProviderFactory.dataOf(
                                HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), DataProviderFactory.dataOf(
                                        HomeSSMetaData.ApplicantTab.NamedInsured.MIDDLE_NAME.getLabel(), "Test")));
                break;
            case HOME_CA_HO3:
            case HOME_CA_HO4:
            case HOME_CA_HO6:
            case HOME_CA_DP3:
                td = getEmptyTestDataCaHome().adjust(HomeCaMetaData.ApplicantTab.class.getSimpleName(), DataProviderFactory.dataOf(
                        HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), DataProviderFactory.dataOf(
                                HomeCaMetaData.ApplicantTab.NamedInsured.MIDDLE_NAME.getLabel(), "Test")));
                break;
            case PUP:
                td = getPupNonPremiumBearingTd();
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    protected TestData getRenewalFillTd() {
        TestData td;
        String type = getPolicyType().getShortName();
        switch (type) {
            case CA_SELECT:
            case CA_CHOICE:
                td = getEmptyTestDataCaAuto().adjust(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName(), DataProviderFactory.dataOf(
                        AutoCaMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM.getLabel(), "Click"));
                break;
            case AUTO_SS:
                td = getEmptyTestDataSSAuto().adjust(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), DataProviderFactory.dataOf(
                        AutoSSMetaData.PremiumAndCoveragesTab.CALCULATE_PREMIUM.getLabel(), "Click"));
                break;
            case HOME_SS_HO3:
            case HOME_SS_HO4:
            case HOME_SS_HO6:
            case HOME_SS_DP3:
                td = getEmptyTestDataSSHome().adjust(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.dataOf(
                        HomeSSMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM.getLabel(), "Click"));
                break;
            case HOME_CA_HO3:
            case HOME_CA_HO4:
            case HOME_CA_HO6:
            case HOME_CA_DP3:
                td = getEmptyTestDataCaHome().adjust(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.dataOf(
                        HomeCaMetaData.PremiumsAndCoveragesQuoteTab.CALCULATE_PREMIUM_BUTTON.getLabel(), "Click"));
                break;
            case PUP:
                if (getState().equals(Constants.States.CA)) {
                    td = getEmptyTestDataCAPup();
                } else {
                    td = getEmptyTestDataSSPup();
                }
                td.adjust(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.dataOf(
                        PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.CALCULATE_PREMIUM.getLabel(), "Click"))
                        .adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.class.getSimpleName(),
                                PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel()), DataProviderFactory.dataOf(
                                        PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.MAKE.getLabel(), "CHEVROLET",
                                        PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.Automobiles.MODEL.getLabel(), "AVEO"));
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    protected TestData getAutoSSFeesTd(LocalDateTime effDate) {
        TestData td = adjustTdPolicyEffDate(getPolicyTD(), effDate);
        switch (getState()) {
            case Constants.States.NJ:
                adjustTdMonthlyPaymentPlan(td)
                        .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel(),
                                AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_EXPIRATION_DATE.getLabel()), formatDateToString(effDate))
                        .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
                                AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel()), "One")
                        .adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]",
                                AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel()), "DUI")
                        .adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel()), Constants.States.VA)
                        .adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "B15375001")
                        .adjust(TestData.makeKeyPath(AutoSSMetaData.DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.FINANCIAL_RESPONSIBILITY_FILING_NEEDED.getLabel()), "Yes")
                        .adjust(TestData.makeKeyPath(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.CASE_NUMBER.getLabel()), "VA651321565")
                        .adjust(AutoSSMetaData.ErrorTab.class.getSimpleName(), getAutoSSErrorTabOverride());
                break;
            case Constants.States.WV:
                td.adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
                        AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.MONTHLY);
                break;
            default:
                // do nothing
        }
        return td;
    }

    protected String formatDateToString(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }

    private Map<String, String> getPupUnderlyingPolicies() {
        Map<String, String> policies = new LinkedHashMap<>();
        PolicyType typeHome;
        PolicyType typeAuto;
        String hoPolicy;
        String autoPolicy;
        String state = getState().intern();
        synchronized (state) {
            if (getState().equals(Constants.States.CA)) {
                typeHome = PolicyType.HOME_CA_HO3;
                typeAuto = PolicyType.AUTO_CA_SELECT;
            } else {
                typeHome = PolicyType.HOME_SS_HO3;
                typeAuto = PolicyType.AUTO_SS;
            }
            if (!underlyingHOPolicies.containsKey(state)) {
                typeHome.get().createPolicy(getStateTestData(testDataManager.policy.get(typeHome), "DataGather", "TestData"));
                hoPolicy = PolicySummaryPage.getPolicyNumber();
                underlyingHOPolicies.put(state, hoPolicy);
                ALL_POLICIES.add(hoPolicy);
            }
            if (!underlyingAutoPolicies.containsKey(state)) {
                typeAuto.get().createPolicy(getStateTestData(testDataManager.policy.get(typeAuto), "DataGather", "TestData"));
                autoPolicy = PolicySummaryPage.getPolicyNumber();
                underlyingAutoPolicies.put(state, autoPolicy);
                ALL_POLICIES.add(autoPolicy);
            }
            policies.put("Primary_HO3", underlyingHOPolicies.get(state));
            policies.put("Primary_Auto", underlyingAutoPolicies.get(state));
        }
        return policies;
    }

    private TestData getCaSelectAddPremiumTd() {
        return getEmptyTestDataCaAuto()
                .adjust(AutoCaMetaData.VehicleTab.class.getSimpleName(), getCaSelectVehicleTabTd())
                .adjust(AutoCaMetaData.AssignmentTab.class.getSimpleName(), getCaAutoAssignmentTabTd());
    }

    private TestData getCaChoiceAddPremiumTd() {
        return getCaSelectAddPremiumTd().adjust(AutoCaMetaData.VehicleTab.class.getSimpleName(), getCaChoiceVehicleTabTd());
    }

    private TestData getSSAutoAddPremiumTd() {
        return getEmptyTestDataSSAuto().adjust(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), DataProviderFactory.dataOf(
                AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel(), "contains=$500,000/$500,000",
                AutoSSMetaData.PremiumAndCoveragesTab.COMPREGENSIVE_DEDUCTIBLE.getLabel(), "contains=$100",
                AutoSSMetaData.PremiumAndCoveragesTab.COLLISION_DEDUCTIBLE.getLabel(), "contains=$100"));
    }

    private TestData getCaSelectReducePremiumTd() {
        return getEmptyTestDataCaAuto().adjust(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName(), DataProviderFactory.dataOf(
                AutoCaMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel(), "index=1",
                AutoCaMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY.getLabel(), "index=1"));
    }

    private TestData getCaChoiceReducePremiumTd() {
        return getCaSelectReducePremiumTd().adjust(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(), DataProviderFactory.dataOf(
                AutoCaMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(), DataProviderFactory.dataOf(
                        AutoCaMetaData.DocumentsAndBindTab.RequiredToBind.REDUCING_UNINSURED_UNDERINSURED_MOTORIST_COVERAGE.getLabel(), "Physically Signed")));
    }

    private TestData getSSAutoReducePremiumTd() {
        return getEmptyTestDataSSAuto().adjust(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), DataProviderFactory.dataOf(
                AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY.getLabel(), "index=1",
                AutoSSMetaData.PremiumAndCoveragesTab.PROPERTY_DAMAGE_LIABILITY.getLabel(), "index=1"));
    }

    private TestData getSSHomeAddPremiumTd() {
        TestData td = getEmptyTestDataSSHome().adjust(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.dataOf(
                HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), "contains=$1,000,000",
                HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), "contains=$500"));
        if (getPolicyType().equals(PolicyType.HOME_SS_HO3) || getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            td.adjust(TestData.makeKeyPath(HomeSSMetaData.UnderwritingAndApprovalTab.class.getSimpleName(),
                    HomeSSMetaData.UnderwritingAndApprovalTab.UNDERWRITER_SELECTED_INSPECTION_TYPE.getLabel()), "index=1");
        }
        return td;
    }

    private TestData getCaHomeAddPremiumTd() {
        return getEmptyTestDataSSHome().adjust(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.dataOf(
                HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), "contains=$1,000,000",
                HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), "contains=$250"));
    }

    private TestData getSSHomeReducePremiumTd() {
        TestData td = getEmptyTestDataSSHome().adjust(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.dataOf(
                HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), "contains=$100,000",
                HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), "contains=$5,000"));
        if (getPolicyType().equals(PolicyType.HOME_SS_HO3) || getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            td.adjust(TestData.makeKeyPath(HomeSSMetaData.UnderwritingAndApprovalTab.class.getSimpleName(),
                    HomeSSMetaData.UnderwritingAndApprovalTab.UNDERWRITER_SELECTED_INSPECTION_TYPE.getLabel()), "index=1");
        }
        return td;
    }

    private TestData getCaHomeReducePremiumTd() {
        return getEmptyTestDataSSHome().adjust(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.dataOf(
                HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), "contains=$100,000",
                HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), "contains=$5,000"));
    }

    private TestData getPupAddPremiumTd() {
        TestData td;
        if (getState().equals(Constants.States.CA)) {
            td = getEmptyTestDataCAPup();
        } else {
            td = getEmptyTestDataSSPup();
        }
        return td.adjust(PersonalUmbrellaMetaData.ErrorTab.class.getSimpleName(), getPupErrorTabOverride())
                .adjust(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.dataOf(
                    PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA.getLabel(), "contains=$3,000,000"));
    }

    private TestData getPupReducePremiumTd() {
        TestData td;
        if (getState().equals(Constants.States.CA)) {
            td = getEmptyTestDataCAPup();
        } else {
            td = getEmptyTestDataSSPup();
        }
        return td.adjust(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.dataOf(
                PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA.getLabel(), "contains=$1,000,000"));
    }

    private TestData getPupErrorTabOverride() {
        List<TestData> errorsOverride = new ArrayList<>();
        TestData overrideTd = DataProviderFactory.dataOf(
                PersonalUmbrellaMetaData.ErrorTab.ErrorsOverride.MESSAGE.getLabel(), "contains=Underwriting approval required for Liability Coverage limits greater than",
                PersonalUmbrellaMetaData.ErrorTab.ErrorsOverride.OVERRIDE.getLabel(), "true",
                PersonalUmbrellaMetaData.ErrorTab.ErrorsOverride.APPROVAL.getLabel(), "true",
                PersonalUmbrellaMetaData.ErrorTab.ErrorsOverride.DURATION.getLabel(), "Life",
                PersonalUmbrellaMetaData.ErrorTab.ErrorsOverride.REASON_FOR_OVERRIDE.getLabel(), "index=1");
        errorsOverride.add(overrideTd);
        return DataProviderFactory.dataOf(PersonalUmbrellaMetaData.ErrorTab.ERROR_OVERRIDE.getLabel(), errorsOverride);
    }

    private TestData getAutoSSErrorTabOverride() {
        List<TestData> errorsOverride = new ArrayList<>();
        TestData overrideTd = DataProviderFactory.dataOf(
                AutoSSMetaData.ErrorTab.ErrorsOverride.MESSAGE.getLabel(), "contains=Driver with a Major violation, including a DUI is unacceptable",
                AutoSSMetaData.ErrorTab.ErrorsOverride.OVERRIDE.getLabel(), "true",
                AutoSSMetaData.ErrorTab.ErrorsOverride.APPROVAL.getLabel(), "true",
                AutoSSMetaData.ErrorTab.ErrorsOverride.DURATION.getLabel(), "Life",
                AutoSSMetaData.ErrorTab.ErrorsOverride.REASON_FOR_OVERRIDE.getLabel(), "index=1");
        errorsOverride.add(overrideTd);
        return DataProviderFactory.dataOf(AutoSSMetaData.ErrorTab.ERROR_OVERRIDE.getLabel(), errorsOverride);
    }

    private TestData getPupNonPremiumBearingTd() {
        TestData td;
        if (getState().equals(Constants.States.CA)) {
            td = getEmptyTestDataCAPup();
        } else {
            td = getEmptyTestDataSSPup();
        }
        return td.adjust(PersonalUmbrellaMetaData.GeneralTab.class.getSimpleName(), DataProviderFactory.dataOf(
                TestData.makeKeyPath(PersonalUmbrellaMetaData.GeneralTab.class.getSimpleName(), PersonalUmbrellaMetaData.GeneralTab.NAMED_INSURED_CONTACT_INFORMATION.getLabel(),
                PersonalUmbrellaMetaData.GeneralTab.NamedInsuredContactInformation.MOBILE_PHONE.getLabel()), "6025551212"));
    }

    private TestData getCaAutoAssignmentTabTd() {
        List<TestData> driverRelationshipTable = new ArrayList<>();
        TestData primaryDriver = DataProviderFactory.dataOf(AutoCaMetaData.AssignmentTab.DriverVehicleRelationshipTableRow.PRIMARY_DRIVER.getLabel(), "index=1");
        driverRelationshipTable.add(primaryDriver);
        driverRelationshipTable.add(primaryDriver);
        return DataProviderFactory.dataOf(AutoCaMetaData.AssignmentTab.DRIVER_VEHICLE_RELATIONSHIP.getLabel(), driverRelationshipTable);
    }

    private TestData getCaSelectVehicleTabTd() {
        return DataProviderFactory.dataOf(
                AutoCaMetaData.VehicleTab.ADD_VEHICLE.getLabel(), "Click",
                AutoCaMetaData.VehicleTab.VIN.getLabel(), "WAUKJAFM8C6314628",
                AutoCaMetaData.VehicleTab.PRIMARY_USE.getLabel(), "Pleasure (recreational driving only)",
                AutoCaMetaData.VehicleTab.EXISTING_DAMEGE.getLabel(), "None",
                AutoCaMetaData.VehicleTab.SALVAGED.getLabel(), "No",
                AutoCaMetaData.VehicleTab.ODOMETER_READING.getLabel(), "5000");
    }

    private TestData getCaChoiceVehicleTabTd() {
        return getCaSelectVehicleTabTd().adjust(AutoCaMetaData.VehicleTab.ARE_THERE_ANY_ADDITIONAL_INTERESTS.getLabel(), "No");
    }

    private TestData getEmptyTestDataCaAuto() {
        return getPolicyTD("Endorsement", "TestData_Empty_Endorsement").resolveLinks()
                .mask(AutoCaMetaData.PrefillTab.class.getSimpleName());
    }

    private TestData getEmptyTestDataSSAuto() {
        return getPolicyTD("Endorsement", "TestData_Empty_Endorsement").resolveLinks()
                .mask(AutoSSMetaData.PrefillTab.class.getSimpleName())
                .mask(AutoSSMetaData.AssignmentTab.class.getSimpleName());
    }

    private TestData getEmptyTestDataCaHome() {
        return getPolicyTD("Endorsement", "TestData_Empty_Endorsement").resolveLinks();
    }

    private TestData getEmptyTestDataSSHome() {
        TestData td = getPolicyTD("Endorsement", "TestData_Empty_Endorsement").resolveLinks();
        if (getPolicyType().equals(PolicyType.HOME_SS_HO3)) {
            td.adjust(HomeSSMetaData.ProductOfferingTab.class.getSimpleName(), DataProviderFactory.emptyData());
        }
        return td;
    }

    private TestData getEmptyTestDataSSPup() {
        return getPolicyTD("Endorsement", "TestData_Empty_Endorsement").resolveLinks();
    }

    private TestData getEmptyTestDataCAPup() {
        return getEmptyTestDataSSPup().adjust(PersonalUmbrellaMetaData.UnderlyingRisksAllResidentsTab.class.getSimpleName(), DataProviderFactory.emptyData());
    }

}
