package com.agni.filetrans;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	WifiManager myWifiManager;
	String txt,s1,s2,s3,s4,state,txt1;
	View v;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    myWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    Button btn = (Button) findViewById(R.id.button1);
	    btn.setOnClickListener(new View.OnClickListener() {
	    	
	        @Override
	        public void onClick(View arg0) {
	            new CallServer().execute();
	        }
	    });
	}

	public void recordData(View view)
	{
		v=view;
		final String state = Environment.getExternalStorageState();
		final String s1="00:26:3e:f5:9a:c2";
		final String s2="00:26:3e:f5:9a:c0";
		final String s3="00:26:3e:f5:9a:c4";
		final String s4="4c:60:de:fc:2e:9c";
		if(myWifiManager.isWifiEnabled())
		{ 
			new CountDownTimer(30100,1000)
			{
				int arr[]=new int[4];
				int i=31;
				//String txt,txt1;
				TextView textView1 = (TextView) findViewById(R.id.textView1);
				public void onTick(long millisecsleft)
				{
					txt=""+(--i);
					if(myWifiManager.startScan())
					{
						// List available APs
						List<ScanResult> scans = myWifiManager.getScanResults(); 
						if(scans != null && !scans.isEmpty())
						{
							if (Environment.MEDIA_MOUNTED.equals(state)) {
								try{
									
									for (ScanResult scan : scans)
									{
										if(scan.BSSID.equals(s1))
											arr[0]+=scan.level;
										if(scan.BSSID.equals(s2))
											arr[1]+=scan.level;
										if(scan.BSSID.equals(s3))
											arr[2]+=scan.level;
										if(scan.BSSID.equals(s4))
											arr[3]+=scan.level;
									}
									
									textView1.setText(txt);
								}
								catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				public void onFinish(){
					try{
						txt1=(arr[0]/30)+" "+(arr[1]/30)+" "+(arr[2]/30)+" "+(arr[3]/30)+" ";
						textView1.setText(txt1);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	
	public class CallServer extends AsyncTask<String, Integer, String> {

		Socket clientSocket;
		 DataOutputStream  dout;
		 DataInputStream din;
		@Override
	    protected String doInBackground(String... arg0) {
	        String modifiedSentence = "hello";
	        
	        EditText editText = (EditText) findViewById(R.id.editText1);
			String IpPort=editText.getText().toString();
			String IP=IpPort.trim().substring(0,IpPort.length()-5);
			int port=Integer.parseInt(IpPort.trim().substring(IpPort.length()-4, IpPort.length()));
	        try {
	            clientSocket = new Socket(IP,port);


	           dout = new DataOutputStream(clientSocket.getOutputStream());
	           din=new DataInputStream(clientSocket.getInputStream());

	            dout.writeUTF("SEND");
					 //FileInputStream fin=new FileInputStream(f);
					 int ch;
					 for(int i=0;i<txt1.length();i++)
					 {
	       	   			ch=txt1.charAt(i);
	       	   			dout.writeUTF(String.valueOf(ch));
					 }
					 dout.writeUTF("-1");
					 show_data(v);
					 dout.writeUTF("DISCONNECT");
					 //fin.close();
					 din.close();
					 dout.close();
					 clientSocket.close();

	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            System.out.println(e);
	        }
	        return modifiedSentence;
	    }
	    
	    public void show_data(View view)
	    {
	    	try{
	    		String s=din.readUTF();
	    	TextView textView1 = (TextView) findViewById(R.id.textView1);
			 textView1.setText(s);
	    	}
	    	catch(Exception e){}
	    }
	    protected void onPostExecute(String result) {
	        //txt.setText(result);
	    }
	}

}
