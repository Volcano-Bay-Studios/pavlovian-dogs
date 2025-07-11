package xyz.volcanobay.pavloviandogs.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.modogthedev.api.VoiceLibApi;
import org.modogthedev.api.events.ClientTalkEvent;
import xyz.volcanobay.pavloviandogs.PavlovianDogs;
import xyz.volcanobay.pavloviandogs.smartanimal.SmartAnimal;
import xyz.volcanobay.pavloviandogs.util.LevenshteinDistance;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static xyz.volcanobay.pavloviandogs.voice.AttentionManager.badWords;
import static xyz.volcanobay.pavloviandogs.voice.AttentionManager.goodWords;

public class PavlovianDogsClient implements ClientModInitializer {

    public static List<String> texts = new ArrayList<>();
    public static List<Float> floats = new ArrayList<>();

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(((context, tickDeltaManager) -> {
            int i = 0;
            for (String string : texts) {
                context.drawString(Minecraft.getInstance().font, string, (int) (Math.min(10,(500-floats.get(i))*10)+(Math.min(0,floats.get(i)-100))), 10+(i*10), new Color(255,255,255, 255).getRGB());
                i++;
            }
            int j = 0;
            ArrayList<Integer> integers = new ArrayList<>();
            for (Float f : floats) {
                floats.set(j,f-1);
                if (f < 0) {
                    integers.add(j);
                }
                j++;
            }
            for (int integer : integers.reversed()) {
                texts.remove(integer);
                floats.remove(integer);
            }
        }));
        VoiceLibApi.registerClientSpeechListener(PavlovianDogsClient::clientPlayerSpeaks);
    }

    public static void addText(String text) {
        texts.add(text);
        floats.add(600f);
    }

    public static void clientPlayerSpeaks(ClientTalkEvent event) {
        Level level = Minecraft.getInstance().player.level();
        BlockPos pos = Minecraft.getInstance().player.blockPosition();
        List<Entity> entities = level.getEntities((Entity) null, new AABB(pos).inflate(10), EntitySelector.NO_SPECTATORS);
        for (Entity entity : entities) {
            if (entity.getCustomName() != null && entity instanceof Wolf wolf) {
                LevenshteinDistance.BestFit bestFit = LevenshteinDistance.bestFitWord(event.getText().toLowerCase(), wolf.getCustomName().getString().toLowerCase());
                int distance = bestFit.distance();

                SmartAnimal smartAnimal = (SmartAnimal) wolf;
                for (String text : badWords) {
                    if (event.getText().toLowerCase().contains(text)) {
                        if (distance < 2) {
                            addText(wolf.getCustomName().getString() + " will remember this.");
                        } else {
                            addText(wolf.getCustomName().getString() + " is hurt by your words.");
                        }
                    }
                }
                for (String text : goodWords) {
                    if (event.getText().toLowerCase().contains(text)) {
                        if (distance < 2) {
                            addText(wolf.getCustomName().getString() + " trusts you.");
                        } else {
                            addText(wolf.getCustomName().getString() + " is excited to see you.");
                        }
                    }
                }
                if (distance < 2 && bestFit.words().length > 1) {
                    if (PavlovianDogs.debug)
                        System.out.println("[" + bestFit.string() + " < " + distance + " > " + wolf.getCustomName().getString() + "] -> " + bestFit.words()[1]);
                    addText(wolf.getCustomName().getString()+" > "+bestFit.words()[1]);
                }
            }
        }
    }
}
