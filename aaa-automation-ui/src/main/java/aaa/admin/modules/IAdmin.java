/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import toolkit.datax.TestData;

public interface IAdmin {
    Logger log = LoggerFactory.getLogger(IAdmin.class);

    void search(TestData td);

    void navigate();
}
