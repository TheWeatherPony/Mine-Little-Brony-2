package weatherpony.minelittlebrony2.entity.living.pony.components

import net.minecraft.entity.Entity
import weatherpony.minelittlebrony2.MineLBrony2_FabricMod
import weatherpony.minelittlebrony2.entity.living.ai.PonyAIProvider
import weatherpony.minelittlebrony2.entity.living.ai.PonyAIProvider.PonyAIHolderUnit
import weatherpony.minelittlebrony2.entity.living.pony.BasePony
import weatherpony.minelittlebrony2.entity.living.pony.ScalaPony
import weatherpony.minelittlebrony2.entity.living.pony.ducks.ISitWithoutRiding

trait FakeRiding extends BasePony with ISitWithoutRiding{
  override def shouldRenderAsSitting():Boolean = {
    MineLBrony2_FabricMod.AICREATOR.asInstanceOf[PonyAIProvider.PonyAIHolderUnit[ScalaPony]].isSitting(this.getThisAsScalaPony())
  }
}