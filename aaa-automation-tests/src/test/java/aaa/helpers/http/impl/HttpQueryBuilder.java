package aaa.helpers.http.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import toolkit.utils.logging.CustomLogger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class HttpQueryBuilder {

	private static final String PARAMS_FILE_DIR = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "http" + File.separator;
	private static final String QUERY_STRING_DELIMITER = "&";
	private static final String LINE_DELIMITER = "---";

	private List<List<HttpQueryParam>> params = new ArrayList<List<HttpQueryParam>>();

	protected static Logger log = CustomLogger.getInstance();

	public void readParamsFile(String paramsFileName) throws IOException {
		params = new ArrayList<List<HttpQueryParam>>();
		Scanner scanner = new Scanner(new FileReader(PARAMS_FILE_DIR + paramsFileName));
		List<HttpQueryParam> paramsBlock = new ArrayList<HttpQueryParam>();
		String line;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (line.startsWith(LINE_DELIMITER)) {
				params.add(paramsBlock);
				paramsBlock = new ArrayList<HttpQueryParam>();
			} else {
				paramsBlock.add(new HttpQueryParam(line));
			}
		}
		params.add(paramsBlock);
		scanner.close();
	}

	public void setParams(List<List<HttpQueryParam>> params) {
		this.params = params;
	}

	public int getRequestsListSize() {
		return params.size();
	}

	public String buildQueryString(int index, Map<String, String> mapping){
		return buildQueryString(index, mapping, null);
	}

	public String buildQueryString(int index, Map<String, String> mapping, String content) {
		List<String> mergedParams = new ArrayList<String>();

		for (HttpQueryParam queryParam : params.get(index)) {
			if (queryParam.isMacros()) {
				String replacement = "";
				try {
					if (queryParam.getElement() == HttpQueryParam.Element.DEFAULT) {
						replacement = mapping.get(queryParam.getValue());
					} else if (queryParam.getElement() == HttpQueryParam.Element.COMBOBOX) {
						replacement = HtmlParser.getValueFromSelectBox(content, queryParam.getValue(), mapping.get(queryParam.getValue()));
					} else if (queryParam.getElement() == HttpQueryParam.Element.RADIOBOOL) {
						replacement = mapping.get(queryParam.getValue()).trim().equalsIgnoreCase("No") ? "false" : "true";
					} else if (queryParam.getElement() == HttpQueryParam.Element.RADIO) {
						replacement = HtmlParser.getValueFromRadioGroup(content, queryParam.getValue(), mapping.get(queryParam.getValue()));
					} else if (queryParam.getElement() == HttpQueryParam.Element.FIELD) {
						replacement = HtmlParser.getValueFromResponse(content, queryParam.getName());
					}
				} catch (Exception e) {
					log.debug("Cannot extract value for " + queryParam.getName() + ". Trying get value from response content");
					try {
						replacement = HtmlParser.getValueFromResponse(content, queryParam.getName());
					} catch (Exception ex) {
						log.debug("Cannot extract value for " + queryParam.getName() + " from reponse content.");
					}
				}
				queryParam.replaceValue(StringUtils.defaultString(replacement));
			}
			mergedParams.add(queryParam.mergeNameAndValue());
		}
		return StringUtils.join(mergedParams, QUERY_STRING_DELIMITER);
	}
}
