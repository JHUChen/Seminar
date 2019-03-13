package com.example.jhu.seminar.Setting;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jhu.seminar.DeviceAdapter;
import com.example.jhu.seminar.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.scan.IScanCallback;
import com.vise.baseble.callback.scan.ScanCallback;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;

import java.util.ArrayList;

public class InsoleBind extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String email;
    private ListView deviceLv;
    private DeviceAdapter adapter;

    private boolean mScanning;
    private AlertDialog dialog;
    private String leftnow = null;
    private String rightnow = null;
    private TextView leftMAC;
    private TextView rightMAC;
    private Button start;
    private Button stop;
    private Button leftunbind;
    private Button rightunbind;

    static final String db_name="test";
    static final String tb_name="reg_phy";
    private SQLiteDatabase db;

    private ScanCallback periodScanCallback = new ScanCallback(new IScanCallback() {
        @Override
        public void onDeviceFound(final BluetoothLeDeviceStore bluetoothLeDeviceStore) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adapter != null && bluetoothLeDeviceStore != null) {
                        adapter.setListAll(bluetoothLeDeviceStore.getDeviceList());
                    }
                }
            });
        }

        @Override
        public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {}

        @Override
        public void onScanTimeout() {}
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insole_bind);
        leftMAC = (TextView)findViewById(R.id.leftMAC);
        rightMAC = (TextView)findViewById(R.id.rightMAC);
        start = (Button)findViewById(R.id.start_scan);
        stop = (Button)findViewById(R.id.stop_scan);
        leftunbind = (Button)findViewById(R.id.leftunbind);
        rightunbind = (Button)findViewById(R.id.rightunbind);

        db = openOrCreateDatabase(db_name,  Context.MODE_PRIVATE, null);
        String createTable = "CREATE TABLE IF NOT EXISTS " + tb_name + " (" +
                "email VARCHAR(64) NOT NULL, " +
                "gender VARCHAR(1) NOT NULL, " +
                "age decimal(3,0) NOT NULL, " +
                "height float NOT NULL, " +
                "bindcheck tinyint(1) NOT NULL, " +
                "regdate date NOT NULL, " +
                "left_MAC VARCHAR(20), " +
                "right_MAC VARCHAR(20), " +
                "PRIMARY KEY (email))";
        db.execSQL(createTable);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        email = user.getEmail().toString();
        select();

        deviceLv = (ListView) findViewById(R.id.device_scan_list);
        adapter = new DeviceAdapter(this);
        deviceLv.setAdapter(adapter);
        deviceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final BluetoothLeDevice device = (BluetoothLeDevice) adapter.getItem(position);

                if (mScanning)
                    stopScan();

                AlertDialog.Builder builder = new AlertDialog.Builder(InsoleBind.this);
                builder.setTitle("選擇綁定的腳");
                String[] colors = {"左腳", "右腳"};

                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                if(leftnow != null)
                                    Toast.makeText(InsoleBind.this,"左腳已有鞋墊綁定，請先解除綁定！",Toast.LENGTH_SHORT).show();
                                else {
                                    leftnow = device.getAddress().toString();
                                    leftMAC.setText(leftnow);
                                    updateData_left(leftnow);
                                }

                                break;
                            case 1:
                                if(rightnow != null)
                                    Toast.makeText(InsoleBind.this,"右腳已有鞋墊綁定，請先解除綁定！",Toast.LENGTH_SHORT).show();
                                else {
                                    rightnow = device.getAddress().toString();
                                    rightMAC.setText(rightnow);
                                    updateData_right(rightnow);
                                }

                                break;
                        }
                    }
                };

                builder.setItems(colors, listener);
                builder.setNegativeButton("取消", null);
                dialog = builder.create();
                dialog.show();
            }
        });

        if(leftnow != null || rightnow != null) {
            if(leftnow != null)
                leftMAC.setText(leftnow);
            if(rightnow != null)
                rightMAC.setText(rightnow);
        }

        start.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScan();
            }
        });

        stop.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
            }
        });

        leftunbind.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(leftnow != null) {
                    leftnow = null;
                    leftMAC.setText("尚未綁定");
                    updateData_left(leftnow);
                }
                else {
                    Toast.makeText(InsoleBind.this,"左腳尚未綁定鞋墊！",Toast.LENGTH_SHORT).show();
                }
            }
        });

        rightunbind.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rightnow != null) {
                    rightnow = null;
                    rightMAC.setText("尚未綁定");
                    updateData_right(rightnow);
                }
                else {
                    Toast.makeText(InsoleBind.this,"右腳尚未綁定鞋墊！",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScan();
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startScan() {
        if (adapter != null) {
            adapter.setListAll(new ArrayList<BluetoothLeDevice>());
        }
        mScanning = true;
        ViseBle.getInstance().startScan(periodScanCallback);
        invalidateOptionsMenu();
    }

    private void stopScan() {
        mScanning = false;
        ViseBle.getInstance().stopScan(periodScanCallback);
        invalidateOptionsMenu();
    }

    private void select() {
        String[] tableColumns = {"left_MAC", "right_MAC"};
        String whereClause = "email = ?";
        String[] whereArgs = { email };
        Cursor c = db.query(tb_name, tableColumns, whereClause, whereArgs, null, null, null);

        if(c.getCount() != 0) {
            c.moveToFirst();
            leftnow = c.getString(0);
            rightnow = c.getString(1);
        }
    }

    private void updateData_left(String left_MAC) {
        ContentValues cvv = new ContentValues();
        cvv.put("left_MAC", left_MAC);
        String whereClause = "email = ?";
        String[] whereArgs = { email };
        int s = db.update(tb_name, cvv, whereClause, whereArgs);
        if(s == 1) {
            Toast.makeText(InsoleBind.this, "綁定設定成功！", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(this, "綁定設定有誤！", Toast.LENGTH_LONG).show();
    }

    private void updateData_right(String right_MAC) {
        ContentValues cvv = new ContentValues();
        cvv.put("right_MAC", right_MAC);
        String whereClause = "email = ?";
        String[] whereArgs = { email };
        int s = db.update(tb_name, cvv, whereClause, whereArgs);
        if(s == 1) {
            Toast.makeText(InsoleBind.this, "綁定設定成功！", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(this, "綁定設定有誤！", Toast.LENGTH_LONG).show();
    }
}
