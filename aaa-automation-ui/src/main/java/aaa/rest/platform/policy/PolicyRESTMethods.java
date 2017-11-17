/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.rest.platform.policy;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

import aaa.main.modules.policy.PolicyType;
import aaa.rest.platform.PlatformRestServiceUtils;
import toolkit.db.DBService;
import toolkit.exceptions.IstfException;

/**
 * Methods for policy and related entities insertion
 * @author Deivydas Piliukaitis
 */
@SuppressWarnings("restriction")
public class PolicyRESTMethods {

    public enum TxType {
        Policy,
        Quote
    }

    private PlatformRestServiceUtils restServiceUtils = new PlatformRestServiceUtils();

    private String customerNumber;

    /**
     * Inserts case profile
     * @param customerNumber
     * @return Document - doc of inserted caseProfile
     */
    public Document insertCaseProfile(String customerNumber) {
        HashMap<String, String> params = new HashMap<>();
        params.put("{CUSTOMER_NUMBER}", customerNumber);
        return restServiceUtils.insertEntity("CaseProfile.xml", params);
    }

    public PlatformRestServiceUtils getRestServiceUtils() {
        return restServiceUtils;
    }

    protected String getPolicyDatasetName(String policyType, TxType txType) {
        return String.format("%s_%s.xml", policyType, txType.toString());
    }

    protected String getBillingDatasetName(String policyType) {
        return String.format("%s_BillingAccount.xml", policyType);
    }

    /**
     * Inserts policy
     * @param policyType
     * @param txType - {@link TxType}
     * @param customerNumber
     * @return Document - doc of inserted policy
     */
    public Document insertPolicy(String policyType, TxType txType, String customerNumber) {
        this.customerNumber = customerNumber;
        return insertPolicy(policyType, txType);
    }

    /**
     * Inserts policy
     * @param policyType
     * @param txType - {@link TxType}
     * @return Document - doc of inserted policy
     */
    private Document insertPolicy(String policyType, TxType txType) {
        if (policyType.equals(PolicyType.PUP)) {
            throw new IstfException("PREC_UM not supported by platform rest service");
        }
        HashMap<String, String> params = new HashMap<>();
        HashMap<String, String> xpathParams = new HashMap<>();

        Document policyDoc = restServiceUtils.loadXmlFrom(restServiceUtils.DATASETS_ROOT + getPolicyDatasetName(policyType, txType));

        params.put("{CUSTOMER_NUMBER}", customerNumber);
        params.put("{CUSTOMER_FIRSTNAME}",
                DBService.get().getValue(String.format("select ci.firstName from CustomerIndividual ci where ci.CUSTOMER_ID = (select c.id from Customer c where c.customerNumber = '%s')", customerNumber)).get());
        params.put("{CUSTOMER_LASTNAME}",
                DBService.get().getValue(String.format("select ci.lastName from CustomerIndividual ci where ci.CUSTOMER_ID = (select c.id from Customer c where c.customerNumber = '%s')", customerNumber)).get());
        params.put("{CUSTOMER_ADDRESS}", DBService.get().getValue(String.format(
                "select a.addressLine1 from AddressEntity a where a.COMMUNICATIONINFO_ID = (select c.CommunicationInfo_ID from Customer c where c.customerNumber = '%s')", customerNumber)).get());
        policyDoc = restServiceUtils.insertEntity(policyDoc, xpathParams, params);

        if (txType.equals(TxType.Policy)) {
            params.put("{POLICY_NUMBER}", restServiceUtils.getXmlAttributeValue(policyDoc, "//policyNumber"));
            params.put("{POLICY_ID}", restServiceUtils.getXmlAttributeValue(policyDoc, "//rootEntity/id"));
            params.put("{AGENCY_CODE}", restServiceUtils.getXmlAttributeValue(policyDoc, "//producerCd"));
            setPremiumParam("NWT", policyDoc, params);

            restServiceUtils.insertEntity(getBillingDatasetName(policyType), params);

            insertMovementCause(policyDoc, params, restServiceUtils.getXmlAttributeValue(policyDoc, "//rootEntity/id"), policyType);
        }
        return policyDoc;
    }

    private void setPremiumParam(String premiumCd, Document policyDoc, HashMap<String, String> params) {
        for (int i = 1; i <= restServiceUtils.findNodes(policyDoc, "//rootEntity/premiums/entry").getLength(); i++) {
            if (restServiceUtils.getXmlAttributeValue(policyDoc, "//rootEntity/premiums/entry[" + i + "]/string").equals(premiumCd)) {
                params.put("{PREMIUM}", restServiceUtils.getXmlAttributeValue(policyDoc, "//rootEntity/premiums/entry[" + i + "]/com.exigen.ipb.policy.domain.PremiumEntry/actualAmt"));
                break;
            }
        }
    }

    /**
     * Inserts movement cause (it is generated on quote issue action)
     * @param policyDoc
     * @param params
     * @param persistenceId - policy id (from PolicySummary - id )
     * @param policyType - {@link PolicyType}
     */
    protected void insertMovementCause(Document policyDoc, HashMap<String, String> params, String persistenceId, String policyType) {
        params.put("{POLICY_OID}", restServiceUtils.getXmlAttributeValue(policyDoc, "//rootEntity/oid"));
        Document movementCauseDoc = restServiceUtils.loadXmlFrom(restServiceUtils.DATASETS_ROOT + "PremiumMovementCause.xml");

        if (policyType.equals(PolicyType.AUTO_CA_SELECT.toString())) {
            movementCauseDoc =
                    createRiskItemAndCoveragePremiumMovements(policyDoc, movementCauseDoc, params, "com.exigen.ipb.policy.auto.domain.VehicleEntity", "com.exigen.ipb.policy.domain.CoverageEntity",
                            persistenceId);
        } else if (policyType.equals(PolicyType.HOME_CA_HO3.toString()) || policyType.equals(PolicyType.HOME_SS_HO3.toString())) {
            movementCauseDoc =
                    createRiskItemAndCoveragePremiumMovements(policyDoc, movementCauseDoc, params, "com.exigen.ipb.policy.domain.DwellEntity", "com.exigen.ipb.policy.domain.CoverageEntity",
                            persistenceId);
        } else if (policyType.equals(PolicyType.AUTO_SS.toString())) {
            movementCauseDoc =
                    createRiskItemAndCoveragePremiumMovements(policyDoc, movementCauseDoc, params, "com.exigen.ipb.policy.auto.domain.VehicleEntity",
                            "com.exigen.ipb.policy.domain.auto.AutoCoverageEntity", persistenceId);
        }

        restServiceUtils.insertEntity(movementCauseDoc, params);
    }

    private Document createRiskItemAndCoveragePremiumMovements(Document policyDocument, Document movementCauseDocument, HashMap<String, String> params, String riskItemName, String coverageName,
            String persistenceId) {
        NodeList nodeListRiskItems = restServiceUtils.findNodes(policyDocument, "//riskItems/" + riskItemName);
        Node premiumMovements = restServiceUtils.findNode(movementCauseDocument, "//com.exigen.ipb.policy.domain.PremiumMovementCause/premiumMovements");
        for (int i = 1; i <= nodeListRiskItems.getLength(); i++) {

            Node movementRiskItem = setUpPremiumMovement(policyDocument, riskItemName, i, -1, params.get("{PREMIUM}"), persistenceId, coverageName);
            premiumMovements.appendChild(movementCauseDocument.importNode(movementRiskItem, true));

            NodeList nodeListCoverages = restServiceUtils.findNodes(policyDocument, "//riskItems/" + riskItemName + "[" + i + "]/coverages/" + coverageName);
            for (int j = 1; j <= nodeListCoverages.getLength(); j++) {
                NodeList nodeListPremiumEntries =
                        restServiceUtils.findNodes(policyDocument, "//riskItems/" + riskItemName + "[" + i + "]/coverages/" + coverageName + "[" + j + "]/premiums/entry");
                for (int x = 1; x <= nodeListPremiumEntries.getLength(); x++) {
                    String premiumCd =
                            restServiceUtils.getXmlAttributeValue(policyDocument, "//riskItems/" + riskItemName + "[" + i + "]/coverages/" + coverageName + "[" + j
                                    + "]/premiums/entry[" + x + "]/com.exigen.ipb.policy.domain.PremiumEntry/premiumCd");
                    String premium =
                            restServiceUtils.getXmlAttributeValue(policyDocument, "//riskItems/" + riskItemName + "[" + i + "]/coverages/" + coverageName + "[" + j
                                    + "]/premiums/entry[" + x + "]/com.exigen.ipb.policy.domain.PremiumEntry/actualAmt");
                    if (premiumCd.equals("NWT") && !premium.equals("0")) {
                        Node movementCoverages = setUpPremiumMovement(policyDocument, riskItemName, i, j, premium, persistenceId, coverageName);
                        premiumMovements.appendChild(movementCauseDocument.importNode(movementCoverages, true));
                    }
                }
            }
        }
        return movementCauseDocument;
    }

    private Node setUpPremiumMovement(Document policyDocument, String riskItemName, int iterableRiskItem, int iterableCoverage, String premium, String persistenceId, String coverageName) {
        Document xmlDoc = new DocumentImpl();
        Element movementRiskItem = xmlDoc.createElement("com.exigen.ipb.policy.domain.PremiumMovement");

        if (iterableCoverage == -1) {
            restServiceUtils.createNode(xmlDoc, movementRiskItem, "oid", "pmri" + String.valueOf(System.currentTimeMillis()));
            restServiceUtils.createNode(xmlDoc, movementRiskItem, "entityOid", restServiceUtils.getXmlAttributeValue(policyDocument, "//riskItems/" + riskItemName + "[" + iterableRiskItem + "]/oid"));
            restServiceUtils.createNode(xmlDoc, movementRiskItem, "premiumLevel", "riskItem");
        } else {
            restServiceUtils.createNode(xmlDoc, movementRiskItem, "oid", "pmco" + String.valueOf(System.currentTimeMillis()));
            restServiceUtils.createNode(
                    xmlDoc,
                    movementRiskItem,
                    "entityOid",
                    restServiceUtils.getXmlAttributeValue(policyDocument, "//riskItems/" + riskItemName + "[" + iterableRiskItem + "]/coverages/" + coverageName + "["
                            + iterableCoverage + "]/oid"));
            restServiceUtils.createNode(
                    xmlDoc,
                    movementRiskItem,
                    "coverageId",
                    restServiceUtils.getXmlAttributeValue(policyDocument, "//riskItems/" + riskItemName + "[" + iterableRiskItem + "]/coverages/" + coverageName + "["
                            + iterableCoverage + "]/id"));
            restServiceUtils.createNode(xmlDoc, movementRiskItem, "premiumLevel", "coverage");
        }
        restServiceUtils.createNode(xmlDoc, movementRiskItem, "entityStatus", "added");
        restServiceUtils.createNode(xmlDoc, movementRiskItem, "policyId", persistenceId);
        restServiceUtils.createNode(xmlDoc, movementRiskItem, "riskItemId", restServiceUtils.getXmlAttributeValue(policyDocument, "//riskItems/" + riskItemName + "[" + iterableRiskItem + "]/id"));
        restServiceUtils.createNode(xmlDoc, movementRiskItem, "annualBasePremiumAmt", premium);
        restServiceUtils.createNode(xmlDoc, movementRiskItem, "basePremiumChangeAmt", premium);
        restServiceUtils.createNode(xmlDoc, movementRiskItem, "netPremiumChangeAmt", premium);
        restServiceUtils.createNode(xmlDoc, movementRiskItem, "grossPremiumChangeAmt", premium);
        restServiceUtils.createNode(xmlDoc, movementRiskItem, "movementType", "added");

        return movementRiskItem;
    }
}
