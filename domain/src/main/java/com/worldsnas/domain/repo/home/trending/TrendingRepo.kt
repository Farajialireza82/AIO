package com.worldsnas.domain.repo.home.trending

import com.worldsnas.domain.repo.home.trending.model.TrendingRepoOutputModel
import com.worldsnas.domain.repo.home.trending.model.TrendingRepoParamModel
import io.reactivex.Single

interface TrendingRepo {

    fun fetch(param: TrendingRepoParamModel) : Single<TrendingRepoOutputModel>
    fun getCache() : Single<TrendingRepoOutputModel.Success>
}