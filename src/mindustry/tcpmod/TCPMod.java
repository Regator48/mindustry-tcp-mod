package mindustry.tcpmod;

import arc.Core;
import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;

public class TCPMod extends Mod {

    static {
        System.out.println("[TCP MOD] Static initializer!");
    }

    public TCPMod() {
        System.out.println("[TCP MOD] Constructor called!");
    }

    @Override
    public void init() {
        System.out.println("[TCP MOD] init called!");
        Events.on(ClientLoadEvent.class, e -> {
            System.out.println("[TCP MOD] ClientLoadEvent fired!");
            Core.app.post(() -> {
                System.out.println("[TCP MOD] Adding button...");
                try {
                    Vars.ui.menufrag.addButton("TCP Mode", () -> {
                        System.out.println("[TCP MOD] Button clicked!");
                    });
                    System.out.println("[TCP MOD] Button added!");
                } catch (Exception ex) {
                    System.out.println("[TCP MOD] Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
        });
    }

    @Override
    public void loadContent() {
    }
}
