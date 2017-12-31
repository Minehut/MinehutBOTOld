package com.minehut.discordbot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by MatrixTunnel on 6/16/2017.
 */
@Getter
public class UserManager {

    public static String FILE_NAME = "users.json";

    private JSONArray userJson = new JSONArray();

    public void save(Object obj) throws IOException {
        BufferedWriter fout = new BufferedWriter(new FileWriter(FILE_NAME));
        fout.write(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(obj.toString()).getAsJsonArray()));
        fout.close();

        userJson = new JSONArray(obj.toString());
    }

    public void load() throws IOException {
        userJson = new JSONArray(new Gson().fromJson(new FileReader(FILE_NAME), JsonElement.class).toString());
    }

}
