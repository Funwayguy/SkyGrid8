package skygrid8.handlers;

import java.io.File;
import java.util.Random;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import skygrid8.core.SG_Settings;
import skygrid8.core.SkyGrid8;
import skygrid8.util.CustomLootTableManager;

public class EventHandler
{
	static boolean dad = false;
	static String[] dadPre = new String[]{"Happy %s Appreciation %s!", "Happy Birthday %s!", "Happy %s Awareness %s!", "Happy %s Conservation %s!", "RISE UP LIGHTS!"};
	static String[] dadName = new String[]{"Darksoto", "Darksoda", "Derposto", "Darktoasto", "Darkroasto", "DarkCostCo", "Cheatosto", "Saltyosto", "Rantosto", "Penguinosto"};
	static String[] dadPost = new String[]{"Day", "Decade", "Hour", "Week", "Month"};
	static String[] dadColors = new String[]{};
	
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
		if(event.getEntity().worldObj.isRemote || !(event.getEntityLiving() instanceof EntityPlayer))
		{
			return;
		}
		
		EntityPlayer player = (EntityPlayer)event.getEntityLiving();
		Random rand = player.worldObj.rand;
		
		if(player.getName().equalsIgnoreCase("darkosto") && !dad && rand.nextInt(100) == 0 && player.ticksExisted == 2400)
		{
			dad = true;
			String s = String.format(dadPre[rand.nextInt(dadPre.length)], dadName[rand.nextInt(dadName.length)], dadPost[rand.nextInt(dadPost.length)]);
			player.addChatComponentMessage(new TextComponentString("" + TextFormatting.GREEN + TextFormatting.BOLD + TextFormatting.UNDERLINE + TextFormatting.ITALIC + s));
			EntityFireworkRocket firework = new EntityFireworkRocket(player.worldObj);
			firework.setPosition(player.posX, player.posY, player.posZ);
			player.worldObj.spawnEntityInWorld(firework);
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.getModID().equals(SkyGrid8.MODID))
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
		}
	}
}
