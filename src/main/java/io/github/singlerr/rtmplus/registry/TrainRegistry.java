package io.github.singlerr.rtmplus.registry;

import jp.ngt.rtm.entity.train.EntityTrainBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TrainRegistry {
    private static final HashMap<String, EntityTrainBase> registeredTrains = new HashMap<>();

    public static boolean trainExists(String name) {
        return registeredTrains.containsKey(name);
    }

    public static String registerTrain(String name, EntityTrainBase train) {
        registeredTrains.put(name, train);
        return name;
    }

    public static void removeTrain(String name) {
        registeredTrains.remove(name);
    }

    public static EntityTrainBase getTrain(String name) {
        return registeredTrains.get(name);
    }

    public static Set<Map.Entry<String, EntityTrainBase>> getTrains() {
        return registeredTrains.entrySet();
    }
}
