package de.p72b.geo.util

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import de.p72b.geo.Geo

class ContextProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        val context = context
        if (context != null) {
            Geo.setContext(context)
        } else {
            Log.e(TAG, "Context injection to geo failed. Context is null!")
        }
        return false
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    companion object {
        private val TAG = ContextProvider::class.java.simpleName
    }
}
