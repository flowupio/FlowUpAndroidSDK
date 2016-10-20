/*
 * Copyright (C) 2016 Go Karumi S.L.
 */

package io.flowup.example;

import android.content.Intent;
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

  private static final String LOGTAG = "FlowUpExample";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(LOGTAG, "onCreate");
    setContentView(R.layout.main_activity);
    MarvelApiConfig marvelApiConfig = MarvelApiConfig.with("54355f684e1983a183d7bfec96a4bf81",
        "4ad71e7b61e40311545909af0d6ebbd52bbfeae3");
    final CharacterApiClient characterApiClient = new CharacterApiClient(marvelApiConfig);
    findViewById(R.id.download_data_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {

        new Thread(new Runnable() {
          @Override public void run() {
            try {
              MarvelResponse<CharactersDto> characters = characterApiClient.getAll(0, 50);
              Log.d(LOGTAG, characters.getResponse().getCharacters().size()
                  + " characters obtained from the api");
            } catch (MarvelApiException e) {
              Log.e(LOGTAG, "Error getting marvel characters", e);
            }
          }
        }).start();
      }
    });
    findViewById(R.id.open_activity_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(getBaseContext(), SecondActivity.class);
        startActivity(intent);
      }
    });
  }

  @Override protected void onStart() {
    super.onStart();
    Log.d(LOGTAG, "onStart");
  }

  @Override protected void onResume() {
    super.onResume();
    Log.d(LOGTAG, "onResume");
  }

  @Override protected void onPause() {
    super.onPause();
    Log.d(LOGTAG, "onPause");
  }

  @Override protected void onStop() {
    super.onStop();
    Log.d(LOGTAG, "onStop");
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    Log.d(LOGTAG, "onDestroy");
  }
}
