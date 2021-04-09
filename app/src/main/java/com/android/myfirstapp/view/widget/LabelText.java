package com.android.myfirstapp.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.myfirstapp.R;
import com.android.myfirstapp.utils.Utils;

public class LabelText extends LinearLayout {
    private TextView label,text;
    private String labelText,textText;
    private float layoutWidth,textSize;

    public LabelText(Context context) {
        this(context,null);
    }

    public LabelText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelText);
        if(typedArray!=null){
            labelText = typedArray.getString(R.styleable.LabelText_label);
            textText = typedArray.getString(R.styleable.LabelText_value);
            layoutWidth = typedArray.getDimension(R.styleable.LabelText_label_width, Utils.dp2px(context,15));
            textSize = typedArray.getDimensionPixelSize(R.styleable.LabelText_text_size, (int) Utils.sp2px(context,5));
            textSize = Utils.px2sp(context,textSize);
            typedArray.recycle();
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.label_text,this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        label = this.findViewById(R.id.text_label);
        label.setTextSize(textSize);
        if(labelText!=null && labelText.length()>0)
            label.setText(labelText);
        label.setWidth((int) layoutWidth);

        text = this.findViewById(R.id.text_text);
        text.setTextSize(textSize);
        if(textText!=null && textText.length()>0)
            text.setText(textText);
    }

    public String getLabel() {
        return labelText;
    }

    public void setLabel(String labelText) {
        if(labelText!=null && labelText.length()>0){
            this.labelText = labelText;
            label.setText(labelText);
        }
    }

    public String getText() {
        return textText;
    }

    public void setText(String textText) {
        if(textText!=null && textText.length()>0){
            this.textText = textText;
            text.setText(textText);
        }
    }
}
