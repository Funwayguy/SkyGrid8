package funwayguy.skygrid.compat.abyssalcraft;

import com.shinoow.abyssalcraft.common.world.WorldProviderDarkRealm;
import funwayguy.skygrid.config.GridRegistry;
import funwayguy.skygrid.world.ChunkProviderGrid;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderDarkRealmGrid extends WorldProviderDarkRealm {

	@Override
	public IChunkGenerator createChunkGenerator() {
		return new ChunkProviderGrid(world, world.getSeed(), GridRegistry.blocksDarkRealm);
	}
}