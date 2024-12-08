package spaetial.util.sound;

import net.minecraft.client.sound.EntityTrackingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public interface UnclampedSound {
    /**
     * I'm going to be honest, I do NOT remember how this works...
     */
    final class EntityTrackingSound extends EntityTrackingSoundInstance implements UnclampedSound {
        public EntityTrackingSound(SoundEvent sound, SoundCategory category, float volume, float pitch, Entity entity, long seed) {
            super(sound, category, volume, pitch, entity, seed);
        }
    }
}
