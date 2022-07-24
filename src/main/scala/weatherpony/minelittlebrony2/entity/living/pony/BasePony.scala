package weatherpony.minelittlebrony2.entity.living.pony

import weatherpony.minelittlebrony2.entity.living.pony.logic.PonyLogic
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning
import net.minecraft.entity.data.{DataTracker, TrackedData, TrackedDataHandlerRegistry}
import net.minecraft.nbt.NbtCompound
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IHavePonyLogic
import net.minecraft.sound.SoundCategory
import weatherpony.minelittlebrony2.entity.living.pony.components.DataManagement
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IStoreData
import weatherpony.minelittlebrony2.entity.living.pony.ducks.DataFactory
import weatherpony.minelittlebrony2.entity.living.pony.ducks.Data



trait BasePony extends RootPony with IHavePonyLogic
  with DataManagement{
  final def getThisAsScalaPony():ScalaPony = this.asInstanceOf[ScalaPony]
  
  var isChangeling:Boolean = false
  var isFlying:Boolean = false//TODO -- connect this for rendering
  
  override def getMyLogic(reasoning:PonyLogicReasoning):PonyLogic[ScalaPony] = {
    this.getData(BasePony.ponyLogicData).logic
  }
  override def getPonyName():String = {
    this.getMyLogic(PonyLogicReasoning.CAPABILITIES).getDisplayName(this.getThisAsScalaPony())
  }
  override def applyEntityAttributes():Unit = {
    super.applyEntityAttributes()
  }
  def applyAttributes_late():Unit = {}
  final def refreshPony():Unit={
    this.refresh_early()
    this.refresh_middle()
    this.refresh_last()
  }
  def refresh_early():Unit={
    
  }
  def refresh_middle():Unit={
    this.getMyLogic(PonyLogicReasoning.CAPABILITIES).setSize(this.getThisAsScalaPony())
  }
  def refresh_last():Unit={
    
  }

  override def getSoundCategory() = SoundCategory.AMBIENT
  
  override def tickMovement():Unit = {
    super.tickMovement()
    if (this.getMyLogic(PonyLogicReasoning.CAPABILITIES).canFlyWell()){
			if (!this.isOnGround()){
				this.isFlying = true;
			}else{
				this.isFlying = false;
			}

			//if ((this.onGround) || (this.motionY >= 0.0D) || (!this.isPegasus));
		}else{
		  this.isFlying = false;
		}
  }
  
  override def pony_onInitialSpawn():Unit = {
    super.pony_onInitialSpawn()
    this.postInitialize()
  }
  override def readNbt(compound:NbtCompound):Unit = {
    super.readNbt(compound)
    this.postInitialize()
  }
  def initializeAI():Unit={}
  def postInitialize():Unit = {
    applyAttributes_late()
  	initializeAI()
  	refreshPony()
  	applyAttributes_late()
  }
  def poisoning(isStarting:Boolean):Unit
  def wetting(rain:Boolean):Unit
}
object BasePony extends ComponentCompanion{
  val ponyLogicData = new PonyLogicDataAddition("PonyType",true)
}
class PonyLogicData(val adder:PonyLogicDataAddition) extends Data{
  var logic:PonyLogic[ScalaPony]=null
  def loadFromString(pony:ScalaPony,logic:String):Unit={
    var ponylogic = PonyLogic.stringToLogic(logic).asInstanceOf[PonyLogic[ScalaPony]]
    if(ponylogic==null && adder.randomizeIfEmpty)
      ponylogic = LogicFactories.getRandomLogic(pony)
    this.logic = ponylogic
    pony._getEntityDataManager().set(adder.serializer,PonyLogic.logicToString(ponylogic))
    pony.refreshAttributes()
  }
  override def load(pony:IStoreData,nbt:NbtCompound):Unit={
    loadFromString(pony.asInstanceOf[ScalaPony],nbt.getString(adder.nbtSave))
  }
  override def save(pony:IStoreData,nbt:NbtCompound):Unit={
    nbt.putString(adder.nbtSave, PonyLogic.logicToString(logic))
  }
  override def parse(pony:IStoreData,key:TrackedData[_]):Unit={
    loadFromString(pony.asInstanceOf[ScalaPony],pony._getEntityDataManager().get(key.asInstanceOf[TrackedData[String]]))
  }
  override def onInitialSpawn(pony:IStoreData):Unit={
    loadFromString(pony.asInstanceOf[ScalaPony],"")
  }
}
class PonyLogicDataAddition(val nbtSave:String,val randomizeIfEmpty:Boolean) extends DataFactory[PonyLogicData]{
  private[pony] var serializer:TrackedData[String]=null
  override def prepare(ponyClass:Class[_<:IStoreData]):List[TrackedData[_<:Any]]={
    serializer = DataTracker.registerData(ponyClass,TrackedDataHandlerRegistry.STRING)
    List(serializer)
  }
  
  override def make(pony:IStoreData):PonyLogicData= new PonyLogicData(this)
  override def initialize(pony:IStoreData):Unit={
    pony.asInstanceOf[ScalaPony]._getEntityDataManager().startTracking(serializer, PonyLogic.logicToString(null))
  }
  def nbtTagNames():Array[String]={
    Array(nbtSave)
  }
}