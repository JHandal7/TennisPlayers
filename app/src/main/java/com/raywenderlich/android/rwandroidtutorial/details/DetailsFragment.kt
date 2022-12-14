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

package com.raywenderlich.android.rwandroidtutorial.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.android.rwandroidtutorial.R
import com.raywenderlich.android.rwandroidtutorial.database.Player
import com.raywenderlich.android.rwandroidtutorial.database.PlayerListItem
import com.raywenderlich.android.rwandroidtutorial.ui.CircleTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_details.*
import java.text.NumberFormat
import java.util.Locale


class DetailsFragment : DialogFragment(), Toolbar.OnMenuItemClickListener {

  private lateinit var detailViewModel: DetailViewModel

  private lateinit var player: Player
  private lateinit var playerListItem: PlayerListItem

  companion object {

    const val PLAYER_KEY = "player_key"

    fun newInstance(player: PlayerListItem): DetailsFragment {
      val fragment = DetailsFragment()
      val args = Bundle().apply {
        putParcelable(PLAYER_KEY, player)
      }
      fragment.arguments = args//Supply the construction arguments for this fragment.
      // The arguments supplied here will be retained across fragment destroy and creation.
      return fragment
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.AppTheme_Fragment)
    detailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
  }
  //Creates ViewModelProvider.
  // This will create ViewModels and retain them in a store of the given ViewModelStoreOwner.

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    dialog!!.window?.attributes?.windowAnimations = R.style.AppTheme_Fragment
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_details, container, false)
  }

  override fun onMenuItemClick(item: MenuItem?): Boolean {
    return when (item?.itemId) {
      R.id.action_delete -> {
        deleteCurrentPlayer()
        true
      }
      else -> false
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    detailsToolbar.setNavigationOnClickListener {
      dismiss()
    }

    // inflate the menu with the star
    detailsToolbar.inflateMenu(R.menu.menu_details)
    detailsToolbar.setOnMenuItemClickListener(this)
    val starMenuItem = detailsToolbar.menu.findItem(R.id.action_favorite)
    val checkbox = starMenuItem.actionView as CheckBox

    arguments?.getParcelable<PlayerListItem>(PLAYER_KEY)?.let { playerListItem = it }

    //TODO observe viewmodel changes
    // 1 Adding an observer to the getPlayer(playerListItem).
    detailViewModel.getPlayer(playerListItem).observe(viewLifecycleOwner, Observer {
      // 2 Updating the local Player with the observer player item it.
      this.player = it

      // 3 Calling to display the player now that the observer???s data is up to date.
      detailViewModel.getPlayer(playerListItem).observe(this, Observer {
        this.player = it

        setupFavoriteToggle(checkbox, it) // called the method here

        displayPlayer()
      })
      displayPlayer()
    })
  }

  private fun displayPlayer() {
    // load the image
    Picasso.get()
        .load(player.imageUrl)
        .error(R.drawable.error_list_image)
        .placeholder(R.drawable.default_list_image)
        .transform(CircleTransformation())
        .into(playerImage)

    // Load the player info
    textViewPlayerName.text =
        String.format(Locale.getDefault(), "%s %s", player.firstName, player.lastName)
    textViewPlayerDescription.text = player.description
    textViewPlayerCountry.text = player.country
    textViewPlayerRank.text = player.rank.toString()
    textViewPlayerPoints.text = getString(R.string.player_points,
        NumberFormat.getNumberInstance().format(player.points))
    textViewPlayerAgeGender.text =
        getString(R.string.player_age_gender, player.age, player.gender)
  }

  private fun setupFavoriteToggle(checkBox: CheckBox, player : Player){
    // TODO setup checkbox to toggle favorite status of player Attaching
    //  OnCheckedChangeListener to the checkbox star MenuItem.
    // Assigning the player-favorite property to the checkbox checked value.
    //Calling updatePlayer(player) from ViewModel.
    //Handling the initial value of checkBox.isChecked.
    // 1
    checkBox.setOnCheckedChangeListener { _, b ->
      // 2
      player.favorite = b
      // 3
      detailViewModel.updatePlayer(player)
    }
// 4
    checkBox.isChecked = player.favorite
  }

  private fun deleteCurrentPlayer(){
    // TODO delete the displayed player
    detailViewModel.deletePlayer(player)
    dismiss()
  }
}
//Dismiss the fragment and its dialog.
// If the fragment was added to the back stack,
// all back stack state up to and including this entry will be popped.
// Otherwise, a new transaction will be committed to remove the fragment.