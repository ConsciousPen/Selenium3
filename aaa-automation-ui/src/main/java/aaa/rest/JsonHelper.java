/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.rest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.minidev.json.JSONArray;
import toolkit.datax.TestData;
import toolkit.datax.impl.JSONDataProvider;
import toolkit.datax.impl.SimpleDataProvider;

public class JsonHelper {

	public static String getValue(String jsonPath, Response response) {
		DocumentContext jsonContext = JsonPath.parse(response.readEntity(String.class));

		Object value = jsonContext.read(jsonPath);
		if (value instanceof JSONArray) {
			value = ((JSONArray) value).get(0);
		}

		return String.valueOf(value);
	}

	public static boolean pathExist(String jsonPath, Response response) {
		DocumentContext jsonContext = JsonPath.parse(response.readEntity(String.class));
		boolean pathExists = false;

		try {
			Object context = jsonContext.read(jsonPath);
			if (context instanceof JSONArray) {
				pathExists = ((JSONArray) context).size() > 0;
			}
		} catch (PathNotFoundException ex) {
		}

		return pathExists;
	}

	public static TestData transferDataToWrite(Response response) {
		JSONObject responseJsonWriteJson = new JSONObject(response.readEntity(String.class));

		Writer writer = null;
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile("tempfile", ".json");
			writer = new FileWriter(tmpFile);
			responseJsonWriteJson.write(writer);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			//log.error("Error to create temp file", e);
		}
		TestData result = new SimpleDataProvider().adjust("TestData", new JSONDataProvider(tmpFile.getParent(), false)
				.getTestData(FilenameUtils.removeExtension(tmpFile.getName())));

		return result;
	}
}
