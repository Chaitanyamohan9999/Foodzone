package com.food.foodzone.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.food.foodzone.R;
import com.food.foodzone.common.AppConstants;
import com.food.foodzone.models.TableDo;
import com.food.foodzone.utils.CustomDateTimePicker;
import com.food.foodzone.utils.RangeTimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ScheduleTimeActivity extends BaseActivity {

    private View llSchedule;
    private TextView tvStartTime, tvEndTime, tvTerms;
    private Button btnProceed;
    private Spinner spMembers, spEndTime;
    private TableDo tableDo;
    private String from = "";
    private int noOfPeople;
    private int noOfHour;

    @Override
    public void initialise() {
        llSchedule              = inflater.inflate(R.layout.schedule_time_layout, null);
        addBodyView(llSchedule);
        lockMenu();
        ivBack.setVisibility(View.VISIBLE);
        llToolbar.setVisibility(View.VISIBLE);
        ivMenu.setVisibility(View.GONE);
        flCart.setVisibility(View.GONE);
        tvTitle.setText("Schedule Reservation");
        initialiseControls();
        if (getIntent().hasExtra("From")){
            from = getIntent().getStringExtra("From");
        }
        if (getIntent().hasExtra("TableDo")){
            tableDo = (TableDo) getIntent().getSerializableExtra("TableDo");
        }

        tvStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate();
            }
        });
        tvEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvStartTime.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please select start Date&Time first", Toast.LENGTH_SHORT).show();
                }
                else {
                    endDateTime();
                }
            }
        });

        final ArrayList<Integer> noOfPeopleList = new ArrayList<>();
        for (int i=1;i<tableDo.tableCapacity;i++){
            noOfPeopleList.add(i);
        }
        spMembers.setAdapter(new ArrayAdapter<Integer>(ScheduleTimeActivity.this, R.layout.spinner_dropdown, noOfPeopleList){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = LayoutInflater.from(ScheduleTimeActivity.this).inflate(R.layout.spinner_dropdown, parent, false);
                final TextView tvDropdown = row.findViewById(R.id.tvDropdown);
                tvDropdown.setText(""+noOfPeopleList.get(position));
                return row;
            }
        });
        spMembers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                noOfPeople = noOfPeopleList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        final ArrayList<String> noOfHours = new ArrayList<>();
        noOfHours.add("Select No.of Hours");
        noOfHours.add("1 Hour");
        noOfHours.add("2 Hours");
        noOfHours.add("3 Hours");
        spEndTime.setAdapter(new ArrayAdapter<String>(ScheduleTimeActivity.this, R.layout.spinner_dropdown, noOfHours){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View row = LayoutInflater.from(ScheduleTimeActivity.this).inflate(R.layout.spinner_dropdown, parent, false);
                final TextView tvDropdown = row.findViewById(R.id.tvDropdown);
                tvDropdown.setText(""+noOfHours.get(position));
                return row;
            }
        });
        spEndTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position >0){
                    noOfHour = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvStartTime.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please select start date/time", Toast.LENGTH_SHORT).show();
                }
//                else if(tvEndTime.getText().toString().equalsIgnoreCase("")) {
                else if(noOfHour < 1) {
                    Toast.makeText(context, "Please select end time", Toast.LENGTH_SHORT).show();
                }
                else if(noOfPeople < 1) {
                    Toast.makeText(context, "Please number of people", Toast.LENGTH_SHORT).show();
                }
                else {
                    tableDo.reservedAt = calendar.getTimeInMillis();
                    tableDo.reservedFor = noOfHour;
                    Intent intent = new Intent(ScheduleTimeActivity.this, MenuListActivity.class);
                    intent.putExtra(AppConstants.From, from);
                    intent.putExtra("TableDo", tableDo);
                    startActivityForResult(intent, 1001);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
        });
    }

    private void initialiseControls() {
        tvStartTime                         = llSchedule.findViewById(R.id.tvStartTime);
        tvEndTime                           = llSchedule.findViewById(R.id.tvEndTime);
        tvTerms                             = llSchedule.findViewById(R.id.tvTerms);
        spMembers                           = llSchedule.findViewById(R.id.spMembers);
        spEndTime                           = llSchedule.findViewById(R.id.spEndTime);
        btnProceed                          = llSchedule.findViewById(R.id.btnProceed);
    }

    private void startDate() {
        CustomDateTimePicker custom = new CustomDateTimePicker(ScheduleTimeActivity.this, System.currentTimeMillis(),
                new CustomDateTimePicker.ICustomDateTimeListener() {
                    @Override
                    public void onSet(Dialog dialog, Calendar calSelected, Date dateSelected, int year, String monthFullName,
                                      String monthShortName, int monthNumber, int day, String weekDayFullName, String weekDayShortName,
                                      int hour24, int hour12, int min, int sec, String AM_PM) {
                        tvStartTime.setText("");
                        calendar = calSelected;
                        tvStartTime.setText(calSelected.get(Calendar.DAY_OF_MONTH)
                                + "-" + AppConstants.TwoDigitsNumber.format(monthNumber + 1) + "-" + year
                                + " " + AppConstants.TwoDigitsNumber.format(hour24) + ":" + AppConstants.TwoDigitsNumber.format(min));
                    }

                    @Override
                    public void onCancel() {

                    }
                });
        custom.set24HourFormat(true);
        custom.setDate(Calendar.getInstance());
        custom.showDialog();
    }
    private Calendar calendar;
    private void endDateTime() {
        try {
            TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hour, int minute) {
                    String endTime = ""+AppConstants.TwoDigitsNumber.format(hour)+" : "+AppConstants.TwoDigitsNumber.format(minute);
                    tvEndTime.setText(endTime);
                }
            };
            calendar.setTimeInMillis(calendar.getTimeInMillis()+3600000);// adding 1 hour
            RangeTimePickerDialog rangeTimePickerDialog = new RangeTimePickerDialog(ScheduleTimeActivity.this, timePickerListener,
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)+30, true);
            rangeTimePickerDialog.setMin(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)+30);
            rangeTimePickerDialog.setMax(calendar.get(Calendar.HOUR_OF_DAY)+AppConstants.Pickup_Max_Time, 0);
            rangeTimePickerDialog.show();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void getData() {

    }
}
