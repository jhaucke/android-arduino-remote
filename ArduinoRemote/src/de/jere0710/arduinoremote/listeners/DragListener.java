package de.jere0710.arduinoremote.listeners;

import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class DragListener implements OnDragListener {

	@Override
	public boolean onDrag(View v, DragEvent event) {
		switch (event.getAction()) {
		case DragEvent.ACTION_DROP:
			// Dropped, reassign View to ViewGroup
			View view = (View) event.getLocalState();
			ViewGroup owner = (ViewGroup) view.getParent();
			owner.removeView(view);
			view.setX(event.getX() - (view.getWidth() / 2));
			view.setY(event.getY() - (view.getHeight() / 2));
			RelativeLayout container = (RelativeLayout) v;
			container.addView(view);
			view.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		return true;
	}
}
