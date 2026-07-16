package mindustry.tcpmod;

import arc.Core;
import arc.Events;
import arc.scene.ui.layout.Table;
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
            Core.app.post(() -> {
                try {
                    setup();
                } catch (Exception err) {
                    Log.err("TCP Mod: Failed to initialize", err);
                }
            });
        });
    }

    private void setup() {
        TCPSettings.load();
        settingsDialog = new TCPSettingsDialog();

        // Add button to main menu
        Vars.ui.menufrag.addButton("TCP Mode", () -> settingsDialog.show());

        Log.info("TCP Mod: Initialized");
    }

    @Override
    public void loadContent() {
    }
}
