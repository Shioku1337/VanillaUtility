package com.github.shioku.vanillautility.updatechecker;

import com.github.shioku.vanillautility.VanillaUtility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {

  private static final String BASE_URL = "https://api.github.com/repos";
  private static final String OWNER = "Shioku1337";
  private static final String REPO = "VanillaUtility";

  public static boolean checkUpdates(VanillaUtility plugin) {
    String requestURL = BASE_URL + "/" + OWNER + "/" + REPO + "/releases/latest";

    String response = sendHTTPGETRequest(requestURL);

    JsonObject json = new Gson().fromJson(response, JsonObject.class);

    try {
      String removeVersionString = json.get("tag_name").getAsString();
      VersionNumber remoteVersion = VersionNumber.fromString(removeVersionString);

      String localVersionString = plugin.getDescription().getVersion();
      VersionNumber localVersion = VersionNumber.fromString(localVersionString);

      if (remoteVersion.major() > localVersion.major()) return true;
      if (remoteVersion.minor() > localVersion.minor()) return true;
      if (remoteVersion.patch() > localVersion.patch() && plugin.getConfig().getBoolean("failOnPatch")) return true;
    } catch (Exception e) {
      plugin.getLogger().severe("Failed to check Updates! Probably invalid tag name. Developer's fault.");
    }

    return false;
  }

  private static String sendHTTPGETRequest(String requestURL) {
    HttpURLConnection connection = null;
    StringBuilder content = new StringBuilder();

    try {
      URL url = new URL(requestURL);
      connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
        for (String line; (line = reader.readLine()) != null;) {
          content.append(line);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return content.toString();
  }
}
