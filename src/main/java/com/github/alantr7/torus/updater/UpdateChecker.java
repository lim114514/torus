package com.github.alantr7.torus.updater;

import com.github.alantr7.bukkitplugin.annotations.core.InvokePeriodically;
import com.github.alantr7.bukkitplugin.annotations.core.Singleton;
import com.github.alantr7.bukkitplugin.versions.Version;
import com.github.alantr7.torus.TorusPlugin;
import com.github.alantr7.torus.config.MainConfig;
import com.github.alantr7.torus.log.Category;
import com.github.alantr7.torus.log.TorusLogger;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Singleton
public class UpdateChecker {

    private static Version latestVersion;

    @InvokePeriodically(delay = 10L, interval = 20 * 60 * 12, limit = 1, sync = false)
    void checkForUpdates() {
        if (!MainConfig.ALLOW_UPDATE_CHECKS)
            return;

        try (InputStream is = new URL("https://api.spigotmc.org/legacy/update.php?resource=129842").openStream()) {
            String versionString = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            latestVersion = Version.from(versionString);

            if (TorusPlugin.getInstance().getVersion().isOlderThan(latestVersion)) {
                TorusLogger.info(Category.GENERAL, "There is an update available (" + latestVersion + ").");
            }
        } catch (Exception e) {
            TorusLogger.error(Category.GENERAL, "Update check has failed.");
            latestVersion = TorusPlugin.getInstance().getVersion();
        }
    }

    public static Version getLatestVersion() {
        return latestVersion != null ? latestVersion : TorusPlugin.getInstance().getVersion();
    }

}
