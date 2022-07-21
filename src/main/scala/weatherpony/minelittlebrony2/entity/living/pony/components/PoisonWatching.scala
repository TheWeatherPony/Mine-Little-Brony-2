package weatherpony.minelittlebrony2.entity.living.pony.components

import net.minecraft.entity.Entity
import net.minecraft.entity.effect.{StatusEffect, StatusEffectInstance, StatusEffects}

import java.util.ArrayList
import weatherpony.minelittlebrony2.entity.living.pony.BasePony
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IMonitorPoisons
import net.minecraft.potion.Potion

trait PoisonWatching extends BasePony with IMonitorPoisons{
  var poisoningList = new ArrayList[StatusEffect](3)
  
  override def addStatusEffect(id:StatusEffectInstance,source:Entity):Boolean = {
    val r = super.addStatusEffect(id,source)
    if(r) {
      val potion = id.getEffectType
      if (PoisonWatching.poisonTypes contains potion) {
        if (poisoningList.isEmpty)
          poisoning(true)
        if (!(poisoningList contains potion))
          poisoningList add potion
      }
    }
    return r
  }
  
  override def onStatusEffectUpgraded(id:StatusEffectInstance,p:Boolean,source:Entity):Unit = {
    super.onStatusEffectUpgraded(id, p,source)
  }
  
  override def removeStatusEffect(effect:StatusEffect):Boolean = {
    val r = super.removeStatusEffect(effect)
    if(poisoningList contains effect){
      poisoningList remove effect
      if(poisoningList.isEmpty)
        poisoning(false)
    }
    r
  }
  
  override def isPoisoned():Boolean = {
    !(poisoningList.isEmpty)
  }
    
}
object PoisonWatching {
  final val poisonTypes:ArrayList[StatusEffect] = {
    var list = new ArrayList[StatusEffect](3)
    list add StatusEffects.POISON
    list add StatusEffects.WITHER
    list add StatusEffects.NAUSEA
    list
  }
}