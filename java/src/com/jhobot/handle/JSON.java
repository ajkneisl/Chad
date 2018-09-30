package com.jhobot.handle;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.*;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sx.blah.discord.handle.obj.IGuild;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class JSON
{
    // Handles JSON data, including the bot.json files and reading from network

    public static String get(String entry)
    {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(System.getenv("appdata") + "\\jho\\bot.json"));
            JSONObject jsonObject  = (JSONObject) obj;
            return (String) jsonObject.get(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void set(String object, String input) throws IOException
    {
        File file = new File(System.getenv("appdata") + "\\jho\\bot.json");
        String jsonString = Files.toString(file, Charsets.UTF_8);
        JsonElement jelement = new JsonParser().parse(jsonString);
        JsonObject jobject = jelement.getAsJsonObject();
        jobject.addProperty(object, input);
        Gson gson = new Gson();

        String resultingJson = gson.toJson(jelement);

        Files.write(resultingJson, file, Charsets.UTF_8);
    }


    public static org.json.JSONObject read(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            org.json.JSONObject json = new org.json.JSONObject(sb.toString());
            return json;
        } finally {
            is.close();
        }
    }
}
