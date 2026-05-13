package com.bellako.kiwi.features.personality.data

data class PersonalityState(
    val realName: String,
    val knightName: String,
    val build: String,
    val goodApps: List<String>,
    val badApps: List<String>,
    val neutralApps: List<String>,
) {
    val questions: List<Question> =
        listOf(
            Question(
                question = "When you finally have free time, what usually happens?",
                options =
                    listOf(
                        "I want to do many things but end up doing very little",
                        "I relax with something easy like videos or scrolling",
                        "I use the time to get important things done",
                    ),
            ),
            Question(
                question = "Which sentence best describes your relationship with your phone?",
                options =
                    listOf(
                        "I use it for everything, even when I try to focus",
                        "I use it mainly to disconnect or entertain myself",
                        "I use it with clear goals like organizing or learning",
                    ),
            ),
            Question(
                question = "What stresses you the most lately?",
                options =
                    listOf(
                        "Feeling like I could be doing more",
                        "Being bored or missing out on something",
                        "Not doing things as well as I want",
                    ),
            ),
            Question(
                question = "What motivates you the most to keep going?",
                options =
                    listOf(
                        "Feeling better about myself",
                        "Having fun and feeling engaged",
                        "Seeing clear and meaningful progress",
                    ),
            ),
            Question(
                question = "When an activity is slow or repetitive...",
                options =
                    listOf(
                        "My mind starts drifting to other things",
                        "I quit and look for something more entertaining",
                        "I push through because I know it’s worth it",
                    ),
            ),
            Question(
                question = "On social media, you usually...",
                options =
                    listOf(
                        "Feel inspired but also compare yourself to others",
                        "Look mainly for entertainment and trends",
                        "Follow accounts that genuinely add value",
                    ),
            ),
            Question(
                question = "When you feel mentally tired...",
                options =
                    listOf(
                        "I keep pushing even if my performance drops",
                        "I distract myself so I don’t have to think",
                        "I stop and try to reorganize myself",
                    ),
            ),
            Question(
                question = "You learn best when...",
                options =
                    listOf(
                        "You experiment and learn as you go",
                        "It’s short, visual, and entertaining",
                        "You understand the theory and the why",
                    ),
            ),
            Question(
                question = "If no one sets a pace for you...",
                options =
                    listOf(
                        "I struggle to prioritize",
                        "I go with the flow depending on how I feel",
                        "I organize myself naturally",
                    ),
            ),
            Question(
                question = "When you face a tough choice, what do you trust most?",
                options =
                    listOf(
                        "My gut feeling",
                        "Logical reasoning",
                        "How others might feel",
                    ),
            ),
        )

    var currentQuestion = 0

    var answers = MutableList(questions.size) { -1 }
}
