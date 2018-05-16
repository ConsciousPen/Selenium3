/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.service.auto_ss.functional;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.format.DateTimeFormatter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.dtoDxp.AAAEndorseResponse;
import aaa.modules.regression.service.helper.dtoDxp.DiscountInfo;
import aaa.modules.regression.service.helper.dtoDxp.DiscountSummary;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestMiniServicesDiscounts extends AutoSSBaseTest {
	private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

	/**
	 * @author Oleg Stasyuk
	 * @name RFI
	 * @scenario
	 * 1.Initiate quote creation.
	 * Insured1
	 * Proof of Prior Insurance (including original inception date of policy and prior BI limits)	Prior BI overridden by agent
	 *
	 * Driver1
	 * Proof of Good Student
	 *
	 * Driver2
	 * DL - Foreign
	 * Smart Driver Course Completed?
	 *
	 * Driver3 - Not Available for Rating, insured with other carrier
	 * Proof of Current Insurance for all "Not Available for Rating" drivers
	 *
	 * Driver4 - Not Available for Rating
	 *
	 *
	 * Vehicle1 -
	 * Photos showing all 4 sides of salvaged vehicles	select salvaged
	 * Proof of purchase date (bill of sale) for new vehicle(s) - less than 30 days
	 *
	 * Vehicle2 -
	 * Proof of equivalent new car added protection coverage with prior carrier for new vehicle(s)	new car added protection; date is more than 30 days ago
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-9495"})
	public void pas9495_miniServicesDiscounts(@Optional("VA") String state) {
/*		createQuoteWithCustomData(state);

		//CustomAssert.enableSoftMode();
		String policyNumber = testEValueDiscount.simplifiedQuoteIssue("ACH");
		printToLog("policyNumber = " + policyNumber);*/
		String policyNumber = "VASS952918540";

		discountsCheckPerTransaction(policyNumber, "policy");

		AAAEndorseResponse endorsementResponse = HelperCommon.executeEndorseStart(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().plusDays(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		discountsCheckPerTransaction(policyNumber, "endorsement");

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void discountsCheckPerTransaction(String policyNumber, String transaction) {
		DiscountSummary policyDiscountsResponse = HelperCommon.executeDiscounts(policyNumber, transaction, 200);

		DiscountInfo afdDiscount = policyDiscountsResponse.policyDiscounts.stream().filter(disc -> "AFD".equals(disc.discountCd)).findFirst().orElse(null);
		assertThat("AFD".equals(afdDiscount.discountCd)).isTrue();
		assertThat("Affinity Discount".equals(afdDiscount.discountName)).isTrue();

		DiscountInfo asdDiscount= policyDiscountsResponse.policyDiscounts.stream().filter(disc -> "ASD".equals(disc.discountCd)).findFirst().get();
		assertThat("ASD".equals(asdDiscount.discountCd)).isTrue();
		assertThat("Advanced Shopping Discount".equals(asdDiscount.discountName)).isTrue();

		policyLevelDiscountsCheck(policyDiscountsResponse, "LD", "Loyalty Discount");
		policyLevelDiscountsCheck(policyDiscountsResponse, "MMD", "Membership Discount");
		policyLevelDiscountsCheck(policyDiscountsResponse, "MPD", "Multi-Policy Discount");
		policyLevelDiscountsCheck(policyDiscountsResponse, "MVD", "Multi-Vehicle Discount");
		policyLevelDiscountsCheck(policyDiscountsResponse, "PPD", "Payment Plan Discount");
		policyLevelDiscountsCheck(policyDiscountsResponse, "EMD", "eValue Discount");

		//vehicle level discount check start
		vehicleLevelDiscountsCheck(policyDiscountsResponse, "ATD", "Anti-Theft Recovery Device");

		//driver level discount check start
		//driverLevelDiscountsCheck(policyDiscountsResponse, "EMD", "eValue Discount");
	}

	private void policyLevelDiscountsCheck(DiscountSummary policyDiscountsResponse, String discountCode, String discountName) {
		DiscountInfo discount = policyDiscountsResponse.policyDiscounts.stream().filter(disc -> discountCode.equals(disc.discountCd)).findFirst().orElseThrow(() -> new IstfException("no such discount"));
		assertThat(discount.discountCd.equals(discountCode)).isTrue();
		assertThat(discount.discountName.equals(discountName)).isTrue();
	}

	private void vehicleLevelDiscountsCheck(DiscountSummary policyDiscountsResponse, String discountCode, String discountName) {
		DiscountInfo discount = policyDiscountsResponse.vehicleDiscounts.stream().filter(disc -> discountCode.equals(disc.discountCd)).findFirst().orElseThrow(() -> new IstfException("no such discount"));
		assertThat(discount.discountCd.equals(discountCode)).isTrue();
		assertThat(discount.discountName.equals(discountName)).isTrue();
	}

	private void driverLevelDiscountsCheck(DiscountSummary policyDiscountsResponse, String discountCode, String discountName) {
		DiscountInfo discount = policyDiscountsResponse.driverDiscounts.stream().filter(disc -> discountCode.equals(disc.discountCd)).findFirst().orElseThrow(() -> new IstfException("no such discount"));
		assertThat(discount.discountCd.equals(discountCode)).isTrue();
		assertThat(discount.discountName.equals(discountName)).isTrue();
	}

	private void createQuoteWithCustomData(String state) {
		TestData td = getTestSpecificTD("TestData").adjust(TestData.makeKeyPath("GeneralTab",
				AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), "$<today+9d:MM/dd/yyyy>");

		if(!"VA".equals(state)){
			td.mask( "AssignmentTab")
			.mask(TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(),AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT.getLabel()));
		}

		mainApp().open();
		createCustomerIndividual();
		//SearchPage.openCustomer("700032251");

		policy.initiate();
		policy.getDefaultView().fillUpTo(td, DocumentsAndBindTab.class, false);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		documentsAndBindTab.saveAndExit();
	}

}
