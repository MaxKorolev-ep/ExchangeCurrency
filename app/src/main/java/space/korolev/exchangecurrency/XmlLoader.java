package space.korolev.exchangecurrency;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Класс для загрузки xml-файла и передачи его парсеру
 */

public class XmlLoader {
    private String urlString;

    public XmlLoader(String urlString) {
        this.urlString = urlString;
    }

    public List<space.korolev.exchangecurrency.Currency> loadXmlFromNetwork() throws XmlPullParserException, IOException {
        InputStream stream = null;
        List<space.korolev.exchangecurrency.Currency> currencyList  = null;
        CurrencyXmlParser currencyXmlParser = new CurrencyXmlParser();
        try {
            stream = downloadUrl();
            currencyList = currencyXmlParser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return currencyList;
    }

    private InputStream downloadUrl() throws IOException {
        URL url = new URL(this.urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 );
        conn.setConnectTimeout(15000 );
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();
        return conn.getInputStream();
    }

}