package emacs.wheretopark;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import emacs.wheretopark.databinding.FragmentAddNewStreeetBinding;
import emacs.wheretopark.databinding.NewLandmarkItemBinding;

/**
 * Created by ganbhat on 7/3/2017.
 */

public class AddNewStreetFragment extends Fragment {
    private static final String STREET_NAME = "STREET_NAME";
    private static final String STREET_LIST = "StreetList";
    private static final String LOG_TAG = "AddNewStreetFragment";

    private FragmentAddNewStreeetBinding binding;

    public AddNewStreetFragment(){

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddNewStreeetBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_new_street, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_done){
            new StreetAdderTask(getStreetDetails()).execute((Void[])null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    List<NewLandmarkItemBinding> landmarkBindings = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        binding.addNewLandmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewLandmarkItemBinding landmarkItemBinding = NewLandmarkItemBinding.inflate(getActivity().getLayoutInflater(),binding.landmarksContainer,true);
                landmarkBindings.add(landmarkItemBinding);
            }
        });
    }

    public StreetDetails getStreetDetails() {
        StreetDetails streetDetails = new StreetDetails();
        boolean isEvenOddRule = binding.isEvenOddRuleToggle.isChecked();
        String streetName = binding.streetNameEt.getText().toString();
        boolean isRightSideParking = binding.rightSideParkingToggle.isChecked();

        Map<String,Boolean> landmarksList = new HashMap<>();
        /* get details of today */
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        boolean isEvenDateToday = dayOfMonth%2==0;

        for(NewLandmarkItemBinding binding:landmarkBindings) {
            String landmarkName = binding.landmarkName.getText().toString();
            boolean isLandmarkTowardsRight = binding.isLandmarkTowardsRight.isChecked();
            boolean isLandmarkParkingAllowedOnEvenDay = isLandmarkParkingAllowedOnEvenDay(isLandmarkTowardsRight,isRightSideParking,isEvenDateToday);
            landmarksList.put(landmarkName,isLandmarkParkingAllowedOnEvenDay);
        }

        streetDetails.setEvenOddRule(isEvenOddRule);
        streetDetails.setNameOfTheStreat(streetName);
        streetDetails.setShopsList(landmarksList);
        return  streetDetails;
    }

    private boolean isLandmarkParkingAllowedOnEvenDay(boolean isLandmarkTowardsRight, boolean isRightSideParking,boolean isEvenDateToday) {
        String landmarkParkingAllowedDay = null;
        if(isLandmarkTowardsRight && isRightSideParking && isEvenDateToday) {
                /* landmark right, right side parking, today even day */
            landmarkParkingAllowedDay = "EVEN"; // parking infront on Even day allowed
        } else if(isLandmarkTowardsRight && !isRightSideParking && !isEvenDateToday) {
                /* landmark right, left side parking, on odd day */
            landmarkParkingAllowedDay = "EVEN"; // on even day, parking infront of landmark allowed
        }else if(isLandmarkTowardsRight && isRightSideParking && !isEvenDateToday) {
                /* landmark is right, parking is right side, on odd day */
            landmarkParkingAllowedDay = "ODD"; // odd day parking infront of landmark
        }else if(!isLandmarkTowardsRight && isRightSideParking && isEvenDateToday) {
            landmarkParkingAllowedDay = "ODD";
        }else if(!isLandmarkTowardsRight && isRightSideParking && !isEvenDateToday) {
            landmarkParkingAllowedDay = "EVEN";

        }else if(!isLandmarkTowardsRight && !isRightSideParking && !isEvenDateToday) {
            landmarkParkingAllowedDay = "ODD";

        }else if(isLandmarkTowardsRight && !isRightSideParking && isEvenDateToday) {
            landmarkParkingAllowedDay = "ODD";

        }else if(!isLandmarkTowardsRight && !isRightSideParking && isEvenDateToday) {
            landmarkParkingAllowedDay = "EVEN";
        }
        return "EVEN".equals(landmarkParkingAllowedDay);
    }

    class StreetAdderTask extends AsyncTask<Void,Void,Void> {

        StreetDetails mStreetDetails = null;
        StreetAdderTask(StreetDetails streetDetails) {
            mStreetDetails = streetDetails;
            Log.i(LOG_TAG,"Street Details"+mStreetDetails);
        }
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(),"Saving..","Please wait while we save your preference",true,false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            getActivity().getSupportFragmentManager().popBackStack();
        }

        @Override
        protected Void doInBackground(Void... params) {
            SharedPreferences sp = getActivity().getSharedPreferences(STREET_NAME, Context.MODE_APPEND);
            List<StreetDetails> streetDetailsList = null;
            Gson gson = new Gson();
            if(sp.contains(STREET_LIST)) {
                String streetListJson = sp.getString(STREET_LIST,null);
                Type listType = new TypeToken<List<StreetDetails>>() {}.getType();
                streetDetailsList = gson.fromJson(streetListJson, listType);
            } else {
                streetDetailsList = new ArrayList<>();
            }
            streetDetailsList.add(mStreetDetails);//add new one
            String newJson = gson.toJson(streetDetailsList);
            Log.i(LOG_TAG,"Street List JSON:"+newJson);
            sp.edit().putString(STREET_LIST,newJson).commit();
            return null;
        }


    }
}
