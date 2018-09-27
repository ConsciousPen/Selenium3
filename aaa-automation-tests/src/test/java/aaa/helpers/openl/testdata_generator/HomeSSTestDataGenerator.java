package aaa.helpers.openl.testdata_generator;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.Constants;
import aaa.helpers.TestDataHelper;
import aaa.helpers.mock.ApplicationMocksManager;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLForm;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

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
		if (Constants.States.AZ.equals(openLPolicy.getCappingDetails().getState())) {
			td = TestDataHelper.merge(td, DataProviderFactory.dataOf(new InitiateRenewalEntryActionTab().getMetaKey(), DataProviderFactory.dataOf(CustomerMetaData.InitiateRenewalEntryActionTab.UNDERWRITING_COMPANY.getLabel(), "CSAA Affinity Insurance Company")));
		}
		return TestDataHelper.merge(initiateRenewalEntryActionData, td);
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
				//new ProductOfferingTab().getMetaKey(), getProductOfferingTabData(openLPolicy),
				new EndorsementTab().getMetaKey(), getEndorsementTabData(openLPolicy),
				new PersonalPropertyTab().getMetaKey(), getPersonalPropertyTabData(openLPolicy),
				new PremiumsAndCoveragesQuoteTab().getMetaKey(), getPremiumsAndCoveragesQuoteTabData(openLPolicy, isLegacyConvPolicy)
		);

		if ("HO3".equals(openLPolicy.getPolicyType())) {
			td.adjust(TestData.makeKeyPath(new ProductOfferingTab().getMetaKey()), getProductOfferingTabData(openLPolicy));
		}

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

	public String autoPolicyNumber = "";

	public TestData getAutoPolicyData(TestData td, HomeSSOpenLPolicy openLPolicy) {
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()),
				openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY));
		TestData insuranceScoreOverrideData = DataProviderFactory.dataOf(
				AutoSSMetaData.RatingDetailReportsTab.InsuranceScoreOverrideRow.ACTION.getLabel(), "Override Score",
				AutoSSMetaData.RatingDetailReportsTab.InsuranceScoreOverrideRow.EDIT_INSURANCE_SCORE.getLabel(), DataProviderFactory.dataOf(
						AutoSSMetaData.RatingDetailReportsTab.EditInsuranceScoreDialog.NEW_SCORE.getLabel(), openLPolicy.getPolicyLossInformation().getAutoCreditBands(),
						AutoSSMetaData.RatingDetailReportsTab.EditInsuranceScoreDialog.REASON_FOR_OVERRIDE.getLabel(), "index=1",
						AutoSSMetaData.RatingDetailReportsTab.EditInsuranceScoreDialog.BTN_SAVE.getLabel(), "Click")
		);
		td.adjust(TestData.makeKeyPath(AutoSSMetaData.RatingDetailReportsTab.class.getSimpleName(), AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel()), insuranceScoreOverrideData);
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

		policyIssueData = TestDataHelper.merge(getMortgageeTabData(openLPolicy), policyIssueData);

		TestData documentsTabData = DataProviderFactory.dataOf(
				DocumentsTab.class.getSimpleName(), getDocumentsToBindData(openLPolicy));

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

	//	public boolean isProofAvailable(HomeSSOpenLPolicy openLPolicy) {
	//		return "Central".equals(openLPolicy.getPolicyDiscountInformation().getTheftAlarmType()) ||
	//				"Central".equals(openLPolicy.getPolicyDiscountInformation().getFireAlarmType()) ||
	//				isVisibleProofOfPEHCR(openLPolicy);
	//	}

	public TestData getDocumentsToBindData(HomeSSOpenLPolicy openLPolicy) {
		//		TestData documentsToBindData = DataProviderFactory.emptyData();
		LinkedHashMap<String, String> tdMap = new LinkedHashMap<>();
		if ("Central".equals(openLPolicy.getPolicyDiscountInformation().getTheftAlarmType())) {
			tdMap.put(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().getProofCentralTheftAlarm()));
		}
		if ("Central".equals(openLPolicy.getPolicyDiscountInformation().getFireAlarmType())) {
			tdMap.put(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_FIRE_ALARM.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().getProofCentralFireAlarm()));
		}
		//		if (isVisibleProofOfPEHCR(openLPolicy)) {
		//			tdMap.put(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_HOME_RENOVATIONS_FOR_MODERNIZATION.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().getProofOfPEHCR()));
		//		}
		if (receiptIsRequired(openLPolicy)) {
			tdMap.put(HomeSSMetaData.DocumentsTab.DocumentsToBind.APPRAISALS_SALES_RECEIPTS_FOR_SCHEDULED_PROPERTY.getLabel(), "Yes");
		}
		if ("DP3".equals(openLPolicy.getPolicyType()) && openLPolicy.getPolicyDiscountInformation().isUnderlyingRenterPolicy()) {
			tdMap.put(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_UNDERLYING_INSURANCE_POLICY.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().getProofOfTenant()));
		}
		return DataProviderFactory.dataOf(HomeSSMetaData.DocumentsTab.DOCUMENTS_TO_BIND.getLabel(), new SimpleDataProvider(tdMap));
	}

	public TestData getRemoveHS0490Data(HomeSSOpenLPolicy openLPolicy) {
		TestData removeFormData = DataProviderFactory.dataOf(new EndorsementTab().getMetaKey(), DataProviderFactory.dataOf(
				HomeSSMetaData.EndorsementTab.HS_04_90.getLabel(), DataProviderFactory.dataOf(
						"Action", "Remove")));
		if (openLPolicy.getForms().stream().anyMatch(c -> "HS0461".equals(c.getFormCode()))) {
			removeFormData = removeFormData.adjust(DataProviderFactory.dataOf(new PersonalPropertyTab().getMetaKey(), DataProviderFactory.emptyData()));
		}
		removeFormData = removeFormData.adjust(DataProviderFactory.dataOf(new PremiumsAndCoveragesQuoteTab().getMetaKey(), DataProviderFactory.dataOf(
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel(), openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCode())).findFirst().get().getLimit())));
		removeFormData = removeFormData.adjust(getMortgageeTabData(openLPolicy));

		return removeFormData;
	}

	private TestData getGeneralTabData(HomeSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.dataOf(
				HomeSSMetaData.GeneralTab.STATE.getLabel(), getState(),
				HomeSSMetaData.GeneralTab.POLICY_TYPE.getLabel(), openLPolicy.getPolicyType(),
				HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel(),
				openLPolicy.getEffectiveDate().minusYears(openLPolicy.getPolicyNamedInsured().getaAAPropPersistency()).format(DateTimeUtils.MM_DD_YYYY),
				HomeSSMetaData.GeneralTab.IMMEDIATE_PRIOR_CARRIER.getLabel(), getImmediatePriorCarrier(openLPolicy.getCappingDetails().getCarrierCode()),
				HomeSSMetaData.GeneralTab.CONTINUOUS_YEARS_WITH_IMMEDIATE_PRIOR_CARRIER.getLabel(), openLPolicy.getPolicyDiscountInformation().getHomeInsPersistency() - openLPolicy.getPolicyNamedInsured().getaAAPropPersistency() < 0 ?
						0 : openLPolicy.getPolicyDiscountInformation().getHomeInsPersistency() - openLPolicy.getPolicyNamedInsured().getaAAPropPersistency()
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
			String membershipNumber = ApplicationMocksManager.getRetrieveMembershipSummaryMock()
					.getMembershipNumber(openLPolicy.getEffectiveDate(), openLPolicy.getPolicyDiscountInformation().getMemberPersistency());
			assertThat(membershipNumber).as("No valid membership number was found for effectiveDate=%1$s and memberPersistency=%2$s fields", openLPolicy.getEffectiveDate(), openLPolicy.getPolicyDiscountInformation().getMemberPersistency())
					.isNotNull();

			aaaMembershipData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), "Yes",
					HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), membershipNumber//,
					//					HomeSSMetaData.ApplicantTab.AAAMembership.LAST_NAME.getLabel(), "Smith"
			);
		} else {
			aaaMembershipData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), "No");
		}

		TestData dwellingAddressData = DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip()
		);
		if (addressContainsCounty(openLPolicy.getPolicyAddress().getState())) {
			dwellingAddressData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.ApplicantTab.DwellingAddress.COUNTY.getLabel(), "County"));
		}

		List<TestData> otherActiveAAAPoliciesData = new ArrayList<>();
		boolean isFirstOtherActiveAAAPolicy = true;

		if ("DP3".equals(openLPolicy.getPolicyType())) {
			TestData ho3PolicyData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), isFirstOtherActiveAAAPolicy ? "Yes" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), isFirstOtherActiveAAAPolicy ? "click" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData(),
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "HO3",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), openLPolicy.getPolicyAddress().getState() + "HO3123456789",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.COVERAGE_E.getLabel(), "100000",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.DEDUCTIBLE.getLabel(), "1000",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.DWELLING_USAGE.getLabel(), "index=1",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.OCCUPANCY_TYPE.getLabel(), "index=1"
					)
			);
			otherActiveAAAPoliciesData.add(ho3PolicyData);
			isFirstOtherActiveAAAPolicy = false;
		}

		if (Boolean.TRUE.equals(openLPolicy.getPolicyDiscountInformation().isAMIGMotorPolicyInd())) {
			TestData aMIGMotorPolicyData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), isFirstOtherActiveAAAPolicy ? "Yes" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), isFirstOtherActiveAAAPolicy ? "click" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData(),
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "AMIG Motorcycle",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), "AMIGMC123456789",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY)
					)
			);
			otherActiveAAAPoliciesData.add(aMIGMotorPolicyData);
			isFirstOtherActiveAAAPolicy = false;
		}

		if (Boolean.TRUE.equals(openLPolicy.getPolicyDiscountInformation().isAMIGWatercraftPolicyInd())) {
			TestData aMIGWatercraftPolicyData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), isFirstOtherActiveAAAPolicy ? "Yes" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), isFirstOtherActiveAAAPolicy ? "click" : null,
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData(),
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "AMIG Watercraft",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), "AMIGWC123456789",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY)
					)
			);
			otherActiveAAAPoliciesData.add(aMIGWatercraftPolicyData);
			isFirstOtherActiveAAAPolicy = false;
		}

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

			TestData autoPolicyData;
			if (Constants.States.PA.equals(openLPolicy.getPolicyAddress().getState())) {
				autoPolicyData = DataProviderFactory.dataOf(
						HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), isFirstOtherActiveAAAPolicy ? "Yes" : null,
						HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), isFirstOtherActiveAAAPolicy ? "click" : null,
						HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.dataOf(
								HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesSearch.POLICY_TYPE.getLabel(), "Auto",
								HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesSearch.POLICY_NUMBER.getLabel(), autoPolicyNumber),
						HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
								"View/Edit", "click",
								"Save", "click"),
						HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_INSURANCE_PERSISTENCY.getLabel(), openLPolicy.getPolicyDiscountInformation().getAutoInsPersistency()
				);
			} else {
				autoPolicyData = DataProviderFactory.dataOf(
						HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), isFirstOtherActiveAAAPolicy ? "Yes" : null,
						HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), isFirstOtherActiveAAAPolicy ? "click" : null,
						HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData(),
						HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
								HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "Auto",
								HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_POLICY_STATE.getLabel(), openLPolicy.getPolicyAddress().getState(),
								HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.COMPANION_AUTO_PENDING_WITH_DISCOUNT.getLabel(), "No",
								HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), openLPolicy.getPolicyAddress().getState() + "SSA12345678",
								HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
								HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TIER.getLabel(), "No".equals(openLPolicy.getPolicyLossInformation().getAutoTier()) ? "N/A" : openLPolicy.getPolicyLossInformation().getAutoTier(),
								HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_POLICY_BI_LIMIT.getLabel(), "index=1",
								HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_INSURANCE_PERSISTENCY.getLabel(), openLPolicy.getPolicyDiscountInformation().getAutoInsPersistency()
						)
				);
			}
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
				HomeSSMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), getNumberOfFamilyUnits(openLPolicy)
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

		TestData propertyValueData = getPropertyValueData(openLPolicy);

		TestData constructionData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), String.format("%d", openLPolicy.getEffectiveDate().minusYears(openLPolicy.getPolicyDwellingRatingInfo().getHomeAge()).getYear()),
				HomeSSMetaData.PropertyInfoTab.Construction.ROOF_TYPE.getLabel(), openLPolicy.getPolicyDwellingRatingInfo().getRoofType(),
				HomeSSMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE.getLabel(), "contains=" + openLPolicy.getPolicyConstructionInfo().getConstructionType().split(" ")[0],
				HomeSSMetaData.PropertyInfoTab.Construction.MASONRY_VENEER.getLabel(), "Masonry Veneer".equals(openLPolicy.getPolicyConstructionInfo().getConstructionType()) ? "Yes" : "No"//,
				//HomeSSMetaData.PropertyInfoTab.Construction.IS_THIS_A_LOG_HOME_ASSEMBLED_BY_A_LICENSED_BUILDING_CONTRACTOR.getLabel(), "Log Home".equals(openLPolicy.getPolicyConstructionInfo().getConstructionType()) ? "Yes" : null
		);
		if (("HO3".equals(openLPolicy.getPolicyType()) || "DP3".equals(openLPolicy.getPolicyType())) && "Log Home".equals(openLPolicy.getPolicyConstructionInfo().getConstructionType())) {
			constructionData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PropertyInfoTab.Construction.IS_THIS_A_LOG_HOME_ASSEMBLED_BY_A_LICENSED_BUILDING_CONTRACTOR.getLabel(), "Yes"));
		}

		TestData additionalQuestionsData = getAdditionalQuestionsData(openLPolicy);

		TestData interiorData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.Interior.DWELLING_USAGE.getLabel(), Boolean.TRUE.equals(openLPolicy.getPolicyDwellingRatingInfo().getSecondaryHome()) ? "Secondary" : "Primary",
				HomeSSMetaData.PropertyInfoTab.Interior.NUMBER_OF_RESIDENTS.getLabel(), openLPolicy.getPolicyNamedInsured().getNumofOccupants(),
				HomeSSMetaData.PropertyInfoTab.Interior.NUMBER_OF_STORIES.getLabel(), getNumberOfStories(openLPolicy.getPolicyDwellingRatingInfo().getNoOfFloors())
		);

		TestData rentalInformationData = null;
		if ("DP3".equals(openLPolicy.getPolicyType())) {
			rentalInformationData = DataProviderFactory.dataOf(
					HomeSSMetaData.PropertyInfoTab.RentalInformation.NUMBER_OF_CONSECUTIVE_YEARS_INSURED_HAS_OWNED_ANY_RENTAL_PROPERTIES.getLabel(), openLPolicy.getPolicyDiscountInformation().getNoOfConsecutiveYrs(),
					HomeSSMetaData.PropertyInfoTab.RentalInformation.PROPERTY_MANAGER.getLabel(), openLPolicy.getPolicyDiscountInformation().getRentalPropertyMgr(),
					HomeSSMetaData.PropertyInfoTab.RentalInformation.DOES_THE_TENANT_HAVE_AN_UNDERLYING_HO4_POLICY.getLabel(), openLPolicy.getPolicyDiscountInformation().isUnderlyingRenterPolicy() ? "Yes" : "No"
			);
		}

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

		TestData homeRenovationData = DataProviderFactory.emptyData();
		if (!"HO4".equals(openLPolicy.getPolicyType())) {
			homeRenovationData = DataProviderFactory.dataOf(
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
					HomeSSMetaData.PropertyInfoTab.HomeRenovation.ROOFG_MONTH_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDwellingRatingInfo().getRoofAge() * 12 + 1).getMonthValue(),
					HomeSSMetaData.PropertyInfoTab.HomeRenovation.ROOF_YEAR_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDwellingRatingInfo().getRoofAge() * 12 + 1).getYear(),
					// heating/cooling
					HomeSSMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_RENOVATION.getLabel(), "Forced Air",
					HomeSSMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_PERCENT_COMPLETE.getLabel(), "100",
					HomeSSMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_MONTH_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovHeatOrCooling() * 12 + 1).getMonthValue(),
					HomeSSMetaData.PropertyInfoTab.HomeRenovation.HEATING_COOLING_YEAR_OF_COMPLECTION.getLabel(), openLPolicy.getEffectiveDate().minusMonths(openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovHeatOrCooling() * 12 + 1).getYear(),
					// green home discount
					HomeSSMetaData.PropertyInfoTab.HomeRenovation.GREEN_HOME_DISCOUNT.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().getGreenHomeDiscApplicability())
			);
		}

		List<TestData> petsOrAnimalsData = getPetsOrAnimalsData(openLPolicy);

		TestData stovesData = null;
		if (openLPolicy.getPolicyConstructionInfo().getWoodStove()) {
			stovesData = DataProviderFactory.dataOf(
					HomeSSMetaData.PropertyInfoTab.Stoves.DOES_THE_PROPERTY_HAVE_A_WOOD_BURNING_STOVE.getLabel(), "Yes",
					HomeSSMetaData.PropertyInfoTab.Stoves.IS_THE_STOVE_THE_SOLE_SOURCE_OF_HEAT.getLabel(), "No",
					HomeSSMetaData.PropertyInfoTab.Stoves.WAS_THE_STOVE_INSTALLED_BY_A_LICENSED_CONTRACTOR.getLabel(), "Yes",
					HomeSSMetaData.PropertyInfoTab.Stoves.DOES_THE_DWELLING_HAVE_AT_LEAST_ONE_SMOKE_DETECTOR_PER_STORY.getLabel(), "Yes"
			);
		}

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
				HomeSSMetaData.PropertyInfoTab.ADDITIONAL_QUESTIONS.getLabel(), additionalQuestionsData,
				HomeSSMetaData.PropertyInfoTab.INTERIOR.getLabel(), interiorData,
				HomeSSMetaData.PropertyInfoTab.RENTAL_INFORMATION.getLabel(), rentalInformationData,
				HomeSSMetaData.PropertyInfoTab.FIRE_PROTECTIVE_DD.getLabel(), fireProtectiveDeviceDiscountData,
				HomeSSMetaData.PropertyInfoTab.THEFT_PROTECTIVE_DD.getLabel(), theftProtectiveDeviceDiscountData,
				HomeSSMetaData.PropertyInfoTab.HOME_RENOVATION.getLabel(), homeRenovationData,
				HomeSSMetaData.PropertyInfoTab.PETS_OR_ANIMALS.getLabel(), petsOrAnimalsData,
				HomeSSMetaData.PropertyInfoTab.STOVES.getLabel(), stovesData,
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
		productOfferingTabData.put(HomeSSMetaData.ProductOfferingTab.VariationControls.COVERAGE_E.getLabel(), new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCode())).findFirst().get().getLimit()).toString().split("\\.")[0]);
		return DataProviderFactory.dataOf(policyLevel, new SimpleDataProvider(productOfferingTabData));
	}

	private TestData getEndorsementTabData(HomeSSOpenLPolicy openLPolicy) {

		TestData endorsementData = new SimpleDataProvider();
		switch (openLPolicy.getPolicyType()) {
			case "DP3":
				for (HomeSSOpenLForm openLForm : openLPolicy.getForms()) {
					String formCode = openLForm.getFormCode();
					if (!endorsementData.containsKey(HomeSSDP3FormTestDataGenerator.getFormMetaKey(formCode))) {
						List<TestData> tdList = HomeSSDP3FormTestDataGenerator.getFormTestData(openLPolicy, formCode);
						if (tdList != null) {
							TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeSSDP3FormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeSSDP3FormTestDataGenerator.getFormMetaKey(formCode), tdList);
							endorsementData.adjust(td);
						}
					}
				}
				break;
			case "HO4":
				if (isFormPresent(openLPolicy, "HS0436")) {
					endorsementData.adjust(DataProviderFactory.dataOf(
							HomeSSMetaData.EndorsementTab.HS_04_54.getLabel(), DataProviderFactory.dataOf("Action", "Add")));
				}
				for (HomeSSOpenLForm openLForm : openLPolicy.getForms()) {
					String formCode = openLForm.getFormCode();
					if (!endorsementData.containsKey(HomeSSHO4FormTestDataGenerator.getFormMetaKey(formCode))) {
						List<TestData> tdList = HomeSSHO4FormTestDataGenerator.getFormTestData(openLPolicy, formCode);
						if (tdList != null) {
							TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeSSHO4FormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeSSHO4FormTestDataGenerator.getFormMetaKey(formCode), tdList);
							endorsementData.adjust(td);
						}
					}
				}
				break;
			case "HO6":
				for (HomeSSOpenLForm openLForm : openLPolicy.getForms()) {
					String formCode = openLForm.getFormCode();
					if (!endorsementData.containsKey(HomeSSHO6FormTestDataGenerator.getFormMetaKey(formCode))) {
						List<TestData> tdList = HomeSSHO6FormTestDataGenerator.getFormTestData(openLPolicy, formCode);
						if (tdList != null) {
							TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeSSHO6FormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeSSHO6FormTestDataGenerator.getFormMetaKey(formCode), tdList);
							endorsementData.adjust(td);
						}
					}
				}
				break;
			default:
				for (HomeSSOpenLForm openLForm : openLPolicy.getForms()) {
					String formCode = openLForm.getFormCode();
					if (!endorsementData.containsKey(HomeSSHO3FormTestDataGenerator.getFormMetaKey(formCode))) {
						List<TestData> tdList = HomeSSHO3FormTestDataGenerator.getFormTestData(openLPolicy, formCode);
						if (tdList != null) {
							TestData td = tdList.size() == 1 ? DataProviderFactory.dataOf(HomeSSHO3FormTestDataGenerator.getFormMetaKey(formCode), tdList.get(0)) : DataProviderFactory.dataOf(HomeSSHO3FormTestDataGenerator.getFormMetaKey(formCode), tdList);
							endorsementData.adjust(td);
						}
					}
				}
		}
		return endorsementData;
	}

	private boolean isFormPresent(HomeSSOpenLPolicy openLPolicy, String formCode) {
		boolean isFormPresent = false;
		for (HomeSSOpenLForm openLForm : openLPolicy.getForms()) {
			if (formCode.equals(openLForm.getFormCode())) {
				isFormPresent = true;
			}
		}
		return isFormPresent;
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
					case "Cameras":
						TestData camerasData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.Cameras.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.Cameras.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.CAMERAS.getLabel(), camerasData));
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
					case "Golfer's Equipment":
						TestData golfEquipmentData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.GolfEquipment.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.GolfEquipment.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.GOLF_EQUIPMENT.getLabel(), golfEquipmentData));
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
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.TRADING_CARDS_OR_COMICS.getLabel(), tradingCardsOrComics));
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
						assertThat(Boolean.TRUE).as("Unknown Type of Coverage: %s", form.getType()).isFalse();
				}
			}
		}
		return personalPropertyData;
	}

	private TestData getPremiumsAndCoveragesQuoteTabData(HomeSSOpenLPolicy openLPolicy, boolean isLegacyConvPolicy) {

		Double covA = Double.parseDouble(openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCode())).findFirst().get().getLimit());
		Double covB = Double.parseDouble(openLPolicy.getCoverages().stream().filter(c -> "CovB".equals(c.getCode())).findFirst().get().getLimit());
		Double covD = Double.parseDouble(openLPolicy.getCoverages().stream().filter(c -> "CovD".equals(c.getCode())).findFirst().get().getLimit());

		String coverageDeductible = openLPolicy.getPolicyCoverageDeductible().getCoverageDeductible().split("/")[0];
		String hurricane = null;
		if (openLPolicy.getPolicyCoverageDeductible().getCoverageDeductible().contains("Hurricane")) {
			hurricane = "contains=" + openLPolicy.getPolicyCoverageDeductible().getCoverageDeductible().split("/")[1].substring(0, 1);
		}

		TestData premiumAndCoveragesQuoteTabData = DataProviderFactory.emptyData();
		if (isLegacyConvPolicy & BillingConstants.PaymentPlan.MORTGAGEE_BILL.equals(openLPolicy.getPolicyDiscountInformation().getPaymentPlan())) {

			premiumAndCoveragesQuoteTabData = DataProviderFactory.dataOf(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.BILL_TO_AT_RENEWAL.getLabel(), "Mortgagee");
		}

		if ("HO3".equals(openLPolicy.getPolicyType()) || "DP3".equals(openLPolicy.getPolicyType())) {
			premiumAndCoveragesQuoteTabData = premiumAndCoveragesQuoteTabData.adjust(DataProviderFactory.dataOf(
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel(), "contains=" + getPaymentPlan(openLPolicy, isLegacyConvPolicy),
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_B.getLabel(), "contains=" + String.format("%d%%", (int) Math.round(covB * 100 / covA)),
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel(), openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCode())).findFirst().get().getLimit(),
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), "contains=" + String.format("%d%%", (int) Math.round(covD * 100 / covA)),
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), "contains=" + new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCode())).findFirst().get().getLimit()).toString().split("\\.")[0],
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_F.getLabel(), new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovF".equals(c.getCode())).findFirst().get().getLimit()).toString().split("\\.")[0],
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), new Dollar(coverageDeductible).toString().split("\\.")[0],
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.HURRICANE_DEDUCTIBLE.getLabel(), hurricane
			));
		}
		if ("HO4".equals(openLPolicy.getPolicyType())) {
			premiumAndCoveragesQuoteTabData = premiumAndCoveragesQuoteTabData.adjust(DataProviderFactory.dataOf(
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel(), "contains=" + getPaymentPlan(openLPolicy, isLegacyConvPolicy),
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), "contains=" + new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCode())).findFirst().get().getLimit()).toString().split("\\.")[0],
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_F.getLabel(), new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovF".equals(c.getCode())).findFirst().get().getLimit()).toString().split("\\.")[0],
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), new Dollar(coverageDeductible).toString().split("\\.")[0]
			));
		}
		if ("HO6".equals(openLPolicy.getPolicyType())) {
			premiumAndCoveragesQuoteTabData = premiumAndCoveragesQuoteTabData.adjust(DataProviderFactory.dataOf(
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel(), "contains=" + getPaymentPlan(openLPolicy, isLegacyConvPolicy),
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel(), openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCode())).findFirst().get().getLimit(),
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), "contains=" + String.format("%d%%", (int) Math.round(covD * 100 / covA)),
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), "contains=" + new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCode())).findFirst().get().getLimit()).toString().split("\\.")[0],
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_F.getLabel(), new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovF".equals(c.getCode())).findFirst().get().getLimit()).toString().split("\\.")[0],
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), new Dollar(coverageDeductible).toString().split("\\.")[0],
					HomeSSMetaData.PremiumsAndCoveragesQuoteTab.HURRICANE_DEDUCTIBLE.getLabel(), hurricane
			));
		}
		return premiumAndCoveragesQuoteTabData;
	}

	private TestData getMortgageeTabData(HomeSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.dataOf(
				new MortgageesTab().getMetaKey(), DataProviderFactory.dataOf(
						HomeSSMetaData.MortgageesTab.MORTGAGEE.getLabel(), "Yes",
						HomeSSMetaData.MortgageesTab.MORTGAGEE_INFORMATION.getLabel(), DataProviderFactory.dataOf(
								HomeSSMetaData.MortgageesTab.MortgageeInformation.NAME.getLabel(), "John Smith",
								HomeSSMetaData.MortgageesTab.MortgageeInformation.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().getZip(),
								HomeSSMetaData.MortgageesTab.MortgageeInformation.STREET_ADDRESS_1.getLabel(), "111 Street Address",
								HomeSSMetaData.MortgageesTab.MortgageeInformation.VALIDATE_ADDRESS_BTN.getLabel(), true,
								HomeSSMetaData.MortgageesTab.MortgageeInformation.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.emptyData(),
								HomeSSMetaData.MortgageesTab.MortgageeInformation.LOAN_NUMBER.getLabel(), "123456789"))
		);
	}

	private String getNumberOfFamilyUnits(HomeSSOpenLPolicy openLPolicy) {
		String numberOfFamilyUnits = "index=1";
		int number = openLPolicy.getPolicyDwellingRatingInfo().getFamilyUnits();
		if ("HO3".equals(openLPolicy.getPolicyType()) || "DP3".equals(openLPolicy.getPolicyType())) {
			if (number < 5) {
				numberOfFamilyUnits = "contains=" + number;
			} else {
				numberOfFamilyUnits = "contains=5";
			}
		}
		if ("HO4".equals(openLPolicy.getPolicyType()) || "HO6".equals(openLPolicy.getPolicyType())) {
			if (number < 5) {
				numberOfFamilyUnits = "contains=" + number;
			} else if (number >= 5 && number <= 15) {
				numberOfFamilyUnits = "contains=5-15";
			} else if (number >= 16 && number <= 30) {
				numberOfFamilyUnits = "contains=16-30";
			} else if (number >= 31 && number <= 45) {
				numberOfFamilyUnits = "contains=31-45";
			} else if (number >= 46 && number <= 40) {
				numberOfFamilyUnits = "contains=46-60";
			} else if (number >= 61) {
				numberOfFamilyUnits = "contains=61";
			}
		}
		return numberOfFamilyUnits;
	}

	private TestData getPropertyValueData(HomeSSOpenLPolicy openLPolicy) {
		String policyType = openLPolicy.getPolicyType();
		switch (policyType) {
			case "HO3":
			case "HO6":
			case "DP3":
				return DataProviderFactory.dataOf(
						HomeSSMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT.getLabel(), openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCode())).findFirst().get().getLimit(),
						HomeSSMetaData.PropertyInfoTab.PropertyValue.ISO_REPLACEMENT_COST.getLabel(), openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCode())).findFirst().get().getLimit(),
						HomeSSMetaData.PropertyInfoTab.PropertyValue.PURCHASE_DATE_OF_HOME.getLabel(), openLPolicy.getEffectiveDate().minusYears(openLPolicy.getPolicyDiscountInformation().getNoOfYrsSinceLoanInception()).format(DateTimeUtils.MM_DD_YYYY),
						HomeSSMetaData.PropertyInfoTab.PropertyValue.NEW_LOAN.getLabel(), true);
			case "HO4":
				return DataProviderFactory.dataOf(
						HomeSSMetaData.PropertyInfoTab.PropertyValue.PERSONAL_PROPERTY_VALUE.getLabel(), openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCode())).findFirst().get().getLimit());
			default:
				throw new IstfException("Unknown Policy Type: " + openLPolicy.getPolicyType());
		}
	}

	private TestData getAdditionalQuestionsData(HomeSSOpenLPolicy openLPolicy) {
		TestData additionalQuestionsData = null;
		String protectionClass = openLPolicy.getPolicyDwellingRatingInfo().getProtectionClass();
		if ("HO4".equals(openLPolicy.getPolicyType()) && ("10".equals(protectionClass) || "10W".equals(protectionClass)) ||
				!"HO4".equals(openLPolicy.getPolicyType()) && ("10".equals(protectionClass) || "10W".equals(protectionClass))
						&& !"Wood shingle/Wood shake".equals(openLPolicy.getPolicyDwellingRatingInfo().getRoofType())
						&& Double.parseDouble(openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCode())).findFirst().get().getLimit()) <= 500000.0) {
			additionalQuestionsData = DataProviderFactory.dataOf(
					HomeSSMetaData.PropertyInfoTab.AdditionalQuestions.IS_THE_LENGTH_OF_DRIVEWAY_LESS_THAN_500_FEET.getLabel(), "No",
					HomeSSMetaData.PropertyInfoTab.AdditionalQuestions.IS_THE_ROAD_TO_THE_HOME_AND_DRIVEWAY_PAVED.getLabel(), "No",
					HomeSSMetaData.PropertyInfoTab.AdditionalQuestions.IS_THERE_A_CREDITABLE_ALTERNATIVE_WATER_SOURCE_WITHIN_1_000_FEET_OF_THE_PROPERTY.getLabel(), "No"
			);
		}
		return additionalQuestionsData;
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

	private String getPaymentPlan(HomeSSOpenLPolicy openLPolicy, boolean isLegacyConvPolicy) {
		String paymentPlan = openLPolicy.getPolicyDiscountInformation().getPaymentPlan();

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
				return isLegacyConvPolicy ? BillingConstants.PaymentPlan.ELEVEN_PAY : "Eleven Pay Low Down";
			case "Monthly Standard":
				return BillingConstants.PaymentPlan.ELEVEN_PAY;
			case "Monthly Low down":
				return isLegacyConvPolicy ? BillingConstants.PaymentPlan.ELEVEN_PAY : "HO6".equals(openLPolicy.getPolicyType()) ? BillingConstants.PaymentPlan.MONTHLY : BillingConstants.PaymentPlan.MONTHLY_LOW_DOWN;
			default:
				throw new IstfException("Unknown mapping for payment plan : " + paymentPlan);
		}
	}

	private boolean isCoastalState(String state) {
		return Constants.States.CT.equals(state) ||
				Constants.States.DE.equals(state) ||
				Constants.States.MD.equals(state) ||
				Constants.States.NJ.equals(state) ||
				Constants.States.VA.equals(state);
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
				case "CSAA General Insurance Company":
					immediatePriorCarrier = "AAA Insurance";
					break;
				default:
					immediatePriorCarrier = carrierCode;
			}
		}
		return immediatePriorCarrier;
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
				case "Fenced in-ground with safety net":
					trampolineType = "Restricted access in-ground with safety net";
					break;
				case "Fenced in-ground without safety net":
					trampolineType = "Restricted access in-ground without safety net";
					break;
				case "Unrestricted access":        //???
					trampolineType = "Unrestricted access";
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
				Constants.States.OH.equals(state) ||
				Constants.States.WV.equals(state);
	}

	private boolean insuranceScoreReport(String state) {return !"MD".equals(state);}

	private boolean isVisibleProofOfPEHCR(HomeSSOpenLPolicy openLPolicy) {
		boolean isVisibleProofOfPEHCR = false;
		if (openLPolicy.getPolicyDwellingRatingInfo().getHomeAge() >= 10 &&
				(openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovPlumbing() < 10 ||
						openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovHeatOrCooling() < 10 ||
						openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovElectrical() < 10 ||
						openLPolicy.getPolicyDiscountInformation().getTimeSinceRenovRoof() < 10)) {
			isVisibleProofOfPEHCR = true;
		}
		return isVisibleProofOfPEHCR;
	}

	private boolean receiptIsRequired(HomeSSOpenLPolicy openLPolicy) {
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0461".equals(form.getFormCode())) {
				switch (form.getType()) {
					case "Musical Instruments":
						return true;
					//TODO add properties/limits requiring receipts
					default:
						return false;
				}
			}
		}
		return false;
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
			if (aaaPoints > 0) {
				claimsDataList.addAll(claimTestDataGenerator.getClaimTestData(true, isFirstClaim));
				isFirstClaim = false;
			}
			if (notAAAPoints > 0) {
				claimsDataList.addAll(claimTestDataGenerator.getClaimTestData(false, isFirstClaim));
			}
		}
		return claimsDataList;
	}
}
