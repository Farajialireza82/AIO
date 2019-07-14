package com.worldsnas.domain.repo.genre

import com.worldsnas.domain.helpers.errorHandler
import com.worldsnas.domain.model.servermodels.GenreServerModel
import com.worldsnas.panther.RFetcher
import io.reactivex.Single
import retrofit2.Response
import javax.inject.Inject

class MovieGenreFetcher @Inject constructor(
    private val api: GenreAPI
) : RFetcher<Unit, ArrayList<GenreServerModel>> {
    override fun fetch(param: Unit): Single<Response<ArrayList<GenreServerModel>>> =
        api.allMovieGenre()
            .errorHandler()
}