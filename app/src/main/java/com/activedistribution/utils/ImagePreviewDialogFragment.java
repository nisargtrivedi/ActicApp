package com.activedistribution.utils;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.activedistribution.R;
import com.activedistribution.view.activities.BaseActivity;
import com.bumptech.glide.Glide;

import java.io.File;

public class ImagePreviewDialogFragment extends DialogFragment {

    private ImageView image_preview_IV;
    private ImageView close_image_preview_IV;
    private ImagePreviewDialogFragment context;
    private Dialog dialog;
    private static String resume;

    //singleton
    public static ImagePreviewDialogFragment newInstance(String url) {
        resume = url;
        return new ImagePreviewDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RelativeLayout root = new RelativeLayout(getActivity());
        root.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // creating the fullscreen dialog
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(root);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.whiteTransparent)));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_image_preview, container, false);
        this.context = this;

        image_preview_IV = view.findViewById(R.id.image_preview_IV);
        previewImage(resume, image_preview_IV, "");
        //image_preview_IV.setImageDrawable(getResources().getDrawable(R.drawable.event1));

        close_image_preview_IV = view.findViewById(R.id.close_image_preview_IV);
        close_image_preview_IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        return view;
    }


    public void previewImage(String loadImg, ImageView view, String crop_path) {
        if (loadImg == null || TextUtils.isEmpty(loadImg)) return;
        String loadImageFinal = "/" + loadImg;

        if (crop_path.contains("crop")) {
            Glide.with(getActivity()).load(Constants.IMAGE_BASE_URL + loadImageFinal).into(view);
        } else if (crop_path.contains("path")) {
            Glide.with(getActivity()).load(new File(loadImg)).into(view);
        } else if (crop_path.contains("url")) {
            Glide.with(getActivity()).load(loadImg).into(view);
        } else {
            Glide.with(getActivity()).load(Constants.IMAGE_BASE_URL + loadImageFinal).into(view);
        }
    }

}
