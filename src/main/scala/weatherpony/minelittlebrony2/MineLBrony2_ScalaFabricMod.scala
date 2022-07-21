package weatherpony.minelittlebrony2

import net.fabricmc.api.ModInitializer
import weatherpony.minelittlebrony2.entity.living.pony.{ComponentCompanion, PonyFromAnimalEntity}

class MineLBrony2_ScalaFabricMod{}
object MineLBrony2_ScalaFabricMod extends ModInitializer {
  override def onInitialize(): Unit = {
    ComponentCompanion.prerig()
    ComponentCompanion.provideClass(classOf[PonyFromAnimalEntity])
    MineLBrony2_FabricMod.giveFromScala(classOf[PonyFromAnimalEntity], PonyFromAnimalEntity.createPonyAttributes())
  }
}
