package net.geforcemods.securitycraft.util;

//import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
//import net.minecraft.world.WorldAccess;
//import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class WorldUtils {
//
//	/**
//	 * Correctly schedules a task for execution on the main thread depending on if the
//	 * provided world is client- or serverside
//	 */
//	public static void addScheduledTask(WorldAccess w, Runnable r)
//	{
//		if(w.isClient()) //clientside
//			MinecraftClient.getInstance().execute(r);
//		else //serverside
//			ServerLifecycleHooks.getCurrentServer().execute(r);
//	}

	/**
	 * Performs a ray trace against all blocks (except liquids) from the starting X, Y, and Z
	 * to the end point, and returns true if a block is within that path.
	 *
	 * Args: Starting X, Y, Z, ending X, Y, Z.
	 */
	public static boolean isPathObstructed(Entity entity, World world, double x1, double y1, double z1, double x2, double y2, double z2) {
		return world.raycast(new RaycastContext(new Vec3d(x1, y1, z1), new Vec3d(x2, y2, z2), ShapeType.OUTLINE, FluidHandling.NONE, entity)) != null;
	}

	public static void spawnLightning(World world, Vec3d pos, boolean effectOnly)
	{
		world.spawnEntity(createLightning(world, pos, effectOnly));
	}

	public static LightningEntity createLightning(World world, Vec3d pos, boolean effectOnly)
	{
		LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);

		lightning.refreshPositionAfterTeleport(pos);
		lightning.setCosmetic(effectOnly);
		return lightning;
	}
}