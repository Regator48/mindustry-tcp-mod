package mindustry.tcpmod;

import arc.util.Log;
import mindustry.Vars;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TCPForcer {
    private static boolean active = false;

    public static void enable() {
        if (active) return;

        try {
            Field providerField = Net.class.getDeclaredField("provider");
            providerField.setAccessible(true);
            Object originalProvider = providerField.get(Vars.net);

            if (!(originalProvider instanceof ArcNetProvider arcProvider)) {
                Log.warn("TCP Mod: Not an ArcNetProvider");
                return;
            }

            Field clientField = ArcNetProvider.class.getDeclaredField("client");
            clientField.setAccessible(true);
            Object client = clientField.get(arcProvider);

            if (client == null) {
                Log.warn("TCP Mod: Client is null");
                return;
            }

            // Get the sendTCP method
            Method sendTCP = client.getClass().getMethod("sendTCP", Object.class);

            // Create dynamic proxy
            Class<?> providerInterface = Class.forName("mindustry.net.Net$NetProvider");
            Object proxy = Proxy.newProxyInstance(
                providerInterface.getClassLoader(),
                new Class<?>[]{ providerInterface },
                (proxyObj, method, args) -> {
                    if ("sendClient".equals(method.getName()) && args.length == 2) {
                        // Force TCP for all client sends
                        sendTCP.invoke(client, args[0]);
                        return null;
                    }
                    return method.invoke(originalProvider, args);
                }
            );

            providerField.set(Vars.net, proxy);
            active = true;
            Log.info("TCP Mod: TCP forcing enabled via proxy");
        } catch (Exception e) {
            Log.err("TCP Mod: Failed to enable TCP forcing", e);
        }
    }

    public static void disable() {
        if (!active) return;
        // Restoring original provider is complex; just log for now
        active = false;
        Log.info("TCP Mod: TCP forcing disabled (restart to fully restore)");
    }

    public static boolean isActive() {
        return active;
    }
}
