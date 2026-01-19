package com.dermememann.elytra.modmenu;

import com.dermememann.elytra.Elytra;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.terraformersmc.modmenu.api.UpdateChecker;
import com.terraformersmc.modmenu.api.UpdateInfo;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UpdateCheckerImpl implements UpdateChecker {

    @Override
    public @Nullable UpdateInfo checkForUpdates() {
        UpdateInfoImpl updateInfo = new UpdateInfoImpl();
        if (FabricLoader.getInstance().getModContainer(Elytra.MOD_ID).isPresent()) {
            String currentVersionString = FabricLoader.getInstance().getModContainer(Elytra.MOD_ID).get().getMetadata().getVersion().getFriendlyString();

            String BASE_API_URL = "https://api.github.com/repos/";
            String OWNER = "fsoel";
            String REPO = "alwayselytra";
            String apiUrl = BASE_API_URL + OWNER + "/" + REPO + "/releases/latest";

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/vnd.github+json");
                connection.setRequestProperty("User-Agent", "Fabric AlwaysElytra Mod");

                try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                    Gson gson = new Gson();
                    Release release = gson.fromJson(reader, Release.class);

                    Version latestVersion = Version.parse(release.tagName.replace("v", ""));
                    Version currentVersion = Version.parse(currentVersionString);

                    Elytra.LOGGER.info("Current: {}, Latest: {}", currentVersion, latestVersion);

                    if (latestVersion.isNewer(currentVersion)) {
                        updateInfo.setUpdateAvailable(true);
                        updateInfo.setDownloadLink(release.assets.getFirst().browserDownloadUrl);
                    }
                    return updateInfo;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (IOException e) {
                Elytra.LOGGER.error("Failed to check for updates", e);
                return updateInfo;
            }
        }
        return updateInfo;
    }

    private static class Release {
        List<Asset> assets;
        @SerializedName("tag_name")
        String tagName;
    }

    private static class Asset {
        @SerializedName("browser_download_url")
        String browserDownloadUrl;
    }

    private static class Version {
        int major;
        int minor;
        int micro;

        public Version(int major, int minor, int micro) {
            this.major = major;
            this.minor = minor;
            this.micro = micro;
        }

        public static Version parse(String versionString) {
            String[] parts = versionString.split("\\.");
            int major = parts.length > 0 ? Integer.parseInt(parts[0]) : 0;
            int minor = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            int micro = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            return new Version(major, minor, micro);
        }

        public boolean isNewer(Version version) {
            if (this.major != version.major) {
                return this.major > version.major;
            }
            if (this.minor != version.minor) {
                return this.minor > version.minor;
            }
            return this.micro > version.micro;
        }

        @Override
        public String toString() {
            return major + "." + minor + "." + micro;
        }
    }
}
