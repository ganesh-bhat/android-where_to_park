package emacs.wheretopark;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by ganbhat on 7/3/2017.
 */

class StreetNameListAdapter extends ArrayAdapter<StreetDetails> {

    LayoutInflater inflater;

    public StreetNameListAdapter(@NonNull Context context, @NonNull List<StreetDetails> objects) {
        super(context, 0, objects);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.street_name_list_item,null);
        TextView textView = (TextView)view.findViewById(R.id.textView);
        StreetDetails item = getItem(position);
        textView.setText(item.getNameOfTheStreat());
        view.setTag(item);
        return view;
    }
}
