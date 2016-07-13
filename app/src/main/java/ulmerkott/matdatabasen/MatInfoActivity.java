package ulmerkott.matdatabasen;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
        // Color same as default activity background color.
        int BACKGROUND_COLOR = Color.parseColor("#fafafa");

        MatChart = (PieChart) findViewById(R.id.chart);

        MatChart.setBackgroundColor(BACKGROUND_COLOR);

        MatChart.setUsePercentValues(true);
        MatChart.setDescription("");

        //MatChart.setCenterText("Hejsanhoppsan");

        MatChart.setDrawHoleEnabled(true);
        MatChart.setHoleColor(BACKGROUND_COLOR);
        MatChart.setHoleRadius(80);

        MatChart.setTransparentCircleColor(BACKGROUND_COLOR);
        MatChart.setTransparentCircleAlpha(110);

        MatChart.setDrawCenterText(false);

        MatChart.setRotationEnabled(false);
        MatChart.setHighlightPerTapEnabled(false);

        // Use half pie
        MatChart.setMaxAngle(360f);
        MatChart.setRotationAngle(360f);
        //MatChart.setCenterTextOffset(0, -20);

        Legend legend = MatChart.getLegend();
        legend.setPosition(Legend.LegendPosition.PIECHART_CENTER);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setYOffset(-10f);
        //legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        //legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setMaxSizePercent(95);
        //legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        legend.setFormSize(14);
        legend.setTextSize(12);
        //legend.setMaxSizePercent(50);
        /*
        legend.setXEntrySpace(30f);
        legend.setYEntrySpace(0f);
        legend.setFormToTextSpace(4f);
        legend.setXOffset(-40f);
        legend.setYOffset(0f);
*/


        setChartData();

        MatChart.animateY(1000, Easing.EasingOption.EaseOutCubic);

    }

    private void setChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        if (FoodInfo.Carb > 0) {
            entries.add(new PieEntry(FoodInfo.getCarbKcal(), "Kolhydrater " + FoodInfo.getCarbKcal() + " kcal"));
        }
        if (FoodInfo.Fat > 0) {
            entries.add(new PieEntry(FoodInfo.getFatKcal(), "Fett " + FoodInfo.getFatKcal() + " kcal"));
        }
        if (FoodInfo.Protein > 0) {
            entries.add(new PieEntry(FoodInfo.getProteinKcal(), "Protein " + FoodInfo.getProteinKcal() + " kcal"));
        }
        if (FoodInfo.Alcohol > 0) {
            entries.add(new PieEntry(FoodInfo.getAlkoholKcal(), "Alkohol " + FoodInfo.getAlkoholKcal() + " kcal"));
        }
        if (FoodInfo.Fiber > 0) {
            entries.add(new PieEntry(FoodInfo.getFiberKcal(), "Fiber " + FoodInfo.getFiberKcal() + " kcal"));
        }

        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(ColorTemplate.MATERIAL_COLORS);
        dataset.setDrawValues(false);
/*
        dataset.setValueTextSize(14);
        dataset.setValueLinePart1Length(0.4f);
        dataset.setValueLinePart2Length(0.1f);
        dataset.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                DecimalFormat df = new DecimalFormat("#.#");
                df.setRoundingMode(RoundingMode.CEILING);
                return df.format(v) + " kcal";
            }
        });
        dataset.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        dataset.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataset.setSliceSpace(0f);
*/
        PieData data = new PieData(dataset);
        MatChart.setDrawEntryLabels(false);
        MatChart.setData(data);
    }
}

