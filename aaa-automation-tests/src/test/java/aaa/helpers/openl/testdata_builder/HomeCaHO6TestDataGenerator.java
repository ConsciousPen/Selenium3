package aaa.helpers.openl.testdata_builder;

import org.apache.commons.lang3.NotImplementedException;

import com.exigen.ipb.etcsa.utils.Dollar;

import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLForm;
import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLPolicy;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ReportsTab;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class HomeCaHO6TestDataGenerator extends TestDataGenerator<HomeCaHO6OpenLPolicy> {
	public HomeCaHO6TestDataGenerator(String state) {
		super(state);
	}

	public HomeCaHO6TestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeCaHO6OpenLPolicy openLPolicy) {
		TestData ratingDataPattern = getRatingDataPattern().resolveLinks();	
		TestData maskedMembershipData = ratingDataPattern.getTestData(new ApplicantTab().getMetaKey()).mask(HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel());
		TestData maskedReportsData = ratingDataPattern.getTestData(new ReportsTab().getMetaKey()).mask(HomeCaMetaData.ReportsTab.AAA_MEMBERSHIP_REPORT.getLabel());
		if (Boolean.FALSE.equals(openLPolicy.getAaaMember())) {
			ratingDataPattern.adjust(new ApplicantTab().getMetaKey(), maskedMembershipData);	
			ratingDataPattern.adjust(new ReportsTab().getMetaKey(), maskedReportsData);
		}
		
		TestData td = DataProviderFactory.dataOf(
				new GeneralTab().getMetaKey(), getGeneralTabData(openLPolicy),
				new ApplicantTab().getMetaKey(), getApplicantTabData(openLPolicy),
				new ReportsTab().getMetaKey(), getReportsTabData(openLPolicy),
				new PropertyInfoTab().getMetaKey(), getPropertyInfoTabData(openLPolicy),
				new EndorsementTab().getMetaKey(), getEndorsementTabData(openLPolicy),
				new PremiumsAndCoveragesQuoteTab().getMetaKey(), getPremiumsAndCoveragesQuoteTabData(openLPolicy));
		
		return TestDataHelper.merge(ratingDataPattern, td);
	}
	
	private TestData getGeneralTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		TestData policyInfo = DataProviderFactory.emptyData();
		TestData currentCarrier = DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.CurrentCarrier.CONTINUOUS_YEARS_WITH_HO_INSURANCE.getLabel(), openLPolicy.getYearsOfPriorInsurance(),
				HomeCaMetaData.GeneralTab.CurrentCarrier.BASE_DATE_WITH_AAA.getLabel(),
					openLPolicy.getEffectiveDate().minusYears(openLPolicy.getYearsWithCsaa()).format(DateTimeUtils.MM_DD_YYYY));
		return DataProviderFactory.dataOf(
				HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), policyInfo,
				HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), currentCarrier);
	}

	private TestData getApplicantTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		TestData namedInsured = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NamedInsured.AAA_EMPLOYEE.getLabel(), getYesOrNo(openLPolicy.getHasEmployeeDiscount()));
		if (openLPolicy.getHasSeniorDiscount()) {
			namedInsured.adjust(HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(),
				openLPolicy.getEffectiveDate().minusYears(60).format(DateTimeUtils.MM_DD_YYYY));
		}
		TestData aaaMembership = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), getYesOrNo(openLPolicy.getAaaMember()));
		if (Boolean.TRUE.equals(openLPolicy.getAaaMember())) {
			aaaMembership.adjust(HomeCaMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "4290023667710001");
			aaaMembership.adjust(HomeCaMetaData.ApplicantTab.AAAMembership.LAST_NAME.getLabel(), "Smith");
		}		
		TestData dwellingAddress = DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), openLPolicy.getDwellings().get(0).getAddress().get(0).getZipCode());
		return DataProviderFactory.dataOf(
				HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), namedInsured,
				HomeCaMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), aaaMembership,
				HomeCaMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), dwellingAddress);
	}
	
	private TestData getReportsTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
	
	private TestData getPropertyInfoTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		return null;
	}
	
	private TestData getEndorsementTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		return null;
	}
	
	private TestData getPremiumsAndCoveragesQuoteTabData(HomeCaHO6OpenLPolicy openLPolicy) {
		//Coverage A is disabled on Premiums & Coverges Quote tab
		//Double covA = openLPolicy.getCoverages().stream().filter(c -> "CovA".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covC = openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covD = openLPolicy.getCoverages().stream().filter(c -> "CovD".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
		Double covE = openLPolicy.getCoverages().stream().filter(c -> "CovE".equals(c.getCoverageCd())).findFirst().get().getLimitAmount();
				
		return DataProviderFactory.dataOf(
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C.getLabel(), covC.toString().split("\\.")[0], 
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_D.getLabel(), covD.toString().split("\\.")[0], 
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E.getLabel(), new Dollar(covE).toString().split("\\.")[0], 
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.DEDUCTIBLE.getLabel(), getDeductibleValueByForm(openLPolicy));
	}
	
	private String getDeductibleValueByForm(HomeCaHO6OpenLPolicy openLPolicy) {
		String deductible = "index=1";
		
		for(HomeCaHO6OpenLForm form: openLPolicy.getForms()) {
			if (form.getFormCode().equals("HO-59C")) {
				deductible = "contains=" + new Dollar(500).toString().split("\\.")[0];
			}
			else if (form.getFormCode().equals("HO-76C")) {
				deductible = "contains=" + new Dollar(1500).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().equals("HO-77C")) {
				deductible = "contains=" + new Dollar(2000).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().equals("HO-78C")) {
				deductible = "contains=" + new Dollar(2500).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().equals("HO-79C")) {
				deductible = "contains=" + new Dollar(3000).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().equals("HO-80C")) {
				deductible = "contains=" + new Dollar(4000).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().equals("HO-81C")) {
				deductible = "contains=" + new Dollar(5000).toString().split("\\.")[0]; 
			}
			else if (form.getFormCode().equals("HO-82C")) {
				deductible = "contains=" + new Dollar(7500).toString().split("\\.")[0]; 
			}
		}
		return deductible;
	}
	
	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
