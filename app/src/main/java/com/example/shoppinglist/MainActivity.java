package com.example.shoppinglist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import kotlinx.coroutines.flow.SharedFlow;

public class MainActivity extends AppCompatActivity {

    public static final String TEXTVIEW_DATA = "TEXTVIEW_DATA";
    SharedPreferences sharedPreferences;
    String sharedPrefFileName = "shrdprf";

    TextView itemsTextView;

    ActivityResultLauncher activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            activityResult -> {
                if (activityResult.getResultCode() != RESULT_OK) return;

                String item = activityResult.getData().getStringExtra(ItemsActivity.ITEM);
                Log.d("ITEMS_TEST", item);
                Log.d("ITEMS_TEST", "I have returned");
                if (itemsTextView.getText().toString().equals(getString(R.string.empty_list)))
                    itemsTextView.setText("");
                itemsTextView.append(item + "\n");
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences(sharedPrefFileName, MODE_PRIVATE);

        itemsTextView = findViewById(R.id.itemsTextView);
        if (savedInstanceState != null)
            itemsTextView.setText(savedInstanceState.getString(TEXTVIEW_DATA));
        else
            itemsTextView.setText(
                    sharedPreferences.getString(TEXTVIEW_DATA, getString(R.string.empty_list))
            );
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TEXTVIEW_DATA, itemsTextView.getText().toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferences.edit()
                .putString(TEXTVIEW_DATA, itemsTextView.getText().toString())
                .apply();
    }

    public void handleAddButtonPressed(View view) {
        Intent intent = new Intent(this, ItemsActivity.class);
        activityResultLauncher.launch(intent);
    }
}