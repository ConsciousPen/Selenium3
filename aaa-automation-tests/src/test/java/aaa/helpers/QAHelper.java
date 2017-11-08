package aaa.helpers;

import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

import static java.security.Policy.getPolicy;

/**
 * @author Tyrone Jemison
 * @description This class is used to facilitate quick and simple automation tasks.
 */
public class QAHelper extends BaseTest
{
    // Localize Data Used For Test Runs
    public String _customerFirstName;
    public String _customerLastName;
    public TestData _td;
    public IPolicy _policy;


    // High Traffic Methods

    /**
     * Used to create either a individual or non-individual customer based on boolean value.
     * @param bIndividual
     */
    public void createCustomer(boolean bIndividual)
    {
        if(bIndividual) {
            createCustomerIndividual();
        }else {
            createCustomerNonIndividual();
        }
    }

    public void createCustomer(boolean bIndividual, TestData td)
    {
        if(bIndividual) {
            createCustomerIndividual(td);
        }else {
            createCustomerNonIndividual(td);
        }
    }

    public String createPolicy(TestData policyTestData)
    {
        return createPolicy(policyTestData);
    }

    public void launchApp()
    {
        mainApp().open();
    }

    public void closeApp()
    {
        mainApp().close();
    }

    public TestData getTestData_BySameClassName(String headerInYaml)
    {
        return getTestSpecificTD(headerInYaml);
    }

    public void getCreatedPolicy(String in_policyNumber)
    {
        SearchPage.openPolicy(in_policyNumber);
    }
}
