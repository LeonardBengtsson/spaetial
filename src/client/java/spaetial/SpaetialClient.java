package spaetial;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import spaetial.command.ClientCommands;
import spaetial.entity.BuilderAllayEntity;
import spaetial.gui.MainModHudOverlay;
import spaetial.gui.screen.WheelScreen;
import spaetial.networking.ClientNetworking;
import spaetial.render.entity.BuilderAllayEntityModel;
import spaetial.render.entity.BuilderAllayEntityRenderer;

@Environment(EnvType.CLIENT)
public final class SpaetialClient implements ClientModInitializer {
	public static final EntityModelLayer MODEL_BUILDER_ALLAY_LAYER = new EntityModelLayer(Spaetial.id("builder_allay"), "main");

	@Override
	public void onInitializeClient() {
		// load config
		// TODO

		// events
		ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::onClientTick);
		ClientPlayConnectionEvents.JOIN.register(ClientEvents::onClientJoin);
		ClientPlayConnectionEvents.DISCONNECT.register(ClientEvents::onClientDisconnect);
		ClientLifecycleEvents.CLIENT_STOPPING.register(ClientEvents::onClientStopping);

		// networking
		ClientNetworking.register();

		// hud
		HudRenderCallback.EVENT.register(new MainModHudOverlay());

		// entity render
		EntityRendererRegistry.register(BuilderAllayEntity.ENTITY_TYPE, BuilderAllayEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(MODEL_BUILDER_ALLAY_LAYER, BuilderAllayEntityModel::getTexturedModelData);

		// sounds
		Registry.register(Registries.SOUND_EVENT, WheelScreen.HOVER_SOUND_ID, WheelScreen.HOVER_SOUND);

		// client commands
		ClientCommands.register();

		// java toolkit
		// TODO is this necessary
		System.setProperty("java.awt.headless", "false");

		// done!
		Spaetial.info("Initialized client!");
	}
}