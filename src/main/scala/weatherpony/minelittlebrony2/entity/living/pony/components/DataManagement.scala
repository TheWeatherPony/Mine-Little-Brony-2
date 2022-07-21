package weatherpony.minelittlebrony2.entity.living.pony.components

import net.minecraft.entity.data.TrackedData

import net.minecraft.nbt.NbtCompound
import weatherpony.minelittlebrony2.entity.living.pony.ComponentCompanion
import weatherpony.minelittlebrony2.entity.living.pony.RootPony
import weatherpony.minelittlebrony2.entity.living.pony.ScalaPony
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IStoreData

trait DataManagement extends RootPony with IStoreData{
  override def initDataTracker():Unit={
    super.initDataTracker()
    this.setupData()
    
  }
  override def writeNbt(compound:NbtCompound):NbtCompound = {
    super.writeNbt(compound)
    for(e <-this.data){
      e.save(this, compound)
    }
    compound
  }
  override def readNbt(compound:NbtCompound):Unit = {
    super.readNbt(compound)
    for(e <-this.data){
      e.load(this, compound)
    }
  }
  override def onTrackedDataSet(key:TrackedData[_]):Unit={
    if(IStoreData.processSerialization(this,key.asInstanceOf[TrackedData[Any]]))
      super.onTrackedDataSet(key)
  }
  override def pony_onInitialSpawn():Unit={
    for(e<-this.data){
      e.onInitialSpawn(this)
    }
  }
}
object DataManagement extends ComponentCompanion{
  //private[this]
  override def prepareForUseBy(clazz:Class[_<:ScalaPony]):Unit={
    IStoreData.prepareForUse(clazz)
  }
}