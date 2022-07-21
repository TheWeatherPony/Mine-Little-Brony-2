package weatherpony.minelittlebrony2.entity.living.ai

import com.minelittlepony.api.pony.meta.Race
import weatherpony.minelittlebrony2.MineLBrony2_FabricMod
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning
import weatherpony.minelittlebrony2.entity.living.pony.ScalaPony

class PonyHeals extends PonyAIProvider[ScalaPony]{
  override def onTick: java.util.function.Function[ScalaPony, Void] = {
    return new java.util.function.Function[ScalaPony, Void](){
      override def apply(pony: ScalaPony): Void = {
        var add:Double = 1
        val logic = pony.getMyLogic(PonyLogicReasoning.CAPABILITIES)
        val race = logic.getPonyTypeInfo.getRace(true)
        if(race == Race.CHANGELING){
          if(pony.isDisguised()){
            add *= .5
            //TODO - increase heal rate when around normal ponies ("getting loved")
          }else{
            add *= 2 //changelings heal twice as fast as normal ponies when not disguised
          }
        }else if(race == Race.ALICORN){
          if((MineLBrony2_FabricMod.AICREATOR.asInstanceOf[PonyAIProvider.PonyAIHolderUnit[ScalaPony]]).hasPledgedAllegiance(pony)) add*=5 //alicorns heal 5 times as fast when tamed
          else add *= 3 //3 times as fast when wild
        }else{

        }
        null
      }
    }
  }
}
