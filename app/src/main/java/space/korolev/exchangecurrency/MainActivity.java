package space.korolev.exchangecurrency;


import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String LOG_TAG = "myLogs";

    private static final String URL = "http://www.cbr.ru/scripts/XML_daily.asp";
    // listView для отображения списка валют
    private ListView listView;
    private TextView tv_currency;
    private EditText tv_setcur;
    private int itempos = 0;
    View oldV;
    private String strFormatVal;
    public String[] getItemObjects = {"", "", "", ""};
    // адаптер для кастомизированного списка объектов Currency
    public CurrencyListAdapter currencyListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_currency = findViewById(R.id.tv_cur);
        tv_setcur = findViewById(R.id.tv_setcur);
        // создание listView для отображения списка
        listView = (ListView) findViewById(R.id.listView);

        // создание адаптера
        currencyListAdapter = new CurrencyListAdapter(this);
        // используется AsyncTask, чтобы загрузить XML и вывести список валют
        new DownloadXmlTask().execute(URL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                itempos = position;
                if (oldV!=null) oldV.setBackgroundColor(getResources().getColor(R.color.white));
                view.setBackgroundColor(getResources().getColor(R.color.teal_700));
                oldV = view;

            }
        });

    }

    public void onClickRefreshData(View view){
        new DownloadXmlTask().execute(URL);
    }

    public void onClickCountData(View view){
        new DownloadXmlTask().execute(URL);
        //curval (pos) [charcode, name,nominal, val]
        currencyListAdapter.curVal(itempos,getItemObjects);
        strFormatVal = tv_setcur.getText().toString();
        int nom = Integer.parseInt(getItemObjects[2]);
        double val1 = Double.parseDouble(strFormatVal);
        strFormatVal = getItemObjects[3];
        double val2 = Double.valueOf(getItemObjects[3]);
        double resVal = val1/( val2/nom);
        strFormatVal =getItemObjects[1] + " := " + String.format("%.02f", resVal);
        tv_currency.setText(strFormatVal);
    }



    private class DownloadXmlTask extends AsyncTask<String, Void, List<Currency>> {
        @Override
        protected List<Currency> doInBackground(String... urls) {
            List<Currency> currencyList = null;
            XmlLoader xmlLoader = new XmlLoader(URL);
            try {
                currencyList = xmlLoader.loadXmlFromNetwork();
            } catch (IOException e) {
                Log.d(LOG_TAG, "Connection error");
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                Log.d(LOG_TAG, "XML error");
                e.printStackTrace();
            }
            return currencyList;
        }

        @Override
        protected void onPostExecute(List<Currency> currencyList) {
            // вывод загруженного из xml-файла списка
            currencyListAdapter = new CurrencyListAdapter(space.korolev.exchangecurrency.MainActivity.this, currencyList);
            listView.setAdapter(currencyListAdapter);
        }
    }
}