package pt.isel.pdm.g04.se2_1.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import pt.isel.pdm.g04.se2_1.SettingsActivity;

/**
 * Project SE2-1, created on 2015/03/18.
 */
public class G4Http {

    public static final String LOGO_SERVER = "http://isel-leic-1415v-pdm-g04-hagreve.github.io/";
    public static final String URL_BASE_PATH = "api/v2/";
    public static final String COMPANIES = "companies";
    public static final String STRIKES = "strikes";
    public static final String LOGO_PATH = "";
    public static final String LOGOS = "logos";

    private static final HashMap<String, String> sCommands = new HashMap<>();

    static {
        sCommands.put(COMPANIES, URL_BASE_PATH + COMPANIES);
        sCommands.put(STRIKES, URL_BASE_PATH + STRIKES);
        sCommands.put(LOGOS, LOGO_PATH);
    }

    private final Context ctx;
    private final String baseUrl, path, pathLeaf;

    public G4Http(Context ctx, String baseUrl, String pathLeaf) {
        this.ctx = ctx;
        this.baseUrl = baseUrl;
        this.path = URL_BASE_PATH;
        this.pathLeaf = pathLeaf;
    }

    public G4Http(Context ctx, String baseUrl, String path, int pathLeaf) {
        this(ctx, baseUrl, path, String.valueOf(pathLeaf));
    }

    public G4Http(Context ctx, String baseUrl, String path, String pathLeaf) {
        this.ctx = ctx;
        this.baseUrl = baseUrl;
        this.path = path;
        this.pathLeaf = pathLeaf;
    }

    public final String getString() throws IOException {
        String _urlString = buildUrlGetString(ctx, baseUrl, path, pathLeaf);
        try {
            return getString(_urlString);
        } catch (MalformedURLException e) {
            G4Log.e("Internal application error. URL is malformed: " + _urlString);
            throw new RuntimeException(e);
        }
    }

    public final Bitmap getBitMap() throws IOException {
        String _urlString = buildUrlGetString(ctx, baseUrl, path, pathLeaf);
        try {
            return getBitMap(_urlString);
        } catch (MalformedURLException e) {
            G4Log.e("Internal application error. URL is malformed: " + _urlString);
            throw new RuntimeException(e);
        }
    }

    public static final String getString(String _urlString) throws IOException, MalformedURLException {
        return getString(new URL(_urlString));
    }

    public static final String getString(URL url) throws IOException {
        HttpURLConnection _httpURLConnection = null;
        BufferedReader _bufferedReader = null;
        try {
            _httpURLConnection = (HttpURLConnection) url.openConnection();
            StringBuilder _stringBuilder = new StringBuilder();
            _bufferedReader = new BufferedReader(
                    new InputStreamReader(_httpURLConnection.getInputStream()));
            for (String line; (line = _bufferedReader.readLine()) != null; ) {
                _stringBuilder.append(line);
            }
            return _stringBuilder.toString();
        } finally {
            if (_bufferedReader != null) _bufferedReader.close();
            if (_httpURLConnection != null) _httpURLConnection.disconnect();
        }
    }

    public static final Bitmap getBitMap(String _urlString) throws IOException {
        return getBitMap(new URL(_urlString));
    }

    public static final Bitmap getBitMap(URL url) throws IOException {
        HttpURLConnection _httpURLConnection = null;
        try {
            _httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream _inputStream = _httpURLConnection.getInputStream();
            return BitmapFactory.decodeStream(_inputStream);
        } finally {
            if (_httpURLConnection != null) _httpURLConnection.disconnect();
        }
    }

    public static String buildBaseUrl(Context ctx) {
        SharedPreferences _sp = G4SharedPreferences.getDefault(ctx);
        String _schema = getDefaultSchema(_sp);
        String _host = _sp.getString(SettingsActivity.SP_HOSTNAME, getDefaultHost(_sp));
        int _port = _sp.getInt(SettingsActivity.SP_PORT, getDefaultPort(_sp));
        return String.format("%s://%s:%s/", _schema, _host, _port);
    }

    public static String slashAppend(String base, String leaf) {
        return slashEnd(base) + leaf;
    }

    public static String slashAppend(int base, String leaf) {
        return slashEnd(base) + leaf;
    }

    public static String slashEnd(int base) {
        return String.valueOf(base) + "/";
    }

    public static String slashEnd(String base) {
        return (base.endsWith("/") ? "" : "/");
    }

    public static String unslashEnd(String base) {
        return (base.endsWith("/") ? base.substring(0, base.length() - 1) : base);
    }

    public static String addPath(String base, String path) {
        return slashEnd(base) + path;
    }

    // region Internal support methods

    private static String buildUrlGetString(Context ctx, String baseUrl, String path, String pathLeaf) {
        if (baseUrl == null) baseUrl = buildBaseUrl(ctx);
        return String.format("%s%s%s", baseUrl, path, pathLeaf == null ? "" : pathLeaf);
    }

    private static String getDefaultSchema(SharedPreferences sp) {
        return G4SharedPreferences.getDefaultTag(sp, SettingsActivity.SP_SCHEMA, G4Defaults.SCHEMA);
    }

    private static String getDefaultHost(SharedPreferences sp) {
        return G4SharedPreferences.getDefaultTag(sp, SettingsActivity.SP_HOSTNAME, G4Defaults.HOSTNAME);
    }

    private static int getDefaultPort(SharedPreferences sp) {
        return G4SharedPreferences.getDefaultTag(sp, SettingsActivity.SP_PORT, G4Defaults.PORT);
    }

    // endregion

}
