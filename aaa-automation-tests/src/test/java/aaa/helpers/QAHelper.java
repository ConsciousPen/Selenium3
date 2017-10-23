package aaa.helpers;

import aaa.modules.BaseTest;
import toolkit.datax.TestData;

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

}
