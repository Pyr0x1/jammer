package it.pyrox.jammer.desktop;

import java.util.List;

import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class IconFrameTransition extends Transition {
	
	private static final int ANIMATION_DURATION = 1000;
	
	private List<Image> imageList;
	private ImageView imageView;
	private int frameDuration;
	
	public IconFrameTransition(List<Image> imageList, ImageView imageView) {
		this.imageList = imageList;
		this.imageView = imageView;
		this.frameDuration = ANIMATION_DURATION / imageList.size();
		setCycleDuration(Duration.millis(ANIMATION_DURATION));
		setCycleCount(INDEFINITE);
	}

	@Override
	protected void interpolate(double fraction) {
		int index = getCurrentFrameIndex(fraction);		
        imageView.setImage(imageList.get(index));
	}
	
	private int getCurrentFrameIndex(double fraction) {
		int result = 0;
		for (int i = 0; i < imageList.size(); i++) {
			if (ANIMATION_DURATION * fraction >= frameDuration * i && ANIMATION_DURATION * fraction < frameDuration * (i + 1)) {
				result = i;
				break;
			}
		}
		return result;
	}
}
