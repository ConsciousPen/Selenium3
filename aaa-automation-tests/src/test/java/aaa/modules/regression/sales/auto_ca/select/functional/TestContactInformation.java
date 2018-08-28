/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ca.select.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestContactInformationAbstract;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;
import toolkit.verification.ETCSCoreSoftAssertions;
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
    @StateList(states =  States.CA)
    @Test(groups = { Groups.REGRESSION, Groups.MEDIUM })
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = {"PAS-270", "PAS-267"})
    public void pas270_contactInformation(@Optional("CA") String state)  {
        initiateQuote(DocumentsAndBindTab.class);

        verifyContactInformationSection();
        changeFNIAndVerifyContactInformationSection(AutoCaMetaData.GeneralTab.FIRST_NAMED_INSURED.getLabel(),
                AutoCaMetaData.DriverTab.REL_TO_FIRST_NAMED_INSURED.getLabel());
        bindPolicy();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
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
    protected void presenceOfContactInformationSection(int insuredNumber, boolean isPresent, ETCSCoreSoftAssertions softly) {
        ((GeneralTab)getGeneralTabElement()).viewInsured(insuredNumber);
        softly.assertThat(getGeneralTabElement().isSectionPresent("Contact Information")).as("'Contact Information' section should be present").isEqualTo(isPresent);
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
