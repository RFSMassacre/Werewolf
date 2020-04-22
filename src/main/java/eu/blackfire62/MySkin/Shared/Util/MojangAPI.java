/*
 * Decompiled with CFR 0.139.
 */
package eu.blackfire62.MySkin.Shared.Util;

import eu.blackfire62.MySkin.Shared.SkinProperty;
import eu.blackfire62.MySkin.Shared.Util.MojangAPIException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;

public class MojangAPI {
    private static final String uuidurl = "https://api.mojang.com/users/profiles/minecraft/";
    private static final String skinurl = "https://sessionserver.mojang.com/session/minecraft/profile/";

    public static UUID getUUID(String name) throws MojangAPIException {
        String output = MojangAPI.readURL(uuidurl + name);
        if (output == null || output.isEmpty()) {
            throw new MojangAPIException(MojangAPIException.Reason.NOT_PREMIUM);
        }
        if (output.contains("\"error\"")) {
            throw new MojangAPIException(MojangAPIException.Reason.RATE_LIMITED);
        }
        String dashlessUUID = MojangAPI.advancedSubstr(output, "{\"id\":\"", "\",\"name\"");
        if (dashlessUUID == null) {
            throw new MojangAPIException(MojangAPIException.Reason.UNKNOWN);
        }
        Pattern pattern = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");
        return UUID.fromString(pattern.matcher(dashlessUUID).replaceAll("$1-$2-$3-$4-$5"));
    }

    public static SkinProperty getSkinProperty(UUID uuid) throws MojangAPIException {
        String output = MojangAPI.readURL(skinurl + uuid.toString().replace("-", "") + "?unsigned=false");
        if (output == null || output.isEmpty()) {
            throw new MojangAPIException(MojangAPIException.Reason.UNKNOWN);
        }
        if (output.contains("{\"error\":\"")) {
            throw new MojangAPIException(MojangAPIException.Reason.RATE_LIMITED);
        }
        String valbeg = "s\",\"value\":\"";
        String mid = "\",\"signature\":\"";
        String sigend = "\"}]}";
        String value = MojangAPI.advancedSubstr(output, valbeg, mid);
        String signature = MojangAPI.advancedSubstr(output, mid, sigend);
        if (value == null || value.isEmpty() || signature == null || signature.isEmpty()) {
            throw new MojangAPIException(MojangAPIException.Reason.UNKNOWN);
        }
        return new SkinProperty(value, signature);
    }

    private static String readURL(String url) throws MojangAPIException {
        try {
            String line;
            HttpsURLConnection con = (HttpsURLConnection)new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "MySkin");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);
            con.setDoOutput(false);
            con.setDoInput(true);
            int response = con.getResponseCode();
            if (response == 429) {
                throw new MojangAPIException(MojangAPIException.Reason.RATE_LIMITED);
            }
            if (response == 503) {
                throw new MojangAPIException(MojangAPIException.Reason.SERVER_UNAVAILABLE);
            }
            StringBuilder output = new StringBuilder();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((line = in.readLine()) != null) {
                output.append(line);
            }
            in.close();
            return output.toString();
        }
        catch (SocketTimeoutException e) {
            throw new MojangAPIException(MojangAPIException.Reason.SERVER_UNAVAILABLE);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String advancedSubstr(String base, String begin, String end) {
        try {
            Pattern patbeg = Pattern.compile(Pattern.quote(begin));
            Pattern patend = Pattern.compile(Pattern.quote(end));
            int resbeg = 0;
            int resend = base.length() - 1;
            Matcher matbeg = patbeg.matcher(base);
            while (matbeg.find()) {
                resbeg = matbeg.end();
            }
            Matcher matend = patend.matcher(base);
            while (matend.find()) {
                resend = matend.start();
            }
            return base.substring(resbeg, resend);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

