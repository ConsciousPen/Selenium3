/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.rest.policy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.exigen.ipb.etcsa.base.app.ApplicationFactory;
import com.exigen.ipb.etcsa.base.app.LoginPage;
import com.exigen.ipb.etcsa.utils.DBManager;

import aaa.common.pages.MainPage;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.rest.JsonHelper;
import aaa.rest.productfactory.ProductFactoryRESTMethods;
import toolkit.config.PropertyProvider;
import toolkit.config.TestProperties;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.rest.ResponseWrapper;

public class PolicyRestImpl {

	protected static Logger log = LoggerFactory.getLogger(PolicyRestImpl.class);
    protected ProductFactoryRESTMethods productFactoryRest = new ProductFactoryRESTMethods();
    protected PolicyRESTMethods policyRest = new PolicyRESTMethods();

    private PolicyType policyType;
    private String quoteNumber;
    private String customerNumber;

    public PolicyRestImpl(String quoteNumber, String customerNumber, PolicyType policyType) {
        this.quoteNumber = quoteNumber;
        this.customerNumber = customerNumber;
        this.policyType = policyType;
    }

    //TODO not updated during AAA customization 
    public String copyPolicy(TestData tdPolicy) {
        quoteNumber = copyQuote(tdPolicy);
        calculatePremium();

        propose();
        ApplicationFactory.get().mainApp(new LoginPage(
                PropertyProvider.getProperty(TestProperties.EU_USER),
                PropertyProvider.getProperty(TestProperties.EU_PASSWORD))).open();
        MainPage.QuickSearch.search(quoteNumber);
        issue(tdPolicy);

        log.info(String.format("Policy was copied via REST: %s", quoteNumber));
        return quoteNumber;
    }

    //TODO not updated during AAA customization 
    public void calculatePremium() {
        if (!PolicyType.PUP.equals(policyType)) {
            calculatePremiumAPI();
        } else {
            ApplicationFactory.get().mainApp(new LoginPage(
                    PropertyProvider.getProperty(TestProperties.EU_USER),
                    PropertyProvider.getProperty(TestProperties.EU_PASSWORD))).open();
            MainPage.QuickSearch.search(quoteNumber);
            policyType.get().calculatePremium(null);
        }
    }

    public void calculatePremiumAPI() {
        log.debug("[REST] Start Calculate " + quoteNumber);
        String instanceName = DBService.get().getValue(String.format("select p.instanceName from PolicySummary p where policyNumber = '%s'", quoteNumber)).get();

        TestData td = productFactoryRest.transferDataForInit(policyType.getShortName(), "dataGather", customerNumber, instanceName);

        ResponseWrapper response = productFactoryRest.postQuoteCommandLoad(td);
        response.jsonVerifier().verify.status(200);

        TestData tdResponse = new SimpleDataProvider()
                .adjust("contextId", JsonHelper.getValue("$.contextId", response))
                .adjust("tabName", JsonHelper.getValue("$..tabs[?(@.tabName=~/Premium.*?/i)].tabName", response))
                .adjust("cookie", response.getResponse().getHeaderString("Set-Cookie").split(";")[0]).resolveLinks();

        response = productFactoryRest.postChangeTabQuote(tdResponse);
        response.jsonVerifier().verify.status(200);

        tdResponse.adjust("instanceName", JsonHelper.getValue("$.components[?(@.referenceName=~/.*RateAction.*?/i || @.referenceName=~/.*PremiumCalculationAction.*?/i)]..instanceName", response));

        response = policyRest.postExecute(tdResponse);
        response.jsonVerifier().verify.status(200);

        response = productFactoryRest.postSaveQuote(tdResponse);
        response.jsonVerifier().verify.status(200);

        //TODO(mburyak): Need to remove after investigate problem
        log.debug("[REST] Unlock " + quoteNumber);
        DBService.get().executeUpdate(String.format("delete from ENTITY_LOCK where entityNumber = '%s'", quoteNumber));
        log.debug("[REST] End Calculate " + quoteNumber);
    }

    public void issue(TestData tdPolicy) {
        log.debug("[UI] Start Issue " + quoteNumber);
        policyType.get().purchase(tdPolicy);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        log.debug("[UI] End Issue " + quoteNumber);
    }

    public void propose(TestData tdPolicy) {
        log.debug("[UI] Start Propose " + quoteNumber);

        PolicyType type = policyType;
        IPolicy policySecondary = (IPolicy) type.get();

        policySecondary.propose().perform(tdPolicy);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.PROPOSED);
        log.debug("[UI] End Propose " + quoteNumber);
    }

    public void propose() {
        log.debug("[REST] Start Propose " + quoteNumber);
        String instanceName = DBService.get().getValue(String.format("select p.instanceName from PolicySummary p where policyNumber = '%s'", quoteNumber)).get();
        TestData td = productFactoryRest.transferDataForInit(policyType.getShortName(), "propose", customerNumber, instanceName);

        ResponseWrapper response = productFactoryRest.postQuoteCommandLoad(td);
        response.jsonVerifier().verify.status(200);

        TestData tdResponse = new SimpleDataProvider()
                .adjust("contextId", JsonHelper.getValue("$.contextId", response))
                .adjust("instanceName", JsonHelper.getValue("$..instanceName", response))
                .adjust("cookie", response.getResponse().getHeaderString("Set-Cookie").split(";")[0]).resolveLinks();

        response = productFactoryRest.putQuotePropose(tdResponse);
        response.jsonVerifier().verify.status(200);

        response = policyRest.postExecute(tdResponse);
        response.jsonVerifier().verify.status(200);

        //TODO(mburyak): Need to remove after investigate problem
        log.debug("[REST] Unlock " + quoteNumber);
        DBService.get().executeUpdate(String.format("delete from ENTITY_LOCK where entityNumber = '%s'", quoteNumber));
        log.debug("[REST] End Propose " + quoteNumber);
    }

    public String copyQuote(TestData tdQuote) {
        log.debug("[REST] Start copy " + quoteNumber);
        synchronized (policyType) {
            String instanceName = DBService.get().getValue(String.format("select p.instanceName from PolicySummary p where policyNumber = '%s'", quoteNumber)).get();

            TestData td = productFactoryRest.transferDataForInit(policyType.getShortName(), "copyQuote", customerNumber, instanceName);

            ResponseWrapper response = productFactoryRest.postQuoteCommandLoad(td);
            response.jsonVerifier().verify.status(200);

            TestData tdResponse = new SimpleDataProvider()
                    .adjust("contextId", JsonHelper.getValue("$.contextId", response))
                    .adjust("instanceName", JsonHelper.getValue("$..instanceName", response))
                    .adjust("cookie", response.getResponse().getHeaderString("Set-Cookie").split(";")[0]).resolveLinks();

            response = productFactoryRest.putQuoteCopy(tdQuote.adjust(tdResponse));
            response.jsonVerifier().verify.status(200);

            response = policyRest.postExecute(tdResponse);
            response.jsonVerifier().verify.status(200);

            quoteNumber = fromSource(JsonHelper.getValue("$.summary", response));

            //TODO(mburyak): Need to remove after investigate problem
            log.debug("[REST] Unlock " + quoteNumber);
            DBService.get().executeUpdate(String.format("delete from ENTITY_LOCK where entityNumber = '%s'", quoteNumber));
        }
        log.info(String.format("Quote was copied via REST: %s", quoteNumber));
        return quoteNumber;
    }

    private String fromSource(String source) {
        String pattern = "(?<=#)\\w{2}\\d+";
        Matcher matcher = Pattern.compile(pattern).matcher(source);
        if (!matcher.find()) {
            throw new RuntimeException("Cannot extract pattern \"" + pattern + "\": " + source);
        }
        return matcher.group();
    }
}
