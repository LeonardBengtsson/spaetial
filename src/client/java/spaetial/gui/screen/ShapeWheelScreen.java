package spaetial.gui.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import spaetial.Spaetial;

/**
 * Provides different options for 3D shapes that can be placed in the world and filled in or used as selections
 *
 * TODO
 */
public class ShapeWheelScreen extends WheelScreen {

    private static final Identifier[] SHAPE_TEXTURES = new Identifier[] {
        Spaetial.id("textures/gui/shape_selection/box.png"),
        Spaetial.id("textures/gui/shape_selection/circle.png"),
        Spaetial.id("textures/gui/shape_selection/sphere.png"),
        Spaetial.id("textures/gui/shape_selection/hollow_sphere.png"),
        Spaetial.id("textures/gui/shape_selection/cylinder.png"),
        Spaetial.id("textures/gui/shape_selection/hollow_cylinder.png"),
        Spaetial.id("textures/gui/shape_selection/pipe.png"),
        Spaetial.id("textures/gui/shape_selection/line.png"),
        Spaetial.id("textures/gui/shape_selection/triangle.png"),
        Spaetial.id("textures/gui/shape_selection/spline.png"),
        Spaetial.id("textures/gui/shape_selection/helix.png"),
        Spaetial.id("textures/gui/shape_selection/torus.png"),
        Spaetial.id("textures/gui/shape_selection/cone.png"),
    };

    public ShapeWheelScreen() {
        super(Spaetial.translate("gui", "selection_wheel.shape.title"), SHAPE_TEXTURES, 48);
    }

    @Override
    protected void onClick(MinecraftClient client, int element, boolean ctrl, boolean shift, boolean alt) {

    }
}
