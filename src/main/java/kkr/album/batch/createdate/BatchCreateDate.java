package kkr.album.batch.createdate;

import java.io.File;

import org.apache.log4j.Logger;

import kkr.album.exception.BaseException;
import kkr.album.model.DateNZ;

public class BatchCreateDate extends BatchCreateDateFwk {
	private static final Logger LOG = Logger.getLogger(BatchCreateDate.class);

	private static String DATE_PATTERN = "yyyyMMdd-HHmmss";

	public void run(File file) throws BaseException {
		LOG.trace("BEGIN");
		try {
			DateNZ date = managerExif.determineDate(file);
			if (date != null) {
				String dateString = date.toString(DATE_PATTERN);
				System.out.println(dateString);
			} else {
				System.out.println("NULL");
			}

			LOG.trace("OK");
		} finally {
			LOG.trace("END");
		}

	}
}
