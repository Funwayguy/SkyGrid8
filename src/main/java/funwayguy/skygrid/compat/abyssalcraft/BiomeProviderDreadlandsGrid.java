package funwayguy.skygrid.compat.abyssalcraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.shinoow.abyssalcraft.api.biome.ACBiomes;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BiomeProviderDreadlandsGrid extends BiomeProvider
{

	private GenLayer biomeToUse;
	private GenLayer biomeIndexLayer;
	private BiomeCache biomeCache;
	private List<Biome> biomesToSpawnIn;

	public BiomeProviderDreadlandsGrid()
	{
		biomeCache = new BiomeCache(this);
		biomesToSpawnIn = new ArrayList<Biome>();
		biomesToSpawnIn.add(ACBiomes.dreadlands);
		biomesToSpawnIn.add(ACBiomes.purified_dreadlands);
		biomesToSpawnIn.add(ACBiomes.dreadlands_forest);
		biomesToSpawnIn.add(ACBiomes.dreadlands_mountains);
	}

	public BiomeProviderDreadlandsGrid(long par1, WorldType par3WorldType)
	{
		this();
		GenLayer[] agenlayer = GenLayerDL.makeTheWorld(par1);
		biomeToUse = agenlayer[0];
		biomeIndexLayer = agenlayer[1];
	}

	public BiomeProviderDreadlandsGrid(World par1world)
	{
		this(par1world.getSeed(), par1world.getWorldInfo().getTerrainType());
	}

	@Override
	public List<Biome> getBiomesToSpawnIn()
	{
		return biomesToSpawnIn;
	}

	@Override
	public Biome getBiome(BlockPos pos)
	{
		return this.getBiome(pos, (Biome)null);
	}

	@Override
	public Biome getBiome(BlockPos pos, Biome biomegen)
	{
		Biome biome = biomeCache.getBiome(pos.getX(), pos.getZ(), biomegen);
		if (biome == null)
			return ACBiomes.dreadlands;

		return biome;
	}

	//	@Override
	//	public float[] getRainfall(float[] par1ArrayOfFloat, int par2, int par3, int par4, int par5) {
	//		IntCache.resetIntCache();
	//
	//		if (par1ArrayOfFloat == null || par1ArrayOfFloat.length < par4 * par5)
	//			par1ArrayOfFloat = new float[par4 * par5];
	//
	//		int[] aint = biomeIndexLayer.getInts(par2, par3, par4, par5);
	//
	//		for (int i1 = 0; i1 < par4 * par5; ++i1) {
	//			float f = BiomeGenBase.getBiome(aint[i1]).getRainfall() / 65536.0F;
	//
	//			if (f > 1.0F)
	//				f = 1.0F;
	//
	//			par1ArrayOfFloat[i1] = f;
	//		}
	//
	//		return par1ArrayOfFloat;
	//	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getTemperatureAtHeight(float par1, int par2) {
		return par1;
	}

	public float[] getTemperatures(float[] par1ArrayOfFloat, int par2, int par3, int par4, int par5) {
		IntCache.resetIntCache();

		if (par1ArrayOfFloat == null || par1ArrayOfFloat.length < par4 * par5)
			par1ArrayOfFloat = new float[par4 * par5];

		int[] aint = biomeIndexLayer.getInts(par2, par3, par4, par5);

		for (int i1 = 0; i1 < par4 * par5; ++i1) {
			float f = Biome.getBiome(aint[i1]).getDefaultTemperature() / 65536.0F; //getIntTemperature()

			if (f > 1.0F)
				f = 1.0F;

			par1ArrayOfFloat[i1] = f;
		}

		return par1ArrayOfFloat;
	}

	@Override
	public Biome[] getBiomesForGeneration(Biome[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5)
	{
		if (par1ArrayOfBiomeGenBase == null || par1ArrayOfBiomeGenBase.length < par4 * par5)
			par1ArrayOfBiomeGenBase = new Biome[par4 * par5];

		int[] aint = biomeToUse.getInts(par2, par3, par4, par5);

		for (int i = 0; i < par4 * par5; ++i)
			if (aint[i] >= 0)
				par1ArrayOfBiomeGenBase[i] = Biome.getBiome(aint[i]);
			else
				par1ArrayOfBiomeGenBase[i] = ACBiomes.dreadlands;

		return par1ArrayOfBiomeGenBase;
	}

	@Override
	public Biome[] getBiomes(Biome[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5)
	{
		return getBiomes(par1ArrayOfBiomeGenBase, par2, par3, par4, par5, true);
	}

	@Override
	public Biome[] getBiomes(Biome[] par1ArrayOfBiomeGenBase, int x, int y, int width, int length, boolean cacheFlag)
	{
		IntCache.resetIntCache();

		if (par1ArrayOfBiomeGenBase == null || par1ArrayOfBiomeGenBase.length < width * length)
			par1ArrayOfBiomeGenBase = new Biome[width * length];

		if (cacheFlag && width == 16 && length == 16 && (x & 15) == 0 && (y & 15) == 0) {
			Biome[] abiomegenbase1 = biomeCache.getCachedBiomes(x, y);
			System.arraycopy(abiomegenbase1, 0, par1ArrayOfBiomeGenBase, 0, width * length);
			return par1ArrayOfBiomeGenBase;
		} else {
			int[] aint = biomeIndexLayer.getInts(x, y, width, length);

			for (int i = 0; i < width * length; ++i)
				if (aint[i] >= 0)
					par1ArrayOfBiomeGenBase[i] = Biome.getBiome(aint[i]);
				else
					par1ArrayOfBiomeGenBase[i] = ACBiomes.dreadlands;

			return par1ArrayOfBiomeGenBase;
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean areBiomesViable(int par1, int par2, int par3, List par4List) {
		IntCache.resetIntCache();
		int l = par1 - par3 >> 2;
		int i1 = par2 - par3 >> 2;
		int j1 = par1 + par3 >> 2;
		int k1 = par2 + par3 >> 2;
		int l1 = j1 - l + 1;
		int i2 = k1 - i1 + 1;
		int[] aint = biomeToUse.getInts(l, i1, l1, i2);

		for (int j2 = 0; j2 < l1 * i2; ++j2) {
			Biome biomegenbase = Biome.getBiome(aint[j2]);

			if (!par4List.contains(biomegenbase))
				return false;
		}

		return true;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public BlockPos findBiomePosition(int par1, int par2, int par3, List par4List, Random par5Random) {
		IntCache.resetIntCache();
		int l = par1 - par3 >> 2;
		int i1 = par2 - par3 >> 2;
		int j1 = par1 + par3 >> 2;
		int k1 = par2 + par3 >> 2;
		int l1 = j1 - l + 1;
		int i2 = k1 - i1 + 1;
		int[] aint = biomeToUse.getInts(l, i1, l1, i2);
		BlockPos blockpos = null;
		int j2 = 0;

		for (int k2 = 0; k2 < l1 * i2; ++k2) {
			int l2 = l + k2 % l1 << 2;
			int i3 = i1 + k2 / l1 << 2;
			Biome biomegenbase = Biome.getBiome(aint[k2]);

			if (par4List.contains(biomegenbase) && (blockpos == null || par5Random.nextInt(j2 + 1) == 0)) {
				blockpos = new BlockPos(l2, 0, i3);
				++j2;
			}
		}

		return blockpos;
	}

	@Override
	public void cleanupCache()
	{
		biomeCache.cleanupCache();
	}
}