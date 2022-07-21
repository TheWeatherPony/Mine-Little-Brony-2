package weatherpony.minelittlebrony2.entity.living.ai;

import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
import weatherpony.minelittlebrony2.entity.living.PonyInventory2;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;

public class PonyMeleeAttackGoal<PONY extends PathAwareEntity & IAmAPony> extends MeleeAttackGoal {
    public PonyMeleeAttackGoal(PONY mob, double speed, boolean pauseWhenMobIdle) {
        super(mob, speed, pauseWhenMobIdle);
    }
    @Override
    public void stop() {
        super.stop();
        ((PONY)this.mob).getInventory().clearMainHand();
    }
    @Override
    public void start() {
        super.start();
        PonyInventory2 inv = ((PONY)this.mob).getInventory();
        int maininvsize = inv.getMainInvSize();
        for(int i=0;i<maininvsize;i++){
            ItemStack stack = inv.getStack(i);
            //stack //FIXME - unfinished code (duh)
        }
    }
}
