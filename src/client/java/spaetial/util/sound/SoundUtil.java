package spaetial.util.sound;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import spaetial.Spaetial;

public final class SoundUtil {
    private SoundUtil() {}

    /**
     * NOTE: This class exists in a client-only environment, but this method can only be called from a server context
     */
    @Deprecated
    public static void playSoundWorld(ServerWorld world, Vec3d pos, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        world.getServer().getPlayerManager().sendToAround(null, pos.x, pos.y, pos.z, sound.getDistanceToTravel(volume), world.getRegistryKey(),
                new PlaySoundS2CPacket(RegistryEntry.of(sound), category, pos.x, pos.y, pos.z, volume, pitch, Spaetial.RANDOM.nextLong())
        );
    }

    /**
     * Plays an unclamped sound on the client, which means that the volume is not limited by minecraft's usual min and max values
     */
    public static void playSoundClient(MinecraftClient client, SoundEvent sound, SoundCategory category, float volume, float pitch) {
        SoundInstance instance = new UnclampedSound.EntityTrackingSound(sound, category, volume, pitch, client.player, Spaetial.RANDOM.nextLong());
        client.getSoundManager().play(instance);
    }
}
