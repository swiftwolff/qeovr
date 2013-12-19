package com.example.qeovr;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	TextView etResponse;
	TextView tvIsConnected;
	EditText url =null;
	private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;
//    private String url = null;
    
    private MediaRecorder mRecorder = null;

//    private UploadFileButton mUploadButton = null;
    private MediaPlayer   mPlayer = null;
    
    public MainActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.wav";
//        url = "http://10.0.23.87:3000/send_audio";
    }
    
    //Methods for recording
    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// get reference to the views
		etResponse = (TextView) findViewById(R.id.etResponse);
		tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
		url = (EditText) findViewById(R.id.ipaddress);
		url.setText("http://");
		// check if you are connected or not
		if(isConnected()){
			tvIsConnected.setBackgroundColor(0xFF00CC00);
			tvIsConnected.setText("You are conncted");
			
		}else{
			tvIsConnected.setText("You are NOT conncted");
		}
		
		// show response on the EditText etResponse
		
		
//		new HttpGetAsyncTask().execute("http://www.google.com");
		
		final Button checkbutton = (Button) findViewById(R.id.checkButton);
		
		checkbutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new HttpGetAsyncTask().execute(url.getText().toString());
			}
		});
				
		
		final Button playbutton = (Button) findViewById(R.id.playButton);
		//Play Button
		playbutton.setOnClickListener(new View.OnClickListener() {
			boolean mStartPlaying = true;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onPlay(mStartPlaying);
                if (mStartPlaying) {
                   playbutton.setText("Stop playing");
                } else {
                    playbutton.setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
				
			}
		});
		
		//Record Button
		final Button recordbutton = (Button) findViewById(R.id.recordButton);
		
		recordbutton.setOnClickListener(new View.OnClickListener() {
			boolean mStartRecording = true;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 onRecord(mStartRecording);
	                if (mStartRecording) {
	                    recordbutton.setText("Stop recording");
	                } else {
	                    recordbutton.setText("Start recording");
	                }
	                mStartRecording = !mStartRecording;
			}
		});
		
		//Upload Button
				final Button uploadbutton = (Button) findViewById(R.id.uploadButton);
				
				uploadbutton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						new HttpPostAsyncTask(mFileName).execute(url.getText().toString()+"/send_audio");
					}
				});
		
	}
	public static String POST(String url, String filepath){
		String result = "";
		HttpURLConnection conn = null;
	    DataOutputStream dos = null;
	    BufferedReader inStream = null;
	    String existingFileName = filepath;
	    String lineEnd = "\r\n";
	    String twoHyphens = "--";
	    String boundary = "*****";
	    int bytesRead, bytesAvailable, bufferSize;
	    byte[] buffer;
	    int maxBufferSize = 1 * 1024 * 1024;
	    String responseFromServer = "";
	    try {

	        //------------------ CLIENT REQUEST
	        FileInputStream fileInputStream = new FileInputStream(new File(existingFileName));
	        // open a URL connection to the Servlet
	        URL posturl = new URL(url);
	        // Open a HTTP connection to the URL
	        conn = (HttpURLConnection) posturl.openConnection();
	        // Allow Inputs
	        conn.setDoInput(true);
	        // Allow Outputs
	        conn.setDoOutput(true);
	        // Don't use a cached copy.
	        conn.setUseCaches(false);
	        // Use a post method.
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Connection", "Keep-Alive");
	        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
	        dos = new DataOutputStream(conn.getOutputStream());
	        dos.writeBytes(twoHyphens + boundary + lineEnd);
	        dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + existingFileName + "\"" + lineEnd);
	        dos.writeBytes(lineEnd);
	        // create a buffer of maximum size
	        bytesAvailable = fileInputStream.available();
	        bufferSize = Math.min(bytesAvailable, maxBufferSize);
	        buffer = new byte[bufferSize];
	        // read file and write it into form...
	        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

	        while (bytesRead > 0) {

	            dos.write(buffer, 0, bufferSize);
	            bytesAvailable = fileInputStream.available();
	            bufferSize = Math.min(bytesAvailable, maxBufferSize);
	            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

	        }

	        // send multipart form data necesssary after file data...
	        dos.writeBytes(lineEnd);
	        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	        // close streams
	        Log.e("Debug", "File is written");
	        fileInputStream.close();
	        dos.flush();
	        dos.close();

	    } catch (MalformedURLException ex) {
	        Log.e("Debug", "error: " + ex.getMessage(), ex);
	    } catch (IOException ioe) {
	        Log.e("Debug", "error: " + ioe.getMessage(), ioe);
	    }

	    //------------------ read the SERVER RESPONSE
	    try {
	    	inStream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//	        inStream = new DataInputStream(conn.getInputStream());
	        String str;

	        while ((str = inStream.readLine()) != null) {

	            Log.e("Debug", "Server Response " + str);
	          
	        }

	        inStream.close();

	    } catch (IOException ioex) {
	        Log.e("Debug", "error: " + ioex.getMessage(), ioex);
	    }
		
		return result;
	}
	public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
 
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
 
            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();
            
            // convert inputstream to string
            if(inputStream != null)
//                result = convertInputStreamToString(inputStream);
            	result = "Response Success Status code: " + httpResponse.getStatusLine().getStatusCode();
//            	result = "You got a response from the server!";
            else
            	result = "Something went wrong";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        return result;
    }
	
	// convert inputstream to String
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
    
 // check network connection
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) 
                return true;
            else
                return false;   
    }
    
    private class HttpGetAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
 
            return GET(urls[0]);
//            Log.i("success","Yes!");
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_SHORT).show();
            etResponse.setText(result);
       }
    }
    
    private class HttpPostAsyncTask extends AsyncTask<String, Void, String> {
    	private String filepath;
    	
    	public HttpPostAsyncTask(String filepath){
    		this.filepath = filepath;
    	}
        @Override
        protected String doInBackground(String... urls) {
 
//            return "Hey there!";
            return POST(urls[0],filepath);
//            Log.i("success","Yes!");
        }
        
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Sent!", Toast.LENGTH_SHORT).show();
            etResponse.setText(result);
       }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
		
	

}
