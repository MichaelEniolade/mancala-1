package me.dacol.marco.mancala.gameUI.pieces;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.widget.Button;
import android.widget.GridLayout;

import me.dacol.marco.mancala.R;
import me.dacol.marco.mancala.logging.Logger;

public class Bowl extends Button {

    private GradientDrawable mButtonShape;

    public static Bowl factory(Context context, int row, int column, String text, int id, int color) {
        Bowl bowl = new Bowl(context, color);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.rowSpec = GridLayout.spec(row);
        params.columnSpec = GridLayout.spec(column);

        bowl.setLayoutParams(params);
        bowl.setText(text);
        bowl.setId(id);

        return bowl;

    }

    public Bowl(Context context, int color) {
        super(context);

        mButtonShape = (GradientDrawable) getResources().getDrawable( R.drawable.bowl );
        mButtonShape.setColor(getResources().getColor(color));

        if (Build.VERSION.SDK_INT >= 16) {
            this.setBackground(mButtonShape);
        } else {
            this.setBackgroundDrawable( mButtonShape );
        }

    }

    private Animator animation() {
        Logger.v(VIEW_LOG_TAG, "Animate!");

        ObjectAnimator animator = ObjectAnimator.ofInt(mButtonShape, "Color", getResources().getColor(R.color.selectedBowl), getResources().getColor(R.color.playerOneBowl));
        animator.setDuration(1000);
        /*ValueAnimator animator = ValueAnimator.ofInt();
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(),
                getResources().getColor( R.color.selectedBowl),
                getResources().getColor(R.color.playerOneBowl));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                GradientDrawable bowlShape = (GradientDrawable) getBackground();
                bowlShape.setColor( (Integer)animation.getAnimatedValue() );
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.start();
*/
        return animator;
    }

}
