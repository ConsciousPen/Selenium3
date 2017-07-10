/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers;

import java.io.File;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import aaa.admin.modules.agencyvendor.AgencyVendorType;
import aaa.admin.modules.cem.campaigns.CampaignType;
import aaa.admin.modules.cem.groupsinformation.GroupInformationType;
import aaa.admin.modules.cem.majorlargeaccount.MajorLargeAccountType;
import aaa.admin.modules.commission.bulkadjustment.BulkAdjustmentType;
import aaa.admin.modules.commission.commissionbonus.CommissionBonusType;
import aaa.admin.modules.commission.commissiongroup.CommissionGroupType;
import aaa.admin.modules.commission.commissionreferral.CommissionReferralType;
import aaa.admin.modules.commission.commissionstrategy.CommissionStrategyType;
import aaa.admin.modules.general.note.NoteType;
import aaa.admin.modules.general.numberrange.NumberRangeType;
import aaa.admin.modules.product.productfactory.ProductFactoryType;
import aaa.admin.modules.reports.operationalreports.OperationalReportType;
import aaa.admin.modules.reports.templates.TemplateType;
import aaa.admin.modules.security.par.PARType;
import aaa.admin.modules.security.profile.ProfileType;
import aaa.admin.modules.security.role.RoleType;
import aaa.admin.modules.taxesfees.feegroup.FeeGroupType;
import aaa.admin.modules.taxesfees.registry.fee.FeeRegistryType;
import aaa.admin.modules.taxesfees.registry.tax.TaxRegistryType;
import aaa.admin.modules.taxesfees.strategy.fee.FeeStrategyType;
import aaa.admin.modules.taxesfees.strategy.tax.TaxStrategyType;
import aaa.admin.modules.workflow.processmanagement.TaskType;
import aaa.main.modules.account.AccountType;
import aaa.main.modules.billing.account.BillingAccountType;
import aaa.main.modules.billing.paymentsmaintenance.PaymentsMaintenanceType;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.mywork.MyWorkType;
import aaa.main.modules.policy.PolicyType;
import aaa.rest.RESTServiceType;
import toolkit.datax.DataFormat;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public final class TestDataManager {

	public enum CommercialLinesLobType {
		CL, CL_AUTO, CL_GL, CL_PROPERTY, CL_IM
	}

	protected DataProviderFactory dataProvider = new DataProviderFactory().applyConfiguration(DataFormat.YAML.name());

	public Map<PolicyType, TestData> policy = new HashMap<PolicyType, TestData>();
	public EnumMap<CustomerType, TestData> customer = new EnumMap<>(CustomerType.class);
	public EnumMap<AccountType, TestData> account = new EnumMap<>(AccountType.class);
	public EnumMap<BillingAccountType, TestData> billingAccount = new EnumMap<>(BillingAccountType.class);
	public EnumMap<PaymentsMaintenanceType, TestData> paymentMaintenance = new EnumMap<>(PaymentsMaintenanceType.class);
	public EnumMap<MyWorkType, TestData> myWork = new EnumMap<>(MyWorkType.class);

	public EnumMap<AgencyVendorType, TestData> agency = new EnumMap<>(AgencyVendorType.class);
	public EnumMap<CommissionBonusType, TestData> commissionBonus = new EnumMap<>(CommissionBonusType.class);
	public EnumMap<CommissionReferralType, TestData> commissionReferral = new EnumMap<>(CommissionReferralType.class);
	public EnumMap<CommissionGroupType, TestData> commissionGroup = new EnumMap<>(CommissionGroupType.class);
	public EnumMap<CommissionStrategyType, TestData> commissionStrategy = new EnumMap<>(CommissionStrategyType.class);
	public EnumMap<BulkAdjustmentType, TestData> bulkAdjustment = new EnumMap<>(BulkAdjustmentType.class);
	public EnumMap<NumberRangeType, TestData> numberRange = new EnumMap<>(NumberRangeType.class);
	public EnumMap<NoteType, TestData> note = new EnumMap<>(NoteType.class);
	public EnumMap<TaskType, TestData> task = new EnumMap<>(TaskType.class);
	public EnumMap<ProductFactoryType, TestData> productFactory = new EnumMap<>(ProductFactoryType.class);
	public EnumMap<OperationalReportType, TestData> operationalReports = new EnumMap<>(OperationalReportType.class);
	public EnumMap<TemplateType, TestData> templates = new EnumMap<>(TemplateType.class);

	public EnumMap<CampaignType, TestData> campaign = new EnumMap<>(CampaignType.class);
	public EnumMap<MajorLargeAccountType, TestData> majorLargeAccount = new EnumMap<>(MajorLargeAccountType.class);
	public EnumMap<GroupInformationType, TestData> groupsInformation = new EnumMap<>(GroupInformationType.class);

	public EnumMap<RoleType, TestData> securityRole = new EnumMap<>(RoleType.class);
	public EnumMap<ProfileType, TestData> profiles = new EnumMap<>(ProfileType.class);
	public EnumMap<PARType, TestData> par = new EnumMap<>(PARType.class);

	public EnumMap<FeeRegistryType, TestData> feeRegistry = new EnumMap<>(FeeRegistryType.class);
	public EnumMap<TaxRegistryType, TestData> taxRegistry = new EnumMap<>(TaxRegistryType.class);
	public EnumMap<FeeStrategyType, TestData> feeStrategy = new EnumMap<>(FeeStrategyType.class);
	public EnumMap<TaxStrategyType, TestData> taxStrategy = new EnumMap<>(TaxStrategyType.class);
	public EnumMap<FeeGroupType, TestData> feeGroup = new EnumMap<>(FeeGroupType.class);

	public EnumMap<RESTServiceType, TestData> rest = new EnumMap<>(RESTServiceType.class);

	public TestDataManager() {
		policy.put(PolicyType.AUTO_SS, dataProvider.get("modules/policy/auto_ss"));
		policy.put(PolicyType.AUTO_CA, dataProvider.get("modules/policy/auto_ca"));
		policy.put(PolicyType.AUTO_CA_CHOICE, dataProvider.get("modules/policy/auto_ca_choice"));
		policy.put(PolicyType.HOME_CA, dataProvider.get("modules/policy/home_ca"));
		policy.put(PolicyType.HOME_CA_HO4, dataProvider.get("modules/policy/home_ca_ho4"));
		policy.put(PolicyType.HOME_CA_HO6, dataProvider.get("modules/policy/home_ca_ho6"));
		policy.put(PolicyType.HOME_CA_DP3, dataProvider.get("modules/policy/home_ca_dp3"));
		policy.put(PolicyType.HOME_SS, dataProvider.get("modules/policy/home_ss"));
		policy.put(PolicyType.HOME_SS_HO4, dataProvider.get("modules/policy/home_ss_ho4"));
		policy.put(PolicyType.HOME_SS_HO6, dataProvider.get("modules/policy/home_ss_ho6"));
		policy.put(PolicyType.HOME_SS_DP3, dataProvider.get("modules/policy/home_ss_dp3"));
		policy.put(PolicyType.PUP, dataProvider.get("modules/policy/pup"));
		policy.put(PolicyType.CEA, dataProvider.get("modules/policy/cea"));

		customer.put(CustomerType.INDIVIDUAL, dataProvider.get("modules/cem/customer/individual"));
		customer.put(CustomerType.NON_INDIVIDUAL, dataProvider.get("modules/cem/customer/nonindividual"));

		account.put(AccountType.INDIVIDUAL, dataProvider.get("modules/cem/account/individual"));
		account.put(AccountType.NON_INDIVIDUAL, dataProvider.get("modules/cem/account/nonindividual"));

		billingAccount.put(BillingAccountType.BILLING_ACCOUNT, dataProvider.get("modules/billing"));
		paymentMaintenance.put(PaymentsMaintenanceType.PAYMENTS_MAINTENANCE, dataProvider.get("modules/billing/billingaccount/paymentsmaintenance"));
		myWork.put(MyWorkType.MY_WORK, dataProvider.get("modules/platform/mywork"));

		agency.put(AgencyVendorType.AGENCY, dataProvider.get("modules/platform/admin/agencyvendor/agency"));
		agency.put(AgencyVendorType.VENDOR, dataProvider.get("modules/platform/admin/agencyvendor/vendor"));
		agency.put(AgencyVendorType.BRAND, dataProvider.get("modules/platform/admin/agencyvendor/brand"));

		rest.put(RESTServiceType.NOTES, dataProvider.get("modules/platform/notes"));
		rest.put(RESTServiceType.BPM, dataProvider.get("modules/platform/mywork"));
		rest.put(RESTServiceType.PARTY_SEARCH, dataProvider.get("modules/party/partysearch"));
		rest.put(RESTServiceType.CUSTOMERS, dataProvider.get("modules/cem/customer"));
		rest.put(RESTServiceType.BILLING, dataProvider.get("modules/billing"));

		commissionGroup.put(CommissionGroupType.COMMISSION_GROUP, dataProvider.get("modules/commission/commissiongroup"));
		commissionBonus.put(CommissionBonusType.COMMISSION_BONUS, dataProvider.get("modules/commission/commissionbonus"));
		commissionReferral.put(CommissionReferralType.COMMISSION_REFERRAL, dataProvider.get("modules/commission/commissionreferral"));
		bulkAdjustment.put(BulkAdjustmentType.BULK_ADJUSTMENT, dataProvider.get("modules/commission/bulkadjustment"));

		commissionStrategy.put(CommissionStrategyType.PC_COMMISSION_STRATEGY_PREC_HO, dataProvider.get("modules/commission/commissionstrategy/commissionstrategy_pc/prec_ho"));
		commissionStrategy.put(CommissionStrategyType.PC_COMMISSION_STRATEGY_PREC_AU, dataProvider.get("modules/commission/commissionstrategy/commissionstrategy_pc/prec_au"));

		productFactory.put(ProductFactoryType.POLICY, dataProvider.get("modules/productfactory/policy"));

		numberRange.put(NumberRangeType.NUMBER_RANGE, dataProvider.get("modules/platform/admin/general/numberrange"));

		note.put(NoteType.NOTE, dataProvider.get("modules/platform/admin/general/note"));

		profiles.put(ProfileType.AGENCY, dataProvider.get("modules/platform/admin/security/profile/agency"));
		profiles.put(ProfileType.CORPORATE, dataProvider.get("modules/platform/admin/security/profile/corporate"));
		profiles.put(ProfileType.VENDOR, dataProvider.get("modules/platform/admin/security/profile/vendor"));

		securityRole.put(RoleType.CORPORATE, dataProvider.get("modules/platform/admin/security/role/corporate"));
		par.put(PARType.PRODUCT_ACCESS_ROLE, dataProvider.get("modules/platform/admin/security/par"));

		campaign.put(CampaignType.CAMPAIGNS, dataProvider.get("modules/cem/cem/campaigns"));
		majorLargeAccount.put(MajorLargeAccountType.MAJOR_LARGE_ACCOUNT, dataProvider.get("modules/cem/cem/majorlargeaccount"));
		groupsInformation.put(GroupInformationType.GROUPS_INFORMATION, dataProvider.get("modules/cem/cem/groupsinformation"));

		task.put(TaskType.PROCESS_MANAGEMENT, dataProvider.get("modules/platform/admin/workflow/processmanagement"));

		taxRegistry.put(TaxRegistryType.TAX, dataProvider.get("modules/taxesfees/registry/tax"));
		feeRegistry.put(FeeRegistryType.FEE, dataProvider.get("modules/taxesfees/registry/fee"));
		taxStrategy.put(TaxStrategyType.TAX, dataProvider.get("modules/taxesfees/strategy/tax"));
		feeStrategy.put(FeeStrategyType.FEE, dataProvider.get("modules/taxesfees/strategy/fee"));
		feeGroup.put(FeeGroupType.FEE_GROUP, dataProvider.get("modules/taxesfees/feegroup"));

		operationalReports.put(OperationalReportType.OPERATIONAL_REPORT, dataProvider.get("modules/platform/admin/reports/operationalreports"));
		templates.put(TemplateType.TEMPLATE, dataProvider.get("modules/platform/admin/reports/templates"));
	}

	public TestData getDefault(Class<?> klass) {
		List<String> path = Arrays.asList(klass.getName().split("\\."));
		return dataProvider.get(StringUtils.join(path.subList(0, path.size() - 1), File.separator).replace("aaa", ""), path.get(path.size() - 1));
	}

	public TestData getCompatibilityTestData() {
		return dataProvider.get("", "TestData");
	}

}
