package aaa.modules.regression.sales.home_ss.ho3;

import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

/**
 * @author Olga Reva
 * @name Test Policy Safe discounts
 * @scenario
 * 1. Find customer or create new if customer does not exist.
 * 2. Initiate new HSS policy creation. 
 * 3. Fill all mandatory fields on all tabs, calculate premium. 
 * 4. Navigate to Property Info tab and set check boxes: 
 * 		Central fire alarm, Full residential sprinklers, 
 * 		Central theft alarm, Gated community.
 * 5. Navigate to Premiums&Coverages Quote tab and re-calculate premium. 
 * 6. Verify Theft Protection, Newer Home, Fire Protection discounts are applied and displaying in Discounts section and in Rating Details. 
 * 7. Verify that premium changed after discounts applied.
 * 8. Bind and purchase policy. 
 * 9. Verify policy status is Active and premium is the same as on P&C screen.
 * @details
 */

public class TestPolicyDiscountSafe extends HomeSSHO3BaseTest {
	
	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void testPolicySafeDiscount(@Optional("") String state) {
		mainApp().open();

		TestData td = getPolicyTD("DataGather", "TestData"); 
		TestData td_safeHome = getTestSpecificTD("TestData"); 
		Dollar premiumWithoutDiscount;
		Dollar premiumWithDiscount;
		
        createCustomerIndividual();
        
        policy.initiate();
        policy.getDefaultView().fillUpTo(td, BindTab.class);

        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumWithoutDiscount = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();   
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
        propertyInfoTab.fillTab(td_safeHome);
        propertyInfoTab.submitTab();
        
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        PremiumsAndCoveragesQuoteTab premiumsTab = new PremiumsAndCoveragesQuoteTab();
        premiumsTab.calculatePremium();
        premiumWithDiscount = PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();

        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(premiumWithoutDiscount).as("Premium after Safe Home discount applied equals to initial premium").isNotEqualTo(premiumWithDiscount);

            Map<String, String> safeHomeDiscounts_dataRow = new HashMap<>();
            safeHomeDiscounts_dataRow.put("Discount Category", "Safe Home");
            safeHomeDiscounts_dataRow.put("Discounts Applied", "Theft Protection, Newer Home, Fire Protection");

            softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(safeHomeDiscounts_dataRow)).exists();

            PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Safe Home Discount Category")).as("Safe Home Discount is not applied").isNotEqualTo("0.0");
            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Newer Home discount")).as("Newer Home discount is not applied").isNotEqualTo("0.0");
            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Theft Alarm")).as("Incorrect value of Theft Alarm in Rating Details").isEqualTo("Central");
            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Private community")).as("Incorrect value of Private community in Rating Details").isEqualTo("Yes");
            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Theft Protective Devices discount")).as("Theft Protective Devices discount is not applied")
                    .isNotEqualTo("0.0");
            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Fire alarm")).as("Incorrect value of Fire alarm in Rating Details").isEqualTo("Central");
            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Sprinkler protection")).as("Incorrect value of Sprinkler protection in Rating Details")
                    .isEqualTo("Full");
            softly.assertThat(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Fire Protective Device discount")).as("Fire Protective Device discount is not applied")
                    .isNotEqualTo("0.0");
            PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
            DocumentsTab documentsTab = new DocumentsTab();
            documentsTab.fillTab(td_safeHome);
            documentsTab.submitTab();

            policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
            new PurchaseTab().submitTab();

            softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
            log.info("TEST: HSS Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());

            softly.assertThat(premiumWithDiscount).as("Incorrect premium value on Consolidated page").isEqualTo(PolicySummaryPage.getTotalPremiumSummaryForProperty());
        });
        
	}

}
