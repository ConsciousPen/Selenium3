package aaa.helpers.performance.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.exigen.ipb.etcsa.utils.DBManager;

import aaa.helpers.TestDataManager;
import aaa.main.modules.customer.CustomerType;
import aaa.rest.customer.CustomerCoreRESTMethods;
import aaa.rest.customer.model.Customer;
import toolkit.datax.TestData;
import toolkit.rest.ResponseWrapper;
import toolkit.utils.logging.CustomLogger;
import toolkit.utils.meters.WaitMeters;

public class BulkCustomerCreationHelper {

    private static final Logger LOGGER = CustomLogger.getInstance();
    private static final String GET_PROC_DEF_ID = "select distinct(PROC_DEF_ID_) from ACT_RU_TASK where NAME_ = 'Opportunity is in cold'";
    private static final String GET_ALL_CUSTOMERS = "select count(*) from Customer";
    private static final String GET_ALL_CUSTOMERS_WITH_TASK = "select count(*) as CUSTOMERS_WITH_TASKS from Customer cr join ACTEX_RU_PROCINST tasks " +
            "on cr.customerNumber=tasks.entityRefNo " +
            "where tasks.entityType='Customer' and tasks.ACT_PROC_DEF_ID='%1$s'";
    private static TestData tdCustomerNonIndividual = new TestDataManager().customer.get(CustomerType.NON_INDIVIDUAL).getTestData("DataGather", "TestData");
    private static Customer customer = createCustomer();

    private static int nextId = 0;
    private static long customersCount = 0;
    private static long failedCustomers = 0;
    private static long previousCustomersCount = 0;
    private static long startTime = System.currentTimeMillis();
    private static boolean runFinished;

    private static ThreadLocal<Long> failedCustomersCountPerThread = new ThreadLocal<Long>() {
        @Override
        protected Long initialValue() {
            return 0L;
        }
    };
    private static ThreadLocal<List<PerfCustomerHolder>> customers = new ThreadLocal<List<PerfCustomerHolder>>() {
        @Override
        protected List<PerfCustomerHolder> initialValue() {
            return new ArrayList<PerfCustomerHolder>();
        }
    };

    private static final ThreadLocal<String> threadId = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return String.format("%05d", ++nextId);
        }
    };

    private static ThreadLocal<Long> startTimePerThread = new ThreadLocal<Long>() {
        @Override
        protected Long initialValue() {
            return 0L;
        }
    };

    public static synchronized void incrementCustomersCount() {
        customersCount++;
    }

    public static synchronized void incrementFailedCustomersCount() {
        failedCustomers++;
    }

    public static void setStartTimePerThread(Long startTime) {
        startTimePerThread.set(startTime);
    }

    public static long getStartTimePerThread() {
        return startTimePerThread.get();
    }

    public static List<PerfCustomerHolder> getPerfCustomerHolders() {
        return customers.get();
    }

    public static Long getFailedCustomersCountPerThread() {
        return failedCustomersCountPerThread.get();
    }

    public static void setFailedCustomersCountPerThread(Long failedCustomersCountPerThread) {
        BulkCustomerCreationHelper.failedCustomersCountPerThread.set(failedCustomersCountPerThread);
    }

    public static String getThreadId() {
        return threadId.get();
    }

    public static void startPrintingTotal(long eachMinutes) {
        Thread newThread = new Thread(() -> {
            long minutes = eachMinutes;
            while (!runFinished) {
                try {
                    TimeUnit.MINUTES.sleep(eachMinutes);
                    printTotals(String.format("Approximately %1$s minute(s) passed", minutes));
                    minutes = minutes + eachMinutes;
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        });
        newThread.start();
    }

    @SuppressWarnings("rawtypes")
	public static void printTotals(String stage) {
        long tempCustCount = customersCount;
        long tempFailedCustomersCount = failedCustomers;
        LOGGER.info("#########################################################");
        LOGGER.info("-----------------------{}--------------------------", stage);
        LOGGER.info("Total                   : ");
        LOGGER.info("Spent Time              : {}", getSpentTime(startTime));
        LOGGER.info("Created Customers amount: {}", tempCustCount);
        LOGGER.info("Failed Customers amount : {}", tempFailedCustomersCount);
        LOGGER.info("ALL Customers from ENV: {}", getGetAllCustomersFromEnv());
        LOGGER.info("ALL Customers with tasks from ENV: {}", getGetAllCustomersWithTasksFromEnv());
        LOGGER.info("Frequency: {} created per period", tempCustCount - previousCustomersCount);
        for (Map.Entry entry : WaitMeters.getAllTotals().entrySet()) {
            if (entry.getKey().equals("POSTCUSTOMERENTITY:POST")) {
                LOGGER.info("Customer creation response time: [{}]", entry.getValue());
            }
            if (entry.getKey().equals("POSTTASKS:POST")) {
                LOGGER.info("Task creation response time: [{}]", entry.getValue());
            }
            if (entry.getKey().equals("POSTTASKNOTES:POST")) {
                LOGGER.info("Notes creation response time: [{}]", entry.getValue());
            }
        }
        previousCustomersCount = tempCustCount;
    }

    public static synchronized String getSpentTime(Long startTime) {
        long timeSpent = System.currentTimeMillis() - startTime;
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(timeSpent),
                TimeUnit.MILLISECONDS.toMinutes(timeSpent) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeSpent)),
                TimeUnit.MILLISECONDS.toSeconds(timeSpent) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeSpent)));
    }

    public static Customer getCustomer() {
        customer.resetAllCustomersIds();
        return customer;
    }

    public static void setRunFinished() {
        runFinished = true;
    }

    private static Customer createCustomer() {
        ResponseWrapper response;
        CustomerCoreRESTMethods restCustomer = new CustomerCoreRESTMethods();
        response = restCustomer.postCustomersNonIndividual(tdCustomerNonIndividual);
        return response.getResponse().readEntity(Customer.class);
    }

    private static String getGetAllCustomersFromEnv() {
        String result = "";
        try {
            result = DBManager.netInstance().getValue(GET_ALL_CUSTOMERS);
        } catch (Exception e) {
            //ignore
        }
        return result;
    }

    private static String getGetAllCustomersWithTasksFromEnv() {
        String result = "";
        try {
            String procDef = DBManager.netInstance().getValue(GET_PROC_DEF_ID);
            result = DBManager.netInstance().getValue(String.format(GET_ALL_CUSTOMERS_WITH_TASK, procDef));
        } catch (Exception e) {
            //ignore
        }
        return result;
    }
}
