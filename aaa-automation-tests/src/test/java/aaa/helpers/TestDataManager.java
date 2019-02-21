/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.helpers;

import java.io.File;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import aaa.admin.modules.agencyvendor.AgencyVendorType;
import aaa.admin.modules.cem.campaigns.CampaignType;
import aaa.admin.modules.cem.groupsinformation.GroupInformationType;
import aaa.admin.modules.cem.majorlargeaccount.MajorLargeAccountType;
import aaa.admin.modules.commission.bulkadjustment.BulkAdjustmentType;
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
import aaa.main.modules.billing.paymentsmaintenance.PaymentsMaintenanceType;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.bct.BctType;
import toolkit.datax.DataFormat;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public final class TestDataManager {

	public Map<PolicyType, TestData> policy = new HashMap<>();
	public EnumMap<CustomerType, TestData> customer = new EnumMap<>(CustomerType.class);
	public EnumMap<AccountType, TestData> account = new EnumMap<>(AccountType.class);
	public TestData billingAccount = new SimpleDataProvider();
	public EnumMap<PaymentsMaintenanceType, TestData> paymentMaintenance = new EnumMap<>(PaymentsMaintenanceType.class);
	public TestData myWork = new SimpleDataProvider();
	public EnumMap<AgencyVendorType, TestData> agency = new EnumMap<>(AgencyVendorType.class);
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
	public Map<PolicyType, TestData> timepoint = new HashMap<>();
	public EnumMap<BctType, TestData> bct = new EnumMap<>(BctType.class);
	protected DataProviderFactory dataProvider = new DataProviderFactory().applyConfiguration(DataFormat.YAML.name());
	public TestData loginUsers = new SimpleDataProvider();
	public TestData notesAndAlerts = new SimpleDataProvider();
	public TestData efolder = new SimpleDataProvider();

	public TestDataManager() {
		loginUsers = dataProvider.get("default/login").getTestData("users");

		policy.put(PolicyType.AUTO_SS, dataProvider.get("default/policy/auto_ss"));
		policy.put(PolicyType.AUTO_CA_SELECT, dataProvider.get("default/policy/auto_ca_select"));
		policy.put(PolicyType.AUTO_CA_CHOICE, dataProvider.get("default/policy/auto_ca_choice"));
		policy.put(PolicyType.HOME_CA_HO3, dataProvider.get("default/policy/home_ca_ho3"));
		policy.put(PolicyType.HOME_CA_HO4, dataProvider.get("default/policy/home_ca_ho4"));
		policy.put(PolicyType.HOME_CA_HO6, dataProvider.get("default/policy/home_ca_ho6"));
		policy.put(PolicyType.HOME_CA_DP3, dataProvider.get("default/policy/home_ca_dp3"));
		policy.put(PolicyType.HOME_SS_HO3, dataProvider.get("default/policy/home_ss_ho3"));
		policy.put(PolicyType.HOME_SS_HO4, dataProvider.get("default/policy/home_ss_ho4"));
		policy.put(PolicyType.HOME_SS_HO6, dataProvider.get("default/policy/home_ss_ho6"));
		policy.put(PolicyType.HOME_SS_DP3, dataProvider.get("default/policy/home_ss_dp3"));
		policy.put(PolicyType.PUP, dataProvider.get("default/policy/pup"));

		timepoint.put(PolicyType.AUTO_SS, dataProvider.get("default/timepoints").getTestData("auto_ss"));
		timepoint.put(PolicyType.AUTO_CA_SELECT, dataProvider.get("default/timepoints").getTestData("auto_ca_select"));
		timepoint.put(PolicyType.AUTO_CA_CHOICE, dataProvider.get("default/timepoints").getTestData("auto_ca_choice"));
		timepoint.put(PolicyType.HOME_CA_HO3, dataProvider.get("default/timepoints").getTestData("home_ca"));
		timepoint.put(PolicyType.HOME_CA_DP3, dataProvider.get("default/timepoints").getTestData("home_ca"));
		timepoint.put(PolicyType.HOME_CA_HO3, dataProvider.get("default/timepoints").getTestData("home_ca"));
		timepoint.put(PolicyType.HOME_CA_HO4, dataProvider.get("default/timepoints").getTestData("home_ca"));
		timepoint.put(PolicyType.HOME_CA_HO6, dataProvider.get("default/timepoints").getTestData("home_ca"));
		timepoint.put(PolicyType.HOME_SS_HO3, dataProvider.get("default/timepoints").getTestData("home_ss"));
		timepoint.put(PolicyType.HOME_SS_HO4, dataProvider.get("default/timepoints").getTestData("home_ss"));
		timepoint.put(PolicyType.HOME_SS_HO6, dataProvider.get("default/timepoints").getTestData("home_ss"));
		timepoint.put(PolicyType.HOME_SS_DP3, dataProvider.get("default/timepoints").getTestData("home_ss"));
		timepoint.put(PolicyType.PUP, dataProvider.get("default/timepoints").getTestData("pup"));

		customer.put(CustomerType.INDIVIDUAL, dataProvider.get("default/cem/customer/individual"));
		customer.put(CustomerType.NON_INDIVIDUAL, dataProvider.get("default/cem/customer/nonindividual"));

		account.put(AccountType.INDIVIDUAL, dataProvider.get("default/cem/account/individual"));
		account.put(AccountType.NON_INDIVIDUAL, dataProvider.get("default/cem/account/nonindividual"));

		billingAccount = dataProvider.get("default/billing");
		paymentMaintenance.put(PaymentsMaintenanceType.PAYMENTS_MAINTENANCE, dataProvider.get("default/billing/billingaccount/paymentsmaintenance"));
		myWork = dataProvider.get("default/mywork");

		agency.put(AgencyVendorType.AGENCY, dataProvider.get("default/platform/admin/agencyvendor/agency"));
		agency.put(AgencyVendorType.VENDOR, dataProvider.get("default/platform/admin/agencyvendor/vendor"));
		agency.put(AgencyVendorType.BRAND, dataProvider.get("default/platform/admin/agencyvendor/brand"));

		bulkAdjustment.put(BulkAdjustmentType.BULK_ADJUSTMENT, dataProvider.get("default/commission/bulkadjustment"));

		productFactory.put(ProductFactoryType.POLICY, dataProvider.get("default/productfactory/policy"));

		numberRange.put(NumberRangeType.NUMBER_RANGE, dataProvider.get("default/platform/admin/general/numberrange"));

		note.put(NoteType.NOTE, dataProvider.get("default/platform/admin/general/note"));

		profiles.put(ProfileType.AGENCY, dataProvider.get("default/platform/admin/security/profile/agency"));
		profiles.put(ProfileType.CORPORATE, dataProvider.get("default/platform/admin/security/profile/corporate"));
		profiles.put(ProfileType.VENDOR, dataProvider.get("default/platform/admin/security/profile/vendor"));

		securityRole.put(RoleType.CORPORATE, dataProvider.get("default/platform/admin/security/role/corporate"));
		par.put(PARType.PRODUCT_ACCESS_ROLE, dataProvider.get("default/platform/admin/security/par"));

		campaign.put(CampaignType.CAMPAIGNS, dataProvider.get("default/cem/cem/campaigns"));
		majorLargeAccount.put(MajorLargeAccountType.MAJOR_LARGE_ACCOUNT, dataProvider.get("default/cem/cem/majorlargeaccount"));
		groupsInformation.put(GroupInformationType.GROUPS_INFORMATION, dataProvider.get("default/cem/cem/groupsinformation"));

		task.put(TaskType.PROCESS_MANAGEMENT, dataProvider.get("default/platform/admin/workflow/processmanagement"));

		taxRegistry.put(TaxRegistryType.TAX, dataProvider.get("default/taxesfees/registry/tax"));
		feeRegistry.put(FeeRegistryType.FEE, dataProvider.get("default/taxesfees/registry/fee"));
		taxStrategy.put(TaxStrategyType.TAX, dataProvider.get("default/taxesfees/strategy/tax"));
		feeStrategy.put(FeeStrategyType.FEE, dataProvider.get("default/taxesfees/strategy/fee"));
		feeGroup.put(FeeGroupType.FEE_GROUP, dataProvider.get("default/taxesfees/feegroup"));

		operationalReports.put(OperationalReportType.OPERATIONAL_REPORT, dataProvider.get("modules/regression/finance/operational_reports"));
		templates.put(TemplateType.TEMPLATE, dataProvider.get("default/platform/admin/reports/templates"));
		bct.put(BctType.BATCH_TEST, dataProvider.get("default/bct").getTestData(BctType.BATCH_TEST.getName()));
		bct.put(BctType.ONLINE_TEST, dataProvider.get("default/bct").getTestData(BctType.ONLINE_TEST.getName()));
		
		notesAndAlerts = dataProvider.get("default/notesandalerts");
		efolder = dataProvider.get("default/efolder");
	}

	public TestData getCompatibilityTestData() {
		return dataProvider.get("", "TestData");
	}

	public TestData getDefault(Class<?> klass) {
		List<String> path = Arrays.asList(klass.getName().split("\\."));
		return dataProvider.get(StringUtils.join(path.subList(0, path.size() - 1), File.separator).replace("aaa", ""), path.get(path.size() - 1));
	}
}
