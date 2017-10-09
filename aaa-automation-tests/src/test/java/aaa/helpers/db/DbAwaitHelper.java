package aaa.helpers.db;


import com.jayway.awaitility.core.ConditionTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.db.DBService;

import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;

/**
 *  Allows to use implicit wait for DB checks
 */
public class DbAwaitHelper {
   private static Logger log = LoggerFactory.getLogger(DbAwaitHelper.class);

    /**
     *
     * @param query SQL query which fetch expected result
     * @param timeout Timeout value
     * @param timeUnit Time Unit (Seconds, minutest ect. {@link java.util.concurrent.TimeUnit}  )
     */
     public static boolean waitForQueryResult(String query, long timeout, TimeUnit timeUnit){
         try{
             await().atMost(timeout, timeUnit).until(() -> DBService.get().getValue(query).isPresent());
             return true;
         } catch (ConditionTimeoutException exception){
             log.info(String.format("Didn't fetch results for query bellow util %s %s \n [%s]",
                     timeout,timeUnit,query));
             return false;
         }
     }

    /**
     * Overload waitQueryResult method {@link DbAwaitHelper#waitForQueryResult(java.lang.String, long, java.util.concurrent.TimeUnit)}
     * Time Unit is always SECONDS
     */
     public static boolean waitForQueryResult(String query,long timeout){
        return waitForQueryResult(query, timeout, TimeUnit.SECONDS);
     }


    /**
     * Wait until result will appear in DB
     * @param query  SQL query which fetch expected result
     * @param timeout Timeout value in SECONDS
     * @return selected value or EMPTY result
     */
     public static String getQueryResult(String query, long timeout){
         return waitForQueryResult(query,timeout) ? DBService.get().getValue(query).get() : "";
     }

}
