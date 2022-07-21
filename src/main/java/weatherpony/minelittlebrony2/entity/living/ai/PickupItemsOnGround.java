package weatherpony.minelittlebrony2.entity.living.ai;

import java.util.List;
import java.util.function.Function;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;

public class PickupItemsOnGround<PONY extends MobEntity &IAmAPony> extends PonyAIProvider<PONY> {
	private Predicate<Entity> predicate = new Predicate<Entity>() {
		@Override
		public boolean apply(Entity input) {
			return (input instanceof ItemEntity);
		}
	};
	private Function<PONY,Void> pickup=new Function<PONY,Void>(){
		@Override
		public Void apply(PONY pony){
			//this code is from EntityPlayer's onLivingUpdate, near the bottom
			Box axisalignedbb;

            if (pony.hasVehicle() && !pony.getVehicle().isRemoved())
            {
                axisalignedbb = pony.getBoundingBox().union(pony.getVehicle().getBoundingBox()).expand(1.0D, 0.0D, 1.0D);
            }
            else
            {
                axisalignedbb = pony.getBoundingBox().expand(1.0D, 0.5D, 1.0D);
            }
            
			//List<Entity> list = pony.world.getEntitiesWithinAABBExcludingEntity(pony, axisalignedbb);
            List<Entity> list = pony.world.getOtherEntities(pony, axisalignedbb, predicate);
            //above gets only the EntityItems

            for (Entity entity : list){
                if (!entity.isRemoved()){
                    //this.collideWithPlayer(entity);
                	collideWithPony(pony,entity);
                }
            }
			return null;
		}
	};
	protected void collideWithPony(PONY pony,Entity entity){
        if(entity instanceof ItemEntity)
		    //essentially EntityPlayers' collideWithPlayer
            onPonyCollision(pony,(ItemEntity)entity);
	}
    protected void onPonyCollision(PONY pony, ItemEntity entityitem) {
        //based on ItemEntity's onPlayerCollision
        if (!entityitem.world.isClient()) {
            ItemStack itemStack = entityitem.getStack();
            Item item = itemStack.getItem();
            int i = itemStack.getCount();
            if (!entityitem.cannotPickup() && (entityitem.getOwner() == null || entityitem.getOwner().equals(pony.getUuid())) && pony.getInventory().insertStack(itemStack)) {
                pony.sendPickup(entityitem, i);
                if (itemStack.isEmpty()) {
                    entityitem.discard();
                    itemStack.setCount(i);
                }

                //player.increaseStat(Stats.PICKED_UP.getOrCreateStat(item), i);
                pony.triggerItemPickedUpByEntityCriteria(entityitem);
            }

        }
    }

	@Override
	public Function<PONY,Void> onTick(){
		return this.pickup;
	}
}
