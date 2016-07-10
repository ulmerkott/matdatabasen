package ulmerkott.matdatabasen;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class MatInfoActivity extends AppCompatActivity {

    private Food FoodInfo;
    private PieChart MatChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mat_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String matRowId = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            matRowId = extras.getString(DatabaseAccess.INTENT_ROW_ID);
        }

        DatabaseAccess matDbAccess = DatabaseAccess.getInstance(this);
        FoodInfo = matDbAccess.GetFood(matRowId);

        MatInfoActivity.this.setTitle(FoodInfo.Name);
        TextView extendedInfo = (TextView) findViewById(R.id.extentedInfo);
        extendedInfo.setText(FoodInfo.Info);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        registerForContextMenu(fab);

        final PopupMenu popup = new PopupMenu(MatInfoActivity.this, fab);
        popup.getMenuInflater().inflate(R.menu.menu_portions, popup.getMenu());
        popup.getMenu().add(R.string.custom_portion);
        for (String key: FoodInfo.Portions.keySet() ) {
            popup.getMenu().add(key);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Setup menu item selection
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        String title = (String) item.getTitle();
                        Toast.makeText(MatInfoActivity.this, title + " " + FoodInfo.Portions.get(title).toString() + " g", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });
                // Handle dismissal with: popup.setOnDismissListener(...);
                // Show the menu
                popup.show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Transition enterTrans = new Slide();
        getWindow().setEnterTransition(enterTrans);

        Transition returnTrans = new Fade();
        getWindow().setReturnTransition(returnTrans);
        getWindow().setExitTransition(returnTrans);


    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        CreatePieChart();
    }

    private void CreatePieChart() {

        MatChart = (PieChart) findViewById(R.id.chart);
/*
        MatChart.setRotationEnabled(false);
        MatChart.setMaxAngle(180f);
        MatChart.setRotationAngle(180f);

        MatChart.setDrawHoleEnabled(true);
        MatChart.setDescription("");
        MatChart.setUsePercentValues(true);
        MatChart.setBackgroundColor(Color.TRANSPARENT);
        MatChart.setHoleColor(Color.TRANSPARENT);
        MatChart.setTransparentCircleAlpha(255);
        MatChart.setTransparentCircleColor(Color.TRANSPARENT);
        MatChart.setCenterTextOffset(0, -20);*/

        MatChart = (PieChart) findViewById(R.id.chart);
        MatChart.setBackgroundColor(Color.parseColor("#fafafa"));


        MatChart.setUsePercentValues(true);
        MatChart.setDescription("");

        MatChart.setCenterText("Hejsanhoppsan");

        MatChart.setDrawHoleEnabled(true);
        MatChart.setHoleColor(Color.parseColor("#fafafa"));

        MatChart.setTransparentCircleColor(Color.parseColor("#fafafa"));
        MatChart.setTransparentCircleAlpha(110);

        MatChart.setHoleRadius(58f);
        MatChart.setTransparentCircleRadius(61f);

        MatChart.setDrawCenterText(true);

        MatChart.setRotationEnabled(false);
        MatChart.setHighlightPerTapEnabled(true);

        MatChart.setMaxAngle(180f); // HALF CHART
        MatChart.setRotationAngle(180f);
        MatChart.setCenterTextOffset(0, -20);



        setChartData();

        MatChart.animateY(1000, Easing.EasingOption.EaseOutCubic);

        Legend legend = MatChart.getLegend();
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);
        legend.setForm(Legend.LegendForm.CIRCLE);
    }

    private void setChartData() {
        // creating data values
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (FoodInfo.Carb > 0) {
            entries.add(new PieEntry(FoodInfo.Carb, "Kolhydrater"));
        }
        if (FoodInfo.Fat > 0) {
            entries.add(new PieEntry(FoodInfo.Fat, "Fett"));
        }
        if (FoodInfo.Protein > 0) {
            entries.add(new PieEntry(FoodInfo.Protein, "Protein"));
        }

        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(ColorTemplate.MATERIAL_COLORS);
        dataset.setValueTextSize(14);
        dataset.setSliceSpace(1);

        PieData data = new PieData(dataset);
        MatChart.setData(data); //set data into chart
    }
}

