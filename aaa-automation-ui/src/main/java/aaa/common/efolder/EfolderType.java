/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.efolder;

public enum EfolderType {
    EFOLDER("Efolder");

    private String efolderName;


    EfolderType(String efolderName) {
        this.efolderName = efolderName;
    }

    public String getName() {
        return efolderName;
    }
}
