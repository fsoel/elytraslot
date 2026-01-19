package com.dermememann.elytra.modmenu;

import com.terraformersmc.modmenu.api.UpdateChannel;
import com.terraformersmc.modmenu.api.UpdateInfo;

public class UpdateInfoImpl implements UpdateInfo {
    private boolean updateAvailable = false;
    private String downloadLink = null;

    @Override
    public boolean isUpdateAvailable() {
        return updateAvailable;
    }

    @Override
    public String getDownloadLink() {
        return downloadLink;
    }

    @Override
    public UpdateChannel getUpdateChannel() {
        return UpdateChannel.RELEASE;
    }

    public void setUpdateAvailable(boolean updateAvailable) {
        this.updateAvailable = updateAvailable;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
}
