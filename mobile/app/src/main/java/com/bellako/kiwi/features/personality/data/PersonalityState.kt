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
                question = "personality_free_time_question",
                options =
                    listOf(
                        "personality_free_time_many_plans",
                        "personality_free_time_relax",
                        "personality_free_time_get_things_done",
                    ),
            ),
            Question(
                question = "personality_phone_question",
                options =
                    listOf(
                        "personality_phone_everything",
                        "personality_phone_disconnect",
                        "personality_phone_clear_goals",
                    ),
            ),
            Question(
                question = "personality_stress_question",
                options =
                    listOf(
                        "personality_stress_doing_more",
                        "personality_stress_missing_out",
                        "personality_stress_not_well_enough",
                    ),
            ),
            Question(
                question = "personality_motivation_question",
                options =
                    listOf(
                        "personality_motivation_self",
                        "personality_motivation_fun",
                        "personality_motivation_progress",
                    ),
            ),
            Question(
                question = "personality_repetitive_question",
                options =
                    listOf(
                        "personality_repetitive_distracted",
                        "personality_repetitive_quit",
                        "personality_repetitive_persist",
                    ),
            ),
            Question(
                question = "personality_social_media_question",
                options =
                    listOf(
                        "personality_social_media_compare",
                        "personality_social_media_entertainment",
                        "personality_social_media_value",
                    ),
            ),
            Question(
                question = "personality_mental_fatigue_question",
                options =
                    listOf(
                        "personality_mental_fatigue_push",
                        "personality_mental_fatigue_distract",
                        "personality_mental_fatigue_reorganize",
                    ),
            ),
            Question(
                question = "personality_learning_question",
                options =
                    listOf(
                        "personality_learning_experiment",
                        "personality_learning_visual",
                        "personality_learning_theory",
                    ),
            ),
            Question(
                question = "personality_pace_question",
                options =
                    listOf(
                        "personality_pace_prioritize",
                        "personality_pace_flow",
                        "personality_pace_organize",
                    ),
            ),
            Question(
                question = "personality_tough_choice_question",
                options =
                    listOf(
                        "personality_tough_choice_gut",
                        "personality_tough_choice_logic",
                        "personality_tough_choice_empathy",
                    ),
            ),
        )

    var currentQuestion = 0

    var answers = MutableList(questions.size) { -1 }
}
