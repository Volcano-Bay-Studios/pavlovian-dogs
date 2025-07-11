package xyz.volcanobay.pavloviandogs.registries;

import xyz.volcanobay.pavloviandogs.smartaction.Action;
import xyz.volcanobay.pavloviandogs.smartaction.DefendOwnerAction;
import xyz.volcanobay.pavloviandogs.smartaction.MoveAction;
import xyz.volcanobay.pavloviandogs.smartaction.SitAction;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class Actions {
    public static HashMap<String, Function<String[],Action>> actions = new HashMap<>();
    public static Function<String[],Action> SIT = register("sit",(text) -> new SitAction("sit",text));
    public static Function<String[],Action> MOVE = register("move",(text) -> new MoveAction("move",text));
    public static Function<String[],Action> DEFEND_OWNER = register("defend_owner",(text) -> new DefendOwnerAction("defend_owner",text));
    public static Function<String[], Action> register(String name, Function<String[],Action> actionSupplier) {
        actions.put(name,actionSupplier);
        return actionSupplier;
    };
    public static void registerAll() {}
}
