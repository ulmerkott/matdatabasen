package ulmerkott.matdatabasen;

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

    public String Name;
    public String Info;
    public Integer Kcal;
    public float Carb;
    public float Fat;
    public float Protein;
    public HashMap<String, Integer> Portions;

    public Food(String name, String info, Integer kcal, float carb, float fat, float protein) {
        Name = name;
        Info = info;
        Kcal = kcal;
        Carb = carb;
        Fat = fat;
        Protein = protein;
    }
}
