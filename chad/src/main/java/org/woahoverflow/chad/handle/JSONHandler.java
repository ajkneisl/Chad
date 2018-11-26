package org.woahoverflow.chad.handle;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.woahoverflow.chad.core.ChadVar;
import org.woahoverflow.chad.handle.ui.ChadError;
import org.woahoverflow.chad.handle.ui.UIHandler;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
                ChadVar.UI_DEVICE.addLog("Created Chad Directory : " + dir.mkdirs(), UIHandler.LogLevel.INFO);
            File bot = new File(dir + "\\bot.json");
            if (!new File(dir + "\\bot.json").exists())
            {
                ChadVar.UI_DEVICE.addLog("Created Bot Directory : " + bot.createNewFile(), UIHandler.LogLevel.INFO);
                org.json.JSONObject obj = new org.json.JSONObject();
                obj.put("token", "");
                obj.put("steam_api_token", "");
                obj.put("uri_link", "");
                obj.put("unstable", false);
                try (FileWriter filew = new FileWriter(bot)) {
                    filew.write(obj.toString());
                    filew.flush();
                } catch (IOException e) {
                    ChadError.throwError("There was an throwError creating files during startup!", e);
                }
            }
            File imgdir = new File(System.getenv("appdata") + "\\chad\\imgcache");
            if (!imgdir.exists())
                ChadVar.UI_DEVICE.addLog("Created Temp Image Directory : " + imgdir.mkdirs(), UIHandler.LogLevel.INFO);
            File dir2 = new File(System.getenv("appdata") + "\\chad\\catpictures");
            if (!dir2.exists())
                ChadVar.UI_DEVICE.addLog("Created Cat Pictures Directory : " + dir2.mkdirs(), UIHandler.LogLevel.INFO);
        } catch (IOException e)
        {
            ChadError.throwError("There was an throwError creating files during startup!", e);
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
        ChadVar.UI_DEVICE.addLog("readFile failed, returning null", UIHandler.LogLevel.SEVERE);
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
