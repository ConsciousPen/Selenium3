package aaa.modules.deloitte.docgen.auto_ca.choice;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.verification.CustomAssert;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum.AutoCaTab;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.Groups;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.modules.policy.auto_ca.actiontabs.PolicyDocGenActionTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.AutoCaChoiceBaseTest;

public class SC1 extends AutoCaChoiceBaseTest {
	PolicyDocGenActionTab docgenActionTab = policy.quoteDocGen().getView().getTab(PolicyDocGenActionTab.class);
	
	/** 
	 * 1. Create CA Choice Quote
	 *    To get AA59XX (Existing Damage Endorsement) document: add Damage to Vehicle (not GOOD)
	 * 2. Check Documents on GODD: displayed, enable/disable
	 * 3. Call DataGather and update the quote
	 * 	  To get AA74CAA (Rating Information Disclosure NB) document: Always generated at NB - US 19824
	 *    To get AADDCA (Discounts - Private Passenger) document (not GODD): Always generated at NB - US 21213
	 *    To get AA43CA +AA43CAB (Named Driver(s) Exclusion) document: Add Excluded Driver - US 21222
	 *    To get WUAECA (Amendatory Endorsement) document: Always generated at NB (not GODD) - US 19851
	 *    To get AA10XX (Insurance Identification Card) document: at least one private passenger vehicle and/or Motorhome with liability coverage is included - US 21197
	 *    To get AA09CA (Special Equipment Endorsement) document: Set Special Equipment Coverage - US 21196
	 *    To get AA47CA (Towing and Labor Coverage Endorsement) document: set Towing and Labor Coverage (not GODD) - US 21228
	 *    To get AA49CA (Rental Car Benefit): set Rental Reimbursement Coverage (not GOOD) - US 21231
	 *    To get AA02CA (Declaration page) document: Always generated at NB - US 21194
	 *    To get AA59XX (Existing Damage Endorsement) document: Damage to Vehicle was added (not GOOD) - US 21219
	 * 4. Check Documents on GODD: documents are enabled
	 * 
	*/
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.CRITICAL })
	public void TC01(@Optional("") String state){
		CustomAssert.enableSoftMode();
		mainApp().open();

		// 1
		createCustomerIndividual();
		createQuote(getPolicyTD().adjust(getTestSpecificTD("TestData_QuoteCreation")));
		
		// 2
		policy.quoteDocGen().start();
		docgenActionTab.verify.documentsEnabled(
				Documents.AA11CA,
				Documents.AHAPXX_CA_CHOICE,
				Documents.AA53CA,
				Documents.AHFMXX,
				Documents.AAIQCA
				);
		docgenActionTab.verify.documentsEnabled(false, 
				Documents.AA41CA,
				Documents.AA52CA,
				Documents.CAU01,
				Documents.CAU04,
				Documents.CAU08,
				Documents.CAU09
				);
		docgenActionTab.buttonCancel.click();
		
		// 3
		policy.dataGather().start();
		NavigationPage.toViewTab(AutoCaTab.DRIVER.get());
		policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData_QuoteUpdate"), PremiumAndCoveragesTab.class, true);
		Tab.buttonSaveAndExit.click();
		
		// 4
		policy.quoteDocGen().start();
		docgenActionTab.verify.documentsEnabled(
				Documents.AA11CA,
				Documents.AA43CA,
				Documents.AHAPXX_CA_CHOICE,
				Documents.AA53CA,
				Documents.AHFMXX,
				Documents.AAIQCA
				);
		docgenActionTab.verify.documentsEnabled(false, 
				Documents.AA41CA,
				Documents.AA52CA,
				Documents.CAU01,
				Documents.CAU04,
				Documents.CAU08,
				Documents.CAU09
				);
		docgenActionTab.verify.documentsPresent(false, 
				Documents.AA09CA,
				Documents.AA47CA,
				Documents.AA49CA,
				Documents.AA59CA,
				Documents.AADDCA);
		docgenActionTab.buttonCancel.click();
		
		// 5
		policy.purchase(getPolicyTD());
		
		
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
}
