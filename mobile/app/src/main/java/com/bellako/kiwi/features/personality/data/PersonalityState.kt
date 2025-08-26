package com.bellako.kiwi.features.personality.data

data class PersonalityState(
    val realName: String,
    val knightName: String,
    val build: String,
    val goodApps: List<String>,
    val badApps: List<String>,
) {
    val questions: List<Question> =
        listOf(
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
