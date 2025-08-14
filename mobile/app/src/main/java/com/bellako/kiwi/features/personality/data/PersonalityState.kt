package com.bellako.kiwi.features.personality.data

data class PersonalityState (
    val realName: String,
    val knightName: String,
    val build: String,
    val goodApps: List<String>,
    val badApps: List<String>
){

    val questions: List<Question> = listOf(
        Question(
            question = "Test question",
            options = listOf(
                "Answer 1",
                "Answer 2",
                "Answer 3",
                "Answer 4",
            )
        ),
        Question(
            question = "When you face a tough choice,\nwhat do you trust most?",
            options = listOf(
                "My gut feeling",
                "Logical reasoning",
                "How others might feel",
            )
        ),
    )

    var currentQuestion = 0

    var answers = MutableList(questions.size) { -1 }


    fun toDTO() : PersonalityDTO {
        return PersonalityDTO(
            realName = realName,
            knightName = knightName,
            build = build,
            goodApps = goodApps,
            badApps = badApps
        )
    }

    fun toDomainObject(): Result<Personality> {
        val realNameResult = UserName.of(realName)
        return realNameResult.fold(
            onSuccess = { validRealName ->
                val knightNameResult = UserName.of(knightName)
                knightNameResult.fold(
                    onSuccess = { validKnightName ->
                        Result.success(Personality(validRealName, validKnightName, build, goodApps, badApps))
                    },
                    onFailure = { err -> Result.failure(err) }
                )
            },
            onFailure = { err -> Result.failure(err) }
        )
    }
}