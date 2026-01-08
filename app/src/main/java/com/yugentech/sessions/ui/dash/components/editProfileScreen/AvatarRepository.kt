package com.yugentech.sessions.ui.dash.components.editProfileScreen

import androidx.annotation.DrawableRes
import com.yugentech.sessions.R

data class Avatar(
    val id: Int,
    val name: String,
    @field:DrawableRes val drawableRes: Int,
    val category: AvatarCategory
)

enum class AvatarCategory(val displayName: String) {
    EVERYDAY_VIBES("Everyday Vibes"),
    PROFESSIONAL("Professional Mode"),
    COFFEE_CONTEMPLATION("Coffee & Contemplation")
}

object AvatarRepository {
    private val avatars = listOf(
        // --- Group 1: Everyday Vibes (9) ---
        Avatar(1, "Chill Vibes", R.drawable.peep_8, AvatarCategory.EVERYDAY_VIBES),
        Avatar(2, "Easy Going", R.drawable.peep_82, AvatarCategory.EVERYDAY_VIBES),
        Avatar(3, "Casual Friday", R.drawable.peep_92, AvatarCategory.EVERYDAY_VIBES),
        Avatar(4, "Cool Singh", R.drawable.peep_85, AvatarCategory.EVERYDAY_VIBES),
        Avatar(5, "Hands Up", R.drawable.peep_16, AvatarCategory.EVERYDAY_VIBES),
        Avatar(6, "Discord Mod", R.drawable.peep_51, AvatarCategory.EVERYDAY_VIBES),
        Avatar(7, "Simple Soul", R.drawable.peep_91, AvatarCategory.EVERYDAY_VIBES),
        Avatar(8, "Karen", R.drawable.peep_86, AvatarCategory.EVERYDAY_VIBES),
        Avatar(9, "Specs Appeal", R.drawable.peep_33, AvatarCategory.EVERYDAY_VIBES),

        // --- Group 2: Professional Mode (9) ---
        Avatar(10, "Business Casual", R.drawable.peep_11, AvatarCategory.PROFESSIONAL),
        Avatar(11, "Clean Cut", R.drawable.peep_79, AvatarCategory.PROFESSIONAL),
        Avatar(12, "Dapper Gent", R.drawable.peep_17, AvatarCategory.PROFESSIONAL),
        Avatar(13, "Beard Boss", R.drawable.peep_27, AvatarCategory.PROFESSIONAL),
        Avatar(14, "Doc Life", R.drawable.peep_101, AvatarCategory.PROFESSIONAL),
        Avatar(15, "Open Collar", R.drawable.peep_63, AvatarCategory.PROFESSIONAL),
        Avatar(16, "Smart Hijabi", R.drawable.peep_6, AvatarCategory.PROFESSIONAL),
        Avatar(17, "Teeth Grind", R.drawable.peep_47, AvatarCategory.PROFESSIONAL),
        Avatar(18, "Wise Elder", R.drawable.peep_100, AvatarCategory.PROFESSIONAL),

        // --- Group 3: Coffee & Contemplation (9) ---
        Avatar(19, "Coffee Break", R.drawable.peep_59, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(20, "Caffeine Hat", R.drawable.peep_34, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(21, "Late Night", R.drawable.peep_87, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(22, "Wise One", R.drawable.peep_102, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(23, "Lost Soul", R.drawable.peep_2, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(24, "Keep It Simple", R.drawable.peep_53, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(25, "Minimalist", R.drawable.peep_98, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(26, "Black Tee", R.drawable.peep_61, AvatarCategory.COFFEE_CONTEMPLATION),
        Avatar(27, "Bored Bun", R.drawable.peep_93, AvatarCategory.COFFEE_CONTEMPLATION)
    )

    fun getAllAvatars(): List<Avatar> = avatars

    fun getAvatarsByCategory(category: AvatarCategory): List<Avatar> =
        avatars.filter { it.category == category }

    fun getAvatarById(id: Int?): Avatar? = avatars.find { it.id == id }

    fun getDefaultAvatar(): Avatar = avatars.first { it.id == 1 }

    fun getAvatarName(id: Int?): String? = getAvatarById(id)?.name

    fun getCategoriesWithAvatars(): Map<AvatarCategory, List<Avatar>> =
        avatars.groupBy { it.category }
}