package weatherpony.minelittlebrony2.util;

import net.minecraft.entity.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class DumbyEntity extends Entity {
	public static final String ID = "minelbrony2:dumby";
	public static EntityType DUMBYENTITYTPYE = Registry.<EntityType<DumbyEntity>>register(Registry.ENTITY_TYPE, ID,
			EntityType.Builder.create(new EntityType.EntityFactory<DumbyEntity>(){
				@Override
				public DumbyEntity create(EntityType<DumbyEntity> type, World world) {
					return new DumbyEntity(world);
				}
			},SpawnGroup.MISC).setDimensions(0,0).maxTrackingRange(10).build(ID)
			);
	public static void init(){}
	public DumbyEntity(World world){
		super(DUMBYENTITYTPYE,world);
	}
	public DumbyEntity(LivingEntity basedOn){
		super(DUMBYENTITYTPYE,basedOn.world);
		this.setInfo(basedOn);
	}
	public void setInfo(Entity basedOn){
		this.changedDims = new EntityDimensions(basedOn.getDimensions(EntityPose.CROUCHING).width,0,true);
		this.copyPositionAndRotation(basedOn);
	}
	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		return this.changedDims;
	}
	EntityDimensions changedDims;


	@Override
	protected void initDataTracker() {

	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {

	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {

	}

	@Override
	public Packet<?> createSpawnPacket() {
		return null;
	}
}
