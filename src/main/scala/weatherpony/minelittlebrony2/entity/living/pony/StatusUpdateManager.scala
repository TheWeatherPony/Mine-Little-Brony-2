package weatherpony.minelittlebrony2.entity.living.pony

import net.minecraft.entity.Entity
import net.minecraft.entity.passive.TameableEntity

trait StatusUpdateManager extends Entity{
  override def handleStatus(id:Byte):Unit={
    if(StatusUpdateManager.process(id,this.asInstanceOf[ScalaPony]))
        super.handleStatus(id)
  }
  def sendMessage(status:StatusEffect):Unit={
    if(status.number == -1)
      throw new IllegalArgumentException("Status not registered")
    this.world.sendEntityStatus(this, status.number.asInstanceOf[Byte]);
  }
}
object StatusUpdateManager {
  val inUse = new StatusEffect(){
    override def execute(pony:ScalaPony):Unit = {}
  }
  val statusUpdateArray = Array.ofDim[StatusEffect](256)
  statusUpdateArray{7} = inUse//TameableEntity's hearts on tame
  statusUpdateArray{6} = inUse//TameableEntity's smoke on tame
  //statusUpdateArray{18}=inUse//EntityAnimal -- spawns hearts for breeding
  statusUpdateArray{20}=inUse //MobEntity -- spawn effects... (what?) (used to be EntityLiving -- spawnExplosionParticle)
  //LivingEntity's swarm of status
    statusUpdateArray{2}= inUse //generic
    statusUpdateArray{33}=inUse //thorns version of above
    statusUpdateArray{36}=inUse //drown
    statusUpdateArray{37}=inUse //on_fire
    statusUpdateArray{44}=inUse //sweet berry bush
    statusUpdateArray{57}=inUse //freeze

    statusUpdateArray{3}=inUse//deathsound (and death)

    statusUpdateArray{29}=inUse//shield block sound
    statusUpdateArray{30}=inUse//shield break sound

    statusUpdateArray{46}=inUse//portal effects

    statusUpdateArray{47}=inUse//break mainhand
    statusUpdateArray{48}=inUse//break offhand
    statusUpdateArray{49}=inUse//break helmet
    statusUpdateArray{50}=inUse//break chest
    statusUpdateArray{51}=inUse//break pants
    statusUpdateArray{52}=inUse//break boots

    statusUpdateArray{54}=inUse//HoneyBlock rich particles...?
    statusUpdateArray{55}=inUse//swap hands
    statusUpdateArray{60}=inUse//death particles

  statusUpdateArray{53}=inUse //Entity -- HoneyBlock thing
  
  private[this] var next=0
  def process(status:Byte,pony:ScalaPony):Boolean={
    val u=statusUpdateArray{status}
    if(u==null || u==inUse)
      return true
    u.execute(pony)
    return false
  }
  def register(status:StatusEffect):Unit={
    while(next < 256){
      if(statusUpdateArray{next} != null){
        next= next+1
      }else{
        statusUpdateArray{next} = status
        status.number=next
        next= next+1
        return
      }
    }
    throw new IndexOutOfBoundsException("Sadly, all available status IDs are taken :(")
  }
}
trait StatusEffect{
  protected[pony] var number= -1
  def execute(pony:ScalaPony):Unit
}