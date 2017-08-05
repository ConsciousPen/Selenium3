/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.general.numberrange;

public enum NumberRangeType {

    NUMBER_RANGE("# Range", new NumberRange());

    private String numberRangeType;
    private INumberRange numberRange;

    NumberRangeType(String numberRangeType, INumberRange numberRange) {
        this.numberRangeType = numberRangeType;
        this.numberRange = numberRange;
    }

    public INumberRange get() {
        return numberRange;
    }

    public String getName() {
        return numberRangeType;
    }

    public String getKey() {
        return numberRange.getClass().getSimpleName();
    }

}
