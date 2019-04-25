package com.tfar.debugstick;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static com.tfar.debugstick.Utility.itemDebugStick;

@Mod.EventBusSubscriber(modid = DebugStick.MODID)
public class ItemDebugStick extends Item {
  public ItemDebugStick(String name) {
    setRegistryName(new ResourceLocation(DebugStick.MODID, name));
    setTranslationKey(getRegistryName().toString());
    setCreativeTab(CreativeTabs.TOOLS);
  }

  @Override
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
        world.setBlockState(pos,(IBlockState)list.get(newState));
      }
    }
    return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
  }

  @Override
  public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
    if (world.isRemote)return ActionResult.newResult(EnumActionResult.FAIL,player.getHeldItem(hand));
    if (player.isSneaking() && world.getBlockState(player.rayTrace(5,1).getBlockPos()).getBlock().isAir(world.getBlockState(player.rayTrace(5,1).getBlockPos()),world,player.rayTrace(5,1).getBlockPos()))Utility.changeMode(player.getHeldItemMainhand());
    return super.onItemRightClick(world, player, hand);
  }

  @SubscribeEvent
  public static void stopGUIs(PlayerInteractEvent.RightClickBlock e){
  //  if (e.getEntityPlayer().getHeldItemMainhand().getItem() == itemDebugStick)e.setCanceled(true);
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void getEntity(PlayerInteractEvent.EntityInteractSpecific e){
    EntityPlayer p = e.getEntityPlayer();
    if (p.world.isRemote)return;
    ItemStack stack = p.getHeldItemMainhand();
    if (stack.getItem() != itemDebugStick)return;
    NBTTagCompound nbt = stack.getTagCompound();
    if (nbt == null)Utility.resetNBT(stack);
    int mode = nbt.getInteger("mode");
    if (mode != 1)return;
    nbt.setString("NBTTagCompound",e.getTarget().toString());
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
    }
    super.addInformation(stack, worldIn, tooltip, flagIn);
  }
}




