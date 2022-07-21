package weatherpony.minelittlebrony2.entity.living.pony

import net.minecraft.entity.attribute.{DefaultAttributeContainer, EntityAttributes}
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.passive.PassiveEntity
import net.minecraft.entity.{EntityData, EntityType, SpawnReason}
import net.minecraft.world.{LocalDifficulty, ServerWorldAccess, World}
import weatherpony.minelittlebrony2.entity.living.pony.components.InteractionHandling
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.{ActionResult, Hand}
import org.jetbrains.annotations.Nullable

class PonyFromAnimalEntity(entityTyp:EntityType[_ <: PonyFromAnimalEntity], wworld:World) extends PonyFromAnimalEntity_Base(entityTyp,wworld) with ScalaPony
with InteractionHandling
{
  override def interact(player:PlayerEntity,hand:Hand):ActionResult={
    //System.out.println("Starting Interaction in last Pony class")
    return super.interact(player, hand)
  }

  override def initialize(world: ServerWorldAccess, difficulty: LocalDifficulty, spawnReason: SpawnReason, @Nullable entityData: EntityData, @Nullable entityNbt: NbtCompound): EntityData = {
    this.pony_onInitialSpawn()
    var livingdata2 = super.initialize(world,difficulty,spawnReason,entityData,entityNbt)

    livingdata2
  }

  override def createChild(world: ServerWorld, entity: PassiveEntity): PassiveEntity = null
}
object PonyFromAnimalEntity{
  def createPonyAttributes(): DefaultAttributeContainer.Builder ={
    MobEntity.createMobAttributes()
      .add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0D)
      .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
      .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1)
  }
}