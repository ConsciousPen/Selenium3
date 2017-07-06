/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.main.pages.summary.claim;

import org.openqa.selenium.By;

import aaa.main.pages.summary.SummaryPage;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.composite.table.Table;

public class ClaimAdjudicationDamagePage extends SummaryPage {

    public static Table tableDamageInfo = new Table(By.id("policyDataGatherForm:body_damageInfoTable_ClaimsDamageView"));
    public static Table tableAllClaimDamages = new Table(By.xpath("//div[@id='productConsolidatedViewForm:scolumn_ClaimsEvaluationConsolidatedDamage']//table"));
    public static Table tableDamageParties = new Table(By.xpath("//div[@id='policyDataGatherForm:damagePartiesInfoTable_ClaimsDamagePartyView']/div//table"));
    public static Table tableVendors = new Table(By.xpath("//div[@id='policyDataGatherForm:vendorsInfoTable_ClaimsVendorConsolidatedView']/div//table"));
    public static Table tableDamageReserves = new Table(By.xpath("//div[@id='policyDataGatherForm:componentInstancesTable_ClaimsDamageReservesView']/div//table"));
    public static Table tableDamageReserveHistory = new Table(By.xpath("//div[@id='policyDataGatherForm:componentInstancesTable_ClaimsDamageTransactionHistory']/div//table"));
    public static Table tableDamageFeatures = new Table(By.xpath("//div[@id='policyDataGatherForm:damageFeaturesInfoTable_ClaimsDamageFeaturesView']//table")).applyConfiguration("NoRecordsFound");
    public static Table tableAllClaimFeatures = new Table(By.xpath("//div[@id='productConsolidatedViewForm:scolumn_EvaluationClaimFeature']//table"));

    public static Link linkDamageUpdate = new Link(By.id("policyDataGatherForm:damageActionLink_updateDamageAction"));
    public static Link linkAddNewDamageVehicle = new Link(By.id("productConsolidatedViewForm:addDamageAutoLoss"));
    public static Link linkAddNewDamageHome = new Link(By.id("productConsolidatedViewForm:addDamagePropertyLoss"));
    public static Link linkAddNewDamageInjury = new Link(By.id("productConsolidatedViewForm:addDamageClaimsInjury"));
    public static Link linkAddNewDamageOtherProperty = new Link(By.id("productConsolidatedViewForm:addDamagePropertyDamage"));
    public static Link linkAddNewFeature = new Link(By.xpath("//*[@id='productConsolidatedViewForm:addFeature' or @id='policyDataGatherForm:damageActionLink_addFeature']"));
    public static Link linkDamageReserves = new Link(By.id("policyDataGatherForm:damageActionLink_updateDamageReservesAction"));
    public static Link linkDamageInquiry = new Link(By.id("policyDataGatherForm:damageActionLink_inquiryDamageAction"));
}
