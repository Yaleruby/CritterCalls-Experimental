package com.example.crittercalls;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioRecord;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.audio.TensorAudio;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.audio.classifier.AudioClassifier;
import org.tensorflow.lite.task.audio.classifier.Classifications;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

public class AudioHelperActivity extends ClassificationActivity {
    private AudioClassifier audioClassifier;

    private static final String MODEL_FILE = "yamnet_classification.tflite";
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final float MINIMUM_DISPLAY_THRESHOLD = 0.3f;
    private AudioRecord audioRecord;
    private TensorAudio audioTensor;
    private String[] animals = {"dog", "cat", "rooster", "pig", "cow", "frog", "hen", "insects", "sheep", "crow", "chirping_birds"};
    private String wavFilePath;  // Path to the WAV file
    private int bufferSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        HandlerThread handlerThread = new HandlerThread("backgroundThread");
//        handlerThread.start();
//        mHandler = HandlerCompat.createAsync(handlerThread.getLooper());

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);


        // Loading the model from the assets folder
        try {
            audioClassifier = AudioClassifier.createFromFile(this, MODEL_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        audioTensor = audioClassifier.createInputTensorAudio();

        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        Python py = Python.getInstance();
        PyObject pyObject = py.getModule("hello");

        PyObject obj = pyObject.callAttr("main");

        outputTextView.setText(obj.toString());


//        try {
//            AnimalSoundModel model = AnimalSoundModel.newInstance(context);
//
//            // Creates inputs for reference.
//            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1}, DataType.FLOAT32);
//            inputFeature0.loadBuffer(byteBuffer);
//
//            // Runs model inference and gets result.
//            AnimalSoundModel.Outputs outputs = model.process(inputFeature0);
//            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//            // Releases model resources if no longer used.
//            model.close();
//        } catch (IOException e) {
//            // TODO Handle the exception
//        }
//
//        try {
//            YamnetClassification model = YamnetClassification.newInstance(context);
//
//            // Creates inputs for reference.
//            TensorBuffer audioClip = TensorBuffer.createFixedSize(new int[]{15600}, DataType.FLOAT32);
//            audioClip.loadBuffer(byteBuffer);
//
//            // Runs model inference and gets result.
//            YamnetClassification.Outputs outputs = model.process(audioClip);
//            TensorBuffer scores = outputs.getScoresAsTensorBuffer();
//
//            // Releases model resources if no longer used.
//            model.close();
//        } catch (IOException e) {
//            // TODO Handle the exception
//        }

    }
    @Override
    public void onStartRecording(View view) {
        // Check for audio recording permissions
        if (checkAudioPermission()) {
            // Permission is already granted, proceed with your audio recording logic
            startRecordingButton.setEnabled(false);
            stopRecordingButton.setEnabled(true);
            startRecording();
        } else {
            // Permission has not been granted yet, request it
            requestAudioPermission();
            showMessage("Requesting Permissions");
        }

//        if(mAudioClassifier != null) return;
//
//        try {
//            AudioClassifier classifier = AudioClassifier.createFromFile(this, MODEL_FILE);
//            TensorAudio audioTensor = classifier.createInputTensorAudio();
//
//            AudioRecord record = classifier.createAudioRecord();
//            record.startRecording();
//
//            Runnable run = new Runnable() {
//                @Override
//                public void run() {
//                    audioTensor.load(record);
//                    List<Classifications> output = classifier.classify(audioTensor);
//                    List<Category> filterModelOutput = output.get(0).getCategories();
//                    for(Category c : filterModelOutput) {
//                        if (c.getScore() > MINIMUM_DISPLAY_THRESHOLD)
//                            outputTextView.setText(" label : " + c.getLabel() + " score : " + c.getScore());
////                            Log.d("tensorAudio_java", " label : " + c.getLabel() + " score : " + c.getScore());
//                    }
//
//                    mHandler.postDelayed(this,classficationInterval);
//                }
//            };
//
//            mHandler.post(run);
//            mAudioClassifier = classifier;
//            mAudioRecord = record;
//        }catch (IOException e){
//            e.printStackTrace();
//        }

//        // showing the audio recorder specification
//        TensorAudio.TensorAudioFormat format = audioClassifier.getRequiredTensorAudioFormat();
//        String specs = "Number of channels: " + format.getChannels() + "\n"
//                + "Sample Rate: " + format.getSampleRate();
//        specsTextView.setText(specs);
//
//        // Creating and start recording
//        audioRecord = audioClassifier.createAudioRecord();
//        audioRecord.startRecording();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                // Classifying audio data
//                List<Classifications> output = audioClassifier.classify(tensorAudio);
//
//                // Filtering out classifications with low probability
//                List<Category> finalOutput = new ArrayList<>();
//                for (Classifications classifications : output) {
//                    for (Category category : classifications.getCategories()) {
//                        if (category.getScore() > probabilityThreshold) {
//                            finalOutput.add(category);
//                        }
//                    }
//                }
//                // Sorting the results
//                Collections.sort(finalOutput, (o1, o2) -> (int) (o1.getScore() - o2.getScore()));
//
//                // Creating a multiline string with the filtered results
//                StringBuilder outputStr = new StringBuilder();
//                for (Category category : finalOutput) {
//                    outputStr.append(category.getLabel())
//                            .append(": ").append(category.getScore()).append("\n");
//                }
//                // Updating the UI
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (finalOutput.isEmpty()) {
//                            outputTextView.setText("Could not classify");
//                        } else {
//                            outputTextView.setText(outputStr.toString());
//                        }
//                    }
//                });
//            }
//        };
//        new Timer().scheduleAtFixedRate(timerTask, 1, 500);
    }

    private void startRecording() {
        // Start recording
        AudioRecord audioRecord = audioClassifier.createAudioRecord();
        audioRecord.startRecording();
        this.audioRecord = audioRecord;
    }
    @Override
    public void onStopRecording(View view) {
        startRecordingButton.setEnabled(true);
        stopRecordingButton.setEnabled(false);

        // Stop recording and release resources
        audioRecord.stop();
        classifyAudio();

        audioRecord.release();

        // Set the path for the WAV file
        wavFilePath = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath() + "/recorded_audio.wav";



//        mHandler.removeCallbacksAndMessages(null);
//        mAudioRecord.stop();
//        mAudioRecord = null;
//        mAudioClassifier = null;

//        timerTask.cancel();
//        audioRecord.stop();
    }

    private void classifyAudio() {
        audioTensor.load(audioRecord);

        List<Classifications> output = audioClassifier.classify(audioTensor);

        // Filtering out classifications with low probability
        List<Category> finalOutput = new ArrayList<>();
        for (Classifications classifications : output) {
            for (Category category : classifications.getCategories()) {
                if (category.getScore() > MINIMUM_DISPLAY_THRESHOLD) {
                    for (String animal : animals) {
                        if (category.getLabel().equals(animal)) {
                            finalOutput.add(category);
                        }
                    }
                }
            }
        }

//        // Filtering out classifications with low probability
//        List<Category> finalOutput = new ArrayList<>();
//        for (Classifications classifications : output) {
//            for (Category category : classifications.getCategories()) {
//                if (category.getScore() > MINIMUM_DISPLAY_THRESHOLD) {
//                    finalOutput.add(category);
//                }
//            }
//        }

        // Sorting the results
        Collections.sort(finalOutput, (o1, o2) -> (int) (o1.getScore() - o2.getScore()));

        // Creating a multiline string with the filtered results
        StringBuilder outputStr = new StringBuilder();
        for (Category category : finalOutput) {
            outputStr.append(category.getLabel())
                    .append(": ").append(category.getScore()).append("\n");
        }

        if (finalOutput.isEmpty()) {
            outputTextView.setText("Could not classify");
        } else {
            outputTextView.setText(outputStr.toString());
        }



//        List<Classifications> output = audioClassifier.classify(audioTensor);
//        List<Category> filterModelOutput = output.get(0).getCategories();
//        for(Category c : filterModelOutput) {
//            if (c.getScore() > MINIMUM_DISPLAY_THRESHOLD)
//                outputTextView.setText(" label : " + c.getLabel() + " score : " + c.getScore());
//            else
//                outputTextView.setText("Could not classify");
//        }
    }

    private void saveAudioToWav() {
        try {
            // Create a File object for the WAV file
            File wavFile = new File(wavFilePath);

            // Create a FileOutputStream to write the audio data to the file
            FileOutputStream fos = new FileOutputStream(wavFile);

            // Get the audio data from the AudioRecord
            byte[] audioData = new byte[bufferSize];  // Adjust bufferSize to your needs
            audioRecord.read(audioData, 0, bufferSize);

            // Write the WAV header
            WaveHeader waveHeader = new WaveHeader(audioData.length);
            fos.write(waveHeader.getHeader(), 0, WaveHeader.HEADER_SIZE);

            // Write the audio data to the file
            fos.write(audioData, 0, audioData.length);

            // Close the FileOutputStream
            fos.close();

            showMessage("Audio saved to: " + wavFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            showMessage("Error saving audio to WAV file");
        }
    }

    // WaveHeader class to create a WAV file header
    class WaveHeader {
        static final int HEADER_SIZE = 44;
        private int totalDataLen;
        private int channels;
        private int sampleRate;
        private int byteRate;
        private int blockAlign;
        private int bitsPerSample;

        public WaveHeader(int totalAudioLen) {
            this.totalDataLen = totalAudioLen + HEADER_SIZE - 8;
            this.channels = 1;  // Mono audio
            this.sampleRate = 44100;  // Adjust sample rate to your needs
            this.bitsPerSample = 16;  // Adjust bits per sample to your needs

            this.byteRate = this.sampleRate * this.channels * this.bitsPerSample / 8;
            this.blockAlign = this.channels * this.bitsPerSample / 8;
        }

        public byte[] getHeader() {
            byte[] header = new byte[HEADER_SIZE];

            // ChunkID (RIFF)
            header[0] = 'R';
            header[1] = 'I';
            header[2] = 'F';
            header[3] = 'F';

            // ChunkSize
            header[4] = (byte) (totalDataLen & 0xff);
            header[5] = (byte) ((totalDataLen >> 8) & 0xff);
            header[6] = (byte) ((totalDataLen >> 16) & 0xff);
            header[7] = (byte) ((totalDataLen >> 24) & 0xff);

            // Format (WAVE)
            header[8] = 'W';
            header[9] = 'A';
            header[10] = 'V';
            header[11] = 'E';

            // Subchunk1ID (fmt)
            header[12] = 'f';
            header[13] = 'm';
            header[14] = 't';
            header[15] = ' ';

            // Subchunk1Size
            header[16] = 16;  // Subchunk1Size is always 16 for PCM
            header[17] = 0;
            header[18] = 0;
            header[19] = 0;

            // AudioFormat (PCM)
            header[20] = 1;
            header[21] = 0;

            // NumChannels
            header[22] = (byte) channels;
            header[23] = 0;

            // SampleRate
            header[24] = (byte) (sampleRate & 0xff);
            header[25] = (byte) ((sampleRate >> 8) & 0xff);
            header[26] = (byte) ((sampleRate >> 16) & 0xff);
            header[27] = (byte) ((sampleRate >> 24) & 0xff);

            // ByteRate
            header[28] = (byte) (byteRate & 0xff);
            header[29] = (byte) ((byteRate >> 8) & 0xff);
            header[30] = (byte) ((byteRate >> 16) & 0xff);
            header[31] = (byte) ((byteRate >> 24) & 0xff);

            // BlockAlign
            header[32] = (byte) blockAlign;
            header[33] = 0;

            // BitsPerSample
            header[34] = (byte) bitsPerSample;
            header[35] = 0;

            // Subchunk2ID (data)
            header[36] = 'd';
            header[37] = 'a';
            header[38] = 't';
            header[39] = 'a';

            // Subchunk2Size
            header[40] = (byte) (totalDataLen - HEADER_SIZE + 8);
            header[41] = 0;
            header[42] = 0;
            header[43] = 0;

            return header;
        }
    }

    private boolean checkAudioPermission() {
        // Check if the permission has been granted
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        // Request audio recording permission
        requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);
    }



    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your audio recording logic
                startRecordingButton.setEnabled(false);
                stopRecordingButton.setEnabled(true);
                startRecording();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    // First time user denied microphone access, explain the importance of the permission
                    // and ask again
                    showPermissionExplanationDialog();
                } else {
                    // Open the app settings to let the user grant the permission manually
                    showAppSettingsRedirect();
                }
                showMessage("Record Audio Permissions Denied");
            }
        }
    }

    private void showAppSettingsRedirect() {
        new AlertDialog.Builder(this)
                .setTitle("Allow Permissions")
                .setMessage("Manually grant the permission.")
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Request the permission again
                        openAppSettings();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPermissionExplanationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permission Needed")
                .setMessage("The app needs audio recording permission to function properly. Please grant the permission.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Request the permission again
                        requestAudioPermission();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        ActivityCompat.startActivityForResult(this, intent, REQUEST_RECORD_AUDIO, null);
    }
    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
