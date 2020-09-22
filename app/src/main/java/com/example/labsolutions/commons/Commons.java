package com.example.labsolutions.commons;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.labsolutions.listviews.ActivitiesInfoDetails;
import com.example.labsolutions.listviews.ListActivitiesDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Commons {

    public Commons() {

    }

    public static void dismissProgressDialog(ProgressDialog progressDialog) {

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public static Map<String, String> setSparesData(TextView sprOneQty, TextView sprOneDesc, TextView sprTwoQty, TextView sprTwoDesc, TextView sprThreeQty, TextView sprThreeDesc, TextView sprFourQty, TextView sprFourDesc) {
        Map<String, String> sparesData = new HashMap();
        if (!sprOneQty.getText().toString().isEmpty() && !sprOneDesc.getText().toString().isEmpty()) {
            sparesData.put("sprOneQty", sprOneQty.getText().toString());
            sparesData.put("sprOneDesc", sprOneDesc.getText().toString());
        }
        if (!sprTwoQty.getText().toString().isEmpty() && !sprTwoDesc.getText().toString().isEmpty()) {
            sparesData.put("sprTwoDesc", sprTwoDesc.getText().toString());
            sparesData.put("sprTwoQty", sprTwoQty.getText().toString());
        }
        if (!sprThreeQty.getText().toString().isEmpty() && !sprThreeDesc.getText().toString().isEmpty()) {
            sparesData.put("sprThreeDesc", sprThreeDesc.getText().toString());
            sparesData.put("sprThreeQty", sprThreeQty.getText().toString());
        }
        if (!sprFourQty.getText().toString().isEmpty() && !sprFourDesc.getText().toString().isEmpty()) {
            sparesData.put("sprFourDesc", sprFourDesc.getText().toString());
            sparesData.put("sprFourQty", sprFourQty.getText().toString());
        }
        return sparesData;
    }

    public static void setMargin(RecyclerView listview) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) listview.getLayoutParams();
        params.setMargins(0, 180, 0, 10);
        listview.setLayoutParams(params);
    }



    public static void setRecycleItems(Context context, ArrayList<ActivitiesInfoDetails> activitiesList, SearchView searchView, RecyclerView listview, String type) {
        final ListActivitiesDetails listActivitiesDetails = new ListActivitiesDetails(context, activitiesList, type);
        listview.setHasFixedSize(true);
        listview.setLayoutManager(new LinearLayoutManager(context));
        listview.setAdapter(listActivitiesDetails);
        listview.setAdapter(listActivitiesDetails);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                listActivitiesDetails.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                listActivitiesDetails.getFilter().filter(newText);
                return false;
            }
        });
    }
    public static Map<String, String> getDuration(long startDate, long endDate) {
        Map<String, String> data = new HashMap<>();
        long milliseconds = endDate - startDate;
        int seconds = (int) milliseconds / 1000;
        data.put("hours", String.valueOf(seconds / 3600));
        data.put("minutes", String.valueOf((seconds % 3600) / 60));
        return data;
    }
}
