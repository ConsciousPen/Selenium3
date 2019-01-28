/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.policy.pup.defaulttabs;

import org.openqa.selenium.By;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * Implementation of a specific tab in a workspace.
 * Tab classes from the default workspace are named <TAB LABEL>Tab, whereas all other tab classes - <TAB LABEL>ActionTab (to prevent duplication).
 * Modify this class if tab filling procedure has to be customized, extra asset list to be added, custom testdata key to be defined, etc.
 * @category Generated
 */
public class PremiumAndCoveragesQuoteTab extends PropertyQuoteTab {
	public static Button btnCalculatePremium = new Button(By.id("policyDataGatherForm:actionButton_AAAPUPRateAction"), Waiters.AJAX);
	public static Button btnContinue = new Button(By.xpath("//input[@id='policyDataGatherForm:nextButton_footer' or @id='policyDataGatherForm:nextInquiryButton_footer']"), Waiters.AJAX);
	public static Table tableTotalPremium = new Table(By.id("policyDataGatherForm:pupTableTotalPremium"));
	public static Table tablePUPCoveragePremium = new Table(By.id("policyDataGatherForm:pupCoverageDetail"));

	public PremiumAndCoveragesQuoteTab() {
		super(PersonalUmbrellaMetaData.PremiumAndCoveragesQuoteTab.class);
	}

	public static Dollar getPolicyTermPremium() {
		return new Dollar(tableTotalPremium.getRow(1).getCell(2).getValue());
	}

	public static Dollar getPUPCoveragePremium() {
		return new Dollar(tablePUPCoveragePremium.getRow(1).getCell(3).getValue());
	}

	@Override
	public Tab fillTab(TestData td) {
		super.fillTab(td);
		calculatePremium();
		return this;
	}

	@Override
	public void calculatePremium() {
		if (!btnCalculatePremium.isPresent()) {
			NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
			NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
		}
		hideHeader();
		btnCalculatePremium.click();
		showHeader();
	}

	@Override
	public Tab submitTab() {
		btnContinue.click();
		return this;
	}

	public static Dollar getPolicyActualPremium() {
		return new Dollar(tableTotalPremium.getRow(1).getCell(5).getValue());
	}
}
