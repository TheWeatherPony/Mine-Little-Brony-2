package weatherpony.minelittlebrony2.entity.living.pony.components

import com.minelittlepony.api.pony.meta.{Race, Sizes}
import weatherpony.minelittlebrony2.entity.living.pony.BasePony
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning
import net.minecraft.entity.{Entity, LivingEntity}
import net.minecraft.util.math.{BlockPos, Box, MathHelper, Vec3d}
import com.minelittlepony.api.pony.{IPony, IPonyData}
import com.minelittlepony.client.pony.Pony
import com.minelittlepony.client.transform.PonyTransformation
import net.minecraft.block.StairsBlock
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier

//FIXME - is this even needed anymore?
trait MLPPony extends BasePony{/* with IPony{
  override def updateForEntity(entity: Entity): Unit = {}
  override def isPerformingRainboom(entity: LivingEntity):Boolean={
    val vel = entity.getVelocity()
    val zMotion2 = vel.x * vel.x + vel.z * vel.z
    return (isFlying(entity) && canFly()) || entity.isFallFlying() & zMotion2 > 0.02
  }
  override def isDefault: Boolean = false
  override def isCrouching(entity:LivingEntity):Boolean={
    val isSneak = this.isSneaking()//reminder: entity == this
    val isFlying = this.isFlying(entity)
    val isSwimming = this.isSwimming(entity)

    return !isPerformingRainboom(entity) && !isSwimming && isSneak && !isFlying
  }
  def isFlying(entity: LivingEntity): Boolean = {
    return !(isOnGround(entity)
      || entity.hasVehicle()
      || (entity.isClimbing() )// && !((entity.isInstanceOf[PlayerEntity] && (entity.asInstanceOf[PlayerEntity]).getAbilities().allowFlying)))
      || entity.isSubmergedInWater()
      || entity.isSleeping());
  }
  private def isOnGround(entity: LivingEntity): Boolean = {
    if (entity.isOnGround) return true
    val below = entity.getEntityWorld.getBlockState(entity.getBlockPos.down(1))
    // Check for stairs so we can keep Pegasi from flailing their wings as they descend
    val offsetAmount = if (below.getBlock.isInstanceOf[StairsBlock]) 1 else 0.05
    val pos = entity.getPos
    val blockpos = new BlockPos(Math.floor(pos.x), Math.floor(pos.y - offsetAmount), Math.floor(pos.z))
    !entity.getEntityWorld.isAir(blockpos)
  }
  def isSwimming(entity: LivingEntity): Boolean = {
    return entity.isSwimming() || entity.isInSwimmingPose();
  }

  import net.minecraft.block.Material
  import net.minecraft.entity.LivingEntity
  import net.minecraft.util.math.BlockPos

  def isPartiallySubmerged(entity: LivingEntity): Boolean = {
    return entity.isSubmergedInWater() || (entity.getEntityWorld().getBlockState(entity.getBlockPos).getMaterial() == Material.WATER);
  }
  def isFullySubmerged(entity: LivingEntity): Boolean = {
    return entity.isSubmergedInWater() && (entity.getEntityWorld().getBlockState(new BlockPos(getVisualEyePosition(entity))).getMaterial() == Material.WATER);
  }
  protected def getVisualEyePosition(entity: LivingEntity): Vec3d = {
    val size = if (entity.isBaby) Sizes.FOAL  else metadata.getSize
    new Vec3d(entity.getX, entity.getY + entity.getEyeHeight(entity.getPose).toDouble * size.getScaleFactor, entity.getZ)
  }
  final def metadata = getMetadata
  override def getMetadata():IPony = {
    this.getMyLogic(PonyLogicReasoning.VISUAL).getPonyTypeInfo()
  }
  override def getRace(ignorePony:Boolean):Race = {
    this.getMetadata().getRace()
  }
  override def getTexture():Identifier = {
    this.getMyLogic(PonyLogicReasoning.VISUAL).getSkin()
  }

  import com.minelittlepony.api.pony.IPony
  import com.minelittlepony.client.render.IPonyRenderContext
  import com.minelittlepony.client.render.PonyRenderDispatcher
  import net.minecraft.entity.LivingEntity

  def isSitting(entity: LivingEntity): Boolean = {
    entity.hasVehicle ||
    /*
                || (entity instanceof PlayerEntity
                        && entity.getVelocity().x == 0 && entity.getVelocity().z == 0
                        && !entity.isInsideWaterOrBubbleColumn() && entity.onGround && isCrouching(entity))*/
  }

  def isRidingInteractive(entity: LivingEntity): Boolean = {
    if (isSitting(entity) && entity.getVehicle.isInstanceOf[LivingEntity]) return PonyRenderDispatcher.getInstance.getPonyRenderer(entity.getVehicle.asInstanceOf[LivingEntity]) != null
    false
  }

  def getMountedPony(entity: LivingEntity): IPony = {
    if (entity.hasVehicle && entity.getVehicle.isInstanceOf[LivingEntity]) {
      val mount = entity.getVehicle.asInstanceOf[LivingEntity]
      val render = PonyRenderDispatcher.getInstance.getPonyRenderer(mount)
      return if (render == null) null
      else render.getEntityPony(mount)
    }
    null
  }

  def getAbsoluteRidingOffset(entity: LivingEntity): Vec3d = {
    val ridingPony = getMountedPony(entity)
    if (ridingPony != null) {
      val ridee = entity.getVehicle.asInstanceOf[LivingEntity]
      val offset = PonyTransformation.forSize(ridingPony.getMetadata.getSize).getRiderOffset
      val scale = ridingPony.getMetadata.getSize.getScaleFactor
      return ridingPony.getAbsoluteRidingOffset(ridee).add(0, offset.y - ridee.getHeight * 1 / scale, 0)
    }
    val delta = MinecraftClient.getInstance.getTickDelta
    val vehicle = entity.getVehicle
    val vehicleOffset = if (vehicle == null) 0
    else vehicle.getHeight - vehicle.getMountedHeightOffset
    new Vec3d(MathHelper.lerp(delta, entity.prevX, entity.getX), MathHelper.lerp(delta, entity.prevY, entity.getY) + vehicleOffset, MathHelper.lerp(delta, entity.prevZ, entity.getZ))
  }

  def getComputedBoundingBox(entity: LivingEntity): Box = {
    val scale = getMetadata.getSize.getScaleFactor + 0.1F
    val pos = getAbsoluteRidingOffset(entity)
    val width = entity.getWidth * scale
    new Box(-width, entity.getHeight * scale, -width, width, 0, width).offset(pos)
  }*/
}