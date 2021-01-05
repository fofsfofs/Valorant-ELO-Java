package elo;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Updater {
    static DbxRequestConfig config = DbxRequestConfig.newBuilder("").build();
    static DbxClientV2 client = new DbxClientV2(config, Secret.dbToken);
    private Login login;

    public Updater(Login l) {
        this.login = l;
        updateNeeded();
    }

    private static String getLatestVersionName() {
        ListFolderResult result = null;
        try {
            result = client.files().listFolder("");
        } catch (DbxException e) {
            e.printStackTrace();
        }
        String file = result.getEntries().get(0).getPathDisplay();
        return file.substring(1);
    }

    private static double getLatestVersionNum() {
        return Double.parseDouble(getLatestVersionName().substring(13, 16));
    }

    private void updateNeeded() {
        if (Double.parseDouble(Program.version) < getLatestVersionNum()) {
            Alert needUpdate = new Alert(Alert.AlertType.INFORMATION, "A new version is available, would you like to download?");
            needUpdate.setTitle("Old version detected");
            needUpdate.setHeaderText(null);
            ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);
            needUpdate.getButtonTypes().setAll(yes, no);

            needUpdate.showAndWait().ifPresent(type -> {
                if (type == yes) {
                    update();
                } else {
                    login.createLogin();
                }
            });
        } else {
            login.createLogin();
        }
    }

    private static void update() {
        try {
            File file = new File(getLatestVersionName());
            FileOutputStream fOut = new FileOutputStream(file);
            client.files().downloadBuilder("/" + getLatestVersionName()).start().download(fOut);
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}