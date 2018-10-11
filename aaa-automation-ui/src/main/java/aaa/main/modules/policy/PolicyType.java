/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy;

import aaa.main.modules.policy.auto_ca.AutoCaPolicy;
import aaa.main.modules.policy.auto_ss.AutoSSPolicy;
import aaa.main.modules.policy.home_ca.HomeCaPolicy;
import aaa.main.modules.policy.home_ss.HomeSSPolicy;
import aaa.main.modules.policy.pup.PupPolicy;

/**
 * Single-root product (actually, entity) enum/factory.
 *
 * @category Generated
 */
public class PolicyType {

	public static final PolicyType AUTO_CA_SELECT = new PolicyType("AutoCA", "California Auto", true, true, new AutoCaPolicy());
	public static final PolicyType AUTO_CA_CHOICE = new PolicyType("AutoCAC", "California Auto", true, true, new AutoCaPolicy());
	public static final PolicyType AUTO_SS = new PolicyType("AutoSS", "Auto Signature Series", true, false, new AutoSSPolicy());
	public static final PolicyType HOME_SS_HO3 = new PolicyType("HomeSS_HO3", "Homeowners Signature Series", false, false, new HomeSSPolicy());
	public static final PolicyType HOME_SS_HO4 = new PolicyType("HomeSS_HO4", "Homeowners Signature Series", false, false, new HomeSSPolicy());
	public static final PolicyType HOME_SS_HO6 = new PolicyType("HomeSS_HO6", "Homeowners Signature Series", false, false, new HomeSSPolicy());
	public static final PolicyType HOME_SS_DP3 = new PolicyType("HomeSS_DP3", "Homeowners Signature Series", false, false, new HomeSSPolicy());
	public static final PolicyType HOME_CA_HO3 = new PolicyType("HomeCA_HO3", "California Homeowners", false, true, new HomeCaPolicy());
	public static final PolicyType HOME_CA_HO4 = new PolicyType("HomeCA_HO4", "California Homeowners", false, true, new HomeCaPolicy());
	public static final PolicyType HOME_CA_HO6 = new PolicyType("HomeCA_HO6", "California Homeowners", false, true, new HomeCaPolicy());
	public static final PolicyType HOME_CA_DP3 = new PolicyType("HomeCA_DP3", "California Homeowners", false, true, new HomeCaPolicy());
	public static final PolicyType PUP = new PolicyType("PUP", "Personal Umbrella Policy", false, false, new PupPolicy());
	protected IPolicy policy;
	protected String shortName;
	protected String fullName;
	protected boolean isAutoPolicy;
	protected boolean isCaProduct;

	public PolicyType(String shortName, String fullName, boolean isAutoPolicy, boolean isCaProduct, IPolicy policy) {
		this.shortName = shortName;
		this.fullName = fullName;
		this.isAutoPolicy = isAutoPolicy;
		this.isCaProduct = isCaProduct;
		this.policy = policy;
	}

	public String getName() {
		return fullName;
	}

	public String getShortName() {
		return shortName;
	}

	public String getKey() {
		return policy.getClass().getSimpleName();
	}

	public boolean isAutoPolicy() {
		return isAutoPolicy;
	}

	public boolean isCaProduct() {
		return isCaProduct;
	}

	public IPolicy get() {
		return policy;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PolicyType thatObject = (PolicyType) o;

		return shortName.equals(thatObject.shortName) && fullName.equals(thatObject.fullName);
	}

	@Override
	public int hashCode() {
		int result = shortName.hashCode();
		result = 31 * result + fullName.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return getShortName();
	}
}
