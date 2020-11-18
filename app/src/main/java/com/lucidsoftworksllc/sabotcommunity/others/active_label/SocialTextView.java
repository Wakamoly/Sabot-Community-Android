package com.lucidsoftworksllc.sabotcommunity.others.active_label;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.List;
import java.util.regex.Pattern;

/**
 * {@link android.widget.TextView} with hashtag, mention, and hyperlink support.
 *
 * @see SocialView
 */
public class SocialTextView extends AppCompatTextView implements SocialView {
    private final SocialView helper;

    public SocialTextView(Context context) {
        this(context, null);
    }

    public SocialTextView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public SocialTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        helper = SocialViewHelper.install(this, attrs);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Pattern getHashtagPattern() {
        return helper.getHashtagPattern();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Pattern getMentionPattern() {
        return helper.getMentionPattern();
    }

    @NonNull
    @Override
    public Pattern getDoubleMentionPattern() {
        return helper.getDoubleMentionPattern();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public Pattern getHyperlinkPattern() {
        return helper.getHyperlinkPattern();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHashtagPattern(@Nullable Pattern pattern) {
        helper.setHashtagPattern(pattern);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMentionPattern(@Nullable Pattern pattern) {
        helper.setMentionPattern(pattern);
    }

    @Override
    public void setDoubleMentionPattern(@Nullable Pattern pattern) {
        helper.setDoubleMentionPattern(pattern);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHyperlinkPattern(@Nullable Pattern pattern) {
        helper.setHyperlinkPattern(pattern);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHashtagEnabled() {
        return helper.isHashtagEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMentionEnabled() {
        return helper.isMentionEnabled();
    }

    @Override
    public boolean isDoubleMentionEnabled() {
        return helper.isDoubleMentionEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isHyperlinkEnabled() {
        return helper.isHyperlinkEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHashtagEnabled(boolean enabled) {
        helper.setHashtagEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMentionEnabled(boolean enabled) {
        helper.setMentionEnabled(enabled);
    }

    @Override
    public void setDoubleMentionEnabled(boolean enabled) {
        helper.setDoubleMentionEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHyperlinkEnabled(boolean enabled) {
        helper.setHyperlinkEnabled(enabled);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public ColorStateList getHashtagColors() {
        return helper.getHashtagColors();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public ColorStateList getMentionColors() {
        return helper.getMentionColors();
    }

    @NonNull
    @Override
    public ColorStateList getDoubleMentionColors() {
        return helper.getDoubleMentionColors();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public ColorStateList getHyperlinkColors() {
        return helper.getHyperlinkColors();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHashtagColors(@NonNull ColorStateList colors) {
        helper.setHashtagColors(colors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMentionColors(@NonNull ColorStateList colors) {
        helper.setMentionColors(colors);
    }

    @Override
    public void setDoubleMentionColors(@NonNull ColorStateList colors) {
        helper.setDoubleMentionColors(colors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHyperlinkColors(@NonNull ColorStateList colors) {
        helper.setHyperlinkColors(colors);
    }

    /**
     * {@inheritDoc}
     */
    @ColorInt
    @Override
    public int getHashtagColor() {
        return helper.getHashtagColor();
    }

    /**
     * {@inheritDoc}
     */
    @ColorInt
    @Override
    public int getMentionColor() {
        return helper.getMentionColor();
    }

    @Override
    public int getDoubleMentionColor() {
        return helper.getDoubleMentionColor();
    }

    /**
     * {@inheritDoc}
     */
    @ColorInt
    @Override
    public int getHyperlinkColor() {
        return helper.getHyperlinkColor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHashtagColor(int color) {
        helper.setHashtagColor(color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMentionColor(int color) {
        helper.setMentionColor(color);
    }

    @Override
    public void setDoubleMentionColor(int color) {
        helper.setDoubleMentionColor(color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHyperlinkColor(int color) {
        helper.setHyperlinkColor(color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHashtagClickListener(@Nullable SocialView.OnClickListener listener) {
        helper.setOnHashtagClickListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnMentionClickListener(@Nullable SocialView.OnClickListener listener) {
        helper.setOnMentionClickListener(listener);
    }

    @Override
    public void setOnDoubleMentionClickListener(@Nullable SocialView.OnClickListener listener) {
        helper.setOnDoubleMentionClickListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHyperlinkClickListener(@Nullable SocialView.OnClickListener listener) {
        helper.setOnHyperlinkClickListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHashtagTextChangedListener(@Nullable OnChangedListener listener) {
        helper.setHashtagTextChangedListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMentionTextChangedListener(@Nullable OnChangedListener listener) {
        helper.setMentionTextChangedListener(listener);
    }

    @Override
    public void setDoubleMentionTextChangedListener(@Nullable OnChangedListener listener) {
        helper.setDoubleMentionTextChangedListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public List<String> getHashtags() {
        return helper.getHashtags();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public List<String> getMentions() {
        return helper.getMentions();
    }

    @NonNull
    @Override
    public List<String> getDoubleMentions() {
        return helper.getDoubleMentions();
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    public List<String> getHyperlinks() {
        return helper.getHyperlinks();
    }
}