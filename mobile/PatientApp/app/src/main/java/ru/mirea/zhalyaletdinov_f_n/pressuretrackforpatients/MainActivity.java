package ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ru.mirea.zhalyaletdinov_f_n.pressuretrackforpatients.databinding.ActivityMainBinding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.mikephil.charting.charts.BarChart;

public class MainActivity extends AppCompatActivity {

    private Button logoutButton;
    private BarChart chart;
    private ActivityMainBinding binding;
    private Button addRecordButton;
    private RecyclerView rv1, rv2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rv1 = binding.rvToday;
        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv1.setAdapter(new TodayCardsAdapter());

        rv2 = binding.rvWeek;
        rv2.setLayoutManager(new LinearLayoutManager(this));
        rv2.setAdapter(new WeekCardsAdapter());

        logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addRecordButton = binding.addRecordButton;
        addRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddRecordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void newModOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, NewTonometrActivity.class);
        startActivity(intent);
    }

    class RecordCards extends RecyclerView.ViewHolder {
        public RecordCards(@NonNull View itemView) {
            super(itemView);
        }
    }

    class TodayCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_row, parent, false);
            return new RecordCards(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    class WeekCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_row, parent, false);
            return new RecordCards(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 7;
        }
    }

}