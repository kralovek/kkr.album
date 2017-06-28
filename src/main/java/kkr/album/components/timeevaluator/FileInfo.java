package kkr.album.components.timeevaluator;

import java.io.File;

import kkr.album.model.DateNZ;
import kkr.album.utils.UtilsPattern;
import kkr.album.utils.UtilsTimes;

public class FileInfo {
	private TimeType type;
	private String symbol;
	private DateNZ dateMove;
	private DateNZ dateOrigin;

	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
		type = UtilsTimes.timeTypeFromFile(file);
		if (type == null) {
			throw new IllegalArgumentException("Unsupported file type: " + file.getName());
		}
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public DateNZ getDateOrigin() {
		return dateOrigin;
	}

	public void setDateOrigin(DateNZ dateOrigin) {
		this.dateOrigin = dateOrigin;
	}

	public DateNZ getDateMove() {
		return dateMove;
	}

	public void setDateMove(DateNZ dateMove) {
		this.dateMove = dateMove;
	}

	public TimeType getType() {
		return type;
	}

	public String toString() {
		return "[" + symbol + "] (" + type.name() + ") "
				+ (dateMove != null ? UtilsPattern.DATE_FORMAT_DATETIME.format(dateMove) : "")
				+ (dateOrigin != null ? " <- " + UtilsPattern.DATE_FORMAT_DATETIME.format(dateOrigin) : "");
	}
}