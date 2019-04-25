package com.tfar.debugstick;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistry;

public class Utility {
  public static ItemDebugStick itemDebugStick = new ItemDebugStick("debugstick");

  public static void register(IForgeRegistry<Item> registry) {
    registry.register(itemDebugStick);
    DebugStick.proxy.registerItemRenderer(itemDebugStick, 0, "inventory");
  }
  public static void resetNBT(ItemStack stack) {
    NBTTagCompound nbt = new NBTTagCompound();
    stack.setTagCompound(nbt);
  }
  public static void changeMode(ItemStack stack){
    NBTTagCompound nbt = stack.getTagCompound();
    if (nbt == null)resetNBT(stack);
    int mode = nbt.getInteger("mode");
    int newMode = (mode > 2) ? 0 : mode + 1;
    nbt.setInteger("mode",newMode);
  }
}