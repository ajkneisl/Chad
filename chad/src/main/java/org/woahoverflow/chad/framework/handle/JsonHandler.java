package org.woahoverflow.chad.framework.handle;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.woahoverflow.chad.framework.Util;
import org.woahoverflow.chad.framework.ui.ChadError;
import org.woahoverflow.chad.framework.ui.UIHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Handles all web and local JSON content
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public class JsonHandler
{

    /**
     * The global handle of the JSON handler
     */
    public static final JsonHandler handle = new JsonHandler().forceCheck();

    /**
     * Public Constructor
     */
    public JsonHandler()
    {
        super();
    }

    /**
     * Makes sure all of the files exist
     *
     * @return The handle
     */
    public synchronized JsonHandler forceCheck()
    {
        try
        {
            File dir = new File(System.getenv("appdata") + "\\chad");
            if (!dir.exists())
                UIHandler.handle.addLog("Created Chad Directory : " + dir.mkdirs(), UIHandler.LogLevel.INFO);
            if (!new File(dir + "\\bot.json").exists())
            {
                File bot = new File(dir + "\\bot.json");
                UIHandler.handle
                    .addLog("Created Bot Directory : " + bot.createNewFile(), UIHandler.LogLevel.INFO);
                org.json.JSONObject obj = new org.json.JSONObject();
                obj.put("token", "");
                obj.put("steam_api_token", "");
                obj.put("uri_link", "");
                obj.put("unstable", false);
                try (FileWriter fileWriter = new FileWriter(bot)) {
                    fileWriter.write(obj.toString());
                    fileWriter.flush();
                } catch (IOException e) {
                    ChadError.throwError("There was an throwError creating files during startup!", e);
                }
            }
            File imgDirectory = new File(System.getenv("appdata") + "\\chad\\imgcache");
            if (!imgDirectory.exists())
                UIHandler.handle
                    .addLog("Created Temp Image Directory : " + imgDirectory.mkdirs(), UIHandler.LogLevel.INFO);
            File dir2 = new File(System.getenv("appdata") + "\\chad\\catpictures");
            if (!dir2.exists())
                UIHandler.handle
                    .addLog("Created Cat Pictures Directory : " + dir2.mkdirs(), UIHandler.LogLevel.INFO);
        } catch (IOException e)
        {
            ChadError.throwError("There was an throwError creating files during startup!", e);
        }
        return this;
    }

    /**
     * Get an entry from the bot.json file
     *
     * @param entry The object to get
     * @return The retrieved object
     */
    @SuppressWarnings("all")
    public synchronized String get(String entry)
    {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new InputStreamReader(new FileInputStream(System.getenv("appdata") + "\\chad\\bot.json"), Charsets.UTF_8));
            JSONObject jsonObject  = (JSONObject) obj;
            return (String) jsonObject.get(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Sets an object in the bot.json file
     *
     * @param object The object to set
     * @param input The input to set the object to
     * @throws IOException With the Files.toString()
     */
    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public synchronized void set(String object, String input) throws IOException
    {
        File file = new File(System.getenv("appdata") + "\\chad\\bot.json");
        String jsonString = Files.toString(file, Charsets.UTF_8);
        JsonElement jElement = new JsonParser().parse(jsonString);
        JsonObject jObject = jElement.getAsJsonObject();
        jObject.addProperty(object, input);
        Gson gson = new Gson();

        String resultingJson = gson.toJson(jElement);

        Files.write(resultingJson, file, Charsets.UTF_8);
    }

    /**
     * Reads a JSONObject from a URL
     *
     * @param url The URL to read from
     * @return The JSONObject
     */
    public org.json.JSONObject read(String url) {
        if (url == null || url.isEmpty())
            return null;
        String httpGet = Util.httpGet(url);
        if (httpGet == null || httpGet.isEmpty())
            return null;
        return new org.json.JSONObject(httpGet);
    }

    /**
     * Reads a JSONArray from a URL
     *
     * @param url The URL to read from
     * @return The JSONArray
     */
    public org.json.JSONArray readArray(String url) {
        if (url == null || url.isEmpty())
            return null;
        String httpGet = Util.httpGet(url);
        if (httpGet == null || httpGet.isEmpty())
            return null;
        return new org.json.JSONArray(httpGet);
    }

    /**
     * Reads a local file into a JSONObject
     *
     * @param file The file to read from
     * @return The JSONObject
     */
    @SuppressWarnings("all")
    public synchronized org.json.JSONObject readFile(String file)
    {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new InputStreamReader(new FileInputStream(System.getenv("appdata") + "\\chad\\" + file), Charsets.UTF_8));
            org.json.JSONObject jsonObject  = (org.json.JSONObject) obj;
            return jsonObject;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        UIHandler.handle.addLog("readFile failed, returning null", UIHandler.LogLevel.SEVERE);
        return null;
    }

    /**
     * Writes to a JSON file
     *
     * @param fileString The file in the Chad directory
     * @param object The object
     * @param input The new value for the object
     * @throws IOException The Files.toString
     */
    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    public synchronized void writeFile(String fileString, String object, String input) throws IOException
    {
        File file = new File(System.getenv("appdata") + "\\chad\\" + fileString);
        String jsonString = Files.toString(file, Charsets.UTF_8);
        JsonElement jElement = new JsonParser().parse(jsonString);
        JsonObject jObject = jElement.getAsJsonObject();
        jObject.addProperty(object, input);
        Gson gson = new Gson();

        String resultingJson = gson.toJson(jElement);

        Files.write(resultingJson, file, Charsets.UTF_8);
    }
}
