package com.wordpress.dhruvapandey.virtualmate;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    TextView replyText;
    EditText inputText;
    Button talkButton;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        replyText = (TextView) findViewById(R.id.replyText);
        inputText = (EditText) findViewById(R.id.inputText);
        talkButton = (Button) findViewById(R.id.talkButton);

        replyText.setText("Hey use Text box then press button to have conversation with me");

        ConvertTextToSpeech();

        inputText.setText("");

        talkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userInput;
                String userOutput;
                String randomText = "";
                String[] inputWords;

                userInput = inputText.getText().toString();

                if (userInput == null) {
                    randomText = askQuestion("randomQuest");
                    Log.d("OnCreate", "userInput is Null!");
                }
                else {
                    inputWords = userInput.split(" ");
                    int size = inputWords.length;
                    Log.d("OnCreate SIZE: ", String.valueOf(size));

                    if (size < 2) // i.e, only inputWords[0] exists -> Ask Question
                    {
                        randomText = askQuestion(inputWords[0]);
                        Log.d("OnCreate", "userInput[1] is Null!");
                    }
                    else {

                        // Look for longest Match sequence
                        randomText = FindRandomReply(userInput);
                        // Second Attempt -- TODO: Find better way of doing it.
                        int count=0;
                        while (randomText == "couldNotFindMatch") {
                            count = count +1;
                            Log.d("couldNotFindMatch loop", String.valueOf(count));
                            randomText = FindRandomReply(userInput);
                            if (count == 20) {
                                randomText = askQuestion(userInput);
                                break;
                            }
                        }

                    }

                }
                replyText.setText(randomText);
                ConvertTextToSpeech();
                inputText.setText("");

            }
        });
    }

    private String askQuestion(String randomQuest) {
        String rndTxt = "";
        BufferedReader reader;
        int i=0;

        try {
            InputStream file = getAssets().open("questions.txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = "Topic Change";

            int randVal ;
            randVal = (int) ((Math.random() * 13) + 1);

            while(i <= randVal){
                i++;
                line = reader.readLine();
                //Log.d("DHRUVA",  i +line);
                //Log.d("PANDEY",  randVal +line);
            }
            rndTxt = line;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return rndTxt;
    }

    // Read from the txt files
    private String FindRandomReply (String userInput) {

        String rndReply = "couldNotFindMatch";
        String nextline = null;

        //
        BufferedReader reader;
        int i = 0;
        int randIdx;
        //
        String fileIdx;// = Integer.toString(randIdx);

        try {
            randIdx = (int) ((Math.random() * 20) + 1); // TODO: Put AUTO COUNT for Num of TXT files = 20
            //randIdx=18;
            fileIdx = Integer.toString(randIdx);

            InputStream file = getAssets().open("subtitle" + fileIdx + ".txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = "Times Up";

            Log.d("READING", "subtitle" + fileIdx + ".txt");

            while (reader.readLine() != null) {

                line = reader.readLine();
                if(line != null) {
                    char[] userInpChar = userInput.toCharArray();
                    char[] subLineChar = line.toCharArray();

                    String[] usrIpWords = userInput.split(" ");
                    String[] subLineWords = line.split(" ");

                    int result = lcsDynamic(usrIpWords, subLineWords);

                    Log.d("READING", "result " + String.valueOf(result));
                    Log.d("READING", "userInpLen " + String.valueOf(usrIpWords.length));

                    int twoThirdOFUsrInpLen = (int) ((0.8 * usrIpWords.length));

                    Log.d("READING", "90% of userInpLen " + String.valueOf(twoThirdOFUsrInpLen));

                    if (result >= twoThirdOFUsrInpLen) { // looking for all match.
                        Log.d("READING", "subtitle" + fileIdx + ".txt");
                        Log.d("READING", "userInpChar " + userInput);
                        Log.d("READING", "subLineChar " + line);

                        nextline = reader.readLine(); // Next line the reply from subtitle.
                        while (i == 0){
                            if (nextline.contains(".")) {
                                break;
                            }
                            else {
                                nextline += " "+reader.readLine();
                            }
                        }


                        Log.d("READING", "Found nextLine " + nextline);
                        break;
                    }
                    else nextline = null;
                }

            }


        } catch (IOException ex) {
            ex.printStackTrace();
        }
        //

        if (nextline != null) rndReply = nextline;
        else rndReply = "couldNotFindMatch";


        Log.d("RETURNING", rndReply);
        return rndReply;

    }

    public int lcsDynamic(String[] str1, String[] str2){

       Log.d("lcsDynamic", "userInp " + Arrays.toString(str1));
       Log.d("lcsDynamic", "subLine " + Arrays.toString(str2));

        int temp[][] = new int[str1.length + 1][str2.length + 1];
        int max = 0;
        for(int i=1; i < temp.length; i++){
            for(int j=1; j < temp[i].length; j++){
                Log.d("lcsDynamic", "userInpStr " + str1[i-1]);
                Log.d("lcsDynamic", "subLineStr " + str2[j-1]);

                if(str1[i-1].toLowerCase().equals(str2[j-1].toLowerCase())) {
                    temp[i][j] = temp[i - 1][j - 1] + 1;
                    Log.d("lcsDynamic", "result " + String.valueOf(temp[i][j]));
                }
                else
                {
                    temp[i][j] = Math.max(temp[i][j-1],temp[i-1][j]);
                }
                if(temp[i][j] > max){
                    max = temp[i][j];
                }
            }
        }
        Log.d("lcsDynamic", "result " + String.valueOf(max));
        return max;

    }



    private void ConvertTextToSpeech() {

        // TODO Auto-generated method stub
        tts=new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                // TODO Auto-generated method stub
                if(status == TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(Locale.US);
                    if(result==TextToSpeech.LANG_MISSING_DATA ||
                            result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("error", "This Language is not supported");
                    }
                    else{
                        String text = replyText.getText().toString();
                        if(text==null||"".equals(text))
                        {
                            text = "Content not available";
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                        }else
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
                else
                    Log.e("error", "Initialisation Failed!");
            }
        });

    }

}
