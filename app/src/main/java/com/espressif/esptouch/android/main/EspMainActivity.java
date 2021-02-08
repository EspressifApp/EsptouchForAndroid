package com.espressif.esptouch.android.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.espressif.esptouch.android.R;
import com.espressif.esptouch.android.v1.EspTouchActivity;
import com.espressif.esptouch.android.v2.EspTouch2Activity;

public class EspMainActivity extends AppCompatActivity {
    private static final String[] ITEMS = {
            "EspTouch",
            "EspTouch V2"
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.main_title);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(new ItemAdapter());
    }

    private class ItemHolder extends RecyclerView.ViewHolder {
        int position;
        TextView label;

        ItemHolder(@NonNull View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.label);

            itemView.setOnClickListener(v -> {
                switch (position) {
                    case 0:
                        startActivity(new Intent(EspMainActivity.this, EspTouchActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(EspMainActivity.this, EspTouch2Activity.class));
                        break;
                }
            });
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.activity_main_item, parent, false);
            return new ItemHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            String item = ITEMS[position];
            holder.position = position;
            holder.label.setText(item);
        }

        @Override
        public int getItemCount() {
            return ITEMS.length;
        }
    }
}
