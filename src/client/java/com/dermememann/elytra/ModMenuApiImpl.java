package com.dermememann.elytra;

import com.dermememann.elytra.modmenu.UpdateCheckerImpl;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.api.UpdateChecker;

public class ModMenuApiImpl implements ModMenuApi {

    private final UpdateCheckerImpl updateChecker;

    public ModMenuApiImpl() {
        updateChecker = new UpdateCheckerImpl();
    }

    @Override
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
}
