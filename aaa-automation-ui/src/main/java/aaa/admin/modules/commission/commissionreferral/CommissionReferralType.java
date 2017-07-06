/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionreferral;

public enum CommissionReferralType {

    COMMISSION_REFERRAL("Commission Referral", new CommissionReferral());

    private String referralType;
    private ICommissionReferral referral;

    CommissionReferralType(String referralType, ICommissionReferral referral) {
        this.referralType = referralType;
        this.referral = referral;
    }

    public ICommissionReferral get() {
        return referral;
    }

    public String getName() {
        return referralType;
    }

    public String getKey() {
        return referral.getClass().getSimpleName();
    }
}
