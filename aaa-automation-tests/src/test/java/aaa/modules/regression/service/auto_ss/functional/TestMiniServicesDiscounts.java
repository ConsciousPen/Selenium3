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

public class TestMiniServicesDiscounts extends AutoSSBaseTest {
	private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

	/**
	 * @author Oleg Stasyuk
	 * @name All VA discounts retrieved from Service
	 * @scenario
	 * Policy Discounts:
	 * Advanced Shopping Discount
	 * Affinity Discount
	 * Loyalty Discount
	 * Membership Discount
	 * Multi-Policy Discount (Life, Home)
	 * Payment Plan Discount
	 * eValue Discount
	 *
	 * Driver Discounts:
	 * Jenny Smith	Good Student Discount
	 * Jenny Smith	SMART Driver Discount
	 * Distant Student	Distant Student Discount
	 * Distant Student	Good Student Discount
	 *
	 * Vehicle Discounts:
	 * 2018, KIA, model	Anti-Theft Recovery Device
	 * Hybrid Discount
	 * Multi-Vehicle Discount
	 * New Car Discount
	 * Passive Restraint Discount
	 * 2018, FORD, Model	Multi-Vehicle Discount
	 * New Car Discount
	 * Telematics Participation Discount
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-9495"})
	public void pas9495_miniServicesDiscounts(@Optional("VA") String state) {
		createQuoteWithDiscountData(state);

		String policyNumber = testEValueDiscount.simplifiedQuoteIssue("ACH");
		printToLog("policyNumber = " + policyNumber);

		discountsCheckPerTransaction(policyNumber, "policy");

		AAAEndorseResponse endorsementResponse = HelperCommon.createEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime().plusDays(15).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		assertThat(endorsementResponse.policyNumber).isEqualTo(policyNumber);

		discountsCheckPerTransaction(policyNumber, "endorsement");
	}

	private void discountsCheckPerTransaction(String policyNumber, String transaction) {
		DiscountSummary policyDiscountsResponse = HelperCommon.viewDiscounts(policyNumber, transaction, 200);

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
		vehicleLevelDiscountsCheck(policyDiscountsResponse, "HD", "Hybrid Discount");
		vehicleLevelDiscountsCheck(policyDiscountsResponse, "NCD", "New Car Discount");
		vehicleLevelDiscountsCheck(policyDiscountsResponse, "PRD", "Passive Restraint Discount");
		vehicleLevelDiscountsCheck(policyDiscountsResponse, "TDD", "Telematics Participation Discount");

		//driver level discount check start
		driverLevelDiscountsCheck(policyDiscountsResponse, "GSD", "Good Student Discount");
		driverLevelDiscountsCheck(policyDiscountsResponse, "SDD", "SMART Driver Discount");
		driverLevelDiscountsCheck(policyDiscountsResponse, "DSD", "Distant Student Discount");
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

	private void createQuoteWithDiscountData(String state) {
		TestData td = getTestSpecificTD("TestData").adjust(TestData.makeKeyPath("GeneralTab",
				AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
				AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), "$<today+9d:MM/dd/yyyy>");

		if(!"VA".equals(state)){
			td.mask( "AssignmentTab")
			.mask(TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(),AutoSSMetaData.PremiumAndCoveragesTab.APPLY_EVALUE_DISCOUNT.getLabel()));
		}

		mainApp().open();
		createCustomerIndividual();

		policy.initiate();
		policy.getDefaultView().fillUpTo(td, DocumentsAndBindTab.class, false);
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.saveAndExit();
	}

}
