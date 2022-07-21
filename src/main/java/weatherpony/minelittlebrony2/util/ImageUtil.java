package weatherpony.minelittlebrony2.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;

public class ImageUtil {
	public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException
    {
        BufferedImage bufferedimage;

        try
        {
            bufferedimage = ImageIO.read(imageStream);
        }
        finally
        {
            IOUtils.closeQuietly(imageStream);
        }

        return bufferedimage;
    }
	public static BufferedImage getFromResources(Identifier image) throws IOException {
		return ImageUtil.readBufferedImage(ImageUtil.class.getClassLoader().getResourceAsStream("assets/"+image.getNamespace()+'/'+image.getPath()));
	}
	public static BufferedImage getFromResources_safeish(Identifier image) {
		try{
			return ImageUtil.readBufferedImage(ImageUtil.class.getClassLoader().getResourceAsStream("assets/"+image.getNamespace()+'/'+image.getPath()));
		}catch(Throwable e){
			e.printStackTrace();
			return null;
		}
	}
	@Environment(EnvType.CLIENT)
    public static NativeImage getNativeFromResource(Identifier image) throws IOException {
        return NativeImage.read(ImageUtil.class.getClassLoader().getResourceAsStream("assets/"+image.getNamespace()+'/'+image.getPath()));
    }
}
