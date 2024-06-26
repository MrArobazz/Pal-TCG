package com.example.paltcg.dataclasses;

import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Pokemon extends AsyncTask<Void,Integer,Void> implements Parcelable {
    String name;

    Integer id_in_set;

    Integer max_pv;
    Integer pv;

    Type type;

    Type weakness;

    Type resistance;

    ArrayList<Attack> attacks;

    boolean ready = false;

    public Pokemon(String name, Integer id_in_set) {
        this.name = name;
        this.id_in_set = id_in_set;
        this.attacks = new ArrayList<>();
    }

    protected Pokemon(Parcel in) {
        name = in.readString();
        id_in_set = in.readInt();
        max_pv = in.readInt();
        pv = in.readInt();
        type = Type.values()[in.readInt()];
        weakness = Type.values()[in.readInt()];
        resistance = Type.values()[in.readInt()];
        int nb_attacks = in.readInt();
        for (int i = 0 ; i < nb_attacks ; i++)
            attacks.add(new Attack(in.readString(),in.readInt()));
        byte tmp_ready = in.readByte();
        ready = tmp_ready != 0;
        if (!ready)
            fetchDatas();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(id_in_set);
        dest.writeInt(max_pv);
        dest.writeInt(pv);
        dest.writeInt(type.ordinal());
        dest.writeInt(weakness.ordinal());
        dest.writeInt(resistance.ordinal());
        dest.writeInt(attacks.size());
        for (Attack attk : attacks) {
            dest.writeString(attk.getName());
            dest.writeInt(attk.getDegats());
        }
        dest.writeByte((byte) (ready ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pokemon> CREATOR = new Creator<Pokemon>() {
        @Override
        public Pokemon createFromParcel(Parcel in) {
            return new Pokemon(in);
        }

        @Override
        public Pokemon[] newArray(int size) {
            return new Pokemon[size];
        }
    };

    public void fetchDatas() {
        // if we do not already fetch datas
        if (!ready)
            // to get Charizard datas directly
            if (name.equals("Charizard")) {
                pv = 120;
                max_pv = pv;
                type = Type.Fire;
                weakness = Type.Water;
                resistance = Type.Fighting;
                attacks.add(new Attack("Fire Spin",100));
                ready = true;
            }
            // for the others
            else execute();
    }

    public String getName() { return name;}

    public Integer getMaxPv() { return max_pv;}
    public Integer getPv() { return pv;}

    public boolean isNotReady() { return !ready;}

    public ArrayList<String> getAttackNames() {
        ArrayList<String> result = new ArrayList<>();
        for (Attack attack : attacks)
            result.add(attack.getName());
        return result;
    }

    public String getBestAttackName() {
        // just a comparison on the list
        int max = -1;
        String bestName = "";
        for (Attack attack : attacks) {
            int tmp_dgts = attack.getDegats();
            if (tmp_dgts > max) {
                max = tmp_dgts;
                bestName = attack.getName();
            }
        }
        return bestName;
    }

    public Integer getDgtsFromAttackName(String attkName) {
        for (Attack attack : attacks)
            if (attack.getName().equals(attkName))
                return attack.getDegats();
        return -1;
    }

    public void attackPokemon(Pokemon opponent, int degats) {
        // weakness and resistance coefs are always the same
        if (opponent.weakness == this.type)
            opponent.pv -= degats * 2;
        else if (opponent.resistance == this.type)
            opponent.pv -= (Math.max(degats - 30, 0));
        else opponent.pv -= degats;
        if (opponent.pv < 0)
            opponent.pv = 0;
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
                                max_pv = pv;
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
                                String attackDgts = result;
                                if (result.length() == 3)
                                    if (result.charAt(2) != '0')
                                        attackDgts = result.substring(0,2);
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
        } catch (IOException e) {
            Log.e("POKEMON_FETCH_DATAS", "Error during connection...",e);
        } finally {
            ready = true;
            Log.i("TAG", "Pokemon: end do in background");
        }
        return null;
    }
}
