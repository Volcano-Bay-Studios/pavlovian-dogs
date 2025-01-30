package xyz.volcanobay.pavloviandogs.voice;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.modogthedev.api.VoiceLibApi;
import org.modogthedev.api.events.ClientTalkEvent;
import org.modogthedev.api.events.ServerPlayerTalkEvent;
import xyz.volcanobay.pavloviandogs.PavlovianDogs;
import xyz.volcanobay.pavloviandogs.registries.Events;
import xyz.volcanobay.pavloviandogs.smartanimal.SmartAnimal;
import xyz.volcanobay.pavloviandogs.smartevents.StringEvent;
import xyz.volcanobay.pavloviandogs.util.LevenshteinDistance;

import java.util.List;
import java.util.Properties;

public class AttentionManager {
    public static final List<String> badWords = List.of(new String[]{"bad dog", "bad girl", "bad boy"});
    public static final List<String> goodWords = List.of(new String[]{"good dog", "good girl", "good boy"});
    public static void playerSpeaks(ServerPlayerTalkEvent event) {
        Level level = event.getPlayer().level();
        BlockPos pos = event.getPlayer().blockPosition();
        List<Entity> entities = level.getEntities((Entity) null, new AABB(pos).inflate(10), EntitySelector.NO_SPECTATORS);
        for (Entity entity : entities) {
            if (entity.getCustomName() != null && entity instanceof Wolf wolf) {

                LevenshteinDistance.BestFit bestFit = LevenshteinDistance.bestFitWord(event.getText().toLowerCase(), wolf.getCustomName().getString().toLowerCase());
                int distance = bestFit.distance();
                if (PavlovianDogs.debug)
                    System.out.println("[" + bestFit.string() + " < " + distance + " > " + wolf.getCustomName().getString() + "] -> " + bestFit.nextWord());

                SmartAnimal smartAnimal = (SmartAnimal) wolf;
                for (String text : badWords) {
                    if (event.getText().toLowerCase().contains(text)) {
                        smartAnimal.getAnimalBrain().discourage(distance < 2 ? 10 : 1);
                        wolf.getLookControl().setLookAt(event.getPlayer().getX(), event.getPlayer().getY(), event.getPlayer().getZ());
                        if (distance < 2)
                            event.getPlayer().level().playSound(event.getPlayer(),wolf.blockPosition(), SoundEvents.WOLF_WHINE,wolf.getSoundSource());
                        else
                            event.getPlayer().level().playSound(event.getPlayer(),wolf.blockPosition(), SoundEvents.WOLF_GROWL,wolf.getSoundSource());
                    }
                }
                for (String text : goodWords) {
                    if (event.getText().toLowerCase().contains(text)) {
                        smartAnimal.getAnimalBrain().encourage(distance < 2 ? 10 : 1);
                        wolf.getLookControl().setLookAt(event.getPlayer().getX(), event.getPlayer().getY(), event.getPlayer().getZ());
                    }
                }
                if (distance < 2) {
                    StringEvent stringEvent = Events.VOICE.get().setPos(pos).setString(bestFit.nextWord());
                    if (!smartAnimal.getAnimalBrain().neuralNetwork.containsKey(stringEvent.getReference()))
                        smartAnimal.getAnimalBrain().addNewEvent(stringEvent.getReference());
                    smartAnimal.addSmartEvent(stringEvent);
                }
            }
        }
    }


    public static void register() {
        VoiceLibApi.registerServerPlayerSpeechListener(AttentionManager::playerSpeaks);
    }
}
