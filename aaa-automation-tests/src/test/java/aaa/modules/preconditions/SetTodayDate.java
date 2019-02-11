package aaa.modules.preconditions;

import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.helpers.constants.Groups;

public class SetTodayDate {
	private static Logger log = LoggerFactory.getLogger(SetTodayDate.class);

	@Test(groups = Groups.PRECONDITION)
	public void setTimeToToday() {
		log.info("Current application date: " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
		TimeSetterUtil.getInstance().adjustTime();
	}
}
