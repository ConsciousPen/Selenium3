/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.components.Efolder;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.DbAwaitHelper;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.config.PropertyProvider;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.Dollar;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.waiters.Waiters;

public class TestEValueDiscount extends AutoSSBaseTest {

	private static final String APP_HOST = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
	private static final String E_VALUE_DISCOUNT = "eValue Discount"; //PAS-440 - rumors have it, that discount might be renamed

	private static String messageInfo2 = "This customer is eligible for the eValue discount, but the following steps must be completed in order to bind.";
	private static String messageBullet4 = "Maintain continuous insurance coverage and bodily injury limits of $50,000/$100,000 or higher"; //$50,000/$100,000 is configurable
	private static String messageBullet10 = "Agree to and sign the eValue acknowledgement";
	private static String messageBullet2 = "Self-Service online using the MyPolicy site";

	private GeneralTab generalTab = new GeneralTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab(); //TODO test with policy.dataGather().getView().getTab(DocumentsAndBindTab.class); instead of new Tab();
	private ErrorTab errorTab = new ErrorTab();

	private static final String EVALUE_CONFIGURATION_PER_STATE_CHECK =
			"select dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id from LOOKUPVALUE\n" +
					" where lookuplist_id = \n" +
					" (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n" +
					" and CODE = 'eMember'\n" +
					" and RISKSTATECD = '%s'";

	private static final String PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_CHECK =
			"select dtype, code, displayValue, productCd, riskStateCd, territoryCd, channelCd, underwriterCd, lookuplist_id from LOOKUPVALUE\n" +
					" where lookuplist_id = \n" +
					" (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup')\n" +
					" and CODE = 'PaperlessPreferences'\n" +
					" and RISKSTATECD = '%s'";

	private static final String EVALUE_STATUS_CHECK = "select evaluestatus from(\n" +
			"select ps.id, em.EVALUESTATUS  from  policysummary ps\n" +
			"join AAAEMemberDetailsEntity em on em.id = ps.EMEMBERDETAIL_ID\n" +
			"where ps.policynumber = '%s'\n" +
			"order by ps.id desc)\n" +
			"where rownum=1";

	private static final String EVALUE_TERRITORY_FOR_VA_CONFIG_CHECK = "select TerritoryCD from(\n" +
			"SELECT code, displayvalue, effective, productCd, riskstatecd, territoryCd, channelCd, underwritercd \n" +
			"FROM LOOKUPVALUE \n" +
			"WHERE LOOKUPLIST_ID IN (\n" +
			"    SELECT ID \n" +
			"    FROM PASADM.LOOKUPLIST \n" +
			"    WHERE LOOKUPNAME LIKE '%Rollout%') \n" +
			"    AND CODE='eMember' \n" +
			"    and RiskStateCd = 'VA')";

	private static final String EVALUE_CHANNEL_FOR_VA_CONFIG_CHECK = "select ChannelCd from(\n" +
			"SELECT code, displayvalue, effective, productCd, riskstatecd, territoryCd, channelCd, underwritercd \n" +
			"FROM LOOKUPVALUE \n" +
			"WHERE LOOKUPLIST_ID IN (\n" +
			"    SELECT ID \n" +
			"    FROM PASADM.LOOKUPLIST \n" +
			"    WHERE LOOKUPNAME LIKE '%Rollout%') \n" +
			"    AND CODE='eMember' \n" +
			"    and RiskStateCd = 'VA')";

	private static final String EVALUE_CURRENT_BI_CONFIG_CHECK = "select effective from (\n" +
			"SELECT code, displayValue, productCd, riskStateCd, effective, expiration \n" +
			"FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n" +
			"    (SELECT ID \n" +
			"    FROM LOOKUPLIST \n" +
			"    WHERE LOOKUPNAME='AAAeMemberQualifications')\n" +
			"and riskstatecd = 'VA'\n" +
			"and productCD = 'AAA_SS'\n" +
			"and code = 'currentBILimits'\n" +
			"and displayvalue = '50000/100000')";

	private static final String EVALUE_PRIOR_BI_CONFIG_CHECK = "select Effective from (\n" +
			"SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n" +
			"FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n" +
			"    (SELECT ID \n" +
			"    FROM LOOKUPLIST \n" +
			"    WHERE LOOKUPNAME='AAAeMemberQualifications')\n" +
			"and riskstatecd = 'VA'\n" +
			"and productCD = 'AAA_SS'\n" +
			"and code = 'priorBILimits'\n" +
			"and displayvalue = '25000/50000')";

	private static final String EVALUE_MEMBERSHIP_CONFIG_CHECK = "select Effective from (\n" +
			"SELECT dtype, code, displayValue, productCd, riskStateCd, effective, expiration \n" +
			"FROM LOOKUPVALUE WHERE LOOKUPLIST_ID IN \n" +
			"    (SELECT ID \n" +
			"    FROM LOOKUPLIST \n" +
			"    WHERE LOOKUPNAME='AAAeMemberQualifications')\n" +
			"and riskstatecd = 'VA'\n" +
			"and productCD = 'AAA_SS'\n" +
			"and code = 'membershipEligibility'\n" +
			"and displayvalue = 'FALSE')";

	private static final String PAPERLESS_PRFERENCE_STUB_POINT = "select VALUE from " +
			"PROPERTYCONFIGURERENTITY" +
			" WHERE propertyname='policyPreferenceApiService.policyPreferenceApiUri' " +
			" and  VALUE= 'http://%s:9098/aaa-external-stub-services-app/ws/policy/preferences'";

	@Test
	@TestInfo(isAuxiliary = true)
	public static void paperlessPreferencesConfigCheck() {
		CustomAssert
				.assertTrue("paperless preference stub endpoint. Please run paperlessPreferencesStubEndpointUpdate", DBService.get().getValue(String.format(PAPERLESS_PRFERENCE_STUB_POINT, APP_HOST)).get()
						.contains(APP_HOST));
	}

	@Test
	@TestInfo(isAuxiliary = true)
	public static void eValueConfigCheck() {
		CustomAssert.enableSoftMode();
		List<String> configForStates = Arrays.asList("VA", "MD", "DC");
		for (String configForState : configForStates) {
			CustomAssert.assertTrue("eValue is not configured for " + configForState + ". Insert configuration (run eValueConfigInsert) and restart the env", DBService.get()
					.getValue(String.format(EVALUE_CONFIGURATION_PER_STATE_CHECK, configForState)).isPresent());
			CustomAssert.assertTrue("Paperless Preferences is not configured for " + configForState + ". Insert configuration (run eValueConfigInsert) and restart the env", DBService.get()
					.getValue(String.format(PAPERLESS_PREFRENCES_CONFIGURATION_PER_STATE_CHECK, configForState)).isPresent());
		}
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Test
	@TestInfo(isAuxiliary = true)
	public static void eValuePriorBiCurrentBiConfigCheck() {
		CustomAssert.enableSoftMode();
		CustomAssert.assertTrue("eValue configuration for Prior BI limits is missing. Please run eValuePriorBiCurrentBiConfigUpdateInsert", DBService.get().getValue(EVALUE_PRIOR_BI_CONFIG_CHECK)
				.isPresent());
		CustomAssert.assertTrue("eValue configuration for Current BI limits is missing. Please run eValuePriorBiCurrentBiConfigUpdateInsert", DBService.get().getValue(EVALUE_CURRENT_BI_CONFIG_CHECK)
				.isPresent());
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	@Test
	@TestInfo(isAuxiliary = true)
	public static void eValueMembershipConfigCheck() {
		CustomAssert.assertTrue("eValue configuration for membership not require. Please run eValueMembershipConfigInsert", DBService.get().getValue(EVALUE_MEMBERSHIP_CONFIG_CHECK).isPresent());
	}

	@Test
	@TestInfo(isAuxiliary = true)
	public static void eValueTerritoryChannelForVAConfigCheck() {
		CustomAssert
				.assertEquals("Territory for VA is not configured, please run eValueTerritoryChannelForVAConfigUpdate", DBService.get().getValue(EVALUE_TERRITORY_FOR_VA_CONFIG_CHECK).get(), "212");
		CustomAssert.assertEquals("Channel for VA is not configured, please run eValueTerritoryChannelForVAConfigUpdate", DBService.get().getValue(EVALUE_CHANNEL_FOR_VA_CONFIG_CHECK)
				.get(), "AZ Club Agent");
	}

	//TODO Replace below TCs with DataProvider when the Optional parameter State will be removed

	/**
	 * PAS-436
	 *
	 * @author Viktoriia Lutsenko
	 * @name Test presence/status of eValue discount on P&C and consolidated pages(Membership = Active, Evalue = Yes)
	 * @scenario 1. Create customer
	 * 2. Create active policy with next conditions:
	 * TS1: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'Yes'
	 * TS2: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
	 * TS3: Current AAA Member = 'No', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
	 * TS4: Current AAA Member = 'Membership Pending', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'Yes'
	 * TS5: Current AAA Member = 'Membership Pending', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
	 * TS6: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed > 4, BI Limit = $300,000/$500,000, Apply eValue Discount  = 'No'
	 * TS7: Current AAA Member = 'Yes', Current Carrier = 'AAA Mid-Atlantic - 500016292', Days Lapsed < 4, BI Limit = $15,000/$30,000, Apply eValue Discount  = 'No'
	 * 3. Verify that on P&C page 'eValue Discount' is present in Discounts & Surcharges table (for TS1 and TS4) and 'eValue Discount' is absent in Discounts & Surcharges table (for TS2, TS3, TS5, TS6 and TS7) .
	 * 4. Bind policy.
	 * 5. Verify that 'eEvalue Status' = 'Pending' (for TS1 and TS4) and 'eEvalue Status' is empty (for TS2, TS3, TS5, TS6 and TS7) on Consolidated page.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-436")
	public void pas436_eValueDiscountVariations(@Optional("VA") String state) {
		testEvalueDiscount("AAAProductOwned_Active", "CurrentCarrierInformation", true, true, "Pending");
		testEvalueDiscount("AAAProductOwned_Active", "CurrentCarrierInformation", false, false, "");
		testEvalueDiscount("AAAProductOwned_No", "CurrentCarrierInformation", false, false, "");
		testEvalueDiscount("AAAProductOwned_Pending", "CurrentCarrierInformation", true, true, "Pending");
		testEvalueDiscount("AAAProductOwned_Pending", "CurrentCarrierInformation", false, false, "");
		testEvalueDiscount("AAAProductOwned_Active", "CurrentCarrierInformation_DayLapsedMore4", false, false, "");
		testEvalueDiscount("AAAProductOwned_Active", "CurrentCarrierInformation_BILimitLess", false, false, "");
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount
	 * @scenario 1. Create new eValue eligible quote
	 * 2. Check Default properties of eValue Discount field
	 * 3. Get premium before eValue discount is applied and after discount is applied
	 * 4. Compare premiums
	 * <p>
	 * Intermediate checks in Rating Details, Policy Summary Page
	 * @details
	 */
	//epic PAS-1438 eValue - New Business
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-305")
	public void pas305_eValueDiscountApplied(@Optional("VA") String state) {

		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();

		//PAS-439 start
		generalTab.getInquiryAssetList().assetFieldsAbsence("Apply eValue Discount");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		//Check field properties and default value of eValue Discount
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("No");
		//PAS-439 end
		//PAS-305 start
		CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
		//PAS-305 end

		//Get premiums before discount is applied
		Dollar policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
		Dollar vehicleCoveragePremiumWithoutEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle1(1);
		Dollar totalPremiumWithoutEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithoutEvalueDiscount.add(vehicleCoveragePremiumWithoutEvalueDiscount);

		//PAS-305 start
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).getCell(4).verify.value("None");
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		//PAS-305 end

		//PAS-2053
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, E_VALUE_DISCOUNT).verify.present(false);
		//PAS-2053

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		//Set discount to Yes
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		//PAS-304 start
		premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium().verify.equals(new Dollar(0));
		//PAS-304 end

		PremiumAndCoveragesTab.calculatePremium();

		//PAS-305 start
		CustomAssert.assertTrue(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
		//PAS-305 end

		//Get premiums after discount is applied
		Dollar policyLevelLiabilityCoveragesPremiumWithEvalueDiscount = premiumAndCoveragesTab.getPolicyLevelLiabilityCoveragesPremium();
		Dollar vehicleCoveragePremiumWithEvalueDiscount = premiumAndCoveragesTab.getVehicleCoveragePremiumByVehicle1(1);
		Dollar totalPremiumWithEvalueDiscount = policyLevelLiabilityCoveragesPremiumWithEvalueDiscount.add(vehicleCoveragePremiumWithEvalueDiscount);

		log.info("totalPremiumWithoutEvalueDiscount: {}", totalPremiumWithoutEvalueDiscount);
		log.info("totalPremiumWithEvalueDiscount: {}", totalPremiumWithEvalueDiscount);

		//Compare premiums before discount and after
		CustomAssert.assertTrue(totalPremiumWithoutEvalueDiscount.moreThan(totalPremiumWithEvalueDiscount));
		//PAS-2053 eValue Status on Policy Summary Page - Don't Show it When not enabled

		//PAS-305 start
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).getCell(4).verify.value("Yes");
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		//PAS-305 end

		//PAS-2053
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, E_VALUE_DISCOUNT).verify.present();
		//PAS-2053

		//PAS-276 start
		policy.dataGather().start();
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		InquiryAssetList inquiryAssetDocumentTabGeneralInfoSection = new InquiryAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.class);
		inquiryAssetDocumentTabGeneralInfoSection.assetFieldUnionCheck(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL.getLabel(), true, true, true);
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION.getLabel(), AssetList.class)
				.getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL).setValue("");
		DocumentsAndBindTab.btnPurchase.click();
		errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS6591343").getCell("Message").verify.contains("'Email' is required");
		//PAS-276 end

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 1. Create new eValue eligible policy with membership pending and paperless preferences yes evalue no
	 * 2. Check policy consolidated view.
	 * 3. See if eMember status = No
	 * 4. DB check for evalue status in the Database NOTENROLLED
	 * @details

	 * @scenario 3. Create new eValue eligible policy with membership yes and paperless preferences yes evalue yes
	 * 2. Check policy consolidated view.
	 * 3. See if eMember status = active
	 * 4. DB check for evalue status in the Database Pending
	 * @scenario 4. Create new eValue eligible policy with membership yes and paperless preferences yes evalue yes
	 * 2. while doing endorsment set evalue no
	 * 3. DB check for evalue status in the Database Inactive
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-300")
	public void pas300_eValueStatusConsViewPaperPrefYes(@Optional("VA") String state) {
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();

		//PAS-302 start VC4
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("");
		CustomAssert.assertEquals(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber)).get(), "NOTENROLLED");
		//PAS-302 end

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.buttonPendedEndorsement.click();
		simplifiedPendedEndorsementIssue();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("Active");
		//PAS-302 start VC2
		CustomAssert.assertEquals(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber)).get(), "ACTIVE");
		//PAS-302 end

		//PAS-302 start VC3
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		Page.dialogConfirmation.confirm();
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.buttonPendedEndorsement.click();
		simplifiedPendedEndorsementIssue();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("");
		CustomAssert.assertEquals(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber)).get(), "INACTIVE");
		//PAS-302 end
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 2. Create new eValue eligible policy with membership pending and paperless preferences yes evalue yes
	 * 2. Check policy consolidated view.
	 * 3. See if eMember status = Pending
	 * 4. DB check for evalue status in the Database Pending
	 * @details
	 * */

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-300")
	public void pas300_eValueStatusConsViewPaperPrefPendingVa(@Optional("VA") String state) {
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("");
		simplifiedQuoteIssue();

		//BUG PAS-4279 Evalue status showing wrong
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("Pending");

		//PAS-302 start VC1
		CustomAssert.assertEquals(DBService.get().getValue(String.format(EVALUE_STATUS_CHECK, policyNumber)).get(), "PENDING");
		//PAS-302 end
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 1. Create new eValue eligible policy with membership pending and paperless preferences Pending
	 * 2. Check policy consolidated view.
	 * 3. See if eMember status = Pending
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-300")
	public void pas300_eValueStatusConsViewPaperPrefPendingDc(@Optional("DC") String state) {
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("");
		simplifiedQuoteIssue("ACH");
		PolicySummaryPage.tableGeneralInformation.getRow(1).getCell("eValue Status").verify.value("Pending");
	}

	/**
	 * new feature
	 *
	 * @author Megha Gubbala
	 * @name Test eValue Status
	 * @scenario 1. Create new eligible policy for the state  where eValue has not yet been rolled out
	 * 2. Check policy consolidated view.
	 * 3.should not  see eValue Status in the General Info section.
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3708")
	public void pas3708_eValueStatusConsViewNotConfigured(@Optional("PA") String state) {
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present(false);
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		CustomAssert.assertFalse(PolicySummaryPage.tableGeneralInformation.getHeader().getValue().contains("eValue Status"));
		simplifiedQuoteIssue();
		CustomAssert.assertFalse(PolicySummaryPage.tableGeneralInformation.getHeader().getValue().contains("eValue Status"));
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test "Has the insured ever been enrolled in eValue?" visibility in different flows
	 * @scenario 1. Create new eValue eligible quote with eValue Discount = true
	 * 2. Check "Has the insured ever been enrolled in eValue?" is available in Data Gather mode for NB
	 * 2. Check "Has the insured ever been enrolled in eValue?" is Not available in Inquiry mode for NB
	 * 3. Check "Has the insured ever been enrolled in eValue?" is Not available in Endorsement
	 * 4. Check "Has the insured ever been enrolled in eValue?" is Not available in Renewal
	 * 5. Cancel Policy, Rewrite
	 * 6. Check "Has the insured ever been enrolled in eValue?" is available for the quote generate as a result of Rewrite and is Not defaulted to Yes (might change in future)
	 * PAS-306 Commission Type checked as part of the script
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-325")
	public void pas325_eValueCommissionRelatedFields(@Optional("VA") String state) {
		List<String> expectedNonEvalueCommissionTypeOptions = Arrays.asList("New Business", "Renewal");
		List<String> expectedEvalueCommissionTypeOptions = Arrays.asList("eValue New Business", "eValue Renewal");

		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());

		//PAS-306, PAS-320, PAS-323 start
		commissionTypeCheck(expectedNonEvalueCommissionTypeOptions, "No", "New Business");
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business");
		//PAS-306, PAS-320, PAS-323 end

		//PAS-2054 start
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		generalTab.getInquiryAssetList().assetFieldsAbsence("Has the insured ever been enrolled in eValue?");
		//PAS-2054 end
		//PAS-325 end
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();
		String originalPolicyNumber = PolicySummaryPage.getPolicyNumber();

		//Inquiry doesn't show the field
		policy.policyInquiry().start();
		generalTab.getInquiryAssetList().assetFieldUnionCheck(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE.getLabel(), false, false, false);
		generalTab.cancel();

		//Endorsement doesn't show the field
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		generalTab.getInquiryAssetList().assetFieldUnionCheck(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE.getLabel(), false, false, false);
		//PAS-306, PAS-320, PAS-323 start
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue New Business");//because the issue happened with eValue Discount = True
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business");
		//PAS-306, PAS-320, PAS-323 end
		generalTab.cancel();
		Page.dialogConfirmation.buttonDeleteEndorsement.click();

		//Renewal doesn't show the field
		policy.renew().start();
		generalTab.getInquiryAssetList().assetFieldUnionCheck(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE.getLabel(), false, false, false);
		//PAS-306, PAS-320, PAS-323, PAS-318 - Renewal is not covered by this story
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue Renewal");
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue Renewal");
		//PAS-306, PAS-320, PAS-323, PAS-318 end
		generalTab.saveAndExit();

		//Cancel and Rewrite
		policy.cancel().perform(getPolicyTD("Cancellation", "TestData"));
		policy.rewrite().perform(getPolicyTD("Rewrite", "TestDataSameDate"));
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).verify
				.present();
		//There might be a new requirement to default the field to yes for the rewrite and Split in case if original policy had eValue Discount=true
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).verify
				.value("No");
		//PAS-306, PAS-320, PAS-323 start
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify
				.optionsContain(expectedNonEvalueCommissionTypeOptions);
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value("Renewal");
		//PAS-306, PAS-320, PAS-323 end

		//Logic requested by business - not to carry over eValue from Original Policy and to have Commission Type dependent on HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE value which is set by Agent
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)
				.setValue("Yes");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value("eValue Renewal");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE)
				.setValue("No");
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value("Renewal");
		generalTab.cancel();

		//PAS-302 start
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, originalPolicyNumber);
		policy.reinstate().perform(getPolicyTD("Reinstatement", "TestData_Plus14Days"));
		TestData adjustedEndorsementActionData = getPolicyTD("Endorsement", "TestData").getTestData("EndorsementActionTab").adjust("Endorsement Date", "/today+15d");
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData").adjust("EndorsementActionTab", adjustedEndorsementActionData));

		//PAS-306, PAS-320, PAS-323 start
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "No", "eValue New Business");//because the Issue Action happened with eValue Discount = True, and the agent is locked in eValue commissions forever
		commissionTypeCheck(expectedEvalueCommissionTypeOptions, "Yes", "eValue New Business");
		//PAS-306, PAS-320, PAS-323 end

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("Yes");
		//PAS-302 end
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount not shown for state where it is not configured
	 * @scenario 1. Create new eValue eligible quote but for the not eligible state (PA)
	 * 1.1. Check "Has the insured ever been enrolled in eValue?" is not shown for Non-Applicable state
	 * 2. Check eValue Discount field is not shown in P&C
	 * 3. Check eValue Discount field is not shown in Rating Details
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-305")
	public void pas305_eValueNotApplicableForState(@Optional("PA") String state) {
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();

		//PAS-325 start
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.HAS_THE_INSURED_EVER_BEEN_ENROLLED_IN_EVALUE).verify
				.present(false);
		//PAS-325 end

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present(false);
		//PAS-309 start
		PremiumAndCoveragesTab.tableGreyBox.verify.present(false);
		//PAS-309 end
		//PAS-305 start
		PremiumAndCoveragesTab.buttonViewRatingDetails.click();
		PremiumAndCoveragesTab.tableRatingDetailsQuoteInfo.getRow(3, E_VALUE_DISCOUNT).verify.present(false);
		PremiumAndCoveragesTab.buttonRatingDetailsOk.click();
		//PAS-305 end
		//PAS-2053 start
		premiumAndCoveragesTab.saveAndExit();
		PolicySummaryPage.tableAppliedDiscountsPolicy.getRowContains(2, E_VALUE_DISCOUNT).verify.present(false);
		//PAS-2053 end

		//PAS-276 start
		policy.dataGather().start();
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		InquiryAssetList inquiryAssetDocumentTabGeneralInfoSection = new InquiryAssetList(By.xpath(Page.DEFAULT_ASSETLIST_CONTAINER), AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.class);
		inquiryAssetDocumentTabGeneralInfoSection.assetFieldPresence(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL.getLabel(), true);
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION.getLabel(), AssetList.class)
				.getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.EMAIL).setValue("");

		DocumentsAndBindTab.btnPurchase.click();
		errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS6591343").verify.present(false);
		//PAS-276 end
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test AHEVAXX eValue Acknowledgement document is can be generated with Generate eSignature Documents
	 * @scenario 1. Create new eValue eligible quote for VA
	 * 2. set eValue Discount = true in P&C
	 * 3. set eValue Acknowledgement = true in Documents and Bind tab
	 * 4. Generate eSignature Documents
	 * 5. Check DB for the form AHEVAXX with tag eSignaturePackage
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-278")
	public void pas278_eValueeSignedPledgeDocumentAHEVAXX(@Optional("VA") String state) {

		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();

		//PAS-264 start
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).verify.present(false);
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).verify.present(false);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).verify.present();
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).verify.value("Yes");
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).verify.present();
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).verify.value("Not Signed");

		Collection<String> expectedValues = new ArrayList<>();
		expectedValues.add("Physically Signed");
		expectedValues.add("Electronically Signed");
		expectedValues.add("Voice Signed");
		expectedValues.add("Not Signed");
		CustomAssert.assertTrue(documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT)
				.getAllValues().containsAll(expectedValues));

		DocumentsAndBindTab.btnPurchase.click();
		errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS8120577").getCell("Message").verify
				.value("A signed eValue Acknowledgement must be received prior to issuing this transa...");
		errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS8120577").getCell("Code").controls.links.get(1).click();
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT).setValue("Electronically Signed");

		DocumentsAndBindTab.btnPurchase.click();
		//if clause used fot the case if there will be no overridable errors
		if (errorTab.getErrorsControl().getTable().isPresent()) {
			errorTab.getErrorsControl().getTable().getRowContains("Code", "AAA_SS8120577").verify.present(false);
			errorTab.cancel();
		} else {
			policy.dataGather().start();
			NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		}
		//PAS-264 end
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		String policyNum = documentsAndBindTab.getPolicyNumber();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).setValue("Yes");

		documentsAndBindTab.getDocumentsForPrintingAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.BTN_GENERATE_ESIGNATURE_DOCUMENTS)
				.click(Waiters.DEFAULT.then(Waiters.SLEEP(2000)));
		documentsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.RECIPIENT_EMAIL_ADDRESS)
				.setValue("test@email.com");
		documentsAndBindTab.getEnterRecipientEmailAddressDialogAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EnterRecipientEmailAddressDialog.BTN_OK).click();
		Page.dialogConfirmation.buttonOk.click();

		//Delay is required for the document to appear in DB and in eFolder
		//PAS-264 start
		String query = GET_DOCUMENT_BY_EVENT_NAME + " and data like '%%ESignatureChannel%%' ";
		String queryFull = String.format(query, policyNum, "AHEVAXX", "ADHOC_DOC_GENERATE");
		CustomAssert.assertTrue(DbAwaitHelper.waitForQueryResult(queryFull, 60));
		log.info("Delay start");
		Waiters.SLEEP(60000).go();
		log.info("Delay end");
		documentsAndBindTab.saveAndExit();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, policyNum);
		Efolder.isDocumentExist("Miscellaneous", "ACKNOWLEDGEMENT FORM");
		//PAS-264 end
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Configuration for eValue for CurrentBILimit and PriorBILimit
	 * @scenario 1. Create new eValue eligible quote for VA
	 * 2. set Prior BI Limit to the one that Disables eValue
	 * 3. Check eValueDiscount field is disable in P&C tab
	 * 4. set Prior BI Limit to the one that Enables eValue
	 * 5. Check eValueDiscount field is enabled in P&C tab
	 * 6. set eValue = Yes
	 * 7. Check Current BI limit shows the lowest available value and the same value is shown in GreyBox
	 * 8. Change Effective Date of the Quote to 8 days in the past
	 * 9. Check Different Prior BI Limit enable/disable eValue configuration
	 * 10. Check Different Current BI Limit is shown in BI Limit field in P&C tab and in GreyBox
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValuePriorBiCurrentBiConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-436")
	public void pas436_eValuePriorBiCurrentBiConfigurationDependency(@Optional("VA") String state) {
		String lowerBiLimit = "$50,000/$100,000";
		String upperBiLimit = "$100,000/$300,000";

		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		pas436_eValuePriorBiCurrentBiConfigurationDependencyCheck("$20,000/$40,000", "$25,000/$50,000", lowerBiLimit);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(8).format(DateTimeUtils.MM_DD_YYYY));
		pas436_eValuePriorBiCurrentBiConfigurationDependencyCheck("$25,000/$50,000", "$100,000/$300,000", upperBiLimit);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void pas436_eValuePriorBiCurrentBiConfigurationDependencyCheck(String disableEvaluePriorBiLimit, String enableEvaluePriorBiLimit, String biLimit) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue(disableEvaluePriorBiLimit);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled(false);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue(enableEvaluePriorBiLimit);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled(true);

		PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).verify.contains(biLimit);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		CustomAssert.assertTrue(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).getAllValues().get(0).contains(biLimit));
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Configuration for eValue based on Territory and Channel
	 * @scenario 1. Create new eValue eligible quote for VA
	 * 2. set Territory <> MidAtlantic
	 * 3. Check eValueDiscount field is not available in P&C tab
	 * 4. set Territory = MidAtlantic
	 * 5. Check eValueDiscount field is present in P&C tab
	 * 6. set Channel = Independent Agent
	 * 7. Check eValueDiscount field is not available in P&C tab
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueTerritoryChannelForVAConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-233")
	public void pas233_eValueTerritoryChannelDependency(@Optional("VA") String state) {
		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		eValueDependencyOnTerrirotyChannelCheck(getTestSpecificTD("PolicyInformationForTerritoriesEValueNotApplicable1"), false);

		eValueDependencyOnTerrirotyChannelCheck(getTestSpecificTD("PolicyInformationForTerritoriesEValueApplicable"), true);

		eValueDependencyOnTerrirotyChannelCheck(getTestSpecificTD("PolicyInformationForTerritoriesEValueNotApplicable2"), false);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void eValueDependencyOnTerrirotyChannelCheck(TestData territoryChannelData, boolean eValueDiscountPresence) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.CHANNEL_TYPE).fill(territoryChannelData);
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY).fill(territoryChannelData);
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.AGENCY_LOCATION).fill(territoryChannelData);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present(eValueDiscountPresence);
	}

	/**
	 * @author Megha Gubbala
	 * @name Test Configuration for eValue for Membership eligibility
	 * @scenario 1. Create new eValue eligible quote for VA
	 * 2. set Membership = no
	 * 3. Check eValueDiscount field is disabled in P&C tab
	 * 4. change the effective date when configuration added for membership eligibility
	 * 5. Check eValueDiscount field is enabled in P&C tab
	 * 6. set eValue = Yes in P&C tab
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueMembershipConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3007")
	public void pas3007_eValueMembershipConfiguration(@Optional("VA") String state) {

		eValueQuoteCreation();

		CustomAssert.enableSoftMode();

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("No");
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MEMBERSHIP_NUMBER).setValue("");

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled(false);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE)
				.setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(8).format(DateTimeUtils.MM_DD_YYYY));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount not shown for state where it is not configured
	 * @scenario 1. Create new eValue eligible quote
	 * 2. Check Grey Box messages when eValue Discount can be selected
	 * 3. Check Grey Box messages when eValue Discount can't be selected
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-309")
	public void pas309_eValueGreyBox(@Optional("VA") String state) {
		String messageInfo1 = "This customer is not eligible for the eValue discount due to the following reason(s):";
		String messageBullet1 = "Pay in full with any payment method or enroll in AutoPay with a checking/savings account or debit card";
		String messageBullet7 = "Has held CSAA Insurance for less than one term";
		String messageBullet8 = "Does not have an active AAA membership";
		String messageBullet9 = "Does not have prior insurance or prior insurance BI limit";

		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();

		//precondition Grey Box with eligible for Discount messages
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		eValueDiscountEligibleGreyBoxCheck();

		//eValue Acknowledgement related GreyBox message
		eValueAcknowledgementMessagesGreyBoxCheck();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).setValue("Not Signed");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		eValueDiscountEligibleGreyBoxCheck();

		//Prior BI Limit related greyBox message
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		String oldAgentEnteredBiLimitsValue = generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).getValue();
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue("contains=15,000");
		eValueDiscountNotEligibleGreyBoxCheck();

		PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).verify.value(messageInfo1);
		PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).verify.contains(messageBullet9);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue(oldAgentEnteredBiLimitsValue);
		eValueDiscountEligibleGreyBoxCheck();

		//membership related greyBox
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Membership Pending");
		eValueDiscountEligibleGreyBoxCheck();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("No");
		eValueDiscountNotEligibleGreyBoxCheck();

		PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).verify.value(messageInfo1);
		PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).verify.contains(messageBullet8);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).setValue("Yes");
		eValueDiscountEligibleGreyBoxCheck();

		premiumAndCoveragesTab.getAssetList().getAsset("Payment Plan", ComboBox.class).setValue("contains=Month");
		PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).verify.contains(messageBullet1);
		premiumAndCoveragesTab.getAssetList().getAsset("Payment Plan", ComboBox.class).setValue("Annual");
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet1));

		//Check No Prior Carrier greyBox
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.OVERRIDE_CURRENT_CARRIER).setValue("No");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet7));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet9));
		eValueDiscountNotEligibleGreyBoxCheck();

		PremiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.saveAndExit();
		simplifiedQuoteIssue();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet7));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet9));
		eValueDiscountNotEligibleGreyBoxCheck();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		TestData defaultTestData = getPolicyTD("DataGather", "TestData");
		TestData currentCarrierSectionAdjusted = getTestSpecificTD("CurrentCarrierInformation");
		TestData generalTabAdjusted = DataProviderFactory.emptyData().adjust("CurrentCarrierInformation", currentCarrierSectionAdjusted);
		TestData currentCarrierData = defaultTestData.adjust("GeneralTab", generalTabAdjusted);
		generalTab.fillTab(currentCarrierData);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet7));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet9));
		eValueDiscountEligibleGreyBoxCheck();

		//eValue Aknowldgement related GreyBox message
		eValueAcknowledgementMessagesGreyBoxCheck();

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Alex Tinkovan
	 * @name Test that limits are greater than or equal to the defined Current BI Threshold
	 * @scenario 1. Create new eValue eligible quote for VA (Prior BI and Membership Conditions)
	 * 2. Set 'Apply eValue Discount' = Yes
	 * 3. Verify that 'Bodily Injury Limit' first row from drop down is $25,000/$50,000
	 * 4. Verify that 'Bodily Injury Limit' drop down contains 7 rows
	 * 5. Set 'Apply eValue Discount' = No
	 * 6. Verify that 'Bodily Injury Limit' first row from drop down is $25,000/$50,000 value
	 * 7. Verify that 'Bodily Injury Limit' drop down contains 8 rows
	 * 8. Set 'Apply eValue Discount' = Yes
	 * 9. Verify that 'Bodily Injury Limit' drop down does not contain $25,000/$50,000
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-265")
	public void pas265_MinimumStateLimitsForBIPreBind(@Optional("VA") String state) {

		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();

		String lowerLimit = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).getAllValues().get(0);
		CustomAssert.assertTrue("Bodily Injury Limit has incorrect lower limit" + lowerLimit, lowerLimit.contains("$50,000/$100,000"));
		CustomAssert.assertTrue(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).getAllValues().size() == 7);

		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("No");
		lowerLimit = premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).getAllValues().get(0);
		CustomAssert.assertTrue("Bodily Injury Limit has incorrect lower limit" + lowerLimit, lowerLimit.contains("$25,000/$50,000"));
		CustomAssert.assertTrue(premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).getAllValues().size() == 8);

		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).setValue(lowerLimit);
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");

		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.BODILY_INJURY_LIABILITY).verify.noOption(lowerLimit);
		PremiumAndCoveragesTab.calculatePremium();

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount Paper;less Preference related GreyBox message is shown correctly
	 * @scenario 1. Create new eValue eligible quote for DC state with Paperless Preferences = Pending
	 * 2. Check Grey Box messages when eValue Discount can be selected
	 * 3. Check Grey Box messages when eValue Discount can not be selected
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-309")
	public void pas309_eValueGreyBoxPaperlessPendingDC(@Optional("DC") String state) {

		pas309_eValueGreyBoxPaperlessCheck("Pending");
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test eValue Discount Paper;less Preference related GreyBox message is shown correctly
	 * @scenario 1. Create new eValue eligible quote for MD state with Paperless Preferences = No
	 * 2. Check Grey Box messages when eValue Discount can be selected
	 * 3. Check Grey Box messages when eValue Discount can not be selected
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "eValueConfigCheck")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-309")
	public void pas309_eValueGreyBoxPaperlessNoMD(@Optional("MD") String state) {

		pas309_eValueGreyBoxPaperlessCheck("No");
	}

	private void pas309_eValueGreyBoxPaperlessCheck(String paperlessPreferenceValue) {
		String messageBullet3 = "Enrollment in paperless notifications for policy and billing documents";

		eValueQuoteCreation();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		//paperlessPreferenceValue is state dependent in Stub
		documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value(paperlessPreferenceValue);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		eValueDiscountEligibleGreyBoxCheck();
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet3));

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getCurrentCarrierInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.CurrentCarrierInformation.AGENT_ENTERED_BI_LIMITS).setValue("contains=15,000");
		eValueDiscountNotEligibleGreyBoxCheck();
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet3));

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void testEvalueDiscount(String membershipStatus, String currentCarrier, boolean evalueIsSelected, boolean evalueIsPresent, String evalueStatus) {
		prefillEvalueTestData(membershipStatus, currentCarrier);
		fillPremiumAndCoveragesTab(evalueIsSelected);
		fillDriverActivityReportsTab();
		TestData tdPolicyCreation = fillDocumentAndBindTab(evalueIsPresent);
		new PurchaseTab().fillTab(tdPolicyCreation).submitTab();

		validateEvalueStatus(evalueStatus);
		validatePolicyStatus();
	}

	private void prefillEvalueTestData(String aaaProductOwned, String currentCarrierInformation) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();

		CustomAssert.enableSoftMode();
		String currentCarrierInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.CURRENT_CARRIER_INFORMATION.getLabel());
		String policyInformationKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel());
		String aaaProductOwnedKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.AAA_PRODUCT_OWNED.getLabel());

		TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData")
				.adjust(currentCarrierInformationKey, getTestSpecificTD(currentCarrierInformation))
				.adjust(policyInformationKey, getTestSpecificTD("PolicyInformation"))
				.adjust(aaaProductOwnedKey, getTestSpecificTD(aaaProductOwned));

		policy.getDefaultView().fillUpTo(tdPolicyCreation, PremiumAndCoveragesTab.class, true);
	}

	private void fillPremiumAndCoveragesTab(boolean eValueIsPresent) {
		if (eValueIsPresent) {
			new PremiumAndCoveragesTab().getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
			PremiumAndCoveragesTab.buttonCalculatePremium.click();
			PremiumAndCoveragesTab.discountsAndSurcharges.verify.contains(E_VALUE_DISCOUNT);
		} else {
			CustomAssert.assertFalse(PremiumAndCoveragesTab.discountsAndSurcharges.getValue().contains(E_VALUE_DISCOUNT));
		}
		PremiumAndCoveragesTab.buttonContinue.click();
	}

	private void fillDriverActivityReportsTab() {
		DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();
		AbstractContainer<?, ?> assetList = driverActivityReportsTab.getAssetList();
		assetList.getAsset(AutoSSMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE).setValue("Yes");
		assetList.getAsset(AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
		driverActivityReportsTab.submitTab();
	}

	private TestData fillDocumentAndBindTab(boolean evalueIsPresent) {
		DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
		String metaKey = documentsAndBindTab.getMetaKey();
		String evalueAcknowledgementKey = TestData.makeKeyPath(metaKey,
				AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_BIND.getLabel(),
				AutoSSMetaData.DocumentsAndBindTab.RequiredToBind.EVALUE_ACKNOWLEDGEMENT.getLabel());
		String proofOfPriorInsuranceKey = TestData.makeKeyPath(metaKey,
				AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(),
				AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_PRIOR_INSURANCE.getLabel());
		TestData tdPolicyCreation = getPolicyTD("DataGather", "TestData")
				.adjust(proofOfPriorInsuranceKey, "Yes");
		if (evalueIsPresent) {
			tdPolicyCreation = tdPolicyCreation.adjust(evalueAcknowledgementKey, "Physically Signed");
		}
		documentsAndBindTab.fillTab(tdPolicyCreation).submitTab();
		return tdPolicyCreation;
	}

	private void validateEvalueStatus(String expectedEvalueStatus) {
		PolicySummaryPage.tableGeneralInformation.getRows().get(0)
				.getCell("eValue Status").verify.valueByRegex("Invalid eValue status", expectedEvalueStatus);
	}

	private void validatePolicyStatus() {
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: Policy created with #{}", PolicySummaryPage.labelPolicyNumber.getValue());
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void eValueAcknowledgementMessagesGreyBoxCheck() {
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");
		PremiumAndCoveragesTab.calculatePremium();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getRequiredToBindAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.DocumentsForPrinting.EVALUE_ACKNOWLEDGEMENT).setValue("Physically Signed");
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).getValue().contains(messageInfo2));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet4));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet10));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet2));
	}

	private void eValueDiscountEligibleGreyBoxCheck() {
		//Check field properties and default value of eValue Discount
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue("Yes");

		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).getValue().contains(messageInfo2));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet4));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet10));
		CustomAssert.assertTrue(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet2));
	}

	private void eValueDiscountNotEligibleGreyBoxCheck() {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.present();
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.enabled(false);
		//BUG PAS-3103 if eValue was set to yes, changing Prior BI Limit to non-eligible for Discount does't reset discount to No
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).verify.value("No");

		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(1).getCell(1).getValue().contains(messageInfo2));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet4));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet10));
		CustomAssert.assertFalse(PremiumAndCoveragesTab.tableGreyBox.getRow(2).getCell(1).getValue().contains(messageBullet2));
	}

	private void commissionTypeCheck(List<String> expectedCommissionTypeOptions, String eValueValue, String defaultCommissionTypeValue) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT).setValue(eValueValue);
		if (Page.dialogConfirmation.isPresent()) {
			Page.dialogConfirmation.confirm();
		}
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.GENERAL.get());
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify
				.optionsContain(expectedCommissionTypeOptions);
		generalTab.getAssetList().getAsset(AutoSSMetaData.GeneralTab.POLICY_INFORMATION).getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.COMMISSION_TYPE).verify.value(defaultCommissionTypeValue);
	}

	void eValueQuoteCreation() {
		//Default VA test data didn't work, so had to use multiple adjustments
		TestData defaultTestData = getPolicyTD("DataGather", "TestData");
		TestData policyInformationSectionAdjusted = getTestSpecificTD("PolicyInformation").adjust("TollFree Number", "1");
		TestData currentCarrierSectionTestSpecific = getTestSpecificTD("CurrentCarrierInformation");
		TestData generalTabAdjusted = defaultTestData.getTestData("GeneralTab")
				.adjust("PolicyInformation", policyInformationSectionAdjusted)
				.adjust("CurrentCarrierInformation", currentCarrierSectionTestSpecific);

		TestData eValuePolicyData = defaultTestData
				.adjust("GeneralTab", generalTabAdjusted)
				.resolveLinks();

		mainApp().open();
		createCustomerIndividual();

		getPolicyType().get().createQuote(eValuePolicyData);
		String policyNum = PolicySummaryPage.getPolicyNumber();
		log.info("policyNum: {}", policyNum);
	}

	public void simplifiedQuoteIssue() {
		simplifiedQuoteIssue("");
	}

	/**
	 * Issues policy with 1 specific payment method
	 *
	 * @param paymentMethod - DC - Debit Card, CC - Credit Card, ACH - EFT
	 */
	public void simplifiedQuoteIssue(String paymentMethod) {
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.AGREEMENT).setValue("I agree");

		for (int i = 0; i < 3; i++) {
			if (DocumentsAndBindTab.btnPurchase.isPresent()) {
				DocumentsAndBindTab.btnPurchase.click();
				errorTab.overrideAllErrors();
			}
			if (Page.dialogConfirmation.isPresent()) {
				Page.dialogConfirmation.reject();
			}
		}
		policy.bind().submit();
		TestData purchaseTabData = getPolicyTD("DataGather", "TestData");
		if (!StringUtils.isEmpty(paymentMethod)) {
			purchaseTabData.adjust("PurchaseTab", getTestSpecificTD("PurchaseTab_" + paymentMethod));
		}
		new PurchaseTab().fillTab(purchaseTabData).submitTab();
	}

	public void simplifiedPendedEndorsementIssue() {
		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.getAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.GENERAL_INFORMATION).getAsset(AutoSSMetaData.DocumentsAndBindTab.GeneralInformation.AUTHORIZED_BY)
				.setValue("Megha");
		DocumentsAndBindTab.btnPurchase.click();
		Page.dialogConfirmation.confirm();

		ErrorTab errorTab = new ErrorTab();
		errorTab.overrideAllErrors();
		if (DocumentsAndBindTab.btnPurchase.isPresent()) {
			DocumentsAndBindTab.btnPurchase.click();
			Page.dialogConfirmation.confirm();
		}
	}
}
