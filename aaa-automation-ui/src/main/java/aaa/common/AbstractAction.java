/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common;

import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.datax.TestData;

/**
 * Abstract entity action class.
 *
 * @category Static
 */
public abstract class AbstractAction {
    protected static Logger log = LoggerFactory.getLogger(AbstractAction.class);

    /**
     * Get action label (for "Select Action" combobox)
     *
     * @return action label
     */
    public abstract String getName();

    /**
     * Get workspace associated with this action
     *
     * @return workspace (aka view)
     */
    public abstract Workspace getView();

    /**
     * Initiate action.
     * Override if action is initiated in non-standard manner.
     */
    public AbstractAction start() {
        log.info(getName() + " action initiated.");
        NavigationPage.setActionAndGo(getName());
        return this;
    }

    /**
     * Fill tabs of associated workspace with provided data and submit if necessary.
     * Override if action is performed in non-standard manner.
     *
     * @param td TestData with data tables for each tab
     */
    public AbstractAction perform(TestData td) {
        start();
        getView().fill(td);
        return submit();
    }

    /**
     * Finalize action (usually by clicking some button).
     * Override if action is finalized in non-standard manner (e.g. custom button is clicked).
     */
    public AbstractAction submit() {
        Tab.buttonOk.click();
        do {
            Page.dialogConfirmation.confirm();
        }
        while (Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible());

        log.info(getName() + " action has been finished.");
        return this;
    }
}
