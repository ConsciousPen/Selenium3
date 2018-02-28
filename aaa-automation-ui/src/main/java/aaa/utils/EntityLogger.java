/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.utils;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.admin.metadata.agencyvendor.BrandMetaData;
import aaa.admin.metadata.security.PARMetaData;
import aaa.admin.modules.cem.campaigns.defaulttabs.CreateCampaignTab;
import aaa.admin.modules.cem.groupsinformation.defaulttabs.CreateGroupInformationTab;
import aaa.admin.modules.cem.majorlargeaccount.defaulttabs.CreateMajorLargeAccountTab;
import aaa.admin.modules.commission.bulkadjustment.defaulttabs.AddBulkAdjustmentRuleTab;
import aaa.admin.modules.commission.bulkadjustment.defaulttabs.AddBulkAdjustmentTab;
import aaa.admin.modules.commission.commissiongroup.defaulttabs.AddCommissionGroupTab;
import aaa.admin.modules.general.note.defaulttabs.AddNoteCategoryTab;
import aaa.admin.modules.general.numberrange.defaulttabs.AddNumberRangeTab;
import aaa.admin.modules.security.par.defaulttabs.GeneralPARTab;
import aaa.admin.modules.security.role.defaulttabs.GeneralRoleTab;
import aaa.admin.modules.taxesfees.registry.tax.defaulttabs.AddFeeTaxRegistryTab;
import aaa.admin.modules.taxesfees.strategy.tax.defaulttabs.AddTaxStrategyTab;
import aaa.admin.modules.workflow.processmanagement.defaulttabs.CreateManualTaskDefinitionTab;
import aaa.admin.pages.agencyvendor.BrandPage;
import aaa.admin.pages.commission.CommissionPage;
import aaa.admin.pages.taxesfees.TaxesFeesPage;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.TextBox;

public abstract class EntityLogger {

    protected static Logger log = LoggerFactory.getLogger(EntityLogger.class);

    public enum EntityType {
        AGENCY_VENDOR,
        AGENCY_NAME,
        BILLING_ACCOUNT,
        BRAND,
        BRAND_TYPE,
        BULK_ADJUSTMENT,
        CASEPROFILE,
        CAMPAIGN,
        CLAIM,
        CLAIM_TERM_LIFE,
        COMMISSION_BONUS,
        COMMISSION_GROUP,
        CUSTOMER,
        FEE_GROUP,
        GROUPS_INFORMATION,
        MAJOR_LARGE_ACCOUNT,
        NOTE,
        NUMBER_RANGE,
        POLICY,
        POLICY_CERTIFICATE,
        PROFILE,
        QUOTE,
        ROLE,
        PAR,
        TASK,
        TAX_FEE_REGISTRY,
        TAX_FEE_STRATEGY
    }

    public static String getEntityHeader(EntityType quote) {
        String content = "";
        String entityId = "";
        String entityType = "";
        String[] headers, values;

        try {
            switch (quote) {

                case CUSTOMER:
                    content = new StaticElement(By.id("custInfoForm:grid")).getValue().replaceAll(":\n", ": ").replaceAll("\n", "; ");
                    entityId = StringUtils.substringAfter(content, "Customer ID: ").split(";")[0];

                    content = "Customer #" + entityId + " (" + StringUtils.replaceEach(content.replace("Customer ID: " + entityId, "").trim(),
                            new String[] {"; ;", "  ", ";)"}, new String[] {";", " ", ")"}) + ")";
                    break;

                case CLAIM_TERM_LIFE:
                    content = new StaticElement(By.xpath("//div[@id='producContextInfoForm:header_panel']//div[@class='header']/div")).getValue() + " ("
                            + new StaticElement(By.xpath("//span[@id='producContextInfoForm:headerColumn_0']/div[2]")).getValue() + ")";
                    content = content.replace("\n", "").replace("Claim:", "").trim();
                    break;

                case POLICY:
                    content = new StaticElement(By.id("productContextInfoForm:grid")).getValue().replaceAll("\n", "; ");
                    entityId = StringUtils.substringAfter(content, "Policy #: ").split(";")[0];
                    entityType = StringUtils.substringAfter(content, "Product Name: ").split(";")[0];

                    content = "#" + entityId + " (" + "Product Name: " + entityType + "; " + StringUtils.replaceEach(
                            StringUtils.replaceEach(content, new String[] {"Policy #: " + entityId, "Product Name: " + entityType}, new String[] {"", ""}).trim() + ")",
                            new String[] {"; ;", ";)"}, new String[] {";", ")"});
                    break;

                case POLICY_CERTIFICATE:
                    content = new StaticElement(By.id("productContextInfoForm:grid")).getValue().replaceAll("\n", "; ");
                    entityId = StringUtils.substringAfter(content, "Certificate Policy #: ").split(";")[0];
                    entityType = StringUtils.substringAfter(content, "Product Name: ").split(";")[0];

                    content = "#" + entityId + " (" + "Product Name: " + entityType + "; " + StringUtils.replaceEach(
                            StringUtils.replaceEach(content, new String[] {"Certificate Policy #: " + entityId, "Product Name: " + entityType}, new String[] {"", ""}).trim() + ")",
                            new String[] {"; ;", ";)"}, new String[] {";", ")"});
                    break;

                case QUOTE:
                    content = new StaticElement(By.id("productContextInfoForm:grid")).getValue().replaceAll("\n", "; ");
                    entityId = StringUtils.substringAfter(content, "Quote #: ").split(";")[0];
                    entityType = StringUtils.substringAfter(content, "Product Name: ").split(";")[0];

                    content = "#" + entityId + " (" + "Product Name: " + entityType + "; " + StringUtils.replaceEach(
                            StringUtils.replaceEach(content, new String[] {"Policy #: " + entityId, "Product Name: " + entityType}, new String[] {"", ""}).trim() + ")",
                            new String[] {"; ;", ";)"}, new String[] {";", ")"});
                    break;

                case AGENCY_VENDOR:
                    content = "(" + new StaticElement(By.xpath("//div[@id='contents']//table//table//tr[.//span[@id='nameLabel']][1]")).getValue().replace("Agency Code:", "; Agency Code:")
                            .replace(" ; ", "; ") + ")";
                    break;

                case AGENCY_NAME:
                    content = new StaticElement(By.xpath("//div[@id='contents']//table//table//span[@id='nameText']")).getValue();
                    break;

                case CAMPAIGN:
                    content = "(" + trim(new CreateCampaignTab().getAssetList().getValue().toString()) + ")";
                    break;

                case GROUPS_INFORMATION:
                    content = "(" + trim(new CreateGroupInformationTab().getAssetList().getValue().toString()) + ")";
                    break;

                case TASK:
                    content = "(" + trim(new CreateManualTaskDefinitionTab().getAssetList().getValue().toString()) + ")";
                    break;

                case NUMBER_RANGE:
                    content = "(" + trim(new AddNumberRangeTab().getAssetList().getValue().toString()) + ")";
                    break;

                case NOTE:
                    content = "(" + trim(new AddNoteCategoryTab().getAssetList().getValue().toString()) + ")";
                    break;

                case ROLE:
                    content = "(" + trim(new GeneralRoleTab().getAssetList().getValue().toString()) + ")";
                    break;

                case PAR:
                    content = "(RoleCode = " + new GeneralPARTab().getAssetList().getAsset(PARMetaData.GeneralPARTab.ROLE_CODE).getValue() + "; " +
                            "RoleName = " + new GeneralPARTab().getAssetList().getAsset(PARMetaData.GeneralPARTab.ROLE_NAME).getValue() + ")";
                    break;

                case TAX_FEE_REGISTRY:
                    content = "(" + trim(new AddFeeTaxRegistryTab().getAssetList().getValue().toString()) + ")";
                    break;

                case TAX_FEE_STRATEGY:
                    headers = Arrays.copyOf(TaxesFeesPage.tableRules.getHeader().getValue().toArray(),
                            TaxesFeesPage.tableRules.getHeader().getValue().toArray().length, String[].class);
                    values = Arrays.copyOf(TaxesFeesPage.tableRules.getRow(1).getValue().toArray(),
                            TaxesFeesPage.tableRules.getRow(1).getValue().toArray().length, String[].class);
                    content = "(" + joinHeaders(headers, values) + "," + trim(new AddTaxStrategyTab().getAssetList().getValue().toString()) + ")";
                    break;

                case BRAND:
                    content = "(Brand Code = " + BrandPage.assetListAddBrand.getAsset(BrandMetaData.BrandTab.AddBrandDialog.BRAND_CODE.getLabel(), TextBox.class).getValue() + "; "
                            + "Brand Name = " + BrandPage.assetListAddBrand.getAsset(BrandMetaData.BrandTab.AddBrandDialog.BRAND_NAME.getLabel(), TextBox.class).getValue() + ")";
                    break;

                case BRAND_TYPE:
                    content = "(Brand Type Code = " + BrandPage.assetListAddBrandType.getAsset(BrandMetaData.BrandTab.AddBrandTypeDialog.BRAND_TYPE_CODE.getLabel(), TextBox.class).getValue() + "; "
                            + "Brand Type Name = " + BrandPage.assetListAddBrandType.getAsset(BrandMetaData.BrandTab.AddBrandTypeDialog.BRAND_TYPE_NAME.getLabel(), TextBox.class).getValue() + ")";
                    break;

                case MAJOR_LARGE_ACCOUNT:
                    content = "(" + trim(new CreateMajorLargeAccountTab().getAssetList().getValue().toString()) + ")";
                    break;

                case BULK_ADJUSTMENT:
                    content = "(" + trim(new AddBulkAdjustmentTab().getAssetList().getValue().toString()) + ", "
                            + trim(new AddBulkAdjustmentRuleTab().getAssetList().getValue().toString()) + ")";
                    break;

                case COMMISSION_BONUS:
                    CommissionPage.buttonSearch.click();
                    headers = Arrays.copyOf(CommissionPage.tableCommissionBonus.getHeader().getValue().toArray(),
                            CommissionPage.tableCommissionBonus.getHeader().getValue().toArray().length, String[].class);
                    values = Arrays.copyOf(CommissionPage.tableCommissionBonus.getRow(1).getValue().toArray(),
                            CommissionPage.tableCommissionBonus.getRow(1).getValue().toArray().length, String[].class);
                    content = "(" + joinHeaders(headers, values) + ")";
                    break;

                case COMMISSION_GROUP:
                    content = "(" + trim(new AddCommissionGroupTab().getAssetList().getValue().toString());
                    break;

                default:
                    break;
            }
        }

        catch (Exception e) {

        }
        return content;
    }

    private static String joinHeaders(String[] headers, String[] values) {
        String content = "";
        for (int i = 0; i <= headers.length - 2; i++) {
            if (!values[i].equals("")) {
                content += headers[i] + " = " + values[i] + ", ";
            }
        }
        return content.substring(0, content.length() - 2);
    }

    private static String trim(String values) {
        String content = "";
        values = values.replace("{", "").replace("}", "");
        for (int i = 0; i <= values.split(",").length - 1; i++) {
            if (!values.split(",")[i].equals("")) {
                if (values.split(",")[i].split("=").length > 1) {
                    content += values.split(",")[i] + ", ";
                }
            }
        }
        return content.substring(0, content.length() - 2);
    }
}
