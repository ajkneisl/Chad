package com.jhobot.handle;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.*;
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
            File dir = new File(System.getenv("appdata") + "\\jho");
            if (!dir.exists())
                System.out.println("Created Jho Directory : " + dir.mkdirs());
            File bot = new File(dir + "\\bot.json");
            if (!new File(dir + "\\bot.json").exists())
            {
                System.out.println("Created Bot Directory : " + bot.createNewFile());
                org.json.JSONObject obj = new org.json.JSONObject();
                obj.put("token", "");
                obj.put("steam_api_token", "");
                obj.put("uri_link", "");
                obj.put("admins", "173495550467899402 274712215024697345 416399667094618124");
                try (FileWriter filew = new FileWriter(bot)) {
                    filew.write(obj.toString());
                    filew.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            File imgdir = new File(System.getenv("appdata") + "\\jho\\imgcache");
            if (!imgdir.exists())
                System.out.println("Created Temp Image Directory : " + imgdir.mkdirs());
            File dir2 = new File(System.getenv("appdata") + "\\jho\\catpictures");
            if (!dir2.exists())
                System.out.println("Created Cat Pictures Directory : " + dir2.mkdirs());
        } catch (IOException e)
        {
            System.out.println("There was an error creating files during startup!");
            e.printStackTrace();
        }
        return this;
    }

    public String get(String entry)
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

    public void set(String object, String input) throws IOException
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
            Object obj = parser.parse(new FileReader(System.getenv("appdata") + "\\jho\\" + file));
            org.json.JSONObject jsonObject  = (org.json.JSONObject) obj;
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("readFile failed, returning null");
        return null;
    }

    @SuppressWarnings("deprecation")
    public void writeFile(String filep, String object, String input) throws IOException
    {
        File file = new File(System.getenv("appdata") + "\\jho\\" + filep);
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
