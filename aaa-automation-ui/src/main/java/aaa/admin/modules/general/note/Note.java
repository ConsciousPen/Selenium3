/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.general.note;

import aaa.utils.EntityLogger;
import aaa.admin.modules.general.note.NoteActions.DeleteNoteCategory;
import aaa.admin.modules.general.note.NoteActions.DisableNoteCategory;
import aaa.admin.modules.general.note.defaulttabs.AddNoteCategoryTab;
import aaa.admin.modules.general.note.views.DefaultView;
import aaa.admin.pages.general.NotePage;
import aaa.common.Workspace;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import toolkit.datax.TestData;

public class Note implements INote {
    private Workspace defaultView = new DefaultView();

    @Override
    public Workspace getDefaultView() {
        return defaultView;
    }

    @Override
    public void initiate() {
        navigate();
        NotePage.buttonAddNoteCategory.click();
    }

    @Override
    public void navigate() {
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.GENERAL.get());
        NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_NOTES.get());
    }

    @Override
    public void create(TestData td) {
        initiate();
        getDefaultView().fill(td);
        String entity = EntityLogger.getEntityHeader(EntityLogger.EntityType.NOTE);
        AddNoteCategoryTab.buttonSave.click();
        log.info("Created Note " + entity);
    }

    @Override
    public void search(TestData td) {
        NotePage.search(td);
    }

    @Override
    public DeleteNoteCategory delete() {
        return new NoteActions.DeleteNoteCategory();
    }

    @Override
    public DisableNoteCategory disable() {
        return new NoteActions.DisableNoteCategory();
    }

}
