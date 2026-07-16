package mindustry.tcpmod;

import arc.Core;
import arc.util.Log;

public class TCPSettings {
    private static final String SETTING_KEY = "tcp-mod.tcp-enabled";

    public static void load() {
        // Settings are automatically loaded by Core.settings
        Log.info("TCP Mod: Settings loaded, TCP enabled = " + isTcpEnabled());
    }

    public static boolean isTcpEnabled() {
        return Core.settings.getBool(SETTING_KEY, false);
    }

    public static void setTcpEnabled(boolean enabled) {
        Core.settings.put(SETTING_KEY, enabled);
        Log.info("TCP Mod: TCP enabled set to " + enabled);
    }
}
