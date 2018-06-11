package com.example.ashen.csmanager.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.ashen.csmanager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import static com.example.ashen.csmanager.Adapters.Constraints.FIRST_COLUMN;
import static com.example.ashen.csmanager.Adapters.Constraints.SECOND_COLUMN;


/**
 * Created by slash on 2/10/2018.
 */

public class TwoColumnListViewAdapter extends BaseAdapter{

    Activity activity;
    ArrayList<HashMap<String,String>> list;
    TextView txtFirst;
    TextView txtSecond;

    public TwoColumnListViewAdapter(Activity activity, ArrayList<HashMap<String, String>> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = activity.getLayoutInflater();

        if(view==null)
        {
            view = inflater.inflate(R.layout.two_column_layout,null);
            txtFirst = (TextView)view.findViewById(R.id.nameTV);
            txtSecond = (TextView)view.findViewById(R.id.cityTV);
        }
        HashMap<String,String> map = list.get(i);
        txtFirst.setText(map.get(FIRST_COLUMN));
        txtSecond.setText(map.get(SECOND_COLUMN));

        return view;
    }

}
