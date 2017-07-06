/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.rest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import toolkit.datax.TestData;
import toolkit.datax.impl.JSONDataProvider;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.rest.ResponseWrapper;

public class JsonHelper {

    public static String getValue(String jsonPath, ResponseWrapper response) {
        DocumentContext jsonContext = JsonPath.parse(response.getResponse().readEntity(String.class));

        Object value = jsonContext.read(jsonPath);
        if (value instanceof net.minidev.json.JSONArray) {
            value = ((net.minidev.json.JSONArray) value).get(0);
        }

        return String.valueOf(value);
    }


    public static boolean pathExist(String jsonPath, ResponseWrapper response) {
        DocumentContext jsonContext = JsonPath.parse(response.getResponse().readEntity(String.class));
        boolean pathExists = false;

        try {
            Object context = jsonContext.read(jsonPath);
            if (context instanceof net.minidev.json.JSONArray) {
                pathExists = ((net.minidev.json.JSONArray) context).size() > 0;
            }
        } catch (PathNotFoundException ex) {
        }

        return pathExists;
    }

    public static TestData transferDataToWrite(ResponseWrapper responseInit) {
        JSONObject responseJsonWriteJson = new JSONObject(responseInit.getResponse().readEntity(String.class));

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
