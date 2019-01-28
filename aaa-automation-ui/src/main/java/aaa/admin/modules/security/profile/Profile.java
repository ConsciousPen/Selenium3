/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.security.profile;

import static aaa.admin.metadata.security.ProfileMetaData.GeneralProfileTab.AGENCY_LOCATIONS;
import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;
import aaa.admin.metadata.security.ProfileMetaData;
import aaa.admin.modules.security.ChannelType;
import aaa.admin.modules.security.profile.defaulttabs.GeneralProfileTab;
import aaa.admin.modules.security.profile.views.DefaultView;
import aaa.admin.pages.security.ProfilePage;
import aaa.common.Tab;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class Profile implements IProfile {
    private ChannelType channelType;
    private Workspace defaultView = new DefaultView();

    public Profile(ChannelType channelType) {
        this.channelType = channelType;
    }

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void create(TestData td) {
        channelType = channelType == null ? ChannelType.valueOf(td.getValue("Channel")) : channelType;
        initiate();
        getDefaultView().fill(td);
		Tab.buttonSave.click();
        log.info("Created Profile " + td);
    }

    @Override
    public void create(TestData td, String agencyName) {
        create(td.adjust(TestData.makeKeyPath(ProfileMetaData.GeneralProfileTab.class.getSimpleName(), AGENCY_LOCATIONS.getLabel(), "Agency Name"), agencyName));
    }

    @Override
    public void createNonEisUser(TestData td) {
        channelType = channelType == null ? ChannelType.valueOf(td.getValue("Channel")) : channelType;
        initiate();
        new GeneralProfileTab().fillTab(td);
		Tab.buttonSave.click();
        log.info("Created Non-Eis User Profile " + td);
    }

    @Override
    public void initiate() {
        navigateToFlow();
        ProfilePage.assetListSearchForm.getAsset(ProfileMetaData.SearchByField.CHANNEL).setValue(channelType.getName());
        ProfilePage.buttonAddNewProfile.click();
    }

    @Override
    public void search(TestData td) {
        ProfilePage.search(td);
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.SECURITY.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.SECURITY_PROFILE.get());
    }

    @Override
    public void navigateToFlow() {
        NavigationPage.toFlow(CSAAApplicationFactory.get().adminApp(), NavigationEnum.AdminAppLeftMenu.SECURITY_PROFILE.getFlow());
    }
}
