package weatherpony.minelittlebrony2.entity.living.pony.components

import weatherpony.minelittlebrony2.entity.living.pony.BasePony
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.util.math.BlockPos

trait WaterWatching extends BasePony {
  //this code is based on code from EntityWolf/WolfEntity
  
  var isPonyWet = false
  var isDrying = false
  var dryingTime = 0

  def dryTime():Integer = {
    40
  }
  override def tickMovement():Unit = {
    super.tickMovement()
    if(!this.world.isClient && this.isPonyWet && !isDrying && (this.isOnGround() || (this.getVehicle().isInstanceOf[BoatEntity]))){
      isDrying = true
      dryingTime == dryTime
    }
  }
  def isInTheRain():Boolean = {
    var blockpos = this.getBlockPos();

    this.world.hasRain(blockpos) || this.world.hasRain(new BlockPos(this.getX(),this.getY()+this.getHeight(),this.getZ()))
  }
  override def tick():Unit = {
    super.tick()
    //this subsection of code is derived from isWet() in Entity
    var becomingWetFromRain:Option[Boolean] = (this) match {
      case _ if this.isTouchingWater() => Some(false)
      case _ if this.isBeingRainedOn() => Some(true)//private method by default, now... using access widener
      case _ => Option.empty[Boolean]
    }
    if(!becomingWetFromRain.isEmpty){
      if(!isPonyWet){
        this.wetting(becomingWetFromRain.get)
      }
      isPonyWet = true
      isDrying = false
      dryingTime = 0
    }else if(isDrying){
      //else if ((this.isWet || this.isShaking) && this.isShaking)
      // ((A | B) & B) = B (A factors out))
      dryingTime -= 1
      
      if(dryingTime == 0){
        isDrying = false
        isPonyWet = false
      }
    }
  }
}