
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.util.Scanner;
import java.net.HttpURLConnection;
import javax.swing.*;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Main{
    public static void main(String[] args) {
        //Creates a MyFrame instance.
        new MyFrame();
    }
}

//Class with operations
class NewsOperations{

    //Method to get the news, if empty string is passed to this, then it only returns headlines.
    static String getNews(String query){
        try{
            URL url;
            //Setting the URL.
            if(query.equals("")){
                url = new URL("https://newsapi.org/v2/top-headlines?country=us&apiKey=9505bfc10bff4d46aa52fd1168da4332");
            }
            else{
                url = new URL("https://newsapi.org/v2/everything?q="+ query +"&apiKey=9505bfc10bff4d46aa52fd1168da4332");
            }
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //If network request success, get response from url using "scan".
            if(conn.getResponseCode() == 200) {
                Scanner scan = new Scanner(url.openStream());
                String str = "";

                // Adding the response from "scan" to variable "str" line by line.
                while(scan.hasNext()) {
                    str += scan.nextLine();
                }
                //returning the data (Which is now a JSON in string format)
                return str;
            }
            else{
                System.out.println("Request to API failed: "+ conn.getResponseCode());
                return "Error";
            }
        }
        catch (Exception ex){
            System.out.println("Error occurred: " + ex);
            return "Error";
        }
    }

    static String[] getUniNews() throws IOException{

        Document doc = Jsoup.connect("https://college.harvard.edu/about/news-announcements").get();
        Elements links = doc.select("p[class=\"c-teaser-archive-list__excerpt\"]");

        String[] linksArray = new String[links.size()];

        for(int i=0;i<links.size();i++){
            linksArray[i] = links.get(i).text();
        }
        //return the array of notifications
        return linksArray;
    }
}

//The actual GUI frame class.
class MyFrame extends JFrame implements ActionListener {

    //creating JFrame elements which can be accessed in any method.
    JPanel titlePanel =  new JPanel();
    JPanel newsPanel =  new JPanel();
    JPanel optionPanel =  new JPanel();

    JButton refreshButton= new JButton("Get News");
    JButton searchButton = new JButton("Search query");
    JTextField inputField = new JTextField();
    JButton aboutButton = new JButton("About Us");
    JButton uniButton = new JButton("University Notifications");

    JLabel heading = new JLabel("News Fetching System");
    ImageIcon titleIcon = new ImageIcon(getClass().getResource("images/news-icon.png"));

        //Creating 7 JLabel's.
    JLabel newsListArray[] = new JLabel[7];

    //Constructor that configures the JFrame
    MyFrame(){

        //Creating Title panel and heading. (TOP)
        titlePanel.setBackground(new Color(0x363636));
        titlePanel.setBorder(BorderFactory.createLineBorder(Color.black,6));
        heading.setForeground(Color.white);
        heading.setFont(new Font("Monospaced",Font.BOLD,40));
        titlePanel.add(heading);
        this.add(titlePanel, BorderLayout.NORTH);

        //Configuring buttons
        refreshButton.addActionListener(this);
        refreshButton.setBorder(BorderFactory.createBevelBorder(0));
        refreshButton.setFont(new Font("Comic Sans",Font.BOLD,15));
        refreshButton.setFocusable(false);
        refreshButton.setForeground(Color.blue);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        aboutButton.addActionListener(this);
        aboutButton.setBorder(BorderFactory.createBevelBorder(0));
        aboutButton.setFont(new Font("Comic Sans",Font.BOLD,15));
        aboutButton.setFocusable(false);
        aboutButton.setForeground(Color.blue);
        aboutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        inputField.setPreferredSize(new Dimension(200,20));
        inputField.setFont(new Font(null, Font.ITALIC,15));

        searchButton.addActionListener(this);
        searchButton.setBorder(BorderFactory.createBevelBorder(0));
        searchButton.setFont(new Font("Comic Sans",Font.BOLD,15));
        searchButton.setFocusable(false);
        searchButton.setForeground(Color.blue);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        uniButton.addActionListener(this);
        uniButton.setBorder(BorderFactory.createBevelBorder(0));
        uniButton.setFont(new Font("Comic Sans",Font.BOLD,15));
        uniButton.setFocusable(false);
        uniButton.setForeground(Color.blue);
        uniButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //Creating Option panel (LHS)
        optionPanel.setLayout(new GridLayout(6,1, 0, 30));
        optionPanel.add(new JLabel(""));    //first row empty.
        optionPanel.add(refreshButton);
            //Creating a single panel for the searching field and the submitting button.
        JPanel innerSearchPanel = new JPanel(new GridLayout(1,2, 5,0));
        innerSearchPanel.setBorder(BorderFactory.createLineBorder(Color.gray,1));
        innerSearchPanel.add(inputField);
        innerSearchPanel.add(searchButton);

        optionPanel.setBackground(new Color(0xEEE8E8));
        optionPanel.add(innerSearchPanel);
        optionPanel.add(uniButton);
        optionPanel.add(aboutButton);
        optionPanel.setPreferredSize(new Dimension(220,50));
        optionPanel.setBorder(BorderFactory.createLineBorder(Color.black,1));
        optionPanel.setBackground(new Color(0xEEE8E8));

        this.add(optionPanel, BorderLayout.WEST);

        //Creating News panel (RHS)
        for(int i=0;i<7;i++){
            //7 rows for news to be put in.
            //Initializing news list as empty.
            newsListArray[i] = new JLabel("");
            newsListArray[i].setBorder(BorderFactory.createLineBorder(Color.black,1));
            newsPanel.add(newsListArray[i]);
        }

        newsPanel.setLayout(new GridLayout(7,1));
        newsPanel.setBackground(Color.white);
        this.add(newsPanel, BorderLayout.CENTER);

        //JFrame options
        this.setIconImage(titleIcon.getImage());
        this.setTitle("News fetcher");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(900, 750);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==refreshButton){
            //Get news using a method of NewsOperations class.
            String news = NewsOperations.getNews("");
            updateGUIList(news);
            heading.setText("News fetched!");

            //Getting the news icon and resizing it.
            ImageIcon newImage= new ImageIcon(titleIcon.getImage().getScaledInstance(80,80,java.awt.Image.SCALE_SMOOTH));
            heading.setIcon(newImage);
        }
        else if(e.getSource()==searchButton){
            //Give as a parameter whatever user typed in the box using a method of NewsOperations class.
            String news = NewsOperations.getNews(inputField.getText());
            updateGUIList(news);
            heading.setText("Searched for '" + inputField.getText() + "'");

            //Getting the zoom icon
            ImageIcon zoomIcon = new ImageIcon(getClass().getResource("images/zoom-icon.png"));
            ImageIcon newImage= new ImageIcon(zoomIcon.getImage().getScaledInstance(80,80,java.awt.Image.SCALE_SMOOTH));
            heading.setIcon(newImage);
        }
        else if(e.getSource()==uniButton){
            heading.setText("University Notifications:");
            try {
                String[] notificationArray = NewsOperations.getUniNews();
                for(int i=0;i<7;i++){
                        newsListArray[i].setText("<html><font size=4 color=green>" + notificationArray[i] + " </font></html>");
                }

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            ImageIcon studentIcon = new ImageIcon(getClass().getResource("images/student.png"));
            ImageIcon newImage= new ImageIcon(studentIcon.getImage().getScaledInstance(80,80,java.awt.Image.SCALE_SMOOTH));
            heading.setIcon(newImage);
        }
        else if(e.getSource()==aboutButton){
            //Get image and set proper size
            ImageIcon smileIcon = new ImageIcon(getClass().getResource("images/smile.png"));
            ImageIcon newImage= new ImageIcon(smileIcon.getImage().getScaledInstance(150,150,java.awt.Image.SCALE_SMOOTH));

            JOptionPane.showMessageDialog(
                    null,
                    "Created By:-\nFJ",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE,
                    newImage);
        }
    }

    //A string of news is passed to this method, and then it updates the GUI.
    void updateGUIList (String news){
        if(!(news.equals("Error"))){
            //Converting the news to a JSONObject, so we can extract information easily.
            JSONObject data = new JSONObject(news);
            JSONArray arr = data.getJSONArray("articles");

            for(int i=0;i<7;i++){
                //Try-catch to prevent ArrayOutOfBoundsException(if less than 6 news available).
                //Will just exit loop instead of whole program terminating.
                try{

                    newsListArray[i].setText(
                            //adding html tags for styling.
                            "<html> <font size=4 > <u>" + arr.getJSONObject(i).getString("title") + "</u></font><br/>"
                            +"<font color=gray>-" +arr.getJSONObject(i).getJSONObject("source").getString("name") + "<br/>"
                            +"Published on "+arr.getJSONObject(i).getString("publishedAt").substring(0,10)+ "</font><html>"
                    );
                }
                catch (Exception ex){
                    System.out.println("Error occurred:" + ex);
                    break;
                }
            }
            //To empty other rows if number of news is less than 6
            for(int i=arr.length();i<7;i++){
                newsListArray[i].setText("");
            }
        }

    }
}