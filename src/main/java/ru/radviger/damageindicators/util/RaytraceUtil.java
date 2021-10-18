package ru.radviger.damageindicators.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class RaytraceUtil {
    public static double getDistanceToClosestSolidWall(EntityLivingBase viewEntity, double traceDistance) {
        return getClosestSolidWall(viewEntity, new Vec3d(viewEntity.posX, viewEntity.posY + 1.5D, viewEntity.posZ), traceDistance, 0, 0.0D);
    }

    public static double getClosestSolidWall(EntityLivingBase viewEntity, Vec3d startPosition, double traceDistance, int count, double offset) {
        double distance = traceDistance - offset;
        if (count++ <= 20 && distance > 0) {
            Vec3d look = viewEntity.getLookVec();
            Vec3d end = startPosition.add(look.x * distance, look.y * distance, look.z * distance);
            RayTraceResult target = viewEntity.world.rayTraceBlocks(startPosition, end, false, false, true);
            if (target != null && target.typeOfHit == RayTraceResult.Type.BLOCK) {
                IBlockState bs = viewEntity.world.getBlockState(target.getBlockPos());
                Block block = bs.getBlock();

                if (bs.isOpaqueCube() && !block.isAir(bs, viewEntity.world, target.getBlockPos())) {
                    return target.hitVec.distanceTo(new Vec3d(viewEntity.posX, viewEntity.posY + 1.5D, viewEntity.posZ));
                }

                return getClosestSolidWall(viewEntity, target.hitVec.add(look.x, look.y, look.z), traceDistance, count, target.hitVec.distanceTo(startPosition));
            }
        }
        return traceDistance;
    }

    public static RayTraceResult rayTrace(EntityLivingBase viewEntity, double distance) {
        Vec3d start = new Vec3d(viewEntity.posX, viewEntity.posY + 1.5D, viewEntity.posZ);
        Vec3d look = viewEntity.getLookVec();
        Vec3d end = start.add(look.x * distance, look.y * distance, look.z * distance);
        return viewEntity.world.rayTraceBlocks(start, end, false, false, true);
    }

    public static boolean isLookingAt(EntityLivingBase viewEntity, double parDistance, float tick, Entity entity) {
        parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
        if (viewEntity != null) {
            World world = viewEntity.world;
            RayTraceResult objectMouseOver = rayTrace(viewEntity, parDistance);
            if (objectMouseOver != null) {
                parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
            }

            Vec3d dirVec = viewEntity.getLookVec();
            List<Entity> targetedEntities = world.getEntitiesWithinAABB(Entity.class, viewEntity.getEntityBoundingBox().expand(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance));
            return targetedEntities.contains(entity);
        } else {
            return false;
        }
    }

    public static Entity getClosestEntity(EntityLivingBase viewEntity, double parDistance) {
        parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
        Entity result = null;
        double distance = parDistance;
        if (viewEntity != null) {
            World world = viewEntity.world;
            RayTraceResult target = rayTrace(viewEntity, parDistance);
            Vec3d playerPosition = new Vec3d(viewEntity.posX, viewEntity.posY + 1.5D, viewEntity.posZ);
            if (target != null) {
                parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
            }

            Vec3d dirVec = viewEntity.getLookVec();
            Vec3d lookFarCoord = playerPosition.add(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance);
            List<Entity> targetedEntities = world.getEntitiesWithinAABBExcludingEntity(viewEntity, viewEntity.getEntityBoundingBox().expand(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance));

            for (Entity targetedEntity : targetedEntities) {
                if (targetedEntity != null && !targetedEntity.isInvisible()) {
                    double d = viewEntity.getDistance(targetedEntity);
                    RayTraceResult mopElIntercept = targetedEntity.getEntityBoundingBox().calculateIntercept(playerPosition, lookFarCoord);
                    if (mopElIntercept != null && d < distance) {
                        result = targetedEntity;
                        distance = d;
                    }
                }
            }
        }

        return result;
    }

    public static EntityLivingBase getClosestLivingEntity(EntityLivingBase viewEntity, double parDistance) {
        parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
        EntityLivingBase result = null;
        double distance = parDistance;
        if (viewEntity != null) {
            World world = viewEntity.world;
            RayTraceResult target = rayTrace(viewEntity, parDistance);
            Vec3d playerPosition = new Vec3d(viewEntity.posX, viewEntity.posY + 1.5D, viewEntity.posZ);
            if (target != null) {
                parDistance = getDistanceToClosestSolidWall(viewEntity, parDistance);
            }

            Vec3d dirVec = viewEntity.getLookVec();
            Vec3d lookFarCoord = playerPosition.add(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance);
            List<EntityLivingBase> targetedEntities = world.getEntitiesWithinAABB(EntityLivingBase.class, viewEntity.getEntityBoundingBox().expand(dirVec.x * parDistance, dirVec.y * parDistance, dirVec.z * parDistance));
            targetedEntities.remove(viewEntity);

            for (EntityLivingBase targetedEntity : targetedEntities) {
                if (targetedEntity != null && !targetedEntity.isInvisible()) {
                    double d = viewEntity.getDistance(targetedEntity);
                    RayTraceResult mopElIntercept = targetedEntity.getEntityBoundingBox().calculateIntercept(playerPosition, lookFarCoord);
                    if (mopElIntercept != null && d < distance) {
                        result = targetedEntity;
                        distance = d;
                    }
                }
            }
        }

        return result;
    }
}
