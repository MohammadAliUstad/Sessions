package com.yugentech.sessions.ui.components.avatar

import androidx.annotation.DrawableRes
import com.yugentech.sessions.R

data class Avatar(
    val id: Int,
    val name: String,
    @field:DrawableRes val drawableRes: Int
)

object AvatarRepository {
    private val avatars = listOf(
        Avatar(
            id = 1,
            name = "Wise Elder",
            drawableRes = R.drawable.peep_100
        ),
        Avatar(
            id = 2,
            name = "Doc Life",
            drawableRes = R.drawable.peep_101
        ),
        Avatar(
            id = 3,
            name = "Dapper Gent",
            drawableRes = R.drawable.peep_17
        ),
        Avatar(
            id = 4,
            name = "Lost Soul",
            drawableRes = R.drawable.peep_2
        ),
        Avatar(
            id = 5,
            name = "Beard Boss",
            drawableRes = R.drawable.peep_27
        ),
        Avatar(
            id = 6,
            name = "Teeth Grind",
            drawableRes = R.drawable.peep_47
        ),
        Avatar(
            id = 7,
            name = "Smart Hijabi",
            drawableRes = R.drawable.peep_6
        ),
        Avatar(
            id = 8,
            name = "Cool Singh",
            drawableRes = R.drawable.peep_85
        ),
        Avatar(
            id = 9,
            name = "Bored Bun",
            drawableRes = R.drawable.peep_93
        )
    )

    fun getAllAvatars(): List<Avatar> = avatars

    fun getAvatarById(id: Int?): Avatar? = avatars.find { it.id == id }

    fun getDefaultAvatar(): Avatar = avatars.first { it.id == 1 }

    fun getAvatarName(id: Int?): String? {
        return getAvatarById(id)?.name
    }
}