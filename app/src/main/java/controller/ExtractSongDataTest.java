package controller;

import java.io.File;
import com.sun.scenario.effect.ImageData;

public class ExtractSongDataTest {
	private String filePath;

	public ExtractSongDataTest(String filePath) {
		extractImageFromFile(filePath);
	}

	private ImageData extractImageFromFile(String filePath) {
		ImageData imageData = null;
		File sourcefile = new File(filePath);
		return imageData;
	}
}
