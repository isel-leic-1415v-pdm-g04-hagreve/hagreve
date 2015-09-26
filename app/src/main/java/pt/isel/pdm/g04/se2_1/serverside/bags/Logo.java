package pt.isel.pdm.g04.se2_1.serverside.bags;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

import pt.isel.pdm.g04.se2_1.provider.HgContract;

/**
 * Project se2-1, created on 2015/05/12.
 */
public class Logo {
    public final int id, path_link;
    public final String name_hint;

    public Logo(int id, int path_link, String name_hint) {
        this.id = id;
        this.path_link = path_link;
        this.name_hint = name_hint;
    }

    public static ContentValues toContentValues(Logo logo, Bitmap logoImage, Bitmap bannerImage) {
        ContentValues _contentValues = new ContentValues();
        _contentValues.put(HgContract.Logos._ID, logo.id);
        _contentValues.put(HgContract.Logos.SOURCEID, logo.path_link);
        _contentValues.put(HgContract.Logos.LOGO, toByteArray(logoImage));
        _contentValues.put(HgContract.Logos.BANNER, toByteArray(bannerImage));
        return _contentValues;
    }

    private static byte[] toByteArray(Bitmap bitmap) {
        ByteArrayOutputStream _byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, _byteArrayOutputStream);
        return _byteArrayOutputStream.toByteArray();
    }

    public static Bitmap toBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
