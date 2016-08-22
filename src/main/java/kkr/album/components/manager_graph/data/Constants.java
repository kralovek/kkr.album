package kkr.album.components.manager_graph.data;

import java.awt.Font;

public interface Constants {

	int IMAGE_WIDTH = 1300;
	int IMAGE_HEIGHT = 800;

	int BORDER_X_LEFT = 40;
	int BORDER_X_RIGHT = 40;

	int BORDER_Y_BOTTOM = 40;
	int BORDER_Y_TOP = 40;

	int COUNT_POINTS_X = 15;
	int COUNT_POINTS_Y = 10;

	int AXIS_COMMA_LENGTH = 8;
	int BORDER_SPACE = 3;

	int TITLE_GRAPH_DIFF_Y = 10;
	int TITLE_AXIS_X_DIFF_Y = 20;
	int TITLE_AXIS_Y1_DIFF_X = 30;
	int TITLE_AXIS_Y2_DIFF_X = 30;

	int LABEL_AXIS_X_DIFF_Y = 8;
	int LABEL_AXIS_Y1_DIFF_X = 30;
	int LABEL_AXIS_Y2_DIFF_X = 30;

	Font FONT_LABEL_AXIS = new Font("SansSerif", Font.PLAIN, 12);
	Font FONT_TITLE_GRAPH = new Font("SansSerif", Font.BOLD, 16);
	Font FONT_TITLE_AXIS = new Font("SansSerif", Font.BOLD, 12);
}
