package it.pyrox.jammer.desktop.util;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Utils {
	
	/**
	 * Custom method to create an Image based on a BufferedImage without antialising
	 * when resizing. Needed because Image constructor with smooth parameter can't be used
	 * in this case because image is not from a URL and setting the smooth parameter
	 * in the enclosing ImageView doesn't work
	 * 
	 * @param bufferedImage The input image
	 * @param scale The scale factor for the result image
	 * @return
	 */
	public static Image getScaledAntialisedImageFromBufferedImage(BufferedImage bufferedImage, int scale) {
		Image imageFromSwing = SwingFXUtils.toFXImage(bufferedImage, null);
		int width = (int) imageFromSwing.getWidth();
		int height = (int) imageFromSwing.getHeight();
		WritableImage resultImage = new WritableImage(width * scale, height * scale);
		PixelReader pixelReader = imageFromSwing.getPixelReader();
		PixelWriter pixelWriter = resultImage.getPixelWriter();
		for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = pixelReader.getColor(i, j);
                for (int k = 0; k < scale; k++) {
                	for (int w = 0; w < scale; w++) {                		
                		pixelWriter.setColor(i * scale + k, j * scale + w, color);
                	}
                }
            }
        }		
		return resultImage;
	}
}
