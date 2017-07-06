/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.main.modules.mywork;

public enum MyWorkType {
    MY_WORK("My Work", new MyWork());

    private String name;
    private IMyWork myWork;

    MyWorkType(String name, IMyWork myWork) {
        this.myWork = myWork;
        this.name = name;
    }

    public IMyWork get() {
        return myWork;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return myWork.getClass().getSimpleName();
    }
}
