package kkr.album.components.manager_graph;

import java.io.File;

import kkr.album.components.manager_graph.data.SetData;
import kkr.album.exception.BaseException;

public interface ManagerGraph {

	void createGraph(File file, int width, int height, String titleGraph, String titleX, SetData setData1,
			SetData setData2) throws BaseException;

	void createGraph(File file, int width, int height, String titleGraph, String titleX, SetData setData)
			throws BaseException;
}
