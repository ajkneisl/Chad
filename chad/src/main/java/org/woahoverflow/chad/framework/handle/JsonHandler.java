package org.woahoverflow.chad.framework.handle;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;

import org.apache.commons.lang3.SystemUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.woahoverflow.chad.core.ChadBot;
import org.woahoverflow.chad.framework.Util;
import org.woahoverflow.chad.framework.ui.ChadError;
import org.woahoverflow.chad.framework.ui.UIHandler;

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
     * Private Constructor
     */
    private JsonHandler()
    {
        super();
    }

    /**
     * Makes sure all of the files exist
     *
     * @return The handle
     */
    private synchronized JsonHandler forceCheck()
    {
        if (SystemUtils.IS_OS_WINDOWS) {
            ChadBot.getLogger().debug("Checking files as if OS is Windows", UIHandler.LogLevel.INFO);
            if (!new File(System.getenv("appdata") + "\\chad").exists())
                ChadBot.getLogger().debug("Created Chad Directory : " + new File(System.getenv("appdata") + "\\chad").mkdirs());
            if (!new File(new File(System.getenv("appdata") + "\\chad") + "\\bot.json").exists())
            {
                File bot = new File(new File(System.getenv("appdata") + "\\chad") + "\\bot.json");
                try {
                    ChadBot.getLogger().debug("Created Bot JSON : " + bot.createNewFile());
                } catch (IOException e) {
                    ChadError.throwError("Error creating bot JSON!", e);
                }

                org.json.JSONObject obj = new org.json.JSONObject();
                obj.put("token", "");
                obj.put("steam_api_key", "");
                obj.put("uri_link", "");
                obj.put("youtube_api_key", "");
                try (FileWriter fileWriter = new FileWriter(bot)) {
                    fileWriter.write(obj.toString());
                    fileWriter.flush();
                } catch (IOException e) {
                    ChadError.throwError("There was an error creating files during startup!", e);
                }
            }
        }

        if (SystemUtils.IS_OS_LINUX) {
            ChadBot.getLogger().debug("Checking files as if OS is Linux", UIHandler.LogLevel.INFO);
            File dir = new File("/home/" + System.getProperty("user.name") + "/chad");
            if (!dir.exists())
                ChadBot.getLogger().debug("Created Chad Directory : " + dir.mkdirs());
            if (!new File(dir + "/bot.json").exists())
            {
                File bot = new File(dir + "/bot.json");
                try {
                    ChadBot.getLogger().debug("Created Bot JSON : " + bot.createNewFile());
                } catch (IOException e) {
                    ChadError.throwError("Error creating bot JSON!", e);
                }

                org.json.JSONObject obj = new org.json.JSONObject();
                obj.put("token", "");
                obj.put("steam_api_key", "");
                obj.put("uri_link", "");
                obj.put("youtube_api_key", "");
                try (FileWriter fileWriter = new FileWriter(bot)) {
                    fileWriter.write(obj.toString());
                    fileWriter.flush();
                } catch (IOException e) {
                    ChadError.throwError("There was an error creating files during startup!", e);
                }
            }
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
        File file = null;

        if (SystemUtils.IS_OS_WINDOWS)
            file = new File(System.getenv("appdata") + "\\chad\\bot.json");

        if (SystemUtils.IS_OS_LINUX)
            file = new File("/home/" + System.getProperty("user.name") + "/chad/bot.json");

        if (file == null)
            System.exit(1);

        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
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
        File file = null;

        if (SystemUtils.IS_OS_WINDOWS)
            file = new File(System.getenv("appdata") + "\\chad\\bot.json");

        if (SystemUtils.IS_OS_LINUX)
            file = new File("/home/" + System.getProperty("user.name") + "/chad/bot.json");

        if (file == null)
            System.exit(1);

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
