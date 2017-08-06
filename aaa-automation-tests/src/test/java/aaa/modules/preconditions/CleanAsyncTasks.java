package aaa.modules.preconditions;

import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import toolkit.utils.DBHelper;
import toolkit.utils.datetime.DateTimeUtils;

public class CleanAsyncTasks {
	private static DBHelper dbHelper = new DBHelper();
	private static Logger log = LoggerFactory.getLogger(CleanAsyncTasks.class);

	@Test
	public void cleanAsynkTasks() {
		String date = DateTimeUtils.getCurrentDateTime().format(DateTimeFormatter.ofPattern("dd-MMM-yy"));
		try {
			dbHelper.connect();
			dbHelper.executeUpdate("delete from asynctaskproperty");
			dbHelper.executeUpdate("delete from asynctaskqueue");
			dbHelper.executeUpdate("delete from asynctask");
			dbHelper.executeUpdate("update ASYNCTASKLOCK set LOCKEDIND='0'," + " EXPIRES='" + date + " 12.00.00.000000000 AM'" + "where ID = '1'");
			log.info("DB update +++++ Clean Asynk Tasks is completed successfully ++++++\n");
			dbHelper.disconnect();
		} catch (Exception e) {
			dbHelper.disconnect();
			Assert.fail("An Excetion occured during SQL execution", e);
		}
	}

	@Test
	public void removeError() {
		try {
			dbHelper.connect();
			dbHelper.executeUpdate("UPDATE CM_NODE_LOCK_INFO SET PARENT_LOCK_ID=NULL,LOCK_IS_DEEP=NULL,LOCK_OWNER=NULL");
			log.info("DB update +++++ Clean parrent lock is completed successfully ++++++\n");
		} catch (Exception e) {
			dbHelper.disconnect();
			Assert.fail("An Excetion occured during SQL execution", e);
		}
	}
}