package weatherpony.minelittlebrony2.entity.living.pony.components

import net.minecraft.entity.Entity
import weatherpony.minelittlebrony2.MineLBrony2_FabricMod
import weatherpony.minelittlebrony2.entity.living.pony.BasePony
import weatherpony.minelittlebrony2.entity.living.pony.ScalaPony
import weatherpony.minelittlebrony2.entity.living.ai.PonyAIProvider
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning

trait AILogic extends BasePony{
  override def initializeAI():Unit={
    super.initializeAI()
    MineLBrony2_FabricMod.AICREATOR.asInstanceOf[PonyAIProvider.PonyAIHolderUnit[ScalaPony]].initAI(this.getThisAsScalaPony())
  }
  override def applyEntityAttributes():Unit={
    super.applyEntityAttributes()
    val f = MineLBrony2_FabricMod.AICREATOR.asInstanceOf[PonyAIProvider.PonyAIHolderUnit[ScalaPony]].applyEntityAttributes()
    if(f!=null)
      f.apply(this.getThisAsScalaPony())
  }
  override def applyAttributes_late():Unit={
    super.applyAttributes_late()
    val f = MineLBrony2_FabricMod.AICREATOR.asInstanceOf[PonyAIProvider.PonyAIHolderUnit[ScalaPony]].applyAttributes_late()
    if(f!=null)
      f.apply(this.getThisAsScalaPony())
  }
  override def refresh_early():Unit={
    super.refresh_early()
    val f = MineLBrony2_FabricMod.AICREATOR.asInstanceOf[PonyAIProvider.PonyAIHolderUnit[ScalaPony]].on_refresh_early()
    if(f!=null)
      f.apply(this.getThisAsScalaPony())
  }
  override def refresh_middle():Unit={
    super.refresh_middle()
    val f = MineLBrony2_FabricMod.AICREATOR.asInstanceOf[PonyAIProvider.PonyAIHolderUnit[ScalaPony]].on_refresh_middle()
    if(f!=null)
      f.apply(this.getThisAsScalaPony())
  }
  override def refresh_last():Unit={
    super.refresh_last()
    val f = MineLBrony2_FabricMod.AICREATOR.asInstanceOf[PonyAIProvider.PonyAIHolderUnit[ScalaPony]].on_refresh_last()
    if(f!=null)
      f.apply(this.getThisAsScalaPony())
  }
  override def tick():Unit={
    super.tick()
    val f = MineLBrony2_FabricMod.AICREATOR.asInstanceOf[PonyAIProvider.PonyAIHolderUnit[ScalaPony]].onTick()
    if(f!=null)
      f.apply(this.getThisAsScalaPony())
    this.getMyLogic(PonyLogicReasoning.CAPABILITIES).onTick(this.getThisAsScalaPony())
  }
}