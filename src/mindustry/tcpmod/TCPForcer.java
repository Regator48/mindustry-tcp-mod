package mindustry.tcpmod;

import arc.util.Log;
import mindustry.Vars;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TCPForcer {
    private static boolean active = false;
    private static Object originalProvider = null;

    public static void enable() {
        if (active) return;

        try {
            Field providerField = Net.class.getDeclaredField("provider");
            providerField.setAccessible(true);
            Object currentProvider = providerField.get(Vars.net);

            if (!(currentProvider instanceof ArcNetProvider arcProvider)) {
                Log.warn("TCP Mod: Not an ArcNetProvider");
                return;
            }

            // Save original for restore
            originalProvider = currentProvider;

            Field clientField = ArcNetProvider.class.getDeclaredField("client");
            clientField.setAccessible(true);
            Object client = clientField.get(arcProvider);

            if (client == null) {
                Log.warn("TCP Mod: Client is null");
                return;
            }

            Method sendTCP = client.getClass().getMethod("sendTCP", Object.class);

            Class<?> providerInterface = Class.forName("mindustry.net.Net$NetProvider");
            Object proxy = Proxy.newProxyInstance(
                providerInterface.getClassLoader(),
                new Class<?>[]{ providerInterface },
                (proxyObj, method, args) -> {
                    if ("sendClient".equals(method.getName()) && args.length == 2) {
                        boolean reliable = (boolean) args[1];
                        // Force ALL packets through TCP (reliable and unreliable)
                        try {
                            sendTCP.invoke(client, args[0]);
                        } catch (Exception e) {
                            Log.err("TCP Mod: TCP send failed, falling back", e);
                            return method.invoke(originalProvider, args);
                        }
                        return null;
                    }
                    return method.invoke(originalProvider, args);
                }
            );

            providerField.set(Vars.net, proxy);
            active = true;
            Log.info("TCP Mod: TCP forcing enabled");
        } catch (Exception e) {
            Log.err("TCP Mod: Failed to enable TCP forcing", e);
        }
    }

    public static void disable() {
        if (!active || originalProvider == null) return;

        try {
            Field providerField = Net.class.getDeclaredField("provider");
            providerField.setAccessible(true);
            providerField.set(Vars.net, originalProvider);
            active = false;
            Log.info("TCP Mod: TCP forcing disabled, restored original provider");
        } catch (Exception e) {
            Log.err("TCP Mod: Failed to restore original provider", e);
            active = false;
        }
    }

    public static boolean isActive() {
        return active;
    }
}
