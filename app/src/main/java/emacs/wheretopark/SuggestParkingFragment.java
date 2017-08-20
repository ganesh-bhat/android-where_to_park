package emacs.wheretopark;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import emacs.wheretopark.databinding.FragmentSuggestParkingBinding;

/**
 * Created by ganbhat on 7/5/2017.
 */

public class SuggestParkingFragment extends Fragment{
    private static final  String LOG_TAG = "SuggestParkingFragment";
    private FragmentSuggestParkingBinding binding;
    private static final String STREET_NAME = "STREET_NAME";
    public static final String STREET_JSON_INPUT = "StreetJson";
    private StreetDetails streetDetails;
    private int landmarkItemIndex = 0;
    private ArrayList<String> shopsList;
    private boolean isUserAnswerRight;

    public SuggestParkingFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSuggestParkingBinding.inflate(inflater);
        binding.setPresenter(this);
        return binding.getRoot();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_suggest_parking, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_edit){

            return true;
        } else if(id == R.id.action_remove){
            deleteStreet();
            getActivity().getSupportFragmentManager().popBackStack();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    public void deleteStreet() {
        SharedPreferences sp = getActivity().getSharedPreferences(STREET_NAME, Context.MODE_APPEND);
        List<StreetDetails> streetDetailsList = null;
        if(sp.contains("StreetList")) {
            String streetListJson = sp.getString("StreetList",null);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<StreetDetails>>() {}.getType();
            streetDetailsList = gson.fromJson(streetListJson,listType);

            // find current street to delete
            for(Iterator<StreetDetails> streetItr = streetDetailsList.iterator();streetItr.hasNext();) {
                StreetDetails eachStreet = streetItr.next();
                if(eachStreet.getNameOfTheStreat().equals(streetDetails.getNameOfTheStreat())) {
                    streetItr.remove();
                    break;
                }
            }

            // update the storage
            Log.i(LOG_TAG,"Updated Street List JSON Read:"+streetDetailsList);
            if(streetDetailsList.size() == 0) {
                sp.edit().remove("StreetList").commit();
            } else {
                sp.edit().putString("StreetList",gson.toJson(streetDetailsList)).commit();
            }

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle arguments = getArguments();
        String streetJson = arguments.getString(STREET_JSON_INPUT);
        Gson gson = new Gson();
        streetDetails = gson.fromJson(streetJson, StreetDetails.class);
        shopsList = new ArrayList<>(streetDetails.getShopsList().keySet());
        showQuestion();
    }

    public void SkipToNextQuestion() {
        landmarkItemIndex++;
        showQuestion();
    }

    public void landmarkTowarsLeft() {//answer taken, now show the parking details
        binding.landmarkQuestionContainer.setVisibility(View.GONE);
        isUserAnswerRight = false;
        showParkingDetails();

    }

    public void landmarkTowarsRight() {//answer taken, now show the parking details
        binding.landmarkQuestionContainer.setVisibility(View.GONE);
        isUserAnswerRight = true;
        showParkingDetails();
    }

    public void showQuestion() {

        if(shopsList.size()>0) {
            landmarkItemIndex = landmarkItemIndex%shopsList.size();//provide rolling for questions
            binding.landmarkQuestion.setText("Which side is "+shopsList.get(landmarkItemIndex));
        } else {
            binding.landmarkQuestion.setText("Error,couldnt show question");
        }
    }


    public void showParkingDetails() {
        String parkingSide = "Left";
        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        boolean isTodayEven = dayOfMonth%2 == 0;
        String questionAnswerdForShop = shopsList.get(landmarkItemIndex);
        
        boolean isLandmarkPakingAllowedOnEvenDay = streetDetails.getShopsList().get(questionAnswerdForShop);

        if(isLandmarkPakingAllowedOnEvenDay && isTodayEven) {
            parkingSide = isUserAnswerRight?"Right":"Left";
        } else if(!isLandmarkPakingAllowedOnEvenDay && isTodayEven) {
            parkingSide = isUserAnswerRight?"Left":"Right";
        } else if(isLandmarkPakingAllowedOnEvenDay && !isTodayEven) {
            parkingSide = isUserAnswerRight?"Left":"Right";
        } else  if(!isLandmarkPakingAllowedOnEvenDay && !isTodayEven) {
            parkingSide = isUserAnswerRight?"Right":"Left";
        }


        binding.landmarkQuestionAnswer.setText(questionAnswerdForShop+" towards your "+(isUserAnswerRight?"Right":"Left"));
        binding.parkingSuggestion.setText("'"+parkingSide+"' side parking today");

    }
}
