package com.exigen.ipb.eisa.utils;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.exigen.ipb.eisa.base.app.CSAAApplicationFactory;
import com.exigen.ipb.eisa.base.config.CustomTestProperties;
import com.exigen.istf.exec.core.TimedTestContext;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import com.exigen.istf.timesetter.client.TimeSetter;
import com.exigen.istf.timesetter.client.TimeSetterClient;
import com.exigen.istf.timesetter.client.config.ClientConfig;
import toolkit.config.PropertyProvider;
import toolkit.exceptions.IstfException;

public class TimeSetterUtil {

	private static final String TIME_FORMAT = "HH:mm:ss";
	private static final Boolean isLocalTime = Boolean.valueOf(PropertyProvider.getProperty(CustomTestProperties.USE_LOCAL_TIME_AS_SERVER, "true"));
	private static final String TIME_SERVICE_CLASS = PropertyProvider.getProperty("time.service.class");
	protected static Logger log = LoggerFactory.getLogger(TimeSetterUtil.class);
	private static Boolean isPEF = false;
	private static TimeSetterUtil instance;
	private static TimeSetter timeSetterClient;

	private TimeSetterUtil() {
	}

	private TimeSetterUtil(ClientConfig config) {
	}

	public static synchronized TimeSetterUtil getInstance() {
		if (TimeShiftTestUtil.isPEFAvailable()) {
			isPEF = true;
		}
		if (instance == null) {
			instance = new TimeSetterUtil();
		}
		if (!isPEF && timeSetterClient == null) {
			try {
				Class clazz = Class.forName(TIME_SERVICE_CLASS);
				Constructor<? extends TimeSetter> ctor = clazz.getConstructor();
				timeSetterClient = ctor.newInstance();
			} catch (ReflectiveOperationException e) {
				log.error("Can't instatiate TimeSetter from class: " + TIME_SERVICE_CLASS, e);
				timeSetterClient = new TimeSetterClient();
			}
		}
		return instance;
	}

	public LocalDateTime getStartTime() {
		LocalDateTime startTime;
		if (isPEF) {
			startTime = getContext().getStartTime();
		} else {
			startTime = timeSetterClient.getStartTime();
		}
		return startTime;
	}

	public LocalDateTime getCurrentTime() {
		LocalDateTime currentTime;
		if (isPEF) {
			currentTime = getContext().getCurrentTime();
		} else {
			currentTime = timeSetterClient.getDateTime();
		}
		return currentTime;
	}

	public LocalDateTime getPhaseStartTime() {
		LocalDateTime phaseStartTime;
		if (isPEF) {
			phaseStartTime = getContext().getPhaseStartTime();
		} else {
			phaseStartTime = getCurrentTime();
		}
		return phaseStartTime;
	}

	public TimedTestContext getContext() {
		return TimeShiftTestUtil.getContext();
	}

	/**
	 * Before any nextphase it is necessary to close current app:
	 * BaseTest.mainApp().close(); BaseTest.adminApp().close();
	 *
	 * @param time
	 */

	public void nextPhase(LocalDateTime time) {
		if (isLocalTime) {
			throw new IstfException("User Local System time is used, Date can't shifted");
		}
		if (time == null) {
			throw new IstfException("Provided time to shift is null");
		}
		LocalDateTime adjDate = time;

		if (adjDate.format(DateTimeFormatter.ofPattern(TIME_FORMAT)).contains("00:00") || adjDate.format(DateTimeFormatter.ofPattern("HH:ss")).equals("00:00")) {
			LocalTime startTime = getStartTime().toLocalTime();
			LocalDate localDAte = adjDate.toLocalDate();

			adjDate = LocalDateTime.of(localDAte, startTime);
		}

		LocalDateTime currentTime = getPhaseStartTime();
		if (!adjDate.isAfter(currentTime)) {
			if (adjDate.toLocalDate().isEqual(currentTime.toLocalDate())) {
				adjDate = currentTime.plusHours(1);
			} else {
				throw new IstfException(String.format("Shift the time in the past is not possible. Current time: %s; Desired time: %s.", currentTime, time));
			}
		}
		CSAAApplicationFactory.get().closeAllApps();
		if (isPEF) {
			getContext().nextPhase(adjDate);
			log.info(String.format("+++++ Application date is set to %s +++++", adjDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"))));
		} else {
			timeSetterClient.setDateTime(adjDate);
		}
	}

	public Boolean isPEF() {
		return isPEF;
	}

	public void adjustTime() {
		if (!isPEF) {
			timeSetterClient.adjustTime();
		} else {
			throw new IstfException("Not applicable for PEF mode");
		}
	}

	public LocalDateTime parse(CharSequence text, DateTimeFormatter formatter) {
		LocalDate date = LocalDate.parse(text, formatter);
		LocalTime time = getStartTime().toLocalTime();
		return LocalDateTime.of(date, time);
	}

	/**
	 * Used to compare the current date/time to a given date/time and verify the date is on or after this date
	 * @param algoEffectiveDate LocalDateTime that is needed to be on or after
	 */
	public void confirmDateIsAfter(LocalDateTime algoEffectiveDate) {
		if (getCurrentTime().isBefore(algoEffectiveDate)) {
			nextPhase(algoEffectiveDate);
		}
	}
}
