package com.agni.wifi_rss_record;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import android.net.wifi.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.CountDownTimer;

public class MainActivity extends Activity {

	WifiManager myWifiManager;
	String txt,txt1,place;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		Button btn = (Button) findViewById(R.id.button4);
		btn.setOnClickListener(new View.OnClickListener() {

	        @Override
	        public void onClick(View arg0) {
	            new CallServer().execute();
	        }
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void set_wifiOn(View view)
	{ 
		myWifiManager.setWifiEnabled(true);
	}
	
	
	public void set_wifiOff(View view)
	{ 
		myWifiManager.setWifiEnabled(false);
	}
	
	public void recordData(View view)
	{
		EditText editText = (EditText) findViewById(R.id.editText1);
		place=editText.getText().toString();
		final String state = Environment.getExternalStorageState();
		final String s1="00:26:3e:f5:9a:c2";
		final String s2="00:26:3e:f5:9a:c0";
		final String s3="00:26:3e:f5:9a:c4";
		final String s4="4c:60:de:fc:2e:9c";
		if(myWifiManager.isWifiEnabled())
		{ 
			new CountDownTimer(301000,1000)
			{
				int arr[][]=new int[4][100];
				int i=0;
				//String txt,txt1;
				TextView textView1 = (TextView) findViewById(R.id.textView1);
				public void onTick(long millisecsleft)
				{
					txt=""+(++i);
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
											arr[0][(scan.level)*-1]++;
										if(scan.BSSID.equals(s2))
											arr[1][(scan.level)*-1]++;
										if(scan.BSSID.equals(s3))
											arr[2][(scan.level)*-1]++;
										if(scan.BSSID.equals(s4))
											arr[3][(scan.level)*-1]++;
										//txt1="\n"+scan.SSID+"\t"+scan.BSSID+"\t"+scan.level;
										txt+="\n"+scan.SSID+"\t"+scan.BSSID+"\t"+scan.level;
										//fos.write(txt1.getBytes());
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
						//File file = new File("/storage/emulated/0/Agni_Wifi/");
						//file.mkdir();
						//file=new File("/storage/emulated/0/Agni_Wifi/",place+".xml");
						//FileOutputStream fos = new FileOutputStream(file,true);
						txt1="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\n<xmldata>";
						for(int i=0;i<100;i++)
						{
							txt1+="\n<value>\n<rss>"+(i*-1)+"</rss>";
							txt1+="\n<m1>"+(arr[0][i]/300.0)+"</m1>";
							txt1+="\n<m2>"+(arr[1][i]/300.0)+"</m2>";
							txt1+="\n<m3>"+(arr[2][i]/300.0)+"</m3>";
							txt1+="\n<m4>"+(arr[3][i]/300.0)+"</m4>";
							txt1+="\n</value>";
						}
							
						txt1+="</xmldata>\n";
						
						//fos.write(txt1.getBytes());
						textView1.setText(txt1);
						//fos.close();
						
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
	
	public class CallServer extends AsyncTask<String, Integer, String> {

	    @Override
	    protected String doInBackground(String... arg0) {
	        String modifiedSentence = "hello";
	        Socket clientSocket;
	        
	        EditText editText = (EditText) findViewById(R.id.editText1);
			place=editText.getText().toString();
			
	        EditText editText1 = (EditText) findViewById(R.id.editText2);
			String IpPort=editText1.getText().toString();
			String IP=IpPort.trim().substring(0,IpPort.length()-5);
			int port=Integer.parseInt(IpPort.trim().substring(IpPort.length()-4, IpPort.length()));
	        try {
	            clientSocket = new Socket(IP,port);


	            DataOutputStream dout = new DataOutputStream(
	                    clientSocket.getOutputStream());
	            DataInputStream din=new DataInputStream(clientSocket.getInputStream());

	            dout.writeUTF("SEND");
	                  
				 dout.writeUTF(place+".xml");
				 String msgFromServer=din.readUTF();
				 if(msgFromServer.compareTo("File Already Exists")==0)
				 {
					 dout.writeUTF("Y");
				 }
					 //FileInputStream fin=new FileInputStream(f);
					 int ch;
					 for(int i=0;i<txt1.length();i++)
					 {
	       	   			ch=txt1.charAt(i);
	       	   			dout.writeUTF(String.valueOf(ch));
					 }
					 dout.writeUTF("-1");
					 dout.writeUTF("DISCONNECT"); 
					 //fin.close();
					 din.close();
					 dout.close();
					 clientSocket.close();

	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return modifiedSentence;
	    }

	    protected void onPostExecute(String result) {
	        //txt.setText(result);
	    }
	}
	
}
