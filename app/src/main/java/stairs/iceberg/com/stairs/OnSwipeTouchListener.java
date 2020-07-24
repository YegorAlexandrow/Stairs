package stairs.iceberg.com.stairs;
import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Created by User on 30.12.2017.
 */
public class OnSwipeTouchListener implements OnTouchListener {

    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener (Context ctx){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 60;
        private static final int SWIPE_VELOCITY_THRESHOLD = 60;

        @Override
        public boolean onDown(MotionEvent e) {
            onTouch(e);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float dy = e2.getY() - e1.getY();
                float dx = e2.getX() - e1.getX();

                if (Math.abs(dx) > SWIPE_THRESHOLD &&
                        Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD &&
                        Math.abs(dy) > SWIPE_THRESHOLD &&
                        Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {

                    if(dy < 0) {
                        if(dx < 0) onSwipeNW();
                        else onSwipeNE();
                    } else {
                        if(dx < 0) onSwipeSW();
                        else onSwipeSE();
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onTouch(MotionEvent e) {
    }

    public void onSwipeNE() {
    }

    public void onSwipeNW() {
    }

    public void onSwipeSE() {
    }

    public void onSwipeSW() {
    }
}
