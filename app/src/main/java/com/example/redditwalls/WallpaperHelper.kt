package com.example.redditwalls

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import com.example.redditwalls.misc.ImageLoader
import com.example.redditwalls.misc.Toaster
import com.example.redditwalls.misc.Utils
import com.example.redditwalls.misc.fromId
import com.example.redditwalls.repositories.FavoriteImagesRepository
import com.example.redditwalls.repositories.SettingsItem
import com.example.redditwalls.repositories.SettingsRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WallpaperHelper @Inject constructor(
    private val imageLoader: ImageLoader,
    private val favoriteImagesRepository: FavoriteImagesRepository,
    private val settingsRepository: SettingsRepository,
    private val toaster: Toaster
) {

    suspend fun setRandomFavoriteWallpaper(context: Context) {
        toast("Setting wallpaper...")
        val image = favoriteImagesRepository.getRandomFavoriteImage()

        image?.let {
            setImageLinkAsWallpaper(
                context,
                it.imageLink,
                settingsRepository.getRandomRefreshLocation()
            )
        }
        if (image == null) {
            toast("No favorites to choose from :(")
        }
    }

    suspend fun setImageLinkAsWallpaper(
        context: Context,
        imageLink: String,
        location: WallpaperLocation
    ) {
        try {
            toast("Setting wallpaper...")
            val resolution = Utils.getResolution(context)
            val wallpaper = imageLoader.loadImage(
                context,
                imageLink,
                resolution
            )

            withContext(Dispatchers.Main) {
                setBitmapAsWallpaper(context, wallpaper, location)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setBitmapAsWallpaper(context: Context, bitmap: Bitmap, location: WallpaperLocation) {
        try {
            val wm = context.getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                val which = when (location) {
                    WallpaperLocation.HOME -> WallpaperManager.FLAG_SYSTEM
                    WallpaperLocation.LOCK -> WallpaperManager.FLAG_LOCK
                    WallpaperLocation.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
                }
                wm.setBitmap(bitmap, null, true, which)
            } else {
                wm.setBitmap(bitmap)
            }

            toaster.t("Successfully set wallpaper")
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showLocationPickerDialog(context: Context, onChoose: (WallpaperLocation) -> Unit) {
        val items = WallpaperLocation.values().map { it.displayText }.toTypedArray()
        MaterialAlertDialogBuilder(context)
            .setTitle("Set where?")
            .setItems(items) { _, i ->
                onChoose(WallpaperLocation.fromId(i))
            }.show()
    }

    private suspend fun toast(msg: String) {
        withContext(Dispatchers.Main) {
            toaster.t(msg)
        }
    }
}

enum class WallpaperLocation(override val id: Int, override val displayText: String) :
    SettingsItem {
    HOME(0, "Home"),
    LOCK(1, "Lock Screen"),
    BOTH(2, "Home and Lock Screen");

    companion object {
        fun fromId(id: Int) = values().fromId(id, BOTH)
    }
}