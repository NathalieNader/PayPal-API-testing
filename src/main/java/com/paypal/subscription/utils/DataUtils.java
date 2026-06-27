package com.paypal.subscription.utils;

import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;

public class DataUtils {

    public static String testDataPath = "src/test/resources/testdata/";


    public static String getJsonData(String fileName, String field) {
        try {
            FileReader File = new FileReader(testDataPath + fileName + ".json");
            JsonElement jsonFile = JsonParser.parseReader(File);

            return jsonFile.getAsJsonObject().get(field).getAsString();
        } catch (Exception e) {
            LogsUtils.error("Faild to retrieve Json File ", e.toString());
        }
        return "";
    }

    public static JsonObject getJsonObject(String fileName) {
        try {
            FileReader File = new FileReader(testDataPath + fileName + ".json");
            JsonElement jsonFile = JsonParser.parseReader(File);

            return jsonFile.getAsJsonObject();
        } catch (Exception e) {
            LogsUtils.error("Failed to retrieve Json File ", e.toString());
        }
        return null;
    }



}
