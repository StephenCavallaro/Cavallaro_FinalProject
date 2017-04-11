package csc4360.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import csc4360.finalproject.jfeinstein.jazzyviewpager.JazzyViewPager;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static android.view.ActionMode actionMode = null;
    public PagerAdapter pagerAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pager)
    JazzyViewPager mJazzy;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.btn_add)
    Button btnAdd;
    @BindString(R.string.reminder_frag_title)
    String reminderFragTitle;
    @BindString(R.string.all_frag_title)
    String allFragTitle;
    int pos = 0;
    private AllFragment allFragment = null;
    private ReminderFragment reminderFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Bind butter knife with the activity
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        // Initialize the JazzyViewPager (ViewPager with animation)
        setupJazziness(JazzyViewPager.TransitionEffect.CubeOut);
        // Start Alarm service
        startService(new Intent(this, csc4360.finalproject.AlarmService.class));

        if (btnAdd != null) {
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                    intent.putExtra("action", "add");
                    startActivity(intent);
                }
            });
        }
    }

    //Initialize view pager. The parameter effect is for animation
    private void setupJazziness(JazzyViewPager.TransitionEffect effect) {
        mJazzy.setTransitionEffect(effect);
        pagerAdapter = new FixedTabsPagerAdapter(getSupportFragmentManager());
        mJazzy.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(mJazzy);
        mJazzy.setPageMargin(30);
        // Search
        mJazzy.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                // Remove old action if any from other fragment
                if (actionMode != null) {
                    actionMode.finish();
                }
                pos = position;
                switch (position) {
                    case 0:
                        reminderFragment = (ReminderFragment) getSupportFragmentManager().getFragments().get(0);
                        reminderFragment.isFirstTime = false;
                        reminderFragment.populateList();
                        break;
                    case 1:
                        allFragment = (AllFragment) getSupportFragmentManager().getFragments().get(1);
                        allFragment.populateList();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    // Tabs adapter
    private class FixedTabsPagerAdapter extends FragmentPagerAdapter {

        FixedTabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            mJazzy.setObjectForPosition(obj, position);
            return obj;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ReminderFragment();
                case 1:
                    return new AllFragment();
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return reminderFragTitle;
                case 1:
                    return allFragTitle;
                default:
                    return null;
            }
        }
    }

}