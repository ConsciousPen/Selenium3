package aaa.helpers.openl.testdata_builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.helpers.TestDataHelper;
import aaa.helpers.mock.MockDataHelper;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLForm;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;

public class HomeSSTestDataGenerator extends TestDataGenerator<HomeSSOpenLPolicy> {
	public HomeSSTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeSSOpenLPolicy openLPolicy) {
		return getRatingData(openLPolicy, false);
	}

	@Override
	public TestData getRenewalEntryData(HomeSSOpenLPolicy openLPolicy) {
		TestData initiateRenewalEntryActionData = super.getRenewalEntryData(openLPolicy);
		TestData td = DataProviderFactory.dataOf(
				new InitiateRenewalEntryActionTab().getMetaKey(), DataProviderFactory.dataOf(
						CustomerMetaData.InitiateRenewalEntryActionTab.PRODUCT_NAME.getLabel(), "Homeowners Signature Series",
						CustomerMetaData.InitiateRenewalEntryActionTab.POLICY_TYPE.getLabel(), openLPolicy.getPolicyType(),
						CustomerMetaData.InitiateRenewalEntryActionTab.INCEPTION_DATE.getLabel(), openLPolicy.getCappingDetails().getPlcyInceptionDate().format(DateTimeUtils.MM_DD_YYYY),
						CustomerMetaData.InitiateRenewalEntryActionTab.LEGACY_POLICY_HAD_MULTI_POLICY_DISCOUNT.getLabel(), "No"
				)
		);
		initiateRenewalEntryActionData = TestDataHelper.merge(td, initiateRenewalEntryActionData);
		return initiateRenewalEntryActionData;
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}

	public TestData getRatingData(HomeSSOpenLPolicy openLPolicy, boolean isLegacyConvPolicy) {
		TestData td = DataProviderFactory.dataOf(
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new ApplicantTab().getMetaKey(), getApplicantTabData(openLPolicy),
				new ReportsTab().getMetaKey(), getReportsTabData(openLPolicy),
				new PropertyInfoTab().getMetaKey(), getPropertyInfoTabData(openLPolicy),
				new ProductOfferingTab().getMetaKey(), getProductOfferingTabData(openLPolicy),
				new EndorsementTab().getMetaKey(), getEndorsementTabData(openLPolicy),
				new PersonalPropertyTab().getMetaKey(), getPersonalPropertyTabData(openLPolicy),
				new PremiumsAndCoveragesQuoteTab().getMetaKey(), getPremiumsAndCoveragesQuoteTabData(openLPolicy)
		);

		TestData ratingDataPattern = getRatingDataPattern()
				.mask(TestData.makeKeyPath(new ApplicantTab().getMetaKey(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()))
				.mask(TestData.makeKeyPath(new ApplicantTab().getMetaKey(), HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel()))
				.mask(TestData.makeKeyPath(new PropertyInfoTab().getMetaKey(), HomeSSMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel()));

		if (Boolean.FALSE.equals(openLPolicy.getPolicyDiscountInformation().isCurrAAAMember())) {
			ratingDataPattern.mask(TestData.makeKeyPath(new ReportsTab().getMetaKey(), HomeSSMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel()));
		}

		td = TestDataHelper.merge(ratingDataPattern, td);
		if (isLegacyConvPolicy) {
			TestData policyInformationTd = td.getTestData(new GeneralTab().getMetaKey())
					.mask(HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel(), HomeSSMetaData.GeneralTab.LEAD_SOURCE.getLabel());
			td.adjust(TestData.makeKeyPath(new GeneralTab().getMetaKey()), policyInformationTd);
			td.adjust(TestData.makeKeyPath(new ReportsTab().getMetaKey(), HomeSSMetaData.ReportsTab.ORDER_INTERNAL_CLAIMS.getLabel()), Arrays.asList(DataProviderFactory.dataOf("Action", "Order report")));
		}
		return td;
	}

	public TestData getPolicyIssueData(HomeSSOpenLPolicy openLPolicy) {
		TestData td = getRatingDataPattern();
		TestData policyIssueData = DataProviderFactory.dataOf(
				MortgageesTab.class.getSimpleName(), td.getTestData(MortgageesTab.class.getSimpleName()),
				UnderwritingAndApprovalTab.class.getSimpleName(), td.getTestData(UnderwritingAndApprovalTab.class.getSimpleName()),
				DocumentsTab.class.getSimpleName(), td.getTestData(DocumentsTab.class.getSimpleName()),
				BindTab.class.getSimpleName(), td.getTestData(BindTab.class.getSimpleName()),
				PurchaseTab.class.getSimpleName(), td.getTestData(PurchaseTab.class.getSimpleName())
		);

		if (BillingConstants.PaymentPlan.MORTGAGEE_BILL.equals(openLPolicy.getPolicyDiscountInformation().getPaymentPlan())) {
			TestData mortgageeTabData = DataProviderFactory.dataOf(
					new MortgageesTab().getMetaKey(), DataProviderFactory.dataOf(
							HomeSSMetaData.MortgageesTab.MORTGAGEE.getLabel(), "Yes",
							HomeSSMetaData.MortgageesTab.MORTGAGEE_INFORMATION.getLabel(), DataProviderFactory.dataOf(
									HomeSSMetaData.MortgageesTab.MortgageeInformation.NAME.getLabel(), "Name",
									HomeSSMetaData.MortgageesTab.MortgageeInformation.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
									HomeSSMetaData.MortgageesTab.MortgageeInformation.STREET_ADDRESS_1.getLabel(), "111 Street Address",
									HomeSSMetaData.MortgageesTab.MortgageeInformation.VALIDATE_ADDRESS_BTN.getLabel(), "true",
									HomeSSMetaData.MortgageesTab.MortgageeInformation.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.emptyData(),
									HomeSSMetaData.MortgageesTab.MortgageeInformation.LOAN_NUMBER.getLabel(), "123456789"))
			);
			policyIssueData = TestDataHelper.merge(mortgageeTabData, policyIssueData);
		}

		// merge Documents to bind
		LinkedHashMap<String, String> documentsToBindData = new LinkedHashMap<>();
		if ("Central".equals(openLPolicy.getPolicyDiscountInformation().getFireAlarmType())) {
			documentsToBindData.put(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().getProofCentralFireAlarm()));
		}
		if (isVisibleProofOfPEHCR(openLPolicy)) {
			documentsToBindData.put(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_HOME_RENOVATIONS_FOR_MODERNIZATION.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().getProofOfPEHCR()));
		}
		TestData documentsTabData = DataProviderFactory.dataOf(
				DocumentsTab.class.getSimpleName(), DataProviderFactory.dataOf(HomeSSMetaData.DocumentsTab.DOCUMENTS_TO_BIND.getLabel(), new SimpleDataProvider(documentsToBindData)));

		return TestDataHelper.merge(documentsTabData, policyIssueData);
	}

	public TestData getEndorsementData(HomeSSOpenLPolicy openLPolicy) {

		TestData endorsementActionTabData = DataProviderFactory.dataOf(
				HomeSSMetaData.EndorsementActionTab.ENDORSEMENT_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				HomeSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel(), "index=1"
		);

		TestData formHS0904EndorsementTabData = DataProviderFactory.dataOf(
				HomeSSMetaData.EndorsementTab.HS_09_04.getLabel(), DataProviderFactory.dataOf(
						HomeSSMetaData.EndorsementTab.EndorsementHS0904.IS_THIS_AN_EXTENSION_OF_A_PRIOR_STRUCTURAL_ALTERATION_COVERAGE_ENDORSEMENT.getLabel(), "Yes",
						HomeSSMetaData.EndorsementTab.EndorsementHS0904.REASON_FOR_EXTENSION.getLabel(), "Reason"
				)

		);

		TestData personalPropertyTabData = openLPolicy.getForms().stream().anyMatch(c -> "HS0461".equals(c.getFormCode())) ? DataProviderFactory.emptyData() : null;

		return DataProviderFactory.dataOf(
				HomeSSMetaData.EndorsementActionTab.class.getSimpleName(), endorsementActionTabData,
				HomeSSMetaData.GeneralTab.class.getSimpleName(), DataProviderFactory.emptyData(),
				HomeSSMetaData.ApplicantTab.class.getSimpleName(), DataProviderFactory.emptyData(),
				HomeSSMetaData.ReportsTab.class.getSimpleName(), DataProviderFactory.emptyData(),
				HomeSSMetaData.PropertyInfoTab.class.getSimpleName(), DataProviderFactory.emptyData(),
				HomeSSMetaData.ProductOfferingTab.class.getSimpleName(), DataProviderFactory.emptyData(),
				HomeSSMetaData.EndorsementTab.class.getSimpleName(), formHS0904EndorsementTabData,
				HomeSSMetaData.PersonalPropertyTab.class.getSimpleName(), personalPropertyTabData
		);
	}

	public TestData getFormHS0492Data(HomeSSOpenLPolicy openLPolicy) {
		List<TestData> publicProtectionClassData = new ArrayList<>();
		List<TestData> editFormHS0492Data = new ArrayList<>();

		publicProtectionClassData.add(new SimpleDataProvider());
		int instanceNum = 1;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0492".equals(form.getFormCode())) {
				publicProtectionClassData.add(DataProviderFactory.dataOf(
						"Report", "Order Report"
				));
				editFormHS0492Data.add(DataProviderFactory.dataOf(
						"Action", "Edit",
						"Instance", String.format("%d", instanceNum),
						HomeSSMetaData.EndorsementTab.EndorsementHS0492.PUBLIC_PROTECTION_CLASS.getLabel(), form.getType()
				));
				instanceNum++;
			}
		}

		TestData reportsTabData = DataProviderFactory.dataOf(
				HomeSSMetaData.ReportsTab.SALES_AGENT_AGREEMENT.getLabel(), "I Agree",
				HomeSSMetaData.ReportsTab.PUBLIC_PROTECTION_CLASS.getLabel(), publicProtectionClassData
		);

		TestData endorsementTabData = DataProviderFactory.dataOf(
				HomeSSMetaData.EndorsementTab.HS_04_92.getLabel(), editFormHS0492Data
		);

		TestData personalPropertyTabData = openLPolicy.getForms().stream().anyMatch(c -> "HS0461".equals(c.getFormCode())) ? DataProviderFactory.emptyData() : null;

		return DataProviderFactory.dataOf(
				new ReportsTab().getMetaKey(), reportsTabData,
				new PropertyInfoTab().getMetaKey(), DataProviderFactory.emptyData(),
				new ProductOfferingTab().getMetaKey(), DataProviderFactory.emptyData(),
				new EndorsementTab().getMetaKey(), endorsementTabData,
				new PersonalPropertyTab().getMetaKey(), personalPropertyTabData
		);
	}

	private TestData getGeneralTabData(HomeSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.dataOf(
				HomeSSMetaData.GeneralTab.STATE.getLabel(), getState(),
				HomeSSMetaData.GeneralTab.POLICY_TYPE.getLabel(), openLPolicy.getPolicyType(),
				HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel(),
				openLPolicy.getEffectiveDate().minusYears(openLPolicy.getPolicyNamedInsured().getaAAPropPersistency()).format(DateTimeUtils.MM_DD_YYYY),
				HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel(), getImmediatePriorCarrier(openLPolicy.getCappingDetails().getCarrierCode()),
				HomeSSMetaData.GeneralTab.CONTINUOUS_YEARS_WITH_IMMEDIATE_PRIOR_CARRIER.getLabel(), getContinuousYears(openLPolicy)
		);
	}

	private TestData getApplicantTabData(HomeSSOpenLPolicy openLPolicy) {

		TestData namedInsuredData = DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.NamedInsured.MARITAL_STATUS.getLabel(), openLPolicy.getPolicyNamedInsured().getMaritialStatus(),
				HomeSSMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().isAAAEmployee()),
				HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), openLPolicy.getEffectiveDate().minusYears(openLPolicy.getPolicyNamedInsured().getAgeOfOldestInsured()).format(DateTimeUtils.MM_DD_YYYY)
		);
		if (Constants.States.NJ.equals(openLPolicy.getPolicyAddress().getState())) {
			namedInsuredData.adjust(DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.NamedInsured.NJ_CHAMBER_OF_COMMERCE.getLabel(), openLPolicy.getChamberOfCommerce() == null ? "None" : "value=" + openLPolicy.getChamberOfCommerce()
			));
		}

		TestData aaaMembershipData;
		if (Boolean.TRUE.equals(openLPolicy.getPolicyDiscountInformation().isCurrAAAMember())) {
			aaaMembershipData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), "Yes",
					HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), MockDataHelper.getMembershipData().getMembershipNumber(openLPolicy.getEffectiveDate(), openLPolicy.getPolicyDiscountInformation().getMemberPersistency()),
					HomeSSMetaData.ApplicantTab.AAAMembership.LAST_NAME.getLabel(), "Smith"
			);
		} else {
			aaaMembershipData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), "No");
		}

		TestData dwellingAddressData = DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
				HomeSSMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel(), "000 Street Address"
		);
		if (addressContainsCounty(openLPolicy.getPolicyAddress().getState())) {
			dwellingAddressData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.ApplicantTab.DwellingAddress.COUNTY.getLabel(), "County"));
		}

		List<TestData> otherActiveAAAPoliciesData = new ArrayList<>();
		boolean isFirstOtherActiveAAAPolicy = true;

		if (Boolean.TRUE.equals(openLPolicy.getPolicyDiscountInformation().isPUPPolicyInd())) {
			TestData pupPolicyData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), isFirstOtherActiveAAAPolicy ? "Yes" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), isFirstOtherActiveAAAPolicy ? "click" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData(),
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "PUP",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.COMPANION_PUP_DP3_PENDING_WITH_DISCOUNT.getLabel(), "No",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), "PUP123456789",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY)
					)
			);
			otherActiveAAAPoliciesData.add(pupPolicyData);
			isFirstOtherActiveAAAPolicy = false;
		}

		if (Boolean.TRUE.equals(openLPolicy.getPolicyDiscountInformation().isAutoPolicyInd())) {
			TestData autoPolicyData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), isFirstOtherActiveAAAPolicy ? "Yes" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), isFirstOtherActiveAAAPolicy ? "click" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData(),
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "Auto",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_POLICY_STATE.getLabel(), openLPolicy.getPolicyAddress().getState(),
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.COMPANION_AUTO_PENDING_WITH_DISCOUNT.getLabel(), "No",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), openLPolicy.getPolicyAddress().getState()+"SSA12345678",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TIER.getLabel(), openLPolicy.getPolicyLossInformation().getAutoTier(),
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_POLICY_BI_LIMIT.getLabel(), "index=1",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_INSURANCE_PERSISTENCY.getLabel(), openLPolicy.getPolicyDiscountInformation().getAutoInsPersistency()
					)
			);
			otherActiveAAAPoliciesData.add(autoPolicyData);
			isFirstOtherActiveAAAPolicy = false;
		}

		if (Boolean.TRUE.equals(openLPolicy.getPolicyDiscountInformation().isLifePolicyInd())) {
			TestData lifePolicyData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), isFirstOtherActiveAAAPolicy ? "Yes" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), isFirstOtherActiveAAAPolicy ? "click" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData(),
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "Life",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), "LIFE123456789",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY)
					)
			);
			otherActiveAAAPoliciesData.add(lifePolicyData);
			isFirstOtherActiveAAAPolicy = false;
		}

		if (Boolean.TRUE.equals(openLPolicy.getPolicyDiscountInformation().isDP3PolicyInd())) {
			TestData dp3PolicyData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), isFirstOtherActiveAAAPolicy ? "Yes" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), isFirstOtherActiveAAAPolicy ? "click" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData(),
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "DP3",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.COMPANION_PUP_DP3_PENDING_WITH_DISCOUNT.getLabel(), "No",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), "DP3123456789",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.COVERAGE_E.getLabel(), "1000",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.DEDUCTIBLE.getLabel(), "1000",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.DWELLING_USAGE.getLabel(), "Primary",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.OCCUPANCY_TYPE.getLabel(), "Owner occupied"
					)
			);
			otherActiveAAAPoliciesData.add(dp3PolicyData);
		}

		if (otherActiveAAAPoliciesData.isEmpty()) {
			otherActiveAAAPoliciesData.add(DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), "No"));
		}

		return DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), Arrays.asList(namedInsuredData),
				HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), aaaMembershipData,
				HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel(), otherActiveAAAPoliciesData
		);
	}

	private TestData getReportsTabData(HomeSSOpenLPolicy openLPolicy) {

		TestData insuranceScoreOverrideData = DataProviderFactory.dataOf(
				HomeSSMetaData.ReportsTab.InsuranceScoreOverrideRow.ACTION.getLabel(), "Override Score",
				HomeSSMetaData.ReportsTab.InsuranceScoreOverrideRow.EDIT_INSURANCE_SCORE.getLabel(), DataProviderFactory.dataOf(
						HomeSSMetaData.ReportsTab.EditInsuranceScoreDialog.SCORE_AFTER_OVERRIDE.getLabel(), openLPolicy.getPolicyLossInformation().getCreditBands(),
						HomeSSMetaData.ReportsTab.EditInsuranceScoreDialog.REASON_FOR_OVERRIDE.getLabel(), "Fair Credit Reporting Act Dispute",
						HomeSSMetaData.ReportsTab.EditInsuranceScoreDialog.BTN_SAVE.getLabel(), "click"
				)
		);
		return insuranceScoreReport(openLPolicy.getPolicyAddress().getState()) ?
				DataProviderFactory.dataOf(HomeSSMetaData.ReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel(), insuranceScoreOverrideData) : null;
	}

	private TestData getPropertyInfoTabData(HomeSSOpenLPolicy openLPolicy) {

		TestData dwellingAddressData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "contains=" + openLPolicy.getPolicyDwellingRatingInfo().getFamilyUnits()
		);

		TestData publicProtectionClassData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), openLPolicy.getPolicyDwellingRatingInfo().getProtectionClass()
		);

		TestData riskMeterData = null;
		if (isCoastalState(openLPolicy.getPolicyAddress().getState()) && openLPolicy.getRiskMeterData() != null) {
			riskMeterData = DataProviderFactory.dataOf(
					HomeSSMetaData.PropertyInfoTab.Riskmeter.DISTANCE_TO_COAST_MILES.getLabel(), openLPolicy.getRiskMeterData().getDistanceToCoast().toString(),
					HomeSSMetaData.PropertyInfoTab.Riskmeter.ELEVATION_FEET.getLabel(), openLPolicy.getRiskMeterData().getElevation().toString()
			);
		}

		TestData propertyValueData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT.getLabel(), openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCoverageCd())).findFirst().get().getLimit(),
				HomeSSMetaData.PropertyInfoTab.PropertyValue.ISO_REPLACEMENT_COST.getLabel(), openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCoverageCd())).findFirst().get().getLimit(),
				HomeSSMetaData.PropertyInfoTab.PropertyValue.PURCHASE_DATE_OF_HOME.getLabel(), openLPolicy.getEffectiveDate().minusYears(openLPolicy.getPolicyDiscountInformation().getNoOfYrsSinceLoanInception()).format(DateTimeUtils.MM_DD_YYYY)
		);

		TestData constructionData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), String.format("%d", openLPolicy.getEffectiveDate().minusYears(openLPolicy.getPolicyDwellingRatingInfo().getHomeAge()).getYear()),
				HomeSSMetaData.PropertyInfoTab.Construction.ROOF_TYPE.getLabel(), openLPolicy.getPolicyDwellingRatingInfo().getRoofType(),
				HomeSSMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE.getLabel(), "contains=" + openLPolicy.getPolicyConstructionInfo().getConstructionType().split(" ")[0],
				HomeSSMetaData.PropertyInfoTab.Construction.MASONRY_VENEER.getLabel(),
				"Masonry Veneer".equals(openLPolicy.getPolicyConstructionInfo().getConstructionType()) ? "Yes" : "No",
				HomeSSMetaData.PropertyInfoTab.Construction.IS_THIS_A_LOG_HOME_ASSEMBLED_BY_A_LICENSED_BUILDING_CONTRACTOR.getLabel(), "Log Home".equals(openLPolicy.getPolicyConstructionInfo().getConstructionType()) ? "Yes" : null
		);

		TestData interiorData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.Interior.DWELLING_USAGE.getLabel(), Boolean.TRUE.equals(openLPolicy.getPolicyDwellingRatingInfo().getSecondaryHome()) ? "Secondary" : "Primary",
				HomeSSMetaData.PropertyInfoTab.Interior.NUMBER_OF_RESIDENTS.getLabel(), openLPolicy.getPolicyNamedInsured().getNumofOccupants(),
				HomeSSMetaData.PropertyInfoTab.Interior.NUMBER_OF_STORIES.getLabel(), getNumberOfStories(openLPolicy.getPolicyDwellingRatingInfo().getNoOfFloors())
		);

		TestData fireProtectiveDeviceDiscountData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.FireProtectiveDD.LOCAL_FIRE_ALARM.getLabel(),
				"Local".equals(openLPolicy.getPolicyDiscountInformation().getFireAlarmType()),
				HomeSSMetaData.PropertyInfoTab.FireProtectiveDD.CENTRAL_FIRE_ALARM.getLabel(),
				"Central".equals(openLPolicy.getPolicyDiscountInformation().getFireAlarmType()),
				HomeSSMetaData.PropertyInfoTab.FireProtectiveDD.FULL_RESIDENTIAL_SPRINKLERS.getLabel(),
				"Full".equals(openLPolicy.getPolicyDiscountInformation().getSprinklerType()),
				HomeSSMetaData.PropertyInfoTab.FireProtectiveDD.PARTIAL_RESIDENTIAL_SPRINKLERS.getLabel(),
				"Partial".equals(openLPolicy.getPolicyDiscountInformation().getSprinklerType())
		);

		TestData theftProtectiveDeviceDiscountData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.TheftProtectiveTPDD.LOCAL_THEFT_ALARM.getLabel(), "Local".equals(openLPolicy.getPolicyDiscountInformation().getTheftAlarmType()),
				HomeSSMetaData.PropertyInfoTab.TheftProtectiveTPDD.CENTRAL_THEFT_ALARM.getLabel(), "Central".equals(openLPolicy.getPolicyDiscountInformation().getTheftAlarmType()),
				HomeSSMetaData.PropertyInfoTab.TheftProtectiveTPDD.GATED_COMMUNITY.getLabel(), openLPolicy.getPolicyDiscountInformation().isPrivateCommunity()
		);

		TestData homeRenovationData = DataProviderFactory.dataOf(
				// plumbing
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_RENOVATION.getLabel(), "100% Copper",
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_PERCENT_COMPLETE.getLabel(), "100",
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_MONTH_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovPlumbing() * 12 + 1).getMonthValue(),
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.PLUMBING_YEAR_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovPlumbing() * 12 + 1).getYear(),
				// electrical
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.ELECTRICAL_RENOVATION.getLabel(), "100% Circuit/Romex",
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.ELECTRICAL_PERCENT_COMPLETE.getLabel(), "100",
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.ELECTRICAL_MONTH_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovElectrical() * 12 + 1).getMonthValue(),
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.ELECTRICAL_YEAR_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovElectrical() * 12 + 1).getYear(),
				// roof
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.ROOF_RENOVATION.getLabel(), "100% Replace",
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.ROOF_PERCENT_COMPLETE.getLabel(), "100",
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.ROOFG_MONTH_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovRoof() * 12 + 1).getMonthValue(),
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.ROOF_YEAR_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovRoof() * 12 + 1).getYear(),
				// heating/cooling
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_RENOVATION.getLabel(), "Forced Air",
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_PERCENT_COMPLETE.getLabel(), "100",
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_MONTH_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovHeatOrCooling() * 12 + 1).getMonthValue(),
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_YEAR_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovHeatOrCooling() * 12 + 1).getYear(),
				// green home discount
				HomeSSMetaData.PropertyInfoTab.HomeRenovation.GREEN_HOME_DISCOUNT.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().getGreenHomeDiscApplicability())
		);

		List<TestData> petsOrAnimalsData = getPetsOrAnimalsData(openLPolicy);

		TestData recreationalEquipmentData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SWIMMING_POOL.getLabel(), getSwimmingPoolType(openLPolicy),
				HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.SPA_HOT_TUB.getLabel(), openLPolicy.getPolicyConstructionInfo().getNumberOfTub() > 0 ? "Restricted access" : "None",
				HomeSSMetaData.PropertyInfoTab.RecreationalEquipment.TRAMPOLINE.getLabel(), getTrampolineType(openLPolicy)
		);

		TestData oilFuelOrPropaneStorageTankData = null;
		if (Constants.States.NJ.equals(openLPolicy.getPolicyAddress().getState()) && openLPolicy.getForms().stream().anyMatch(c -> "HS0578".equals(c.getFormCode()))) {
			oilFuelOrPropaneStorageTankData = DataProviderFactory.dataOf(
					HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.OIL_FUEL_OR_PROPANE_STORAGE_TANK.getLabel(), getStorageTankType(openLPolicy),
					HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.ADD_FUEL_SYSTEM_STORAGE_TANK_COVERAGE.getLabel(), "Yes",
					HomeSSMetaData.PropertyInfoTab.OilPropaneStorageTank.AGE_OF_OIL_OR_PROPANE_FUEL_STORAGE_TANK.getLabel(), openLPolicy.getForms().stream().filter(c -> "HS0578".equals(c.getFormCode())).findFirst().get().getOptionalValue().toString().split("\\.")[0]
			);
		}

		List<TestData> claimHistoryData = openLPolicy.getPolicyLossInformation().getRecentYCF() < 3 ? getClaimsHistoryData(openLPolicy) : null;

		return DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), publicProtectionClassData,
				HomeSSMetaData.PropertyInfoTab.RISKMETER.getLabel(), riskMeterData,
				HomeSSMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(), propertyValueData,
				HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), constructionData,
				HomeSSMetaData.PropertyInfoTab.INTERIOR.getLabel(), interiorData,
				HomeSSMetaData.PropertyInfoTab.FIRE_PROTECTIVE_DD.getLabel(), fireProtectiveDeviceDiscountData,
				HomeSSMetaData.PropertyInfoTab.THEFT_PROTECTIVE_DD.getLabel(), theftProtectiveDeviceDiscountData,
				HomeSSMetaData.PropertyInfoTab.HOME_RENOVATION.getLabel(), homeRenovationData,
				HomeSSMetaData.PropertyInfoTab.PETS_OR_ANIMALS.getLabel(), petsOrAnimalsData,
				HomeSSMetaData.PropertyInfoTab.RECREATIONAL_EQUIPMENT.getLabel(), recreationalEquipmentData,
				HomeSSMetaData.PropertyInfoTab.OIL_FUEL_OR_PROPANE_STORAGE_TANK.getLabel(), oilFuelOrPropaneStorageTankData,
				HomeSSMetaData.PropertyInfoTab.CLAIM_HISTORY.getLabel(), claimHistoryData
		);
	}

	private TestData getProductOfferingTabData(HomeSSOpenLPolicy openLPolicy) {
		LinkedHashMap<String, String> productOfferingTabData = new LinkedHashMap<>();
		String policyLevel = openLPolicy.getLevel();
		if (!HomeSSMetaData.ProductOfferingTab.HERITAGE.getLabel().equals(policyLevel)) {
			productOfferingTabData.put(HomeSSMetaData.ProductOfferingTab.VariationControls.SELECT_VARIATION.getLabel(), "true");
		}
		productOfferingTabData.put(HomeSSMetaData.ProductOfferingTab.VariationControls.COVERAGE_E.getLabel(), new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCoverageCd())).findFirst().get().getLimit()).toString().split("\\.")[0]);
		return DataProviderFactory.dataOf(policyLevel, new SimpleDataProvider(productOfferingTabData));
	}

	private TestData getEndorsementTabData(HomeSSOpenLPolicy openLPolicy) {

		TestData endorsementData = new SimpleDataProvider();

		for (HomeSSOpenLForm openLForm : openLPolicy.getForms()) {
			String formCode = openLForm.getFormCode();
			if (!endorsementData.containsKey(HomeSSFormTestDataGenerator.getFormMetaKey(formCode))) {
				List<TestData> tdList = HomeSSFormTestDataGenerator.getFormTestData(openLPolicy, formCode);
				if (tdList != null) {
					TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeSSFormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeSSFormTestDataGenerator.getFormMetaKey(formCode), tdList);
					endorsementData.adjust(td);
				}
			}
		}
		if (!openLPolicy.getForms().stream().anyMatch(c -> "HS0490".equals(c.getFormCode()))) {
			endorsementData.adjust(DataProviderFactory.dataOf(
					HomeSSMetaData.EndorsementTab.HS_04_90.getLabel(), DataProviderFactory.dataOf(
							"Action", "Remove"
					)));
		}
		//		endorsementData.mask(new EndorsementTab().getMetaKey(), HomeSSMetaData.EndorsementTab.HS_04_90.getLabel()); //TODO
		return endorsementData;
	}

	private TestData getPersonalPropertyTabData(HomeSSOpenLPolicy openLPolicy) {
		TestData personalPropertyData = null;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0461".equals(form.getFormCode())) {
				if (personalPropertyData == null) {
					personalPropertyData = new SimpleDataProvider();
				}
				switch (form.getType()) {
					case "Bicycles":
						TestData bicyclesData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.Furs.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.Furs.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.BICYCLES.getLabel(), bicyclesData));
						break;
					case "Fine Arts":
						TestData fineArtsData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.FineArts.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.FineArts.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.FINE_ARTS.getLabel(), fineArtsData));
						break;
					case "Firearms":
						TestData firearmsData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.Firearms.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.Firearms.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.FIREARMS.getLabel(), firearmsData));
						break;
					case "Furs":
						TestData fursData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.Furs.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.Furs.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.FURS.getLabel(), fursData));
						break;
					case "Jewelry":
						TestData jewelryData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.Jewelry.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.Jewelry.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.JEWELRY.getLabel(), jewelryData));
						break;
					case "Medical Devices":
						TestData medicalDevicesData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.MedicalDevices.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.MedicalDevices.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.MEDICAL_DEVICES.getLabel(), medicalDevicesData));
						break;
					case "Musical Instruments":
						TestData musicalInstrumentsData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.MusicalInstruments.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.MusicalInstruments.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.MUSICAL_INSTRUMENTS.getLabel(), musicalInstrumentsData));
						break;
					case "Trading Cards and Comic Books":
						TestData tradingCardsOrComics = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.TradingCardsOrComics.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.TradingCardsOrComics.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.MUSICAL_INSTRUMENTS.getLabel(), tradingCardsOrComics));
						break;
					case "Postage Stamps":
						TestData postageStampsData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.Silverware.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.Silverware.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.POSTAGE_STAMPS.getLabel(), postageStampsData));
						break;
					case "Rare Coins":
						TestData rareCoinsData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.Coins.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.Coins.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.COINS.getLabel(), rareCoinsData));
						break;
					case "Silverware":
						TestData silverwareData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.Silverware.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.Silverware.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.SILVERWARE.getLabel(), silverwareData));
						break;
					//TODO add all types of Coverages
					default:
						CustomAssertions.assertThat(Boolean.TRUE).as("Unknown Type of Coverage: %s", form.getType()).isFalse();
				}
			}
		}
		return personalPropertyData;
	}

	private TestData getPremiumsAndCoveragesQuoteTabData(HomeSSOpenLPolicy openLPolicy) {

		Double covA = Double.parseDouble(openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCoverageCd())).findFirst().get().getLimit());
		Double covB = Double.parseDouble(openLPolicy.getCoverages().stream().filter(c -> "CovB".equals(c.getCoverageCd())).findFirst().get().getLimit());
		Double covD = Double.parseDouble(openLPolicy.getCoverages().stream().filter(c -> "CovD".equals(c.getCoverageCd())).findFirst().get().getLimit());

		String coverageDeductible = openLPolicy.getPolicyCoverageDeductible().getCoverageDeductible();
		String hurricane = null;
		if (coverageDeductible.contains("%")) {
			coverageDeductible = openLPolicy.getPolicyCoverageDeductible().getCoverageDeductible().split("/")[0];
			hurricane = "contains=" + openLPolicy.getPolicyCoverageDeductible().getCoverageDeductible().split("/")[1].substring(0, 1);
		}

		return DataProviderFactory.dataOf(
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel(), "contains=" + getPaymentPlan(openLPolicy.getPolicyDiscountInformation().getPaymentPlan()),
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_B.getLabel(), "contains=" + String.format("%d%%", (int) Math.round(covB * 100 / covA)),
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel(), openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCoverageCd())).findFirst().get().getLimit(),
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), "contains=" + String.format("%d%%", (int) Math.round(covD * 100 / covA)),
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCoverageCd())).findFirst().get().getLimit()).toString().split("\\.")[0],
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_F.getLabel(), new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovF".equals(c.getCoverageCd())).findFirst().get().getLimit()).toString().split("\\.")[0],
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), new Dollar(coverageDeductible).toString().split("\\.")[0],
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.HURRICANE_DEDUCTIBLE.getLabel(), hurricane
		);
	}

	private String getNumberOfStories(Double noOfFloors) {
		if (noOfFloors.equals(1.0) || noOfFloors.equals(2.0)) {
			return noOfFloors.toString().split("\\.")[0];
		}
		if (noOfFloors.equals(1.5) || noOfFloors.equals(2.5)) {
			return noOfFloors.toString();
		}
		return "3+";
	}

	private String getPaymentPlan(String paymentPlan) {
		switch (paymentPlan) {
			case "Pay in Full":
				return BillingConstants.PaymentPlan.PAY_IN_FULL;
			case "Mortgagee Bill":
				return BillingConstants.PaymentPlan.MORTGAGEE_BILL;
			case "Semi-Annual":
				return BillingConstants.PaymentPlan.SEMI_ANNUAL;
			case "Quarterly":
				return BillingConstants.PaymentPlan.QUARTERLY;
			case "Monthly Zero down":
				return "Eleven Pay Low Down";
			case "Monthly Standard":
				return BillingConstants.PaymentPlan.ELEVEN_PAY;
			case "Monthly Low down":
				return BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN;
			default:
				throw new IstfException("Unknown mapping for payment plan : " + paymentPlan);
		}
	}

	private boolean isCoastalState(String state) {
		switch (state) {
			case Constants.States.CT:
			case Constants.States.DE:
			case Constants.States.MD:
			case Constants.States.NJ:
			case Constants.States.VA:
				return true;
			default:
				return false;
		}
	}

	private String getImmediatePriorCarrier(String carrierCode) {
		String immediatePriorCarrier;
		if (StringUtils.isBlank(carrierCode)) {
			immediatePriorCarrier = "index=2";
		} else {
			switch (carrierCode) {
				case "CSAA Affinity Insurance Company (formerly Keystone Insurance Company) ":
					immediatePriorCarrier = "CSAA Affinity Insurance Company";
					break;
				default:
					immediatePriorCarrier = carrierCode;
			}
		}
		return immediatePriorCarrier;
	}

	private Integer getContinuousYears(HomeSSOpenLPolicy openLPolicy) {
		int years = openLPolicy.getPolicyDiscountInformation().getHomeInsPersistency() - openLPolicy.getPolicyNamedInsured().getaAAPropPersistency();
		if (years < 0) {
			throw new IstfException("Batch- DiscountInformation/homeInsPersistency must be more or equal to Batch- NamedInsured/aAAPropPersistency");
		}
		return years;
	}

	private String getSwimmingPoolType(HomeSSOpenLPolicy openLPolicy) {
		String swimmingPoolType;
		if (StringUtils.isBlank(openLPolicy.getPolicyConstructionInfo().getSwimmingPoolType())) {
			swimmingPoolType = "None";
		} else {
			switch (openLPolicy.getPolicyConstructionInfo().getSwimmingPoolType()) {
				case "Fenced with no accessories":
					swimmingPoolType = "Restricted access with no accessories";
					break;
				case "Fenced with slide and diving board":
					swimmingPoolType = "Restricted access with slide and diving board";
					break;
				case "Fenced with slide only":
					swimmingPoolType = "Restricted access with slide only";
					break;
				case "Fenced with diving board only":
					swimmingPoolType = "Restricted access with diving board only";
					break;
				case "Not fenced or no locking gate":
					swimmingPoolType = "Unrestricted access";
					break;
				default:
					throw new IstfException("Unknown mapping for swimmingPoolType=" + openLPolicy.getPolicyConstructionInfo().getSwimmingPoolType());
			}
		}
		return swimmingPoolType;
	}

	private String getTrampolineType(HomeSSOpenLPolicy openLPolicy) {
		String trampolineType;
		if (StringUtils.isBlank(openLPolicy.getPolicyConstructionInfo().getTrampoline())) {
			trampolineType = "None";
		} else {
			switch (openLPolicy.getPolicyConstructionInfo().getTrampoline()) {
				case "Fenced above ground with safety net":
					trampolineType = "Restricted access above ground with safety net";
					break;
				case "Fenced above ground without safety net":
					trampolineType = "Restricted access above ground without safety net";
					break;
				case "Fenced in-ground without safety net":
					trampolineType = "Restricted access above ground without safety net";
					break;
				default:
					throw new IstfException("Unknown mapping for trampoline = " + openLPolicy.getPolicyConstructionInfo().getTrampoline());
			}
		}
		return trampolineType;
	}

	private String getStorageTankType(HomeSSOpenLPolicy openLPolicy) {
		String storageTankType;
		switch (openLPolicy.getForms().stream().filter(c -> "HS0578".equals(c.getFormCode())).findFirst().get().getType()) {
			case "Underground":
				storageTankType = "Active underground propane tank";
				break;
			case "Above Ground":
				storageTankType = "Above ground oil or propane tank on slab";
				break;
			default:
				throw new IstfException("Unknown mapping for tank type = " + openLPolicy.getForms().stream().filter(c -> "HS0578".equals(c.getFormCode())).findFirst().get().getType());
		}
		return storageTankType;
	}

	private boolean addressContainsCounty(String state) {
		return Constants.States.IN.equals(state) ||
				Constants.States.OH.equals(state);
	}

	private boolean insuranceScoreReport(String state) {return !"MD".equals(state);}

	private boolean isVisibleProofOfPEHCR(HomeSSOpenLPolicy openLPolicy) {
		boolean isVisibleProofOfPEHCR = false;
		if (openLPolicy.getPolicyDwellingRatingInfo().getHomeAge() >= 10 &&
				openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovPlumbing() < 10 &&
				openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovHeatOrCooling() < 10 &&
				openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovElectrical() < 10 &&
				openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovRoof() < 10) {
			isVisibleProofOfPEHCR = true;
		}
		return isVisibleProofOfPEHCR;
	}

	private List<TestData> getPetsOrAnimalsData(HomeSSOpenLPolicy openLPolicy) {
		List<TestData> petsOrAnimalsData = new ArrayList<>();
		if (openLPolicy.getPolicyConstructionInfo().getDogType() == null) {
			// only Live Stock Animals
			if (openLPolicy.getPolicyConstructionInfo().getLiveStkNo() > 0) {
				petsOrAnimalsData.add(DataProviderFactory.dataOf(
						HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.ARE_ANY_PETS_OR_ANIMALS_KEPT_ON_THE_PROPERTY.getLabel(), "Yes",
						HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.ANIMAL_TYPE.getLabel(), "Cows",
						HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.ANIMAL_COUNT.getLabel(), openLPolicy.getPolicyConstructionInfo().getLiveStkNo()
				));
			}
		} else {
			switch (openLPolicy.getPolicyConstructionInfo().getDogType()) {
				case 1:
					if (openLPolicy.getPolicyConstructionInfo().getLiveStkNo() > 0) {
						petsOrAnimalsData.add(DataProviderFactory.dataOf(
								HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.ANIMAL_TYPE.getLabel(), "Livestock - Cow",
								HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.ANIMAL_COUNT.getLabel(), openLPolicy.getPolicyConstructionInfo().getLiveStkNo()
						));
					}
					petsOrAnimalsData.add(DataProviderFactory.dataOf(
							HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.ANIMAL_TYPE.getLabel(), "Dog - Other breed",
							HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.OTHER_SPECIFY.getLabel(), "Pooch",
							HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.ANIMAL_COUNT.getLabel(), "1"
					));
					petsOrAnimalsData.get(0).adjust(DataProviderFactory.dataOf(HomeSSMetaData.PropertyInfoTab.PetsOrAnimals.ARE_ANY_PETS_OR_ANIMALS_KEPT_ON_THE_PROPERTY.getLabel(), "Yes"));
					break;
				case 2:
					throw new IstfException("Dog type = 2, Applicants/Insureds with vicious dogs or exotic animals are ineligible");
				default:
					throw new IstfException("Unknown mapping for dog type = " + openLPolicy.getPolicyConstructionInfo().getDogType());

			}
		}
		return petsOrAnimalsData;
	}

	private List<TestData> getClaimsHistoryData(HomeSSOpenLPolicy openLPolicy) {
		List<TestData> claimsDataList = new ArrayList<>();
		int aaaPoints = openLPolicy.getPolicyLossInformation().getExpClaimPoint();
		int notAAAPoints = openLPolicy.getPolicyLossInformation().getPriorClaimPoint();
		boolean isFirstClaim = true;

		if (aaaPoints + notAAAPoints == 0) {
			claimsDataList.add(DataProviderFactory.emptyData());
		} else {
			HomeSSClaimTestDataGenerator claimTestDataGenerator = new HomeSSClaimTestDataGenerator(openLPolicy);
			if (aaaPoints != 0) {
				claimsDataList.addAll(claimTestDataGenerator.getClaimTestData(true, isFirstClaim));
				isFirstClaim = false;
			}
			if (notAAAPoints != 0) {
				claimsDataList.addAll(claimTestDataGenerator.getClaimTestData(false, isFirstClaim));
			}
		}
		return claimsDataList;
	}
}