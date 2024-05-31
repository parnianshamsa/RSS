import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

public class RSSReader {
    private static final int MAX_ITEMS =5 ;
    static File file=new File("data.txt");
    private static String toString(InputStream inputStream) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream , "UTF-8"));
        String inputLine;
        StringBuilder stringBuilder = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null)
            stringBuilder.append(inputLine);
        return stringBuilder.toString();
    }

    public static String fetchPageSource(String urlString) throws Exception
    {
        URI uri = new URI(urlString);
        URL url = uri.toURL();
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML , like Gecko) Chrome/108.0.0.0 Safari/537.36");
        return toString(urlConnection.getInputStream());
    }

    public static String extractPageTitle(String html)
    {
        try
        {
            Document doc = Jsoup.parse(html);
            return doc.select("title").first().text();
        }
        catch (Exception e)
        {
            return "Error: no title tag found in page source!";
        }
    }

    public static String extractRssUrl(String url) throws IOException
    {
        Document doc = Jsoup.connect(url).get();
        return doc.select("[type='application/rss+xml']").attr("abs:href");
    }

    public static void retrieveRssContent(String rssUrl)
    {
        try {
            String rssXml = fetchPageSource(rssUrl);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            StringBuilder xmlStringBuilder = new StringBuilder();
            xmlStringBuilder.append(rssXml);
            ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(input);
            NodeList itemNodes = doc.getElementsByTagName("item");

            for (int i = 0; i < MAX_ITEMS; ++i) {
                Node itemNode = itemNodes.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) itemNode;
                    System.out.println("Title: " + element.getElementsByTagName("title").item(0).getTextContent());
                    System.out.println("Link: " + element.getElementsByTagName("link").item(0).getTextContent());
                    System.out.println("Description: " + element.getElementsByTagName("description").item(0).getTextContent());
                }
            }
        }
        catch (Exception e)
        {
            System.out.

                    println("Error in retrieving RSS content for "+rssUrl +": "+e.getMessage());
        }
    }


    public static void main(String[] args) throws Exception {
        Scanner scanner=new Scanner(System.in);
        System.out.println("Welcome to RSS Reader!");
        System.out.println("Type a valid number for your desired action :\n[1] show updates\n[2] Add URL\n[3] Remove URL\n[4] Exit");
        int s=0;

        while( s!=4) {
            s=scanner.nextInt();
            if (s == 1) {
                showupdate();
                if (!f){
                    break;
                }
                break;
            }
            if (s == 2) {
                addurl();
            }
            if (s == 3) {
                remoeurl();
            }
            System.out.println("Type a valid number for your desired action :\n[1] show updates\n[2] Add URL\n[3] Remove URL\n[4] Exit");

        }

    }

    public static void addurl() throws Exception {

        FileWriter writer = new FileWriter(file,true);
        BufferedWriter bufferedWriter=new BufferedWriter(writer);
        Scanner scanner = new Scanner(file);

        Scanner scanner1=new Scanner(System.in);
        System.out.println("please enter website URL to add:");
        String url = scanner1.nextLine();
        boolean found=false;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.contains(url)) {
                found = true;
                break;
            }
        }
        scanner.close();
        if (found) {
            System.out.println(url+"\nalready exists");
        }
        else {
            String S= fetchPageSource(url);
            String title= extractPageTitle(S);
            String rss=extractRssUrl(url);
            bufferedWriter.write(title+";"+url+";"+rss+"\n");
            bufferedWriter.close();
            System.out.println("Added\t"+url+"\tsuccessfully");
        }

    }
    static boolean f = true;
    public static void showupdate() throws Exception {
        Scanner scanner = new Scanner(file);
        ArrayList<String> list=new ArrayList<>();
        while (scanner.hasNextLine()){
            list.add(scanner.nextLine());
        }

        System.out.println("show updates for:");
        int n=1;
        System.out.println("[0] ALL websites");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] title=line.split(";");
            System.out.println("["+n+"]"+title[0]);
            n++;
        }
        System.out.println("enter -1 to return");
        Scanner scanner1=new Scanner(System.in);
        int k=scanner1.nextInt();
        if(k==0){
            for (int i = 0; i < list.size(); i++){
                String[] urlSplit = list.get(i).split(";");
                retrieveRssContent(urlSplit[2]);
            }
        }
        else if (k==-1) {
            f = false;
        }
        else if (k>n||k<-1) {
            System.out.println("error");

        }else {

            String sline=list.get(k-1);
            String[] urlline=sline.split(";");
            retrieveRssContent(urlline[2]);

        }


    }
    public static void remoeurl() throws IOException {
        System.out.println("please enter url to remove:");
        Scanner scanner=new Scanner(System.in);
        String lineremove=scanner.nextLine();
        ArrayList<String> list=new ArrayList<>();
        FileReader fileReader=new FileReader(file);
        BufferedReader bufferedReader=new BufferedReader(fileReader);
        String line;
        boolean flag = true;
        while ((line = bufferedReader.readLine()) != null){
            if(!line.contains(lineremove))
                list.add(line);
            else
                flag = false;
        }


        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for (int i = 0; i < list.size(); i++) {
            bufferedWriter.write(list.get(i));
        }

        if (flag == false)
        {
            System.out.println("removed " + lineremove + " successfully");
        }
        else System.out.println("couldn,t find "+ lineremove);

    }}