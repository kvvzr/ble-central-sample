package net.twinkrun.ble_sample;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 25252;
    private final static int REQUEST_COARSE_LOCATION = 83025;

    private BluetoothAdapter mBleAdapter;
    private BluetoothLeScanner mBleScanner;
    private ScanCallback mScanCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBle(new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                if (result.getDevice().getName() == null) {
                    return;
                }
                Log.d("MainActivity", result.getDevice().getName());
                Log.d("MainActivity", "result.getRssi():" + result.getRssi());
            }
        });
    }

    private void setupBle(ScanCallback scanCallback) {
        mScanCallback = scanCallback;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        } else {
            setupScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_COARSE_LOCATION == requestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && mBleAdapter != null && mBleAdapter.isEnabled()) {
            startScan();
        }
    }

    private void setupScan() {
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = manager.getAdapter();

        if (mBleAdapter == null || !mBleAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        } else {
            startScan();
        }
    }

    private void startScan() {
        mBleScanner = mBleAdapter.getBluetoothLeScanner();
        mBleScanner.startScan(mScanCallback);
    }
}
