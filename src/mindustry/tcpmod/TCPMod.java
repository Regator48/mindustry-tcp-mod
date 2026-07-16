package mindustry.tcpmod;

import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.util.Log;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public class TCPMod extends Mod {

    public TCPMod() {
        Log.info("TCP Mod: Loaded");
    }

    @Override
    public void init() {
        Log.info("TCP Mod: init");

        // Enable TCP if it was enabled before
        if (Core.settings.getBool("tcp-mod-enabled", false)) {
            TCPForcer.enable();
        }

        Events.on(ClientLoadEvent.class, e -> {
            Core.app.post(() -> {
                Vars.ui.menufrag.addButton("TCP Mode", this::showDialog);
                Log.info("TCP Mod: Button added");
            });
        });
    }

    private void showDialog() {
        BaseDialog dialog = new BaseDialog("TCP Mode");
        dialog.addCloseButton();

        boolean enabled = TCPForcer.isActive();

        dialog.cont.table(t -> {
            t.left();
            t.image(Icon.link).scaling(Scaling.fit).size(32).padRight(10);
            t.add("TCP over UDP").style(Styles.defaultLabel).color(Color.white).growX().left();
        }).growX().pad(10).row();

        dialog.cont.add("Force TCP instead of UDP to reduce error snapshots.\nWorks in multiplayer - falls back automatically if TCP fails.")
            .color(Color.lightGray).fontScale(0.9f).wrap().growX().pad(10).row();

        dialog.cont.button(enabled ? "ON" : "OFF", () -> {
            if (TCPForcer.isActive()) {
                TCPForcer.disable();
                Core.settings.put("tcp-mod-enabled", false);
            } else {
                TCPForcer.enable();
                Core.settings.put("tcp-mod-enabled", true);
            }
            dialog.hide();
            showDialog();
        }).height(60).width(200).pad(10)
          .color(enabled ? Color.green : Color.red).row();

        dialog.cont.add(enabled ? "Status: ACTIVE" : "Status: INACTIVE")
            .color(enabled ? Color.green : Color.red).pad(10);

        dialog.show();
    }

    @Override
    public void loadContent() {
    }
}
