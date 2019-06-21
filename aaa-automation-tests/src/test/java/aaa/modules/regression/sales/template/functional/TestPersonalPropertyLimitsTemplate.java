package aaa.modules.regression.sales.template.functional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.EndorsementForms;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.PersonalPropertyMultiAssetList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestPersonalPropertyLimitsTemplate extends PolicyBaseTest {

//    private static final String BICYCLES = "Bicycles";
//    private static final String CAMERAS = "Cameras";
//    private static final String COINS = "Coins";
//    private static final String FINE_ARTS = "Fine arts";
//    private static final String FIREARMS = "Firearms";
//    private static final String FURS = "Furs";
//    private static final String GOLF_EQUIPMENT = "Golf equipment";
//    private static final String JEWELRY = "Jewelry";
//    private static final String MEDICAL_DEVICES = "Medical devices";
//    private static final String MUSICAL_INSTRUMENTS = "Musical instruments";
//    private static final String POSTAGE_STAMPS = "Postage stamps";
//    private static final String SILVERWARE = "Silverware";
//    private static final String TRADING_CARDS_COMICS = "Trading cards or comics";


    private Map<String, Dollar> articleTypes = new LinkedHashMap<String, Dollar>() {{
        put("Bicycles", new Dollar(10000));
        put("Cameras", new Dollar(10000));
        put("Coins", new Dollar(10000));
        put("Fine arts", new Dollar(20000));
        put("Firearms", new Dollar(10000));
        put("Furs", new Dollar(20000));
        put("Golf equipment", new Dollar(20000));
        put("Jewelry", new Dollar(50000));
        put("Medical devices", new Dollar(20000));
        put("Musical instruments", new Dollar(20000));
        put("Postage stamps", new Dollar(10000));
        put("Silverware", new Dollar(20000));
        put("Trading cards or comics", new Dollar(10000));
    }};

    private PersonalPropertyTab personalPropertyTab = new PersonalPropertyTab();
    private EndorsementTab endorsementTab = new EndorsementTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
    private DocumentsTab documentsTab = new DocumentsTab();
    private BindTab bindTab = new BindTab();
    private ErrorTab errorTab = new ErrorTab();
    private PurchaseTab purchaseTab = new PurchaseTab();

    protected void testScheduledPersonalPropertyMaxLimitsNB() {

        // Initiate quote and fill up to Documents Tab
        createQuoteAndFillUpTo(getPolicyTD(), DocumentsTab.class);
        premiumsAndCoveragesQuoteTab.calculatePremium();

        // ****************************************************
//        mainApp().open();
//        SearchPage.openQuote("QAZH3952918574");
//        policy.dataGather().start();
//        premiumsAndCoveragesQuoteTab.calculatePremium();
        // ****************************************************

        // Capture Coverage C and calculate percentages
        Dollar covC = new Dollar(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C).getValue());
        Dollar covC25 = covC.getPercentage(25);
        Dollar covC50 = covC.getPercentage(50);

        // Navigate to SPP section and add HS 04 61 endorsement
        NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
        endorsementTab.getAddEndorsementLink(EndorsementForms.HomeSSEndorsementForms.HS_04_61.getFormId()).click();
        endorsementTab.btnSaveForm.click();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT_SCHEDULED_PERSONAL_PROPERTY.get());

        // Loop through each article type
        for (Map.Entry<String, Dollar> entry : articleTypes.entrySet()) {
            PersonalPropertyMultiAssetList thisAsset = personalPropertyTab.getAssetList().getAsset(entry.getKey(), PersonalPropertyMultiAssetList.class);

            // Calculate number of items to reach 25% of cov C for category
            Dollar catMax = entry.getValue();
            int maxItems = (int)Math.floor(covC25.divide(catMax));

            // Add one item above threshold
            thisAsset.fill(getCategoryTd(entry.getKey(), 1)
                    .adjust(TestData.makeKeyPath(entry.getKey() + "[0]", "Limit of liability"), entry.getValue().add(new Dollar(1)).toString()));

            // Navigate to Bind, validate error message
            navigateToBindTabAndSubmit();
            errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS3282281);

            // Navigate back to SPP tab and remove items
            errorTab.cancel();
            navigateToSPPTab();
            thisAsset.removeAll();

            // Add multiple items each under threshold and total above category threshold
            thisAsset.fill(getCategoryTd(entry.getKey(), maxItems + 1));
            navigateToBindTabAndSubmit();
            errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS4230168);

            // Navigate back to SPP tab and remove one item
            errorTab.cancel();
            navigateToSPPTab();
            thisAsset.removeSection(1);

            // Navigate to Bind, validate no errors
            navigateToBindTabAndSubmit();
            assertThat(purchaseTab.isVisible()).isTrue();
            PurchaseTab.buttonCancel.click();

            // Start data gather mode and navigate to SPP tab
            policy.dataGather().start();
            navigateToSPPTab();

        }

        // TODO add testing for above overall threshold

        // TODO add testing for 2+ groups under cat thresholds and under overall threshold and bind
    }

    private TestData getCategoryTd(String category, int numItems) {
        TestData td = DataProviderFactory.dataOf(
                "Limit of liability", articleTypes.get(category),
                "Description", "test");
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


