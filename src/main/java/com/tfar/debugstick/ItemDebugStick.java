package com.tfar.debugstick;

import com.google.common.collect.ImmutableList;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.commands.CommandUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.datatransfer.*;
import java.awt.Toolkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.tfar.debugstick.Utility.itemDebugStick;

@Mod.EventBusSubscriber(modid = DebugStick.MODID)
public class ItemDebugStick extends Item {
  public ItemDebugStick(String name) {
    setRegistryName(new ResourceLocation(DebugStick.MODID, name));
    setTranslationKey(getRegistryName().toString());
    setCreativeTab(CreativeTabs.TOOLS);
  }

  //called when right clicking a block

  @Override
  @Nonnull
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    if (world.isRemote)return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    ItemStack stack = player.getHeldItemMainhand();
    NBTTagCompound nbt = stack.getTagCompound();
    if (nbt == null)Utility.resetNBT(stack);
    int mode = nbt.getInteger("mode");
    switch (mode){
      case 0: {nbt.setString("Blockstate",world.getBlockState(pos).toString());break;}
      case 1: {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null)break;
        nbt.setString("NBTTagCompound",tile.writeToNBT(new NBTTagCompound()).toString());break;}
      case 2:{
        nbt.setString("BlockstateList",world.getBlockState(pos).getBlock().getBlockState().getValidStates().toString());break;
      }
      case 3:{
        ImmutableList list = world.getBlockState(pos).getBlock().getBlockState().getValidStates();
        int index = list.indexOf(world.getBlockState(pos));
        int newState = (index + 1 >= list.size()) ? 0 : index + 1;
        world.setBlockState(pos,(IBlockState)list.get(newState));break;
      }
      case 4:{
        String blockstateInfo = CraftTweakerMC.getBlockState(world.getBlockState(pos)).toString();
        nbt.setString("CTBlockstate", blockstateInfo);
        player.sendMessage(new TextComponentString(TextFormatting.WHITE + "Blockstate " + TextFormatting.GREEN + blockstateInfo));
        List<String> oredicts = Utility.getOreDictOfItem(new ItemStack(Item.getItemFromBlock(world.getBlockState(pos).getBlock()),1,world.getBlockState(pos).getBlock().getMetaFromState(world.getBlockState(pos))));
        if (!oredicts.isEmpty())
        player.sendMessage(new TextComponentString(TextFormatting.WHITE + "Oredicts " + TextFormatting.AQUA + oredicts));
        else player.sendMessage(new TextComponentString(TextFormatting.RED + "No oredicts found"));
        StringSelection stringSelection = new StringSelection(blockstateInfo);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
        break;
      }
    }
    return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
  }

  @Override
  @Nonnull
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player,@Nonnull EnumHand hand) {
    if (world.isRemote)return ActionResult.newResult(EnumActionResult.FAIL,player.getHeldItem(hand));
    if (player.isSneaking() && world.getBlockState(player.rayTrace(10,1).getBlockPos()).getBlock().isAir(world.getBlockState(player.rayTrace(10,1).getBlockPos()),world,player.rayTrace(5,1).getBlockPos()))Utility.changeMode(player);
    return super.onItemRightClick(world, player, hand);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    if (worldIn == null)return;
    NBTTagCompound nbt = stack.getTagCompound();
    if (nbt == null)return;
    int mode = nbt.getInteger("mode");
    switch (mode){
      case 0: {tooltip.add("Blockstate: "+nbt.getString("Blockstate"));break;}
      case 1: {tooltip.add("NBTTagCompound: "+nbt.getString("NBTTagCompound"));break;}
      case 2: {tooltip.add("Blockstate List: "+nbt.getString("BlockstateList"));break;}
      case 3: {tooltip.add("Transform");break;}
      case 4: {tooltip.add("CT Blockstate:"+nbt.getString("CTBlockstate"));break;}
      case 5: {tooltip.add("CT Entity:"+nbt.getString("CTEntity"));break;}
    }
    super.addInformation(stack, worldIn, tooltip, flagIn);
  }
}




