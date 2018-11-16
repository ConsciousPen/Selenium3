/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.common.enums;

public class PrivilegeEnum {

	public enum Privilege {
        A30("A30"),
        C32("C32"),
        L41("L41"),
        F35("F35");

        String privilegeName;

        Privilege(String privilegeName) {
            this.privilegeName = privilegeName;
        }

        public String getName() {
            return privilegeName;
        }
    }
}
