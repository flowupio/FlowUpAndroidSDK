/*
 * Copyright (C) 2015 Go Karumi S.L.
 */

package com.flowup.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.karumi.marvelapiclient.CharacterApiClient;
import com.karumi.marvelapiclient.MarvelApiConfig;
import com.karumi.marvelapiclient.MarvelApiException;
import com.karumi.marvelapiclient.model.CharactersDto;
import com.karumi.marvelapiclient.model.MarvelResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        MarvelApiConfig marvelApiConfig = MarvelApiConfig.with("54355f684e1983a183d7bfec96a4bf81",
                "4ad71e7b61e40311545909af0d6ebbd52bbfeae3");
        final CharacterApiClient characterApiClient = new CharacterApiClient(marvelApiConfig);
        findViewById(R.id.download_data_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MarvelResponse<CharactersDto> characters = characterApiClient.getAll(0, 50);
                            Log.d("FlowUp", characters.getResponse().getCharacters().size()
                                    + "characters obtained from the api");
                        } catch (MarvelApiException e) {
                            Log.e("Error", "Error retrieveing marvel characters", e);
                        }
                    }
                }).start();
            }
        });
    }
}
