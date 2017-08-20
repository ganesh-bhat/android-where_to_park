package emacs.wheretopark.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;

import emacs.wheretopark.R;
import emacs.wheretopark.databinding.FragmentSuggestParkingBinding;
import emacs.wheretopark.engine.ParkingComputeEngine;
import emacs.wheretopark.model.StreetDetails;
import emacs.wheretopark.storage.BackendStorage;

/**
 * Created by ganbhat on 7/5/2017.
 */

public class SuggestParkingFragment extends Fragment{
    private FragmentSuggestParkingBinding binding;
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
            BackendStorage.BACKEND_STORAGE.deleteStreet(streetDetails);
            getActivity().getSupportFragmentManager().popBackStack();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        String landmarkShopChosen = shopsList.get(landmarkItemIndex);
        String parkingSide = ParkingComputeEngine.getParkingSide(streetDetails,landmarkShopChosen,isUserAnswerRight);
        binding.landmarkQuestionAnswer.setText(landmarkShopChosen+" towards your "+(isUserAnswerRight?"Right":"Left"));
        binding.parkingSuggestion.setText("'"+parkingSide+"' side parking today");
    }
}
