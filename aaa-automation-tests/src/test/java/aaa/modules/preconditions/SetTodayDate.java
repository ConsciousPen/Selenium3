package aaa.modules.preconditions;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpJob;


public class SetTodayDate{
	private static Logger log = LoggerFactory.getLogger(SetTodayDate.class);

	@Test(groups = Groups.PRECONDITION)
	public void setTimeToToday(){
		try {
			HttpJob.stopAsyncManager();
		} catch (IOException e) {
			log.error("Async manager was not stoped", e);
		}
		log.info("Current application date: " + TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")));
		TimeSetterUtil.getInstance().adjustTime();
	}
}
