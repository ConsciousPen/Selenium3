package aaa.modules.financials;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.main.metadata.policy.*;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;

public class FinancialsTestDataFactory extends PolicyBaseTest {

    protected static final List<String> ALL_POLICIES = Collections.synchronizedList(new ArrayList<>());

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
        }
        return td;
    }

    protected TestData getEndorsementTD() {
        return getEndorsementTD(TimeSetterUtil.getInstance().getCurrentTime());
    }

    protected TestData getEndorsementTD(LocalDateTime effDate) {
        TestData td =  getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Endorsement"), "TestData");
        String type = getPolicyType().getShortName();
        String date = effDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
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

    protected TestData getCancellationTD() {
        return getCancellationTD(TimeSetterUtil.getInstance().getCurrentTime());
    }

    protected TestData getCancellationTD(LocalDateTime effDate) {
        TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("Cancellation"), "TestData");
        String type = getPolicyType().getShortName();
        String date = effDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        switch (type) {
            case CA_SELECT:
            case CA_CHOICE:
                td.adjust(TestData.makeKeyPath(AutoCaMetaData.CancellationActionTab.class.getSimpleName(), AutoCaMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=2");
                td.adjust(TestData.makeKeyPath(AutoCaMetaData.CancellationActionTab.class.getSimpleName(), AutoCaMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            case AUTO_SS:
                td.adjust(TestData.makeKeyPath(AutoSSMetaData.CancellationActionTab.class.getSimpleName(), AutoSSMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=1");
                td.adjust(TestData.makeKeyPath(AutoSSMetaData.CancellationActionTab.class.getSimpleName(), AutoSSMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            case HOME_SS_HO3:
            case HOME_SS_HO4:
            case HOME_SS_HO6:
            case HOME_SS_DP3:
                td.adjust(TestData.makeKeyPath(HomeSSMetaData.CancellationActionTab.class.getSimpleName(), HomeSSMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=1");
                td.adjust(TestData.makeKeyPath(HomeSSMetaData.CancellationActionTab.class.getSimpleName(), HomeSSMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            case HOME_CA_HO3:
            case HOME_CA_HO4:
            case HOME_CA_HO6:
            case HOME_CA_DP3:
                td.adjust(TestData.makeKeyPath(HomeCaMetaData.CancelActionTab.class.getSimpleName(), HomeCaMetaData.CancelActionTab.CANCELLATION_REASON.getLabel()), "index=1");
                td.adjust(TestData.makeKeyPath(HomeCaMetaData.CancelActionTab.class.getSimpleName(), HomeCaMetaData.CancelActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
                break;
            case PUP:
                td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.CancellationActionTab.class.getSimpleName(),
                        PersonalUmbrellaMetaData.CancellationActionTab.CANCELLATION_REASON.getLabel()), "index=1");
                td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.CancellationActionTab.class.getSimpleName(),
                        PersonalUmbrellaMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()), date);
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

    protected TestData adjustTdPolicyEffDate(TestData td, LocalDateTime date) {
        String type = getPolicyType().getShortName();
        String sDate = date.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
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
                td.adjust(TestData.makeKeyPath(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.NAMED_INSURED.getLabel(),
                        PersonalUmbrellaMetaData.PrefillTab.NamedInsured.AAA_EMPLOYEE.getLabel()), "Yes");
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
                td = new SimpleDataProvider();  // TODO needs replaced with PUP method for premium reduction
                break;
            default:
                throw new IstfException("No Policy Type was matched!");
        }
        return td;
    }

    private Map<String, String> getPupUnderlyingPolicies() {
        Map<String, String> policies = new LinkedHashMap<>();
        PolicyType type;
        PolicyType typeAuto;
        String hoPolicy;
        String autoPolicy;
        String state = getState().intern();
        synchronized (state) {
            if (getState().equals(Constants.States.CA)) {
                type = PolicyType.HOME_CA_HO3;
                typeAuto = PolicyType.AUTO_CA_SELECT;
            } else {
                type = PolicyType.HOME_SS_HO3;
                typeAuto = PolicyType.AUTO_SS;
            }
            type.get().createPolicy(getStateTestData(testDataManager.policy.get(type), "DataGather", "TestData"));
            hoPolicy = PolicySummaryPage.getPolicyNumber();
            policies.put("Primary_HO3", hoPolicy);
            ALL_POLICIES.add(hoPolicy);
            typeAuto.get().createPolicy(getStateTestData(testDataManager.policy.get(typeAuto), "DataGather", "TestData"));
            autoPolicy = PolicySummaryPage.getPolicyNumber();
            policies.put("Primary_Auto", autoPolicy);
            ALL_POLICIES.add(autoPolicy);
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
                HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), "contains=$250"));
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
        List<TestData> errorsOverride = new ArrayList<>();
        TestData overrideTd = DataProviderFactory.dataOf(
                PersonalUmbrellaMetaData.ErrorTab.ErrorsOverride.MESSAGE.getLabel(), "contains=Underwriting approval required for Liability Coverage limits greater than",
                PersonalUmbrellaMetaData.ErrorTab.ErrorsOverride.OVERRIDE.getLabel(), "true",
                PersonalUmbrellaMetaData.ErrorTab.ErrorsOverride.APPROVAL.getLabel(), "true",
                PersonalUmbrellaMetaData.ErrorTab.ErrorsOverride.DURATION.getLabel(), "Life",
                PersonalUmbrellaMetaData.ErrorTab.ErrorsOverride.REASON_FOR_OVERRIDE.getLabel(), "index=1");
        errorsOverride.add(overrideTd);
        TestData errorTabOverride = DataProviderFactory.dataOf(PersonalUmbrellaMetaData.ErrorTab.ERROR_OVERRIDE.getLabel(), errorsOverride);

        TestData td;
        if (getState().equals(Constants.States.CA)) {
            td = getEmptyTestDataCAPup();
        } else {
            td = getEmptyTestDataSSPup().adjust(PersonalUmbrellaMetaData.ErrorTab.class.getSimpleName(), errorTabOverride);
        }
        return td.adjust(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.dataOf(
                    PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.PERSONAL_UMBRELLA.getLabel(), "contains=$2,000,000"));
    }

    private TestData getEmptyTestDataCaAuto() {
        return DataProviderFactory.emptyData()
                .adjust(AutoCaMetaData.GeneralTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoCaMetaData.DriverTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoCaMetaData.MembershipTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoCaMetaData.VehicleTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoCaMetaData.AssignmentTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoCaMetaData.FormsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoCaMetaData.PremiumAndCoveragesTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(), DataProviderFactory.emptyData());
    }

    private TestData getEmptyTestDataSSAuto() {
        return DataProviderFactory.emptyData()
                .adjust(AutoSSMetaData.GeneralTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoSSMetaData.DriverTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoSSMetaData.RatingDetailReportsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoSSMetaData.VehicleTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoSSMetaData.FormsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoSSMetaData.DriverActivityReportsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(AutoSSMetaData.DocumentsAndBindTab.class.getSimpleName(), DataProviderFactory.emptyData());
    }

    private TestData getEmptyTestDataCaHome() {
        return DataProviderFactory.emptyData()
                .adjust(HomeCaMetaData.GeneralTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeCaMetaData.ApplicantTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeCaMetaData.ReportsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeCaMetaData.PropertyInfoTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeCaMetaData.EndorsementTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeCaMetaData.MortgageesTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeCaMetaData.UnderwritingAndApprovalTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeCaMetaData.DocumentsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeCaMetaData.BindTab.class.getSimpleName(), DataProviderFactory.emptyData());
    }

    private TestData getEmptyTestDataSSHome() {
        TestData td = DataProviderFactory.emptyData()
                .adjust(HomeSSMetaData.GeneralTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeSSMetaData.ApplicantTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeSSMetaData.ReportsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeSSMetaData.PropertyInfoTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeSSMetaData.EndorsementTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeSSMetaData.MortgageesTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeSSMetaData.UnderwritingAndApprovalTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeSSMetaData.DocumentsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(HomeSSMetaData.BindTab.class.getSimpleName(), DataProviderFactory.emptyData());
        if (getPolicyType().equals(PolicyType.HOME_SS_HO3)) {
            td.adjust(HomeSSMetaData.ProductOfferingTab.class.getSimpleName(), DataProviderFactory.emptyData());
        }
        return td;
    }

    private TestData getEmptyTestDataCAPup() {
        return DataProviderFactory.emptyData()
                .adjust(PersonalUmbrellaMetaData.PrefillTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(PersonalUmbrellaMetaData.GeneralTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(PersonalUmbrellaMetaData.UnderlyingRisksPropertyTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(PersonalUmbrellaMetaData.UnderlyingRisksOtherVehiclesTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(PersonalUmbrellaMetaData.UnderlyingRisksAllResidentsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(PersonalUmbrellaMetaData.ClaimsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(PersonalUmbrellaMetaData.EndorsementsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(PersonalUmbrellaMetaData.UnderwritingAndApprovalTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(PersonalUmbrellaMetaData.DocumentsTab.class.getSimpleName(), DataProviderFactory.emptyData())
                .adjust(PersonalUmbrellaMetaData.BindTab.class.getSimpleName(), DataProviderFactory.emptyData());
    }

    private TestData getEmptyTestDataSSPup() {
        return getEmptyTestDataCAPup().mask(PersonalUmbrellaMetaData.UnderlyingRisksAllResidentsTab.class.getSimpleName());
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

}
