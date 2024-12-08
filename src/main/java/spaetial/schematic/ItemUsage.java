package spaetial.schematic;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUsage {
    private final Type type;
    private final Item itemBefore;
    private final int amountBefore;
    private final ItemStack itemAfter;

    public ItemUsage(Type type, Item itemBefore, int amountBefore, ItemStack itemAfter) {
        this.type = type;
        this.itemBefore = itemBefore;
        this.amountBefore = amountBefore;
        this.itemAfter = itemAfter;
    }

    public enum Type {
        USE,
        GAIN,
        MODIFY,
    }
}
