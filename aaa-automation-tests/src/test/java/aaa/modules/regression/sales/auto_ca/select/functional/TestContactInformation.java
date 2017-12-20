/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.modules.regression.sales.template.functional.TestContactInformationAbstract;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;


public class TestContactInformation extends TestContactInformationAbstract {
    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * * @author Viktoriia Lutsenko
     *
     * PAS-270
     *
     * See detailed steps in template file
     * {@link aaa.modules.regression.sales.template.functional.TestContactInformationAbstract}
     */
    @Parameters({"state"})
    @Test(groups = { Groups.FUNCTIONAL, Groups.MEDIUM })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = {"PAS-270", "PAS-267"})
    public void pas270_contactInformation(@Optional("CA") String state)  {
        initiateQuote(DocumentsAndBindTab.class);

        CustomAssert.enableSoftMode();
        verifyContactInformationSection();
        changeFNIAndVerifyContactInformationSection(AutoCaMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel(),
                AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel());
        bindPolicy();
        verifyPolicyStatus();
        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();
    }

    @Override
    protected String getGeneralTab() {
        return NavigationEnum.AutoCaTab.GENERAL.get();
    }

    @Override
    protected String getDriverTab() {
        return NavigationEnum.AutoCaTab.DRIVER.get();
    }

    @Override
    protected String getPremiumAndCoverageTab() {
        return NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get();
    }

    @Override
    protected String getDocumentsAndBind() {
        return NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get();
    }

    @Override
    protected InquiryAssetList getInquiryAssetList() {
        return new InquiryAssetList(new GeneralTab().getAssetList().getLocator(), AutoCaMetaData.GeneralTab.class);
    }

    @Override
    protected CommonErrorTab getErrorTabElement() {
        return new ErrorTab();
    }

    @Override
    protected Tab getGeneralTabElement() {
        return new GeneralTab();
    }

    @Override
    protected Tab getDriverTabElement() {
        return new DriverTab();
    }

    @Override
    protected Tab getPremiumAndCoverageTabElement() {
        return new PremiumAndCoveragesTab();
    }

    @Override
    protected Tab getDriverActivityReportsTabElement() {
        return new DriverActivityReportsTab();
    }

    @Override
    protected Tab getDocumentsAndBindElement() {
        return new DocumentsAndBindTab();
    }

    @Override
    protected Tab getPurchaseTabElement() {
        return new PurchaseTab();
    }

    @Override
    protected AssetDescriptor<RadioGroup> getSalesAgentAgreement() {
        return AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT;
    }

    @Override
    protected AssetDescriptor<Button> getValidateDrivingHistory() {
        return AutoCaMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY;
    }

    @Override
    protected AssetDescriptor<TextBox> getHomePhoneNumber() {
        return AutoCaMetaData.GeneralTab.ContactInformation.HOME_PHONE_NUMBER;
    }

    @Override
    protected ErrorEnum.Errors getVerificationError() {
        return ErrorEnum.Errors.ERROR_AAA_CSA9231984;
    }

    @Override
    protected void presenceOfContactInformationSection(int insuredNumber, boolean isPresent) {
        ((GeneralTab)getGeneralTabElement()).viewInsured(insuredNumber);
        getInquiryAssetList().assetSectionPresence("Contact Information", isPresent);
    }

    @Override
    protected void setRelationshipToNI(int driverNumber, int relationship, String relationshipToFNI) {
        DriverTab.viewDriver(driverNumber);
        ((DriverTab)getDriverTabElement()).getAssetList().getAsset(relationshipToFNI, ComboBox.class).setValueByIndex(relationship);

    }

    @Override
    protected void addPhoneNumberToInsured(int insuredNumber, String phoneNumber) {
        ((GeneralTab)getGeneralTabElement()).viewInsured(insuredNumber);
        ((GeneralTab)getGeneralTabElement()).getContactInfoAssetList().getAsset(getHomePhoneNumber()).setValue(phoneNumber);
    }
}
