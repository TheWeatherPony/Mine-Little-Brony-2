package weatherpony.minelittlebrony2.entity.living.pony.components

import com.minelittlepony.api.pony.meta.Race
import net.minecraft.entity.Entity
import net.minecraft.entity.data.{DataTracker, TrackedData, TrackedDataHandlerRegistry}
import net.minecraft.nbt.NbtCompound
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning
import weatherpony.minelittlebrony2.entity.living.pony.BasePony
import weatherpony.minelittlebrony2.entity.living.pony.ComponentCompanion
import weatherpony.minelittlebrony2.entity.living.pony.ScalaPony
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IMayBeChangeling
import weatherpony.minelittlebrony2.entity.living.pony.logic.PonyLogic

trait PossibleChangeling extends BasePony with IMayBeChangeling
  with DataManagement
  with AttributeModification {

  protected var myDisguiseLogic:PonyLogic[ScalaPony] = null
  protected override var isChangeling:Boolean

  
  override def initDataTracker():Unit={
    super.initDataTracker()
    this.dataTracker.startTracking(PossibleChangeling.CHANGELING_VARIANT,PonyLogic.logicToString(null))
  }
  
  //def isChangeling() = 
  def isDisguised() = isBothChangelingAndDisguised()
  def isBothChangelingAndDisguised():Boolean = {
    return this.isChangeling && this.myDisguiseLogic != null
  }
  override def getMyLogic(reasoning:PonyLogicReasoning):PonyLogic[ScalaPony] = {
    if(this.isChangeling && reasoning!=PonyLogicReasoning.CAPABILITIES && myDisguiseLogic != null){
      myDisguiseLogic
    }else{
      super.getMyLogic(reasoning)
    }
	}
  override def shockOutOfDisguise()={
    this.undisguise()
  }
  override def refresh_early():Unit = {
    this.isChangeling = this.getChangelingLogic().getPonyTypeInfo().getRace(true) == Race.CHANGELING
    super.refresh_early()
  }
  override def writeNbt(compound:NbtCompound):NbtCompound = {
    val r = super.writeNbt(compound)
    if(this.isBothChangelingAndDisguised()){
      compound.putString("ChangelingDisguiseType", PonyLogic.logicToString(this.myDisguiseLogic))
    }
    r
  }
  override def readNbt(compound:NbtCompound):Unit = {
    super.readNbt(compound)
    refresh_early()
    if(this.isChangeling){
      if(compound.contains("ChangelingDisguiseType")){
        val disguiseType = compound.getString("ChangelingDisguiseType")
        myDisguiseLogic = PonyLogic.stringToLogic(disguiseType).asInstanceOf[PonyLogic[ScalaPony]]
      }else{
        myDisguiseLogic = null;
      }
    }
  }
  override def onTrackedDataSet(key:TrackedData[_]):Unit = {
    if(key == PossibleChangeling.CHANGELING_VARIANT){
      val disguiseType = PonyLogic.stringToLogic(this.dataTracker.get(key.asInstanceOf[TrackedData[String]]))
      myDisguiseLogic = disguiseType.asInstanceOf[PonyLogic[ScalaPony]]
    }else
      super.onTrackedDataSet(key)
  }
  
  def undisguise():Unit = {
    if(myDisguiseLogic != null){
      myDisguiseLogic = null
      this.dataTracker.set(PossibleChangeling.CHANGELING_VARIANT, PonyLogic.logicToString(null))
    }
  }
  override def tryAttack(target: Entity):Boolean = {
    val r = super.tryAttack(target)
    if(this.isChangeling && !this.isDisguised() && target.isInstanceOf[ScalaPony] && !target.asInstanceOf[ScalaPony].isChangeling){
      this.disguise(target.asInstanceOf[ScalaPony].getMyLogic(PonyLogicReasoning.VISUAL))
    }
    r
  }
  def disguise(disguise:PonyLogic[ScalaPony]):Unit ={
    if(!this.isChangeling)
      throw new IllegalArgumentException
    this.dataTracker.set(PossibleChangeling.CHANGELING_VARIANT, PonyLogic.logicToString(disguise))
    this.myDisguiseLogic = disguise
  }
  def getActiveDisguiseLogic() = myDisguiseLogic
  def getChangelingLogic() = this.getMyLogic(PonyLogicReasoning.CAPABILITIES)
}

object PossibleChangeling extends ComponentCompanion{
  private[PossibleChangeling] var CHANGELING_VARIANT:TrackedData[String] = null
  override def prepareForUseBy(clazz:Class[_<:ScalaPony])={
    CHANGELING_VARIANT = DataTracker.registerData[String](clazz, TrackedDataHandlerRegistry.STRING)
  }
}