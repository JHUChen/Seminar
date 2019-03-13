package com.example.jhu.seminar;

import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jhu.seminar.Account.BindAccount;
import com.example.jhu.seminar.Setting.SettingMain;
import com.example.jhu.seminar.WMSA.WalkModeStatsAnal;
import com.example.jhu.seminar.event.ConnectEvent;
import com.example.jhu.seminar.event.NotifyDataEvent;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.scan.IScanCallback;
import com.vise.baseble.callback.scan.ScanCallback;
import com.vise.baseble.common.PropertyType;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;
import com.vise.baseble.model.resolver.GattAttributeResolver;
import com.vise.baseble.utils.BleUtil;
import com.vise.baseble.utils.HexUtil;
import com.vise.xsnow.cache.SpCache;
import com.vise.xsnow.event.BusManager;
import com.vise.xsnow.event.Subscribe;
import com.vise.xsnow.permission.OnPermissionCallback;
import com.vise.xsnow.permission.PermissionManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String email;
    private FirebaseAuth mAuth;
    private boolean is_exit = false;
    private Button W;

    private TextView l_speed;
    private TextView l_distance;
    private TextView l_frequency;
    private TextView r_speed;
    private TextView r_distance;
    private TextView r_frequency;
    private boolean devconcheck_1 = false;
    private boolean devconcheck_2 = false;
    private boolean stopcheck = false;

    static final String db_name="test";
    static final String tb_name="reg_phy";
    private SQLiteDatabase db;
    private String leftnow = null;
    private String rightnow = null;

    private static final String LIST_NAME = "NAME";
    private static final String LIST_UUID = "UUID";
    public static final String NOTIFY_CHARACTERISTIC_UUID_KEY = "notify_uuid_key";
    private SimpleExpandableListAdapter simpleExpandableListAdapter;
    private SpCache mSpCache;
    private BluetoothLeDevice mDevice;
    private List<BluetoothGattService> mGattServices = new ArrayList<>();
    private List<List<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();

    private SimpleExpandableListAdapter simpleExpandableListAdapter_2;
    private SpCache mSpCache_2;
    private BluetoothLeDevice mDevice_2;
    private List<BluetoothGattService> mGattServices_2 = new ArrayList<>();
    private List<List<BluetoothGattCharacteristic>> mGattCharacteristics_2 = new ArrayList<>();

    private ImageView myImageView;
    private Bitmap bm;
    private Bitmap newImage;
    private Canvas c;
    private Paint paint;

    private ScanCallback periodScanCallback = new ScanCallback(new IScanCallback() {
        @Override
        public void onDeviceFound(final BluetoothLeDeviceStore bluetoothLeDeviceStore) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bluetoothLeDeviceStore != null) {
                        //adapter_2.setListAll(bluetoothLeDeviceStore.getDeviceList());
                        int i;
                        if (!devconcheck_1) {
                            for (i = 0; i < bluetoothLeDeviceStore.getDeviceList().size(); i++) {
                                if (bluetoothLeDeviceStore.getDeviceList().get(i).getAddress().equals(leftnow)) {
                                    mDevice = bluetoothLeDeviceStore.getDeviceList().get(i);
                                    mSpCache = new SpCache(MainActivity.this);
                                    if (!BluetoothDeviceManager.getInstance().isConnected(mDevice)) {
                                        BluetoothDeviceManager.getInstance().connect(mDevice);
                                        invalidateOptionsMenu();
                                    }
                                }
                            }
                        }
                        if (!devconcheck_2) {
                            for (i = 0; i < bluetoothLeDeviceStore.getDeviceList().size(); i++) {
                                if (bluetoothLeDeviceStore.getDeviceList().get(i).getAddress().equals(rightnow)) {
                                    mDevice_2 = bluetoothLeDeviceStore.getDeviceList().get(i);
                                    mSpCache_2 = new SpCache(MainActivity.this);
                                    if (!BluetoothDeviceManager.getInstance().isConnected(mDevice_2)) {
                                        BluetoothDeviceManager.getInstance().connect(mDevice_2);
                                        invalidateOptionsMenu();
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {
        }

        @Override
        public void onScanTimeout() {
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            email = user.getEmail();
        } else {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, BindAccount.class);
            startActivity(intent);
            finish();
        }

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

        //addData("jhu19960525@gmail.com","B","5","168",true);
        //addData("elaine850524@yahoo.com.tw","G","2","160",false);
        select();

        BluetoothDeviceManager.getInstance().init(this);
        BusManager.getBus().register(this);
        l_speed = (TextView) findViewById(R.id.l_speed);
        l_distance = (TextView) findViewById(R.id.l_distance);
        l_frequency = (TextView) findViewById(R.id.l_frequency);
        r_speed = (TextView) findViewById(R.id.r_speed);
        r_distance = (TextView) findViewById(R.id.r_distance);
        r_frequency = (TextView) findViewById(R.id.r_frequency);
        W = (Button)findViewById(R.id.submit_2);
        W.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, WalkModeStatsAnal.class);
                startActivity(intent);
            }
        });

        myImageView = (ImageView) findViewById(R.id.insole);
        bm = BitmapFactory.decodeResource(getResources(), R.drawable.p0);
        Bitmap.Config config = bm.getConfig();
        int width = bm.getWidth();
        int height = bm.getHeight();
        newImage = Bitmap.createBitmap(width, height, config);

        c = new Canvas(newImage);
        c.drawBitmap(bm, 0, 0, null);
        paint = new Paint();
    }

    /*private void addData(String email, String gender, String age, String height, boolean bindcheck) {
        Calendar mCal = Calendar.getInstance();
        CharSequence s = DateFormat.format("yyyy-MM-dd", mCal.getTime());

        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("gender", gender);
        cv.put("age", Integer.valueOf(age));
        cv.put("height", height);
        cv.put("bindcheck", bindcheck);
        cv.put("regdate", s.toString());

        db.insert(tb_name, null, cv);
    }*/

    private void setColor(ImageView myImageView, Bitmap newImage, Paint paint, Canvas c, int num, int col) {
        String color[] = {"#FFFF00", "#FFBB00", "#FF8800", "#FF5511", "#FF0000"};

        if (num == 0) {
            if (col == 1) {
                paint.setColor(Color.DKGRAY);
                c.drawCircle(150, 75, 45, paint);
            } else if (col == 2) {
                paint.setColor(Color.parseColor(color[0]));
                c.drawCircle(150, 75, 45, paint);
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(150, 75, 25, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(150, 75, 10, paint);
            } else if (col == 3) {
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(150, 75, 45, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(150, 75, 25, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(150, 75, 10, paint);
            } else if (col == 4) {
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(150, 75, 45, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(150, 75, 25, paint);
                paint.setColor(Color.parseColor(color[4]));
                c.drawCircle(150, 75, 10, paint);
            }
        } else if (num == 1) {
            if (col == 1) {
                paint.setColor(Color.DKGRAY);
                c.drawCircle(75, 175, 45, paint);
            } else if (col == 2) {
                paint.setColor(Color.parseColor(color[0]));
                c.drawCircle(75, 175, 45, paint);
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(75, 175, 25, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(75, 175, 10, paint);
            } else if (col == 3) {
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(75, 175, 45, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(75, 175, 25, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(75, 175, 10, paint);
            } else if (col == 4) {
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(75, 175, 45, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(75, 175, 25, paint);
                paint.setColor(Color.parseColor(color[4]));
                c.drawCircle(75, 175, 10, paint);
            }
        } else if (num == 2) {
            if (col == 1) {
                paint.setColor(Color.DKGRAY);
                c.drawCircle(180, 175, 45, paint);
            } else if (col == 2) {
                paint.setColor(Color.parseColor(color[0]));
                c.drawCircle(180, 175, 45, paint);
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(180, 175, 25, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(180, 175, 10, paint);
            } else if (col == 3) {
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(180, 175, 45, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(180, 175, 25, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(180, 175, 10, paint);
            } else if (col == 4) {
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(180, 175, 45, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(180, 175, 25, paint);
                paint.setColor(Color.parseColor(color[4]));
                c.drawCircle(180, 175, 10, paint);
            }
        } else if (num == 3) {
            if (col == 1) {
                paint.setColor(Color.DKGRAY);
                c.drawCircle(180, 450, 45, paint);
            } else if (col == 2) {
                paint.setColor(Color.parseColor(color[0]));
                c.drawCircle(180, 450, 45, paint);
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(180, 450, 25, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(180, 450, 10, paint);
            } else if (col == 3) {
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(180, 450, 45, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(180, 450, 25, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(180, 450, 10, paint);
            } else if (col == 4) {
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(180, 450, 45, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(180, 450, 25, paint);
                paint.setColor(Color.parseColor(color[4]));
                c.drawCircle(180, 450, 10, paint);
            }
        }

        myImageView.setImageBitmap(newImage);
    }

    private void setColor_2(ImageView myImageView, Bitmap newImage, Paint paint, Canvas c, int num, int col) {
        String color[] = {"#FFFF00", "#FFBB00", "#FF8800", "#FF5511", "#FF0000"};

        if (num == 0) {
            if (col == 1) {
                paint.setColor(Color.DKGRAY);
                c.drawCircle(350, 75, 45, paint);
            } else if (col == 2) {
                paint.setColor(Color.parseColor(color[0]));
                c.drawCircle(350, 75, 45, paint);
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(350, 75, 25, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(350, 75, 10, paint);
            } else if (col == 3) {
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(350, 75, 45, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(350, 75, 25, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(350, 75, 10, paint);
            } else if (col == 4) {
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(350, 75, 45, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(350, 75, 25, paint);
                paint.setColor(Color.parseColor(color[4]));
                c.drawCircle(350, 75, 10, paint);
            }
        } else if (num == 1) {
            if (col == 1) {
                paint.setColor(Color.DKGRAY);
                c.drawCircle(435, 175, 45, paint);
            } else if (col == 2) {
                paint.setColor(Color.parseColor(color[0]));
                c.drawCircle(435, 175, 45, paint);
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(435, 175, 25, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(435, 175, 10, paint);
            } else if (col == 3) {
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(435, 175, 45, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(435, 175, 25, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(435, 175, 10, paint);
            } else if (col == 4) {
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(435, 175, 45, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(435, 175, 25, paint);
                paint.setColor(Color.parseColor(color[4]));
                c.drawCircle(435, 175, 10, paint);
            }
        } else if (num == 2) {
            if (col == 1) {
                paint.setColor(Color.DKGRAY);
                c.drawCircle(315, 175, 45, paint);
            } else if (col == 2) {
                paint.setColor(Color.parseColor(color[0]));
                c.drawCircle(315, 175, 45, paint);
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(315, 175, 25, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(315, 175, 10, paint);
            } else if (col == 3) {
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(315, 175, 45, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(315, 175, 25, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(315, 175, 10, paint);
            } else if (col == 4) {
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(315, 175, 45, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(315, 175, 25, paint);
                paint.setColor(Color.parseColor(color[4]));
                c.drawCircle(315, 175, 10, paint);
            }
        } else if (num == 3) {
            if (col == 1) {
                paint.setColor(Color.DKGRAY);
                c.drawCircle(325, 450, 45, paint);
            } else if (col == 2) {
                paint.setColor(Color.parseColor(color[0]));
                c.drawCircle(325, 450, 45, paint);
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(325, 450, 25, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(325, 450, 10, paint);
            } else if (col == 3) {
                paint.setColor(Color.parseColor(color[1]));
                c.drawCircle(325, 450, 45, paint);
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(325, 450, 25, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(325, 450, 10, paint);
            } else if (col == 4) {
                paint.setColor(Color.parseColor(color[2]));
                c.drawCircle(325, 450, 45, paint);
                paint.setColor(Color.parseColor(color[3]));
                c.drawCircle(325, 450, 25, paint);
                paint.setColor(Color.parseColor(color[4]));
                c.drawCircle(325, 450, 10, paint);
            }
        }

        myImageView.setImageBitmap(newImage);
    }

    @Subscribe
    public void showConnectedDevice(ConnectEvent event) {
        if (event != null) {
            //updateConnectedDevice();
            if (event.isSuccess()) {
                Toast.makeText(MainActivity.this, "Connect Success!", Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                if (event.getDeviceMirror().getBluetoothLeDevice().getAddress().equals(leftnow)) {
                    if (event.getDeviceMirror() != null && event.getDeviceMirror().getBluetoothGatt() != null) {
                        simpleExpandableListAdapter = displayGattServices(event.getDeviceMirror().getBluetoothGatt().getServices());
                    }
                }
                if (event.getDeviceMirror().getBluetoothLeDevice().getAddress().equals(rightnow)) {
                    if (event.getDeviceMirror() != null && event.getDeviceMirror().getBluetoothGatt() != null) {
                        simpleExpandableListAdapter_2 = displayGattServices_2(event.getDeviceMirror().getBluetoothGatt().getServices());
                    }
                }
            } else {
                if (event.isDisconnected()) {
                    Toast.makeText(MainActivity.this, "Disconnect!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Connect Failure!", Toast.LENGTH_SHORT).show();
                }
                invalidateOptionsMenu();
            }
        }
    }

    @Subscribe
    public void showDeviceNotifyData(NotifyDataEvent event) {
        float fre, dis, spd, fre_2, dis_2, spd_2;
        String sped, dist, freq, p, sped_2, dist_2, freq_2, p_2;
        int lp_1, lp_2, lp_3, lp_4, rp_1, rp_2, rp_3, rp_4;

        if (event != null && event.getData() != null && event.getBluetoothLeDevice() != null) {
            if (event.getBluetoothLeDevice().getAddress().equals(mDevice.getAddress())) {
                devconcheck_1 = true;
                String tmp = HexUtil.encodeHexStr(event.getData());
                freq = tmp.substring(8, 16).toString();
                dist = tmp.substring(16, 24).toString();
                sped = tmp.substring(24, 32).toString();
                p = tmp.substring(32, 40).toString();
                int ft = Integer.parseInt(freq, 16);
                int dt = Integer.parseInt(dist, 16);
                int st = Integer.parseInt(sped, 16);
                float fretmp = new Float(ft).floatValue();
                float dttmp = new Float(dt).floatValue();
                float spdtmp = new Float(st).floatValue();
                int ptmp = Integer.parseInt(p, 16);
                fre = fretmp / 1000;
                dis = dttmp / 10000;
                spd = spdtmp / 1000;
                lp_1 = ptmp / 1000;
                lp_2 = (ptmp - lp_1 * 1000) / 100;
                lp_3 = (ptmp - (lp_1 * 1000) - (lp_2 * 100)) / 10;
                lp_4 = (ptmp - (lp_1 * 1000) - (lp_2 * 100) - (lp_3 * 10));
                l_frequency.setText(String.valueOf(fre).toString() + " step/s");
                l_distance.setText(String.valueOf(dis).toString() + " m/step");
                l_speed.setText(String.valueOf(spd).toString() + " m/s");

                setColor(myImageView, newImage, paint, c, 0, lp_1);
                setColor(myImageView, newImage, paint, c, 1, lp_2);
                setColor(myImageView, newImage, paint, c, 2, lp_3);
                setColor(myImageView, newImage, paint, c, 3, lp_4);
            }
            if (event.getBluetoothLeDevice().getAddress().equals(mDevice_2.getAddress())) {
                devconcheck_2 = true;
                String tmp_2 = HexUtil.encodeHexStr(event.getData());
                freq_2 = tmp_2.substring(8, 16).toString();
                dist_2 = tmp_2.substring(16, 24).toString();
                sped_2 = tmp_2.substring(24, 32).toString();
                p_2 = tmp_2.substring(32, 40).toString();
                int ft_2 = Integer.parseInt(freq_2, 16);
                int dt_2 = Integer.parseInt(dist_2, 16);
                int st_2 = Integer.parseInt(sped_2, 16);
                float fretmp_2 = new Float(ft_2).floatValue();
                float dttmp_2 = new Float(dt_2).floatValue();
                float spdtmp_2 = new Float(st_2).floatValue();
                int ptmp_2 = Integer.parseInt(p_2, 16);
                fre_2 = fretmp_2 / 1000;
                dis_2 = dttmp_2 / 10000;
                spd_2 = spdtmp_2 / 1000;
                rp_1 = ptmp_2 / 1000;
                rp_2 = (ptmp_2 - rp_1 * 1000) / 100;
                rp_3 = (ptmp_2 - (rp_1 * 1000) - (rp_2 * 100)) / 10;
                rp_4 = (ptmp_2 - (rp_1 * 1000) - (rp_2 * 100) - (rp_3 * 10));
                r_frequency.setText(String.valueOf(fre_2).toString() + " step/s");
                r_distance.setText(String.valueOf(dis_2).toString() + " m/step");
                r_speed.setText(String.valueOf(spd_2).toString() + " m/s");

                setColor_2(myImageView, newImage, paint, c, 0, rp_1);
                setColor_2(myImageView, newImage, paint, c, 1, rp_2);
                setColor_2(myImageView, newImage, paint, c, 2, rp_3);
                setColor_2(myImageView, newImage, paint, c, 3, rp_4);
            }
        }
        if (devconcheck_1 && devconcheck_2 && stopcheck == false) {
            stopcheck = true;
            stopScan();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBluetoothPermission();
        startScan();
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScan();
        if (BluetoothDeviceManager.getInstance().isConnected(mDevice)) {
            BluetoothDeviceManager.getInstance().disconnect(mDevice);
            invalidateOptionsMenu();
        }
        if (BluetoothDeviceManager.getInstance().isConnected(mDevice_2)) {
            BluetoothDeviceManager.getInstance().disconnect(mDevice_2);
            invalidateOptionsMenu();
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        ViseBle.getInstance().clear();
        BusManager.getBus().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                enableBluetooth();
            }
        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void checkBluetoothPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                PermissionManager.instance().with(this).request(new OnPermissionCallback() {
                    @Override
                    public void onRequestAllow(String permissionName) {
                        enableBluetooth();
                    }

                    @Override
                    public void onRequestRefuse(String permissionName) {
                        finish();
                    }

                    @Override
                    public void onRequestNoAsk(String permissionName) {
                        finish();
                    }
                }, Manifest.permission.ACCESS_COARSE_LOCATION);
            } else {
                enableBluetooth();
            }
        } else {
            enableBluetooth();
        }
    }

    private void enableBluetooth() {
        if (!BleUtil.isBleEnable(this)) {
            BleUtil.enableBluetooth(this, 1);
        }
    }

    private void startScan() {
        ViseBle.getInstance().startScan(periodScanCallback);
        invalidateOptionsMenu();
    }

    private void stopScan() {
        ViseBle.getInstance().stopScan(periodScanCallback);
        invalidateOptionsMenu();
    }

    private SimpleExpandableListAdapter displayGattServices_2(final List<BluetoothGattService> gattServices) {
        if (gattServices == null) return null;
        String uuid;
        final String unknownServiceString = getResources().getString(R.string.unknown_service);
        final String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        final List<Map<String, String>> gattServiceData = new ArrayList<>();
        final List<List<Map<String, String>>> gattCharacteristicData = new ArrayList<>();

        mGattServices_2 = new ArrayList<>();
        mGattCharacteristics_2 = new ArrayList<>();

        // Loops through available GATT Services.
        for (final BluetoothGattService gattService : gattServices) {
            final Map<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, GattAttributeResolver.getAttributeName(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            final List<Map<String, String>> gattCharacteristicGroupData = new ArrayList<>();
            final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            final List<BluetoothGattCharacteristic> charas = new ArrayList<>();

            // Loops through available Characteristics.
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                final Map<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, GattAttributeResolver.getAttributeName(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }

            mGattServices_2.add(gattService);
            mGattCharacteristics_2.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        final SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(this, gattServiceData, android.R.layout
                .simple_expandable_list_item_2, new String[]{LIST_NAME, LIST_UUID}, new int[]{android.R.id.text1, android.R.id.text2},
                gattCharacteristicData, android.R.layout.simple_expandable_list_item_2, new String[]{LIST_NAME, LIST_UUID}, new
                int[]{android.R.id.text1, android.R.id.text2});

        final BluetoothGattService service = mGattServices_2.get(2);
        final BluetoothGattCharacteristic characteristic = mGattCharacteristics_2.get(2).get(0);
        final int charaProp = characteristic.getProperties();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mSpCache_2.put(NOTIFY_CHARACTERISTIC_UUID_KEY + mDevice_2.getAddress(), characteristic.getUuid().toString());
            BluetoothDeviceManager.getInstance().bindChannel(mDevice_2, PropertyType.PROPERTY_NOTIFY, service.getUuid(), characteristic.getUuid(), null);
            BluetoothDeviceManager.getInstance().registerNotify(mDevice_2, false);
        }

        return gattServiceAdapter;
    }

    private SimpleExpandableListAdapter displayGattServices(final List<BluetoothGattService> gattServices) {
        if (gattServices == null) return null;
        String uuid;
        final String unknownServiceString = getResources().getString(R.string.unknown_service);
        final String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        final List<Map<String, String>> gattServiceData = new ArrayList<>();
        final List<List<Map<String, String>>> gattCharacteristicData = new ArrayList<>();

        mGattServices = new ArrayList<>();
        mGattCharacteristics = new ArrayList<>();

        // Loops through available GATT Services.
        for (final BluetoothGattService gattService : gattServices) {
            final Map<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, GattAttributeResolver.getAttributeName(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            final List<Map<String, String>> gattCharacteristicGroupData = new ArrayList<>();
            final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            final List<BluetoothGattCharacteristic> charas = new ArrayList<>();

            // Loops through available Characteristics.
            for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                final Map<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, GattAttributeResolver.getAttributeName(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }

            mGattServices.add(gattService);
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        final SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(this, gattServiceData, android.R.layout
                .simple_expandable_list_item_2, new String[]{LIST_NAME, LIST_UUID}, new int[]{android.R.id.text1, android.R.id.text2},
                gattCharacteristicData, android.R.layout.simple_expandable_list_item_2, new String[]{LIST_NAME, LIST_UUID}, new
                int[]{android.R.id.text1, android.R.id.text2});

        final BluetoothGattService service = mGattServices.get(2);
        final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(2).get(0);
        final int charaProp = characteristic.getProperties();
        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mSpCache.put(NOTIFY_CHARACTERISTIC_UUID_KEY + mDevice.getAddress(), characteristic.getUuid().toString());
            BluetoothDeviceManager.getInstance().bindChannel(mDevice, PropertyType.PROPERTY_NOTIFY, service.getUuid(), characteristic.getUuid(), null);
            BluetoothDeviceManager.getInstance().registerNotify(mDevice, false);
        }

        return gattServiceAdapter;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SettingMain.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.action_signout)
        {
            FirebaseAuth.getInstance().signOut();
            LoginManager.getInstance().logOut();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, BindAccount.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean returnValue;
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0 && is_exit == false) {
            Toast.makeText(getBaseContext(), "再按一次返回鍵離開", Toast.LENGTH_SHORT).show();
            is_exit = true;
            new Thread(new Runnable() {
                public void run()
                {
                    try {
                        Thread.sleep(2000);
                        is_exit = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            returnValue = true;
        }
        else {
            returnValue = super.onKeyDown(keyCode, event);
        }

        return returnValue;
    }
}
