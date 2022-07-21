package weatherpony.minelittlebrony2.entity.living.pony.components


import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.networking.v1.{PacketByteBufs, ServerPlayNetworking}
import net.minecraft.block.entity.ViewerCountManager
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import weatherpony.minelittlebrony2.entity.living.{PonyInventory2, PonyLogicReasoning}
import weatherpony.minelittlebrony2.entity.living.pony.BasePony
import weatherpony.minelittlebrony2.entity.living.pony.ComponentCompanion
import weatherpony.minelittlebrony2.entity.living.pony.ScalaPony
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IHaveAnInventory
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.mob.MobEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import net.minecraft.util.math.MathHelper
import org.jetbrains.annotations.Nullable
import weatherpony.minelittlebrony2.MineLBrony2_FabricMod
import weatherpony.minelittlebrony2.entity.living.pony.logic.PonyInventoryLogicStub
import weatherpony.minelittlebrony2.screen.PonyInventoryScreenHandler
import net.minecraft.entity.EntityData
import net.minecraft.entity.SpawnReason
import net.minecraft.network.PacketByteBuf
import net.minecraft.world.LocalDifficulty
import net.minecraft.world.ServerWorldAccess

import java.lang.Iterable

trait InventoryManagement extends MobEntity with BasePony with IHaveAnInventory {//with IInventoryChangedListener{
  protected[components] var inventory:PonyInventory2[ScalaPony]= new PonyInventory2[ScalaPony](this.getThisAsScalaPony())

  override def initialize(world: ServerWorldAccess, difficulty: LocalDifficulty, spawnReason: SpawnReason, @Nullable entityData: EntityData, @Nullable entityNbt: NbtCompound): EntityData = {
    var retData = super.initialize(world,difficulty,spawnReason,entityData,entityNbt)
    inventory.enforcePonyProperties()
    retData
  }
  override def getMainHandStack():ItemStack = {
    inventory.getMainHandStack
  }
  override def getOffHandStack():ItemStack = {
    inventory.getOffhandStack
  }

  override def getArmorItems: Iterable[ItemStack] = {
    inventory.armor
  }

  override def getEquippedStack(slot: EquipmentSlot): ItemStack = {
    val i = slot.getEntitySlotId
    slot match {
      case EquipmentSlot.MAINHAND => inventory.getMainHandStack
      case EquipmentSlot.OFFHAND => inventory.getOffhandStack
      case default => if (inventory.armor.size() > i) inventory.armor.get(i) else ItemStack.EMPTY
    }
  }

  override def canPickUpLoot() = false

  override def equipStack(slot: EquipmentSlot, stack: ItemStack): Unit = {
    val i = slot.getEntitySlotId
    slot.getType match {
      case EquipmentSlot.Type.ARMOR => if(inventory.armor.size()>i) inventory.armor.set(i, stack) else throw new RuntimeException()
      case EquipmentSlot.Type.HAND => {
        if(slot == EquipmentSlot.OFFHAND) inventory.offHand.set(0,stack)
        else throw new RuntimeException("not yet supported: setting main hand of ponies")
      }


    }
  }

  override def getInventory():PonyInventory2[ScalaPony]=inventory
  override def refresh_middle():Unit={
    super.refresh_middle()
    //inventory.setupInventory()
  }
  override def refresh_last():Unit={
    super.refresh_last()
    inventory.enforcePonyProperties()
  }
  override def onDeath(cause: DamageSource):Unit={
    vanishCursedItems()
    inventory.dropAll()
    super.onDeath(cause)
  }

  override def _openInventory(player: PlayerEntity): Unit = {
    if (!this.world.isClient) {
      val playerServ = player.asInstanceOf[ServerPlayerEntity];
      if (playerServ.currentScreenHandler ne playerServ.playerScreenHandler) playerServ.closeHandledScreen
      playerServ.incrementScreenHandlerSyncId

      val payload = new PacketByteBuf(Unpooled.buffer())//PacketByteBufs.create();
      payload.writeByte(playerServ.screenHandlerSyncId.toByte)
      payload.writeInt(this.getId)
      payload.writeInt(PonyInventoryLogicStub.PonyLogicStublet.compress(this.getMyLogic(PonyLogicReasoning.CAPABILITIES), this.getThisAsScalaPony()));

      ServerPlayNetworking.send(playerServ, MineLBrony2_FabricMod.PONY_INVENTORY_OPEN_ID, payload)
      playerServ.currentScreenHandler = PonyInventoryScreenHandler.makeForServer(playerServ.screenHandlerSyncId, player.getInventory, this.getThisAsScalaPony())
      playerServ.onScreenHandlerOpened(playerServ.currentScreenHandler)
    }
  }

  protected def vanishCursedItems(): Unit = {
    var i = 0
    while ( {
      i < this.inventory.size
    }) {
      val itemStack = this.inventory.getStack(i)
      if (!itemStack.isEmpty && EnchantmentHelper.hasVanishingCurse(itemStack)) this.inventory.removeStack(i)

      i += 1
    }
  }

  override def dropItem(stack: ItemStack, throwRandomly: Boolean, retainOwnership: Boolean): ItemEntity = if (stack.isEmpty) null
  else {
    if (this.world.isClient) this.swingHand(Hand.MAIN_HAND)
    val d = this.getEyeY - 0.30000001192092896D
    val itemEntity = new ItemEntity(this.world, this.getX, d, this.getZ, stack)
    itemEntity.setPickupDelay(40)
    if (retainOwnership) itemEntity.setThrower(this.getUuid)
    var f = .0f
    var g = .0f
    if (throwRandomly) {
      f = this.random.nextFloat * 0.5F
      g = this.random.nextFloat * 6.2831855F
      itemEntity.setVelocity((-MathHelper.sin(g) * f).toDouble, 0.20000000298023224D, (MathHelper.cos(g) * f).toDouble)
    }
    else {
      f = 0.3F
      g = MathHelper.sin(this.getPitch * 0.017453292F)
      val j = MathHelper.cos(this.getPitch() * 0.017453292F)
      val k = MathHelper.sin(this.getYaw() * 0.017453292F)
      val l = MathHelper.cos(this.getYaw() * 0.017453292F)
      val m = this.random.nextFloat * 6.2831855F
      val n = 0.02F * this.random.nextFloat
      itemEntity.setVelocity((-k * j * 0.3F).toDouble + Math.cos(m.toDouble) * n.toDouble, (-g * 0.3F + 0.1F + (this.random.nextFloat - this.random.nextFloat) * 0.1F).asInstanceOf[Double], (l * j * 0.3F).toDouble + Math.sin(m.toDouble) * n.toDouble)
    }
    this.world.spawnEntity(itemEntity)
    itemEntity
  }
  override def writeNbt(compound:NbtCompound):NbtCompound={
    super.writeNbt(compound)
    compound.put("Inventory", this.inventory.writeNbt(new NbtCompound()));
    compound
  }
  override def readNbt(compound:NbtCompound):Unit={
    super.readNbt(compound);
    val nbttaglist = compound.getCompound("Inventory");
    this.inventory.readNbt(nbttaglist);
  }

  val stateManager: ViewerCountManager = null
}
object InventoryManagement extends ComponentCompanion{

}