package aaa.helpers.billing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import aaa.helpers.ssh.RemoteHelper;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.utils.logging.CustomLogger;

public class DeclineRecurringPaymentHelper {
	
	private static final String DECLINE_PAYMENT_REMOTE_PATH = "/home/mp2/pas/sit/PMT_E_PMTCTRL_PASSYS_7002_D/inbound/";
	private static final String FILE_LAST_PART = "PMT_E_PMTCTRL_PASSYS_7019_D";
	//private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HHmmss");
	
	public static synchronized File createFile(String state, String policyNumber, String paymentNumber, String amount) {
		File file;
		String fileName;
		LocalDateTime date = DateTimeUtils.getCurrentDateTime();
		
		do {
			fileName = date.format(DATE_PATTERN) + "_" + date.format(TIME_PATTERN) + "_" + FILE_LAST_PART + ".dat";
			file = new File(CustomLogger.getLogDirectory().concat("/DeclinePayment_Files/"), fileName);
			date = date.plusSeconds(1);
		} while (file.exists());
		file.getParentFile().mkdir();
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write("HDR|QA|PMTCTRL|PMT_E_PMTCTRL_PASSYS_7019_D|n01apl4913|CB97B3D9-1F9B-F8DF-79D5-C81D113760A8\n");
			bw.write("DTL|48786|" + Math.round(999999 * Math.random()) + "|ERR|PCRECPAY||" + paymentNumber + "|" + amount + "|19-R03|Account Number Not on File|PAS|WUIC|PC|PA||||" 
					+ policyNumber + "|" + state + "|" + amount + "||" + amount + "|||||||||||||\n");
			bw.write("TRL|" + file.getName() + "|20140911051757|1197AADE-C2FC-8865-385E-5C6B653B5D0C|1|1|" + amount);
			bw.flush();
		} catch (IOException e) {
			throw new IstfException(e);
		}
		
		return file;
	}

	public static synchronized void copyFileToServer(File file) {
		if (file == null) {
			throw new IstfException("Decline Payment File is NULL");
		}
		RemoteHelper.get().uploadFile(file.getAbsolutePath(), DECLINE_PAYMENT_REMOTE_PATH + file.getName());
	}
	
}
