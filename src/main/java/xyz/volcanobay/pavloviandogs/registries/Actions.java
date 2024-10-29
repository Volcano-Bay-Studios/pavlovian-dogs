package xyz.volcanobay.pavloviandogs.registries;

import xyz.volcanobay.pavloviandogs.smartaction.Action;
import xyz.volcanobay.pavloviandogs.smartaction.DefendOwnerAction;
import xyz.volcanobay.pavloviandogs.smartaction.MoveAction;
import xyz.volcanobay.pavloviandogs.smartaction.SitAction;

import java.util.HashMap;
import java.util.function.Supplier;

public class Actions {
    public static HashMap<String, Supplier<Action>> actions = new HashMap<>();
    public static Supplier<Action> SIT = register("sit",() -> new SitAction("sit"));
    public static Supplier<Action> MOVE = register("move",() -> new MoveAction("move"));
    public static Supplier<Action> DEFEND_OWNER = register("defend_owner",() -> new DefendOwnerAction("defend_owner"));
    public static <T extends Action> Supplier<T> register(String name,Supplier<Action> actionSupplier) {
        actions.put(name,actionSupplier);
        return (Supplier<T>) actionSupplier;
    };
    public static void registerAll() {}
}
