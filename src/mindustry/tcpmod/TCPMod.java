package mindustry.tcpmod;

import arc.Core;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;

public class TCPMod extends Mod {

    public TCPMod() {
        Log.info("[TCP Mod] Constructor called");
    }

    @Override
    public void init() {
        Log.info("[TCP Mod] init called");
        Events.on(ClientLoadEvent.class, e -> {
            Log.info("[TCP Mod] ClientLoadEvent fired");
            Core.app.post(() -> {
                Log.info("[TCP Mod] Adding button...");
                try {
                    Vars.ui.menufrag.addButton("TCP Mode", () -> {
                        Log.info("[TCP Mod] Button clicked");
                    });
                    Log.info("[TCP Mod] Button added successfully");
                } catch (Exception ex) {
                    Log.err("[TCP Mod] Failed to add button", ex);
                }
            });
        });
    }

    @Override
    public void loadContent() {
    }
}
