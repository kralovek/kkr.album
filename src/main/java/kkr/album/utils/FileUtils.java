package kkr.album.utils;

import javax.xml.stream.XMLStreamReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static final void closeRessource(XMLStreamReader ressource) {
        if (ressource != null) {
            try {
                ressource.close();
            } catch (Exception ex) {
            }
        }

    }

    public static final void closeRessource(Closeable ressource) {
        if (ressource != null) {
            try {
                ressource.close();
            } catch (Exception ex) {
            }
        }

    }
}
