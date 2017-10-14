package funwayguy.skygrid.compat.abyssalcraft;

import com.shinoow.abyssalcraft.common.world.WorldProviderAbyss;
import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.world.ChunkProviderGrid;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderAbyssalWastelandGrid extends WorldProviderAbyss {

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderGrid(world, world.getSeed(), GridRegistry.blocksAbyssalWasteland);
	}
}