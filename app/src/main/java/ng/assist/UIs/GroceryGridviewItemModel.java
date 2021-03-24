package ng.assist.UIs;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class GroceryGridviewItemModel extends LinearLayout {
    public GroceryGridviewItemModel(Context context) {
        super(context);
    }

    public GroceryGridviewItemModel(Context context, AttributeSet attributeSet){

        super(context,attributeSet);
    }

    public GroceryGridviewItemModel(Context context, AttributeSet attributeSet, int defStyle){

        super(context,attributeSet,defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        super.onMeasure(widthMeasureSpec,widthMeasureSpec*2);
    }
}
