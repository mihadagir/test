package dom.test;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final int POSITION_ZAGLAVNOY = 0;
    public static final String EDIT_FORMAT_TEXT = "edit_format_text";
    public static final String TEXT_FORMAT_TEXT = "text_format_text";
    EditText editUnformatText;
    TextView textFormatedText;
    Button butFormarExecute;
    TaskReformat taskReformat;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editUnformatText = (EditText) findViewById(R.id.edit_not_formatted_text);
        textFormatedText = (TextView) findViewById(R.id.text_formated_text);
        butFormarExecute = (Button) findViewById(R.id.but_format_text);

        if (savedInstanceState != null) loadTextEditAndText();

        butFormarExecute.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String unformatText = " ";
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (editUnformatText.length() != 0) {
                    unformatText = editUnformatText.getText().toString();
                } else
                    Toast.makeText(MainActivity.this, "Please, input text", Toast.LENGTH_LONG).show();

                taskReformat = new TaskReformat();
                taskReformat.execute(unformatText);
            }
        });
    }

    void saveTextEditAndText() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(EDIT_FORMAT_TEXT, editUnformatText.getText().toString());
        ed.putString(TEXT_FORMAT_TEXT, textFormatedText.getText().toString());
        ed.apply();
    }

    void loadTextEditAndText() {
        sPref = getPreferences(MODE_PRIVATE);
        editUnformatText.setText(sPref.getString(EDIT_FORMAT_TEXT, ""));
        textFormatedText.setText(sPref.getString(TEXT_FORMAT_TEXT, ""));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveTextEditAndText();
    }

    private class TaskReformat extends AsyncTask<String, String, Void> {
        char[] arrayOfCharacter = {'.', '!', '?'};
        StringBuilder sbStrokaForChange;
        String unformatText;
        char zaglavnaya;

        @Override
        protected Void doInBackground(String... strings) {
            char zamena, zamenaExecute, characterOfPunctuation;
            boolean flag;

            unformatText = strings[0];
            flag = false;
            unformatText = unformatText.replaceAll("[.,!?;:]", "$0 ").replaceAll("\\s+", " ")
                    .replace(" .", ".").trim();

            int pos = unformatText.indexOf(" ");
            sbStrokaForChange = new StringBuilder(unformatText);
            publishProgress(String.valueOf(unformatText));
            while (pos != -1) {
                characterOfPunctuation = unformatText.charAt(pos - 1);
                for (char ch : arrayOfCharacter) {
                    if (characterOfPunctuation == ch)
                        flag = true;
                }
                if (flag) {
                    zamena = unformatText.charAt(pos + 1);
                    zamenaExecute = Character.toUpperCase(zamena);
                } else {
                    zamena = unformatText.charAt(pos + 1);
                    zamenaExecute = Character.toLowerCase(zamena);
                }
                sbStrokaForChange.setCharAt(pos + 1, zamenaExecute);
                pos = unformatText.indexOf(" ", pos + 1);
                flag = false;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Set upper case of first char
            if (unformatText.length() != 0) {
                zaglavnaya = sbStrokaForChange.charAt(POSITION_ZAGLAVNOY);
                sbStrokaForChange.setCharAt(POSITION_ZAGLAVNOY, Character.toUpperCase(zaglavnaya));
            }
            textFormatedText.setText(sbStrokaForChange.toString());
        }
    }
}


