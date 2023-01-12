package com.smartshehar.customercallingv2;

import static android.content.Context.WINDOW_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.material.card.MaterialCardView;
import com.google.common.util.concurrent.ListenableFuture;
import com.smartshehar.customercallingv2.receivers.PhoneStateReceiver;

public class FloatingWindow extends Worker {

    private WindowManager wm;
    private MaterialCardView ll;
    private ImageView btnStop;


    private static final String TAG = "FloatingWindow";

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public FloatingWindow(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        wm = (WindowManager) appContext.getSystemService(WINDOW_SERVICE);
        ll = (MaterialCardView) LayoutInflater.from(appContext).inflate(R.layout.layout_popup, null);
        btnStop = ll.findViewById(R.id.btClosePopup);
        ctx = appContext;
    }



    Context ctx;

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Result doWork() {
        //Set the view data
        setCustomerData(getInputData());

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        final WindowManager.LayoutParams parameters = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, LAYOUT_FLAG, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        parameters.x = 0;
        parameters.y = 0;
        parameters.gravity = Gravity.CENTER;

        ContextCompat.getMainExecutor(ctx).execute(() -> {
            wm.addView(ll, parameters);
        });


        ll.setOnTouchListener(new OnTouchListener(parameters));

        btnStop.setOnClickListener(v -> wm.removeView(ll));
        return Result.success();
    }



    private void setCustomerData(@NonNull Data data) {
        if (data.getString("name") != null) {
            ((TextView) ll.findViewById(R.id.tv_custNamePopup)).setText(data.getString("name"));
        }
        if (data.getString("phone") != null) {
            ((TextView) ll.findViewById(R.id.tv_custPhonePopup)).setText(data.getString("phone"));
        }
    }

    class OnTouchListener implements View.OnTouchListener {

        private final WindowManager.LayoutParams updatedParameters;
        int x, y;
        float touchedX, touchedY;

        OnTouchListener(WindowManager.LayoutParams layoutParams){
            this.updatedParameters = layoutParams;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = updatedParameters.x;
                    y = updatedParameters.y;

                    touchedX = event.getRawX();
                    touchedY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    updatedParameters.x = (int) (x + (event.getRawX() - touchedX));
                    updatedParameters.y = (int) (y + (event.getRawY() - touchedY));

                    wm.updateViewLayout(ll, updatedParameters);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

}