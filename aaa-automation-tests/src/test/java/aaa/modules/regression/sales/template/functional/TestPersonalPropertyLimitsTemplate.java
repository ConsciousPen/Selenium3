package aaa.modules.regression.sales.template.functional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.EndorsementForms;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.PersonalPropertyMultiAssetList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.TextBox;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestPersonalPropertyLimitsTemplate extends PolicyBaseTest {

    private Map<String, Dollar> articleTypes = new LinkedHashMap<String, Dollar>() {{
        put(HomeSSMetaData.PersonalPropertyTab.BICYCLES.getLabel(), new Dollar(10000));
        put(HomeSSMetaData.PersonalPropertyTab.CAMERAS.getLabel(), new Dollar(10000));
        put(HomeSSMetaData.PersonalPropertyTab.COINS.getLabel(), new Dollar(10000));
        put(HomeSSMetaData.PersonalPropertyTab.FINE_ARTS.getLabel(), new Dollar(20000));
        put(HomeSSMetaData.PersonalPropertyTab.FIREARMS.getLabel(), new Dollar(10000));
        put(HomeSSMetaData.PersonalPropertyTab.FURS.getLabel(), new Dollar(20000));
        put(HomeSSMetaData.PersonalPropertyTab.GOLF_EQUIPMENT.getLabel(), new Dollar(20000));
        put(HomeSSMetaData.PersonalPropertyTab.JEWELRY.getLabel(), new Dollar(50000));
        put(HomeSSMetaData.PersonalPropertyTab.MEDICAL_DEVICES.getLabel(), new Dollar(20000));
        put(HomeSSMetaData.PersonalPropertyTab.MUSICAL_INSTRUMENTS.getLabel(), new Dollar(20000));
        put(HomeSSMetaData.PersonalPropertyTab.POSTAGE_STAMPS.getLabel(), new Dollar(10000));
        put(HomeSSMetaData.PersonalPropertyTab.SILVERWARE.getLabel(), new Dollar(20000));
        put(HomeSSMetaData.PersonalPropertyTab.TRADING_CARDS_OR_COMICS.getLabel(), new Dollar(10000));
    }};

    private PersonalPropertyTab personalPropertyTab = new PersonalPropertyTab();
    private EndorsementTab endorsementTab = new EndorsementTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
    private DocumentsTab documentsTab = new DocumentsTab();
    private BindTab bindTab = new BindTab();
    private ErrorTab errorTab = new ErrorTab();
    private PurchaseTab purchaseTab = new PurchaseTab();

    private static String limitOfLiability = HomeSSMetaData.PersonalPropertyTab.Bicycles.LIMIT_OF_LIABILITY.getLabel();
    private static String description = HomeSSMetaData.PersonalPropertyTab.Bicycles.DESCRIPTION.getLabel();

    protected void testScheduledPersonalPropertyMaxLimitsNB() {

        // Initiate quote and fill up to Documents Tab
        createQuoteAndFillUpTo(getPolicyTD(), DocumentsTab.class);
        premiumsAndCoveragesQuoteTab.calculatePremium();

        // Capture Coverage C and calculate percentages
        Dollar covC = new Dollar(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C).getValue());
        Dollar covC25 = covC.getPercentage(25);
        Dollar covC50 = covC.getPercentage(50);

        // Calculate amount to reach 25% of cov C for category (for use in loop)
        int numItems = 5;
        Dollar perItemValue = covC25.divide(numItems);

        // Navigate to SPP section and add HS 04 61 endorsement
        NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
        endorsementTab.getAddEndorsementLink(EndorsementForms.HomeSSEndorsementForms.HS_04_61.getFormId()).click();
        endorsementTab.btnSaveForm.click();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT_SCHEDULED_PERSONAL_PROPERTY.get());

        // Loop through each article type
        for (Map.Entry<String, Dollar> entry : articleTypes.entrySet()) {
            PersonalPropertyMultiAssetList thisAsset = personalPropertyTab.getAssetList().getAsset(entry.getKey(), PersonalPropertyMultiAssetList.class);

            // Add one item above threshold
            thisAsset.fill(getCategoryTd(entry.getKey(), entry.getValue().add(new Dollar(1)).toString(), 1));

            // Navigate to Bind, validate error message
            navigateToBindTabAndSubmit();
            errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3282281);
            if (entry.getValue().equals(covC25) || entry.getValue().moreThan(covC25)) {
                errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS4230168);
            }

            // Navigate back to SPP tab and remove items
            errorTab.cancel();
            navigateToSPPTab();
            thisAsset.removeAll();

            // Add multiple items each under threshold and aggregate total above category threshold
            thisAsset.fill(getCategoryTd(entry.getKey(), perItemValue.toPlaingString(), numItems).resolveLinks()
                    .adjust(TestData.makeKeyPath(entry.getKey() + "[" + (numItems - 1) + "]", limitOfLiability), perItemValue.add(new Dollar(1)).toPlaingString()));
            navigateToBindTabAndSubmit();
            errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS4230168);

            // Navigate back to SPP tab and update last item
            errorTab.cancel();
            navigateToSPPTab();
            thisAsset.getAsset(limitOfLiability, TextBox.class).setValue(perItemValue.toPlaingString());

            // Navigate to Bind, validate no errors
            navigateToBindTabAndSubmit();
            assertThat(purchaseTab.isVisible()).isTrue();
            PurchaseTab.buttonCancel.click();

            // Start data gather mode and navigate to SPP tab and remove entries
            policy.dataGather().start();
            navigateToSPPTab();
            thisAsset.removeAll();

        }

        // Add items at max threshold from multiple categories until 50% of cov C is exceeded
        Dollar currentTotal = new Dollar(0);
        for (Map.Entry<String, Dollar> entry : articleTypes.entrySet()) {
            PersonalPropertyMultiAssetList thisAsset = personalPropertyTab.getAssetList().getAsset(entry.getKey(), PersonalPropertyMultiAssetList.class);
            thisAsset.fill(getCategoryTd(entry.getKey(), entry.getValue().toPlaingString(), 1));
            currentTotal = currentTotal.add(entry.getValue());
            if (currentTotal.moreThan(covC50)) {
                break;
            }
        }

        // Navigate to bind tab and validate error
        navigateToBindTabAndSubmit();
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS4230108);

    }

    private TestData getCategoryTd(String category, String limit, int numItems) {
        TestData td;
        switch (category) {
            case PolicyConstants.ScheduledPersonalPropertyTable.COINS:
                td = DataProviderFactory.dataOf(limitOfLiability, limit, HomeSSMetaData.PersonalPropertyTab.Coins.NUMBER_OF_ARTICLES.getLabel(), "3", description, "test");
                break;
            case PolicyConstants.ScheduledPersonalPropertyTable.FINE_ARTS:
                td = DataProviderFactory.dataOf(limitOfLiability, limit, HomeSSMetaData.PersonalPropertyTab.FineArts.FORM_OF_ART.getLabel(), "Painting", description, "test");
                break;
            case PolicyConstants.ScheduledPersonalPropertyTable.GOLF_EQUIPMENT:
                td = DataProviderFactory.dataOf(limitOfLiability, limit, HomeSSMetaData.PersonalPropertyTab.GolfEquipment.LEFT_OR_RIGHT_HANDED_CLUB.getLabel(), "contains=Left", description, "test");
                break;
            case PolicyConstants.ScheduledPersonalPropertyTable.JEWELRY:
                td = DataProviderFactory.dataOf(limitOfLiability, limit, HomeSSMetaData.PersonalPropertyTab.Jewelry.JEWELRY_CATEGORY.getLabel(), "Ring", description, "test");
                break;
            case PolicyConstants.ScheduledPersonalPropertyTable.MEDICAL_DEVICES:
                td = DataProviderFactory.dataOf(limitOfLiability, limit, HomeSSMetaData.PersonalPropertyTab.MedicalDevices.TYPE_OF_DEVICE.getLabel(), "Hearing Aid", description, "test");
                break;
            case PolicyConstants.ScheduledPersonalPropertyTable.POSTAGE_STAMPS:
                td = DataProviderFactory.dataOf(limitOfLiability, limit, HomeSSMetaData.PersonalPropertyTab.PostageStamps.NUMBER_OF_STAMPS.getLabel(), "2", description, "test");
                break;
            case PolicyConstants.ScheduledPersonalPropertyTable.SILVERWARE:
                td = DataProviderFactory.dataOf(limitOfLiability, limit, HomeSSMetaData.PersonalPropertyTab.Silverware.SET_OR_INDIVIDUAL_PIECE.getLabel(), "Set", description, "test");
                break;
            case PolicyConstants.ScheduledPersonalPropertyTable.TRADING_CARDS_COMICS:
                td = DataProviderFactory.dataOf(limitOfLiability, limit, HomeSSMetaData.PersonalPropertyTab.TradingCardsOrComics.NUMBER_OF_COMIC_BOOKS_OR_CARDS.getLabel(), "2",
                        HomeSSMetaData.PersonalPropertyTab.TradingCardsOrComics.CERTIFICATE_OF_AUTHENTICITY_RECEIVED.getLabel(), "Yes", description, "test");
                break;
            default:
                td = DataProviderFactory.dataOf(limitOfLiability, limit, description, "test");
        }
        List<TestData> list = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            list.add(td);
        }
        list = IntStream.range(0, numItems).mapToObj(i -> td).collect(Collectors.toList());
        return DataProviderFactory.dataOf(category, list);
    }

    private void navigateToBindTabAndSubmit() {
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.DOCUMENTS.get());
        if (documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.APPRAISALS_SALES_RECEIPTS_FOR_SCHEDULED_PROPERTY).isPresent()) {
            documentsTab.getRequiredToBindAssetList().getAsset(HomeSSMetaData.DocumentsTab.DocumentsToBind.APPRAISALS_SALES_RECEIPTS_FOR_SCHEDULED_PROPERTY).setValue("Yes");
        }
        documentsTab.submitTab();
        bindTab.submitTab();

    }

    private void navigateToSPPTab() {
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT_SCHEDULED_PERSONAL_PROPERTY.get());
    }

}


