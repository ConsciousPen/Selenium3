package aaa.helpers.conversion;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import toolkit.utils.datetime.DateTimeUtils;
import toolkit.utils.logging.CustomLogger;
import toolkit.verification.CustomAssert;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.ssh.RemoteHelper;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.mifmif.common.regex.Generex;

public class ConversionUtils {

	protected static Logger log = LoggerFactory.getLogger(ConversionUtils.class);

	public static String importPolicy(ConversionPolicyData conversionData) {
		return importPolicy(conversionData, null);
	}

	public static String importPolicy(ConversionPolicyData conversionData, ITestContext context) {
		File importFile = prepareXML(conversionData);
		RemoteHelper.uploadFile(importFile.getAbsolutePath(), conversionData.getConversionType().getRemoteImportFolder() + importFile.getName());
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusHours(1));
		JobUtils.executeJob(conversionData.getConversionType().getJob());
		String policyNum = verifyResponseSuccessAndGetNumber(conversionData.getConversionType(), importFile.getName(), context);
		log.info(String.format("Conversion policy with type %s imported with number %s and effective date %s"
			, conversionData.getConversionType().name(), policyNum, conversionData.getEffectiveDate()));
		return policyNum;
	}

	protected static File prepareXML(ConversionPolicyData conversionData) {
		String newName = String.format("%s_%s_%s-%s.xml", conversionData.getConversionType().name(),
			conversionData.getFile().getName().substring(0, conversionData.getFile().getName().lastIndexOf(".")),
			LocalDateTime.now().format(DateTimeUtils.TIME_STAMP), new Generex("\\d{4}").random());
		File changedFile = new File(CustomLogger.getLogDirectory() + File.separator + "uploded_files", newName);
		changedFile.getAbsoluteFile().getParentFile().mkdir();

		try {
			XPath xpath = XPathFactory.newInstance().newXPath();
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			Document document = docFactory.newDocumentBuilder().parse(conversionData.getFile());

			for (String key : conversionData.getValues().keySet()) {
				NodeList nodes = (NodeList) xpath.compile(key).evaluate(document, XPathConstants.NODESET);
				for (int i = 0; i < nodes.getLength(); i++) {
					nodes.item(i).setTextContent(conversionData.getValues().get(key));
				}
			}

			Files.createFile(Paths.get(changedFile.getAbsoluteFile().getAbsolutePath()));
			FileWriterWithEncoding fileWriter = new FileWriterWithEncoding(changedFile, "UTF8");
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.transform(new DOMSource(document), new StreamResult(fileWriter));
		} catch (Exception e) {
			throw new RuntimeException("Can't change value in imported data", e);
		}

		return changedFile;
	}

	protected static String verifyResponseSuccessAndGetNumber(ConversionType conversionType, String fileName, ITestContext context) {
		String responseFilePath = conversionType.getRemoteResponseFolder() + fileName;
		String downloadTo = CustomLogger.getLogDirectory() + File.separator + "downloaded_files" + File.separator + fileName;
		RemoteHelper.downloadFileWithWait(responseFilePath, downloadTo, 30000);
		if (context != null) {
			context.setAttribute("attachment", downloadTo);
		}

		// try {
		// log.debug("Response file data\n" + FileUtils.readFileToString(new File(downloadTo)));
		// } catch (Exception ignore) {
		// }

		XPath xpath = XPathFactory.newInstance().newXPath();
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		Document document;
		try {
			document = docFactory.newDocumentBuilder().parse(downloadTo);
		} catch (Exception e) {
			throw new AssertionError("Can't read downloaded response file: " + downloadTo);
		}

		NodeList successNodes;
		try {
			successNodes = (NodeList) xpath.compile("//importResponse/*[(self::status or self::importStatus) and .='Success']").evaluate(document, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new AssertionError("Error verifying importStatus in response file: " + downloadTo, e);
		}

		if (successNodes.getLength() < 1) {
			// try to find reason of import failure in response file
			StringBuilder message = new StringBuilder();
			try {
				NodeList nodes = (NodeList) xpath.compile("//importResponse/message").evaluate(document, XPathConstants.NODESET);
				message.append(nodes.item(0).getTextContent()).append(": ");

				nodes = (NodeList) xpath.compile("//statusMessage[status[.='Failure']]/message").evaluate(document, XPathConstants.NODESET);
				for (int i = 0; i < nodes.getLength(); i++) {
					String reason = nodes.item(i).getTextContent();
					if (!message.toString().contains(reason))
						message.append(reason).append("; ");
				}

				message.setLength(message.length() - 2);
				if (message.length() > 1000) {
					message.setLength(1000);
					message.append("...");
				}
			} catch (Exception e) {
				log.info("Can't find reason of import failure. " + e.getMessage());
			}
			CustomAssert.assertFalse(String.format("Response file %1$s doesn't have Success status. Reason: %2$s.",
				fileName, message.toString()), successNodes.getLength() < 1);
		}

		try {
			NodeList nodes = (NodeList) xpath.compile("//policyNumber").evaluate(document, XPathConstants.NODESET);
			return nodes.item(0).getTextContent();
		} catch (XPathExpressionException e) {
			throw new AssertionError("Error getting policy number in response file: " + fileName, e);
		}
	}
}
