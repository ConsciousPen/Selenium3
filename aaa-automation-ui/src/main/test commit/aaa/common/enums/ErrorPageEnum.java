/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.enums;

public class ErrorPageEnum {

    public enum ErrorsColumn {
        CODE("Code"),
        SEVERITY("Severity"),
        MESSAGE("Message");

        String id;

        ErrorsColumn(String id) {
            this.id = id;
        }

        public String get() {
            return id;
        }
    }
}
