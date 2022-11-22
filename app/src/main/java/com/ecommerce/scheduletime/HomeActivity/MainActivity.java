package com.ecommerce.scheduletime.HomeActivity;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ecommerce.scheduletime.CreateAccount.AuthenticationActivity;
import com.ecommerce.scheduletime.Dialog.DialogNewTask;
import com.ecommerce.scheduletime.Fragments.CalendarFragment;
import com.ecommerce.scheduletime.Fragments.ListFragment;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper;
import com.ecommerce.scheduletime.Sync.SyncDatabase;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    public static Calendar getData = java.util.Calendar.getInstance();

    public static int fOpen = 1;
    public static boolean scrollToNewTask = false;
    public static Date currentTime = Calendar.getInstance().getTime();
    public static Date date_selected = new Date();

    private ActionBarDrawerToggle mDrawerToggle;

    private NavigationView drawerNav;
    public static DrawerLayout mDrawerLayout;
    public static BottomAppBar bottomAppBar;
    public static FloatingActionButton fab;
    private BottomNavigationView bottomNavigationView;
    private TextView help_feedback, terms_use, contact_us;


    String user_full_name, user_email, user_photo_uri, user_auth_with;
    private FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        user_full_name = prefs.getString("user_full_name", "");
        user_email = prefs.getString("user_email", "");
        user_photo_uri = prefs.getString("user_photo_uri", "");
        user_auth_with = prefs.getString("user_auth_with", "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.bar);
        setSupportActionBar(toolbar);
        //get User
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(MainActivity.this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(MainActivity.this);


        // Set the back arrow in the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        }
        initDrawer(toolbar);
        initStatusBar(toolbar);

        bottomAppBar = findViewById(R.id.bottomAppBar);
        fab = findViewById(R.id.fab);

        drawerNav = (NavigationView) findViewById(R.id.drawer_nav_view);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        //drawerNav.setCheckedItem(R.id.drawer_nav_profile);
        bottomNavigationView.getMenu().getItem(1).setEnabled(false);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, new ListFragment())
                .commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.listFragment:
                        fragment = new ListFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, fragment)
                                .commit();
                        fOpen = 1;
                        getData = java.util.Calendar.getInstance();
                        break;
                    case R.id.calenderFragment:
                        fragment = new CalendarFragment();
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, fragment)
                                .commit();
                        fOpen = 2;
                        getData = java.util.Calendar.getInstance();
                        break;
                }
                return true;
            }
        });
        drawerNav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.drawer_nav_profile:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;
                    case R.id.drawer_nav_settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case R.id.drawer_nav_logout:
                        if (fUser == null) {
                            startActivity(new Intent(MainActivity.this, AuthenticationActivity.class));
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.login), Toast.LENGTH_SHORT).show();
                        } else {

                            //Firebase Logout *(Twitter)* *(Facebook)*
                            if (fUser != null) {
                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }

                            //Google Logout
                            if (account != null) {
                                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(MainActivity.this, AuthenticationActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                });
                            }
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.log_out), Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //hide the BottomNavigationView when scrolling
        //CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        //layoutParams.setBehavior(new BottomNavigationViewBehavior());

        //User info Drawer headerView
        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_nav_view);
        View headerView = navigationView.getHeaderView(0);
        CircleImageView drawer_nav_profile_image = (CircleImageView) headerView.findViewById(R.id.drawer_nav_profile_image);
        CircleImageView drawer_nav_profile_auth = (CircleImageView) headerView.findViewById(R.id.drawer_nav_profile_auth);
        CircleImageView drawer_nav_profile_auth_background = (CircleImageView) headerView.findViewById(R.id.drawer_nav_profile_auth_background);
        TextView drawer_nav_profile_name = (TextView) headerView.findViewById(R.id.drawer_nav_profile_name);
        TextView drawer_nav_profile_email = (TextView) headerView.findViewById(R.id.drawer_nav_profile_email);

        try {
            Glide.with(MainActivity.this).load(Uri.parse(user_photo_uri)).into(drawer_nav_profile_image);
            if (user_auth_with.equals("Firebase")) {
                drawer_nav_profile_auth.setVisibility(View.GONE);
                headerView.findViewById(R.id.drawer_nav_profile_auth_background).setVisibility(View.GONE);
            } else if (user_auth_with.equals("Google")) {
                Glide.with(MainActivity.this).load(R.drawable.ic_icons8_google_1).into(drawer_nav_profile_auth);
            } else if (user_auth_with.equals("Facebook")) {
                Glide.with(MainActivity.this).load(R.drawable.ic_icons8_facebook).into(drawer_nav_profile_auth);
            } else if (user_auth_with.equals("Twitter")) {
                Glide.with(MainActivity.this).load(R.drawable.icons8_twitter).into(drawer_nav_profile_auth);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        drawer_nav_profile_name.setText(user_full_name);
        drawer_nav_profile_email.setText(user_email);

        //Underline texts Drawer
        help_feedback = findViewById(R.id.drawer_HelpAndFeedback);
        help_feedback.setPaintFlags(help_feedback.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        terms_use = findViewById(R.id.drawer_TermsOfUse);
        terms_use.setPaintFlags(terms_use.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        contact_us = findViewById(R.id.drawer_ContactUs);
        contact_us.setPaintFlags(contact_us.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        help_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.behance.net/gallery/126520417/To-Do-App-Design")));
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        terms_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.termsfeed.com/live/53b04044-cba4-403b-8dcb-c87190596b00")));
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContactUsActivity.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        //new SyncDatabase(MainActivity.this);
    }

    //Add new Task
    public void fab(View view) {

        currentTime = Calendar.getInstance().getTime();

        DialogNewTask dialogNewTask = new DialogNewTask((Activity) MainActivity.this);
        dialogNewTask.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogNewTask.getNew_saveBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialogNewTask.getRepeat() == 0) {
                    // Repeat One time.
                    MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);
                    myDB.addSchedule(dialogNewTask.getDate(), dialogNewTask.getTitle(), dialogNewTask.getDescription(),
                            dialogNewTask.getPriority(), dialogNewTask.getCategory(), dialogNewTask.getTime(), "no",
                            dialogNewTask.getReminder());

                    dialogNewTask.dismiss();

                    if (fOpen == 1) {
                        //restart listFragment and scroll to new task
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, new ListFragment())
                                .commit();
                        scrollToNewTask = true;
                    } else if (fOpen == 2) {
                        //restart calendarFragment and scroll to new task
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainerView, new CalendarFragment())
                                .commit();
                        scrollToNewTask = true;
                    }

                } else if (dialogNewTask.getRepeat() == 1) {
                    // Repeat Everyday.
                    MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);

                    Calendar calendar_start = Calendar.getInstance();
                    List<String> start_date = Arrays.asList(dialogNewTask.getStartDate().split("-"));
                    calendar_start.set(Calendar.YEAR, Integer.parseInt(start_date.get(0)));
                    calendar_start.set(Calendar.MONTH, Integer.parseInt(start_date.get(1)));
                    calendar_start.set(Calendar.DAY_OF_MONTH, Integer.parseInt(start_date.get(2)));

                    Calendar calendar_end = Calendar.getInstance();
                    List<String> end_date = Arrays.asList(dialogNewTask.getEndDate().split("-"));
                    calendar_end.set(Calendar.YEAR, Integer.parseInt(end_date.get(0)));
                    calendar_end.set(Calendar.MONTH, Integer.parseInt(end_date.get(1)));
                    calendar_end.set(Calendar.DAY_OF_MONTH, Integer.parseInt(end_date.get(2)));

                    if (calendar_end.getTimeInMillis() - calendar_start.getTimeInMillis() < 432000000) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.this_date_not_accepted), Toast.LENGTH_SHORT).show();
                    } else {
                        while (calendar_start.getTimeInMillis() <= calendar_end.getTimeInMillis()) {
                            String date = String.valueOf(calendar_end.get(Calendar.YEAR)) + "-" + String.valueOf(calendar_end.get(Calendar.MONTH) + "-" + String.valueOf(calendar_end.get(Calendar.DAY_OF_MONTH)));
                            myDB.addSchedule(date, dialogNewTask.getTitle(), dialogNewTask.getDescription(),
                                    dialogNewTask.getPriority(), dialogNewTask.getCategory(), dialogNewTask.getTime(), "no",
                                    dialogNewTask.getReminder());

                            calendar_end.setTimeInMillis(calendar_end.getTimeInMillis() - 86400000); // Add one day to calendar_start.
                        }

                        dialogNewTask.dismiss();

                        if (fOpen == 1) {
                            //restart listFragment and scroll to new task
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainerView, new ListFragment())
                                    .commit();
                            scrollToNewTask = true;
                        } else if (fOpen == 2) {
                            //restart calendarFragment and scroll to new task
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainerView, new CalendarFragment())
                                    .commit();
                            scrollToNewTask = true;
                        }
                    }

                } else if (dialogNewTask.getRepeat() == 2) {
                    // Repeat in days selected.
                    MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);

                    Calendar calendar_start = Calendar.getInstance();
                    List<String> start_date = Arrays.asList(dialogNewTask.getStartDate().split("-"));
                    calendar_start.set(Calendar.YEAR, Integer.parseInt(start_date.get(0)));
                    calendar_start.set(Calendar.MONTH, Integer.parseInt(start_date.get(1)));
                    calendar_start.set(Calendar.DAY_OF_MONTH, Integer.parseInt(start_date.get(2)));

                    Calendar calendar_end = Calendar.getInstance();
                    List<String> end_date = Arrays.asList(dialogNewTask.getEndDate().split("-"));
                    calendar_end.set(Calendar.YEAR, Integer.parseInt(end_date.get(0)));
                    calendar_end.set(Calendar.MONTH, Integer.parseInt(end_date.get(1)));
                    calendar_end.set(Calendar.DAY_OF_MONTH, Integer.parseInt(end_date.get(2)));

                    List<Integer> days = dialogNewTask.getDays();

                    if (days.size() == 0) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.there_is_no_day_selected), Toast.LENGTH_SHORT).show();
                    } else if (calendar_end.getTimeInMillis() - calendar_start.getTimeInMillis() < 432000000) {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.this_date_not_accepted), Toast.LENGTH_SHORT).show();
                    } else {
                        while (calendar_start.getTimeInMillis() <= calendar_end.getTimeInMillis()) {

                            for (int i = 0; i < days.size(); i++) {
                                if (calendar_end.get(Calendar.DAY_OF_WEEK) == days.get(i)) {
                                    String date = String.valueOf(calendar_end.get(Calendar.YEAR)) + "-" + String.valueOf(calendar_end.get(Calendar.MONTH) + "-" + String.valueOf(calendar_end.get(Calendar.DAY_OF_MONTH)));
                                    myDB.addSchedule(date, dialogNewTask.getTitle(), dialogNewTask.getDescription(),
                                            dialogNewTask.getPriority(), dialogNewTask.getCategory(), dialogNewTask.getTime(), "no",
                                            dialogNewTask.getReminder());
                                }
                            }

                            calendar_end.setTimeInMillis(calendar_end.getTimeInMillis() - 86400000); // Subtract one day from calendar_end.
                        }

                        dialogNewTask.dismiss();

                        if (fOpen == 1) {
                            //restart listFragment and scroll to new task
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainerView, new ListFragment())
                                    .commit();
                            scrollToNewTask = true;
                        } else if (fOpen == 2) {
                            //restart calendarFragment and scroll to new task
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainerView, new CalendarFragment())
                                    .commit();
                            scrollToNewTask = true;
                        }
                    }
                }

            }
        });
        dialogNewTask.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //Toast.makeText(MainActivity.this, "dismiss", Toast.LENGTH_SHORT).show();
            }
        });
        dialogNewTask.build();
        dialogNewTask.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        if (mDrawerToggle != null) mDrawerToggle.syncState();
    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            // slide drawer from right to left
            //((DrawerLayout.LayoutParams) drawer.getChildAt(1).getLayoutParams()).gravity = GravityCompat.END;
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.syncState();
            mDrawerToggle = toggle;
        }
        mDrawerLayout = drawer;
    }

    protected void initStatusBar(View toolbar) {
        // Ensure `setStatusBarImmersiveMode()`
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            // Ensure content view `fitsSystemWindows` is false.
            ViewGroup contentParent = (ViewGroup) findViewById(android.R.id.content);
            View content = contentParent.getChildAt(0);
            // If using `DrawerLayout`, must ensure its subviews `fitsSystemWindows` are all false.
            // Because in some roms, such as MIUI, it will fits system windows for each subview.
            setFitsSystemWindows(content, false, true);

            // Add padding to hold the status bar place.
            clipToStatusBar(toolbar);

            // Add a view to hold the status bar place.
            // Note: if using appbar_scrolling_view_behavior of CoordinatorLayout, however,
            // the holder view could be scrolled to outside as it above the app bar.
            //holdStatusBar(toolbar, R.color.colorPrimary);
        }
    }

    protected void setFitsSystemWindows(View view, boolean fitSystemWindows, boolean applyToChildren) {
        if (view == null) return;
        view.setFitsSystemWindows(fitSystemWindows);
        if (applyToChildren && (view instanceof ViewGroup)) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0, n = viewGroup.getChildCount(); i < n; i++) {
                viewGroup.getChildAt(i).setFitsSystemWindows(fitSystemWindows);
            }
        }
    }

    protected void clipToStatusBar(View view) {
        final int statusBarHeight = getStatusBarHeight(this);
        view.getLayoutParams().height += statusBarHeight;
        view.setPadding(0, statusBarHeight, 0, 0);
    }

    protected int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    protected void setStatusBarImmersiveMode(@ColorInt int color) {
        Window win = getWindow();

        // StatusBar
        if (Build.VERSION.SDK_INT >= 19) { // 19, 4.4, KITKAT
            win.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (Build.VERSION.SDK_INT >= 21) { // 21, 5.0, LOLLIPOP
            win.getAttributes().systemUiVisibility |=
                    (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            win.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            win.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            win.setStatusBarColor(color);
        }

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setStatusBarImmersiveMode(Color.TRANSPARENT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDrawerLayout != null) {
            getMenuInflater().inflate(R.menu.drawer_menu, menu);
        }
        //TintUtils.tintList(this, menu, R.color.bar_icon_color);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}