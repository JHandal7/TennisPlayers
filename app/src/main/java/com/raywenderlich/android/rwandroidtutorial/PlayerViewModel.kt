/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.rwandroidtutorial

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.raywenderlich.android.rwandroidtutorial.database.Player
import com.raywenderlich.android.rwandroidtutorial.database.PlayerListItem
import com.raywenderlich.android.rwandroidtutorial.database.PlayersDatabase
import kotlinx.coroutines.CoroutineScope

open class PlayerViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: PlayerRepository

  init {
    val playerDao = PlayersDatabase
        .getDatabase(application,viewModelScope,application.resources)
        .playerDao()
    repository = PlayerRepository(playerDao)
  }
/*
  suspend fun populateDatabase() {
    val resources = getApplication<Application>().resources
    val jsonString = resources.openRawResource(R.raw.players).bufferedReader().use {
      it.readText()
    }
    val typeToken = object : TypeToken<List<Player>>() {}.type
    val tennisPlayers = Gson().fromJson<List<Player>>(jsonString, typeToken)
    repository.insertAllPlayers(tennisPlayers)
  }*/


    fun getAllPlayers(): LiveData<List<PlayerListItem>> {
        return repository.getAllPlayers()
    }
}