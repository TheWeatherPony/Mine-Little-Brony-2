package weatherpony.minelittlebrony2.entity.living.pony.ducks;

import weatherpony.minelittlebrony2.entity.living.pony.logic.PonyLogic;

public interface IMayBeChangeling extends IHavePonyLogic{
	public boolean isChangeling();
	public boolean isDisguised();
	public PonyLogic getActiveDisguiseLogic();
	public PonyLogic getChangelingLogic();
	public void shockOutOfDisguise();
}
