package com.worldsnas.domain.repo.people

import arrow.core.Either
import com.worldsnas.core.ErrorHolder
import com.worldsnas.domain.helpers.eitherError
import com.worldsnas.domain.model.repomodel.PersonRepoModel
import com.worldsnas.domain.model.servermodels.PersonServerModel
import com.worldsnas.domain.model.servermodels.ResultsServerModel
import com.worldsnas.panther.Mapper
import com.worldsnas.panther.RFetcher
import io.reactivex.Single
import javax.inject.Inject

class PeopleRepoImpl @Inject constructor(
    private val fetcher: RFetcher<PeopleRequestModel, ResultsServerModel<PersonServerModel>>,
    private val personMapper: Mapper<PersonServerModel, PersonRepoModel>
) : PeopleRepo {
    private var people : MutableList<PersonRepoModel> = mutableListOf()

    override fun fetchPeople(page : Int): Single<Either<ErrorHolder, List<PersonRepoModel>>> =
        fetcher.fetch(PeopleRequestModel(1))
            .eitherError {
                it
                    .list
                    .map { person ->
                        personMapper
                            .map(person)
                    }
                    .also {personList->
                        if (page == 1){
                            people = ArrayList(personList)
                        }else{
                            people.addAll(personList)
                        }
                    }
            }

    override fun cachedPeople() : Single<List<PersonRepoModel>> =
            Single.just(people)
}