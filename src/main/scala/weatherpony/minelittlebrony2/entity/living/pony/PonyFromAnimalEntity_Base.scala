package weatherpony.minelittlebrony2.entity.living.pony

import weatherpony.minelittlebrony2.entity.living.pony.ducks.MiscHelperFunctions
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IChangeAttributes
import net.minecraft.entity.passive.TameableEntity
import weatherpony.minelittlebrony2.entity.living.pony.ducks.PossibleAI
import net.minecraft.entity.EntityType
import net.minecraft.entity.ai.pathing.EntityNavigation
import net.minecraft.text.HoverEvent
import net.minecraft.world.World

abstract class PonyFromAnimalEntity_Base(entityTyp:EntityType[_ <: PonyFromAnimalEntity_Base], wworld:World) extends TameableEntity(entityTyp,wworld)
  with PossibleAI with IChangeAttributes
  with MiscHelperFunctions
  {
  override def _getEntityDataManager() = dataTracker
  override def getInWaterVariable() = this.touchingWater
  override def pony_getHoverEvent():HoverEvent = getHoverEvent()
  override def RAND()=this.random
  override def cannotDespawn()=true

  override def getMinAmbientSoundDelay() = {
    80
  }
 
  override def initGoals():Unit = {
//    this.aiSit = new EntityAISit(this)
    super.initGoals()
  }
  
  override def keepsTrackOfAnAttackTargetForAI() = true
  override def pony_getAttackTarget() = {
    this.getTarget()
  }
  def pony_getNavigator: EntityNavigation = this.navigation
}