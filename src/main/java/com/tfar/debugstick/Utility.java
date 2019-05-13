package com.tfar.debugstick;

import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import static com.tfar.debugstick.DebugStick.MODID;
import static net.minecraft.util.text.TextFormatting.GOLD;
@Mod.EventBusSubscriber(modid = MODID)
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
  public static void changeMode(EntityPlayer player){
    ItemStack stack = player.getHeldItemMainhand();
    NBTTagCompound nbt = stack.getTagCompound();
    if (nbt == null)resetNBT(stack);
    int mode = nbt.getInteger("mode");
    int newMode = (mode > 4) ? 0 : mode + 1;
    nbt.setInteger("mode",newMode);
    switch (newMode){
      case 0:{player.sendStatusMessage(new TextComponentString(GOLD + "Blockstate"),true);break;}
      case 1:{player.sendStatusMessage(new TextComponentString(GOLD + "NBT"),true);break;}
      case 2:{player.sendStatusMessage(new TextComponentString(GOLD + "Blockstate list"),true);break;}
      case 3:{player.sendStatusMessage(new TextComponentString(GOLD + "Transform"),true);break;}
      case 4:{player.sendStatusMessage(new TextComponentString(GOLD + "CrT Blockstate"),true);break;}
      case 5:{player.sendStatusMessage(new TextComponentString(GOLD + "CrT Entity"),true);break;}

    }
  }
  public static List<String> getOreDictOfItem(ItemStack stack) {
    try {
      int[] ids = OreDictionary.getOreIDs(stack);
    List<String> names = new ArrayList<>();
    for(int id : ids) {
      names.add(OreDictionary.getOreName(id));
    }
    return names;
    } catch (Exception e){
      return new ArrayList<>();
    }
  }

  @SubscribeEvent
  public static void getEntity(PlayerInteractEvent.EntityInteractSpecific e) {
    EntityPlayer p = e.getEntityPlayer();
    if (p.world.isRemote  ||  e.getHand() != EnumHand.MAIN_HAND)return;
    ItemStack stack = p.getHeldItemMainhand();
    if (stack.getItem() != itemDebugStick) return;
    NBTTagCompound nbt = stack.getTagCompound();
    if (nbt == null) Utility.resetNBT(stack);
    int mode = nbt.getInteger("mode");
    if (mode == 1) {
      nbt.setString("NBTTagCompound", e.getTarget().toString());
    }
    else if (mode == 5) {
      String s = "<entity:"+CraftTweakerMC.getIEntity(e.getTarget()).getDefinition().getId()+">";

      nbt.setString("CTEntity", s);
      p.sendMessage(new TextComponentString(TextFormatting.WHITE + "Entity " + TextFormatting.GREEN + s));
      StringSelection stringSelection = new StringSelection(s);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, null);
    }
  }
}