package weatherpony.minelittlebrony2.entity.living.pony.ducks


import net.minecraft.entity.data.TrackedData

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.collection.mutable.HashSet
import net.minecraft.entity.LivingEntity
import net.minecraft.nbt.NbtCompound

//note: ponies will technically also have the ScalaPony trait -- if you add data, feel free to cast from IStoreData to that
trait IStoreData extends LivingEntity with MiscHelperFunctions {
  protected[this] val data=DataFactory.all.map{d => d.make(this)}
  private[pony] def setupData():Unit={
    for(e <- DataFactory.all){
      e.initialize(this)
    }
  }
  def getData[TYPE<:Data](data:DataFactory[TYPE]):TYPE={
    return this.data{data.spot}.asInstanceOf[TYPE]
  }
}
object IStoreData{
  def prepareForUse(ponyClass:Class[_<:IStoreData]):Unit={
    DataFactory.prepareForUse(ponyClass)
  }
  def processSerialization(pony:IStoreData,key:TrackedData[Any]):Boolean={
    if(DataFactory.serializerLookup contains key){
      pony.getData(DataFactory.serializerLookup(key)).parse(pony,key)
      false
    }else{
      true
    }
  }
}
trait Data{
  def load(pony:IStoreData,nbt:NbtCompound):Unit
  def save(pony:IStoreData,nbt:NbtCompound):Unit
  def parse(pony:IStoreData,key:TrackedData[_]):Unit={
    
  }
  def onInitialSpawn(pony:IStoreData):Unit={
    
  }
}
trait DataFactory[+TYPE<:Data]{
  private[ducks] val spot=DataFactory.all.size
  DataFactory.all append this
  for(e <-this.nbtTagNames()){
    if(!DataFactory.nbts.add(e))
      throw new RuntimeException
  }
  def prepare(ponyClass:Class[_<:IStoreData]):List[TrackedData[_<:Any]]
  
  def make(pony:IStoreData):TYPE
  def initialize(pony:IStoreData):Unit={
    
  }
  def nbtTagNames():Array[String]
}
object DataFactory{
  private[ducks] val all=new ArrayBuffer[DataFactory[Data]]
  private[DataFactory] val nbts=new HashSet[String]
  private[ducks] val serializerLookup = HashMap.empty[TrackedData[_<:Any],DataFactory[Data]]
  def prepareForUse(ponyClass:Class[_<:IStoreData]):Unit={
    for(e<-this.all){
      for(s<-e.prepare(ponyClass)){
        serializerLookup += (s->e)
      }
    }
  }
  
}