package funwayguy.skygrid.compat.abyssalcraft;

import net.minecraft.world.chunk.IChunkGenerator;

import com.shinoow.abyssalcraft.common.world.WorldProviderDreadlands;
import com.shinoow.abyssalcraft.lib.ACLib;

import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.world.ChunkProviderGrid;

public class WorldProviderDreadlandsGrid extends WorldProviderDreadlands {

	@Override
	public void createBiomeProvider() {
		biomeProvider = new BiomeProviderDreadlandsGrid(worldObj.getSeed(), worldObj.getWorldInfo().getTerrainType());
		hasNoSky = true;
		setDimension(ACLib.dreadlands_id);
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderGrid(worldObj, worldObj.getSeed(), GridRegistry.blocksDreadlands);
	}
}