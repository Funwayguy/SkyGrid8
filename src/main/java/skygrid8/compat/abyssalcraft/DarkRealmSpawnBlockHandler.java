package skygrid8.compat.abyssalcraft;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.block.ACBlocks;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import skygrid8.core.SG_Settings;

public class DarkRealmSpawnBlockHandler {

	@SubscribeEvent
	public void onDimensionChange(PlayerChangedDimensionEvent event){
		if(event.toDim == AbyssalCraft.configDimId4){
			if(!event.player.worldObj.isRemote){
				event.player.setPositionAndUpdate(event.player.posX, SG_Settings.height - SG_Settings.dist + 1, event.player.posZ);
				if(event.player.worldObj.isAirBlock(event.player.getPosition().down()))
					event.player.worldObj.setBlockState(event.player.getPosition().down(), ACBlocks.darkstone.getDefaultState());
			}
		}
	}
}
