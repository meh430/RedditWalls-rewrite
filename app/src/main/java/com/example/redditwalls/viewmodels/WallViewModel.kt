package com.example.redditwalls.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.redditwalls.misc.ImageLoader
import com.example.redditwalls.misc.Utils
import com.example.redditwalls.models.Image
import com.example.redditwalls.models.PostInfo
import com.example.redditwalls.models.Resolution
import com.example.redditwalls.models.Resource
import com.example.redditwalls.repositories.FavoriteImagesRepository
import com.example.redditwalls.repositories.RWRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WallViewModel @Inject constructor(
    private val rwRepository: RWRepository,
    private val favoriteImagesRepository: FavoriteImagesRepository,
    private val imageLoader: ImageLoader
) : BaseViewModel() {
    val postInfo = MutableLiveData<Resource<PostInfo>>()
    val isFavorite = MutableLiveData<Boolean>()
    private lateinit var currentImage: Image

    init {
        postInfo.value = Resource.loading()
    }

    fun initialize(image: Image) {
        currentImage = image
        viewModelScope.launch {
            isFavorite.postValue(favoriteImagesRepository.favoriteExists(currentImage.imageLink))
        }
    }

    fun toggleFavorite() {
        isFavorite.value?.let {
            viewModelScope.launch {
                favoriteImagesRepository.favoriteExists(currentImage.imageLink).let { exists ->
                    if (exists) {
                        favoriteImagesRepository.deleteFavoriteImage(currentImage.imageLink)
                    } else {
                        favoriteImagesRepository.insertFavorite(currentImage)
                    }

                    isFavorite.postValue(!exists)
                }
            }
        }
    }

    fun getPostInfo(
        postLink: String,
        imageLink: String,
        context: Context,
        resolution: Resolution
    ) {
        getResource(postInfo) {
            val imageSize = imageLoader.getImageSize(context, imageLink, resolution)
            PostInfo(rwRepository.getPostInfo(postLink, imageSize), Utils.getResolution(context))
        }
    }
}