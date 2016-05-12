package com.example.inspection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.inspection.dao.LocalNextMonthScheduleDAO;
import com.example.inspection.dao.LocalPreMonthScheduleDAO;
import com.example.inspection.dao.LocalScheduleDAO;
import com.example.inspection.models.Schedule;
import com.example.inspection.sync.SyncManager;

import java.util.Calendar;

public class CalendarFragment extends Fragment {

    private FrameLayout calendarLayout;
    private int currentMonth, currentYear, currentDay, currentWeekDay;
    private CalendarMonthlyFragment calendarMonthlyFragment;
    private CalendarWeeklyFragment calendarWeeklyFragment;
    private FragmentTransaction ft;
    private int viewIndex = 0; //0 for monthly, 1for weekly
    private static String empID = "";
    private MenuItem modeModify;

    public static CalendarFragment newInstance(String id) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putString("empID", id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_calendar, container, false);

        init(view);
        //get the calendar data from the server and create day view
        new GetSchedule().execute();

        //TODO: on item click listeners
        return view;
    }

    public void init(View view){
        empID = getArguments().getString("empID");

        calendarLayout = (FrameLayout) view.findViewById(R.id.calendar_fragment);

        calendarMonthlyFragment = CalendarMonthlyFragment.newInstance(empID);
        calendarWeeklyFragment = CalendarWeeklyFragment.newInstance(empID);
        ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.calendar_fragment, calendarMonthlyFragment).commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.clear();
        inflater.inflate(R.menu.option_menu_calendar, menu);
        MenuItem modeModify = menu.findItem(R.id.modeModify);
        modeModify.setIcon(getResources().getDrawable(R.mipmap.ic_weekly_calendar));
        super.onCreateOptionsMenu(menu, inflater);;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()){
            case R.id.modeModify:
                if(viewIndex == 0) {
                    viewIndex = 1;
                    ft.replace(R.id.calendar_fragment, calendarWeeklyFragment).addToBackStack(null).commit();
                    item.setIcon(getResources().getDrawable(R.mipmap.ic_calendar));
                } else if (viewIndex == 1){
                    viewIndex = 0;
                    ft.replace(R.id.calendar_fragment, calendarMonthlyFragment).addToBackStack(null).commit();
                    item.setIcon(getResources().getDrawable(R.mipmap.ic_weekly_calendar));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            LocalPreMonthScheduleDAO schPreDAO = syncManager.syncPreMonthCalendar();
            result[0] = schPreDAO.toSchedule(schPreDAO.getAll());

            currentMonth = Calendar.getInstance().get(Calendar.MONTH); // 0-11 = Jan to Dec
            currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (currentMonth != 11) {
                currentMonth++;
            } else {
                currentMonth = 0;
                currentYear++;
            }
            if (currentMonth + 1 < 10)
                month = "0" + (currentMonth + 1);
            else
                month = currentMonth + 1 + "";
            syncManager = new SyncManager("getSchedule.php?empID=" + empID + "&YM="+currentYear+"-"+month, getActivity().getApplicationContext());
            LocalNextMonthScheduleDAO schNextDAO = syncManager.syncNextMonthCalendar();
            result[2] = schNextDAO.toSchedule(schNextDAO.getAll());
            return result;
        }

        @Override
        protected void onPostExecute(Schedule[] result) {
            super.onPostExecute(result);
            calendarMonthlyFragment.setSchedule(result);
            calendarWeeklyFragment.setSchedule(result[1]);
        }
    }
}
