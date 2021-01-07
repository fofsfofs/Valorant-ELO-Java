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
import java.util.Arrays;
import java.util.List;

public class Updater {
    DbxRequestConfig config = DbxRequestConfig.newBuilder("").build();
    DbxClientV2 client = new DbxClientV2(config, Secret.dbToken);
    private Login login;
    private String extension = "";

    public Updater(Login l) {
        this.login = l;
        getExtension();
        if (!extension.equals("dev")) {
            deleteOld();
            updateNeeded();
        } else {
            login.createLogin();
        }
    }

    private void deleteOld() {
        String[] paths;
        paths = new File(System.getProperty("user.dir")).list();
        for (String path : paths) {
            if (path.contains(extension) && !path.contains(Program.version + "." + extension)) {
                File file = new File(path);
                file.delete();
            }
        }
    }

    private String getLatestVersionName() {
        ListFolderResult result = null;
        try {
            result = client.files().listFolder("/" + extension.toUpperCase() + "s");
        } catch (DbxException e) {
            e.printStackTrace();
        }
        String file = result.getEntries().get(0).getPathDisplay();
        return file.substring(6);
    }

    private double getLatestVersionNum() {
        return Double.parseDouble(getLatestVersionName().substring(13, getLatestVersionName().indexOf("." + extension)));
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
                    try {
                        if (extension.equals("jar")) {
                            Process proc = Runtime.getRuntime().exec("java -jar " + getLatestVersionName());
                        } else if (extension.equals("exe")) {
                            Process proc = Runtime.getRuntime().exec(getLatestVersionName());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    login.createLogin();
                }
            });
        } else {
            login.createLogin();
        }
    }

    private void getExtension() {
        String[] paths;
        paths = new File(System.getProperty("user.dir")).list();
        List<String> l = Arrays.asList(paths);
        if (l.contains(String.format("Valorant-ELO-%s.jar", Program.version))) {
            extension = "jar";
        } else if (l.contains(String.format("Valorant-ELO-%s.exe", Program.version))) {
            extension = "exe";
        } else {
            extension = "dev";
        }
    }

    private void update() {
        try {
            File file = new File(getLatestVersionName());
            FileOutputStream fOut = new FileOutputStream(file);
            client.files().downloadBuilder("/" + extension.toUpperCase() + "s/" + getLatestVersionName()).start().download(fOut);
            fOut.close();
        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
