package uk.org.maps3;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class WhereAreYouActivity extends Activity  {
	boolean _active;
	String _password;
    EditText pwdText;
    CheckBox enableCheckBox;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setDefaultPrefs();
        savePrefs();
        pwdText = (EditText) findViewById(R.id.passwordText1);
        enableCheckBox = (CheckBox) findViewById(R.id.enableCheckBox);
        setFormValues();
        
        // Save the data when the enter or TAB key is pressed
        pwdText.setOnKeyListener(new View.OnKeyListener() {
        	public boolean onKey(View v, int keyCode, KeyEvent event) {
        		if ((event.getAction() == KeyEvent.ACTION_DOWN)&& 
        				((keyCode == KeyEvent.KEYCODE_ENTER) ||
        						(keyCode == KeyEvent.KEYCODE_TAB))) {
        			msgBox("Saving Data");
        			getFormValues();
        			savePrefs();
        			return true;
        		}
        		return false;
        	}
        });

        pwdText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
      			msgBox("Saving Data");
    			getFormValues();
    			savePrefs();
			}
		});
        
        enableCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        	public void onCheckedChanged(CompoundButton v, boolean b) {
        		msgBox("Saving Data");
    			getFormValues();
    			savePrefs();
        	}
        });
    }
    
    private void setFormValues() {
    	pwdText.setText(_password);
    	enableCheckBox.setChecked(_active);
    }
    
    private void getFormValues() {
    	_password = pwdText.getText().toString();
    	_active = enableCheckBox.isChecked();
    }
    
    private void setDefaultPrefs() {
    	SharedPreferences settings = getSharedPreferences("WhereAreYou",MODE_PRIVATE);
    	_active = settings.getBoolean("Active", true);
    	_password = settings.getString("Password","WAYN");
    }
    
    private void savePrefs() {
    	SharedPreferences settings = getSharedPreferences("WhereAreYou",MODE_PRIVATE);
    	SharedPreferences.Editor editor = settings.edit();
    	editor.putBoolean("Active", _active);
    	editor.putString("Password",_password);
    	editor.commit();
    }

    private void msgBox(String msg) {
     	Toast.makeText(this,
    			msg,
    			Toast.LENGTH_SHORT).show();
    	Log.d("WhereAreYouActivity",msg);

    }
}