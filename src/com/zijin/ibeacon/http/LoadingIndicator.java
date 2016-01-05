package com.zijin.ibeacon.http;

import android.app.Activity;
import android.app.ProgressDialog;

public class LoadingIndicator {
    private static ProgressDialog dialog;

    public static void show(Activity activity, String message) {
        cancel();

     /*   View v = LayoutInflater.from(activity).inflate(R.layout.progress, null);
        ImageView image = (ImageView) v.findViewById(R.id.progress_image);
        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.progress_anim);
        anim.setInterpolator(new LinearInterpolator());
        image.startAnimation(anim);
        TextView msg = (TextView) v.findViewById(R.id.progress_message);
        msg.setText(message);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(activity.getResources().getDrawable(R.drawable.app_logo_mobilefinance));
        dialog = builder.show();
        dialog.getWindow().setContentView(v);*/
        dialog = new ProgressDialog(activity);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.show();
    }

    public static void cancel() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }
}
