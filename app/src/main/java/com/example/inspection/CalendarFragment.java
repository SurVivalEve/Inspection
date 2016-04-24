package com.example.inspection;

import android.content.Context;
import android.content.res.Resources;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.inspection.models.Appointment;
import com.example.inspection.models.Schedule;
import com.example.inspection.sync.SyncManager;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Stack;

public class CalendarFragment extends Fragment {

    private String empID="";
    private int viewIndex = 0;
    private String[] weeks = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private String[] months = new String[]{"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    private String[] timeMarks = new String[]{"00", "01", "02", "03", "04", "05", "06",
            "07", "08", "09", "10", "11", "12", "13", "14", "15", "16",
            "17", "18", "19", "20", "21", "22", "23", "00"};
    private LinearLayout monthlyLayout, weeklyLayout, weekdayContainerM, weekdayContainerW, timeMark;
    private TextView[] weekday = new TextView[7];
    private LinearLayout[] dayContainer = new LinearLayout[6];
    private LinearLayout[] dayDetailContainer = new LinearLayout[7];
    private TextView[] dayOfMonth = new TextView[weekday.length*dayContainer.length];
    private TextView[] totalTaskNum = new TextView[weekday.length*dayContainer.length];
    private View[] transparentRegion = new View[weekday.length*dayContainer.length];
    private View[] greenRegion = new View[weekday.length*dayContainer.length];
    private View[] item_day = new View[weekday.length*dayContainer.length];
    private View[] emptyViews = new View[(timeMarks.length-1)*weeks.length];
    private TextView month, pre, next;
    private int currentMonth, currentYear, currentDay, currentWeekDay;
    private LayoutInflater layoutInflater;
    private Schedule schedule = new Schedule();
    private Stack<View> appointmentView = new Stack();
    private int[] dayInWeekdayContainerW = new int[weekday.length];
    private int[] monthInWeekdayContainerW = new int[weekday.length];
    private int heightOfDayContainerView = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        empID = getArguments().getString("empID");
        month = (TextView) view.findViewById(R.id.month);
        pre = (TextView) view.findViewById(R.id.pre);
        next = (TextView) view.findViewById(R.id.next);
        monthlyLayout = (LinearLayout) view.findViewById(R.id.monthlyLayout);
        weeklyLayout = (LinearLayout) view.findViewById(R.id.weeklyLayout);
        weekdayContainerM = (LinearLayout) view.findViewById(R.id.weekdayContainerM);
        weekdayContainerW = (LinearLayout) view.findViewById(R.id.weekdayContainerW);
        for(int i=0; i<6; i++) {
            final int VIEWID = getResources().getIdentifier("dayContainer" + (i + 1), "id", this.getActivity().getPackageName());
            dayContainer[i] = (LinearLayout) view.findViewById(VIEWID);
        }
        for(int i=0; i<7; i++) {
            final int VIEWID = getResources().getIdentifier("dayDetailContainer" + (i + 1), "id", this.getActivity().getPackageName());
            dayDetailContainer[i] = (LinearLayout) view.findViewById(VIEWID);
        }
        timeMark = (LinearLayout) view.findViewById(R.id.timeMark);
        pre.setOnClickListener(preClickListener);
        next.setOnClickListener(nextClickListener);
        layoutInflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //set month
        month.setText(months[Calendar.getInstance().get(Calendar.MONTH)]);
        //save the current detail
        currentMonth = Calendar.getInstance().get(Calendar.MONTH); // 0-11 = Jan to Dec
        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        currentWeekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1; //0-6 = Sun to Sat

        //declares varibales that's the weight of day item
        LinearLayout.LayoutParams weekdayParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);

        //set weekday on monthlyLayout
        for(int i=0; i<weekday.length; i++) {
            View item_weekday = layoutInflater.inflate(R.layout.item_weekday, null);
            weekdayContainerM.addView(item_weekday);
            weekday[i] = (TextView) item_weekday.findViewById(R.id.weekday);
            weekday[i].setText(weeks[i]);
            item_weekday.setLayoutParams(weekdayParam);
        }

        //set weekday on weeklyLayout
        for(int i=0; i<weekday.length; i++) {
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

        //get the calendar data from the server and create day view
        new GetSchedule().execute();

        //TODO: on item click listeners
        return view;
    }

    private void createView() {
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 0f);
        if (viewIndex == 1){
            monthlyLayout.setLayoutParams(param);
            param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            weeklyLayout.setLayoutParams(param);
        } else if (viewIndex == 0) {
            weeklyLayout.setLayoutParams(param);
            param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
            monthlyLayout.setLayoutParams(param);
        }

        //set montly layout
        //set param of day item
        LinearLayout.LayoutParams dayParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        //add day item into layout
        for (int i = 1; i <= weekday.length * dayContainer.length; i++) {
            //day item
            item_day[i - 1] = layoutInflater.inflate(R.layout.item_day_of_calendar, null);
            dayOfMonth[i - 1] = (TextView) item_day[i - 1].findViewById(R.id.dayOfMonth);
            transparentRegion[i - 1] = item_day[i - 1].findViewById(R.id.transparentRegion);
            greenRegion[i - 1] = item_day[i - 1].findViewById(R.id.greenRegion);
            item_day[i - 1].setLayoutParams(dayParam);     //set weight of day item
            dayContainer[(i - 1) / weekday.length].addView(item_day[i - 1]);       //add day item
            //set day of month
            Calendar c = Calendar.getInstance();
            if (c.get(Calendar.MONTH) != currentMonth)
                c.add(Calendar.MONTH, currentMonth - c.get(Calendar.MONTH));
            if (c.get(Calendar.YEAR) != currentYear)
                c.add(Calendar.YEAR, currentYear - c.get(Calendar.YEAR));
            c.add(Calendar.DATE, 1 - c.get(Calendar.DAY_OF_MONTH));
            c.add(Calendar.DATE, -c.get(Calendar.DAY_OF_WEEK) % 7 + i);
            dayOfMonth[i - 1].setText(c.get(Calendar.DAY_OF_MONTH) + "");

            //set the percentage of graph in  dayContent
            int assign = 0, notAssign = 0, total = 0;
            if (c.get(Calendar.MONTH) == currentMonth) {
                if (schedule.getAssign()[c.get(Calendar.DAY_OF_MONTH) - 1] > 0) {
                    assign = schedule.getAssign()[c.get(Calendar.DAY_OF_MONTH) - 1];
                    total += assign;
                }
                if (schedule.getNotAssign()[c.get(Calendar.DAY_OF_MONTH) - 1] > 0) {
                    notAssign = schedule.getNotAssign()[c.get(Calendar.DAY_OF_MONTH) - 1];
                    total += notAssign;
                }
                if (assign == 0 && notAssign == 0)
                    assign = 1;
            } else {
                notAssign = 1;
            }
            LinearLayout.LayoutParams paramForTransparent = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, assign);
            LinearLayout.LayoutParams paramForGreen = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, notAssign);
            transparentRegion[i - 1].setLayoutParams(paramForTransparent);
            greenRegion[i - 1].setLayoutParams(paramForGreen);

            //set total num of task in day
            totalTaskNum[i - 1] = (TextView) item_day[i - 1].findViewById(R.id.totalTaskNum);
            if (c.get(Calendar.MONTH) == currentMonth)
                totalTaskNum[i - 1].setText(total + "");
            else
                totalTaskNum[i - 1].setText("");

            //vertical line separator
            View lineSeparator = layoutInflater.inflate(R.layout.item_vertical_line, null);
            if ((i - 1) % 7 != 6)
                dayContainer[(i - 1) / 7].addView(lineSeparator);        //add vertical line sperator

            //set day color
            if (c.get(Calendar.MONTH) == currentMonth)
                dayOfMonth[i - 1].setTextColor(Color.RED);

            //set shadow
            dayOfMonth[i - 1].setShadowLayer(1, 0, 0, Color.BLACK);

            //set bold and underline to today
            if (currentMonth == Calendar.getInstance().get(Calendar.MONTH)) {
                if (c.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    dayOfMonth[i - 1].setTypeface(null, Typeface.BOLD);
                    String today = new String(c.get(Calendar.DAY_OF_MONTH) + "");
                    SpannableString s_today = new SpannableString(today);
                    s_today.setSpan(new UnderlineSpan(), 0, today.length(), 0);
                    dayOfMonth[i - 1].setText(s_today);
                }
            }

            // set onClickListener for the item day
            final int INDEX = i - 1;
            final int CLICKEDDAY = c.get(Calendar.DAY_OF_MONTH);        //1-31
            final int CLICKEDMONTH = c.get(Calendar.MONTH);     //0-11
            item_day[i - 1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                // change the month while clicked the day item with other month
                    if (CLICKEDMONTH == 0 && currentMonth == 11 && next.getVisibility() == View.VISIBLE) {
                        next();
                    } else if (CLICKEDMONTH == 11 && currentMonth == 0 && pre.getVisibility() == View.VISIBLE) {
                        pre();
                    } else if (CLICKEDMONTH > currentMonth && next.getVisibility() == View.VISIBLE) {
                        next();
                    } else if (CLICKEDMONTH < currentMonth && pre.getVisibility() == View.VISIBLE) {
                        pre();
                    } else {
                        Schedule daySchedule = new Schedule();
                        int assign = schedule.getAssign()[CLICKEDDAY - 1];
                        int notAssign = schedule.getNotAssign()[CLICKEDDAY - 1];
                        //filter the schedule
                        for (int i = 0; i < schedule.getAppointments().size(); i++) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(schedule.getAppointments().get(i).getDate());
                            if ((c.get(Calendar.MONTH) == CLICKEDMONTH) && (c.get(Calendar.DAY_OF_MONTH) == CLICKEDDAY)) {
                                daySchedule.getAppointments().add(schedule.getAppointments().get(i));
                            }
                        }
                        //                        Log.d("assigned", assign+"");
                        //                        Log.d("not-assign", notAssign+"");

                        JobFragment jobFragment = new JobFragment();
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("joblist", daySchedule);
                        jobFragment.setArguments(bundle);
                        ft.replace(R.id.daily_fragment, jobFragment).commit();
                    }
                }
            });
        }
        // set weekly layout
        ScrollView weekDetailScroll = (ScrollView) this.getView().findViewById(R.id.weekDetailScroll);
        int displayHeight = weekDetailScroll.getChildAt(0).getHeight();
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
            timeMarkText.setText(timeMarks[i]+"  ");
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
                        assignedBy.setText(schedule.getAppointments().get(i).getEmpName().split(" ")[0]);
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
    }

    private View.OnClickListener preClickListener = new View.OnClickListener(){
        public void onClick(View view){
            pre();
        }
    };

    private View.OnClickListener nextClickListener = new View.OnClickListener(){
        public void onClick(View view){
            next();
        }
    };

    private void pre(){
        if(currentMonth == Calendar.getInstance().get(Calendar.MONTH))
            pre.setVisibility(View.INVISIBLE);
        else
            pre.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);

        if(currentMonth!=0) {
            currentMonth--;
        } else {
            currentMonth = 11;
            currentYear--;
        }

        month.setText(months[currentMonth]);
        // clear the view in dayContainer
        for(int i=0; i<dayContainer.length; i++) {
            dayContainer[i].removeAllViews();
        }
        //get the calendar data from the server and create day view
        new GetSchedule().execute();
    }

    private void next() {
        if (currentMonth == Calendar.getInstance().get(Calendar.MONTH))
            next.setVisibility(View.INVISIBLE);
        else
            next.setVisibility(View.VISIBLE);
        pre.setVisibility(View.VISIBLE);

        if (currentMonth != 11) {
            currentMonth++;
        } else {
            currentMonth=0;
            currentYear++;
        }

        month.setText(months[currentMonth]);
        // clear the view in dayContainer
        for(int i=0; i<dayContainer.length; i++) {
            dayContainer[i].removeAllViews();
        }
        //get the calendar data from the server and create day view
        new GetSchedule().execute();
    }

    public void setAppointmentView(){

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

    private class GetSchedule extends AsyncTask<Void, Void, Schedule> {

        @Override
        protected Schedule doInBackground(Void... params) {
            //network things
            String month = "";
            if(currentMonth+1<10)
                month="0"+(currentMonth+1);
            else
                month = currentMonth+1+"";
            SyncManager syncManager = new SyncManager("getSchedule.php?empID=" + empID + "&YM="+currentYear+"-"+month);
            return syncManager.syncCalendar();
        }

        @Override
        protected void onPostExecute(Schedule schedule) {
            super.onPostExecute(schedule);
            setSchedule(schedule);
            createView();
        }
    }
}
