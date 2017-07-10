/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.modules.policy;

import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.AutoCaPolicy;
import aaa.main.modules.policy.auto_ss.AutoSSPolicy;
import aaa.main.modules.policy.cea.CeaPolicy;
import aaa.main.modules.policy.home_ca.HomeCaPolicy;
import aaa.main.modules.policy.home_ss.HomeSSPolicy;
import aaa.main.modules.policy.pup.PupPolicy;
import aaa.rest.policy.PolicyRest;
import aaa.rest.policy.personallines.PersonalLinesPolicyRest;

/**
 * Single-root product (actually, entity) enum/factory.
 * 
 * @category Generated
 */
public class PolicyType {

	protected IPolicy policy;
	protected String shortName;
	protected String fullName;
	protected PolicyRest policyRest;

	public PolicyType(String shortName, String fullName, IPolicy policy) {
		this.shortName = shortName;
		this.fullName = fullName;
		this.policy = policy;
		policyRest = new PersonalLinesPolicyRest(this);
	}

	public static final PolicyType AUTO_CA = new PolicyType("AutoCA", "California Auto", new AutoCaPolicy());
	public static final PolicyType AUTO_CA_CHOICE = new PolicyType("AutoCAC", "California Auto", new AutoCaPolicy());
	public static final PolicyType AUTO_SS = new PolicyType("AutoSS", "Auto Signature Series", new AutoSSPolicy());
	public static final PolicyType HOME_SS = new PolicyType("HomeSS", "Homeowners Signature Series", new HomeSSPolicy());
	public static final PolicyType HOME_SS_HO4 = new PolicyType("HomeSS_HO4", "Homeowners Signature Series", new HomeSSPolicy());
	public static final PolicyType HOME_SS_HO6 = new PolicyType("HomeSS_HO6", "Homeowners Signature Series", new HomeSSPolicy());
	public static final PolicyType HOME_SS_DP3 = new PolicyType("HomeSS_DP3", "Homeowners Signature Series", new HomeSSPolicy());
	public static final PolicyType HOME_CA = new PolicyType("HomeCA", "California Homeowners", new HomeCaPolicy());
	public static final PolicyType HOME_CA_HO4 = new PolicyType("HOME_CA_HO4", "California Homeowners", new HomeCaPolicy());
	public static final PolicyType HOME_CA_HO6 = new PolicyType("HOME_CA_HO6", "California Homeowners", new HomeCaPolicy());
	public static final PolicyType HOME_CA_DP3 = new PolicyType("HOME_CA_DP3", "California Homeowners", new HomeCaPolicy());
	public static final PolicyType PUP = new PolicyType("PUP", "Personal Umbrella Policy", new PupPolicy());
	public static final PolicyType CEA = new PolicyType("CEA", "California Earthquake", new CeaPolicy());

	public String getName() {
		return fullName;
	}

	public String getShortName() {
		return shortName;
	}

	public String getKey() {
		return policy.getClass().getSimpleName();
	}

	public IPolicy get() {
		return policy;
	}

	public PolicyRest getPolicyRest() {
		return policyRest;
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject == null) {
			return false;
		}
		if (!PolicyType.class.isAssignableFrom(anObject.getClass())) {
			return false;
		}
		if (!(anObject instanceof PolicyType)) {
			return false;
		}
		PolicyType policyType = (PolicyType) anObject;
		if ((this.shortName == null) ? (policyType.getShortName() != null) : !this.shortName.equals(policyType.getShortName())) {
			return false;
		}
		if ((this.fullName == null) ? (policyType.getName() != null) : !this.fullName.equals(policyType.getName())) {
			return false;
		}
		return true;
	}
}
