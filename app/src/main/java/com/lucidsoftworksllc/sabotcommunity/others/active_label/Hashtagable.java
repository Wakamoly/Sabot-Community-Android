package com.lucidsoftworksllc.sabotcommunity.others.active_label;

import androidx.annotation.NonNull;

/**
 * Abstract hashtag to be used with {com.hendraanggrian.appcompat.widget.HashtagArrayAdapter}.
 */
public interface Hashtagable {

    /**
     * Unique id of this hashtag.
     */
    @NonNull
    CharSequence getId();

    /**
     * Optional count, located right to hashtag name.
     */
    int getCount();
}