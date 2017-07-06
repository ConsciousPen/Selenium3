/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.rest.platform;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import toolkit.utils.datetime.DateTime;

/**
 * Class for XML dataset processing
 * @author Deivydas Piliukaitis
 */
public class BaseDataSetLoader {

    /**
     * Default XML dataset timestamp format
     */
    public static final SimpleDateFormat XML_TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd 00:00:00.0");

    /**
     * Default XML dataset date format
     */
    public static final SimpleDateFormat XML_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private InputStream input;

    private Map<String, String> replaceValues = new HashMap<>();

    public BaseDataSetLoader() {
        replaceValues.put("{UID}", String.valueOf(System.currentTimeMillis()));
        Calendar time = Calendar.getInstance();
        if (time.get(Calendar.HOUR) == 0 && time.get(Calendar.MINUTE) < 30) {
            time.set(Calendar.MINUTE, 30);
        }
        time.add(Calendar.MINUTE, -30); // workaround: when server system time differs from test time tests fail due to incorrect transaction order.
        replaceValues.put("{TIME}", new SimpleDateFormat("HH:mm:ss.SSS").format(time.getTime()));
    }

    /**
     * Makes string from given XML resource.
     * @param resourceName which be used to produce entity
     * @return xmlString representing given resource
     * @throws IllegalArgumentException if resource not found
     */
    public String findXmlEntitySource(String resourceName) throws IOException {
        input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        if (input == null) {
            throw new IllegalArgumentException("Dataset " + resourceName
                    + " not found in test classpath!");
        }
        String xmlString = IOUtils.toString(input);
        return xmlString;
    }

    /**
     * Maps values in given XML string.
     * @param xmlString XML content as string
     * @return mapped XML string
     * @throws IllegalArgumentException if xmlString is null
     */
    public String applyPlaceholderValues(String xmlString, Map<String, String> replaceValues) throws IOException {
        this.replaceValues.putAll(replaceValues);
        if (xmlString == null) {
            throw new IllegalArgumentException("XML string can not be null.");
        }
        for (String key : this.replaceValues.keySet()) {
            xmlString = xmlString.replace(key, this.replaceValues.get(key));
        }
        return xmlString;
    }

    /**
     * Applies given date offset {@code TODAY} to given XML string if date is present.
     * @param xmlString XML content as string
     * @param offset period which will be applied in days
     * @return XML string with applied date offset
     */
    public String applyDateOffset(String xmlString, int offset) {
        Matcher matcher = Pattern.compile("[{]TODAY([+|-][0-9]+)?[}]").matcher(xmlString);
        synchronized (XML_DATE_FORMAT) {
            while (matcher.find()) {
                String dateHolder = matcher.group();
                String number = dateHolder.replaceAll("[^-,0-9]", "");
                int days = number.isEmpty() ? 0 : Integer.parseInt(number);
                String date = XML_DATE_FORMAT
                        .format(new DateTime().addDays(days + offset).convertToJavaDate());
                xmlString = xmlString.replace(dateHolder, date);
            }
        }
        return xmlString;
    }

    /**
     * Replaces date values with placeholders that are autmatically resolved
     * during dataset parsing.
     * @param xmlString
     * - String of xml image.
     * @return - String of xml image.
     * @throws ParseException
     */
    public static String codeDates(String xmlString) throws ParseException {
        Matcher matcher = Pattern.compile("[1-2][0-9]{3}-[0-1][0-9]-[0-3][0-9]").matcher(xmlString);
        synchronized (XML_DATE_FORMAT) {
            while (matcher.find()) {
                String dateString = matcher.group();
                int days = getNumberOfDaysBetween(new Date(), XML_DATE_FORMAT.parse(dateString), false, false);
                String dateHolder = days > 0 ? "{TODAY+" + days + "}" : days < 0 ? "{TODAY" + days + "}" : "{TODAY}";
                xmlString = xmlString.replace(dateString, dateHolder);
            }
        }
        return xmlString;
    }

    /**
     * Replaces time values with placeholders that are autmatically resolved
     * during dataset parsing.
     * @param xmlString
     * - String of xml image.
     * @return - String of xml image.
     * @throws ParseException
     */
    public static String codeTime(String xmlString) throws ParseException {
        Matcher matcher = Pattern.compile("[0-2][0-9](:[0-6][0-9]){2}.[0-9]{1,3}").matcher(xmlString);
        while (matcher.find()) {
            String time = matcher.group();
            if (!time.equals("00:00:00.0")) {
                xmlString = xmlString.replace(time, "{TIME}");
            }
        }
        return xmlString;
    }

    /**
     * Replaces oid values with placeholders that are autmatically resolved
     * during dataset parsing.
     * @param xmlString
     * - String of xml image.
     * @return - String of xml image.
     * @throws ParseException
     */

    public static String codeOids(String xmlString) {
        xmlString = codeOidTag(xmlString, "oid");
        xmlString = codeOidTag(xmlString, "proxiedEntityOid");
        xmlString = codeOidTag(xmlString, "instanceName");
        xmlString = codeOidTag(xmlString, "connectedToInstanceName");
        return xmlString;
    }

    private static String codeOidTag(String xmlString, String tagName) {
        Matcher matcher = Pattern.compile("<" + tagName + ">(.*)</" + tagName + ">").matcher(xmlString);
        while (matcher.find()) {
            String oldOid = matcher.group();
            if (oldOid.length() > String.valueOf(System.currentTimeMillis()).length()) {
                xmlString = xmlString.replace(oldOid, oldOid.replaceFirst(".{13}</" + tagName + ">", "{UID}</" + tagName + ">"));
            }
        }
        return xmlString;
    }

    /**
     * Calculates number of days between two dates. Order of the dates does not matter.
     * @param start start date
     * @param end end date
     * @param inclusive whether to include the end day
     * @return The number of days between the two dates. Zero is
     * returned if the dates are the same
     * @throws IllegalArgumentException if any of dates is null
     */
    public static int getNumberOfDaysBetween(Date start, Date end, boolean inclusive) {

        return getNumberOfDaysBetween(start, end, inclusive, true);
    }

    /**
     * Calculates number of days between two dates. Order of the dates does matter (may return negative value).
     * @param start
     * @param end
     * @param inclusive
     * @param abs
     * @return
     * @throws IllegalArgumentException if any of dates is null
     */

    public static int getNumberOfDaysBetween(Date start, Date end, boolean inclusive, boolean abs) {

        if (start == null || end == null) {
            throw new IllegalArgumentException("Neither of dates can be null");
        }
        Calendar d1 = new GregorianCalendar();
        d1.setTime(start);
        Calendar d2 = new GregorianCalendar();
        d2.setTime(end);
        int sign = 1;
        if (compareTime(d1.getTime(), d2.getTime()) > 0) { // swap dates so that d1 is start and d2 is end
            Calendar swap = d1;
            d1 = d2;
            d2 = swap;
            sign = -1;
        }
        int days = d2.get(Calendar.DAY_OF_YEAR) - d1.get(Calendar.DAY_OF_YEAR);
        int y2 = d2.get(Calendar.YEAR);
        if (d1.get(Calendar.YEAR) != y2) {
            d1 = (Calendar) d1.clone();
            do {
                days += d1.getActualMaximum(Calendar.DAY_OF_YEAR);
                d1.add(Calendar.YEAR, 1);
            }
            while (d1.get(Calendar.YEAR) != y2);
        }
        if (inclusive) {
            days++;
        }
        return abs ? days : sign * days;
    }

    public static int compareTime(Date d1, Date d2) {
        return Long.signum(d1.getTime() - d2.getTime());
    }
}
