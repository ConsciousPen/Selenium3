/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.Tab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.AbstractContainer;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

/**
 * *@author Viktoriia Lutsenko
 * *@name Absence of contact information for other named insureds than FNI.
 * *@scenario
 * 1.Initiate quote creation.
 * 2.Add FNI with only email as contact information.
 * 3.Add 2nd named insured.
 * 4.Add 3rd named insured.
 * 5.Verify that FNI has Contact Information section, 2nd and 3rd named insureds don't have Contact Information section.
 * 6.Bind policy.
 * 7.Verify that system shows error, because rule for mandatory phone number is triggered for FNI only (AAA_SS10240324 - SS, ERROR_AAA_CSA9231984 - CA).
 * 8.Fill phone number for FNI.
 * 9.Change 2nd named insured to FNI, don't fill contact information.
 * 10.FNI has contact information section, others don't have.
 * 11.Bind policy.
 * 12.Verify that system shows error, because rule for mandatory phone number is triggered (AAA_SS10240324 - SS, ERROR_AAA_CSA9231984 - CA).
 * 13.Fill contact information for the FNI.
 * 14.Bind policy.
 * 15.Verify that policy is bound.
 * *@details
 */
public abstract class TestContactInformationAbstract extends PolicyBaseTest {

    protected abstract String getGeneralTab();
    protected abstract String getDriverTab();
    protected abstract String getPremiumAndCoverageTab();
    protected abstract String getDocumentsAndBind();

    protected abstract Tab getGeneralTabElement();
    protected abstract Tab getDriverTabElement();
    protected abstract Tab getPremiumAndCoverageTabElement();
    protected abstract Tab getDriverActivityReportsTabElement();
    protected abstract Tab getDocumentsAndBindElement();
    protected abstract Tab getPurchaseTabElement();

    protected abstract CommonErrorTab getErrorTabElement();
    protected abstract AssetDescriptor<RadioGroup> getSalesAgentAgreement();
    protected abstract AssetDescriptor<Button> getValidateDrivingHistory();
    protected abstract AssetDescriptor<TextBox> getHomePhoneNumber();
    protected abstract ErrorEnum.Errors getVerificationError();

    public abstract void pas270_contactInformation(String state);

    /**
     * Steps: #1-#4
     */
    protected void initiateQuote(Class tabClass) {
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(getTestSpecificTD("TestData"), tabClass, true);
        NavigationPage.toViewSubTab(getGeneralTab());
    }

    /**
     * Steps: #5-#7
     */
    protected void verifyContactInformationSection() {
        assertContactsInformationPresent(true, false, false);
        verificationOfMandatoryPhoneNumber();
    }

    /**
     * Steps: #8-#12
     */
    protected void changeFNIAndVerifyContactInformationSection(String firstNI, String relationshipToFNI) {
        NavigationPage.toViewSubTab(getGeneralTab());
        addPhoneNumberToInsured(1, "1112223334");
        getGeneralTabElement().getAssetList().getAsset(firstNI, ComboBox.class ).setValueByIndex(1);
        Page.dialogConfirmation.confirm();
        assertContactsInformationPresent(false, true, false);
        NavigationPage.toViewSubTab(getDriverTab());
        setRelationshipToNI(1,2, relationshipToFNI);
        setRelationshipToNI(3,3, relationshipToFNI);
        verificationOfMandatoryPhoneNumber();
    }

    /**
     * Steps: #13-#14
     */
    protected void bindPolicy() {
        NavigationPage.toViewSubTab(getGeneralTab());
        addPhoneNumberToInsured(1, "2223334445");
        NavigationPage.toViewSubTab(getDocumentsAndBind());
        getDocumentsAndBindElement().submitTab();
        getPurchaseTabElement().fillTab(getTestSpecificTD("TestData")).submitTab();
    }

    private void assertContactsInformationPresent(boolean firstNIPresent, boolean secondNIPresent, boolean thirdNIPresent) {
        CustomSoftAssertions.assertSoftly(softly -> {
            presenceOfContactInformationSection(1, firstNIPresent, softly);
            presenceOfContactInformationSection(2, secondNIPresent, softly);
            presenceOfContactInformationSection(3, thirdNIPresent, softly);
        });
    }

    protected abstract void presenceOfContactInformationSection(int insuredNumber, boolean isPresent, ETCSCoreSoftAssertions softly);

    protected abstract void setRelationshipToNI(int driverNumber, int relationship, String relationshipToFNI);

    private void verificationOfMandatoryPhoneNumber() {
        NavigationPage.toViewSubTab(getPremiumAndCoverageTab());
        getPremiumAndCoverageTabElement().submitTab();
        AbstractContainer<?, ?> assetList = getDriverActivityReportsTabElement().getAssetList();
        if (assetList.getAsset(getSalesAgentAgreement()).isPresent()) {
            assetList.getAsset(getSalesAgentAgreement()).setValue("I Agree");
        }
        assetList.getAsset(getValidateDrivingHistory()).click();
        getDriverActivityReportsTabElement().submitTab();
        getDocumentsAndBindElement().submitTab();
        assertThat(getErrorTabElement().getErrorsControl().getTable()).as("%s should be displayed only for first named insured", getVerificationError().getCode()).hasRows(1);
        getErrorTabElement().cancel();
    }

    protected abstract void addPhoneNumberToInsured(int insuredNumber, String phoneNumber);

}
