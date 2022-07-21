package weatherpony.minelittlebrony2.entity.living.ai

import java.util.UUID
import com.minelittlepony.api.pony.meta.Race
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.ai.goal.{FollowOwnerGoal, SitGoal}
import net.minecraft.entity.passive.TameableEntity
import weatherpony.minelittlebrony2.entity.living.ai.PonyAIProvider.IAllianceProvider
import weatherpony.minelittlebrony2.entity.living.ai.PonyAIProvider.ISittingController
import weatherpony.minelittlebrony2.entity.living.pony.ScalaPony

import java.util.ArrayList
import net.minecraft.item.{ItemStack, Items}
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.potion.{PotionUtil, Potions}
import weatherpony.minelittlebrony2.entity.living.pony.StatusEffect
import weatherpony.minelittlebrony2.entity.living.PonyLogicReasoning

class PonyAITamable2 extends PonyAIProvider[ScalaPony]
  with IAllianceProvider[ScalaPony]
  with ISittingController[ScalaPony]
  //with SittingDictator
  //with DataFactory[PonyAIData]
  {
  
  def attemptTame(pony:ScalaPony,player:PlayerEntity):Boolean={
    return true
  }
  
  val tamingSmokeStatus = new StatusEffect(){
    this.number = 6;//not registered...
    override def execute(pony:ScalaPony):Unit={
      playTamedEffect(pony,false)
    }
  }
  val tamingHeartsStatus = new StatusEffect(){
    this.number = 7;//not registered...
    override def execute(pony:ScalaPony):Unit={
      playTamedEffect(pony,true)
    }
  }
  
  //DataFactory
  /*protected[ai] var TAMED_DP:TrackedData[java.lang.Byte]=null
  protected[ai] var OWNER_DP:TrackedData[Optional[UUID]]=null*/
  /*override def prepare(ponyClass:Class[_<:IStoreData]):List[TrackedData[_<:Any]]={
    this.TAMED_DP = DataTracker.registerData(ponyClass, TrackedDataHandlerRegistry.BYTE)
    this.OWNER_DP = DataTracker.registerData(ponyClass, TrackedDataHandlerRegistry.OPTIONAL_UUID)
    
    StatusUpdateManager.register(tamingSmokeStatus)
    StatusUpdateManager.register(tamingHeartsStatus)
    
    return List(this.TAMED_DP,this.OWNER_DP)
  }*/
  
  /*override def make(pony:IStoreData):PonyAIData={
    return new PonyAIData(pony.asInstanceOf[ScalaPony],this)
  }
  override def initialize(pony:IStoreData):Unit={
    pony.getData().asInstanceOf[ScalaPony].getDataManager().register(TAMED_DP, 0.toByte.asInstanceOf[java.lang.Byte])
    pony.asInstanceOf[ScalaPony].getDataManager().register(OWNER_DP, Optional.absent[UUID]())
  }
  protected[ai] val ownerNBT="owner"
  protected[ai] val tamedNBT="tamed"
  override def nbtTagNames():Array[String]={
    return Array[String]()
  }*/
  //PonyAIProvider
  var aiSit:SitGoal=null;
  override def initAI(pony:ScalaPony):Unit={
    val pony2 = pony.asInstanceOf[TameableEntity with ScalaPony]//FIXME -- stop with the assumptions, as they may not always be true in the future
    aiSit = new SitGoal(pony2)
    pony.pony_getGoalSelector.add(1, this.aiSit)
    pony.pony_getGoalSelector.add(12, new FollowOwnerGoal(pony2, 1.0D, 10.0F, 2.0F, true))
  }
  override def getInteractions():java.util.List[InteractionUnit[ScalaPony]]={
    val l = new ArrayList[InteractionUnit[ScalaPony]]
    //taming
    l.add(new InteractionUnit[ScalaPony]{
      override def iconInCaseOfConflict()=new ItemStack(Items.BONE)
      override def applies(pony:ScalaPony,player:PlayerEntity, using:ItemStack):Boolean={
        if(pony.getMyLogic(PonyLogicReasoning.CAPABILITIES).getPonyTypeInfo().getRace(true) == Race.CHANGELING)
          return false
        
        if(isWild(pony)){
          if(!using.isEmpty()){
            if(using.getItem() == Items.APPLE){
              return true
            }
          }
        }
        return false
      }
      override def execute(pony:ScalaPony,player:PlayerEntity,using:ItemStack):Unit={
        if(attemptTame(pony,player)){
          setTamedBy(pony,player)
          pony.pony_getNavigator().stop()
          //set attack target to null
          aiSit.start()
          val pony2 = pony.asInstanceOf[TameableEntity]
          pony2.setSitting(true)
          playTamedEffect(pony,true)
          pony.sendMessage(tamingHeartsStatus)
        }else{
          playTamedEffect(pony,false)
          pony.sendMessage(tamingSmokeStatus)
        }
        if (!player.isCreative){
          using.decrement(1);
        }
      }
    })
    //healing
    l.add(new InteractionUnit[ScalaPony]{
      override def iconInCaseOfConflict=PotionUtil.setPotion(new ItemStack(Items.POTION),Potions.HEALING)
      override def applies(pony:ScalaPony,player:PlayerEntity, using:ItemStack):Boolean={
        if(hasPledgedAllegiance(pony)){
          if(!using.isEmpty()){
            if(true){//FIXME - check HP
              if(using.getItem() == Items.BREAD){
                return true
              }
            }
          }
        }
        return false
      }
      override def execute(pony:ScalaPony,player:PlayerEntity, using:ItemStack):Unit={
        if (!player.isCreative){
          using.decrement(1);
        }
        pony.healPony(5,false)
      }
    })
    //already tamed sitting
    l.add(new InteractionUnit[ScalaPony]{
      override def iconInCaseOfConflict()= ItemStack.EMPTY;
      override def applies(pony:ScalaPony,player:PlayerEntity, using:ItemStack):Boolean={
        return using==ItemStack.EMPTY && (!isWild(pony) && getAllegianceOwner_singular(pony)==player);
      }
      override def execute(pony:ScalaPony,player:PlayerEntity,using:ItemStack):Unit={
        pony.pony_getNavigator().stop()
        //set attack target to null

        PonyAITamable2.this.aiSit.stop()
        val pony2 = pony.asInstanceOf[TameableEntity]
        pony2.setSitting(!pony2.isSitting())
      }
    })
    l
  }
  def playTamedEffect(pony:ScalaPony,tamed:Boolean):Unit={
    /*val enumparticletypes = if(tamed) EnumParticleTypes.HEART else EnumParticleTypes.SMOKE_NORMAL
    for (i <- 0 to 7){
      val d0 = pony.RAND().nextGaussian() * 0.02D;
      val d1 = pony.RAND().nextGaussian() * 0.02D;
      val d2 = pony.RAND().nextGaussian() * 0.02D;
      pony.world.spawnParticle(enumparticletypes, pony.posX + (pony.RAND().nextFloat() * pony.width * 2.0F) - pony.width, pony.posY + 0.5D + pony.RAND().nextFloat() * pony.height, pony.posZ + pony.RAND().nextFloat() * pony.width * 2.0F - pony.width, d0, d1, d2);
    }*/
    pony.asInstanceOf[TameableEntity].showEmoteParticle(tamed)
  }
  override def onDamaged():java.util.function.Function[ScalaPony,Void]={
    ///...TODO ?
    return null
  }
  //ISittingController
  override def isSitting(pony:ScalaPony):Boolean={
    val pony2 = pony.asInstanceOf[TameableEntity]
    pony2.isSitting()
    //aiSit.shouldContinue()
    //return this.isSitting(pony.getDataManager().get(this.TAMED_DP))
  }
  protected[ai] def isSitting(b:Byte):Boolean={
    return (b &2)!=0
  }
  protected[ai] def setSitting(b:Byte,s:Boolean):java.lang.Byte={
    if(s)
      return (b|2).asInstanceOf[Byte]
    else
      return (b&(~2)).asInstanceOf[Byte]
  }
  //IAllianceProvider
  override def isWild(pony:ScalaPony):Boolean={
    return !pony.asInstanceOf[TameableEntity].isTamed()
  }
  protected[ai] def setTamedBy(pony:ScalaPony,player:PlayerEntity):Unit={
    if(player eq null) {
      pony.asInstanceOf[TameableEntity].setTamed(false)
      pony.asInstanceOf[TameableEntity].setOwnerUuid(null)
    }else{
      pony.asInstanceOf[TameableEntity].setTamed(true)
      pony.asInstanceOf[TameableEntity].setOwnerUuid(player.getUuid)
    }
  }
  protected[ai] def setTamed(b:Byte,t:Boolean):java.lang.Byte={
    if(t)
      return (b|1).asInstanceOf[Byte]
    else
      return (b&(~1)).asInstanceOf[Byte]
  }
  override def hasPledgedAllegiance(pony:ScalaPony):Boolean={
    return pony.asInstanceOf[TameableEntity].isTamed
  }
  override def getAllegianceOwner_singular(pony:ScalaPony):LivingEntity={
    return pony.asInstanceOf[TameableEntity].getOwner
    /*try{
      val uuid = this.getOwnerID(pony)
      return if(uuid==null) null else pony.world.getPlayerByUuid(uuid)
    }catch{
      case j:IllegalArgumentException => return null
    }*/
  }
  protected[ai] def getOwnerID(pony:ScalaPony):UUID={
    pony.asInstanceOf[TameableEntity].getOwnerUuid
  }
  override def isAllied(pony:ScalaPony,other:LivingEntity):Boolean={
    if(this.hasPledgedAllegiance(pony)){
      val owner = this.getAllegianceOwner_singular(pony)
      if(owner==other)
        return true
      if(owner!=null)
        return owner.isTeammate(other)
    }
    return pony.getScoreboardTeam().equals(other.getScoreboardTeam())
  }
  override def shouldBeConsidered_Tamed(pony:ScalaPony):Boolean={
    return this.hasPledgedAllegiance(pony)
  }
  
}

/*class PonyAIData(val pony:ScalaPony,val maker:PonyAITamable2) extends Data with SittingDictator{
  var aiSit = new PonyAISit(pony,this)
  override def load(pony:IStoreData,nbt:NBTTagCompound):Unit={
    val s = nbt.getString(maker.ownerNBT)
    val owner = if(s.isEmpty()) Optional.absent[UUID]() else Optional.fromNullable(UUID.fromString(s))
    val tamed = nbt.getByte(maker.tamedNBT)
    if(maker.isSitting(tamed) && !owner.isPresent())
      throw new IllegalStateException("not sure if this should be supported atm... sitting without an owner")
    val pony2 = pony.asInstanceOf[ScalaPony]
    pony2.getDataManager().set[java.lang.Byte](maker.TAMED_DP,tamed)
    pony2.getDataManager().set(maker.OWNER_DP, owner)
    aiSit.setSitting(maker.isSitting(tamed))
  }
  override def save(pony:IStoreData,nbt:NBTTagCompound):Unit={
    val owner = pony.getDataManager().get(maker.OWNER_DP)
    nbt.setString(maker.ownerNBT, if(owner.isPresent()) owner.get().toString() else "")
    nbt.setByte(maker.tamedNBT, pony.getDataManager().get(maker.TAMED_DP))
  }
  override def parse(pony:IStoreData,key:DataParameter[_]):Unit={
    
  }
  override def onInitialSpawn(pony:IStoreData):Unit={
    
  }
  override def setSittingFlags(sitting:Boolean):Unit={
    pony.getDataManager().set(maker.TAMED_DP, maker.setSitting(pony.getDataManager().get(maker.TAMED_DP), sitting))
  }
  override def executingSitting():Boolean={
		if(maker.isWild(pony))
			return false;
		if(this.pony.isInWater())
			return false;
		if(!this.pony.onGround)//hmm...
			return false;
		
		val owner = maker.getAllegianceOwner_singular(pony)
		if(owner == null)
			return true
		if(this.pony.getDistanceSq(owner) < 144.0D && owner.getRevengeTarget() != null)
			return false
		return this.aiSit.isSitting()
  }
}*/