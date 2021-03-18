package space.korolev.exchangecurrency;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Адаптер для отображения кастомизированного списка валют
 */

public class CurrencyListAdapter extends BaseAdapter{
    Context ctx;
//    public int itempos;
    LayoutInflater lInflater;
    List<space.korolev.exchangecurrency.Currency> objects;

    CurrencyListAdapter(Context context) {
        ctx = context;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    CurrencyListAdapter(Context context, List<space.korolev.exchangecurrency.Currency> currencyList) {
        ctx = context;
        objects = currencyList;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // количество элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView  (int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.currency_row, parent, false);
        }

        space.korolev.exchangecurrency.Currency c = getCurrency(position);


        // заполнение элемента View :
        ((TextView) view.findViewById(R.id.currency_char_code)).setText(c.getCharCode());
        ((TextView) view.findViewById(R.id.currency_name)).setText(c.getName());
        ((TextView) view.findViewById(R.id.currency_nominal)).setText(c.getNominal());
        ((TextView) view.findViewById(R.id.currency_value)).setText(c.getValue());

        return view;
    }

    // возвращает Currency по позиции
    space.korolev.exchangecurrency.Currency getCurrency(int position) {
        return ((space.korolev.exchangecurrency.Currency) getItem(position));
    }



    public String[] curVal(int pos, String[] str)
    {
        space.korolev.exchangecurrency.Currency c = getCurrency(pos);
        str[0] = c.getCharCode();
        str[1] = c.getName();
        str[2] = c.getNominal();
        str[3] = c.getValue();
        return str;
    }



}
