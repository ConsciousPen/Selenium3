package com.exigen.ipb.etcsa.utils;

import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import aaa.common.pages.LoginPage;
import com.exigen.ipb.etcsa.base.app.CSAAApplicationFactory;
import org.joda.time.DateTime;

import com.exigen.istf.exec.core.TimedTestContext;
import com.exigen.istf.exec.testng.TimeShiftTestUtil;
import com.exigen.istf.timesetter.client.TimeSetter;
import com.exigen.istf.timesetter.client.TimeSetterClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolkit.config.PropertyProvider;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;

public class TimeSetterUtil {

	protected static Logger log = LoggerFactory.getLogger(TimeSetterUtil.class);

	private static final String TIME_FORMAT = "HH:mm:ss";
	private static Boolean isPEF = false;
	private static TimeSetterUtil instance;
	private static TimeSetter timeSetterClient;

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
		if (!isPEF && timeSetterClient == null) {
			try {
				Class clazz = Class.forName(PropertyProvider.getProperty("time.service.class"));
				Constructor<? extends TimeSetter> ctor = clazz.getConstructor(String.class);
				timeSetterClient = ctor.newInstance(new Object[]{PropertyProvider.getProperty("app.host")});
			} catch (ReflectiveOperationException e) {
				log.error("Can't instatiate TimeSetter from class: " + PropertyProvider.getProperty("time.service.class"), e);
				timeSetterClient = new TimeSetterClient(PropertyProvider.getProperty("app.host"));
			}
		}
		return instance;
	}

	public LocalDateTime getStartTime() {
		LocalDateTime startTime;
		if (isPEF)
			startTime = jodaDateToJava(getContext().startTime());
		else
			startTime = istfDateToJava(timeSetterClient.getStartTime());
		return startTime;
	}

	public LocalDateTime getCurrentTime() {
		LocalDateTime currentTime;
		if (isPEF)
			currentTime = jodaDateToJava(getContext().currentTime());
		else
			currentTime = istfDateToJava(timeSetterClient.getDateTime());
		return currentTime;
	}

	public LocalDateTime getPhaseStartTime() {
		LocalDateTime phaseStartTime;
		if (isPEF) {
			phaseStartTime = jodaDateToJava(getContext().phaseStartTime());
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
		if (time == null)
			throw new IstfException("Provided time to shift is null");
		LocalDateTime adjDate = time;

		if (adjDate.format(DateTimeFormatter.ofPattern(TIME_FORMAT)).contains("00:00") || adjDate.format(DateTimeFormatter.ofPattern("HH:ss")).equals("00:00")) {
			LocalTime startTime = getStartTime().toLocalTime();
			LocalDate localDAte = adjDate.toLocalDate();

			adjDate = LocalDateTime.of(localDAte, startTime);
		}

		LocalDateTime currentTime = getPhaseStartTime();
		if (!adjDate.isAfter(currentTime)) {
			if (adjDate.toLocalDate().isEqual(currentTime.toLocalDate()))
				adjDate = currentTime.plusHours(1);
			else {
				throw new IstfException(String.format("Shift the time in the past is not possible. Current time: %s; Desired time: %s.", currentTime, time));
			}
		}
		closeAllApps();
		if (isPEF) {
			getContext().nextPhase(javaDateToYoda(adjDate));
			log.info(String.format("+++++ Application date is set to %s +++++", adjDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"))));
		} else {
			timeSetterClient.setDateTime(javaDateToIstf(adjDate));
		}
	}

	public Boolean isPEF() {
		return isPEF;
	}

	public void adjustTime() {
		timeSetterClient.adjustTime();
	}

	public static LocalDateTime jodaDateToJava(DateTime date) {
		return LocalDateTime.parse(date.toString("yyyyMMddHHmmss"), DateTimeUtils.TIME_STAMP);
	}

	public static DateTime javaDateToYoda(LocalDateTime date) {
		return new DateTime(date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
	}

	@SuppressWarnings("deprecation")
	public static LocalDateTime istfDateToJava(toolkit.utils.datetime.DateTime date) {
		try {
			LocalDateTime.parse(date.toString("yyyyMMddHHmmss"), DateTimeUtils.TIME_STAMP);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return LocalDateTime.parse(date.toString("yyyyMMddHHmmss"), DateTimeUtils.TIME_STAMP);
	}
	
	@SuppressWarnings("deprecation")
	public static toolkit.utils.datetime.DateTime javaDateToIstf(LocalDateTime date) {
		return new toolkit.utils.datetime.DateTime(date.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), "yyyyMMddHHmmss");
	}
	
	public LocalDateTime parse(CharSequence text, DateTimeFormatter formatter) { 
		LocalDate date = LocalDate.parse(text, formatter); 
		LocalTime time = getStartTime().toLocalTime(); 
		return LocalDateTime.of(date, time); 
	}

	private void closeAllApps() {
		CSAAApplicationFactory.get().mainApp(new LoginPage(new SimpleDataProvider())).close();
		CSAAApplicationFactory.get().adminApp(new LoginPage(new SimpleDataProvider())).close();
		CSAAApplicationFactory.get().opReportApp(new LoginPage(new SimpleDataProvider())).close();
	}
}
