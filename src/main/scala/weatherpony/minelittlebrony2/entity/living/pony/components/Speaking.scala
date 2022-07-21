package weatherpony.minelittlebrony2.entity.living.pony.components

import net.minecraft.entity.damage.DamageSource
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning
import weatherpony.minelittlebrony2.entity.living.SoundTypes
import weatherpony.minelittlebrony2.entity.living.pony.BasePony
import weatherpony.minelittlebrony2.entity.living.pony.ducks.ISpeak

import net.minecraft.sound.{SoundEvent, SoundEvents}


trait Speaking extends BasePony with ISpeak{

  override def say(sound:SoundTypes):Unit = {
    this.getMyLogic(PonyLogicReasoning.AUDIO).playSound(this.getThisAsScalaPony(),sound)
  }
  /*
  var currentSpeech:SoundTypes = null
  def processSpeech():Unit={
    if(currentSpeech != null){
      this.getMyLogic(PonyLogicReasoning.AUDIO).playSound(this.getThisAsScalaPony(),currentSpeech)
      currentSpeech = null
    }
  }
  */
  override def onHeal(potion:Boolean):Unit = {
    super.onHeal(potion)
    this say (if(potion) SoundTypes.heal_potion else SoundTypes.heal_food)
  }
  override def poisoning(isStarting:Boolean):Unit = {
    this say SoundTypes.poisoned
  }
  override def wetting(rain:Boolean):Unit = {
    this say (if(rain) SoundTypes.water_rained_on else SoundTypes.water_submerged)
  }
  override def getFallSound(heightIn:Int):SoundEvent = {
    if(heightIn > 4){
      this say SoundTypes.big_fall
      return SoundEvents.ENTITY_GENERIC_BIG_FALL
    }else{
      this say SoundTypes.small_fall
      return SoundEvents.ENTITY_GENERIC_SMALL_FALL
    }
  }
  
  
  
  /*override def playLivingSound():Unit = {
    super.playLivingSound()
    //this say SoundTypes.ambient
  }*/
  
  override def getDeathSound(): SoundEvent = {
    this say SoundTypes.death
    return super.getDeathSound()
  }
  
  override def damage(damageSrc:DamageSource, damageAmount:Float):Boolean = {
    if (!this.isInvulnerableTo(damageSrc)){
      if(damageSrc.isExplosive){
        this say SoundTypes.exploded
      }else if(damageSrc.isFire()){
        //that would be waaay too noisy
      }else{
        this say SoundTypes.damaged
      }
    }
    super.damage(damageSrc, damageAmount)
  }
  
  override def setOnFireFor(time:Int):Unit ={
    super.setOnFireFor(time)
    this say SoundTypes.lit_on_fire

  }
  override def extinguish():Unit ={
    if(this.isOnFire())
      this say SoundTypes.extinguished_fire
    super.extinguish()
  }
}