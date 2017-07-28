package aaa.helpers.http.impl;

import java.io.IOException;
import java.net.URLDecoder;

import aaa.helpers.http.impl.HttpHelper;

public class HtmlParser {

	private HtmlParser() {
	}

	public static String getValueFromSelectBox(String source, String label, String value)
			throws IOException {
		String subsString;
		if (source.indexOf(">" + label + "</label>") != -1) {
			int start = source.indexOf(">" + label + "</label>");
			int end = source.indexOf("</tr>", start);
			subsString = source.substring(start, end);
		} else {
			subsString = source;
		}
		String regex = String.format(
				"<option([^>]+)value=\"([^\"]+)\"([^>]+)?>%s<\\/option>", value.replace("$", "."));
		return HttpHelper.find(subsString, regex, 2);
	}

	public static String getValueFromRadioGroup(String source, String label, String value)
			throws IOException {
		int start = source.indexOf(">" + label + "</label>");
		int end = source.indexOf("</tr>", start);
		String subString = source.substring(start, end);
		String regex = String.format(
				"(<input([^>]+)value=\"([^\"]+)\"([^>]+)\\/><label([^>]+)>([^<]+)?%s<\\/label>)",
				value.replace("$", "."));
		return HttpHelper.find(subString, regex, 3);
	}

	public static String getValueFromResponse(String source, String fieldName)
			throws IOException {
		fieldName = URLDecoder.decode(fieldName, "UTF-8");

		String[] fieldAttrs = HttpHelper.find(source,"<(([^>]+)name=\"" + fieldName + "\"([^>]+))>")
				.split("\\s+");
		String fieldTag = fieldAttrs[0].toLowerCase();
		if (fieldTag.equals("select")) {
			int start = source.indexOf("name=\""  + fieldName + "\"");
			int end = source.indexOf("</select>", start);
			return HttpHelper.find(
					source.substring(start, end),
					"<option([^>]+)value=\"([^>]+)\"([^>]+)selected=\"selected\"([^>]+)?>",
					2);
		} else if (fieldTag.endsWith("input")) {
			String inputType = "";
			for (int i = 0; i < fieldAttrs.length; i ++) {
				if (fieldAttrs[i].toLowerCase().startsWith("type")) {
					inputType = fieldAttrs[i].toLowerCase()
							.replace("type=\"", "").replace("\"", "");
					break;
				}
			}
			if (inputType.equals("radio")) {
				return HttpHelper.find(source,
						"<([^>]+)name=\"" + fieldName + "\"([^>]+)checked=\"checked\"([^>]+)" +
								"value=\"([^\"]+)\"([^>]+)>", 4);
			}  else {
				return HttpHelper.find(source,
						"<([^>]+)name=\"" + fieldName + "\"([^>]+)value=\"([^\"]+)\"([^>]+)>", 3);
			}
		}
		return "";
	}

	public static String getFlowUrl(String source) throws IOException {
		String regex = "id=\"headerForm\" name=\"headerForm\" method=\"post\" +" +
				"action=\"([^\"]+)\"";
		return HttpHelper.find(source, regex).replace("&amp;", "&");
	}

	public static String getQuoteId(String source) throws IOException {
		String regex =
				"<span id=\"producContextInfoForm:policyDetail_policyNumTxt\">([^<]+)<\\/span>";
		return HttpHelper.find(source, regex);
	}
}