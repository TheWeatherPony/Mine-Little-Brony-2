package weatherpony.minelittlebrony2.entity.living.pony

import com.minelittlepony.api.pony.meta.Race
import net.minecraft.entity.ai.goal.GoalSelector
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning
import weatherpony.minelittlebrony2.entity.living.pony.components.AILogic
import weatherpony.minelittlebrony2.entity.living.pony.components.MLPPony
import weatherpony.minelittlebrony2.entity.living.pony.components.PoisonWatching
import weatherpony.minelittlebrony2.entity.living.pony.components.PossibleChangeling
import weatherpony.minelittlebrony2.entity.living.pony.components.Speaking
import weatherpony.minelittlebrony2.entity.living.pony.components.WaterWatching
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony
import weatherpony.minelittlebrony2.MineLBrony2_FabricMod
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.mob.MobEntity
import net.minecraft.world.explosion.Explosion
import weatherpony.minelittlebrony2.entity.living.ai.PonyAIProvider
import weatherpony.minelittlebrony2.entity.living.pony.components.InventoryManagement
import weatherpony.minelittlebrony2.entity.living.pony.components.FakeRiding

object ScalaPony{
  val explodeTwice_explosionPower = 3 //the same as an uncharged Creeper
}

trait ScalaPony extends MobEntity
  with PossibleChangeling
  //with BasePony
  //with DataManagement
  //with AttributeModification
  
  with PoisonWatching
  with WaterWatching
  with Speaking
  
  with AILogic
  with FakeRiding
  with InventoryManagement
  
  with MLPPony
  with StatusUpdateManager
  with IAmAPony{
  var diedByExplosion = false

  def pony_getGoalSelector: GoalSelector = this.goalSelector

  def pony_getTargetSelector: GoalSelector = this.targetSelector
  override def onDeath(source:DamageSource):Unit={
    super.onDeath(source)
    if(this.diedByExplosion && this.getMyLogic(PonyLogicReasoning.CAPABILITIES).explodeTwice()){
      this.world.createExplosion(this,DamageSource.mob(this).setExplosive(),null, this.getX(),this.getY(), this.getZ(), ScalaPony.explodeTwice_explosionPower, false/*this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)*/, Explosion.DestructionType.BREAK)
    }
  }

  override def computeFallDamage(distance:Float,damageMultiplier:Float):Int={
    val logic = this.getMyLogic(PonyLogicReasoning.CAPABILITIES)
    val distance2 = this.fallDistance - logic.fallReduction_constant(this) +3
    val damageMultiplier2 = damageMultiplier * logic.fallReduction_magnitude(this)
    super.computeFallDamage(distance2, damageMultiplier2)
  }
  override def damage(dmgsrc:DamageSource,deal:Float):Boolean = {
    if(this.isInvulnerableTo(dmgsrc)){
			return false;
		}else{
		  var deal2 = deal
			//a little something I added
			if (isChangeling && isDisguised && !this.getActiveDisguiseLogic().canFlyWell) undisguise
      val race = this.getMyLogic(PonyLogicReasoning.CAPABILITIES).getPonyTypeInfo().getRace(true)
      if((dmgsrc.isExplosive() && (race.hasWings()))){
        deal2 = deal2 *2 /3
      }else if((dmgsrc.isMagic() && (race.hasHorn() || race == Race.ZEBRA))){
        deal2 = deal2 *2 /3;
      }
      if((race == Race.ALICORN)/* || this is a guard?*/){
        deal2 = deal2 *2 /3;
      }
		  
			this.onDamaged()
			val r = super.damage(dmgsrc, deal2);
      if(this.isDead){
        diedByExplosion = dmgsrc.isExplosive
      }
      r
		}
  }
  private def onDamaged():Unit={
    var f = MineLBrony2_FabricMod.AICREATOR.asInstanceOf[PonyAIProvider.PonyAIHolderUnit[ScalaPony]].onDamaged()
    if(f!=null)
      f.apply(this)
  }
  //FIXME - convert to Fabric
  //this is for the height an entity is willing to drop in the pathfinding code, under normal conditions
  /*override def getMaxFallHeight():Int = {//TODO - use stuff from PonyLogic
    if (this.keepsTrackOfAnAttackTargetForAI() && this.pony_getAttackTarget() == null)
    {
        return 3;
    }
    else
    {
        var i = (this.getHealth() - this.getMaxHealth() * 0.33F).toInt
        i = i - (3 - this.world.getDifficulty().getId()) * 4;

        if (i < 0)
        {
            i = 0;
        }

        return i + 3;
    }
  }*/
  
}