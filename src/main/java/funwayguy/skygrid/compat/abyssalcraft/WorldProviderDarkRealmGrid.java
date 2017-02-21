package funwayguy.skygrid.compat.abyssalcraft;

import net.minecraft.world.chunk.IChunkGenerator;

import com.shinoow.abyssalcraft.common.world.WorldProviderDarkRealm;

import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.world.ChunkProviderGrid;

public class WorldProviderDarkRealmGrid extends WorldProviderDarkRealm {

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderGrid(worldObj, worldObj.getSeed(), GridRegistry.blocksDarkRealm);
	}
}