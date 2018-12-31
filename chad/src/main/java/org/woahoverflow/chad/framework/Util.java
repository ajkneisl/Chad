package org.woahoverflow.chad.framework;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.stream.Collectors;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.woahoverflow.chad.core.ChadBot;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.RequestBuffer;

/**
 * The Utility class for Chad
 *
 * @author sho, codebasepw
 * @since 0.6.3 B2
 */
public final class Util
{

    /**
     * The user agent
     */
    private static final String USER_AGENT = "Mozilla/5.0";

    /**
     * Gets the current timestamp
     *
     * @return The current timestamp
     */
    public static synchronized String getTimeStamp()
    {
        return new SimpleDateFormat("MM/dd/yyyy hh:mm").format(Calendar.getInstance().getTime());
    }

    /**
     * Gets a String from an http
     *
     * @param url The URL to request
     * @return The gotten String
     */
    public static synchronized String httpGet(String url) {
        try {
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            // optional default is GET
            con.setRequestMethod("GET");
            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);
            int responseCode = con.getResponseCode();
            if (responseCode != 200)
            {
                ChadBot.getLogger().error("Failed to send a request to url {}\nResponse Code : {}", url, responseCode);
                return "";
            }
            ChadBot.getLogger().info("Fulfilled a request at URL : {}", url);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String response = in.lines().collect(Collectors.joining());
            in.close();
            return response;
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public static String fixEnumString(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * Flips a coin
     *
     * @return Returns either true or false, it's randomized
     */
    public static boolean coinFlip()
    {
        Random random = new Random();
        int flip = -1;
        for (int i = 0; i < 100; i++)
            flip = random.nextInt(2);
        return flip == 0;
    }

    /**
     * Checks if a guild exists within the Client
     *
     * @param cli The client
     * @param guild The guild's ID
     * @return If it exists/still exists
     */
    public static synchronized boolean guildExists(IDiscordClient cli, Long guild)
    {
        return RequestBuffer.request(cli::getGuilds).get().stream().anyMatch(g -> g.getLongID() == guild);
    }

    private static String getCurrentDateTime()
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        System.out.println(dateFormat.format(new Date()));
        return dateFormat.format(new Date());
    }

    public static long howOld(long searchTimestamp)
    {
        return Math.abs(System.currentTimeMillis() - searchTimestamp);
    }

    /**
     * Turns ms into a seconds, day and hours format
     *
     * @param ms The time
     * @return The formatted string`
     */
    public static String fancyDate(long ms) {
        String response = "";

        long seconds = ms/1000;

        if (!(seconds > 60)) {
            // Assuming there's multiple seconds
            return seconds + " seconds";
        }

        long minutes = seconds/60;

        if (!(minutes >= 60)) {
            response = minutes > 1 ? String.format("%s minutes %s seconds", minutes, seconds-(minutes*60)) : String.format("%s minute %s seconds", minutes, seconds-(minutes*60));
            return response;
        }

        long hours = minutes/60;
        long hoursMinutes = minutes-(hours*60);

        if (!(hours >= 24)) {
            response += hours > 1 ? String.format("%s hours ", hours) : String.format("%s hour ", hours);
            response += hoursMinutes > 1 ? String.format("%s minutes", hoursMinutes) : String.format("%s minute", hoursMinutes);
            return response;
        }

        long days = hours/24;
        long hoursDays = hours - (days*24);

        response += days > 1 ? String.format("%s days ", days) : String.format("%s day ", days);
        response += hoursDays > 1 ? String.format("%s hours", hoursDays) : String.format("%s hour", hoursDays);
        return response;
    }

}
