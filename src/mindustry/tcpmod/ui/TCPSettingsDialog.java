package mindustry.tcpmod.ui;

import arc.Core;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.tcpmod.TCPForcer;
import mindustry.tcpmod.TCPSettings;

public class TCPSettingsDialog extends BaseDialog {

    public TCPSettingsDialog() {
        super("TCP Mode");

        addCloseButton();

        shown(this::build);
        resized(this::build);
    }

    private void build() {
        cont.clear();
        cont.top();

        // Header
        cont.table(header -> {
            header.left();
            header.image(Icon.link)
                    .scaling(Scaling.fit)
                    .size(32)
                    .padRight(10);
            header.add("TCP over UDP")
                    .style(Styles.defaultLabel)
                    .color(Color.white)
                    .growX()
                    .left();
        }).growX().pad(10).row();

        // Description
        cont.add("Force TCP connections instead of UDP to reduce error snapshots.")
                .color(Color.lightGray)
                .fontScale(0.9f)
                .wrap()
                .growX()
                .pad(10)
                .row();

        // Toggle card
        buildToggleCard();

        // Status
        cont.table(status -> {
            status.left();
            status.add("Status: ")
                    .style(Styles.defaultLabel)
                    .color(Color.gray);
            status.add(TCPForcer.isActive() ? "ACTIVE" : "INACTIVE")
                    .style(Styles.defaultLabel)
                    .color(TCPForcer.isActive() ? Color.green : Color.red);
        }).growX().pad(10).row();
    }

    private void buildToggleCard() {
        boolean enabled = TCPSettings.isTcpEnabled();

        Table card = cont.button(Styles.black8, () -> {
            TCPSettings.setTcpEnabled(!enabled);
            if (!enabled) {
                TCPForcer.enable();
            } else {
                TCPForcer.disable();
            }
            build();
        })
                .height(120f)
                .pad(10f)
                .top()
                .left()
                .color(enabled ? Color.green : Color.red)
                .get();

        card.top().left();
        card.table(container -> {
            container.top().left().margin(12);

            container.table(header -> {
                header.left();
                header.image(enabled ? Icon.link : Icon.cancel)
                        .scaling(Scaling.fit)
                        .size(24)
                        .padRight(8);
                header.add("Force TCP")
                        .style(Styles.defaultLabel)
                        .color(Color.white)
                        .growX()
                        .left();
            }).growX().row();

            container.add(enabled ? "TCP mode is ON" : "TCP mode is OFF")
                    .color(Color.lightGray)
                    .fontScale(0.9f)
                    .wrap()
                    .growX()
                    .padTop(10)
                    .row();

            container.add().growY().row();
            container.add(enabled ? "@enabled" : "@disabled")
                    .color(enabled ? Color.green : Color.red)
                    .left();
        })
                .pad(12)
                .grow()
                .top()
                .left();
    }
}
