package aaa.admin.pages.cem;

import org.openqa.selenium.By;

import aaa.admin.metadata.cem.CemMetaData;
import aaa.admin.pages.AdminPage;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.assets.AssetList;
import toolkit.webdriver.controls.composite.table.Table;

public class CampaignPage extends AdminPage {

    public static AssetList assetListEndCampain = new AssetList(By.id("endCampaignModalPanel_container"), CemMetaData.EndCampaignActionTab.class);

    public static Button buttonCreateNewCampaign = new Button(By.id("createNewCampaign"));

    public static Table tableCampaigns = new Table(By.id("searchForm:searchTable"));

}
