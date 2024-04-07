package com.example.paltcg.dataclasses;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Pokemon extends AsyncTask<Void,Integer,Void> {
    String name;

    Integer id_in_set;

    Integer pv;

    Type type;

    Type weakness;

    Type resistance;

    ArrayList<Attack> attacks;

    public Pokemon(String name, Integer id_in_set) {
        this.name = name;
        this.id_in_set = id_in_set;
        this.attacks = new ArrayList<>();
        execute();
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder strAttacks = new StringBuilder("[");
        for (Attack attack : attacks) {
            strAttacks.append(attack).append(";");
        }
        strAttacks.append("]");
        return "[NAME : " + name + ";HP : " + pv + ";TYPE : " + type +
                ";WEAKNESS : " + weakness + ";RESISTANCE : " + resistance +
                ";ATTAQUES : " + strAttacks + "]";
    }
    @Override
    protected Void doInBackground(Void... voids) {
        Log.i("TAG", "doInBackground: " + name + id_in_set);
        try {
            Document doc = Jsoup.connect("https://bulbapedia.bulbagarden.net/wiki/" + name + "_(Base_Set_" + id_in_set + ")").get();

            Element elem = doc.getElementById("mw-content-text");
            if (elem != null) {
                Element datasTable = elem.selectFirst("table");

                // Find the type
                for (Element row : datasTable.select("tr")) {
                    Elements ths = row.select("th");
                    if (ths.size() == 1) {
                        Element th = ths.get(0);
                        Element a = th.selectFirst("span");
                        if (a != null) {
                            if (a.text().equals("Type")) {
                                Element td = row.selectFirst("td");
                                type = Type.valueOf(td.selectFirst("a").text());
                            }
                            else if (a.text().equals("HP")) {
                                Element td = row.selectFirst("td");
                                pv = Integer.parseInt(td.text());
                            }
                        }
                    }
                    if (ths.size() == 3) {
                        Element weakness_value = ths.get(0).selectFirst("a");
                        if (weakness_value == null)
                            weakness = Type.None;
                        else
                            weakness = Type.valueOf(weakness_value.attr("title"));

                        Element resistance_value = ths.get(1).selectFirst("a");
                        if (resistance_value == null)
                            resistance = Type.None;
                        else
                            resistance = Type.valueOf(resistance_value.attr("title"));
                    }
                }

                // Find the attacks
                // We need to get all tables
                Elements tests = elem.select("table");
                for (Element test : tests) {
                    // We gonna looking for the rights ones
                    Elements trs = test.select("tr");
                    if (trs.size() == 1) {
                        Elements ths = trs.select("th");
                        if (ths.size() == 3) {
                            if (trs.get(0).selectFirst("th[class=roundyleft]") != null) {
                                // here we are on the right ones
                                String attackName = ths.get(1).ownText();
                                String result = ths.get(2).text();
                                String attackDgts =
                                        result.length() > 2 ? result.substring(0,2) : result;
                                if (attackDgts.length() < 2) {
                                    attackDgts = "20";
                                }
                                Attack attack = new Attack(attackName,Integer.parseInt(attackDgts));
                                if (attacks.stream().noneMatch(a -> a.getName().equals(attackName))) {
                                    attacks.add(attack);
                                }
                            }
                        }
                    }
                }
            }
            Log.i("TAG", "doInBackground: " + name + pv + type + weakness + resistance + attacks);
        } catch (IOException e) {
            Log.e("POKEMON_FETCH_DATAS", "Error during connection...",e);
        }
        Log.i("TAG", "Pokemon: end do in background");
        return null;
    }
}
