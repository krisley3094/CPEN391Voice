package com.example.krisley3094.vorec;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechSettings;
import com.google.protobuf.ByteString;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.google.auth.oauth2.GoogleCredentials.getApplicationDefault;

public class MainActivity extends AppCompatActivity {

    private String TAG = "VOICE : ";

    SpeechClient speech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void startButton(View view) {



        new LongOperation().execute();
    }

    private class LongOperation extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            AssetManager assetManager = getAssets();

            try {
                InputStream keyInputStream = assetManager.open("cpen-91c7e0d5c2d9.json");
                GoogleCredentials credentials = GoogleCredentials.fromStream(keyInputStream);
                FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

                SpeechSettings speechSettings =
                        SpeechSettings.newBuilder()
                                .setCredentialsProvider(credentialsProvider)
                                .build();

                speech = SpeechClient.create(speechSettings);

                InputStream fileInputStream = assetManager.open("voiceTest.wav");
                ByteString audioBytes = ByteString.readFrom(fileInputStream);
                // Builds the sync recognize request
                RecognitionConfig config = RecognitionConfig.newBuilder()
                        //.setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        //.setSampleRateHertz(16000)
                        .setLanguageCode("en-US")
                        .build();
                RecognitionAudio audio = RecognitionAudio.newBuilder()
                        .setContent(audioBytes)
                        .build();

                // Performs speech recognition on the audio file
                RecognizeResponse response = speech.recognize(config, audio);
                List<SpeechRecognitionResult> results = response.getResultsList();

                for (SpeechRecognitionResult result : results) {
                    Log.d("CLIENT READY : ", "inner");
                    // There can be several alternative transcripts for a given chunk of speech. Just use the
                    // first (most likely) one here.
                    SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                    //System.out.printf("Transcription: %s%n", alternative.getTranscript());
                    Log.d(TAG, "Transcription: %s%n" + alternative.getTranscript());
                }
                try {
                    speech.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }






}


