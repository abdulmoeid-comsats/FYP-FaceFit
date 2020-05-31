package com.example.facefit;

import android.Manifest;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.ar.core.Config;
import com.google.ar.core.Config.AugmentedFaceMode;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.EnumSet;
import java.util.Set;




public class FaceArFragment extends ArFragment {

    @Override
    protected Config getSessionConfiguration(Session session) {
        Config config = new Config(session);
        config.setAugmentedFaceMode(AugmentedFaceMode.MESH3D);
        return config;
    }

    @Override
    protected Set<Session.Feature> getSessionFeatures() {
        return EnumSet.of(Session.Feature.FRONT_CAMERA);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout =
                (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);

        getPlaneDiscoveryController().hide();
        getPlaneDiscoveryController().setInstructionView(null);

        return frameLayout;
    }
    @Override
    public String[] getAdditionalPermissions() {
        String[] additionalPermissions = super.getAdditionalPermissions();
        int permissionLength = additionalPermissions != null ? additionalPermissions.length : 0;
        String[] permissions = new String[permissionLength + 1];
        permissions[0] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (permissionLength > 0) {
            System.arraycopy(additionalPermissions, 0, permissions, 1, additionalPermissions.length);
        }
        return permissions;
    }
}
