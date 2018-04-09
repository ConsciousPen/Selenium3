package aaa.helpers.openl.testdata_builder;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLForm;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssertions;

public class HomeSSTestDataGenerator extends TestDataGenerator<HomeSSOpenLPolicy> {

	public HomeSSTestDataGenerator(String state) {
		super(state);
	}

	public HomeSSTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeSSOpenLPolicy openLPolicy) {

		TestData td = DataProviderFactory.dataOf(
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new ApplicantTab().getMetaKey(), getApplicantTabData(openLPolicy),
				new ReportsTab().getMetaKey(), getReportsTabData(),
				new PropertyInfoTab().getMetaKey(), getPropertyInfoTabData(openLPolicy),
				new ProductOfferingTab().getMetaKey(), getProductOfferingTabData(openLPolicy),
				new EndorsementTab().getMetaKey(), getEndorsementTabData(openLPolicy),
				new PersonalPropertyTab().getMetaKey(), getPersonalPropertyTabData(openLPolicy),
				new PremiumsAndCoveragesQuoteTab().getMetaKey(), getPremiumsAndCoveragesQuoteTabData(openLPolicy)
		);

		return TestDataHelper.merge(getRatingDataPattern().mask(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel())), td);
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}

	private TestData getGeneralTabData(HomeSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.dataOf(
				HomeSSMetaData.GeneralTab.STATE.getLabel(), getState(),
				HomeSSMetaData.GeneralTab.POLICY_TYPE.getLabel(), openLPolicy.getPolicyType(),
				HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
				HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel(),
				openLPolicy.getEffectiveDate().minusYears(openLPolicy.getPolicyNamedInsured().get(0).getaAAPropPersistency()).format(DateTimeUtils.MM_DD_YYYY)
		);
	}

	private TestData getApplicantTabData(HomeSSOpenLPolicy openLPolicy) {

		TestData namedInsuredData = DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().get(0).isAAAEmployee())
		);

		TestData aaaMembershipData = DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), getYesOrNo(openLPolicy.getPolicyDiscountInformation().get(0).isCurrAAAMember())
		);

//		int streetNumber = RandomUtils.nextInt(100, 1000);
//		String streetName = RandomStringUtils.randomAlphabetic(10).toUpperCase() + " St";
		TestData dwellingAddressData = DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getPolicyAddress().get(0).getZip()
//				HomeSSMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel(), streetNumber + " " + streetName,
//				HomeSSMetaData.ApplicantTab.DwellingAddress.VALIDATE_ADDRESS_DIALOG.getLabel(), DataProviderFactory.dataOf("Street number", streetNumber, "Street Name", streetName)
		);

		TestData otherActiveAAAPoliciesData = DataProviderFactory.emptyData();
		if ((Boolean.TRUE).equals(openLPolicy.getPolicyDiscountInformation().get(0).isAutoPolicyInd())) {
			otherActiveAAAPoliciesData = DataProviderFactory.dataOf(
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OTHER_ACTIVE_AAA_POLICIES.getLabel(), "Yes",
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN.getLabel(), "click",
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH.getLabel(), DataProviderFactory.emptyData(),
					HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL.getLabel(), DataProviderFactory.dataOf(
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TYPE.getLabel(), "Auto",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_POLICY_STATE.getLabel(), openLPolicy.getPolicyAddress().get(0).getState(),
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.COMPANION_AUTO_PENDING_WITH_DISCOUNT.getLabel(), "No",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_NUMBER.getLabel(), "123456789",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.EFFECTIVE_DATE.getLabel(), openLPolicy.getEffectiveDate().format(DateTimeUtils.MM_DD_YYYY),
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TIER.getLabel(), openLPolicy.getPolicyLossInformation().get(0).getAutoTier(),
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_POLICY_BI_LIMIT.getLabel(), "index=1",
							HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.AUTO_INSURANCE_PERSISTENCY.getLabel(), openLPolicy.getPolicyDiscountInformation().get(0).getAutoInsPersistency()
					)
			);
		}

		TestData activeUnderlyingPoliciesAutoData = DataProviderFactory.dataOf(

		);

		return DataProviderFactory.dataOf(
				HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), Arrays.asList(namedInsuredData),
				HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), aaaMembershipData,
				HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel(), otherActiveAAAPoliciesData
		);
	}

	private TestData getReportsTabData() {
		return DataProviderFactory.emptyData();
	}

	private TestData getPropertyInfoTabData(HomeSSOpenLPolicy openLPolicy) {

		TestData dwellingAddressData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.DwellingAddress.NUMBER_OF_FAMILY_UNITS.getLabel(), "contains=" + openLPolicy.getPolicyDwellingRatingInfo().get(0).getFamilyUnits()
		);

		TestData publicProtectionClassData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.PublicProtectionClass.PUBLIC_PROTECTION_CLASS.getLabel(), openLPolicy.getPolicyDwellingRatingInfo().get(0).getProtectionClass()
		);

		TestData propertyValueData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.PropertyValue.COVERAGE_A_DWELLING_LIMIT.getLabel(), openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCoverageCd())).findFirst().get().getLimit()
		);

		TestData constructionData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.Construction.YEAR_BUILT.getLabel(), openLPolicy.getPolicyDwellingRatingInfo().get(0).getYearBuilt(),
				HomeSSMetaData.PropertyInfoTab.Construction.ROOF_TYPE.getLabel(), openLPolicy.getPolicyDwellingRatingInfo().get(0).getRoofType(),
				HomeSSMetaData.PropertyInfoTab.Construction.CONSTRUCTION_TYPE.getLabel(), openLPolicy.getPolicyConstructionInfo().get(0).getConstructionType(),
				HomeSSMetaData.PropertyInfoTab.Construction.MASONRY_VENEER.getLabel(),
				"Masonry Veneer".equals(openLPolicy.getPolicyConstructionInfo().get(0).getConstructionType()) ? "Yes" : "No"
		);

		TestData interiorData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.Interior.DWELLING_USAGE.getLabel(),
				Boolean.TRUE.equals(openLPolicy.getPolicyDwellingRatingInfo().get(0).getSecondaryHome()) ? "Secondary" : "Primary"
		);

		TestData fireFireProtectiveDeviceDiscountData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.FireProtectiveDD.LOCAL_FIRE_ALARM.getLabel(),
				"Local".equals(openLPolicy.getPolicyDiscountInformation().get(0).getFireAlarmType()),
				HomeSSMetaData.PropertyInfoTab.FireProtectiveDD.CENTRAL_FIRE_ALARM.getLabel(),
				"Central".equals(openLPolicy.getPolicyDiscountInformation().get(0).getFireAlarmType()),
				HomeSSMetaData.PropertyInfoTab.FireProtectiveDD.FULL_RESIDENTIAL_SPRINKLERS.getLabel(),
				"Full".equals(openLPolicy.getPolicyDiscountInformation().get(0).getSprinklerType()),
				HomeSSMetaData.PropertyInfoTab.FireProtectiveDD.PARTIAL_RESIDENTIAL_SPRINKLERS.getLabel(),
				"Partial".equals(openLPolicy.getPolicyDiscountInformation().get(0).getSprinklerType())
		);

		TestData theftProtectiveDeviceDiscountData = DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.TheftProtectiveTPDD.LOCAL_THEFT_ALARM.getLabel(),
				"Local".equals(openLPolicy.getPolicyDiscountInformation().get(0).getTheftAlarmType()),
				HomeSSMetaData.PropertyInfoTab.TheftProtectiveTPDD.CENTRAL_THEFT_ALARM.getLabel(),
				"Central".equals(openLPolicy.getPolicyDiscountInformation().get(0).getTheftAlarmType()),
				HomeSSMetaData.PropertyInfoTab.TheftProtectiveTPDD.GATED_COMMUNITY.getLabel(), openLPolicy.getPolicyDiscountInformation().get(0).isPrivateCommunity()
		);

		return DataProviderFactory.dataOf(
				HomeSSMetaData.PropertyInfoTab.DWELLING_ADDRESS.getLabel(), dwellingAddressData,
				HomeSSMetaData.PropertyInfoTab.PUBLIC_PROTECTION_CLASS.getLabel(), publicProtectionClassData,
				HomeSSMetaData.PropertyInfoTab.PROPERTY_VALUE.getLabel(), propertyValueData,
				HomeSSMetaData.PropertyInfoTab.CONSTRUCTION.getLabel(), constructionData,
				HomeSSMetaData.PropertyInfoTab.INTERIOR.getLabel(), interiorData,
				HomeSSMetaData.PropertyInfoTab.FIRE_PROTECTIVE_DD.getLabel(), fireFireProtectiveDeviceDiscountData,
				HomeSSMetaData.PropertyInfoTab.THEFT_PROTECTIVE_DD.getLabel(), theftProtectiveDeviceDiscountData
		);
	}

	private TestData getProductOfferingTabData(HomeSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
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
		endorsementData.mask(new EndorsementTab().getMetaKey(), HomeSSMetaData.EndorsementTab.HS_04_90.getLabel()); //TODO
		return endorsementData;
	}

	private TestData getPersonalPropertyTabData(HomeSSOpenLPolicy openLPolicy) {
		TestData personalPropertyData = null;
		for (HomeSSOpenLForm form : openLPolicy.getForms()) {
			if ("HS0461".equals(form.getFormCode())) {
				if (personalPropertyData == null)
					personalPropertyData = new SimpleDataProvider();
				switch (form.getType()) {
					case "Furs":
						TestData fursData = DataProviderFactory.dataOf(
								HomeSSMetaData.PersonalPropertyTab.Furs.LIMIT_OF_LIABILITY.getLabel(), form.getLimit().toString().split("\\.")[0],
								HomeSSMetaData.PersonalPropertyTab.Furs.DESCRIPTION.getLabel(), "Description"
						);
						personalPropertyData.adjust(DataProviderFactory.dataOf(HomeSSMetaData.PersonalPropertyTab.FURS.getLabel(), fursData));
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

		return DataProviderFactory.dataOf(
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_B.getLabel(), "contains=" + String.format("%d%%", (int)Math.round (covB * 100 / covA)),
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel(), openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCoverageCd())).findFirst().get().getLimit(),
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), "contains=" + String.format("%d%%", (int) Math.round(covD * 100 / covA)),
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCoverageCd())).findFirst().get().getLimit()).toString().split("\\.")[0],
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_F.getLabel(), new Dollar(openLPolicy.getCoverages().stream().filter(c -> "CovF".equals(c.getCoverageCd())).findFirst().get().getLimit()).toString().split("\\.")[0]
		);
	}
}
