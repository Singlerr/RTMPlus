package io.github.singlerr.rtmplus.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class PassengerRegistry {
    private static final HashMap<String, PassengerData> registry = new HashMap<>();

    public static void registerPassenger(String playerName, PassengerData data) {
        registry.put(playerName, data);

    }

    public static boolean anyPassengerExists() {
        return !registry.isEmpty();
    }

    public static Set<Map.Entry<String, PassengerData>> getAllPassengers() {
        return registry.entrySet();
    }

    public static boolean isPassenger(String playerName) {
        return registry.containsKey(playerName);
    }

    public static void removePassenger(String playerName) {
        registry.remove(playerName);
    }

    public static PassengerData getPassengerData(String playerName) {
        return registry.get(playerName);
    }
}
