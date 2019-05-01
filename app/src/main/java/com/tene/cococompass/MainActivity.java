package com.tene.cococompass;

import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView iv_compass;
    private TextView tv_degree;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float azimuth = 0f;
    private float currentAzimuth = 0f;
    private SensorManager sensorManager;
    //private final Sensor accelerometer;

   /* public MainActivity(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_compass = findViewById(R.id.iv_compass);
        tv_degree = findViewById(R.id.tv_degree);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_GAME);

        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        final float alpha = 0.97f;
        synchronized (this){
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                gravity[0] = alpha*gravity[0] + (1-alpha)*event.values[0];
                gravity[1] = alpha*gravity[1] + (1-alpha)*event.values[1];
                gravity[2] = alpha*gravity[2] + (1-alpha)*event.values[2];

                //double gravity1 = Math.sqrt(gravity[0]*gravity[0] + gravity[1]*gravity[1] + gravity[2]*gravity[2]);
                //Log.d("gravity", ""+gravity1);
            }

            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                geomagnetic[0] = alpha*geomagnetic[0] + (1-alpha)*event.values[0];
                geomagnetic[1] = alpha*geomagnetic[1] + (1-alpha)*event.values[1];
                geomagnetic[2] = alpha*geomagnetic[2] + (1-alpha)*event.values[2];
            }

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R,I,gravity,geomagnetic);
            if (success){
                float orientation[] = new float[3];
                SensorManager.getOrientation(R,orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);
                tv_degree.setText(Math.round(azimuth)+"°");
                azimuth = (azimuth+360)%360;


                Animation animation = new RotateAnimation(-currentAzimuth, -azimuth,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                currentAzimuth = azimuth;

                animation.setDuration(500);
                animation.setRepeatCount(0);
                animation.setFillAfter(true);

                Glide.with(this)
                        .asBitmap()
                        .load(com.tene.cococompass.R.drawable.compass)
                        .into(iv_compass);
                iv_compass.startAnimation(animation);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
