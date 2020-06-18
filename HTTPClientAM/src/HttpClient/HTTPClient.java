package HttpClient;
import java.io.*;
import java.net.Socket;

public class HTTPClient {
   
    private String userAgent;
    private String lastStatus;
    private static int SERVER_PORT = 80;
    private static String url = "www.riweb.tibeica.com";
    
    
    public HTTPClient(String userAgent)
    {
        this.userAgent = userAgent;
        
    }

    public boolean getResource(String localPath, String domainName, String host, int port) throws IOException
    {
        StringBuilder requestBuilder = new StringBuilder();
        //1.Construirea cererii HTTP care sa cuprinda:
        requestBuilder.append("GET " + localPath + " HTTP/1.1\r\n");
        requestBuilder.append("Host: " + domainName + "\r\n");
        requestBuilder.append("User-Agent: " + this.userAgent + "\r\n");
        
        // final cerere
        requestBuilder.append("\r\n");
        String requestString = requestBuilder.toString();
        System.out.println("Request:\n");
        System.out.println(requestString);
       
        
        //2.Deschiderea unei conexiuni TCP, port 80, catre hostul dorit
        Socket tcpSocket = new Socket(host, port);
        DataOutputStream outServer = new DataOutputStream(tcpSocket.getOutputStream());
        
        //3. Transmiterea cererii catre server, utilizând conexiunea stabilita anterior.
        BufferedReader inServer = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream())); 
        outServer.writeBytes(requestString);
        System.out.println("Request sent to " + host + ".");

        System.out.println("Response: \n");
        //4. Preluarea raspunsului HTTP oferit de catre server, urmat de închiderea conexiunii.
        String responseLine;
        boolean ok = false;
        responseLine = inServer.readLine();
        if (responseLine.contains("200 OK"))
        {
            ok = true;
        }
        lastStatus = responseLine;
        System.out.println(responseLine);

       
        //5.Prelucrarea raspunsului HTTP obtinut:
        if (ok == true) 
        {
        	 String regLine = "";
             while ((regLine = inServer.readLine()) != null)
             {
                 if (regLine.equals("")) //end of response headers
                 {
                     break;
                 }
                 System.out.println(regLine);
             }
            // parse response content
            StringBuilder pageBuilder = new StringBuilder();
            while ((regLine = inServer.readLine()) != null)
            {
                pageBuilder.append(regLine + System.lineSeparator());
            }

            //salvam intr-un fisier html
            String filePath ="site.html";
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(pageBuilder.toString());
            writer.close();
            System.out.println("\nCached at \"" + filePath + "\".");
        }
        tcpSocket.close();

        return true;
    }

    public String getLastStatus()
    {
        return lastStatus;
    }

    public static void main(String args[])
    {
        HTTPClient httpClient = new HTTPClient("CLIENT RIW");
        
        try
        {
            httpClient.getResource("/", url, "67.207.88.228", SERVER_PORT);
        }
        //În cazul în care resursa web dorita nu suporta metoda GET, se va genera un mesaj de atentionare
        catch (IOException ioe)
        {
            System.out.println("Eroare socket:");
            ioe.printStackTrace();
        }
    }
	
	

}
