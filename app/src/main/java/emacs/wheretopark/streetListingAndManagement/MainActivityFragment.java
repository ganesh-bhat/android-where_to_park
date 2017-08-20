package emacs.wheretopark.streetListing;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import emacs.wheretopark.R;
import emacs.wheretopark.fragments.SuggestParkingFragment;
import emacs.wheretopark.model.StreetDetails;
import emacs.wheretopark.storage.BackendStorage;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends ListFragment {

    private static final String LOG_TAG = "MainActivityFragment";
    private static final String STREET_NAME = "STREET_NAME";
    List<StreetDetails> streetDetailsList = new ArrayList<>();
    ArrayAdapter adapter;
    StreetNameListLoader dataLoaderTask;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.street_name_list_fragment,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new StreetNameListAdapter(getContext(),streetDetailsList);
        setListAdapter(adapter);

    }


    @Override
    public void onDetach() {
        if(dataLoaderTask.getStatus() == AsyncTask.Status.PENDING || dataLoaderTask.getStatus() == AsyncTask.Status.RUNNING) {
            dataLoaderTask.cancel(true);
        }
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        dataLoaderTask = new StreetNameListLoader();
        dataLoaderTask.execute((Void[])null);
    }


    @Override
    public void onDestroy() {
        if(dataLoaderTask.getStatus() == AsyncTask.Status.PENDING || dataLoaderTask.getStatus() == AsyncTask.Status.RUNNING) {
            dataLoaderTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        StreetDetails street = (StreetDetails)getListAdapter().getItem(position);
        Gson gson = new Gson();
        String streetJson =gson.toJson(street);
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("main fragment").commit();

        Bundle bundle = new Bundle();
        bundle.putString(SuggestParkingFragment.STREET_JSON_INPUT,streetJson);
        SuggestParkingFragment suggestParkingFragment = new SuggestParkingFragment();
        suggestParkingFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,suggestParkingFragment).addToBackStack("third fragment").commit();

    }

    class StreetNameListLoader extends AsyncTask<Void,Void,List<StreetDetails>> {

        @Override
        protected List<StreetDetails> doInBackground(Void... params) {
            streetDetailsList = BackendStorage.BACKEND_STORAGE.getAllStreetDetails();
            return streetDetailsList;
        }

        @Override
        protected void onPostExecute(List<StreetDetails> newStreetDetails) {
            if(newStreetDetails!=null && !newStreetDetails.isEmpty()) {
                streetDetailsList.clear();
                streetDetailsList.addAll(newStreetDetails);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
