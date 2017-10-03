/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;


import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class PaperlessPreferences extends AutoSSBaseTest {

    private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();

    /**
     * @author Oleg Stasyuk
     * @name Test Paperless Preferences properties and Inquiry mode
     * @scenario 1. Create new Paperless Preferences eligible quote but for the not eligible state (PA)
     * 2. Check Enrolled in Paperless? field and Manage Paperless Preferences button are shown in Documents tab
     * 3. Check Manage Paperless Preferences button is enabled
     * 4. Save and Exist
     * 5. Open quote in Inquiry mode
     * 6. Check Manage Paperless Preferences button is disabled
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void pas282_eValuePaperlessPreferences(@Optional("VA") String state) {

        EValueDiscount eValueDiscount = new EValueDiscount();
        eValueDiscount.eValueQuoteCreationVA();

        CustomAssert.enableSoftMode();
        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        //PAS-282, PAS-268 start
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.enabled(false);
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.value("Yes");

        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.MANAGE_PAPERLESS_PREFERENCES).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.MANAGE_PAPERLESS_PREFERENCES).verify.enabled();
        //PAS-282, PAS-268 end

        documentsAndBindTab.saveAndExit();
        policy.quoteInquiry().start();

        //PAS-269 start
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.MANAGE_PAPERLESS_PREFERENCES).verify.present();
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.MANAGE_PAPERLESS_PREFERENCES).verify.enabled(false);
        //PAS-269 end

        documentsAndBindTab.cancel();
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    /**
     * @author Oleg Stasyuk
     * @name Test Paperless Preferences not shown for state where it is not configured
     * @scenario 1. Create new Paperless Preferences eligible quote but for the not eligible state (PA)
     * 2. Check Enrolled in Paperless? field and Manage Paperless Preferences button are not shown in Documents tab
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void pas838_eValuePaperlessPreferences(@Optional("PA") String state) {

        EValueDiscount eValueDiscount = new EValueDiscount();
        eValueDiscount.eValueQuoteCreationVA();

        CustomAssert.enableSoftMode();
        policy.dataGather().start();
        NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.ENROLLED_IN_PAPERLESS).verify.present(false);
        documentsAndBindTab.getPaperlessPreferencesAssetList().getAsset(AutoSSMetaData.DocumentsAndBindTab.PaperlessPreferences.MANAGE_PAPERLESS_PREFERENCES).verify.present(false);
        documentsAndBindTab.saveAndExit();

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    //TODO what needs to be automated
/*PAS-838
    GIVEN that Paperless Preferences have not been enabled for my state/product/effective date
    WHEN I navigate to the Bind tab
    THEN I should not see the Preferences section visible
*/

/*PAS-266
* GIVEN the agent is creating a new quote/viewing a policy in other than View Only mode
* WHEN I click on the Paperless Preferences button
* THEN I see the Preferences UI
* AND the UI shows both billing and policy preferencesGiven the agent is updating Paperless Preferences
* AND selects preferences for Billing
* AND Policy
* WHEN I click SAVE on the API UI
* THEN the "Enrolled in Preferences?" field is set to Pending
*
* Given the agent is updating Paperless Preferences
* AND selects preferences for Billing OR Policy but NOT BOTH
* WHEN I click SAVE on the API UI
* THEN the "Enrolled in Preferences?" field is set to See Below Paperless Preferences - Billing Paperless Preferences - Policy Status on Enrolled in Preferences? Opt In Opt In Yes Opt In Opt Out Yes (Billing Only) Opt In Pending Pending (Policy) Opt Out Opt In Yes (Policy Only) Opt Out Opt Out No Opt Out Pending Pending (Policy Only) Pending Opt In Pending (Billing) Pending Opt Out Pending (Billiing Only) Pending Pending Pending */

/*PAS-268 - done
GIVEN I am an agent
WHEN I navigate to the General Info Page
THEN I see the Enrolled in Paperless question
AND the answer is populated based on the customers current preferences
 */

/*PAS-269 - done
* GIVEN I am an agent and I am in a policy in other than inquiry mode
* WHEN I go to the General Info Page
* THEN I see the preferences button displayed and enabled
*
* GIVEN I am an agent and I am in a policy in inquiry mode
* WHEN I go to the General Info Page
* THEN I see the preferences button displayed and disabled.
*
* GIVEN I am an agent and have entered the email address on General Info Page
* WHEN I click on the preferences button
* THEN I am able to set my preferences
* AND change email is populated in the preferences API POP UP
*
* GIVEN I am an agent and I am in a quote
* WHEN I go to the General Info Page
* THEN I see the preferences button is displayed and disabled
* */

/*Question to Sabra
how do we receive data from stub? can we trick the app with stub response <> Yes for example submitting a request with expected value?*/

/*PAS-271 OSI - not planned - stubbed
GIVEN I am an agent on the General Info page in other than inquiry mode or Quote
WHEN I select the paperless billing preferences button
AND I change/add my current billing preferences enrollment
AND save my choices
THEN the answer is updated with my new current paperless billing preferences enrollment
*/

/*
*  	PAS-282 	Move Paperless Preferences Button from General Info to Bind Info Page
*
* */


/*    PAS-285

Binding Auto NB and Paperless Preferences



GIVEN I have a quote at new business
AND my quote has been rated at least once
WHEN I navigate to the Bind page
THEN I can click on paperless preferences
AND I can select both policy and billing preferences
AND the Enrolled in Paperless is updated based on the status

GIVEN I have a quote at new business
AND eValue is opted in
AND quote is rated at least once
WHEN I do NOT opt in to paperless preferences for billing AND policy
THEN I receive an error message EV100003

GIVEN I have a new business policy
AND I have opted in to paperless preferences during the quote bind process
WHEN I review my preferences for my new policy
THEN I see the preferences as pending (or based on what my quote preferences were)


PAS-287
As an agent, I want clear help text to explain the paperless preferences options that are available to me so that I am able to clearly state the requirements to my customer for opting into the discount.

Help Text ID: HT10001
Link to spreadsheet: https://csaaig.atlassian.net/wiki/x/LJCKB

Link to UX: https://xd.adobe.com/view/824e6167-dc5b-44d6-9f4b-38b4982f124d/screen/d56dba54-db51-46bd-b72d-e5e0e4705cd1/PAS-Quote-Documents-1



https://csaaig.atlassian.net/browse/PAS-283
As an Agent, I do not want to see the document delivery section on the bind page if I am opted into Paperless Policy Preferences so that it doesn't bother me anymoreOnly SS Auto
*/

/*PAS-327
*Given that I am enrolled in eValueAND at NB + 15 I did not meet the membership or billing paperless preferences CriteriaAND AT NB+ 30 I have still not purchased Membership or I have still not signed up for Paperless Billing PreferencesWHEN the system validates NB + 30THEN the eValue Discount is removed automatically from the policy.
* */

/*PAS-329
Given that I am enrolled in eValueAND at NB + 15 I did not meet the policy paperless preferences CriteriaAND AT NB+ 30 I have still not purchased Membership or I have still not signed up for Paperless Billing and policy PreferencesWHEN the system validates NB + 30THEN the eValue status is updated to show Inactive and a user note is added to show the eValue discount is removed
* */


}
