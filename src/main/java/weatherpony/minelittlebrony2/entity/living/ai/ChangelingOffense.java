package weatherpony.minelittlebrony2.entity.living.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;

import java.util.function.Function;
import java.util.function.Predicate;

public class ChangelingOffense<PONY extends LivingEntity & IAmAPony> extends PonyAIProvider<PONY>{
    public static class ChangelingTargetGoal<PONY extends LivingEntity & IAmAPony> extends ActiveTargetGoal<PONY> {
        public ChangelingTargetGoal(PONY pony){
            super((MobEntity)pony, (Class<PONY>) pony.getClass(),10,true, true,new Predicate<LivingEntity>(){
                @Override
                public boolean test(LivingEntity targetPony) {
                    PONY tpony = (PONY)targetPony;
                    if(pony.isChangeling()){
                        if(pony.isDisguised()) return false;//a disguised changeling is a happy changeling
                        return !tpony.isChangeling();//hunt a non-changeling
                    }else{
                        if(tpony.isChangeling()){
                            return !tpony.isDisguised();
                        }else
                            return false;//both non-changeling :)
                    }
                }
            });
        }
        protected double getFollowRange() {
            return 32;
        }
    }
    @Override
    public void initAI(PONY pony){
        pony.pony_getTargetSelector().add(1, new ChangelingTargetGoal(pony));
        pony.pony_getGoalSelector().add(5, new MeleeAttackGoal((PathAwareEntity) pony,1.0D, false));
    }
    @Override
    public Function<PONY,Void> onDamaged(){
        return (PONY pony)->{
            if(pony.isDisguised())
                pony.shockOutOfDisguise();
            return null;
        };
    }
}
