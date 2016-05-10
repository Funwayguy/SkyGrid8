package skygrid8.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.FoodStats;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import skygrid8.core.SG_Settings;
import skygrid8.core.SkyGrid8;

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
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(event.getModID().equals(SkyGrid8.MODID))
		{
			ConfigHandler.config.save();
			ConfigHandler.initConfigs();
		}
	}
}
