package spaetial;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spaetial.entity.BuilderAllayEntity;
import spaetial.networking.PacketTypes;
import spaetial.server.ServerEvents;
import spaetial.server.networking.ServerNetworking;

import java.util.Random;

public final class Spaetial implements ModInitializer {
	public static final String MOD_ID = "spaetial";
	public static final Random RANDOM = new Random();

	private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void info(Object object) {
		LOGGER.info(object.toString());
	}
	public static void debug(Object... objects) {
		var i = false;
		for (var obj : objects) {
			LOGGER.info((i ? "| " : "> ") + obj);
			i = true;
		}
	}
	public static void warn(Object... objects) {
		var i = false;
		for (var obj : objects) {
			LOGGER.warn((i ? "| " : "> ") + obj);
			i = true;
		}
	}
	public static void error(Object... objects) {
		var i = false;
		for (var obj : objects) {
			LOGGER.error((i ? "| " : "> ") + obj);
			i = true;
		}
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
	public static MutableText translateWithArgs(@Nullable String category, String key, Object... args) {
		return Text.translatable(translationKey(category, key), args);
	}
	public static MutableText translate(@Nullable String category, String... keyParts) {
		return Text.translatable(translationKey(category, keyParts));
	}
	public static String translationKey(@Nullable String category, String... keyParts) {
		var stringBuilder = new StringBuilder();
		if (category != null) {
			stringBuilder.append(category);
			stringBuilder.append('.');
		}
		stringBuilder.append(MOD_ID);
		for (var s : keyParts) {
			stringBuilder.append('.');
			stringBuilder.append(s);
		}
		return stringBuilder.toString();
	}

	public static final String BUILDER_ALLAY_SPAWN_EGG_ITEM_ID = "builder_allay_spawn_egg";
	public static final Item BUILDER_ALLAY_SPAWN_EGG_ITEM = Registry.register(Registries.ITEM, Spaetial.id(BUILDER_ALLAY_SPAWN_EGG_ITEM_ID),
			new SpawnEggItem(BuilderAllayEntity.ENTITY_TYPE, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Spaetial.id(BUILDER_ALLAY_SPAWN_EGG_ITEM_ID)))));

	@Override
	public void onInitialize() {
		// server-side events
		ServerTickEvents.START_SERVER_TICK.register(ServerEvents::onServerTick);
		ServerLifecycleEvents.SERVER_STARTING.register(ServerEvents::onServerStarting);
		ServerLifecycleEvents.SERVER_STOPPING.register(ServerEvents::onServerStopping);
		ServerPlayConnectionEvents.JOIN.register(ServerEvents::onPlayerJoins);
		ServerPlayConnectionEvents.DISCONNECT.register(ServerEvents::onPlayerDisconnects);

		// networking
		PacketTypes.register();
		ServerNetworking.register();

		// items
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
			content.add(BUILDER_ALLAY_SPAWN_EGG_ITEM);
		});

		// entities
		FabricDefaultAttributeRegistry.register(BuilderAllayEntity.ENTITY_TYPE, BuilderAllayEntity.createMobAttributes());

		// done!
		info("Initialized main!");
	}
}