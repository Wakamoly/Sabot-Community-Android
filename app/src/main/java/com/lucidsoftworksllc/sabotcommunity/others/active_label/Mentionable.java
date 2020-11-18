package com.lucidsoftworksllc.sabotcommunity.others.active_label;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Abstract mention to be used with {com.hendraanggrian.appcompat.widget.MentionArrayAdapter}.
 */
public interface Mentionable {

    /**
     * Unique id of this mention.
     */
    @NonNull
    CharSequence getUsername();

    /**
     * Type of this mention, can be clan, username, or game.
     */
    @NonNull
    CharSequence getType();

    /**
     * Optional display name, located above username.
     */
    @Nullable
    CharSequence getDisplayname();

    /**
     * Optional avatar, may be Drawable, resources, or string url pointing to image.
     */
    @Nullable
    Object getAvatar();
}