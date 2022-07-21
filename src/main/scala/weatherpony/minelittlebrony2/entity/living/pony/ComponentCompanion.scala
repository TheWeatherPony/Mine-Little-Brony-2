package weatherpony.minelittlebrony2.entity.living.pony

import java.util.ArrayList
import weatherpony.minelittlebrony2.entity.living.pony.components.DataManagement
import weatherpony.minelittlebrony2.entity.living.pony.components.PossibleChangeling
import weatherpony.minelittlebrony2.entity.living.pony.components.InventoryManagement

import scala.collection.mutable.ListBuffer

trait ComponentCompanion {
  System.out.println("CREATING A COMPANION: "+this.getClass())
  ComponentCompanion.allComponentCompanionsWithData += this
  def prepareForUseBy(by:Class[_<:ScalaPony]):Unit={}
  def prep():Unit={}
}
object ComponentCompanion{
  def prerig():Unit={
    DataManagement.prep()
    BasePony.prep()
    PossibleChangeling.prep()
    InventoryManagement.prep()
  }
  var allComponentCompanionsWithData = ListBuffer[ComponentCompanion]()
  def provideClass(clazz:Class[_<:ScalaPony]):Unit = {
    for(e <- allComponentCompanionsWithData)
      e prepareForUseBy clazz
  }
}