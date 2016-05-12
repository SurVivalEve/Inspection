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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.inspection.dao.LocalNextMonthScheduleDAO;
import com.example.inspection.dao.LocalPreMonthScheduleDAO;
import com.example.inspection.dao.LocalScheduleDAO;
import com.example.inspection.models.Schedule;
import com.example.inspection.sync.SyncManager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Stack;

public class CalendarMonthlyFragment extends Fragment {

    private String[] weeks = new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private String[] months = new String[]{"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    private LinearLayout weekdayContainerM;
    private TextView[] weekday = new TextView[7];
    private LinearLayout[] dayContainer = new LinearLayout[6];
    private TextView[] dayOfMonth = new TextView[weekday.length*dayContainer.length];
    private TextView[] totalTaskNum = new TextView[weekday.length*dayContainer.length];
    private View[] transparentRegion = new View[weekday.length*dayContainer.length];
    private View[] greenRegion = new View[weekday.length*dayContainer.length];
    private View[] item_day = new View[weekday.length*dayContainer.length];
    private TextView month, pre, next;
    private int currentMonth, currentYear, currentDay, currentWeekDay;
    private LayoutInflater layoutInflater;
    private Schedule[] schedule = new Schedule[3];
    private int monthIndex = 1;

    private static String empID = "";

    public static CalendarMonthlyFragment newInstance(String id) {
        CalendarMonthlyFragment fragment = new CalendarMonthlyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("empID", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_calendar_monthly, container, false);

        init(view);
        createView();

        //TODO: on item click listeners
        return view;
    }

    private void init(View view){
        empID = getArguments().getString("empID");
        LocalScheduleDAO schDAO = new LocalScheduleDAO(getActivity().getApplicationContext());
        LocalPreMonthScheduleDAO schPreDAO = new LocalPreMonthScheduleDAO(getActivity().getApplicationContext());
        LocalNextMonthScheduleDAO schNextDAO = new LocalNextMonthScheduleDAO(getActivity().getApplicationContext());
        Schedule[] sch = new Schedule[3];
        try {
            sch[0] = schPreDAO.toSchedule(schPreDAO.getAll());
        } catch (Exception e){
            Log.d("pre scheduledao error", "toschedule error");
            Log.d("Details", e.toString());
            sch[0] = new Schedule();
        }
        try {
            sch[1] = schDAO.toSchedule(schDAO.getAll());
        } catch (Exception e){
            Log.d("cur scheduledao error", "toschedule error");
            Log.d("Details", e.toString());
            sch[1] = new Schedule();
        }
        try {
            sch[2] = schNextDAO.toSchedule(schNextDAO.getAll());
        } catch (Exception e){
            Log.d("next scheduledao error", "toschedule error");
            Log.d("Details", e.toString());
            sch[2] = new Schedule();
        }
        setSchedule(sch);

        month = (TextView) view.findViewById(R.id.month);
        pre = (TextView) view.findViewById(R.id.pre);
        next = (TextView) view.findViewById(R.id.next);
        weekdayContainerM = (LinearLayout) view.findViewById(R.id.weekdayContainerM);
        for(int i=0; i<6; i++) {
            final int VIEWID = getResources().getIdentifier("dayContainer" + (i + 1), "id", this.getActivity().getPackageName());
            dayContainer[i] = (LinearLayout) view.findViewById(VIEWID);
        }
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
    }

    public void createView() {
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
                if (schedule[monthIndex].getAssign()[c.get(Calendar.DAY_OF_MONTH) - 1] > 0) {
                    assign = schedule[monthIndex].getAssign()[c.get(Calendar.DAY_OF_MONTH) - 1];
                    total += assign;
                }
                if (schedule[monthIndex].getNotAssign()[c.get(Calendar.DAY_OF_MONTH) - 1] > 0) {
                    notAssign = schedule[monthIndex].getNotAssign()[c.get(Calendar.DAY_OF_MONTH) - 1];
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
                        int assign = schedule[monthIndex].getAssign()[CLICKEDDAY - 1];
                        int notAssign = schedule[monthIndex].getNotAssign()[CLICKEDDAY - 1];
                        //filter the schedule
                        for (int i = 0; i < schedule[monthIndex].getAppointments().size(); i++) {
                            Calendar c = Calendar.getInstance();
                            c.setTime(schedule[monthIndex].getAppointments().get(i).getDate());
                            if ((c.get(Calendar.MONTH) == CLICKEDMONTH) && (c.get(Calendar.DAY_OF_MONTH) == CLICKEDDAY)) {
                                daySchedule.getAppointments().add(schedule[monthIndex].getAppointments().get(i));
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

        //creat the new view for view previous month
        monthIndex--;
        createView();
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

        //creat the new view for view previous month
        monthIndex++;
        createView();
    }

    public void setSchedule(Schedule[] schedule){
        this.schedule = schedule;
    }

    public Schedule getSchedule(int month){
        //month: 0 for perivous month, 1 for current month, 2 for next month
        return this.schedule[month];
    }

    private class GetSchedule extends AsyncTask<Void, Void, Schedule[]> {

        @Override
        protected Schedule[] doInBackground(Void... params) {
            Schedule[] result = new Schedule[3];
            String month = "";
            currentMonth = Calendar.getInstance().get(Calendar.MONTH); // 0-11 = Jan to Dec
            currentYear = Calendar.getInstance().get(Calendar.YEAR);

            //get current schedule data
            if(currentMonth+1<10)
                month="0"+(currentMonth+1);
            else
                month = currentMonth+1+"";
            SyncManager syncManager = new SyncManager("getSchedule.php?empID=" + empID + "&YM="+currentYear+"-"+month, getActivity().getApplicationContext());
            LocalScheduleDAO schDAO = syncManager.syncCalendar();
            result[1] = schDAO.toSchedule(schDAO.getAll());

            //get previous schedule data
            if(currentMonth!=0) {
                currentMonth--;
            } else {
                currentMonth = 11;
                currentYear--;
            }
            if(currentMonth+1<10)
                month="0"+(currentMonth+1);
            else
                month = currentMonth+1+"";
            syncManager = new SyncManager("getSchedule.php?empID=" + empID + "&YM="+currentYear+"-"+month, getActivity().getApplicationContext());
            schDAO = syncManager.syncCalendar();
            result[0] = schDAO.toSchedule(schDAO.getAll());

            //get next schedule data
            if (currentMonth != 11) {
                currentMonth++;
            } else {
                currentMonth=0;
                currentYear++;
            }
            if(currentMonth+1<10)
                month="0"+(currentMonth+1);
            else
                month = currentMonth+1+"";
            syncManager = new SyncManager("getSchedule.php?empID=" + empID + "&YM="+currentYear+"-"+month, getActivity().getApplicationContext());
            schDAO = syncManager.syncCalendar();
            result[2] = schDAO.toSchedule(schDAO.getAll());
            return result;
        }

        @Override
        protected void onPostExecute(Schedule[] result) {
            super.onPostExecute(result);
            setSchedule(result);
            createView();
        }
    }
}
