package com.example.inspection;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.inspection.dao.LocalScheduleDAO;
import com.example.inspection.models.Schedule;
import com.example.inspection.sync.SyncManager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Stack;

public class CalendarWeeklyFragment extends Fragment {

    private String[] weeks = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private String[] timeMarks = new String[]{"00", "01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
            "17", "18", "19", "20", "21", "22", "23", "00"};
    private LinearLayout weekdayContainerW, timeMark;
    private ScrollView weekDetailScroll;
    private TextView[] weekday = new TextView[7];
    private LinearLayout[] dayDetailContainer = new LinearLayout[7];
    private View[] emptyViews = new View[(timeMarks.length-1)*weeks.length];
    private int currentMonth, currentYear, currentDay, currentWeekDay;
    private LayoutInflater layoutInflater;
    private Schedule schedule = new Schedule();
    private Stack<View> appointmentView = new Stack();
    private int[] dayInWeekdayContainerW = new int[weekday.length];
    private int[] monthInWeekdayContainerW = new int[weekday.length];
    private int heightOfDayContainerView = 0;
    private TextView[] times = new TextView[25];

    private static String empID = "";

    public static CalendarWeeklyFragment newInstance(String id) {
        CalendarWeeklyFragment fragment = new CalendarWeeklyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("empID", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_calendar_weekly, container, false);

        init(view);
        createView();

        //TODO: on item click listeners
        return view;
    }

    private void init(View view){
        empID = getArguments().getString("empID");
        refreshSchedule();

        weekDetailScroll = (ScrollView) view.findViewById(R.id.weekDetailScroll);
        weekdayContainerW = (LinearLayout) view.findViewById(R.id.weekdayContainerW);
        timeMark = (LinearLayout) view.findViewById(R.id.timeMark);
        for(int i=0; i<7; i++) {
            final int VIEWID = getResources().getIdentifier("dayDetailContainer" + (i + 1), "id", this.getActivity().getPackageName());
            dayDetailContainer[i] = (LinearLayout) view.findViewById(VIEWID);
        }

        layoutInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //save the current detail
        currentMonth = Calendar.getInstance().get(Calendar.MONTH); // 0-11 = Jan to Dec
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        currentWeekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1; //0-6 = Sun to Sat

        //declares varibales that's the weight of day item
        LinearLayout.LayoutParams weekdayParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);

        //set weekday on weeklyLayout
        for (int i=0; i<weekday.length; i++) {
            View item_weekday = layoutInflater.inflate(R.layout.item_weekday, null);
            weekdayContainerW.addView(item_weekday);
            weekday[i] = (TextView) item_weekday.findViewById(R.id.weekday);
            int day = (currentDay - currentWeekDay + i);
            monthInWeekdayContainerW[i] = currentMonth+1;
            if(day <= 0) {
                Calendar cal = new GregorianCalendar(currentYear, (currentMonth-1)%12, currentDay);
                day+=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                monthInWeekdayContainerW[i] = (currentMonth)%12;
            }
            if(day > (new GregorianCalendar(currentYear, currentMonth, currentDay)
                    .getActualMaximum(Calendar.DAY_OF_MONTH))) {
                Calendar cal = new GregorianCalendar(currentYear, currentMonth, currentDay);
                day-=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                monthInWeekdayContainerW[i] = (currentMonth+2)%12;
            }
            dayInWeekdayContainerW[i] = day;
            String dayString = day +"";
            if(day < 10)
                dayString = "0"+day;
            if(currentWeekDay == i) {
                //set bold and underline to today
                weekday[i].setTypeface(null, Typeface.BOLD);
                String today = new String(weeks[i] + "\n " + dayString);
                SpannableString s_today = new SpannableString(today);
                s_today.setSpan(new UnderlineSpan(), 0, today.length(), 0);
                weekday[i].setText(s_today);
            } else {
                weekday[i].setText(weeks[i] + "\n " + dayString);
            }
            item_weekday.setLayoutParams(weekdayParam);
        }
    }

    public void refreshSchedule(){
        LocalScheduleDAO schDAO = new LocalScheduleDAO(getActivity().getApplicationContext());
        Schedule sch = new Schedule();
        try {
            sch = schDAO.toSchedule(schDAO.getAll());
        } catch (Exception e){
            Log.d("cur scheduledao error", "toschedule error");
            Log.d("Details", e.toString());
            setSchedule(new Schedule());
        }
        setSchedule(sch);
    }

    public void createView() {
        // set weekly layout
        WindowManager wm = (WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int displayHeight = (display.getHeight()-2)*17/19;
        Log.d("displayHeight", displayHeight+"");
        heightOfDayContainerView = displayHeight / 10;
        int count = 0;
        for(int i=0; i<(timeMarks.length-1)*weekday.length; i++){
            if(getAppointmentNum(monthInWeekdayContainerW[i/24], dayInWeekdayContainerW[i/24], i%24)>count)
                count = getAppointmentNum(monthInWeekdayContainerW[i/24], dayInWeekdayContainerW[i/24], i%24);
        }
        if(count*120>heightOfDayContainerView)
            heightOfDayContainerView = 120*count;
        for(int i=0; i<dayDetailContainer.length; i++) {
            //for typesetting
            View typesettingView = new View(this.getActivity());
            typesettingView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 30));
            dayDetailContainer[i].addView(typesettingView);
            //horizontal line separator
            View lineSeparator = layoutInflater.inflate(R.layout.item_horizontal_line, null);
            dayDetailContainer[i].addView(lineSeparator);        //add vertical line sperator
            for (int t = 0; t < timeMarks.length-1; t++) {
                View emptyView = layoutInflater.inflate(R.layout.item_empty_view, null);
                emptyViews[t+i*(timeMarks.length-1)] = emptyView;
                emptyViews[t+i*(timeMarks.length-1)].setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, heightOfDayContainerView));
                dayDetailContainer[i].addView(emptyViews[t + i * (timeMarks.length - 1)]);
                //horizontal line separator
                lineSeparator = layoutInflater.inflate(R.layout.item_horizontal_line, null);
                dayDetailContainer[i].addView(lineSeparator);        //add vertical line sperator
            }
        }
        for (int i = 0; i < timeMarks.length; i++) {
            TextView timeMarkText = new TextView(this.getActivity());
            if(i!=timeMarks.length-1) {
                timeMarkText.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, heightOfDayContainerView+4));
            } else {
                timeMarkText.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, heightOfDayContainerView/3));
            }
            timeMarkText.setGravity(Gravity.RIGHT);
            timeMarkText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            timeMarkText.setText(timeMarks[i] + "  ");
            times[i] = timeMarkText;
            timeMark.addView(timeMarkText);
        }
        for(int i=0; i< schedule.getAppointments().size(); i++) {
            int month = schedule.getAppointments().get(i).getDate().getMonth();
            int day = schedule.getAppointments().get(i).getDate().getDate();
            int hour = schedule.getAppointments().get(i).getDate().getHours();
            int min = schedule.getAppointments().get(i).getDate().getMinutes();
            for(int k=0; k<weekday.length; k++){
                if(day == dayInWeekdayContainerW[k] && month == (monthInWeekdayContainerW[k]-1)) {
                    // add item_appointment_view
                    View item_appointment_view = layoutInflater.inflate(R.layout.item_appointment_view, null);
                    item_appointment_view.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 120));
                    TextView time = (TextView) item_appointment_view.findViewById(R.id.time);
                    TextView assignedBy = (TextView) item_appointment_view.findViewById(R.id.assignedBy);
                    time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                    assignedBy.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                    String hourString = hour+"";
                    String minString = min+"";
                    if(hour<10)
                        hourString = "0"+hourString;
                    if(min<10)
                        minString = "0"+minString;
                    time.setText(hourString + ":" + minString);
                    if(schedule.getAppointments().get(i).getEmpID().equalsIgnoreCase(""))
                        assignedBy.setText("non-assigned");
                    else
                        assignedBy.setText(schedule.getAppointments().get(i).getEmpName());
                    final int index = i;
                    item_appointment_view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //change to job detail
                            JobDetailFragment jobDetailFragment = new JobDetailFragment();
                            FragmentTransaction ft = ((MainMenu) getContext()).getSupportFragmentManager().beginTransaction();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("appointment", schedule.getAppointments().get(index));
                            jobDetailFragment.setArguments(bundle);
                            ft.replace(R.id.main_fragment, jobDetailFragment, "job_details_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
                    appointmentView.add(item_appointment_view);
                    if (dayDetailContainer[k].getChildCount()%50%2 == 1)
                        dayDetailContainer[k].addView(item_appointment_view, 2 + hour * 2+1);
                    else
                        dayDetailContainer[k].addView(item_appointment_view, 2 + hour * 2);
                    emptyViews[k * 24 + hour].getLayoutParams().height = heightOfDayContainerView-(getAppointmentNum(month+1, day, hour) * 120);
                }
            }
        }
        for(int i=0; i<24; i++) {
            boolean result = true;
            int[] h = new int[7];
            for(int k=0; k<7; k++) {
                h[k] = emptyViews[i + 24 * k].getLayoutParams().height;
            }
            for(int k=1; k<7; k++){
                if (h[k] != h[0])
                    result = false;
            }
            if(result == true) {
                for (int k = 0; k < 7; k++) {
                    emptyViews[i + 24 * k].getLayoutParams().height = displayHeight / 10;
                    times[i].getLayoutParams().height = displayHeight / 10 +4;
                }
            }
        }
        times[24].getLayoutParams().height = displayHeight / 10 /3 ;
    }

    private int getAppointmentNum(int month, int day, int period){
        // get the appointment number of that period in one hours and in current year
        // period can be input from 0 to 23 (0 = 00:00 to 00:59)
        // month can be input from 1-12 (1 = Jan, 12 = Dec)
        int count = 0;
        for(int i=0; i<schedule.getAppointments().size(); i++){
            if (schedule.getAppointments().get(i).getDate().getDate() == day && schedule.getAppointments().get(i).getDate().getMonth() == (month-1)){
                if(schedule.getAppointments().get(i).getDate().getHours() == period)
                    count++;
            }
        }
        return count;
    }

    public void setSchedule(Schedule schedule){
        this.schedule = schedule;
    }

    public Schedule getSchedule(){
        return this.schedule;
    }

}
