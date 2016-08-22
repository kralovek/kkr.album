package kkr.album.batch.createdate;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import kkr.album.exception.BaseException;

public class BatchCreateDate extends BatchCreateDateFwk {
	private static final Logger LOG = Logger.getLogger(BatchCreateDate.class);

	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd-HHmmss");

	public void run(File file) throws BaseException {
		LOG.trace("BEGIN");
		try {
			Date date = managerExif.determineDate(file);
			if (date != null) {
				String dateString = DATE_FORMAT.format(date);
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
