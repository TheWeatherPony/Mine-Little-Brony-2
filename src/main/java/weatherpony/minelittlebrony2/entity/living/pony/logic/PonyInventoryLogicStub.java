package weatherpony.minelittlebrony2.entity.living.pony.logic;

import jline.internal.Nullable;
import net.minecraft.entity.LivingEntity;
import weatherpony.minelittlebrony2.entity.living.pony.ducks.IAmAPony;

public interface PonyInventoryLogicStub<PONY extends LivingEntity & IAmAPony> {
    boolean canWearArmor();
    boolean canDualWield();
    int mainInventorySize(@Nullable PONY pony);
    public final class PonyLogicStublet<PONY extends LivingEntity & IAmAPony> implements PonyInventoryLogicStub<PONY>{
        private static int ARMORMASK   = 1<<31;
        private static int OFFHANDMASK = 1<<30;
        private final boolean canWearArmor, canDualWield;
        private final int mainInventorySize;
        public PonyLogicStublet(int payload){
            this.canWearArmor = (payload & ARMORMASK  ) == ARMORMASK;
            this.canDualWield = (payload & OFFHANDMASK) == OFFHANDMASK;
            this.mainInventorySize = payload & ~(ARMORMASK | OFFHANDMASK);
        }
        public static <PONY extends LivingEntity & IAmAPony> int compress(PonyInventoryLogicStub logic, PONY pony){
            return  (logic.canWearArmor() ? ARMORMASK : 0) |
                    (logic.canDualWield() ? OFFHANDMASK: 0) |
                    (logic.mainInventorySize(pony));
        }

        @Override
        public boolean canWearArmor() {
            return this.canWearArmor;
        }
        @Override
        public boolean canDualWield() {
            return this.canDualWield;
        }
        @Override
        public int mainInventorySize(PONY pony) {
            return this.mainInventorySize;
        }
    }
}
