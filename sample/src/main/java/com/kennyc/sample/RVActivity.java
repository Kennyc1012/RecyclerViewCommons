package com.kennyc.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kennyc.adapters.ArrayRecyclerAdapter;
import com.kennyc.adapters.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class RVActivity extends AppCompatActivity implements View.OnClickListener {

    public static Intent createIntent(Context context, boolean isSimple) {
        return new Intent(context, RVActivity.class).putExtra("isSimple", isSimple);
    }

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rv);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        List<String> data = new ArrayList<>(100);

        for (int i = 0; i < 100; i++) {
            data.add("Position " + i);
        }

        if (getIntent().getBooleanExtra("isSimple", false)) {
            ArrayRecyclerAdapter<String> adapter = new ArrayRecyclerAdapter<>(this, android.R.layout.simple_list_item_1, data, this);
            recyclerView.setAdapter(adapter);
        } else {
            CustomAdapter adapter = new CustomAdapter(this, data, this);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        if (recyclerView.getAdapter() instanceof ArrayRecyclerAdapter) {
            Object item = ((ArrayRecyclerAdapter) recyclerView.getAdapter()).getItem(recyclerView.getChildAdapterPosition(v));
            Toast.makeText(getApplicationContext(), item.toString(), Toast.LENGTH_SHORT).show();
        } else {
            String item = ((CustomAdapter) recyclerView.getAdapter()).getItem(recyclerView.getChildAdapterPosition(v));
            Toast.makeText(getApplicationContext(), item, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        if (recyclerView.getAdapter() instanceof CustomAdapter) {
            ((CustomAdapter) recyclerView.getAdapter()).onDestroy();
        }

        super.onDestroy();
    }

    private static class CustomAdapter extends BaseRecyclerAdapter<String, CustomAdapter.VH> {

        private View.OnClickListener listener;

        public CustomAdapter(Context context, List<String> data, View.OnClickListener listener) {
            super(context, data);
            this.listener = listener;
        }

        @Override
        public CustomAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
            VH vh = new VH(inflateView(android.R.layout.simple_list_item_1, parent));
            vh.textView.setOnClickListener(listener);
            return vh;
        }

        @Override
        public void onDestroy() {
            listener = null;
        }

        @Override
        public void onBindViewHolder(CustomAdapter.VH holder, int position) {
            holder.textView.setText(getItem(position));
        }

        public static class VH extends RecyclerView.ViewHolder {
            TextView textView;

            public VH(View view) {
                super(view);
                textView = (TextView) view;
            }
        }
    }
}
