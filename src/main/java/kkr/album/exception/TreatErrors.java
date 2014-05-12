package kkr.album.exception;

import org.apache.log4j.Logger;


public final class TreatErrors {
	private static final Logger logger = Logger.getLogger(TreatErrors.class);

	private TreatErrors() {
		super();
	}

	public static final void treatException(Throwable throwable) {
		if (throwable != null) {
			if (throwable instanceof ConfigurationException) {
				logger.error("###############################");
				logger.error("CONFIGURATION PROBLEM");
				logger.error("");
				printCauses(throwable);
				logger.error("###############################");
				return;
			} else if (throwable instanceof FunctionalException) {
				logger.error("###############################");
				logger.error("FUNCTIONAL PROBLEM");
				logger.error("");
				printCauses(throwable);
				logger.error("###############################");
				return;
			} else if (throwable instanceof TechnicalException) {
				logger.error("###############################");
				logger.error("TECHNICAL PROBLEM");
				logger.error("");
				printCauses(throwable);
				logger.error("-------------------------------");
				logger.error("", throwable);
				logger.error("###############################");
				return;
			}
		}

		logger.error("###############################");
		logger.error("UNEXPECTED PROBLEM");
		logger.error("");
		printCauses(throwable);
		logger.error("-------------------------------");
		logger.error("", throwable);
		logger.error("###############################");
	}

	private static void printCauses(Throwable throwable) {
		if (throwable == null) {
			return;
		}
		logger.error("REASON: ");
		logger.error(throwable.getMessage());
		Throwable th = throwable.getCause();
		for (int n = 1; th != null; th = th.getCause(), n++) {
			logger.error("CAUSE " + n + ": ");
			logger.error(th.getMessage());
		}
	}
}
