package org.wordpress.android.models;

import android.text.TextUtils;

import org.json.JSONObject;
import org.wordpress.android.util.DateTimeUtils;
import org.wordpress.android.util.JSONUtil;
import org.wordpress.android.util.StringUtils;

/**
 * Created by nbradbury on 7/8/13.
 * TODO: unify this with Comment.java
 */
public class ReaderComment {
    public long commentId;
    public long blogId;
    public long postId;
    public long parentId;

    private String authorName;
    private String authorAvatar;

    private String authorUrl;
    private String status;
    private String text;

    public long timestamp;
    private String published;

    // not stored in db - denotes the indentation level when displaying this comment
    public transient int level = 0;

    public static ReaderComment fromJson(JSONObject json, long blogId) {
        if (json==null)
            throw new IllegalArgumentException("null json comment");

        ReaderComment comment = new ReaderComment();

        comment.blogId = blogId;
        comment.commentId = json.optLong("ID");
        comment.status = JSONUtil.getString(json, "status");

        // note that content may contain html, adapter needs to handle it
        comment.text = stripScript(JSONUtil.getString(json, "content"));

        comment.published = JSONUtil.getString(json, "date");
        comment.timestamp = DateTimeUtils.iso8601ToTimestamp(comment.published);

        JSONObject jsonPost = json.optJSONObject("post");
        if (jsonPost!=null)
            comment.postId = jsonPost.optLong("ID");

        JSONObject jsonAuthor = json.optJSONObject("author");
        if (jsonAuthor!=null) {
            // author names may contain html entities (esp. pingbacks)
            comment.authorName = JSONUtil.getStringDecoded(jsonAuthor, "name");
            comment.authorAvatar = JSONUtil.getString(jsonAuthor, "avatar_URL");
            comment.authorUrl = JSONUtil.getString(jsonAuthor, "URL");
        }

        JSONObject jsonParent = json.optJSONObject("parent");
        if (jsonParent!=null)
            comment.parentId = jsonParent.optLong("ID");

        return comment;
    }

    // comments on posts that use the "Sociable" plugin ( http://wordpress.org/plugins/sociable/ )
    // may have a script block which contains <!--//--> followed by a CDATA section followed by <!]]>,
    // all of which will show up if we don't strip it here (example: http://cl.ly/image/0J0N3z3h1i04 )
    // first seen at http://houseofgeekery.com/2013/11/03/13-terrible-x-men-we-wont-see-in-the-movies/
    // TODO: move this to a utility class
    private static String stripScript(final String text) {
        StringBuilder sb = new StringBuilder(text);
        int start = sb.indexOf("<script");

        while (start > -1) {
            int end = sb.indexOf("</script>", start);
            if (end == -1)
                return sb.toString();
            sb.delete(start, end+9);
            start = sb.indexOf("<script", start);
        }

        return sb.toString();
    }

    public String getAuthorName() {
        return StringUtils.notNullStr(authorName);
    }

    public void setAuthorName(String authorName) {
        this.authorName = StringUtils.notNullStr(authorName);
    }

    public String getAuthorAvatar() {
        return StringUtils.notNullStr(authorAvatar);
    }
    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = StringUtils.notNullStr(authorAvatar);
    }

    public String getAuthorUrl() {
        return StringUtils.notNullStr(authorUrl);
    }
    public void setAuthorUrl(String authorUrl) {
        this.authorUrl = StringUtils.notNullStr(authorUrl);
    }

    public String getText() {
        return StringUtils.notNullStr(text);
    }
    public void setText(String text) {
        this.text = StringUtils.notNullStr(text);
    }

    public String getStatus() {
        return StringUtils.notNullStr(status);
    }
    public void setStatus(String status) {
        this.status = StringUtils.notNullStr(status);
    }

    public String getPublished() {
        return StringUtils.notNullStr(published);
    }
    public void setPublished(String published) {
        this.published = StringUtils.notNullStr(published);
    }

    //

    public boolean hasAvatar() {
        return !TextUtils.isEmpty(authorAvatar);
    }

    public boolean hasAuthorUrl() {
        return !TextUtils.isEmpty(authorUrl);
    }
}
