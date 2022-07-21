package weatherpony.minelittlebrony2.entity.living.pony.components

import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning
import weatherpony.minelittlebrony2.entity.living.pony.BasePony
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IChangeAttributes
import net.minecraft.entity.attribute.{EntityAttribute, EntityAttributes}

trait AttributeModification extends BasePony with IChangeAttributes {
  override def applyEntityAttributes():Unit={
    super.applyEntityAttributes()
  }
  
  override def applyAttributes_late():Unit={
    super.applyAttributes_late()
    //refreshAttributes()
    
  }
  //don't call this during refresh code!
  override final def refreshAttributes():Unit={
    refreshPony()
  }
  
  override def refresh_last():Unit={
    super.refresh_last()
    val finished = this.getThisAsScalaPony()
    val logic = this.getMyLogic(PonyLogicReasoning.CAPABILITIES)
    val HP = this.getHealth() / this.getMaxHealth()
    this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(logic.getMaxHealth(finished))
    this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(logic.movementSpeed(finished))
    this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(logic.getAttackDamage(finished))
    //this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_SPEED).setBaseValue(logic.getAttackSpeed(finished))
    
    this.setHealth(this.getMaxHealth() * HP)
  }
  
}