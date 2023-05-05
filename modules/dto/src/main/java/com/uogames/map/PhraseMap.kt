package com.uogames.map

import com.uogames.dto.DefaultUUID
import com.uogames.dto.global.GlobalPhrase
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.local.LocalImage
import com.uogames.dto.local.LocalPhrase
import com.uogames.dto.local.LocalPhraseView
import com.uogames.dto.local.LocalPronunciation
import java.util.UUID

object PhraseMap {

    fun LocalPhrase.toGlobal(image: LocalImage?, pronunciation: LocalPronunciation?) = GlobalPhrase(
        globalId = globalId ?: DefaultUUID.value,
        globalOwner = globalOwner ?: "",
        phrase = phrase,
        definition = definition,
        lang = lang,
        idPronounce = pronunciation?.globalId,
        idImage = image?.globalId,
        timeChange = timeChange,
        like = 0,
        dislike = 0,
        country = country
    )

    fun LocalPhrase.toGlobal(imageID: UUID?, pronunciationID: UUID?) = GlobalPhrase(
        globalId = globalId ?: DefaultUUID.value,
        globalOwner = globalOwner ?: "",
        phrase = phrase,
        definition = definition,
        lang = lang,
        idPronounce = pronunciationID,
        idImage = imageID,
        timeChange = timeChange,
        like = 0,
        dislike = 0,
        country = country
    )

    fun LocalPhrase.update(phrase: GlobalPhrase) = LocalPhrase(
        id = id,
        phrase = phrase.phrase,
        definition = phrase.definition,
        lang = phrase.lang,
        idPronounce = idPronounce,
        idImage = idImage,
        timeChange = phrase.timeChange,
        like = phrase.like,
        dislike = phrase.dislike,
        globalId = phrase.globalId,
        globalOwner = phrase.globalOwner,
        country = phrase.country
    )

    fun LocalPhrase.update(phrase: GlobalPhrase?, idPronounce: Int?, idImage: Int?) = LocalPhrase(
        id = id,
        phrase = phrase?.phrase ?: this.phrase,
        definition = phrase?.definition ?: this.definition,
        lang = phrase?.lang ?: this.lang,
        idPronounce = idPronounce,
        idImage = idImage,
        timeChange = phrase?.timeChange ?: this.timeChange,
        like = phrase?.like ?: this.like,
        dislike = phrase?.dislike ?: this.dislike,
        globalId = phrase?.globalId ?: this.globalId,
        globalOwner = phrase?.globalOwner ?: this.globalOwner,
        country = phrase?.country ?: this.country
    )

    fun LocalPhrase.update(view: GlobalPhraseView, idPronounce: Int?, idImage: Int?) = LocalPhrase(
        id = id,
        phrase = view.phrase,
        definition = view.definition,
        lang = view.lang,
        country = view.country,
        idPronounce = idPronounce,
        idImage = idImage,
        timeChange = view.timeChange,
        like = view.like,
        dislike = view.dislike,
        globalId = view.globalId,
        globalOwner = view.user.globalOwner
    )

    fun GlobalPhraseView.toGlobalPhrase() = GlobalPhrase(
        globalId = globalId,
        globalOwner = user.globalOwner,
        phrase = phrase,
        definition = definition,
        lang = lang,
        country = country,
        idPronounce = pronounce?.globalId,
        idImage = image?.globalId,
        timeChange = timeChange,
        like = like,
        dislike = dislike
    )

    fun LocalPhraseView.toLocalPhrase() = LocalPhrase(
        id = id,
        phrase = phrase,
        definition = definition,
        lang = lang,
        country = country,
        idPronounce = pronounce?.id,
        idImage = image?.id,
        timeChange = timeChange,
        like = like,
        dislike = dislike,
        globalId = globalId,
        globalOwner = globalOwner,
        changed = changed
    )

}