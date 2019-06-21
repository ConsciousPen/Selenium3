package aaa.modules.regression.sales.template.functional;

import java.util.*;
import java.util.stream.Collectors;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.EndorsementForms;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PersonalPropertyTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.PersonalPropertyMultiAssetList;
import toolkit.webdriver.controls.TextBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

public class TestPersonalPropertyLimitsTemplate extends PolicyBaseTest {

    private static final String BICYCLES = "Bicycles";
    private static final String CAMERAS = "Cameras";
    private static final String COINS = "Coins";
    private static final String FINE_ARTS = "Fine arts";
    private static final String FIREARMS = "Firearms";
    private static final String FURS = "Furs";
    private static final String GOLF_EQUIPMENT = "Golf equipment";
    private static final String JEWELRY = "Jewelry";
    private static final String MEDICAL_DEVICES = "Medical devices";
    private static final String MUSICAL_INSTRUMENTS = "Musical instruments";
    private static final String POSTAGE_STAMPS = "Postage stamps";
    private static final String SILVERWARE = "Silverware";
    private static final String TRADING_CARDS_COMICS = "Trading cards or comics";

    private List<String> articleTypesl = new ArrayList<>(
            Arrays.asList("Bicycles", "Cameras", "Coins", "Fine arts", "Firearms", "Furs", "Golf equipment", "Jewelry",
                    "Medical devices", "Musical instruments", "Postage stamps", "Silverware", "Trading cards or comics"));

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

    protected void testScheduledPersonalPropertyMaxLimitsNB() {
//        createQuoteAndFillUpTo(getPolicyTD(), PremiumsAndCoveragesQuoteTab.class);
        mainApp().open();
        SearchPage.openQuote("QAZH3952918568");
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        Dollar covC = new Dollar(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_C).getValue());
        NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT.get());
        endorsementTab.getAddEndorsementLink(EndorsementForms.HomeSSEndorsementForms.HS_04_61.getFormId()).click();
        endorsementTab.btnSaveForm.click();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_ENDORSEMENT_SCHEDULED_PERSONAL_PROPERTY.get());

        for (Map.Entry<String, Dollar> entry : articleTypes.entrySet()) {
            Dollar catMax = entry.getValue();
            int numItems = (int)Math.ceil(covC.divide(catMax));
            personalPropertyTab.getAssetList().getAsset(entry.getKey(), PersonalPropertyMultiAssetList.class).expandSection();
            for (int i = 0; i < numItems; i++) {
                personalPropertyTab.fillTab(testDataManager.getDefault(TestPersonalPropertyLimitsTemplate.class).getTestData("TestData_" + entry.getKey().replaceAll(" ", "_")));
            }

        }


    }

    private List<PersonalPropertyMultiAssetList> getSppAssetLists() {
        List<PersonalPropertyMultiAssetList> assets;
        SortedSet<AssetDescriptor<?>> assetDescriptorListSPP = MetaData.getAssets(HomeSSMetaData.PersonalPropertyTab.class);
        assets = assetDescriptorListSPP.stream().map(asset -> new PersonalPropertyMultiAssetList(asset.getLocator(), HomeSSMetaData.PersonalPropertyTab.class)).collect(Collectors.toList());
        return assets;
    }

//    private List<TextBox> getCategoryAssets(String category) {
//        List<PersonalPropertyMultiAssetList> assetLists = getSppAssetLists();
//        for (PersonalPropertyMultiAssetList assetList : assetLists) {
//            //if assetList.get
//        }
//        return assetLists;
//    }

}


