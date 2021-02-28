package com.digitalpathology.digi_report.helper_classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.digitalpathology.digi_report.R;

public class SliderAdapter extends PagerAdapter {
    private Context context;
    int images[] = {
            R.drawable.ic_dr_lifeline_icon,
            R.drawable.ic_lady_with_report,
            R.drawable.ic_get_data_anytime
    };

    int headings[] = {
            R.string.text_add_medical,
            R.string.text_store_medical,
            R.string.text_get_data_anytime
    };

    private LayoutInflater layoutInflater;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container, false);

        ImageView sliderImage = view.findViewById(R.id.slider_image);
        TextView sliderTitle = view.findViewById(R.id.slider_title);

        sliderImage.setImageResource(images[position]);
        sliderTitle.setText(headings[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((ConstraintLayout)object);
    }
}
