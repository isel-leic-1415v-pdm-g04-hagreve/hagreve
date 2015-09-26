package pt.isel.pdm.g04.se2_1.helpers;

import android.widget.ListView;

/**
 * Project SE2-1, created on 2015/04/15.
 */
public class G4ListView {

    public static int[] getCheckedItemsIds(ListView listView) {
        long[] _positions = listView.getCheckedItemIds();
        int _length = _positions.length;
        int[] _results = new int[_length];
        for (int i = 0; i < _length; i++) _results[i] = (int) _positions[i];
        return _results;
    }

}
