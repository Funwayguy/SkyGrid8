package funwayguy.skygrid.compat.abyssalcraft;

import com.shinoow.abyssalcraft.common.world.WorldProviderOmothol;

import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.world.ChunkProviderGrid;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderOmotholGrid extends WorldProviderOmothol {

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderGrid(world, world.getSeed(), GridRegistry.blocksOmothol); //normally I'd use a fixed seed here
	}
}