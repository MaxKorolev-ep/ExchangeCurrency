package space.korolev.exchangecurrency;


import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;

    final String LOG_TAG = "myLogs";

    private static final String URL = "http://www.cbr.ru/scripts/XML_daily.asp";
    // listView для отображения списка валют
 //   CountDownTimer countDownTimer ;
    private ListView listView;
    private TextView tv_currency;
    private EditText tv_setcur;
    private int itempos = 0;
    View oldV;
   public Toast toast;
    private String strFormatVal;
    public String[] getItemObjects = {"", "", "", ""};
    // адаптер для кастомизированного списка объектов Currency
    public CurrencyListAdapter currencyListAdapter;
    public onRefreshTimer refreshTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_currency = findViewById(R.id.tv_cur);
        tv_setcur = findViewById(R.id.tv_setcur);
        // создание listView для отображения списка
        listView = (ListView) findViewById(R.id.listView);
        //создание таймера
        refreshTimer = new onRefreshTimer(120000,1000);
        // создание адаптера
        currencyListAdapter = new CurrencyListAdapter(this);
        // используется AsyncTask, чтобы загрузить XML и вывести список валют
        new DownloadXmlTask().execute(URL);
        //Рекламный баннер
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //Рекламный баннер


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                itempos = position;
                if (oldV!=null) oldV.setBackground(getResources().getDrawable(R.drawable.itemborder));
                view.setBackgroundColor(getResources().getColor(R.color.focuslist));
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
            refreshTimer.cancel();
            refreshTimer.start();
            return currencyList;
        }

        @Override
        protected void onPostExecute(List<Currency> currencyList) {
            // вывод загруженного из xml-файла списка
            currencyListAdapter = new CurrencyListAdapter(space.korolev.exchangecurrency.MainActivity.this, currencyList);
            listView.setAdapter(currencyListAdapter);
        }
    }

    private class onRefreshTimer extends CountDownTimer {
        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public onRefreshTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            new DownloadXmlTask().execute(URL);
            toast = Toast.makeText(getBaseContext(),"Данные обновлены", Toast.LENGTH_LONG);
            toast.show();
        }
    }

}