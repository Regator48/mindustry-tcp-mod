package mindustry.tcpmod;

import arc.Core;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;
import mindustry.tcpmod.ui.TCPSettingsDialog;

public class TCPMod extends Mod {

    public static TCPSettingsDialog settingsDialog;

    public TCPMod() {
        Log.info("TCP Mod loaded");
    }

    @Override
    public void init() {
        Events.on(ClientLoadEvent.class, e -> {
            // Wait longer for UI to be ready
            Core.app.post(() -> {
                Core.app.post(() -> {
                    Core.app.post(() -> {
                        try {
                            setup();
                        } catch (Exception err) {
                            Log.err("TCP Mod: Failed to initialize", err);
                        }
                    });
                });
            });
        });
    }

    private void setup() {
        TCPSettings.load();
        settingsDialog = new TCPSettingsDialog();

        // Add button to main menu
        if (Vars.ui != null && Vars.ui.menufrag != null) {
            Vars.ui.menufrag.addButton("TCP Mode", () -> settingsDialog.show());
            Log.info("TCP Mod: Added button to menu");
        } else {
            Log.warn("TCP Mod: UI or menufrag is null");
        }

        Log.info("TCP Mod: Initialized");
    }

    @Override
    public void loadContent() {
    }
}
