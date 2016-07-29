package ulmerkott.matdatabasen;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by nangaroo on 7/2/16.
 */
public class Food {
   /* FROM THE DB:
    "id" INTEGER,
    "name" TEXT,
    "energi, beräknad (kJ)" REAL,
    "kolhydrater, digererbara (g)" TEXT,
    "fett, totalt (g)" TEXT,
    "protein, totalt (g)" TEXT,
    "portions" TEXT DEFAULT ('NULL')
     */

    private class MacroNutrient
    {
        private float Grams;
        private float KcalFactor;

        public MacroNutrient(float grams, float kcalFactor) {
            Grams = grams;
            KcalFactor = kcalFactor;
        }

        public float GetKcal() {
            return Grams * KcalFactor;
        }
    }

    private float Portion = 1;

    public String Name;
    public String Info;
    public Integer Kcal;

    public MacroNutrient Carb;
    public MacroNutrient Fat;
    public MacroNutrient Protein;
    public MacroNutrient Alcohol;
    public MacroNutrient Fiber;

    public HashMap<String, Integer> Portions;
    public List<MacroNutrient> MacroNutrients;

    public Food(String name, String info, Integer kcal, float carb, float fat, float protein,
                float alcohol, float fiber) {
        Name = name;
        Info = info;
        Kcal = kcal;

        Carb = new MacroNutrient(carb, 4);
        Fat = new MacroNutrient(fat, 9);
        Protein = new MacroNutrient(protein, 4);
        Alcohol = new MacroNutrient(alcohol, 7);
        Fiber = new MacroNutrient(fiber, 2);

        MacroNutrients.add(Carb);
        MacroNutrients.add(Fat);
        MacroNutrients.add(Protein);
        MacroNutrients.add(Alcohol);
        MacroNutrients.add(Fiber);
    }

    public float getCarbKcal() {
        return Carb.GetKcal() * Portion;
    }

    public float getFatKcal() {
        return Fat.GetKcal() * Portion;
    }

    public float getProteinKcal() {
        return Protein.GetKcal() * Portion;
    }

    public float getAlkoholKcal() {
        return Alcohol.GetKcal() * Portion;
    }

    public float getFiberKcal() {
        return Fiber.GetKcal() * Portion;
    }

    public float getTotalKcal() {
      float total = 0;
      for (MacroNutrient mn: MacroNutrients) {
        total += mn.GetKcal();
      }
      return total * Portion;
    }

    public void setPortion(float grams) {
        Portion = grams/100;
        Log.d("ULMER", "Portion factor is " + Portion);
    }
}
