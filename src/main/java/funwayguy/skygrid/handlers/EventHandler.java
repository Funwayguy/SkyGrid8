package funwayguy.skygrid.handlers;

import funwayguy.skygrid.core.SG_Settings;
import funwayguy.skygrid.core.SkyGrid;
import funwayguy.skygrid.util.CustomLootTableManager;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.Random;

public class EventHandler
{
	@SubscribeEvent
	public void onJoinWorld(EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof EntityPlayer && SG_Settings.oldRegen)
		{
			EntityPlayer player = (EntityPlayer)event.getEntity();
			FoodStats old = player.getFoodStats();
			
			if(old instanceof OldFoodStats)
			{
				return;
			}
			
			NBTTagCompound tags = new NBTTagCompound();
			OldFoodStats nStats = new OldFoodStats();
			old.writeNBT(tags);
			nStats.readNBT(tags);
			ObfuscationReflectionHelper.setPrivateValue(EntityPlayer.class, player, nStats, "field_71100_bB", "foodStats");
		}
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event)
	{
		if(event.getEntity().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer)event.getEntityLiving();
		Random rand = player.world.rand;
		
		if(player.getName().equalsIgnoreCase("dark" + "osto") && !CustomLootTableManager.dad && rand.nextInt(10) == 0 && player.ticksExisted == 2400)
		{
			CustomLootTableManager.dad = true;
			String s = String.format(CustomLootTableManager.dadPre[rand.nextInt(CustomLootTableManager.dadPre.length)], CustomLootTableManager.dadName[rand.nextInt(CustomLootTableManager.dadName.length)], CustomLootTableManager.dadPost[rand.nextInt(CustomLootTableManager.dadPost.length)]);
			player.sendMessage(new TextComponentString("" + TextFormatting.GREEN + TextFormatting.BOLD + TextFormatting.UNDERLINE + TextFormatting.ITALIC + s));
			EntityFireworkRocket firework = new EntityFireworkRocket(player.world);
			firework.setPosition(player.posX, player.posY, player.posZ);
			player.world.spawnEntity(firework);
		}
	}
	
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event)
	{
		if(event.getSource() != null && event.getEntityLiving() instanceof EntityPlayer)
		{
			CustomLootTableManager.dad = false;
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.getModID().equals(SkyGrid.MODID))
		{
			ConfigHandler.config.save();
			ConfigHandler.initConfigs();
		}
	}
	static boolean lootLoaded = false;
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		if(!event.getWorld().isRemote && !lootLoaded)
		{
	    	CustomLootTableManager.LoadLoot(new File("config/skygrid/loot.json"), event.getWorld().getLootTableManager());
	    	lootLoaded = true;
		}
	}
	
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event)
	{
		if(!event.getWorld().isRemote && !event.getWorld().getMinecraftServer().isServerRunning())
		{
			lootLoaded = false;
			CustomLootTableManager.dad = false;
		}
	}
}
