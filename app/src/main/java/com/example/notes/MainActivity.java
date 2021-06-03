package com.example.notes;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;

import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.microsoft.cognitiveservices.speech.AudioDataStream;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.Future;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity {

    private static String speechSubscriptionKey = "65222b6e6d8547cc81754ade5f2b41f2";
    private static String serviceRegion = "westeurope";
    private static String TAG = "AAAAAA";
    private static String TABLENAME = "Notes";
    private String lang;

    private ListView lv;
    private Button button;
    private MyDBHelper dbHelper;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private Spinner langSpinner;
   // private String fileName = "azaza";
    private File path;
    private ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new MyDBHelper(this, TABLENAME);
        //dbHelper.dropTable();
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
        lv = (ListView) findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item);
        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (dbHelper.getWords(TABLENAME).get(i) != null)
                {
                    Log.d(TAG, "onItemClick: " + dbHelper.getWords(TABLENAME).get(i).getPath());
                    Toast.makeText(MainActivity.this, dbHelper.getWords(TABLENAME).get(i).getPath() , Toast.LENGTH_SHORT).show();
                    playStart(dbHelper.getWords(TABLENAME).get(i).getPath());
                }
            }
        });
        updateAdapter();
        button = (Button) findViewById(R.id.button1);
        langSpinner = findViewById(R.id.spinner);

        int requestCode = 5;
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);
    }

    public void recordStart(String fileName)
    {
        try {
            releaseRecorder();
            File outFile = new File(getApplicationContext().getFilesDir() + "/" + fileName);
            if (outFile.exists()) {
                outFile.delete();
            }
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(getApplicationContext().getFilesDir() + "/" + fileName);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void recordStop() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
        }
    }

    public void playStart(String fileName) {
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            Toast.makeText(this, getApplicationContext().getFilesDir() + "/" + fileName, Toast.LENGTH_SHORT).show();
            mediaPlayer.setDataSource(getApplicationContext().getFilesDir() + "/" + fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        releaseRecorder();
    }


    public void onSpeechButtonClicked(View v) {
        TextView txt = (TextView) this.findViewById(R.id.hello);
        String filename = new Date().toString();
        try {
            SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            String currentLang = langSpinner.getSelectedItem().toString();
            config.setSpeechRecognitionLanguage(currentLang);
            Log.d(TAG, "onSpeechButtonClicked: " + currentLang + "------" + config.getSpeechRecognitionLanguage());
            assert(config != null);
            SpeechRecognizer reco = new SpeechRecognizer(config);
            assert(reco != null);
            recordStart(Integer.toString(filename.hashCode()));
            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            assert(task != null);

            SpeechRecognitionResult result = task.get();
            assert(result != null);
            recordStop();
            if (result.getReason() == ResultReason.RecognizedSpeech)
            {
                String text = "";
                boolean flag = false;
                for (char c: result.toString().toCharArray())
                {
                    if (c == '>')
                    {
                        flag = false;
                        break;
                    }
                    if (flag == true)
                    {
                        text += c;
                    }
                    if (c == '<')
                        flag = true;
                }
                dbHelper.addWord(TABLENAME, new Note(text, filename));
                updateAdapter();
            }
            else {
                txt.setText("Error recognizing. Did you update the subscription info?" + System.lineSeparator() + result.toString());
            }

            reco.close();
            //recordStop();
            Log.d(TAG, Arrays.toString(dbHelper.getWords(TABLENAME).toArray()));
        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
           // assert(false);
        }
    }

    public void updateAdapter()
    {
        ArrayList<Note> arr;
        arrayAdapter.clear();
        arr = dbHelper.getWords(TABLENAME);
        if (arr != null)
        {
            for (Note note : arr) {
                arrayAdapter.add(note.date + " " + note.text);
            }
            lv.deferNotifyDataSetChanged();
        }
    }
}
