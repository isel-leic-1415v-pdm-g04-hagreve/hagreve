package pt.isel.pdm.g04.se2_1.helpers;

import android.util.SparseBooleanArray;

/**
 * Project SE2-1, created on 2015/03/24.
 */
public class G4SparseBooleanArray extends SparseBooleanArray {

    public G4SparseBooleanArray(SparseBooleanArray sba) {
        super(sba.size());
        for (int i = 0; i < sba.size(); i++)
            append(sba.keyAt(i), sba.valueAt(i));
    }

    public static int[] getNewIntArray(SparseBooleanArray source) {
        int _size = 0;
        for (int i = 0; i < source.size(); i++) if (source.valueAt(i)) _size++;
        return new int[_size];
    }

}
