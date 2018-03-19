package aaa.modules.regression.document_fulfillment.home_ss.dp3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestMaigSpecificFormsGenerationTemplate;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;
import java.util.List;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.PRE_RENEWAL;
import static org.assertj.core.api.Assertions.assertThat;

public class TestMaigSpecificFormsGeneration extends TestMaigSpecificFormsGenerationTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_DP3;
	}


	/**
	 * Specific Conversion Packet Generation for DE, VA , PA , CW with default payment plan
	 * @author Viktor Petrenko
	 * PAS-9607
	 * PAS-2674
	 * PAS-8777
	 * PAS-8766
	 * @throws NoSuchFieldException
	 * See detailed steps in template file
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2674"})
	public void pas2674_ConversionPacket(@Optional("PA") String state) throws NoSuchFieldException {
		verifyConversionFormsSequence(getTestDataWithAdditionalInterest(getConversionPolicyDefaultTD()));
	}

	/**
	 * Specific Conversion Packet Generation for DE, VA , PA , CW with mortgagee payment plan
	 * @author Viktor Petrenko
	 * PAS-9607
	 * PAS-2674
	 * PAS-8777
	 * PAS-8766
	 * @throws NoSuchFieldException
	 * See detailed steps in template file
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-2674"})
	public void pas2674_ConversionPacketMortgagee(@Optional("DE") String state) throws NoSuchFieldException {
		// CW, DE, VA
		verifyConversionFormsSequence(adjustWithMortgageeData(getConversionPolicyDefaultTD()));
	}

	/**
	 * Specific Billing Packet Generation For CW, DE, VA
	 * @author Viktor Petrenko
	 * PAS-9816
	 * PAS-9607
	 * @throws NoSuchFieldException
	 * See detailed steps in template file
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-9816"})
	public void pas9816_BillingPacketGeneration(@Optional("DE") String state) throws NoSuchFieldException {
		verifyBillingFormsSequence(getConversionPolicyDefaultTD().adjust(TestData.makeKeyPath("PremiumsAndCoveragesQuoteTab", "Payment plan"), "Monthly (Renewal)").resolveLinks());
	}

	/**
	 * CONTENT & TRIGGER (timeline): Pre-Renewal letter (insured bill) PA DP3
	 * @author Viktor Petrenko
	 * PAS-6731
	 *
	 *
	 * Set Home Insurance payments are SETUP to be billed directly to me
	 * Generate Pre Renewal offer on R-65 timeline and check HSRNMXX presence
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-6731"})
	public void pas6731_PreRenewalLetterGeneration(@Optional("PA") String state){
		LocalDateTime renewalOfferEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(70);

		String policyNumber = generatePreRenewalEvent(getConversionPolicyDefaultTD(),renewalOfferEffectiveDate, getTimePoints().getPreRenewalLetterGenerationDate(renewalOfferEffectiveDate));

		List<Document> docs = DocGenHelper.getDocumentsList(policyNumber,PRE_RENEWAL);
		assertThat(docs.stream().map(Document::getTemplateId).toArray()).contains(DocGenEnum.Documents.HSRNMXX.getIdInXml());
	}


	/**
	 * CONTENT & TRIGGER (timeline): Pre-Renewal letter (insured bill) PA DP3
	 * @author Viktor Petrenko
	 * PAS-6731
	 *
	 *
	 * Set Home Insurance payments are SETUP to be billed directly to me
	 * Generate Pre Renewal offer on R-65 timeline and check HSRNMXX absence
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-6731"})
	public void pas6731_PreRenewalLetterGenerationNegativeScenario(@Optional("PA") String state){
		LocalDateTime renewalOfferEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(70);
		LocalDateTime preRenewalGenDate = renewalOfferEffectiveDate.minusDays(55);

		// Create manual entry
		String policyNumber = generatePreRenewalEvent(getConversionPolicyDefaultTD(),renewalOfferEffectiveDate, preRenewalGenDate);

		List<Document> docs = DocGenHelper.getDocumentsList(policyNumber,PRE_RENEWAL);
		assertThat(docs.stream().map(Document::getTemplateId).toArray()).doesNotContain(DocGenEnum.Documents.HSRNMXX.getIdInXml());
	}

	/**
	 * CONTENT & TRIGGER (timeline): Pre-Renewal letter (mortgagee) PA DP3
	 * @author Viktor Petrenko
	 * PAS-10666
	 *
	 *
	 * Set Home Insurance payments are SETUP to be billed directly to me
	 * Generate Pre Renewal offer on R-65 timeline and check HSRNMXX presence
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-6731"})
	public void pas10666_PreRenewalLetterGeneration(@Optional("PA") String state){
		LocalDateTime renewalOfferEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(70);

		String policyNumber = generatePreRenewalEvent(adjustWithMortgageeData(getConversionPolicyDefaultTD()),renewalOfferEffectiveDate, getTimePoints().getPreRenewalLetterGenerationDate(renewalOfferEffectiveDate));

		List<Document> docs = DocGenHelper.getDocumentsList(policyNumber,PRE_RENEWAL);
		assertThat(docs.stream().map(Document::getTemplateId).toArray()).contains(DocGenEnum.Documents.HSRNMXX.getIdInXml());
	}


	/**
	 * CONTENT & TRIGGER (timeline): Pre-Renewal letter (mortgagee) PA DP3
	 * @author Viktor Petrenko
	 * PAS-10666
	 * @throws NoSuchFieldException
	 *
	 * Set Home Insurance payments are SETUP to be billed directly to me
	 * Generate Pre Renewal offer on R-65 timeline and check HSRNMXX absence
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_SS_DP3, testCaseId = {"PAS-10666"})
	public void pas10666_PreRenewalLetterGenerationNegativeScenario(@Optional("PA") String state){
		LocalDateTime renewalOfferEffectiveDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(70);
		LocalDateTime preRenewalGenDate = renewalOfferEffectiveDate.minusDays(55);

		// Create manual entry
		String policyNumber = generatePreRenewalEvent(adjustWithMortgageeData(getConversionPolicyDefaultTD()),renewalOfferEffectiveDate, preRenewalGenDate);

		List<Document> docs = DocGenHelper.getDocumentsList(policyNumber,PRE_RENEWAL);
		assertThat(docs.stream().map(Document::getTemplateId).toArray()).doesNotContain(DocGenEnum.Documents.HSRNMXX.getIdInXml());
	}
}
