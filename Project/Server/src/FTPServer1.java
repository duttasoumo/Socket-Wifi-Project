		//***FTPServer***/
		import java.net.*;
		import java.io.*;
		import java.util.*;
		import java.sql.*;
		
		public class FTPServer1
		{
	         public static void main(String args[]) throws Exception
                  {
			ServerSocket soc=new ServerSocket(5217);
		   System.out.println("FTP Server Started on port number 5217");
                   while(true)
                    {
                      System.out.println("Waiting for connection....");
                      transferfile t=new transferfile(soc.accept());
                    }
	           }
                  }
                 class transferfile extends Thread
                 {
                    Socket ClientSoc;
                    DataInputStream din;
                    DataOutputStream dout;
                    int a[];
                    transferfile(Socket soc)
                     {
                      try
                        {
                    	  a=new int[4];
                         ClientSoc=soc;
                         din=new DataInputStream(ClientSoc.getInputStream());
                         dout=new DataOutputStream(ClientSoc.getOutputStream());
                         System.out.println("FTP Client Connected.......");
                         start();
                        }
                       catch(Exception ex){}
                     }
                           
                       void ReceiveFile() throws Exception
                         {
                               	int i=0;
                                 int ch;
                                 String temp="";
                                 do
                                 {
                                   	ch=Integer.parseInt(din.readUTF());
                                   	if(ch!=-1) temp+=(char)ch;
                                   	//System.out.println(ch+" "+temp);
                                   	if(ch==32)
                                   	{
                                   		a[i++]=Integer.parseInt(temp.trim());
                                   		temp="";
                                   	}
                                }while(ch!=-1);
                       }


                       public void run()
                       {
                            try
                            {
                              System.out.println("Waiting for Command....");
                              String Command=din.readUTF();
                             if(Command.compareTo("SEND")==0)
                              {
                              	System.out.println("\tSEND Command Received....");
                               	ReceiveFile();
				give_loc();
				}
                            }
                              catch(Exception ex)
                               {
                               }
                       }

			public void give_loc()
			{
				String tab_name;
				double prb;
				double p[]=new double[100];
				String n[]=new String[100];
				//a[0]=-45;a[1]=-60;a[2]=-55;a[3]=-32;
				int i,j,k=0;
				try{
    					Class.forName("com.mysql.jdbc.Driver");
					Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/wifi","root","1234");
    					
					DatabaseMetaData md = con.getMetaData();
					ResultSet rs = md.getTables(null, null, "%", null);
					j=0;
					
					while (rs.next())
					{
						prb=1;
						tab_name=rs.getString(3);
						System.out.println(tab_name);
						for(i=0; i<4; i++)
						{
							PreparedStatement st = con.prepareStatement("select m"+(i+1)+" from "+tab_name+" where rss="+a[i]);
					    		ResultSet r1=st.executeQuery();
							if(r1.next())
							{
							     if(Double.parseDouble(r1.getString("m"+(i+1)))!=0){  prb*= Double.parseDouble(r1.getString("m"+(i+1)));
								System.out.println(Double.parseDouble(r1.getString("m"+(i+1))));}
 
      							}
						}
						if(prb==1) prb=0;
						System.out.println(prb);
						p[j]=prb;
						n[j++]=tab_name;
					}

					prb=p[0];
					for(i=1;i<j;i++)
						if(p[i]>prb)
						{
							prb=p[i];
							k=i;
						}
					dout.writeUTF(n[k]);
					System.out.println(n[k]);
				}
				catch(Exception e){System.out.println(e);}
				System.exit(0);
			}
}