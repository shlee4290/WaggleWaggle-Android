package com.somasoma.wagglewaggle.presentation.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.somasoma.wagglewaggle.core.*
import com.somasoma.wagglewaggle.data.Avatar
import com.somasoma.wagglewaggle.data.model.dto.member.Member
import com.somasoma.wagglewaggle.data.model.dto.member.OnlineResponse
import com.somasoma.wagglewaggle.data.model.dto.world.WorldRoom
import com.somasoma.wagglewaggle.domain.usecase.member.GetMemberUseCase
import com.somasoma.wagglewaggle.domain.usecase.member.GetOnlineUseCase
import com.somasoma.wagglewaggle.domain.usecase.member.PutEditMemberUseCase
import com.somasoma.wagglewaggle.domain.usecase.world.GetWorldListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val sharedPreferenceHelper: SharedPreferenceHelper,
    private val networkUtil: NetworkUtil,
    private val getMemberUseCase: GetMemberUseCase,
    private val getWorldListUseCase: GetWorldListUseCase,
    private val getOnlineUseCase: GetOnlineUseCase,
    private val putEditMemberUseCase: PutEditMemberUseCase
) : AndroidViewModel(application) {
    var backgroundSemicircleRadius: Int = 0
    var backgroundSemicircleHeight: Int = 0
    var topBarHeight: Int = 0

    val navigateToSettingEvent = SingleLiveEvent<Unit>()
    val navigateToFollowerFollowing = SingleLiveEvent<Unit>()
    val navigateToCreateWorld = SingleLiveEvent<Unit>()
    val scrollToPrevAvatarEvent = SingleLiveEvent<Unit>()
    val scrollToNextAvatarEvent = SingleLiveEvent<Unit>()
    private val _avatars = MutableLiveData<List<Avatar>>()
    val avatars: LiveData<List<Avatar>> = _avatars
    private val _worlds = MutableLiveData<List<WorldRoom>>()
    val worlds: LiveData<List<WorldRoom>> = _worlds
    private val _onlineUsers = MutableLiveData<List<Member>>()
    val onlineUsers: LiveData<List<Member>> = _onlineUsers
    private val _selectedAvatar = MutableLiveData<Avatar>()
    val selectedAvatar: LiveData<Avatar> = _selectedAvatar

    init {
        makeAvatars()
//        getOnlineUsers()
//        getWorlds()
        getMember()
        _onlineUsers.value = listOf(
            Member(1, "Mike", null, null, null, "male3", null, null, null, null, null),
            Member(2, "찰스", null, null, null, "male1", null, null, null, null, null),
            Member(3, "Rady", null, null, null, "female3", null, null, null, null, null),
            Member(4, "하링", null, null, null, "female2", null, null, null, null, null),
            Member(5, "메리", null, null, null, "male2", null, null, null, null, null),
            Member(6, "leon", null, null, null, "male3", null, null, null, null, null),
            Member(7, "Peter", null, null, null, null, null, null, null, null, null),
        )
        _worlds.value = listOf(
            WorldRoom(null, null, "함께 놀아요", "종묘", 8, null, listOf("111", "222", "333"), null),
            WorldRoom(null, null, "Let's Play", "종묘", 1, null, listOf("444", "555", "666"), null),
            WorldRoom(null, null, "월드 제목", "종묘", 17, null, listOf("777", "888", "999"), null),
            WorldRoom(null, null, "동해물과 백두산이", "종묘", 20, null, listOf("1010", "1111", "1212"), null),
            WorldRoom(null, null, "마르고 닳도록 하느님이 보우하사 우리나라 만세", "종묘", 0, null, listOf("1313", "1414", "1515", "1313", "1414", "1515", "1313", "1414", "1515"), null),
        )
    }

    private fun makeAvatars() {
        _avatars.value = Avatar.values().toList()
    }

    private fun getMember() {
        networkUtil.restApiCall(
            getMemberUseCase::getMember,
            sharedPreferenceHelper.getLong(PreferenceConstant.MEMBER_ID),
            viewModelScope
        ) {
            onSuccessCallback = {
                _selectedAvatar.value = string2Avatar(it?.avatar)
            }

            onErrorCallback = {
                _selectedAvatar.value = DEFAULT_AVATAR
            }

            onNetworkErrorCallback = {
                _selectedAvatar.value = DEFAULT_AVATAR
            }
        }
    }

    private fun getOnlineUsers() {
        networkUtil.restApiCall(getOnlineUseCase::getOnline, Unit, viewModelScope) {
            onSuccessCallback = { onlineResponse ->
                _onlineUsers.value = makeOnlineUserList(onlineResponse)
                Timber.d(onlineUsers.value.toString())
            }

            onErrorCallback = {

            }

            onNetworkErrorCallback = {

            }
        }
    }

    private fun makeOnlineUserList(onlineResponse: OnlineResponse?): MutableList<Member> {
        val tmpOnlineUsers = mutableListOf<Member>()

        addFollowingOnlineMembersToList(onlineResponse, tmpOnlineUsers)
        addNotFollowingOnlineMembersToList(onlineResponse, tmpOnlineUsers)

        return tmpOnlineUsers
    }

    private fun addNotFollowingOnlineMembersToList(
        onlineResponse: OnlineResponse?,
        tmpOnlineUsers: MutableList<Member>
    ) {
        onlineResponse?.onlineMembers?.let {
            addMembersToList(it, tmpOnlineUsers)
        }
    }

    private fun addFollowingOnlineMembersToList(
        onlineResponse: OnlineResponse?,
        tmpOnlineUsers: MutableList<Member>
    ) {
        onlineResponse?.onlineFollowingMembers?.let {
            addMembersToList(it, tmpOnlineUsers)
        }
    }

    private fun addMembersToList(
        members: List<Member?>,
        tmpOnlineUsers: MutableList<Member>
    ) {
        for (member in members) {
            member?.let {
                tmpOnlineUsers.add(member)
            }
        }
    }

    private fun getWorlds() {
        networkUtil.restApiCall(
            getWorldListUseCase::getWorldList,
            Unit,
            viewModelScope
        ) {
            onSuccessCallback = { it ->
                it?.worldRoomList?.let { worldRoomList ->
                    _worlds.value = makeWorlds(worldRoomList)
                    Timber.d(worlds.value.toString())
                }
            }

            onErrorCallback = {

            }

            onNetworkErrorCallback = {

            }
        }
    }

    private fun makeWorlds(worldRoomList: List<WorldRoom?>): MutableList<WorldRoom> {
        val tmpWorlds = mutableListOf<WorldRoom>()

        for (world in worldRoomList) {
            world?.let {
                tmpWorlds.add(world)
            }
        }
        return tmpWorlds
    }

    fun onAvatarSelected(index: Int) {
        val selectedAvatar = avatars.value?.get(index) ?: return
        Timber.d(selectedAvatar.toString())

        networkUtil.restApiCall(
            putEditMemberUseCase::putEditMember,
            Member(
                sharedPreferenceHelper.getInt(PreferenceConstant.MEMBER_ID),
                null,
                null,
                null,
                null,
                avatar2String(selectedAvatar),
                null,
                null,
                null,
                null,
                null
            ),
            viewModelScope
        ) {
            onErrorCallback = {

            }

            onNetworkErrorCallback = {

            }
        }
    }

    fun onClickPrevAvatarButton() {
        scrollToPrevAvatarEvent.call()
    }

    fun onClickNextAvatarButton() {
        scrollToNextAvatarEvent.call()
    }

    fun onClickSettingButton() {
        navigateToSettingEvent.call()
    }

    fun onClickFollowButton() {
        navigateToFollowerFollowing.call()
    }

    fun onClickCreateWorldButton() {
        navigateToCreateWorld.call()
    }
}