/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.delta.ct.auto;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Viktor Petrenko
 * @name
 * @scenario 1. Create customer
 * 2. Initiate AutoSS quote creation
 * 3. Go to General Tab
 * 4. Verify Dropdown Values on General tab
 * 5. Verify that there is no Motorcycle option in 'AAA Products Owned' section
 * 6. Select option "Yes" For all available products owned - Life, Home, Renters, Condo.
 * 7. Do not provide policy numbers for any of those current AAA policies.
 * 8. Verify field TollFree Number visible
 * 9. Select any option other than "None" for 'Adversely Impacted' field.
 * 10. Verify dropdown visible
 * @details
 */
@Test(groups = {Groups.DELTA, Groups.HIGH})
public class TestDeltaScenario1 extends AutoSSBaseTest {
    //todo make it empty
    private String quoteNumber;// = "QCTSS950552321";

    private DriverTab driverTab = new DriverTab();
    private PrefillTab prefillTab = new PrefillTab();
    private GeneralTab generalTab = new GeneralTab();

    public String scenarioPolicyType = "Auto SS";

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

    /**
     * @author Viktor Petrenko
     * @name Prefill tab controls check for AutoSS product, CT state [TC01]
     * @scenario 1. Create customer
     * 2. Initiate AutoSS quote creation
     * 3. Move to Prefill tab
     * 4. Verify Dropdown Values in Prefill tab
     * @details
     */
    @Test(groups = {Groups.DELTA, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS)
    public void testSC1_TC1() {
        preconditions();

        CustomAssert.enableSoftMode();
        //010-005CT
        //If the zip code is associated with only one county/township, the drop down list contains only that county/township value with default being that value
        prefillTab.getAssetList().getAsset(AutoSSMetaData.PrefillTab.COUNTY_TOWNSHIP).verify.
                value("New Haven / New Haven");
        //If the zip code spans across counties/townships, the drop down list will contain the applicable counties/townships that are associated with the zip code; the default value is 'blank'.


        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();

        prefillTab.fillTab(getPolicyTD());
        Tab.buttonSaveAndExit.click();
    }

    /**
     * @author Viktor Petrenko
     * @name General tab controls check for AutoSS product, CT state [TC02]
     * @scenario 1. Open created quoted
     * 2. Move to General tab
     * 3. Verify Dropdown Values in General tab
     * @details
     */
    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC02() {
        preconditions(NavigationEnum.AutoSSTab.GENERAL);

        //Residence
        List<String> expectedValuesOfResidence = Arrays.asList("Own Home", "Own Condo", "Own Mobile Home", "Rents Multi-Family Dwelling",
                "Rents Single-Family Dwelling", "Lives with Parent", "Other");
        generalTab.getNamedInsuredInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.NamedInsuredInformation.RESIDENCE).verify.optionsContain(expectedValuesOfResidence);
        //Current AAA Member
        List<String> expectedValuesOfCurrentMember = Arrays.asList("Yes", "No", "Membership Pending");
        generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.CURRENT_AAA_MEMBER).verify.optionsContain(expectedValuesOfCurrentMember);
        //Source of Business
        List<String> expectedValuesOfSourceOfBusiness = Arrays.asList("New Business", "Spin", "Split", "Rewrite");
        generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.SOURCE_OF_BUSINESS).verify.optionsContain(expectedValuesOfSourceOfBusiness);
        //Policy Type
        List<String> expectedValuesOfPolicyType = Arrays.asList("Standard", "Named Non Owner");
        generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TYPE).verify.optionsContain(expectedValuesOfPolicyType);
        //PolicyTerm
        List<String> expectedValuesOfPolicyTerm = Arrays.asList("Annual", "Semi-annual");
        generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TERM).verify.optionsContain(expectedValuesOfPolicyTerm);
        //Motorcycle related items should be absent on page
        generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.LIFE).verify.present();
        generalTab.getAAAProductOwnedAssetList().getAsset(AutoSSMetaData.GeneralTab.AAAProductOwned.MOTORCYCLE).verify.present(false);

        generalTab.fillTab(getPolicyTD());

        //1. ELC field is present
        //2. Drop-down contain correct values
        //3. ELC default value = None
        generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXTRAORDINARY_LIFE_CIRCUMSTANCE).verify.present();
        List<String> expectedValuesOfELC = Arrays.asList("None", "A catastrophic illness or injury", "The death of a spouse, child or parent",
                "Involuntary loss of employment for a period of 3 months/ more, if it results from involuntary termination", "Divorce",
                "Total or other loss that makes your home uninhabitable", "Identity theft", "Other events, as determined by the insurer", "Declined");
        generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXTRAORDINARY_LIFE_CIRCUMSTANCE).verify.optionsContain(expectedValuesOfELC);

        //020-008CT, ELC default = None
        generalTab.getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXTRAORDINARY_LIFE_CIRCUMSTANCE).verify.value("None");

        Tab.buttonSaveAndExit.click();
    }

    @Test
    @TestInfo(component = ComponentConstant.Service.AUTO_SS)
    public void testSC1_TC03() {
        preconditions(NavigationEnum.AutoSSTab.GENERAL);

        generalTab.getAssetList().getAsset(AutoSSMetaData.PrefillTab.ZIP_CODE).setValue("06001");
        generalTab.getAssetList().getAsset(AutoSSMetaData.PrefillTab.COUNTY_TOWNSHIP).verify.
                value("");

        generalTab.getAssetList().getAsset(AutoSSMetaData.PrefillTab.ZIP_CODE).setValue(getCustomerIndividualTD("DataGather","TestData")
                .getTestData("GeneralTab").getValue("Zip Code"));}

    private void preconditions(NavigationEnum.AutoSSTab navigateTo) {
        initiateQuote();
        NavigationPage.toViewTab(navigateTo.get());
    }

    private void preconditions() {
        initiateQuote();
    }

    private void initiateQuote() {
        mainApp().open();
        String quote = getQuoteNumber();
        MainPage.QuickSearch.buttonSearchPlus.click();

        SearchPage.openQuote(quote);
        policy.dataGather().start();
    }

    private String getQuoteNumber() {
        if (quoteNumber == null) {
            mainApp().open();
            createCustomerIndividual();
            policy.initiate();
            policy.getDefaultView().fillUpTo(getPolicyTD(), PrefillTab.class, true);
            Tab.buttonSaveAndExit.click();
            quoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
            log.info("DELTA CT SC1: ASS Quote created with #" + quoteNumber);
        }
        return quoteNumber;
    }
}
