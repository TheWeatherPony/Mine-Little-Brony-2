package weatherpony.minelittlebrony2.entity.living.pony.components

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.mutable.ArrayBuffer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.{ActionResult, Hand}
import weatherpony.minelittlebrony2.MineLBrony2_FabricMod
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning
import weatherpony.minelittlebrony2.entity.living.ai.InteractionUnit
import weatherpony.minelittlebrony2.entity.living.ai.PonyAIProvider.PonyAIHolderUnit
import weatherpony.minelittlebrony2.entity.living.pony.BasePony
import weatherpony.minelittlebrony2.entity.living.pony.PonyFromAnimalEntity_Base
import weatherpony.minelittlebrony2.entity.living.pony.ScalaPony

trait InteractionHandling extends PonyFromAnimalEntity_Base with BasePony{
  override def interact(player:PlayerEntity,hand:Hand):ActionResult={
    //print("STARTING INTERACTION")
    if(!this.isAlive())
      return ActionResult.PASS
    val itemstack:ItemStack = player.getStackInHand(hand)
    val start = startInteraction(player,itemstack)
    if(start.isEmpty){
      //return ActionResult.FAIL
      return this.interactMob(player, hand)
    }
    if(start.size > 1){
      throw new UnsupportedOperationException("Having 2 interactions that may be applicable at the same time is planned, but not yet implemented")
    }else{
      start{0}.execute(getThisAsScalaPony(), player, itemstack)
    }
    return ActionResult.SUCCESS
  }
  private def startInteraction(player:PlayerEntity,itemstack:ItemStack):ArrayBuffer[InteractionUnit[ScalaPony]]={
    val valids = new ArrayBuffer[InteractionUnit[ScalaPony]]
    parseInteractionList(player,itemstack,valids,this.getMyLogic(PonyLogicReasoning.CAPABILITIES).getInteractions())
    parseInteractionList(player,itemstack,valids,MineLBrony2_FabricMod.AICREATOR.asInstanceOf[PonyAIHolderUnit[ScalaPony]].getInteractions())
    return valids
  }
  private def parseInteractionList(player:PlayerEntity,itemstack:ItemStack,valids:ArrayBuffer[InteractionUnit[ScalaPony]],l:java.util.List[InteractionUnit[ScalaPony]]):Unit={
    for(e<-l.asScala){
      if(e.applies(getThisAsScalaPony(), player, itemstack))
          valids +=e
    }
  }
  
}