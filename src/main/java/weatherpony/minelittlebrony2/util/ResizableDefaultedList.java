package weatherpony.minelittlebrony2.util;

import com.google.common.collect.Lists;
import net.minecraft.util.collection.DefaultedList;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResizableDefaultedList<E> extends DefaultedList<E> {
    protected final E initialElement;
    public static <E> ResizableDefaultedList<E> ofSize(int size) {
        return new ResizableDefaultedList(new ArrayList(size), (Object)null);
    }

    public static <E> ResizableDefaultedList<E> ofSize(int size, E defaultValue) {
        Validate.notNull(defaultValue);
        Object[] objects = new Object[size];
        Arrays.fill(objects, defaultValue);
        return new ResizableDefaultedList(new ArrayList(Arrays.asList(objects)), defaultValue);
    }

    protected ResizableDefaultedList(List<E> delegate, @Nullable E initialElement) {
        super(delegate, initialElement);
        this.initialElement = initialElement;
    }
    public DefaultedList<E> resize(int newSize){
        int oldSize = this.size();
        DefaultedList<E> ret = DefaultedList.<E>ofSize(0);
        if(oldSize > newSize) {
            for(int i=oldSize-1;i>=newSize;i--)
                ret.add(this.remove(i));
        }else if(oldSize < newSize) {
            for(int i=oldSize;i<newSize;i++)
                this.add(this.initialElement);
        }//else (oldSize == newSize) noop
        return ret;
    }
}
