package aaa.modules.regression.document_fulfillment.template.functional;

import aaa.common.pages.SearchPage;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.auto_ca.AutoCaPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;

import java.time.LocalDateTime;

public abstract class TestCinAbstract extends BaseTest {
    IPolicy policy;

    /**
     * Prepares error message for logging
     *
     * @param msg error message
     * @return error message containing data about policy and DocGen even
     */
    protected String getPolicyErrorMessage(String msg, String policyNumber, AaaDocGenEntityQueries.EventNames event) {
        return msg + " for policy: " + policyNumber + ", event: " + event;
    }
}
