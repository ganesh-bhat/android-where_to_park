package emacs.wheretopark;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import emacs.wheretopark.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

   FragmentManager.OnBackStackChangedListener fragmentChangeListener = new FragmentManager.OnBackStackChangedListener()
        {
            public void onBackStackChanged()
            {
                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof MainActivityFragment) {
                    currentFragment.onResume();
                }
            }
        };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewStreet();
            }
        });
        getSupportFragmentManager().addOnBackStackChangedListener(fragmentChangeListener);
    }

    /* fab click listener on register new street */
    public void registerNewStreet() {
        Toast.makeText(this,"register new street!",Toast.LENGTH_LONG).show();
        getSupportFragmentManager().beginTransaction().addToBackStack("main fragment").commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddNewStreetFragment()).addToBackStack("second fragment").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSupportFragmentManager().removeOnBackStackChangedListener(fragmentChangeListener);

    }
}
