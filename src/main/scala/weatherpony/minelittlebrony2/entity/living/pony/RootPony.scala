package weatherpony.minelittlebrony2.entity.living.pony

import net.minecraft.entity.LivingEntity
import net.minecraft.scoreboard.Team
import net.minecraft.text.{LiteralTextContent, MutableText, Style, Text}
import weatherpony.minelittlebrony2.entity.living.pony.ducks.MiscHelperFunctions

trait RootPony extends LivingEntity with MiscHelperFunctions {
  /*override def _setSize(width:Float, height:Float):Unit = {
    super.setSize(width, height)
  }*/
  
  def getPonyName():String
  override def getCustomName():Text = {
    //super.getCustomName
    MutableText.of(new LiteralTextContent(getPonyName()))
  }

  override def shouldRenderName()={
    //super.shouldRenderName()
    getPonyName()!=null
  }
  override def getDisplayName():Text = {
    /*if(this.getTeam() != null)
			return super.getDisplayName();*/

    return Team.decorateName(this.getScoreboardTeam, this.getName).styled((style) => {
      val name = this.getPonyName()
      def foo(style:Style) = style.withHoverEvent(this.getHoverEvent).withInsertion(name)

      foo(style)
    })
  }
  
  override def heal(healAmount:Float):Unit = {
    this.healPony(healAmount/*net.minecraftforge.event.ForgeEventFactory.onLivingHeal(this, healAmount)*/, true)
  }
  def healPony(halfHearts:Float, potion:Boolean):Unit = {
    if (halfHearts <= 0) return
    val f = this.getHealth()

    if (f > 0.0F){
        this.setHealth(f + halfHearts)
        this.onHeal(potion)
    }
  }
  def onHeal(potion:Boolean):Unit = {}
  def onSetOnFire():Unit = {}
  override def setFireTicks(seconds:Int)={
    if(!this.isOnFire())
  		this.onSetOnFire()
  	super.setFireTicks(seconds)
  }
  def applyEntityAttributes():Unit={}
}