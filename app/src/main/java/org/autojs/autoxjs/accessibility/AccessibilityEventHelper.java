package org.autojs.autoxjs.accessibility;

import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_CLICKED;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_LONG_CLICKED;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SCROLLED;
import static android.view.accessibility.AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;

import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;

import com.stardust.util.SparseArrayEntries;

import org.autojs.autoxjs.R;

/**
 * Created by Stardust on 2017/3/16.
 */

public class AccessibilityEventHelper {

    private static final SparseArray<Integer> EVENT_ACTION_RES_ID_MAP = new SparseArrayEntries<Integer>()
            .entry(TYPE_VIEW_CLICKED, R.string.text_clicked)
            .entry(TYPE_VIEW_LONG_CLICKED, R.string.text_long_clicked)
            .entry(TYPE_VIEW_SCROLLED, R.string.text_scrolled)
            .entry(TYPE_VIEW_TEXT_CHANGED, R.string.text_text_changed)
            .sparseArray();

    public static int getEventTypeNameResId(AccessibilityEvent event) {
        return EVENT_ACTION_RES_ID_MAP.get(event.getEventType());
    }
}
