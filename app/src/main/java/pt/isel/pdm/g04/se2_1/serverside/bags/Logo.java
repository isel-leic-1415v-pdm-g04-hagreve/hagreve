package pt.isel.pdm.g04.se2_1.serverside.bags;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

import pt.isel.pdm.g04.se2_1.provider.HgContract;

public class Logo {

    public final int id;
    public final int path_link;
    public final String name_hint;

    public Logo(int id, int path_link, String name_hint) {
        this.id = id;
        this.path_link = path_link;
        this.name_hint = name_hint;
    }

    public static ContentValues toContentValues(Logo logo, Bitmap logoImage, Bitmap bannerImage) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(HgContract.Logos._ID, logo.id);
        contentValues.put(HgContract.Logos.SOURCEID, logo.path_link);
        contentValues.put(HgContract.Logos.LOGO, toByteArray(logoImage));
        contentValues.put(HgContract.Logos.BANNER, toByteArray(bannerImage));
        return contentValues;
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
