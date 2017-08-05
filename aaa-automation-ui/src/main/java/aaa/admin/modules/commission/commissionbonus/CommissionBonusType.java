/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.commission.commissionbonus;

public enum CommissionBonusType {

    COMMISSION_BONUS("Commission Bonus", new CommissionBonus());

    private String bonusType;
    private ICommissionBonus bonus;

    CommissionBonusType(String bonusType, ICommissionBonus bonus) {
        this.bonusType = bonusType;
        this.bonus = bonus;
    }

    public ICommissionBonus get() {
        return bonus;
    }

    public String getName() {
        return bonusType;
    }

    public String getKey() {
        return bonus.getClass().getSimpleName();
    }
}
