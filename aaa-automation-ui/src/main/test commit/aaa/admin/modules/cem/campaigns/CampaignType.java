/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.cem.campaigns;

public enum CampaignType {
    CAMPAIGNS("Campaigns", new Campaign());

    private String type;
    private ICampaign entity;

    CampaignType(String t, ICampaign e) {
        type = t;
        entity = e;
    }

    public ICampaign get() {
        return entity;
    }

    public String getName() {
        return type;
    }

    public String getKey() {
        return entity.getClass().getSimpleName();
    }
}
