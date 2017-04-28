package com.nelson.annotations_sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.annotation.Bind;
import com.nelson.viewinject_api.ViewInjector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Nelson on 17/4/27.
 */

public class CategoryActivity extends AppCompatActivity {

    private List<String> mData = new ArrayList<>(Arrays.asList("text1", "text2"));

    @Bind(R.id.id_listview)
    ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ViewInjector.injectView(this);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CategoryActivity.this, "position = " + mData.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        mListView.setAdapter(new CategoryAdapter(this, mData));
    }


    class CategoryAdapter extends ArrayAdapter<String> {

        private LayoutInflater mInflater;
        private Context mContext;

        public CategoryAdapter(Context context, List<String> objects) {
            super(context, -1, objects);
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (viewHolder == null) {
                convertView = mInflater.inflate(R.layout.item_category, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.idTv.setText(getItem(position));
            return convertView;
        }

        class ViewHolder {

            @Bind(R.id.id_iv)
            ImageView idIv;
            @Bind(R.id.id_tv)
            TextView idTv;

            ViewHolder(View view) {
                ViewInjector.injectView(this, view);
            }
        }
    }
}
