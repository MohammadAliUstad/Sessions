package com.yugentech.sessions.theme.config

// Which design the Home screen is drawn in, in the order Appearance offers them
enum class HomeLayout(val label: String, val description: String) {
    CLASSIC("Classic", "The original layout"),
    REFINED("Refined", "Calmer, with the ring in front"),
    STUDIO("Studio", "A dial and a floating dock")
}
