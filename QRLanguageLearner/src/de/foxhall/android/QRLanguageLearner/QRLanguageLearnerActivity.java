package de.foxhall.android.QRLanguageLearner;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


public class QRLanguageLearnerActivity extends Activity implements TextToSpeech.OnInitListener{
    private TextToSpeech mTts;
    private static final int MY_DATA_CHECK_CODE = 2109;
    private static final String TAG = "QRLanguageLearner";
	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Log.w(TAG,String.format("Hallo %s","Ciaran"));
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }

    public void SayIt(String whatToSay){
    	mTts.speak(whatToSay,TextToSpeech.QUEUE_FLUSH,null);
    }
    
    public void onInit(int i){
    	
    }

    public void ScanHandler(View v){
    	IntentIntegrator.initiateScan(this);
    }
    

    // Callback    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	switch(requestCode) {
    	  case MY_DATA_CHECK_CODE: {
    		  if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
              {
                  // success, create the TTS instance
                  mTts = new TextToSpeech(this, this);
              }
              else
              {
                  // missing data, install it
                  Intent installIntent = new Intent();
                  installIntent.setAction(
                          TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                  startActivity(installIntent);
              } 
    	  }
    	  case IntentIntegrator.REQUEST_CODE: {
    		
    		if (resultCode != RESULT_CANCELED) {
              IntentResult scanResult = 
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
    		  if (scanResult != null) {
    	        String upc = scanResult.getContents();
    	        try {
    	          AssetFileDescriptor afd = getAssets().openFd(String.format("%s.mp3",upc));
    	          MediaPlayer player = new MediaPlayer();
    	          player.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
    	          player.prepare();
    	          player.start();
    	          player.reset();
    	          player.release();
                  
    	        } catch (IOException e) {
    	        	Log.w(TAG,String.format("No asset match for %s",upc));
    	        	SayIt(upc);
    	        } catch (Exception e){
    	        	Log.e(TAG,e.toString());
    	        }
    	        Toast.makeText(this, upc, 3).show();
    	       
    	      }
    		  else {
    			  Log.w(TAG,"barcode scan failed - possibly because screen was rotated");
    			  //Toast.makeText(this, "Barcode scan failed!", 3).show();
    		  }
    	    }
    	    break;
    	  }
    	  
       }
      }

}