package elo;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;

public class Updater {
    static DbxRequestConfig config = DbxRequestConfig.newBuilder("").build();
    static DbxClientV2 client = new DbxClientV2(config, Secret.dbToken);

    public Updater() {

    }

    private static double getLatestVersionNum() {
        ListFolderResult result = null;
        try {
            result = client.files().listFolder("");
        } catch (DbxException e) {
            e.printStackTrace();
        }
        String file = result.getEntries().get(0).getPathLower().substring(14, 17);
        return Double.parseDouble(file);
    }


    public static boolean updateNeeded() {
        if (Double.parseDouble(Program.version) < Updater.getLatestVersionNum()) {
            return true;
        } else {
            return false;
        }
    }
}
