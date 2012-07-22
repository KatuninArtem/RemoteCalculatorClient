package com.jump.demos.clients;




import com.jump.demos.calculator.CalculatorInterface;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RemoteCalculatorClientActivity extends Activity {
    /** Called when the activity is first created. */
	private CalculatorInterface cInterface;
	
	private Button addButton;
	private Button subButton;
	private Button mulButton;
	private Button divButton;
	
	private EditText elem1;
	private EditText elem2;
	
	private EditText result;
	
	private Button clearButton;
	
	private Point values = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean connected = this.bindService(new Intent("com.jump.demos.calculator.CalculatorService"), connection, BIND_AUTO_CREATE);
        
        if(connected) {
        	setContentView(R.layout.main);
        	setupUI();
        } else {
        Toast.makeText(RemoteCalculatorClientActivity.this, "Service not found", Toast.LENGTH_SHORT).show();
        }
        	
    }
    
    private ServiceConnection connection = new ServiceConnection() {

		public void onServiceConnected(ComponentName name, IBinder service) {
			cInterface = CalculatorInterface.Stub.asInterface((IBinder)service);
			Toast.makeText(RemoteCalculatorClientActivity.this, "Successfully binded to a service", Toast.LENGTH_SHORT).show();
		}

		public void onServiceDisconnected(ComponentName name) {
			cInterface = null;
		}
    	
    };
    
    
    private void setupUI() {
    	
    	addButton = (Button)findViewById(R.id.addButton);
    	subButton = (Button)findViewById(R.id.subButton);
    	mulButton = (Button)findViewById(R.id.mulButton);
    	divButton = (Button)findViewById(R.id.divButton);
    	clearButton = (Button)findViewById(R.id.clearButton);
    	
    	elem1 = (EditText)findViewById(R.id.elem1);
    	elem2 = (EditText)findViewById(R.id.elem2);
    	result = (EditText)findViewById(R.id.result);
    	
    	addButton.setOnClickListener(buttonListener);
    	subButton.setOnClickListener(buttonListener);
    	mulButton.setOnClickListener(buttonListener);
    	divButton.setOnClickListener(buttonListener);
    	clearButton.setOnClickListener(buttonListener);
    	
    }
    
    private View.OnClickListener buttonListener = new View.OnClickListener() {
		
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.addButton:
				callService(0);
				break;
			case R.id.subButton:
				callService(1);
				break;
			case R.id.mulButton:
				callService(2);
				break;
			case R.id.divButton:
				callService(3);
				break;
			case R.id.clearButton:
				values = getValues();
				elem1.setText("");
				elem2.setText("");
				result.setText("");
				break;
			}
		}
	};
	
	private Point getValues() {
		int a = 0;
		int b = 0;
		
		try {
			a = Integer.valueOf(elem1.getText().toString());
			b = Integer.valueOf(elem2.getText().toString());
		} catch (NumberFormatException e) {
			Toast.makeText(RemoteCalculatorClientActivity.this, "Invalid integer values", Toast.LENGTH_SHORT).show();
			return null;
		}
		Point p = new Point(a, b);
		return p;
	}
	
	private void callService(int op) {
		int res = 0;
		
		values = getValues();
		if(values == null) 
			return;
		
		try {
			switch(op) {
			case 0:
				res = cInterface.add(values.x, values.y);
				break;
			case 1:
				res = cInterface.subtract(values.x, values.y);
				break;
			case 2:
				res = cInterface.multiply(values.x, values.y);
				break;
			case 3:
				res = cInterface.divide(values.x, values.y);
				break;
			}
			result.setText(String.valueOf(res));
		} catch (RemoteException e) {
			Toast.makeText(RemoteCalculatorClientActivity.this, "Service unavailable", Toast.LENGTH_SHORT).show();
		} catch (RuntimeException e) {
			Toast.makeText(RemoteCalculatorClientActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
    
    protected void onDestroy() {
 	   super.onDestroy();
 	   this.unbindService(connection);
 	   cInterface = null;
 	 };
}