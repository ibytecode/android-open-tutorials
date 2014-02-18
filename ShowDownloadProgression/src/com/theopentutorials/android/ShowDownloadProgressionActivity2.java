package com.theopentutorials.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.theopentutorials.android.adapters.CustomListViewAdapter;
import com.theopentutorials.android.adapters.RowItem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
         
public class ShowDownloadProgressionActivity2 extends Activity {
	ImageView imageView;
	ProgressDialog progressDialog;
 	CustomListViewAdapter listViewAdapter;
 	ListView listView;
          
	public static final String URL = "http://10.0.2.2:8080/images/open_tutorials_logo.png";
	public static final String URL1 = "http://10.0.2.2:8080/images/ibc_logo.png";
	public static final String URL2 = "http://10.0.2.2:8080/images/open_tutorials_logo.png";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main1);
        imageView = (ImageView) findViewById(R.id.thumbnail);
        listView = (ListView) findViewById(R.id.imageList);
        GetXMLTask task = new GetXMLTask(this);
		task.execute(new String[] { URL, URL1, URL2 });
		
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
    
    private class GetXMLTask extends AsyncTask<String, Integer, List<RowItem>> {
    	private Activity context;
    	List<RowItem> rowItems;
    	int noOfURLs;
		public GetXMLTask(Activity context) {
			this.context = context;
		}
		@Override
		protected List<RowItem> doInBackground(String... urls) {
			noOfURLs = urls.length;
			rowItems = new ArrayList<RowItem>();
			Bitmap map = null;
			for (String url : urls) {
				map = downloadImage(url);
				rowItems.add(new RowItem(map));
			}
			return rowItems;			
		}
  
		@Override
		protected void onPostExecute(List<RowItem> rowItems) {
			listViewAdapter = new CustomListViewAdapter(context, rowItems);
			listView.setAdapter(listViewAdapter);
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
			if(rowItems != null) {
				progressDialog.setMessage("Loading " + (rowItems.size()+1) + "/" + noOfURLs);
			}
	   }
	 
	}
}