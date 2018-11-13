package org.woahoverflow.chad.handle;

import org.woahoverflow.chad.handle.logging.LogLevel;
import org.woahoverflow.chad.handle.ui.ChadException;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.*;
import org.woahoverflow.chad.core.ChadVar;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;

@SuppressWarnings({"ALL", "deprecation"})
public class JSONHandler
{
    // Handles JSONHandler data, including the bot.json files and reading from network
    
    public JSONHandler()
    {
        super();
    }
    
    public JSONHandler forceCheck()
    {
        try
        {
            File dir = new File(System.getenv("appdata") + "\\chad");
            if (!dir.exists())
                ChadVar.UI_HANDLER.addLog("Created Chad Directory : " + dir.mkdirs(), LogLevel.INFO);
            File bot = new File(dir + "\\bot.json");
            if (!new File(dir + "\\bot.json").exists())
            {
                ChadVar.UI_HANDLER.addLog("Created Bot Directory : " + bot.createNewFile(), LogLevel.INFO);
                org.json.JSONObject obj = new org.json.JSONObject();
                obj.put("token", "");
                obj.put("steam_api_token", "");
                obj.put("uri_link", "");
                try (FileWriter filew = new FileWriter(bot)) {
                    filew.write(obj.toString());
                    filew.flush();
                } catch (IOException e) {
                    ChadException.error("There was an error creating files during startup!", e);
                }
            }
            File imgdir = new File(System.getenv("appdata") + "\\chad\\imgcache");
            if (!imgdir.exists())
                ChadVar.UI_HANDLER.addLog("Created Temp Image Directory : " + imgdir.mkdirs(), LogLevel.INFO);
            File dir2 = new File(System.getenv("appdata") + "\\chad\\catpictures");
            if (!dir2.exists())
                ChadVar.UI_HANDLER.addLog("Created Cat Pictures Directory : " + dir2.mkdirs(), LogLevel.INFO);
        } catch (IOException e)
        {
            ChadException.error("There was an error creating files during startup!", e);
        }
        return this;
    }

    public String get(String entry)
    {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(System.getenv("appdata") + "\\chad\\bot.json"));
            JSONObject jsonObject  = (JSONObject) obj;
            return (String) jsonObject.get(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void set(String object, String input) throws IOException
    {
        File file = new File(System.getenv("appdata") + "\\chad\\bot.json");
        String jsonString = Files.toString(file, Charsets.UTF_8);
        JsonElement jelement = new JsonParser().parse(jsonString);
        JsonObject jobject = jelement.getAsJsonObject();
        jobject.addProperty(object, input);
        Gson gson = new Gson();

        String resultingJson = gson.toJson(jelement);

        Files.write(resultingJson, file, Charsets.UTF_8);
    }


    public org.json.JSONObject read(String url) throws JSONException {
        return new org.json.JSONObject(Util.httpGet(url));
    }
    public org.json.JSONArray readArray(String url) throws JSONException {
        return new org.json.JSONArray(Util.httpGet(url));
    }

    public org.json.JSONObject readFile(String file)
    {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(System.getenv("appdata") + "\\chad\\" + file));
            org.json.JSONObject jsonObject  = (org.json.JSONObject) obj;
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        ChadVar.UI_HANDLER.addLog("readFile failed, returning null", LogLevel.SEVERE);
        return null;
    }

    @SuppressWarnings("deprecation")
    public void writeFile(String filep, String object, String input) throws IOException
    {
        File file = new File(System.getenv("appdata") + "\\chad\\" + filep);
        String jsonString = Files.toString(file, Charsets.UTF_8);
        JsonElement jelement = new JsonParser().parse(jsonString);
        JsonObject jobject = jelement.getAsJsonObject();
        jobject.addProperty(object, input);
        Gson gson = new Gson();

        String resultingJson = gson.toJson(jelement);

        //noinspection deprecation
        Files.write(resultingJson, file, Charsets.UTF_8);
    }
}
