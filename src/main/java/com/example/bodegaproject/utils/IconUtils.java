package com.example.bodegaproject.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class IconUtils {
    public static ImageView createIcon(String iconPath) {
        Image icon = new Image(Objects.requireNonNull(IconUtils.class.getResource(iconPath)).toExternalForm());
        ImageView iconView = new ImageView(icon);
        iconView.setFitWidth(40);
        iconView.setFitHeight(40);
        return iconView;
    }
}

