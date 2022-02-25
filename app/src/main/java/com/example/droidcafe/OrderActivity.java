package com.example.droidcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.droidcafe.databinding.ActivityOrderBinding;

public class OrderActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView pickUpDateText;
    private TextView pickUpTimeText;
    private Button buttonPickupDate, buttonPickupTime;
    private EditText phoneEdit;
    private int checkedRadioId = 1;
    private RadioGroup deliveryOptions;
    private String fullDate, fullTime;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (deliveryOptions.getCheckedRadioButtonId() == R.id.pickUpRadio) {
            if (fullDate != null) {
                outState.putString("set_date", fullDate);
            }
            if (fullTime != null) {
                outState.putString("set_time", fullTime);
            }
            outState.putInt("checked_radio", checkedRadioId);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.droidcafe.databinding.ActivityOrderBinding binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonPickupDate = binding.buttonPickupDate;
        buttonPickupTime = binding.buttonPickupTime;
        pickUpDateText = binding.pickUpDateText;
        pickUpTimeText = binding.pickUpTimeText;
        deliveryOptions = binding.deliveryRadioGroup;
        phoneEdit = binding.phoneEdit;

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            checkedRadioId = savedInstanceState.getInt("checked_radio");
            if (checkedRadioId == 3) {
                String putDate = savedInstanceState.getString("set_date");
                String putTime = savedInstanceState.getString("set_time");

                if (putDate != null) {
                    pickUpDateText.setText(putDate);
                    fullDate = putDate;
                }

                if (putTime != null) {
                    pickUpTimeText.setText(putTime);
                    fullTime = putTime;
                }
            }
        }

        Intent order = getIntent();
        TextView orderMessageView = binding.orderMessage;
        orderMessageView.setText(order.getStringExtra(MainActivity.EXTRA_MESSAGE));

        //Phone Number Type
        Spinner phoneType = binding.phoneType;
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.phoneType,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneType.setAdapter(arrayAdapter);

        //Radio Buttons
        deliveryOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String displayMessageToast;
                if (i == R.id.pickUpRadio) {
                    checkedRadioId = 3;
                    displayMessageToast = getString(R.string.pick_up);
                    buttonPickupDate.setVisibility(View.VISIBLE);
                    buttonPickupTime.setVisibility(View.VISIBLE);
                    if (!pickUpDateText.getText().toString().equals("Date") || !pickUpTimeText.getText().toString().equals("Time")) {
                        pickUpDateText.setVisibility(View.VISIBLE);
                        pickUpTimeText.setVisibility(View.VISIBLE);
                    }
                } else if (i == R.id.nextDayRadio) {
                    checkedRadioId = 2;
                    displayMessageToast = getString(R.string.next_day_ground_delivery);
                    buttonPickupDate.setVisibility(View.GONE);
                    buttonPickupTime.setVisibility(View.GONE);
                    pickUpDateText.setVisibility(View.GONE);
                    pickUpTimeText.setVisibility(View.GONE);
                } else {
                    checkedRadioId = 1;
                    displayMessageToast = getString(R.string.same_day_messenger_service);
                    buttonPickupDate.setVisibility(View.GONE);
                    buttonPickupTime.setVisibility(View.GONE);
                    pickUpDateText.setVisibility(View.GONE);
                    pickUpTimeText.setVisibility(View.GONE);
                }
                displayToast(displayMessageToast);
            }
        });

        //Phone Number
        if (phoneEdit != null) {
            phoneEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    boolean handled = false;
                    if (actionId == EditorInfo.IME_ACTION_SEND) {
                        dialNumber(phoneEdit);
                        handled = true;
                    }
                    return handled;
                }
            });
        }

        buttonPickupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        buttonPickupTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });
    }

    private void dialNumber(EditText phoneEdit) {
        String phoneNumber = null;
        if (phoneEdit != null) {
            phoneNumber = "tel:" + phoneEdit.getText().toString();
        }

        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.setData(Uri.parse(phoneNumber));
        try {
            startActivity(dialIntent);
        } catch (Exception e) {
            Log.d("Implicit Intent", "Can't Handle this error - " + e);
        }
    }

    //Display Toast
    public void displayToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String phoneTypeSelected = adapterView.getItemAtPosition(i).toString();
        displayToast(phoneTypeSelected);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setDate(int year, int month, int day) {
        String strYear = Integer.toString(year);
        String strMonth = Integer.toString(month + 1);
        String strDay = Integer.toString(day);
        fullDate = strDay + "/" + strMonth + "/" + strYear;
        pickUpDateText.setVisibility(View.VISIBLE);
        pickUpDateText.setText(fullDate);
        if (pickUpTimeText.getVisibility() == View.GONE) pickUpTimeText.setVisibility(View.VISIBLE);
    }

    public void setTime(int hour, int minute) {
        String strMinute;
        String strHour;

        if (hour < 12) {
            strMinute = minute + " AM";
            strHour = Integer.toString(hour);
        } else {
            if (hour != 12) {
                hour -= 12;
            }
            strHour = Integer.toString(hour);
            strMinute = minute + " PM";
        }

        if (hour < 10) strHour = "0" + strHour;
        if (minute < 10) strMinute = "0" + strMinute;

        fullTime = strHour + ":" + strMinute;
        pickUpTimeText.setVisibility(View.VISIBLE);
        pickUpTimeText.setText(fullTime);
        if (pickUpDateText.getVisibility() == View.GONE) pickUpDateText.setVisibility(View.VISIBLE);
    }
}