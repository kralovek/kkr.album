package kkr.album.main.group;

import java.util.List;

import kkr.album.exception.ConfigurationException;
import kkr.album.main.ConfigArchiveFiles.Mode;

public class Config_MainGroupA {

	public static enum Etape {
		MODIFY_PHOTOS, INDEX_FILES, RESIZE_PHOTOS, ARCHIVE_FILES, COPY_PICASA
	}

	private List<Etape> etapes;

	public Config_MainGroupA(final String[] pArgs)
			throws ConfigurationException {
		init(pArgs);
	}

	protected void init(final String[] pArgs) throws ConfigurationException {
		if (pArgs.length % 2 != 0) {
			throw new ConfigurationException(
					"A command-line parameter is missing a value");
		}
		for (int i = 0; i < pArgs.length; i += 2) {
			if (pArgs[i + 1].isEmpty()) {
				throw new ConfigurationException("Value of the parameter '"
						+ pArgs[i] + "' is empty");
			}

			if ("-etape".equals(pArgs[i])) {
				Etape etape = decodeEtape(pArgs[i + 1]);
				if (etape == null) {
					throw new ConfigurationException("Unknown etape: '"
							+ pArgs[i + 1] + "'");
				}
				etapes.add(etape);
			}
		}
	}

	private Etape decodeEtape(String value) {
		if (Etape.MODIFY_PHOTOS.equals(value)) {
			return Etape.MODIFY_PHOTOS;
		} else if (Etape.INDEX_FILES.equals(value)) {
			return Etape.INDEX_FILES;
		} else if (Etape.RESIZE_PHOTOS.equals(value)) {
			return Etape.RESIZE_PHOTOS;
		} else if (Etape.ARCHIVE_FILES.equals(value)) {
			return Etape.ARCHIVE_FILES;
		} else if (Etape.COPY_PICASA.equals(value)) {
			return Etape.COPY_PICASA;
		} else {
			return null;
		}
	}

	public List<Etape> getEtapes() {
		return etapes;
	}
}
