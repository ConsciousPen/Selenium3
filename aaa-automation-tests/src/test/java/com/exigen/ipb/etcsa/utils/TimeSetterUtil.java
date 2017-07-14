package com.exigen.ipb.etcsa.utils;

import java.util.Calendar;

import com.exigen.istf.exec.core.DateTimeTestUtil;
import com.exigen.istf.exec.core.TimedTestContext;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import com.exigen.istf.timesetter.client.TimeSetter;
import com.exigen.istf.timesetter.client.TimeSetterClient;

import aaa.modules.BaseTest;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTime;

public class TimeSetterUtil {

	private static Boolean isPEF = false;
	private static TimeSetterUtil instance;
	private static TimeSetter timeSetterClient;
	private static final String TIME_FORMAT = "HH:mm:ss";

	// private static Logger log =
	// LoggerFactory.getLogger(TimeSetterUtil.class);

	private TimeSetterUtil() {

		if (TimeShiftTestUtil.isContextAvailable()) {
			isPEF = true;
		}
	}

	public static synchronized TimeSetterUtil getInstance() {
		if (instance == null) {
			instance = new TimeSetterUtil();
		}
		if (!isPEF && timeSetterClient == null)
			timeSetterClient = new TimeSetterClient(PropertyProvider.getProperty("app.host"));
		return instance;
	}

	public DateTime getStartTime() {
		DateTime startTime;
		if (isPEF)
			startTime = DateTimeTestUtil.toISTFDateTime(getContext().startTime());
		else
			startTime = timeSetterClient.getStartTime();
		return startTime;
	}

	public DateTime getCurrentTime() {
		DateTime currentTime;
		if (isPEF)
			currentTime = DateTimeTestUtil.toISTFDateTime(getContext().currentTime());
		else
			currentTime = convertCalendarToDateTime(timeSetterClient.getTime());
		return currentTime;

	}

	public void nextPhase(DateTime time) {
		if (time == null)
			throw new NullPointerException();

		if (time.toString(TIME_FORMAT).contains("00:00") || time.toString("HH:ss").equals("00:00")) {
			DateTime startTime = getStartTime();
			time = time.addHours(Integer.parseInt(startTime.toString("HH"))).addMinutes(Integer.parseInt(startTime.toString("mm"))).addSeconds(Integer.parseInt(startTime.toString("ss")));
		}
		DateTime currentTime = getPhaseStartTime();
		if (!time.convertToJavaDate().after(currentTime.convertToJavaDate())) {
			if (time.toString(DateTime.MM_DD_YYYY).equals(currentTime.toString(DateTime.MM_DD_YYYY)))
				time = currentTime.addHours(1);
			else throw new IstfException(String.format("Shift the time in the past is not possible. Current time: %s; Desired time: %s.", currentTime, time ));
		}
		if (isPEF) {
			BaseTest.mainApp().close();
			BaseTest.adminApp().close();
			getContext().nextPhase(DateTimeTestUtil.fromISTFDateTime(time));
		} else
			timeSetterClient.setTime(time.convertToJavaDate());
	}

	public DateTime getPhaseStartTime() {
		DateTime phaseStartTime;
		if (isPEF)
			phaseStartTime = DateTimeTestUtil.toISTFDateTime(getContext().phaseStartTime());
		else
			phaseStartTime = getCurrentTime();
		return phaseStartTime;
	}

	public TimedTestContext getContext() {
		return TimeShiftTestUtil.getContext();
	}

	public Boolean isPEF() {
		return isPEF;
	}

	public void adjustTime() {
		timeSetterClient.adjustTime();
	}

	public static DateTime convertCalendarToDateTime(Calendar calendar) {
		if (calendar == null) {
			return null;
		}
		DateTime dateTime = new DateTime(calendar.getTime());
		dateTime.setTimeZone(calendar.getTimeZone().getID());
		return dateTime;
	}
}
