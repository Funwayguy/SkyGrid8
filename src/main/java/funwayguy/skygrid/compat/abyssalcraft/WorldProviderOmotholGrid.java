package funwayguy.skygrid.compat.abyssalcraft;

import net.minecraft.world.chunk.IChunkGenerator;

import com.shinoow.abyssalcraft.common.world.WorldProviderOmothol;

import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.world.ChunkProviderGrid;

public class WorldProviderOmotholGrid extends WorldProviderOmothol {

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderGrid(worldObj, worldObj.getSeed(), GridRegistry.blocksOmothol); //normally I'd use a fixed seed here
	}
}