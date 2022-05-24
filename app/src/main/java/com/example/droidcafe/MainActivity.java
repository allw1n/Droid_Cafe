package com.example.droidcafe;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.View;

import com.example.droidcafe.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.android.droidcafe.extra.MESSAGE";
    private String mOrderMessage;

    public TextView donutDescriptionView;
    public String donutDescription;
    public EditText editDonutDescription;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mOrderMessage != null) {
            outState.putString("order_made", mOrderMessage);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.droidcafe.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        if (savedInstanceState != null) {
            mOrderMessage = savedInstanceState.getString("order_made");
        }

        binding.fab.setOnClickListener(view -> {
            giveOrder();
        });

        donutDescriptionView = findViewById(R.id.donutDescriptionView);
        registerForContextMenu(donutDescriptionView);

        donutDescription = donutDescriptionView.getText().toString();
        editDonutDescription = findViewById(R.id.editDonutDescription);

        ImageView donut, froyo, iceCream;

        donut = findViewById(R.id.donut);
        donut.setOnClickListener(view -> {
            mOrderMessage = getString(R.string.donut_order);
            displayToast(mOrderMessage);
        });

        froyo = findViewById(R.id.froyo);
        froyo.setOnClickListener(view -> {
            mOrderMessage = getString(R.string.froyo_order);
            displayToast(mOrderMessage);
        });

        iceCream = findViewById(R.id.iceCream);
        iceCream.setOnClickListener(view -> {
            mOrderMessage = getString(R.string.ice_cream_order);
            displayToast(mOrderMessage);
        });

        if (editDonutDescription.getText() != null) {
            setDescription();
        }

        PreferenceManager.setDefaultValues(this, R.xml.messages_preferences, false);

        PreferenceManager.setDefaultValues(this, R.xml.sync_preferences, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_order) {
            giveOrder();
            return true;
        }
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void giveOrder() {
        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
        if (mOrderMessage == null) {
            Toast.makeText(this, "Please order something first!", Toast.LENGTH_SHORT).show();
        }
        else {
            intent.putExtra(EXTRA_MESSAGE, mOrderMessage);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_copy) {
            copyText(donutDescription);
        }

        if (id == R.id.action_share) {
            String shareString = donutDescription;
            String mimeType = getString(R.string.text_plain);
            new ShareCompat.IntentBuilder(MainActivity.this)
                    .setType(mimeType)
                    .setText(shareString)
                    .startChooser();
        }

        if (id == R.id.action_delete) {
            donutDescriptionView.setVisibility(View.GONE);
            editDonutDescription.setVisibility(View.VISIBLE);
        }

        if (id == R.id.action_edit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Alert!");
            builder.setMessage("Delete and Edit?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    donutDescriptionView.setVisibility(View.GONE);
                    editDonutDescription.setVisibility(View.VISIBLE);
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

        return super.onContextItemSelected(item);
    }

    private void setDescription() {
        editDonutDescription.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                donutDescriptionView.setText(editDonutDescription.getText().toString());
                editDonutDescription.setVisibility(View.GONE);
                editDonutDescription.setText("");
                donutDescriptionView.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    private void copyText(String copyString) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Donut Description", copyString);
        clipboard.setPrimaryClip(clipData);
    }
}