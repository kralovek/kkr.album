package kkr.album.components.manager_graph.utils;

import kkr.album.components.manager_graph.data.ImageData;

public class AdaptPoint {

	public static int x(ImageData imageData, int pos) {
		return pos;
	}

	public static int y(ImageData imageData, int pos) {
		int retval = imageData.getImage().getHeight() - pos;
		return retval;
	}
}
