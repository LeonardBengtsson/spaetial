package spaetial.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import spaetial.Spaetial;
import spaetial.editing.ClientClipboardCache;
import spaetial.schematic.Schematic;
import spaetial.schematic.ClientSchematicPlacements;
import spaetial.editing.ClientManager;

/**
 * Allows the choice between pasting the in-game clipboard or decoding a schematic from the system clipboard
 *
 * <p><b>Clipboard:</b> Places a schematic created from the in-game clipboard
 *
 * <p><b>Schematic:</b> Parses the system clipboard as a schematic and creates a schematic placement
 *
 * @see ClientManager#pasteAction(MinecraftClient, ClientPlayerEntity, boolean)
 * @see ClientSchematicPlacements#addSchematicPlacement(ClientPlayerEntity, boolean, boolean, Schematic, boolean)
 * @see ClientClipboardCache#placeClipboard(ClientPlayerEntity, boolean, boolean)
 */
public class PasteOptionsWheelScreen extends WheelScreen {
    private final Schematic schem;

    private static final Identifier[] SHAPE_TEXTURES = new Identifier[] {
            Spaetial.id("textures/gui/paste/clipboard.png"),
            Spaetial.id("textures/gui/paste/schematic.png"),
    };

    public PasteOptionsWheelScreen(Schematic schem) {
        super(Spaetial.translate("gui", "selection_wheel.paste.title"), SHAPE_TEXTURES, 16);
        this.schem = schem;
    }

    @Override
    protected void onClick(MinecraftClient client, int element, boolean ctrl, boolean shift, boolean alt) {
        if (client.player == null) return;
        switch (element) {
            case 0 -> {
                var result = ClientClipboardCache.placeClipboard(client.player, ctrl, true);
                if (!result) {
                    Spaetial.warn("Failed to place clipboard from paste options");
                }
            }
            case 1 -> {
                if (schem != null) {
                    ClientSchematicPlacements.addSchematicPlacement(client.player, ctrl, true, schem, true);
                } else {
                    Spaetial.warn("Failed to add schematic placement from paste options");
                }
            }
        }
    }
}
