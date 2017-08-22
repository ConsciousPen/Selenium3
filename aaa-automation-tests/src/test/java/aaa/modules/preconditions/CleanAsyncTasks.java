package aaa.modules.preconditions;

import java.time.format.DateTimeFormatter;

import aaa.helpers.constants.Groups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;

public class CleanAsyncTasks {
	private static Logger log = LoggerFactory.getLogger(CleanAsyncTasks.class);

	@Test(groups = Groups.PRECONDITION)
	public void cleanAsynkTasks() {
		String date = DateTimeUtils.getCurrentDateTime().format(DateTimeFormatter.ofPattern("dd-MMM-yy"));
		DBService.get().executeUpdate("delete from asynctaskproperty");
		DBService.get().executeUpdate("delete from asynctaskqueue");
		DBService.get().executeUpdate("delete from asynctask");
		DBService.get().executeUpdate("update ASYNCTASKLOCK set LOCKEDIND='0'," + " EXPIRES='" + date + " 12.00.00.000000000 AM'" + "where ID = '1'");
		log.info("DB update +++++ Clean Asynk Tasks is completed successfully ++++++\n");
	}

	@Test(groups = Groups.PRECONDITION)
	public void removeError() {
		DBService.get().executeUpdate("UPDATE CM_NODE_LOCK_INFO SET PARENT_LOCK_ID=NULL,LOCK_IS_DEEP=NULL,LOCK_OWNER=NULL");
		log.info("DB update +++++ Clean parrent lock is completed successfully ++++++\n");
	}
}