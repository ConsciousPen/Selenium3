/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.common;

import toolkit.webdriver.controls.composite.assets.metadata.MetaData;

/**
 * Abstract DefaultTab class.
 */
public class DefaultTab extends Tab {

    protected DefaultTab(Class<? extends MetaData> mdClass) {
        super(mdClass);
    }

    @Override
    public Tab submitTab() {
        buttonNext.click();
        return this;
    }
}
