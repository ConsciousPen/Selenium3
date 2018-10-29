package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.EndorsementForms;
import aaa.main.enums.products.HomeSSConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.datax.TestData;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestKYCityAndCountyTaxesForEndorsementFormsTemplate extends TestEndorsementsTabTemplate {


    private static final String TAX_RATE = "10%";
    private static final double TAX_RATE_VALUE = 10;

    protected void testKYCityAndCountyTaxesNBTx() {

        createQuoteAndFillUpTo(getTestData(), PremiumsAndCoveragesQuoteTab.class);
        NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());

        validateCityAndCountyTaxIncrease();

    }

    protected void testKYCityAndCountyTaxesEndorsementsTx() {

        openAppAndCreatePolicy(getTestData());

        initiateEndorsementTx();

        validateCityAndCountyTaxIncrease();

    }

    protected void testKYCityAndCountyTaxesRenewalTx() {

        openAppAndCreatePolicy(getTestData());

        initiateRenewalTx();

        validateCityAndCountyTaxIncrease();

    }

    @Override
    protected void addOptionalEndorsement(String endorsementFormId) {
        endorsementTab.getAddEndorsementLink(endorsementFormId).click();

        EndorsementForms.HomeSSEndorsementForms formEnum = EndorsementForms.HomeSSEndorsementForms.getFormEnum(endorsementFormId);

        switch (formEnum) {
            case HS_04_53:
                endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_04_53)
                        .getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0453.COVERAGE_LIMIT).setValue("index=3");
                break;
            case HS_09_26:
                endorsementTab.getAssetList().getAsset(HomeSSMetaData.EndorsementTab.HS_09_26)
                        .getAsset(HomeSSMetaData.EndorsementTab.EndorsementHS0926.COVERAGE_LIMIT).setValue("index=2");
                break;
                }
        endorsementTab.btnSaveForm.click();
    }

    private void validateCityAndCountyTaxIncrease(){

        addEndorsementForm(
                EndorsementForms.HomeSSEndorsementForms.HS_04_53.getFormId(),
                EndorsementForms.HomeSSEndorsementForms.HS_09_26.getFormId(),
                EndorsementForms.HomeSSEndorsementForms.HS_09_34.getFormId());

        premiumsAndCoveragesQuoteTab.calculatePremium();
        Dollar dwellingPremium = PremiumsAndCoveragesQuoteTab.getPolicyDwellingPremium();
        Dollar endorsementsPremium = PremiumsAndCoveragesQuoteTab.getEndorsedPolicyActualPremium();


        assertThat(TAX_RATE).isEqualTo(PremiumsAndCoveragesQuoteTab.tableTaxes
                .getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.CITY_TAX)
                .getCell(HomeSSConstants.StateAndLocalTaxesTable.PERCENTAGE).getValue());
        assertThat(TAX_RATE).isEqualTo(PremiumsAndCoveragesQuoteTab.tableTaxes
                .getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.COUNTY_TAX)
                .getCell(HomeSSConstants.StateAndLocalTaxesTable.PERCENTAGE).getValue());

        Dollar cityTax = new Dollar(PremiumsAndCoveragesQuoteTab.tableTaxes
                .getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.CITY_TAX)
                .getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM).getValue());
        Dollar countyTax = new Dollar(PremiumsAndCoveragesQuoteTab.tableTaxes
                .getRowContains(HomeSSConstants.StateAndLocalTaxesTable.DESCRIPTION, HomeSSConstants.StateAndLocalTaxesTable.COUNTY_TAX)
                .getCell(HomeSSConstants.StateAndLocalTaxesTable.TERM_PREMIUM).getValue());

        assertThat(cityTax).isEqualTo(dwellingPremium.getPercentage(TAX_RATE_VALUE));
        assertThat(countyTax).isEqualTo(endorsementsPremium.add(dwellingPremium).getPercentage(TAX_RATE_VALUE));

    }

    private TestData getTestData() {

        return getPolicyTD()
                .adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
                        HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel()), "41073")
                .adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
                        HomeSSMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel()), "268 WARD AVE")
                .adjust(TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(),
                        HomeSSMetaData.ApplicantTab.DwellingAddress.COUNTY.getLabel()), "CountyCounty")
                .mask(TestData.makeKeyPath(HomeSSMetaData.DocumentsTab.class.getSimpleName(), HomeSSMetaData.DocumentsTab.DOCUMENTS_TO_ISSUE.getLabel(),
                        HomeSSMetaData.DocumentsTab.DocumentsToIssue.KENTUCKY_MINE_SUBSIDENCE_INSURANCE_FUND_WAIVER_FORM.getLabel()));
    }

}
