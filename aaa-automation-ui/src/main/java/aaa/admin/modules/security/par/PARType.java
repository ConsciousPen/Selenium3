/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.admin.modules.security.par;

public enum PARType {
    PRODUCT_ACCESS_ROLE("Product Access Role", new PAR());

    private String type;
    private IPAR entity;

    PARType(String t, IPAR e) {
        type = t;
        entity = e;
    }

    public IPAR get() {
        return entity;
    }

    public String getName() {
        return type;
    }

    public String getKey() {
        return entity.getClass().getSimpleName();
    }
}
