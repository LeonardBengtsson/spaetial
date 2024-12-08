package spaetial.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import spaetial.Spaetial;
import spaetial.editing.ClientManager;
import spaetial.editing.state.CarveSelectionState;
import spaetial.util.hud.ScreenUtil;

/**
 * Provides different ways to edit the current selection.
 *
 * <p><b>Set/Replace:</b> Opens a {@link SetReplaceMenuScreen} where the region in the current selection can be filled
 * with blocks based on a few options
 *
 * <p><b>Change Material:</b> Opens a {@link ChangeMaterialMenuScreen} where the region in the current selection can
 * be modified by changing blocks of the same material (e.g. Cobblestone and Cobblestone stairs) to another material
 * (e.g. Spruce Planks and Spruce Stairs)
 *
 * <p><b>Carve Selection:</b> Changes the current editing state to a {@link CarveSelectionState}, where the current
 * selection can be transformed by omitting specific blocks or rows of blocks from the selection
 *
 * <p><b>Filter Selection:</b> Opens a {@link FilterSelectionMenuScreen} where the current selection can be transformed
 * to omit blocks based on a filter
 *
 * <p><b>Create schematic:</b> Opens a {@link CreateSchematicMenuScreen} where a schematic can be created based on the current selection
 *
 * @see SetReplaceMenuScreen
 * @see ChangeMaterialMenuScreen
 * @see CarveSelectionState
 * @see FilterSelectionMenuScreen
 * @see CreateSchematicMenuScreen
 */
public class SelectionContextWheelScreen extends WheelScreen {

    private static final Identifier[] OPTIONS_TEXTURES = new Identifier[] {
            Spaetial.id("textures/gui/selection_context_menu/set_replace.png"),
            Spaetial.id("textures/gui/selection_context_menu/change_material.png"),
            Spaetial.id("textures/gui/selection_context_menu/carve_selection.png"),
            Spaetial.id("textures/gui/selection_context_menu/filter_selection.png"),
            Spaetial.id("textures/gui/selection_context_menu/create_schematic.png"),
    };

    public SelectionContextWheelScreen() {
        super(Spaetial.translate("gui", "selection_wheel.selection_context_menu.title"), OPTIONS_TEXTURES, 16);
    }

    @Override
    protected void onClick(MinecraftClient client, int element, boolean ctrl, boolean shift, boolean alt) {
        switch (element) {
            case 0 -> {
                ScreenUtil.openScreen(client, new SetReplaceMenuScreen());
            }
            case 1 -> {
                ScreenUtil.openScreen(client, new ChangeMaterialMenuScreen());
            }
            case 2 -> {
                ClientManager.switchEditingState(new CarveSelectionState());
            }
            case 3 -> {
                ScreenUtil.openScreen(client, new FilterSelectionMenuScreen());
            }
            case 4 -> {
                ScreenUtil.openScreen(client, new CreateSchematicMenuScreen());
            }
        }
    }
}
