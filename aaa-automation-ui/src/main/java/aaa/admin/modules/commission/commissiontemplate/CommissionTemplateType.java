/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissiontemplate;

public enum CommissionTemplateType {

    COMMISSION_TEMPLATE_HEAP("Heap", new CommissionTemplate("Heap")),
    COMMISSION_TEMPLATE_GRADED("Graded", new CommissionTemplate("Graded"));

    private String templateType;
    private ICommissionTemplate template;

    CommissionTemplateType(String templateType, ICommissionTemplate template) {
        this.templateType = templateType;
        this.template = template;
    }

    public ICommissionTemplate get() {
        return template;
    }

    public String getName() {
        return templateType;
    }

    public String getKey() {
        return template.getClass().getSimpleName();
    }
}
