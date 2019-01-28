package aaa.helpers.billing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.helpers.ssh.RemoteHelper;
import toolkit.exceptions.IstfException;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.utils.logging.CustomLogger;

public class RemittancePaymentsHelper {

	private static final String REMITTANCE_REMOTE_PATH = "/home/mp2/pas/sit/PMT_E_PMTCTRL_PASSYS_7001_D/inbound/";

	/**
	 * This method is used for prepare remittance file with data specified by input parameters.
	 * 
	 * @param state - State (e.g. "AZ")
	 * @param policyNo - Existing Policy number
	 * @param amount - Value of payment
	 * @param paymentSystem - External Payment System name
	 */
	public static synchronized File createRemittanceFile(String state, String policyNo, Dollar amount, String paymentSystem) {
		final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyyMMdd");
		final DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("HHmmss");

		File file;
		LocalDateTime date = DateTimeUtils.getCurrentDateTime();

		do {
			String fileName = policyNo + "_" + date.format(DATE_TIME_PATTERN) + "_PMT_E_PMTCTRL_PASSYS_7001_D.csv";
			file = new File(CustomLogger.getLogDirectory().concat("/Remmitance_Files/"), fileName);
			date = date.plusSeconds(1);
		} while (file.exists());
		file.getParentFile().mkdir();

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write("HDR|QA|PMTCTRL|PMT_E_PMTCTRL_PASSYS_7001_D|P01AWU410|0c131710-a94c-404f-850d-2a86ab3e0fd7\n");
			bw.write("DTL|" + Math.round(999999 * Math.random()) + "|PMT|" + date.format(DATE_PATTERN) + "|" + date.format(TIME_PATTERN) + "|" + paymentSystem
					+ "||55031|47204|CL0|USER0|CSIIB|PA|" + state + "SS|" + policyNo + "|600010703|" + state + "|" + date.format(DATE_PATTERN) + "|612284964181078|" + amount.toPlaingString()
					+ "|CHCK||||||||1813|Y\n");
			bw.write("TRL|" + file.getName() + "|" + date.format(DATE_TIME_PATTERN) + "|20204d22-0905-4a25-bef3-9d0094fdd342|1|1|" + amount.toPlaingString() + "\n");
			bw.flush();
		} catch (IOException e) {
			throw new IstfException(e);
		}
		return file;
	}

	public static synchronized void copyRemittanceFileToServer(File file) {
		if (file == null)
			throw new IstfException("Remmitance file is NULL");
		RemoteHelper.get().uploadFile(file.getAbsolutePath(), REMITTANCE_REMOTE_PATH + file.getName());
	}
}
