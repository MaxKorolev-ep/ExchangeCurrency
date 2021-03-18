package space.korolev.exchangecurrency;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс-парсер для заполнения списка элементов Currency данными из xml-файла
 */

public class CurrencyXmlParser {

    final static String LOG_TAG = "myParseLogs";

    public List<space.korolev.exchangecurrency.Currency> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            return readCurrencyList(parser);
        } finally {
            in.close();
        }
    }

    // парсим xml файл в список объектов Currency
    private static List<space.korolev.exchangecurrency.Currency> readCurrencyList(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<space.korolev.exchangecurrency.Currency> currencyList = new ArrayList();
        space.korolev.exchangecurrency.Currency currency = null;

        while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {

            if (parser.getEventType() == XmlPullParser.START_TAG) {

                if (parser.getName().equals("CharCode")) {
                    currency = new space.korolev.exchangecurrency.Currency();
                    currency.setCharCode(parser.nextText());
                    Log.d(LOG_TAG, "CharCode = " + currency.getCharCode());
                }

                else if (currency != null){

                    if (parser.getName().equals("Nominal")) {
                        currency.setNominal(parser.nextText());
                        Log.d(LOG_TAG, "Nominal = " + currency.getNominal());
                    }

                    else if (parser.getName().equals("Name")) {
                        currency.setName(parser.nextText());
                        Log.d(LOG_TAG, "Name = " + currency.getName());
                    }

                    else if (parser.getName().equals("Value")) {
                        String value = parser.nextText().replace(",", ".");
                        currency.setValue(value);
                        currencyList.add(currency);
                        Log.d(LOG_TAG, "Value = " + currency.getValue());
                    }
                }
            }
            parser.next();
        }
        return currencyList;
    }

}