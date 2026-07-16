package mindustry.tcpmod;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.graphics.Color;
import arc.scene.ui.layout.Table;
import arc.util.Http;
import arc.util.Log;
import arc.util.Scaling;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.mod.Mod;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public class TCPMod extends Mod {

    private static final String REPO = "Regator48/mindustry-tcp-mod";
    private static final String API_URL = "https://api.github.com/repos/" + REPO + "/releases/latest";
    private static Table tcpIndicator;
    private static String currentVersion = "1.2";
    private static String latestVersion = null;
    private static String downloadUrl = null;

    public TCPMod() {
        Log.info("TCP Mod: Loaded");
    }

    @Override
    public void init() {
        Log.info("TCP Mod: init");

        if (Core.settings.getBool("tcp-mod-enabled", false)) {
            TCPForcer.enable();
        }

        Events.on(ClientLoadEvent.class, e -> {
            Core.app.post(() -> {
                Vars.ui.menufrag.addButton("TCP Mode", this::showDialog);
                setupHUD();
                checkForUpdates();
            });
        });
    }

    private void setupHUD() {
        tcpIndicator = new Table();
        tcpIndicator.visible(() -> Vars.ui.hudfrag.shown && Vars.state.isGame() && TCPForcer.isActive());
        tcpIndicator.top().left();
        tcpIndicator.table(t -> {
            t.background(Styles.black6);
            t.margin(6f);
            t.add("[TCP]").color(Color.yellow).style(Styles.defaultLabel);
        });
        Vars.ui.hudGroup.addChild(tcpIndicator);
    }

    private void checkForUpdates() {
        Log.info("TCP Mod: Checking for updates...");
        Http.get(API_URL).error(e -> {
            Log.warn("TCP Mod: Failed to check for updates", e);
        }).submit(response -> {
            try {
                String json = response.getResultAsString();

                // Simple parsing without Jval
                int tagStart = json.indexOf("\"tag_name\":\"") + 12;
                int tagEnd = json.indexOf("\"", tagStart);
                if (tagStart > 11 && tagEnd > tagStart) {
                    latestVersion = json.substring(tagStart, tagEnd).replace("v", "");
                }

                int urlStart = json.indexOf("browser_download_url") + 23;
                int urlEnd = json.indexOf("\"", urlStart);
                while (urlStart > 22 && urlEnd > urlStart && json.charAt(urlStart) != '"') {
                    urlStart++;
                    urlEnd = json.indexOf("\"", urlStart);
                }
                if (urlStart > 22 && urlEnd > urlStart) {
                    downloadUrl = json.substring(urlStart, urlEnd);
                }

                Log.info("TCP Mod: Latest version: " + latestVersion + ", downloadUrl: " + downloadUrl);

                if (latestVersion != null && !latestVersion.equals(currentVersion) && downloadUrl != null) {
                    Core.app.post(() -> showUpdateDialog());
                }
            } catch (Exception e) {
                Log.warn("TCP Mod: Failed to parse update info", e);
            }
        });
    }

    private void showUpdateDialog() {
        BaseDialog dialog = new BaseDialog("Update Available");
        dialog.addCloseButton();

        dialog.cont.add("TCP Mod " + latestVersion + " is available!")
            .color(Color.yellow).pad(10).row();

        dialog.cont.add("You have version " + currentVersion)
            .color(Color.lightGray).pad(5).row();

        if (downloadUrl != null && !downloadUrl.isEmpty()) {
            dialog.cont.table(t -> {
                t.button("Update", () -> {
                    dialog.hide();
                    downloadUpdate();
                }).height(50).width(150).pad(10).color(Color.green);

                t.button("Skip", dialog::hide).height(50).width(150).pad(10).color(Color.gray);
            }).pad(10).row();
        } else {
            dialog.cont.add("Download URL not available").color(Color.red).pad(10).row();
            dialog.cont.button("Close", dialog::hide).height(50).width(150).pad(10).color(Color.gray).row();
        }

        dialog.show();
    }

    private void downloadUpdate() {
        if (downloadUrl == null || downloadUrl.isEmpty()) {
            Vars.ui.showErrorMessage("No download URL available. Please try again later.");
            return;
        }

        BaseDialog dialog = new BaseDialog("Downloading");
        dialog.cont.add("Downloading update...").pad(20).row();
        dialog.cont.add("Please wait").color(Color.lightGray).pad(10).row();
        dialog.show();

        Http.get(downloadUrl).error(e -> {
            Log.err("TCP Mod: Download failed", e);
            dialog.hide();
            Core.app.post(() -> Vars.ui.showErrorMessage("Download failed: " + e.getMessage()));
        }).submit(response -> {
            try {
                Fi modsDir = Vars.modDirectory;
                Fi tempFile = modsDir.child("tcp-mod-new.jar");
                Fi finalFile = modsDir.child("tcp-mod.jar");

                tempFile.writeString(response.getResultAsString());

                if (finalFile.exists()) {
                    finalFile.delete();
                }
                tempFile.moveTo(finalFile);

                dialog.hide();
                Core.app.post(() -> {
                    BaseDialog restartDialog = new BaseDialog("Restart Required");
                    restartDialog.cont.add("Update downloaded!").pad(10).row();
                    restartDialog.cont.add("Please restart Mindustry to apply the update.")
                        .color(Color.lightGray).pad(5).row();
                    restartDialog.cont.button("OK", restartDialog::hide).height(60).width(200).pad(10);
                    restartDialog.show();
                });

                Log.info("TCP Mod: Update downloaded successfully");
            } catch (Exception e) {
                Log.err("TCP Mod: Failed to save update", e);
                dialog.hide();
                Core.app.post(() -> Vars.ui.showErrorMessage("Failed to save update: " + e.getMessage()));
            }
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

        dialog.cont.add("Force TCP instead of UDP to reduce error snapshots.")
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

        if (latestVersion != null && !latestVersion.equals(currentVersion)) {
            dialog.cont.row();
            dialog.cont.button("Update Available: v" + latestVersion, this::showUpdateDialog)
                .color(Color.yellow).pad(10);
        }

        dialog.show();
    }

    @Override
    public void loadContent() {
    }
}
