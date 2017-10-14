package funwayguy.skygrid.compat.abyssalcraft;

import com.shinoow.abyssalcraft.common.world.WorldProviderDreadlands;
import com.shinoow.abyssalcraft.lib.ACLib;

import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.world.ChunkProviderGrid;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderDreadlandsGrid extends WorldProviderDreadlands {

	@Override
	public void init() {
		biomeProvider = new BiomeProviderDreadlandsGrid(world.getSeed(), world.getWorldInfo().getTerrainType());
		hasSkyLight = true;
		setDimension(ACLib.dreadlands_id);
	}

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderGrid(world, world.getSeed(), GridRegistry.blocksDreadlands);
	}
}