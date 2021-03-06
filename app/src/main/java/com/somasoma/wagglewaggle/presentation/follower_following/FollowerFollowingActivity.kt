package com.somasoma.wagglewaggle.presentation.follower_following

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.somasoma.wagglewaggle.R
import com.somasoma.wagglewaggle.data.model.dto.member.Member
import com.somasoma.wagglewaggle.data.model.dto.world.WorldRoom
import com.somasoma.wagglewaggle.databinding.ActivityFollowerFollowingBinding
import com.somasoma.wagglewaggle.presentation.base.BaseActivity
import com.somasoma.wagglewaggle.presentation.main.MainActivity
import com.somasoma.wagglewaggle.presentation.main.MemberClickListener
import com.somasoma.wagglewaggle.presentation.main.create_world.CreateWorldActivity
import com.somasoma.wagglewaggle.presentation.profile.ProfileActivity
import com.somasoma.wagglewaggle.presentation.setting.SettingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class FollowerFollowingActivity : BaseActivity() {
    companion object {
        private const val MEMBER = "member"
    }

    private val viewModel: FollowerFollowingViewModel by viewModels()
    private lateinit var binding: ActivityFollowerFollowingBinding
    private val followListMemberClickListener = object : MemberClickListener {
        override fun onClick(member: Member) {
            viewModel.onFollowMemberClicked(member)
        }
    }
    private val followListEnterClickListener = object : EnterButtonClickListener {
        override fun onClick(worldRoom: WorldRoom) {
            viewModel.onEnterButtonClicked(worldRoom)
        }
    }
    private val followingListAdapter = FollowListAdapter(followListMemberClickListener, followListEnterClickListener)
    private val followerListAdapter = FollowListAdapter(followListMemberClickListener, followListEnterClickListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_follower_following)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.listFollowing.adapter = followingListAdapter
        binding.listFollower.adapter = followerListAdapter
        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 0) {
                    viewModel.onFollowerTabClicked()
                } else {
                    viewModel.onFollowingTabClicked()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        collect()
    }

    override fun onStart() {
        super.onStart()

        viewModel.getFollower()
        viewModel.getFollowing()
    }

    private fun collect() {
        repeatOnStart { viewModel.eventFlow.collect { handleEvent(it) } }
        repeatOnStart { viewModel.following.collect { onFollowingLoaded(it) } }
        repeatOnStart { viewModel.follower.collect { onFollowerLoaded(it) } }
    }

    private fun onFollowingLoaded(following: List<Member>) {
        followingListAdapter.submitList(following)
    }

    private fun onFollowerLoaded(follower: List<Member>) {
        followerListAdapter.submitList(follower)
    }

    private fun handleEvent(event: FollowerFollowingViewModel.Event) = when (event) {
        FollowerFollowingViewModel.Event.NavigateToSetting -> navigateToSettingEvent()
        FollowerFollowingViewModel.Event.NavigateToMain -> navigateToMain()
        FollowerFollowingViewModel.Event.NavigateToCreateWorld -> navigateToCreateWorld()
        FollowerFollowingViewModel.Event.NavigateToUnityWorld -> navigateToUnityWorld()
        is FollowerFollowingViewModel.Event.NavigateToProfile -> navigateToProfile(event.member)
    }

    private fun navigateToCreateWorld() {
        val navigateIntent = Intent(this, CreateWorldActivity::class.java)
        startActivity(navigateIntent)
    }

    private fun navigateToMain() {
        val navigateIntent = Intent(this, MainActivity::class.java)
        navigateIntent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivity(navigateIntent)
    }

    private fun navigateToSettingEvent() {
        val navigateIntent = Intent(this, SettingActivity::class.java)
        startActivity(navigateIntent)
    }

    private fun navigateToProfile(member: Member) {
        val navigateIntent = Intent(this, ProfileActivity::class.java)
        navigateIntent.putExtra(MEMBER, Json.encodeToString(member))
        startActivity(navigateIntent)
    }

    private fun navigateToUnityWorld() {
//        val navigateIntent = Intent(this, com.unity3d.player.UnityPlayerActivity::class.java)
//        startActivity(navigateIntent)
    }
}