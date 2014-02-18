package com.theopentutorials.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
         
public class ShowDownloadProgressionActivity extends Activity {
	ImageView imageView;
	ProgressDialog progressDialog;

	public static final String URL = "http://10.0.2.2:8080/images/open_tutorials_logo.png";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imageView = (ImageView) findViewById(R.id.thumbnail);
        GetXMLTask task = new GetXMLTask();
		task.execute(new String[] { URL });
		     
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("In progress...");
		progressDialog.setMessage("Loading...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setIndeterminate(false);   
		progressDialog.setMax(100);
		progressDialog.setIcon(R.drawable.arrow_stop_down);
		progressDialog.setCancelable(true);
		progressDialog.show();
    }
    
    private class GetXMLTask extends AsyncTask<String, Integer, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... urls) {
			Bitmap map = null;
			for (String url : urls) {
				map = downloadImage(url);
			}
			return map;			
		}
  
		@Override
		protected void onPostExecute(Bitmap result) {
			imageView.setImageBitmap(result);
			progressDialog.dismiss();
		}	
		
		private Bitmap downloadImage(String urlString) {
			
			int count = 0;
			Bitmap bitmap = null;

			URL url;
			try {
				url = new URL(urlString);
				URLConnection connection = url.openConnection();
		        int lenghtOfFile = connection.getContentLength();
		       
		        InputStream input = new BufferedInputStream(url.openStream());
		        ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
		        BufferedOutputStream out = null;
		        out = new BufferedOutputStream(dataStream);
		        
		        byte data[] = new byte[512];        
		        long total = 0;

		        while ((count = input.read(data)) != -1) {
		            total += count;
		            // publishing the progress....
		            // After this onProgressUpdate will be called
		            publishProgress((int)((total*100)/lenghtOfFile));

		            // writing data to file
		            out.write(data, 0, count);
		        }

		        // flushing output
		        out.flush();

		        // closing streams
		        out.close();
		        input.close();
				
				BitmapFactory.Options bmOptions = new BitmapFactory.Options();
				bmOptions.inSampleSize = 1;
				
				byte[] bytes = dataStream.toByteArray();
		        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,bmOptions);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return bitmap;
		}
		
		protected void onProgressUpdate(Integer... progress) {
	        // setting progress percentage
			progressDialog.setProgress(progress[0]);
	   }
	 
	}
}