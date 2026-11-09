package com.yugentech.sessions.ui.config.editProfileScreen.components

import androidx.annotation.StringRes
import androidx.annotation.DrawableRes
import com.yugentech.sessions.R

data class Avatar(
    val id: Int,
    @param:StringRes val name: Int,
    @field:DrawableRes val drawableRes: Int,
    val category: AvatarCategory
)

enum class AvatarCategory(@param:StringRes val displayName: Int) {
    EVERYDAY_VIBES(R.string.avatar_category_everyday),
    PROFESSIONAL(R.string.avatar_category_professional),
    COFFEE_CONTEMPLATION(R.string.avatar_category_coffee)
}

object AvatarRepository {
    private val avatars = listOf(
        // Everyday Vibes
        Avatar(1, R.string.avatar_chill_vibes, R.drawable.peep_8, AvatarCategory.EVERYDAY_VIBES),
        Avatar(2, R.string.avatar_easy_going, R.drawable.peep_82, AvatarCategory.EVERYDAY_VIBES),
        Avatar(3, R.string.avatar_weekender, R.drawable.peep_92, AvatarCategory.EVERYDAY_VIBES),
        Avatar(4, R.string.avatar_cool_singh, R.drawable.peep_85, AvatarCategory.EVERYDAY_VIBES),
        Avatar(5, R.string.avatar_hands_up, R.drawable.peep_16, AvatarCategory.EVERYDAY_VIBES),
        Avatar(6, R.string.avatar_beard_mod, R.drawable.peep_51, AvatarCategory.EVERYDAY_VIBES),
        Avatar(7, R.string.avatar_simple_soul, R.drawable.peep_91, AvatarCategory.EVERYDAY_VIBES),
        Avatar(8, R.string.avatar_karen, R.drawable.peep_86, AvatarCategory.EVERYDAY_VIBES),
        Avatar(9, R.string.avatar_studious, R.drawable.peep_33, AvatarCategory.EVERYDAY_VIBES),

        // Professional Mode
        Avatar(10, R.string.avatar_corporate, R.drawable.peep_11, AvatarCategory.PROFESSIONAL),
        Avatar(11, R.string.avatar_clean_cut, R.drawable.peep_79, AvatarCategory.PROFESSIONAL),
        Avatar(12, R.string.avatar_dapper_gent, R.drawable.peep_17, AvatarCategory.PROFESSIONAL),
        Avatar(13, R.string.avatar_beard_boss, R.drawable.peep_27, AvatarCategory.PROFESSIONAL),
        Avatar(14, R.string.avatar_doc_life, R.drawable.peep_101, AvatarCategory.PROFESSIONAL),
        Avatar(15, R.string.avatar_open_collar, R.drawable.peep_63, AvatarCategory.PROFESSIONAL),
        Avatar(16, R.string.avatar_smart_hijabi, R.drawable.peep_6, AvatarCategory.PROFESSIONAL),
        Avatar(17, R.string.avatar_teeth_grind, R.drawable.peep_47, AvatarCategory.PROFESSIONAL),
        Avatar(18, R.string.avatar_wise_elder, R.drawable.peep_100, AvatarCategory.PROFESSIONAL),

        // Coffee & Contemplation
        Avatar(19, R.string.avatar_vibing, R.drawable.peep_59, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(20, R.string.avatar_late_night, R.drawable.peep_87, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(21, R.string.avatar_wise_one, R.drawable.peep_102, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(22, R.string.avatar_lost_soul, R.drawable.peep_2, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(23, R.string.avatar_unbothered, R.drawable.peep_53, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(24, R.string.avatar_minimalist, R.drawable.peep_98, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(25, R.string.avatar_black_tee, R.drawable.peep_61, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(26, R.string.avatar_caffeinated, R.drawable.peep_34, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(27, R.string.avatar_bored_bun, R.drawable.peep_93, AvatarCategory.COFFEE_CONTEMPLATION)
    )

    fun getAllAvatars(): List<Avatar> = avatars

    fun getAvatarsByCategory(category: AvatarCategory): List<Avatar> =
        avatars.filter { it.category == category }

    fun getAvatarById(id: Int?): Avatar? = avatars.find { it.id == id }

    fun getDefaultAvatar(): Avatar = avatars.first { it.id == 1 }

    @StringRes
    fun getAvatarName(id: Int?): Int? = getAvatarById(id)?.name

    fun getCategoriesWithAvatars(): Map<AvatarCategory, List<Avatar>> =
        avatars.groupBy { it.category }
}