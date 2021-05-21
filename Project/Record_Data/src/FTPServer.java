		//***FTPServer***/
		import java.net.*;
		import java.io.*;
		import java.util.*;
		import java.sql.*;
		import org.w3c.dom.*;
		import javax.xml.parsers.*;
		
		public class FTPServer
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
		    String filename;
                    transferfile(Socket soc)
                     {
                      try
                        {
                         ClientSoc=soc;
                         din=new DataInputStream(ClientSoc.getInputStream());
                         dout=new DataOutputStream(ClientSoc.getOutputStream());
                         System.out.println("FTP Client Connected.......");
                         start();
                        }
                       catch(Exception ex)
                        {
 
                        }
                     }
                       
                           
                       void ReceiveFile() throws Exception
                              {
                               filename=din.readUTF();
                               if(filename.compareTo("File not found")==0)
                               {
                                    return;
                               }
                               File f= new File(filename);
                               String option;
                               if(f.exists())
                               {
                                dout.writeUTF("File Already Exists");
                                option=din.readUTF();
                               }
                               else
                               {
                                 dout.writeUTF("SendFile");
                                 option="Y";
                               }
                               if(option.compareTo("Y")==0)
                               {
                                 FileOutputStream fout=new FileOutputStream(f);
                                 int ch;
                                 String temp;
                                 do
                                 {
                                   temp=din.readUTF();
                                   ch=Integer.parseInt(temp);
                                   if(ch!=-1)
                                   {
                                     fout.write(ch);
                                   }
                                  }while(ch!=-1);
                                   fout.close();
                                   dout.writeUTF("File Send Successfully");
                                  }
                                else
                                {
                                  return;
                                }
				InsertXMLData();
                         }
				
			public void InsertXMLData()
			{
				
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/wifi", "root", "1234");
				Statement st=con.createStatement();
				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
				String filename1=filename.substring(0,filename.length()-4);
				 String sql = "CREATE TABLE  " + filename1 +
                   				"(rss VARCHAR(20) not NULL, " +
                 		        	  " m1 VARCHAR(50), " + 
   				                  " m2 VARCHAR(50), " + 
                   			        	" m3 VARCHAR(50), " + 
							" m4 VARCHAR(50), " + 
                   					" PRIMARY KEY ( rss ))"; 

				      st.executeUpdate(sql);
				    System.out.println("Here");
				Document doc = docBuilder.parse (new File(filename));
				System.out.println("Here1");
				doc.getDocumentElement().normalize();
				System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());
				NodeList listOfPersons = doc.getElementsByTagName("value");
				for(int s=0; s<listOfPersons.getLength(); s++)
				{

					Node firstPersonNode = listOfPersons.item(s);
					if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE)
					{
						Element firstPersonElement = (Element)firstPersonNode;
						NodeList nameList = firstPersonElement.getElementsByTagName("rss");
						Element nameElement =(Element)nameList.item(0);

						NodeList textFNList = nameElement.getChildNodes();
						String rss=((Node)textFNList.item(0)).getNodeValue().trim();

						NodeList addressList = firstPersonElement.getElementsByTagName("m1");
						Element addressElement =(Element)addressList.item(0);

						NodeList textLNList = addressElement.getChildNodes();
						String m1= ((Node)textLNList.item(0)).getNodeValue().trim();

						NodeList addressList1 = firstPersonElement.getElementsByTagName("m2");
						Element addressElement1 =(Element)addressList1.item(0);

						NodeList textLNList1 = addressElement1.getChildNodes();
						String m2= ((Node)textLNList1.item(0)).getNodeValue().trim();

						NodeList addressList2 = firstPersonElement.getElementsByTagName("m3");
						Element addressElement2 =(Element)addressList2.item(0);

						NodeList textLNList2 = addressElement2.getChildNodes();
						String m3= ((Node)textLNList2.item(0)).getNodeValue().trim();

						NodeList addressList3 = firstPersonElement.getElementsByTagName("m4");
						Element addressElement3 =(Element)addressList3.item(0);

						NodeList textLNList3 = addressElement3.getChildNodes();
						String m4= ((Node)textLNList3.item(0)).getNodeValue().trim();

						int i=st.executeUpdate("insert into wifi."+filename1+"(rss,m1,m2,m3,m4) values('"+rss+"','"+m1+"','"+m2+"','"+m3+"','"+m4+"')");
					}
				}
				System.out.println("Data is successfully inserted!");
				System.exit(0);
			   }
			    catch (Exception err) 
				{
					System.out.println(err);
				}
			System.exit(0);

			}
	
                       public void run()
                       {
                         while(true)
                         {
                           try
                            {
                              System.out.println("Waiting for Command....");
                              String Command=din.readUTF();
                              if(Command.compareTo("SEND")==0)
                              {
                               System.out.println("\tSEND Command Received....");
                               ReceiveFile();
                               continue;
                               }

                              else if(Command.compareTo("DISCONNECT")==0)
                               {
                                System.out.println("\tDisconnect Command Received....");
                                System.exit(1);
                               }

                               }
                              catch(Exception ex)
                               {
                               }
                       }
                       }
}

                           
                              
                                    
     
                                                                



				



































